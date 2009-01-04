/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

public class TableAssertionsTest extends JWebUnitAPITestCase {

	public static Test suite() {
		Test suite = new TestSuite(TableAssertionsTest.class);
		return new JettySetup(suite);
	}

	public void setUp() throws Exception {
		super.setUp();
		setBaseUrl(HOST_PATH + "/TableAssertionsTest");
		beginAt("/TableAssertionsTestPage.html");
	}

	public void testAssertTablePresent() throws Throwable {
		assertPassFail("assertTablePresent", "testTable", "noTable");
	}

	public void testAssertTableNotPresent() throws Throwable {
		assertPassFail("assertTableNotPresent", "noTable", "testTable");
	}

	public void testAssertTextInTable() throws Throwable {
		assertPassFail("assertTextInTable", new Object[] { "testTable",
				"table text" }, new Object[] { "testTable", "no such text" });
	}

	public void testAssertTextNotInTable() throws Throwable {
		assertPassFail("assertTextNotInTable", new Object[] { "testTable",
				"no such text" }, new Object[] { "testTable", "table text" });
	}

	public void testAssertTextArrayInTable() throws Throwable {
		assertPassFail("assertTextInTable", new Object[] { "testTable",
				new String[] { "table text", "table text row 2" } },
				new Object[] { "testTable",
						new String[] { "table text", "no such row 2" } });
	}

	public void testAssertTextArrayNotInTable() throws Throwable {
		assertPassFail("assertTextNotInTable", new Object[] { "testTable",
				new String[] { "no such row 1", "no such row 2" } },
				new Object[] { "testTable",
						new String[] { "no such row 1", "table text row 2" } });
	}

    public void testAssertMatchInTable() throws Throwable {
        assertPassFail("assertMatchInTable",
                       new Object[]{"testTable", "table [Tt]ext"},
                       new Object[]{"testTable", "no.*text"});
    }

    public void testAssertNoMatchInTable() throws Throwable {
        assertPassFail("assertNoMatchInTable",
                       new Object[]{"testTable", "no.*text"},
                       new Object[]{"testTable", "table [Tt]ext"});
    }

    public void testAssertMatchArrayInTable() throws Throwable {
        assertPassFail("assertMatchInTable",
                       new Object[]{"testTable", new String[]{"table [Tt]ext", "table [Tt]ext row 2"}},
                       new Object[]{"testTable", new String[]{"table [Tt]ext", "no.*row 2"}});
    }

    public void testAssertNoMatchArrayInTable() throws Throwable {
        assertPassFail("assertNoMatchInTable",
                       new Object[]{"testTable", new String[]{"no.*row 1", "no.*row 2"}},
                       new Object[]{"testTable", new String[]{"no.*row 1", "table [Tt]ext row 2"}});
    }

	public void testAssertTableEquals() throws Throwable {
		assertPass("assertTableEquals", new Object[] {
				"testTable",
				new String[][] { { "table text" },
						{ "table text row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

    public void testAssertTableRowCountEquals() throws Throwable {
        assertPassFail("assertTableRowCountEquals", new Object[] {
                "tree", new Integer(3)}, new Object[] {
                        "tree", new Integer(4)});
    }

    public void testAssertTableEqualsExtraColumn() throws Throwable {
		assertFail("assertTableEquals", new Object[] {
				"testTable",
				new String[][] { { "table text", "extra column" },
						{ "table text row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

	public void testAssertTableEqualsExtraRow() throws Throwable {
		assertFail("assertTableEquals",
				new Object[] {
						"testTable",
						new String[][] { { "table text" },
								{ "table text row 2" },
								{ "table text row 3", "row 3 col 1" },
								{ "no row 4" } } });
	}

	public void testAssertTableEqualsInvalidColumnText() throws Throwable {
		assertFail("assertTableEquals", new Object[] {
				"testTable",
				new String[][] { { "table text" },
						{ "no such text in row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

	public void testAssertTableEqualsMissingText() throws Throwable {
		assertFail("assertTableEquals",
				new Object[] {
						"testTable",
						new String[][] { { "table text" },
								{ "table text row 2" },
								{ "table text row 3", "" } } });
	}

	public void testAssertTableRowsEquals() throws Throwable {
		assertPass("assertTableRowsEqual", new Object[] {
				"testTable",
				new Integer(1),
				new String[][] { { "table text row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

	public void testAssertTableRowsEqualsTooManyExpected() throws Throwable {
		assertFail("assertTableRowsEqual", new Object[] {
				"testTable",
				new Integer(2),
				new String[][] { { "table text row 3", "row 3 col 1" },
						{ "unexpected" } } });
	}

	public void testTableWithSpaces() throws Throwable {
		assertTablePresent("tree");
		String[][] table = { { "root", " ", "" },
				{ "child1 ;semicolon", " ", "child2", " " },
				{ "child1.1", "", "child2.1", "child2.2" } };
		assertTableEquals("tree", table);
	}

    public void testAssertTableMatch() throws Throwable {
        assertPass("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table [Tt]ext"},
                                                            {"table [Tt]ext row 2"},
                                                            {"table [Tt]ext row 3", "row [0-9] col 1"}}});
    }

    public void testAssertTableMatchExtraColumn() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table text", "", "extra column"},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "row 3 col 1"}}});
    }

    public void testAssertTableMatchExtraRow() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "row 3 col 1"},
                                                            {"no row 4"}}});
    }

    public void testAssertTableMatchInvalidColumnText() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table [Tt]ext", ""},
                                                            {"no such [Tt]ext in row 2", ""},
                                                            {"table text row 3", "row 3 col 1"}}});
    }

    public void testAssertTableMatchMissingText() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "^$"}}});
    }

    public void testAssertTableRowsMatch() throws Throwable {
        assertPass("assertTableRowsMatch",
                   new Object[]{"testTable",
                                new Integer(1),
                                new String[][]{{"table text row 2"},
                                               {"table text row 3", "row 3 col 1"}}});
    }

    public void testAssertTableRowsMatchTooManyExpected() throws Throwable {
        assertFail("assertTableRowsMatch",
                   new Object[]{"testTable",
                                new Integer(2),
                                new String[][]{{"table text row 3", "row 3 col 1"},
                                               {"unexpected", ""}}});
    }
    
    public void testTableWithSpacesMatch() throws Throwable {
        assertTablePresent("tree");
        String[][] table = {{"root", "", ""},
        {"child1 ;semicolon", "", "child2", ""},
        {"child1.1", "", "child2.1", "child2.2"}};
        assertTableMatch("tree", table);
    }

}
