/**
 * Copyright (c) 2002-2012, JWebUnit team.
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

import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;

import org.junit.Test;

import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Table;

/**
 * Test table equals assertions using expected tables.
 */
public class ExpectedTableAssertionsHtmlTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/ExpectedTableAssertionsTest");
        beginAt("/TableAssertionsTestPageHtml.html");
    }

    @Test
    public void testAssertTableEquals() throws Throwable {
        Cell[][] cells = new Cell[4][];
        cells[0] = new Cell[3];
        cells[0][0] = new Cell("", 1, 2);
        cells[0][1] = new Cell("Average", 2, 1);
        cells[0][2] = new Cell("Red eyes", 1, 2);
        cells[1] = new Cell[2];
        cells[1][0] = new Cell("height", 1, 1);
        cells[1][1] = new Cell("weight", 1, 1);
        cells[2] = new Cell[4];
        cells[2][0] = new Cell("Males", 1, 1);
        cells[2][1] = new Cell("1.9", 1, 1);
        cells[2][2] = new Cell("0.003", 1, 1);
        cells[2][3] = new Cell("40%", 1, 1);
        cells[3] = new Cell[4];
        cells[3][0] = new Cell("Females", 1, 1);
        cells[3][1] = new Cell("1.7", 1, 1);
        cells[3][2] = new Cell("0.002", 1, 1);
        cells[3][3] = new Cell("43%", 1, 1);
        Table table = new Table(cells);
        assertPass("assertTableEquals", new Object[] { "myTable", table });
    }

    @Test
    public void testAssertTableEqualsMissingRows() throws Throwable {
        Cell[][] cells = new Cell[3][];
        cells[0] = new Cell[3];
        cells[0][0] = new Cell("", 1, 2);
        cells[0][1] = new Cell("Average", 2, 1);
        cells[0][2] = new Cell("Red eyes", 1, 2);
        cells[1] = new Cell[2];
        cells[1][0] = new Cell("height", 1, 1);
        cells[1][1] = new Cell("weight", 1, 1);
        cells[2] = new Cell[4];
        cells[2][0] = new Cell("Males", 1, 1);
        cells[2][1] = new Cell("1.9", 1, 1);
        cells[2][2] = new Cell("0.003", 1, 1);
        cells[2][3] = new Cell("40%", 1, 1);
        Table table = new Table(cells);
        assertPass("assertTableRowsEqual", new Object[] { "myTable",
                Integer.valueOf(0), table });
        assertFail("assertTableEquals", new Object[] { "myTable", table });
    }

}
