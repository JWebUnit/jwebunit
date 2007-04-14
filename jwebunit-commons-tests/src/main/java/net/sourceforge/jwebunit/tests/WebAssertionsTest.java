/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test the assertions provided by WebTestCase using the PseudoServer test
 * package provided by Russell Gold in httpunit.
 * 
 * @author Wilkes Joiner
 * @author Jim Weaver
 */
public class WebAssertionsTest extends JWebUnitAPITestCase {

	public static Test suite() {
		Test suite = new TestSuite(WebAssertionsTest.class);
		return new JettySetup(suite);
	}

	public void setUp() throws Exception {
		super.setUp();
		getTestContext().setBaseUrl(HOST_PATH + "/WebAssertionsTest");
		beginAt("/testPage.html");
	}

    public void testAssertTitleEquals() throws Throwable {
        assertPass("assertTitleEquals", new String[] { "testPage" });
        assertFail("assertTitleEquals", "wrong title");
    }

    public void testAssertTitleMatch() throws Throwable {
        assertPass("assertTitleMatch", new String[] { "test[Pp]age" });
        assertFail("assertTitleMatch", "[Ww]rong title");
    }

	public void testAssertTextPresent() throws Throwable {
		assertPassFail("assertTextPresent", "This is a test.",
				"no such text");
	}
    public void testAssertMatch() throws Throwable {
        assertPassFail("assertMatch", "This (is)* a .* test.", "no.*text");
    }

	public void testAssertTextNotPresent() throws Throwable {
		assertTextNotPresent("no such text");
		//assertPassFail("assertTextNotPresent", "no such text",
		//		"This is a test page.");
	}

    public void testAssertNoMatch() throws Throwable {
        assertNoMatch("no.*text");
        //assertPassFail("assertNoMatch", "no.*text", "This (is)* a .* page.");
    }

	public void testAssertLinkPresentWithText() throws Throwable {
		assertPassFail("assertLinkPresentWithText", "test link", "no such link");
	}

	public void testAssertLinkNotPresentWithText() throws Throwable {
		assertPassFail("assertLinkNotPresentWithText", "no such link",
				"test link");
	}

	public void testAssertLinkPresentWithTextN() throws Throwable {
		assertPass("assertLinkPresentWithText", new Object[] { "test link",
				new Integer(0) });
		assertFail("assertLinkPresentWithText", new Object[] { "test link",
				new Integer(1) });
	}

	public void testAssertLinkNotPresentWithTextN() throws Throwable {
		assertPass("assertLinkNotPresentWithText", new Object[] { "test link",
				new Integer(1) });
		assertFail("assertLinkNotPresentWithText", new Object[] { "test link",
				new Integer(0) });
	}

	public void testAssertLinkPresent() throws Throwable {
		assertPassFail("assertLinkPresent", "test_link_id", "no_link_id");
	}

	public void testAssertLinkNotPresent() throws Throwable {
		assertPassFail("assertLinkNotPresent", "no_link_id", "test_link_id");
	}

	public void testAssertLinkPresentWithImage() throws Throwable {
		assertPassFail("assertLinkPresentWithImage", "graphic.jpg",
				"nosuchgraphic.jsp");
	}

	public void testAssertLinkNotPresentWithImage() throws Throwable {
		assertPassFail("assertLinkNotPresentWithImage", "nosuchgraphic.jpg",
				"graphic.jpg");
	}

	public void testAssertElementPresent() throws Throwable {
		assertElementPresent("row1");
		assertPassFail("assertElementPresent", "span_id", "no_id");
	}

	public void testAssertElementNotPresent() throws Throwable {
		assertPassFail("assertElementNotPresent", "no_id", "span_id");
	}

	public void testAssertTextNotInElement() throws Throwable {
		assertTextNotInElement("outer_id", "nosuchtext");
		assertTextNotInElement("inner_id", "Outer");
		assertFail("assertTextNotInElement",
				new Object[] { "outer_id", "Outer" });
	}

	public void testAssertElementContainsText() throws Throwable {
		assertTextInElement("span_id", "Span");
		assertTextInElement("span_id", "Text");
		assertTextInElement("span_id", "Span Text");
        assertTextInElement("span_empty", "");
		assertFail("assertTextInElement",
				new Object[] { "span_id", "Not Text" });
	}

	public void testAssertElementContainsTextInChild() throws Throwable {
		assertTextInElement("outer_id", "Outer");
		assertTextInElement("outer_id", "Text");
		assertTextInElement("outer_id", "Inner Text");
		assertTextInElement("outer2", "$100,000/$300,000");
	}

    public void testAssertNoMatchInElement() throws Throwable {
        assertNoMatchInElement("outer_id", "no[Ss]uchtext");
        assertNoMatchInElement("inner_id", "Out+er");
        assertFail("assertNoMatchInElement", new Object[] {"outer_id", "Out+er"});
    }

    public void testAssertMatchInElement() throws Throwable {
        assertMatchInElement("span_id", "Sp[Aa]n");
        assertMatchInElement("span_id", "Te+xt");
        assertMatchInElement("span_id", "Span\\sText");
        assertFail("assertMatchInElement", new Object[] {"span_id", "Not.*Text"});
    }
    
    public void testAssertMatchInElementChild() throws Throwable {
        assertMatchInElement("outer_id", "Out+er");
        assertMatchInElement("outer_id", "Texx*t");
        assertMatchInElement("outer_id", "Inner.*Text");
    }

    /** 
     * @deprecated
     */
    public void testAssertFormElementEquals() throws Throwable {
        assertFormElementEquals("testInputElement", "testValue");
        assertFail("assertFormElementEquals", new Object[] {"testInputElement", "AnotherValue"});
    }
    
    public void testAssertTextFieldEquals() throws Throwable {
        assertTextFieldEquals("testInputElement", "testValue");
        assertFail("assertTextFieldEquals", new Object[] {"testInputElement", "AnotherValue"});
    }
    
    public void testAssertHiddenFieldPresent() throws Throwable {
        assertHiddenFieldPresent("hidden", "h");
        assertFail("assertHiddenFieldPresent", new Object[] {"hidden", "AnotherValue"});
    }

    public void testAssertFormElementMatch() throws Throwable {
        assertFormElementMatch("testInputElement", "test[Vv]alue");
        assertFail("assertFormElementMatch", new Object[] {"testInputElement", "Another[Vv]alue"});
    }

    public void testAssertSelectedOptionEquals() throws Throwable {
        assertSelectedOptionEquals("testSelect", "Value1");
        assertFail("assertSelectedOptionEquals", new Object[] {"testSelect", "AnotherValue"});
    }

    public void testAssertSelectedOptionMatch() throws Throwable {
        assertSelectedOptionMatches("testSelect", "[Vv]alue1");
        assertFail("assertSelectedOptionMatches", new Object[] {"testSelect", "Another[Vv]alue"});
    }


}
