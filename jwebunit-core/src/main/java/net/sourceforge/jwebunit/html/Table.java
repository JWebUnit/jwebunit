/**
 * Copyright (c) 2010, JWebUnit team.
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

package net.sourceforge.jwebunit.html;

import java.util.ArrayList;

import junit.framework.Assert;

/**
 * Represents an expected table for comparison with an actual html table.
 * 
 * @author Jim Weaver
 * @author Julien Henry
 */
public class Table {

    private ArrayList rows = new ArrayList();

    /**
     * Construct a table without providing any contents; they can be appended subsequently.
     */
    public Table() {
    }

    /**
     * Construct a table from a two dimensional array of objects. Each object's string value will be used with a colspan
     * of 1, unless an object is an {@link net.sourceforge.jwebunit.html.Cell}, in which case its defined value and
     * colspan are used.
     * 
     * @param values two-dimensional array representing table cells.
     */
    public Table(Object[][] values) {
        appendRows(values);
    }

    /**
     * Append any number of rows, represented by a two dimensional array of objects. Each object's string value will be
     * used with a colspan of 1, unless an object is an {@link net.sourceforge.jwebunit.html.Cell}, in which case its
     * defined value and colspan are used.
     * 
     * @param newExpectedValues two-dimensional array representing expected table cells.
     */
    public void appendRows(Object[][] newExpectedValues) {
        for (int i = 0; i < newExpectedValues.length; i++) {
            rows.add(new Row(newExpectedValues[i]));
        }
    }

    /**
     * Append another table's rows.
     * 
     * @param table table whose rows are to be appended.
     */
    public void appendRows(Table table) {
        rows.addAll(table.getRows());
    }

    /**
     * Append a single expected row.
     * 
     * @param row row to be appended.
     */
    public void appendRow(Row row) {
        rows.add(row);
    }

    public int getRowCount() {
        return getRows().size();
    }

    public ArrayList getRows() {
        return rows;
    }

    public boolean hasText(String text) {
        for (int i = 0; i < getRowCount(); i++) {
            Row row = (Row) getRows().get(i);
            if (row.hasText(text))
                return true;
        }
        return false;
    }

    public boolean hasMatch(String regexp) {
        for (int i = 0; i < getRowCount(); i++) {
            Row row = (Row) getRows().get(i);
            if (row.hasMatch(regexp))
                return true;
        }
        return false;
    }

    public void assertEquals(Table t) {
        Assert.assertTrue("Row count are not equal", this.getRows().size() == t
                .getRows().size());
        for (int i = 0; i < this.getRows().size(); i++) {
            ((Row) this.getRows().get(i))
                    .assertEquals((Row) t.getRows().get(i));
        }
    }

    public void assertSubTableEquals(int startRow, Table t) {
        Table sub = new Table();
        if (startRow + t.getRowCount() > this.getRowCount())
            Assert.fail("Expected rows [" + t.getRowCount()
                    + "] larger than actual rows in range being compared"
                    + " [" + (this.getRowCount() - startRow) + "].");
        for (int i = startRow; i < startRow + t.getRowCount(); i++) {
            sub.appendRow((Row) this.getRows().get(i));
        }
        sub.assertEquals(t);
    }

    public void assertMatch(Table t) {
        Assert.assertTrue("Row count are not equal", this.getRows().size() == t
                .getRows().size());
        for (int i = 0; i < this.getRows().size(); i++) {
            ((Row) this.getRows().get(i)).assertMatch((Row) t.getRows().get(i));
        }
    }

    public void assertSubTableMatch(int startRow, Table t) {
        Table sub = new Table();
        if (startRow + t.getRowCount() > this.getRowCount())
            Assert.fail("Expected rows [" + t.getRowCount()
                    + "] larger than actual rows in range being compared"
                    + " [" + (this.getRowCount() - startRow) + "].");
        for (int i = startRow; i < startRow + t.getRowCount(); i++) {
            sub.appendRow((Row) this.getRows().get(i));
        }
        sub.assertMatch(t);
    }

}
