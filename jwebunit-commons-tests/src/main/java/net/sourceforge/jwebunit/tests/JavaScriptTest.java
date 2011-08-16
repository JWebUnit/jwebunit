/**
 * Copyright (c) 2011, JWebUnit team.
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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickButtonWithText;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static net.sourceforge.jwebunit.junit.JWebUnit.setExpectedJavaScriptAlert;
import static net.sourceforge.jwebunit.junit.JWebUnit.setExpectedJavaScriptConfirm;
import static net.sourceforge.jwebunit.junit.JWebUnit.setExpectedJavaScriptPrompt;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author henryju
 */

public class JavaScriptTest  extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/JavaScriptTest");
    }
    
    @Test public void testDocumentWrite() {
        beginAt("DocumentWrite.html");
        assertTextPresent("Hello World");
    }
    
    @Test public void testAlert() {
    	setExpectedJavaScriptAlert("Foo Bar");
        beginAt("Alert.html");
    }
    
    @Test public void testInvalidAlertOnPageLoad() {
    	setExpectedJavaScriptAlert("invalid");
    	try {
    		beginAt("Alert.html");
    		fail();
    	} catch (RuntimeException e) {
    		//OK
    	}        
    }

    @Test public void testMultipleAlerts() {
    	setExpectedJavaScriptAlert(new String[] {"Alert 1", "Alert 2"});
        beginAt("MultipleAlerts.html");
    }

    @Test public void testConfirm() {
    	setExpectedJavaScriptConfirm("Foo Bar", true);
        beginAt("Confirm.html");
        assertLinkPresent("Toto");
        assertLinkNotPresent("Titi");
    }

    @Test public void testPrompt() {
    	setExpectedJavaScriptPrompt("Foo Bar", "toto");
        beginAt("Prompt.html");
        assertTextPresent("Toto");
    }

    @Test public void testPromptCanceled() {
    	setExpectedJavaScriptPrompt("Foo Bar", null);
        beginAt("Prompt.html");
        assertTextPresent("Cancel");
    }
    
    /**
     * Test that the <code>navigator.userAgent</code> is actually available. 
     * 
     * @see bug 1724695
     */
    @Test public void testUserAgent() {
    	beginAt("userAgent.html");
    	assertTextPresent("Mozilla");	// the default browser is a Mozilla browser
    }
    
    /**
     * Test prototype.js integration and make sure that it works
     * in JWebUnit
     * 
     * @see bug 2208784 
     * @author Jevon
     * @throws InterruptedException 
     */
    @Test public void testPrototypeJs() throws InterruptedException {
    	beginAt("prototype.html");
    	clickButtonWithText("do ajax");
    	// we wait a while for the ajax to return
    	Thread.sleep(500);
    	assertTextPresent("hello, world!");
    	assertTextNotPresent("not loaded");
    }
    
}
