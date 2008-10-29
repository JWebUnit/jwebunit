/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.selenium;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;
import net.sourceforge.jwebunit.tests.*;

/**
 * Test Suite for JWebUnit.
 * 
 * @author Julien Henry
 */
public class JWebUnitTest extends TestCase {

    /**
     * Runs all the tests for JWebUnit. Add each new TestCase by using the <code>addTestSuite(Class)</code> method, so
     * that the TestCase's <code>suite</code> method <strong>isn't called </strong>. This prevents
     * <code>JettySetup</code> from starting the Jetty server twice and consequently the error 'port 80xx is already
     * in use'.
     * 
     * @return the <code>TestSuite</code> containing all the tests for JWebUnit ready to run utilizing Jetty as
     *         testserver.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.sourceforge.jwebunit");
        // $JUnit-BEGIN$
        suite.addTestSuite(FormSubmissionTest.class);
        suite.addTestSuite(WebAssertionsTest.class);
        suite.addTestSuite(FramesAndWindowsTest.class);
        suite.addTestSuite(TableAssertionsTest.class);
        suite.addTestSuite(ExpectedTableAssertionsHtmlTest.class);
        suite.addTestSuite(ExpectedTableAssertionsXHtmlTest.class);
        suite.addTestSuite(JavaScriptEventsTest.class);
        suite.addTestSuite(JavaScriptTest.class);
        suite.addTestSuite(HelloWorldTest.class);
        suite.addTestSuite(HtmlParsingTest.class);
        suite.addTestSuite(WebCookieTest.class);
        suite.addTestSuite(TestContextTest.class);
        suite.addTestSuite(FormAssertionsTest.class);
        suite.addTestSuite(NavigationTest.class);
        suite.addTestSuite(XPathTest.class);
        suite.addTestSuite(CharsetTest.class);
        suite.addTestSuite(ButtonAssertionsTest.class);
        suite.addTestSuite(NonHtmlContentTest.class);
        suite.addTestSuite(RedirectionTest.class);
        suite.addTestSuite(ImageTest.class);
        suite.addTestSuite(ResourceBundleAssertionsTest.class);
        suite.addTestSuite(IElementTest.class);
        suite.addTestSuite(ResponseServletTest.class);
        suite.addTestSuite(CustomTesterTest.class);
        //suite.addTest(JUnitPerfTest.suite());
        // $JUnit-END$
        return new JettySetup(suite);
    }

}
