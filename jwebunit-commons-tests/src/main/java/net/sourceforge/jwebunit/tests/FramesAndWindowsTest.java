/**
 * Copyright (c) 2002-2015, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jwebunit.tests;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

/**
 * User: djoiner
 * Date: Nov 21, 2002
 * Time: 11:35:52 AM
 */
public class FramesAndWindowsTest extends JWebUnitAPITestCase {
    
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
    
    @Test public void testOpenWindow() throws Throwable {
        gotoRootAndOpenChild("ChildPage1");
        assertPassFail("assertWindowPresent", new Object[]{"ChildPage1"}, new Object[]{"NoSuchChild"});
    }   

    @Test
    public void testGotoWindow() {
        gotoRootAndOpenChild("ChildPage1");
        gotoWindow("ChildPage1");
        assertTextPresent("Child Page 1");
    }
    
    @Test
    public void testGotoWindowByTitle() {
        gotoRootAndOpenChild("ChildPage2");
        gotoWindowByTitle("Child Page 2");
        assertTextPresent("This is child 2");
    }
    
    @Test public void testAssertWindowWithTitle() throws Throwable {
        gotoRootAndOpenChild("ChildPage2");
        assertPassFail("assertWindowPresentWithTitle", new Object[]{"Child Page 2"}, new Object[]{"NoSuchTitle"});
    }

    @Test
    public void testCloseWindow() {
        beginAt("RootPage.html");
        assertTitleEquals("This is the Root");
        clickLink("ChildPage1");
        gotoWindow("ChildPage1");
        assertTextPresent("Child Page 1");
        closeWindow();
        assertWindowCountEquals(1);
        assertTitleEquals("This is the Root");
    }

    @Test public void testAssertWindowCountEquals() {
        beginAt("RootPage.html");
        assertWindowCountEquals(1);
        clickLink("ChildPage1");
        assertWindowCountEquals(2);
        gotoWindow("ChildPage1");
        closeWindow();
        assertWindowCountEquals(1);
    }

    // ----------- frames test --------------

    @Test public void testGotoFrame() {
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
    
    @Test public void testGetFrameSource() {
        beginAt("Frames.html");
        assertThat(getPageSource(), containsString("<frameset rows=\"33%, 33%, 33%\">"));
        gotoFrame("TopFrame");
        assertTextPresent("TopFrame");
	}

	@Test public void testGotoFrameById() {
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

    @Test public void testGotoInlineFrame() {
        beginAt("InlineFrame.html");
        assertTextPresent("TopFrame");
        // Is this how it should work? see also the test below
        assertTextNotPresent("ContentFrame");
        gotoFrame("ContentFrame");
        assertTextPresent("ContentFrame"); // only 'ContentFrame' matches frameset tag too
    }

    @Test public void testFormInputInFrame() {
        beginAt("Frames.html");
        gotoFrame("ContentFrame");
        assertFormPresent();
        setTextField("color", "red");
        submit("submit");
        assertTextPresent("color=[red]");
    }

    @Test public void testFormInputInInlineFrame() {
        beginAt("InlineFrame.html");
        gotoFrame("ContentFrame");
        assertFormPresent();
        setTextField("color", "red");
        submit("submit");
        assertTextPresent("color=[red]");
    }

}
