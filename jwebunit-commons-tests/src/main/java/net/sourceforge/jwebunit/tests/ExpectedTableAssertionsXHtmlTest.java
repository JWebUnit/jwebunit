package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test table equals assertions using expected tables.
 */
public class ExpectedTableAssertionsXHtmlTest extends JWebUnitAPITestCase {
    
	public static Test suite() {
		return new JettySetup(new TestSuite(ExpectedTableAssertionsXHtmlTest.class));
	}

    public void setUp() throws Exception {
        super.setUp();
		getTestContext().setBaseUrl(HOST_PATH + "/ExpectedTableAssertionsTest");
		beginAt("/TableAssertionsTestPageXHtml.html");
    }

    public void testAssertTableEquals() throws Throwable {
        Cell[][] cells = new Cell[4][];
        cells[0] = new Cell[3];
        cells[0][0]=new Cell("",1,2);
        cells[0][1]=new Cell("Average",2,1);
        cells[0][2]=new Cell("Red eyes",1,2);
        cells[1] = new Cell[2];
        cells[1][0]=new Cell("height",1,1);
        cells[1][1]=new Cell("weight",1,1);
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
        Table table = new Table(cells);
        assertPass("assertTableEquals", new Object[]{"myTable", table});
    }

    public void testAssertTableEqualsMissingRows() throws Throwable {
        Cell[][] cells = new Cell[3][];
        cells[0] = new Cell[3];
        cells[0][0]=new Cell("",1,2);
        cells[0][1]=new Cell("Average",2,1);
        cells[0][2]=new Cell("Red eyes",1,2);
        cells[1] = new Cell[2];
        cells[1][0]=new Cell("height",1,1);
        cells[1][1]=new Cell("weight",1,1);
        cells[2] = new Cell[4];
        cells[2][0]=new Cell("Males",1,1);
        cells[2][1]=new Cell("1.9",1,1);
        cells[2][2]=new Cell("0.003",1,1);
        cells[2][3]=new Cell("40%",1,1);
        Table table = new Table(cells);
        assertFail("assertTableEquals", new Object[]{"myTable", table});
    }

}
