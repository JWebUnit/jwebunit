/**
 * Copyright (c) 2002-2014, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jwebunit.htmlunit;

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
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.WebWindowNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomComment;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow.CellIterator;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import net.sourceforge.jwebunit.api.HttpHeader;
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
import org.apache.http.auth.AuthScope;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
   * holder for alternative refresh handler.
   */
  private RefreshHandler refreshHandler;
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
   * Should throw exception on Javascript error.
   */
  private boolean throwExceptionOnScriptError = true;

  /**
   * Javascript alerts.
   */
  private List<JavascriptAlert> expectedJavascriptAlerts = new LinkedList<JavascriptAlert>();

  /**
   * Javascript confirms.
   */
  private List<JavascriptConfirm> expectedJavascriptConfirms = new LinkedList<JavascriptConfirm>();

  /**
   * Javascript prompts.
   */
  private List<JavascriptPrompt> expectedJavascriptPrompts = new LinkedList<JavascriptPrompt>();

  /**
   * The default browser version.
   */
  private BrowserVersion defaultBrowserVersion = BrowserVersion.FIREFOX_38;

  /**
     * Should we ignore failing status codes?
     */
  private boolean ignoreFailingStatusCodes = false;

  /**
  * Do we provide a timeout limit (in seconds)? Default 0 = unlimited timeout.
  */
  private int timeout = 0;

  // Implementation of IJWebUnitDialog

  /**
   * Initializes default HtmlUnit testing engine implementation.
   */
  public HtmlUnitTestingEngineImpl() {
  }

  /**
   * Initializes HtmlUnit testing engine implementation with web client.
   *
   * @param client web client
   */
  HtmlUnitTestingEngineImpl(WebClient client) {
    this.wc = client;
  }

  /**
   * Begin a dialog with an initial URL and test client context.
   *
   * @param initialURL absolute url at which to begin dialog.
   * @param context contains context information for the test client.
   * @throws TestingEngineResponseException
   */
  @Override
  public void beginAt(URL initialURL, TestContext context)
      throws TestingEngineResponseException {
    this.setTestContext(context);
    initWebClient();
    gotoPage(initialURL);
  }

  /**
   * Close the browser and check that all expected Javascript alerts, confirms and
   * prompts have been taken care of.
   */
  @Override
  public void closeBrowser() throws ExpectedJavascriptAlertException,
      ExpectedJavascriptConfirmException,
      ExpectedJavascriptPromptException {
    if (wc != null) {
      wc.close();
      wc = null;
    }
    form = null; // reset current form
    if (this.expectedJavascriptAlerts.size() > 0) {
      throw new ExpectedJavascriptAlertException(
          (expectedJavascriptAlerts.get(0))
              .getMessage());
    }
    if (this.expectedJavascriptConfirms.size() > 0) {
      throw new ExpectedJavascriptConfirmException(
          (expectedJavascriptConfirms.get(0))
              .getMessage());
    }
    if (this.expectedJavascriptPrompts.size() > 0) {
      throw new ExpectedJavascriptPromptException(
          (expectedJavascriptPrompts.get(0))
              .getMessage());
    }

  }

  /**
   * Go to a particular page.
   *
   * @throws TestingEngineResponseException if an error response code is encountered
   * 	and ignoreFailingStatusCodes is not enabled.
   */
  @Override
  public void gotoPage(URL initialURL) throws TestingEngineResponseException {
    try {
      wc.getPage(initialURL);
      win = wc.getCurrentWindow();
      form = null;
    } catch (FailingHttpStatusCodeException ex) {
      throw new TestingEngineResponseException(ex.getStatusCode(),
          "unexpected status code [" + ex.getStatusCode() + "] at URL: [" + initialURL + "]", ex);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see net.sourceforge.jwebunit.api.IJWebUnitDialog#setScriptingEnabled(boolean)
   */
  @Override
  public void setScriptingEnabled(boolean value) {
    // This variable is used to set Javascript before wc is instancied
    jsEnabled = value;
    if (wc != null) {
      wc.getOptions().setJavaScriptEnabled(value);
    }
  }

  @Override
  public void setThrowExceptionOnScriptError(boolean value) {
    throwExceptionOnScriptError = value;
    if (wc != null) {
      wc.getOptions().setThrowExceptionOnScriptError(value);
    }
  }

  @Override
  public List<javax.servlet.http.Cookie> getCookies() {
    List<javax.servlet.http.Cookie> result = new LinkedList<javax.servlet.http.Cookie>();
    Set<Cookie> cookies = wc.getCookieManager().getCookies();
    for (Cookie cookie : cookies) {
      javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(
          cookie.getName(), cookie.getValue());
      c.setComment(cookie.toHttpClient().getComment());
      c.setDomain(cookie.getDomain());
      Date expire = cookie.toHttpClient().getExpiryDate();
      if (expire == null) {
        c.setMaxAge(-1);
      } else {
        Date now = Calendar.getInstance().getTime();
        // Convert milli-second to second
        Long second = Long.valueOf((expire.getTime() - now.getTime()) / 1000);
        c.setMaxAge(second.intValue());
      }
      c.setPath(cookie.getPath());
      c.setSecure(cookie.toHttpClient().isSecure());
      c.setVersion(cookie.toHttpClient().getVersion());
      result.add(c);
    }
    return result;
  }

  @Override
  public boolean hasWindow(String windowName) {
    try {
      getWindow(windowName);
    } catch (WebWindowNotFoundException e) {
      return false;
    }
    return true;
  }

  @Override
  public boolean hasWindowByTitle(String title) {
    return getWindowByTitle(title) != null;
  }

  /**
   * Make the window with the given name in the current conversation active.
   *
   * @param windowName
   */
  @Override
  public void gotoWindow(String windowName) {
    setMainWindow(getWindow(windowName));
  }

  @Override
  public void gotoWindow(int windowID) {
    setMainWindow(wc.getWebWindows().get(windowID));
  }

  @Override
  public int getWindowCount() {
    return wc.getWebWindows().size();
  }

  /**
   * Goto first window with the given title.
   *
   * @param title
   */
  @Override
  public void gotoWindowByTitle(String title) {
    WebWindow window = getWindowByTitle(title);
    if (window != null) {
      setMainWindow(window);
    }
    else {
      throw new RuntimeException("No window found with title [" + title + "]");
    }
  }

  /**
   * Close the current window.
   */
  @Override
  public void closeWindow() {
    if (win != null) {
      ((TopLevelWindow) win.getTopWindow()).close();
      win = wc.getCurrentWindow();
      form = null;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasFrame(String frameNameOrId) {
    return getFrame(frameNameOrId) != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
  @Override
  public void setWorkingForm(int index) {
    setWorkingForm(getForm(index));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWorkingForm(String nameOrId, int index) {
    setWorkingForm(getForm(nameOrId, index));
  }

  /**
   * Return true if the current response contains a form.
   */
  @Override
  public boolean hasForm() {
    return ((HtmlPage) win.getEnclosedPage()).getForms().size() > 0;
  }

  /**
   * Return true if the current response contains a specific form.
   *
   * @param nameOrID name of id of the form to check for.
   */
  @Override
  public boolean hasForm(String nameOrID) {
    return getForm(nameOrID) != null;
  }

  @Override
  public boolean hasForm(String nameOrID, int index) {
    return getForm(nameOrID, index) != null;
  }

  @Override
  public boolean hasFormParameterNamed(String paramName) {
    for (HtmlElement e : getCurrentPage().getHtmlElementDescendants()) {
      if (e.getAttribute("name").equals(paramName)) {
        // set the working form if none has been set
        if (e.getEnclosingForm() != null && getWorkingForm() == null) {
          setWorkingForm(e.getEnclosingForm());
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Return the current value of a text input element with name <code>paramName</code>.
   *
   * @param paramName name of the input element. TODO: Find a way to handle multiple text input element with same
   *            name.
   */
  @Override
  public String getTextFieldValue(String paramName) {
    // first try the current form
    if (form != null) {
      for (HtmlElement e : form.getHtmlElementDescendants()) {
        if (e instanceof HtmlInput && e.getAttribute("name").equals(paramName)) {
          // we found it
          return ((HtmlInput) e).getValueAttribute();
        }
        if (e instanceof HtmlTextArea && e.getAttribute("name").equals(paramName)) {
          // we found it
          return ((HtmlTextArea) e).getText();
        }
      }
    }

    // not in the current form: try *all* elements
    HtmlElement outside_element = getHtmlElementWithAttribute("name", paramName);
    if (outside_element != null) {
      if (outside_element instanceof HtmlInput) {
        // set current form if not null
        if (outside_element.getEnclosingForm() != null) {
          form = outside_element.getEnclosingForm();
        }
        return ((HtmlInput) outside_element).getValueAttribute();
      }
      if (outside_element instanceof HtmlTextArea) {
        // set current form if not null
        if (outside_element.getEnclosingForm() != null) {
          form = outside_element.getEnclosingForm();
        }
        return ((HtmlTextArea) outside_element).getText();
      }
    }

    // we can't find it anywhere
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
  @Override
  public String getHiddenFieldValue(String paramName) {
    // first try the current form
    if (form != null) {
      for (HtmlElement e : form.getHtmlElementDescendants()) {
        if (e instanceof HtmlHiddenInput && e.getAttribute("name").equals(paramName)) {
          // we found it
          return ((HtmlInput) e).getValueAttribute();
        }
      }
    }

    // not in the current form: try *all* elements
    HtmlElement outside_element = getHtmlElementWithAttribute("name", paramName);
    if (outside_element != null) {
      if (outside_element instanceof HtmlHiddenInput) {
        // set current form if not null
        if (outside_element.getEnclosingForm() != null) {
          form = outside_element.getEnclosingForm();
        }
        return ((HtmlHiddenInput) outside_element).getValueAttribute();
      }
    }

    // we can't find it anywhere
    throw new RuntimeException("No hidden field with name [" + paramName
      + "] was found.");
  }

  /**
   * Set a form text, password input element or textarea to the provided value.
   *
   * @param fieldName name of the input element or textarea
   * @param text parameter value to submit for the element.
   */
  @Override
  public void setTextField(String paramName, String text) {
    // first try the current form
    if (form != null) {
      for (HtmlElement e : form.getHtmlElementDescendants()) {
        if (e instanceof HtmlInput && e.getAttribute("name").equals(paramName)) {
          // we found it
          ((HtmlInput) e).setValueAttribute(text);
          return;
        }
        if (e instanceof HtmlTextArea && e.getAttribute("name").equals(paramName)) {
          // we found it
          ((HtmlTextArea) e).setText(text);
          return;
        }
      }
    }

    // not in the current form: try *all* elements
    HtmlElement outside_element = getHtmlElementWithAttribute("name", paramName);
    if (outside_element != null) {
      if (outside_element instanceof HtmlInput) {
        ((HtmlInput) outside_element).setValueAttribute(text);
        // set current form if not null
        if (outside_element.getEnclosingForm() != null) {
          form = outside_element.getEnclosingForm();
        }
        return;
      }
      if (outside_element instanceof HtmlTextArea) {
        ((HtmlTextArea) outside_element).setText(text);
        // set current form if not null
        if (outside_element.getEnclosingForm() != null) {
          form = outside_element.getEnclosingForm();
        }
        return;
      }
    }

    // we can't find it anywhere
    throw new RuntimeException("No text field with name [" + paramName
      + "] was found.");
  }

  /**
   * Set a form hidden element to the provided value.
   *
   * @param fieldName name of the hidden input element
   * @param paramValue parameter value to submit for the element.
   */
  @Override
  public void setHiddenField(String fieldName, String text) {
    // first try the current form
    if (form != null) {
      for (HtmlElement e : form.getHtmlElementDescendants()) {
        if (e instanceof HtmlHiddenInput && e.getAttribute("name").equals(fieldName)) {
          // we found it
          ((HtmlHiddenInput) e).setValueAttribute(text);
          return;
        }
      }
    }

    // not in the current form: try *all* elements
    HtmlElement outside_element = getHtmlElementWithAttribute("name", fieldName);
    if (outside_element != null) {
      if (outside_element instanceof HtmlHiddenInput) {
        ((HtmlHiddenInput) outside_element).setValueAttribute(text);
        // set current form if not null
        if (outside_element.getEnclosingForm() != null) {
          form = outside_element.getEnclosingForm();
        }
        return;
      }
    }

    // we can't find it anywhere
    throw new RuntimeException("No hidden field with name [" + fieldName
      + "] was found.");
  }

  /**
   * Return a string array of select box option values.
   *
   * @param selectName name of the select box.
   */
  @Override
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
  @Override
  public String[] getSelectOptionValues(String selectName, int index) {
    List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
    if (sels == null || sels.size() < index + 1) {
      throw new RuntimeException("Did not find select with name [" + selectName
        + "] at index " + index);
    }
    HtmlSelect sel = sels.get(index);
    ArrayList<String> result = new ArrayList<String>();
    for (HtmlOption opt : sel.getOptions()) {
      result.add(opt.getValueAttribute());
    }
    return result.toArray(new String[result.size()]);
  }

  private String[] getSelectedOptions(HtmlSelect sel) {
    String[] result = new String[sel.getSelectedOptions().size()];
    int i = 0;
    for (HtmlOption opt : sel.getSelectedOptions()) {
      result[i++] = opt.getValueAttribute();
    }
    return result;
  }

  @Override
  public String[] getSelectedOptions(String selectName) {
    HtmlSelect sel = getForm().getSelectByName(selectName);
    return getSelectedOptions(sel);
  }

  @Override
  public String[] getSelectedOptions(String selectName, int index) {
    List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
    if (sels == null || sels.size() < index + 1) {
      throw new RuntimeException("Did not find select with name [" + selectName
        + "] at index " + index);
    }
    HtmlSelect sel = sels.get(index);
    return getSelectedOptions(sel);
  }

  private String getSelectOptionValueForLabel(HtmlSelect sel, String label) {
    for (HtmlOption opt : sel.getOptions()) {
      if (opt.asText().equals(label)) {
        return opt.getValueAttribute();
      }
    }
    throw new RuntimeException("Unable to find option " + label + " for "
      + sel.getNameAttribute());
  }

  @Override
  public String getSelectOptionValueForLabel(String selectName, String label) {
    HtmlSelect sel = getForm().getSelectByName(selectName);
    return getSelectOptionValueForLabel(sel, label);
  }

  @Override
  public String getSelectOptionValueForLabel(String selectName, int index, String label) {
    List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
    if (sels == null || sels.size() < index + 1) {
      throw new RuntimeException("Did not find select with name [" + selectName
        + "] at index " + index);
    }
    HtmlSelect sel = sels.get(index);
    return getSelectOptionValueForLabel(sel, label);
  }

  private String getSelectOptionLabelForValue(HtmlSelect sel, String value) {
    for (HtmlOption opt : sel.getOptions()) {
      if (opt.getValueAttribute().equals(value)) {
        return opt.asText();
      }
    }
    throw new RuntimeException("Unable to find option " + value + " for "
      + sel.getNameAttribute());
  }

  @Override
  public String getSelectOptionLabelForValue(String selectName, String value) {
    HtmlSelect sel = getForm().getSelectByName(selectName);
    return getSelectOptionLabelForValue(sel, value);
  }

  @Override
  public String getSelectOptionLabelForValue(String selectName, int index, String value) {
    List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
    if (sels == null || sels.size() < index + 1) {
      throw new RuntimeException("Did not find select with name [" + selectName
        + "] at index " + index);
    }
    HtmlSelect sel = sels.get(index);
    return getSelectOptionLabelForValue(sel, value);
  }

  @Override
  public URL getPageURL() {
    return win.getEnclosedPage().getWebResponse().getWebRequest().getUrl();
  }

  @Override
  public String getPageSource() {
    return win.getEnclosedPage().getWebResponse()
        .getContentAsString();
  }

  @Override
  public String getPageTitle() {
    return getCurrentPageTitle();
  }

  @Override
  public String getPageText() {
    Page page = win.getEnclosedPage();
    if (page instanceof HtmlPage) {
      return ((HtmlPage) page).getBody().asText();
    }
    if (page instanceof TextPage) {
      return ((TextPage) page).getContent();
    }
    if (page instanceof JavaScriptPage) {
      return ((JavaScriptPage) page).getContent();
    }
    if (page instanceof XmlPage) {
      return ((XmlPage) page).getContent();
    }
    if (page instanceof UnexpectedPage) {
      return ((UnexpectedPage) page).getWebResponse()
          .getContentAsString();
    }
    throw new RuntimeException(
        "Unexpected error in getPageText(). This method need to be updated.");
  }

  @Override
  public String getServerResponse() {
    StringBuffer result = new StringBuffer();
    WebResponse wr = wc.getCurrentWindow().getEnclosedPage()
        .getWebResponse();
    result.append(wr.getStatusCode()).append(" ").append(
        wr.getStatusMessage()).append("\n");
    result.append("Location: ").append(wr.getWebRequest().getUrl()).append("\n");
    for (NameValuePair h : wr.getResponseHeaders()) {
      result.append(h.getName()).append(": ").append(h.getValue())
          .append("\n");
    }
    result.append("\n");
    result.append(wr.getContentAsString());
    return result.toString();
  }

  @Override
  public InputStream getInputStream() {
    try {
      return wc.getCurrentWindow().getEnclosedPage().getWebResponse()
          .getContentAsStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (imageWindow != null) {
        wc.deregisterWebWindow(imageWindow);
      }
    }
  }

  /**
   * Create the {@link WebClient} that will be used for this test.
   * Subclasses should only override this method if they need to override
   * the default {@link WebClient}.
   *
   * <p>Also see issue 2697234.
   *
   * @author Jevon
   * @return A newly created {@link WebClient}
   */
  protected WebClient createWebClient() {
    /*
     * The user agent string is now provided by default to new test cases.
     * It can still be overridden if testContext.getUserAgent() is not
     * null (i.e. has been set manually.)
     *
     * @author Jevon
     */
    BrowserVersion bv;
    if (testContext.getUserAgent() != null) {
      bv = BrowserVersion.FIREFOX_38;
      bv.setUserAgent(testContext.getUserAgent());
    } else {
      bv = defaultBrowserVersion; // use default (which includes a full UserAgent string)
    }

    if (getTestContext().getProxyHost() != null && getTestContext().getProxyPort() > 0) {
      // Proxy configuration
      return new WebClient(bv, getTestContext().getProxyHost(), getTestContext().getProxyPort());
    } else {
      return new WebClient(bv);
    }
  }

  /**
   * Initialise the web client before accessing a page.
   */
  private void initWebClient() {

    wc = createWebClient();

    wc.getOptions().setJavaScriptEnabled(jsEnabled);
    wc.getOptions().setThrowExceptionOnFailingStatusCode(!ignoreFailingStatusCodes);
    wc.getOptions().setThrowExceptionOnScriptError(throwExceptionOnScriptError);
    wc.getOptions().setRedirectEnabled(true);
    wc.getOptions().setUseInsecureSSL(true);
    if (refreshHandler == null) {
      wc.setRefreshHandler(new ImmediateRefreshHandler());
    } else {
      wc.setRefreshHandler(refreshHandler);
    }
    wc.getOptions().setTimeout(timeout);
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
      creds.addCredentials(getTestContext().getProxyUser(),
          getTestContext().getProxyPasswd(), getTestContext()
              .getProxyHost(), getTestContext().getProxyPort(), AuthScope.ANY_REALM);
    }
    wc.setCredentialsProvider(creds);
    wc.addWebWindowListener(new WebWindowListener() {
      @Override
      public void webWindowClosed(WebWindowEvent event) {
        if (win == null || event.getOldPage().equals(win.getEnclosedPage())) {
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

      @Override
      public void webWindowContentChanged(WebWindowEvent event) {
        form = null;
        String winName = event.getWebWindow().getName();
        Page oldPage = event.getOldPage();
        Page newPage = event.getNewPage();
        String oldPageTitle = "no_html";
        if (oldPage instanceof HtmlPage) {
          oldPageTitle = ((HtmlPage) oldPage).getTitleText();
        }
        String newPageTitle = "no_html";
        if (newPage instanceof HtmlPage) {
          newPageTitle = ((HtmlPage) newPage).getTitleText();
        }
        logger.debug("Window \"{}\" changed : \"{}\" became \"{}", new Object[] {winName, oldPageTitle, newPageTitle});
      }

      @Override
      public void webWindowOpened(WebWindowEvent event) {
        String win = event.getWebWindow().getName();
        Page newPage = event.getNewPage();
        if (newPage instanceof HtmlPage) {
          logger.debug("Window {} opened : {}", win, ((HtmlPage) newPage).getTitleText());
        } else {
          logger.info("Window {} opened", win);
        }
      }
    });
    // Add Javascript Alert Handler
    wc.setAlertHandler(new AlertHandler() {
      @Override
      public void handleAlert(Page page, String msg) {
        if (expectedJavascriptAlerts.size() < 1) {
          throw new UnexpectedJavascriptAlertException(msg);
        } else {
          JavascriptAlert expected = expectedJavascriptAlerts
              .remove(0);
          if (!msg.equals(expected.getMessage())) {
            throw new UnexpectedJavascriptAlertException(msg);
          }
        }
      }
    });
    // Add Javascript Confirm Handler
    wc.setConfirmHandler(new ConfirmHandler() {
      @Override
      public boolean handleConfirm(Page page, String msg) {
        if (expectedJavascriptConfirms.size() < 1) {
          throw new UnexpectedJavascriptConfirmException(msg);
        } else {
          JavascriptConfirm expected = expectedJavascriptConfirms
              .remove(0);
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
      @Override
      public String handlePrompt(Page page, String msg) {
        if (expectedJavascriptPrompts.size() < 1) {
          throw new UnexpectedJavascriptPromptException(msg);
        } else {
          JavascriptPrompt expected = expectedJavascriptPrompts
              .remove(0);
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

    for (Map.Entry<String, String> requestHeader : requestHeaders.entrySet()) {
      wc.addRequestHeader(requestHeader.getKey(), requestHeader.getValue());
    }
  }

  /**
   * Return the window with the given name in the current conversation.
   *
   * @param windowName
   * @throws WebWindowNotFoundException if the window could not be found
   */
  public WebWindow getWindow(String windowName) {
    return wc.getWebWindowByName(windowName);
  }

  /**
   * Return the currently opened window (issue 2697234).
   *
   * @return the currently opened window
   */
  public WebWindow getCurrentWindow() {
    return win;
  }

  /**
   * Return the current web client (issue 2697234).
   *
   * @return the current web client
   */
  public WebClient getWebClient() {
    return wc;
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
   * Get all the comments in a document, as a list of strings.
   */
  @Override
  public List<String> getComments() {
    List<String> comments = new ArrayList<String>();
    getComments(comments, ((HtmlPage) win.getEnclosedPage()));

    return comments;
  }

  /**
   * Recursively find comments for all child nodes.
   *
   * @param comments
   * @param node
   */
  private void getComments(List<String> comments, Node node) {
    NodeList nodes = node.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node n = nodes.item(i);
      if (n instanceof DomComment) {
        comments.add(((DomComment) n).getData().trim());
      }
      // add all child nodes
      getComments(comments, n);
    }
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
  protected HtmlForm getForm() {
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
    return ((HtmlPage) win.getEnclosedPage()).getForms().get(
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
      if (nameOrID.equals(form.getId())
        || nameOrID.equals(form.getNameAttribute())) {
        forms.add(form);
      }
    }
    if (forms.size() > index) {
      return forms.get(index);
    }
    else {
      return null;
    }
  }

  private List<HtmlForm> getForms() {
    HtmlPage page = (HtmlPage) win.getEnclosedPage();
    return page.getForms();
  }

  protected HtmlPage getCurrentPage() {
    Page page = win.getEnclosedPage();
    if (page instanceof HtmlPage) {
      return (HtmlPage) page;
    }
    throw new RuntimeException("Non HTML content");
  }

  private void setWorkingForm(HtmlForm newForm) {
    if (newForm == null) {
      throw new UnableToSetFormException("Attempted to set form to null.");
    }
    form = newForm;
  }

  private HtmlForm getWorkingForm() {
    return form;
  }

  /**
   * Does an element with a certain attribute value exist in this page?
   *
  * @param attributeName attribute name to search for
  * @param value value to search for
  * @return true if the element is found
  */
  private boolean hasHtmlElementWithAttribute(String attributeName, String value) {
    return getHtmlElementWithAttribute(attributeName, value) != null;
  }

  /**
   * Get an element with a certain attribute value.
   *
   * @param attributeName attribute name to search for
   * @param value value to search for
   * @return the element found, or null
   */
  private HtmlElement getHtmlElementWithAttribute(String attributeName, String value) {
    for (HtmlElement e : getCurrentPage().getHtmlElementDescendants()) {
      if (e.getAttribute(attributeName).equals(value)) {
        return e;
      }
    }
    return null;
  }

  /**
     * Return true if a form parameter (input element) is present on the current response.
     *
     * @param selectName name of the input element to check for
     */
  public boolean hasFormSelectNamed(String selectName) {
    return hasHtmlElementWithAttribute("name", selectName);
  }

  /**
   * Return the HtmlUnit submit button with a given name.
   *
   * @param buttonName name of button.
   * @return the button
   */
  public HtmlElement getSubmitButton(String buttonName) {
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

  public HtmlElement getResetButton(String buttonName) {
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
        HtmlButton btn = (HtmlButton) o;
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
  public HtmlElement getSubmitButton(String buttonName,
      String buttonValue) {
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
  @Override
  public boolean hasSubmitButton() {
    final HtmlForm htmlForm = getForm();
    List<?> l = htmlForm.getByXPath("//input[@type='submit' or @type='image']");
    List<?> l2 = htmlForm.getByXPath("//button[@type='submit']");
    return (l.size() > 0 || l2.size() > 0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasSubmitButton(String buttonName) {
    return getSubmitButton(buttonName) != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasSubmitButton(String buttonName, String buttonValue) {
    try {
      return getSubmitButton(buttonName, buttonValue) != null;
    } catch (UnableToSetFormException e) {
      return false;
    }

  }

  @Override
  public boolean hasResetButton() {
    HtmlForm form = getForm();
    List<?> l = form.getByXPath("//input[@type='reset']");
    List<?> l2 = form.getByXPath("//button[@type='reset']");
    return (l.size() > 0 || l2.size() > 0);
  }

  @Override
  public boolean hasResetButton(String buttonName) {
    return getResetButton(buttonName) != null;
  }

  /**
   * Return the HtmlUnit Button with a given id.
   *
   * @param buttonId
   */
  private HtmlElement getButton(String buttonId) {
    HtmlElement btn = null;
    try {
      btn = getCurrentPage().getHtmlElementById(buttonId);
      if (btn instanceof HtmlButton || btn instanceof HtmlButtonInput
        || btn instanceof HtmlSubmitInput
        || btn instanceof HtmlResetInput) {
        return btn;
      }
    } catch (ElementNotFoundException e) {
      return null;
    }
    return null;
  }

  /**
   * Checks whether a button containing the specified text as its label exists.
   * For HTML input tags of type submit, reset, or button, this checks the
   * value attribute.  For HTML button tags, this checks the element's
   * content by retrieving the text content.
   *
   * <p>This method does not check whether the button is currently visible to the
   * client.
   *
   * @param text the text of the button (between &lt;button&gt;&lt;/button&gt;)
   * or the value of the "value" attribute.
   * @return <code>true</code> when the button with text could be found.
   */
  @Override
  public boolean hasButtonWithText(String text) {
    return getButtonWithText(text) != null ? true : false;
  }

  /**
   * Returns the first button that contains the specified text as its label.
   * For HTML input tags of type submit, reset, or button, this checks the
   * value attribute.  For HTML button tags, this checks the element's
   * content by retrieving the text content.
   *
   * <p>This method does not check whether the button is currently visible to the
   * client.
   *
   * @param buttonValueText the text of the button (between &lt;button&gt;&lt;/button&gt;)
   * or the value of the "value" attribute.
   * @return the ClickableElement with the specified text or null if
   * no such button is found.
   */
  public HtmlElement getButtonWithText(String buttonValueText) {
    if (buttonValueText == null) {
      throw new NullPointerException("Cannot search for button with null text");
    }

    List<? extends HtmlElement> l = ((HtmlPage) win.getEnclosedPage()).getDocumentElement()
        .getHtmlElementsByTagNames(
            Arrays.asList(new String[] {"button", "input"}));
    for (HtmlElement e : l) {
      if (e instanceof HtmlButton) {
        // we cannot use asText(), as this returns an empty string if the
        // button is not currently displayed, resulting in different
        // behaviour as the <input> Buttons
        if (buttonValueText.equals(((HtmlButton) e).getTextContent())) {
          return e;
        }
      }
      else if (e instanceof HtmlButtonInput ||
        e instanceof HtmlSubmitInput ||
        e instanceof HtmlResetInput) {
        if (buttonValueText.equals(e.getAttribute("value"))) {
          return e;
        }
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
  @Override
  public boolean hasButton(String buttonId) {
    try {
      return getButton(buttonId) != null;
    } catch (UnableToSetFormException e) {
      return false;
    }
  }

  @Override
  public boolean isCheckboxSelected(String checkBoxName) {
    HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
    return cb.isChecked();
  }

  @Override
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
          if (cellHtml.indexOf(text) != -1) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public Table getTable(String tableSummaryNameOrId) {
    HtmlTable table = getHtmlTable(tableSummaryNameOrId);
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
   * Return the HtmlUnit WebTable object representing a specified table in the current response. Null is returned if a
   * parsing exception occurs looking for the table or no table with the id or summary could be found.
   *
   * @param tableSummaryNameOrId summary or id of the table to return.
   */
  private HtmlTable getHtmlTable(String tableSummaryNameOrId) {
    try {
      return (HtmlTable) ((HtmlPage) win.getEnclosedPage())
          .getHtmlElementById(tableSummaryNameOrId);
    } catch (ElementNotFoundException e) {
      // Not found
    }
    try {
      return (HtmlTable) ((HtmlPage) win.getEnclosedPage())
          .getDocumentElement().getOneHtmlElementByAttribute("table",
              "summary", tableSummaryNameOrId);
    } catch (ElementNotFoundException e) {
      // Not found
    }
    try {
      return (HtmlTable) ((HtmlPage) win.getEnclosedPage())
          .getDocumentElement().getOneHtmlElementByAttribute("table",
              "name", tableSummaryNameOrId);
    } catch (ElementNotFoundException e) {
      // Not found
    }
    return null;
  }

  @Override
  public boolean hasTable(String tableSummaryNameOrId) {
    return getHtmlTable(tableSummaryNameOrId) != null;
  }

  /**
   * Submit the current form with the default submit button. See {@link #getForm}for an explanation of how the
   * current form is established.
   */
  @Override
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

    } catch (FailingHttpStatusCodeException e) {
      throw new TestingEngineResponseException(
          e.getStatusCode(), e);
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
  @Override
  public void submit(String buttonName) {
    List<HtmlElement> l = new LinkedList<HtmlElement>();
    l.addAll(getForm().getInputsByName(buttonName));
    l.addAll(getForm().getButtonsByName(buttonName));
    try {
      for (HtmlElement o : l) {
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
    } catch (FailingHttpStatusCodeException e) {
      throw new TestingEngineResponseException(
          e.getStatusCode(), e);
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
  @Override
  public void submit(String buttonName, String buttonValue) {
    List<HtmlElement> l = new LinkedList<HtmlElement>();
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
    } catch (FailingHttpStatusCodeException e) {
      // entirely possible that it can fail here
      throw new TestingEngineResponseException(
          e.getStatusCode(), e);
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
  @Override
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
  @Override
  public boolean hasLinkWithText(String linkText, int index) {
    return getLinkWithText(linkText, index) != null;
  }

  @Override
  public boolean hasLinkWithExactText(String linkText, int index) {
    return getLinkWithExactText(linkText, index) != null;
  }

  @Override
  public boolean hasLinkWithImage(String imageFileName, int index) {
    return getLinkWithImage(imageFileName, index) != null;
  }

  /**
   * Return true if a link is present in the current response with the specified id.
   *
   * @param anId link id to check for.
   */
  @Override
  public boolean hasLink(String anId) {
    try {
      ((HtmlPage) win.getEnclosedPage()).getHtmlElementById(anId);
    } catch (ElementNotFoundException e) {
      return false;
    }
    return true;
  }

  @Override
  public void clickLinkWithText(String linkText, int index) {
    HtmlAnchor link = getLinkWithText(linkText, index);
    if (link == null) {
      throw new RuntimeException("No Link found for \"" + linkText
        + "\" with index " + index);
    }
    try {
      link.click();
    } catch (IOException e) {
      throw new RuntimeException("Click failed", e);
    }
  }

  @Override
  public void clickLinkWithExactText(String linkText, int index) {
    HtmlAnchor link = getLinkWithExactText(linkText, index);
    if (link == null) {
      throw new RuntimeException("No Link found for \"" + linkText
        + "\" with index " + index);
    }
    try {
      link.click();
    } catch (IOException e) {
      throw new RuntimeException("Click failed", e);
    }
  }

  private HtmlCheckBoxInput getCheckbox(String checkBoxName) {
    Object[] l = getForm().getInputsByName(checkBoxName).toArray();
    for (int i = 0; i < l.length; i++) {
      if (l[i] instanceof HtmlCheckBoxInput) {
        return (HtmlCheckBoxInput) l[i];
      }
    }
    throw new RuntimeException("No checkbox with name [" + checkBoxName
      + "] was found in current form.");
  }

  private HtmlCheckBoxInput getCheckbox(String checkBoxName, String value) {
    Object[] l = getForm().getInputsByName(checkBoxName).toArray();
    for (int i = 0; i < l.length; i++) {
      if ((l[i] instanceof HtmlCheckBoxInput) &&
        ((HtmlCheckBoxInput) l[i]).getValueAttribute()
            .equals(value)) {
        return (HtmlCheckBoxInput) l[i];
      }
    }
    throw new RuntimeException("No checkbox with name [" + checkBoxName
      + "] and value [" + value + "] was found in current form.");
  }

  /**
   * Select a specified checkbox. If the checkbox is already checked then the checkbox will stay checked.
   *
   * @param checkBoxName name of checkbox to be deselected.
   */
  @Override
  public void checkCheckbox(String checkBoxName) {
    HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
    if (!cb.isChecked()) {
      try {
        cb.click();
      } catch (IOException e) {
        throw new RuntimeException("checkCheckbox failed", e);
      }
    }
  }

  @Override
  public void checkCheckbox(String checkBoxName, String value) {
    HtmlCheckBoxInput cb = getCheckbox(checkBoxName, value);
    if (!cb.isChecked()) {
      try {
        cb.click();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("checkCheckbox failed", e);
      }
    }
  }

  /**
   * Deselect a specified checkbox. If the checkbox is already unchecked then the checkbox will stay unchecked.
   *
   * @param checkBoxName name of checkbox to be deselected.
   */
  @Override
  public void uncheckCheckbox(String checkBoxName) {
    HtmlCheckBoxInput cb = getCheckbox(checkBoxName);
    if (cb.isChecked()) {
      try {
        cb.click();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("checkCheckbox failed", e);
      }
    }
  }

  @Override
  public void uncheckCheckbox(String checkBoxName, String value) {
    HtmlCheckBoxInput cb = getCheckbox(checkBoxName, value);
    if (cb.isChecked()) {
      try {
        cb.click();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("uncheckCheckbox failed", e);
      }
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
  @Override
  public void clickRadioOption(String radioGroup, String radioOption) {
    HtmlRadioButtonInput rb = getRadioOption(radioGroup, radioOption);
    if (!rb.isChecked()) {
      try {
        rb.click();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("checkCheckbox failed", e);
      }
    }
  }

  /**
   * Navigate by submitting a request based on a link with a given ID. A RuntimeException is thrown if no such link
   * can be found.
   *
   * @param anID id of link to be navigated.
   */
  @Override
  public void clickLink(String anID) {
    clickElementByXPath("//a[@id=\"" + anID + "\"]");
  }

  private HtmlAnchor getLinkWithImage(String filename, int index) {
    return (HtmlAnchor) getHtmlElementByXPath("(//a[img[contains(@src,\""
      + filename + "\")]])[" + (index + 1) + "]");
  }

  private HtmlAnchor getLinkWithText(String linkText, int index) {
    List<HtmlAnchor> lnks = ((HtmlPage) win.getEnclosedPage()).getAnchors();
    int count = 0;
    for (HtmlAnchor lnk : lnks) {
      if ((lnk.asText().indexOf(linkText) >= 0) && (count++ == index)) {
        return lnk;
      }
    }
    return null;
  }

  private HtmlAnchor getLinkWithExactText(String linkText, int index) {
    List<HtmlAnchor> lnks = ((HtmlPage) win.getEnclosedPage()).getAnchors();
    int count = 0;
    for (HtmlAnchor lnk : lnks) {
      if ((lnk.asText().equals(linkText)) && (count++ == index)) {
        return lnk;
      }
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
  @Override
  public void clickLinkWithImage(String imageFileName, int index) {
    HtmlAnchor link = getLinkWithImage(imageFileName, index);
    if (link == null) {
      throw new RuntimeException("No Link found with filename \""
        + imageFileName + "\" and index " + index);
    }
    try {
      link.click();
    } catch (IOException e) {
      throw new RuntimeException("Click failed", e);
    }
  }

  @Override
  public boolean hasElement(String anID) {
    return getHtmlElement(anID) != null;
  }

  @Override
  public boolean hasElementByXPath(String xpath) {
    return getHtmlElementByXPath(xpath) != null;
  }

  @Override
  public void clickElementByXPath(String xpath) {
    HtmlElement e = getHtmlElementByXPath(xpath);
    if (e == null) {
      throw new RuntimeException("No element found with xpath \"" + xpath
        + "\"");
    }
    try {
      e.click();
    } catch (IOException exp) {
      throw new RuntimeException("Click failed", exp);
    }
  }

  @Override
  public String getElementAttributByXPath(String xpath, String attribut) {
    HtmlElement e = getHtmlElementByXPath(xpath);
    if (e == null) {
      return null;
    }
    return e.getAttribute(attribut);
  }

  @Override
  public String getElementTextByXPath(String xpath) {
    HtmlElement e = getHtmlElementByXPath(xpath);
    if (e == null) {
      return null;
    }
    return e.asText();
  }

  /**
   * Click the indicated button (input type=button).
   *
   * @param buttonId
   */
  @Override
  public void clickButton(String buttonId) {
    HtmlElement btn = getButton(buttonId);
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
  @Override
  public void clickButtonWithText(String buttonValueText) {
    HtmlElement b = getButtonWithText(buttonValueText);
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
  @Override
  public boolean hasRadioOption(String radioGroup, String radioOption) {
    return getRadioOption(radioGroup, radioOption) != null;
  }

  @Override
  public String getSelectedRadio(String radioGroup) {
    List<HtmlRadioButtonInput> radios = getForm().getRadioButtonsByName(radioGroup);
    for (HtmlRadioButtonInput radio : radios) {
      if (radio.isChecked()) {
        return radio.getValueAttribute();
      }
    }
    throw new RuntimeException("Unexpected state: no radio button was selected in radio group [" + radioGroup + "]. Is it possible in a real browser?");
  }

  /**
   * Return true if a select box contains the indicated option.
   *
   * @param selectName name of the select box.
   * @param optionLabel label of the option.
   */
  @Override
  public boolean hasSelectOption(String selectName, String optionLabel) {
    String[] opts = getSelectOptionValues(selectName);
    for (int i = 0; i < opts.length; i++) {
      String label = getSelectOptionLabelForValue(selectName, opts[i]);
      if (label.equals(optionLabel)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return true if a select box contains the indicated option.
   *
   * @param selectName name of the select box.
   * @param optionValue value of the option.
   */
  @Override
  public boolean hasSelectOptionValue(String selectName, String optionValue) {
    String[] opts = getSelectOptionValues(selectName);
    for (int i = 0; i < opts.length; i++) {
      if (opts[i].equals(optionValue)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Select the specified set of options in the select element
   * with the provided name.
   * @param selectName name of the select box
   * @param options set of options to select.
   */
  @Override
  public void selectOptions(String selectName, String[] options) {
    HtmlSelect sel = getForm().getSelectByName(selectName);
    if (!sel.isMultipleSelectEnabled() && options.length > 1) {
      throw new RuntimeException("Multiselect not enabled");
    }
    for (String option : options) {
      boolean found = false;
      for (HtmlOption opt : sel.getOptions()) {
        if (opt.getValueAttribute().equals(option)) {
          sel.setSelectedAttribute(opt, true);
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("Option " + option
          + " not found");
      }
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
  @Override
  public boolean hasSelectOption(String selectName, int index, String optionLabel) {
    String[] opts = getSelectOptionValues(selectName, index);
    for (int i = 0; i < opts.length; i++) {
      String label = getSelectOptionLabelForValue(selectName, index, opts[i]);
      if (label.equals(optionLabel)) {
        return true;
      }
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
  @Override
  public boolean hasSelectOptionValue(String selectName, int index, String optionValue) {
    String[] opts = getSelectOptionValues(selectName, index);
    for (int i = 0; i < opts.length; i++) {
      if (opts[i].equals(optionValue)) {
        return true;
      }
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
  @Override
  public void selectOptions(String selectName, int index, String[] options) {
    List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
    if (sels == null || sels.size() < index + 1) {
      throw new RuntimeException("Did not find select with name [" + selectName
        + "] at index " + index);
    }
    HtmlSelect sel = sels.get(index);
    if (!sel.isMultipleSelectEnabled() && options.length > 1) {
      throw new RuntimeException("Multiselect not enabled");
    }
    for (String option : options) {
      boolean found = false;
      for (HtmlOption opt : sel.getOptions()) {
        if (opt.getValueAttribute().equals(option)) {
          sel.setSelectedAttribute(opt, true);
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("Option " + option
          + " not found");
      }
    }
  }

  @Override
  public void unselectOptions(String selectName, String[] options) {
    HtmlSelect sel = getForm().getSelectByName(selectName);
    if (!sel.isMultipleSelectEnabled() && options.length > 1) {
      throw new RuntimeException("Multiselect not enabled");
    }
    for (String option : options) {
      boolean found = false;
      for (HtmlOption opt : sel.getOptions()) {
        if (opt.asText().equals(option)) {
          sel.setSelectedAttribute(opt, false);
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("Option " + option
          + " not found");
      }
    }
  }

  @Override
  public void unselectOptions(String selectName, int index, String[] options) {
    List<HtmlSelect> sels = getForm().getSelectsByName(selectName);
    if (sels == null || sels.size() < index + 1) {
      throw new RuntimeException("Did not find select with name [" + selectName
        + "] at index " + index);
    }
    HtmlSelect sel = sels.get(index);
    if (!sel.isMultipleSelectEnabled() && options.length > 1) {
      throw new RuntimeException("Multiselect not enabled");
    }
    for (String option : options) {
      boolean found = false;
      for (HtmlOption opt : sel.getOptions()) {
        if (opt.asText().equals(option)) {
          sel.setSelectedAttribute(opt, false);
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("Option " + option
          + " not found");
      }
    }
  }

  @Override
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

  @Override
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
   * {@inheritDoc}
   */
  @Override
  public void gotoRootWindow() {
    win = win.getTopWindow();
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
  protected TestContext getTestContext() {
    return testContext;
  }

  @Override
  public void setExpectedJavaScriptAlert(JavascriptAlert[] alerts)
      throws ExpectedJavascriptAlertException {
    if (this.expectedJavascriptAlerts.size() > 0) {
      throw new ExpectedJavascriptAlertException(
          (expectedJavascriptAlerts.get(0))
              .getMessage());
    }
    for (int i = 0; i < alerts.length; i++) {
      expectedJavascriptAlerts.add(alerts[i]);
    }
  }

  @Override
  public void setExpectedJavaScriptConfirm(JavascriptConfirm[] confirms)
      throws ExpectedJavascriptConfirmException {
    if (this.expectedJavascriptConfirms.size() > 0) {
      throw new ExpectedJavascriptConfirmException(
          (expectedJavascriptConfirms.get(0))
              .getMessage());
    }
    for (int i = confirms.length - 1; i >= 0; i--) {
      expectedJavascriptConfirms.add(confirms[i]);
    }
  }

  @Override
  public void setExpectedJavaScriptPrompt(JavascriptPrompt[] prompts)
      throws ExpectedJavascriptPromptException {
    if (this.expectedJavascriptPrompts.size() > 0) {
      throw new ExpectedJavascriptPromptException(
          (expectedJavascriptPrompts.get(0))
              .getMessage());
    }
    for (int i = prompts.length - 1; i >= 0; i--) {
      expectedJavascriptPrompts.add(prompts[i]);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementByXPath(java.lang.String)
   */
  @Override
  public IElement getElementByXPath(String xpath) {
    HtmlElement element = this.getHtmlElementByXPath(xpath);
    if (element != null) {
      return new HtmlUnitElementImpl(element);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementByID(java.lang.String)
   */
  @Override
  public IElement getElementByID(String id) {
    HtmlElement element = this.getHtmlElement(id);
    if (element != null) {
      return new HtmlUnitElementImpl(element);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementsByXPath(java.lang.String)
   */
  @Override
  public List<IElement> getElementsByXPath(String xpath) {
    List<IElement> children = new ArrayList<IElement>();
    for (Object child : getCurrentPage().getByXPath(xpath)) {
      if (child instanceof HtmlElement) {
        children.add(new HtmlUnitElementImpl((HtmlElement) child));
      }
    }
    return children;
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sourceforge.jwebunit.api.ITestingEngine#getServerResponseCode()
   */
  @Override
  public int getServerResponseCode() {
    return getWebResponse().getStatusCode();
  }

  /**
   * Get the last WebResponse from HtmlUnit.
   */
  public WebResponse getWebResponse() {
    return wc.getCurrentWindow().getEnclosedPage().getWebResponse();
  }

  /*
   * @param ignoreFailingStatusCodes the ignoreFailingStatusCodes to set
   */
  @Override
  public void setIgnoreFailingStatusCodes(boolean ignore) {
    ignoreFailingStatusCodes = ignore;
    if (wc != null) {
      wc.getOptions().setThrowExceptionOnFailingStatusCode(!ignore);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sourceforge.jwebunit.api.ITestingEngine#getHeader(java.lang.String)
   */
  @Override
  public String getHeader(String name) {
    return getWebResponse().getResponseHeaderValue(name);
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sourceforge.jwebunit.api.ITestingEngine#getAllHeaders()
   */
  @Override
  @Deprecated
  public Map<String, String> getAllHeaders() {
    Map<String, String> map = new java.util.HashMap<String, String>();
    for (NameValuePair header : getWebResponse().getResponseHeaders()) {
      map.put(header.getName(), header.getValue());
    }
    return map;
  }

  @Override
  public List<HttpHeader> getResponseHeaders() {
    List<HttpHeader> result = new LinkedList<HttpHeader>();
    for (NameValuePair header : getWebResponse().getResponseHeaders()) {
      result.add(new HttpHeader(header.getName(), header.getValue()));
    }
    return result;
  }

  /**
   * An alternative to setting the {@link TestContext#setUserAgent(String) user agent string manually}
   * is to provide it with all the information for a complete browser version.
   *
   * @see com.gargoylesoftware.htmlunit.BrowserVersion
   * @return The default browser version
   */
  public BrowserVersion getDefaultBrowserVersion() {
    return defaultBrowserVersion;
  }

  /**
   * An alternative to setting the {@link TestContext#setUserAgent(String) user agent string manually}
   * is to provide it with all the information for a complete browser version.
   *
   * @see com.gargoylesoftware.htmlunit.BrowserVersion
   * @param the browser version to set as default for this engine instance
   */
  public void setDefaultBrowserVersion(BrowserVersion defaultBrowserVersion) {
    this.defaultBrowserVersion = defaultBrowserVersion;
  }

  @Override
  public void setTimeout(int milliseconds) {
    if (wc != null && wc.getWebConnection() != null) {
      throw new IllegalArgumentException("Cannot set the timeout when the WebConnection has already been created.");
    }
    timeout = milliseconds;
  }

  public void setRefreshHandler(RefreshHandler handler) {
    this.refreshHandler = handler;

    if (wc != null) {
      wc.setRefreshHandler(refreshHandler);
    }
  }

}
