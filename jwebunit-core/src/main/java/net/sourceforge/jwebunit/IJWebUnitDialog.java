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
import net.sourceforge.jwebunit.locator.ClickableHtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocator;
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
     * Test if a window is present.
     */
    boolean hasWindow(WindowLocator window);

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
     * @param frame a frame locator.
     */
    boolean hasFrame(FrameLocator frame);

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frame a frame locator.
     */
    void gotoFrame(FrameLocator frame);

    /**
     * Set the form on the current page that the client wishes to work with.
     * 
     * @param form
     *            a form locator.
     */
    void setWorkingForm(HtmlFormLocator form) throws ElementNotFoundException;

    /**
     * Get the current value of a html element's attribut.
     * @param htmlElement a locator.
     * @param attribut name of the attribut (e.g. value, alt, width)
     * @return current value of a html element's attribut.
     */
    String getAttributeValue(HtmlElementLocator htmlElement, String attribut) throws ElementNotFoundException;

    /**
     * Gets the text of an element. This works for any element that contains text.
     * @param htmlElement a locator.
     * @return current text content of an element.
     */
    String getText(HtmlElementLocator htmlElement) throws ElementNotFoundException;

    /**
     * Count how many elements match the given locator.
     * @param htmlElement a locator.
     * @return element count.
     */
    int getCount(HtmlElementLocator htmlElement) throws ElementNotFoundException;

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
     */
    SelectOption[] getSelectOptions(HtmlSelectLocator htmlSelect) throws net.sourceforge.jwebunit.exception.ElementNotFoundException;

    /**
     * Return the values of the currently selected items in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    SelectOption[] getSelectedOptions(HtmlSelectLocator htmlSelect) throws net.sourceforge.jwebunit.exception.ElementNotFoundException;

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
     */
    void unselectOptions(HtmlSelectLocator htmlSelect, HtmlOptionLocator[] options);

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
     * Submit the current form with the default submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    void submit();

    /**
     * Reset the current form with the default reset button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    void reset();

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
     * Each framework have it's own way to represent a Table. Dialogs are
     * responsible for converting to the unified jWebUnit format.
     * 
     * @param table
     *            table locator.
     * @return unified jWebUnit representation of a table.
     */
    Table getTable(HtmlTableLocator table) throws net.sourceforge.jwebunit.exception.ElementNotFoundException;

    /**
     * Test if Html element exists.
     * 
     * @param htmlElement
     *            a locator for the element.
     */
    boolean hasElement(HtmlElementLocator htmlElement);

    /**
     * Click html element.
     * 
     * @param htmlElement
     *            html element locator.
     */
    void clickElement(ClickableHtmlElementLocator htmlElement) throws ElementNotFoundException;

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
