/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import java.net.SocketTimeoutException;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test redirection support.
 * 
 * @author Julien Henry
 */
public class ResponseServletTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(ResponseServletTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        setIgnoreFailingStatusCodes(true);	// ignore failing status codes
        getTestContext().setBaseUrl(HOST_PATH + "/ResponseServletTest");
    }

    /*
     * currently we can't get the response code from HtmlUnit unless it is a failing code
     */ 
    public void testDefault() {
        beginAt("/SimpleForm.html");
        submit();
        assertResponseCodeBetween(200, 299);
        
        // test the headers
        assertHeaderPresent("Test");
        assertHeaderNotPresent("Not-present");
        assertHeaderEquals("Test", "test2");
        assertHeaderMatches("Header-Added", "[0-9]{2}");
    }

    public void testResponse200() {
        beginAt("/SimpleForm.html");
        setTextField("status", "200");
        submit();
        assertResponseCode(200);
    }

    /*
     * HtmlUnit cannot handle a 301 without a valid Location: header
    public void testResponse301() {
        beginAt("/SimpleForm.html");
        setTextField("status", "301");
        submit();
        assertResponseCode(301);
    }
     */

    public void testResponse404() {
        beginAt("/SimpleForm.html");
        assertTitleEquals("response form");
        setTextField("status", "404");
        submit();
        assertResponseCode(404);
    }

    public void testResponse501() {
        beginAt("/SimpleForm.html");
        assertTitleEquals("response form");
        setTextField("status", "501");
        submit();
        assertResponseCode(501);
    }
    
    /**
     * Issue 1674646: add support for specifying the timeout of pages
     */
    public void testTimeout() {
    	
        // test that timeout was fired
    	setTimeout(500);			// specify a global timeout of 0.5 seconds (must be set before the WebConnection is initialised)
        beginAt("/SimpleForm.html");
        assertTitleEquals("response form");
        setTextField("timeout", "1");		// server wait for 1 seconds
        try {
        	submit();
        	fail("timeout was not called");	// we should not get here
        } catch (RuntimeException e) {
        	assertTrue("timeout caused by SocketTimeoutException, but was " + e.getCause().getClass(), e.getCause() instanceof SocketTimeoutException);
        }
        
        // close and reset the browser
        closeBrowser();
        
        // test that timeout wasn't fired 
        setTimeout(2000);			// specify a global timeout of 2 seconds (must be set before the WebConnection is initialised)
        beginAt("/SimpleForm.html");
        assertTitleEquals("response form");
        setTextField("timeout", "1");		// server wait for 1 seconds
        submit();
        assertTextPresent("hello, world!");
    }

}
