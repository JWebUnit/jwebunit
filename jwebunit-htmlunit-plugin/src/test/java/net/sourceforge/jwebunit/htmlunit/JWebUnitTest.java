/**
 * Copyright (c) 2010, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */


package net.sourceforge.jwebunit.htmlunit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.*;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test Suite for JWebUnit.
 * 
 * @author Julien Henry
 * @author Wilkes Joiner
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
        suite.addTestSuite(SelectOptionsTest.class);
        suite.addTestSuite(IElementTest.class);
        suite.addTestSuite(ResponseServletTest.class);
        suite.addTestSuite(CustomTesterTest.class);
        suite.addTest(JUnitPerfTest.suite());
        // $JUnit-END$
        return new JettySetup(suite);
    }

}
