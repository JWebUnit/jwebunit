/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import net.sourceforge.jwebunit.tests.util.JettySetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test all methods that use XPath
 * @author Julien Henry
 */
public class XPathTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(XPathTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/XPathTest");
    }

    public void testAssertElementPresentByXPath() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail(
                "assertElementPresentByXPath",
                "//tr[contains(.//a/@href,\"next\") and contains(string(),\"test link\")]",
                "//InvalidXPath");
    }

    public void testAssertElementNotPresentByXPath() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertElementNotPresentByXPath", "//InvalidXPath",
                "//tr[contains(.//a/@href,\"next\") and contains(string(),\"test link\")]");
    }

    public void testClickElementByXPath() throws Throwable {
        beginAt("/testPage.html");
        clickElementByXPath("//tr//a[contains(@href,\"next\") and contains(string(),\"test link\")]");
        assertTitleEquals("Next");
    }
}
