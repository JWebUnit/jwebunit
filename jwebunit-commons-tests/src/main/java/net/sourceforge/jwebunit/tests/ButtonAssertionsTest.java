package net.sourceforge.jwebunit.tests;

public class ButtonAssertionsTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH+"/ButtonAssertionsTest");
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

}
