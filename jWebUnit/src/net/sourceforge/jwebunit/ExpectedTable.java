package net.sourceforge.jwebunit;

import java.util.ArrayList;

/**
 * Create an expected table for comparison with actual.  Support colspans and
 * appending of expected rows.
 *
 * @author Jim Weaver
 */
public class ExpectedTable {

    private ArrayList expectedRows = new ArrayList();

    public ExpectedTable() {
    }

    public ExpectedTable(Object[][] expectedValues) {
        appendRows(expectedValues);
    }

    public void appendRows(Object[][] newExpectedValues) {
        for (int i = 0; i < newExpectedValues.length; i++) {
            Object[] expectedColumns = newExpectedValues[i];
            expectedRows.add(new ExpectedRow(expectedColumns));
        }
    }

    public void appendRows(ExpectedTable exptectedTable) {
        expectedRows.addAll(exptectedTable.getExpectedRows());
    }

    public String[][] getExpectedStrings() {
        String[][] asStringArray = new String[expectedRows.size()][];
        for (int i = 0; i < expectedRows.size(); i++) {
            ExpectedRow expectedRow = (ExpectedRow)expectedRows.get(i);
            asStringArray[i] = expectedRow.getExpandedColumns();
        }
        return asStringArray;
    }

    public String toString() {
        StringBuffer asString = new StringBuffer();
        String[][] asStringArray = getExpectedStrings();
        for (int i = 0; i < asStringArray.length; i++) {
            asString.append("{");
            Object[] expectedRow = asStringArray[i];
            for (int j = 0; j < expectedRow.length; j++) {
                asString.append("{");
                String column = (String) expectedRow[j];
                asString.append(column);
                asString.append("}");
            }
            asString.append("}");
        }
        return asString.toString();
    }

    ArrayList getExpectedRows() {
        return expectedRows;
    }

}
