/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.util.ExceptionUtility;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElementPredicate;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebClient;
import com.meterware.httpunit.WebClientListener;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.meterware.httpunit.WebWindow;
import com.meterware.httpunit.cookies.CookieJar;

/**
 * Acts as the wrapper for HttpUnit access. A dialog is initialized with a given URL, and maintains conversational
 * state as the dialog progresses through link navigation, form submission, etc. Public access is provided for
 * wrappered HttpUnit objects.
 * 
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class HttpUnitDialog extends CompositeJWebUnitDialog {
	
	private WebClient wc;
	private WebResponse resp;
	private TestContext testContext;
	private WebForm form;
	private Map multiselectMap = new HashMap();

	/**
	 * Constructor for creating a default testing engine for jWebUnit.
	 * If the dialog is not specified then jWebUnit will default to using httpunit.
	 */
	public HttpUnitDialog() {
	    super();
	}
	
	/**
	 * Begin a dialog with an initial URL and test client context.
	 * 
	 * @param initialURL
	 *            absolute url at which to begin dialog.
	 * @param context
	 *            contains context information for the test client.
	 */
	public void beginAt(String initialURL, TestContext context) {
		this.setTestContext(context);
		initWebClient();
		try {
			resp = wc.getResponse(new GetMethodWebRequest(initialURL));
		} catch (Exception e) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
		}
	}

	private void initWebClient() {
		wc = (getTestContext() != null) ? getTestContext().getWebClient() : new WebConversation();

		wc.addClientListener(new WebClientListener() {
			public void requestSent(WebClient webClient, WebRequest webRequest) {
			}

            public void responseReceived(WebClient webClient, WebResponse webResponse) {
				resp = webClient.getCurrentPage();
                form = null;
                multiselectMap.clear();
            }
        });
    }
	/**
	 * Return the window with the given name in the current conversation.
	 * 
	 * @param windowName
	 */
	public WebWindow getWindow(String windowName) {
		return wc.getOpenWindow(windowName);
	}
	/**
	 * Return the HttpUnit WebClient object for this dialog.
	 */
	public WebClient getWebClient() {
		return wc;
	}

	/**
	 * Return the HttpUnit object which represents the current response.
	 */
	public WebResponse getResponse() {
		return resp;
	}

    /**
     * Return the first open window with the given title.
     */
    public WebWindow getWindowByTitle(String title) {
        WebWindow[] webWindows = wc.getOpenWindows();
        for (int i = 0; i < webWindows.length; i++) {
            WebWindow window = webWindows[i];
            try {
                if (getTestContext().toEncodedString(window.getCurrentPage().getTitle())
                        .equals(title)) { return window; }
            } catch (SAXException e) {
                throw new RuntimeException(ExceptionUtility
                        .stackTraceToString(e));
            }
        }
        return null;
    }

    /**
     * Return the string representation of the current response, encoded as
     * specified by the current {@link net.sourceforge.jwebunit.TestContext}.
     */
    public String getResponseText() {
        try {
            return getTestContext().toEncodedString(resp.getText());
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
            return getTestContext().toEncodedString(resp.getTitle());
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Contributed by Vivek Venugopalan.
     */
    private CookieJar getCookies() {
        return (getResponse() != null) ? new CookieJar(getResponse()) : null;
    }

    public boolean hasCookie(String cookieName) {
        CookieJar respJar = getCookies();
        String[] cookieNames = respJar.getCookieNames();

        for (int i = 0; i < cookieNames.length; i++) {
            if (cookieNames[i].equals(cookieName)) { return true; }
        }
        return false;
    }

    public String getCookieValue(String cookieName) {
        return getCookies().getCookieValue(cookieName);
    }

    //TODO: Move other dump methods to dialog!!

    /**
     * <p>
     * Return the current form active for the dialog. A form from the dialog's
     * response is implicitly identified as active when an element from the form
     * is inspected or set:
     * </p>
     * <ul>
     * <li>{@link #getFormParameterValue}</li>
     * <li>{@link #hasFormParameterNamed}</li>
     * <li>{@link #getSubmitButton}</li>
     * <li>{@link #setFormParameter}</li>
     * </ul>
     * <p>
     * The active form can also be explicitly set by {@link #setWorkingForm}.
     * </p>
     * <p>
     * If this method is called without the form having been implicitly or
     * explicitly set, it will attempt to return the default first form on the
     * response.
     * </p>
     * 
     * @exception UnableToSetFormException
     *                This runtime assertion failure will be raised if there is
     *                no form on the response.
     * @return HttpUnit WebForm object representing the current active form from
     *         the response.
     */
    public WebForm getForm() {
        if (form == null && hasForm()) setWorkingForm(getForm(0));
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

    private void setWorkingForm(WebForm newForm) {
        if (newForm == null)
                throw new UnableToSetFormException(
                        "Attempted to set form to null.");
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
     * @param paramName
     *            name of the input element to check for
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
     * @param paramName
     *            name of the input element
     * @param paramValue
     *            parameter value to submit for the element.
     */
    public void setFormParameter(String paramName, String paramValue) {
        checkFormStateWithParameter(paramName);
        WebResponse oldPage = getWebClient().getCurrentPage();
        getForm().setParameter(paramName, paramValue);
        //if an onchange event caused our page to change, set response to new
        // page - otherwise leave
        // the response alone.
        if (oldPage != getWebClient().getCurrentPage()) {
            resp = getWebClient().getCurrentPage();
        }
    }

    public void updateFormParameter(String paramName, String paramValue) {
        checkFormStateWithParameter(paramName);
        if (!multiselectMap.containsKey(paramName))
                multiselectMap.put(paramName, new ArrayList());
        List values = (List) multiselectMap.get(paramName);
        if (!values.contains(paramValue)) values.add(paramValue);
        getForm().setParameter(paramName,
                (String[]) values.toArray(new String[0]));
    }

    /**
     * Return the current value of a form input element.
     * 
     * @param paramName
     *            name of the input element.
     */
    public String getFormParameterValue(String paramName) {
        checkFormStateWithParameter(paramName);
        return getForm().getParameterValue(paramName);
    }

    /**
     * Specify that no parameter value should be submitted for a given input
     * element. Typically used to uncheck check boxes.
     * 
     * @param paramName
     *            name of the input element.
     */
    public void removeFormParameter(String paramName) {
        checkFormStateWithParameter(paramName);
        getForm().removeParameter(paramName);
    }

    public void removeFormParameterWithValue(String paramName, String value) {
        checkFormStateWithParameter(paramName);
        if (multiselectMap.containsKey(paramName)) {
            List values = (List) multiselectMap.get(paramName);
            values.remove(value);
            getForm().setParameter(paramName,
                    (String[]) values.toArray(new String[0]));
        }
    }

    /**
     * Return true if a form parameter (input element) is present on the current
     * response preceded by a given label.
     * 
     * @param paramLabel
     *            label of the input element to check for
     */
    public boolean hasFormParameterLabeled(String paramLabel) {
        return null != getFormElementNameForLabel(paramLabel);
    }

    /**
     * Return the name of a form parameter (input element) on the current
     * response preceded by a givel label.
     * 
     * @param formElementLabel
     *            label of the input element to fetch name.
     */
    public String getFormElementNameForLabel(String formElementLabel) {
        try {
            Document document = getResponse().getDOM();
            Element root = document.getDocumentElement();
            NodeList forms = root.getElementsByTagName("form");

            for (int i = 0; i < forms.getLength(); i++) {
                Element form = (Element) forms.item(i);
                TextAndElementWalker walker = new TextAndElementWalker(form,
                        new String[] { "input", "select", "textarea" });
                Element formElement = walker
                        .getElementAfterText(formElementLabel);
                if (formElement != null) { return formElement
                        .getAttribute("name"); }
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
     * @param buttonName
     *            name of button.
     */
    public SubmitButton getSubmitButton(String buttonName) {
        checkFormStateWithButton(buttonName);
        return getForm().getSubmitButton(buttonName);
    }

    public String getSubmitButtonValue(String buttonName) {
        return getSubmitButton(buttonName).getValue().trim();
    }
    
    /**
     * Return the HttpUnit SubmitButton with a given name and value.
     * @param buttonName
     * @pararm buttonValue
     */
    public SubmitButton getSubmitButton(String buttonName, String buttonValue) {
        checkFormStateWithButton(buttonName);
        return getForm().getSubmitButton(buttonName, buttonValue);
    }

    public boolean hasSubmitButton(String buttonName) {
        try {
            return getSubmitButton(buttonName) != null;
        } catch (UnableToSetFormException e) {
            return false;
        }

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
    public Button getButton(String buttonId) {
        return getForm().getButtonWithID(buttonId);
    }

    /**
     * Checks if a button with <code>text</code> is present.
     * 
     * @param text
     *            the text of the button (contents of the value attribute).
     * @return <code>true</code> when the button with text could be found.
     */
    public boolean hasButtonWithText(String text) {
        Button[] buttons = getForm().getButtons();

        boolean found = false;
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            if (button.getValue().equals(text)) {
                found = true;
                break;
            }
        }
        return found;
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
        try {
            return (getTestContext().toEncodedString(resp.getText()).indexOf(text) >= 0);
        } catch (IOException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

	
    public boolean isCheckboxSelected(String checkBoxName) {
        boolean bReturn = false;
        String theFormParameterValue = getFormParameterValue(checkBoxName); 
        if(theFormParameterValue != null && theFormParameterValue.equalsIgnoreCase("on")) {
            bReturn = true;
        }
        return bReturn;
    }

    public boolean isCheckboxNotSelected(String checkBoxName) {
        boolean bReturn = false;
        if(getFormParameterValue(checkBoxName) == null) {
            bReturn = true;
        }
        return bReturn;
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
        WebTable table = getWebTableBySummaryOrId(tableSummaryOrId);
        if (table == null) { throw new RuntimeException(
                "No table with summary or id [" + tableSummaryOrId
                        + "] found in response."); }
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                TableCell cell = table.getTableCell(row, col);
                if (cell != null) {
                    String cellHtml = getNodeHtml(cell.getDOM());
                    if (cellHtml.indexOf(text) != -1) return true;
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
        return getTestContext().toEncodedString(nodeHtml);
    }

    /**
     * Return the text (without any markup) of the tree rooted at node.
     */
    private static String getNodeText(Node node) {
        String nodeText = "";
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.hasChildNodes()) {
                nodeText += getNodeText(child);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                nodeText += ((Text) child).getData();
            }
        }
        return nodeText;
    }

    /**
     * Return the HttpUnit WebTable object representing a specified table in the
     * current response. Null is returned if a parsing exception occurs looking
     * for the table or no table with the id or summary could be found.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table to return.
     */
    public WebTable getWebTableBySummaryOrId(String tableSummaryOrId) {
        WebTable table;
        try {
            table = resp.getTableWithSummary(tableSummaryOrId);
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
     * Return a sparse array (rows or columns without displayable text are
     * removed) for a given table in the response.
     * 
     * @param tableSummaryOrId
     *            summary or id of the table.
     */
    public String[][] getSparseTableBySummaryOrId(String tableSummaryOrId) {
        WebTable table = getWebTableBySummaryOrId(tableSummaryOrId);
        table.purgeEmptyCells();
        String[][] sparseTableCellValues = table.asText();
        return sparseTableCellValues;
    }

    /**
     * Submit the current form with the default submit button. See
     * {@link #getForm}for an explanation of how the current form is
     * established.
     */
    public void submit() {
        try {
            resp = getForm().submit();
        } catch (Exception e) {
            throw new RuntimeException("HttpUnit Error submitting form using default submit button, " +
                    "check that form has single submit button, otherwise use submit(name): \n" + ExceptionUtility.stackTraceToString(e));
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
            getForm().getSubmitButton(buttonName).click();
            resp = wc.getCurrentPage();
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

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
    public void submit(String buttonName, String buttonValue) {
        try {
            getForm().getSubmitButton(buttonName, buttonValue).click();
            resp = wc.getCurrentPage();
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }        
    }

    /**
     * Resets the Dialog
     */
    public void reset() {
    }

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    public void resetForm() {
        getForm().reset();
    }

    protected void submitRequest(WebLink aLink) {
        try {
            aLink.click();
            resp = wc.getCurrentPage();
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
     * @param linkText
     *            text to check for in links on the response.
     */
    public boolean isLinkPresentWithText(String linkText) {
        try {
            return (resp.getLinkWith(linkText) != null);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
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
        try {
            return (resp.getFirstMatchingLink(new LinkImagePredicate(),
                    imageFileName) != null);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
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
            return resp.getLinkWithID(anId) != null;
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Navigate by submitting a request based on a link containing the specified
     * text. A RuntimeException is thrown if no such link can be found.
     * 
     * @param linkText
     *            text which link to be navigated should contain.
     */
    public void clickLinkWithText(String linkText) {
        WebLink link = null;
        try {
            link = resp.getLinkWith(linkText);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        if (link == null)
                throw new RuntimeException("No Link found for \"" + linkText
                        + "\"");
        submitRequest(link);
    }

    public void clickLinkWithText(String linkText, int index) {
        WebLink link = getLinkWithText(linkText, index);
        if (link == null)
                throw new RuntimeException("No Link found for \"" + linkText
                        + "\" with index " + index);
        submitRequest(link);
    }
    
    /**
     * Select a specified checkbox.  If the checkbox is already checked then the checkbox
     * will stay checked.
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
        setFormParameter(checkBoxName, "on");
    }

    public void checkCheckbox(String checkBoxName, String value) {
        updateFormParameter(checkBoxName, value);
    }


    /**
     * Deselect a specified checkbox.  If the checkbox is already unchecked then the checkbox
     * will stay unchecked.
     *
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        removeFormParameter(checkBoxName);
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        removeFormParameterWithValue(checkBoxName, value);
    }
	
	/**
	 * Navigate by submitting a request based on a link with a given ID. A RuntimeException is thrown if no such link
	 * can be found.
	 * 
	 * @param anID
	 *            id of link to be navigated.
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
		submitRequest(link);
	}

    private WebLink getLinkWithText(String linkText, int index) {
        WebLink link = null;
        try {
            WebLink links[] = resp.getLinks();
            int count = 0;
            for (int i = 0; i < links.length; ++i) {
                Node node = links[i].getDOMSubtree();
                if (nodeContainsText(node, linkText)) {
                    if (count == index) {
                        link = links[i];
                        break;
                    } else {
                        count++;
                    }
                }
            }
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        return link;
    }

    public static boolean nodeContainsText(Node node, String linkText) {
        return getNodeText(node).indexOf(linkText) != -1;
    }

    public void clickLinkWithTextAfterText(String linkText, String labelText) {
        WebLink link = getLinkWithTextAfterText(linkText, labelText);
        if (link == null)
                throw new RuntimeException("No Link found for \"" + linkText
                        + "\" with label \"" + labelText + "\"");
        submitRequest(link);
    }

    private WebLink getLinkWithTextAfterText(String linkText, String labelText) {
        try {
            TextAndElementWalker walker = new TextAndElementWalker(resp
                    .getDOM().getDocumentElement(), new String[] { "a" });
            final Element linkElement = walker.getElementWithTextAfterText(
                    linkText, labelText);
            if (linkElement != null) { return resp.getFirstMatchingLink(
                    new SameLinkPredicate(), linkElement); }
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        return null;
    }

    private static class SameLinkPredicate implements HTMLElementPredicate {

        public boolean matchesCriteria(Object found, Object given) {
            WebLink link = (WebLink) found;
            Element foundElement = (Element) link.getDOMSubtree();
            Element givenElement = (Element) given;

            NamedNodeMap foundAttributes = foundElement.getAttributes();
            NamedNodeMap givenAttributes = givenElement.getAttributes();

            if (foundAttributes.getLength() != givenAttributes.getLength()) { return false; }

            for (int i = 0; i < foundAttributes.getLength(); i++) {
                Attr foundAttribute = (Attr) foundAttributes.item(i);
                Attr givenAttribute = (Attr) givenAttributes
                        .getNamedItem(foundAttribute.getName());
                if (!foundAttribute.getValue()
                        .equals(givenAttribute.getValue())) { return false; }
            }

            return true;
        }
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
        WebLink link = null;
        try {
            link = resp.getFirstMatchingLink(new LinkImagePredicate(),
                    imageFileName);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        if (link == null)
                throw new RuntimeException(
                        "No Link found with imageFileName \"" + imageFileName
                                + "\"");
        submitRequest(link);
    }

    /**
     * Click the indicated button (input type=button).
     * 
     * @param buttonId
     */
    public void clickButton(String buttonId) {
        try {
            getButton(buttonId).click();
            resp = wc.getCurrentPage();
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
     * @param selectName
     *            name of the select box.
     */
    public String[] getOptionsFor(String selectName) {
        return getForm().getOptions(selectName);
    }

    /**
     * Return a string array of select box option values.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String[] getOptionValuesFor(String selectName) {
        return getForm().getOptionValues(selectName);
    }

    /**
     * Return the label of the currently selected item in a select box.
     * 
     * @param selectName
     *            name of the select box.
     */
    public String getSelectedOption(String selectName) {
        String val = getFormParameterValue(selectName);
        String[] vals = getOptionValuesFor(selectName);
        for (int i = 0; i < vals.length; i++) {
            if (vals[i].equals(val)) return getOptionsFor(selectName)[i];
        }
        return null;
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
     * Return true if a select box has the given option (by label).
     * 
     * @param selectName
     *            name of the select box.
     * @param optionLabel
     *            label of the option.
     * @return
     */
    public boolean hasSelectOption(String selectName, String optionLabel) {
        String[] opts = getOptionsFor(selectName);
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(optionLabel)) return true;
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
        setFormParameter(selectName, getValueForOption(selectName, option));
    }

    /**
     * Return the org.w3c.dom.Element in the current response by id.
     * 
     * @param anID
     *            id of the element.
     */
    public Element getElement(String anID) {
        try {
            return walkDOM(getResponse().getDOM().getDocumentElement(), anID);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private Element walkDOM(Element element, String anID) {
        if (element.getAttribute("id").equals(anID)
                || element.getAttribute("ID").equals(anID)) return element;
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
     * @param element
     *            org.w3c.com.Element to inspect.
     * @param text
     *            text to check for.
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
        setMainWindow(wc.getOpenWindows()[0]);
    }

    private void setMainWindow(WebWindow win) {
        wc.setMainWindow(win);
        resp = wc.getMainWindow().getCurrentPage();
    }

    /**
     * Make the frame with the given name active in the current conversation.
     * 
     * @param frameName
     */
    public void gotoFrame(String frameName) {
        resp = getFrame(frameName);
        form = null;
    }

    /**
     * Return the response for the given frame in the current conversation.
     * 
     * @param frameName
     */
    public WebResponse getFrame(String frameName) {
        return wc.getMainWindow().getFrameContents(frameName);
    }

    /**
     * Patch sumbitted by Alex Chaffee.
     */
    public void gotoPage(String url) throws TestingEngineResponseException {
        try {
            resp = wc.getResponse(url);
        } catch (Exception e) {
            throw new TestingEngineResponseException(ExceptionUtility.stackTraceToString(e));
        }
    }

    /**
     * Dumps out all the cookies in the response received. The output is written
     * to the passed in Stream
     * 
     * @return void
     */
    public void dumpCookies(PrintStream stream) {
        CookieJar respJar = getCookies();
        String[] cookieNames = respJar.getCookieNames();
        for (int i = 0; i < cookieNames.length; i++)
            stream.print(cookieNames[i] + " :  ["
                    + respJar.getCookieValue(cookieNames[i]) + "]\n");
    }

    /**
     * Dump html of current response to System.out - for debugging purposes.
     * 
     * @param stream
     */
    public void dumpResponse() {
        dumpResponse(System.out);
    }

    /**
     * Dump html of current response to a specified stream - for debugging
     * purposes.
     * 
     * @param stream
     */
    public void dumpResponse(PrintStream stream) {
        try {
            stream.println(getResponseText());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for
     * debugging purposes.
     * 
     * @param tableNameOrId
     * @param stream
     */
    public void dumpTable(String tableNameOrId, PrintStream stream) {
        dumpTable(tableNameOrId, getSparseTableBySummaryOrId(tableNameOrId),
                stream);
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for
     * debugging purposes.
     * 
     * @param tableNameOrId
     * @param table
     */
    public void dumpTable(String tableNameOrId, String[][] table) {
        dumpTable(tableNameOrId, table, System.out);
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for
     * debugging purposes.
     * 
     * @param tableNameOrId
     * @param table
     * @param stream
     */
    public void dumpTable(String tableNameOrId, String[][] table,
            PrintStream stream) {
        stream.print("\n" + tableNameOrId + ":");
        for (int i = 0; i < table.length; i++) {
            String[] cell = table[i];
            stream.print("\n\t");
            for (int j = 0; j < cell.length; j++) {
                stream.print("[" + cell[j] + "]");
            }
        }
    }

	/**
	 * @param testContext The testContext to set.
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


}