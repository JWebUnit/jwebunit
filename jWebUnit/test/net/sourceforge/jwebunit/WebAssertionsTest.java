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

    public void testAssertLinkPresentWithTextN() throws Throwable {
        assertPass(
            "assertLinkPresentWithText",
            new Object[] { "test link", new Integer(0)});
        assertFail(
            "assertLinkPresentWithText",
            new Object[] { "test link", new Integer(1)});
    }

    public void testAssertLinkNotPresentWithTextN() throws Throwable {
        assertPass(
            "assertLinkNotPresentWithText",
            new Object[] { "test link", new Integer(1)});
        assertFail(
            "assertLinkNotPresentWithText",
            new Object[] { "test link", new Integer(0)});
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
    
    public void testAssertTextNotInElement() throws Throwable {
        assertTextNotInElement("outer_id", "nosuchtext");
        assertTextNotInElement("inner_id", "Outer");
        assertFail("assertTextNotInElement", new Object[] {"outer_id", "Outer"});
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
        assertTextInElement("outer2", "$100,000/$300,000");
    }


    private void addTestPage() {
        defineWebPage("testPage", "This is a test page." +
                                  "<table summary=\"testTable\">" +
                                  "<tr ID=\"row1\"><td>table text</td></tr>" +
                                  "<tr><td>table text row 2</td></tr>" +
                                  "<tr><td>table text row 3</td><td>row 3 col 1</td>" +
                                  "<a id=\"test_link_id\" href=\"someurl.html\">test link</a>" +
								  /* Changed this page structure to test bug ID  908372 */
								  "<a href=\"somepage.html\">" +
								  "<img height=\"22\" width=\"22\" alt=\"New User\" src=\"firstimage.gif\">" +
                                  "<img src=\"graphic.jpg\"alt=\"New User\" /></a>" +
                                  "<form>" +
                                  "<input type=\"text\" name=\"testInputElement\" value=\"testValue\"/>" +
                                  "<input type=\"submit\" name=\"submitButton\" value=\"buttonLabel\"/>" +
                                  "<input type=\"checkbox\" name=\"checkboxselected\" CHECKED>" +
                                  "<input type=\"checkbox\" name=\"checkboxnotselected\">" +
                                  "</form>" +
                                  "<span id=\"span_id\">Span Text</span>" +
                                  "<span id=\"outer_id\">Outer <span id=\"inner_id\">Inner Text</span> Text</span>" +
                                  "<form name=\"form2\"></form>" +
                                  "<form name=\"form3\">" +
								  "<table><tr><td>" +
								  "<span id=\"outer2\" >" +
								  "<input type=\"hidden\" name=\"hidden\" value=\"h\" />$100,000/$300,000</span>" +
								  "</td></tr></table>" + "</table>" +
								  "</form>");
        defineWebPage("noFormPage", "");
    }

}
