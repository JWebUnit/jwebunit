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

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import net.sourceforge.jwebunit.junit.WebTester;

import org.junit.Test;

public class ButtonAssertionsTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/ButtonAssertionsTest");
    }

    @Test
    public void testAssertButtonwithOneFormfound() {
        beginAt("/pageWithOneForm.html");
        assertButtonPresent("button1");
        assertButtonPresent("buttonOutside");
        setWorkingForm("form1");
        assertButtonPresent("button1");
        assertButtonPresent("buttonOutside");
        assertButtonPresent("button1");
        assertButtonPresent("buttonOutside");
        
        // test for issue 1874971 
        assertButtonPresentWithText("Input button");
    }

    @Test
    public void testAssertButtonwithTowFormsfound() {
        beginAt("/pageWithTwoForms.html");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
        setWorkingForm("form1");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
        setWorkingForm("form2");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
        assertButtonPresent("button1");
        assertButtonPresent("button2");
        assertButtonPresent("buttonOutside");
    }
    
    @Test
    public void testAssertButtonWithText() {
        beginAt("/pageWithTwoForms.html");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
        setWorkingForm("form1");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
        assertButtonPresentWithText("the submit btn");
        assertButtonPresentWithText("the reset btn");
        assertButtonPresentWithText("the btn btn");
        setWorkingForm("form2");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
        assertButtonPresentWithText("Testbutton");
        assertButtonPresentWithText("Testbutton2");
        assertButtonPresentWithText("Outside");
    }
    
    /**
     * As per the semantics of {@link WebTester#assertButtonPresentWithText(String)}, 
     * buttons that are not currently displayed are still "present".
     */
    @Test
    public void testHiddenButtonsAreStillFound() {
    	beginAt("/pageWithOneForm.html");
    	assertButtonPresentWithText("Hidden Input");
    	assertButtonPresentWithText("Hidden Button");
    }

}
