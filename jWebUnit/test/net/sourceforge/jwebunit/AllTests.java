package net.sourceforge.jwebunit;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.NavigationTest;
import net.sourceforge.jwebunit.FormSubmissionTest;
import net.sourceforge.jwebunit.WebAssertionsTest;
import net.sourceforge.jwebunit.util.reflect.MethodInvokerTest;

/**
 * Test Suite for jWebUnit.
 *
 * @author Wilkes Joiner
 */
public class AllTests extends TestSuite{
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(NavigationTest.class);
        suite.addTestSuite(FormSubmissionTest.class);
        suite.addTestSuite(WebAssertionsTest.class);
        suite.addTestSuite(TestContextTest.class);
        suite.addTestSuite(ExpectedTableTest.class);
        suite.addTestSuite(ExpectedTableAssertionsTest.class);
        suite.addTestSuite(FormAssertionsTest.class);
        suite.addTestSuite(TableAssertionsTest.class);
        suite.addTestSuite(TextAndElementWalkerTest.class);
		suite.addTestSuite(FramesAndWindowsTest.class);
		suite.addTestSuite(JavaScriptEventsTest.class);
		suite.addTestSuite(ServletUnitTest.class);
		suite.addTestSuite(MethodInvokerTest.class);
		suite.addTestSuite(WebCookieTest.class);
		suite.addTestSuite(FormAssertionBug.class);
		suite.addTest(HelloWorldTest.suite());
        return suite;
    }

     public static void main( String[] args ) {
        junit.textui.TestRunner.run( suite() );
    }
}
