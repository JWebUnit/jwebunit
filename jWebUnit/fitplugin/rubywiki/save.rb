#! /usr/bin/ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT
require 'cgi'
require './riki'

page = ENV['QUERY_STRING'] =~ /^(#{Riki::LINK})$/ ? $1 : "WelcomeVisitors"  # $& is the last match
text = CGI.new.params['Text']
text = text.nil? ? "" : text[0]
Riki::SavePage.new(page.untaint, text.untaint).display