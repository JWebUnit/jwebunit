/*
 * User: djoiner
 * Date: Oct 5, 2002
 * Time: 1:11:31 PM
 */
package net.sourceforge.jwebunit.fit;

import fit.Fixture;

import java.io.File;

public class FileResult extends FitResult {
    private Fixture.Counts counts;

    public FileResult(File outname, Fixture.Counts counts) {
        super(outname);
        this.counts = counts;
    }

    public Fixture.Counts getCounts() {
        return counts;
    }

    public String getLinkString() {
        return getOutput().getName();
    }

    public String getDisplayName() {
        int i = getOutput().getName().indexOf(".fit.out.html");
        return getOutput().getName().substring(0, i);
    }

}