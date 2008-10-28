/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test the IElement interface
 *
 * @author jmwright
 */
public class IElementTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(IElementTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/IElementTest");
        beginAt("/template.html");
    }
    
    public void testSimple() {
    	// test an element that exists
    	IElement element = getElementByXPath("//input[@id='test']");
    	assertNotNull(element);
    	assertEquals(element.name(), "input");
    	assertEquals(element.attribute("name"), "element_name");
    	assertEquals(element.attribute("id"), "test");
    	assertEquals(element.attribute("value"), "test3");
    }

    public void testMissing() {
    	// a missing element should throw an exception
    	try {
    		IElement element = getElementByXPath("//input[@id='test2']");
    		fail("getElementByXPath() should have thrown an assertion exception.");
    	} catch (AssertionFailedError e) {
    		// nothing
    	}
    }

    // TODO: expand API for IElement
    // TODO: test cases to change the element and make sure XPath has changed
    
}
