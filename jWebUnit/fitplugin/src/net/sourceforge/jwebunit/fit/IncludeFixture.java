/*
 * Created on Feb 24, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package net.sourceforge.jwebunit.fit;

import java.io.PrintWriter;
import java.io.StringWriter;

import fit.Fixture;
import fit.Parse;

/**
 * @author djoiner
 */
public class IncludeFixture extends Fixture {
	
	public void doRow(Parse cells) {
		Parse dataCell =cells.parts; 
		String fname = dataCell.text();
		Parse tables;
		try {
			String c = FileRunner.readIncludeFile(fname);
			tables = new Parse(c);
			this.doTables(tables);
			StringWriter st = new StringWriter();
			tables.print(new PrintWriter(st));
			dataCell.addToBody(st.toString());
		} catch (Exception e) {
			exception(dataCell, e);
			e.printStackTrace();
			
		}				
	}

}
