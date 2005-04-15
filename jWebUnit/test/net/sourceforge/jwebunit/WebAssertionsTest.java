package net.sourceforge.jwebunit;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.util.JettySetup;

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

	public void testAssertTextPresent() throws Throwable {
		assertPassFail("assertTextPresent", "This is a test.",
				"no such text");
	}

	public void testAssertTextNotPresent() throws Throwable {
		assertTextNotPresent("no such text");
		//assertPassFail("assertTextNotPresent", "no such text",
		//		"This is a test page.");
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
		assertFail("assertTextInElement",
				new Object[] { "span_id", "Not Text" });
	}

	public void testAssertElementContainsTextInChild() throws Throwable {
		assertTextInElement("outer_id", "Outer");
		assertTextInElement("outer_id", "Text");
		assertTextInElement("outer_id", "Inner Text");
		assertTextInElement("outer2", "$100,000/$300,000");
	}

}