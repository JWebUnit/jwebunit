/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

/**
 * User: djoiner
 * Date: Nov 21, 2002
 * Time: 11:35:52 AM
 */

public class FramesAndWindowsTest extends JWebUnitTest {

    public void setUp() throws Exception {
        super.setUp();
        defineWebPage("RootPage",
                "This is the Root" +
                "<form name='realform'><input name='color' value='blue'></form>" +
                "<a id=\"SetColorGreen\" href='nothing.html' onClick=\"JavaScript:document.realform.color.value='green';return false;\">green</a>" +
                "<a id=\"ChildPage1\" href=\"\" onClick=\"JavaScript:window.open('ChildPage1.html', 'ChildPage1');\">green</a>" +
                "<a id=\"ChildPage2\">Child Page 2</a>\n");
        defineWebPage("ChildPage1", "This is child 1");
        defineWebPage("ChildPage2", "This is child 2");
        defineWebPage("Frames", "<html><frameset rows=\"33%, 33%, 33%\"><frame name=\"TopFrame\" src=\"TopFrame.html\"><frame name=\"ContentFrame\" src=\"ContentFrame.html\"><frame name=\"BottomFrame\" src=\"BottomFrame.html\"></frameset></html>");
        defineWebPage("TopFrame", "<html><body>TopFrame</body></html>");
        defineWebPage("ContentFrame", "<html><body>ContentFrame" +
                        "<form name='frameForm' method ='GET' action='TargetPage'>" +
                        "  <input name='color' value='blue'>" +
                        "  <input type='submit'>" +
                        "</form></body></html>");
        defineWebPage("BottomFrame", "<html><body>BottomFrame</body></html>");
        defineResource("TargetPage?color=red", "<html><body>This is the red page</html></body>");
    }

    public void testOpenWindow() throws Throwable {
        gotoRootAndOpenChild("ChildPage1");
        assertPassFail("assertWindowPresent", new Object[]{"ChildPage1"}, new Object[]{"NoSuchChild"});
    }

    //todo: move to javascript events test
    public void testGreenLink() {
        beginAt("RootPage.html");
        assertFormElementEquals("color", "blue");
        clickLink("SetColorGreen");
        assertFormElementEquals("color", "green");
    }

    public void testGotoWindow() {
        gotoRootAndOpenChild("ChildPage1");
        gotoWindow("ChildPage1");
        assertTextPresent("child 1");
    }

    public void testSwitchWindows() {
        gotoRootAndOpenChild("ChildPage1");
        gotoWindow("ChildPage1");
        gotoRootWindow();
        assertTextPresent("This is the Root");
    }

    private void gotoRootAndOpenChild(String childName) {
        beginAt("RootPage.html");
        clickLink(childName);
    }

    public void testGotoFrame() {
        beginAt("Frames.html");
        gotoFrame("TopFrame");
        assertTextPresent("TopFrame");
        gotoFrame("BottomFrame");
        assertTextPresent("BottomFrame");
        gotoFrame("ContentFrame");
        assertTextPresent("ContentFrame");
    }

    public void testFormInputInFrame() {
        beginAt("Frames.html");
        gotoFrame("ContentFrame");
        setFormElement("color", "red");
        submit();
        assertTextPresent("This is the red page");
    }
}
