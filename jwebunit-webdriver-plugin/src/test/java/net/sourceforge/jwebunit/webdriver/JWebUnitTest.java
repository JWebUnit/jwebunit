/**
 * Copyright (c) 2002-2015, JWebUnit team.
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
package net.sourceforge.jwebunit.webdriver;

import net.sourceforge.jwebunit.tests.ButtonAssertionsTest;
import net.sourceforge.jwebunit.tests.CharsetTest;
import net.sourceforge.jwebunit.tests.ConcurrentJWebUnitTest;
import net.sourceforge.jwebunit.tests.CustomTesterTest;
import net.sourceforge.jwebunit.tests.ExpectedTableAssertionsHtmlTest;
import net.sourceforge.jwebunit.tests.ExpectedTableAssertionsXHtmlTest;
import net.sourceforge.jwebunit.tests.FormAssertionsTest;
import net.sourceforge.jwebunit.tests.FormSubmissionTest;
import net.sourceforge.jwebunit.tests.FramesAndWindowsTest;
import net.sourceforge.jwebunit.tests.HelloWorldTest;
import net.sourceforge.jwebunit.tests.HtmlParsingTest;
import net.sourceforge.jwebunit.tests.IElementTest;
import net.sourceforge.jwebunit.tests.ImageTest;
import net.sourceforge.jwebunit.tests.JavaScriptEventsTest;
import net.sourceforge.jwebunit.tests.JavaScriptTest;
import net.sourceforge.jwebunit.tests.NavigationTest;
import net.sourceforge.jwebunit.tests.NonHtmlContentTest;
import net.sourceforge.jwebunit.tests.ResourceBundleAssertionsTest;
import net.sourceforge.jwebunit.tests.ResponseServletTest;
import net.sourceforge.jwebunit.tests.SelectOptionsTest;
import net.sourceforge.jwebunit.tests.TableAssertionsTest;
import net.sourceforge.jwebunit.tests.TestContextTest;
import net.sourceforge.jwebunit.tests.WebAssertionsTest;
import net.sourceforge.jwebunit.tests.WebCookieTest;
import net.sourceforge.jwebunit.tests.XPathTest;
import net.sourceforge.jwebunit.tests.util.JettySetup;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
    //RedirectionTest.class,
    ImageTest.class,
    ResourceBundleAssertionsTest.class,
    SelectOptionsTest.class,
    IElementTest.class,
    ResponseServletTest.class,
    CustomTesterTest.class,
    ConcurrentJWebUnitTest.class
})
public class JWebUnitTest extends JettySetup {
    
    @AfterClass
    public static void tearDown() throws Exception {
        shutdown();
    }

}
