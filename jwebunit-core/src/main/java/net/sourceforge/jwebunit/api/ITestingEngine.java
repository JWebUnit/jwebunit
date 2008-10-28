/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.api;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import net.sourceforge.jwebunit.exception.ExpectedJavascriptAlertException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptConfirmException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptPromptException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.javascript.JavascriptAlert;
import net.sourceforge.jwebunit.javascript.JavascriptConfirm;
import net.sourceforge.jwebunit.javascript.JavascriptPrompt;
import net.sourceforge.jwebunit.util.TestContext;
import net.sourceforge.jwebunit.api.IElement;

/**
 * This is the interface for all communications between JWebUnit and the specific running test engine.
 * 
 * @author Julien Henry
 * @author Nick Neuberger
 */
public interface ITestingEngine {

    /**
     * Open the browser at an initial URL.
     * 
     * @param aInitialURL Initial URL
     * @param aTestContext Test context
     * @throws TestingEngineResponseException If something bad happend (404)
     */
    void beginAt(URL aInitialURL, TestContext aTestContext)
            throws TestingEngineResponseException;

    /**
     * Close the browser and check if there is no pending Javascript alert, confirm or prompt.
     * 
     * @throws ExpectedJavascriptAlertException If there is pending Javascript alert
     *             {@link ITestingEngine#setExpectedJavaScriptAlert(JavascriptAlert[])}
     * @throws ExpectedJavascriptConfirmException If there is pending Javascript confirm
     *             {@link ITestingEngine#setExpectedJavaScriptConfirm(JavascriptConfirm[])}
     * @throws ExpectedJavascriptPromptException If there is pending Javascript prompt
     *             {@link ITestingEngine#setExpectedJavaScriptPrompt(JavascriptPrompt[])}
     */
    void closeBrowser() throws ExpectedJavascriptAlertException,
            ExpectedJavascriptConfirmException,
            ExpectedJavascriptPromptException;

    /**
     * Simulate user typing a new URL in the browser.
     * 
     * @param url Full URL of the page.
     * @throws TestingEngineResponseException If something bad happend (404)
     */
    void gotoPage(URL url) throws TestingEngineResponseException;
    
    /**
     * Enable or disable Javascript support.
     * 
     * @param value true to enable Javascript.
     */
    void setScriptingEnabled(boolean value);

    /**
     * Get all cookies.
     * 
     * @return List of javax.servlet.http.Cookie.
     */
    List getCookies();

    /**
     * Test if the window with the given name is present.
     * 
     * @param windowName Name of the window.
     * @return true if the Window exists.
     */
    boolean hasWindow(String windowName);

    /**
     * Test if window with the given title is present.
     * 
     * @param windowTitle Title of the window.
     * @return true if the Window exists.
     */
    boolean hasWindowByTitle(String windowTitle);

    /**
     * Make the window with the given name active.
     * 
     * @param windowName Name of the window
     */
    void gotoWindow(String windowName);

    /**
     * Goto first window with the given title.
     * 
     * @param title Title of the window
     */
    void gotoWindowByTitle(String title);

    /**
     * Goto window with the given Javascript ID.
     * 
     * @param windowID Javascript ID of the window
     */
    void gotoWindow(int windowID);

    /**
     * Make the root window active.
     */
    void gotoRootWindow();

    /**
     * Get the number of openend Windows.
     * 
     * @return Number of openend Windows.
     */
    int getWindowCount();

    /**
     * Close the current window.
     * 
     */
    void closeWindow();

    /**
     * Test if the given frame is present.
     * 
     * @param frameNameOrId Name or ID of the frame. ID is checked first.
     * @return true if the frame exists.
     */
    boolean hasFrame(String frameNameOrId);

    /**
     * Make the frame with the given name or ID active in the current conversation.
     * 
     * @param frameNameOrId Name or ID of the frame. ID is checked first.
     */
    void gotoFrame(String frameNameOrId);

    /**
     * Set the form on the current page that the client wishes to work with explicitly by index in the page.
     * 
     * @param index The 0-based index, when more than one form with the same name is expected.
     */
    void setWorkingForm(int index);

    /**
     * Set the form on the current page that the client wishes to work with explicitly by either the form name or id
     * (match by id is attempted first).
     * 
     * @param nameOrId name or id of the form to be worked with.
     * @param index The 0-based index, when more than one form with the same name is expected.
     */
    void setWorkingForm(String nameOrId, int index);

    /**
     * Check whether the current page contains a form.
     * 
     * @return true if there is at least a form.
     */
    boolean hasForm();

    /**
     * Return true if the current page contains a specific form.
     * 
     * @param nameOrID name of id of the form to check for.
     * @return true if there is at least a form.
     */
    boolean hasForm(String nameOrID);

    /**
     * Return true if a form input element is present on the current form.
     * 
     * @param paramName name of the input element to check for
     * @return true if there is at least a form parameter.
     */
    boolean hasFormParameterNamed(String paramName);

    /**
     * Return the current value of a text field with name <code>paramName</code>. Text fields are input text, input
     * password and textarea
     * 
     * @param paramName name of the text field element.
     * @return Text content of the text field.
     */
    String getTextFieldValue(String paramName);

    /**
     * Return the current value of a hidden input element with name <code>paramName</code>.
     * 
     * @param paramName name of the hidden input element.
     * @return Value of the hidden input.
     */
    String getHiddenFieldValue(String paramName);

    /**
     * Fill a text, password or textarea field with the provided text.
     * 
     * @param inputName name of the text, password or textarea element
     * @param text value to type in the field.
     */
    void setTextField(String inputName, String text);
    
    /**
     * Fill hidden field with the provided text.
     * 
     * @param inputName name of the hidden element
     * @param text value to set in the hidden field.
     */
    void setHiddenField(String inputName, String text);

    /**
     * Return a string array of select box option values.
     * 
     * Exemple: <br/>
     * 
     * <code>
     *     <FORM action="http://my_host/doit" method="post">
     *         <P>
     *             <SELECT multiple size="4" name="component-select">
     *                 <OPTION selected value="Component_1_a">Component_1</OPTION>
     *                 <OPTION selected value="Component_1_b">Component_2</OPTION>
     *                 <OPTION>Component_3</OPTION>
     *                 <OPTION>Component_4</OPTION>
     *                 <OPTION>Component_5</OPTION>
     *              </SELECT><BR/>
     *              <INPUT type="submit" value="Send">
     *              <INPUT type="reset">
     *         </P>
     *     </FORM>
     * </code>
     * 
     * Should return [Component_1_a, Component_1_b, Component_3, Component_4, Component_5]
     * 
     * @param selectName name of the select box.
     * @return Array of select options values.
     */
    String[] getSelectOptionValues(String selectName);
    
    /**
     * Return a string array of option values for the Nth select box
     * with the specified name.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index used when more than one select with
     * the same name is expected.
     * @return Array of select options values.
     */
    String[] getSelectOptionValues(String selectName, int index);

    /**
     * Return the values of the currently selected items in a select box.
     * 
     * @param selectName name of the select box.
     */
    String[] getSelectedOptions(String selectName);

    /**
     * Return the values of the currently selected items in the Nth select box
     * with the provided name.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index used when more than one select with
     * the same name is expected.
     */
    String[] getSelectedOptions(String selectName, int index);

    
    /**
     * Get the label for a given option of a select box.
     * 
     * @param selectName name of the select box.
     * @param optionValue label of the option.
     */
    String getSelectOptionLabelForValue(String selectName, String optionValue);

    /**
     * Get the label for a given option of the Nth select box with the
     * specified name.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index used when more than one select with
     * the same name is expected.
     * @param optionValue label of the option.
     */
    String getSelectOptionLabelForValue(String selectName, int index, String optionValue);

    
    /**
     * Get the value for a given option of a select box.
     * 
     * @param selectName name of the select box.
     * @param optionLabel label of the option.
     */
    String getSelectOptionValueForLabel(String selectName, String optionLabel);

    /**
     * Get the value for a given option of the Nth select box with
     * the specified name.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index used when more than one select with
     * the same name is expected.
     * @param optionLabel label of the option.
     */
    String getSelectOptionValueForLabel(String selectName, int index, String optionLabel);

    
    /**
     * Select option(s) of a select box by value.
     * 
     * @param selectName name of the select box.
     * @param optionValues values of the options to select.
     */
    void selectOptions(String selectName, String[] optionValues);

    /**
     * Select option(s) of the Nth select box by value.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionValues values of the options to select.
     */
    void selectOptions(String selectName, int index, String[] optionValues);

    
    /**
     * Unselect option(s) of a select box by value.
     * 
     * @param selectName name of the select box.
     * @param optionValues vaules of the options to unselect.
     */
    void unselectOptions(String selectName, String[] optionValues);

    /**
     * Unselect option(s) of the Nth select box with the specified name
     * by value.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionValues vaules of the options to unselect.
     */
    void unselectOptions(String selectName, int index, String[] optionValues);

    
    /**
     * Test if a select box has the given option (by label).
     * 
     * @param selectName name of the select box.
     * @param optionLabel label of the option.
     * @return true if a select box has the given option (by label).
     */
    boolean hasSelectOption(String selectName, String optionLabel);

    /**
     * Test if a select box has the given option (by value).
     * 
     * @param selectName name of the select box.
     * @param optionValue value of the option.
     * @return true if a select box has the given option (by value).
     */
    boolean hasSelectOptionValue(String selectName, String optionValue);

    /**
     * Test if the Nth select box has the given option (by label).
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionLabel label of the option.
     * @return true if a select box has the given option (by label).
     */
    boolean hasSelectOption(String selectName, int index, String optionLabel);

    /**
     * Test if the Nth select box has the given option (by value).
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionValue value of the option.
     * @return true if a select box has the given option (by value).
     */
    boolean hasSelectOptionValue(String selectName, int index, String optionValue);

    
    /**
     * Determines if the checkbox is selected.
     * 
     * @param checkBoxName name of the checkbox.
     * @return true if the first checkbox with given name is selected.
     */
    boolean isCheckboxSelected(String checkBoxName);

    /**
     * Determines if the checkbox is selected.
     * 
     * @param checkBoxName name attribut of the checkbox.
     * @param checkBoxValue value attribut of the checkbox.
     * @return true if the first checkbox with given name and value is selected.
     */
    boolean isCheckboxSelected(String checkBoxName, String checkBoxValue);

    /**
     * Select a specified checkbox. If the checkbox is already checked then the checkbox will stay checked.
     * 
     * @param checkBoxName name of checkbox to be selected.
     */
    void checkCheckbox(String checkBoxName);

    /**
     * Select a specified checkbox. If the checkbox is already checked then the checkbox will stay checked.
     * 
     * @param checkBoxName name of checkbox to be selected.
     * @param checkBoxValue value of the checkbox (to differenciate checkboxes with the same name).
     */
    void checkCheckbox(String checkBoxName, String checkBoxValue);

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then the checkbox will stay unchecked.
     * 
     * @param checkBoxName name of checkbox to be deselected.
     */
    void uncheckCheckbox(String checkBoxName);

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then the checkbox will stay unchecked.
     * 
     * @param checkBoxName name of checkbox to be deselected.
     * @param value value of the checkbox (to differenciate checkboxes with the same name).
     */
    void uncheckCheckbox(String checkBoxName, String value);

    /**
     * Clicks a radio option. Asserts that the radio option exists first.
     * 
     * @param radioGroup name of the radio group.
     * @param radioOptionValue value of the option to check for.
     */
    void clickRadioOption(String radioGroup, String radioOptionValue);

    /**
     * Checks if a radio group contains the indicated option.
     * 
     * @param radioGroup name of the radio group.
     * @param radioOptionValue value of the option to check for.
     */
    boolean hasRadioOption(String radioGroup, String radioOptionValue);
    
    /**
     * Return the currently selected radio button.
     * @param radioGroup name of the radio group.
     * @return value of the selected radio.
     */
    String getSelectedRadio(String radioGroup);

    /**
     * Checks if the current form contains a submit button.
     * 
     */
    boolean hasSubmitButton();

    /**
     * Checks if the current form contains a specific submit button.<br/> A submit button can be the following HTML
     * elements:
     * <ul>
     * <li>input type=submit
     * <li>input type=image
     * <li>button type=submit
     * </ul>
     * 
     * @param nameOrID name or id of the button to check for.
     */
    boolean hasSubmitButton(String nameOrID);

    /**
     * Checks if the current form contains a specific submit button.<br/> A submit button can be the following HTML
     * elements:
     * <ul>
     * <li>input type=submit
     * <li>input type=image
     * <li>button type=submit
     * </ul>
     * 
     * @param nameOrID name of id of the button to check for.
     * @param value value of the button
     */
    boolean hasSubmitButton(String nameOrID, String value);

    /**
     * Submit the current form with the default submit button. See {@link #getForm}for an explanation of how the
     * current form is established.<br/> A submit button can be the following HTML elements:
     * <ul>
     * <li>input type=submit
     * <li>input type=image
     * <li>button type=submit
     * </ul>
     */
    void submit();

    /**
     * Submit the current form with the specifed submit button. See {@link #getForm}for an explanation of how the
     * current form is established.<br/> A submit button can be the following HTML elements:
     * <ul>
     * <li>input type=submit
     * <li>input type=image
     * <li>button type=submit
     * </ul>
     * 
     * @param buttonName name of the button to use for submission.
     */
    void submit(String buttonName);

    /**
     * Submit the current form with the specifed submit button (by name and value). See {@link #getForm}for an
     * explanation of how the current form is established.<br/> A submit button can be the following HTML elements:
     * <ul>
     * <li>input type=submit
     * <li>input type=image
     * <li>button type=submit
     * </ul>
     * 
     * @author Dragos Manolescu
     * @param buttonName name of the button to use for submission.
     * @param buttonValue value/label of the button to use for submission
     */
    void submit(String buttonName, String buttonValue);

    /**
     * Checks if the current form contains a reset button.<br/> A reset button can be the following HTML elements:
     * <ul>
     * <li>input type=reset
     * <li>button type=reset
     * </ul>
     * 
     */
    boolean hasResetButton();

    /**
     * Checks if the current form contains a specific reset button.<br/> A reset button can be the following HTML
     * elements:
     * <ul>
     * <li>input type=reset
     * <li>button type=reset
     * </ul>
     * 
     * @param nameOrID name or id of the button to check for.
     */
    boolean hasResetButton(String nameOrID);

    /**
     * Reset the current form with the default reset button. See {@link #getForm}for an explanation of how the current
     * form is established.<br/> A reset button can be the following HTML elements:
     * <ul>
     * <li>input type=reset
     * <li>button type=reset
     * </ul>
     */
    void reset();

    /**
     * Checks if a button with <code>text</code> is present.<br/> A button can be the following HTML elements:
     * <ul>
     * <li>input type=button
     * <li>button type=button
     * </ul>
     * 
     * @param text the text of the button (contents of the value attribute).
     * @return <code>true</code> when the button with text could be found.
     */
    boolean hasButtonWithText(String text);

    /**
     * Checks if a button with <code>id</code> is present.<br/> A button can be the following HTML elements:
     * <ul>
     * <li>input type=button
     * <li>button type=button
     * </ul>
     * 
     * @param buttonId the ID of the button.
     * @return <code>true</code> when the button with text could be found.
     */
    boolean hasButton(String buttonId);

    /**
     * Click the indicated button. <br/> A button can be the following HTML elements:
     * <ul>
     * <li>input type=button
     * <li>button type=button
     * </ul>
     * 
     * @param buttonId the ID of the button.
     */
    void clickButton(String buttonId);

    /**
     * Clicks a button with <code>text</code> of the value attribute. <br/> A button can be the following HTML
     * elements:
     * <ul>
     * <li>input type=button
     * <li>button type=button
     * </ul>
     * 
     * @param buttonValueText the text of the button (contents of the value attribute).
     */
    void clickButtonWithText(String buttonValueText);

    /**
     * Get the location of the current page.
     * @return an URL.
     */
    URL getPageURL();
    
    /**
     * Return the string representation of the current page, encoded as specified by the current
     * {@link net.sourceforge.jwebunit.util.TestContext}.
     * 
     * @return Visible text in the page.
     */
    String getPageText();

    /**
     * Return the source of the current page (like in a browser).
     * 
     * @return Source of the page (or HTTP Body as String)
     */
    String getPageSource();

    /**
     * Return the page title of the current response page, encoded as specified by the current
     * {@link net.sourceforge.jwebunit.util.TestContext}.
     * 
     * @return Title of the page.
     */
    String getPageTitle();

    /**
     * Return the response of the server for the current page.
     * 
     * @return HTTP header & body
     */
    String getServerResponse();

    /**
     * Gets the last server response as input stream.
     * 
     */
    InputStream getInputStream();

    /**
     * Gets the input stream for a given URL - can be used to test images or other resources without changing the current
     * navigation context.
     * 
     * @param url the url to the resource
     */
    InputStream getInputStream(URL url)
            throws TestingEngineResponseException;

    /**
     * Check if the Table object representing a specified table exists.
     * 
     * @param tableSummaryNameOrId summary, name or id of the table.
     * @return true if table exists.
     */
    boolean hasTable(String tableSummaryNameOrId);

    /**
     * Each framework have it's own way to represent a Table. Testing engines are responsible for converting to the unified
     * JWebUnit format.
     * 
     * @param tableSummaryNameOrId summary, name or id of the table to return.
     * @return unified JWebUnit representation of a table.
     */
    Table getTable(String tableSummaryNameOrId);

    /**
     * Return true if a link is present in the current response containing the specified text.
     * 
     * @param linkText text to check for in links on the response.
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    boolean hasLinkWithText(String linkText, int index);

    /**
     * Return true if a link is present in the current page containing the exact specified text. Note. This will call
     * String.trim() to trim all leading / trailing spaces.
     * 
     * RFE 996031...
     * 
     * @param linkText text to check for in links on the response.
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    boolean hasLinkWithExactText(String linkText, int index);

    /**
     * Return true if a link is present with a given image based on filename of image.
     * 
     * @param imageFileName A suffix of the image's filename; for example, to match
     *            <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     */
    boolean hasLinkWithImage(String imageFileName, int index);

    /**
     * Return true if a link is present in the current response with the specified id.
     * 
     * @param anId link id to check for.
     */
    boolean hasLink(String anId);

    /**
     * Navigate by submitting a request based on a link containing the specified text. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param linkText text which link to be navigated should contain.
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    void clickLinkWithText(String linkText, int index);

    /**
     * Navigate by clicking a link with the exact specified text. A RuntimeException is thrown if no such link can be
     * found.
     * 
     * @param linkText exact text which link to be navigated should contain.
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    void clickLinkWithExactText(String linkText, int index);

    /**
     * Navigate by submitting a request based on a link with a given ID. A RuntimeException is thrown if no such link
     * can be found.
     * 
     * @param anID id of link to be navigated.
     */
    void clickLink(String anID);

    /**
     * Navigate by submitting a request based on a link with a given image file name. A RuntimeException is thrown if no
     * such link can be found.
     * 
     * @param imageFileName A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"</tt>,
     *            you could just pass in <tt>"my_icon.png"</tt>.
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    void clickLinkWithImage(String imageFileName, int index);

    /**
     * Test if element with given id exists.
     * 
     * @param anID id of the element.
     * @return true if element was found.
     */
    boolean hasElement(String anID);

    /**
     * Test if element with given xpath exists.
     * 
     * @param xpath xpath of the element.
     * @return true if element was found.
     */
    boolean hasElementByXPath(String xpath);

    /**
     * Click element with given xpath.
     * 
     * @param xpath xpath of the element.
     */
    void clickElementByXPath(String xpath);

    /**
     * Get attribut value of the given element. For example, if you have img src="bla.gif" alt="toto",
     * getElementAttributByXPath("//img[@src='bla.gif']", "alt") returns "toto"
     * 
     * @param xpath xpath of the element.
     * @param attribut name of the attribut.
     * @return Attribut value or null if the element is not found.
     */
    String getElementAttributByXPath(String xpath, String attribut);

    /**
     * Get text of the given element.
     * 
     * @param xpath xpath of the element.
     */
    String getElementTextByXPath(String xpath);
    
    
    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param elementID ID of element to inspect.
     * @param text text to check for.
     * @return true if text was found.
     */
    boolean isTextInElement(String elementID, String text);

    /**
     * Return true if a given regexp is contained within the specified element.
     * 
     * @param elementID Id of element to inspect.
     * @param regexp regexp to match.
     * @return true if a match is found.
     */
    boolean isMatchInElement(String elementID, String regexp);

    /**
     * Tell the testing engine that the given alert boxes are expected in the given order.
     * 
     * @param alerts Expected alerts.
     * @throws ExpectedJavascriptAlertException If there are still unconsummed alert since a previous call of this
     *             method.
     */
    void setExpectedJavaScriptAlert(JavascriptAlert[] alerts)
            throws ExpectedJavascriptAlertException;

    /**
     * Tell the testing engine that the given confirm boxes are expected in the given order.
     * 
     * @param confirms Expected confirms.
     * @throws ExpectedJavascriptConfirmException If there are still unconsummed confirm since a previous call of this
     *             method.
     */
    void setExpectedJavaScriptConfirm(JavascriptConfirm[] confirms)
            throws ExpectedJavascriptConfirmException;

    /**
     * Tell the testing engine that the given prompt boxes are expected in the given order.
     * 
     * @param prompts Expected prompts.
     * @throws ExpectedJavascriptPromptException If there are still unconsummed prompt since a previous call of this
     *             method.
     */
    void setExpectedJavaScriptPrompt(JavascriptPrompt[] prompts)
            throws ExpectedJavascriptPromptException;

	/**
	 * Get an element wrapper for a given xpath.
	 * 
	 * @see #getElementsByXPath(String)
	 * @param xpath XPath to evaluate
	 * @return The element if found
	 */
	IElement getElementByXPath(String xpath);
	
	/**
	 * Get an element wrapper for a given ID.
	 * 
	 * @param id element ID to find
	 * @return The element if found
	 */
	IElement getElementByID(String id);
	
	/**
	 * Get a list of all elements that match the given xpath.
	 * 
	 * @see #getElementByXPath(String)
	 * @param xpath XPath to evaluate
	 * @return List of all elements found
	 */
	List<IElement> getElementsByXPath(String xpath);
	
}
