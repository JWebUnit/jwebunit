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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertButtonPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextFieldEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickButton;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithText;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoWindow;
import static net.sourceforge.jwebunit.junit.JWebUnit.reset;
import static net.sourceforge.jwebunit.junit.JWebUnit.selectOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static net.sourceforge.jwebunit.junit.JWebUnit.setScriptingEnabled;
import static net.sourceforge.jwebunit.junit.JWebUnit.setWorkingForm;
import static net.sourceforge.jwebunit.junit.JWebUnit.submit;

import org.junit.Test;

/**
 * User: djoiner
 * Date: Nov 22, 2002
 * Time: 10:24:54 AM
 */

public class JavaScriptEventsTest  extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/JavaScriptEventsTest");
    }
    
    @Test public void testFormOnSubmit() {
        beginAt("FormOnSubmit.html");
        submit();
        gotoWindow("child");
        assertTextPresent("on=submit");
    }

    @Test public void testFormOnReset() {
        beginAt("FormOnSubmit.html");
        reset();
        gotoWindow("child");
        assertTextPresent("on=reset");
    }

    @Test public void testButtonOnClick() {
        beginAt("FormOnSubmit.html");
        assertButtonPresent("b1");
        clickButton("b1");
        gotoWindow("child");
        assertTextPresent("on=click");
    }


    @Test public void testJavaScriptInFile() {
        beginAt("index.html");
        assertTitleEquals("Startpage");
        clickButton("next");
        assertTextPresent("Here is the text we expect");
    }
 
    @Test public void testLinkAssertsWorkJavascriptDisabled() {
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

	@Test public void testLinkClickSetsWindowLocation()
	{
		beginAt("index.html");
		assertTitleEquals("Startpage");
		clickLinkWithText("Next");
		assertTitleEquals("Next page");
	}
    
	@Test public void testOnChangeSetsWindowLocation() throws Exception {
		beginAt("onchange.html");
		assertTitleEquals("The Title");
		selectOption("testSelect", "Value2");
		assertTitleEquals("Submitted parameters");
        assertTextPresent("testSelect=V2");
	}

    @Test public void testGreenLink() {
        beginAt("index.html");
        assertTextFieldEquals("color", "blue");
        clickLink("SetColorGreen");
        assertTextFieldEquals("color", "green");
    } 
    
    @Test public void testFormOnSubmitSetTarget() {
        beginAt("FormOnSubmitSetTarget.html");
        setWorkingForm("formID");
        submit("go");
    }
}
