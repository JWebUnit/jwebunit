/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.util.JettySetup;

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
        getTestContext().setBaseUrl(HOST_PATH + "/FramesAndWindowsTest");
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
        beginAt("RootPage.html");
        gotoRootAndOpenChild("ChildPage1");
        gotoWindow("ChildPage1");
        assertTextPresent("child 1");
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

    // ----------- frames test --------------

    public void testGotoFrame() {
        beginAt("Frames.html");
		assertFramePresent("TopFrame");
        gotoFrame("TopFrame");
        assertTextPresent("TopFrame");
		assertFramePresent("BottomFrame");
        gotoFrame("BottomFrame");
        assertTextPresent("BottomFrame");
		assertFramePresent("ContentFrame");
        gotoFrame("ContentFrame");
        assertTextPresent("ContentFrame");
    }

	public void testGotoInlineFrame() {
		beginAt("InlineFrame.html");
		assertTextPresent("TopFrame");
        // Is this how it should work? see also the test below
        assertTextNotPresent("<p>ContentFrame</p>");
		gotoFrame("ContentFrame");
		assertTextPresent("<p>ContentFrame</p>"); // only 'ContentFrame' matches frameset tag too
	}

    public void testFormInputInFrame() {
        beginAt("Frames.html");
        gotoFrame("ContentFrame");
        assertFormPresent();
        setFormElement("color", "red");
        submit("submit");
        // TODO should it bee nessecary to select frame again?
        gotoFrame("ContentFrame");
        assertTextPresent(" color=red ");
    }

	/* this just posts to a new frameset inside the frame, is the test needed?
    public void testFormInputInFrameToFrame() {
		beginAt("Frames.html");
		gotoFrame("ContentFrame");
		setFormElement("color", "green");
		submit();
		assertTitleEquals("Frames2");
	} */
    
}
