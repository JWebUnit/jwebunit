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

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.tests.util.JettySetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test url navigation methods on WebTestCase (starting at a url, navigating
 * links).
 * 
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class NavigationTest extends JWebUnitAPITestCase {

	public static Test suite() {
		Test suite = new TestSuite(NavigationTest.class);
		return new JettySetup(suite);
	}

	public void setUp() throws Exception {
		super.setUp();
		setBaseUrl(HOST_PATH + "/NavigationTest");
	}

	public void testBeginAtRelative() {
	    beginAt("/blah.html");
	}

	public void testBeginAtAbsolute() {
	    beginAt(HOST_PATH + "/NavigationTest/blah.html");
	}

	public void testForwardSlashConfusion() throws Exception {
	    beginAt("/blah.html");
	    beginAt("blah.html");
		getTestContext().setBaseUrl(HOST_PATH + "/NavigationTest/");
		beginAt("/blah.html");
		beginAt("blah.html");
	}

	public void testInvalidBeginAt() {

		//the testing engines should throw an exception if a 404 Error is found.
        assertException(TestingEngineResponseException.class, "beginAt", new Object[] {"/nosuchresource.html"});

	}

	public void testClickLinkWithText() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		clickLinkWithText("an active link");
		assertTitleEquals("targetPage");
	}

	public void testClickLinkWithTextN() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		clickLinkWithText("an active link", 0);
		assertTitleEquals("targetPage");

		beginAt("/pageWithLink.html");
		clickLinkWithText("an active link", 1);

		assertTitleEquals("targetPage2");
		beginAt("/pageWithLink.html");
		try {
			clickLinkWithText("an active link", 2);
			fail();
		} catch (AssertionError expected) {
			assertEquals("Link with text [an active link] and index [2] "
					+ "not found in response.", expected.getMessage());
		}
		assertTitleEquals("pageWithLink");
	}

	public void testClickLinkWithImage() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		clickLinkWithImage("graphic.jpg");
		assertTitleEquals("targetPage2");
	}

	public void testClickLinkByID() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		clickLink("activeID");
		assertTitleEquals("targetPage");
	}

	public void testInvalidClickLink() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		try {
			clickLinkWithText("no such link");
		} catch (Throwable t) {
			return;
		}
		fail("Expected exception");
	}

	public void testGotoPageRelative() {
		beginAt("/targetPage.html");
		assertTitleEquals("targetPage");
		gotoPage("/targetPage2.html");
		assertTitleEquals("targetPage2");
	}

	public void testGotoPageAbsolute() {
		beginAt("/targetPage.html");
		assertTitleEquals("targetPage");
                gotoPage(HOST_PATH + "/NavigationTest/targetPage2.html");
		assertTitleEquals("targetPage2");
	}

	//For bug 726143
	public void testLinkWithEscapedText() {
		beginAt("/pageWithAmpersandInLink.html");
		assertLinkPresentWithText("Map & Directions");
		clickLinkWithText("Map & Directions");
		assertTitleEquals("targetPage");
	}
	
	/**
	 * Testing for issue 996031
	 */
	public void testLinkExactText() {
		beginAt("/test1.html");
		assertTitleEquals("test1");
		assertLinkPresentWithExactText("one");
		assertLinkPresentWithExactText("tone");
		clickLinkWithExactText("one");
		assertTitleEquals("test2");
		
		// the following should fail
		boolean passed = false;
		try {
			clickLinkWithExactText("doesn't exist");
		} catch (AssertionError e) {
			// expected
			passed = true;
		}
		assertTrue("non-existant link should throw an error", passed);
	}
	
}