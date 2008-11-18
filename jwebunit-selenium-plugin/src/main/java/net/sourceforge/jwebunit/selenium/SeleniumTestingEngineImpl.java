/*************************************************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net) * Distributed open-source, see full license under
 * LICENCE.txt *
 ************************************************************************************************************/
package net.sourceforge.jwebunit.selenium;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.api.ITestingEngine;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptAlertException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptConfirmException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptPromptException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.javascript.JavascriptAlert;
import net.sourceforge.jwebunit.javascript.JavascriptConfirm;
import net.sourceforge.jwebunit.javascript.JavascriptPrompt;
import net.sourceforge.jwebunit.util.TestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Acts as the wrapper for Selenium access. A testing engine is initialized with a given URL, and maintains
 * conversational state as the dialog progresses through link navigation, form submission, etc.
 * 
 * @author Julien Henry
 */
public class SeleniumTestingEngineImpl implements ITestingEngine {

	/**
	 * Logger for this class.
	 */
	private final Logger logger = LoggerFactory.getLogger(SeleniumTestingEngineImpl.class);

	private Selenium selenium;

	// Timeout in milliseconds. It's a string 'cause Selenium wants a string.
	private static final String timeout = "3000";
	private static final int port = 4444;

	private TestContext testContext;

	// The xpath string that identifie the current form
	// ie : @name='myForm'
	private String formIdent;

	// The xpath prefix that identifie the current frame
	// ie : /html/frameset/frame[@name='myFrame']
	private String currentFrame;

	public SeleniumTestingEngineImpl() {
	}

	public void beginAt(URL aInitialURL, TestContext aTestContext) throws TestingEngineResponseException {
		this.setTestContext(aTestContext);
		selenium = new DefaultSelenium("localhost", port, "*chrome", aInitialURL.toString());
		selenium.start();
		gotoPage(aInitialURL);
	}

	public void checkCheckbox(String checkBoxName, String value) {
		selenium.check("xpath=//input[@type='checkbox' and @name='" + checkBoxName + "' and @value='" + value
				+ "']");
	}

	public void checkCheckbox(String checkBoxName) {
		selenium.check("xpath=//input[@type='checkbox' and @name='" + checkBoxName + "']");
	}

	public void clickButton(String buttonId) {
		selenium.click("id=buttonId");
		selenium.waitForPageToLoad(timeout);
	}

	public void clickButtonWithText(String buttonValueText) {
		selenium.click("xpath=" + formSelector() + "//button[contains(.,'" + buttonValueText + "')]");
		selenium.waitForPageToLoad(timeout);
	}

	public void clickElementByXPath(String xpath) {
		selenium.click("xpath=" + xpath);
		selenium.waitForPageToLoad(timeout);
	}

	public void clickLink(String anID) {
		selenium.click("xpath=//a[@id='" + anID + "']");
		selenium.waitForPageToLoad(timeout);
	}

	public void clickLinkWithExactText(String linkText, int index) {
		selenium.click("xpath=//a[.//*='" + linkText + "'][" + index + 1 + "]");
		selenium.waitForPageToLoad(timeout);
	}

	public void clickLinkWithImage(String imageFileName, int index) {
		selenium.click("xpath=//a[contains(img/@src,'" + imageFileName + "')][" + index + 1 + "]");
		selenium.waitForPageToLoad(timeout);
	}

	public void clickLinkWithText(String linkText, int index) {
		selenium.click("xpath=//a[contains(.,'" + linkText + "')][" + index + 1 + "]");
		selenium.waitForPageToLoad(timeout);
	}

	public void clickRadioOption(String radioGroup, String radioOptionValue) {
		selenium.click("xpath=" + formSelector() + "//input[@type='radio' and @name='" + radioGroup
				+ "' and @value='" + radioOptionValue + "']");
	}

	public void closeBrowser() throws TestingEngineResponseException {
		selenium.stop();
	}

	public void closeWindow() {
		selenium.close();
	}

	public String getPageSource() {
		return selenium.getHtmlSource();
	}

	public String getPageText() {
		return selenium.getBodyText();
	}

	public String getPageTitle() {
		return selenium.getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getCookies()
	 */
	public List getCookies() {
		List l = new LinkedList();
		// FIXME How to parse this String in Cookie
		l.add(selenium.getCookie());
		return l;
	}

	public String[] getSelectedOptions(String selectName) {
		return selenium.getSelectedValues("xpath=" + formSelector() + "//select[@name='" + selectName + "']");
	}

	public String getSelectOptionLabelForValue(String selectName, String optionValue) {
		return selenium.getText("xpath=" + formSelector() + "//select[@name='" + selectName
				+ "']/option[@value='" + optionValue + "']");
	}

	public String getSelectOptionValueForLabel(String selectName, String optionLabel) {
		return selenium.getValue("xpath=" + formSelector() + "//select[@name='" + selectName
				+ "']/option[contains(.,'" + optionLabel + "')]");
	}

	public String[] getSelectOptionValues(String selectName) {
		String[] labels = selenium.getSelectOptions("xpath=" + formSelector() + "//select[@name='"
				+ selectName + "']");
		String[] values = new String[labels.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getSelectOptionValueForLabel(selectName, labels[i]);
		}
		return values;
	}

	public String[] getSelectedOptions(String selectName, int index) {
		throw new UnsupportedOperationException("getSelectedOptions(String selectName, int index)");
	}

	public String getSelectOptionLabelForValue(String selectName, int index, String optionValue) {
		throw new UnsupportedOperationException(
				"getSelectOptionLabelForValue(String selectName, int index, String optionValue)");
	}

	public String getSelectOptionValueForLabel(String selectName, int index, String optionLabel) {
		throw new UnsupportedOperationException(
				"getSelectOptionValueForLabel(String selectName, int index, String optionLabel)");
	}

	public String[] getSelectOptionValues(String selectName, int index) {
		throw new UnsupportedOperationException("getSelectOptionValues(String selectName, int index)");
	}

	public String getServerResponse() {
		throw new UnsupportedOperationException("getServerResponse");
	}

	public void goBack() {
		selenium.goBack();
		selenium.waitForPageToLoad(timeout);
	}

	public void gotoFrame(String frameName) {
		currentFrame = "/html/frameset/frame[@name='" + frameName + "']";
	}

	public void gotoPage(URL url) throws TestingEngineResponseException {
		selenium.open(url.toString());
		// selenium.waitForPageToLoad(timeout); implicit after open()
	}

	public void gotoRootWindow() {
		selenium.selectWindow("null");
	}

	public void gotoWindow(String windowName) {
		selenium.selectWindow(windowName);
	}

	public void gotoWindowByTitle(String title) {
		// TODO Implement gotoWindowByTitle in SeleniumDialog
		throw new UnsupportedOperationException("gotoWindowByTitle");
	}

	public boolean hasButton(String buttonId) {
		// Not bothering with formSelector here because we're using an ID
		// to identify the element. Is this the right thing to do?
		return selenium.isElementPresent("xpath=//button[@id='" + buttonId + "']");
	}

	public boolean hasButtonWithText(String text) {
		return selenium.isElementPresent("xpath=" + formSelector() + "//button[contains(.,'" + text + "')]");
	}

	public boolean hasElement(String anID) {
		return selenium.isElementPresent("xpath=//*[@id='" + anID + "']");
	}

	public boolean hasElementByXPath(String xpath) {
		return selenium.isElementPresent("xpath=" + xpath);
	}

	public boolean hasForm() {
		return selenium.isElementPresent("xpath=//form");
	}

	public boolean hasForm(String nameOrID) {
		return selenium.isElementPresent("xpath=//form[@name='" + nameOrID + "' or @id='" + nameOrID + "']");
	}

	public boolean hasFormParameterNamed(String paramName) {
		return selenium.isElementPresent("xpath=" + formSelector() + "//*[@name='" + paramName + "']");
	}

	public boolean hasFrame(String frameName) {
		return selenium.isElementPresent("xpath=//frame[@name='" + frameName + "']");
	}

	public boolean hasLink(String anId) {
		return selenium.isElementPresent("xpath=//a[@id='" + anId + "']");
	}

	public boolean hasLinkWithExactText(String linkText, int index) {
		return selenium.isElementPresent("xpath=//a[.//*='" + linkText + "'][" + index + 1 + "]");
	}

	public boolean hasLinkWithImage(String imageFileName, int index) {
		return selenium.isElementPresent("xpath=//a[contains(img/@src,'" + imageFileName + "')][" + index + 1
				+ "]");
	}

	public boolean hasLinkWithText(String linkText, int index) {
		return selenium.isElementPresent("xpath=//a[contains(.,'" + linkText + "')][" + index + 1 + "]");
	}

	public boolean hasRadioOption(String radioGroup, String radioOptionValue) {
		return selenium.isElementPresent("xpath=" + formSelector() + "//input[@name='" + radioGroup
				+ "' and @value='" + radioOptionValue + "']");
	}

	public boolean hasSelectOption(String selectName, String optionLabel) {
		try {
			getSelectOptionValueForLabel(selectName, optionLabel);
			return true;
		} catch (SeleniumException e) {
			return false;
		}
	}

	public boolean hasSelectOptionValue(String selectName, String optionValue) {
		try {
			return getSelectOptionLabelForValue(selectName, optionValue) != null;
		} catch (SeleniumException e) {
			return false;
		}
	}

	public boolean hasSelectOption(String selectName, int index, String optionLabel) {
		boolean equals;
		try {
			equals = getSelectedOptions(selectName)[index].equals(optionLabel);
		} catch (RuntimeException e) {
			logger.error("", e);
			equals = false;
		}
		return equals;
	}

	public boolean hasSelectOptionValue(String selectName, int index, String optionValue) {
		return false;
	}

	public boolean hasSubmitButton() {
		String xpath1 = formSelector() + "//input[@type='submit' or @type='image']";
		String xpath2 = formSelector() + "//button[@type='submit']";
		return selenium.isElementPresent("xpath=" + xpath1 + "|" + xpath2);
	}

	public boolean hasSubmitButton(String nameOrID, String value) {
		String xpath1 = formSelector() + "//input[(@type='submit' or @type='image') and (@id='" + nameOrID
				+ "' or @name='" + nameOrID + "') and @value='" + value + "']";
		String xpath2 = formSelector() + "//button[@type='submit' and (@id='" + nameOrID + "' or @name='"
				+ nameOrID + "') and @value='" + value + "']";
		return selenium.isElementPresent("xpath=" + xpath1 + "|" + xpath2);
	}

	public boolean hasSubmitButton(String nameOrID) {
		String xpath1 = formSelector() + "//input[(@type='submit' or @type='image') and (@id='" + nameOrID
				+ "' or @name='" + nameOrID + "')]";
		String xpath2 = formSelector() + "//button[@type='submit' and (@id='" + nameOrID + "' or @name='"
				+ nameOrID + "')]";
		return selenium.isElementPresent("xpath=" + xpath1 + "|" + xpath2);
	}

	public boolean hasResetButton() {
		return selenium.isElementPresent("xpath=" + formSelector() + "//input[@type='reset']");
	}

	public boolean hasResetButton(String nameOrID) {
		return selenium.isElementPresent("xpath=" + formSelector() + "//input[@type='reset' and (@id='"
				+ nameOrID + "' or @name='" + nameOrID + "')]");
	}

	public boolean hasTable(String tableSummaryNameOrId) {
		return selenium.isElementPresent("xpath=//table[@id='" + tableSummaryNameOrId + "' or @name='"
				+ tableSummaryNameOrId + "' or @summary='" + tableSummaryNameOrId + "']");
	}

	public boolean hasWindow(String windowName) {
		// TODO Implement hasWindow in SeleniumDialog
		throw new UnsupportedOperationException("hasWindow");
	}

	public boolean hasWindowByTitle(String title) {
		// TODO Implement hasWindowByTitle in SeleniumDialog
		throw new UnsupportedOperationException("hasWindowByTitle");
	}

	public boolean isCheckboxSelected(String checkBoxName) {
		return selenium.isChecked("xpath=" + formSelector() + "//input[@type='checkbox' and @name='"
				+ checkBoxName + "']");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#isCheckboxSelected(java.lang.String, java.lang.String)
	 */
	public boolean isCheckboxSelected(String checkBoxName, String checkBoxValue) {
		return selenium.isChecked("xpath=" + formSelector() + "//input[@type='checkbox' and @name='"
				+ checkBoxName + "' and @value='" + checkBoxValue + "']");
	}

	public boolean isMatchInElement(String elementID, String regexp) {
		boolean match;
		try {
			String locator = "id=" + elementID;
			String elementText = selenium.getText(locator);
			if(elementText == null || elementText.equals("")) elementText = selenium.getValue(locator);
			match = (elementText != null ? elementText.matches(regexp) : false);
		} catch (RuntimeException e) {
			logger.error("", e);
			match = false;
		}
		return match;
	}

	public boolean isTextInElement(String elementID, String text) {
		boolean contains;
		try {
			String locator = "id=" + elementID;
			String elementText = selenium.getText(locator);
			if(elementText == null || elementText.equals("")) elementText = selenium.getValue(locator);
			contains = (elementText != null ? elementText.contains(text) : false);
		} catch (RuntimeException e) {
			logger.error("", e);
			contains = false;
		}
		return contains;
	}

	public void refresh() {
		selenium.refresh();
		selenium.waitForPageToLoad(timeout);
	}

	public void reset() {
		selenium.click("xpath=" + formSelector() + "//input[@type='reset']");
		selenium.waitForPageToLoad(timeout);
	}

	public void selectOptions(String selectName, String[] optionsValue) {
		if(optionsValue.length == 1) {
			selenium.select("xpath=" + formSelector() + "//select[@name='" + selectName + "']", "value="
					+ optionsValue[0]);
		} else {
			for (int i = 0; i < optionsValue.length; i++) {
				selenium.addSelection("xpath=" + formSelector() + "//select[@name='" + selectName + "']",
						"value=" + optionsValue[i]);
			}
		}
	}

	public void selectOptions(String selectName, int index, String[] optionsValue) {
	}

	public void setScriptingEnabled(boolean value) {
		if(value == false) throw new UnsupportedOperationException("setScriptingEnabled");
	}

	public void setTextField(String inputName, String text) {
		String xpath1 = formSelector() + "//input[@name='" + inputName
				+ "' and (@type='text' or @type='password' or @type='file')]";
		String xpath2 = formSelector() + "//textarea[@name='" + inputName + "']";
		selenium.type("xpath=" + xpath1 + "|" + xpath2, text);
	}

	public void setHiddenField(String inputName, String text) {
		String xpath = formSelector() + "//input[@name='" + inputName + "' and @type='hidden']";
		selenium.type("xpath=" + xpath, text);
	}

	public void setWorkingForm(String nameOrId, int index) {
		if(nameOrId != null) formIdent = "(@name='" + nameOrId + "' or @id='" + nameOrId + "')][ position()="
				+ (index + 1);
		else formIdent = null;
	}

	public void submit() {
		String xpath1 = formSelector() + "//input[@type='submit' or @type='image']";
		String xpath2 = formSelector() + "//button[@type='submit']";
		selenium.click("xpath=" + xpath1 + "|" + xpath2);
		selenium.waitForPageToLoad(timeout);
	}

	public void submit(String nameOrID, String value) {
		String xpath1 = formSelector() + "//input[(@type='submit' or @type='image') and (@id='" + nameOrID
				+ "' or @name='" + nameOrID + "') and @value='" + value + "']";
		String xpath2 = formSelector() + "//button[@type='submit' and (@id='" + nameOrID + "' or @name='"
				+ nameOrID + "') and @value='" + value + "']";
		selenium.click("xpath=" + xpath1 + "|" + xpath2);
		selenium.waitForPageToLoad(timeout);
	}

	public void submit(String nameOrID) {
		String xpath1 = formSelector() + "//input[(@type='submit' or @type='image') and (@id='" + nameOrID
				+ "' or @name='" + nameOrID + "')]";
		String xpath2 = formSelector() + "//button[@type='submit' and (@id='" + nameOrID + "' or @name='"
				+ nameOrID + "')]";
		selenium.click("xpath=" + xpath1 + "|" + xpath2);
		selenium.waitForPageToLoad(timeout);
	}

	public void uncheckCheckbox(String checkBoxName, String value) {
		selenium.uncheck("xpath=" + formSelector() + "//input[@type='checkbox' and @name='" + checkBoxName
				+ "' and @value='" + value + "']");
	}

	public void uncheckCheckbox(String checkBoxName) {
		selenium.uncheck("xpath=" + formSelector() + "//input[@type='checkbox' and @name='" + checkBoxName
				+ "']");
	}

	public void unselectOptions(String selectName, String[] options) {
		for (int i = 0; i < options.length; i++) {
			selenium.removeSelection("xpath=" + formSelector() + "//select[@name='" + selectName + "']",
					"value=" + options[i]);
		}
	}

	public void unselectOptions(String selectName, int index, String[] options) {
		throw new UnsupportedOperationException(
				"unselectOptions(String selectName, int index, String[] options");
	}

	public TestContext getTestContext() {
		return testContext;
	}

	public void setTestContext(TestContext testContext) {
		this.testContext = testContext;
	}

	public Table getTable(String tableSummaryNameOrId) {
		// TODO Auto-generated method stub
		return null;
	}

	protected String formSelector() {
		if(formIdent == null) return "";
		return "//form[" + formIdent + "]";
	}

	public int getWindowCount() {
		// TODO implement getWindowCount in SeleniumDialog
		throw new UnsupportedOperationException("getWindowCount");
	}

	public void gotoWindow(int windowID) {
		selenium.selectWindow("" + windowID);
	}

	public String getTextFieldValue(String paramName) {
		// TODO implement getTextFieldValue in SeleniumDialog
		throw new UnsupportedOperationException("getTextFieldValue");
	}

	public String getHiddenFieldValue(String paramName) {
		// TODO implement getHiddenFieldValue in SeleniumDialog
		throw new UnsupportedOperationException("getHiddenFieldValue");
	}

	public String getJavascriptAlert() throws ElementNotFoundException {
		if(selenium.isAlertPresent()) {
			return selenium.getAlert();
		} else {
			throw new net.sourceforge.jwebunit.exception.ElementNotFoundException(
					"There is no pending alert.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementAttributByXPath(java.lang.String,
	 *      java.lang.String)
	 */
	public String getElementAttributByXPath(String xpath, String attribut) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("getElementAttributByXPath");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementTextByXPath(java.lang.String)
	 */
	public String getElementTextByXPath(String xpath) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("getElementTextByXPath");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getInputStream()
	 */
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("getInputStream");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getInputStream(java.net.URL)
	 */
	public InputStream getInputStream(URL url) throws TestingEngineResponseException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("getInputStream");
	}

	public URL getPageURL() {
		URL url;
		try {
			url = new URL(selenium.getLocation());
		} catch (MalformedURLException e) {
			logger.error("", e);
			url = null;
		}
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getSelectedRadio(java.lang.String)
	 */
	public String getSelectedRadio(String radioGroup) {
		int count = 0;
		while (selenium.isElementPresent("xpath=" + formSelector() + "//input[@type='radio' and @name='"
				+ radioGroup + "'][" + (count + 1) + "]")) {
			if("on".equals(selenium.getValue("xpath=" + formSelector() + "//input[@type='radio' and @name='"
					+ radioGroup + "'][" + (count + 1) + "]"))) { return selenium.getAttribute("xpath="
					+ formSelector() + "//input[@type='radio' and @name='" + radioGroup + "'][" + (count + 1)
					+ "]@value"); }
			count++;
		}
		return null;
	}

	protected int getRadioCount(String radioGroup) {
		int count = 0;
		while (selenium.isElementPresent("xpath=" + formSelector() + "//input[@type='radio' and @name='"
				+ radioGroup + "'][" + (count + 1) + "]")) {
			count++;
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#setExpectedJavaScriptAlert(net.sourceforge.jwebunit.javascript.JavascriptAlert[])
	 */
	public void setExpectedJavaScriptAlert(JavascriptAlert[] alerts) throws ExpectedJavascriptAlertException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("setExpectedJavaScriptAlert");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#setExpectedJavaScriptConfirm(net.sourceforge.jwebunit.javascript.JavascriptConfirm[])
	 */
	public void setExpectedJavaScriptConfirm(JavascriptConfirm[] confirms)
			throws ExpectedJavascriptConfirmException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("setExpectedJavaScriptConfirm");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#setExpectedJavaScriptPrompt(net.sourceforge.jwebunit.javascript.JavascriptPrompt[])
	 */
	public void setExpectedJavaScriptPrompt(JavascriptPrompt[] prompts)
			throws ExpectedJavascriptPromptException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("setExpectedJavaScriptPrompt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#setWorkingForm(int)
	 */
	public void setWorkingForm(int index) {
		formIdent = "position()=" + (index + 1);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementByXPath(java.lang.String)
	 */
	public IElement getElementByXPath(String xpath) {
		// TODO implement method
		throw new UnsupportedOperationException("getElementByXPath");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementByID(java.lang.String)
	 */
	public IElement getElementByID(String id) {
		// TODO implement method
		throw new UnsupportedOperationException("getElementByID");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getElementsByXPath(java.lang.String)
	 */
	public List<IElement> getElementsByXPath(String xpath) {
		// TODO implement method
		throw new UnsupportedOperationException("getElementsByXPath");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getServerResponseCode()
	 */
	public int getServerResponseCode() {
		// TODO implement method
		// I'm not even sure if Selenium can get this response code out.
		throw new UnsupportedOperationException("getServerResponseCode");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getAllHeaders()
	 */
	public Map<String, String> getAllHeaders() {
		// TODO implement method
		throw new UnsupportedOperationException("getAllHeaders");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#getHeader(java.lang.String)
	 */
	public String getHeader(String name) {
		// TODO implement method
		throw new UnsupportedOperationException("getHeader");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.ITestingEngine#setIgnoreFailingStatusCodes(boolean)
	 */
	public void setIgnoreFailingStatusCodes(boolean ignore) {
		// TODO implement method
		throw new UnsupportedOperationException("setIgnoreFailingStatusCodes");
	}

	public boolean hasDomComment(String comment) {
		// TODO implement method
		throw new UnsupportedOperationException("hasDomComment");
	}

}
