package net.sourceforge.jwebunit.selenium;

import java.io.PrintStream;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.util.ExceptionUtility;
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

    private Selenium selenium;

    private static final int port = 4444;

    private TestContext testContext;

    // The xpath string that identifie the current form
    // ie : @name='myForm'
    private String formIdent;

    // The xpath prefix that identifie the current frame
    // ie : /html/frameset/frame[@name='myFrame']
    private String currentFrame;

    public SeleniumDialog() {
    }

    public void beginAt(String aInitialURL, TestContext aTestContext)
            throws TestingEngineResponseException {
        this.setTestContext(aTestContext);
        selenium = new DefaultSelenium("localhost", port, "*firefox",
                aInitialURL);
        selenium.start();
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
    }

    public void clickButtonWithText(String buttonValueText) {
        selenium.click("xpath=//button[contains(.,'" + buttonValueText + "')]");
    }

    public void clickElementByXPath(String xpath) {
        selenium.click("xpath=" + xpath);
    }

    public void clickLink(String anID) {
        selenium.click("xpath=//a[@id='" + anID + "']");
    }

    public void clickLinkWithExactText(String linkText, int index) {
        selenium.click("xpath=//a[.//*='" + linkText + "'][" + index + 1 + "]");
    }

    public void clickLinkWithImage(String imageFileName, int index) {
        selenium.click("xpath=//a[contains(img/@src,'" + imageFileName + "')]");
    }

    public void clickLinkWithText(String linkText, int index) {
        selenium.click("xpath=//a[contains(.,'" + linkText + "')][" + index + 1
                + "]");
    }

    public void clickLinkWithTextAfterText(String linkText, String labelText) {
        throw new UnsupportedOperationException("clickLinkWithTextAfterText");
    }

    public void clickRadioOption(String radioGroup, String radioOptionValue) {
        selenium.click("xpath=//input[@name='" + radioGroup + "' and @value='"
                + radioOptionValue + "']");
    }

    public void closeBrowser() throws TestingEngineResponseException {
        selenium.stop();
    }

    public void closeWindow() {
        selenium.close();
    }

    public String[][] getCookies() {
        // TODO Implement getCookies in SeleniumDialog
        throw new UnsupportedOperationException("getCookies");
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
        return selenium.getValue("xpath=//form[" + formIdent + "]/*[@name='"
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
        return selenium.getSelectedValues("xpath=//form[" + formIdent
                + "]/select[@name='" + selectName + "']");
    }

    public String getSelectOptionLabelForValue(String selectName,
            String optionValue) {
        return selenium.getText("xpath=//form[" + formIdent
                + "]/select/option[@value='" + optionValue + "']");
    }

    public String getSelectOptionValueForLabel(String selectName,
            String optionLabel) {
        return selenium.getValue("xpath=//form[" + formIdent
                + "]/select/option[contains(.,'" + optionLabel + "']");
    }

    public String[] getSelectOptionValues(String selectName) {
        String[] labels = selenium.getSelectOptions("xpath=//form[" + formIdent
                + "]/select[@name='" + selectName + "']");
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
    }

    public void gotoFrame(String frameName) {
        currentFrame = "/html/frameset/frame[@name='" + frameName + "']";
    }

    public void gotoPage(String url) throws TestingEngineResponseException {
        selenium.open(url);
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
        return selenium.isElementPresent("xpath=//button[@id='" + buttonId
                + "']");
    }

    public boolean hasButtonWithText(String text) {
        return selenium.isElementPresent("xpath=//button[contains(.,'" + text
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
        return selenium.isElementPresent("xpath=//form[" + formIdent
                + "]/*[@name='" + paramName + "']");
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
        return selenium.isElementPresent("xpath=//input[@name='" + radioGroup
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

    public boolean hasSubmitButton(String nameOrID, String value) {
        return selenium
                .isElementPresent("xpath=//input[@type='submit' and (@id='"
                        + nameOrID + "' or @name='" + nameOrID
                        + "') and @value='" + value + "']");
    }

    public boolean hasSubmitButton(String nameOrID) {
        return selenium
                .isElementPresent("xpath=//input[@type='submit' and (@id='"
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
        return selenium.isChecked("xpath=//input[@type='checkbox' and @name='"
                + checkBoxName + "']").equals("true");
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
    }

    public void reset() {
        selenium.click("xpath=//form["+formIdent+"]/input[@type='reset']");
    }

    public void selectOptions(String selectName, String[] optionsValue) {
        for (int i=0; i<optionsValue.length; i++) {
            selenium.addSelection("xpath=//form["+formIdent+"]/select[@name='"+selectName+"']","value="+optionsValue[i]);
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
        selenium.type("xpath=//form["+formIdent+"]/input[@name='"+inputName+"' and (@type=text or @type=password)]", text);
    }

    public void setWorkingForm(String nameOrId) {
        formIdent="@name='"+nameOrId+"' or @id='"+nameOrId+"'";
    }

    public void submit() {
        selenium.click("xpath=//form["+formIdent+"]/input[@type='submit']");
    }

    public void submit(String buttonName, String buttonValue) {
        selenium.click("xpath=//form["+formIdent+"]/input[@type='submit' and @name='"+buttonName+"' and @value='"+buttonValue+"']");
    }

    public void submit(String buttonName) {
        selenium.click("xpath=//form["+formIdent+"]/input[@type='submit' and @name='"+buttonName+"']");
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        selenium.uncheck("xpath=//input[@type='checkbox' and @name='"
                + checkBoxName + "' and @value='" + value + "']");
    }

    public void uncheckCheckbox(String checkBoxName) {
        selenium.uncheck("xpath=//input[@type='checkbox' and @name='"
                + checkBoxName + "']");
    }

    public void unselectOptions(String selectName, String[] options) {
        for (int i=0; i<options.length; i++) {
            selenium.removeSelection("xpath=//form["+formIdent+"]/select[@name='"+selectName+"']","value="+options[i]);
        }
    }

    public TestContext getTestContext() {
        return testContext;
    }

    public void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    public String[][] getSparseTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return null;
    }

    public String[][] getTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return null;
    }

}