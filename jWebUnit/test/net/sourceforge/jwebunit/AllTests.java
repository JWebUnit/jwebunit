package net.sourceforge.jwebunit;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.util.JettySetup;
import net.sourceforge.jwebunit.util.reflect.MethodInvokerTest;

/**
 * Test Suite for jWebUnit.
 * 
 * @author Wilkes Joiner
 */
public class AllTests extends TestSuite {

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
		TestSuite suite = new TestSuite();
		suite.addTestSuite(NavigationTest.class);

		//major work. requires using jsp / servlet request response cycles.
		//suite.addTestSuite(FormSubmissionTest.class);

		suite.addTestSuite(WebAssertionsTest.class);
		suite.addTestSuite(TestContextTest.class);
		suite.addTestSuite(ExpectedTableTest.class);
		suite.addTestSuite(ExpectedTableAssertionsTest.class);
		suite.addTestSuite(FormAssertionsTest.class);
		suite.addTestSuite(TableAssertionsTest.class);
		suite.addTestSuite(TextAndElementWalkerTest.class);
		
		//not moved over to using jetty / JWebUnitAPITestCase
		//suite.addTestSuite(FramesAndWindowsTest.class);

		//not moved over to using jetty / JWebUnitAPITestCase
		//suite.addTestSuite(JavaScriptEventsTest.class);
		
		suite.addTestSuite(MethodInvokerTest.class);
		suite.addTestSuite(WebCookieTest.class);

		//not moved over to using jetty / JWebUnitAPITestCase
		//suite.addTestSuite(FormAssertionBug.class);
		
		suite.addTestSuite(HelloWorldTest.class);
		return new JettySetup(suite);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}