package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.JWebUnitTest;

/**
 * Test url navigation methods on WebTestCase (starting at a url, navigating links).
 *
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class NavigationTest extends JWebUnitTest {

    public NavigationTest(String s) throws Exception {
        super(s);
    }

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

    public void testClickLink() {
        gotoLinkTestPage();
        clickLinkWithText("an active link");
        assertTitleEquals("targetPage");
    }

    public void testClickLinkByID() {
        gotoLinkTestPage();
        clickLink("activeID");
        assertTitleEquals("targetPage");
    }

    private void gotoLinkTestPage() {
        defineWebPage("pageWithLink", "<a href=\"/targetPage.html\" id=\"activeID\">an <b>active</b> link</A>\n");
        defineWebPage("targetPage", "");
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
