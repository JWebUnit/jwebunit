#! /usr/bin/ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT

# use strict;
#$|++;
print "Content-type: text/html\n\n";
#
LINK = "[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+";
MARK = "\263";
page = ENV['QUERY_STRING'] =~ /^(#{LINK})$/ ? $1 : "WelcomeVisitors"  # $& is the last match
page.untaint
mode = $_ =~ /append/ ##??????
puts "<!-- #{mode.inspect} -->"

text = ""
if File.exist?("pages/#{page}")
    text = IO.readlines("pages/#{page}").join
#	if ( /$mark/ ) {
#	 my %bla = split /$mark/, $_ ;
#	 $_ = $bla{text};  # convert hidden-field page to plain
#	 }
	text.gsub!(/&/, '&amp;')
	text.gsub!(/</, '&lt;')
	text.gsub!(/>/, '&gt;')
end

par = {}
if ( mode )
  par['title'] = "Add comment to #{page}"
  text = ""
  mode = "append"
else
  par['title'] = "Edit #{page}"
  mode = "edit"
end

par['page'] = page
par['body'] = <<-BODY
	<form method="post" action="save.rb?#{page}">
	<textarea name=Text rows=16 cols=60 wrap=virtual>#{text}</textarea>
	<p><input type="submit" value=" Save ">
      <input type="hidden" name="mode" value="#{mode}">
	</form>
BODY

template = IO.readlines("template.html").join
template.gsub!(/\$(\w+)/) { par.has_key?($1) ? par[$1] : '' }
print template