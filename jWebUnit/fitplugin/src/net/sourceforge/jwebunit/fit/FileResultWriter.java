/*
 * User: djoiner
 * Date: Oct 5, 2002
 * Time: 1:11:31 PM
 */
package net.sourceforge.jwebunit.fit;

import fit.Fixture;
import fit.Parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileResultWriter extends FitResultWriter {
    private Parse tables;
    private Fixture.Counts counts;

    public FileResultWriter(File outname, Fixture.Counts counts, Parse tables) {
        super(outname);
        this.counts = counts;
        this.tables = tables;
    }

    public String getLinkString() {
        return getOutput().getName();
    }

    public String getDisplayName() {
        int i = getOutput().getName().indexOf(".fit.out.html");
        return getOutput().getName().substring(0, i);
    }

    public Fixture.Counts getCounts() {
        return counts;
    }

    public void write() {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(getOutput())));
            tables.print(pw);
            pw.flush();
        } catch (IOException e) {
            exception(e);
        }
    }

    protected void exception(Exception e) {
        tables = new Parse("body", "Unable to write output. Test ignored.", null, null);
        Fixture f = new Fixture();
        f.exception(tables, e);
        counts = f.counts;
    }

}