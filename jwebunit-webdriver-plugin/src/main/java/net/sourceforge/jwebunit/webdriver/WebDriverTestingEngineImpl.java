/**
 * Copyright (c) 2010, JWebUnit team.
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
package net.sourceforge.jwebunit.webdriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.api.ITestingEngine;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptAlertException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptConfirmException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptPromptException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.javascript.JavascriptAlert;
import net.sourceforge.jwebunit.javascript.JavascriptConfirm;
import net.sourceforge.jwebunit.javascript.JavascriptPrompt;
import net.sourceforge.jwebunit.util.TestContext;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Acts as the wrapper for Webdriver access. A testing engine is initialized with a given URL, and maintains
 * conversational state as the dialog progresses through link navigation, form submission, etc.
 * 
 * @author Julien Henry
 */
public class WebDriverTestingEngineImpl implements ITestingEngine {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(WebDriverTestingEngineImpl.class);
    private WebDriver driver;
    private TestContext testContext;
    // The xpath string that identifie the current form
    // ie : @name='myForm'
    private String formIdent;

    public WebDriverTestingEngineImpl() {
    }

    public void beginAt(URL aInitialURL, TestContext aTestContext) throws TestingEngineResponseException {
        this.setTestContext(aTestContext);
        driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3);

        gotoPage(aInitialURL);
    }

    public void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    public void closeBrowser() throws ExpectedJavascriptAlertException, ExpectedJavascriptConfirmException, ExpectedJavascriptPromptException {
        driver.close();
    }

    public void gotoPage(URL url) throws TestingEngineResponseException {
        driver.get(url.toString());
    }

    public void setScriptingEnabled(boolean value) {
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setJavascriptEnabled(value);
        }
    }

    public void setThrowExceptionOnScriptError(boolean value) {
    }

    public List<javax.servlet.http.Cookie> getCookies() {
        List<javax.servlet.http.Cookie> result = new LinkedList<javax.servlet.http.Cookie>();
        Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(
                    cookie.getName(), cookie.getValue());
            c.setDomain(cookie.getDomain());
            Date expire = cookie.getExpiry();
            if (expire == null) {
                c.setMaxAge(-1);
            } else {
                Date now = Calendar.getInstance().getTime();
                // Convert milli-second to second
                Long second = Long.valueOf((expire.getTime() - now.getTime()) / 1000);
                c.setMaxAge(second.intValue());
            }
            c.setPath(cookie.getPath());
            c.setSecure(cookie.isSecure());
            result.add(c);
        }
        return result;
    }

    public boolean hasWindow(String windowName) {
        //Save current handle
        String current = driver.getWindowHandle();
        try {
            driver.switchTo().window(windowName);
            driver.switchTo().window(current);
            return true;
        } catch (NoSuchWindowException e) {
            return false;
        }
    }

    public boolean hasWindowByTitle(String windowTitle) {
        //Save current handle
        String current = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getTitle().equals(windowTitle)) {
                driver.switchTo().window(current);
                return true;
            }
        }
        driver.switchTo().window(current);
        return false;
    }

    public void gotoWindow(String windowName) {
        driver.switchTo().window(windowName);
    }

    public void gotoWindowByTitle(String title) {
        //Save current handle
        String current = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getTitle().equals(title)) {
                return;
            }
        }
        driver.switchTo().window(current);
    }

    public void gotoWindow(int windowID) {
        driver.switchTo().window("" + windowID);
    }

    public void gotoRootWindow() {
        driver.switchTo().window("top");
    }

    public int getWindowCount() {
        return driver.getWindowHandles().size();
    }

    public void closeWindow() {
        driver.close();
    }

    public boolean hasFrame(String frameNameOrId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void gotoFrame(String frameNameOrId) {
        driver.switchTo().frame(frameNameOrId);
    }

    public void setWorkingForm(int index) {
        formIdent = "position()=" + (index + 1);
    }

    public void setWorkingForm(String nameOrId, int index) {
        if (nameOrId != null) {
            formIdent = "(@name='" + nameOrId + "' or @id='" + nameOrId + "')][position()="
                    + (index + 1);
        } else {
            formIdent = null;
        }
    }

    protected String formSelector() {
        if (formIdent == null) {
            return "//form";
        }
        return "//form[" + formIdent + "]";
    }

    public boolean hasForm() {
        return hasElementByXPath("//form");
    }

    public boolean hasForm(String nameOrID) {
        return hasElementByXPath("//form[@name='" + nameOrID + "' or @id='" + nameOrID + "']");
    }

    public boolean hasFormParameterNamed(String paramName) {
        return hasElementByXPath(formSelector() + "//*[@name='" + paramName + "']");
    }

    private WebElement getWebElementByXPath(String xpathAfterForm, boolean onlyInCurrentForm) {
        //First try the current form
        if (formIdent != null) {
            try {
                return driver.findElement(By.xpath("//form[" + formIdent + "]" + xpathAfterForm));
            } catch (NoSuchElementException ex) {
                if (onlyInCurrentForm) {
                    return null;
                }
            }
        }
        // not in the current form: try other forms
        List<WebElement> forms = driver.findElements(By.tagName("form"));
        int index = 0;
        for (WebElement f : forms) {
            try {
                WebElement e = driver.findElement(By.xpath("//form[position()=" + (index + 1) + "]" + xpathAfterForm));
                setWorkingForm(index);
                return e;
            } catch (NoSuchElementException ex) {
                index ++;
            }
        }
        // now look everywhere (maybe outside of form)
        try {
            return driver.findElement(By.xpath("//body" + xpathAfterForm));
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    private List<WebElement> getWebElementsByXPath(String xpathAfterForm) {
        try {
            return driver.findElements(By.xpath(formSelector() + xpathAfterForm));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public String getTextFieldValue(String paramName) {
        WebElement e = getWebElementByXPath("/input[@type='text' and @name='" + paramName + "']", false);
        return e.getValue();
    }

    public String getHiddenFieldValue(String paramName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTextField(String inputName, String text) {
        WebElement e = getWebElementByXPath("/input[@type='text' and @name='" + inputName + "']", false);
        if (e == null) {
            e = getWebElementByXPath("/textarea[@name='" + inputName + "']", false);
        }
        if (e == null) {
            e = getWebElementByXPath("/input[@type='file' and @name='" + inputName + "']", false);
        }
        e.clear();
        e.sendKeys(text);
    }

    public void setHiddenField(String inputName, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getSelectOptionValues(String selectName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getSelectOptionValues(String selectName, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getSelectedOptions(String selectName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getSelectedOptions(String selectName, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSelectOptionLabelForValue(String selectName, String optionValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSelectOptionLabelForValue(String selectName, int index, String optionValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSelectOptionValueForLabel(String selectName, String optionLabel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSelectOptionValueForLabel(String selectName, int index, String optionLabel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void selectOptions(String selectName, String[] optionValues) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void selectOptions(String selectName, int index, String[] optionValues) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unselectOptions(String selectName, String[] optionValues) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unselectOptions(String selectName, int index, String[] optionValues) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasSelectOption(String selectName, String optionLabel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasSelectOptionValue(String selectName, String optionValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasSelectOption(String selectName, int index, String optionLabel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasSelectOptionValue(String selectName, int index, String optionValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCheckboxSelected(String checkBoxName) {
        WebElement e = getWebElementByXPath("/input[@type='checkbox' and @name='" + checkBoxName + "']", true);
        return e.isSelected();
    }

    public boolean isCheckboxSelected(String checkBoxName, String checkBoxValue) {
        WebElement e = getWebElementByXPath("/input[@type='checkbox' and @name='" + checkBoxName + "' and @value='" + checkBoxValue + "']", true);
        return e.isSelected();
    }

    public void checkCheckbox(String checkBoxName) {
        WebElement e = getWebElementByXPath("/input[@type='checkbox' and @name='" + checkBoxName + "']", true);
        if (!e.isSelected()) {
            e.toggle();
        }
    }

    public void checkCheckbox(String checkBoxName, String checkBoxValue) {
        WebElement e = getWebElementByXPath("/input[@type='checkbox' and @name='" + checkBoxName + "' and @value='" + checkBoxValue + "']", true);
        if (!e.isSelected()) {
            e.toggle();
        }
    }

    public void uncheckCheckbox(String checkBoxName) {
        WebElement e = getWebElementByXPath("/input[@type='checkbox' and @name='" + checkBoxName + "']", true);
        if (e.isSelected()) {
            e.toggle();
        }
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        WebElement e = getWebElementByXPath("/input[@type='checkbox' and @name='" + checkBoxName + "' and @value='" + value + "']", true);
        if (e.isSelected()) {
            e.toggle();
        }
    }

    public void clickRadioOption(String radioGroup, String radioOptionValue) {
        WebElement e = getWebElementByXPath("/input[@type='radio' and @name='" + radioGroup + "' and @value='" + radioOptionValue + "']", false);
        e.click();
    }

    public boolean hasRadioOption(String radioGroup, String radioOptionValue) {
        WebElement e = getWebElementByXPath("/input[@type='radio' and @name='" + radioGroup + "' and @value='" + radioOptionValue + "']", false);
        return e != null;
    }

    public String getSelectedRadio(String radioGroup) {
        List<WebElement> radios = getWebElementsByXPath("/input[@type='radio' and @name='" + radioGroup + "']");
        for (WebElement r : radios) {
            if (r.isSelected()) {
                return r.getValue();
            }
        }
        return null;
    }

    public boolean hasSubmitButton() {
        return (getWebElementByXPath("/input[@type='submit' or @type='image']", true) != null) || (getWebElementByXPath("/button[@type='submit']", false) != null);
    }

    public boolean hasSubmitButton(String nameOrID) {
        return (getWebElementByXPath("/input[(@type='submit' or @type='image') and (@name='" + nameOrID + "' or @id='" + nameOrID + "')]", true) != null)
                || (getWebElementByXPath("/button[@type='submit' and (@name='" + nameOrID + "' or @id='" + nameOrID + "')]", true) != null);
    }

    public boolean hasSubmitButton(String nameOrID, String value) {
        return (getWebElementByXPath("/input[(@type='submit' or @type='image') and (@name='" + nameOrID + "' or @id='" + nameOrID + "') and @value='" + value + "']", true) != null)
                || (getWebElementByXPath("/button[@type='submit' and (@name='" + nameOrID + "' or @id='" + nameOrID + "') and @value='" + value + "']", true) != null);
    }

    public void submit() {
        driver.findElement(By.xpath(formSelector())).submit();
    }

    public void submit(String nameOrID) {
        WebElement e = getWebElementByXPath("/input[(@type='submit' or @type='image') and (@name='" + nameOrID + "' or @id='" + nameOrID + "')]", true);
        if (e == null) {
            e = getWebElementByXPath("/button[@type='submit' and (@name='" + nameOrID + "' or @id='" + nameOrID + "')]", true);
        }
        e.submit();
    }

    public void submit(String buttonName, String buttonValue) {
        WebElement e = getWebElementByXPath("/input[(@type='submit' or @type='image') and (@name='" + buttonName + "' or @id='" + buttonName + "') and @value='" + buttonValue + "']", true);
        if (e == null) {
            e = getWebElementByXPath("/button[@type='submit' and (@name='" + buttonName + "' or @id='" + buttonName + "') and @value='" + buttonValue + "']", true);
        }
        e.submit();
    }

    public boolean hasResetButton() {
        return getWebElementByXPath("/input[@type='reset']", false) != null;
    }

    public boolean hasResetButton(String nameOrID) {
        return getWebElementByXPath("/input[@type='reset' and (@name='" + nameOrID + "' or @id='" + nameOrID + "')]", true) != null;
    }

    public void reset() {
        getWebElementByXPath("/input[@type='reset']", true).click();;
    }

    private WebElement getButton(String nameOrID) {
        WebElement e = getWebElementByXPath("/input[(@type='submit' or @type='image' or @type='reset' or @type='button') and (@name='" + nameOrID + "' or @id='" + nameOrID + "')]", false);
        if (e == null) {
            e = getWebElementByXPath("/button[@type='submit' and (@name='" + nameOrID + "' or @id='" + nameOrID + "')]", false);
        }
        return e;
    }

    private WebElement getButtonWithText(String text) {
        WebElement e = getWebElementByXPath("/input[(@type='submit' or @type='image' or @type='reset' or @type='button') and contains(.,'" + text + "')]", false);
        if (e == null) {
            e = getWebElementByXPath("/button[@type='submit' and contains(.,'" + text + "')]", false);
        }
        return e;
    }

    public boolean hasButtonWithText(String text) {
        return getButtonWithText(text) != null;
    }

    public boolean hasButton(String buttonId) {
        return getButton(buttonId) != null;
    }

    public void clickButton(String buttonId) {
        getButton(buttonId).click();
    }

    public void clickButtonWithText(String buttonValueText) {
        getButtonWithText(buttonValueText).click();
    }

    public URL getPageURL() {
        try {
            return new URL(driver.getCurrentUrl());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getPageText() {
        return driver.findElement(By.xpath("//body")).getText();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getServerResponse() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getInputStream(URL url) throws TestingEngineResponseException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasTable(String tableSummaryNameOrId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Table getTable(String tableSummaryNameOrId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasLinkWithText(String linkText, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasLinkWithExactText(String linkText, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasLinkWithImage(String imageFileName, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasLink(String anId) {
        return driver.findElement(By.xpath("//a[@id='" + anId + "']")) != null;
    }

    public void clickLinkWithText(String linkText, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clickLinkWithExactText(String linkText, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clickLink(String anID) {
        driver.findElement(By.xpath("//a[@id='" + anID + "']")).click();
    }

    public void clickLinkWithImage(String imageFileName, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasElement(String anID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasElementByXPath(String xpath) {
        try {
            driver.findElement(By.xpath(xpath));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickElementByXPath(String xpath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getElementAttributByXPath(String xpath, String attribut) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getElementTextByXPath(String xpath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isTextInElement(String elementID, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isMatchInElement(String elementID, String regexp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setExpectedJavaScriptAlert(JavascriptAlert[] alerts) throws ExpectedJavascriptAlertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setExpectedJavaScriptConfirm(JavascriptConfirm[] confirms) throws ExpectedJavascriptConfirmException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setExpectedJavaScriptPrompt(JavascriptPrompt[] prompts) throws ExpectedJavascriptPromptException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IElement getElementByXPath(String xpath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IElement getElementByID(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<IElement> getElementsByXPath(String xpath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getServerResponseCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getHeader(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, String> getAllHeaders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setIgnoreFailingStatusCodes(boolean ignore) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getComments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTimeout(int milliseconds) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
