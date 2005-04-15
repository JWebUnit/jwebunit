package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.util.JettySetup;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.meterware.httpunit.WebResponse;

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
		beginAt("/SingleNamedButtomForm.jsp");
		setFormElement("color", "blue");
		submit("button");
		assertTextPresent("Parms are: color=blue");
		beginAt("/QueryForm.html");
		setFormElement("color", "red");
		submit();
		assertTextPresent("Parms are: color=red");
	}

	public void testCheckBoxSelection() {
		beginAt("/SingleNamedButtonForm.html");
		checkCheckbox("checkBox");
		setFormElement("color", "blue");
		submit();
		assertTextPresent("Parms are: color=blue&checkBox=on");
	}

	public void testCheckBoxSelectionWithSameFieldName() {
		beginAt("/CheckboxForm.html");
		checkCheckbox("checkBox", "1");
		checkCheckbox("checkBox", "3");
		checkCheckbox("checkBox", "3"); // check for duplicates
		submit();
		assertTextPresent("Parms are: checkBox=1&checkBox=3");
	}

	public void testCheckBoxDeSelectionWithSameFieldName() {
		beginAt("/CheckboxForm.html");
		checkCheckbox("checkBox", "1");
		checkCheckbox("checkBox", "3");
		uncheckCheckbox("checkBox", "3");
		submit();
		assertTextPresent("Parms are: checkBox=1");
	}

	public void testCheckBoxDeselection() {
		beginAt("/SingleNamedButtonForm.html");
		checkCheckbox("checkBox");
		setFormElement("color", "blue");
		uncheckCheckbox("checkBox");
		submit();
		assertTextPresent("Parms are: color=blue");
	}

	public void testSingleFormSingleUnnamedButtonSubmission() {
		beginAt("/SingleUnnamedButtonForm.html");
		submit();
		assertTextPresent("Parms are: color=blue");
	}

	public void testSingleNamedButtonSubmission() {
		beginAt("/SingleNamedButtonForm.html");
		setFormElement("color", "red");
		submit();
		assertTextPresent("Parms are: color=red");
	}

	public void testSingleFormMultipleButtonSubmission() {
		gotoMultiButtonPage();
		submit("color");
		assertTextPresent("Parms are: color=red");
		gotoMultiButtonPage();
		submit("color", "blue");
		assertTextPresent("Parms are: color=blue");
	}

	public void testBogusParameter() {
		gotoMultiButtonPage();
		try {
			setFormElement("nonexistent", "anyvalue");
		} catch (AssertionFailedError e) {
			return;
		}
		fail("Expected AssertionFailedError");
	}

	public void testParamSetOnMultiForm() {
		beginAt("/MultiFormPage.html");
		setFormElement("param1", "anyvalue");
		setWorkingForm("form2");
		setFormElement("param2", "anyvalue");
		submit("button2a");
		assertTextPresent("param2=anyvalue");
	}

	public void testSetWorkingFormById() {
		beginAt("/MultiFormPage.html");
		setWorkingForm("form5");
	}

	public void testInvalidButton() {
		gotoMultiButtonPage();
		try {
			submit("button1");
		} catch (RuntimeException e) {
			assertTrue(e.getMessage(), e.getMessage().indexOf(
					"com.meterware.httpunit.HttpNotFoundException") != -1);
			return;
		}
		fail("Should have failed");
	}

	public void testUnnamedSubmitOnSpecificForm() {
		beginAt("/MultiFormPage.html");
		setFormElement("param4", "anyvalue");
		submit();
		assertTextPresent("param4=anyvalue");
	}

	public void testNamedSubmitOnSpecificForm() {
		beginAt("/MultiFormPage.html");
		setFormElement("param2", "anyvalue");
		submit("button2b");
		assertTextPresent("param2=anyvalue&button2b=b2b");
	}

	public void testSubmissionReset() {
		beginAt("/MultiFormPage.html");
		setFormElement("param2", "anyvalue");
		WebResponse oldResp = getDialog().getResponse();
		submit("button2b");
		assertFalse(getDialog().hasForm());
		assertTrue(getDialog().getResponse() != oldResp);
	}

	public void testSelectOption() {
		beginAt("/MultiFormPage.html");
		assertOptionEquals("select1", "one");
		selectOption("select1", "two");
		assertOptionEquals("select1", "two");
		assertFormElementEquals("select1", "2");
	}

	public void testSimpleLabeledForm() {
		beginAt("/QueryFormSimple.html");
		setFormElementWithLabel("First", "oneValue");
		setFormElementWithLabel("Second", "anotherValue");
		submit();
		assertTextPresent("param1=oneValue&param2=anotherValue");
	}

	public void testTrickyLabeledForm() {
		beginAt("/QueryFormTricky.html");
		setFormElementWithLabel("Trick", "oneValue");
		setFormElementWithLabel("Treat", "anotherValue");
		submit();
		assertTextPresent("param3=oneValue&param4=anotherValue");
	}

	private void gotoMultiButtonPage() {
		beginAt("/MultiNamedButtonForm.html");
	}

	private void addServletResource() {
		//		addTargetResource("TargetPage", "color=blue");
		//		addTargetResource("TargetPage", "color=red");
		//		addTargetResource("TargetPage", "color=blue&checkBox=on");
		//		addTargetResource("TargetPage", "color=blue&size=big");
		//		addTargetResource("TargetPage", "color=blue&size=small");
		//		addTargetResource("TargetPage", "param2=anyvalue");
		//		addTargetResource("TargetPage", "param4=anyvalue");
		//		addTargetResource("TargetPage", "param2=anyvalue&button2b=b2b");
		//		addTargetResource("TargetPage", "param2=anyvalue&button2a=b2a");
		//		addTargetResource("TargetPage",
		// "param1=oneValue&param2=anotherValue");
		//		addTargetResource("TargetPage",
		// "param3=oneValue&param4=anotherValue");
		//		addTargetResource("TargetPage", "checkBox=1&checkBox=3");
		//		addTargetResource("TargetPage", "checkBox=1");
	}

}