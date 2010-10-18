/**
 * Copyright (c) 2010, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */


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