#! /usr/bin/ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT

# use strict;
#$|++;
print "Content-type: text/html\n\n";

LINK = "[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+"

ENV['QUERY_STRING'] =~ /search=([^\&]*)/
target = $1.untaint
target.gsub!(/\+/, ' ')
target.gsub!(/\%(..)/) {[$1.hex].pack('C') }

pat = target
pat.gsub!(/[+?.*()[\]{}|\\]/, "\\#{$&}")
pat = "\\b\\w*(#{pat})\\w*\\b";

target.gsub!(/"/, '&quot;')
body = <<EOF
 <form action="search.rb">
  <input type="text" size="40" name="search" value="#{target}">
  <input type="submit" value="Search">
 </form>
EOF

files = Dir.entries('pages').select {|e| e.untaint =~ /#{LINK}/}.sort!

hits = 0
files.each do |file|
    contents = IO.readlines("pages/#{file}").join
    regx = Regexp.new(pat, Regexp::IGNORECASE)
	if regx.match(file) || regx.match(contents)
		hits += 1
		body += "<a href=wiki.rb?#{file}>#{file}<\/a> . . . . . .  #{$&}<br>\n"
    end
end

hits = hits > 0 ? hits.to_s : "No"

par = {}
par['summary'] = "#{hits} pages found out of #{files.length} pages searched."
par['title'] = "Search Results"
par['page'] = "WelcomeVisitors"
par['body'] = body

template = IO.readlines("template.html").join
template.gsub!(/\$(\w+)/) { par.has_key?($1) ? par[$1] : '' }
print template