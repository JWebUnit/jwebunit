package net.sourceforge.jwebunit.fit;

import fit.RowFixture;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.jwebunit.html.Table;

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
        Table sparseTable =  WebFixture.tester.getDialog().getTable(getSourceTableSummaryOrId());
        TableRow [] rowObjects = new TableRow[sparseTable.getRowCount() - getDataStartRow()];
        int index = 0;
        for (int i = getDataStartRow(); i < sparseTable.getRowCount(); i++) {
            rowObjects[index] = buildTableRow(getRowAsStringArray(sparseTable, i));
            index ++;
        }
        return rowObjects;
    }

    private String[] getRowAsStringArray(Table sparseTable, int rowNumber) {
		throw new UnsupportedOperationException("sparseTable[i] from the old JWebUnit API has not been converted to use the new Table class");
	}

	/**
     * This is zero based
     * @return
     */
    protected int getDataStartRow() {
        return 2;
    }

    private TableRow buildTableRow(String[] rowCells) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = getTargetClass().getConstructor(new Class[] {String[].class});
        return (TableRow) constructor.newInstance(new Object [] {rowCells});
    }

}
