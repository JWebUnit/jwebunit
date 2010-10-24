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
        setBaseUrl(HOST_PATH + "/XPathTest");
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
    
    public void testGetElementTextByXPath() throws Throwable {
        beginAt("/testPage.html");        
        assertEquals("test link", getElementTextByXPath("//tr//a[contains(@href,\"next\")]"));
    }
}
