require 'riki/ViewPage'
module Riki
    class RunPage < ViewPage
        def run
            parser = Riki::Parser.new(@page)
            def parser.asAnchor(title)
                if File.exists?("pages/#{title.untaint}")
                    Riki.pageAnchor(title)
                else
                    title # strip page creation on run pages
                end
            end
            input = parser.parse(readFile("pages/#{@page}"))
            File.open(inputFile.untaint, "w") {|f| f.write(input) }
            `java -cp #{RikiConfig::CPATH} net.sourceforge.jwebunit.fit.FileRunner #{inputFile.untaint} #{outputFile.untaint}`
        end

        def body
            readFile(outputFile)
        end

        def inputFile
            Dir.mkdir('input') unless File.exist?('input')
            File.expand_path("input/#{page}.fit.in.html")
        end
        def outputFile
            Dir.mkdir('output') unless File.exist?('output')
            File.expand_path("output/#{page}.fit.out.html")
        end
    end
end