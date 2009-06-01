/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test the Cookies methods provided by WebTestCase.
 * 
 * @author Julien HENRY
 */
public class WebCookieTest extends JWebUnitAPITestCase {


	public static Test suite() {
		return new JettySetup(new TestSuite(WebCookieTest.class));
	}

    public void setUp() throws Exception {
        super.setUp();
		getTestContext().addCookie("cookie1", "Cookievalue1", "localhost");
		setBaseUrl(HOST_PATH);
    }
    
    public void testAddCookie() {
    	beginAt("/cookies.jsp");
    	assertTextPresent("cookie1=Cookievalue1");
    }
    
    public void testAddAnotherCookie() {
    	getTestContext().addCookie("cookie2", "Cookievalue2", "localhost");
    	beginAt("/cookies.jsp");
    	assertTextPresent("cookie1=Cookievalue1");
    	assertTextPresent("cookie2=Cookievalue2");
    }

    public void testAssertCookiePresent() throws Throwable {
    	beginAt("/cookies.jsp");
    	assertCookiePresent("serveurCookie");
	}

    public void testAssertCookieValue() throws Throwable {
    	beginAt("/cookies.jsp");
    	assertCookieValueEquals("serveurCookie", "foo");
	}

    public void testAssertCookieMatch() throws Throwable {
    	beginAt("/cookies.jsp");
    	assertCookieValueMatch("serveurCookie", "fo*");
	}
    
    /**
     * Test that the cookie still exists across multiple requests,
     * even if the cookie is not explicitly set each time.
     */
    public void testCookieWithoutExplicitSet() {
    	beginAt("/cookies.jsp");		// beginAt also resets cookies
    	assertCookieValueEquals("serveurCookie", "foo");
    	gotoPage("/cookies.jsp?dont_set=1");
    	assertCookieValueEquals("serveurCookie", "foo");	// should still be there
    	gotoPage("/cookies.jsp?dont_set=1");
    	assertCookieValueEquals("serveurCookie", "foo");	// should still be there
    	gotoPage("/cookies.jsp?dont_set=1");
    	assertCookieValueEquals("serveurCookie", "foo");	// should still be there
    	
    	
    }
    
}