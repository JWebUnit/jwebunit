#! /usr/bin/ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT

require './riki'
max = ENV['QUERY_STRING'] =~ /\bmax=(\d+)/ ? $1.to_i : 25
Riki::ChangesPage.new(max.untaint).display