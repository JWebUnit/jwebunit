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

    public ExpectedCell(String expectedValue) {
        this(expectedValue, 1);
    }

    public ExpectedCell(String expectedValue, int colspan) {
        this.expectedValue = expectedValue;
        this.colspan = colspan;
    }

    public int getColspan() {
        return colspan;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

}
