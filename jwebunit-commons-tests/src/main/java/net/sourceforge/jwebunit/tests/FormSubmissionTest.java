package net.sourceforge.jwebunit.tests;

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

	public void testSetInputField() {
		beginAt("/SingleNamedButtonForm.html");
		setTextField("color", "blue");
		submit("button");
        assertTextPresent("Submitted parameters");
        //dumpResponse(System.out);
		assertTextPresent("Params are: color=blue");
		clickLink("return");
        setTextField("color", "red");
		submit();
		assertTextPresent("Params are: color=red");
	}

	public void testCheckBoxSelection() {
		beginAt("/SingleNamedButtonForm.html");
		checkCheckbox("checkBox");
        setTextField("color", "blue");
		submit();
		assertTextPresent("Params are: color=blue checkBox=on");
	}

	public void testCheckBoxSelectionWithSameFieldName() {
		beginAt("/CheckboxForm.html");
		checkCheckbox("checkBox", "1");
		checkCheckbox("checkBox", "3");
		checkCheckbox("checkBox", "3"); // check for duplicates
		submit();
		assertTextPresent("Params are: checkBox=1,3 ");
	}

	public void testCheckBoxDeSelectionWithSameFieldName() {
		beginAt("/CheckboxForm.html");
		checkCheckbox("checkBox", "1");
		checkCheckbox("checkBox", "3");
		uncheckCheckbox("checkBox", "3");
		submit();
		assertTextPresent("Params are: checkBox=1");
	}

	public void testCheckBoxDeselection() {
		beginAt("/SingleNamedButtonForm.html");
		checkCheckbox("checkBox");
        assertFormElementEquals("checkBox", "on");
        setTextField("color", "blue");
		uncheckCheckbox("checkBox");
		submit();
		assertTextPresent("Params are: color=blue ");
	}

	public void testSingleFormSingleUnnamedButtonSubmission() {
		beginAt("/SingleUnnamedButtonForm.html");
        setTextField("color", "blue");
		submit();
		assertTextPresent(" color=blue ");
	}

	public void testSingleNamedButtonSubmission() {
		beginAt("/SingleNamedButtonForm.html");
        setTextField("color", "red");
		submit();
		assertTextPresent("Params are: color=red");
	}

	public void testSingleFormMultipleButtonSubmission() {
		gotoMultiButtonPage();
		submit("color");
		assertTextPresent("Params are: color=red");
		gotoMultiButtonPage();
		submit("color", "blue");
		assertTextPresent("Params are: color=blue");
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

	public void testInvalidButton() {
        beginAt("/InvalidActionForm.html");
		try {
			submit("button1");
            fail("Should have failed because the target page does not exist");
		} catch (RuntimeException e) {
            //TODO Have a better way to know if 404 happened
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
		assertTextPresent(" param2=anyvalue ");
        assertTextPresent(" button2b=b2b ");
	}

	public void testSubmissionReset() {
		beginAt("/MultiFormPage.html");
        setTextField("param2", "anyvalue");
        reset();
		submit("button2b");
        assertTextNotPresent(" param2=anyvalue ");
        assertTextPresent(" button2b=b2b ");
	}

	public void testSelectOption() {
		beginAt("/MultiFormPage.html");
		assertSelectedOptionEquals("select1", "one");
		selectOption("select1", "two");
		assertSelectedOptionEquals("select1", "two");
	}

	public void testSimpleLabeledForm() {
		beginAt("/QueryFormSimple.html");
		setFormElementWithLabel("First", "oneValue");
		setFormElementWithLabel("Second", "anotherValue");
		submit();
		assertTextPresent(" param1=oneValue ");
        assertTextPresent(" param2=anotherValue ");
	}

	public void testTrickyLabeledForm() {
		beginAt("/QueryFormTricky.html");
		setFormElementWithLabel("Trick", "oneValue");
		setFormElementWithLabel("Treat", "anotherValue");
		submit();
		assertTextPresent(" param3=oneValue ");
        assertTextPresent(" param4=anotherValue ");
	}

	private void gotoMultiButtonPage() {
		beginAt("/MultiNamedButtonForm.html");
	}

}