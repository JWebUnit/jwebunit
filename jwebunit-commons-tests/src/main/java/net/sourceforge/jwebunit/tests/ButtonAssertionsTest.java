/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

public class ButtonAssertionsTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/ButtonAssertionsTest");
    }

    public void testAssertButtonwithOneFormfound() {
        beginAt("/pageWithOneForm.html");
        assertButtonPresent("button1");
        assertButtonPresent("buttonOutside");
        setWorkingForm("form1");
        assertButtonPresent("button1");
        assertButtonPresent("buttonOutside");
        assertButtonPresent("button1");
        assertButtonPresent("buttonOutside");
        
        // test for issue 1874971 
        assertButtonPresentWithText("Input button");
    }

    public void testAssertButtonwithTowFormsfound() {
        beginAt("/pageWithTwoForms.html");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
        setWorkingForm("form1");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
        setWorkingForm("form2");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
    }
    
    public void testAssertButtonWithText() {
        beginAt("/pageWithTwoForms.html");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
        setWorkingForm("form1");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
        assertButtonPresentWithText("the submit btn");
        assertButtonPresentWithText("the reset btn");
        assertButtonPresentWithText("the btn btn");
        setWorkingForm("form2");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
    }

}
