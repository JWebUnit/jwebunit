# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT

# straight up port from Ward's perl quicki wiki to ruby
# plus support for tables

require 'cgi'
require 'rikiConfig'
require 'riki/Parser'
require 'riki/RikiPage'
require 'riki/EditPage'
require 'riki/LoginPage'
require 'riki/RunPage'
require 'riki/SavePage'
require 'riki/SearchPage'
require 'riki/ViewPage'
require 'riki/ChangesPage'


module Riki
    MARK = "\263"
    LINK = "[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+"
    PROTOCOL = "(http|ftp|mailto|file|gopher|telnet|news)"

    def Riki.param(cgi, key, default='')
        cgi[key].empty? ? default : cgi[key][0].untaint
    end

    def Riki.pageAnchor(title)
        %Q[<a href="wiki.rb?page=#{title}">#{title}<\/a>]
    end

    def Riki.hiddenField(name, value)
        %Q[<input type="hidden" name="#{name}" value="#{value}"/>]
    end

    def Riki.authorized?(uid,pwd)
        return true unless RikiConfig::REQUIRE_LOGIN
        return false if uid.nil? || pwd.nil?

        IO.foreach(RikiConfig::USER_FILE) { |line|
            user, pass = line.split
            return pwd == pass if uid == user
        }

        return false
    end

    def Riki.hasLoggedIn?(cgi)
        return true unless RikiConfig::REQUIRE_LOGIN
        cgi.cookies.has_key?("loggedin") && cgi.cookies["loggedin"][0] == 'true'
    end

end