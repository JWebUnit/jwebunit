/*
 * User: djoiner
 * Date: Oct 5, 2002
 * Time: 1:11:31 PM
 */
package net.sourceforge.jwebunit.fit;

import java.io.File;

public class FileResult extends FitResult  {
    private int right;
    private int wrong;
    private int ignores;
    private int exceptions;

    public FileResult(File outname, int right, int wrong, int ignores, int exceptions) {
        super(outname);
        this.right = right;
        this.wrong = wrong;
        this.ignores = ignores;
        this.exceptions = exceptions;
    }

    public void dumpCounts() {
        System.out.println(getDisplayName() + ": " + counts());
    }

    public String getDisplayName() {
        int i = getOutput().getName().indexOf(".fit.out.html");
        return getOutput().getName().substring(0, i);
    }

    public boolean didFail() {
        return getWrong() > 0 || getExceptions() > 0;
    }

    public int getRight() {
        return right;
    }

    public int getWrong() {
        return wrong;
    }

    public int getIgnores() {
        return ignores;
    }

    public int getExceptions() {
        return exceptions;
    }
}