/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
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
