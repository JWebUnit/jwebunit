/**
 * Copyright (c) 2002-2014, JWebUnit team.
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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableMatch;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTablePresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;

import org.junit.Test;

public class TableAssertionsTest extends JWebUnitAPITestCase {

	public void setUp() throws Exception {
		super.setUp();
		setBaseUrl(HOST_PATH + "/TableAssertionsTest");
		beginAt("/TableAssertionsTestPage.html");
	}

	@Test public void testAssertTablePresent() throws Throwable {
		assertPassFail("assertTablePresent", "testTable", "noTable");
	}

	@Test public void testAssertTableNotPresent() throws Throwable {
		assertPassFail("assertTableNotPresent", "noTable", "testTable");
	}

	@Test public void testAssertTextInTable() throws Throwable {
		assertPassFail("assertTextInTable", new Object[] { "testTable",
				"table text" }, new Object[] { "testTable", "no such text" });
	}

	@Test public void testAssertTextNotInTable() throws Throwable {
		assertPassFail("assertTextNotInTable", new Object[] { "testTable",
				"no such text" }, new Object[] { "testTable", "table text" });
	}

	@Test public void testAssertTextArrayInTable() throws Throwable {
		assertPassFail("assertTextInTable", new Object[] { "testTable",
				new String[] { "table text", "table text row 2" } },
				new Object[] { "testTable",
						new String[] { "table text", "no such row 2" } });
	}

	@Test public void testAssertTextArrayNotInTable() throws Throwable {
		assertPassFail("assertTextNotInTable", new Object[] { "testTable",
				new String[] { "no such row 1", "no such row 2" } },
				new Object[] { "testTable",
						new String[] { "no such row 1", "table text row 2" } });
	}

    @Test public void testAssertMatchInTable() throws Throwable {
        assertPassFail("assertMatchInTable",
                       new Object[]{"testTable", "table [Tt]ext"},
                       new Object[]{"testTable", "no.*text"});
    }

    @Test public void testAssertNoMatchInTable() throws Throwable {
        assertPassFail("assertNoMatchInTable",
                       new Object[]{"testTable", "no.*text"},
                       new Object[]{"testTable", "table [Tt]ext"});
    }

    @Test public void testAssertMatchArrayInTable() throws Throwable {
        assertPassFail("assertMatchInTable",
                       new Object[]{"testTable", new String[]{"table [Tt]ext", "table [Tt]ext row 2"}},
                       new Object[]{"testTable", new String[]{"table [Tt]ext", "no.*row 2"}});
    }

    @Test public void testAssertNoMatchArrayInTable() throws Throwable {
        assertPassFail("assertNoMatchInTable",
                       new Object[]{"testTable", new String[]{"no.*row 1", "no.*row 2"}},
                       new Object[]{"testTable", new String[]{"no.*row 1", "table [Tt]ext row 2"}});
    }

	@Test public void testAssertTableEquals() throws Throwable {
		assertPass("assertTableEquals", new Object[] {
				"testTable",
				new String[][] { { "table text" },
						{ "table text row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

    @Test public void testAssertTableRowCountEquals() throws Throwable {
        assertPassFail("assertTableRowCountEquals", new Object[] {
                "tree", new Integer(3)}, new Object[] {
                        "tree", Integer.valueOf(4)});
    }

    @Test public void testAssertTableEqualsExtraColumn() throws Throwable {
		assertFail("assertTableEquals", new Object[] {
				"testTable",
				new String[][] { { "table text", "extra column" },
						{ "table text row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

	@Test public void testAssertTableEqualsExtraRow() throws Throwable {
		assertFail("assertTableEquals",
				new Object[] {
						"testTable",
						new String[][] { { "table text" },
								{ "table text row 2" },
								{ "table text row 3", "row 3 col 1" },
								{ "no row 4" } } });
	}

	@Test public void testAssertTableEqualsInvalidColumnText() throws Throwable {
		assertFail("assertTableEquals", new Object[] {
				"testTable",
				new String[][] { { "table text" },
						{ "no such text in row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

	@Test public void testAssertTableEqualsMissingText() throws Throwable {
		assertFail("assertTableEquals",
				new Object[] {
						"testTable",
						new String[][] { { "table text" },
								{ "table text row 2" },
								{ "table text row 3", "" } } });
	}

	@Test public void testAssertTableRowsEquals() throws Throwable {
		assertPass("assertTableRowsEqual", new Object[] {
				"testTable",
				Integer.valueOf(1),
				new String[][] { { "table text row 2" },
						{ "table text row 3", "row 3 col 1" } } });
	}

	@Test public void testAssertTableRowsEqualsTooManyExpected() throws Throwable {
		assertFail("assertTableRowsEqual", new Object[] {
				"testTable",
				Integer.valueOf(2),
				new String[][] { { "table text row 3", "row 3 col 1" },
						{ "unexpected" } } });
	}

	@Test public void testTableWithSpaces() throws Throwable {
		assertTablePresent("tree");
		String[][] table = { { "root", " ", "" },
				{ "child1 ;semicolon", " ", "child2", " " },
				{ "child1.1", "", "child2.1", "child2.2" } };
		assertTableEquals("tree", table);
	}

    @Test public void testAssertTableMatch() throws Throwable {
        assertPass("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table [Tt]ext"},
                                                            {"table [Tt]ext row 2"},
                                                            {"table [Tt]ext row 3", "row [0-9] col 1"}}});
    }

    @Test public void testAssertTableMatchExtraColumn() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table text", "", "extra column"},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "row 3 col 1"}}});
    }

    @Test public void testAssertTableMatchExtraRow() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "row 3 col 1"},
                                                            {"no row 4"}}});
    }

    @Test public void testAssertTableMatchInvalidColumnText() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table [Tt]ext", ""},
                                                            {"no such [Tt]ext in row 2", ""},
                                                            {"table text row 3", "row 3 col 1"}}});
    }

    @Test public void testAssertTableMatchMissingText() throws Throwable {
        assertFail("assertTableMatch",
                   new Object[]{"testTable", new String[][]{{"table text", ""},
                                                            {"table text row 2", ""},
                                                            {"table text row 3", "^$"}}});
    }

    @Test public void testAssertTableRowsMatch() throws Throwable {
        assertPass("assertTableRowsMatch",
                   new Object[]{"testTable",
                                Integer.valueOf(1),
                                new String[][]{{"table text row 2"},
                                               {"table text row 3", "row 3 col 1"}}});
    }

    @Test public void testAssertTableRowsMatchTooManyExpected() throws Throwable {
        assertFail("assertTableRowsMatch",
                   new Object[]{"testTable",
                                Integer.valueOf(2),
                                new String[][]{{"table text row 3", "row 3 col 1"},
                                               {"unexpected", ""}}});
    }
    
    @Test public void testTableWithSpacesMatch() throws Throwable {
        assertTablePresent("tree");
        String[][] table = {{"root", "", ""},
        {"child1 ;semicolon", "", "child2", ""},
        {"child1.1", "", "child2.1", "child2.2"}};
        assertTableMatch("tree", table);
    }

}
