package net.sourceforge.jwebunit.fit;

/**
 * Abstract class to represent the data of a table row.  Subclass and override
 * constructor to map cells in a given table row to fields or methods on the
 * subclass.
 *
 * @author Jim Weaver
 */
public abstract class TableRow {

    protected String[] rowCells;

    public TableRow(String[] rowCells) {
        this.rowCells = rowCells;
    }

}
