package net.sourceforge.jwebunit;

import junit.framework.AssertionFailedError;

/**
 * Test url navigation methods on WebTestCase (starting at a url, navigating links).
 *
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class NavigationTest extends JWebUnitTest {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testBeginAt() {
        defineResource("/blah.html", "");
        beginAt("/blah.html");
    }

    public void testForwardSlashConfusion() throws Exception {
        defineWebPage("/app/blah", "here");
        getTestContext().setBaseUrl(hostPath + "/app");
        beginAt("/blah.html");
        beginAt("blah.html");
        getTestContext().setBaseUrl(hostPath + "/app/");
        beginAt("/blah.html");
        beginAt("blah.html");
    }

    public void testInvalidBeginAt() {
        defineResource("/", "");
        try {
            beginAt("/nosuchresource.html");
        } catch (Throwable t) {
            return;
        }
        fail("Expected exception");
    }

    public void testClickLinkWithText() {
        gotoLinkTestPage();
        clickLinkWithText("an active link");
        assertTitleEquals("targetPage");
    }

    public void testClickLinkWithTextN() {
        gotoLinkTestPage();
        clickLinkWithText("an active link", 0);
        assertTitleEquals("targetPage");
        beginAt("/pageWithLink.html");
        clickLinkWithText("an active link", 1);
        assertTitleEquals("targetPage2");
        beginAt("/pageWithLink.html");
        try {
            clickLinkWithText("an active link", 2);
            fail();
        } catch (AssertionFailedError expected) {
            assertEquals(
                "Link with text [an active link] and index 2 "
                    + "not found in response.",
                expected.getMessage());
        }
        assertTitleEquals("pageWithLink");
    }

    public void testClickLinkWithTextAfterText() {
        defineWebPage("pageWithLink",
            "First: <a href=\"/targetPage1.html\">link text</a>\n" +
            "Second: <a href=\"/targetPage2.html\">link text</a>)\n");
        defineWebPage("targetPage1", "");
        defineWebPage("targetPage2", "");

        beginAt("/pageWithLink.html");
        clickLinkWithTextAfterText("link text", "First:");
        assertTitleEquals("targetPage1");

        beginAt("/pageWithLink.html");
        clickLinkWithTextAfterText("link text", "Second:");
        assertTitleEquals("targetPage2");
    }

    public void testClickLinkWithImage() {
        gotoLinkTestPage();
        clickLinkWithImage("graphic.jpg");
        assertTitleEquals("targetPage2");
    }

    public void testClickLinkByID() {
        gotoLinkTestPage();
        clickLink("activeID");
        assertTitleEquals("targetPage");
    }

    private void gotoLinkTestPage() {
        defineWebPage("pageWithLink",
            "<a href=\"/targetPage.html\" id=\"activeID\">an <b>active</b> link</A>\n" +
            "<a href=\"/targetPage2.html\"><img src=\"graphic.jpg\"/></a>)\n" +
            "<a href=\"/targetPage2.html\">an active <i>link</i></a>\n");
        defineWebPage("targetPage", "");
        defineWebPage("targetPage2", "");
        beginAt("/pageWithLink.html");
        assertTitleEquals("pageWithLink");
    }

    public void testInvalidClickLink() {
        gotoLinkTestPage();
        try {
            clickLinkWithText("no such link");
        } catch (Throwable t) {
            return;
        }
        fail("Expected exception");
    }

}
