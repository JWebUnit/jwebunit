/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit;

import java.net.URL;

import net.sourceforge.jwebunit.exception.ElementNotFoundException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.SelectOption;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.locator.FormLocator;
import net.sourceforge.jwebunit.locator.FrameLocator;
import net.sourceforge.jwebunit.locator.HtmlCheckboxLocator;
import net.sourceforge.jwebunit.locator.HtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlOptionLocator;
import net.sourceforge.jwebunit.locator.HtmlSelectLocator;
import net.sourceforge.jwebunit.locator.HtmlTableLocator;
import net.sourceforge.jwebunit.locator.WindowLocator;

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
     * @deprecated use {@link beginAt(URL, TestContext)}
     */
    void beginAt(String aInitialURL, TestContext aTestContext)
            throws TestingEngineResponseException;

    /**
     * Open the browser at an initial URL.
     * 
     * @param url
     *            Initial URL
     * @param aTestContext
     *            Test context
     */
    void beginAt(URL url, TestContext aTestContext)
            throws TestingEngineResponseException;

    /**
     * Close the browser.
     */
    void closeBrowser() throws TestingEngineResponseException;

    /**
     * Simulate user typing a new URL in the browser.
     * 
     * @deprecated use {@link gotoPage(URL)}
     */
    void gotoPage(String url) throws TestingEngineResponseException;

    /**
     * Simulate user typing a new URL in the browser.
     */
    void gotoPage(URL url) throws TestingEngineResponseException;

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
     * @return array with 2 columns. First is cookie names, second is cookie
     *         values.
     */
    String[][] getCookies();

    /**
     * Test if the window with the given name is present.
     * 
     * @param windowName
     * @deprecated use {@link hasWindow(WindowLocator)}
     */
    boolean hasWindow(String windowName);

    /**
     * Test if window with the given title is present.
     * @deprecated use {@link hasWindow(WindowLocator)}
     */
    boolean hasWindowByTitle(String title);

    /**
     * Test if a window is present.
     */
    boolean hasWindow(WindowLocator window);

    /**
     * Make the window with the given name active.
     * 
     * @param windowName
     *            Name of the window
     * @deprecated use{@link gotoWindow(WindowLocator)}
     */
    void gotoWindow(String windowName);

    /**
     * Goto first window with the given title.
     * 
     * @param title
     *            Title of the window
     * @deprecated use{@link gotoWindow(WindowLocator)}
     */
    void gotoWindowByTitle(String title);

    /**
     * Goto window with the given Javascript ID.
     * 
     * @param windowID
     *            Javascript ID of the window
     * @deprecated use{@link gotoWindow(WindowLocator)}
     */
    void gotoWindow(int windowID);

    /**
     * Make the given window active.
     * 
     * @param window
     *            Locator of the window
     */
    void gotoWindow(WindowLocator window);

    /**
     * Make the root window active.
     */
    void gotoRootWindow();

    /**
     * Get the number of openend Windows.
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
     * @param frameName
     * @deprecated use {@link hasFrame(FrameLocator)}
     */
    boolean hasFrame(String frameName);

    /**
     * Test if the given frame is present.
     * 
     * @param frame a frame locator.
     */
    boolean hasFrame(FrameLocator frame);

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     * @deprecated use {@link gotoFrame(FrameLocator)}
     */
    void gotoFrame(String frameName);

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frame a frame locator.
     */
    void gotoFrame(FrameLocator frame);

    /**
     * Set the form on the current page that the client wishes to work with
     * explicitly by either the form name or id (match by id is attempted
     * first).
     * 
     * @param nameOrId
     *            name or id of the form to be worked with.
     * @param index
     *            The 0-based index, when more than one form with the same name
     *            is expected.
     * @deprecated use {@link setWorkingForm(FormLocator)}
     */
    void setWorkingForm(String nameOrId, int index);

    /**
     * Set the form on the current page that the client wishes to work with.
     * 
     * @param form
     *            a form locator.
     */
    void setWorkingForm(FormLocator form);

    /**
     * Return true if the current page contains at least a form.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasForm();

    /**
     * Return true if the current page contains a specific form.
     * 
     * @param nameOrID
     *            name of id of the form to check for.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasForm(String nameOrID);

    /**
     * Return true if a form input element is present on the current form.
     * 
     * @param paramName
     *            name of the input element to check for
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasFormParameterNamed(String paramName);

    /**
     * Return the current value of a form input element.
     * 
     * @param paramName
     *            name of the input element.
     * @deprecated
     */
    String getFormParameterValue(String paramName);

    /**
     * Return the current value of a text field with name <code>paramName</code>.
     * Text fields are input text, input password and textarea
     * 
     * @param paramName
     *            name of the text field element.
     * @deprecated use {@link getAttributeValue(HtmlElementLocator, String)}
     */
    String getTextFieldValue(String paramName);
    
    /**
     * Get the current value of a html element's attribut.
     * @param htmlElement a locator.
     * @param attribut name of the attribut (e.g. value, alt, width)
     * @return current value of a html element's attribut.
     */
    String getAttributeValue(HtmlElementLocator htmlElement, String attribut);

    /**
     * Return the current value of a hidden input element with name
     * <code>paramName</code>.
     * 
     * @param paramName
     *            name of the hidden input element.
     * @deprecated use {@link getAttributeValue(HtmlElementLocator, String)}
     */
    String getHiddenFieldValue(String paramName);

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
     * Fill a text, password or textarea field with the provided text.
     * 
     * @param htmlElement
     *            locator of the text, password, file or textarea element
     * @param text
     *            value to type in the field.
     */
    void setTextField(HtmlElementLocator htmlElement, String text);

    /**
     * Return a string array of select box option values.
     * 
     * Exemple: <br/>
     * 
     * <pre>
     *           &lt;FORM action=&quot;http://my_host/doit&quot; method=&quot;post&quot;&gt;
     *             &lt;P&gt;
     *               &lt;SELECT multiple size=&quot;4&quot; name=&quot;component-select&quot;&gt;
     *                 &lt;OPTION selected value=&quot;Component_1_a&quot;&gt;Component_1&lt;/OPTION&gt;
     *                 &lt;OPTION selected value=&quot;Component_1_b&quot;&gt;Component_2&lt;/OPTION&gt;
     *                 &lt;OPTION&gt;Component_3&lt;/OPTION&gt;
     *                 &lt;OPTION&gt;Component_4&lt;/OPTION&gt;
     *                 &lt;OPTION&gt;Component_5&lt;/OPTION&gt;
     *               &lt;/SELECT&gt;
     *               &lt;INPUT type=&quot;submit&quot; value=&quot;Send&quot;&gt;&lt;INPUT type=&quot;reset&quot;&gt;
     *             &lt;/P&gt;
     *           &lt;/FORM&gt;
     * </pre>
     * 
     * Should return [Component_1_a, Component_1_b, Component_3, Component_4,
     * Component_5]
     * 
     * @param selectName
     *            name of the select box.
     * @deprecated
     */
    String[] getSelectOptionValues(String selectName);
    
    /**
     * Return a string array of select box option values.
     * 
     * Exemple: <br/>
     * 
     * <pre>
     *           &lt;FORM action=&quot;http://my_host/doit&quot; method=&quot;post&quot;&gt;
     *             &lt;P&gt;
     *               &lt;SELECT multiple size=&quot;4&quot; name=&quot;component-select&quot;&gt;
     *                 &lt;OPTION selected value=&quot;Component_1_a&quot;&gt;Component_1&lt;/OPTION&gt;
     *                 &lt;OPTION selected value=&quot;Component_1_b&quot;&gt;Component_2&lt;/OPTION&gt;
     *                 &lt;OPTION&gt;Component_3&lt;/OPTION&gt;
     *                 &lt;OPTION&gt;Component_4&lt;/OPTION&gt;
     *                 &lt;OPTION&gt;Component_5&lt;/OPTION&gt;
     *               &lt;/SELECT&gt;
     *               &lt;INPUT type=&quot;submit&quot; value=&quot;Send&quot;&gt;&lt;INPUT type=&quot;reset&quot;&gt;
     *             &lt;/P&gt;
     *           &lt;/FORM&gt;
     * </pre>
     * 
     * Should return [Component_1_a, Component_1_b, Component_3, Component_4,
     * Component_5]
     * 
     * @param selectName
     *            name of the select box.
     */
    SelectOption[] getSelectOption(HtmlSelectLocator htmlSelect);

    /**
     * Return the values of the currently selected items in a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @deprecated
     */
    String[] getSelectedOptions(String selectName);

    /**
     * Return the values of the currently selected items in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    SelectOption[] getSelectedOptions(HtmlSelectLocator htmlSelect);

    /**
     * Get the label for a given option of a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionValue
     *            label of the option.
     * @deprecated
     */
    String getSelectOptionLabelForValue(String selectName, String optionValue);

    /**
     * Get the value for a given option of a select box.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     * @deprecated
     */
    String getSelectOptionValueForLabel(String selectName, String optionLabel);

    /**
     * Select option(s) of a select box by value.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionsValue
     *            values of the options to select.
     * @deprecated
     */
    void selectOptions(String selectName, String[] optionsValue);

    /**
     * Select option(s) of a select box. If multi-select is enabled,
     * you can specify more than one option. Options are selected in the given order.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionsValue
     *            values of the options to select.
     */
    void selectOptions(HtmlSelectLocator htmlSelect, HtmlOptionLocator[] options);

    /**
     * Unselect option(s) of a select box by display label.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionsValue
     *            vaules of the options to unselect.
     * @deprecated
     */
    void unselectOptions(String selectName, String[] options);
    
    /**
     * Unselect option(s) of a select box by display label.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionsValue
     *            vaules of the options to unselect.
     */
    void unselectOptions(HtmlSelectLocator htmlSelect, HtmlOptionLocator[] options);

    /**
     * Test if a select box has the given option (by label).
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     * @return true if a select box has the given option (by label).
     * @deprecated use {@hasElement(HtmlElementLocator)}
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
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasSelectOptionValue(String selectName, String optionValue);

    /**
     * Determines if the checkbox is selected.
     * 
     * @param checkBoxName
     *            name of the checkbox.
     * @return true if the checkbox is selected.
     * @deprecated
     */
    boolean isCheckboxSelected(String checkBoxName);
    
    /**
     * Determines if the checkbox is selected.
     * 
     * @param checkBoxName
     *            name of the checkbox.
     * @return true if the checkbox is selected.
     * 
     */
    boolean isCheckboxSelected(HtmlCheckboxLocator checkbox);

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be selected.
     * @deprecated use {@link clickElement(HtmlElementLocator)}
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
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void checkCheckbox(String checkBoxName, String value);

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then
     * the checkbox will stay unchecked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     * @deprecated use {@link clickElement(HtmlElementLocator)}
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
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void uncheckCheckbox(String checkBoxName, String value);

    /**
     * Clicks a radio option. Asserts that the radio option exists first.
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOptionValue
     *            value of the option to check for.
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void clickRadioOption(String radioGroup, String radioOptionValue);

    /**
     * Checks if a radio group contains the indicated option.
     * 
     * @param radioGroup
     *            name of the radio group.
     * @param radioOptionValue
     *            value of the option to check for.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasRadioOption(String radioGroup, String radioOptionValue);

    /**
     * Checks if the current form contains a submit button.
     * @deprecated use {@hasElement(HtmlElementLocator)}     * 
     */
    boolean hasSubmitButton();

    /**
     * Checks if the current form contains a specific submit button.
     * 
     * @param nameOrID
     *            name or id of the button to check for.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasSubmitButton(String nameOrID);

    /**
     * Checks if the current form contains a specific submit button.
     * 
     * @param nameOrID
     *            name of id of the button to check for.
     * @param value
     *            value of the button
     * @deprecated use {@hasElement(HtmlElementLocator)}
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
     * @deprecated use {@link clickElement(HtmlElementLocator)}
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
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void submit(String buttonName, String buttonValue);

    /**
     * Checks if the current form contains a reset button.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasResetButton();

    /**
     * Checks if the current form contains a specific reset button.
     * 
     * @param nameOrID
     *            name or id of the button to check for.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasResetButton(String nameOrID);

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
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasButtonWithText(String text);

    /**
     * Checks if a button with <code>id</code> is present.
     * 
     * @param buttonId
     *            the ID of the button.
     * @return <code>true</code> when the button with text could be found.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasButton(String buttonId);

    /**
     * Click the indicated button (input type=button ou button type=button).
     * 
     * @param buttonId
     *            the ID of the button.
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void clickButton(String buttonId);

    /**
     * Clicks a button with <code>text</code> of the value attribute.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     * @deprecated use {@link clickElement(HtmlElementLocator)}
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
     *            summary, name or id of the table.
     * @return true if table exists.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasTable(String tableSummaryNameOrId);

    /**
     * Each framework have it's own way to represent a Table. Dialogs are
     * responsible for converting to the unified jWebUnit format.
     * 
     * @param tableSummaryNameOrId
     *            summary, name or id of the table to return.
     * @return unified jWebUnit representation of a table.
     * @deprecated
     */
    Table getTable(String tableSummaryNameOrId);

    /**
     * Each framework have it's own way to represent a Table. Dialogs are
     * responsible for converting to the unified jWebUnit format.
     * 
     * @param table
     *            table locator.
     * @return unified jWebUnit representation of a table.
     */
    Table getTable(HtmlTableLocator table);

    /**
     * Return true if a link is present in the current response containing the
     * specified text.
     * 
     * @param linkText
     *            text to check for in links on the response.
     * @param index
     *            The 0-based index, when more than one link with the same text
     *            is expected.
     * @deprecated use {@hasElement(HtmlElementLocator)}
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
     * @deprecated use {@hasElement(HtmlElementLocator)}
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
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasLinkWithImage(String imageFileName, int index);

    /**
     * Return true if a link is present in the current response with the
     * specified id.
     * 
     * @param anId
     *            link id to check for.
     * @deprecated use {@hasElement(HtmlElementLocator)}
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
     * @deprecated use {@link clickElement(HtmlElementLocator)}
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
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void clickLinkWithExactText(String linkText, int index);

    /**
     * Navigate by submitting a request based on a link with a given ID. A
     * RuntimeException is thrown if no such link can be found.
     * 
     * @param anID
     *            id of link to be navigated.
     * @deprecated use {@link clickElement(HtmlElementLocator)}
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
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void clickLinkWithImage(String imageFileName, int index);

    /**
     * Test if element with given id exists.
     * 
     * @param anID
     *            id of the element.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasElement(String anID);

    /**
     * Test if element with given xpath exists.
     * 
     * @param xpath
     *            xpath of the element.
     * @deprecated use {@hasElement(HtmlElementLocator)}
     */
    boolean hasElementByXPath(String xpath);
    
    /**
     * Test if Html element exists.
     * 
     * @param htmlElement
     *            a locator for the element.
     */
    boolean hasElement(HtmlElementLocator htmlElement);

    /**
     * Click element with given xpath.
     * 
     * @param xpath
     *            xpath of the element.
     * @deprecated use {@link clickElement(HtmlElementLocator)}
     */
    void clickElementByXPath(String xpath);

    /**
     * Click html element.
     * 
     * @param htmlElement
     *            html element locator.
     */
    void clickElement(HtmlElementLocator htmlElement);

    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param elementID
     *            ID of element to inspect.
     * @param text
     *            text to check for.
     * @deprecated use {@link isTextInElement(HtmlElementLocator, String)}
     */
    boolean isTextInElement(String elementID, String text);

    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param htmlElement
     *            a locator of html element to inspect.
     * @param text
     *            text to check for.
     */
    boolean isTextInElement(HtmlElementLocator htmlElement, String text);

    /**
     * Return true if a given regexp is contained within the specified element.
     * 
     * @param elementID
     *            Id of element to inspect.
     * @param regexp
     *            regexp to match.
     * @deprecated use {@link isMatchInElement(HtmlElementLocator, String)}
     */
    boolean isMatchInElement(String elementID, String regexp);

    /**
     * Return true if a given regexp is contained within the specified element.
     * 
     * @param htmlElement
     *            a locator of html element to inspect.
     * @param regexp
     *            regexp to match.
     */
    boolean isMatchInElement(HtmlElementLocator htmlElement, String regexp);

    /**
     * When you perform an action, the dialog keep an history of each Javascript
     * alert thrown. This method get the first Javascript alert in the list and
     * remove it. With Selenium, you HAVE TO check presence of alert. If not, an
     * exception is thrown on the next action. With HtmlUnit, no exception is
     * thrown, but the list will never be cleared if you don't check alert.
     * 
     * @return Text of the alert.
     * @throws ElementNotFoundException
     *             If there is no alert in the list.
     */
    String getJavascriptAlert() throws ElementNotFoundException;
}
