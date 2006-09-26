/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import net.sourceforge.jwebunit.locator.HtmlButtonLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocatorByName;

public class ButtonAssertionsTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH+"/ButtonAssertionsTest");
    }

    public void testAssertButtonwithOneFormfound() {
        beginAt("/pageWithOneForm.html");
        assertElementPresent(new HtmlButtonLocator("button1"));
        assertElementPresent(new HtmlButtonLocator("buttonOutside"));
        setWorkingForm(new HtmlFormLocatorByName("form1"));
        assertElementPresent(new HtmlButtonLocator("button1"));
        assertElementPresent(new HtmlButtonLocator("buttonOutside"));
        assertElementPresent(new HtmlButtonLocator("button1"));
        assertElementPresent(new HtmlButtonLocator("buttonOutside"));
    }

    public void testAssertButtonwithTowFormsfound() {
        beginAt("/pageWithTwoForms.html");
        assertElementPresent(new HtmlButtonLocator("button1"));
        assertElementPresent(new HtmlButtonLocator("button2"));
        assertElementPresent(new HtmlButtonLocator("buttonOutside"));
        setWorkingForm(new HtmlFormLocatorByName("form1"));
        assertElementPresent(new HtmlButtonLocator("button1"));
        assertElementPresent(new HtmlButtonLocator("button2"));
        assertElementPresent(new HtmlButtonLocator("buttonOutside"));
        setWorkingForm(new HtmlFormLocatorByName("form2"));
        assertElementPresent(new HtmlButtonLocator("button1"));
        assertElementPresent(new HtmlButtonLocator("button2"));
        assertElementPresent(new HtmlButtonLocator("buttonOutside"));
        assertElementPresent(new HtmlButtonLocator("button1"));
        assertElementPresent(new HtmlButtonLocator("button2"));
        assertElementPresent(new HtmlButtonLocator("buttonOutside"));
    }

}
