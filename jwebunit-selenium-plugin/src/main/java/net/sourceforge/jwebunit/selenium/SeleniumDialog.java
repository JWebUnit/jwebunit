/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.selenium;


import java.net.URL;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.TestContext;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Acts as the wrapper for Selenium access. A dialog is initialized with a given
 * URL, and maintains conversational state as the dialog progresses through link
 * navigation, form submission, etc.
 * 
 * @author Julien Henry
 * 
 */
public class SeleniumDialog implements IJWebUnitDialog {
    /**
     * Logger for this class.
     */
    private static final Log LOGGER = LogFactory.getLog(SeleniumDialog.class);

    private Selenium selenium;

    // Timeout in milliseconds.  It's a string 'cause Selenium wants a string.
    private static final String timeout = "3000";
    private static final int port = 4444;

    private TestContext testContext;

    // The xpath string that identifie the current form
    // ie : @name='myForm'
    private String formIdent;

    // The xpath prefix that identifie the current frame
    // ie : /html/frameset/frame[@name='myFrame']
    private String currentFrame;

    public void beginAt(URL url, TestContext aTestContext) throws TestingEngineResponseException {
		// TODO Raccord de méthode auto-généré
		
	}

	public void clickElement(ClickableHtmlElementLocator htmlElement) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		
	}

	public String getAttributeValue(HtmlElementLocator htmlElement, String attribut) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public int getCount(HtmlElementLocator htmlElement) {
		// TODO Raccord de méthode auto-généré
		return 0;
	}

	public int getFrameCount() {
		// TODO Raccord de méthode auto-généré
		return 0;
	}

	public SelectOption[] getSelectedOptions(HtmlSelectLocator htmlSelect) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public SelectOption[] getSelectOptions(HtmlSelectLocator htmlSelect) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public Table getTable(HtmlTableLocator table) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public String getText(HtmlElementLocator htmlElement) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public void gotoFrame(FrameLocator frame) {
		// TODO Raccord de méthode auto-généré
		
	}

	public void gotoPage(URL url) throws TestingEngineResponseException {
		// TODO Raccord de méthode auto-généré
		
	}

	public void gotoWindow(WindowLocator window) {
		// TODO Raccord de méthode auto-généré
		
	}

	public boolean hasFrame(FrameLocator frame) {
		// TODO Raccord de méthode auto-généré
		return false;
	}

	public boolean hasWindow(WindowLocator window) {
		// TODO Raccord de méthode auto-généré
		return false;
	}

	public void selectOptions(List<HtmlOptionLocator> options) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		
	}

	public void setAttributeValue(HtmlElementLocator htmlElement, String attribut, String value) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		
	}

	public void setTextArea(HtmlTextAreaLocator textArea, String value) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		
	}

	public void unselectOptions(List<HtmlOptionLocator> options) throws ElementNotFoundException {
		// TODO Raccord de méthode auto-généré
		
	}

	public SeleniumDialog() {
    }

    public void beginAt(String aInitialURL, TestContext aTestContext)
            throws TestingEngineResponseException {
        this.setTestContext(aTestContext);
        selenium = new DefaultSelenium("localhost", port, "*chrome",
                aInitialURL);
        selenium.start();
        gotoPage(aInitialURL);
    }

    public void checkCheckbox(String checkBoxName, String value) {
        selenium.check("xpath=//input[@type='checkbox' and @name='"
                + checkBoxName + "' and @value='" + value + "']");
    }

    public void checkCheckbox(String checkBoxName) {
        selenium.check("xpath=//input[@type='checkbox' and @name='"
                + checkBoxName + "']");
    }

    public void clickButton(String buttonId) {
        selenium.click("id=buttonId");
        //selenium.waitForPageToLoad(timeout);
    }

    public void clickButtonWithText(String buttonValueText) {
        selenium.click("xpath=" + formSelector() + "//button[contains(.,'" + buttonValueText + "')]");
        //selenium.waitForPageToLoad(timeout);
    }

    public void clickElementByXPath(String xpath) {
        selenium.click("xpath=" + xpath);
        //selenium.waitForPageToLoad(timeout);
    }

    public void clickLink(String anID) {
        selenium.click("xpath=//a[@id='" + anID + "']");
        //selenium.waitForPageToLoad(timeout);
    }

    public void clickLinkWithExactText(String linkText, int index) {
        selenium.click("xpath=//a[.//*='" + linkText + "'][" + index + 1 + "]");
        //selenium.waitForPageToLoad(timeout);
    }

    public void clickLinkWithImage(String imageFileName, int index) {
        selenium.click("xpath=//a[contains(img/@src,'" + imageFileName + "')]");
        //selenium.waitForPageToLoad(timeout);
    }

    public void clickLinkWithText(String linkText, int index) {
        selenium.click("xpath=//a[contains(.,'" + linkText + "')][" + index + 1
                + "]");
        //selenium.waitForPageToLoad(timeout);
    }

    public void clickLinkWithTextAfterText(String linkText, String labelText) {
        throw new UnsupportedOperationException("clickLinkWithTextAfterText");
    }

    public void clickRadioOption(String radioGroup, String radioOptionValue) {
        selenium.click("xpath=" + formSelector() + "//input[@name='" + radioGroup + "' and @value='"
                + radioOptionValue + "']");
    }

    public void closeBrowser() throws TestingEngineResponseException {
        selenium.stop();
    }

    public void closeWindow() {
        selenium.close();
    }

    public List<Cookie> getCookies() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public String getCookieValue(String cookieName) {
        // TODO Implement getCookieValue in SeleniumDialog
        throw new UnsupportedOperationException("getCookieValue");
    }

    public String getFormElementNameBeforeLabel(String formElementLabel) {
        throw new UnsupportedOperationException("getFormElementNameBeforeLabel");
    }

    public String getFormElementNameForLabel(String formElementLabel) {
        throw new UnsupportedOperationException("getFormElementNameForLabel");
    }

    public String getFormElementValueBeforeLabel(String formElementLabel) {
        throw new UnsupportedOperationException(
                "getFormElementValueBeforeLabel");
    }

    public String getFormElementValueForLabel(String formElementLabel) {
        throw new UnsupportedOperationException("getFormElementValueForLabel");
    }

    public String getFormParameterValue(String paramName) {
        return selenium.getValue("xpath=" + formSelector() + "//*[@name='"
                + paramName + "']");
    }

    public String getPageSource() {
        return selenium.getHtmlSource();
    }

    public String getPageText() {
        return selenium.getText("xpath=/");
    }

    public String getPageTitle() {
        return selenium.getTitle();
    }

    public String[] getSelectedOptions(String selectName) {
        return selenium.getSelectedValues("xpath=" + formSelector()
                + "//select[@name='" + selectName + "']");
    }

    public String getSelectOptionLabelForValue(String selectName,
            String optionValue) {
        return selenium.getText("xpath=" + formSelector()
                + "//select/option[@value='" + optionValue + "']");
    }

    public String getSelectOptionValueForLabel(String selectName,
            String optionLabel) {
        return selenium.getValue("xpath=" + formSelector()
                + "//select/option[contains(.,'" + optionLabel + "']");
    }

    public String[] getSelectOptionValues(String selectName) {
        String[] labels = selenium.getSelectOptions("xpath=" + formSelector()
                + "//select[@name='" + selectName + "']");
        String[] values = new String[labels.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = getSelectOptionValueForLabel(selectName, labels[i]);
        }
        return values;
    }

    public String getServerResponse() {
        throw new UnsupportedOperationException("getServerResponse");
    }

    public void goBack() {
        selenium.goBack();
        //selenium.waitForPageToLoad(timeout);
    }

    public void gotoFrame(String frameName) {
        currentFrame = "/html/frameset/frame[@name='" + frameName + "']";
    }

    public void gotoPage(String url) throws TestingEngineResponseException {
        selenium.open(url);
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
        // to identify the element.  Is this the right thing to do?
        return selenium.isElementPresent("xpath=//button[@id=\'" + buttonId
                + "\']");
    }

    public boolean hasButtonWithText(String text) {
        return selenium.isElementPresent("xpath=" + formSelector() + "//button[contains(.,'" + text
                + "')]");
    }

    public boolean hasCookie(String cookieName) {
        // TODO Implement hasCookie in SeleniumDialog
        throw new UnsupportedOperationException("hasCookie");
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
        return selenium.isElementPresent("xpath=//form[@name='" + nameOrID
                + "' or @id='" + nameOrID + "']");
    }

    public boolean hasFormParameterLabeled(String paramLabel) {
        throw new UnsupportedOperationException("hasFormParameterLabeled");
    }

    public boolean hasFormParameterNamed(String paramName) {
        return selenium.isElementPresent("xpath=" + formSelector()
                + "//*[@name='" + paramName + "']");
    }

    public boolean hasFrame(String frameName) {
        // TODO Implement hasFrame in SeleniumDialog
        throw new UnsupportedOperationException("hasFrame");
    }

    public boolean hasLink(String anId) {
        return selenium.isElementPresent("xpath=//a[@id='" + anId + "']");
    }

    public boolean hasLinkWithExactText(String linkText, int index) {
        return selenium.isElementPresent("xpath=//a[.//*='" + linkText + "']["
                + index + 1 + "]");
    }

    public boolean hasLinkWithImage(String imageFileName, int index) {
        return selenium.isElementPresent("xpath=//a[contains(img/@src,'"
                + imageFileName + "')]");
    }

    public boolean hasLinkWithText(String linkText, int index) {
        return selenium.isElementPresent("xpath=//a[contains(.,'" + linkText
                + "')][" + index + 1 + "]");
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
            getSelectOptionLabelForValue(selectName, optionValue);
            return true;
        } catch (SeleniumException e) {
            return false;
        }
    }
    
    public boolean hasSubmitButton() {
        return selenium
        .isElementPresent("xpath=" + formSelector() + "//input[@type='submit']");
    }

    public boolean hasSubmitButton(String nameOrID, String value) {
        return selenium
                .isElementPresent("xpath=" + formSelector() + "//input[@type='submit' and (@id='"
                        + nameOrID + "' or @name='" + nameOrID
                        + "') and @value='" + value + "']");
    }

    public boolean hasSubmitButton(String nameOrID) {
        String xpath = "xpath=" + formSelector() + "//input[@type='submit' and (@id='"
        + nameOrID + "' or @name='" + nameOrID + "')]";
        LOGGER.debug("Execute isElementPresent("+xpath+")");
        return selenium
                .isElementPresent(xpath);
    }

    public boolean hasResetButton() {
        return selenium
        .isElementPresent("xpath=" + formSelector() + "//input[@type='reset']");
    }

    public boolean hasResetButton(String nameOrID) {
        return selenium
                .isElementPresent("xpath=" + formSelector() + "//input[@type='reset' and (@id='"
                        + nameOrID + "' or @name='" + nameOrID + "')]");
    }

    public boolean hasTable(String tableSummaryNameOrId) {
        return selenium.isElementPresent("xpath=//table[@id='"+tableSummaryNameOrId+"' or @name='"+tableSummaryNameOrId+"' or @summary='"+tableSummaryNameOrId+"']");
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

    public boolean isMatchInElement(String elementID, String regexp) {
        //TODO Implement isMatchInElement in SeleniumDialog
        throw new UnsupportedOperationException("isMatchInElement");
    }

    public boolean isTextInElement(String elementID, String text) {
        // TODO Implement isTextInElement in SeleniumDialog
        throw new UnsupportedOperationException("isTextInElement");
    }

    public void refresh() {
        selenium.refresh();
        //selenium.waitForPageToLoad(timeout);
    }

    public void reset() {
        selenium.click("xpath=" + formSelector() + "//input[@type='reset']");
        //selenium.waitForPageToLoad(timeout);
    }

    public void selectOptions(String selectName, String[] optionsValue) {
        for (int i=0; i<optionsValue.length; i++) {
            selenium.addSelection("xpath=" + formSelector() + "//select[@name='"+selectName+"']","value="+optionsValue[i]);
        }
    }

    public void setFormParameter(String paramName, String paramValue) {
        throw new UnsupportedOperationException("setFormParameter");
    }

    public void setScriptingEnabled(boolean value) {
        if (value==false)
            throw new UnsupportedOperationException("setScriptingEnabled");
    }

    public void setTextField(String inputName, String text) {
        //TODO Add textarea support and file support
        try {
            selenium.type("xpath=" + formSelector() + "//input[@name=\'"+inputName+"\' and (@type=\'text\' or @type=\'password\' or @type=\'file\')]", text);
        } catch (SeleniumException e) {
            try {
            selenium.type("xpath=" + formSelector() + "//textarea[@name=\'"+inputName+"\']", text);
            } catch(SeleniumException e2) {
                throw e;
            }
        }
    }

    public void setWorkingForm(String nameOrId, int index) {
        if (nameOrId != null)
            formIdent="(@name='"+nameOrId+"' or @id='"+nameOrId+"') and position()="+index;
        else
            formIdent=null;
    }

    public void submit() {
        selenium.click("xpath=" + formSelector() + "//input[@type='submit']");
        //selenium.waitForPageToLoad(timeout);
    }

    public void submit(String buttonName, String buttonValue) {
        selenium.click("xpath=" + formSelector() + "//input[@type='submit' and @name='"+buttonName+"' and @value='"+buttonValue+"']");
        //selenium.waitForPageToLoad(timeout);
    }

    public void submit(String buttonName) {
        selenium.click("xpath=" + formSelector() + "//input[@type='submit' and @name='"+buttonName+"']");
        //selenium.waitForPageToLoad(timeout);
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        selenium.uncheck("xpath=" + formSelector() + "//input[@type='checkbox' and @name='"
                + checkBoxName + "' and @value='" + value + "']");
    }

    public void uncheckCheckbox(String checkBoxName) {
        selenium.uncheck("xpath=" + formSelector() + "//input[@type='checkbox' and @name='"
                + checkBoxName + "']");
    }

    public void unselectOptions(String selectName, String[] options) {
        for (int i=0; i<options.length; i++) {
            selenium.removeSelection("xpath=" + formSelector() + "//select[@name='"+selectName+"']","value="+options[i]);
        }
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
        if (formIdent == null)
            return "";
        return "//form[" + formIdent + "]";
    }

    public int getWindowCount() {
        return selenium.getAllWindowTitles().length;
    }

    public void gotoWindow(int windowID) {
        selenium.selectWindow(""+windowID);
    }

    public String getTextFieldValue(String paramName) {
        //TODO implement getTextFieldValue in SeleniumDialog
        throw new UnsupportedOperationException("getTextFieldValue");
    }

    public String getHiddenFieldValue(String paramName) {
        //TODO implement getHiddenFieldValue in SeleniumDialog
        throw new UnsupportedOperationException("getHiddenFieldValue");
    }
    
    public String getJavascriptAlert() throws ElementNotFoundException {
        if (selenium.isAlertPresent()) {
            return selenium.getAlert(); 
        }
        else {
            throw new net.sourceforge.jwebunit.exception.ElementNotFoundException("There is no pending alert.");
        }
    }
}