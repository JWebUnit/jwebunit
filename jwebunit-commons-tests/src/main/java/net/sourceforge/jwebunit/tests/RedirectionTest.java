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
public class RedirectionTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(RedirectionTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/RedirectionTest");
    }

    public void testRedirection() {
        beginAt("/redirect.html");
        assertTitleEquals("Redirected");
        closeBrowser();
        beginAt(HOST_PATH + "/redirect.jsp");
        assertTitleEquals("Redirected");
    }

}
