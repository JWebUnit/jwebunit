package net.sourceforge.jwebunit.fit;

import fit.RowFixture;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Fixture to validate data in Html tables.  To test a given table, a concrete subclass
 * of this fixture as well as a concrete subclass of a TableRow must be provided.
 *
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public abstract class TableFixture extends RowFixture {

    protected abstract String getSourceTableSummaryOrId();

    public Object[] query() throws Exception {
        String [][] sparseTable =  WebFixture.tester.getDialog().getSparseTableBySummaryOrId(getSourceTableSummaryOrId());
        TableRow [] rowObjects = new TableRow[sparseTable.length - getDataStartRow()];
        int index = 0;
        for (int i = getDataStartRow(); i < sparseTable.length; i++) {
            rowObjects[index] = buildTableRow(sparseTable[i]);
            index ++;
        }
        return rowObjects;
    }

    protected int getDataStartRow() {
        return 2;
    }

    private TableRow buildTableRow(String[] rowCells) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = getTargetClass().getConstructor(new Class[] {String[].class});
        return (TableRow) constructor.newInstance(new Object [] {rowCells});
    }

}
