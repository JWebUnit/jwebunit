module Riki
    class Parser
        def initialize(page='')
            @page = page
        end
        def parse(lines)
            @codeArr = []
            body = ''

            lines.gsub!(/&/, '&amp;')
            lines.gsub!(/</, '&lt;')
            lines.gsub!(/>/, '&gt;')

            lines.split(/\r?\n/).each do |s|
                urls   = []
                urlNum = 0

                while (s.sub!(/\b(javascript):\S.*/, "#{MARK}#{urlNum}#{MARK}"))
                    urls[urlNum] = $&
                    urlNum+=1
                end

                while (@codeArr.last != 'table' &&
                       s.sub!(/\b#{PROTOCOL}:[^\s\<\>\[\]"'\(\)]*[^\s\<\>\[\]"'\(\)\,\.\?]/,
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

                s.gsub!(/\[Search\]/, %Q[<form id="search" action="wiki.rb">
                                      #{Riki.hiddenField("mode", "search")}
                                      <input type="text" name="search" size="40">&nbsp;<input type="submit" value="Search">
                                      </form>])
                s.gsub!(/\b#{LINK}\b/)         { asAnchor($&)              }
                s.gsub!(/#{MARK}(\d+)#{MARK}/) { inPlaceUrl(urls[$1.to_i]) }
                s.gsub!(/\[run\]/i, %Q[<a href="wiki.rb?page=#{@page}&mode=run">Run Fit Test</a>])  # to run fit
                body += "#{s}\n"
            end

            body += emitCode('', 0);
            return body
        end


        def emitCode(code, depth)
            tags = ''
            startTag =  if code =~ /table/
                            %Q[table class="wiki"]
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
                if e =~ /^\s+$/
                    cellspans.push ['&nbsp;', 1]
                elsif e.strip != ''
                    cellspans.push [e, 1]
                else
                    cellspans.last[1] += 1 unless cellspans.empty?
                end
            end
            row = '<tr class="wiki">'
            cellspans.each do |cell|
                content = cell[0]
                span = cell[1]
                td = (span > 1) ? %Q[<td  class="wiki" colspan="#{span}">] : %Q[<td class="wiki">]
                td += content + "</td>"
                row += td
            end
            row += '</tr>'
        end

        def asAnchor(title)
            if File.exists?("pages/#{title.untaint}")
                Riki.pageAnchor(title)
            else
                %Q[<a href="wiki.rb?page=#{title}&mode=edit">?<\/a>#{title}]
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
end