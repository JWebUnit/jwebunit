#! /usr/bin/ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT

require './riki'
page = ENV['QUERY_STRING'] =~ /^(#{Riki::LINK})$/ ? $1 : "WelcomeVisitors"  # $& is the last match
Riki::EditPage.new(page.untaint).display