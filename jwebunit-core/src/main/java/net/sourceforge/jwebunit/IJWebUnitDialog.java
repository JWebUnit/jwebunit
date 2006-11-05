/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit;

import java.net.URL;
import java.util.List;

import javax.servlet.http.Cookie;

import net.sourceforge.jwebunit.exception.ElementNotFoundException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.SelectOption;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.locator.ClickableHtmlElementLocator;
import net.sourceforge.jwebunit.locator.FrameLocator;
import net.sourceforge.jwebunit.locator.HtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlOptionLocator;
import net.sourceforge.jwebunit.locator.HtmlSelectLocator;
import net.sourceforge.jwebunit.locator.HtmlTableLocator;
import net.sourceforge.jwebunit.locator.HtmlTextAreaLocator;
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
     * Get all cookies in current session.
     * 
     */
    List<Cookie> getCookies();

    /**
     * Test if a window is present.
     */
    boolean hasWindow(WindowLocator window);

    /**
     * Make the given window active.
     * Reset the current frame.
     * 
     * @param window
     *            Locator of the window
     */
    void gotoWindow(WindowLocator window);

    /**
     * Make the root window active.
     * Reset the current frame.
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
     * @param frame
     *            a frame locator.
     */
    boolean hasFrame(FrameLocator frame);

    /**
     * Make the frame with the given name active in the current window.
     * 
     * @param frame
     *            a frame locator.
     */
    void gotoFrame(FrameLocator frame);

    /**
     * Count number of frame in current context.
     * 
     */
    int getFrameCount();

    /**
     * Get the current value of a html element's attribut.
     * 
     * @param htmlElement
     *            a locator.
     * @param attribut
     *            name of the attribut (e.g. value, alt, width)
     * @return current value of a html element's attribut.
     */
    String getAttributeValue(HtmlElementLocator htmlElement, String attribut)
            throws ElementNotFoundException;

    /**
     * Set the value of a html element's attribut.
     * 
     * @param htmlElement
     *            a locator.
     * @param attribut
     *            name of the attribut (e.g. value, alt, width)
     * @param value
     *            value of the html element's attribut.
     */
    void setAttributeValue(HtmlElementLocator htmlElement, String attribut,
            String value) throws ElementNotFoundException;

    /**
     * Get the textual representation of element childs.
     * 
     * @param htmlElement
     *            a locator.
     * @return current text content of an element.
     */
    String getText(HtmlElementLocator htmlElement)
            throws ElementNotFoundException;

    /**
     * Set the text content of a textarea.
     * 
     * @param textArea
     *            a textarea locator.
     * @param value
     *            the value to set
     */
    void setTextArea(HtmlTextAreaLocator textArea, String value)
            throws ElementNotFoundException;

    /**
     * Count how many elements match the given locator.
     * 
     * @param htmlElement
     *            a locator.
     * @return element count.
     */
    int getCount(HtmlElementLocator htmlElement);

    /**
     * Return a string array of select box option values.
     * 
     * Exemple: <br/>
     * 
     * <pre>
     *              &lt;FORM action=&quot;http://my_host/doit&quot; method=&quot;post&quot;&gt;
     *                &lt;P&gt;
     *                  &lt;SELECT multiple size=&quot;4&quot; name=&quot;component-select&quot;&gt;
     *                    &lt;OPTION selected value=&quot;Component_1_a&quot;&gt;Component_1&lt;/OPTION&gt;
     *                    &lt;OPTION selected value=&quot;Component_1_b&quot;&gt;Component_2&lt;/OPTION&gt;
     *                    &lt;OPTION&gt;Component_3&lt;/OPTION&gt;
     *                    &lt;OPTION&gt;Component_4&lt;/OPTION&gt;
     *                    &lt;OPTION&gt;Component_5&lt;/OPTION&gt;
     *                  &lt;/SELECT&gt;
     *                  &lt;INPUT type=&quot;submit&quot; value=&quot;Send&quot;&gt;&lt;INPUT type=&quot;reset&quot;&gt;
     *                &lt;/P&gt;
     *              &lt;/FORM&gt;
     * </pre>
     * 
     * Should return [Component_1_a, Component_1_b, Component_3, Component_4,
     * Component_5]
     * 
     * @param selectName
     *            name of the select box.
     */
    SelectOption[] getSelectOptions(HtmlSelectLocator htmlSelect)
            throws ElementNotFoundException;

    /**
     * Return the values of the currently selected items in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    SelectOption[] getSelectedOptions(HtmlSelectLocator htmlSelect)
            throws ElementNotFoundException;

    /**
     * Select option(s) of a select box. If multi-select is enabled, you can
     * specify more than one option. Options are selected in the given order.
     * 
     * @param options
     *            option(s) to select.
     */
    void selectOptions(List<HtmlOptionLocator> options)
            throws ElementNotFoundException;

    /**
     * Unselect option(s) of a select box. If multi-select is enabled, you can
     * specify more than one option. Options are unselected in the given order.
     * 
     * @param options
     *            option(s) to unselect.
     */
    void unselectOptions(List<HtmlOptionLocator> options)
            throws ElementNotFoundException;

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
     * Return the response from the server (usually header and html source).
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
    Table getTable(HtmlTableLocator table)
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException;

    /**
     * Click html element.
     * 
     * @param htmlElement
     *            html element locator.
     */
    void clickElement(ClickableHtmlElementLocator htmlElement)
            throws ElementNotFoundException;

}
