/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit;

/**
 * Represents an expected cell of an html table - a string value spanning an
 * indicated amount of columns.
 *
 * @author Jim Weaver
 */
public class ExpectedCell {

    private int colspan;
    private String expectedValue;

    /**
     * Construct an expected cell with a default colspan of 1.
     * @param expectedValue text expected within the cell.
     */
    public ExpectedCell(String expectedValue) {
        this(expectedValue, 1);
    }

    /**
     * Construct an expected cell with a specified colspan.
     *
     * @param expectedValue text expected within the cell.
     * @param colspan number of columns the cell is expected to span.
     */
    public ExpectedCell(String expectedValue, int colspan) {
        this.expectedValue = expectedValue;
        this.colspan = colspan;
    }

    /**
     * Return the colspan for this cell.
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * Return the expected text for the cell.
     */
    public String getExpectedValue() {
        return expectedValue;
    }

}
