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
    		getElementByXPath("//input[@id='test2']");
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
	    	// the element should also be available through the normal method
	    	assertFormElementPresent("element_name");
    	}

    	String testingText = new Date().toString();
    	setTextField("element_name", testingText);
    	assertTextFieldEquals("element_name", testingText);		// should still work
    	
    	{
	    	IElement element = getElementByXPath("//input[@id='test']");
	    	assertNotNull(element);
	    	assertEquals(element.getName(), "input");
	    	assertEquals(element.getAttribute("name"), "element_name");
	    	assertEquals(element.getAttribute("id"), "test");
	    	assertEquals(element.getAttribute("value"), testingText);		// should have changed
	    	// the element should also be available through the normal method
	    	assertFormElementPresent("element_name");
    	}
    	
    }
    
    public void testWithXpath() {
    	IElement element = getElementByXPath("//body");
    	assertNotNull(element);
    	assertEquals("body", element.getName());
    	
    	// get first input children
    	IElement input = element.getElement("input");
    	assertNotNull(input);
    	assertEquals("input", input.getName());
    	assertEquals("element_name", input.getAttribute("name"));
    	assertEquals("test3", input.getAttribute("value"));
    	
    	// get all input children
    	List<IElement> inputs = element.getElements("input");
    	assertEquals(4, inputs.size());	// there should be two
    	assertEquals("test3", inputs.get(0).getAttribute("value"));
    	assertEquals("Do nothing", inputs.get(1).getAttribute("value"));
    	assertEquals("initial", inputs.get(2).getAttribute("value"));
    	assertEquals("unchanged", inputs.get(3).getAttribute("value"));
    	
    	// test regexps
    	assertMatch("init.+", inputs.get(2).getAttribute("value"));
    	assertNotMatch("^xinitial", inputs.get(2).getAttribute("value"));
    	assertMatch("test regexp with message", "init.+", inputs.get(2).getAttribute("value"));
    	assertNotMatch("test regexp with message", "$xinitial", inputs.get(2).getAttribute("value"));
    	
    	// get parent through xpath
    	IElement parent = element.getElement("..");
    	assertNotNull(parent);
    	assertEquals("html", parent.getName());
    	
    }
    
    /**
     * Test that setting attributes manually (e.g setAttribute("value") 
     * properly calls any attached Javascript.
     */
    public void testAttributeJavascript() {
    	String testingText = new Date().toString();
    	
    	{
	    	IElement js1 = getElementById("js1");
	    	IElement js2 = getElementById("js2");
	    	
	    	assertEquals(js1.getAttribute("value"), "initial");
	    	assertEquals(js2.getAttribute("value"), "unchanged");
	    	
	    	// change js1's value
	    	js1.setAttribute("value", testingText);
    	}
    	
    	// refresh the elements and check they have changed
    	{
	    	IElement js1 = getElementById("js1");
	    	IElement js2 = getElementById("js2");
	    	
	    	assertEquals(js1.getAttribute("value"), testingText);
	    	assertEquals(js2.getAttribute("value"), testingText);
    	}

    }
    
}
