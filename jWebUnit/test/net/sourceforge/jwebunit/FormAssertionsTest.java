/*
 * User: djoiner
 * Date: Sep 9, 2002
 * Time: 3:15:10 PM
 */
package net.sourceforge.jwebunit;

public class FormAssertionsTest extends JWebUnitTest {

    public void setUp() throws Exception {
        super.setUp();
        addTestPage();
        beginAt("/testPage.html");
    }

    public void testAssertFormParameterPresent() throws Throwable {
        assertPassFail("assertFormElementPresent", "testInputElement", "noSuchElement");
        assertPass("assertFormElementPresent", "checkboxselected");
    }

    public void testAssertFormParameterNotPresent() throws Throwable {
        assertPassFail("assertFormElementNotPresent", "noSuchElement", "testInputElement");
    }

    public void testAssertFormParameterPresentWithLabel() throws Throwable {
        assertPassFail("assertFormElementPresentWithLabel", "Test Input", "No Such Label");
        assertFail("assertFormElementPresentWithLabel", "This is a test page");
    }

    public void testAssertFormParameterNotPresentWithLabel() throws Throwable {
        assertPassFail("assertFormElementNotPresentWithLabel", "No Such Label", "Test Input");
        assertPass("assertFormElementNotPresentWithLabel", "This is a test page");
    }

    public void testAssertHasForm() throws Throwable {
        assertPass("assertFormPresent", NOARGS);
        beginAt("/noFormPage.html");
        assertFail("assertFormPresent", NOARGS);
    }

    public void testAssertHasNamedForm() throws Throwable {
        assertPass("assertFormPresent", new String[]{"form2"});
        assertFail("assertFormPresent", new String[]{"noform"});
    }

    public void testAssertFormParameterEquals() throws Throwable {
        assertPass("assertFormElementEquals", new Object[]{"testInputElement", "testValue"});
        assertFail("assertFormElementEquals", new Object[]{"testInputElement", "noSuchValue"});
        assertFail("assertFormElementEquals", new Object[]{"noSuchElement", "testValue"});
    }

    public void testCheckboxSelected() throws Throwable {
        assertPassFail("assertCheckboxSelected", "checkboxselected", "checkboxnotselected");
        assertFail("assertCheckboxSelected", "nosuchbox");
    }

    public void testCheckboxNotSelected() throws Throwable {
        assertPassFail("assertCheckboxNotSelected", "checkboxnotselected", "checkboxselected");
        assertFail("assertCheckboxNotSelected", "nosuchbox");
    }

    public void testAssertSubmitButtonPresent() throws Throwable {
        assertPassFail("assertSubmitButtonPresent", "submitButton", "noSuchButton");
    }

    public void testAssertSubmitButtonNotPresent() throws Throwable {
        assertPassFail("assertSubmitButtonNotPresent", "noSuchButton", "submitButton");
    }

    public void testAssertSubmitButtonValue() throws Throwable {
        assertPassFail("assertSubmitButtonValue",
                new Object[]{"submitButton", "buttonLabel"},
                new Object[]{"submitButton", "noSuchLabel"});
    }

    public void testAssertRadioOptionPresent() throws Throwable {
        assertPassFail("assertRadioOptionPresent",
                new String[]{"cool", "cat"},
                new String[]{"cool", "fish"});
    }

    public void testAssertRadioOptionNotPresent() throws Throwable {
        assertPassFail("assertRadioOptionNotPresent",
                new String[]{"cool", "fish"},
                new String[]{"cool", "cat"});
    }

    public void testAssertRadioOptionSelected() throws Throwable {
        assertPassFail("assertRadioOptionSelected",
                new String[]{"cool", "dog"},
                new String[]{"cool", "cat"});
    }

    public void testAssertRadioOptionNotSelected() throws Throwable {
        assertPassFail("assertRadioOptionNotSelected", new String[]{"cool", "cat"}, new String[]{"cool", "dog"});
    }

    public void testAssertOptionsEqual() throws Throwable {
        assertPass("assertOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertFail("assertOptionsEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertFail("assertOptionsEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    public void testAssertOptionsNotEqual() throws Throwable {
        assertFail("assertOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four"}});
        assertPass("assertOptionsNotEqual", new Object[]{"select1", new String[]{"one", "four", "three", "two"}});
        assertPass("assertOptionsNotEqual", new Object[]{"select1", new String[]{"one", "two", "three", "four", "five"}});
    }

    public void testAssertOptionValuesEqual() throws Throwable {
        assertPass("assertOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertFail("assertOptionValuesEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertFail("assertOptionValuesEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    public void testAssertOptionValuesNotEqual() throws Throwable {
        assertFail("assertOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4"}});
        assertPass("assertOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "4", "3", "2"}});
        assertPass("assertOptionValuesNotEqual", new Object[]{"select1", new String[]{"1", "2", "3", "4", "5"}});
    }

    public void testAssertSelectedOptionEquals() throws Throwable {
        assertPassFail("assertOptionEquals", new String[]{"select1", "one"}, new String[]{"select1", "two"});
    }

    public void testAssertButtonPresent() throws Throwable {
        assertPassFail("assertButtonPresent", "b1", "nobutton");
    }

    public void testAssertButtonNotPresent() throws Throwable {
        assertPassFail("assertButtonNotPresent", "nobutton", "b1");
    }

    private void addTestPage() {
        defineWebPage("testPage", "This is a test page." +
                "<table summary=\"testTable\">" +
                "<tr><td>table text</td></tr>" +
                "<tr><td>table text row 2</td></tr>" +
                "<tr><td>table text row 3</td><td>row 3 col 1</td>" +
                "<a href=\"someurl.html\">test link</a>" +
                "<form id=\"form1\">" +
                "<input type=\"button\" id=\"b1\"/>" +
                "<select name=\"selectOption\"><option value=\"1\">One</option><option value=\"2\">Two</option><option value=\"3\">Three</option></select>" +
                "Test Input : <input type=\"text\" name=\"testInputElement\" value=\"testValue\"/>" +
                "<input type=\"submit\" name=\"submitButton\" value=\"buttonLabel\"/>" +
                "<input type=\"checkbox\" name=\"checkboxselected\" CHECKED>" +
                "<input type=\"checkbox\" name=\"checkboxnotselected\">" +
                "<textarea name=\"text\" cols=\"44\" rows=\"3\" wrap=\"VIRTUAL\"></textarea>" +
                "</form>" +
                "<form name=\"form2\"></form>" +
                "<form id=\"form3\">" +
                "<input type=\"radio\" name=\"cool\" value=\"dog\" checked=\"checked\"/>" +
                "<input type=\"radio\" name=\"cool\" value=\"cat\"/>" +
                "<input type=\"radio\" name=\"cool\" value=\"chicken\"/>" +
                "</form>" +
                "<form id=\"form4\">" +
                "<select name=\"select1\">" +
                "<option value=\"1\">one</option>" +
                "<option value=\"2\">two</option>" +
                "<option value=\"3\">three</option>" +
                "<option value=\"4\">four</option>" +
                "</select>" +
                "</form>" +
                "</table>");
        defineWebPage("noFormPage", "");
    }

}
