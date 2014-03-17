/**
 * Copyright (c) 2002-2014, JWebUnit team.
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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.setCustomTester;

import org.junit.Before;

import net.sourceforge.jwebunit.junit.WebTester;

import org.junit.After;
import org.junit.Test;


/**
 * Test the new constructor methods for custom web testers
 * 
 * @author jmwright
 *
 */
public class CustomTesterTest extends JWebUnitAPITestCase {
	
	/**
	 * A custom tester to allow us to make sure it is called.
	 * 
	 * @author jmwright
	 *
	 */
	static class MyWebTester extends WebTester {

		/** 
		 * We extend the normal method to not fail for our special case.
		 * 
		 * @see net.sourceforge.jwebunit.junit.WebTester#assertTitleEquals(java.lang.String)
		 */
		@Override
		public void assertTitleEquals(String title) {
			super.assertTitleEquals(title + " [custom]");
		}
		
	}
	
	@Before
    public void setUp() throws Exception {
        setCustomTester(new MyWebTester());
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/CustomTesterTest");
    }

    @Test
    public void testCustomTester() throws Throwable {
        beginAt("/test.html");
        assertTitleEquals("test");		// this will normally fail for a non-custom class
    }
    
    @After
    public void cleanup() {
        setCustomTester(null);
    }
}
