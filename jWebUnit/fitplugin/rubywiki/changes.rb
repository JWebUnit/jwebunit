#! /usr/bin/ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT

print "Content-type: text/html\n\n"

LINK = "[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+"

datedpages = Dir.entries('pages').select {|e| e.untaint =~ /#{LINK}/}
datedpages.map! {|e| [File.mtime("pages/#{e}"), e]}
datedpages.sort! { |a,b| b[0] <=> a[0] }

max = ENV['QUERY_STRING'] =~ /\bmax=(\d+)/ ? $1.to_i : 25

body = "<dl>\n"
lastdate = nil
datedpages.each do |datedpage|
    time = datedpage[0]
    file = datedpage[1]
    date = File.mtime("pages/#{file}").strftime("%B %d, %Y")
	if ( date != lastdate )
        body += "<dt>"
		body += "<br>" unless lastdate.nil?
		body += date
        lastdate = date
	end
    body += "<dd><a href=wiki.rb?#{file}>#{file}</a>\n";
    break if (max -= 1) == 0;
end
body += "</dl>\n"

par = {}
par['body' ] = body
par['title'] = "Recent Changes"
par['page' ] = "http:changes.rb"

template = IO.readlines("template.html").join
template.gsub!(/\$(\w+)/) { par.has_key?($1) ? par[$1] : '' }
print template