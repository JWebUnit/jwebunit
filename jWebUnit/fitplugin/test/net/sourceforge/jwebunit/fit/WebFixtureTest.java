/*
 * User: djoiner
 * Date: Sep 26, 2002
 * Time: 11:36:01 AM
 */
package net.sourceforge.jwebunit.fit;

import junit.framework.TestCase;

public class WebFixtureTest extends TestCase {

    public static final int MINIMUM_TESTS = 50;
    public static final String TEST_ROOT = "fitplugin/test/";
    
    public WebFixtureTest(String s) {
        super(s);
    }

    public void testWebFixture() throws Exception {
        new PseudoWebApp();
        // avoid the need of the system property, always use .fit files for input
        RunnerUtility.overrideSystemPropertyAndUseWikiParser = true;
        // run the tests
        DirectoryRunner testRunner = 
            DirectoryRunner.parseArgs(new String[]
                {TEST_ROOT + "testInput",
                 TEST_ROOT + "testOutput"});
        testRunner.run();
		testRunner.getResultWriter().write();
        // sanity check
        assertTrue("Should find at least " + MINIMUM_TESTS + " tests",
                0 < testRunner.getResultWriter().getTotal());
        // report failures to JUnit
        String resultsUrl = TEST_ROOT + "testOutput/index.html";
		assertEquals("Failures detected. Check " + resultsUrl + ".", 0, 
			testRunner.getResultWriter().getCounts().wrong);
        assertEquals("Exceptions detected. Check " + resultsUrl + ".", 0, 
        	testRunner.getResultWriter().getCounts().exceptions);
    }

}