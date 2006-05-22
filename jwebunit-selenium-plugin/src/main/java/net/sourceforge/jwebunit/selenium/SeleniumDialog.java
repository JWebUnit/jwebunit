/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.selenium;

import java.io.PrintStream;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
//import org.openqa.selenium.server.SeleniumServer;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.util.ExceptionUtility;
import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.TestContext;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Acts as the wrapper for HtmlUnit access. A dialog is initialized with a given
 * URL, and maintains conversational state as the dialog progresses through link
 * navigation, form submission, etc.
 * 
 * @author Julien Henry
 * 
 */
public class SeleniumDialog implements IJWebUnitDialog {

    private DefaultSelenium selenium;

   // private static SeleniumServer server;
    
    private static final int port = 4444;

    private TestContext testContext;

    private String form = null;

    public SeleniumDialog() {
//        if (server == null) {
//            try {
//                server = new SeleniumServer(port);
//                server.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//        }
    }

    /**
     * Begin a dialog with an initial URL and test client context.
     * 
     * @param initialURL
     *            absolute url at which to begin dialog.
     * @param context
     *            contains context information for the test client.
     * @throws TestingEngineResponseException
     */
    public void beginAt(String initialURL, TestContext context)
            throws TestingEngineResponseException {
        this.setTestContext(context);
        selenium = new DefaultSelenium("localhost", port, "*firefox",
                initialURL);
        selenium.start();
    }

    public boolean isWindowExists(String windowName) {
        try {
            selenium.selectWindow(windowName);
            selenium.selectWindow("null");
            return true;
        } catch (SeleniumException e) {
            return false;
        }
    }

    public boolean isWindowByTitleExists(String title) {
        throw new UnsupportedOperationException("isWindowByTitleExists");
    }

    public boolean isElementPresent(String anID) {
        return isXPathElementPresent("//[@id=\"" + anID + "\"]");
    }

    /**
     * Return the page title of the current response page, encoded as specified
     * by the current {@link net.sourceforge.jwebunit.TestContext}.
     */
    public String getCurrentPageTitle() {
        throw new UnsupportedOperationException("getCurrentPageTitle");
    }

    /**
     * <p>
     * Return the current form active for the dialog.
     * </p>
     * <p>
     * The active form can also be explicitly set by {@link #setWorkingForm}.
     * </p>
     * <p>
     * If this method is called without the form having been implicitly or
     * explicitly set, it will attempt to return the default first form on the
     * page.
     * </p>
     * 
     * @exception UnableToSetFormException
     *                This runtime assertion failure will be raised if there is
     *                no form on the response.
     * @return HtmlForm object representing the current active form from the
     *         response.
     */
    private String getForm() {
        return form;
    }

    /**
     * Set the form on the current response that the client wishes to work with
     * explicitly by either the form name or id (match by id is attempted
     * first).
     * 
     * @param nameOrId
     *            name or id of the form to be worked with.
     */
    public void setWorkingForm(String nameOrId) {
        form = nameOrId;
    }

    /**
     * Return true if the current response contains a form.
     */
    public boolean hasForm() {
        try {
            selenium.isElementPresent("//form");
        } catch (SeleniumException e) {
            return false;
        }
        return true;
    }

    /**
     * Return true if the current response contains a specific form.
     * 
     * @param nameOrID
     *            name of id of the form to check for.
     */
    public boolean hasForm(String nameOrID) {
        try {
            selenium.isElementPresent("//form[@name=\"" + nameOrID
                    + "\" or @id=\"" + nameOrID + "\"]");
        } catch (SeleniumException e) {
            return false;
        }
        return true;
    }

    /**
     * Return true if a form parameter (input element) is present on the current
     * response.
     * 
     * @param inputName
     *            name of the input element to check for
     */
    public boolean hasFormInputNamed(String inputName) {
        try {
            selenium.isElementPresent("//input[@name=\"" + inputName
                    + "\"]");
        } catch (SeleniumException e) {
            return false;
        }
        return true;
    }

    /**
     * Return true if a form button is present on the current response.
     * 
     * @param buttonName
     *            name of the button element to check for
     */
    public boolean hasFormButtonNamed(String buttonName) {
        try {
            selenium.isElementPresent("//button[@name=\"" + buttonName
                    + "\"]");
        } catch (SeleniumException e) {
            return false;
        }
        return true;
    }

    /**
     * Set a form text or password element to the provided value.
     * 
     * @param fieldName
     *            name of the input element
     * @param paramValue
     *            parameter value to submit for the element.
     */
    public void setTextField(String fieldName, String paramValue) {
        selenium.type(fieldName, paramValue);
    }

    /**
     * Return the current value of a form input element.
     * 
     * @param paramName
     *            name of the input element.
     */
    public String getFormInputValue(String paramName) {
        throw new UnsupportedOperationException("getFormParameterValue");
    }

    public String getSubmitButtonValue(String buttonName) {
        throw new UnsupportedOperationException("getSubmitButtonValue");
    }

    public boolean hasSubmitButton(String buttonName) {
        try {
            selenium
                    .isElementPresent("//input[@type=\"submit\" and @name=\""
                            + buttonName + "\"]");
        } catch (SeleniumException e) {
            return false;
        }
        return true;
    }

    public boolean hasSubmitButton(String buttonName, String buttonValue) {
        try {
            selenium
                    .isElementPresent("//input[@type=\"submit\" and @name=\""
                            + buttonName
                            + "\" and @value=\""
                            + buttonValue
                            + "\"]");
        } catch (SeleniumException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a button with <code>text</code> is present.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     * @return <code>true</code> when the button with text could be found.
     */
    public boolean hasButtonWithText(String text) {
        throw new UnsupportedOperationException("hasButtonWithText");
    }

    /**
     * Returns if the button identified by <code>buttonId</code> is present.
     * 
     * @param buttonId
     *            the id of the button
     * @return <code>true</code> when the button was found.
     */
    public boolean hasButton(String buttonId) {
        throw new UnsupportedOperationException("hasButton");
    }

    /**
     * Return true if given text is present anywhere in the current response.
     * 
     * @param text
     *            string to check for.
     */
    public boolean isTextInResponse(String text) {
        try {
            selenium.isTextPresent(text);
        } catch (SeleniumException e) {
            return false;
        }
        return true;
    }

    /**
     * Return true if given regexp has a match anywhere in the current response.
     * 
     * @param regexp
     *            regexp to match.
     */
    public boolean isMatchInResponse(String regexp) {
        throw new UnsupportedOperationException("isMatchInResponse");
    }

    public boolean isCheckboxSelected(String checkBoxName) {
        throw new UnsupportedOperationException("isCheckboxSelected");
    }

    /**
     * Return true if given text is present in a specified table of the
     * response.
     * 
     * @param tableSummaryOrId
     *            table summary or id to inspect for expected text.
     * @param text
     *            expected text to check for.
     */
    public boolean isTextInTable(String tableSummaryOrId, String text) {
        throw new UnsupportedOperationException("isTextInTable");
    }

    /**
     * Return true if given regexp has a match in a specified table of the
     * response.
     * 
     * @param tableSummaryOrId
     *            table summary or id to inspect for expected text.
     * @param regexp
     *            regexp to match.
     */
    public boolean isMatchInTable(String tableSummaryOrId, String regexp) {
        throw new UnsupportedOperationException("isMatchInTable");
    }

    public String[][] getTableBySummaryOrIdAsText(String tableSummaryOrId) {
        throw new UnsupportedOperationException("getTableBySummaryOrIdAsText");
    }

    public boolean isTableBySummaryOrIdExists(String tableSummaryOrId) {
        throw new UnsupportedOperationException("isTableBySummaryOrIdExists");
    }

    /**
     * Submit the current form with the default submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    public void submit() {
    }

    /**
     * Submit the current form with the specifed submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     * 
     * @param buttonName
     *            name of the button to use for submission.
     */
    public void submit(String buttonName) {
    }

    /**
     * Submit the current form with the specifed submit button (by name and
     * value). See {@link #getForm}for an explanation of how the current form
     * is established.
     * 
     * @author Dragos Manolescu
     * @param buttonName
     *            name of the button to use for submission.
     * @param buttonValue
     *            value/label of the button to use for submission
     */
    public void submit(String buttonName, String buttonValue) {
        throw new UnsupportedOperationException("submit");
    }

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    public void reset() {
    }

    /**
     * Return true if a link is present in the current response containing the
     * specified text (note that HttpUnit uses contains rather than an exact
     * match - if this is a problem consider using ids on the links to uniquely
     * identify them).
     * 
     * @param linkText
     *            text to check for in links on the response.
     */
    public boolean isLinkPresentWithText(String linkText) {
        return isLinkPresentWithText(linkText, 1);
    }

    /**
     * Return true if a link is present in the current response containing the
     * specified text (note that HttpUnit uses contains rather than an exact
     * match - if this is a problem consider using ids on the links to uniquely
     * identify them).
     * 
     * @param linkText
     *            text to check for in links on the response.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    public boolean isLinkPresentWithText(String linkText, int index) {
        throw new UnsupportedOperationException("isLinkPresentWithText");
    }

    /**
     * Return true if a link is present with a given image based on filename of
     * image.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match
     *            <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    public boolean isLinkPresentWithImage(String imageFileName) {
        throw new UnsupportedOperationException("isLinkPresentWithImage");
    }

    /**
     * Return true if a link is present in the current response with the
     * specified id.
     * 
     * @param anId
     *            link id to check for.
     */
    public boolean isLinkPresent(String anId) {
        throw new UnsupportedOperationException("isLinkPresent");
    }

    /**
     * Navigate by submitting a request based on a link containing the specified
     * text. A RuntimeException is thrown if no such link can be found.
     * 
     * @param linkText
     *            text which link to be navigated should contain.
     */
    public void clickLinkWithText(String linkText) {
    }

    public void clickLinkWithText(String linkText, int index) {
    }

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
    }

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then
     * the checkbox will stay unchecked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
    }

    /**
     * Clicks a radio option. Asserts that the radio option exists first. *
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOption
     *            value of the option to check for.
     */
    public void clickRadioOption(String radioGroup, String radioOption) {
    }

    /**
     * Navigate by submitting a request based on a link with a given ID. A
     * RuntimeException is thrown if no such link can be found.
     * 
     * @param anID
     *            id of link to be navigated.
     */
    public void clickLink(String anID) {
    }

    /**
     * Navigate by submitting a request based on a link with a given image file
     * name. A RuntimeException is thrown if no such link can be found.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match
     *            <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    public void clickLinkWithImage(String imageFileName) {
    }

    /**
     * Click the indicated button (input type=button).
     * 
     * @param buttonId
     */
    public void clickButton(String buttonId) {
    }

    public void clickButtonWithText(String buttonValueText) {
    }

    /**
     * Return true if a radio group contains the indicated option.
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOption
     *            value of the option to check for.
     */
    public boolean hasRadioOption(String radioGroup, String radioOption) {
        throw new UnsupportedOperationException("hasRadioOption");
    }

    /**
     * Return a string array of select box option labels.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String[] getOptionsFor(String selectName) {
        throw new UnsupportedOperationException("getOptionsFor");
    }

    /**
     * Return a string array of select box option values.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String[] getOptionValuesFor(String selectName) {
        throw new UnsupportedOperationException("getOptionValuesFor");
    }

    /**
     * Return the label of the currently selected item in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String getSelectedOption(String selectName) {
        throw new UnsupportedOperationException("getSelectedOption");
    }

    /**
     * Get the value for a given option of a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @param option
     *            label of the option.
     */
    public String getValueForOption(String selectName, String option) {
        throw new UnsupportedOperationException("getValueForOption");
    }

    /**
     * Return true if a select box contains the indicated option.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     */
    public boolean hasSelectOption(String selectName, String optionLabel) {
        throw new UnsupportedOperationException("hasSelectOption");
    }

    /**
     * Return true if a select box contains the indicated option.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionValue
     *            value of the option.
     */
    public boolean hasSelectOptionByValue(String selectName, String optionValue) {
        throw new UnsupportedOperationException("hasSelectOptionByValue");
    }

    /**
     * Select an option of a select box by display label.
     * 
     * @param selectName
     *            name of the select box.
     * @param option
     *            label of the option to select.
     */
    public void selectOption(String selectName, String option) {
    }

    /**
     * Select an option of a select box by value.
     * 
     * @param selectName
     *            name of the select box.
     * @param option
     *            value of the option to select.
     */
    public void selectOptionByValue(String selectName, String option) {
    }

    public boolean isTextInElement(String elementID, String text) {
        throw new UnsupportedOperationException("isTextInElement");
    }

    public boolean isMatchInElement(String elementID, String regexp) {
        throw new UnsupportedOperationException("isTextInElement");
    }

    private RE getRE(String regexp) {
        try {
            return new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Make the window with the given name in the current conversation active.
     * 
     * @param windowName
     */
    public void gotoWindow(String windowName) {
    }

    /**
     * Goto first window with the given title.
     * 
     * @param windowName
     */
    public void gotoWindowByTitle(String title) {
    }

    /**
     * Make the root window in the current conversation active.
     */
    public void gotoRootWindow() {
    }

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     */
    public void gotoFrame(String frameName) {
    }

    public boolean isFrameExists(String frameName) {
        throw new UnsupportedOperationException("isFrameExists");
    }

    /**
     * @param testContext
     *            The testContext to set.
     */
    public void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * @return Returns the testContext.
     */
    public TestContext getTestContext() {
        return testContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.capgemini.capwebunit.IJWebUnitDialog#clickLinkWithImage(java.lang.String,
     *      int)
     */
    public void clickLinkWithImage(String imageFileName, int index) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.capgemini.capwebunit.IJWebUnitDialog#clickXPathElement(java.lang.String)
     */
    public void clickXPathElement(String xpath) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.capgemini.capwebunit.IJWebUnitDialog#hasFormSelectNamed(java.lang.String)
     */
    public boolean hasFormSelectNamed(String inputName) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.capgemini.capwebunit.IJWebUnitDialog#isLinkPresentWithImage(java.lang.String,
     *      int)
     */
    public boolean isLinkPresentWithImage(String imageFileName, int index) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.capgemini.capwebunit.IJWebUnitDialog#isXPathElementPresent(java.lang.String)
     */
    public boolean isXPathElementPresent(String xpath) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#checkCheckbox(java.lang.String,
     *      java.lang.String)
     */
    public void checkCheckbox(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithExactText(java.lang.String,
     *      int)
     */
    public void clickLinkWithExactText(String arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithExactText(java.lang.String)
     */
    public void clickLinkWithExactText(String arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithTextAfterText(java.lang.String,
     *      java.lang.String)
     */
    public void clickLinkWithTextAfterText(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpCookies(java.io.PrintStream)
     */
    public void dumpCookies(PrintStream arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse()
     */
    public void dumpResponse() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse(java.io.PrintStream)
     */
    public void dumpResponse(PrintStream arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String,
     *      java.io.PrintStream)
     */
    public void dumpTable(String arg0, PrintStream arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String,
     *      java.lang.String[][], java.io.PrintStream)
     */
    public void dumpTable(String arg0, String[][] arg1, PrintStream arg2) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String,
     *      java.lang.String[][])
     */
    public void dumpTable(String arg0, String[][] arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookieValue(java.lang.String)
     */
    public String getCookieValue(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementNameBeforeLabel(java.lang.String)
     */
    public String getFormElementNameBeforeLabel(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementNameForLabel(java.lang.String)
     */
    public String getFormElementNameForLabel(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementValueBeforeLabel(java.lang.String)
     */
    public String getFormElementValueBeforeLabel(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementValueForLabel(java.lang.String)
     */
    public String getFormElementValueForLabel(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormParameterValue(java.lang.String)
     */
    public String getFormParameterValue(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponsePageTitle()
     */
    public String getPageTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponseText()
     */
    public String getPageText() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSparseTableBySummaryOrId(java.lang.String)
     */
    public String[][] getSparseTableBySummaryOrId(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getTableBySummaryOrId(java.lang.String)
     */
    public String[][] getTableBySummaryOrId(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoPage(java.lang.String)
     */
    public void gotoPage(String arg0) throws TestingEngineResponseException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasCookie(java.lang.String)
     */
    public boolean hasCookie(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFormParameterLabeled(java.lang.String)
     */
    public boolean hasFormParameterLabeled(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFormParameterNamed(java.lang.String)
     */
    public boolean hasFormParameterNamed(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isCheckboxNotSelected(java.lang.String)
     */
    public boolean isCheckboxNotSelected(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isFramePresent(java.lang.String)
     */
    public boolean isFramePresent(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithExactText(java.lang.String,
     *      int)
     */
    public boolean isLinkPresentWithExactText(String arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithExactText(java.lang.String)
     */
    public boolean isLinkPresentWithExactText(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWebTableBySummaryOrIdPresent(java.lang.String)
     */
    public boolean isWebTableBySummaryOrIdPresent(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWindowByTitlePresent(java.lang.String)
     */
    public boolean isWindowByTitlePresent(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWindowPresent(java.lang.String)
     */
    public boolean isWindowPresent(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#removeFormParameter(java.lang.String)
     */
    public void removeFormParameter(String arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#removeFormParameterWithValue(java.lang.String,
     *      java.lang.String)
     */
    public void removeFormParameterWithValue(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#resetDialog()
     */
    public void resetDialog() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setFormParameter(java.lang.String,
     *      java.lang.String)
     */
    public void setFormParameter(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setScriptingEnabled(boolean)
     */
    public void setScriptingEnabled(boolean arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#uncheckCheckbox(java.lang.String,
     *      java.lang.String)
     */
    public void uncheckCheckbox(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#updateFormParameter(java.lang.String,
     *      java.lang.String)
     */
    public void updateFormParameter(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#closeBrowser()
     */
    public void closeBrowser() throws TestingEngineResponseException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#closeWindow()
     */
    public void closeWindow() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookies()
     */
    public String[][] getCookies() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getPageSource()
     */
    public String getPageSource() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectedOptions(java.lang.String)
     */
    public String[] getSelectedOptions(String selectName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getServerResponse()
     */
    public String getServerResponse() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSparseTable(java.lang.String)
     */
    public String[][] getSparseTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getTable(java.lang.String)
     */
    public String[][] getTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#goBack()
     */
    public void goBack() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasElement(java.lang.String)
     */
    public boolean hasElement(String anID) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFrame(java.lang.String)
     */
    public boolean hasFrame(String frameName) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLink(java.lang.String)
     */
    public boolean hasLink(String anId) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLinkWithExactText(java.lang.String, int)
     */
    public boolean hasLinkWithExactText(String linkText, int index) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLinkWithImage(java.lang.String, int)
     */
    public boolean hasLinkWithImage(String imageFileName, int index) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLinkWithText(java.lang.String, int)
     */
    public boolean hasLinkWithText(String linkText, int index) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasTable(java.lang.String)
     */
    public boolean hasTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasWindow(java.lang.String)
     */
    public boolean hasWindow(String windowName) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasWindowByTitle(java.lang.String)
     */
    public boolean hasWindowByTitle(String title) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#refresh()
     */
    public void refresh() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#selectOptions(java.lang.String, java.lang.String[])
     */
    public void selectOptions(String selectName, String[] options) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#unselectOptions(java.lang.String, java.lang.String[])
     */
    public void unselectOptions(String selectName, String[] options) {
        // TODO Auto-generated method stub
        
    }

}