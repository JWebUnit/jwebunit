package net.sourceforge.jwebunit.fit;

/**
 * User: djoiner
 * Date: Mar 17, 2003
 * Time: 11:41:47 PM
 */
public class RunnerUtility {
    public static boolean useWikiParser() {
        return System.getProperty("wiki") != null;
    }

}
