/**
 * Copyright (c) 2011, JWebUnit team.
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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresentWithExactText;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresentWithText;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithExactText;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithText;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;

import org.junit.Test;

/**
 * Test url navigation methods on WebTestCase (starting at a url, navigating
 * links).
 * 
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class NavigationTest extends JWebUnitAPITestCase {

    @Before
	public void setUp() throws Exception {
		super.setUp();
		setBaseUrl(HOST_PATH + "/NavigationTest");
	}

	@Test
	public void testBeginAtRelative() {
	    beginAt("/blah.html");
	}

	@Test
	public void testBeginAtAbsolute() {
	    beginAt(HOST_PATH + "/NavigationTest/blah.html");
	}

	@Test
	public void testForwardSlashConfusion() throws Exception {
	    beginAt("/blah.html");
	    beginAt("blah.html");
		getTestContext().setBaseUrl(HOST_PATH + "/NavigationTest/");
		beginAt("/blah.html");
		beginAt("blah.html");
	}

	@Test
	public void testInvalidBeginAt() {
		//the testing engines should throw an exception if a 404 Error is found.
		TestingEngineResponseException e = assertException(TestingEngineResponseException.class, "beginAt", new Object[] {"/nosuchresource.html"});
		assertEquals(404, e.getHttpStatusCode());
	}
	
	@Test
	public void testClickLinkWithText() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		clickLinkWithText("an active link");
		assertTitleEquals("targetPage");
	}

	@Test 
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

	@Test
	public void testClickLinkWithImage() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		clickLinkWithImage("graphic.jpg");
		assertTitleEquals("targetPage2");
	}

    @Test
    public void testClickLinkWithImageAnd0Index() {
        beginAt("/pageWithLink.html");
        assertTitleEquals("pageWithLink");

        clickLinkWithImage("graphic.jpg", 0);
        assertTitleEquals("targetPage2");
    }

    @Test
    public void testClickLinkWithImageAnd1Index() {
        beginAt("/pageWithLink.html");
        assertTitleEquals("pageWithLink");

        clickLinkWithImage("graphic.jpg", 1);
        assertTitleEquals("targetPage");
    }

    @Test
    public void testClickLinkByID() {
		beginAt("/pageWithLink.html");
		assertTitleEquals("pageWithLink");

		clickLink("activeID");
		assertTitleEquals("targetPage");
	}

	@Test
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

	@Test
	public void testGotoPageRelative() {
		beginAt("/targetPage.html");
		assertTitleEquals("targetPage");
		gotoPage("/targetPage2.html");
		assertTitleEquals("targetPage2");
	}
	
	@Test
	public void testGotoPageAbsolute() {
		beginAt("/targetPage.html");
		assertTitleEquals("targetPage");
                gotoPage(HOST_PATH + "/NavigationTest/targetPage2.html");
		assertTitleEquals("targetPage2");
	}

	@Test
	public void testInvalidGotoPage() {
		beginAt("/targetPage.html");
		//the testing engines should throw an exception if a 404 Error is found.
		TestingEngineResponseException e = assertException(TestingEngineResponseException.class, "gotoPage", new Object[] {"/nosuchresource.html"});
		assertEquals(404, e.getHttpStatusCode());
	}

	//For bug 726143
	@Test
	public void testLinkWithEscapedText() {
		beginAt("/pageWithAmpersandInLink.html");
		assertLinkPresentWithText("Map & Directions");
		clickLinkWithText("Map & Directions");
		assertTitleEquals("targetPage");
	}
	
	/**
	 * Testing for issue 996031
	 */
	@Test
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