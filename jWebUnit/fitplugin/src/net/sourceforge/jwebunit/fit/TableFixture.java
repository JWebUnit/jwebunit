package net.sourceforge.jwebunit.fit;

import fit.Fixture;
import fit.Parse;
import net.sourceforge.jwebunit.ExpectedTable;
import net.sourceforge.jwebunit.ExpectedRow;
import net.sourceforge.jwebunit.ExpectedCell;
import junit.framework.AssertionFailedError;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Fixture to validate that a target html table matches the parsed fit table.
 *
 * @author Jim Weaver
 */
public class TableFixture extends Fixture {

    ExpectedTable table = new ExpectedTable();
    String tableSummaryOrId;

//    public Class getTargetClass() {
//        return Option.class;
//    }

//    public Object[] query() throws Exception  {
//        String[] labels = WebFixture.tester.getDialog().getOptionsFor(selectName);
//        String[] values = WebFixture.tester.getDialog().getOptionValuesFor(selectName);
//        return Option.buildOptions(labels, values);
//    }


    public void doRows(Parse rows) {
        tableSummaryOrId = rows.parts.text();
        super.doRows(rows.more);
        try {
            WebFixture.tester.assertTableEquals(tableSummaryOrId, table);
            right(rows.parts);
        } catch (AssertionFailedError e) {
            wrong(rows.parts, e.getMessage());
        }
    }

    public void doRow(Parse row) {
        table.appendRow(buildExpectedRow(row));
    }

    private ExpectedRow buildExpectedRow(Parse row) {
        ExpectedRow expectedRow = new ExpectedRow(buildExpectedCells(row.parts));
        return expectedRow;
    }

    private ExpectedCell[] buildExpectedCells(Parse cells) {
        ExpectedCell[] expectedCells = new ExpectedCell [cells.size()];
        for (int i=0; cells != null; i++) {
            expectedCells[i] = new ExpectedCell(cells.text(), getColSpan(cells));
            cells = cells.more;
        }
        return expectedCells;
    }

    private int getColSpan(Parse cells) {
        Perl5Util util = new Perl5Util();
        return (util.match("/colspan=\"(\\d+)\"/", cells.tag)) ?
                Integer.parseInt(util.group(1)) : 1;
    }

//    private void compareTables(Parse rows) {
//        String[][] expectedCellValues = table.getExpectedStrings();
//        String[][] sparseTableCellValues = dialog.getSparseTableBySummaryOrId(tableSummaryOrId);
//        if (expectedCellValues.length > (sparseTableCellValues.length - startRow))
////            Assert.fail("Expected rows [" + expectedCellValues.length + "] larger than actual rows in range being compared" +
////                    " [" + (sparseTableCellValues.length - startRow) + "].");
//        for (int i = 0; i < expectedCellValues.length; i++) {
//            String[] row = expectedCellValues[i];
//            for (int j = 0; j < row.length; j++) {
//                if (row.length != sparseTableCellValues[i].length)
////                    Assert.fail("Unequal number of columns for row " + i + " of table " + tableSummaryOrId +
////                            ". Expected [" + row.length + "] found [" + sparseTableCellValues[i].length + "].");
//                String expectedString = row[j];
////                Assert.assertEquals("Expected " + tableSummaryOrId + " value at [" + i + "," + j + "] not found.",
////                        expectedString, context.toEncodedString(sparseTableCellValues[i + startRow][j].trim()));
//            }
//        }
//    }

}
