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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;

import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;

/**
 * Test form submission related methods of WebTestCase.
 * 
 * If there is more than one submit button on a page, WebTestCase / httpunit
 * require indication of which button to submit with prior to form submission.
 * 
 * @author Jim Weaver
 */
public class FormSubmissionTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/FormSubmissionTest");
    }

    @Test
    public void testSetTextField() {
        beginAt("/SingleNamedButtonForm.html");
        setTextField("color", "blue");
        submit("button");
        assertTextPresent("Submitted parameters");
        assertTextPresent("color=blue");
        clickLink("return");
        setTextField("color", "red");
        submit();
        assertTextPresent("color=red");
    }

    @Test
    public void testSetTextArea() {
        beginAt("/TextAreaForm.html");
        setTextField("text", "sometext");
        submit("button");
        assertTextPresent("Submitted parameters");
        assertTextPresent("Params are:" + System.getProperty("line.separator") + "text=sometext");
        clickLink("return");
        setTextField("text", "anothertext");
        submit();
        assertTextPresent("text=anothertext");
    }

    @Test
    public void testSetFileField() {
        beginAt("/InputFileForm.html");
        File temp = null;
        try {
            // Create temp file.
            temp = File.createTempFile("data", ".txt");
            // Delete temp file when program exits.
            temp.deleteOnExit();
            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write("abcdefgh");
            out.close();
        } catch (IOException e) {
            fail(e.toString());
        }
        String filename = temp.getAbsolutePath();
        setTextField("file", filename);
        submit("button");
        assertTextPresent("Submitted parameters");
        //The following depend on the browser: IE send full path (i.e. temp.getAbsolutePath()) but FF send only file name.
        assertTextPresent("file=" + temp.getName() + "{abcdefgh}");
    }
    
    @Test
    public void testSubmitImageInput() {
        beginAt("/InputImageForm.html");
        setTextField("color", "toto");
        assertSubmitButtonPresent();
        submit();
        assertTextPresent("Submitted parameters");
        assertTextPresent("color=toto");
    }

    @Test
    public void testSubmitImageInputByName() {
        beginAt("/InputImageForm.html");
        setTextField("color", "toto");
        assertSubmitButtonPresent("image");
        submit("image");
        assertTextPresent("Submitted parameters");
        assertTextPresent("color=toto");
    }

    @Test
    public void testCheckBoxSelection() {
        beginAt("/SingleNamedButtonForm.html");
        checkCheckbox("checkBox");
        setTextField("color", "blue");
        submit();
        assertTextPresent("color=blue");
        // checkBox contains 2 parameters: one for the hidden input and one for
        // the checkbox
        assertTextPresent("checkBox=,on");
    }

    @Test
    public void testCheckBoxSelectionWithSameFieldName() {
        beginAt("/CheckboxForm.html");
        checkCheckbox("checkBox", "1");
        checkCheckbox("checkBox", "3");
        checkCheckbox("checkBox", "3"); // check for duplicates
        submit();
        assertTextPresent("checkBox=1,3" + System.getProperty("line.separator"));
    }

    @Test
    public void testCheckBoxDeSelectionWithSameFieldName() {
        beginAt("/CheckboxForm.html");
        checkCheckbox("checkBox", "1");
        checkCheckbox("checkBox", "3");
        uncheckCheckbox("checkBox", "3");
        submit();
        assertTextPresent("checkBox=1");
    }

    @Test
    public void testCheckBoxDeselection() {
        beginAt("/SingleNamedButtonForm.html");
        checkCheckbox("checkBox"); // Fail with httpunit because of hidden
                                    // field with same name
        assertCheckboxSelected("checkBox");
        setTextField("color", "blue");
        uncheckCheckbox("checkBox");
        submit();
        assertTextPresent("color=blue" + System.getProperty("line.separator"));
    }
    
    @Test
    public void testRadioSelection() {
    	beginAt("/RadioForm.html");
    	clickRadioOption("radio", "1");
    	assertRadioOptionSelected("radio", "1");
    	submit();
    	assertTextPresent("radio=1" + System.getProperty("line.separator"));
    	clickLink("return");
    	clickRadioOption("radio", "2");
    	clickRadioOption("radio", "3");
    	assertRadioOptionNotSelected("radio", "1");
    	assertRadioOptionNotSelected("radio", "2");
    	assertRadioOptionSelected("radio", "3");
    	submit();
    	assertTextPresent("radio=3" + System.getProperty("line.separator"));
    }

    @Test
    public void testSingleFormSingleUnnamedButtonSubmission() {
        beginAt("/SingleUnnamedButtonForm.html");
        setTextField("color", "blue");
        submit();
        assertTextPresent("color=blue" + System.getProperty("line.separator"));
    }

    @Test
    public void testSingleNamedButtonSubmission() {
        beginAt("/SingleNamedButtonForm.html");
        setTextField("color", "red");
        submit();
        assertTextPresent("color=red");
    }

    @Test
    public void testSingleFormMultipleButtonSubmission() {
        gotoMultiButtonPage();
        submit("color");
        assertTextPresent("Params are:" + System.getProperty("line.separator") + "color=red");
        gotoMultiButtonPage();
        submit("color", "blue");
        assertTextPresent("color=blue");
    }

    @Test
    public void testBogusParameter() {
        gotoMultiButtonPage();
        try {
            setTextField("nonexistent", "anyvalue");
            fail("Expected AssertionError");
        } catch (AssertionError e) {
            //OK it was expected
        }
    }

    @Test
    public void testParamSetOnMultiForm() {
        beginAt("/MultiFormPage.html");
        setTextField("param1", "anyvalue");
        setWorkingForm("form2");
        setTextField("param2", "anyvalue");
        submit("button2a");
        assertTextPresent("param2=anyvalue");
    }

    @Test
    public void testTextFieldSetOnMultiFormWithSameName() {
        beginAt("/MultiFormPage.html");
        setWorkingForm("form2");
        setTextField("param2", "foo");
        setTextField("email", "anyvalue");
        submit();
        assertTextPresent("email=anyvalue");
        assertTextPresent("param2=foo");
        closeBrowser();
        beginAt("/MultiFormPage.html");
        setWorkingForm("form3");
        setTextField("param3", "foo");
        setTextField("email", "anyvalue");
        submit();
        assertTextPresent("param3=foo");
        assertTextPresent("email=anyvalue");
    }

    @Test
    public void testSetWorkingFormById() {
        beginAt("/MultiFormPage.html");
        setWorkingForm("form5");
    }

    @Test
    public void testSetWorkingFormWithSameName() {
        beginAt("/MultiFormPage.html");
        setWorkingForm("myForm", 0);
        assertSubmitButtonPresent("myInput1");
        assertSubmitButtonNotPresent("myInput2");
        setWorkingForm("myForm", 1);
        assertSubmitButtonNotPresent("myInput1");
        assertSubmitButtonPresent("myInput2");
    }

    @Test
    public void testInvalidButton() {
        beginAt("/InvalidActionForm.html");
        try {
        	submit("button1");
        	fail("A TestingEngineResponseException was expected.");
        } catch (TestingEngineResponseException e) {
        	assertEquals(404, e.getHttpStatusCode());
        }
    }

    @Test
    public void testUnnamedSubmitOnSpecificForm() {
        beginAt("/MultiFormPage.html");
        setTextField("param4", "anyvalue");
        submit();
        assertTextPresent("param4=anyvalue");
    }

    @Test
    public void testNamedSubmitOnSpecificForm() {
        beginAt("/MultiFormPage.html");
        setTextField("param2", "anyvalue");
        submit("button2b");
        assertTextPresent("param2=anyvalue" + System.getProperty("line.separator"));
        assertTextPresent("button2b=b2b");
    }

    @Test
    public void testSubmissionReset() {
        beginAt("/MultiFormPage.html");
        setTextField("param2", "anyvalue");
        reset();
        submit("button2b");
        assertTextNotPresent("param2=anyvalue\n");
        assertTextPresent("button2b=b2b");
    }

    @Test
    public void testSelectOption() {
        beginAt("/MultiFormPage.html");
        assertSelectedOptionEquals("select1", "one");
        selectOption("select1", "two");
        assertSelectedOptionEquals("select1", "two");
    }

    @Test
    public void testSelectOptionInAnotherForm() {
        beginAt("/MultiFormPage.html");
        setWorkingForm("form6bis");
        assertSelectedOptionEquals("select1", "four");
        selectOption("select1", "five");
        assertSelectedOptionEquals("select1", "five");
    }

    @Test
    public void testSelectOptionByValue() {
        beginAt("/MultiFormPage.html");
        assertSelectedOptionValueEquals("select1", "1");
        selectOptionByValue("select1", "2");
        assertSelectedOptionValueEquals("select1", "2");
    }

    private void gotoMultiButtonPage() {
        beginAt("/MultiNamedButtonForm.html");
    }
    
    @Test
    public void testCachedForm() {
        beginAt("/Submit1.html");
        assertTextPresent("Page 1");
        submit();
        assertTextPresent("Page 2");
        submit();
        assertTextPresent("Page 3");
    }
    
    /**
     * Submit input
     */
    @Test
    public void testClickButtonWithText1() {
        beginAt("/SingleNamedButtonForm.html");
        setTextField("color", "blue");
        clickButtonWithText("click me");
        assertTextPresent("Submitted parameters");
        assertTextPresent("color=blue");
    }
    
    @Test
    public void testSetHiddenField() {
        beginAt("/SingleNamedButtonForm.html");
        assertHiddenFieldPresent("hidden", "foo");
        setHiddenField("hidden", "bar");
        submit();
        assertTextPresent("hidden=bar");
    }


}
