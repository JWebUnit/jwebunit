/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.util;

import java.net.URL;

import net.sourceforge.jwebunit.JWebUnitAPITestCase;
import net.sourceforge.jwebunit.TestingEngineRegistry;

import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;

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
    
    private String key;

    /**
     * Constructor.
     * 
     * @param test
     * @param key The name of the dialog that will be used
     */
    public JettySetup(Test test, String key) {
        super(test);
        this.key=key;
    }

    /**
     * Constructor.
     * 
     * @param test
     */
    public JettySetup(Test test) {
        super(test);
        this.key=TestingEngineRegistry.TESTING_ENGINE_HTTPUNIT;
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
            jettyServer = new Server();
            XmlConfiguration xmlConfiguration = new XmlConfiguration(jettyConfig);
            xmlConfiguration.configure(jettyServer);
            jettyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not start the Jetty server: " + e);
        }
        JWebUnitAPITestCase.setDialogKey(key);
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
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not stop the Jetty server: " + e);
        }
    }
}