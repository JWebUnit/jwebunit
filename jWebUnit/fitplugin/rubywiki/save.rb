#! /usr/bin/ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT
require 'cgi'

MARK = "\263";
LINK = "[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+";

page = ENV['QUERY_STRING'] =~ /^(#{LINK})$/ ? $1 : "WelcomeVisitors"  # $& is the last match
page.untaint
params = CGI.new.params
bodyHash = {}
params.each { |k,v|
    v = v[0]
    v.gsub!(/\+/, ' ')
	v.gsub!(/\%(..)/) { [$1.hex].pack('C') }
	bodyHash[k] = v
}

s = bodyHash['Text']
if s =~ /\n/
  s.gsub!(/\r/, '') # presume PC just strip cr
else
  s.gsub!(/\r/, "\n")  # replace cr with lf
end

s.gsub!(/#{MARK}/,'')  # strip markers

# -v- autobacklink
if s.gsub!(/<<(#{LINK})\b/, '\1')
 File.open("pages/#{$1}", "a") { |f| f.print "\n----\n$page" }
end

mode = "w";
if (bodyHash[mode] =~ /append/)
 mode = "a"
 s = "\n----\n\n#{s}";
end

File.open("pages/#{page}", mode) { |f| f.print s }


require './wiki.rb' # maybe work?

#my %par;
#$par{title} = "Thank You";
#$par{page} = $page;
#$par{body} = << "";
#	The <a href=wiki.cgi?$page>$page</a> page has been saved.
#	You may <b>back</b> up to the edit form and make further changes.
#	Remember to <b>reload</b> old copies of this page and especially
#	old copies of the editor.</i>
#
#open(T, 'template.html') or die "template.html: $!";
#undef $/;
#$_ = <T>;
#close(T);
#s/\$(\w+)/defined($par{$1}) ? $par{$1} : ''/geo;
#print;
#$/ = "\n";