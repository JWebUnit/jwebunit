/*
 * User: djoiner
 * Date: Sep 9, 2002
 * Time: 3:26:47 PM
 */
package net.sourceforge.jwebunit;

public class TableAssertionsTest extends JWebUnitTest {

    public void setUp() throws Exception {
        super.setUp();
        addTestPage();
        beginAt("/testPage.html");
    }

    public void testAssertTablePresent() throws Throwable {
        assertPassFail("assertTablePresent", "testTable", "noTable");
    }

    public void testAssertTableNotPresent() throws Throwable {
        assertPassFail("assertTableNotPresent", "noTable", "testTable");
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

    private void addTestPage() {
        defineWebPage("testPage", "This is a test page." +
                                  "<table id=\"testTable\">" +
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
