/*
 * User: djoiner
 * Date: Sep 26, 2002
 * Time: 11:36:01 AM
 */
package net.sourceforge.jwebunit.fit;

import junit.framework.TestCase;

public class WebFixtureTest extends TestCase {

    public WebFixtureTest(String s) {
        super(s);
    }

    public void testWebFixture() throws Exception {
        new PseudoWebApp();
        DirectoryRunner testRunner = 
        	DirectoryRunner.parseArgs(new String[]
        		{"fitplugin\\test\\testInput",
				 "fitplugin\\test\\testOutput"});
        testRunner.run();
		testRunner.getResultWriter().write();
		assertEquals("Failures detected.", 0, 
			testRunner.getResultWriter().getCounts().wrong);
        assertEquals("Exceptions detected.", 0, 
        	testRunner.getResultWriter().getCounts().exceptions);
    }

}