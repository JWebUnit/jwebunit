package net.sourceforge.jwebunit;

import junit.framework.TestCase;

/**
 * Unit test expected table design.
 */
public class ExpectedTableTest extends TestCase {

    public ExpectedTableTest(String s) {
        super(s);
    }

    /**
     * Should be able to construct with a simple string [][].
     */
    public void testConstructionWithStrings() {
        ExpectedTable table = new ExpectedTable(new Object [][]
            {{"1_1", "1_2"},{"2_1", "2_2"}});
        assertEquals(table.toString(), "{{1_1}{1_2}}{{2_1}{2_2}}");
    }

    /**
     * Should be able to construct with an object [][] containing
     * strings and expected cell objects.
     */
    public void testConstructionWithExpectedCell() {
        ExpectedTable table = new ExpectedTable(new Object [][]
            { {"1_1", "1_2", "1_3"}, {"2_1", new ExpectedCell("2_2-3",2)} } );
        assertEquals(table.toString(), "{{1_1}{1_2}{1_3}}{{2_1}{2_2-3}{2_2-3}}");
    }

    /**
     * Should be able to append expected rows - will make reuse of common
     * expected table rows in test data easier.
     */
    public void testAppendRows() {
        ExpectedTable table = new ExpectedTable(new Object [][]
            {{"1_1", "1_2"},{"2_1", "2_2"}});
        table.appendRows(new Object[][] {{"3_1"}});
        assertEquals(table.toString(), "{{1_1}{1_2}}{{2_1}{2_2}}{{3_1}}");
    }

    public void testAppendTables() {
        ExpectedTable table = new ExpectedTable(new Object [][]
            {{"1_1", "1_2"},{"2_1", "2_2"}});
        table.appendRows(new ExpectedTable(new Object[][]{{"3_1"}}));
        assertEquals(table.toString(), "{{1_1}{1_2}}{{2_1}{2_2}}{{3_1}}");
    }

}
