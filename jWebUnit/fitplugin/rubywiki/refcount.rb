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

LINK = "([A-Z][a-z0-9]+([A-Z][a-z0-9]+)+)";

files = Dir.entries('pages').select {|e| e.untaint =~ /#{LINK}/}.sort!

refs = {}
all_targets = []
files.each do |file|
 contents = IO.readlines("pages/#{file}").join
 targets = {}
 contents.scan(/#{LINK}/).each {|link| targets[link[0]] = file }
 targets.each_key do |target|
    refs[target] = [] unless refs.has_key?(target)
    refs[target].push(file)
 end
 all_targets << targets
end

refcnt = {}
files.each do |file|
    refcnt[file] = refs.has_key?(file) ? refs[file].length : 0
end

files.sort! {|a,b| refcnt[b] <=> refcnt[a] || a <=> b }

body = "<table border=0 cellspacing=0 cellpadding=0>\n"
files.each do |file|
 body += <<ROW
<tr>
    <td valign="right">#{refcnt[file]}</td>
    <td>&nbsp;&nbsp;<a href="wiki.rb?#{file}">#{file}</a></td>
</tr>
ROW
end
body += "</table>\n"
par = {}
par['summary'] = ""
par['title'] = "Page References"
par['page'] = "http:pagerefs.rb"
par['body'] = body

template = IO.readlines("template.html").join
template.gsub!(/\$(\w+)/) { par.has_key?($1) ? par[$1] : '' }
print template