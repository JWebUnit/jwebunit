/*
 * Created on Jul 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.jwebunit;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.util.JettySetup;

/**
 * Sanity check for the Jetty setup.
 * 
 * @author Martijn Dashorst
 */
public class HelloWorldTest extends WebTestCase {
    public HelloWorldTest(String name) {
        super(name);
    }

    public void setUp() {
        getTestContext().setBaseUrl("http://localhost:8081/jwebunit");
        beginAt("/helloworld.html");
    }

    public void testTitle() {
        assertTitleEquals("Hello, World!");
    }

    public static Test suite() {
        return new JettySetup(new TestSuite(HelloWorldTest.class));
    }
}