/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit;

import java.io.PrintStream;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;

import org.w3c.dom.Element;

import com.meterware.httpunit.WebClient;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.meterware.httpunit.WebWindow;

/**
 * This is the interface for all communications between jWebUnit and the specific running
 * test engine or dialog.
 * 
 * @author Nick Neuberger
 */
public interface IJWebUnitDialog {

    
    /**
     * Calls the concrete dialog class to construct a new dialog instance.
     * 
     * THIS MUST BE IMPLEMENTED.
     * @param url
     * @param context
     * @return
     */
    public abstract IJWebUnitDialog constructNewDialog(String url, TestContext context);    
    
	public abstract void beginAt(String aInitialURL, TestContext aTestContext);
    
    /**
     * Return the window with the given name in the current conversation.
     * 
     * @param windowName
     */
    public abstract WebWindow getWindow(String windowName);

    /**
     * Return the first open window with the given title.
     */
    public abstract WebWindow getWindowByTitle(String title);
    
    /**
     * Return the HttpUnit WebClient object for this dialog.
     */
    public abstract WebClient getWebClient();

    /**
     * Return the HttpUnit object which represents the current response.
     */
    public abstract WebResponse getResponse();

    /**
     * Return the string representation of the current response, encoded as specified by the current
     * {@link net.sourceforge.jwebunit.TestContext}.
     */
    public abstract String getResponseText();

    /**
     * Return the page title of the current response page, encoded as specified by the current
     * {@link net.sourceforge.jwebunit.TestContext}.
     */
    public abstract String getResponsePageTitle();

    public abstract boolean hasCookie(String cookieName);

    public abstract String getCookieValue(String cookieName);

    //TODO: Move other dump methods to dialog!!
    //public abstract WebForm getForm();

    /**
     * Set the form on the current response that the client wishes to work with explicitly by either the form name or
     * id (match by id is attempted first).
     * 
     * @param nameOrId
     *            name or id of the form to be worked with.
     */
    public abstract void setWorkingForm(String nameOrId);

    /**
     * Return true if the current response contains a form.
     */
    public abstract boolean hasForm();

    /**
     * Return true if the current response contains a specific form.
     * 
     * @param nameOrID
     *            name of id of the form to check for.
     */
    public abstract boolean hasForm(String nameOrID);

    /**
     * Return true if a form parameter (input element) is present on the current response.
     * 
     * @param paramName
     *            name of the input element to check for
     */
    public abstract boolean hasFormParameterNamed(String paramName);

    /**
     * Set a form parameter / input element to the provided value.
     * 
     * @param paramName
     *            name of the input element
     * @param paramValue
     *            parameter value to submit for the element.
     */
    public abstract void setFormParameter(String paramName, String paramValue);

    public abstract void updateFormParameter(String paramName, String paramValue);

    /**
     * Return the current value of a form input element.
     * 
     * @param paramName
     *            name of the input element.
     */
    public abstract String getFormParameterValue(String paramName);

    /**
     * Specify that no parameter value should be submitted for a given input element. Typically used to uncheck check
     * boxes.
     * 
     * @param paramName
     *            name of the input element.
     */
    public abstract void removeFormParameter(String paramName);

    public abstract void removeFormParameterWithValue(String paramName,
            String value);

    /**
     * Return true if a form parameter (input element) is present on the current response preceded by a given label.
     * 
     * @param paramLabel
     *            label of the input element to check for
     */
    public abstract boolean hasFormParameterLabeled(String paramLabel);

    /**
     * Return the name of a form parameter (input element) on the current response preceded by a givel label.
     * 
     * @param formElementLabel
     *            label of the input element to fetch name.
     */
    public abstract String getFormElementNameForLabel(String formElementLabel);

    public abstract String getSubmitButtonValue(String buttonName);

    public abstract boolean hasSubmitButton(String buttonName);

    public abstract boolean hasSubmitButton(String buttonName, String buttonValue);

    /**
     * Checks if a button with <code>text</code> is present.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     * @return <code>true</code> when the button with text could be found.
     */
    public abstract boolean hasButtonWithText(String text);

    public abstract boolean hasButton(String buttonId);

    /**
     * Return true if given text is present anywhere in the current response.
     * 
     * @param text
     *            string to check for.
     */
    public abstract boolean isTextInResponse(String text);

    /**
     * Return true if given text is present in a specified table of the response.
     * 
     * @param tableSummaryOrId
     *            table summary or id to inspect for expected text.
     * @param text
     *            expected text to check for.
     */
    public abstract boolean isTextInTable(String tableSummaryOrId, String text);

    /**
     * Return the HttpUnit WebTable object representing a specified table in the current response. Null is returned if
     * a parsing exception occurs looking for the table or no table with the id or summary could be found.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table to return.
     */
    public abstract WebTable getWebTableBySummaryOrId(String tableSummaryOrId);

    /**
     * Return a sparse array (rows or columns without displayable text are removed) for a given table in the response.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table.
     */
    public abstract String[][] getSparseTableBySummaryOrId(
            String tableSummaryOrId);

    /**
     * Submit the current form with the default submit button. See {@link #getForm}for an explanation of how the
     * current form is established.
     */
    public abstract void submit();

    /**
     * Submit the current form with the specifed submit button. See {@link #getForm}for an explanation of how the
     * current form is established.
     * 
     * @param buttonName
     *            name of the button to use for submission.
     */
    public abstract void submit(String buttonName);

    
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
    public abstract void submit(String buttonName, String buttonValue);

    /**
     * Reset the Dialog for the next test.  This is not reset Form.
     */
    public abstract void reset();

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    public abstract void resetForm();
    
    /**
     * Return true if a link is present in the current response containing the specified text (note that HttpUnit uses
     * contains rather than an exact match - if this is a problem consider using ids on the links to uniquely identify
     * them).
     * 
     * @param linkText
     *            text to check for in links on the response.
     */
    public abstract boolean isLinkPresentWithText(String linkText);

    /**
     * Return true if a link is present in the current response containing the specified text (note that HttpUnit uses
     * contains rather than an exact match - if this is a problem consider using ids on the links to uniquely identify
     * them).
     * 
     * @param linkText
     *            text to check for in links on the response.
     * @param index
     *            The 0-based index, when more than one link with the same text is expected.
     */
    public abstract boolean isLinkPresentWithText(String linkText, int index);

    
    
    /**
     * Return true if a link is present in the current response containing the Exact specified text.
     * Note. This will call String.trim() to trim all leading / trailing spaces.
     * 
     * RFE 996031...
     * 
     * @param linkText
     *            text to check for in links on the response.
     */
    public abstract boolean isLinkPresentWithExactText(String linkText);

    /**
     * Return true if a link is present in the current response containing the Exact specified text.
     * Note. This will call String.trim() to trim all leading / trailing spaces.
     * 
     * RFE 996031...
     * 
     * @param linkText
     *            text to check for in links on the response.
     * @param index
     *            The 0-based index, when more than one link with the same text is expected.
     */
    public abstract boolean isLinkPresentWithExactText(String linkText, int index);
    
    /**
     * Return true if a link is present with a given image based on filename of image.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    public abstract boolean isLinkPresentWithImage(String imageFileName);

    /**
     * Return true if a link is present in the current response with the specified id.
     * 
     * @param anId
     *            link id to check for.
     */
    public abstract boolean isLinkPresent(String anId);

    /**
     * Determines if the checkbox is selected.
     * @param checkBoxName
     */    
    public abstract boolean isCheckboxSelected(String checkBoxName);
    
    /**
     * Determines if the checkbox is not selected.
     * @param checkBoxName
     */
    public abstract boolean isCheckboxNotSelected(String checkBoxName);
    
    /**
     * Select a specified checkbox.  If the checkbox is already checked then the checkbox
     * will stay checked.
     * @param checkBoxName name of checkbox to be deselected.
     */
    public abstract void checkCheckbox(String checkBoxName);

    public abstract void checkCheckbox(String checkBoxName, String value);

    /**
     * Deselect a specified checkbox.  If the checkbox is already unchecked then the checkbox
     * will stay unchecked.
     * @param checkBoxName name of checkbox to be deselected.
     */
    public abstract void uncheckCheckbox(String checkBoxName);

    public abstract void uncheckCheckbox(String checkBoxName, String value);
    
    /**
     * Navigate by submitting a request based on a link containing the specified text. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param linkText
     *            text which link to be navigated should contain.
     */
    public abstract void clickLinkWithText(String linkText);

    public abstract void clickLinkWithText(String linkText, int index);

    /**
     * Navigate by submitting a request based on a link with the exact specified text. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param linkText
     *            exact text which link to be navigated should contain.
     */
    public abstract void clickLinkWithExactText(String linkText);

    public abstract void clickLinkWithExactText(String linkText, int index);
    
    
    public abstract void clickLinkWithTextAfterText(String linkText,
            String labelText);

    /**
     * Navigate by submitting a request based on a link with a given ID. A RuntimeException is thrown if no such link
     * can be found.
     * 
     * @param anID
     *            id of link to be navigated.
     */
    public abstract void clickLink(String anID);

    /**
     * Navigate by submitting a request based on a link with a given image file name. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    public abstract void clickLinkWithImage(String imageFileName);

    /**
     * Click the indicated button (input type=button).
     * 
     * @param buttonId
     */
    public abstract void clickButton(String buttonId);

    /**
     * Clicks a radio option.  Asserts that the radio option exists first.	 
     * 
     * * @param radioGroup
	 *			name of the radio group.
	 * @param radioOption
	 * 			value of the option to check for.
     */    
    public abstract void clickRadioOption(String radioGroup, String radioOption);
    
    /**
     * Return true if a radio group contains the indicated option.
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOption
     *            value of the option to check for.
     */
    public abstract boolean hasRadioOption(String radioGroup, String radioOption);

    /**
     * Return a string array of select box option labels.
     * 
     * @param selectName
     *            name of the select box.
     */
    public abstract String[] getOptionsFor(String selectName);

    /**
     * Return a string array of select box option values.
     * 
     * @param selectName
     *            name of the select box.
     */
    public abstract String[] getOptionValuesFor(String selectName);

    /**
     * Return the label of the currently selected item in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    public abstract String getSelectedOption(String selectName);

    /**
     * Get the value for a given option of a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @param option
     *            label of the option.
     */
    public abstract String getValueForOption(String selectName, String option);

    /**
     * Select an option of a select box by display label.
     * 
     * @param selectName
     *            name of the select box.
     * @param option
     *            label of the option to select.
     */
    public abstract void selectOption(String selectName, String option);

    /**
     * Return true if a select box has the given option (by label).
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     * @return
     */
    public abstract boolean hasSelectOption(String selectName, String optionLabel);

    
    /**
     * Return the org.w3c.dom.Element in the current response by id.
     * 
     * @param anID
     *            id of the element.
     */
    public abstract Element getElement(String anID);

    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param element
     *            org.w3c.com.Element to inspect.
     * @param text
     *            text to check for.
     */
    public abstract boolean isTextInElement(Element element, String text);

    /**
     * Make the window with the given name in the current conversation active.
     * 
     * @param windowName
     */
    public abstract void gotoWindow(String windowName);

    /**
     * Goto first window with the given title.
     * 
     * @param windowName
     */
    public abstract void gotoWindowByTitle(String title);    
    
    /**
     * Make the root window in the current conversation active.
     */
    public abstract void gotoRootWindow();

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     */
    public abstract void gotoFrame(String frameName);

    /**
     * Return the response for the given frame in the current conversation.
     * 
     * @param frameName
     */
    public abstract WebResponse getFrame(String frameName);

    /**
     * Patch sumbitted by Alex Chaffee.
     */
    public abstract void gotoPage(String url) throws TestingEngineResponseException;

    /**
     * Dumps out all the cookies in the response received. The output is written to the passed in Stream
     * 
     * @return void
     */
    public abstract void dumpCookies(PrintStream stream);

    /**
     * Dump html of current response to System.out - for debugging purposes.
     *
     * @param stream
     */
    public abstract void dumpResponse();

    /**
     * Dump html of current response to a specified stream - for debugging purposes.
     *
     * @param stream
     */
    public abstract void dumpResponse(PrintStream stream);

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param stream
     */
    public abstract void dumpTable(String tableNameOrId, PrintStream stream);

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param table
     */
    public abstract void dumpTable(String tableNameOrId, String[][] table);

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param table
     * @param stream
     */
    public abstract void dumpTable(String tableNameOrId, String[][] table,
            PrintStream stream);
}