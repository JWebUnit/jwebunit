/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import net.sourceforge.jwebunit.locator.HtmlAnchorLocatorByText;
import net.sourceforge.jwebunit.locator.HtmlButtonLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlCheckboxInputLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlFileInputLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlFormLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlResetInputLocator;
import net.sourceforge.jwebunit.locator.HtmlSubmitInputLocator;
import net.sourceforge.jwebunit.locator.HtmlSubmitInputLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlTextInputLocatorByName;
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
        setTextField(new HtmlTextInputLocatorByName("color"), "blue");
        click(new HtmlSubmitInputLocatorByName("button"));
        assertTextPresent("Submitted parameters");
        assertTextPresent("Params are: color=blue");
        click(new HtmlAnchorLocatorByText("return"));
        setTextField(new HtmlTextInputLocatorByName("color"), "red");
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Params are: color=red");
    }

    public void testSetTextArea() {
        beginAt("/TextAreaForm.html");
        setTextField(new HtmlTextInputLocatorByName("text"), "sometext");
        click(new HtmlSubmitInputLocatorByName("button"));
        assertTextPresent("Submitted parameters");
        assertTextPresent("Params are: text=sometext");
        click(new HtmlAnchorLocatorByText("return"));
        setTextField(new HtmlTextInputLocatorByName("text"), "anothertext");
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Params are: text=anothertext");
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
        setTextField(new HtmlFileInputLocatorByName("file"), filename);
        click(new HtmlSubmitInputLocatorByName("button"));
        assertTextPresent("Submitted parameters");
        assertTextPresent("file=" + temp.getName() + "{abcdefgh}");
    }

    public void testCheckBoxSelection() {
        beginAt("/SingleNamedButtonForm.html");
        click(new HtmlCheckboxInputLocatorByName("checkBox"));
        setTextField(new HtmlTextInputLocatorByName("color"), "blue");
        click(new HtmlSubmitInputLocator());
        // checkBox contains 2 parameters: one for the hidden input and one for
        // the checkbox
        assertTextPresent("Params are: color=blue checkBox=,on");
    }

    public void testCheckBoxSelectionWithSameFieldName() {
        beginAt("/CheckboxForm.html");
        HtmlCheckboxInputLocatorByName cb1 = new HtmlCheckboxInputLocatorByName("checkBox");
        cb1.addAttribut("value", "1");
        click(cb1);
        HtmlCheckboxInputLocatorByName cb2 = new HtmlCheckboxInputLocatorByName("checkBox");
        cb1.addAttribut("value", "3");
        click(cb2);
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Params are: checkBox=1,3 ");
    }

    public void testCheckBoxDeSelectionWithSameFieldName() {
        beginAt("/CheckboxForm.html");
        HtmlCheckboxInputLocatorByName cb1 = new HtmlCheckboxInputLocatorByName("checkBox");
        cb1.addAttribut("value", "1");
        click(cb1);
        HtmlCheckboxInputLocatorByName cb2 = new HtmlCheckboxInputLocatorByName("checkBox");
        cb1.addAttribut("value", "3");
        click(cb2);
        click(cb2);
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Params are: checkBox=1");
    }

    public void testCheckBoxDeselection() {
        beginAt("/SingleNamedButtonForm.html");
        click(new HtmlCheckboxInputLocatorByName("checkBox"));
        assertCheckboxSelected(new HtmlCheckboxInputLocatorByName("checkBox"));
        setTextField(new HtmlTextInputLocatorByName("color"), "blue");
        click(new HtmlCheckboxInputLocatorByName("checkBox"));
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Params are: color=blue ");
    }

    public void testSingleFormSingleUnnamedButtonSubmission() {
        beginAt("/SingleUnnamedButtonForm.html");
        setTextField(new HtmlTextInputLocatorByName("color"), "blue");
        click(new HtmlSubmitInputLocator());
        assertTextPresent(" color=blue ");
    }

    public void testSingleNamedButtonSubmission() {
        beginAt("/SingleNamedButtonForm.html");
        setTextField(new HtmlTextInputLocatorByName("color"), "red");
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Params are: color=red");
    }

    public void testSingleFormMultipleButtonSubmission() {
        gotoMultiButtonPage();
        click(new HtmlSubmitInputLocatorByName("color"));
        assertTextPresent("Params are: color=red");
        gotoMultiButtonPage();
        HtmlSubmitInputLocatorByName l = new HtmlSubmitInputLocatorByName("color");
        l.addAttribut("value", "blue");
        click(l);
        assertTextPresent("Params are: color=blue");
    }

    public void testBogusParameter() {
        gotoMultiButtonPage();
        try {
            setTextField(new HtmlTextInputLocatorByName("nonexistent"), "anyvalue");
        } catch (AssertionFailedError e) {
            return;
        }
        fail("Expected AssertionFailedError");
    }

    public void testParamSetOnMultiForm() {
        beginAt("/MultiFormPage.html");
        setTextField(new HtmlTextInputLocatorByName("param1"), "anyvalue");
        setWorkingForm(new HtmlFormLocatorByName("form2"));
        setTextField(new HtmlTextInputLocatorByName("param2"), "anyvalue");
        click(new HtmlSubmitInputLocatorByName("button2a"));
        assertTextPresent("param2=anyvalue");
    }

    public void testSetWorkingFormById() {
        beginAt("/MultiFormPage.html");
        setWorkingForm(new HtmlFormLocatorByName("form5"));
    }

    public void testSetWorkingFormWithSameName() {
        beginAt("/MultiFormPage.html");
        setWorkingForm(new HtmlFormLocatorByName("myForm", 0));
        assertElementPresent(new HtmlSubmitInputLocatorByName("myInput1"));
        assertElementNotPresent(new HtmlSubmitInputLocatorByName("myInput2"));
        setWorkingForm(new HtmlFormLocatorByName("myForm", 1));
        assertElementNotPresent(new HtmlSubmitInputLocatorByName("myInput1"));
        assertElementPresent(new HtmlSubmitInputLocatorByName("myInput2"));
    }

    public void testInvalidButton() {
        beginAt("/InvalidActionForm.html");
        try {
            click(new HtmlSubmitInputLocatorByName("button1"));
            fail("Should have failed because the target page does not exist");
        } catch (RuntimeException e) {
            // TODO Have a better way to know if 404 happened
            assertTrue("Should return 404 error", true);
        }
    }

    public void testUnnamedSubmitOnSpecificForm() {
        beginAt("/MultiFormPage.html");
        setTextField(new HtmlTextInputLocatorByName("param4"), "anyvalue");
        click(new HtmlSubmitInputLocator());
        assertTextPresent("param4=anyvalue");
    }

    public void testNamedSubmitOnSpecificForm() {
        beginAt("/MultiFormPage.html");
        setTextField(new HtmlTextInputLocatorByName("param2"), "anyvalue");
        click(new HtmlSubmitInputLocatorByName("button2b"));
        assertTextPresent("param2=anyvalue ");
        assertTextPresent(" button2b=b2b");
    }

    public void testSubmissionReset() {
        beginAt("/MultiFormPage.html");
        setTextField(new HtmlTextInputLocatorByName("param2"), "anyvalue");
        click(new HtmlResetInputLocator());
        click(new HtmlSubmitInputLocatorByName("button2b"));
        assertTextNotPresent("param2=anyvalue ");
        assertTextPresent(" button2b=b2b");
    }

    public void testSelectOption() {
        beginAt("/MultiFormPage.html");
        assertSelectedOptionEquals("select1", "one");
        selectOption("select1", "two");
        assertSelectedOptionEquals("select1", "two");
    }
    
    public void testSelectOptionInAnotherForm() {
        beginAt("/MultiFormPage.html");
        setWorkingForm(new HtmlFormLocatorByName("form6bis"));
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
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Page 2");
        click(new HtmlSubmitInputLocator());
        assertTextPresent("Page 3");
    }

}
