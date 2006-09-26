/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.html;

import net.sourceforge.jwebunit.exception.AssertEqualsException;
import net.sourceforge.jwebunit.exception.AssertMatchException;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Represents a cell of an html table - a string value spanning an indicated
 * amount of columns.
 * 
 * @author Jim Weaver
 * @author Julien Henry
 */
public class Cell {

    private int colspan;

    private int rowspan;

    private String value;

    /**
     * Construct a cell with a default colspan/rowspan of 1.
     * 
     * @param value
     *            text expected within the cell.
     */
    public Cell(String value) {
        this(value, 1, 1);
    }

    /**
     * Construct a cell with a specified colspan.
     * 
     * @param value
     *            text expected within the cell.
     * @param colspan
     *            number of columns the cell is expected to span.
     * @param rowspan
     *            number of rows the cell is expected to span.
     */
    public Cell(String value, int colspan, int rowspan) {
        this.value = value;
        this.colspan = colspan;
        this.rowspan = rowspan;
    }

    /**
     * @return the colspan for this cell.
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * @return the rowspan for this cell.
     */
    public int getRowspan() {
        return rowspan;
    }

    /**
     * @return the text for the cell.
     */
    public final String getValue() {
        return value;
    }

    /**
     * Assert that the current cell equals given one. Check text, colspan and
     * rowspan.
     * 
     * @param c
     *            given cell
     */
    public void assertEquals(Cell c) throws AssertEqualsException {
        if (!this.getValue().equals(c.getValue())) {
            throw new AssertEqualsException(c.toString(), this.toString());
        }
        if (this.getColspan() != c.getColspan()) {
            throw new AssertEqualsException(c.toString(), this.toString());
        }
        if (this.getRowspan() != c.getRowspan()) {
            throw new AssertEqualsException(c.toString(), this.toString());
        }
    }

    /**
     * Assert that the current cell matches given one. Check colspan and
     * rowspan. Regexp is in text of given cell.
     * 
     * @param c
     *            given cell
     */
    public void assertMatch(Cell c) throws AssertMatchException, RESyntaxException {
        RE re = getRE(c.getValue());
        if (!re.match(this.getValue())) {
            throw new AssertMatchException(c.getValue(), this.toString());
        }
        if (this.getColspan() != c.getColspan()) {
            throw new AssertMatchException(c.toString(), this.toString());
        }
        if (this.getRowspan() != c.getRowspan()) {
            throw new AssertMatchException(c.toString(), this.toString());
        }
    }

    /**
     * Check if the current cell contains given text.
     * 
     * @param text
     *            given text.
     * @return true if the current cell contains given text.
     */
    public boolean equals(String text) {
        return this.getValue().equals(text);
    }

    /**
     * Check if the current cell matches given text.
     * 
     * @param regexp
     *            given regexp.
     * @return true if the current cell matches given text.
     */
    public boolean match(String regexp) {
        RE re = getRE(regexp);
        return re.match(this.getValue());
    }

    /**
     * Create a regexp.
     * 
     * @param regexp
     *            regexp pattern
     * @return regexp object
     */
    private RE getRE(String regexp) throws RESyntaxException {
        return new RE(regexp, RE.MATCH_SINGLELINE);
    }
}
