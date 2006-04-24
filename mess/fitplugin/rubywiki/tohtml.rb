require 'riki'

class MyParser < Riki::Parser
    def asAnchor(title)
        if File.exists?("pages/#{title}")
            %Q[<a href="#{@dir}/#{title}.html">#{title}<\/a>]
        else
            title
        end

    end

    # strip out run links
    def inPlaceUrl(origRef)
        origRef =~ /run\.rb/ ? "" : super(origRef)
    end
end

page = 'TestCheckbox'
lines = IO.readlines("pages/#{page}").join
parser = MyParser.new(page)
puts parser.parse(lines)

