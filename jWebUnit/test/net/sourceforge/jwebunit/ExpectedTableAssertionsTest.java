package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.*;

/**
 * Test table equals assertions using expected tables.
 */
public class ExpectedTableAssertionsTest extends JWebUnitTest {

    public ExpectedTableAssertionsTest(String s) {
        super(s);
    }

    public void setUp() throws Exception {
        super.setUp();
        addTestPage();
        gotoURL("/testPage.html");
    }

    public void testAssertTableEquals() throws Throwable {
        ExpectedTable goodTable = new ExpectedTable(new Object[][] {
            {"table text", ""},
            {"table text row 2", ""},
            {"table text row 3", "row 3 col 1"},
            {new ExpectedCell("row 4", 2)},
        });
        assertPass("assertTableEquals", new Object[]{"testTable", goodTable});
    }

    public void testAssertTableEqualsMissingRows() throws Throwable {
        ExpectedTable badTable = new ExpectedTable(new Object[][] {
            {"table text", ""},
            {new ExpectedCell("row 4", 2)},
        });
        assertFail("assertTableEquals", new Object[]{"testTable", badTable});
    }

    private void addTestPage() {
        defineWebPage("testPage", "This is a test page." +
                                  "<table summary=\"testTable\">" +
                                  "<tr><td>table text</td></tr>" +
                                  "<tr><td>table text row 2</td></tr>" +
                                  "<tr><td>table text row 3</td><td>row 3 col 1</td></tr>" +
                                  "<tr><td colspan=\"2\">row 4</td></tr>" +
                                  "</table>");
        defineWebPage("noFormPage", "");
    }


}
