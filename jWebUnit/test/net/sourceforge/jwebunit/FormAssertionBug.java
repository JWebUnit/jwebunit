package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.JWebUnitTest;

/**
 *
 * User: hienqnguyen
 * Date: Mar 4, 2004
 * Time: 10:10:41 PM
 * To change this template use Options | File Templates.
 */
public class FormAssertionBug extends JWebUnitTest
{
    public void setUp() throws Exception
    {
        super.setUp();

        addTestPage();
        addIFramePage();
        beginAt("formAssertionWithIFrameBug.html");
    }

    private void addIFramePage()
    {
        defineWebPage("iFrameWithForm","This is the IFRAME content " +
            "<form name='formInIFrame' method='post' action=''>" +
                "<input type='text' name='nestedTextField'></input>" +
            "</form>"
        );
    }

    private void addTestPage() {
        super.defineWebPage("formAssertionWithIFrameBug",
            "This is the main page with a form and an IFRAME " +
            "<form name='formInMainPage' method='post' action=''>" +
                "<input type='text' name='topTextField'></input>" +
            "</form>"    +
            "<IFRAME name='iFrameWithForm' src='/iFrameWithForm.html'/>"
        );
    }

    public void testNestedFormElementInFramePresent() throws Throwable
    {
        assertPassFail("assertFormPresent", "formInMainPage", "noSuchForm");
        assertPassFail("assertFormElementPresent", "topTextField", "noSuchElement");
        gotoFrame("iFrameWithForm");
        assertPassFail("assertFormPresent", "formInIFrame", "noSuchForm");
        assertPassFail("assertFormElementPresent", "nestedTextField", "noSuchElement");
    }
}
