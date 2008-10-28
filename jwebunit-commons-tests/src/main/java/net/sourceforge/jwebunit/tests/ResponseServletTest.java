/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

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
     * 
    public void testDefault() {
        beginAt("/SimpleForm.html");
        submit();
        assertResponseCodeBetween(200, 299);
    }

    public void testResponse200() {
        beginAt("/SimpleForm.html");
        setTextField("status", "200");
        submit();
        assertResponseCode(200);
    }
    */

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

}
