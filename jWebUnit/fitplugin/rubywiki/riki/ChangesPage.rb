module Riki
    class ChangesPage < RikiPage
        def initialize(cgi, page)
            super(cgi, "RecentChanges")
            @max   = param('max', 25).to_i - 1
            @title = "Recent Changes"
        end


        def run
            @datedpages = Dir.entries('pages').select {|e| e.untaint =~ /#{Riki::LINK}/}
            @datedpages.map! {|e| [File.mtime("pages/#{e}"), e]}
            @datedpages.sort! { |a,b| b[0] <=> a[0] }
            @datedpages = @datedpages[0..@max]
        end

        def body
            lastdate = nil
            s = %Q[<form name="maxChanges" method="POST" action="wiki.rb">
                        <input type="hidden" name="mode" value="changes"/>
                        Changes to view: <select name="max" onChange="javascript:document.maxChanges.submit();">
                            <option value=""></option>
                            <option value="5">5</option>
                            <option value="10">10</option>
                            <option value="25">25</option>
                            <option value="50">50</option>
                            <option value="0">All</option>
                        </select>
                   </form>]
            s += "<dl>\n"

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
                s += "<dd><a href=wiki.rb?page=#{file}>#{file}</a>\n";
            end
            s += "</dl>\n"
        end
    end
end