/*
 * User: djoiner
 * Date: Oct 5, 2002
 * Time: 1:11:31 PM
 */
package net.sourceforge.jwebunit.fit;

public class FileResult {
    private String filename;
    private String outname;
    private int right;
    private int wrong;
    private int ignores;
    private int exceptions;

    public FileResult(String filename, String outname, int right, int wrong, int ignores, int exceptions) {
        this.filename = filename;
        this.outname = outname;
        this.right = right;
        this.wrong = wrong;
        this.ignores = ignores;
        this.exceptions = exceptions;
    }

    public String counts() {
        return right + " right, " +
                wrong + " wrong, " +
                ignores + " ignored, " +
                exceptions + " exceptions";
    }

    public void dumpCounts() {
        System.out.println(getFilename() + ": " + counts());
    }

    public String getFilename() {
        return filename;
    }

    public String getOutname() {
        return outname;
    }

    public String getTestName() {
        int i = filename.indexOf(".fit.in.html");
        return filename.substring(0, i);
    }

    public boolean didFail() {
        return wrong > 1 || exceptions > 1;
    }
}