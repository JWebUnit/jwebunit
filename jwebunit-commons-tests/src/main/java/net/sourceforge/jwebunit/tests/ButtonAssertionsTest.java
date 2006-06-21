package net.sourceforge.jwebunit.tests;

public class ButtonAssertionsTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testAssertButtonwithOneFormfound() {
        beginAt("/pagewithOneform.html");
        assertButtonPresent("button1");
    }

    public void testAssertButtonwithTowFormsfound() {
        beginAt("/pagewithTowforms.html");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
    }

}
