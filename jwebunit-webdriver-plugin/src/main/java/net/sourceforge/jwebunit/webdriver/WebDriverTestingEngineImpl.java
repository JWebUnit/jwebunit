/**
 * Copyright (c) 2011, JWebUnit team.
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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.sourceforge.jwebunit.api.HttpHeader;
import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.api.ITestingEngine;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptAlertException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptConfirmException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptPromptException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.javascript.JavascriptAlert;
import net.sourceforge.jwebunit.javascript.JavascriptConfirm;
import net.sourceforge.jwebunit.javascript.JavascriptPrompt;
import net.sourceforge.jwebunit.util.TestContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
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
    private ProxyServer proxyServer;
    private WebDriver driver;
    private TestContext testContext;
    private static final int TRY_COUNT = 50;
    private static final int DEFAULT_PORT = 8183;
    private static final Random RANDOM = new Random();
    private HttpResponse response;
    // The xpath string that identifie the current form
    // ie : @name='myForm'
    private String formIdent;
    private boolean throwExceptionOnScriptError = true;//TODO
    private boolean ignoreFailingStatusCodes = false;
    private Map<String, String> requestHeaders;
    /**
     * Is Javascript enabled.
     */
    private boolean jsEnabled = true;

    public WebDriverTestingEngineImpl() {
    }

    public void beginAt(URL aInitialURL, TestContext aTestContext) throws TestingEngineResponseException {
        this.setTestContext(aTestContext);
        // start the proxy        
        Proxy proxy = startBrowserMobProxy();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, proxy);
        capabilities.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, jsEnabled);
        capabilities.setBrowserName("htmlunit");
        capabilities.setVersion("firefox");

        driver = new HtmlUnitDriver(capabilities);
        
        //Reset form
        formIdent = null;

        // Deal with cookies
        for (javax.servlet.http.Cookie c : aTestContext.getCookies()) {
            //FIXME Hack for BrowserMob
            String domain = c.getDomain();
            if ("localhost".equals(domain)) {
                domain = null;
            }
            if ("".equals(domain)) {
                domain = null;
            }
            Date expiry = null;
            if (c.getMaxAge() != -1) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, c.getMaxAge());
                expiry = cal.getTime();
            }            
            driver.manage().addCookie(
                    new org.openqa.selenium.Cookie(c
                            .getName(), c.getValue(), domain, c.getPath() != null ? c                                          
                                .getPath() : "", expiry, c.getSecure()));
        }
        // Deal with custom request header
        this.requestHeaders = aTestContext.getRequestHeaders();

        gotoPage(aInitialURL);
    }
    
    private Proxy startBrowserMobProxy() {
        for (int i = 1; i <= TRY_COUNT; i++) {
            int port = getRandomPort();
            proxyServer = new ProxyServer(port);
            try {
                proxyServer.start();
                proxyServer.addResponseInterceptor(new HttpResponseInterceptor() {
                    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                        response.setEntity(new BufferedHttpEntity(response.getEntity()));
                        WebDriverTestingEngineImpl.this.response = response;
                    }
                });
                proxyServer.addRequestInterceptor(new HttpRequestInterceptor() {
                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        for (Map.Entry<String, String> requestHeader : requestHeaders.entrySet()) {
                            request.addHeader(requestHeader.getKey(), requestHeader.getValue());
                        }
                    }
                });
                return proxyServer.seleniumProxy();
            }
            catch (Exception e) {
                if (i<TRY_COUNT) {
                    logger.error("Error while starting BrowserMob proxy. Retry...", e);
                    continue;
                }
            }
        }
        throw new RuntimeException("Unable to start BrowserMob proxy after " + TRY_COUNT + " retries");
    }
    
    private static int getRandomPort() {
        synchronized (RANDOM) {
            return DEFAULT_PORT + RANDOM.nextInt(1000);
        }
    }

    public void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    public void closeBrowser() throws ExpectedJavascriptAlertException, ExpectedJavascriptConfirmException, ExpectedJavascriptPromptException {
        formIdent = null;
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        if (proxyServer != null) {
            try {
                proxyServer.stop();
                proxyServer = null;
            }
            catch (Exception e) {
                logger.error("Error while stopping proxy", e);
                throw new RuntimeException("Error while stopping proxy", e);
            }
        }
    }

    public void gotoPage(URL url) throws TestingEngineResponseException {
        formIdent = null;
        //Big hack for browsermob
        String urlStr = url.toString().replace("http://localhost", "http://127.0.0.1");
        driver.get(urlStr);
        throwFailingHttpStatusCodeExceptionIfNecessary(
            response.getStatusLine().getStatusCode(), urlStr);
    }
    
    /**
     * Copied from {@link WebClient#throwFailingHttpStatusCodeExceptionIfNecessary(WebResponse)}
     *
     * @param webResponse
     */
    private void throwFailingHttpStatusCodeExceptionIfNecessary(int statusCode, String url) {
        final boolean successful = (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES)
            || statusCode == HttpStatus.SC_USE_PROXY
            || statusCode == HttpStatus.SC_NOT_MODIFIED;
        if (!this.ignoreFailingStatusCodes && !successful) {
            throw new TestingEngineResponseException(statusCode,
                "unexpected status code [" + statusCode + "] at URL: [" + url + "]");
        }
    }

    public void setScriptingEnabled(boolean value) {
        // This variable is used to set Javascript before wc is instancied
        jsEnabled = value;
        if (driver != null && driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setJavascriptEnabled(value);
        }
    }

    public void setThrowExceptionOnScriptError(boolean value) {
        this.throwExceptionOnScriptError = true;
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
            if (title.equals(driver.getTitle())) {
                return;
            }
        }
        driver.switchTo().window(current);
        throw new RuntimeException("No window found with title [" + title + "]");
    }

    public void gotoWindow(int windowID) {
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[windowID]);
    }

    public void gotoRootWindow() {
        driver.switchTo().defaultContent();
    }

    public int getWindowCount() {
        return driver.getWindowHandles().size();
    }

    public void closeWindow() {
        formIdent = null;
        driver.close();//FIXME Issue 1466
    }

    public boolean hasFrame(String frameNameOrId) {
        List<WebElement> frameset = driver.findElements(By.tagName("frame"));
        for (WebElement frame : frameset) {
            if (frameNameOrId.equals(frame.getAttribute("name"))
                || frameNameOrId.equals(frame.getAttribute("id"))) {
                return true;
            }
        }
        return false;
    }

    public void gotoFrame(String frameNameOrId) {
        driver.switchTo().frame(frameNameOrId);
    }

    public void setWorkingForm(int index) {
        formIdent = "position()=" + (index + 1);
    }

    public void setWorkingForm(String nameOrId, int index) {
        if (nameOrId != null) {
            formIdent = "(@name=" + escapeQuotes(nameOrId) + " or @id=" + escapeQuotes(nameOrId) + ")][position()="
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
        return hasElementByXPath("//form[@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + "]");
    }

    public boolean hasFormParameterNamed(String paramName) {
        return getWebElementByXPath("//*[@name=" + escapeQuotes(paramName) + "]", false, true) != null;
    }

    private WebElement getWebElementByXPath(String xpathAfterForm, boolean searchOnlyInCurrentForm, boolean overrideWorkingForm) {
        //First try the current form
        if (formIdent != null) {
            try {
                return driver.findElement(By.xpath("//form[" + formIdent + "]" + xpathAfterForm));
            } catch (NoSuchElementException ex) {
                if (searchOnlyInCurrentForm) {
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
                if (overrideWorkingForm) {
                    setWorkingForm(index);
                }
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
        WebElement e = getTextField(paramName);
        return e.getAttribute("value");
    }

    public String getHiddenFieldValue(String paramName) {
        WebElement e = getWebElementByXPath("//input[@type='hidden' and @name=" + escapeQuotes(paramName) + "]", false, true);
        return e.getAttribute("value");
    }

    public void setTextField(String inputName, String text) {
        WebElement e = getTextField(inputName);
        e.clear();
        e.sendKeys(text);
    }
    
    /**
     * Look for any text field (input text, input password, textarea, file input).
     */
    private WebElement getTextField(String paramName) {
        WebElement e = getWebElementByXPath("//input[@type='text' and @name=" + escapeQuotes(paramName) + "]", false, true);
        if (e == null) {
            e = getWebElementByXPath("//textarea[@name=" + escapeQuotes(paramName) + "]", false, true);
        }
        if (e == null) {
            e = getWebElementByXPath("//input[@type='file' and @name=" + escapeQuotes(paramName) + "]", false, true);
        }
        if (e == null) {
            e = getWebElementByXPath("//input[@type='password' and @name=" + escapeQuotes(paramName) + "]", false, true);
        }
        return e;
    }

    public void setHiddenField(String inputName, String text) {
        WebElement e = getWebElementByXPath("//input[@type='hidden' and @name=" + escapeQuotes(inputName) + "]", false, true);
        ((JavascriptExecutor)driver).executeScript("arguments[0].value=" + escapeQuotes(text), e); 
    }

    public String[] getSelectOptionValues(String selectName) {
        return getSelectOptionValues(selectName, 0);
    }

    public String[] getSelectOptionValues(String selectName, int index) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        ArrayList<String> result = new ArrayList<String>();
        for (WebElement opt : select.getOptions()) {
            result.add(opt.getAttribute("value"));
        }
        return result.toArray(new String[result.size()]);
    }

    public String[] getSelectedOptions(String selectName) {
        return getSelectedOptions(selectName, 0);
    }

    private String[] getSelectedOptions(Select select) {
    	//FIXME http://code.google.com/p/selenium/issues/detail?id=2295
        String[] result = new String[select.getAllSelectedOptions().size()];
        int i = 0;
        for (WebElement opt : select.getAllSelectedOptions()) {
            result[i++] = opt.getAttribute("value");
        }
        return result;
    }

    public String[] getSelectedOptions(String selectName, int index) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        return getSelectedOptions(select);
    }

    private String getSelectOptionValueForLabel(Select select, String label) {
        for (WebElement opt : select.getOptions()) {
            if (opt.getText().equals(label)) {
                return opt.getAttribute("value");
            }
        }
        throw new RuntimeException("Unable to find option " + label);
    }

    private String getSelectOptionLabelForValue(Select select, String value) {
        for (WebElement opt : select.getOptions()) {
            if (opt.getAttribute("value").equals(value)) {
                return opt.getText();
            }
        }
        throw new RuntimeException("Unable to find option " + value);
    }

    public String getSelectOptionLabelForValue(String selectName, String optionValue) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "]", true, true));
        return getSelectOptionLabelForValue(select, optionValue);
    }

    public String getSelectOptionLabelForValue(String selectName, int index, String optionValue) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        return getSelectOptionLabelForValue(select, optionValue);
    }

    public String getSelectOptionValueForLabel(String selectName, String optionLabel) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "]", true, true));
        return getSelectOptionValueForLabel(select, optionLabel);
    }

    public String getSelectOptionValueForLabel(String selectName, int index, String optionLabel) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        return getSelectOptionValueForLabel(select, optionLabel);
    }

    public void selectOptions(String selectName, String[] optionValues) {
        selectOptions(selectName, 0, optionValues);
    }

    public void selectOptions(String selectName, int index, String[] optionValues) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        if (!select.isMultiple() && optionValues.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        for (String option : optionValues) {
            boolean found = false;
            for (WebElement opt : select.getOptions()) {
                if (opt.getAttribute("value").equals(option)) {
                    select.selectByValue(option);
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

    public void unselectOptions(String selectName, String[] optionValues) {
        unselectOptions(selectName, 0, optionValues);
    }

    public void unselectOptions(String selectName, int index, String[] optionValues) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        if (!select.isMultiple() && optionValues.length > 1)
            throw new RuntimeException("Multiselect not enabled");
        for (String option : optionValues) {
            boolean found = false;
            for (WebElement opt : select.getOptions()) {
                if (opt.getAttribute("value").equals(option)) {
                    select.deselectByValue(option);
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

    public boolean hasSelectOption(String selectName, String optionLabel) {
        return hasSelectOption(selectName, 0, optionLabel);
    }

    public boolean hasSelectOptionValue(String selectName, String optionValue) {
        return hasSelectOptionValue(selectName, 0, optionValue);
    }

    public boolean hasSelectOption(String selectName, int index, String optionLabel) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        for (WebElement opt : select.getOptions()) {
            if (opt.getText().equals(optionLabel)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSelectOptionValue(String selectName, int index, String optionValue) {
        Select select = new Select(getWebElementByXPath("//select[@name=" + escapeQuotes(selectName) + "][" + (index + 1) + "]", true, true));
        for (WebElement opt : select.getOptions()) {
            if (opt.getAttribute("value").equals(optionValue)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCheckboxSelected(String checkBoxName) {
        WebElement e = getWebElementByXPath("//input[@type='checkbox' and @name=" + escapeQuotes(checkBoxName) + "]", true, true);
        return e.isSelected();
    }

    public boolean isCheckboxSelected(String checkBoxName, String checkBoxValue) {
        WebElement e = getWebElementByXPath("//input[@type='checkbox' and @name=" + escapeQuotes(checkBoxName) + " and @value=" + escapeQuotes(checkBoxValue) + "]", true, true);
        return e.isSelected();
    }

    public void checkCheckbox(String checkBoxName) {
        WebElement e = getWebElementByXPath("//input[@type='checkbox' and @name=" + escapeQuotes(checkBoxName) + "]", true, true);
        if (!e.isSelected()) {
            e.click();
        }
    }

    public void checkCheckbox(String checkBoxName, String checkBoxValue) {
        WebElement e = getWebElementByXPath("//input[@type='checkbox' and @name=" + escapeQuotes(checkBoxName) + " and @value=" + escapeQuotes(checkBoxValue) + "]", true, true);
        if (!e.isSelected()) {
            e.click();
        }
    }

    public void uncheckCheckbox(String checkBoxName) {
        WebElement e = getWebElementByXPath("//input[@type='checkbox' and @name=" + escapeQuotes(checkBoxName) + "]", true, true);
        if (e.isSelected()) {
            e.click();
        }
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        WebElement e = getWebElementByXPath("//input[@type='checkbox' and @name=" + escapeQuotes(checkBoxName) + " and @value=" + escapeQuotes(value) + "]", true, true);
        if (e.isSelected()) {
            e.click();
        }
    }

    public void clickRadioOption(String radioGroup, String radioOptionValue) {
        WebElement e = getWebElementByXPath("//input[@type='radio' and @name=" + escapeQuotes(radioGroup) + " and @value=" + escapeQuotes(radioOptionValue) + "]", false, true);
        e.click();
    }

    public boolean hasRadioOption(String radioGroup, String radioOptionValue) {
        WebElement e = getWebElementByXPath("//input[@type='radio' and @name=" + escapeQuotes(radioGroup) + " and @value=" + escapeQuotes(radioOptionValue) + "]", false, true);
        return e != null;
    }

    public String getSelectedRadio(String radioGroup) {
        List<WebElement> radios = getWebElementsByXPath("/input[@type='radio' and @name=" + escapeQuotes(radioGroup) + "]");
        for (WebElement r : radios) {
            if (r.isSelected()) {
                return r.getAttribute("value");
            }
        }
        return null;
    }

    public boolean hasSubmitButton() {
        return (getWebElementByXPath("//input[@type='submit' or @type='image']", true, true) != null) || (getWebElementByXPath("//button[@type='submit']", false, true) != null);
    }

    public boolean hasSubmitButton(String nameOrID) {
        return (getWebElementByXPath("//input[(@type='submit' or @type='image') and (@name=" + escapeQuotes(nameOrID) + " or @id=" + nameOrID + ")]", true, true) != null)
                || (getWebElementByXPath("//button[@type='submit' and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ")]", true, true) != null);
    }

    public boolean hasSubmitButton(String nameOrID, String value) {
        return (getWebElementByXPath("//input[(@type='submit' or @type='image') and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ") and @value=" + escapeQuotes(value) + "]", true, true) != null)
                || (getWebElementByXPath("//button[@type='submit' and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ") and @value=" + escapeQuotes(value) + "]", true, true) != null);
    }

    public void submit() {
        WebElement e = getWebElementByXPath("//input[@type='submit' or @type='image']", true, true);
        if (e == null) {
            e = getWebElementByXPath("//button[@type='submit']", true, true);
        }
        e.submit();
        throwFailingHttpStatusCodeExceptionIfNecessary(
            response.getStatusLine().getStatusCode(), driver.getCurrentUrl());
    }

    public void submit(String nameOrID) {
        WebElement e = getWebElementByXPath("//input[(@type='submit' or @type='image') and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ")]", true, true);
        if (e == null) {
            e = getWebElementByXPath("//button[@type='submit' and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ")]", true, true);
        }
        e.submit();
        throwFailingHttpStatusCodeExceptionIfNecessary(
            response.getStatusLine().getStatusCode(), driver.getCurrentUrl());
    }

    public void submit(String buttonName, String buttonValue) {
        WebElement e = getWebElementByXPath("//input[(@type='submit' or @type='image') and (@name=" + escapeQuotes(buttonName) + " or @id=" + escapeQuotes(buttonName) + ") and @value=" + escapeQuotes(buttonValue) + "]", true, true);
        if (e == null) {
            e = getWebElementByXPath("//button[@type='submit' and (@name=" + escapeQuotes(buttonName) + " or @id=" + escapeQuotes(buttonName) + ") and @value=" + escapeQuotes(buttonValue) + "]", true, true);
        }
        e.submit();
        throwFailingHttpStatusCodeExceptionIfNecessary(
            response.getStatusLine().getStatusCode(), driver.getCurrentUrl());
    }

    public boolean hasResetButton() {
        return getWebElementByXPath("//input[@type='reset']", false, true) != null;
    }

    public boolean hasResetButton(String nameOrID) {
        return getWebElementByXPath("//input[@type='reset' and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ")]", true, true) != null;
    }

    public void reset() {
        getWebElementByXPath("//input[@type='reset']", true, true).click();;
    }

    private WebElement getButton(String nameOrID) {
        WebElement e = getWebElementByXPath("//input[(@type='submit' or @type='image' or @type='reset' or @type='button') and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ")]", false, true);
        if (e == null) {
            e = getWebElementByXPath("//button[@type='submit' and (@name=" + escapeQuotes(nameOrID) + " or @id=" + escapeQuotes(nameOrID) + ")]", false, true);
        }
        return e;
    }

    private WebElement getButtonWithText(String text) {
        WebElement e = getWebElementByXPath("//input[(@type='submit' or @type='reset' or @type='button') and contains(@value," + escapeQuotes(text) + ")]", false, true);
        if (e == null) {
            e = getWebElementByXPath("//button[contains(.," + escapeQuotes(text) + ")]", false, true);
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
        try {
            return driver.findElement(By.xpath("//body")).getText();
        } catch (Exception e) {
            //Maybe this is not HTML
            return driver.getPageSource();
        }
    }

    public String getPageSource() {
        String encoding = "ISO-8859-1";
        if (response.getEntity().getContentEncoding() != null) {
            encoding = response.getEntity().getContentEncoding().getValue();
        }
        
        try {
            return IOUtils.toString(response.getEntity().getContent(), encoding);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getServerResponse() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getInputStream() {
        try {
            return response.getEntity().getContent();
        }
        catch (Exception e) {
            throw new TestingEngineResponseException(e);
        }
    }

    public InputStream getInputStream(URL url) throws TestingEngineResponseException {
        try {
            return url.openStream();//TODO support proxy
        }
        catch (IOException e) {
            throw new TestingEngineResponseException(e);
        }
    }

    public boolean hasTable(String tableSummaryNameOrId) {
        return getHtmlTable(tableSummaryNameOrId) != null;
    }

    public Table getTable(String tableSummaryNameOrId) {
        WebElement table = getHtmlTable(tableSummaryNameOrId);
        Table result = new Table();
        List<WebElement> trs = table.findElements(By.xpath("tr | tbody/tr"));
        for (int i = 0; i < trs.size(); i++) {
            Row newRow = new Row();
            WebElement tr = trs.get(i);
            List<WebElement> tds = tr.findElements(By.xpath("td | th"));
            for (WebElement td : tds) {
                int colspan;
                try {
                    colspan = Integer.valueOf(td.getAttribute("colspan"));
                }
                catch (NumberFormatException e) {
                    colspan = 1;
                }
                int rowspan;
                try {
                    rowspan = Integer.valueOf(td.getAttribute("rowspan"));
                }
                catch (NumberFormatException e) {
                    rowspan = 1;
                }
                newRow.appendCell(new Cell(td.getText(), 
                    colspan, 
                    rowspan));
            }
            result.appendRow(newRow);
        }
        return result;
    }
    
    /**
     * Return the Webdriver WebElement object representing a specified table in the current response. Null is returned if a
     * parsing exception occurs looking for the table or no table with the id or summary could be found.
     * 
     * @param tableSummaryOrId summary or id of the table to return.
     */
    private WebElement getHtmlTable(String tableSummaryOrId) {
        try {
            return driver.findElement(By.xpath("(//table[@id="
                + escapeQuotes(tableSummaryOrId) + " or @summary=" + escapeQuotes(tableSummaryOrId) + "])"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private WebElement getLinkWithImage(String filename, int index) {
        try {
            return driver.findElement(By.xpath("(//a[img[contains(@src,"
                + escapeQuotes(filename) + ")]])[" + (index + 1) + "]"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private WebElement getLinkWithText(String linkText, int index) {
        List<WebElement> lnks = driver.findElements(By.xpath("//a"));
        int count = 0;
        for (WebElement lnk : lnks) {
            if ((lnk.getText().indexOf(linkText) >= 0) && (count++ == index)) {
                return lnk;
            }
        }
        return null;
    }

    private WebElement getLinkWithExactText(String linkText, int index) {
        List<WebElement> lnks = driver.findElements(By.xpath("//a"));
        int count = 0;
        for (WebElement lnk : lnks) {
            if (lnk.getText().equals(linkText) && (count++ == index)) {
                return lnk;
            }
        }
        return null;
    }

    public boolean hasLinkWithText(String linkText, int index) {
        return getLinkWithText(linkText, index) != null;
    }

    public boolean hasLinkWithExactText(String linkText, int index) {
        return getLinkWithExactText(linkText, index) != null;
    }

    public boolean hasLinkWithImage(String imageFileName, int index) {
        return getLinkWithImage(imageFileName, index) != null;
    }

    public boolean hasLink(String anId) {
        try {
            driver.findElement(By.xpath("//a[@id='" + anId + "']"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickLinkWithText(String linkText, int index) {
        WebElement link = getLinkWithText(linkText, index);
        if (link == null) {
            throw new RuntimeException("No Link found for \"" + linkText
                    + "\" with index " + index);
        }
        link.click();
    }

    public void clickLinkWithExactText(String linkText, int index) {
        WebElement link = getLinkWithExactText(linkText, index);
        if (link == null) {
            throw new RuntimeException("No Link found for \"" + linkText
                    + "\" with index " + index);
        }
        link.click();
    }

    public void clickLink(String anID) {
        driver.findElement(By.xpath("//a[@id='" + anID + "']")).click();
    }

    public void clickLinkWithImage(String imageFileName, int index) {
        WebElement link = getLinkWithImage(imageFileName, index);
        if (link == null) {
            throw new RuntimeException("No Link found with filename \""
                    + imageFileName + "\" and index " + index);
        }
        link.click();
    }
    
    public boolean hasElement(String anID) {
        try {
            driver.findElement(By.id(anID));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
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
        driver.findElement(By.xpath(xpath)).click();
    }

    public String getElementAttributByXPath(String xpath, String attribut) {
        return driver.findElement(By.xpath(xpath)).getAttribute(attribut);
    }

    public String getElementTextByXPath(String xpath) {
        return driver.findElement(By.xpath(xpath)).getText();
    }

    public boolean isTextInElement(String elementID, String text) {
        return isTextInElement(driver.findElement(By.id(elementID)), text);
    }

    /**
     * Return true if a given string is contained within the specified element.
     * 
     * @param element element to inspect.
     * @param text text to check for.
     */
    private boolean isTextInElement(WebElement element, String text) {
        return element.getText().indexOf(text) >= 0;
    }
    
    public boolean isMatchInElement(String elementID, String regexp) {
        return isMatchInElement(driver.findElement(By.id(elementID)), regexp);
    }

    /**
     * Return true if a given regexp is contained within the specified element.
     * 
     * @param element element to inspect.
     * @param regexp regexp to match.
     */
    private boolean isMatchInElement(WebElement element, String regexp) {
        RE re = getRE(regexp);
        return re.match(element.getText());
    }

    private RE getRE(String regexp) {
        try {
            return new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            throw new RuntimeException(e);
        }
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
        try {
            return new WebDriverElementImpl(driver.findElement(By.xpath(xpath)));
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public IElement getElementByID(String id) {
        try {
            return new WebDriverElementImpl(driver.findElement(By.id(id)));
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<IElement> getElementsByXPath(String xpath) {
        List<IElement> result = new ArrayList<IElement>();
        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        for (WebElement child : elements) {
            result.add(new WebDriverElementImpl(child));
        }
        return result;
    }

    public int getServerResponseCode() {
        return response.getStatusLine().getStatusCode();
    }

    public String getHeader(String name) {
        return response.getHeaders(name).length>0?response.getHeaders(name)[0].getValue():null;
    }

    public Map<String, String> getAllHeaders() {
        Map<String, String> map = new java.util.HashMap<String, String>();
        for (Header header : response.getAllHeaders()) {
            map.put(header.getName(), header.getValue());
        }
        return map;
    }

    public void setIgnoreFailingStatusCodes(boolean ignore) {
        this.ignoreFailingStatusCodes = ignore;
    }

    public List<String> getComments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTimeout(int milliseconds) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<HttpHeader> getResponseHeaders() {
        List<HttpHeader> result = new LinkedList<HttpHeader>();
        for (Header header : response.getAllHeaders()) {
            result.add(new HttpHeader(header.getName(), header.getValue()));
        }
        return result;
    }
    
    /**
     * Copied from {@link Select}
     * @param toEscape
     * @return
     */
    protected String escapeQuotes(String toEscape) {
        // Convert strings with both quotes and ticks into: foo'"bar -> concat("foo'", '"', "bar")
        if (toEscape.indexOf("\"") > -1 && toEscape.indexOf("'") > -1) {
          boolean quoteIsLast = false;
          if (toEscape.indexOf("\"") == toEscape.length() -1) {
            quoteIsLast = true;
          }
          String[] substrings = toEscape.split("\"");

          StringBuilder quoted = new StringBuilder("concat(");
          for (int i = 0; i < substrings.length; i++) {
            quoted.append("\"").append(substrings[i]).append("\"");
            quoted.append(((i == substrings.length -1) ? (quoteIsLast ? ", '\"')" : ")") : ", '\"', "));
          }
          return quoted.toString();
        }

        // Escape string with just a quote into being single quoted: f"oo -> 'f"oo'
        if (toEscape.indexOf("\"") > -1) {
          return String.format("'%s'", toEscape);
        }

        // Otherwise return the quoted string
        return String.format("\"%s\"", toEscape);
      }

}
