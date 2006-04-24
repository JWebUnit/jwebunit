module Riki
    class RikiPage
        attr_reader :page, :title, :body, :summary, :action, :header, :logout
        attr_accessor :loggedIn

        def initialize(cgi, page)
            @cgi = cgi
            @page = page
            @title, @body, @summary, @action, @logout = '', '', '', '', ''
            @cookies = []
        end

        def addCookie(aCookie)
            @cookies << aCookie
        end

        def run
            #override for pre-display processing
        end

        def displayHash
            par = {}
            par['page' ] = page
            par['title'] = title
            par['body'] = body
            par['summary'] = summary
            par['action'] = action
            par['login'] = login
            return par
        end

        def login
            return "" unless RikiConfig::REQUIRE_LOGIN
            if userLoggedIn?
                %Q[<a id="logout" href="wiki.rb?page=#{page}&mode=logout">Logout</a>]
            else
                %Q[<a id="login" href="wiki.rb?page=#{page}&mode=login">Login</a>]
            end
        end

        def display
            run
            par = displayHash
            template = readFile("template.html")
            template.gsub!(/\$(\w+)/) { par.has_key?($1) ? par[$1] : '' }
            @cgi.out('cookie' => @cookies) { template }
        end

        def readFile(file)
            IO.readlines(file.untaint).join
        end

        def inspectStr(obj)
            "<!-- #{obj.inspect} -->"
        end

        def param(key, default='')
            Riki.param(@cgi, key, default)
        end

        def userLoggedIn?
            return true unless RikiConfig::REQUIRE_LOGIN
            @loggedIn.nil? ? Riki.hasLoggedIn?(@cgi) : @loggedIn
        end

    end
end