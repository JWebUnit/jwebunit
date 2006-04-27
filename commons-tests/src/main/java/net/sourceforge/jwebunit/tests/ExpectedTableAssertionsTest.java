package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.util.JettySetup;

/**
 * Test table equals assertions using expected tables.
 */
public class ExpectedTableAssertionsTest extends JWebUnitAPITestCase {


	public static Test suite() {
		return new JettySetup(new TestSuite(ExpectedTableAssertionsTest.class));
	}

    public void setUp() throws Exception {
        super.setUp();
		getTestContext().setBaseUrl(HOST_PATH + "/ExpectedTableAssertionsTest");
		beginAt("/ExpectedTableAssertionsTestPage.html");
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

}
