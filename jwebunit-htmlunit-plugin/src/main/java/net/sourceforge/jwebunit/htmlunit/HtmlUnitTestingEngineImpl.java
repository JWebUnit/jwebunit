/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.htmlunit;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.api.ITestingEngine;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptAlertException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptConfirmException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptPromptException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.exception.UnexpectedJavascriptAlertException;
import net.sourceforge.jwebunit.exception.UnexpectedJavascriptConfirmException;
import net.sourceforge.jwebunit.exception.UnexpectedJavascriptPromptException;
import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.javascript.JavascriptAlert;
import net.sourceforge.jwebunit.javascript.JavascriptConfirm;
import net.sourceforge.jwebunit.javascript.JavascriptPrompt;
import net.sourceforge.jwebunit.util.TestContext;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.PromptHandler;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.WebWindowNotFoundException;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow.CellIterator;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * Acts as the wrapper for HtmlUnit access. A testing engine is initialized with a given URL, and maintains conversational state
 * as the dialog progresses through link navigation, form submission, etc.
 * 
 * @author Julien Henry
 * 
 */
public class HtmlUnitTestingEngineImpl implements ITestingEngine {
    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(HtmlUnitTestingEngineImpl.class);

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
     * Javascript alerts.
     */
    private LinkedList<JavascriptAlert> expectedJavascriptAlerts = new LinkedList<JavascriptAlert>();

    /**
     * Javascript confirms.
     */
    private LinkedList<JavascriptConfirm> expectedJavascriptConfirms = new LinkedList<JavascriptConfirm>();

    /**
     * Javascript prompts.
     */
    private LinkedList<JavascriptPrompt> expectedJavascriptPrompts = new LinkedList<JavascriptPrompt>();

    // Implementation of IJWebUnitDialog

    /**
     * Begin a dialog with an initial URL and test client context.
     * 
     * @param initialURL absolute url at which to begin dialog.
     * @param context contains context information for the test client.
     * @throws TestingEngineResponseException
     */
    public void beginAt(URL initialURL, TestContext context)
            throws TestingEngineResponseException {
        this.setTestContext(context);
        initWebClient();
        try {
            wc.getPage(initialURL);
            win = wc.getCurrentWindow();
            form = null;
        } catch (FailingHttpStatusCodeException aException) {
            throw new TestingEngineResponseException(
                    aException.getStatusCode(), aException);

        } catch (IOException aException) {
            throw new RuntimeException(aException);
        }
    }

    public void closeBrowser() throws ExpectedJavascriptAlertException,
            ExpectedJavascriptConfirmException,
            ExpectedJavascriptPromptException {
        wc = null;
        if (this.expectedJavascriptAlerts.size() > 0) {
            throw new ExpectedJavascriptAlertException(
                    ((JavascriptAlert) (expectedJavascriptAlerts.get(0)))
                            .getMessage());
        }
        if (this.expectedJavascriptConfirms.size() > 0) {
            throw new ExpectedJavascriptConfirmException(
                    ((JavascriptConfirm) (expectedJavascriptConfirms.get(0)))
                            .getMessage());
        }
        if (this.expectedJavascriptPrompts.size() > 0) {
            throw new ExpectedJavascriptPromptException(
                    ((JavascriptPrompt) (expectedJavascriptPrompts.get(0)))
                            .getMessage());
        }

    }

    public void gotoPage(URL initialURL) throws TestingEngineResponseException {
        try {
            wc.getPage(initialURL);
            win = wc.getCurrentWindow();
            form = null;
        } catch (FailingHttpStatusCodeException aException) {
            throw new TestingEngineResponseException(aException.getStatusCode());
        } catch (IOException aException) {
            throw new RuntimeException(aException);
        }
    }

    /**
     * @see net.sourceforge.jwebunit.api.IJWebUnitDialog#setScriptingEnabled(boolean)
     */
    public void setScriptingEnabled(boolean value) {
        // This variable is used to set Javascript before wc is instancied
        jsEnabled = value;
        if (wc != null) {
            wc.setJavaScriptEnabled(value);
        }
    }

    public List<javax.servlet.http.Cookie> getCookies() {
        List<javax.servlet.http.Cookie> result = new LinkedList<javax.servlet.http.Cookie>();
        Set<Cookie> cookies = wc.getCookieManager().getCookies();
        for (Cookie cookie : cookies) {
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(
                    cookie.getName(), cookie.getValue());
            c.setComment(cookie.getComment());
            c.setDomain(cookie.getDomain());
            Date expire = cookie.getExpiryDate();
            if (expire == null) {
                c.setMaxAge(-1);
            } else {
                Date now = Calendar.getInstance().getTime();
                // Convert milli-second to second
                Long second = new Long(
                        (expire.getTime() - now.getTime()) / 1000);
                c.setMaxAge(second.intValue());
            }
            c.setPath(cookie.getPath());
            c.setSecure(cookie.getSecure());
            c.setVersion(cookie.getVersion());
            result.add(c);
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
     * @param title
     */
    public void gotoWindowByTitle(String title) {
        WebWindow window = getWindowByTitle(title);
        if (window != null) {
            setMainWindow(window);
        }
    }

    public void closeWindow() {
        if (win != null) {
            wc.deregisterWebWindow(win);
            win = wc.getCurrentWindow();
            form = null;
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean hasFrame(String frameNameOrId) {
        return getFrame(frameNameOrId) != null;
    }

    /**
     * {@inheritDoc}
     */
    public void gotoFrame(String frameNameOrId) {
    	WebWindow frame = getFrame(frameNameOrId);
    	if (frame == null) {
    		throw new RuntimeException("No frame found in current page with name or id [" + frameNameOrId + "]");
    	}
        win = frame;
    }

    /**
     * {@inheritDoc}
     */
    public void setWorkingForm(int index) {
        setWorkingForm(getForm(index));
    }

    /**
     * {@inheritDoc}
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
     * @param nameOrID name of id of the form to check for.
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
     * Return the current value of a text input element with name <code>paramName</code>.
     * 
     * @param paramName name of the input element. TODO: Find a way to handle multiple text input element with same
     *            name.
     */
    public String getTextFieldValue(String paramName) {
        List<HtmlElement> textFieldElements = new LinkedList<HtmlElement>();
        if (form != null) {
            textFieldElements.addAll(getForm().getHtmlElementsByAttribute(
                    "input", "type", "text"));
            textFieldElements.addAll(getForm().getHtmlElementsByAttribute(
                    "input", "type", "password"));
        } else {
            for (Iterator<HtmlForm> i = getCurrentPage().getForms().iterator(); i
                    .hasNext();) {
                HtmlForm f = (HtmlForm) i.next();
                textFieldElements.addAll(f.getHtmlElementsByAttribute("input",
                        "type", "text"));
                textFieldElements.addAll(f.getHtmlElementsByAttribute("input",
                        "type", "password"));
            }
        }
        Iterator<HtmlElement> it = textFieldElements.iterator();
        while (it.hasNext()) {
            HtmlInput input = (HtmlInput) it.next();
            if (paramName.equals(input.getNameAttribute())) {
                if (form == null) {
                    form = input.getEnclosingFormOrDie();
                }
                return input.getValueAttribute();
            }
        }
        // If no text field with the name paramName then try with textareas.
        textFieldElements.clear();
        if (form != null) {
            textFieldElements.addAll(getForm().getTextAreasByName(paramName));
        } else {
            for (Iterator<HtmlForm> i = getCurrentPage().getForms().iterator(); i
                    .hasNext();) {
                HtmlForm f = (HtmlForm) i.next();
                textFieldElements.addAll(f.getTextAreasByName(paramName));
            }
        }
        it = textFieldElements.iterator();
        while (it.hasNext()) {
            HtmlTextArea textInput = (HtmlTextArea) it.next();
            if (paramName.equals(textInput.getNameAttribute())) {
                if (form == null) {
                    form = textInput.getEnclosingFormOrDie();
                }
                return textInput.getText();
            }
        }
        throw new RuntimeException(
                "getTextFieldParameterValue failed, text field with name ["
                        + paramName + "] does not exist.");
    }

    /**
     * Return the current value of a hidden input element with name <code>paramName</code>.
     * 
     * @param paramName name of the input element. TODO: Find a way to handle multiple hidden input element with same
     *            name.
     */
    public String getHiddenFieldValue(String paramName) {
        List<HtmlElement> hiddenFieldElements = new LinkedList<HtmlElement>();
        if (form != null) {
            hiddenFieldElements.addAll(getForm().getHtmlElementsByAttribute(
                    "input", "type", "hidden"));
        } else {
            for (Iterator<HtmlForm> i = getCurrentPage().getForms().iterator(); i
                    .hasNext();) {
                HtmlForm f = (HtmlForm) i.next();
                hiddenFieldElements.addAll(f.getHtmlElementsByAttribute(
                        "input", "type", "hidden"));
            }
        }
        Iterator<HtmlElement> it = hiddenFieldElements.iterator();
        while (it.hasNext()) {
            HtmlHiddenInput hiddenInput = (HtmlHiddenInput) it.next();
            if (paramName.equals(hiddenInput.getNameAttribute())) {
                if (form == null) {
                    form = hiddenInput.getEnclosingFormOrDie();
                }
                return hiddenInput.getValueAttribute();
            }
        }
        throw new RuntimeException(
                "getHiddenFieldParameterValue failed, hidden field with name ["
                        + paramName + "] does not exist.");
    }

    /**
     * Set a form text, password input element or textarea to the provided value.
     * 
     * @param fieldName name of the input element or textarea
     * @param text parameter value to submit for the element.
     */
    public void setTextField(String fieldName, String text) {
        List<HtmlElement> textFieldElements = new LinkedList<HtmlElement>();
        if (form != null) {
            textFieldElements.addAll(getForm().getHtmlElementsByAttribute(
                    "input", "name", fieldName));
            textFieldElements.addAll(getForm().getTextAreasByName(fieldName));
        } else {
            for (Iterator<HtmlForm> i = getCurrentPage().getForms().iterator(); i
                    .hasNext();) {
                HtmlForm f = (HtmlForm) i.next();
                textFieldElements.addAll(f.getHtmlElementsByAttribute("input",
                        "name", fieldName));
                textFieldElements.addAll(f.getTextAreasByName(fieldName));
            }
        }
        for (Iterator<HtmlElement> i = textFieldElements.iterator(); i.hasNext();) {
            HtmlElement e = (HtmlElement) i.next();
            if (e instanceof HtmlTextInput) {
                ((HtmlTextInput) e).setValueAttribute(text);
                if (form == null) {
                    form = e.getEnclosingFormOrDie();
                }
                return;
            }
            if (e instanceof HtmlPasswordInput) {
                ((HtmlPasswordInput) e).setValueAttribute(text);
                if (form == null) {
                    form = e.getEnclosingFormOrDie();
                }
                return;
            }
            if (e instanceof HtmlFileInput) {
                ((HtmlFileInput) e).setValueAttribute(text);
                if (form == null) {
                    form = e.getEnclosingFormOrDie();
                }
                return;
            }
            if (e instanceof HtmlTextArea) {
                ((HtmlTextArea) e).setText(text);
                if (form == null) {
                    form = e.getEnclosingFormOrDie();
                }
                return;
            }
        }
        throw new RuntimeException("No text field with name [" + fieldName
                + "] was found.");
    }
    
    /**
     * Set a form hidden element to the provided value.
     * 
     * @param fieldName name of the hidden input element
     * @param paramValue parameter value to submit for the element.
     */
    public void setHiddenField(String fieldName, String text) {
        List<HtmlInput> hiddenFieldElements = new LinkedList<HtmlInput>();
        if (form != null) {
            hiddenFieldElements.addAll(getForm().getInputsByName(fieldName));
        } else {
            for (Iterator<HtmlForm> i = getCurrentPage().getForms().iterator(); i
                    .hasNext();) {
                HtmlForm f = (HtmlForm) i.next();
                hiddenFieldElements.addAll(f.getInputsByName(fieldName));
            }
        }
        for (Iterator<HtmlInput> i = hiddenFieldElements.iterator(); i.hasNext();) {
            HtmlElement e = (HtmlElement) i.next();
            if (e instanceof HtmlHiddenInput) {
                ((HtmlHiddenInput) e).setValueAttribute(text);
                if (form == null) {
                    form = e.getEnclosingFormOrDie();
                }
                return;
            }
        }
        throw new RuntimeException("No hidden field with name [" + fieldName
                + "] was found.");
    }

    /**
     * Return a string array of select box option values.
     * 
     * @param selectName name of the select box.
     */
    public String[] getSelectOptionValues(String selectName) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        ArrayList<String> result = new ArrayList<String>();
        for (HtmlOption opt : sel.getOptions()) {
            result.add(opt.getValueAttribute());
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Return a string array of the Nth select box option values.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index when more than one
     * select with the same name is expected.
     */
    public String[] getSelectOptionValues(String selectName, int index) {
        List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
        if ( sels == null || sels.size() < index+1)
        	throw new RuntimeException("Did not find select with name [" + selectName 
        	                           + "] at index " + index);
        	
        HtmlSelect sel = sels.get(index);
        ArrayList<String> result = new ArrayList<String>();
        for (HtmlOption opt : sel.getOptions()) {
            result.add(opt.getValueAttribute());
        }
        return (String[]) result.toArray(new String[0]);
    }
    
    private String[] getSelectedOptions(HtmlSelect sel) {
        String[] result = new String[sel.getSelectedOptions().size()];
        int i = 0;
        for (HtmlOption opt : sel.getSelectedOptions())
            result[i++] = opt.getValueAttribute();
        return result;
    }

    
    public String[] getSelectedOptions(String selectName) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        return getSelectedOptions(sel);
    }

    public String[] getSelectedOptions(String selectName, int index) {
        List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
        if ( sels == null || sels.size() < index+1)
        	throw new RuntimeException("Did not find select with name [" + selectName 
        	                           + "] at index " + index);
        HtmlSelect sel = sels.get(index);
        return getSelectedOptions(sel);
    }

    
    private String getSelectOptionValueForLabel(HtmlSelect sel, String label) {
        for (HtmlOption opt : sel.getOptions()) {
            if (opt.asText().equals(label))
                return opt.getValueAttribute();
        }
        throw new RuntimeException("Unable to find option " + label + " for "
                + sel.getNameAttribute());
    }

    public String getSelectOptionValueForLabel(String selectName, String label) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        return getSelectOptionValueForLabel(sel, label);
    }
    
    public String getSelectOptionValueForLabel(String selectName, int index, String label) {
        List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
        if ( sels == null || sels.size() < index+1)
        	throw new RuntimeException("Did not find select with name [" + selectName 
        	                           + "] at index " + index);
        HtmlSelect sel = (HtmlSelect)sels.get(index);
        return getSelectOptionValueForLabel(sel, label);
    }

    private String getSelectOptionLabelForValue(HtmlSelect sel, String value) {
        for (HtmlOption opt : sel.getOptions()) {
            if (opt.getValueAttribute().equals(value))
                return opt.asText();
        }
        throw new RuntimeException("Unable to find option " + value + " for "
                + sel.getNameAttribute());
    }
    
    public String getSelectOptionLabelForValue(String selectName, String value) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        return getSelectOptionLabelForValue(sel, value);
    }
    
    public String getSelectOptionLabelForValue(String selectName, int index, String value) {
        List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
        if ( sels == null || sels.size() < index+1)
        	throw new RuntimeException("Did not find select with name [" + selectName 
        	                           + "] at index " + index);
        	
        HtmlSelect sel = (HtmlSelect)sels.get(index);
        return getSelectOptionLabelForValue(sel, value);
    }

    
    
    public URL getPageURL() {
        return win.getEnclosedPage().getWebResponse().getUrl();
    }
    
    public String getPageSource() {
        return win.getEnclosedPage().getWebResponse()
                .getContentAsString();
    }

    public String getPageTitle() {
        return getCurrentPageTitle();
    }

    public String getPageText() {
        Page page = win.getEnclosedPage();
        if (page instanceof HtmlPage)
            return ((HtmlPage) page).asText();
        if (page instanceof TextPage)
            return ((TextPage) page).getContent();
        if (page instanceof JavaScriptPage)
            return ((JavaScriptPage) page).getContent();
        if (page instanceof XmlPage)
            return ((XmlPage) page).getContent();
        if (page instanceof UnexpectedPage)
            return ((UnexpectedPage) page).getWebResponse()
                    .getContentAsString();
        throw new RuntimeException(
                "Unexpected error in getPageText(). This method need to be updated.");
    }

    public String getServerResponse() {
        StringBuffer result = new StringBuffer();
        WebResponse wr = wc.getCurrentWindow().getEnclosedPage()
                .getWebResponse();
        result.append(wr.getStatusCode()).append(" ").append(
                wr.getStatusMessage()).append("\n");
        result.append("Location: ").append(wr.getUrl()).append("\n");
        for (NameValuePair h : wr.getResponseHeaders()) {
            result.append(h.getName()).append(": ").append(h.getValue())
                    .append("\n");
        }
        result.append("\n");
        result.append(wr.getContentAsString());
        return result.toString();
    }

    public InputStream getInputStream() {
        try {
            return wc.getCurrentWindow().getEnclosedPage().getWebResponse()
                    .getContentAsStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getInputStream(URL resourceUrl)
            throws TestingEngineResponseException {
        WebWindow imageWindow = null;
        try {
            // as far as I can tell, there is no such thing as an iframe/object kind of "window" in htmlunit, so I'm
            // opening a fake new window here
            imageWindow = wc.openWindow(resourceUrl, "for_stream");
            Page page = imageWindow.getEnclosedPage();
            return page.getWebResponse().getContentAsStream();
        } catch (FailingHttpStatusCodeException aException) {
            throw new TestingEngineResponseException(
                    aException.getStatusCode(), aException);

        } catch (IOException aException) {
            throw new RuntimeException(aException);
        } finally {
            if (imageWindow != null) {
                wc.deregisterWebWindow(imageWindow);
            }
        }
    }

    private void initWebClient() {
        
        BrowserVersion bv = new BrowserVersion(BrowserVersion.INTERNET_EXPLORER,
                "4.0", testContext.getUserAgent(), "1.2", 6);
        if (getTestContext().getProxyHost()!=null && getTestContext().getProxyPort()>0) {
            //Proxy
            wc = new WebClient(bv, getTestContext().getProxyHost(), getTestContext().getProxyPort());
        }
        else {
            wc = new WebClient(bv);
        }
        wc.setJavaScriptEnabled(jsEnabled);
        wc.setThrowExceptionOnScriptError(true);
        wc.setRedirectEnabled(true);
        wc.setRefreshHandler(new ImmediateRefreshHandler());
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
        if (getTestContext().hasProxyAuthorization()) {
            creds.addProxyCredentials(getTestContext().getProxyUser(),
                    getTestContext().getPassword(), getTestContext()
                            .getProxyHost(), getTestContext().getProxyPort());
        }
        wc.setCredentialsProvider(creds);
        wc.addWebWindowListener(new WebWindowListener() {
            public void webWindowClosed(WebWindowEvent event) {
                if (event.getOldPage().equals(win.getEnclosedPage())) {
                    win = wc.getCurrentWindow();
                    form = null;
                }
                String win = event.getWebWindow().getName();
                Page oldPage = event.getOldPage();
                String oldPageTitle = "no_html";
                if (oldPage instanceof HtmlPage) {
                    oldPageTitle = ((HtmlPage) oldPage).getTitleText();
                }
                logger.debug("Window {} closed : {}", win, oldPageTitle);
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
                logger.debug("Window \"{}\" changed : \"{}\" became \"{}", new Object[] {winName, oldPageTitle, newPageTitle});
            }

            public void webWindowOpened(WebWindowEvent event) {
                String win = event.getWebWindow().getName();
                Page newPage = event.getNewPage();
                if (newPage != null && newPage instanceof HtmlPage) {
                    logger.debug("Window {} opened : {}", win, ((HtmlPage) newPage).getTitleText());
                } else {
                    logger.info("Window {} opened", win);
                }
            }
        });
        // Add Javascript Alert Handler
        wc.setAlertHandler(new AlertHandler() {
            public void handleAlert(Page page, String msg) {
                if (expectedJavascriptAlerts.size() < 1) {
                    throw new UnexpectedJavascriptAlertException(msg);
                } else {
                    JavascriptAlert expected = (JavascriptAlert) expectedJavascriptAlerts
                            .removeFirst();
                    if (!msg.equals(expected.getMessage())) {
                        throw new UnexpectedJavascriptAlertException(msg);
                    }
                }
            }
        });
        // Add Javascript Confirm Handler
        wc.setConfirmHandler(new ConfirmHandler() {
            public boolean handleConfirm(Page page, String msg) {
                if (expectedJavascriptConfirms.size() < 1) {
                    throw new UnexpectedJavascriptConfirmException(msg);
                } else {
                    JavascriptConfirm expected = (JavascriptConfirm) expectedJavascriptConfirms
                            .removeFirst();
                    if (!msg.equals(expected.getMessage())) {
                        throw new UnexpectedJavascriptConfirmException(msg);
                    } else {
                        return expected.getAction();
                    }
                }
            }
        });
        // Add Javascript Prompt Handler
        wc.setPromptHandler(new PromptHandler() {
            public String handlePrompt(Page page, String msg) {
                if (expectedJavascriptPrompts.size() < 1) {
                    throw new UnexpectedJavascriptPromptException(msg);
                } else {
                    JavascriptPrompt expected = (JavascriptPrompt) expectedJavascriptPrompts
                            .removeFirst();
                    if (!msg.equals(expected.getMessage())) {
                        throw new UnexpectedJavascriptPromptException(msg);
                    } else {
                        return expected.getInput();
                    }
                }
            }
        });
        // Deal with cookies
        for (javax.servlet.http.Cookie c : getTestContext().getCookies()) {
            // If Path==null, cookie is not send to the server.
            wc.getCookieManager().addCookie(
                    new Cookie(c.getDomain() != null ? c.getDomain() : "", c
                            .getName(), c.getValue(), c.getPath() != null ? c                                          
                            .getPath() : "", c.getMaxAge(), c.getSecure()));
        }
        // Deal with custom request header
        Map<String, String> requestHeaders = getTestContext().getRequestHeaders();

        for (String nextRequestHeaderName : requestHeaders.keySet()) {
            String nextRequestHeaderValue = (String) requestHeaders
                    .get(nextRequestHeaderName);

            wc.addRequestHeader(nextRequestHeaderName, nextRequestHeaderValue);
        }
    }

    /**
     * Return the window with the given name in the current conversation.
     * 
     * @param windowName
     */
    private WebWindow getWindow(String windowName) {
        return wc.getWebWindowByName(windowName);
    }

    private HtmlElement getHtmlElement(String anID) {
        try {
            return ((HtmlPage) win.getEnclosedPage()).getHtmlElementById(anID);
        } catch (ElementNotFoundException e) {
            return null;
        }
    }

    private HtmlElement getHtmlElementByXPath(String xpath) {
        return getHtmlElementByXPath(getCurrentPage(), xpath);
    }

    private HtmlElement getHtmlElementByXPath(DomNode parent, String xpath) {
        return (HtmlElement) parent.getFirstByXPath(xpath);
    }

    /**
     * Return the first open window with the given title.
     */
    private WebWindow getWindowByTitle(String title) {
        for (WebWindow window : wc.getWebWindows()) {
            if (window.getEnclosedPage() instanceof HtmlPage
                    && ((HtmlPage) window.getEnclosedPage()).getTitleText()
                            .equals(title)) {
                return window;
            }
        }
        return null;
    }

    /**
     * Return the page title of the current response page, encoded as specified by the current
     * {@link net.sourceforge.jwebunit.util.TestContext}.
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
     * If this method is called without the form having been implicitly or explicitly set, it will attempt to return the
     * default first form on the page.
     * </p>
     * 
     * @exception UnableToSetFormException This runtime assertion failure will be raised if there is no form on the
     *                response.
     * @return HtmlForm object representing the current active form from the response.
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
        List<HtmlForm> forms = new ArrayList<HtmlForm>();
        for (HtmlForm form : getCurrentPage().getForms()) {
            if (nameOrID.equals(form.getIdAttribute())
                    || nameOrID.equals(form.getNameAttribute())) {
                forms.add(form);
            }
        }
        if (forms.size()>index) {
            return forms.get(index);
        }
        else {
            return null;
        }
    }

    private HtmlForm getFormWithInput(String inputName) {
        // Search in Working form if available
        if (form != null) {
            if (!form.getHtmlElementsByAttribute("input",
                    "name", inputName).isEmpty()) {
                return form;
            }
            if (!form.getTextAreasByName(inputName).isEmpty()) {
                return form;
            }
        } else {
            if (hasForm()) {
                for (HtmlForm form : getForms()) {
                    List<HtmlElement> inputElements = new LinkedList<HtmlElement>();
                    inputElements.addAll(form.getHtmlElementsByAttribute("input",
                            "name", inputName));
                    if (inputElements.isEmpty()) {
                        inputElements.addAll(form.getTextAreasByName(inputName));
                    }
                    if (!inputElements.isEmpty()) {
                        setWorkingForm(form);
                        return form;
                    }
                }
            }
        }
        return null;
    }

    private HtmlForm getFormWithSelect(String selectName) {
        // Search in Working form if available
        if (form != null) {
            try {
                if (form.getSelectByName(selectName) != null) {
                    return form;
                }
            } catch (ElementNotFoundException e) {
                // Nothing
            }
        } else {
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
        }
        return null;
    }

    private List<HtmlForm> getForms() {
        HtmlPage page = (HtmlPage) win.getEnclosedPage();
        return page.getForms();
    }

    private HtmlPage getCurrentPage() {
        Page page = win.getEnclosedPage();
        if (page instanceof HtmlPage)
            return (HtmlPage) page;
        throw new RuntimeException("Non HTML content");
    }

    private void setWorkingForm(HtmlForm newForm) {
        if (newForm == null)
            throw new UnableToSetFormException("Attempted to set form to null.");
        form = newForm;
    }

    /**
     * Return true if a form parameter (input element) is present on the current response.
     * 
     * @param inputName name of the input element to check for
     */
    public boolean hasFormInputNamed(String inputName) {
        return (getFormWithInput(inputName) != null);
    }

    /**
     * Return true if a form parameter (input element) is present on the current response.
     * 
     * @param selectName name of the input element to check for
     */
    public boolean hasFormSelectNamed(String selectName) {
        return (getFormWithSelect(selectName) != null);
    }

    /**
     * Return the HtmlUnit submit button with a given name.
     * 
     * @param buttonName name of button.
     * @return the button
     */
    public ClickableElement getSubmitButton(String buttonName) {
        List<HtmlElement> btns = new LinkedList<HtmlElement>();
        if (form != null) {
            btns.addAll(getForm().getInputsByName(buttonName));
        } else {
            for (HtmlForm f : getCurrentPage().getForms()) {
                btns.addAll(f.getInputsByName(buttonName));
            }
        }
        for (HtmlElement o : btns) {
            if (o instanceof HtmlSubmitInput) {
                HtmlSubmitInput btn = (HtmlSubmitInput) o;
                if (form == null) {
                    form = btn.getEnclosingFormOrDie();
                }
                return btn;
            }
            if (o instanceof HtmlImageInput) {
                HtmlImageInput btn = (HtmlImageInput) o;
                if (form == null) {
                    form = btn.getEnclosingFormOrDie();
                }
                return btn;
            }
            if (o instanceof HtmlButton) {
                HtmlButton btn = (HtmlButton) o;
                if (btn.getTypeAttribute().equals("submit")) {
                    if (form == null) {
                        form = btn.getEnclosingFormOrDie();
                    }
                    return btn;
                }
            }
        }
        return null;
    }

    public HtmlResetInput getResetButton(String buttonName) {
        List<HtmlElement> btns = new LinkedList<HtmlElement>();
        if (form != null) {
            btns.addAll(getForm().getInputsByName(buttonName));
        } else {
            for (HtmlForm f : getCurrentPage().getForms()) {
                btns.addAll(f.getInputsByName(buttonName));
            }
        }
        for (HtmlElement o : btns) {
            if (o instanceof HtmlResetInput) {
                HtmlResetInput btn = (HtmlResetInput) o;
                if (form == null) {
                    form = btn.getEnclosingFormOrDie();
                }
                return btn;
            }
            if (o instanceof HtmlButton) {
                HtmlResetInput btn = (HtmlResetInput) o;
                if (btn.getTypeAttribute().equals("reset")) {
                    if (form == null) {
                        form = btn.getEnclosingFormOrDie();
                    }
                    return btn;
                }
            }
        }
        return null;
    }

    /**
     * Return the HtmlUnit submit button with a given name and value.
     * 
     * @param buttonName button name.
     * @param buttonValue button value.
     * @return HtmlSubmitInput, HtmlImageInput or HtmlButton
     */
    public ClickableElement getSubmitButton(String buttonName,
            String buttonValue) {
        List<HtmlElement> btns = new LinkedList<HtmlElement>();
        if (form != null) {
            btns.addAll(getForm().getInputsByName(buttonName));
        } else {
            for (HtmlForm f  : getCurrentPage().getForms()) {
                btns.addAll(f.getInputsByName(buttonName));
            }
        }
        for (HtmlElement o : btns) {
            if (o instanceof HtmlSubmitInput) {
                HtmlSubmitInput btn = (HtmlSubmitInput) o;
                if (btn.getValueAttribute().equals(buttonValue)) {
                    if (form == null) {
                        form = btn.getEnclosingFormOrDie();
                    }
                    return btn;
                }
            }
            if (o instanceof HtmlImageInput) {
                HtmlImageInput btn = (HtmlImageInput) o;
                if (btn.getValueAttribute().equals(buttonValue)) {
                    if (form == null) {
                        form = btn.getEnclosingFormOrDie();
                    }
                    return btn;
                }
            }
            if (o instanceof HtmlButton) {
                HtmlButton btn = (HtmlButton) o;
                if (btn.getValueAttribute().equals(buttonValue)
                        && btn.getTypeAttribute().equals("submit")) {
                    if (form == null) {
                        form = btn.getEnclosingFormOrDie();
                    }
                    return btn;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSubmitButton() {
        final HtmlForm htmlForm = getForm();
        List<?> l = htmlForm.getByXPath("//input[@type='submit' or @type='image']");
        List<?> l2 = htmlForm.getByXPath("//button[@type='submit']");
        return (l.size() > 0 || l2.size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSubmitButton(String buttonName) {
        return getSubmitButton(buttonName) != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSubmitButton(String buttonName, String buttonValue) {
        try {
            return getSubmitButton(buttonName, buttonValue) != null;
        } catch (UnableToSetFormException e) {
            return false;
        }

    }

    public boolean hasResetButton() {
        HtmlForm form = getForm();
        List<?> l = form.getByXPath("//input[@type='reset']");
        List<?> l2 = form.getByXPath("//button[@type='reset']");
        return (l.size() > 0 || l2.size() > 0);
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
     * Checks whether a button containing the specified text as its label exists.
     * For HTML input tags of type submit, reset, or button, this checks the
     * value attribute.  For HTML button tags, this checks the element's
     * content by converting it to text.  
     * @param text the text of the button (between &lt;button&gt;&lt;/button&gt;)
     * or the value of the "value" attribute.
     * @return <code>true</code> when the button with text could be found.
     */
    public boolean hasButtonWithText(String text) {
        return getButtonWithText(text) != null ? true : false;
    }

    /**
     * Returns the first button that contains the specified text as its label.
     * For HTML input tags of type submit, reset, or button, this checks the
     * value attribute.  For HTML button tags, this checks the element's
     * content by converting it to text.  
     * @param buttonValueText the text of the button (between &lt;button&gt;&lt;/button&gt;)
     * or the value of the "value" attribute.
     * @return the ClickableElement with the specified text or null if 
     * no such button is found. 
     */
    public ClickableElement getButtonWithText(String buttonValueText) {
        List<? extends HtmlElement> l = ((HtmlPage) win.getEnclosedPage()).getDocumentElement()
                .getHtmlElementsByTagNames(
                        Arrays.asList(new String[] { "button", "input" }));
        for (HtmlElement e : l) {
            if ( e instanceof HtmlButton )
            {
            	if (((HtmlButton) e).asText().equals(buttonValueText))
            		return (ClickableElement) e;
            }
            else if ( e instanceof HtmlButtonInput ||
            		  e instanceof HtmlSubmitInput ||
            		  e instanceof HtmlResetInput )
            {
            	if ( buttonValueText.equals(e.getAttributeValue("value")) )
            		return (ClickableElement)e;
            }
        }
        return null;
    }

    /**
     * Returns if the button identified by <code>buttonId</code> is present.
     * 
     * @param buttonId the id of the button
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

    public boolean isCheckboxSelected(String checkBoxName, String checkBoxValue) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName, checkBoxValue);
        return cb.isChecked();
    }

    /**
     * Return true if given text is present in a specified table of the response.
     * 
     * @param tableSummaryOrId table summary or id to inspect for expected text.
     * @param text expected text to check for.
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
     * Return the HttpUnit WebTable object representing a specified table in the current response. Null is returned if a
     * parsing exception occurs looking for the table or no table with the id or summary could be found.
     * 
     * @param tableSummaryOrId summary or id of the table to return.
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
     * Submit the current form with the default submit button. See {@link #getForm}for an explanation of how the
     * current form is established.
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
                if (inpt[i] instanceof HtmlImageInput) {
                    ((HtmlImageInput) inpt[i]).click();
                    return;
                }
                if (inpt[i] instanceof HtmlButton
                        && ((HtmlButton) inpt[i]).getTypeAttribute().equals(
                                "submit")) {
                    ((HtmlButton) inpt[i]).click();
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "HtmlUnit Error submitting form using default submit button, "
                            + "check that form has single submit button, otherwise use submit(name): \n",
                    e);
        }
        throw new RuntimeException("No submit button found in current form.");
    }

    /**
     * Submit the current form with the specified submit button. See {@link #getForm}for an explanation of how the
     * current form is established.
     * 
     * @param buttonName name of the button to use for submission.
     */
    public void submit(String buttonName) {
        List<ClickableElement> l = new LinkedList<ClickableElement>();
        l.addAll(getForm().getInputsByName(buttonName));
        l.addAll(getForm().getButtonsByName(buttonName));
        try {
            for (ClickableElement o : l) {
                if (o instanceof HtmlSubmitInput) {
                    HtmlSubmitInput inpt = (HtmlSubmitInput) o;
                    inpt.click();
                    return;
                }
                if (o instanceof HtmlImageInput) {
                    HtmlImageInput inpt = (HtmlImageInput) o;
                    inpt.click();
                    return;
                }
                if (o instanceof HtmlButton) {
                    HtmlButton inpt = (HtmlButton) o;
                    if (inpt.getTypeAttribute().equals("submit")) {
                        inpt.click();
                        return;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "HtmlUnit Error submitting form using default submit button", e);
        }
        throw new RuntimeException("No submit button found in current form.");
    }

    /**
     * Submit the current form with the specifed submit button (by name and value). See {@link #getForm}for an
     * explanation of how the current form is established.
     * 
     * @param buttonName name of the button to use for submission.
     * @param buttonValue value/label of the button to use for submission
     */
    public void submit(String buttonName, String buttonValue) {
        List<ClickableElement> l = new LinkedList<ClickableElement>();
        l.addAll(getForm().getInputsByName(buttonName));
        l.addAll(getForm().getButtonsByName(buttonName));
        try {
            for (int i = 0; i < l.size(); i++) {
                Object o = l.get(i);
                if (o instanceof HtmlSubmitInput) {
                    HtmlSubmitInput inpt = (HtmlSubmitInput) o;
                    if (inpt.getValueAttribute().equals(buttonValue)) {
                        inpt.click();
                        return;
                    }
                }
                if (o instanceof HtmlImageInput) {
                    HtmlImageInput inpt = (HtmlImageInput) o;
                    if (inpt.getValueAttribute().equals(buttonValue)) {
                        inpt.click();
                        return;
                    }
                }
                if (o instanceof HtmlButton) {
                    HtmlButton inpt = (HtmlButton) o;
                    if (inpt.getTypeAttribute().equals("submit")
                            && inpt.getValueAttribute().equals(buttonValue)) {
                        inpt.click();
                        return;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "HtmlUnit Error submitting form using submit button with name ["
                            + buttonName + "] and value [" + buttonValue
                            + "]", e);
        }
        throw new RuntimeException(
                "No submit button found in current form with name ["
                        + buttonName + "] and value [" + buttonValue + "].");
    }

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how the current form is established.
     */
    public void reset() {
        getForm().reset();
    }

    /**
     * Return true if a link is present in the current response containing the specified text (note that HttpUnit uses
     * contains rather than an exact match - if this is a problem consider using ids on the links to uniquely identify
     * them).
     * 
     * @param linkText text to check for in links on the response.
     * @param index The 0-based index, when more than one link with the same text is expected.
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
     * Return true if a link is present in the current response with the specified id.
     * 
     * @param anId link id to check for.
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
            throw new RuntimeException("Click failed", e);
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
            throw new RuntimeException("Click failed", e);
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
     * Select a specified checkbox. If the checkbox is already checked then the checkbox will stay checked.
     * 
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
        if (!cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                throw new RuntimeException("checkCheckbox failed", e);
            }
    }

    public void checkCheckbox(String checkBoxName, String value) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName, value);
        if (!cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed", e);
            }
    }

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then the checkbox will stay unchecked.
     * 
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
        if (cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed", e);
            }
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        HtmlCheckBoxInput cb = getCheckbox(checkBoxName, value);
        if (cb.isChecked())
            try {
                cb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("uncheckCheckbox failed", e);
            }
    }
    
    private HtmlRadioButtonInput getRadioOption(String radioGroup, String radioOption) {
        for (HtmlForm form : getForms()) {
            List<HtmlRadioButtonInput> buttons = form.getRadioButtonsByName(radioGroup);
            for (HtmlRadioButtonInput button : buttons) {
                if (button.getValueAttribute().equals(radioOption)) {
                    return button;
                }
            }
        }
        return null;
    }

    /**
     * Clicks a radio option. Asserts that the radio option exists first. *
     * 
     * @param radioGroup name of the radio group.
     * @param radioOption value of the option to check for.
     */
    public void clickRadioOption(String radioGroup, String radioOption) {
        HtmlRadioButtonInput rb = getRadioOption(radioGroup, radioOption);
        if (!rb.isChecked())
            try {
                rb.click();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("checkCheckbox failed", e);
            }
    }

    /**
     * Navigate by submitting a request based on a link with a given ID. A RuntimeException is thrown if no such link
     * can be found.
     * 
     * @param anID id of link to be navigated.
     */
    public void clickLink(String anID) {
        clickElementByXPath("//a[@id=\"" + anID + "\"]");
    }

    private HtmlAnchor getLinkWithImage(String filename, int index) {
        return (HtmlAnchor) getHtmlElementByXPath("(//a[img[contains(@src,\""
                + filename + "\")]])[" + index + 1 + "]");
    }

    private HtmlAnchor getLinkWithText(String linkText, int index) {
        List<HtmlAnchor> lnks = ((HtmlPage) win.getEnclosedPage()).getAnchors();
        int count = 0;
        for (HtmlAnchor lnk : lnks) {
            if ((lnk.asText().indexOf(linkText) >= 0) && (count++ == index))
                return lnk;
        }
        return null;
    }

    private HtmlAnchor getLinkWithExactText(String linkText, int index) {
        List<HtmlAnchor> lnks = ((HtmlPage) win.getEnclosedPage()).getAnchors();
        int count = 0;
        for (HtmlAnchor lnk : lnks) {
            if ((lnk.asText().equals(linkText)) && (count++ == index))
                return lnk;
        }
        return null;
    }

    /**
     * Navigate by submitting a request based on a link with a given image file name. A RuntimeException is thrown if no
     * such link can be found.
     * 
     * @param imageFileName A suffix of the image's filename; for example, to match
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
            throw new RuntimeException("Click failed", e);
        }
    }

    public boolean hasElement(String anID) {
        return getHtmlElement(anID) != null;
    }

    public boolean hasElementByXPath(String xpath) {
        return getHtmlElementByXPath(xpath) != null;
    }

    public void clickElementByXPath(String xpath) {
        HtmlElement e = getHtmlElementByXPath(xpath);
        if (e == null)
            throw new RuntimeException("No element found with xpath \"" + xpath
                    + "\"");
        try {
            ClickableElement c = (ClickableElement) e;
            c.click();
        } catch (ClassCastException exp) {
            throw new RuntimeException("Element with xpath \"" + xpath
                    + "\" is not clickable", exp);
        } catch (IOException exp) {
            throw new RuntimeException("Click failed", exp);
        }
    }

    public String getElementAttributByXPath(String xpath, String attribut) {
        HtmlElement e = getHtmlElementByXPath(xpath);
        if (e == null)
            return null;
        return e.getAttributeValue(attribut);
    }


    public String getElementTextByXPath(String xpath) {
        HtmlElement e = getHtmlElementByXPath(xpath);
        if (e == null)
            return null;
        return e.asText();
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
            throw new RuntimeException(e);
        }
    }

 	/**
     * Clicks the first button that contains the specified text as its label.
     * For HTML input tags of type submit, reset, or button, this checks the
     * value attribute.  For HTML button tags, this checks the element's
     * content by converting it to text.  or an HTML &lt;button&gt; tag.
     */
    public void clickButtonWithText(String buttonValueText) {
    	ClickableElement b = getButtonWithText(buttonValueText);
    	if (b != null) {
    		try {
    			b.click();
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	}
    	else {
    		throw new RuntimeException("No button found with text: " + buttonValueText);
    	}
    }

    /**
     * Return true if a radio group contains the indicated option.
     * 
     * @param radioGroup name of the radio group.
     * @param radioOption value of the option to check for.
     */
    public boolean hasRadioOption(String radioGroup, String radioOption) {
        return getRadioOption(radioGroup, radioOption) != null;
    }
    
    public String getSelectedRadio(String radioGroup) {
        List<HtmlRadioButtonInput> radios = getForm().getRadioButtonsByName(radioGroup);
        for (HtmlRadioButtonInput radio : radios) {
        	if (radio.isChecked()) {
        		return radio.getValueAttribute();
        	}
        }
        throw new RuntimeException("Unexpected state: no radio button was selected in radio group ["+radioGroup+"]. Is it possible in a real browser?");
    }

    /**
     * Return true if a select box contains the indicated option.
     * 
     * @param selectName name of the select box.
     * @param optionLabel label of the option.
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
     * @param selectName name of the select box.
     * @param optionValue value of the option.
     */
    public boolean hasSelectOptionValue(String selectName, String optionValue) {
        String[] opts = getSelectOptionValues(selectName);
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(optionValue))
                return true;
        }
        return false;
    }

    /**
     * Select the specified set of options in the select element
     * with the provided name.
     * @param selectName name of the select box
     * @param options set of options to select.
     */
    public void selectOptions(String selectName, String[] options) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        if (!sel.isMultipleSelectEnabled() && options.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        for (String option : options) {
            boolean found = false;
            for (HtmlOption opt : sel.getOptions()) {
                if (opt.getValueAttribute().equals(option)) {
                    sel.setSelectedAttribute(opt, true);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new RuntimeException("Option " + option
                        + " not found");
        }
    }

    /**
     * Return true if the Nth select box contains the indicated option.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionLabel label of the option.
     */
    public boolean hasSelectOption(String selectName, int index, String optionLabel) {
    	String[] opts = getSelectOptionValues(selectName, index);
        for (int i = 0; i < opts.length; i++) {
            String label = getSelectOptionLabelForValue(selectName, index, opts[i]);
            if (label.equals(optionLabel))
                return true;
        }
        return false;
    }

    /**
     * Return true if the Nth select box contains the indicated option.
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionValue value of the option.
     */
    public boolean hasSelectOptionValue(String selectName, int index, String optionValue) {
        String[] opts = getSelectOptionValues(selectName, index);
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(optionValue))
                return true;
        }
        return false;
    }

    /**
     * Select the specified set of options in the select element
     * with the provided name.
     * @param selectName name of the select box
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param options set of options to select.
     */
    public void selectOptions(String selectName, int index, String[] options) {
    	List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
    	if ( sels == null || sels.size() < index+1 )
        	throw new RuntimeException("Did not find select with name [" + selectName 
        	                           + "] at index " + index);
    	HtmlSelect sel = (HtmlSelect)sels.get(index); 
        if (!sel.isMultipleSelectEnabled() && options.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        for (String option : options) {
            boolean found = false;
            for (HtmlOption opt : sel.getOptions()) {
                if (opt.getValueAttribute().equals(option)) {
                    sel.setSelectedAttribute(opt, true);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new RuntimeException("Option " + option
                        + " not found");
        }
    }

    
    public void unselectOptions(String selectName, String[] options) {
        HtmlSelect sel = getForm().getSelectByName(selectName);
        if (!sel.isMultipleSelectEnabled() && options.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        for (String option : options) {
            boolean found = false;
            for (HtmlOption opt : sel.getOptions()) {
                if (opt.asText().equals(option)) {
                    sel.setSelectedAttribute(opt, false);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new RuntimeException("Option " + option
                        + " not found");
        }
    }
    public void unselectOptions(String selectName, int index, String[] options) {
        List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
        if ( sels == null || sels.size() < index+1)
        	throw new RuntimeException("Did not find select with name [" + selectName 
        	                           + "] at index " + index);
        HtmlSelect sel = sels.get(index);
        if (!sel.isMultipleSelectEnabled() && options.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        for (String option : options) {
            boolean found = false;
            for (HtmlOption opt : sel.getOptions()) {
                if (opt.asText().equals(option)) {
                    sel.setSelectedAttribute(opt, false);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new RuntimeException("Option " + option
                        + " not found");
        }
    }

    public boolean isTextInElement(String elementID, String text) {
        return isTextInElement(getHtmlElement(elementID), text);
    }

    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param element org.w3c.com.Element to inspect.
     * @param text text to check for.
     */
    private boolean isTextInElement(HtmlElement element, String text) {
        return element.asText().indexOf(text) >= 0;
    }

    public boolean isMatchInElement(String elementID, String regexp) {
        return isMatchInElement(getHtmlElement(elementID), regexp);
    }

    /**
     * Return true if a given regexp is contained within the specified element.
     * 
     * @param element org.w3c.com.Element to inspect.
     * @param regexp regexp to match.
     */
    private boolean isMatchInElement(HtmlElement element, String regexp) {
        RE re = getRE(regexp);
        return re.match(element.asText());
    }

    private RE getRE(String regexp) {
        try {
            return new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            throw new RuntimeException(e);
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
     * Return the given frame in the current conversation.
     * 
     * @param frameNameOrId Frame name or ID.
     * @return The frame found or null.
     */
    private WebWindow getFrame(String frameNameOrId) {
        // First try ID
        for (FrameWindow frame : getCurrentPage().getFrames()) {
            if (frameNameOrId.equals(frame.getFrameElement().getId())) {
                return frame;
            }
        }
        // Now try with Name
        for (FrameWindow frame : getCurrentPage().getFrames()) {
            if (frameNameOrId.equals(frame.getName())) {
                return frame;
            }
        }
        // Nothing was found.
        return null;
    }

    /**
     * @param testContext The testContext to set.
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

    public void setExpectedJavaScriptAlert(JavascriptAlert[] alerts)
            throws ExpectedJavascriptAlertException {
        if (this.expectedJavascriptAlerts.size() > 0) {
            throw new ExpectedJavascriptAlertException(
                    ((JavascriptAlert) (expectedJavascriptAlerts.get(0)))
                            .getMessage());
        }
        for (int i = 0; i < alerts.length; i++) {
            expectedJavascriptAlerts.add(alerts[i]);
        }
    }

    public void setExpectedJavaScriptConfirm(JavascriptConfirm[] confirms)
            throws ExpectedJavascriptConfirmException {
        if (this.expectedJavascriptConfirms.size() > 0) {
            throw new ExpectedJavascriptConfirmException(
                    ((JavascriptConfirm) (expectedJavascriptConfirms.get(0)))
                            .getMessage());
        }
        for (int i = confirms.length - 1; i >= 0; i--) {
            expectedJavascriptConfirms.add(confirms[i]);
        }
    }

    public void setExpectedJavaScriptPrompt(JavascriptPrompt[] prompts)
            throws ExpectedJavascriptPromptException {
        if (this.expectedJavascriptPrompts.size() > 0) {
            throw new ExpectedJavascriptPromptException(
                    ((JavascriptPrompt) (expectedJavascriptPrompts.get(0)))
                            .getMessage());
        }
        for (int i = prompts.length - 1; i >= 0; i--) {
            expectedJavascriptPrompts.add(prompts[i]);
        }
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementByXPath(java.lang.String)
	 */
	public IElement getElementByXPath(String xpath) {
		return new HtmlUnitElementImpl(this.getHtmlElementByXPath(xpath));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementByID(java.lang.String)
	 */
	public IElement getElementByID(String id) {
		return new HtmlUnitElementImpl(this.getHtmlElement(id));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementsByXPath(java.lang.String)
	 */
	public List<IElement> getElementsByXPath(String xpath) {
		List<IElement> children = new ArrayList<IElement>();
		for (Object child : getCurrentPage().getByXPath(xpath)) {
			if (child instanceof HtmlElement)
				children.add(new HtmlUnitElementImpl((HtmlElement) child));
		}
		return children;
	}

}
