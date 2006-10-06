/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.htmlunit;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.jaxen.JaxenException;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.SelectOption;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.locator.ClickableHtmlElementLocator;
import net.sourceforge.jwebunit.locator.FrameLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlFormLocator;
import net.sourceforge.jwebunit.locator.FrameLocator;
import net.sourceforge.jwebunit.locator.HtmlCheckboxLocator;
import net.sourceforge.jwebunit.locator.HtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlOptionLocator;
import net.sourceforge.jwebunit.locator.HtmlSelectLocator;
import net.sourceforge.jwebunit.locator.HtmlTableLocator;
import net.sourceforge.jwebunit.locator.WindowLocator;
import net.sourceforge.jwebunit.locator.WindowLocatorByName;
import net.sourceforge.jwebunit.util.ExceptionUtility;
import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.TestContext;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.WebWindowNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow.CellIterator;
import com.gargoylesoftware.htmlunit.html.xpath.HtmlUnitXPath;

/**
 * Acts as the wrapper for HtmlUnit access. A dialog is initialized with a given
 * URL, and maintains conversational state as the dialog progresses through link
 * navigation, form submission, etc.
 * 
 * @author Julien Henry
 * 
 */
public class HtmlUnitDialog implements IJWebUnitDialog {
    /**
     * Logger for this class.
     */
    private static final Log LOGGER = LogFactory.getLog(HtmlUnitDialog.class);

    /**
     * Encapsulate browser abilities.
     */
    private WebClient wc;

    /**
     * The currently selected window.
     */
    private WebWindow win;

    /**
     * A ref to the test context.
     */
    private TestContext testContext;

    /**
     * The currently selected form.
     */
    private HtmlForm form;

    /**
     * Is Javascript enabled.
     */
    private boolean jsEnabled = true;

    /**
     * Javascript alerts
     */
    private LinkedList<String> javascriptAlerts = new LinkedList<String>();

    // Implementation of IJWebUnitDialog

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCount(net.sourceforge.jwebunit.locator.HtmlElementLocator)
     */
    public int getCount(HtmlElementLocator htmlElement)
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        return getElements(htmlElement).size();
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getText(net.sourceforge.jwebunit.locator.HtmlElementLocator)
     */
    public String getText(HtmlElementLocator htmlElement)
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        List<HtmlElement> l = getElements(htmlElement);
        if (l.size() < 0) {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
                    htmlElement);
        }
        return l.get(0).asText();
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#beginAt(java.net.URL,
     *      net.sourceforge.jwebunit.TestContext)
     */
    public void beginAt(URL url, TestContext context)
            throws TestingEngineResponseException {
        this.setTestContext(context);
        initWebClient();
        try {
            wc.getPage(url);
            win = wc.getCurrentWindow();
            form = null;
        } catch (FailingHttpStatusCodeException aException) {
            // cant find requested page. most browsers will return a page with
            // 404 in the body or title.
            throw new TestingEngineResponseException(ExceptionUtility
                    .stackTraceToString(aException));

        } catch (IOException aException) {
            throw new RuntimeException(ExceptionUtility
                    .stackTraceToString(aException), aException);
        }
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickElement(net.sourceforge.jwebunit.locator.HtmlElementLocator)
     */
    public void clickElement(ClickableHtmlElementLocator htmlElement)
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        List<HtmlElement> l = getElements(htmlElement);
        if (l.size() < 0) {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
                    htmlElement);
        }
        try {
            ClickableElement e = (ClickableElement) l.get(0);
            try {
                e.click();
            } catch (IOException e1) {
                throw new RuntimeException("Unexpected error: click on "
                        + htmlElement.toString() + " failed.");
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Unexpected error: "
                    + htmlElement.toString()
                    + " is not a HtmlUnit clickable element.");
        }
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getAttributeValue(net.sourceforge.jwebunit.locator.HtmlElementLocator,
     *      java.lang.String)
     */
    public String getAttributeValue(HtmlElementLocator htmlElement,
            String attribut)
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        List<HtmlElement> l = getElements(htmlElement);
        if (l.size() < 0) {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
                    htmlElement);
        }
        String value = l.get(0).getAttributeValue(attribut);
        if (value.equals("ATTRIBUTE_NOT_DEFINED")) {
            return null;
        }
        if (value.equals("ATTRIBUTE_VALUE_EMPTY")) {
            return "";
        }
        return value;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectedOptions(net.sourceforge.jwebunit.locator.HtmlSelectLocator)
     */
    public SelectOption[] getSelectedOptions(HtmlSelectLocator htmlSelect)
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        List<HtmlElement> l = getElements(htmlSelect);
        if (l.size() < 0) {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
                    htmlSelect);
        }
        try {
            HtmlSelect sel = (HtmlSelect) l.get(0);
            List opts = sel.getSelectedOptions();
            SelectOption[] result = new SelectOption[opts.size()];
            for (int i = 0; i < result.length; i++)
                result[i] = new SelectOption(new HtmlOptionLocator(htmlSelect,
                        i), ((HtmlOption) opts.get(i)).getValueAttribute(),
                        ((HtmlOption) opts.get(i)).getLabelAttribute());
            return result;
        } catch (ClassCastException e) {
            throw new RuntimeException("Unexpected error: "
                    + htmlSelect.toString()
                    + " is not a HtmlUnit select element.");
        }
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectOption(net.sourceforge.jwebunit.locator.HtmlSelectLocator)
     */
    public SelectOption[] getSelectOptions(HtmlSelectLocator htmlSelect)
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        List<HtmlElement> l = getElements(htmlSelect);
        if (l.size() < 0) {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
                    htmlSelect);
        }
        HtmlSelect sel = null;
        try {
            sel = (HtmlSelect) l.get(0);
        } catch (ClassCastException e) {
            throw new RuntimeException("Unexpected error: "
                    + htmlSelect.toString()
                    + " is not a HtmlUnit select element.");
        }
        List opts = sel.getOptions();
        SelectOption[] result = new SelectOption[opts.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = new SelectOption(new HtmlOptionLocator(htmlSelect, i),
                    ((HtmlOption) opts.get(i)).getValueAttribute(),
                    ((HtmlOption) opts.get(i)).getLabelAttribute());
        return result;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getTable(net.sourceforge.jwebunit.locator.HtmlTableLocator)
     */
    public Table getTable(HtmlTableLocator tableLocator) throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        List<HtmlElement> l = getElements(tableLocator);
        if (l.size() < 0) {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
                    tableLocator);
        }
        HtmlTable table = null;
        try {
            table = (HtmlTable) l.get(0);
        } catch (ClassCastException e) {
            throw new RuntimeException("Unexpected error: "
                    + tableLocator.toString()
                    + " is not a HtmlUnit select element.");
        }
        Table result = new Table();
        for (int i = 0; i < table.getRowCount(); i++) {
            Row newRow = new Row();
            HtmlTableRow htmlRow = table.getRow(i);
            CellIterator cellIt = htmlRow.getCellIterator();
            while (cellIt.hasNext()) {
                HtmlTableCell htmlCell = cellIt.nextCell();
                newRow.appendCell(new Cell(htmlCell.asText(), htmlCell
                        .getColumnSpan(), htmlCell.getRowSpan()));
            }
            result.appendRow(newRow);
        }
        return result;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoFrame(net.sourceforge.jwebunit.locator.FrameLocator)
     */
    public void gotoFrame(FrameLocator frame) {
        if (frame instanceof FrameLocatorByName) {
            win = getFrame(((FrameLocatorByName)frame).getName());
        } else {
            throw new UnsupportedOperationException("Unknow FrameLocator. gotoFrame should be updated.");
        }
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoPage(java.net.URL)
     */
    public void gotoPage(URL url) throws TestingEngineResponseException {
        try {
            wc.getPage(url);
            win = wc.getCurrentWindow();
            form = null;
        } catch (ConnectException aException) {
            // cant find requested page. most browsers will return a page with
            // 404 in the body or title.
            throw new TestingEngineResponseException(ExceptionUtility
                    .stackTraceToString(aException));

        } catch (IOException aException) {
            throw new RuntimeException(ExceptionUtility
                    .stackTraceToString(aException));
        }
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoWindow(net.sourceforge.jwebunit.locator.WindowLocator)
     */
    public void gotoWindow(WindowLocator window) {
        if (window instanceof WindowLocatorByName) {
            setMainWindow(getWindow(((WindowLocatorByName)window).getName()));
        }
        else {
            throw new UnsupportedOperationException("Unknow WindowLocator. gotoWindow should be updated.");
        }
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasElement(net.sourceforge.jwebunit.locator.HtmlElementLocator)
     */
    public boolean hasElement(HtmlElementLocator htmlElement) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFrame(net.sourceforge.jwebunit.locator.FrameLocator)
     */
    public boolean hasFrame(FrameLocator frame) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasWindow(net.sourceforge.jwebunit.locator.WindowLocator)
     */
    public boolean hasWindow(WindowLocator window) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isCheckboxSelected(net.sourceforge.jwebunit.locator.HtmlCheckboxLocator)
     */
    public boolean isCheckboxSelected(HtmlCheckboxLocator checkbox) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isMatchInElement(net.sourceforge.jwebunit.locator.HtmlElementLocator,
     *      java.lang.String)
     */
    public boolean isMatchInElement(HtmlElementLocator htmlElement,
            String regexp) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isTextInElement(net.sourceforge.jwebunit.locator.HtmlElementLocator,
     *      java.lang.String)
     */
    public boolean isTextInElement(HtmlElementLocator htmlElement, String text) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#selectOptions(net.sourceforge.jwebunit.locator.HtmlSelectLocator,
     *      net.sourceforge.jwebunit.locator.HtmlOptionLocator[])
     */
    public void selectOptions(HtmlSelectLocator htmlSelect,
            HtmlOptionLocator[] options) {
        // TODO Auto-generated method stub

    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setTextField(net.sourceforge.jwebunit.locator.HtmlElementLocator,
     *      java.lang.String)
     */
    public void setTextField(HtmlElementLocator htmlElement, String text) {
        // TODO Auto-generated method stub

    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setWorkingForm(net.sourceforge.jwebunit.locator.HtmlFormLocator)
     */
    public void setWorkingForm(HtmlFormLocator form) {
        // TODO Auto-generated method stub

    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#unselectOptions(net.sourceforge.jwebunit.locator.HtmlSelectLocator,
     *      net.sourceforge.jwebunit.locator.HtmlOptionLocator[])
     */
    public void unselectOptions(HtmlSelectLocator htmlSelect,
            HtmlOptionLocator[] options) {
        // TODO Auto-generated method stub

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
        // TODO
    }

    public void closeBrowser() {
        wc = null;
    }

    public void gotoPage(String initialURL)
            throws TestingEngineResponseException {
        try {
            gotoPage(new URL(initialURL));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void goBack() {
        // TODO Implement goBack in HtmlUnitDialog
        throw new UnsupportedOperationException("goBack");
    }

    public void refresh() {
        // TODO Implement refresh in HtmlUnitDialog
        throw new UnsupportedOperationException("refresh");
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setScriptingEnabled(boolean)
     */
    public void setScriptingEnabled(boolean value) {
        // This variable is used to set Javascript before wc is instancied
        jsEnabled = value;
        if (wc != null) {
            wc.setJavaScriptEnabled(value);
        }
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasCookie(java.lang.String)
     */
    public boolean hasCookie(String cookieName) {
        final HttpState stateForUrl = wc.getWebConnection().getState();
        Cookie[] cookies = stateForUrl.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(cookieName))
                return true;
        }
        return false;
    }

    /**
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookieValue(java.lang.String)
     */
    public String getCookieValue(String cookieName) {
        final HttpState stateForUrl = wc.getWebConnection().getState();
        Cookie[] cookies = stateForUrl.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(cookieName))
                return cookies[i].getValue();
        }
        throw new RuntimeException("Cookie " + cookieName + " not found");
    }

    public String[][] getCookies() {
        final HttpState stateForUrl = wc.getWebConnection().getState();
        Cookie[] cookies = stateForUrl.getCookies();
        String[][] result = new String[cookies.length][2];
        for (int i = 0; i < cookies.length; i++) {
            result[i][0] = cookies[i].getName();
            result[i][1] = cookies[i].getValue();
        }
        return result;
    }

    public boolean hasWindow(String windowName) {
        try {
            getWindow(windowName);
        } catch (WebWindowNotFoundException e) {
            return false;
        }
        return true;
    }

    public boolean hasWindowByTitle(String title) {
        return getWindowByTitle(title) != null;
    }

    /**
     * Make the window with the given name in the current conversation active.
     * 
     * @param windowName
     */
    public void gotoWindow(String windowName) {
        setMainWindow(getWindow(windowName));
    }

    public void gotoWindow(int windowID) {
        setMainWindow((WebWindow) wc.getWebWindows().get(windowID));
    }

    public int getWindowCount() {
        return wc.getWebWindows().size();
    }

    /**
     * Goto first window with the given title.
     * 
     * @param windowName
     */
    public void gotoWindowByTitle(String title) {
        WebWindow window = getWindowByTitle(title);
        if (window != null) {
            setMainWindow(window);
        }
    }

    public void closeWindow() {
        if (getWindowCount() == 1) {
            closeBrowser();
        } else {
            wc.deregisterWebWindow(win);
            win = wc.getCurrentWindow();
            form = null;
        }

    }

    public boolean hasFrame(String frameName) {
        return getFrame(frameName) != null;
    }

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     */
    public void gotoFrame(String frameName) {
        win = getFrame(frameName);
    }

    /**
     * Set the form on the current response that the client wishes to work with
     * explicitly by either the form name or id (match by id is attempted
     * first).
     * 
     * @param nameOrId
     *            name or id of the form to be worked with.
     */
    public void setWorkingForm(String nameOrId, int index) {
        setWorkingForm(getForm(nameOrId, index));
    }

    /**
     * Return true if the current response contains a form.
     */
    public boolean hasForm() {
        return ((HtmlPage) win.getEnclosedPage()).getForms().size() > 0;
    }

    /**
     * Return true if the current response contains a specific form.
     * 
     * @param nameOrID
     *            name of id of the form to check for.
     */
    public boolean hasForm(String nameOrID) {
        return getForm(nameOrID) != null;
    }

    public boolean hasFormParameterNamed(String paramName) {
        if (hasFormSelectNamed(paramName))
            return true;
        return hasFormInputNamed(paramName);
    }

    /**
     * Return the current value of a form input element. A special attention is
     * given to checkboxes, as we want value of checked element
     * 
     * @param paramName
     *            name of the input element.
     * @deprecated
     */
    public String getFormParameterValue(String paramName) {
        checkFormStateWithInput(paramName);
        HtmlRadioButtonInput rbtn = getForm().getCheckedRadioButton(paramName);
        if (rbtn != null)
            return rbtn.getValueAttribute();
        try {
            // TODO What should I return when it is a multi-select
            return ((HtmlOption) getForm().getSelectByName(paramName)
                    .getSelectedOptions().get(0)).getValueAttribute();
        } catch (ElementNotFoundException e) {

        }
        try {
            return getForm().getInputByName(paramName).getValueAttribute();
        } catch (ElementNotFoundException e) {

        }
        throw new RuntimeException("getFormParameterValue failed");
    }

    /**
     * Return the current value of a text input element with name
     * <code>paramName</code>.
     * 
     * @param paramName
     *            name of the input element. TODO: Find a way to handle multiple
     *            text input element with same name.
     */
    public String getTextFieldValue(String paramName) {
        checkFormStateWithInput(paramName);
        List textFieldElements = getForm().getHtmlElementsByAttribute("input",
                "type", "text");
        textFieldElements.addAll(getForm().getHtmlElementsByAttribute("input",
                "type", "password"));
        Iterator it = textFieldElements.iterator();
        while (it.hasNext()) {
            HtmlInput input = (HtmlInput) it.next();
            if (paramName.equals(input.getNameAttribute())) {
                return input.getValueAttribute();
            }
        }
        // If no text field with the name paramName then try with textareas.
        textFieldElements = getForm().getTextAreasByName(paramName);
        it = textFieldElements.iterator();
        while (it.hasNext()) {
            HtmlTextArea textInput = (HtmlTextArea) it.next();
            if (paramName.equals(textInput.getNameAttribute())) {
                return textInput.getText();
            }
        }
        throw new RuntimeException(
                "getTextFieldParameterValue failed, text field with name ["
                        + paramName + "] does not exist.");
    }

    /**
     * Return the current value of a hidden input element with name
     * <code>paramName</code>.
     * 
     * @param paramName
     *            name of the input element. TODO: Find a way to handle multiple
     *            hidden input element with same name.
     */
    public String getHiddenFieldValue(String paramName) {
        checkFormStateWithInput(paramName);
        List textFieldElements = getForm().getHtmlElementsByAttribute("input",
                "type", "hidden");
        Iterator it = textFieldElements.iterator();
        while (it.hasNext()) {
            HtmlHiddenInput textInput = (HtmlHiddenInput) it.next();
            if (paramName.equals(textInput.getNameAttribute())) {
                return textInput.getValueAttribute();
            }
        }
        throw new RuntimeException(
                "getHiddenFieldParameterValue failed, hidden field with name ["
                        + paramName + "] does not exist.");
    }

    /**
     * Set a form text, password input element or textarea to the provided
     * value.
     * 
     * @param fieldName
     *            name of the input element or textarea
     * @param paramValue
     *            parameter value to submit for the element.
     */
    public void setTextField(String fieldName, String paramValue) {
        checkFormStateWithInput(fieldName);
        List inputElements = getForm().getHtmlElementsByAttribute("input",
                "name", fieldName);
        if (!inputElements.isEmpty()) {
            HtmlInput input = (HtmlInput) inputElements.get(0);
            input.setValueAttribute(paramValue);
        } else {
            inputElements = getForm().getTextAreasByName(fieldName);
            if (!inputElements.isEmpty()) {
                HtmlTextArea textArea = (HtmlTextArea) inputElements.get(0);
                textArea.setText(paramValue);
            }
        }
    }

    /**
     * Return a string array of select box option values.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String[] getSelectOptionValues(String selectName) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        ArrayList result = new ArrayList();
        List opts = sel.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            HtmlOption opt = (HtmlOption) opts.get(i);
            result.add(opt.getValueAttribute());
        }
        return (String[]) result.toArray(new String[0]);
    }

    public String[] getSelectedOptions(String selectName) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        List opts = sel.getSelectedOptions();
        String[] result = new String[opts.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = ((HtmlOption) opts.get(i)).getValueAttribute();
        return result;
    }

    public String getSelectOptionValueForLabel(String selectName, String label) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        List opts = sel.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            HtmlOption opt = (HtmlOption) opts.get(i);
            if (opt.asText().equals(label))
                return opt.getValueAttribute();
        }
        throw new RuntimeException("Unable to find option " + label + " for "
                + selectName);
    }

    public String getSelectOptionLabelForValue(String selectName, String value) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        List opts = sel.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            HtmlOption opt = (HtmlOption) opts.get(i);
            if (opt.getValueAttribute().equals(value))
                return opt.asText();
        }
        throw new RuntimeException("Unable to find option " + value + " for "
                + selectName);
    }

    public String getPageSource() {
        return getCurrentPage().getWebResponse().getContentAsString();
    }

    public String getPageTitle() {
        return getCurrentPageTitle();
    }

    public String getPageText() {
        return ((HtmlPage) getCurrentPage()).asText();
    }

    public String getServerResponse() {
        return wc.getCurrentWindow().getEnclosedPage().getWebResponse()
                .getContentAsString();
    }

    private void initWebClient() {
        wc = new WebClient(new BrowserVersion(BrowserVersion.INTERNET_EXPLORER,
                "4.0", testContext.getUserAgent(), "1.2", 6));
        wc.setJavaScriptEnabled(jsEnabled);
        wc.setThrowExceptionOnScriptError(true);
        DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
        if (getTestContext().hasAuthorization()) {
            creds.addCredentials(getTestContext().getUser(), getTestContext()
                    .getPassword());
        }
        if (getTestContext().hasNTLMAuthorization()) {
            InetAddress netAddress;
            String address;
            try {
                netAddress = InetAddress.getLocalHost();
                address = netAddress.getHostName();
            } catch (UnknownHostException e) {
                address = "";
            }
            creds.addNTLMCredentials(getTestContext().getUser(),
                    getTestContext().getPassword(), "", -1, address,
                    getTestContext().getDomain());
        }
        wc.addWebWindowListener(new WebWindowListener() {
            public void webWindowClosed(WebWindowEvent event) {
                if (event.getOldPage().equals(win.getEnclosedPage())) {
                    win = wc.getCurrentWindow();
                    form = null;
                }
                String win = event.getWebWindow().getName();
                Page oldPage = event.getOldPage();
                LOGGER.info("Window " + win + " closed : "
                        + ((HtmlPage) oldPage).getTitleText());
            }

            public void webWindowContentChanged(WebWindowEvent event) {
                form = null;
                String winName = event.getWebWindow().getName();
                Page oldPage = event.getOldPage();
                Page newPage = event.getNewPage();
                String oldPageTitle = "no_html";
                if (oldPage instanceof HtmlPage)
                    oldPageTitle = ((HtmlPage) oldPage).getTitleText();
                String newPageTitle = "no_html";
                if (newPage instanceof HtmlPage)
                    newPageTitle = ((HtmlPage) newPage).getTitleText();
                LOGGER.info("Window \"" + winName + "\" changed : \""
                        + oldPageTitle + "\" became \"" + newPageTitle + "\"");
            }

            public void webWindowOpened(WebWindowEvent event) {
                String win = event.getWebWindow().getName();
                Page newPage = event.getNewPage();
                if (newPage != null) {
                    LOGGER.info("Window " + win + " openend : "
                            + ((HtmlPage) newPage).getTitleText());
                } else {
                    LOGGER.info("Window " + win + " openend");
                }
            }
        });
        // Add Javascript Alert Handler
        wc.setAlertHandler(new AlertHandler() {
            public void handleAlert(Page page, String msg) {
                javascriptAlerts.add(msg);
            }
        });
    }

    /**
     * Return the window with the given name in the current conversation.
     * 
     * @param windowName
     */
    private WebWindow getWindow(String windowName) {
        return wc.getWebWindowByName(windowName);
    }

    private HtmlElement getElement(String anID) {
        try {
            return ((HtmlPage) win.getEnclosedPage()).getHtmlElementById(anID);
        } catch (ElementNotFoundException e) {
            return null;
        }
    }

    protected List<HtmlElement> getElements(HtmlElementLocator l) {
        return getElementsByXPath(getCurrentPage(), l.getXPath());
    }

    private HtmlElement getElementByXPath(String xpath) {
        List<HtmlElement> l = getElementsByXPath(getCurrentPage(), xpath);
        if (l.size() > 0)
            return l.get(0);
        else
            return null;
    }

    private List<HtmlElement> getElementsByXPath(Object parent, String xpath) {
        List l = null;
        try {
            final HtmlUnitXPath xp = new HtmlUnitXPath(xpath);
            l = xp.selectNodes(parent);
        } catch (JaxenException e) {
            return null;
        }
        return l;
    }

    /**
     * Return the first open window with the given title.
     */
    private WebWindow getWindowByTitle(String title) {
        List webWindows = wc.getWebWindows();
        for (int i = 0; i < webWindows.size(); i++) {
            WebWindow window = (WebWindow) webWindows.get(i);
            if (window.getEnclosedPage() instanceof HtmlPage
                    && ((HtmlPage) window.getEnclosedPage()).getTitleText()
                            .equals(title)) {
                return window;
            }
        }
        return null;
    }

    /**
     * Return the page title of the current response page, encoded as specified
     * by the current {@link net.sourceforge.jwebunit.TestContext}.
     */
    public String getCurrentPageTitle() {
        if (win.getEnclosedPage() instanceof HtmlPage) {
            return ((HtmlPage) win.getEnclosedPage()).getTitleText();
        }
        return "";
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
    private HtmlForm getForm() {
        if (form == null) {
            if (hasForm()) {
                setWorkingForm(getForm(0));
                return getForm(0);
            } else {
                throw new RuntimeException("No form in current page");
            }
        } else {
            return form;
        }
    }

    private HtmlForm getForm(int formIndex) {
        return (HtmlForm) ((HtmlPage) win.getEnclosedPage()).getForms().get(
                formIndex);
    }

    private HtmlForm getForm(String nameOrID) {
        try {
            return (HtmlForm) ((HtmlPage) win.getEnclosedPage())
                    .getHtmlElementById(nameOrID);
        } catch (ElementNotFoundException e) {

        }
        try {
            return ((HtmlPage) win.getEnclosedPage()).getFormByName(nameOrID);
        } catch (ElementNotFoundException e) {

        }
        return null;
    }

    private HtmlForm getForm(String nameOrID, int index) {
        HtmlForm form = null;
        Iterator iter = getCurrentPage().getForms().iterator();
        for (int pos = 0; pos <= index && iter.hasNext();) {
            HtmlForm curr = (HtmlForm) iter.next();
            if (nameOrID.equals(curr.getIdAttribute())
                    || nameOrID.equals(curr.getNameAttribute())) {
                pos++;
                form = curr;
            }
        }
        return form;
    }

    private HtmlForm getFormWithButton(String buttonName) {
        if (hasForm()) {
            for (int i = 0; i < getForms().size(); i++) {
                HtmlForm form = (HtmlForm) getForms().get(i);
                if (form.getButtonsByName(buttonName).size() > 0)
                    return form;
                try {
                    HtmlInput inp = form.getInputByName(buttonName);
                    if (inp instanceof HtmlButtonInput)
                        return form;
                    if (inp instanceof HtmlSubmitInput)
                        return form;
                    if (inp instanceof HtmlResetInput)
                        return form;
                } catch (ElementNotFoundException e) {

                }
            }
        }
        return null;
    }

    private HtmlForm getFormWithInput(String inputName) {
        if (hasForm()) {
            for (int i = 0; i < getForms().size(); i++) {
                HtmlForm form = (HtmlForm) getForms().get(i);
                List inputElements = form.getHtmlElementsByAttribute("input",
                        "name", inputName);
                if (inputElements.isEmpty()) {
                    inputElements = form.getTextAreasByName(inputName);
                }
                if (!inputElements.isEmpty()) {
                    setWorkingForm(form);
                    return form;
                }
            }
        }
        return null;
    }

    private HtmlForm getFormWithSelect(String selectName) {
        if (hasForm()) {
            for (int i = 0; i < getForms().size(); i++) {
                HtmlForm form = (HtmlForm) getForms().get(i);
                try {
                    if (form.getSelectByName(selectName) != null) {
                        setWorkingForm(form);
                        return form;
                    }
                } catch (ElementNotFoundException e) {
                    // Nothing
                }
            }
        }
        return null;
    }

    private List getForms() {
        HtmlPage page = (HtmlPage) win.getEnclosedPage();
        return page.getForms();
    }

    private HtmlPage getCurrentPage() {
        Page page = win.getEnclosedPage();
        if (page instanceof HtmlPage)
            return (HtmlPage) page;
        if (page instanceof UnexpectedPage)
            throw new RuntimeException("Unexpected content");
        return (HtmlPage) page;
    }

    private void checkFormStateWithInput(String paramName) {
        if (form == null) {
            try {
                setWorkingForm(getFormWithInput(paramName));
            } catch (UnableToSetFormException e) {
                throw new UnableToSetFormException(
                        "Unable to set form based on parameter [" + paramName
                                + "].");
            }
        }
    }

    private void checkFormStateWithButton(String buttonName) {
        if (form == null) {
            setWorkingForm(getFormWithButton(buttonName));
        }
    }

    private void setWorkingForm(HtmlForm newForm) {
        if (newForm == null)
            throw new UnableToSetFormException("Attempted to set form to null.");
        form = newForm;
    }

    /**
     * Return true if a form parameter (input element) is present on the current
     * response.
     * 
     * @param inputName
     *            name of the input element to check for
     */
    public boolean hasFormInputNamed(String inputName) {
        return (getFormWithInput(inputName) != null);
    }

    /**
     * Return true if a form parameter (input element) is present on the current
     * response.
     * 
     * @param selectName
     *            name of the input element to check for
     */
    public boolean hasFormSelectNamed(String selectName) {
        return (getFormWithSelect(selectName) != null);
    }

    /**
     * Return true if a form button is present on the current response.
     * 
     * @param buttonName
     *            name of the button element to check for
     */
    public boolean hasFormButtonNamed(String buttonName) {
        return (getFormWithButton(buttonName) != null);
    }

    /**
     * Return the HttpUnit SubmitButton with a given name.
     * 
     * @param buttonName
     *            name of button.
     */
    public HtmlSubmitInput getSubmitButton(String buttonName) {
        try {
            checkFormStateWithButton(buttonName);
        } catch (UnableToSetFormException e) {
            return null;
        }
        try {
            return (HtmlSubmitInput) getForm().getInputByName(buttonName);
        } catch (ElementNotFoundException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public HtmlResetInput getResetButton(String buttonName) {
        try {
            checkFormStateWithButton(buttonName);
        } catch (UnableToSetFormException e) {
            return null;
        }
        try {
            return (HtmlResetInput) getForm().getInputByName(buttonName);
        } catch (ElementNotFoundException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public String getSubmitButtonValue(String buttonName) {
        return getSubmitButton(buttonName).getValueAttribute().trim();
    }

    /**
     * Return the HttpUnit SubmitButton with a given name and value.
     * 
     * @param buttonName
     * @pararm buttonValue
     */
    public HtmlSubmitInput getSubmitButton(String buttonName, String buttonValue) {
        checkFormStateWithButton(buttonName);
        List btns = null;
        try {
            btns = getForm().getInputsByName(buttonName);
        } catch (ClassCastException e) {
            return null;
        }
        for (int i = 0; i < btns.size(); i++) {
            HtmlSubmitInput btn = (HtmlSubmitInput) btns.get(i);
            if (btn.getValueAttribute().equals(buttonValue))
                return btn;
        }
        return null;
    }

    public boolean hasSubmitButton() {
        List l = null;
        try {
            final HtmlUnitXPath xp = new HtmlUnitXPath(
                    "//input[@type=\"submit\"]");
            l = xp.selectNodes(getForm());
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
        return (l.size() > 0);
    }

    public boolean hasSubmitButton(String buttonName) {
        return getSubmitButton(buttonName) != null;
    }

    public boolean hasSubmitButton(String buttonName, String buttonValue) {
        try {
            return getSubmitButton(buttonName, buttonValue) != null;
        } catch (UnableToSetFormException e) {
            return false;
        }

    }

    public boolean hasResetButton() {
        List l = null;
        try {
            final HtmlUnitXPath xp = new HtmlUnitXPath(
                    "//input[@type=\"reset\"]");
            l = xp.selectNodes(getForm());
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
        return (l.size() > 0);
    }

    public boolean hasResetButton(String buttonName) {
        return getResetButton(buttonName) != null;
    }

    /**
     * Return the HtmlUnit Button with a given id.
     * 
     * @param buttonId
     */
    private ClickableElement getButton(String buttonId) {
        HtmlElement btn = null;
        try {
            btn = getCurrentPage().getHtmlElementById(buttonId);
            if (btn instanceof HtmlButton || btn instanceof HtmlButtonInput
                    || btn instanceof HtmlSubmitInput
                    || btn instanceof HtmlResetInput)
                return (ClickableElement) btn;
        } catch (ElementNotFoundException e) {
            return null;
        }
        return null;
    }

    /**
     * Checks if a button with <code>text</code> is present.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     * @return <code>true</code> when the button with text could be found.
     */
    public boolean hasButtonWithText(String text) {
        boolean bReturn = getButtonWithText(text) != null ? true : false;
        return bReturn;
    }

    public HtmlButton getButtonWithText(String buttonValueText) {
        List l = ((HtmlPage) win.getEnclosedPage()).getDocumentElement()
                .getHtmlElementsByTagNames(
                        Arrays.asList(new String[] {"button"}));
        for (int i = 0; i < l.size(); i++) {
            HtmlElement e = (HtmlElement) l.get(i);
            if (((HtmlButton) e).getValueAttribute().equals(buttonValueText))
                return (HtmlButton) e;
        }
        return null;
    }

    /**
     * Returns if the button identified by <code>buttonId</code> is present.
     * 
     * @param buttonId
     *            the id of the button
     * @return <code>true</code> when the button was found.
     */
    public boolean hasButton(String buttonId) {
        try {
            return getButton(buttonId) != null;
        } catch (UnableToSetFormException e) {
            return false;
        }
    }

    public boolean isCheckboxSelected(String checkBoxName) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
        return cb.isChecked();
    }

    public boolean isCheckboxNotSelected(String checkBoxName) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
        return !cb.isChecked();
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
        HtmlTable table = getHtmlTable(tableSummaryOrId);
        if (table == null) {
            throw new RuntimeException("No table with summary or id ["
                    + tableSummaryOrId + "] found in response.");
        }
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; table.getCellAt(row, col) != null; col++) {
                HtmlTableCell cell = table.getCellAt(row, col);
                if (cell != null) {
                    String cellHtml = cell.asText();
                    if (cellHtml.indexOf(text) != -1)
                        return true;
                }
            }
        }
        return false;
    }

    public Table getTable(String tableSummaryOrId) {
        HtmlTable table = getHtmlTable(tableSummaryOrId);
        Table result = new Table();
        for (int i = 0; i < table.getRowCount(); i++) {
            Row newRow = new Row();
            HtmlTableRow htmlRow = table.getRow(i);
            CellIterator cellIt = htmlRow.getCellIterator();
            while (cellIt.hasNext()) {
                HtmlTableCell htmlCell = cellIt.nextCell();
                newRow.appendCell(new Cell(htmlCell.asText(), htmlCell
                        .getColumnSpan(), htmlCell.getRowSpan()));
            }
            result.appendRow(newRow);
        }
        return result;
    }

    /**
     * Return the HttpUnit WebTable object representing a specified table in the
     * current response. Null is returned if a parsing exception occurs looking
     * for the table or no table with the id or summary could be found.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table to return.
     */
    public HtmlTable getHtmlTable(String tableSummaryOrId) {
        try {
            return (HtmlTable) ((HtmlPage) win.getEnclosedPage())
                    .getHtmlElementById(tableSummaryOrId);
        } catch (ElementNotFoundException e) {

        }
        try {
            return (HtmlTable) ((HtmlPage) win.getEnclosedPage())
                    .getDocumentElement().getOneHtmlElementByAttribute("table",
                            "summary", tableSummaryOrId);
        } catch (ElementNotFoundException e) {

        }
        return null;
    }

    public boolean hasTable(String tableSummaryOrId) {
        HtmlTable table = getHtmlTable(tableSummaryOrId);
        return (table != null);
    }

    /**
     * Submit the current form with the default submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    public void submit() {
        try {
            Object[] inpt = getForm().getHtmlElementsByTagName("input")
                    .toArray();
            for (int i = 0; i < inpt.length; i++) {
                if (inpt[i] instanceof HtmlSubmitInput) {
                    ((HtmlSubmitInput) inpt[i]).click();
                    return;
                }
            }
            for (int i = 0; i < inpt.length; i++) {
                if (inpt[i] instanceof HtmlButtonInput) {
                    ((HtmlButtonInput) inpt[i]).click();
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "HtmlUnit Error submitting form using default submit button, "
                            + "check that form has single submit button, otherwise use submit(name): \n"
                            + ExceptionUtility.stackTraceToString(e));
        }
        throw new RuntimeException("No submit button found in current form.");
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
        try {
            getForm().getInputByName(buttonName).click();
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
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
        List l = getForm().getInputsByName(buttonName);
        for (int i = 0; i < l.size(); i++) {
            HtmlSubmitInput inpt = (HtmlSubmitInput) l.get(i);
            try {
                if (inpt.getValueAttribute().equals(buttonValue)) {
                    inpt.click();
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    public void reset() {
        getForm().reset();
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
    public boolean hasLinkWithText(String linkText, int index) {
        return getLinkWithText(linkText, index) != null;
    }

    public boolean hasLinkWithExactText(String linkText, int index) {
        return getLinkWithExactText(linkText, index) != null;
    }

    public boolean hasLinkWithImage(String imageFileName, int index) {
        return getLinkWithImage(imageFileName, index) != null;
    }

    /**
     * Return true if a link is present in the current response with the
     * specified id.
     * 
     * @param anId
     *            link id to check for.
     */
    public boolean hasLink(String anId) {
        try {
            ((HtmlPage) win.getEnclosedPage()).getHtmlElementById(anId);
        } catch (ElementNotFoundException e) {
            return false;
        }
        return true;
    }

    public void clickLinkWithText(String linkText, int index) {
        HtmlAnchor link = getLinkWithText(linkText, index);
        if (link == null)
            throw new RuntimeException("No Link found for \"" + linkText
                    + "\" with index " + index);
        try {
            link.click();
        } catch (IOException e) {
            throw new RuntimeException("Click failed");
        }
    }

    public void clickLinkWithExactText(String linkText, int index) {
        HtmlAnchor link = getLinkWithExactText(linkText, index);
        if (link == null)
            throw new RuntimeException("No Link found for \"" + linkText
                    + "\" with index " + index);
        try {
            link.click();
        } catch (IOException e) {
            throw new RuntimeException("Click failed");
        }
    }

    private HtmlCheckBoxInput getCheckbox(String checkBoxName) {
        Object[] l = getForm().getInputsByName(checkBoxName).toArray();
        for (int i = 0; i < l.length; i++) {
            if (l[i] instanceof HtmlCheckBoxInput)
                return (HtmlCheckBoxInput) l[i];
        }
        throw new RuntimeException("No checkbox with name [" + checkBoxName
                + "] was found in current form.");
    }

    private HtmlCheckBoxInput getCheckbox(String checkBoxName, String value) {
        Object[] l = getForm().getInputsByName(checkBoxName).toArray();
        for (int i = 0; i < l.length; i++) {
            if (l[i] instanceof HtmlCheckBoxInput)
                if (((HtmlCheckBoxInput) l[i]).getValueAttribute()
                        .equals(value))
                    return (HtmlCheckBoxInput) l[i];
        }
        throw new RuntimeException("No checkbox with name [" + checkBoxName
                + "] and value [" + value + "] was found in current form.");
    }

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
        if (!cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed :" + e);
            }
    }

    public void checkCheckbox(String checkBoxName, String value) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName, value);
        if (!cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed :" + e);
            }
    }

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then
     * the checkbox will stay unchecked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
        if (cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed :" + e);
            }
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName, value);
        if (cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("uncheckCheckbox failed :" + e);
            }
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
        HtmlRadioButtonInput rb = (HtmlRadioButtonInput) getForm()
                .getRadioButtonInput(radioGroup, radioOption);
        if (!rb.isChecked())
            try {
                rb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed :" + e);
            }
    }

    /**
     * Navigate by submitting a request based on a link with a given ID. A
     * RuntimeException is thrown if no such link can be found.
     * 
     * @param anID
     *            id of link to be navigated.
     */
    public void clickLink(String anID) {
        clickElementByXPath("//a[@id=\"" + anID + "\"]");
    }

    private HtmlAnchor getLinkWithImage(String filename, int index) {
        return (HtmlAnchor) getElementByXPath("(//a[img[contains(@src,\""
                + filename + "\")]])[" + index + 1 + "]");
    }

    private HtmlAnchor getLinkWithText(String linkText, int index) {
        List lnks = ((HtmlPage) win.getEnclosedPage()).getAnchors();
        int count = 0;
        for (int i = 0; i < lnks.size(); i++) {
            HtmlAnchor lnk = (HtmlAnchor) lnks.get(i);
            if ((lnk.asText().indexOf(linkText) >= 0) && (count++ == index))
                return lnk;
        }
        return null;
    }

    private HtmlAnchor getLinkWithExactText(String linkText, int index) {
        List lnks = ((HtmlPage) win.getEnclosedPage()).getAnchors();
        int count = 0;
        for (int i = 0; i < lnks.size(); i++) {
            HtmlAnchor lnk = (HtmlAnchor) lnks.get(i);
            if ((lnk.asText().equals(linkText)) && (count++ == index))
                return lnk;
        }
        return null;
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
    public void clickLinkWithImage(String imageFileName, int index) {
        HtmlAnchor link = getLinkWithImage(imageFileName, index);
        if (link == null)
            throw new RuntimeException("No Link found with filename \""
                    + imageFileName + "\" and index " + index);
        try {
            link.click();
        } catch (IOException e) {
            throw new RuntimeException("Click failed");
        }
    }

    public boolean hasElement(String anID) {
        return getElement(anID) != null;
    }

    public boolean hasElementByXPath(String xpath) {
        return getElementByXPath(xpath) != null;
    }

    public void clickElementByXPath(String xpath) {
        HtmlElement e = getElementByXPath(xpath);
        if (e == null)
            throw new RuntimeException("No element found with xpath \"" + xpath
                    + "\"");
        try {
            ClickableElement c = (ClickableElement) e;
            c.click();
        } catch (ClassCastException exp) {
            throw new RuntimeException("Element with xpath \"" + xpath
                    + "\" is not clickable");
        } catch (IOException exp) {
            throw new RuntimeException("Click failed");
        }
    }

    /**
     * Click the indicated button (input type=button).
     * 
     * @param buttonId
     */
    public void clickButton(String buttonId) {
        ClickableElement btn = getButton(buttonId);
        try {
            btn.click();
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    public void clickButtonWithText(String buttonValueText) {
        try {
            if (hasButtonWithText(buttonValueText)) {
                getButtonWithText(buttonValueText).click();
            }
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
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
        List forms = getForms();
        for (int i = 0; i < forms.size(); i++) {
            HtmlForm form = (HtmlForm) forms.get(i);
            try {
                form.getRadioButtonInput(radioGroup, radioOption);
                return true;
            } catch (ElementNotFoundException e) {

            }
        }
        return false;
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
        String[] opts = getSelectOptionValues(selectName);
        for (int i = 0; i < opts.length; i++) {
            String label = getSelectOptionLabelForValue(selectName, opts[i]);
            if (label.equals(optionLabel))
                return true;
        }
        return false;
    }

    /**
     * Return true if a select box contains the indicated option.
     * 
     * @param selectName
     *            name of the select box.
     * @param optionValue
     *            value of the option.
     */
    public boolean hasSelectOptionValue(String selectName, String optionValue) {
        String[] opts = getSelectOptionValues(selectName);
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(optionValue))
                return true;
        }
        return false;
    }

    public void selectOptions(String selectName, String[] options) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        if (!sel.isMultipleSelectEnabled() && options.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        List l = sel.getOptions();
        for (int j = 0; j < options.length; j++) {
            boolean found = false;
            for (int i = 0; i < l.size(); i++) {
                HtmlOption opt = (HtmlOption) l.get(i);
                if (opt.getValueAttribute().equals(options[j])) {
                    sel.setSelectedAttribute(opt, true);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new RuntimeException("Option " + options[j]
                        + " not found");
        }
    }

    public void unselectOptions(String selectName, String[] options) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        if (!sel.isMultipleSelectEnabled() && options.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        List l = sel.getOptions();
        for (int j = 0; j < options.length; j++) {
            boolean found = false;
            for (int i = 0; i < l.size(); i++) {
                HtmlOption opt = (HtmlOption) l.get(i);
                if (opt.asText().equals(options[j])) {
                    sel.setSelectedAttribute(opt, false);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new RuntimeException("Option " + options[j]
                        + " not found");
        }
    }

    public boolean isTextInElement(String elementID, String text) {
        return isTextInElement(getElement(elementID), text);
    }

    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param element
     *            org.w3c.com.Element to inspect.
     * @param text
     *            text to check for.
     */
    private boolean isTextInElement(HtmlElement element, String text) {
        return element.asText().indexOf(text) >= 0;
    }

    public boolean isMatchInElement(String elementID, String regexp) {
        return isMatchInElement(getElement(elementID), regexp);
    }

    /**
     * Return true if a given regexp is contained within the specified element.
     * 
     * @param element
     *            org.w3c.com.Element to inspect.
     * @param regexp
     *            regexp to match.
     */
    private boolean isMatchInElement(HtmlElement element, String regexp) {
        RE re = getRE(regexp);
        return re.match(element.asText());
    }

    private RE getRE(String regexp) {
        try {
            return new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Make the root window in the current conversation active.
     */
    public void gotoRootWindow() {
        setMainWindow((WebWindow) wc.getWebWindows().get(0));
    }

    private void setMainWindow(WebWindow win) {
        wc.setCurrentWindow(win);
        this.win = win;
    }

    public String getJavascriptAlert()
            throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
        if (!javascriptAlerts.isEmpty()) {
            return javascriptAlerts.removeFirst();
        } else {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
                    "There is no pending alert.");
        }
    }

    /**
     * Return the response for the given frame in the current conversation.
     * 
     * @param frameName
     */
    private WebWindow getFrame(String frameName) {
        return ((HtmlPage) win.getEnclosedPage()).getFrameByName(frameName);
    }

    /**
     * @param testContext
     *            The testContext to set.
     */
    private void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * @return Returns the testContext.
     */
    private TestContext getTestContext() {
        return testContext;
    }

}
