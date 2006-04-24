/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit;

import java.io.PrintStream;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;

/**
 * This is the interface for all communications between jWebUnit and the specific running
 * test engine or dialog.
 * 
 * @author Julien Henry
 * @author Nick Neuberger
 */
public interface IJWebUnitDialog {

    
    /**
     * Calls the concrete dialog class to construct a new dialog instance.
     * 
     * @param aInitialURL Initial URL
     * @param aTestContext Test context
     */
	void beginAt(String aInitialURL, TestContext aTestContext) throws TestingEngineResponseException;
    
    /**
     * Test if the window with the given name is in the current conversation.
     * 
     * @param windowName
     */
    boolean isWindowPresent(String windowName);

    /**
     * Test if the first open window with the given title.
     */
    boolean isWindowByTitlePresent(String title);
    
    /**
     * Return the string representation of the current response, encoded as specified by the current
     * {@link net.sourceforge.jwebunit.TestContext}.
     */
    String getResponseText();

    /**
     * Return the page title of the current response page, encoded as specified by the current
     * {@link net.sourceforge.jwebunit.TestContext}.
     */
    String getResponsePageTitle();

    boolean hasCookie(String cookieName);

    String getCookieValue(String cookieName);

    //TODO: Move other dump methods to dialog!!
    //WebForm getForm();

    /**
     * Enable or disable Javascript support
     */
    void setScriptingEnabled(boolean value);
    
    /**
     * Set the form on the current response that the client wishes to work with explicitly by either the form name or
     * id (match by id is attempted first).
     * 
     * @param nameOrId
     *            name or id of the form to be worked with.
     */
    void setWorkingForm(String nameOrId);

    /**
     * Return true if the current response contains a form.
     */
    boolean hasForm();

    /**
     * Return true if the current response contains a specific form.
     * 
     * @param nameOrID
     *            name of id of the form to check for.
     */
    boolean hasForm(String nameOrID);

    /**
     * Return true if a form parameter (input element) is present on the current response.
     * 
     * @param paramName
     *            name of the input element to check for
     */
    boolean hasFormParameterNamed(String paramName);

    /**
     * Set a form parameter / input element to the provided value.
     * 
     * @param paramName
     *            name of the input element
     * @param paramValue
     *            parameter value to submit for the element.
     */
    void setFormParameter(String paramName, String paramValue);

    void updateFormParameter(String paramName, String paramValue);

    /**
     * Return the current value of a form input element.
     * 
     * @param paramName
     *            name of the input element.
     */
    String getFormParameterValue(String paramName);

    /**
     * Specify that no parameter value should be submitted for a given input element. Typically used to uncheck check
     * boxes.
     * 
     * @param paramName
     *            name of the input element.
     */
    void removeFormParameter(String paramName);

    void removeFormParameterWithValue(String paramName,
            String value);

    /**
     * Return true if a form parameter (input element) is present on the current response preceded by a given label.
     * 
     * @param paramLabel
     *            label of the input element to check for
     */
    boolean hasFormParameterLabeled(String paramLabel);

    /**
     * Return the name of a form parameter (input element) on the current response preceded by a givel label.
     * 
     * @param formElementLabel
     *            label of the input element to fetch name.
     */
    String getFormElementNameForLabel(String formElementLabel);

    String getSubmitButtonValue(String buttonName);

    boolean hasSubmitButton(String buttonName);

    boolean hasSubmitButton(String buttonName, String buttonValue);

    /**
     * Checks if a button with <code>text</code> is present.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     * @return <code>true</code> when the button with text could be found.
     */
    boolean hasButtonWithText(String text);

    boolean hasButton(String buttonId);

    /**
     * Return true if given text is present anywhere in the current response.
     * 
     * @param text
     *            string to check for.
     */
    boolean isTextInResponse(String text);

    /**
     * Return true if given regexp has a match anywhere in the current response.
     * 
     * @param regexp
     *            regexp to match.
     */
    boolean isMatchInResponse(String regexp);

    /**
     * Return true if given text is present in a specified table of the response.
     * 
     * @param tableSummaryOrId
     *            table summary or id to inspect for expected text.
     * @param text
     *            expected text to check for.
     */
    boolean isTextInTable(String tableSummaryOrId, String text);

    /**
     * Return true if given regexp has a match in a specified table of the response.
     * 
     * @param tableSummaryOrId
     *            table summary or id to inspect for expected text.
     * @param regexp
     *            regexp to match.
     */
    boolean isMatchInTable(String tableSummaryOrId, String regexp);

    /**
     * Test if the Table object representing a specified table in the current response. Null is returned if
     * a parsing exception occurs looking for the table or no table with the id or summary could be found.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table to return.
     */
    boolean isWebTableBySummaryOrIdPresent(String tableSummaryOrId);

    /**
     * Return a array for a given table.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table.
     */
    String[][] getTableBySummaryOrId(String tableSummaryOrId);

    /**
     * Return a sparse array (rows or columns without displayable text are removed) for a given table in the response.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table.
     */
    String[][] getSparseTableBySummaryOrId(String tableSummaryOrId);

    /**
     * Submit the current form with the default submit button. See {@link #getForm}for an explanation of how the
     * current form is established.
     */
    void submit();

    /**
     * Submit the current form with the specifed submit button. See {@link #getForm}for an explanation of how the
     * current form is established.
     * 
     * @param buttonName
     *            name of the button to use for submission.
     */
    void submit(String buttonName);

    
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
    void submit(String buttonName, String buttonValue);

    /**
     * Reset the Dialog for the next test.  This is not reset Form.
     */
    void reset() throws TestingEngineResponseException;

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    void resetForm();
    
    /**
     * Return true if a link is present in the current response containing the specified text (note that HttpUnit uses
     * contains rather than an exact match - if this is a problem consider using ids on the links to uniquely identify
     * them).
     * 
     * @param linkText
     *            text to check for in links on the response.
     */
    boolean isLinkPresentWithText(String linkText);

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
    boolean isLinkPresentWithText(String linkText, int index);

    
    
    /**
     * Return true if a link is present in the current response containing the Exact specified text.
     * Note. This will call String.trim() to trim all leading / trailing spaces.
     * 
     * RFE 996031...
     * 
     * @param linkText
     *            text to check for in links on the response.
     */
    boolean isLinkPresentWithExactText(String linkText);

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
    boolean isLinkPresentWithExactText(String linkText, int index);
    
    /**
     * Return true if a link is present with a given image based on filename of image.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    boolean isLinkPresentWithImage(String imageFileName);

    /**
     * Return true if a link is present in the current response with the specified id.
     * 
     * @param anId
     *            link id to check for.
     */
    boolean isLinkPresent(String anId);

    /**
     * Determines if the checkbox is selected.
     * @param checkBoxName
     */    
    boolean isCheckboxSelected(String checkBoxName);
    
    /**
     * Determines if the checkbox is not selected.
     * @param checkBoxName
     */
    boolean isCheckboxNotSelected(String checkBoxName);
    
    /**
     * Select a specified checkbox.  If the checkbox is already checked then the checkbox
     * will stay checked.
     * @param checkBoxName name of checkbox to be deselected.
     */
    void checkCheckbox(String checkBoxName);

    void checkCheckbox(String checkBoxName, String value);

    /**
     * Deselect a specified checkbox.  If the checkbox is already unchecked then the checkbox
     * will stay unchecked.
     * @param checkBoxName name of checkbox to be deselected.
     */
    void uncheckCheckbox(String checkBoxName);

    void uncheckCheckbox(String checkBoxName, String value);
    
    /**
     * Navigate by submitting a request based on a link containing the specified text. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param linkText
     *            text which link to be navigated should contain.
     */
    void clickLinkWithText(String linkText);

    void clickLinkWithText(String linkText, int index);

    /**
     * Navigate by submitting a request based on a link with the exact specified text. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param linkText
     *            exact text which link to be navigated should contain.
     */
    void clickLinkWithExactText(String linkText);

    void clickLinkWithExactText(String linkText, int index);
    
    
    void clickLinkWithTextAfterText(String linkText,
            String labelText);

    /**
     * Navigate by submitting a request based on a link with a given ID. A RuntimeException is thrown if no such link
     * can be found.
     * 
     * @param anID
     *            id of link to be navigated.
     */
    void clickLink(String anID);

    /**
     * Navigate by submitting a request based on a link with a given image file name. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    void clickLinkWithImage(String imageFileName);

    /**
     * Click the indicated button (input type=button).
     * 
     * @param buttonId
     */
    void clickButton(String buttonId);

    /**
     * Clicks a button with <code>text</code> of the value attribute.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     */
    void clickButtonWithText(String buttonValueText);

    
    
    /**
     * Clicks a radio option.  Asserts that the radio option exists first.	 
     * 
     * * @param radioGroup
	 *			name of the radio group.
	 * @param radioOption
	 * 			value of the option to check for.
     */    
    void clickRadioOption(String radioGroup, String radioOption);
    
    /**
     * Return true if a radio group contains the indicated option.
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOption
     *            value of the option to check for.
     */
    boolean hasRadioOption(String radioGroup, String radioOption);

    /**
     * Return a string array of select box option labels.
     * 
     * @param selectName
     *            name of the select box.
     */
    String[] getOptionsFor(String selectName);

    /**
     * Return a string array of select box option values.
     * 
     * @param selectName
     *            name of the select box.
     */
    String[] getOptionValuesFor(String selectName);

    /**
     * Return the label of the currently selected item in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    String getSelectedOption(String selectName);

    /**
     * Get the value for a given option of a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @param option
     *            label of the option.
     */
    String getValueForOption(String selectName, String option);

    /**
     * Select an option of a select box by display label.
     * 
     * @param selectName
     *            name of the select box.
     * @param option
     *            label of the option to select.
     */
    void selectOption(String selectName, String option);

    /**
     * Return true if a select box has the given option (by label).
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     * @return
     */
    boolean hasSelectOption(String selectName, String optionLabel);

    
    /**
     * Test if element with given id exists.
     * 
     * @param anID
     *            id of the element.
     */
    boolean isElementPresent(String anID);

    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param elementID
     *            ID of element to inspect.
     * @param text
     *            text to check for.
     */
    boolean isTextInElement(String elementID, String text);

    /**
     * Return true if a given regexp is contained within the specified element.
     * 
     * @param elementID
     *            Id of element to inspect.
     * @param regexp
     *            regexp to match.
     */
    boolean isMatchInElement(String elementID, String regexp);

    /**
     * Make the window with the given name in the current conversation active.
     * 
     * @param windowName
     */
    void gotoWindow(String windowName);

    /**
     * Goto first window with the given title.
     * 
     * @param windowName
     */
    void gotoWindowByTitle(String title);    
    
    /**
     * Make the root window in the current conversation active.
     */
    void gotoRootWindow();

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     */
    void gotoFrame(String frameName);

    /**
     * Test if the given frame is present.
     * 
     * @param frameName
     */
    boolean isFramePresent(String frameName);

    /**
     * Patch sumbitted by Alex Chaffee.
     */
    void gotoPage(String url) throws TestingEngineResponseException;

    /**
     * Dumps out all the cookies in the response received. The output is written to the passed in Stream
     * 
     * @return void
     */
    void dumpCookies(PrintStream stream);

    /**
     * Dump html of current response to System.out - for debugging purposes.
     *
     * @param stream
     */
    void dumpResponse();

    /**
     * Dump html of current response to a specified stream - for debugging purposes.
     *
     * @param stream
     */
    void dumpResponse(PrintStream stream);

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param stream
     */
    void dumpTable(String tableNameOrId, PrintStream stream);

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param table
     */
    void dumpTable(String tableNameOrId, String[][] table);

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param table
     * @param stream
     */
    void dumpTable(String tableNameOrId, String[][] table,
            PrintStream stream);

    /**
     * Get the name of the first input element appearing before a text label.
     * @param formElementLabel the input element label
     * @return the 'name' attribute of the first input element preceding the text
     */
    String getFormElementNameBeforeLabel(String formElementLabel);

    /**
     * Get the vale of the first input element appearing before a text label.
     * @param formElementLabel the input element label
     * @return the 'value' attribute of the first input element preceding the text
     */
    String getFormElementValueBeforeLabel(String formElementLabel);

    /**
     * Get the value of the first input element appearing after a text label.
     * @param formElementLabel the input element label
     * @return the 'name' attribute of the first input element preceding the text
     */
    String getFormElementValueForLabel(String formElementLabel);
}