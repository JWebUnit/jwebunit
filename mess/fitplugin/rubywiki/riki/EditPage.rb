module Riki
    class EditPage < RikiPage
        def title
            "Edit #{page}"
        end

        def body
            text = ""
            if File.exist?("pages/#{page}")
                text = readFile("pages/#{page}")
                text.gsub!(/&/, '&amp;')
                text.gsub!(/</, '&lt;')
                text.gsub!(/>/, '&gt;')
            end
            return <<-BODY
                <form id="edit" method="post" action="wiki.rb">
                #{Riki.hiddenField("page", page)}
                #{Riki.hiddenField("mode", "save")}
                <textarea name=Text rows=16 cols=80>#{text}</textarea>
                <p><input type="submit" value=" Save ">
                </form>
            BODY
        end
    end
end