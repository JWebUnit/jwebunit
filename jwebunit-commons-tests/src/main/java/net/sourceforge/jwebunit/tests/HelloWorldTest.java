/*
 * Created on Jul 12, 2004
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
		getTestContext().setBaseUrl(HOST_PATH);
		beginAt("/helloworld.html");
	}

	public void testTitle() {
		assertTitleEquals("Hello, World!");
	}

}