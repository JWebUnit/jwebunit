/*
 * User: DJoiner
 * Date: Oct 8, 2002
 * Time: 11:10:09 AM
 */
package net.sourceforge.jwebunit.fit;

import java.io.File;

public abstract class FitResult {
    private File output;

    FitResult(File output) {
        this.output = output;
    }

    public String counts() {
        return getRight() + " right, " +
                getWrong() + " wrong, " +
                getIgnores() + " ignored, " +
                getExceptions() + " exceptions";
    }

    public abstract String getDisplayName();

    public abstract boolean didFail();

    public String getOutname() {
        return getOutput().getName();
    }

    public File getOutput() {
        return output;
    }

    public abstract int getRight();

    public abstract int getWrong();

    public abstract int getIgnores();

    public abstract int getExceptions();

}
