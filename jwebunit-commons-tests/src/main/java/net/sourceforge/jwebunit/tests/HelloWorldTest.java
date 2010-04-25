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

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Sanity check for the Jetty setup.
 * 
 * @author Martijn Dashorst
 */
public class HelloWorldTest extends JWebUnitAPITestCase {
	public HelloWorldTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new JettySetup(new TestSuite(HelloWorldTest.class));
	}

	public void setUp() throws Exception {
		super.setUp();
		setBaseUrl(HOST_PATH);
		beginAt("/helloworld.html");
	}

	public void testTitle() {
		assertTitleEquals("Hello, World!");
		assertTitleNotSame("Goodbye, World!");
	}

}