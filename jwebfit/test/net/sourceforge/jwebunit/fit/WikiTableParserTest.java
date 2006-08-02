/*
 * Created on Feb 25, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package net.sourceforge.jwebunit.fit;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * @author djoiner
 */
public class WikiTableParserTest extends TestCase {
	
	public void testSingleColumnSingleRow() throws IOException {
		String expected = 
			"<table border=\"1\">\n<tr><td>r1c1</td></tr>\n</table>";
		assertEquals(expected, parse("|r1c1|"));
	}
	
	public void testSingleColumnSingleRowWithTrailingSpace() throws IOException {
		String expected = 
			"<table border=\"1\">\n<tr><td>r1c1</td></tr>\n</table>";
		assertEquals(expected, parse("|r1c1| "));
	}
	
	public void testColspan() throws IOException {
		String expected = 
			"<table border=\"1\">\n<tr><td colspan=\"2\">r1c1</td></tr>\n</table>";
		assertEquals(expected, parse("|r1c1||"));
	}
	
	public void testSecondCellColspan() throws IOException {
		String expected = 
			"<table border=\"1\">\n<tr><td>r1c1</td><td colspan=\"2\">r1c2</td></tr>\n</table>";
		assertEquals(expected, parse("|r1c1|r1c2||"));
	}
	
	public void testSecondCellEmpty() throws IOException {
		String expected = 
			"<table border=\"1\">\n<tr><td>r1c1</td><td>&nbsp;</td></tr>\n</table>";
		assertEquals(expected, parse("|r1c1| |"));
	}

	public void testSingleColumnTwoRows() throws IOException {
		String expected = 
			"<table border=\"1\">\n<tr><td>r1c1</td></tr>\n<tr><td>r2c1</td></tr>\n</table>";
		assertEquals(expected, parse("|r1c1|\n|r2c1|"));
	}

	public void testTwoTables() throws IOException {
		String expected = "<table border=\"1\">\n<tr><td>r1c1</td></tr>\n</table>";
		expected += "\n<p>\n<table border=\"1\">\n<tr><td>r1c1</td></tr>\n</table>";
		String result = parse("|r1c1|\n\n|r1c1|");
		assertEquals(expected, result);
	}

	public void testTwoTablesWithComments() throws IOException {
		String expected = "<table border=\"1\">\n<tr><td>r1c1</td></tr>\n</table>";
		expected += "\nBlah\n<table border=\"1\">\n<tr><td>r1c1</td></tr>\n</table>";
		String result = parse("|r1c1|\nBlah\n|r1c1|");
		System.out.println(result);
		assertEquals(expected, result);
	}

	private String parse(String in) throws IOException {
		return new WikiParser().parse(new StringReader(in));
	}
}
