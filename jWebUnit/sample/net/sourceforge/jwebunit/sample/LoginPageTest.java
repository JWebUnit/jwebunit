/*
 * User: djoiner
 * Date: May 31, 2002
 * Time: 4:41:11 PM
 */
package net.sourceforge.jwebunit.sample;

import net.meterware.httpunit.HttpUnitOptions;
import net.meterware.httpunit.PseudoServer;
import net.meterware.httpunit.PseudoServlet;
import net.meterware.httpunit.WebResource;
import net.sourceforge.jwebunit.WebTestCase;


public class LoginPageTest extends WebTestCase {

    private static final String VALID_UID = "validuid";
    private static final String VALID_PWD = "validpwd";
    private static final String INVALID_UID = "invaliduid";
    private static final String INVALID_PWD = "invalidpwd";
    private PseudoServer server;
    private String hostPath;


    public LoginPageTest(String name) {
        super(name, "http://localhost:80", null);
    }

    public void setUp() throws Exception {
        server = new PseudoServer();
        HttpUnitOptions.reset();
        hostPath = "http://localhost:" + server.getConnectedPort();
        setBaseUrl(hostPath);
        defineResouces();
    }

    public void tearDown() throws Exception {
        if (server != null) server.shutDown();
    }

    public void testInitialState() {
        beginAt("/");
        assertFormControlPresent("username");
        assertFormControlPresent("password");
        assertSubmitButtonPresent("login");
    }

    public void testValidLogin() {
        loginAs(VALID_UID, VALID_PWD);
        assertTitleEquals("Logged In");
    }

    public void testInvalidUsername() {
        loginAs(INVALID_UID, VALID_PWD);
        assertTitleEquals("Login");
        assertTextInResponse("Invalid Login");
    }

    public void testInvalidPassword() {
        loginAs(VALID_UID, INVALID_PWD);
        assertTitleEquals("Login");
        assertTextInResponse("Invalid Login");
    }

    private void loginAs(String user, String pass) {
        beginAt("/");
        setFormParameter("username", user);
        setFormParameter("password", pass);
        submit("login");
    }

    private void defineResouces() {
        server.setResource("/",
                           "<html>" +
                           "<head>" +
                           "	<title>Login</title>" +
                           "</head>" +
                           "<body>" +
                           "    <form action=\"login\" method=\"post\">" +
                           "    	<table align=\"center\">" +
                           "    		<tr>" +
                           "    			<td>Username:</td>" +
                           "    			<td><input type=\"text\" length=\"15\" name=\"username\"/></td>" +
                           "    		</tr>" +
                           "    		<tr>" +
                           "    			<td>Password:</td>" +
                           "    			<td><input type=\"password\" length=\"15\" name=\"password\"/></td>" +
                           "    		</tr>" +
                           "    		<tr>" +
                           "    			<td colspan=\"10\" align=\"center\"><input type=\"submit\" value=\"Login\" name=\"login\"/></td>" +
                           "    		</tr>" +
                           "    </table>" +
                           "    </form>" +
                           "</body>" +
                           "</html>");

        server.setResource("login", new PseudoServlet() {
            public WebResource getPostResponse() {
                String uid = getParameter("username")[0];
                String pwd = getParameter("password")[0];
                if (uid.equals(VALID_UID) && pwd.equals(VALID_PWD)) {
                    return new WebResource("<html><head><title>Logged In</title></head><body></body></html>");
                }
                return new WebResource("<html><head><title>Login</title></head><body>Invalid Login</body></html>");
            }
        });
    }


}

