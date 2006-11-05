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
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jaxen.JaxenException;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.SelectOption;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.locator.ClickableHtmlElementLocator;
import net.sourceforge.jwebunit.locator.FrameLocatorByName;
import net.sourceforge.jwebunit.locator.FrameLocator;
import net.sourceforge.jwebunit.locator.HtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlOptionLocator;
import net.sourceforge.jwebunit.locator.HtmlSelectLocator;
import net.sourceforge.jwebunit.locator.HtmlTableLocator;
import net.sourceforge.jwebunit.locator.HtmlTextAreaLocator;
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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
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
	 * Is Javascript enabled.
	 */
	private boolean jsEnabled = true;

	// Implementation of IJWebUnitDialog

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#setAttributeValue(net.sourceforge.jwebunit.locator.HtmlElementLocator,
	 *      java.lang.String, java.lang.String)
	 */
	public void setAttributeValue(HtmlElementLocator htmlElement,
			String attribut, String value)
			throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
		HtmlElement e = getOneElement(htmlElement);
		e.setAttributeValue(attribut, value);
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#setTextArea(net.sourceforge.jwebunit.locator.HtmlTextAreaLocator,
	 *      java.lang.String)
	 */
	public void setTextArea(HtmlTextAreaLocator textArea, String value)
			throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
		HtmlElement e = getOneElement(textArea);
		((HtmlTextArea) e).setText(value);
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCount(net.sourceforge.jwebunit.locator.HtmlElementLocator)
	 */
	public int getCount(HtmlElementLocator htmlElement) {
		return getElements(htmlElement).size();
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#getText(net.sourceforge.jwebunit.locator.HtmlElementLocator)
	 */
	public String getText(HtmlElementLocator htmlElement)
			throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
		HtmlElement e = getOneElement(htmlElement);
		return e.asText();
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
		HtmlElement e = getOneElement(htmlElement);
		try {
			ClickableElement ce = (ClickableElement) e;
			try {
				ce.click();
			} catch (IOException e1) {
				throw new RuntimeException("Unexpected error: click on "
						+ htmlElement.toString() + " failed.");
			}
		} catch (ClassCastException ex) {
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
		HtmlElement e = getOneElement(htmlElement);
		String value = e.getAttributeValue(attribut);
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
		HtmlElement e = getOneElement(htmlSelect);
		try {
			HtmlSelect sel = (HtmlSelect) e;
			List opts = sel.getSelectedOptions();
			SelectOption[] result = new SelectOption[opts.size()];
			for (int i = 0; i < result.length; i++)
				result[i] = new SelectOption(new HtmlOptionLocator(htmlSelect,
						i), ((HtmlOption) opts.get(i)).getValueAttribute(),
						((HtmlOption) opts.get(i)).getLabelAttribute());
			return result;
		} catch (ClassCastException ex) {
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
		HtmlElement e = getOneElement(htmlSelect);
		HtmlSelect sel = null;
		try {
			sel = (HtmlSelect) e;
		} catch (ClassCastException ex) {
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
	public Table getTable(HtmlTableLocator tableLocator)
			throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
		HtmlElement e = getOneElement(tableLocator);
		HtmlTable table = null;
		try {
			table = (HtmlTable) e;
		} catch (ClassCastException ex) {
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
			win = getFrame(((FrameLocatorByName) frame).getName());
		} else {
			throw new UnsupportedOperationException(
					"Unknow FrameLocator. gotoFrame should be updated.");
		}
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFrameCount()
	 */
	public int getFrameCount() {
		return ((HtmlPage) win.getEnclosedPage()).getFrames().size();
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoPage(java.net.URL)
	 */
	public void gotoPage(URL url) throws TestingEngineResponseException {
		try {
			wc.getPage(url);
			win = wc.getCurrentWindow();
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
			setMainWindow(getWindow(((WindowLocatorByName) window).getName()));
		} else {
			throw new UnsupportedOperationException(
					"Unknow WindowLocator. gotoWindow should be updated.");
		}
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFrame(net.sourceforge.jwebunit.locator.FrameLocator)
	 */
	public boolean hasFrame(FrameLocator frame) {
		boolean result = false;
		if (frame instanceof FrameLocatorByName) {
			result = (getFrame(((FrameLocatorByName) frame).getName()) != null);
		} else {
			throw new UnsupportedOperationException(
					"Unknow FrameLocator. hasFrame should be updated.");
		}
		return result;
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasWindow(net.sourceforge.jwebunit.locator.WindowLocator)
	 */
	public boolean hasWindow(WindowLocator window) {
		boolean result = false;
		if (window instanceof WindowLocatorByName) {
			result = (getWindow(((WindowLocatorByName) window).getName()) != null);
		} else {
			throw new UnsupportedOperationException(
					"Unknow FrameLocator. hasFrame should be updated.");
		}
		return result;
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#selectOptions(net.sourceforge.jwebunit.locator.HtmlSelectLocator,
	 *      net.sourceforge.jwebunit.locator.HtmlOptionLocator[])
	 */
	public void selectOptions(List<HtmlOptionLocator> options)
			throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
		for (HtmlOptionLocator opt : options) {
			HtmlSelect sel = (HtmlSelect) getOneElement(options.get(0)
					.getSelect());
			sel.setSelectedAttribute(((HtmlOption) getOneElement(opt)), true);
		}
	}

	/**
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#unselectOptions(net.sourceforge.jwebunit.locator.HtmlSelectLocator,
	 *      net.sourceforge.jwebunit.locator.HtmlOptionLocator[])
	 */
	public void unselectOptions(List<HtmlOptionLocator> options)
			throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
		for (HtmlOptionLocator opt : options) {
			HtmlSelect sel = (HtmlSelect) getOneElement(options.get(0)
					.getSelect());
			sel.setSelectedAttribute(((HtmlOption) getOneElement(opt)), false);
		}
	}

	public void closeBrowser() {
		wc = null;
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
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookies()
	 */
	public List<javax.servlet.http.Cookie> getCookies() {
		List<javax.servlet.http.Cookie> result = new LinkedList<javax.servlet.http.Cookie>();
		final HttpState stateForUrl = wc.getWebConnection().getState();
		Cookie[] cookies = stateForUrl.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(
					cookies[i].getName(), cookies[i].getValue());
			c.setComment(cookies[i].getComment());
			c.setDomain(cookies[i].getDomain());
			c.setPath(cookies[i].getPath());
			result.add(c);
		}
		return result;
	}

	public int getWindowCount() {
		return wc.getWebWindows().size();
	}

	public void closeWindow() {
		if (getWindowCount() == 1) {
			closeBrowser();
		} else {
			wc.deregisterWebWindow(win);
			win = wc.getCurrentWindow();
		}

	}

	public String getPageSource() {
		return getCurrentPage().getWebResponse().getContentAsString();
	}

	public String getPageTitle() {
		if (win.getEnclosedPage() instanceof HtmlPage) {
			return ((HtmlPage) win.getEnclosedPage()).getTitleText();
		}
		return "";
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
				}
				String win = event.getWebWindow().getName();
				Page oldPage = event.getOldPage();
				LOGGER.info("Window " + win + " closed : "
						+ ((HtmlPage) oldPage).getTitleText());
			}

			public void webWindowContentChanged(WebWindowEvent event) {
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
	}

	/**
	 * Return the window with the given name in the current conversation.
	 * 
	 * @param windowName
	 */
	protected WebWindow getWindow(String windowName) {
		return wc.getWebWindowByName(windowName);
	}

	protected HtmlElement getOneElement(HtmlElementLocator l)
			throws net.sourceforge.jwebunit.exception.ElementNotFoundException {
		List<HtmlElement> list = getElements(l);
		if (list.size() <= 0) {
			throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
					l);
		} else {
			return list.get(0);
		}
	}

	protected List<HtmlElement> getElements(HtmlElementLocator l) {
		return getElementsByXPath(getCurrentPage(), l.getXPath());
	}

	protected HtmlElement getElementByXPath(String xpath) {
		List<HtmlElement> l = getElementsByXPath(getCurrentPage(), xpath);
		if (l.size() > 0)
			return l.get(0);
		else
			return null;
	}

	protected List<HtmlElement> getElementsByXPath(Object parent, String xpath) {
		List<HtmlElement> l = new ArrayList<HtmlElement>();
		List li;
		try {
			final HtmlUnitXPath xp = new HtmlUnitXPath(xpath);
			li = xp.selectNodes(parent);
		} catch (JaxenException e) {
			return null;
		}
		for (Object o : li) {
			l.add((HtmlElement) o);
		}
		return l;
	}

	private HtmlPage getCurrentPage() {
		Page page = win.getEnclosedPage();
		if (page instanceof HtmlPage)
			return (HtmlPage) page;
		if (page instanceof UnexpectedPage)
			throw new RuntimeException("Unexpected content");
		return (HtmlPage) page;
	}

	/**
	 * Make the root window in the current conversation active.
	 */
	public void gotoRootWindow() {
		setMainWindow((WebWindow) wc.getWebWindows().get(0));
	}

	protected void setMainWindow(WebWindow win) {
		wc.setCurrentWindow(win);
		this.win = win;
	}

	/**
	 * Return the response for the given frame in the current conversation.
	 * 
	 * @param frameName
	 */
	protected WebWindow getFrame(String frameName) {
		return ((HtmlPage) win.getEnclosedPage()).getFrameByName(frameName);
	}

	/**
	 * @param testContext
	 *            The testContext to set.
	 */
	protected void setTestContext(TestContext testContext) {
		this.testContext = testContext;
	}

	/**
	 * @return Returns the testContext.
	 */
	protected TestContext getTestContext() {
		return testContext;
	}

}
