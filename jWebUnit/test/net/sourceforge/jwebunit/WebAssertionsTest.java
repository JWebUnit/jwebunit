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
    public WebAssertionsTest(String s) {
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

    public void testAssertTextInResponse() throws Throwable {
        assertPassFail("assertTextInResponse", "This is a test page.", "no such text");
    }

    public void testAssertTextNotInResponse() throws Throwable {
        assertPassFail("assertTextNotInResponse", "no such text", "This is a test page.");
    }

    public void testAssertLinkInResponse() throws Throwable {
        assertPassFail("assertLinkInResponse", "test link", "no such link");
    }

    public void testAssertLinkNotInResponse() throws Throwable {
        assertPassFail("assertLinkNotInResponse", "no such link", "test link");
    }

    private void addTestPage() {
        defineWebPage("testPage", "This is a test page." +
                                  "<table summary=\"testTable\">" +
                                  "<tr><td>table text</td></tr>" +
                                  "<tr><td>table text row 2</td></tr>" +
                                  "<tr><td>table text row 3</td><td>row 3 col 1</td>" +
                                  "<a href=\"someurl.html\">test link</a>" +
                                  "<form>" +
                                  "<input type=\"text\" name=\"testInputElement\" value=\"testValue\"/>" +
                                  "<input type=\"submit\" name=\"submitButton\" value=\"buttonLabel\"/>" +
                                  "<input type=\"checkbox\" name=\"checkboxselected\" CHECKED>" +
                                  "<input type=\"checkbox\" name=\"checkboxnotselected\">" +
                                  "</form>" +
                                  "<form name=\"form2\"></form>" +
                                  "</table>");
        defineWebPage("noFormPage", "");
    }

}
