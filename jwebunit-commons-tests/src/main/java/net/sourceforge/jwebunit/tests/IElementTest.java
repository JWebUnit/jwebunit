/**
 * Copyright (c) 2010, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.jwebunit.tests;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertCommentNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertCommentPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertMatch;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertNotMatch;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextFieldEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementById;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementsByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import net.sourceforge.jwebunit.api.IElement;

import org.junit.Test;

/**
 * Test the IElement interface
 *
 * @author jmwright
 */
public class IElementTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/IElementTest");
        beginAt("/template.html");
    }
    
    @Test public void testSimple() {
    	// test an element that exists
    	IElement element = getElementByXPath("//input[@id='test']");
    	assertNotNull(element);
    	assertEquals(element.getName(), "input");
    	assertEquals(element.getAttribute("name"), "element_name");
    	assertEquals(element.getAttribute("id"), "test");
    	assertEquals(element.getAttribute("value"), "test3");
    }

    @Test public void testMissing() {
    	// a missing element should throw an exception
    	try {
    		getElementByXPath("//input[@id='test2']");
    		fail("getElementByXPath() should have thrown an assertion exception.");
    	} catch (AssertionError e) {
    		// nothing
    	}
    }
    
    /**
     * Test parent, child methods
     */
    @Test public void testChildren() {
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
    @Test public void testMultiple() {
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
    @Test public void testChanging() {
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
    
    @Test public void testWithXpath() {
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
    @Test public void testAttributeJavascript() {
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
    
    /**
     * Tests searching for comments.
     */
    @Test public void testComments() {
    	// whitespace is ignored
    	assertCommentPresent("a comment");
    	assertCommentPresent("another comment");
    	assertCommentPresent("  a comment");
    	assertCommentPresent("   another comment  ");
    	
    	// but case is not
    	assertCommentNotPresent("A Comment");
    	assertCommentNotPresent("definitely not here");
    }
    
    /**
     * Test preceding element XPath.
     * preceding: "Selects everything in the document that is before the start tag of the current node"
     */
    @Test public void testPreceding() {
    	IElement element = getElementById("first"); // li
    	// should get the first <input>, which is
    	// <input id="test" name="element_name" value="test3">
    	IElement preceding = element.getElement("preceding::input"); 
    	
    	assertEquals(preceding.getName(), "input");
    	assertEquals(preceding.getAttribute("name"), "element_name");
    }
    
    /**
     * Test that {@link IElement#equals(Object)} is implemented
     * correctly.
     * 
     */
    @Test public void testIElementEquals() {
    	
    	// through getElementById
    	IElement container1 = getElementById("container");
    	
    	// through IElement.getElement
    	IElement span = getElementByXPath("//span[@class='inline']");
    	IElement container2 = span.getElement("..");
    	
    	// through getByXPath
    	IElement container3 = null;
    	for (IElement e : getElementsByXPath("//div")) {
    		if ("container".equals(e.getAttribute("id"))) {
    			container3 = e;
    		}
    	}
    	
    	// should have found all of these
    	assertNotNull(container1);
    	assertNotNull(container2);
    	assertNotNull(container3);
    	
    	// check equality
    	assertEquals(container1, container2);
    	assertEquals(container2, container3);
    	assertEquals(container1, container3);
    	
    }
    
}
