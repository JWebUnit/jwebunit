/*
 * Created on Feb 25, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package net.sourceforge.jwebunit.fit;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author djoiner
 */
public class WikiParser {

	private boolean inTable = false;

	public String parse(Reader input) throws IOException {
		LineNumberReader reader = new LineNumberReader(input);
		String line = reader.readLine();
		StringBuffer result = new StringBuffer();
		while (line != null) {
			parseLine(line.trim(), result);
			line = reader.readLine();
		}
		if (inTable)
			result.append("</table>");
		return result.toString();
	}

	public void parseLine(String line, StringBuffer result) {
		if (line.startsWith("|")) {
			if (!inTable) {
				result.append("<table border=\"1\">\n");
				inTable = true;
			}
			result.append(parseRow(line));
		} else {
			if (inTable) {
				result.append("</table>\n");
				inTable = false;
			}
			if (line.equals("")) {
				result.append("<p>");
			} else {
				result.append(line);
			}
		
		}
		result.append("\n");
	}

	public String parseRow(String input) {
		List cells = parseCells(input);
		return rowToString(cells);
	}

	public List parseCells(String input) {
		List cells = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(input, "|", true);
		TableData currentData = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!"|".equals(token)) {
				currentData = new TableData(token);
				cells.add(currentData);
			} else if (currentData != null) {
				currentData.colspan++;
			}
		
		}
		return cells;
	}

	public String rowToString(List cells) {
		StringBuffer result = new StringBuffer("<tr>");
		for (Iterator iter = cells.iterator(); iter.hasNext();) {
			TableData td = (TableData) iter.next();
			result.append(td.toString());
		}
		result.append("</tr>");
		return result.toString();
	}

	class TableData {
		String data;
		int colspan;
		TableData(String data) {
			this.data = data.trim().equals("") ? "&nbsp;" : data;
			colspan = 0;
		}

		public String toString() {
			String openTag = "";
			if (colspan > 1) {
				openTag = "<td colspan=\"" + colspan + "\">";
			} else {
				openTag = "<td>";
			}
			return openTag + data + "</td>";
		}
	}

}
