module Riki
    class LoginPage < RikiPage
        attr_accessor :error
        def title
            "Login Page"
        end

        def login
            ""
        end

        def body
            %Q[<span class="error">#{@error}</span>
            <form id="action" method="POST" action="wiki.rb">
                #{Riki.hiddenField("page", page)}
                #{Riki.hiddenField("mode", "auth")}
                <table>
                <tr><td align="right">Username:</td><td><input name="username" type="text"/></td></tr>
                <tr><td align="right">Password:</td><td><input name="password" type="password"/></td></tr>
                <tr><td colspan="2" align="center"><input name="actionButton" type="submit" value=" Login "></td></tr>
                </table>
            </form>
            ]
        end
    end
end