package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.JWebUnitTest;

import java.io.IOException;

import junit.framework.AssertionFailedError;
import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;

/**
 * Test form submission related methods of WebTestCase.
 *
 * If there is more than one submit button on a page, WebTestCase / httpunit
 * require indication of which button to submit with prior to form submission.
 *
 * @author Jim Weaver
 */
public class FormSubmissionTest extends JWebUnitTest {

    public FormSubmissionTest(String s) {
        super(s);
    }

    public void setUp() throws Exception {
        super.setUp();
        addServletResource();
    }

    public void testSetInputField() {
        addSingleNamedButtonForm();
        beginAt("/QueryForm.html");
        setFormParameter("color", "blue");
        submit("button");
        assertTextInResponse("Parms are: color=blue");
        beginAt("/QueryForm.html");
        setFormParameter("color", "red");
        submit();
        assertTextInResponse("Parms are: color=red");
    }

    public void testSingleFormSingleUnnamedButtonSubmission() {
        addSingleUnnamedButtonForm();
        beginAt("/QueryForm.html");
        submit();
        assertTextInResponse("Parms are: color=blue");
    }

    public void testSingleNamedButtonSubmission() {
        addSingleNamedButtonForm();
        beginAt("/QueryForm.html");
        setFormParameter("color", "red");
        submit();
        assertTextInResponse("Parms are: color=red");
    }

    public void testSingleFormMultipleButtonSubmission() {
        gotoMultiButtonPage();
        submit("color");
        assertTextInResponse("Parms are: color=red");
    }

    public void testBogusParameter() {
        gotoMultiButtonPage();
        try {
            setFormParameter("nonexistent", "anyvalue");
        } catch (AssertionFailedError e) {
            return;
        }
        fail("Expected AssertionFailedError");
    }

    public void testParamSetOnMultiForm() {
        addMultiForm();
        beginAt("/QueryForm.html");
        setFormParameter("param2", "anyvalue");
        setFormParameter("param1", "anyvalue");
        submit("button2a");
        assertTextInResponse("param2=anyvalue");
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
        setFormParameter("param4", "anyvalue");
        submitForm("form4");
        assertTextInResponse("param4=anyvalue");
    }

    public void testNamedSubmitOnSpecificForm() {
        addMultiForm();
        beginAt("/QueryForm.html");
        setFormParameter("param2", "anyvalue");
        submitForm("form2", "button2b");
        assertTextInResponse("param2=anyvalue&button2b=b2b");
    }

    private void gotoMultiButtonPage() {
        addMultiNamedButtonForm();
        beginAt("/QueryForm.html");
    }

    private void addServletResource() {
        addTargetResource("TargetPage", "color=blue");
        addTargetResource("TargetPage", "color=red");
        addTargetResource("TargetPage", "color=blue&size=big");
        addTargetResource("TargetPage", "color=blue&size=small");
        addTargetResource("TargetPage", "param2=anyvalue");
        addTargetResource("TargetPage", "param4=anyvalue");
        addTargetResource("TargetPage", "param2=anyvalue&button2b=b2b");
        addTargetResource("TargetPage", "param2=anyvalue&button2a=b2a");
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
                       "<input type=\"text\" name=\"color\"><input type=\"submit\" name=\"button\"></form></body></html>");
    }

    private void addMultiNamedButtonForm() {
        defineResource("QueryForm.html",
                       "<html><head></head>" +
                       "<form method=GET action=\"TargetPage\">" +
                       "<input name=\"button1\" value=\"b1\" type=submit>" +
                       "<input name=\"color\" value=\"red\" type=submit>" +
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
                       "</body></html>");

    }

}
