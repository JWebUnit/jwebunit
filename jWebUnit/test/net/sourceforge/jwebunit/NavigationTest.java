package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.util.JettySetup;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test url navigation methods on WebTestCase (starting at a url, navigating
 * links).
 * 
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class NavigationTest extends WebTestCase {

    private String hostPath;

    public static Test suite() {
        Test suite = new TestSuite(NavigationTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        hostPath = "http://localhost:8081/jwebunit";
        getTestContext().setBaseUrl(hostPath + "/NavigationTest");
    }

    public void testBeginAt() {
        beginAt("/blah.html");
    }

    public void testForwardSlashConfusion() throws Exception {
        beginAt("/blah.html");
        beginAt("blah.html");
        getTestContext().setBaseUrl(hostPath + "/NavigationTest/");
        beginAt("/blah.html");
        beginAt("blah.html");
    }

    public void testInvalidBeginAt() {
        try {
            beginAt("/nosuchresource.html");
            fail("Expected exception");
        } catch (Throwable t) {
        }
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
        } catch (AssertionFailedError expected) {
            assertEquals("Link with text [an active link] and index 2 "
                    + "not found in response.", expected.getMessage());
        }
        assertTitleEquals("pageWithLink");
    }

    public void testClickLinkWithTextAfterText() {
        beginAt("/pageWithLinkWithTextAfterText.html");
        clickLinkWithTextAfterText("link text", "First:");
        assertTitleEquals("targetPage");

        beginAt("/pageWithLinkWithTextAfterText.html");
        clickLinkWithTextAfterText("link text", "Second:");
        assertTitleEquals("targetPage2");
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

    public void testGotoPage() {
        beginAt("/targetPage.html");
        assertTitleEquals("targetPage");
        gotoPage("/targetPage2.html");
        assertTitleEquals("targetPage2");
    }
}