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
public class FormSubmissionWithLabelTest extends JWebUnitAPITestCase {

	public static Test suite() {
		Test suite = new TestSuite(FormSubmissionWithLabelTest.class);
		return new JettySetup(suite);
	}	
	
	public void setUp() throws Exception {
		super.setUp();
		getTestContext().setBaseUrl(HOST_PATH + "/FormSubmissionTest");
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

}