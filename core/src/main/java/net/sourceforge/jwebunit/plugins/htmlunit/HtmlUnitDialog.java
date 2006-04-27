/**
 * HtmlUnitDialog
 * Dialogue entre jWebUnit (capWebUnit) et HtmlUnit. 
 * @author Julien HENRY
 * @version 1.0
 */
package net.sourceforge.jwebunit.plugins.htmlunit;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.jaxen.JaxenException;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.util.ExceptionUtility;
import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.TestContext;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.WebWindowNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
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
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.xpath.HtmlUnitXPath;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

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
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(HtmlUnitDialog.class);

    private WebClient wc;

    private WebWindow win;

    private TestContext testContext;

    private HtmlForm form;

    /**
     * Is Javascript enabled
     */
    private boolean jsEnabled = true;

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
        initWebClient();
        try {
            wc.getPage(new URL(initialURL));
            win = wc.getCurrentWindow();
            form = null;
        } catch (FailingHttpStatusCodeException aException) {
            // cant find requested page. most browsers will return a page with
            // 404 in the body or title.
            throw new TestingEngineResponseException(ExceptionUtility
                    .stackTraceToString(aException));

        } catch (IOException aException) {
            throw new RuntimeException(ExceptionUtility
                    .stackTraceToString(aException));
        }
    }

    public void gotoPage(String initialURL)
            throws TestingEngineResponseException {
        try {
            wc.getPage(new URL(initialURL));
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

    private void initWebClient() {
        wc = new WebClient();
        wc.setJavaScriptEnabled(jsEnabled);
        wc.addWebWindowListener(new WebWindowListener() {
            public void webWindowClosed(WebWindowEvent event) {
                if (event.getOldPage().equals(win.getEnclosedPage())) {
                    win = wc.getCurrentWindow();
                    form = null;
                }
                String win = event.getWebWindow().getName();
                Page oldPage = event.getOldPage();
                logger.info("Window " + win + " closed : "
                        + ((HtmlPage) oldPage).getTitleText());
            }

            public void webWindowContentChanged(WebWindowEvent event) {
                String win = event.getWebWindow().getName();
                Page oldPage = event.getOldPage();
                Page newPage = event.getNewPage();
                String oldPageTitle = "non_html";
                if (oldPage instanceof HtmlPage)
                    oldPageTitle = ((HtmlPage) oldPage).getTitleText();
                String newPageTitle = "non_html";
                if (newPage instanceof HtmlPage)
                    newPageTitle = ((HtmlPage) newPage).getTitleText();
                logger.info("Window " + win + " changed : " + oldPageTitle
                        + " became " + newPageTitle);
            }

            public void webWindowOpened(WebWindowEvent event) {
                String win = event.getWebWindow().getName();
                Page newPage = event.getNewPage();
                if (newPage != null) {
                    logger.info("Window " + win + " closed : "
                            + ((HtmlPage) newPage).getTitleText());
                }
            }
        });
    }

    public boolean isWindowExists(String windowName) {
        try {
            getWindow(windowName);
        } catch (WebWindowNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Return the window with the given name in the current conversation.
     * 
     * @param windowName
     */
    private WebWindow getWindow(String windowName) {
        return wc.getWebWindowByName(windowName);
    }

    public boolean isWindowByTitleExists(String title) {
        return getWindowByTitle(title) != null;
    }

    public boolean isElementPresent(String anID) {
        return getElement(anID) != null;
    }

    public boolean isXPathElementPresent(String xpath) {
        return getXPathElement(xpath) != null;
    }

    private HtmlElement getElement(String anID) {
        try {
            return ((HtmlPage) win.getEnclosedPage()).getHtmlElementById(anID);
        } catch (ElementNotFoundException e) {
            return null;
        }
    }

    private HtmlElement getXPathElement(String xpath) {
        List l = null;
        try {
            final HtmlUnitXPath xp = new HtmlUnitXPath(xpath);
            l = xp.selectNodes(getCurrentPage());
        } catch (JaxenException e) {
            return null;
        }
        return (HtmlElement) (l.size() > 0 ? l.get(0) : null);
    }

    /**
     * Return the first open window with the given title.
     */
    private WebWindow getWindowByTitle(String title) {
        List webWindows = wc.getWebWindows();
        for (int i = 0; i < webWindows.size(); i++) {
            WebWindow window = (WebWindow) webWindows.get(i);
            if (window.getEnclosedPage() instanceof HtmlPage
                    && getTestContext().toEncodedString(
                            ((HtmlPage) window.getEnclosedPage())
                                    .getTitleText()).equals(title)) {
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
            return getTestContext().toEncodedString(
                    ((HtmlPage) win.getEnclosedPage()).getTitleText());
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
        if (form == null && hasForm())
            setWorkingForm(getForm(0));
        return form;
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
                try {
                    if (form.getInputByName(inputName) != null) {
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

    /**
     * Set the form on the current response that the client wishes to work with
     * explicitly by either the form name or id (match by id is attempted
     * first).
     * 
     * @param nameOrId
     *            name or id of the form to be worked with.
     */
    public void setWorkingForm(String nameOrId) {
        setWorkingForm(getForm(nameOrId));
    }

    private void setWorkingForm(HtmlForm newForm) {
        if (newForm == null)
            throw new UnableToSetFormException("Attempted to set form to null.");
        form = newForm;
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
     * Set a form text or password element to the provided value.
     * 
     * @param fieldName
     *            name of the input element
     * @param paramValue
     *            parameter value to submit for the element.
     */
    public void setFormTextOrPassword(String fieldName, String paramValue) {
        checkFormStateWithInput(fieldName);
        getForm().getInputByName(fieldName).setValueAttribute(paramValue);
    }

    /**
     * Return the current value of a form input element. A special attention is
     * given to checkboxes, as we want value of checked element
     * 
     * @param paramName
     *            name of the input element.
     */
    public String getFormInputValue(String paramName) {
        checkFormStateWithInput(paramName);
        HtmlRadioButtonInput rbtn = getForm().getCheckedRadioButton(paramName);
        if (rbtn != null)
            return rbtn.getValueAttribute();
        try {
            return getForm().getInputByName(paramName).getValueAttribute();
        } catch (ElementNotFoundException e) {

        }
        throw new RuntimeException("getFormParameterValue a échoué");
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

    /**
     * Return the HttpUnit Button with a given id.
     * 
     * @param buttonId
     */
    public ClickableElement getButton(String buttonId) {
        HtmlElement btn = null;
        try {
            btn = ((HtmlPage) win.getEnclosedPage())
                    .getHtmlElementById(buttonId);
        } catch (ElementNotFoundException e) {
            // Non trouvé
            return null;
        }
        if (btn instanceof HtmlButton || btn instanceof HtmlButtonInput
                || btn instanceof HtmlSubmitInput
                || btn instanceof HtmlResetInput)
            return (ClickableElement) btn;
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
                        Arrays.asList(new String[] { "button" }));
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

    /**
     * Return true if given text is present anywhere in the current response.
     * 
     * @param text
     *            string to check for.
     */
    public boolean isTextInResponse(String text) {
        return (getTestContext().toEncodedString(
                win.getEnclosedPage().getWebResponse().getContentAsString())
                .indexOf(text) >= 0);
    }

    /**
     * Return true if given regexp has a match anywhere in the current response.
     * 
     * @param regexp
     *            regexp to match.
     */
    public boolean isMatchInResponse(String regexp) {
        RE re = getRE(regexp);
        return re.match(getTestContext().toEncodedString(
                win.getEnclosedPage().getWebResponse().getContentAsString()));
    }

    public boolean isCheckboxSelected(String checkBoxName) {
        HtmlCheckBoxInput cb = (HtmlCheckBoxInput) getForm().getInputByName(
                checkBoxName);
        return cb.isChecked();
    }

    public boolean isCheckboxNotSelected(String checkBoxName) {
        HtmlCheckBoxInput cb = (HtmlCheckBoxInput) getForm().getInputByName(
                checkBoxName);
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
        HtmlTable table = getWebTableBySummaryOrId(tableSummaryOrId);
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
        HtmlTable table = getWebTableBySummaryOrId(tableSummaryOrId);
        if (table == null) {
            throw new RuntimeException("No table with summary or id ["
                    + tableSummaryOrId + "] found in response.");
        }
        RE re = getRE(regexp);
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; table.getCellAt(row, col) != null; col++) {
                HtmlTableCell cell = table.getCellAt(row, col);
                if (cell != null) {
                    String cellHtml = cell.asText();
                    if (re.match(cellHtml)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String[][] getTableBySummaryOrIdAsText(String tableSummaryOrId) {
        HtmlTable table = getWebTableBySummaryOrId(tableSummaryOrId);
        int size = getTableColCount(table);
        String[][] result = new String[table.getRowCount()][size];
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < size; j++) {
                HtmlTableCell cell = table.getCellAt(i, j);
                result[i][j] = cell != null ? cell.asText() : "";
            }
        }
        return result;
    }

    private int getTableColCount(HtmlTable table) {
        int result = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            HtmlTableRow row = table.getRow(i);
            result = Math.max(row.getCells().size(), result);
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
    public HtmlTable getWebTableBySummaryOrId(String tableSummaryOrId) {
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

    public boolean isTableBySummaryOrIdExists(String tableSummaryOrId) {
        HtmlTable table = getWebTableBySummaryOrId(tableSummaryOrId);
        return (table != null);
    }

    /**
     * Submit the current form with the default submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    public void submit() {
        try {
            getForm().submit();
        } catch (IOException e) {
            throw new RuntimeException(
                    "HtmlUnit Error submitting form using default submit button, "
                            + "check that form has single submit button, otherwise use submit(name): \n"
                            + ExceptionUtility.stackTraceToString(e));
        }
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
    public void resetForm() {
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
     */
    public boolean isLinkPresentWithText(String linkText) {
        return getLinkWithText(linkText) != null;
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
        return getLinkWithText(linkText, index) != null;
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
        return getLinkWithImage(imageFileName, 0) != null;
    }

    public boolean isLinkPresentWithImage(String imageFileName, int index) {
        return getLinkWithImage(imageFileName, index) != null;
    }

    /**
     * Return true if a link is present in the current response with the
     * specified id.
     * 
     * @param anId
     *            link id to check for.
     */
    public boolean isLinkPresent(String anId) {
        try {
            ((HtmlPage) win.getEnclosedPage()).getHtmlElementById(anId);
        } catch (ElementNotFoundException e) {
            return false;
        }
        return true;
    }

    /*
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithText(java.lang.String)
     */
    public boolean isLinkPresentWithExactText(String linkText) {
        throw new UnsupportedOperationException("isLinkPresentWithText");
    }

    /*
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isLinkPresentWithText(java.lang.String,
     *      int)
     */
    public boolean isLinkPresentWithExactText(String linkText, int index) {
        throw new UnsupportedOperationException("isLinkPresentWithText");
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

    /**
     * Navigate by submitting a request based on a link containing the specified
     * text. A RuntimeException is thrown if no such link can be found.
     * 
     * @param linkText
     *            text which link to be navigated should contain.
     */
    public void clickLinkWithText(String linkText) {
        clickLinkWithText(linkText, 0);
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

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
        HtmlCheckBoxInput cb = (HtmlCheckBoxInput) getForm().getInputByName(
                checkBoxName);
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
        HtmlCheckBoxInput cb = (HtmlCheckBoxInput) getForm().getInputByName(
                checkBoxName);
        if (cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed :" + e);
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
        clickXPathElement("//a[@id=\"" + anID + "\"]");
    }

    /**
     * Clicks element by xpath. A RuntimeException is thrown if no such element
     * can be found or if element is not clickable.
     * 
     * @param xpath
     *            xpath of element to be clicked.
     */
    public void clickXPathElement(String xpath) {
        HtmlElement e = getXPathElement(xpath);
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

    private HtmlAnchor getLinkWithText(String linkText) {
        return getLinkWithText(linkText, 0);
    }

    private HtmlAnchor getLinkWithImage(String filename, int index) {
        return (HtmlAnchor) getXPathElement("(//a[img[contains(@src,\""
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
        clickLinkWithImage(imageFileName, 0);
    }

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
     * Return a string array of select box option labels.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String[] getOptionsFor(String selectName) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        ArrayList result = new ArrayList();
        List opts = sel.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            HtmlOption opt = (HtmlOption) opts.get(i);
            result.add(opt.asText());
        }
        return (String[]) result.toArray(new String[0]);
    }

    /**
     * Return a string array of select box option values.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String[] getOptionValuesFor(String selectName) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        ArrayList result = new ArrayList();
        List opts = sel.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            HtmlOption opt = (HtmlOption) opts.get(i);
            result.add(opt.getValueAttribute());
        }
        return (String[]) result.toArray(new String[0]);
    }

    /**
     * Return the label of the currently selected item in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String getSelectedOption(String selectName) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        List opts = sel.getSelectedOptions();
        return ((HtmlOption) opts.get(0)).asText();
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
        String[] opts = getOptionsFor(selectName);
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(option))
                return getOptionValuesFor(selectName)[i];
        }
        throw new RuntimeException("Unable to find option " + option + " for "
                + selectName);
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
        String[] opts = getOptionsFor(selectName);
        for (int i = 0; i < opts.length; i++) {
            String opt = opts[i];
            if (opt.equals(optionLabel))
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
    public boolean hasSelectOptionByValue(String selectName, String optionValue) {
        String[] opts = getOptionValuesFor(selectName);
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(optionValue))
                return true;
        }
        return false;
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
        HtmlSelect sel = getForm().getSelectByName(selectName);
        List l = sel.getOptions();
        for (int i = 0; i < l.size(); i++) {
            HtmlOption opt = (HtmlOption) l.get(i);
            if (opt.asText().equals(option)) {
                sel.setSelectedAttribute(opt, true);
                return;
            }
        }
        throw new RuntimeException("Option label not found");
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
        HtmlSelect sel = getForm().getSelectByName(selectName);
        sel.setSelectedAttribute(option, true);
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
    public boolean isTextInElement(HtmlElement element, String text) {
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
    public boolean isMatchInElement(HtmlElement element, String regexp) {
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
     * Make the window with the given name in the current conversation active.
     * 
     * @param windowName
     */
    public void gotoWindow(String windowName) {
        setMainWindow(getWindow(windowName));
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

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     */
    public void gotoFrame(String frameName) {
        win = getFrame(frameName);
    }

    public boolean isFrameExists(String frameName) {
        return getFrame(frameName) != null;
    }

    /**
     * Return the response for the given frame in the current conversation.
     * 
     * @param frameName
     */
    public WebWindow getFrame(String frameName) {
        return ((HtmlPage) win.getEnclosedPage()).getFrameByName(frameName);
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#checkCheckbox(java.lang.String,
     *      java.lang.String)
     */
    public void checkCheckbox(String checkBoxName, String value) {
        List l = getForm().getInputsByName(checkBoxName);
        for (int i = 0; i < l.size(); i++) {
            HtmlCheckBoxInput cb = (HtmlCheckBoxInput) l.get(i);
            if (cb.getValueAttribute().equals(value) && !cb.isChecked())
                try {
                    cb.click();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("checkCheckbox failed :" + e);
                }
        }
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#constructNewDialog(java.lang.String,
     *      net.sourceforge.jwebunit.TestContext)
     */
    public IJWebUnitDialog constructNewDialog(String url, TestContext context) {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpCookies(java.io.PrintStream)
     */
    public void dumpCookies(PrintStream stream) {
        final HttpState stateForUrl = wc.getWebConnection().getState();
        Cookie[] cookies = stateForUrl.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            stream.println(cookies[i].toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse()
     */
    public void dumpResponse() {
        dumpResponse(System.out);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse(java.io.PrintStream)
     */
    public void dumpResponse(PrintStream stream) {
        Page page = win.getEnclosedPage();
        if (page instanceof HtmlPage) {
            stream.println(((HtmlPage) page).asXml());
        }
        if (page instanceof XmlPage) {
            stream.println(((XmlPage) page).getContent());
        }
        if (page instanceof TextPage) {
            stream.println(((TextPage) page).getContent());
        }
        if (page instanceof UnexpectedPage) {
            int data;
            try {
                InputStream in = ((UnexpectedPage) page).getInputStream();
                while ((data = in.read()) > -1) {
                    // Just print the data
                    Character c = new Character((char) data);
                    stream.print(c + "");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     *      java.lang.String[][], java.io.PrintStream)
     */
    public void dumpTable(String tableNameOrId, String[][] table,
            PrintStream stream) {
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookieValue(java.lang.String)
     */
    public String getCookieValue(String cookieName) {
        throw new UnsupportedOperationException("getCookieValue");
    }

    private HtmlElement getHtmlElementForLabel(String formElementLabel) {
        return getXPathElement("getHtmlElementForLabel");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementNameBeforeLabel(java.lang.String)
     */
    public String getFormElementNameBeforeLabel(String formElementLabel) {
        throw new UnsupportedOperationException("getFormElementNameBeforeLabel");
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementValueBeforeLabel(java.lang.String)
     */
    public String getFormElementValueBeforeLabel(String formElementLabel) {
        throw new UnsupportedOperationException("getFormElementValueBeforeLabel");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementValueForLabel(java.lang.String)
     */
    public String getFormElementValueForLabel(String formElementLabel) {
        throw new UnsupportedOperationException("getFormElementValueForLabel");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormParameterValue(java.lang.String)
     */
    public String getFormParameterValue(String paramName) {
        try {
            return ((HtmlOption) getForm().getSelectByName(paramName)
                    .getSelectedOptions().get(0)).getValueAttribute();
        } catch (ElementNotFoundException e) {

        }
        return getFormInputValue(paramName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponsePageTitle()
     */
    public String getResponsePageTitle() {
        return getCurrentPageTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponseText()
     */
    public String getResponseText() {
        return getCurrentPage().getWebResponse().getContentAsString();
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getTableBySummaryOrId(java.lang.String)
     */
    public String[][] getTableBySummaryOrId(String tableSummaryOrId) {
        return getTableBySummaryOrIdAsText(tableSummaryOrId);
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFormParameterLabeled(java.lang.String)
     */
    public boolean hasFormParameterLabeled(String paramLabel) {
        throw new UnsupportedOperationException("hasFormParameterLabeled");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFormParameterNamed(java.lang.String)
     */
    public boolean hasFormParameterNamed(String paramName) {
        if (hasFormSelectNamed(paramName))
            return true;
        return hasFormInputNamed(paramName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isFramePresent(java.lang.String)
     */
    public boolean isFramePresent(String frameName) {
        return isFrameExists(frameName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWebTableBySummaryOrIdPresent(java.lang.String)
     */
    public boolean isWebTableBySummaryOrIdPresent(String tableSummaryOrId) {
        return isTableBySummaryOrIdExists(tableSummaryOrId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWindowByTitlePresent(java.lang.String)
     */
    public boolean isWindowByTitlePresent(String title) {
        return isWindowByTitleExists(title);

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWindowPresent(java.lang.String)
     */
    public boolean isWindowPresent(String windowName) {
        return isWindowExists(windowName);
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setFormParameter(java.lang.String,
     *      java.lang.String)
     */
    public void setFormParameter(String paramName, String paramValue) {
        setFormTextOrPassword(paramName, paramValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#uncheckCheckbox(java.lang.String,
     *      java.lang.String)
     */
    public void uncheckCheckbox(String checkBoxName, String value) {
        List l = getForm().getInputsByName(checkBoxName);
        for (int i = 0; i < l.size(); i++) {
            HtmlCheckBoxInput cb = (HtmlCheckBoxInput) l.get(0);
            if (cb.getValueAttribute().equals(value) && cb.isChecked())
                try {
                    cb.click();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("uncheckCheckbox failed :" + e);
                }
        }
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
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#reset()
     */
    public void reset() throws TestingEngineResponseException {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setScriptingEnabled(boolean)
     */
    public void setScriptingEnabled(boolean value) {
        // This variable is used to set Javascript before wc is instancied
        jsEnabled = value;
        if (wc != null) {
            wc.setJavaScriptEnabled(value);
        }
    }

}