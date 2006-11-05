/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
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
        //FIXME Fails with HtmlUnit
        //assertTextPresent("Hello World");
    }
    
    public void testAlert() {
    	setExpectedJavaScriptAlert("Foo Bar");
        beginAt("Alert.html");
    }
    
    public void testInvalidAlertOnPageLoad() {
    	setExpectedJavaScriptAlert("invalid");
    	try {
    		beginAt("Alert.html");
    		fail();
    	} catch (RuntimeException e) {
    		//OK
    	}        
    }

    public void testMultipleAlerts() {
    	setExpectedJavaScriptAlert(new String[] {"Alert 1", "Alert 2"});
        beginAt("MultipleAlerts.html");
    }

    public void testConfirm() {
    	setExpectedJavaScriptConfirm("Foo Bar", true);
        beginAt("Confirm.html");
        assertLinkPresent("Toto");
        assertLinkNotPresent("Titi");
    }

    public void testPrompt() {
    	setExpectedJavaScriptPrompt("Foo Bar", "toto");
        beginAt("Prompt.html");
        assertTextPresent("Toto");
    }

    public void testPromptCanceled() {
    	setExpectedJavaScriptPrompt("Foo Bar", null);
        beginAt("Prompt.html");
        assertTextPresent("Cancel");
    }
}
