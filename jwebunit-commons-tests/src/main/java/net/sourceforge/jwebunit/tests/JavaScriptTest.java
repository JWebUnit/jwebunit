/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * @author henryju
 */

public class JavaScriptTest  extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(JavaScriptTest.class);
        return new JettySetup(suite);
    }   
    
    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/JavaScriptTest");
    }
    
    public void testDocumentWrite() {
        beginAt("DocumentWrite.html");
        assertTextPresent("Hello World");
    }

}
