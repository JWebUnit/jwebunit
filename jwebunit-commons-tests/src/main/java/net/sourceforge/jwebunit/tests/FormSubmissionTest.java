/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sourceforge.jwebunit.tests.util.JettySetup;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test form submission related methods of WebTestCase.
 * 
 * If there is more than one submit button on a page, WebTestCase / httpunit
 * require indication of which button to submit with prior to form submission.
 * 
 * @author Jim Weaver
 */
public class FormSubmissionTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(FormSubmissionTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/FormSubmissionTest");
    }

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

    public void testSetTextArea() {
        beginAt("/TextAreaForm.html");
        setTextField("text", "sometext");
        submit("button");
        assertTextPresent("Submitted parameters");
        assertTextPresent("Params are: text=sometext");
        clickLink("return");
        setTextField("text", "anothertext");
        submit();
        assertTextPresent("text=anothertext");
    }

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
        assertTextPresent("file=" + temp.getName() + "{abcdefgh}");
    }
    
    public void testSubmitImageInput() {
        beginAt("/InputImageForm.html");
        setTextField("color", "toto");
        assertSubmitButtonPresent();
        submit();
        assertTextPresent("Submitted parameters");
        assertTextPresent("color=toto");
    }

    public void testSubmitImageInputByName() {
        beginAt("/InputImageForm.html");
        setTextField("color", "toto");
        assertSubmitButtonPresent("image");
        submit("image");
        assertTextPresent("Submitted parameters");
        assertTextPresent("color=toto");
    }

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

    public void testCheckBoxSelectionWithSameFieldName() {
        beginAt("/CheckboxForm.html");
        checkCheckbox("checkBox", "1");
        checkCheckbox("checkBox", "3");
        checkCheckbox("checkBox", "3"); // check for duplicates
        submit();
        assertTextPresent("checkBox=1,3 ");
    }

    public void testCheckBoxDeSelectionWithSameFieldName() {
        beginAt("/CheckboxForm.html");
        checkCheckbox("checkBox", "1");
        checkCheckbox("checkBox", "3");
        uncheckCheckbox("checkBox", "3");
        submit();
        assertTextPresent("checkBox=1");
    }

    public void testCheckBoxDeselection() {
        beginAt("/SingleNamedButtonForm.html");
        checkCheckbox("checkBox"); // Fail with httpunit because of hidden
                                    // field with same name
        assertCheckboxSelected("checkBox");
        setTextField("color", "blue");
        uncheckCheckbox("checkBox");
        submit();
        assertTextPresent("color=blue ");
    }
    
    public void testRadioSelection() {
    	beginAt("/RadioForm.html");
    	clickRadioOption("radio", "1");
    	assertRadioOptionSelected("radio", "1");
    	submit();
    	assertTextPresent("radio=1 ");
    	clickLink("return");
    	System.out.println(getPageSource());
    	clickRadioOption("radio", "2");
    	clickRadioOption("radio", "3");
    	assertRadioOptionNotSelected("radio", "1");
    	assertRadioOptionNotSelected("radio", "2");
    	assertRadioOptionSelected("radio", "3");
    	submit();
    	assertTextPresent("radio=3 ");
    }

    public void testSingleFormSingleUnnamedButtonSubmission() {
        beginAt("/SingleUnnamedButtonForm.html");
        setTextField("color", "blue");
        submit();
        assertTextPresent("color=blue ");
    }

    public void testSingleNamedButtonSubmission() {
        beginAt("/SingleNamedButtonForm.html");
        setTextField("color", "red");
        submit();
        assertTextPresent("color=red");
    }

    public void testSingleFormMultipleButtonSubmission() {
        gotoMultiButtonPage();
        submit("color");
        assertTextPresent("Params are: color=red");
        gotoMultiButtonPage();
        submit("color", "blue");
        assertTextPresent("color=blue");
    }

    public void testBogusParameter() {
        gotoMultiButtonPage();
        try {
            setTextField("nonexistent", "anyvalue");
        } catch (AssertionFailedError e) {
            return;
        }
        fail("Expected AssertionFailedError");
    }

    public void testParamSetOnMultiForm() {
        beginAt("/MultiFormPage.html");
        setTextField("param1", "anyvalue");
        setWorkingForm("form2");
        setTextField("param2", "anyvalue");
        submit("button2a");
        assertTextPresent("param2=anyvalue");
    }

    public void testSetWorkingFormById() {
        beginAt("/MultiFormPage.html");
        setWorkingForm("form5");
    }

    public void testSetWorkingFormWithSameName() {
        beginAt("/MultiFormPage.html");
        setWorkingForm("myForm", 0);
        assertSubmitButtonPresent("myInput1");
        assertSubmitButtonNotPresent("myInput2");
        setWorkingForm("myForm", 1);
        assertSubmitButtonNotPresent("myInput1");
        assertSubmitButtonPresent("myInput2");
    }

    public void testInvalidButton() {
        beginAt("/InvalidActionForm.html");
        try {
            submit("button1");
            fail("Should have failed because the target page does not exist");
        } catch (RuntimeException e) {
            // TODO Have a better way to know if 404 happened
            assertTrue("Should return 404 error", true);
        }
    }

    public void testUnnamedSubmitOnSpecificForm() {
        beginAt("/MultiFormPage.html");
        setTextField("param4", "anyvalue");
        submit();
        assertTextPresent("param4=anyvalue");
    }

    public void testNamedSubmitOnSpecificForm() {
        beginAt("/MultiFormPage.html");
        setTextField("param2", "anyvalue");
        submit("button2b");
        assertTextPresent("param2=anyvalue ");
        assertTextPresent("button2b=b2b");
    }

    public void testSubmissionReset() {
        beginAt("/MultiFormPage.html");
        setTextField("param2", "anyvalue");
        reset();
        submit("button2b");
        assertTextNotPresent("param2=anyvalue ");
        assertTextPresent("button2b=b2b");
    }

    public void testSelectOption() {
        beginAt("/MultiFormPage.html");
        assertSelectedOptionEquals("select1", "one");
        selectOption("select1", "two");
        assertSelectedOptionEquals("select1", "two");
    }

    public void testSelectOptionInAnotherForm() {
        beginAt("/MultiFormPage.html");
        setWorkingForm("form6bis");
        assertSelectedOptionEquals("select1", "four");
        selectOption("select1", "five");
        assertSelectedOptionEquals("select1", "five");
    }

    public void testSelectOptionByValue() {
        beginAt("/MultiFormPage.html");
        assertSelectedOptionValueEquals("select1", "1");
        selectOptionByValue("select1", "2");
        assertSelectedOptionValueEquals("select1", "2");
    }

    private void gotoMultiButtonPage() {
        beginAt("/MultiNamedButtonForm.html");
    }
    
    public void testCachedForm() {
        beginAt("/Submit1.html");
        assertTextPresent("Page 1");
        submit();
        assertTextPresent("Page 2");
        submit();
        assertTextPresent("Page 3");
    }

}
