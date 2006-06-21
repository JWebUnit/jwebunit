/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * User: djoiner
 * Date: Nov 22, 2002
 * Time: 10:24:54 AM
 */

public class JavaScriptEventsTest  extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(JavaScriptEventsTest.class);
        return new JettySetup(suite);
    }   
    
    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/JavaScriptEventsTest");
    }
    
    public void testFormOnSubmit() {
        beginAt("FormOnSubmit.html");
        submit();
        gotoWindow("child");
        assertTextPresent("on=submit");
    }

    public void testFormOnReset() {
        beginAt("FormOnSubmit.html");
        reset();
        gotoWindow("child");
        assertTextPresent("on=reset");
    }

    public void testButtonOnClick() {
        beginAt("FormOnSubmit.html");
        assertButtonPresent("b1");
        clickButton("b1");
        gotoWindow("child");
        assertTextPresent("on=click");
    }


    public void testJavaScriptInFile() {
        beginAt("index.html");
        assertTitleEquals("Startpage");
        clickButton("next");
        assertTextPresent("Here is the text we expect");
        
        beginAt("index.html");
        assertTitleEquals("Startpage");
        submit();
        assertTextPresent("Here is the text we expect");
    }
 
    public void testLinkAssertsWorkJavascriptDisabled() {
        setScriptingEnabled(false);
        beginAt("index.html");
        clickLink("linkNext");
        assertTitleEquals("Startpage");
        assertTextNotPresent("Here is the text we expect");

        beginAt("index.html");
        clickButton("next");
        assertTitleEquals("Startpage");
        assertTextNotPresent("Here is the text we expect");
        setScriptingEnabled(true);
        
        // and test that javascript is enabled for the next begin
        beginAt("index.html");
        clickLink("linkNext");
        //dumpHtml();
        assertTitleEquals("Next page");
    }

	public void testLinkClickSetsWindowLocation()
	{
		beginAt("index.html");
		assertTitleEquals("Startpage");
		clickLinkWithText("Next");
		assertTitleEquals("Next page");
	}
    
	public void testOnChangeSetsWindowLocation() throws Exception {
		beginAt("onchange.html");
		assertTitleEquals("The Title");
		selectOption("testSelect", "Value2");
		assertTitleEquals("Submitted parameters");
        assertTextPresent("testSelect=V2");
	}

    public void testGreenLink() {
        beginAt("index.html");
        assertFormElementEquals("color", "blue");
        clickLink("SetColorGreen");
        assertFormElementEquals("color", "green");
    } 
    
    public void testFormOnSubmitSetTarget() {
        beginAt("FormOnSubmitSetTarget.html");
        setWorkingForm("formID");
        submit("go");
    }
}
