/*
 * User: djoiner
 * Date: Sep 9, 2002
 * Time: 3:15:10 PM
 */
package net.sourceforge.jwebunit.tests;

import net.sourceforge.jwebunit.tests.util.JettySetup;
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
        assertPassFail("assertSelectOptionPresent",
                new String[]{"selectOption", "One"},
                new String[]{"selectOption", "NoSuchOption"});
    }
    
    public void testAssertSelectOptionNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectOptionNotPresent",
                new String[]{"selectOption", "NoSuchOption"},
                new String[]{"selectOption", "One"});    	
    }

    public void testAssertSelectOptionValuePresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectOptionValuePresent",
                new String[]{"selectOption", "1"},
                new String[]{"selectOption", "NoSuchOption"});
    }
    
    public void testAssertSelectOptionValueNotPresent() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectOptionValueNotPresent",
                new String[]{"selectOption", "NoSuchOption"},
                new String[]{"selectOption", "1"});       
    }

    public void testAssertSelectOptionsEqual() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertSelectOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertFail("assertSelectOptionsEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertFail("assertSelectOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    public void testAssertSelectOptionsNotEqual() throws Throwable {
        beginAt("/testPage.html");
        assertFail("assertSelectOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertPass("assertSelectOptionsNotEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertPass("assertSelectOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    public void testAssertSelectOptionValuesEqual() throws Throwable {
        beginAt("/testPage.html");
        assertPass("assertSelectOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertFail("assertSelectOptionValuesEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertFail("assertSelectOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    public void testAssertSelectOptionValuesNotEqual() throws Throwable {
        beginAt("/testPage.html");
        assertFail("assertSelectOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertPass("assertSelectOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertPass("assertSelectOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    public void testAssertSelectedOptionEquals() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectedOptionEquals", new String[]{"select1", "one"}, new String[]{"select1", "two"});
    }

    public void testAssertSelectedOptionValueEquals() throws Throwable {
        beginAt("/testPage.html");
        assertPassFail("assertSelectedOptionValueEquals", new String[]{"select1", "1"}, new String[]{"select1", "2"});
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
