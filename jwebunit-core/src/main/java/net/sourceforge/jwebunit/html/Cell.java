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
package net.sourceforge.jwebunit.html;

import junit.framework.Assert;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Represents a cell of an html table - a string value spanning an indicated amount of columns.
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
     * @param value text expected within the cell.
     */
    public Cell(String value) {
        this(value, 1, 1);
    }

    /**
     * Construct a cell with a specified colspan.
     * 
     * @param value text expected within the cell.
     * @param colspan number of columns the cell is expected to span.
     * @param rowspan number of rows the cell is expected to span.
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
     * Assert that the current cell equals given one. Check text, colspan and rowspan.
     * 
     * @param c given cell
     */
    public void assertEquals(Cell c) {
        Assert.assertTrue(c.getValue() + " do not equal " + this.getValue(),
                this.getValue().equals(c.getValue()));
        Assert.assertTrue("Expected colspan was " + c.getColspan()
                + " but was " + this.getColspan(), this.getColspan() == c
                .getColspan());
        Assert.assertTrue("Expected rowspan was " + c.getRowspan()
                + " but was " + this.getRowspan(), this.getRowspan() == c
                .getRowspan());
    }

    /**
     * Assert that the current cell matches given one. Check colspan and rowspan. Regexp is in text of given cell.
     * 
     * @param c given cell
     */
    public void assertMatch(Cell c) {
        RE re = getRE(c.getValue());
        Assert.assertTrue(c.getValue() + " do not match " + this.getValue(), re
                .match(this.getValue()));
        Assert.assertTrue("Expected colspan was " + c.getColspan()
                + " but was " + this.getColspan(), this.getColspan() == c
                .getColspan());
        Assert.assertTrue("Expected rowspan was " + c.getRowspan()
                + " but was " + this.getRowspan(), this.getRowspan() == c
                .getRowspan());
    }

    /**
     * Check if the current cell contains given text.
     * 
     * @param text given text.
     * @return true if the current cell contains given text.
     */
    public boolean equals(String text) {
        return this.getValue().equals(text);
    }

    /**
     * Check if the current cell matches given text.
     * 
     * @param regexp given regexp.
     * @return true if the current cell matches given text.
     */
    public boolean match(String regexp) {
        RE re = getRE(regexp);
        return re.match(this.getValue());
    }

    /**
     * Create a regexp.
     * 
     * @param regexp regexp pattern
     * @return regexp object
     */
    private RE getRE(String regexp) {
        RE re = null;
        try {
            re = new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            Assert.fail(e.toString());
        }
        return re;
    }
}
