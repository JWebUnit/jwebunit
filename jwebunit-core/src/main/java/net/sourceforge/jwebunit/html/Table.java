/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.html;

import java.util.ArrayList;

import net.sourceforge.jwebunit.exception.AssertEqualsException;
import net.sourceforge.jwebunit.exception.AssertMatchException;

/**
 * Represents an expected table for comparison with an actual html table.
 * 
 * @author Julien Henry
 */
public class Table {

    private ArrayList<Row> rows = new ArrayList<Row>();

    /**
     * Construct a table without providing any contents; they can be appended
     * subsequently.
     */
    public Table() {
    }

    /**
     * Construct a table from a two dimensional array of objects. Each object's
     * string value will be used with a colspan of 1, unless an object is an
     * {@link net.sourceforge.jwebunit.html.Cell}, in which case its defined
     * value and colspan are used.
     * 
     * @param values
     *            two-dimensional array representing table cells.
     */
    public Table(Object[][] values) {
        appendRows(values);
    }

    /**
     * Append any number of rows, represented by a two dimensional array of
     * objects. Each object's string value will be used with a colspan of 1,
     * unless an object is an {@link net.sourceforge.jwebunit.html.Cell}, in
     * which case its defined value and colspan are used.
     * 
     * @param newExpectedValues
     *            two-dimensional array representing expected table cells.
     */
    public void appendRows(Object[][] newExpectedValues) {
        for (int i = 0; i < newExpectedValues.length; i++) {
            rows.add(new Row(newExpectedValues[i]));
        }
    }

    /**
     * Append another table's rows.
     * 
     * @param table
     *            table whose rows are to be appended.
     */
    public void appendRows(Table table) {
        rows.addAll(table.getRows());
    }

    /**
     * Append a single expected row.
     * 
     * @param row
     *            row to be appended.
     */
    public void appendRow(Row row) {
        rows.add(row);
    }

    public int getRowCount() {
        return getRows().size();
    }

    ArrayList<Row> getRows() {
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

    public void assertEquals(Table t) throws AssertEqualsException {
        if(this.getRows().size() != t.getRows().size()) {
            throw new AssertEqualsException(t.toString(), this.toString());
        }
        for (int i = 0; i < this.getRows().size(); i++) {
            ((Row) this.getRows().get(i))
                    .assertEquals((Row) t.getRows().get(i));
        }
    }

    public void assertSubTableEquals(int startRow, Table t) throws AssertEqualsException {
        Table sub = new Table();
        if (startRow + t.getRowCount() > this.getRowCount())
            throw new AssertEqualsException(t.toString(), this.toString());
        for (int i = startRow; i < startRow + t.getRowCount(); i++) {
            sub.appendRow((Row) this.getRows().get(i));
        }
        sub.assertEquals(t);
    }

    public void assertMatch(Table t) throws AssertMatchException {
        if(this.getRows().size() != t.getRows().size()) {
            throw new AssertMatchException(t.toString(), this.toString());
        }
        for (int i = 0; i < this.getRows().size(); i++) {
            ((Row) this.getRows().get(i)).assertMatch((Row) t.getRows().get(i));
        }
    }

    public void assertSubTableMatch(int startRow, Table t) throws AssertMatchException {
        Table sub = new Table();
        if (startRow + t.getRowCount() > this.getRowCount())
            throw new AssertMatchException(t.toString(), this.toString());
        for (int i = startRow; i < startRow + t.getRowCount(); i++) {
            sub.appendRow((Row) this.getRows().get(i));
        }
        sub.assertMatch(t);
    }

}
