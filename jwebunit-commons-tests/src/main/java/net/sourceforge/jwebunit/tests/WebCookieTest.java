package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test the Cookies provided by WebTestCase using the PseudoServer test package
 * provided by Russell Gold in httpunit.
 * 
 * @author Vivek Venugopalan
 */
public class WebCookieTest extends JWebUnitAPITestCase {


	public static Test suite() {
		return new JettySetup(new TestSuite(WebCookieTest.class));
	}

    public void setUp() throws Exception {
        super.setUp();
		getTestContext().addCookie("cookie1", "Cookievalue1");
		getTestContext().setBaseUrl(HOST_PATH + "/ExpectedTableAssertionsTest");
		beginAt("/ExpectedTableAssertionsTestPage.html");
    }
    
	public void testAssertCookieDump() throws Throwable {
		//dumpCookies();
	}
}