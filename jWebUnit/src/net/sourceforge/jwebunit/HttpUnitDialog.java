/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.meterware.httpunit.WebClientListener;
import com.meterware.httpunit.WebClient;
import net.sourceforge.jwebunit.util.ExceptionUtility;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Acts as the wrapper for HttpUnit access.  A dialog is initialized with a
 * given URL, and maintains conversational state as the dialog progresses through link navigation,
 * form submission, etc.  Public access is provided for wrappered HttpUnit objects.
 *
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class HttpUnitDialog {
    private WebConversation wc;
    private WebResponse resp;
    private TestContext context;
    private WebForm form;

    /**
     * Begin a dialog with an initial URL and test client context.
     *
     * @param initialURL absolute url at which to begin dialog.
     * @param context contains context information for the test client.
     */
    public HttpUnitDialog(String initialURL, TestContext context) {
        this.context = context;
        initWebConversation();
        try {
            resp = wc.getResponse(new GetMethodWebRequest(initialURL));
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private void initWebConversation() {
        wc = new WebConversation();

        if (context != null) {
            if (context.hasAuthorization())
                wc.setAuthorization(context.getUser(), context.getPassword());
            if (context.hasCookies()) {
                List cookies = context.getCookies();
                for (Iterator iter = cookies.iterator(); iter.hasNext();) {
                    Cookie c = (Cookie) iter.next();
                    wc.addCookie(c.getName(), c.getValue());
                }
            }
        }

        wc.addClientListener(new WebClientListener() {
            public void requestSent(WebClient webClient, WebRequest webRequest) {
            }

            public void responseReceived(WebClient webClient, WebResponse webResponse) {
                resp = webResponse;
                form = null;
            }
        });
    }

    /**
     * Return the HttpUnit WebConversation object for this dialog.
     */
    public WebConversation getWebConversation() {
        return wc;
    }

    /**
     * Return the HttpUnit object which represents the current response.
     */
    public WebResponse getResponse() {
        return resp;
    }

    /**
     * Return the string representation of the current response, encoded
     * as specified by the current {@link net.sourceforge.jwebunit.TestContext}.
     */
    public String getResponseText() {
        try {
            return context.toEncodedString(resp.getText());
        } catch (IOException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Return the page title of the current response page, encoded as specified
     * by the current {@link net.sourceforge.jwebunit.TestContext}.
     */
    public String getResponsePageTitle() {
        try {
            return context.toEncodedString(resp.getTitle());
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * <p>Return the current form active for the dialog.  A form from the dialog's
     * response is implicitly identified as active when an element from the form is inspected
     * or set:</p>
     * <ul>
     * <li>{@link #getFormParameterValue}</li>
     * <li>{@link #hasFormParameterNamed}</li>
     * <li>{@link #getSubmitButton}</li>
     * <li>{@link #setFormParameter}</li>
     * </ul>
     * <p>The active form can also be explicitly set by {@link #setWorkingForm}.</p>
     * <p>If this method is called without the form having been implicitly or explicitly
     * set, it will attempt to return the default first form on the response.</p>
     *
     * @exception UnableToSetFormException This runtime assertion failure will be raised if there is
     * no form on the response.
     * @return HttpUnit WebForm object representing the current active form from the
     * response.
     */
    public WebForm getForm() {
        if (form == null && hasForm())
            setWorkingForm(getForm(0));
        return form;
    }

    private WebForm getForm(int formIndex) {
        try {
            return resp.getForms()[formIndex];
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private WebForm getForm(String nameOrID) {
        try {
            WebForm f = resp.getFormWithID(nameOrID);
            return (f != null) ? f : resp.getFormWithName(nameOrID);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private WebForm getFormWithButton(String buttonName) {
        if (hasForm()) {
            for (int i = 0; i < getForms().length; i++) {
                WebForm webForm = getForms()[i];
                if (webForm.getSubmitButton(buttonName) != null)
                    return webForm;
            }
        }
        return null;
    }

    private WebForm getFormWithParameter(String paramName) {
        if (hasForm()) {
            for (int i = 0; i < getForms().length; i++) {
                WebForm webForm = getForms()[i];
                String[] names = webForm.getParameterNames();
                for (int j = 0; j < names.length; j++) {
                    if (names[j].equals(paramName)) return webForm;
                }

            }
        }
        return null;
    }


    private WebForm[] getForms() {
        try {
            return resp.getForms();
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private void checkFormStateWithParameter(String paramName) {
        if (form == null) {
            try {
                setWorkingForm(getFormWithParameter(paramName));
            } catch (UnableToSetFormException e) {
                throw new UnableToSetFormException("Unable to set form based on parameter [" + paramName + "].");
            }
        }
    }

    private void checkFormStateWithButton(String buttonName) {
        if (form == null) {
            setWorkingForm(getFormWithButton(buttonName));
        }
    }

    /**
     * Set the form on the current response that the client wishes to work
     * with explicitly by either the form name or id (match by id is attempted
     * first).
     *
     * @param nameOrId name or id of the form to be worked with.
     */
    public void setWorkingForm(String nameOrId) {
        setWorkingForm(getForm(nameOrId));
    }

    private void setWorkingForm(WebForm newForm) {
        if (newForm == null)
            throw new UnableToSetFormException("Attempted to set form to null.");
        form = newForm;
    }

    /**
     * Return true if the current response contains a form.
     */
    public boolean hasForm() {
        try {
            return resp.getForms().length > 0;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return true if the current response contains a specific form.
     *
     * @param nameOrID name of id of the form to check for.
     */
    public boolean hasForm(String nameOrID) {
        return getForm(nameOrID) != null;
    }

    /**
     * Return true if a form parameter (input element) is present
     * on the current response.
     *
     * @param paramName name of the input element to check for
     */
    public boolean hasFormParameterNamed(String paramName) {
        try {
            checkFormStateWithParameter(paramName);
        } catch (UnableToSetFormException e) {
            return false;
        }
        return getForm().hasParameterNamed(paramName);
    }

    /**
     * Set a form parameter / input element to the provided value.
     *
     * @param paramName name of the input element
     * @param paramValue parameter value to submit for the element.
     */
    public void setFormParameter(String paramName, String paramValue) {
        checkFormStateWithParameter(paramName);
        getForm().setParameter(paramName, paramValue);
    }

    /**
     * Return the current value of a form input element.
     *
     * @param paramName name of the input element.
     */
    public String getFormParameterValue(String paramName) {
        checkFormStateWithParameter(paramName);
        return getForm().getParameterValue(paramName);
    }

    /**
     * Specify that no parameter value should be submitted for a given input element.
     * Typically used to uncheck check boxes.
     *
     * @param paramName name of the input element.
     */
    public void removeFormParameter(String paramName) {
        checkFormStateWithParameter(paramName);
        getForm().removeParameter(paramName);
    }

    /**
     * Return true if a form parameter (input element) is present
     * on the current response preceded by a given label.
     *
     * @param paramLabel label of the input element to check for
     */
    public boolean hasFormParameterLabeled(String paramLabel) {
        return null != getFormElementNameForLabel(paramLabel);
    }

    /**
     * Return the name of a form parameter (input element) on the current
     * response preceded by a givel label.
     *
     * @param formElementLabel label of the input element to fetch name.
     */
    public String getFormElementNameForLabel(String formElementLabel) {
        try {
            Document document = getResponse().getDOM();
            Element root = document.getDocumentElement();
            NodeList forms = root.getElementsByTagName("form");

            for (int i = 0; i < forms.getLength(); i++) {
                Element form = (Element) forms.item(i);
                FormWalker walker = new FormWalker(form);
                Element formElement =
                        walker.getFormElementWithLabel(formElementLabel);
                if (formElement != null) {
                    return formElement.getAttribute("name");
                }
            }

            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return the HttpUnit SubmitButton with a given name.
     *
     * @param buttonName name of button.
     */
    public SubmitButton getSubmitButton(String buttonName) {
        checkFormStateWithButton(buttonName);
        return getForm().getSubmitButton(buttonName);
    }

    /**
     * Return true if given text is present anywhere in the current response.
     *
     * @param text string to check for.
     */
    public boolean isTextInResponse(String text) {
        try {
            return (context.toEncodedString(resp.getText()).indexOf(text) >= 0);
        } catch (IOException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Return true if given text is present in a specified table of the response.
     *
     * @param tableSummaryOrId table summary or id to inspect for expected text.
     * @param text expected text to check for.
     */
    public boolean isTextInTable(String tableSummaryOrId, String text) {
        WebTable table = getWebTableBySummaryOrId(tableSummaryOrId);
        if (table == null) {
            throw new RuntimeException(
                    "No table with summary or id [" + tableSummaryOrId + "] found in response.");
        }
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                TableCell cell = table.getTableCell(row, col);
                if (cell != null) {
                    String cellHtml = getNodeHtml(cell.getDOM());
                    if (cellHtml.indexOf(text) != -1)
                        return true;
                }
            }
        }
        return false;
    }

    private String getNodeHtml(Node node) {
        String nodeHtml = "";
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                nodeHtml += "<" + child.getNodeName() + ">";
            }
            if (child.hasChildNodes()) {
                nodeHtml += getNodeHtml(child);
            } else {
                nodeHtml += child.getNodeValue();
            }
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                nodeHtml += "</" + child.getNodeName() + ">";
            }
        }
        return context.toEncodedString(nodeHtml);
    }

    /**
     * Return the HttpUnit WebTable object representing a specified table
     * in the current response.  Null is returned if a parsing exception
     * occurs looking for the table or no table with the id or summary could
     * be found.
     *
     * @param tableSummaryOrId summary or id of the table to return.
     */
    public WebTable getWebTableBySummaryOrId(String tableSummaryOrId) {
        WebTable table;
        try {
            table =  resp.getTableWithSummary(tableSummaryOrId);
            if (table == null) {
                table = resp.getTableWithID(tableSummaryOrId);
            }
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
        return table;
    }

   /**
    * Return a sparse array (rows or columns without displayable text are removed)
    * for a given table in the response.
    *
    * @param tableSummaryOrId summary or id of the table.
    */
    public String[][] getSparseTableBySummaryOrId(String tableSummaryOrId) {
        WebTable table = getWebTableBySummaryOrId(tableSummaryOrId);
        table.purgeEmptyCells();
        String[][] sparseTableCellValues = table.asText();
        return sparseTableCellValues;
    }

    /**
     * Submit the current form with the default submit button.
     * See {@link #getForm} for an explanation of how the current form is established.
     */
    public void submit() {
        WebRequest formRequest = getForm().getRequest((SubmitButton) null);
        submitRequest(formRequest);
    }

    /**
     * Submit the current form with the specifed submit button.
     * See {@link #getForm} for an explanation of how the current form is established.
     *
     * @param buttonName name of the button to use for submission.
     */
    public void submit(String buttonName) {
        submitRequest(getForm().getRequest(buttonName));
    }

    private void submitRequest(WebRequest aWebRequest) {
        try {
            resp = wc.getResponse(aWebRequest);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Return true if a link is present in the current response containing the
     * specified text (note that HttpUnit uses contains rather than an exact
     * match - if this is a problem consider using ids on the links to uniquely
     * identify them).
     *
     * @param linkText text to check for in links on the response.
     */
    public boolean isLinkPresentWithText(String linkText) {
        try {
            return (resp.getLinkWith(linkText) != null);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Return true if a link is present with a given image based on filename of image.
     *
     * @param imageFileName A suffix of the image's filename; for example, to match
     *                      <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    public boolean isLinkPresentWithImage(String imageFileName) {
        try {
            return (resp.getFirstMatchingLink(new LinkImagePredicate(), imageFileName) != null);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Return true if a link is present in the current response with the
     * specified id.
     *
     * @param anId link id to check for.
     */
    public boolean isLinkPresent(String anId) {
        try {
            return resp.getLinkWithID(anId) != null;
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Navigate by submitting a request based on a link containing the
     * specified text.  A RuntimeException is thrown if no such link can be found.
     *
     * @param linkText text which link to be navigated should contain.
     */
    public void clickLinkWithText(String linkText) {
        WebLink link = null;
        try {
            link = resp.getLinkWith(linkText);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        if (link == null)
            throw new RuntimeException("No Link found for \"" + linkText + "\"");
        submitRequest(link.getRequest());
    }

    /**
     * Navigate by submitting a request based on a link with a given ID.  A
     * RuntimeException is thrown if no such link can be found.
     *
     * @param anID id of link to be navigated.
     */
    public void clickLink(String anID) {
        WebLink link = null;
        try {
            link = resp.getLinkWithID(anID);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        if (link == null)
            throw new RuntimeException("No Link found with ID \"" + anID + "\"");
        submitRequest(link.getRequest());

    }

    /**
     * Navigate by submitting a request based on a link with a given image file name.
     * A RuntimeException is thrown if no such link can be found.
     *
     *
     * @param imageFileName A suffix of the image's filename; for example, to match
     *                      <tt>"images/my_icon.png"<tt>, you could just pass in
     *                      <tt>"my_icon.png"<tt>.
     */
    public void clickLinkWithImage(String imageFileName) {
        WebLink link = null;
        try {
            link = resp.getFirstMatchingLink(new LinkImagePredicate(), imageFileName);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        if (link == null)
            throw new RuntimeException("No Link found with imageFileName \"" + imageFileName + "\"");
        submitRequest(link.getRequest());
    }

    /**
     * Return true if a radio group contains the indicated option.
     *
     * @param radioGroup name of the radio group.
     * @param radioOption value of the option to check for.
     */
    public boolean hasRadioOption(String radioGroup, String radioOption) {
        WebForm[] forms = getForms();
        for (int i = 0; i < forms.length; i++) {
            WebForm form = forms[i];
            String[] opts = form.getOptionValues(radioGroup);
            for (int j = 0; j < opts.length; j++) {
                String opt = opts[j];
                if (radioOption.equals(opt)) return true;
            }
        }
        return false;
    }

    /**
     * Return a string array of select box option labels.
     *
     * @param selectName name of the select box.
     */
    public String[] getOptionsFor(String selectName) {
        return getForm().getOptions(selectName);
    }

    /**
     * Return a string array of select box option values.
     *
     * @param selectName name of the select box.
     */
    public String[] getOptionValuesFor(String selectName) {
        return getForm().getOptionValues(selectName);
    }

    /**
     * Return the label of the currently selected item in a select box.
     *
     * @param selectName name of the select box.
     */
    public String getSelectedOption(String selectName) {
        String val = getFormParameterValue(selectName);
        String[] vals = getOptionValuesFor(selectName);
        for (int i = 0; i < vals.length; i++) {
            if (vals[i].equals(val))
                return getOptionsFor(selectName)[i];
        }
        return null;
    }

    /**
     * Get the value for a given option of a select box.
     *
     * @param selectName name of the select box.
     * @param option label of the option.
     */
    public String getValueForOption(String selectName, String option) {
        String[] opts = getOptionsFor(selectName);
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(option))
                return getOptionValuesFor(selectName)[i];
        }
        throw new RuntimeException("Unable to find option " + option + " for " + selectName);
    }

    /**
     * Select an option of a select box by display label.
     *
     * @param selectName name of the select box.
     * @param option label of the option to select.
     */
    public void selectOption(String selectName, String option) {
        setFormParameter(selectName, getValueForOption(selectName, option));
    }

    /**
     * Return the org.w3c.dom.Element in the current response by id.
     *
     * @param anID id of the element.
     */
    public Element getElement(String anID) {
        try {
            return walkDOM(getResponse().getDOM().getDocumentElement(), anID);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private Element walkDOM(Element element, String anID) {
        if (element.getAttribute("id").equals(anID) || element.getAttribute("ID").equals(anID))
            return element;
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element el = walkDOM((Element) child, anID);
                if (el != null) return el;
            }
        }
        return null;
    }

    /**
     * Return true if a given string is contained within the specified element.
     *
     * @param element org.w3c.com.Element to inspect.
     * @param text text to check for.
     */
    public boolean isTextInElement(Element element, String text) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                if (((Text) child).getData().indexOf(text) != -1) return true;
            }

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (isTextInElement((Element) child, text)) return true;
            }
        }
        return false;
    }
}