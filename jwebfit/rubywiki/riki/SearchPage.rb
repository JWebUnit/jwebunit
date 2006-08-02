module Riki
    class SearchPage < RikiPage
        def initialize(cgi, page)
            super(cgi, page)
            @target = param('search')
            @title = "Search Results"
            @hits  = 0
            @searchResults = {}
            @pageCount = 0
        end

        def run
            return if @target.nil? || @target.strip == ''
            @target.gsub!(/\+/, ' ')
            @target.gsub!(/\%(..)/) {[$1.hex].pack('C') }
            pat = @target.dup
            pat.gsub!(/[+?.*()[\]{}|\\]/, "\\#{$&}")
            pat = "\\b\\w*(#{pat})\\w*\\b";
            @target.gsub!(/"/, '&quot;')

            files = Dir.entries('pages').select {|e| e =~ /#{LINK}/}
            @pageCount = files.length

            files.each do |file|
                contents = readFile("pages/#{file}")
                regx = /#{pat}/i
            	if regx.match(file) || regx.match(contents)
                    @hits += 1
                    @searchResults[file] = $&
                end
            end
        end

        def body
            @body = <<-EOF
             <form id="search" method="POST" action="wiki.rb">
              #{Riki.hiddenField("mode", "search")}
              <input type="text" size="40" name="search" value="#{@target}"> &nbsp;
              <input type="submit" value="Search">
             </form>
            EOF
            @body += "<table>"
            @body += %Q[<tr><td colspan="3"><b>#{hits}</b>  </td></tr>]
            @searchResults.sort.each {|k,v| @body += %Q[<tr><td><a href="wiki.rb?page=#{k}">#{k}<\/a></td><td>. . . . . .</td><td>#{v}</td><tr>\n] }
            @body += "</table>"
            return @body
        end

        def hits
            hitsString = @hits > 0 ? @hits.to_s : 'No'
            "#{hitsString} pages found out of #{@pageCount} pages searched."
        end

    end
end