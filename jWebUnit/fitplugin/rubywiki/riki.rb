#! /usr/bin/ruby
## Copyright 1994-2000, Cunningham & Cunningham, Inc.
## in collaboration with Dave W. Smith
## Open Source for personal use only.
## ... and then only
## with the understanding that the owner(s) cannot be
## responsible for any behavior of the program or
## any damages that it may cause. See LICENSE.TXT

## straight up port from Ward's perl quicki wiki to ruby
## plus support for tables

module Riki
    MARK = "\263"
    LINK = "[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+"
    PROTOCOL = "(http|ftp|mailto|file|gopher|telnet|news)"

    def Riki.applyTemplate(par)
        template = read("template.html")
        template.gsub!(/\$(\w+)/) { par.has_key?($1) ? par[$1] : '' }
        template
    end

    def Riki.read(file)
        IO.readlines(file).join
    end

    def Riki.inspect(obj)
        puts "<!-- #{obj.inspect} -->"
    end

    class RikiPage
        attr_reader :page, :title, :body, :summary, :action
        def initialize(page)
            @page = page
            @title, @body, @summary, @action = '', '', '', ''
        end

        def run
            #override for pre-display processing
        end

        def displayHash
            par = {}
            par['page' ] = page
            par['title'] = title
            par['body'] = body
            par['summary'] = summary
            par['action'] = action
            return par
        end

        def display
            print "Content-type: text/html\n\n"
            run
            print Riki.applyTemplate(displayHash)
        end
    end

    class ViewPage < RikiPage
        def initialize(page)
            super(page)
            @title = @page.gsub(/(.)([A-Z])/, '\1 \2')
            @action = %Q[<form method=post action="edit.rb?#{@page}">
                         <input type=submit value=" Edit "></form>]
            if File.exist?("pages/#{@page}")
                @summary = %Q[-- Last edited #{File.mtime("pages/#{@page}").strftime("%B %d, %Y")}]
            end
        end

        def body
            if File.exist?("pages/#{@page}")
              body = Riki::Parser.new.parse(IO.readlines("pages/#{@page}").join)
            else
              body = <<-BODY
              "#{@page}" does not yet exist. <BR>
                Use the <strong>Edit</strong> button to create it.
                BODY
            end
            return body
        end
    end

    class SearchPage < RikiPage
        def initialize(target)
            super("WelcomeVisitors")
            @target = target
            @title = "Search Results"
        end

        def run
            @target.gsub!(/\+/, ' ')
            @target.gsub!(/\%(..)/) {[$1.hex].pack('C') }

            pat = @target.dup
            pat.gsub!(/[+?.*()[\]{}|\\]/, "\\#{$&}")
            pat = "\\b\\w*(#{pat})\\w*\\b";
            @target.gsub!(/"/, '&quot;')

            files = Dir.entries('pages').select {|e| e =~ /#{LINK}/}
            @pageCount = files.length
            @hits  = 0
            @searchResults = {}
            files.each do |file|
                contents = Riki.read("pages/#{file}")
                regx = /#{pat}/i
            	if regx.match(file) || regx.match(contents)
                    @hits += 1
                    @searchResults[file] = $&
                end
            end
        end

        def body
            @body = <<-EOF
             <form action="search.rb">
              <input type="text" size="40" name="search" value="#{@target}">
              <input type="submit" value="Search">
             </form>
            EOF
            @searchResults.sort.each {|k,v| @body += "<a href=wiki.rb?#{k}>#{k}<\/a> . . . . . .  #{v}<br>\n" }
            return @body
        end

        def summary
            hitsString = @hits > 0 ? @hits.to_s : 'No'
            "#{hitsString} pages found out of #{@pageCount} pages searched."
        end

    end

    class EditPage < RikiPage
        def title
            "Edit #{page}"
        end

        def body
            text = ""
            if File.exist?("pages/#{page}")
                text = Riki.read("pages/#{page}")
                text.gsub!(/&/, '&amp;')
                text.gsub!(/</, '&lt;')
                text.gsub!(/>/, '&gt;')
            end
            return <<-BODY
                <form method="post" action="save.rb?#{page}">
                <textarea name=Text rows=16 cols=80>#{text}</textarea>
                <p><input type="submit" value=" Save ">
                </form>
            BODY
        end
    end

    class SavePage < RikiPage
        def initialize(page, text)
            super(page)
            @text = text
            @title = "Thank You"
            @body = <<-BODY
            	The <a href="wiki.rb?#{page}">#{page}</a> page has been saved.
                You may <b>back</b> up to the edit form and make further changes.
            	Remember to <b>reload</b> old copies of this page and especially
            	old copies of the editor.</i>
            BODY
        end

        def run
            Riki.inspect(@text)
            if @text =~ /\n/
              @text.gsub!(/\r/, '') # presume PC just strip cr
            else
              @text.gsub!(/\r/, "\n")  # replace cr with lf
            end
            @text.gsub!(/\+/, ' ')
            @text.gsub!(/\%(..)/) { [$1.hex].pack('C') }
            @text.gsub!(/#{MARK}/,'')  # strip markers

            # -v- autobacklink
            if @text.gsub!(/<<(#{LINK})\b/, '\1')
                File.open("pages/#{$1}", "a") { |f| f.print "\n----\n$page" }
            end
            File.open("pages/#{page}", "w") { |f| f.print @text}
        end
    end

    class RefCountPage < RikiPage
        def initialize
            super("http:pagerefs.rb")
            @title = "Page References"
        end

        def run
            @files = Dir.entries('pages').select {|e| e =~ /#{LINK}/}.sort!
            refs = {}
            @files.each do |file|
                contents = Riki.read("pages/#{file}")
                targets = {}
                contents.scan(/(#{LINK})/).each {|link| targets[link[0]] = file }
                Riki.inspect targets
                targets.each_key do |target|
                    refs[target] = [] unless refs.has_key?(target)
                    refs[target].push(file)
                end
            end
            @refcnt = {}
            @files.each { |file| @refcnt[file] = refs.has_key?(file) ? refs[file].length : 0 }
            @files.sort! {|a,b| @refcnt[b] <=> @refcnt[a] || a <=> b }
        end

        def body
            s = "<table border=0 cellspacing=0 cellpadding=0>\n"
            @files.each do |file|
                s += <<-ROW
                <tr>
                <td valign="right">#{@refcnt[file]}</td>
                <td>&nbsp;&nbsp;<a href="wiki.rb?#{file}">#{file}</a></td>
                </tr>
                ROW
            end
            s += "</table>\n"
        end
    end

    class ChangesPage < RikiPage
        def initialize(max)
            super("http:changes.rb")
            @max   = max
            @title = "Recent Changes"
        end

        def run
            @datedpages = Dir.entries('pages').select {|e| e =~ /#{LINK}/}
            @datedpages.map! {|e| [File.mtime("pages/#{e}"), e]}
            @datedpages.sort! { |a,b| b[0] <=> a[0] }
            @datedpages = @datedpages[0...@max]
        end

        def body
            lastdate = nil
            s = "<dl>\n"
            @datedpages.each do |datedpage|
                time = datedpage[0]
                file = datedpage[1]
                date = File.mtime("pages/#{file}").strftime("%B %d, %Y")
            	if ( date != lastdate )
                    s += "<dt>"
            		s += "<br>" unless lastdate.nil?
            		s += date
                    lastdate = date
            	end
                s += "<dd><a href=wiki.rb?#{file}>#{file}</a>\n";
            end
            s += "</dl>\n"
        end
    end

    class Parser
        def parse(lines)
            @codeArr = []
            body = ''

            lines.gsub!(/&/, '&amp;')
            lines.gsub!(/</, '&lt;')
            lines.gsub!(/>/, '&gt;')

            lines.split('\n').each do |s|
                urls   = []
                urlNum = 0

                while (s.sub!(/\b(javascript):\S.*/, "#{MARK}#{urlNum}#{MARK}"))
                  urls[urlNum] = $&
                  urlNum+=1
                end

                while (s.sub!(/\b#{PROTOCOL}:[^\s\<\>\[\]"'\(\)]*[^\s\<\>\[\]"'\(\)\,\.\?]/,
                       "#{MARK}#{urlNum}#{MARK}"))
                  urls[urlNum] = $&
                  urlNum+=1
                end

                # -v- emitcode block-tag section
                code = nil
                s.sub!(/^\t+/, '')  # legacy pages with tab indented lists
                code = '...'                           if s.sub!(/^\s*$/, '<p>') && @codeArr.last != 'table'
                body += emitCode(code='pre', 1)        if s =~ /^\s/
                body += emitCode(code='ul', $1.length) if s.sub!(/^(\*+)/, '<li>')
                body += emitCode(code='ol', $1.length) if s.sub!(/^(\#+)/, '<li>')
                body += emitCode(code='dl', $1.length) if s.sub!(/^(:+)(.+?):( +)/, '<dt>\2<dd>')
                body += emitCode(code='ol', 1)         if s.sub!(/^(\d+)\./, '<li>')
                body += emitCode(code='h4', 1)         if s.sub!(/^!!!!/, '')
                body += emitCode(code='h3', 1)         if s.sub!(/^!!!/, '')
                body += emitCode(code='h2', 1)         if s.sub!(/^!!/, '')
                body += emitCode(code='blockquote', 1) if s.sub!(/^\"\"/, '')
                if s =~ /^\|/
                    s.sub!(/^\|.*\|\s*$/) {asRow($&)}
                    body += emitCode(code='table', 1)
                end

                body += emitCode('', 0) unless code

                ## -v- inline tag section
                s.sub! (/^-----*/,       '<hr>')
                s.gsub!(/'{3}(.*?)'{3}/, '<strong>\1</strong>')
                s.gsub!(/'{2}(.*?)'{2}/, '<em>\1</em>')

                s.gsub!(/\[Search\]/, '<form action="search.rb">' +
                                      '<input type="text" name="search" size="40"><input type="submit" value="Search">'+
                                      '</form>')
                s.gsub!(/\b#{LINK}\b/)         { asAnchor($&)              }
                s.gsub!(/#{MARK}(\d+)#{MARK}/) { inPlaceUrl(urls[$1.to_i]) }
                body += "#{s}\n"
            end

            body += emitCode('', 0);
            return body
        end


        def emitCode(code, depth)
          tags = ''
          #comment codeArr if code =~ /table/
          startTag = if code =~ /table/
                       "table border=\"1\" cellspacing=\"0\" cellpadding=\"3\""
                     else
                       code.dup
                     end
          while(@codeArr.length > depth)
            tags += "</" + @codeArr.pop + ">\n<p>"
          end
          while(@codeArr.length < depth)
            @codeArr.push code
            tags += "<#{startTag}>"
          end
          if !@codeArr.empty? && @codeArr.last != code
            tags += "</#{@codeArr.last}>\n<#{startTag}>";  # split with \n
            @codeArr[-1] = code
          end
          return tags
        end

        def asRow(s)
            cells = s.sub!(/^\|/, '').scan(/([^|]*)\|/).flatten
            cellspans = []
            cells.each do |e|
                if e.strip != ''
                    cellspans.push [e, 1]
                else
                    cellspans.last[1] += 1 unless cellspans.empty?
                end
            end
            row = '<tr>'
            cellspans.each do |cell|
                content = cell[0]
                span = cell[1]
                td = (span > 1) ? "<td colspan=\"#{span}\">" : "<td>"
                td += content + "</td>"
                row += td
            end
            row += '</tr>'
        end

        def asAnchor(title)
          if File.exists?("pages/#{title}")
            %Q[<a href="wiki.rb?#{title}">#{title}<\/a>]
          else
            %Q[<a href="edit.rb?#{title}">?<\/a>#{title}]
          end
        end

        def inPlaceUrl(origRef)
          ref = origRef.dup
          ref.sub!(/^(javascript.{30}).*/, "#{$1} ...")
          return "<img src=\"#{ref}\">" if (ref =~ /\.(gif|jpeg|jpg|png)$/i)
          return "<a href=\"#{origRef}\">#{ref}<\/a>"
        end

        def comment(obj)
            puts "<!-- #{obj.inspect} -->"
        end

    end #class

end #module