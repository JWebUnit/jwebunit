#! /usr/bin/ruby
## Copyright 1994-2000, Cunningham & Cunningham, Inc.
## in collaboration with Dave W. Smith
## Open Source for personal use only.
## ... and then only
## with the understanding that the owner(s) cannot be
## responsible for any behavior of the program or
## any damages that it may cause. See LICENSE.TXT

## straight up port from Ward's perl quicki wiki to ruby
## plus support for tables
require './riki.rb'
require 'cgi'

page = Riki.param('page', 'WelcomeVisitors')

pageClass = case Riki.param('mode')
                when 'edit'   then Riki::EditPage
                when 'save'   then Riki::SavePage
                when 'search' then Riki::SearchPage
                when 'run'    then Riki::RunPage
                else               Riki::ViewPage
            end

pageClass.new(page.untaint).display