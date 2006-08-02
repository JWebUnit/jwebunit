module Riki
    class SavePage < RikiPage
        def initialize(cgi, page)
            super(cgi, page)
            @text = param('Text')
            @title = "Thank You"
            @body = <<-BODY
            	The <a href="wiki.rb?page=#{page}">#{page}</a> page has been saved.
                You may <b>back</b> up to the edit form and make further changes.
            	Remember to <b>reload</b> old copies of this page and especially
            	old copies of the editor.</i>
            BODY
        end

        def run
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
            File.open("pages/#{page}", "w") { |f| f.print @text }
        end
    end
end