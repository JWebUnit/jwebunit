package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.JWebUnitTest;

/**
 * Test the assertions provided by WebTestCase using the PseudoServer test package provided
 * by Russell Gold in httpunit.
 *
 * @author Wilkes Joiner
 * @author Jim Weaver
 */
public class WebAssertionsTest extends JWebUnitTest {
    private static final Object[] NOARGS = new Object[0];

    public WebAssertionsTest(String s) {
        super(s);
    }

    public void setUp() throws Exception {
        super.setUp();
        addTestPage();
        gotoURL("/testPage.html");
    }

    public void testAssertTitleEquals() throws Throwable {
        assertPassFail("assertTitleEquals", "testPage", "wrong title");
    }

    public void testAssertTextInResponse() throws Throwable {
        assertPassFail("assertTextInResponse", "This is a test page.", "no such text");
    }

    public void testAssertTextNotInResponse() throws Throwable {
        assertPassFail("assertTextNotInResponse", "no such text", "This is a test page.");
    }

    public void testAssertTablePresent() throws Throwable {
        assertPassFail("assertTablePresent", "testTable", "notable");
    }

    public void testAssertTextInTable() throws Throwable {
        assertPassFail("assertTextInTable",
                       new Object[]{"testTable", "table text"},
                       new Object[]{"testTable", "no such text"});
    }

    public void testAssertTextNotInTable() throws Throwable {
        assertPassFail("assertTextNotInTable",
                       new Object[]{"testTable", "no such text"},
                       new Object[]{"testTable", "table text"});
    }

    public void testAssertTextArrayInTable() throws Throwable {
        assertPassFail("assertTextInTable",
                       new Object[]{"testTable", new String[]{"table text", "table text row 2"}},
                       new Object[]{"testTable", new String[]{"table text", "no such row 2"}});
    }

    public void testAssertTextArrayNotInTable() throws Throwable {
        assertPassFail("assertTextNotInTable",
                       new Object[]{"testTable", new String[]{"no such row 1", "no such row 2"}},
                       new Object[]{"testTable", new String[]{"no such row 1", "table text row 2"}});
    }

    public void testAssertTableEquals() throws Throwable {
        assertPass("assertTableEquals",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "row 3 col 1"}}});
    }

    public void testAssertTableEqualsExtraColumn() throws Throwable {
        assertFail("assertTableEquals",
                   new Object[]{"testTable", new String[][]{{"table text", "", "extra column"},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "row 3 col 1"}}});
    }

    public void testAssertTableEqualsExtraRow() throws Throwable {
        assertFail("assertTableEquals",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "row 3 col 1"},
                                                            {"no row 4"}}});
    }

    public void testAssertTableEqualsInvalidColumnText() throws Throwable {
        assertFail("assertTableEquals",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"no such text in row 2", ""},
                                                            {"table text row 3", "row 3 col 1"}}});
    }

    public void testAssertTableEqualsMissingText() throws Throwable {
        assertFail("assertTableEquals",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", ""}}});
    }

    public void testAssertTableRowsEquals() throws Throwable {
        assertPass("assertTableRowsEqual",
                   new Object[]{"testTable",
                                new Integer(1),
                                new String[][]{{"table text row 2", ""},
                                               {"table text row 3", "row 3 col 1"}}});
    }

    public void testAssertTableRowsEqualsTooManyExpected() throws Throwable {
        assertFail("assertTableRowsEqual",
                   new Object[]{"testTable",
                                new Integer(2),
                                new String[][]{{"table text row 3", "row 3 col 1"},
                                               {"unexpected", ""}}});
    }

    public void testAssertFormControlPresent() throws Throwable {
        assertPassFail("assertFormControlPresent", "testInputElement", "noSuchElement");
    }

    public void testAssertFormControlNotPresent() throws Throwable {
        assertPassFail("assertFormControlNotPresent", "noSuchElement", "testInputElement");
    }

    public void testAssertHasForm() throws Throwable {
        assertPass("assertHasForm", NOARGS);
        gotoURL("/noFormPage.html");
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

    public void testAssertLinkInResponse() throws Throwable {
        assertPassFail("assertLinkInResponse", "test link", "no such link");
    }

    public void testAssertLinkNotInResponse() throws Throwable {
        assertPassFail("assertLinkNotInResponse", "no such link", "test link");
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
