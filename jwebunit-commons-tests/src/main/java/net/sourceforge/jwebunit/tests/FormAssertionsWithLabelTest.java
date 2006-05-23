/*
 * User: djoiner
 * Date: Sep 9, 2002
 * Time: 3:15:10 PM
 */
package net.sourceforge.jwebunit.tests;

import net.sourceforge.jwebunit.tests.util.JettySetup;
import junit.framework.Test;
import junit.framework.TestSuite;

public class FormAssertionsWithLabelTest extends JWebUnitAPITestCase {
	
    public static Test suite() {
        Test suite = new TestSuite(FormAssertionsWithLabelTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/FormAssertionsTest");
    }

    public void testAssertFormParameterPresentWithLabel() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementPresentWithLabel", "Test Input", "No Such Label");
        assertFail("assertFormElementPresentWithLabel", "This is a test page");
    }

    public void testAssertFormParameterNotPresentWithLabel() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementNotPresentWithLabel", "No Such Label", "Test Input");
        assertPass("assertFormElementNotPresentWithLabel", "This is a test page");
    }
}
