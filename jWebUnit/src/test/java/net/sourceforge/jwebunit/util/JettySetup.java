/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.util;

import java.io.IOException;
import java.net.URL;

import org.mortbay.jetty.Server;
import org.mortbay.util.MultiException;

import junit.extensions.TestSetup;
import junit.framework.Test;

/**
 * Sets up and tears down the Jetty servlet engine before and after the tests in
 * the <code>TestSuite</code> have run.
 * 
 * @author Eelco Hillenius
 */
public class JettySetup extends TestSetup {
    /**
     * The Jetty server we are going to use as test server.
     */
    private Server jettyServer = null;

    /**
     * Constructor.
     * 
     * @param test
     */
    public JettySetup(Test test) {
        super(test);
    }

    /**
     * Starts the Jetty server using the <tt>jetty-test-config.xml</tt> file
     * which is loaded using the classloader used to load Jetty.
     * 
     * @see junit.extensions.TestSetup#setUp()
     */
    public void setUp() {
        try {
            URL jettyConfig = JettySetup.class
                    .getResource("/jetty-test-config.xml");
            if(jettyConfig==null) {
                fail("Unable to locate jetty-test-config.xml on the classpath");
            }
            jettyServer = new Server(jettyConfig);
            jettyServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not start the Jetty server: " + e);
        } catch (MultiException e) {
            e.printStackTrace();
            fail("Could not start the Jetty server: " + e);
        }
    }

    /**
     * Stops the Jetty server.
     * 
     * @see junit.extensions.TestSetup#tearDown()
     */
    public void tearDown() {
        try {
            jettyServer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Jetty server was interrupted: " + e);
        }
    }
}