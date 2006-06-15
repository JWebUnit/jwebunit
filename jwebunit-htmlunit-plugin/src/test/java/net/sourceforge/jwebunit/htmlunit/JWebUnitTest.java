package net.sourceforge.jwebunit.htmlunit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.TestingEngineRegistry;
import net.sourceforge.jwebunit.tests.util.JettySetup;
import net.sourceforge.jwebunit.tests.*;

/**
 * Test Suite for jWebUnit.
 * 
 * @author Julien Henry
 * @author Wilkes Joiner
 */
public class JWebUnitTest extends TestCase {

	/**
	 * Runs all the tests for jWebUnit. Add each new TestCase by using the
	 * <code>addTestSuite(Class)</code> method, so that the TestCase's
	 * <code>suite</code> method <strong>isn't called </strong>. This prevents
	 * <code>JettySetup</code> from starting the Jetty server twice and
	 * consequently the error 'port 80xx is already in use'.
	 * 
	 * @return the <code>TestSuite</code> containing all the tests for
	 *         jWebUnit ready to run utilizing Jetty as testserver.
	 */
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.sourceforge.jwebunit");
        //$JUnit-BEGIN$
        suite.addTestSuite(FormSubmissionTest.class);
        suite.addTestSuite(WebAssertionsTest.class);
        suite.addTestSuite(FramesAndWindowsTest.class);
        //suite.addTestSuite(FormSubmissionCheckboxesTest.class); (**Label fonctions are not supported by HtmlUnit)
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
        //$JUnit-END$
        return new JettySetup(suite);
    }

}