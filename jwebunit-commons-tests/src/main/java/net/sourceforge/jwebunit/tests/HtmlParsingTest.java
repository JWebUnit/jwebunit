/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.locator.HtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocator;
import net.sourceforge.jwebunit.locator.HtmlSubmitInputLocator;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Tests the use of HttpUnit and the types of html documents accepted.
 */
public class HtmlParsingTest extends JWebUnitAPITestCase {

    // URL: getDialog().getWebClient().getCurrentPage().getText()
    
    public static Test suite() {
        Test suite = new TestSuite(HtmlParsingTest.class);
        return new JettySetup(suite);
    }   
    
    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/HtmlTest/");
    }
    
    public void testSimpleForm() {
        beginAt("SimpleForm.html");
        assertElementPresent(new HtmlFormLocator());
        click(new HtmlSubmitInputLocator());
    }

    public void testInvalidForm() {
        beginAt("InvalidForm.html");
        assertElementPresent(new HtmlFormLocator());
        click(new HtmlSubmitInputLocator());
    }

    public void testInvalidFormNoDoctype() {
        beginAt("InvalidFormNoDoctype.html");
        assertElementPresent(new HtmlFormLocator());
        click(new HtmlSubmitInputLocator());
    }    

    public void testValidComplexForm() {
        beginAt("ValidComplexForm.html");
        assertElementPresent(new HtmlFormLocator());
        click(new HtmlSubmitInputLocator());
    }    

    public void testValidFormNoDoctype() {
        beginAt("ValidFormNoDoctype.html");
        assertElementPresent(new HtmlFormLocator());
        click(new HtmlSubmitInputLocator());
    }    
    
    public void testXhtmlStrict() {
        beginAt("XhtmlStrict.html");
        // run a method that gets the DOM
        assertElementPresent(new HtmlElementLocator("div1"));
    }
}
