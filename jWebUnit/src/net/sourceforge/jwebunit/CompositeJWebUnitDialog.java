/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

import java.io.PrintStream;

import org.w3c.dom.Element;

import com.meterware.httpunit.WebClient;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.meterware.httpunit.WebWindow;

/**
 * This class is a "composite" implementation of the IJWebUnitDialog interface.
 * Functions: -Implements all of the interface calls with a stubbed
 * "NotSupportedException".
 * 
 * Intended uses: -Extend this when a specific engine doesn't care about
 * implementing all of the required methods.
 * 
 * @author Nick Neuberger
 *  
 */
public abstract class CompositeJWebUnitDialog implements IJWebUnitDialog {

    /**
     * Default Constructor used for reflection on what class to instantiate within
     * the WebTester class for the running testing engine (HttpUnitDialog / JacobieDialog
     */
    public CompositeJWebUnitDialog() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#constructNewDialog(java.lang.String,
     *      java.lang.String)
     */
    public IJWebUnitDialog constructNewDialog(String url, TestContext context) {
        throw new UnsupportedOperationException("constructNewDialog");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getWindow(java.lang.String)
     */
    public WebWindow getWindow(String windowName) {
        throw new UnsupportedOperationException("getWindow");
    }

    /**
     * Return the first open window with the given title.
     */
    public WebWindow getWindowByTitle(String title) {
        throw new UnsupportedOperationException("getWindowByTitle");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getWebClient()
     */
    public WebClient getWebClient() {
        throw new UnsupportedOperationException("getWebClient");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponse()
     */
    public WebResponse getResponse() {
        throw new UnsupportedOperationException("getResponse");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponseText()
     */
    public String getResponseText() {
        throw new UnsupportedOperationException("getResponseText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponsePageTitle()
     */
    public String getResponsePageTitle() {
        throw new UnsupportedOperationException("getResponsePageTitle");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasCookie(java.lang.String)
     */
    public boolean hasCookie(String cookieName) {
        throw new UnsupportedOperationException("hasCookie");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookieValue(java.lang.String)
     */
    public String getCookieValue(String cookieName) {
        throw new UnsupportedOperationException("getCookieValue");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getForm()
     */
    //public IWebForm getForm() {
    //    throw new UnsupportedOperationException("getForm");
    //}

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setWorkingForm(java.lang.String)
     */
    public void setWorkingForm(String nameOrId) {
        throw new UnsupportedOperationException("setWorkingForm");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasForm()
     */
    public boolean hasForm() {
        throw new UnsupportedOperationException("hasForm");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasForm(java.lang.String)
     */
    public boolean hasForm(String nameOrID) {
        throw new UnsupportedOperationException("hasForm");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFormParameterNamed(java.lang.String)
     */
    public boolean hasFormParameterNamed(String paramName) {
        throw new UnsupportedOperationException("hasFormParameterNamed");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setFormParameter(java.lang.String,
     *      java.lang.String)
     */
    public void setFormParameter(String paramName, String paramValue) {
        throw new UnsupportedOperationException("setFormParameter");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#updateFormParameter(java.lang.String,
     *      java.lang.String)
     */
    public void updateFormParameter(String paramName, String paramValue) {
        throw new UnsupportedOperationException("updateFormParameter");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormParameterValue(java.lang.String)
     */
    public String getFormParameterValue(String paramName) {
        throw new UnsupportedOperationException("getFormParameterValue");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#removeFormParameter(java.lang.String)
     */
    public void removeFormParameter(String paramName) {
        throw new UnsupportedOperationException("removeFormParameter");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#removeFormParameterWithValue(java.lang.String,
     *      java.lang.String)
     */
    public void removeFormParameterWithValue(String paramName, String value) {
        throw new UnsupportedOperationException("removeFormParameterWithValue");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFormParameterLabeled(java.lang.String)
     */
    public boolean hasFormParameterLabeled(String paramLabel) {
        throw new UnsupportedOperationException("hasFormParameterLabeled");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementNameForLabel(java.lang.String)
     */
    public String getFormElementNameForLabel(String formElementLabel) {
        throw new UnsupportedOperationException("getFormElementNameForLabel");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSubmitButtonValue(java.lang.String)
     */
    public String getSubmitButtonValue(String buttonName) {
        throw new UnsupportedOperationException("getSubmitButtonValue");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasSubmitButton(java.lang.String)
     */
    public boolean hasSubmitButton(String buttonName) {
        throw new UnsupportedOperationException("hasSubmitButton");
    }
    
    public boolean hasSubmitButton(String buttonName, String buttonValue) {
        throw new UnsupportedOperationException("hasSubmitButton(buttonName, buttonValue)");
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
    
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasButton(java.lang.String)
     */
    public boolean hasButton(String buttonId) {
        throw new UnsupportedOperationException("hasButton");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isTextInResponse(java.lang.String)
     */
    public boolean isTextInResponse(String text) {
        throw new UnsupportedOperationException("isTextInResponse");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isTextInTable(java.lang.String,
     *      java.lang.String)
     */
    public boolean isTextInTable(String tableSummaryOrId, String text) {
        throw new UnsupportedOperationException("isTextInTable");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getWebTableBySummaryOrId(java.lang.String)
     */
    public WebTable getWebTableBySummaryOrId(String tableSummaryOrId) {
        throw new UnsupportedOperationException("getWebTableBySummaryOrId");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSparseTableBySummaryOrId(java.lang.String)
     */
    public String[][] getSparseTableBySummaryOrId(String tableSummaryOrId) {
        throw new UnsupportedOperationException("getSparseTableBySummaryOrId");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#submit()
     */
    public void submit() {
        throw new UnsupportedOperationException("submit");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#submit(java.lang.String)
     */
    public void submit(String buttonName) {
        throw new UnsupportedOperationException("submit");
    }

    /**
     * Submit the current form with the specifed submit button (by name and value). See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     * 
     * @author Dragos Manolescu
     * @param buttonName
     *            name of the button to use for submission.
     * @param buttonValue
     * 			  value/label of the button to use for submission
     */
    public void submit(String buttonName, String buttonValue) {
        throw new UnsupportedOperationException("submit(buttonName, buttonValue)");
    }


    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#reset()
     */
    public void reset() {
        throw new UnsupportedOperationException("reset");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithText(java.lang.String)
     */
    public boolean isLinkPresentWithText(String linkText) {
        throw new UnsupportedOperationException("isLinkPresentWithText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithText(java.lang.String,
     *      int)
     */
    public boolean isLinkPresentWithText(String linkText, int index) {
        throw new UnsupportedOperationException("isLinkPresentWithText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithText(java.lang.String)
     */
    public boolean isLinkPresentWithExactText(String linkText) {
        throw new UnsupportedOperationException("isLinkPresentWithText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithText(java.lang.String,
     *      int)
     */
    public boolean isLinkPresentWithExactText(String linkText, int index) {
        throw new UnsupportedOperationException("isLinkPresentWithText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithImage(java.lang.String)
     */
    public boolean isLinkPresentWithImage(String imageFileName) {
        throw new UnsupportedOperationException("isLinkPresentWithImage");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresent(java.lang.String)
     */
    public boolean isLinkPresent(String anId) {
        throw new UnsupportedOperationException("isLinkPresent");
    }

    public boolean isCheckboxSelected(String checkBoxName) {
        throw new UnsupportedOperationException("isCheckboxSelected");
    }

    public boolean isCheckboxNotSelected(String checkBoxName) {
        throw new UnsupportedOperationException("isCheckboxNotSelected");
    }

    /**
     * Select a specified checkbox.  If the checkbox is already checked then the checkbox
     * will stay checked.
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
        throw new UnsupportedOperationException("checkCheckbox");
    }

    public void checkCheckbox(String checkBoxName, String value) {
        throw new UnsupportedOperationException("checkCheckbox2Params");
    }

    /**
     * Deselect a specified checkbox.  If the checkbox is already unchecked then the checkbox
     * will stay unchecked.
     *
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        throw new UnsupportedOperationException("uncheckCheckbox");
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        throw new UnsupportedOperationException("uncheckCheckbox2Params");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithText(java.lang.String)
     */
    public void clickLinkWithText(String linkText) {
        throw new UnsupportedOperationException("clickLinkWithText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithText(java.lang.String,
     *      int)
     */
    public void clickLinkWithText(String linkText, int index) {
        throw new UnsupportedOperationException("clickLinkWithText");
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithExactText(java.lang.String)
     */
    public void clickLinkWithExactText(String linkText) {
        throw new UnsupportedOperationException("clickLinkWithExactText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithExactText(java.lang.String,
     *      int)
     */
    public void clickLinkWithExactText(String linkText, int index) {
        throw new UnsupportedOperationException("clickLinkWithExactText");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithTextAfterText(java.lang.String,
     *      java.lang.String)
     */
    public void clickLinkWithTextAfterText(String linkText, String labelText) {
        throw new UnsupportedOperationException("clickLinkWithTextAfterText");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLink(java.lang.String)
     */
    public void clickLink(String anID) {
        throw new UnsupportedOperationException("clickLink");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithImage(java.lang.String)
     */
    public void clickLinkWithImage(String imageFileName) {
        throw new UnsupportedOperationException("clickLinkWithImage");
    }

    /**
     * Clicks a radio option.  Asserts that the radio option exists first.	 
     * 
     * * @param radioGroup
	 *			name of the radio group.
	 * @param radioOption
	 * 			value of the option to check for.
     */    
    public void clickRadioOption(String radioGroup, String radioOption) {
        throw new UnsupportedOperationException("clickRadioOption");
    }    
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickButton(java.lang.String)
     */
    public void clickButton(String buttonId) {
        throw new UnsupportedOperationException("clickButton");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasRadioOption(java.lang.String,
     *      java.lang.String)
     */
    public boolean hasRadioOption(String radioGroup, String radioOption) {
        throw new UnsupportedOperationException("hasRadioOption");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getOptionsFor(java.lang.String)
     */
    public String[] getOptionsFor(String selectName) {
        throw new UnsupportedOperationException("getOptionsFor");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getOptionValuesFor(java.lang.String)
     */
    public String[] getOptionValuesFor(String selectName) {
        throw new UnsupportedOperationException("getOptionValuesFor");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectedOption(java.lang.String)
     */
    public String getSelectedOption(String selectName) {
        throw new UnsupportedOperationException("getSelectedOption");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getValueForOption(java.lang.String,
     *      java.lang.String)
     */
    public String getValueForOption(String selectName, String option) {
        throw new UnsupportedOperationException("getValueForOption");
    }

    public boolean hasSelectOption(String selectName, String optionLabel) {
        throw new UnsupportedOperationException("hasSelectOption");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#selectOption(java.lang.String,
     *      java.lang.String)
     */
    public void selectOption(String selectName, String option) {
        throw new UnsupportedOperationException("selectOption");

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getElement(java.lang.String)
     */
    public Element getElement(String anID) {
        throw new UnsupportedOperationException("getElement");

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isTextInElement(org.w3c.dom.Element,
     *      java.lang.String)
     */
    public boolean isTextInElement(Element element, String text) {
        throw new UnsupportedOperationException("isTextInElement");

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoWindow(java.lang.String)
     */
    public void gotoWindow(String windowName) {
        throw new UnsupportedOperationException("gotoWindow");
    }
    
    /**
     * Goto first window with the given title.
     * 
     * @param windowName
     */
    public void gotoWindowByTitle(String title) {
        throw new UnsupportedOperationException("gotoWindowByTitle");
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoRootWindow()
     */
    public void gotoRootWindow() {
        throw new UnsupportedOperationException("gotoRootWindow");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoFrame(java.lang.String)
     */
    public void gotoFrame(String frameName) {
        throw new UnsupportedOperationException("gotoFrame");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFrame(java.lang.String)
     */
    public WebResponse getFrame(String frameName) {
        throw new UnsupportedOperationException("getFrame");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoPage(java.lang.String)
     */
    public void gotoPage(String url) {
        throw new UnsupportedOperationException("gotoPage");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpCookies(java.io.PrintStream)
     */
    public void dumpCookies(PrintStream stream) {
        throw new UnsupportedOperationException("dumpCookies");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse()
     */
    public void dumpResponse() {
        throw new UnsupportedOperationException("dumpResponse");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse(java.io.PrintStream)
     */
    public void dumpResponse(PrintStream stream) {
        throw new UnsupportedOperationException("dumpResponse");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String,
     *      java.io.PrintStream)
     */
    public void dumpTable(String tableNameOrId, PrintStream stream) {
        throw new UnsupportedOperationException("dumpTable");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String,
     *      java.lang.String[][])
     */
    public void dumpTable(String tableNameOrId, String[][] table) {
        throw new UnsupportedOperationException("dumpTable");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String,
     *      java.lang.String[][], java.io.PrintStream)
     */
    public void dumpTable(String tableNameOrId, String[][] table, PrintStream stream) {
        throw new UnsupportedOperationException("dumpTable");
    }

}