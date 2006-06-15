package net.sourceforge.jwebunit;
import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import junit.framework.TestCase;

/**
 * Test the methods of Table. Exemple come from 
 * <a href="http://www.w3.org/TR/html401/struct/tables.html#h-11.1">HTML 4.01 specs</a>.
 * @author Julien Henry
 *
 */
public class TableTest extends TestCase {
    
    private Table table;

    public void setUp() throws Exception {
        super.setUp();
        Cell[][] cells = new Cell[4][];
        cells[0] = new Cell[3];
        cells[0][0]=new Cell("",1,2);
        cells[0][1]=new Cell("Average",2,1);
        cells[0][2]=new Cell("Red eyes",1,2);
        cells[1] = new Cell[2];
        cells[1][0]=new Cell("height",1,1);
        cells[1][1]=new Cell("width",1,1);
        cells[2] = new Cell[4];
        cells[2][0]=new Cell("Males",1,1);
        cells[2][1]=new Cell("1.9",1,1);
        cells[2][2]=new Cell("0.003",1,1);
        cells[2][3]=new Cell("40%",1,1);
        cells[3] = new Cell[4];
        cells[3][0]=new Cell("Females",1,1);
        cells[3][1]=new Cell("1.7",1,1);
        cells[3][2]=new Cell("0.002",1,1);
        cells[3][3]=new Cell("43%",1,1);
        table = new Table(cells);
    }
    
    public void testAssertSubTableEqual() {
        String[] r1 = {"Males", "1.9", "0.003", "40%"};
        String[] r2 = {"Females", "1.7", "0.002", "43%"};
        Table sub = new Table();
        sub.appendRow(new Row(r1));
        sub.appendRow(new Row(r2));
        table.assertSubTableEquals(2, sub);
    }
    
    public void testAssertSubTableMatch() {
        String[] r1 = {"Mal(e|r)s", "1\\.?", "0\\.003", "40\\%"};
        String[] r2 = {"Fem(.)les", "1\\.?", "0[:punct:]002", "43\\%"};
        Table sub = new Table();
        sub.appendRow(new Row(r1));
        sub.appendRow(new Row(r2));
        table.assertSubTableMatch(2, sub);
    }
}
