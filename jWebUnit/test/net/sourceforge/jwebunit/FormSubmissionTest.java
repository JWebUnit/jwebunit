package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.JWebUnitTest;

import java.io.IOException;

import junit.framework.AssertionFailedError;
import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;
import com.meterware.httpunit.WebResponse;

/**
 * Test form submission related methods of WebTestCase.
 *
 * If there is more than one submit button on a page, WebTestCase / httpunit
 * require indication of which button to submit with prior to form submission.
 *
 * @author Jim Weaver
 */
public class FormSubmissionTest extends JWebUnitTest {

    public void setUp() throws Exception {
        super.setUp();
        addServletResource();
    }

    public void testSetInputField() {
        addSingleNamedButtonForm();
        beginAt("/QueryForm.html");
        setFormElement("color", "blue");
        submit("button");
        assertTextPresent("Parms are: color=blue");
        beginAt("/QueryForm.html");
        setFormElement("color", "red");
        submit();
        assertTextPresent("Parms are: color=red");
    }

    public void testCheckBoxSelection() {
        addSingleNamedButtonForm();
        beginAt("/QueryForm.html");
        checkCheckbox("checkBox");
        setFormElement("color", "blue");
        submit();
        assertTextPresent("Parms are: color=blue&checkBox=on");
    }

    public void testCheckBoxSelectionWithSameFieldName() {
        addCheckboxForm();
        beginAt("/QueryForm.html");
        checkCheckbox("checkBox", "1");
        checkCheckbox("checkBox", "3");
        checkCheckbox("checkBox", "3");  // check for duplicates
        submit();
        assertTextPresent("Parms are: checkBox=1&checkBox=3");
    }

    public void testCheckBoxDeSelectionWithSameFieldName() {
        addCheckboxForm();
        beginAt("/QueryForm.html");
        checkCheckbox("checkBox", "1");
        checkCheckbox("checkBox", "3");
        uncheckCheckbox("checkBox", "3");
        submit();
        assertTextPresent("Parms are: checkBox=1");
    }

    public void testCheckBoxDeselection() {
        addSingleNamedButtonForm();
        beginAt("/QueryForm.html");
        checkCheckbox("checkBox");
        setFormElement("color", "blue");
        uncheckCheckbox("checkBox");
        submit();
        assertTextPresent("Parms are: color=blue");
    }

    public void testSingleFormSingleUnnamedButtonSubmission() {
        addSingleUnnamedButtonForm();
        beginAt("/QueryForm.html");
        submit();
        assertTextPresent("Parms are: color=blue");
    }

    public void testSingleNamedButtonSubmission() {
        addSingleNamedButtonForm();
        beginAt("/QueryForm.html");
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
        addMultiForm();
        beginAt("/QueryForm.html");
        setFormElement("param1", "anyvalue");
        setWorkingForm("form2");
        setFormElement("param2", "anyvalue");
        submit("button2a");
        assertTextPresent("param2=anyvalue");
    }

    public void testSetWorkingFormById() {
        addMultiForm();
        beginAt("/QueryForm.html");
        setWorkingForm("form5");
    }

    public void testInvalidButton() {
        gotoMultiButtonPage();
        try {
            submit("button1");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().indexOf("com.meterware.httpunit.HttpNotFoundException") != -1);
            return;
        }
        fail("Should have failed");
    }

    public void testUnnamedSubmitOnSpecificForm() {
        addMultiForm();
        beginAt("/QueryForm.html");
        setFormElement("param4", "anyvalue");
        submit();
        assertTextPresent("param4=anyvalue");
    }

    public void testNamedSubmitOnSpecificForm() {
        addMultiForm();
        beginAt("/QueryForm.html");
        setFormElement("param2", "anyvalue");
        submit("button2b");
        assertTextPresent("param2=anyvalue&button2b=b2b");
    }

    public void testSubmissionReset() {
        addMultiForm();
        beginAt("/QueryForm.html");
        setFormElement("param2", "anyvalue");
        WebResponse oldResp = getDialog().getResponse();
        submit("button2b");
        assertFalse(getDialog().hasForm());
        assertTrue(getDialog().getResponse() != oldResp);
    }

    public void testSelectOption() {
        addMultiForm();
        beginAt("/QueryForm.html");
        assertOptionEquals("select1", "one");
        selectOption("select1", "two");
        assertOptionEquals("select1", "two");
        assertFormElementEquals("select1", "2");
    }

    public void testSimpleLabeledForm() {
        addSimpleLabeledForm();
        beginAt("/QueryForm.html");
        setFormElementWithLabel("First", "oneValue");
        setFormElementWithLabel("Second", "anotherValue");
        submit();
        assertTextPresent("param1=oneValue&param2=anotherValue");
    }

    public void testTrickyLabeledForm() {
        addTrickyLabeledForm();
        beginAt("/QueryForm.html");
        setFormElementWithLabel("Trick", "oneValue");
        setFormElementWithLabel("Treat", "anotherValue");
        submit();
        assertTextPresent("param3=oneValue&param4=anotherValue");
    }

    private void gotoMultiButtonPage() {
        addMultiNamedButtonForm();
        beginAt("/QueryForm.html");
    }

    private void addServletResource() {
        addTargetResource("TargetPage", "color=blue");
        addTargetResource("TargetPage", "color=red");
        addTargetResource("TargetPage", "color=blue&checkBox=on");
        addTargetResource("TargetPage", "color=blue&size=big");
        addTargetResource("TargetPage", "color=blue&size=small");
        addTargetResource("TargetPage", "param2=anyvalue");
        addTargetResource("TargetPage", "param4=anyvalue");
        addTargetResource("TargetPage", "param2=anyvalue&button2b=b2b");
        addTargetResource("TargetPage", "param2=anyvalue&button2a=b2a");
        addTargetResource("TargetPage", "param1=oneValue&param2=anotherValue");
        addTargetResource("TargetPage", "param3=oneValue&param4=anotherValue");
        addTargetResource("TargetPage", "checkBox=1&checkBox=3");
        addTargetResource("TargetPage", "checkBox=1");
    }

    private void addTargetResource(String name, final String parms) {
        String resourceName = name;
        if (parms != null && !parms.equals("")) {
            resourceName += "?" + parms;
        }
//        System.out.println("resourceName = " + resourceName);
        defineResource(resourceName, new PseudoServlet() {
            public WebResource getPostResponse() {
                WebResource result = new WebResource("<html><body><table><tr><td>Parms are: " + parms +
                        "</td></tr></table></body></html>");
                return result;
            }

            public WebResource getGetResponse() throws IOException {
                return getPostResponse();
            }
        });
    }

    private void addSingleUnnamedButtonForm() {
        defineResource("QueryForm.html",
                "<html><head></head>" +
                "<form method=GET action=\"TargetPage?color=blue\">" +
                "<input type=submit></form></body></html>");
    }

    private void addSingleNamedButtonForm() {
        defineResource("QueryForm.html",
                "<html><head></head>" +
                "<form method=GET action=\"TargetPage\">" +
                "<input type=\"text\" name=\"color\"><input type=\"submit\" name=\"button\">" +
                "<input type=\"checkbox\" name=\"checkBox\" value=\"on\">" +
                "</form></body></html>");
    }


    private void addCheckboxForm() {
        defineResource("QueryForm.html",
                "<html><head></head>" +
                "<form method=GET action=\"TargetPage\">" +
                "<input type=\"checkbox\" name=\"checkBox\" value=\"1\">" +
                "<input type=\"checkbox\" name=\"checkBox\" value=\"2\">" +
                "<input type=\"checkbox\" name=\"checkBox\" value=\"3\">" +
                "<input type=\"submit\" name=\"button\">" +
                "</form></body></html>");
    }

    private void addMultiNamedButtonForm() {
        defineResource("QueryForm.html",
                "<html><head></head>" +
                "<form method=GET action=\"TargetPage\">" +
                "<input name=\"button1\" value=\"b1\" type=submit>" +
                "<input name=\"color\" value=\"red\" type=submit>" +
                "<input name=\"color\" value=\"blue\" type=submit>" +
                "</form></body></html>");
    }

    private void addMultiForm() {
        defineResource("QueryForm.html",
                "<html><head></head>" +
                "<form method=GET action=\"TargetPage\">" +
                "<input type=\"text\" name=\"param1\">" +
                "<input type=\"submit\" name=\"button1\"></form>" +

                "<form name=\"form2\" method=GET action=\"TargetPage\">" +
                "<input type=\"text\" name=\"param2\">" +
                "<input type=\"submit\" name=\"button2a\" value=\"b2a\">" +
                "<input type=\"submit\" name=\"button2b\" value=\"b2b\"></form>" +

                "<form name=\"form3\" method=GET action=\"TargetPage\">" +
                "<input type=\"text\" name=\"param3\"><input type=\"submit\"></form>" +

                "<form name=\"form4\" method=GET action=\"TargetPage\">" +
                "<input type=\"text\" name=\"param4\"><input type=\"submit\"></form>" +
                "<form id=\"form5\"/>" +
                "<form id=\"form6\">" +
                "<select name=\"select1\">" +
                "<option value=\"1\">one</option>" +
                "<option value=\"2\">two</option>" +
                "<option value=\"3\">three</option>" +
                "</select></form>" +
                "</body></html>");

    }

    private void addSimpleLabeledForm() {
        defineResource("QueryForm.html",
                "<html><head></head>" +
                "<body>" +
                "<form method=GET action=\"TargetPage\">" +
                "First : <input type=\"text\" name=\"param1\">" +
                "Second : <textarea name=\"param2\"></form>" +
                "</body></html>");
    }

    private void addTrickyLabeledForm() {
        defineResource("QueryForm.html",
                "<html><head></head>" +
                "Trick!" +
                "<form method=GET action=\"TargetPage\">" +
                "First : <input type=\"text\" name=\"param1\">" +
                "Second : <input type=\"text\" name=\"param2\">" +
                "Trick!</form>" +
                "<form name=\"form2\" method=GET action=\"TargetPage\">" +
                "Trick! <input type=\"text\" name=\"param3\">" +
                "Treat! <input type=\"text\" name=\"param4\">" +
                "</form></html>");
    }

}