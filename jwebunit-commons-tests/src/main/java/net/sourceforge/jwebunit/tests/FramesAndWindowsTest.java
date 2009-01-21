/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * User: djoiner
 * Date: Nov 21, 2002
 * Time: 11:35:52 AM
 */

public class FramesAndWindowsTest extends JWebUnitAPITestCase {
    
    public static Test suite() {
        Test suite = new TestSuite(FramesAndWindowsTest.class);
        return new JettySetup(suite);
    }   
    
    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/FramesAndWindowsTest");
    }
    
    /**
     * helper function
     * @param childName
     */
    private void gotoRootAndOpenChild(String childName) {
        beginAt("RootPage.html");
        clickLink(childName);
    }
    
    // ------------ windows test ------------    
    
    public void testOpenWindow() throws Throwable {
        gotoRootAndOpenChild("ChildPage1");
        assertPassFail("assertWindowPresent", new Object[]{"ChildPage1"}, new Object[]{"NoSuchChild"});
    }   

    public void testGotoWindow() {
        gotoRootAndOpenChild("ChildPage1");
        gotoWindow("ChildPage1");
        assertTextPresent("child 1");
    }
    
    public void testGotoWindowByID() {
        gotoRootAndOpenChild("ChildPage3");
        gotoWindow(1);
        assertTextPresent("child 3");
    }

    public void testGotoWindowByTitle() {
        gotoRootAndOpenChild("ChildPage2");
        gotoWindowByTitle("Child Page 2");
        assertTextPresent("child 2");
    }
    
    public void testAssertWindowWithTitle() throws Throwable {
        gotoRootAndOpenChild("ChildPage2");
        assertPassFail("assertWindowPresentWithTitle", new Object[]{"Child Page 2"}, new Object[]{"NoSuchTitle"});
    }

    public void testSwitchWindows() {
        gotoRootAndOpenChild("ChildPage1");
        gotoWindow("ChildPage1");
        gotoRootWindow();
        assertTextPresent("This is the Root");
    }

    public void testCloseWindow() {
        beginAt("RootPage.html");
        assertTitleEquals("This is the Root");
        clickLink("ChildPage1");
        gotoWindow("ChildPage1");
        assertTextPresent("child 1");
        closeWindow();
        assertWindowCountEquals(1);
        assertTitleEquals("This is the Root");
    }

    public void testAssertWindowCountEquals() {
        beginAt("RootPage.html");
        assertWindowCountEquals(1);
        clickLink("ChildPage1");
        assertWindowCountEquals(2);
        gotoWindow("ChildPage1");
        closeWindow();
        assertWindowCountEquals(1);
    }

    // ----------- frames test --------------

    public void testGotoFrame() {
        beginAt("Frames.html");
		assertFramePresent("TopFrame");
        gotoFrame("TopFrame");
        assertTextPresent("TopFrame");
        gotoRootWindow();
        assertFramePresent("BottomFrame");
        gotoFrame("BottomFrame");
        assertTextPresent("BottomFrame");
        gotoRootWindow();
		assertFramePresent("ContentFrame");
        gotoFrame("ContentFrame");
        assertTextPresent("ContentFrame");
        assertException(RuntimeException.class, "gotoFrame",
				new Object[] { "BottomFrame" });
	}
    
    public void testGetFrameSource() {
        beginAt("Frames.html");
        assertTrue(getPageSource().indexOf("<frameset rows=\"33%, 33%, 33%\">")>=0);
        gotoFrame("TopFrame");
        assertEquals("<html><body>TopFrame</body></html>", getPageSource());
        assertTextPresent("TopFrame");
	}

	public void testGotoFrameById() {
        beginAt("Frames.html");
        assertFramePresent("frame1");
        gotoFrame("frame1");
        assertTextPresent("TopFrame");
        gotoRootWindow();
        assertFramePresent("frame3");
        gotoFrame("frame3");
        assertTextPresent("BottomFrame");
        gotoRootWindow();
        assertFramePresent("frame2");
        gotoFrame("frame2");
        assertTextPresent("ContentFrame");
        assertException(RuntimeException.class, "gotoFrame", new Object[] { "TopFrame" });
    }

    public void testGotoInlineFrame() {
        beginAt("InlineFrame.html");
        assertTextPresent("TopFrame");
        // Is this how it should work? see also the test below
        assertTextNotPresent("ContentFrame");
        gotoFrame("ContentFrame");
        assertTextPresent("ContentFrame"); // only 'ContentFrame' matches frameset tag too
    }

    public void testFormInputInFrame() {
        beginAt("Frames.html");
        gotoFrame("ContentFrame");
        assertFormPresent();
        setTextField("color", "red");
        submit("submit");
        assertTextPresent("color=red" + System.getProperty("line.separator"));
    }

    public void testFormInputInInlineFrame() {
        beginAt("InlineFrame.html");
        gotoFrame("ContentFrame");
        assertFormPresent();
        setTextField("color", "red");
        submit("submit");
        assertTextPresent("color=red" + System.getProperty("line.separator"));
    }

    //TODO this just posts to a new frameset inside the frame, is the test needed?
    public void testFormInputInFrameToFrame() {
		beginAt("Frames.html");
		gotoFrame("ContentFrame");
        setTextField("color", "green");
		submit();
		assertTitleEquals("Submitted parameters");
	}
    
}
