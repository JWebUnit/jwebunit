/*
 * User: djoiner
 * Date: Sep 9, 2002
 * Time: 3:15:10 PM
 */
package net.sourceforge.jwebunit;

public class FormAssertionsTest extends JWebUnitTest {
    public FormAssertionsTest(String s) {
        super(s);
    }

    public void setUp() throws Exception {
        super.setUp();
        addTestPage();
        beginAt("/testPage.html");
    }

    public void testAssertFormControlPresent() throws Throwable {
        assertPassFail("assertFormControlPresent", "testInputElement", "noSuchElement");
    }

    public void testAssertFormControlNotPresent() throws Throwable {
        assertPassFail("assertFormControlNotPresent", "noSuchElement", "testInputElement");
    }

    public void testAssertHasForm() throws Throwable {
        assertPass("assertHasForm", NOARGS);
        beginAt("/noFormPage.html");
        assertFail("assertHasForm", NOARGS);
    }

    public void testAssertHasNamedForm() throws Throwable {
        assertPass("assertHasForm", new String[] {"form2"});
        assertFail("assertHasForm", new String[] {"form3"});
    }

    public void testAssertFormControlEquals() throws Throwable {
        assertPass("assertFormControlEquals", new Object[]{"testInputElement", "testValue"});
        assertFail("assertFormControlEquals", new Object[]{"testInputElement", "noSuchValue"});
        assertFail("assertFormControlEquals", new Object[]{"noSuchElement", "testValue"});
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

    private void addTestPage() {
        defineWebPage("testPage", "This is a test page." +
                                  "<table summary=\"testTable\">" +
                                  "<tr><td>table text</td></tr>" +
                                  "<tr><td>table text row 2</td></tr>" +
                                  "<tr><td>table text row 3</td><td>row 3 col 1</td>" +
                                  "<a href=\"someurl.html\">test link</a>" +
                                  "<form>" +
                                  "<input type=\"text\" name=\"testInputElement\" value=\"testValue\"/>" +
                                  "<input type=\"submit\" name=\"submitButton\" value=\"buttonLabel\"/>" +
                                  "<input type=\"checkbox\" name=\"checkboxselected\" CHECKED>" +
                                  "<input type=\"checkbox\" name=\"checkboxnotselected\">" +
                                  "</form>" +
                                  "<form name=\"form2\"></form>" +
                                  "</table>");
        defineWebPage("noFormPage", "");
    }

}
