package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.JWebUnitTest;

/**
 * Test the assertions provided by WebTestCase using the PseudoServer test package provided
 * by Russell Gold in httpunit.
 *
 * @author Wilkes Joiner
 * @author Jim Weaver
 */
public class WebAssertionsTest extends JWebUnitTest {
    public WebAssertionsTest(String s) throws Exception {
        super(s);
    }

    public void setUp() throws Exception {
        super.setUp();
        addTestPage();
        beginAt("/testPage.html");
    }

    public void testAssertTitleEquals() throws Throwable {
        assertPass("assertTitleEquals", new String[]{"testPage"});
        assertFail("assertTitleEquals", "wrong title");
    }

    public void testAssertTextPresent() throws Throwable {
        assertPassFail("assertTextPresent", "This is a test page.", "no such text");
    }

    public void testAssertTextNotPresent() throws Throwable {
        assertPassFail("assertTextNotPresent", "no such text", "This is a test page.");
    }

    public void testAssertLinkPresentWithText() throws Throwable {
        assertPassFail("assertLinkPresentWithText", "test link", "no such link");
    }

    public void testAssertLinkNotPresentWithText() throws Throwable {
        assertPassFail("assertLinkNotPresentWithText", "no such link", "test link");
    }

    public void testAssertLinkPresent() throws Throwable {
        assertPassFail("assertLinkPresent", "test_link_id", "no_link_id");
    }

    public void testAssertLinkNotPresent() throws Throwable {
        assertPassFail("assertLinkNotPresent", "no_link_id", "test_link_id");
    }

    public void testAssertLinkPresentWithImage() throws Throwable {
        assertPassFail("assertLinkPresentWithImage", "graphic.jpg", "nosuchgraphic.jsp");
    }

    public void testAssertLinkNotPresentWithImage() throws Throwable {
        assertPassFail("assertLinkNotPresentWithImage", "nosuchgraphic.jpg", "graphic.jpg");
    }

    public void testAssertElementPresent() throws Throwable {
        assertElementPresent("row1");
        assertPassFail("assertElementPresent", "span_id", "no_id");
    }

    public void testAssertElementNotPresent() throws Throwable {
        assertPassFail("assertElementNotPresent", "no_id", "span_id");
    }

    public void testAssertElementContainsText() throws Throwable {
        assertTextInElement("span_id", "Span");
        assertTextInElement("span_id", "Text");
        assertTextInElement("span_id", "Span Text");
        assertFail("assertTextInElement", new Object[] {"span_id", "Not Text"});
    }
    public void testAssertElementContainsTextInChild() throws Throwable {
        assertTextInElement("outer_id", "Outer");
        assertTextInElement("outer_id", "Text");
        assertTextInElement("outer_id", "Inner Text");
    }


    private void addTestPage() {
        defineWebPage("testPage", "This is a test page." +
                                  "<table summary=\"testTable\">" +
                                  "<tr ID=\"row1\"><td>table text</td></tr>" +
                                  "<tr><td>table text row 2</td></tr>" +
                                  "<tr><td>table text row 3</td><td>row 3 col 1</td>" +
                                  "<a id=\"test_link_id\" href=\"someurl.html\">test link</a>" +
                                  "<a id=\"test_graphic_link_id\" href=\"someurl2.html\"><img src=\"graphic.jpg\"/></a>" +
                                  "<form>" +
                                  "<input type=\"text\" name=\"testInputElement\" value=\"testValue\"/>" +
                                  "<input type=\"submit\" name=\"submitButton\" value=\"buttonLabel\"/>" +
                                  "<input type=\"checkbox\" name=\"checkboxselected\" CHECKED>" +
                                  "<input type=\"checkbox\" name=\"checkboxnotselected\">" +
                                  "</form>" +
                                  "<span id=\"span_id\">Span Text</span>" +
                                  "<span id=\"outer_id\">Outer <span id=\"inner_id\">Inner Text</span> Text</span>" +
                                  "<form name=\"form2\"></form>" +
                                  "</table>");
        defineWebPage("noFormPage", "");
    }

}
