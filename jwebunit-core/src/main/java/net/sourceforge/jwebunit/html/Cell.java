package net.sourceforge.jwebunit.html;

import junit.framework.Assert;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Represents a cell of an html table - a string value spanning an
 * indicated amount of columns.
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
     * Return the colspan for this cell.
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * Return the rowspan for this cell.
     */
    public int getRowspan() {
        return rowspan;
    }

    /**
     * Return the text for the cell.
     */
    public String getValue() {
        return value;
    }
    
    public void assertEquals(Cell c) {
        Assert.assertTrue(c.getValue()+" do not equal "+this.getValue(), this.getValue().equals(c.getValue()));
        Assert.assertTrue("Expected colspan was "+c.getColspan()+" but was "+this.getColspan(), this.getColspan()==c.getColspan());
        Assert.assertTrue("Expected rowspan was "+c.getRowspan()+" but was "+this.getRowspan(), this.getRowspan()==c.getRowspan());
    }

    public void assertMatch(Cell c) {
        RE re = getRE(c.getValue());
        Assert.assertTrue(c.getValue()+" do not match "+this.getValue(), re.match(this.getValue()));
        Assert.assertTrue("Expected colspan was "+c.getColspan()+" but was "+this.getColspan(), this.getColspan()==c.getColspan());
        Assert.assertTrue("Expected rowspan was "+c.getRowspan()+" but was "+this.getRowspan(), this.getRowspan()==c.getRowspan());
    }
    
    public boolean equals(String text) {
        return this.getValue().equals(text);
    }
    
    public boolean match(String regexp) {
        RE re = getRE(regexp);
        return re.match(this.getValue());
    }

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
