/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import com.clarkware.junitperf.ConstantTimer;
import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TestFactory;
import com.clarkware.junitperf.Timer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test junit perf integration.
 * 
 * @author Julien Henry
 */
public class JUnitPerfTest extends TestCase {

    public static TestSuite suite() {

        // create a timed test that verifies that the called function
        // is completed in the specified time
        TestSuite suite = new TestSuite();
        
        int users = 5;
        Timer timer = new ConstantTimer(10);
        Test factory = new TestFactory(NavigationTest.class);
        Test loadTest = new LoadTest(factory, users, timer);
        
        suite.addTest(loadTest);

        return suite;
    }

}