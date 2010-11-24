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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormElementEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormElementMatch;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertHiddenFieldPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertMatchInElement;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertNoMatch;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertNoMatchInElement;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertSelectedOptionEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertSelectedOptionMatches;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextFieldEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextInElement;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextNotInElement;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test the assertions provided by WebTestCase using the PseudoServer test
 * package provided by Russell Gold in httpunit.
 * 
 * @author Wilkes Joiner
 * @author Jim Weaver
 */
public class WebAssertionsTest extends JWebUnitAPITestCase {

	public void setUp() throws Exception {
		super.setUp();
		setBaseUrl(HOST_PATH + "/WebAssertionsTest");
		beginAt("/testPage.html");
	}

    @Test
    public void testAssertTitleEquals() throws Throwable {
        assertPass("assertTitleEquals", new String[] { "testPage" });
        assertFail("assertTitleEquals", "wrong title");
    }

    @Test
    public void testAssertTitleMatch() throws Throwable {
        assertPass("assertTitleMatch", new String[] { "test[Pp]age" });
        assertFail("assertTitleMatch", "[Ww]rong title");
    }

    @Test
    public void testAssertTitleNotEquals() throws Throwable {
        assertPass("assertTitleNotEquals", new String[] { "wrong title" });
        assertFail("assertTitleNotEquals", "testPage");
    }

    @Test
    public void testAssertTextPresent() throws Throwable {
        assertPassFail("assertTextPresent", "This is a test.", "no such text");
    }

    @Test
    public void testAssertMatch() throws Throwable {
        assertPassFail("assertMatch", "This (is)* a .* test.", "no.*text");
    }

    @Test
    public void testAssertTextNotPresent() throws Throwable {
        assertPassFail("assertTextNotPresent", "no such text", "This is a test.");
    }

    @Test public void testAssertNoMatch() throws Throwable {
        assertPassFail("assertNoMatch", "no.*text", "This (is)* a .* test.");
    }
    
    /**
     * Check that {@link #assertNoMatch(String)} can actually fail.
     */
    @Test(expected=AssertionError.class)
    public void testAssertNoMatchFails() throws Throwable {
        // 'Span Text' definitely exists in the source page text
        assertNoMatch("Span Text");
    }

	@Test public void testAssertLinkPresentWithText() throws Throwable {
		assertPassFail("assertLinkPresentWithText", "test link", "no such link");
	}

	@Test public void testAssertLinkNotPresentWithText() throws Throwable {
		assertPassFail("assertLinkNotPresentWithText", "no such link",
				"test link");
	}

	@Test public void testAssertLinkPresentWithTextN() throws Throwable {
		assertPass("assertLinkPresentWithText", new Object[] { "test link",
				Integer.valueOf(0) });
		assertFail("assertLinkPresentWithText", new Object[] { "test link",
				Integer.valueOf(1) });
	}

	@Test public void testAssertLinkNotPresentWithTextN() throws Throwable {
		assertPass("assertLinkNotPresentWithText", new Object[] { "test link",
				new Integer(1) });
		assertFail("assertLinkNotPresentWithText", new Object[] { "test link",
				Integer.valueOf(0) });
	}

	@Test public void testAssertLinkPresent() throws Throwable {
		assertPassFail("assertLinkPresent", "test_link_id", "no_link_id");
	}

	@Test public void testAssertLinkNotPresent() throws Throwable {
		assertPassFail("assertLinkNotPresent", "no_link_id", "test_link_id");
	}

	@Test public void testAssertLinkPresentWithImage() throws Throwable {
		assertPassFail("assertLinkPresentWithImage", "graphic.jpg",
				"nosuchgraphic.jsp");
	}

	@Test public void testAssertLinkNotPresentWithImage() throws Throwable {
		assertPassFail("assertLinkNotPresentWithImage", "nosuchgraphic.jpg",
				"graphic.jpg");
	}

	@Test public void testAssertElementPresent() throws Throwable {
		assertElementPresent("row1");
		assertPassFail("assertElementPresent", "span_id", "no_id");
	}

	@Test public void testAssertElementNotPresent() throws Throwable {
		assertPassFail("assertElementNotPresent", "no_id", "span_id");
	}

	@Test public void testAssertTextNotInElement() throws Throwable {
		assertTextNotInElement("outer_id", "nosuchtext");
		assertTextNotInElement("inner_id", "Outer");
		assertFail("assertTextNotInElement",
				new Object[] { "outer_id", "Outer" });
	}

	@Test public void testAssertElementContainsText() throws Throwable {
		assertTextInElement("span_id", "Span");
		assertTextInElement("span_id", "Text");
		assertTextInElement("span_id", "Span Text");
        assertTextInElement("span_empty", "");
		assertFail("assertTextInElement",
				new Object[] { "span_id", "Not Text" });
	}

	@Test public void testAssertElementContainsTextInChild() throws Throwable {
		assertTextInElement("outer_id", "Outer");
		assertTextInElement("outer_id", "Text");
		assertTextInElement("outer_id", "Inner Text");
		assertTextInElement("outer2", "$100,000/$300,000");
	}

    @Test public void testAssertNoMatchInElement() throws Throwable {
        assertNoMatchInElement("outer_id", "no[Ss]uchtext");
        assertNoMatchInElement("inner_id", "Out+er");
        assertFail("assertNoMatchInElement", new Object[] {"outer_id", "Out+er"});
    }

    @Test public void testAssertMatchInElement() throws Throwable {
        assertMatchInElement("span_id", "Sp[Aa]n");
        assertMatchInElement("span_id", "Te+xt");
        assertMatchInElement("span_id", "Span\\sText");
        assertFail("assertMatchInElement", new Object[] {"span_id", "Not.*Text"});
    }
    
    @Test public void testAssertMatchInElementChild() throws Throwable {
        assertMatchInElement("outer_id", "Out+er");
        assertMatchInElement("outer_id", "Texx*t");
        assertMatchInElement("outer_id", "Inner.*Text");
    }

    /** 
     * @deprecated
     */
    @Test public void testAssertFormElementEquals() throws Throwable {
        assertFormElementEquals("testInputElement", "testValue");
        assertFail("assertFormElementEquals", new Object[] {"testInputElement", "AnotherValue"});
    }
    
    @Test public void testAssertTextFieldEquals() throws Throwable {
        assertTextFieldEquals("testInputElement", "testValue");
        assertFail("assertTextFieldEquals", new Object[] {"testInputElement", "AnotherValue"});
    }
    
    @Test public void testAssertHiddenFieldPresent() throws Throwable {
        assertHiddenFieldPresent("hidden", "h");
        assertFail("assertHiddenFieldPresent", new Object[] {"hidden", "AnotherValue"});
    }

    @Test public void testAssertFormElementMatch() throws Throwable {
        assertFormElementMatch("testInputElement", "test[Vv]alue");
        assertFail("assertFormElementMatch", new Object[] {"testInputElement", "Another[Vv]alue"});
    }

    @Test public void testAssertSelectedOptionEquals() throws Throwable {
        assertSelectedOptionEquals("testSelect", "Value1");
        assertFail("assertSelectedOptionEquals", new Object[] {"testSelect", "AnotherValue"});
    }

    @Test public void testAssertSelectedOptionMatch() throws Throwable {
        assertSelectedOptionMatches("testSelect", "[Vv]alue1");
        assertFail("assertSelectedOptionMatches", new Object[] {"testSelect", "Another[Vv]alue"});
    }


}
