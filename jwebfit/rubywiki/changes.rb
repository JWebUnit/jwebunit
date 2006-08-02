#!ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT

require './riki'

class ChangesPage < Riki::RikiPage
    def initialize
        super("http:changes.rb")
        @max   = param('max', 25).to_i
        @title = "Recent Changes"
    end

    def run
        @datedpages = Dir.entries('pages').select {|e| e.untaint =~ /#{Riki::LINK}/}
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
            s += "<dd><a href=wiki.rb?page=#{file}>#{file}</a>\n";
        end
        s += "</dl>\n"
    end
end

Riki::ChangesPage.new.display