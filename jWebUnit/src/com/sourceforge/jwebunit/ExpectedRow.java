package net.sourceforge.jwebunit;


/**
 * Represents an expected row of an html table.  Used internal to package only for
 * the time being.  May make public if we need to support rowspan.
 *
 * @author Jim Weaver
 */
class ExpectedRow {

    private ExpectedCell[] expectedCells;

    ExpectedRow(Object[] columns) {
        this.expectedCells = new ExpectedCell[columns.length];
        for (int i = 0; i < columns.length; i++) {
            Object column = columns[i];
            if (column instanceof ExpectedCell) {
                this.expectedCells[i] = (ExpectedCell)column;
            } else {
                this.expectedCells[i] = new ExpectedCell(column.toString(), 1);
            }
        }
    }

    String[] getExpandedColumns() {
        String[] expandedColumns = new String[getNumberOfColumns()];
        int targetColumn = 0;
        for (int i = 0; i < expectedCells.length; i++) {
            targetColumn = expandIntoColumns(expectedCells[i], expandedColumns, targetColumn);
        }
        return expandedColumns;
    }

    private int getNumberOfColumns() {
        int numCols = 0;
        for (int i = 0; i < expectedCells.length; i++) {
            ExpectedCell column = expectedCells[i];
            numCols += (column).getColspan();
        }
        return numCols;
    }

    private int expandIntoColumns(ExpectedCell cell, String[] targetArray, int offset) {
        for (int columnsSpanned = 0; columnsSpanned < cell.getColspan(); columnsSpanned++) {
            targetArray[offset] = cell.getExpectedValue();
            offset++;
        }
        return offset;
    }

}
