/*
 * User: djoiner
 * Date: Sep 26, 2002
 * Time: 11:36:01 AM
 */
package net.sourceforge.jwebunit.fit;

import junit.framework.TestCase;
import fit.Fixture;

import java.util.StringTokenizer;

public class WebFixtureTest extends TestCase {

    public WebFixtureTest(String s) {
        super(s);
    }

    public void testWebFixture() throws Exception {
        new PseudoWebApp();
        DirectoryRunner testRunner = DirectoryRunner.parseArgs(new String[]
                                                                {"fitplugin\\test\\testInput",
                                                                 "fitplugin\\test\\testOutput"});
        testRunner.run();
        assertEquals("Failures detected.", 0, getCount("wrong"));
        assertEquals("Exceptions detected.", 0, getCount("exceptions"));
    }

    private int getCount(String type) {
        StringTokenizer tokenizer = new StringTokenizer(Fixture.counts(), " ,");
        String prevToken = null;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (prevToken != null && token.equals(type)) {
                return new Integer(prevToken).intValue();
            }
            prevToken = token;
        }
        throw new RuntimeException("Count for type [" + type + "] not found.");
    }

}
