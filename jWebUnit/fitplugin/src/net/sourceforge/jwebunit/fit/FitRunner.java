/*
 * User: DJoiner
 * Date: Oct 8, 2002
 * Time: 2:54:11 PM
 */
package net.sourceforge.jwebunit.fit;
public abstract class FitRunner {
    protected FitResult result;

    public abstract void run ();

    public FitResult getResult() {
        return result;
    }
}
