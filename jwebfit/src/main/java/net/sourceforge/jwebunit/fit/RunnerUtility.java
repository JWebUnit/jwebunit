package net.sourceforge.jwebunit.fit;

/**
 * Used to tell the runners which kind of files they should expect for input.
 * User: djoiner
 * Date: Mar 17, 2003
 * Time: 11:41:47 PM
 */
public class RunnerUtility {
    static boolean overrideSystemPropertyAndUseWikiParser = false;
    /**
     * @return true if there is a system property named 'wiki' with any value.
     */
    public static boolean useWikiParser() {
        return overrideSystemPropertyAndUseWikiParser 
            || System.getProperty("wiki") != null;
    }

}
