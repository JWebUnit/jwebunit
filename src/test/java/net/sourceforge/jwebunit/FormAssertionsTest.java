/*
 * User: djoiner
 * Date: Sep 9, 2002
 * Time: 3:15:10 PM
 */
package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.util.JettySetup;
import junit.framework.Test;
import junit.framework.TestSuite;

public class FormAssertionsTest extends JWebUnitAPITestCase {
	
    public static Test suite() {
        Test suite = new TestSuite(FormAssertionsTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/FormAssertionsTest");
    }

    public void testAssertButtonWithTextPresent() {
        beginAt("/assertButtonWithText.html");
        assertButtonPresentWithText("buttonValue1");
        assertButtonPresentWithText("buttonValue2");
        assertButtonNotPresentWithText("buttonValue3");
    }
    
    public void testAssertFormParameterPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementPresent", "testInputElement", "noSuchElement");
        assertPass("assertFormElementPresent", "checkboxselected");
        assertPass("assertFormElementEmpty", "testInputElement2");
    }

    public void testAssertFormParameterNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementNotPresent", "noSuchElement", "testInputElement");
    }

    public void testAssertFormParameterPresentWithLabel() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementPresentWithLabel", "Test Input", "No Such Label");
        assertFail("assertFormElementPresentWithLabel", "This is a test page");
    }

    public void testAssertFormParameterNotPresentWithLabel() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertFormElementNotPresentWithLabel", "No Such Label", "Test Input");
        assertPass("assertFormElementNotPresentWithLabel", "This is a test page");
    }

    public void testAssertHasForm() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertFormPresent", NOARGS);
        beginAt("/noFormPage.html");
        assertFail("assertFormPresent", NOARGS);
        assertPass("assertFormNotPresent", NOARGS);        
    }

    public void testAssertHasNamedForm() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertFormPresent", new String[]{"form2"});
        assertFail("assertFormPresent", new String[]{"noform"});
        assertPass("assertFormNotPresent", new String[]{"noform"});
    }

    public void testAssertFormParameterEquals() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertFormElementEquals", new Object[]{"testInputElement", "testValue"});
        assertFail("assertFormElementEquals", new Object[]{"testInputElement", "noSuchValue"});
        assertFail("assertFormElementEquals", new Object[]{"noSuchElement", "testValue"});
    }

    public void testCheckboxSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertCheckboxSelected", "checkboxselected", "checkboxnotselected");
        assertFail("assertCheckboxSelected", "nosuchbox");
    }

    public void testCheckboxNotSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertCheckboxNotSelected", "checkboxnotselected", "checkboxselected");
        assertFail("assertCheckboxNotSelected", "nosuchbox");
    }

    public void testAssertSubmitButtonPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSubmitButtonPresent", "submitButton", "noSuchButton");
    }

    public void testAssertSubmitButtonNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSubmitButtonNotPresent", "noSuchButton", "submitButton");
    }

    public void testAssertSubmitButtonValue() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSubmitButtonPresent",
                new Object[]{"submitButton", "buttonLabel"},
                new Object[]{"submitButton", "noSuchLabel"});
    }

    public void testAssertRadioOptionPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionPresent",
                new String[]{"cool", "cat"},
                new String[]{"cool", "fish"});
    }

    public void testAssertRadioOptionNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionNotPresent",
                new String[]{"cool", "fish"},
                new String[]{"cool", "cat"});
    }

    public void testAssertRadioOptionSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionSelected",
                new String[]{"cool", "dog"},
                new String[]{"cool", "cat"});
    }

    public void testAssertRadioOptionNotSelected() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertRadioOptionNotSelected", new String[]{"cool", "cat"}, new String[]{"cool", "dog"});
    }
    
    public void testAssertSelectOptionPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertOptionPresent",
                new String[]{"selectOption", "One"},
                new String[]{"selectOption", "NoSuchOption"});
    }
    
    public void testAssertSelectOptionNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertOptionNotPresent",
                new String[]{"selectOption", "NoSuchOption"},
                new String[]{"selectOption", "One"});    	
    }

    public void testAssertOptionsEqual() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertFail("assertOptionsEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertFail("assertOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    public void testAssertOptionsNotEqual() throws Throwable {
        beginAt("/testPage.html");
        assertFail("assertOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertPass("assertOptionsNotEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertPass("assertOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    public void testAssertOptionValuesEqual() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertFail("assertOptionValuesEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertFail("assertOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    public void testAssertOptionValuesNotEqual() throws Throwable {
        beginAt("/testPage.html");
        assertFail("assertOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertPass("assertOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertPass("assertOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    public void testAssertSelectedOptionEquals() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertOptionEquals", new String[]{"select1", "one"}, new String[]{"select1", "two"});
    }

    public void testAssertButtonPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertButtonPresent", "b1", "nobutton");
    }

    public void testAssertButtonNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertButtonNotPresent", "nobutton", "b1");
    }
    
}
