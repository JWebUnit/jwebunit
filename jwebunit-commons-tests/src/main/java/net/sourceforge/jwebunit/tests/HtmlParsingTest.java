/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Tests the use of HttpUnit and the types of html documents accepted.
 */
public class HtmlParsingTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(HtmlParsingTest.class);
        return new JettySetup(suite);
    }   
    
    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/HtmlTest/");
    }
    
    public void testSimpleForm() {
        beginAt("SimpleForm.html");
        assertFormPresent();
        submit();
    }

    public void testInvalidForm() {
        beginAt("InvalidForm.html");
        assertFormPresent();
        submit();
    }

    public void testInvalidFormNoDoctype() {
        beginAt("InvalidFormNoDoctype.html");
        assertFormPresent();
        submit();
    }    

    public void testValidComplexForm() {
        beginAt("ValidComplexForm.html");
        assertFormPresent();
        submit();
    }    

    public void testValidFormNoDoctype() {
        beginAt("ValidFormNoDoctype.html");
        assertFormPresent();
        submit();
    }    
    
    public void testXhtmlStrict() {
        beginAt("XhtmlStrict.html");
        // run a method that gets the DOM
        assertElementPresent("div1");
        //TODO This test gives a "org.w3c.dom.DOMException: NOT_SUPPORTED_ERR" with nekohtml 0.9.5 and httpunit
    }
}
