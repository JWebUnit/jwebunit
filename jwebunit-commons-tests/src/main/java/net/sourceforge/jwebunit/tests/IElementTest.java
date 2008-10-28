/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import java.util.Date;
import java.util.List;

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
    	assertEquals(element.getName(), "input");
    	assertEquals(element.getAttribute("name"), "element_name");
    	assertEquals(element.getAttribute("id"), "test");
    	assertEquals(element.getAttribute("value"), "test3");
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
    
    /**
     * Test parent, child methods
     */
    public void testChildren() {
    	assertElementPresent("first");
    	IElement element = getElementById("first");
    	assertEquals(element.getName(), "li");
    	assertEquals(element.getTextContent(), "one");
    	assertEquals(element.getAttribute("id"), "first");
    	
    	// parent should be an <ol>
    	IElement parent = element.getParent();
    	assertEquals(parent.getName(), "ol");
    	
    	// it should have four children
    	List<IElement> children = parent.getChildren();
    	assertEquals(children.size(), 4);
    	assertEquals(children.get(0).getTextContent(), "one");
    	assertEquals(children.get(1).getTextContent(), "two");
    	assertEquals(children.get(2).getTextContent(), "three");
    	assertEquals(children.get(3).getTextContent(), "four");
    	
    	// they are all <li>'s
    	for (IElement e : children)
    		assertEquals(e.getName(), "li");
    }
    
    /**
     * Test getting the XPath for multiple possible results
     */
    public void testMultiple() {
    	List<IElement> children = getElementsByXPath("//li");
    	assertEquals(children.size(), 4);
    	assertEquals(children.get(0).getTextContent(), "one");
    	assertEquals(children.get(1).getTextContent(), "two");
    	assertEquals(children.get(2).getTextContent(), "three");
    	assertEquals(children.get(3).getTextContent(), "four");
    	
    }

    /**
     * change the element and make sure XPath has changed
     */
    public void testChanging() {
    	{
	    	IElement element = getElementByXPath("//input[@id='test']");
	    	assertNotNull(element);
	    	assertEquals(element.getName(), "input");
	    	assertEquals(element.getAttribute("name"), "element_name");
	    	assertEquals(element.getAttribute("id"), "test");
	    	assertEquals(element.getAttribute("value"), "test3");
    	}

    	String testingText = new Date().toString();
    	setFormElement("element_name", testingText);
    	assertFormElementEquals("element_name", testingText);	// should still work
    	
    	{
	    	IElement element = getElementByXPath("//input[@id='test']");
	    	assertNotNull(element);
	    	assertEquals(element.getName(), "input");
	    	assertEquals(element.getAttribute("name"), "element_name");
	    	assertEquals(element.getAttribute("id"), "test");
	    	assertEquals(element.getAttribute("value"), "testingText");		// should have changed
    	}
    	
    }
    
}
