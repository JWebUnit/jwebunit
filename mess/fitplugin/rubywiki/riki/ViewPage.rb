
module Riki
    class ViewPage < RikiPage
        def initialize(cgi, page)
            super(cgi, page)
            @title = @page.gsub(/(.)([A-Z])/, '\1 \2')
            if File.exist?("pages/#{@page}")
                @summary = %Q[Last edited #{File.mtime("pages/#{@page}").strftime("%B %d, %Y")}<br>]
            end
        end

        def body
            if File.exist?("pages/#{@page}")
                body = Riki::Parser.new(@page).parse(IO.readlines("pages/#{@page}").join)
            else
                body = <<-BODY
                    "#{@page}" does not yet exist. <BR>
                    Use the <strong>Edit</strong> button to create it.
                BODY
            end
            return body
        end

        def action
            if userLoggedIn?
                %Q[<a id="action" href="wiki.rb?page=#{page}&mode=edit">Edit</a>]
            else
                ""
            end
        end
    end
end