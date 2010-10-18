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

import org.junit.Test;

import net.sourceforge.jwebunit.api.IElement;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;

public class FormAssertionsTest extends JWebUnitAPITestCase {
	
    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/FormAssertionsTest");
    }

    @Test
    public void testAssertButtonWithTextPresent() {
        beginAt("/assertButtonWithText.html");
        assertButtonPresentWithText("foo");
        assertButtonPresentWithText("bar");
        assertButtonNotPresentWithText("foobar");
    }
    
    @Test
    public void testAssertFormParameterPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementPresent", "testInputElement", "noSuchElement");
        assertPass("assertFormElementPresent", "checkboxselected");
        assertPass("assertFormElementEmpty", "testInputElement2");
        assertPass("assertFormElementPresent", "text");
    }

    @Test
    public void testAssertFormParameterNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementNotPresent", "noSuchElement", "testInputElement");
    }

    @Test
    public void testAssertFormPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertFormPresent", NOARGS);
        beginAt("/noFormPage.html");
        assertFail("assertFormPresent", NOARGS);
        assertPass("assertFormNotPresent", NOARGS);        
    }

    @Test
    public void testAssertFormPresentByName() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertFormPresent", new String[]{"form2"});
        assertFail("assertFormPresent", new String[]{"noform"});
        assertPass("assertFormNotPresent", new String[]{"noform"});
    }

    @Test
    public void testAssertFormElementEquals() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertTextFieldEquals", new Object[]{"testInputElement", "testValue"}, new Object[]{"testInputElement", "noSuchValue"});
        assertPass("assertSubmitButtonPresent", new Object[]{"submitButton", "buttonLabel"});
        setWorkingForm("form5");
        assertPass("assertTextFieldEquals", new Object[]{"textarea", "sometexthere"});
        setWorkingForm("form3");
        assertPass("assertRadioOptionSelected", new Object[]{"cool", "dog"});
        setWorkingForm("form5");
        assertPassFail("assertHiddenFieldPresent", new Object[]{"hiddenelement", "hiddenvalue"}, new Object[]{"hiddenelement", "notThisValue"});
        setWorkingForm("form1");
        assertFail("assertTextFieldEquals", new Object[]{"noSuchElement", "testValue"});
        assertFail("assertHiddenFieldPresent", new Object[]{"noSuchElement", "testValue"});
        setWorkingForm("form5");        
        assertPassFail("assertTextFieldEquals", new Object[]{"passwordelement", "password"}, new Object[]{"passwordelement", "noSuchValue"});
    }

    @Test
    public void testCheckboxSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertCheckboxSelected", "checkboxselected", "checkboxnotselected");
        assertFail("assertCheckboxSelected", "nosuchbox");
    }

    @Test
    public void testCheckboxSelectedByName() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertCheckboxSelected", new Object[]{"checkboxnotselected", "actuallyselected"},
                                                 new Object[]{"checkboxselected", "actuallynotselected"});
        assertFail("assertCheckboxSelected", new Object[]{"checkboxselected", "nosuchvalue"});
    }

    @Test
    public void testCheckboxNotSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertCheckboxNotSelected", "checkboxnotselected", "checkboxselected");
        assertFail("assertCheckboxNotSelected", "nosuchbox");
    }

    @Test
    public void testCheckboxNotSelectedByName() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertCheckboxNotSelected", new Object[]{"checkboxselected", "actuallynotselected"},
                                                 new Object[]{"checkboxnotselected", "actuallyselected"});
        assertFail("assertCheckboxNotSelected", new Object[]{"checkboxnotselected", "nosuchvalue"});
    }

    @Test
    public void testAssertSubmitButtonPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSubmitButtonPresent", "submitButton", "noSuchButton");
    }

    @Test
    public void testAssertSubmitButtonNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSubmitButtonNotPresent", "noSuchButton", "submitButton");
    }

    @Test
    public void testAssertSubmitButtonValue() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSubmitButtonPresent",
                new Object[]{"submitButton", "buttonLabel"},
                new Object[]{"submitButton", "noSuchLabel"});
    }

    @Test
    public void testAssertResetButtonPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertResetButtonPresent", "resetButton", "noSuchButton");
    }

    @Test
    public void testAssertResetButtonNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertResetButtonNotPresent", "noSuchButton", "resetButton");
    }

    @Test
    public void testAssertRadioOptionPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionPresent",
                new String[]{"cool", "cat"},
                new String[]{"cool", "fish"});
    }

    @Test
    public void testAssertRadioOptionNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionNotPresent",
                new String[]{"cool", "fish"},
                new String[]{"cool", "cat"});
    }

    @Test
    public void testAssertRadioOptionSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionSelected",
                new String[]{"cool", "dog"},
                new String[]{"cool", "cat"});
    }

    @Test
    public void testAssertRadioOptionNotSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionNotSelected", new String[]{"cool", "cat"}, new String[]{"cool", "dog"});
    }
    
    @Test
    public void testAssertSelectOptionPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectOptionPresent",
                new String[]{"selectOption", "One"},
                new String[]{"selectOption", "NoSuchOption"});
    }
    
    @Test
    public void testAssertSelectOptionNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectOptionNotPresent",
                new String[]{"selectOption", "NoSuchOption"},
                new String[]{"selectOption", "One"});    	
    }

    @Test
    public void testAssertSelectOptionValuePresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectOptionValuePresent",
                new String[]{"selectOption", "1"},
                new String[]{"selectOption", "NoSuchOption"});
    }
    
    @Test
    public void testAssertSelectOptionValueNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectOptionValueNotPresent",
                new String[]{"selectOption", "NoSuchOption"},
                new String[]{"selectOption", "1"});       
    }

    @Test
    public void testAssertSelectOptionsEqual() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertSelectOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertFail("assertSelectOptionsEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertFail("assertSelectOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    @Test
    public void testAssertSelectOptionsNotEqual() throws Throwable {
        beginAt("/testPage.html");
        assertFail("assertSelectOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertPass("assertSelectOptionsNotEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertPass("assertSelectOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    @Test
    public void testAssertSelectOptionValuesEqual() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertSelectOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertFail("assertSelectOptionValuesEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertFail("assertSelectOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    @Test
    public void testAssertSelectOptionValuesNotEqual() throws Throwable {
        beginAt("/testPage.html");
        assertFail("assertSelectOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertPass("assertSelectOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertPass("assertSelectOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    @Test
    public void testAssertSelectedOptionEquals() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectedOptionEquals", new String[]{"select1", "one"}, new String[]{"select1", "two"});
    }

    @Test
    public void testAssertSelectedOptionValueEquals() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectedOptionValueEquals", new String[]{"select1", "1"}, new String[]{"select1", "2"});
    }

    @Test
    public void testAssertButtonPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertButtonPresent", "b1", "nobutton");
    }

    @Test
    public void testAssertButtonNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertButtonNotPresent", "nobutton", "b1");
    }
    
    /**
     * Test elements with complicated names.
     */
    @Test
    public void testComplicatedElementNames() {
    	beginAt("/testPage.html");
    	
    	// the names to search for
    	String[] names = new String[] {
    			"complicated[5]", "complicated'6'", "complicated['7'].8",
    	};
    	
    	// test each of them
    	for (String s : names) {
    		assertFormElementPresent(s);
    		assertTextFieldEquals(s, s);
    	}
    }

    /**
     * Testing for issue 1996193: clickRadioOption does not work as expected 
     */
    @Test
    public void testIssue1996193() {
    	beginAt("/testPage.html");
    	
    	String option1 = "coreAnswers['0-1'].strAnswer"; 
    	assertRadioOptionPresent(option1, "1"); 
    	clickRadioOption(option1, "1");
    	assertRadioOptionSelected(option1, "1");

    }
    
    /**
     * tests for label elements
     */
    @Test
    public void testLabels() {
    	beginAt("/testPage.html");
    	
    	assertLabelPresent("label1");
    	assertLabeledFieldEquals("label1", "one");

    	assertLabelPresent("label2");
    	assertLabeledFieldEquals("label2", "two");

    	assertLabelPresent("label3");
    	assertLabeledFieldEquals("label3", "three");

    	assertLabelPresent("label4");
    	assertLabeledFieldEquals("label4", "2");

    	assertLabelPresent("label5");
    	assertLabeledFieldEquals("label5", "2");

    	assertLabelPresent("label6");
    	assertLabeledFieldEquals("label6", "ten");

    	assertLabelPresent("label7");
    	assertLabeledFieldEquals("label7", "10");

    	assertLabelPresent("label8");
    	assertLabeledFieldEquals("label8", "eight");
    }
    
    /**
     * Test setting elements retrieved through labels
     */
    @Test
    public void testSetLabels() {
    	beginAt("/testPage.html");

    	assertLabelPresent("label1");
    	assertLabeledFieldEquals("label1", "one");
    	assertTextFieldEquals("label1_field1", "one");
    	assertEquals(getElementById("field1_id").getAttribute("value"), "one");
    	
    	// through setLabeledFormElementField
    	setLabeledFormElementField("label1", "two");
    	
    	assertLabeledFieldEquals("label1", "two");
    	assertTextFieldEquals("label1_field1", "two");
    	assertEquals(getElementById("field1_id").getAttribute("value"), "two");
    	
    	// through normal setValue
    	setTextField("label1_field1", "three");

    	assertLabeledFieldEquals("label1", "three");
    	assertTextFieldEquals("label1_field1", "three");
    	assertEquals(getElementById("field1_id").getAttribute("value"), "three");
    	
    	// through IElement
    	IElement element = getElementById("field1_id");
    	assertNotNull(element);
    	element.setAttribute("value", "four");

    	assertLabeledFieldEquals("label1", "four");
    	assertTextFieldEquals("label1_field1", "four");
    	assertEquals(getElementById("field1_id").getAttribute("value"), "four");
    	
    }
    
    /**
     * Even though the element has no value set (i.e. getAttribute("value") == null),
     * it should still qualify as equal to an empty string.
     */
    @Test
    public void testLabeledEmptyElement() {
    	beginAt("/testPage.html");
    	assertLabeledFieldEquals("label9", "");

    }

}
