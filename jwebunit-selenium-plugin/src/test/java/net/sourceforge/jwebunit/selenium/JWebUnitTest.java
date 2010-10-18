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

package net.sourceforge.jwebunit.selenium;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
@RunWith(Suite.class)
@Suite.SuiteClasses({
    FormSubmissionTest.class,
    WebAssertionsTest.class,
    FramesAndWindowsTest.class,
    TableAssertionsTest.class,
    ExpectedTableAssertionsHtmlTest.class,
    ExpectedTableAssertionsXHtmlTest.class,
    JavaScriptEventsTest.class,
    JavaScriptTest.class,
    HelloWorldTest.class,
    HtmlParsingTest.class,
    WebCookieTest.class,
    TestContextTest.class,
    FormAssertionsTest.class,
    NavigationTest.class,
    XPathTest.class,
    CharsetTest.class,
    ButtonAssertionsTest.class,
    NonHtmlContentTest.class,
    RedirectionTest.class,
    ImageTest.class,
    ResourceBundleAssertionsTest.class,
    SelectOptionsTest.class,
    IElementTest.class,
    ResponseServletTest.class,
    CustomTesterTest.class,
    JUnitPerfTest.class
})
public class JWebUnitTest extends JettySetup {

}
