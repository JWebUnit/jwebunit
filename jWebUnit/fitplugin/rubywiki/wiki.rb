#!ruby
## Copyright 1994-2000, Cunningham & Cunningham, Inc.
## in collaboration with Dave W. Smith
## Open Source for personal use only.
## ... and then only
## with the understanding that the owner(s) cannot be
## responsible for any behavior of the program or
## any damages that it may cause. See LICENSE.TXT

## straight up port from Ward's perl quicki wiki to ruby
## plus support for tables
$: << '.'
require 'riki.rb'
require 'cgi'

cgi = CGI.new
page = Riki.param(cgi, 'page', 'WelcomeVisitors')
mode = Riki.param(cgi, 'mode')
pageClass = case mode
                when 'edit'    then Riki::EditPage
                when 'save'    then Riki::SavePage
                when 'search'  then Riki::SearchPage
                when 'run'     then Riki::RunPage
                when 'login'   then Riki::LoginPage
                when 'changes' then Riki::ChangesPage
                else               Riki::ViewPage
            end


pageClass = Riki::ChangesPage if page == 'RecentChanges'
thePage = pageClass.new(cgi, page.untaint)

if(mode == 'auth') then
    uid = Riki.param(cgi, 'username', nil)
    pwd = Riki.param(cgi, 'password', nil)
    if Riki.authorized?(uid, pwd)
        thePage.addCookie(CGI::Cookie.new('loggedin', 'true'))
        thePage.loggedIn = true
    else
        thePage = Riki::LoginPage.new(cgi, page.untaint)
        thePage.error = "Invalid username or password"
    end
end

if(mode == 'edit' && !Riki.hasLoggedIn?(cgi))
    thePage = Riki::LoginPage.new(cgi, page.untaint)
    thePage.error = "You must login to edit a page."
end

if(mode == 'logout')
    thePage.addCookie(CGI::Cookie.new('loggedin', 'false'))
    thePage.loggedIn = false
end

thePage.display