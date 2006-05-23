/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;

/**
 * This is the interface for all communications between jWebUnit and the
 * specific running test engine or dialog.
 * 
 * @author Julien Henry
 * @author Nick Neuberger
 */
public interface IJWebUnitDialog {

    /**
     * Open the browser at an initial URL.
     * 
     * @param aInitialURL
     *            Initial URL
     * @param aTestContext
     *            Test context
     */
    void beginAt(String aInitialURL, TestContext aTestContext)
            throws TestingEngineResponseException;

    /**
     * Close the browser.
     */
    void closeBrowser() throws TestingEngineResponseException;

    /**
     * Simulate user typing a new URL in the browser.
     */
    void gotoPage(String url) throws TestingEngineResponseException;

    /**
     * Simulate user pressing "go back" button of the browser.
     */
    void goBack();

    /**
     * Simulate user pressing "refresh" button of the browser.
     */
    void refresh();

    /**
     * Enable or disable Javascript support
     */
    void setScriptingEnabled(boolean value);

    /**
     * Test if a cookie is present with given name.
     * 
     * @param cookieName
     *            name of the cookie.
     * @return true if the cookie is present.
     */
    boolean hasCookie(String cookieName);

    /**
     * Get cookie value.
     * 
     * @param cookieName
     *            name of the cookie.
     * @return value of the cookie.
     */
    String getCookieValue(String cookieName);

    /**
     * Get all cookies name and value.
     * 
     * @param cookieName
     *            name of the cookie.
     * @return array with 2 columns. First is cookie names, second is cookie values.
     */
    String[][] getCookies();

    /**
     * Test if the window with the given name is present.
     * 
     * @param windowName
     */
    boolean hasWindow(String windowName);

    /**
     * Test if window with the given title is present.
     */
    boolean hasWindowByTitle(String title);

    /**
     * Make the window with the given name active.
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
     * Make the root window active.
     */
    void gotoRootWindow();

    /**
     * Close the current window.
     * 
     */
    void closeWindow();

    /**
     * Test if the given frame is present.
     * 
     * @param frameName
     */
    boolean hasFrame(String frameName);

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     */
    void gotoFrame(String frameName);

    /**
     * Set the form on the current page that the client wishes to work with
     * explicitly by either the form name or id (match by id is attempted
     * first).
     * 
     * @param nameOrId
     *            name or id of the form to be worked with.
     */
    void setWorkingForm(String nameOrId);

    /**
     * Return true if the current page contains a form.
     */
    boolean hasForm();

    /**
     * Return true if the current page contains a specific form.
     * 
     * @param nameOrID
     *            name of id of the form to check for.
     */
    boolean hasForm(String nameOrID);
    
    /**
     * Return true if a form input element is present on the current
     * form.
     * 
     * @param paramName
     *            name of the input element to check for
     */
    boolean hasFormParameterNamed(String paramName);

    /**
     * Return the current value of a form input element.
     * 
     * @param paramName
     *            name of the input element.
     */
    String getFormParameterValue(String paramName);

    /**
     * Fill a text, password or textarea field with the provided text.
     * 
     * @param fieldName
     *            name of the text, password or textarea element
     * @param text
     *            value to type in the field.
     */
    void setTextField(String inputName, String text);

    /**
     * Return a string array of select box option values.
     * 
     * Exemple: <br/>
     * 
     * <pre>
     *        &lt;FORM action=&quot;http://my_host/doit&quot; method=&quot;post&quot;&gt;
     *          &lt;P&gt;
     *            &lt;SELECT multiple size=&quot;4&quot; name=&quot;component-select&quot;&gt;
     *              &lt;OPTION selected value=&quot;Component_1_a&quot;&gt;Component_1&lt;/OPTION&gt;
     *              &lt;OPTION selected value=&quot;Component_1_b&quot;&gt;Component_2&lt;/OPTION&gt;
     *              &lt;OPTION&gt;Component_3&lt;/OPTION&gt;
     *              &lt;OPTION&gt;Component_4&lt;/OPTION&gt;
     *              &lt;OPTION&gt;Component_5&lt;/OPTION&gt;
     *            &lt;/SELECT&gt;
     *            &lt;INPUT type=&quot;submit&quot; value=&quot;Send&quot;&gt;&lt;INPUT type=&quot;reset&quot;&gt;
     *          &lt;/P&gt;
     *        &lt;/FORM&gt;
     * </pre>
     * 
     * Should return [Component_1_a, Component_1_b, Component_3, Component_4,
     * Component_5]
     * 
     * @param selectName
     *            name of the select box.
     */
    String[] getSelectOptionValues(String selectName);

    /**
     * Return the values of the currently selected items in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    String[] getSelectedOptions(String selectName);

    /**
     * Get the label for a given option of a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionValue
     *            label of the option.
     */
    String getSelectOptionLabelForValue(String selectName, String optionValue);

    /**
     * Get the value for a given option of a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     */
    String getSelectOptionValueForLabel(String selectName, String optionLabel);

    /**
     * Select option(s) of a select box by value.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionsValue
     *            values of the options to select.
     */
    void selectOptions(String selectName, String[] optionsValue);

    /**
     * Unselect option(s) of a select box by display label.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionsValue
     *            vaules of the options to unselect.
     */
    void unselectOptions(String selectName, String[] options);

    /**
     * Test if a select box has the given option (by label).
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     * @return true if a select box has the given option (by label).
     */
    boolean hasSelectOption(String selectName, String optionLabel);

    /**
     * Test if a select box has the given option (by value).
     * 
     * @param selectName
     *            name of the select box.
     * @param optionValue
     *            value of the option.
     * @return true if a select box has the given option (by value).
     */
    boolean hasSelectOptionValue(String selectName, String optionValue);

    /**
     * Determines if the checkbox is selected.
     * 
     * @param checkBoxName
     *            name of the checkbox.
     * @return true if the checkbox is selected.
     */
    boolean isCheckboxSelected(String checkBoxName);

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be selected.
     */
    void checkCheckbox(String checkBoxName);

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be selected.
     * @param value
     *            value of the checkbox (to differenciate checkboxes with the
     *            same name).
     */
    void checkCheckbox(String checkBoxName, String value);

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then
     * the checkbox will stay unchecked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     */
    void uncheckCheckbox(String checkBoxName);

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then
     * the checkbox will stay unchecked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     * @param value
     *            value of the checkbox (to differenciate checkboxes with the
     *            same name).
     */
    void uncheckCheckbox(String checkBoxName, String value);

    /**
     * Clicks a radio option. Asserts that the radio option exists first.
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOptionValue
     *            value of the option to check for.
     */
    void clickRadioOption(String radioGroup, String radioOptionValue);

    /**
     * Checks if a radio group contains the indicated option.
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOptionValue
     *            value of the option to check for.
     */
    boolean hasRadioOption(String radioGroup, String radioOptionValue);

    /**
     * Checks if the current form contains a specific submit button.
     * 
     * @param nameOrID
     *            name of id of the button to check for.
     */
    boolean hasSubmitButton(String nameOrID);

    /**
     * Checks if the current form contains a specific submit button.
     * 
     * @param nameOrID
     *            name of id of the button to check for.
     * @param value
     *            value of the button
     */
    boolean hasSubmitButton(String nameOrID, String value);

    /**
     * Submit the current form with the default submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    void submit();

    /**
     * Submit the current form with the specifed submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     * 
     * @param buttonName
     *            name of the button to use for submission.
     */
    void submit(String buttonName);

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
    void submit(String buttonName, String buttonValue);

    /**
     * Reset the current form with the default reset button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    void reset();

    /**
     * Checks if a button with <code>text</code> is present.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     * @return <code>true</code> when the button with text could be found.
     */
    boolean hasButtonWithText(String text);

    /**
     * Checks if a button with <code>id</code> is present.
     * 
     * @param buttonId
     *            the ID of the button.
     * @return <code>true</code> when the button with text could be found.
     */
    boolean hasButton(String buttonId);

    /**
     * Click the indicated button (input type=button ou button type=button).
     * 
     * @param buttonId the ID of the button.
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
     * Return the string representation of the current page, encoded as
     * specified by the current {@link net.sourceforge.jwebunit.TestContext}.
     */
    String getPageText();

    /**
     * Return the source of the current page (like in a browser).
     */
    String getPageSource();

    /**
     * Return the page title of the current response page, encoded as specified
     * by the current {@link net.sourceforge.jwebunit.TestContext}.
     */
    String getPageTitle();

    /**
     * Return the response of the server.
     */
    String getServerResponse();

    /**
     * Check if the Table object representing a specified table exists.
     * 
     * @param tableSummaryNameOrId
     *            summary, name or id of the table to return.
     * @return true if table exists.
     */
    boolean hasTable(String tableSummaryNameOrId);

    /**
     * Return a array for a given table.
     * 
     * @param tableSummaryNameOrId
     *            summary or id of the table.
     */
    String[][] getTable(String tableSummaryNameOrId);

    /**
     * Return a sparse array (rows or columns without displayable text are
     * removed) for a given table in the response.
     * 
     * @param tableSummaryNameOrId
     *            summary or id of the table.
     */
    String[][] getSparseTable(String tableSummaryNameOrId);

    /**
     * Return true if a link is present in the current response containing the
     * specified text.
     * 
     * @param linkText
     *            text to check for in links on the response.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    boolean hasLinkWithText(String linkText, int index);

    /**
     * Return true if a link is present in the current page containing the exact
     * specified text. Note. This will call String.trim() to trim all leading /
     * trailing spaces.
     * 
     * RFE 996031...
     * 
     * @param linkText
     *            text to check for in links on the response.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    boolean hasLinkWithExactText(String linkText, int index);

    /**
     * Return true if a link is present with a given image based on filename of
     * image.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match
     *            <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    boolean hasLinkWithImage(String imageFileName, int index);

    /**
     * Return true if a link is present in the current response with the
     * specified id.
     * 
     * @param anId
     *            link id to check for.
     */
    boolean hasLink(String anId);

    /**
     * Navigate by submitting a request based on a link containing the specified
     * text. A RuntimeException is thrown if no such link can be found.
     * 
     * @param linkText
     *            text which link to be navigated should contain.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    void clickLinkWithText(String linkText, int index);

    /**
     * Navigate by clicking a link with the exact specified text. A
     * RuntimeException is thrown if no such link can be found.
     * 
     * @param linkText
     *            exact text which link to be navigated should contain.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    void clickLinkWithExactText(String linkText, int index);

    /**
     * Navigate by submitting a request based on a link with a given ID. A
     * RuntimeException is thrown if no such link can be found.
     * 
     * @param anID
     *            id of link to be navigated.
     */
    void clickLink(String anID);

    /**
     * Navigate by submitting a request based on a link with a given image file
     * name. A RuntimeException is thrown if no such link can be found.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match
     *            <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    void clickLinkWithImage(String imageFileName, int index);

    /**
     * Test if element with given id exists.
     * 
     * @param anID
     *            id of the element.
     */
    boolean hasElement(String anID);

    /**
     * Test if element with given xpath exists.
     * 
     * @param xpath
     *            xpath of the element.
     */
    boolean hasElementByXPath(String xpath);
    
    /**
     * Click element with given xpath.
     * 
     * @param xpath
     *            xpath of the element.
     */
    void clickElementByXPath(String xpath);

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

    // *****************************************************************
    // DEPRECATED OR NOT EASY TO IMPLEMENT (TO REMOVE)
    // *****************************************************************
    /**
     * Get the name of the first input element appearing before a text label.
     * 
     * @param formElementLabel
     *            the input element label
     * @return the 'name' attribute of the first input element preceding the
     *         text
     * @deprecated label is not a good way to locate elements
     */
    String getFormElementNameBeforeLabel(String formElementLabel);

    /**
     * Get the vale of the first input element appearing before a text label.
     * 
     * @param formElementLabel
     *            the input element label
     * @return the 'value' attribute of the first input element preceding the
     *         text
     * @deprecated label is not a good way to locate elements
     */
    String getFormElementValueBeforeLabel(String formElementLabel);

    /**
     * Get the value of the first input element appearing after a text label.
     * 
     * @param formElementLabel
     *            the input element label
     * @return the 'name' attribute of the first input element preceding the
     *         text
     * @deprecated label is not a good way to locate elements
     */
    String getFormElementValueForLabel(String formElementLabel);


    /**
     * Set a form parameter / input element to the provided value.
     * 
     * @param paramName
     *            name of the input element
     * @param paramValue
     *            parameter value to submit for the element.
     * @deprecated use setTextField, checkCheckBox, ...
     */
    void setFormParameter(String paramName, String paramValue);

    /**
     * Return true if a form parameter (input element) is present on the current
     * response preceded by a given label.
     * 
     * @param paramLabel
     *            label of the input element to check for
     * @deprecated label is not a good way to locate elements
     */
    boolean hasFormParameterLabeled(String paramLabel);

    /**
     * Return the name of a form parameter (input element) on the current
     * response preceded by a givel label.
     * 
     * @param formElementLabel
     *            label of the input element to fetch name.
     * @deprecated label is not a good way to locate elements
     */
    String getFormElementNameForLabel(String formElementLabel);
    
    /**
     * 
     * @param linkText
     * @param labelText
     * @deprecated this is not a good way to locate elements
     */
    void clickLinkWithTextAfterText(String linkText, String labelText);

}