/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.junit;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.sourceforge.jwebunit.api.ITestingEngine;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptAlertException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptConfirmException;
import net.sourceforge.jwebunit.exception.ExpectedJavascriptPromptException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.javascript.JavascriptAlert;
import net.sourceforge.jwebunit.javascript.JavascriptConfirm;
import net.sourceforge.jwebunit.javascript.JavascriptPrompt;
import net.sourceforge.jwebunit.util.TestContext;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Provides a high-level API for basic web application navigation and validation by providing
 * JUnit assertions. It supports use of a property file for web resources (a la Struts), though a resource file for the
 * app is not required.
 * 
 * @author Julien Henry
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class WebTester {
    private ITestingEngine testingEngine = null;

    private TestContext testContext = null;

    /**
     * This is the testing engine key that the webtester will use to find the correct testing engine from the registry.
     */
    private String testingEngineKey = null;

    /**
     * Provides access to the testing engine for subclasses - in case functionality not yet wrappered required by test.
     * 
     * If the testing engine is not explicitly set the JWebUnit framework will default to using the orignal testing engine,
     * which is, htmlunit.
     * 
     * @return IJWebUnitDialog instance used to wrapper htmlunit conversation.
     * @deprecated You should not use plugin specific fonctionality. Please ask for a new core feature instead.
     */
    public ITestingEngine getDialog() {
        return getTestingEngine();
    }

    /**
     * Protected version of deprecated getDialog(). Not deprecated for internal use.
     * 
     * @return IJWebUnitDialog instance.
     */
    protected ITestingEngine getTestingEngine() {
        if (testingEngine == null) {
            // defaulting to the HtmlUnitDialog implementation.
            testingEngine = initializeDialog();
        }
        return testingEngine;
    }

    /**
     * Initializes the IJWebUnitDialog when the testing engine is null. This will construct a new instance of the testing engine based
     * on the specified testing engine key.
     */
    protected ITestingEngine initializeDialog() {
        ITestingEngine theIJWebUnitDialog = null;
        String theTestingEngineKey = getTestingEngineKey();
        Class theClass;
        try {
            theClass = TestingEngineRegistry
                    .getTestingEngineClass(theTestingEngineKey);
        } catch (ClassNotFoundException e1) {
            throw new RuntimeException(e1);
        }
        try {
            theIJWebUnitDialog = (ITestingEngine) theClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Can't Instantiate Testing Engine with class [" + theClass
                            + "] with key [" + theTestingEngineKey + "].", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("IllegalAccessException using class ["
                    + theClass + "] with key [" + theTestingEngineKey + "].", e);
        }

        return theIJWebUnitDialog;
    }

    /**
     * Close the current conversation.
     */
    public void closeBrowser() {
        try {
            getTestingEngine().closeBrowser();
        } catch (ExpectedJavascriptAlertException e) {
            Assert.fail("You previously tell that alert with message ["
                    + e.getAlertMessage()
                    + "] was expected, but nothing appeared.");
        } catch (ExpectedJavascriptConfirmException e) {
            Assert.fail("You previously tell that confirm with message ["
                    + e.getConfirmMessage()
                    + "] was expected, but nothing appeared.");
        } catch (ExpectedJavascriptPromptException e) {
            Assert.fail("You previously tell that prompt with message ["
                    + e.getPromptMessage()
                    + "] was expected, but nothing appeared.");
        }
    }

    /**
     * Close the current window.
     */
    public void closeWindow() {
        getTestingEngine().closeWindow();
    }

    /**
     * Set the testing engine.
     * 
     * @param aIJWebUnitDialog Testing engine.
     */
    public void setDialog(ITestingEngine aIJWebUnitDialog) {
        testingEngine = aIJWebUnitDialog;
    }

    /**
     * Provide access to test testContext.
     * 
     * @return TestContext
     */
    public TestContext getTestContext() {
        if (testContext == null) {
            // defaulting to the original implementation.
            testContext = new TestContext();
        }
        return testContext;
    }

    /**
     * Allows setting an external test testContext class that might be extended from TestContext. Example:
     * setTestContext(new CompanyATestContext());
     * 
     * CompanyATestContext extends TestContext.
     * 
     * @param aTestContext
     */
    public void setTestContext(TestContext aTestContext) {
        testContext = aTestContext;
    }

    /**
     * Begin conversation at a URL absolute or relative to base URL. Use
     * {@link TestContext#setBaseUrl(String) getTestContext().setBaseUrl(String)} to define base URL. Absolute URL
     * should start with "http://", "https://" or "www.".
     * 
     * @param url absolute or relative URL (relative to base URL).
     * @throws TestingEngineResponseException If something bad happend (404)
     */
    public void beginAt(String aRelativeURL) throws TestingEngineResponseException {
        try {
            getTestingEngine().beginAt(createUrl(aRelativeURL, getTestContext().getBaseUrl()), testContext);
        } catch (MalformedURLException e) {
            Assert.fail(e.getLocalizedMessage());
        }

    }

    private URL createUrl(String url, URL baseURL) throws MalformedURLException {
        if (url.startsWith("http://") || url.startsWith("https://")
                || url.startsWith("file://")) {
            return new URL(url);
        } else if (url.startsWith("www.")) {
            return new URL("http://" + url);
        } else {
            url = url.startsWith("/") ? url.substring(1) : url;
            return new URL(baseURL, url);
        }
    }

    /**
     * Return the value of a web resource based on its key. This translates to a property file lookup with the locale
     * based on the current TestContext.
     * 
     * @param key name of the web resource.
     * @return value of the web resource, encoded according to TestContext.
     */
    public String getMessage(String key) {
        String message = "";
        Locale locale = testContext.getLocale();
        try {
            message = ResourceBundle.getBundle(
                    getTestContext().getResourceBundleName(), locale)
                    .getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No message found for key [" + key
                    + "].", e);
        }
        return message;
    }
    
    /**
     * Return the value of a web resource based on its key, using MessageFormat
     * to perform parametric substitution with formatting.
     * 
     * @see MessageFormat
     * @param key
     *            name of the web resource.
     * @param args
     *            array of arguments to be formatted into message
     * @return value of the web resource after formatting
     */
    public String getMessage(String key, Object[] args) {
        String message = getMessage(key);

        // TODO: Struts escapes single quotes... maybe this should too

        MessageFormat format = new MessageFormat(message, getTestContext().getLocale());

        return format.format(args);
    }

    // Assertions

    /**
     * Assert title of current html page in conversation matches an expected
     * value.
     * 
     * @param title
     *            expected title value
     */
    public void assertTitleEquals(String title) {
        Assert.assertEquals(title, getTestingEngine().getPageTitle());
    }

    /**
     * Assert title of current html page in conversation matches an expected regexp.
     * 
     * @param regexp expected title regexp
     */
    public void assertTitleMatch(String regexp) {
        RE re = null;
        try {
            re = new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue("Unable to match [" + regexp + "] in title", re
                .match(getTestingEngine().getPageTitle()));
    }

    /**
     * Assert title of current html page matches the value of a specified web
     * resource.
     * 
     * @param titleKey
     *            web resource key for title
     */
    public void assertTitleEqualsKey(String titleKey) {
        Assert.assertEquals(getMessage(titleKey), getTestingEngine().getPageTitle());
    }
    
    /**
     * Assert title of current page matches formatted message resource
     * 
     * @param titleKey
     * @param args
     */
    public void assertTitleEqualsKey(String titleKey, Object[] args) {
        Assert.assertEquals(getMessage(titleKey, args), getTestingEngine().getPageTitle());
    }

    /**
     * Assert that a web resource's value is present.
     * 
     * @param key
     *            web resource name
     */
    public void assertKeyPresent(String key) {
        assertTextPresent(getMessage(key));
    }
    
    /**
     * Assert that a web resource's value (with formatting) is present
     * 
     * @param key
     * @param args
     */
    public void assertKeyPresent(String key, Object[] args) {
        assertTextPresent(getMessage(key, args));
    }

    /**
     * Assert that supplied text is present.
     * 
     * @param text
     */
    public void assertTextPresent(String text) {
        if (!(getTestingEngine().getPageText().indexOf(text) >= 0))
            Assert.fail("Expected text not found in current page: [" + text
                    + "]\n Page content was: ["
                    + getTestingEngine().getPageText() + "]");
    }

    /**
     * Assert that supplied regexp is matched.
     * 
     * @param regexp
     */
    public void assertMatch(String regexp) {
        RE re = getRE(regexp);
        if (!re.match(getTestingEngine().getPageText()))
            Assert.fail("Expected rexexp not matched in response: [" + regexp
                    + "]");
    }

    /**
     * Assert that a web resource's value is not present.
     * 
     * @param key web resource name
     */
    public void assertKeyNotPresent(String key) {
        assertTextNotPresent(getMessage(key));
    }
    
    /**
     * Assert that a web resource's formatted value is not present.
     * 
     * @param key
     *            web resource name
     */
    public void assertKeyNotPresent(String key, Object[] args) {
        assertTextNotPresent(getMessage(key, args));
    }

    /**
     * Assert that supplied text is not present.
     * 
     * @param text
     */
    public void assertTextNotPresent(String text) {
        if (getTestingEngine().getPageText().indexOf(text) >= 0)
            Assert.fail("Text found in response when not expected: [" + text
                    + "]");
    }

    /**
     * Assert that supplied regexp is not present.
     * 
     * @param regexp
     */
    public void assertNoMatch(String regexp) {
        RE re = getRE(regexp);
        if (re.match(getTestingEngine().getPageText()))
            Assert.fail("Regexp matched in response when not expected: ["
                    + regexp + "]");
    }

    /**
     * 
     * @param tableSummaryNameOrId
     * @return Object that represent a html table in a way independent from plugin.
     */
    public Table getTable(String tableSummaryNameOrId) {
        return getTestingEngine().getTable(tableSummaryNameOrId);
    }

    /**
     * Assert that a table with a given summary or id value is present.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     */
    public void assertTablePresent(String tableSummaryNameOrId) {
        if (!getTestingEngine().hasTable(tableSummaryNameOrId))
            Assert.fail("Unable to locate table \"" + tableSummaryNameOrId
                    + "\"");
    }

    /**
     * Assert that a table with a given summary or id value is not present.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     */
    public void assertTableNotPresent(String tableSummaryNameOrId) {
        if (getTestingEngine().hasTable(tableSummaryNameOrId))
            Assert.fail("Located table \"" + tableSummaryNameOrId + "\"");
    }

    /**
     * Assert that the value of a given web resource is present in a specific table.
     * 
     * @param tableSummaryOrId summary or id attribute value of table
     * @param key web resource name
     */
    public void assertKeyInTable(String tableSummaryOrId, String key) {
        assertTextInTable(tableSummaryOrId, getMessage(key));
    }
    
    /**
     * Assert that the value of a given web resource is present in a specific
     * table.
     * 
     * @param tableSummaryOrId
     *            summary or id attribute value of table
     * @param key
     *            web resource name
     */
    public void assertKeyInTable(String tableSummaryOrId, String key, Object[] args) {
        assertTextInTable(tableSummaryOrId, getMessage(key, args));
    }

    /**
     * Assert that supplied text is present in a specific table.
     * 
     * @param tableSummaryNameOrId
     *            summary, name or id attribute value of table
     * @param text
     */
    public void assertTextInTable(String tableSummaryNameOrId, String text) {
        assertTablePresent(tableSummaryNameOrId);
        Assert.assertTrue("Could not find: [" + text + "]" + "in table ["
                + tableSummaryNameOrId + "]", getTestingEngine().getTable(
                tableSummaryNameOrId).hasText(text));
    }

    /**
     * Assert that supplied regexp is matched in a specific table.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param regexp
     */
    public void assertMatchInTable(String tableSummaryNameOrId, String regexp) {
        assertTablePresent(tableSummaryNameOrId);
        Assert.assertTrue("Could not match: [" + regexp + "]" + "in table ["
                + tableSummaryNameOrId + "]", getTestingEngine().getTable(
                tableSummaryNameOrId).hasMatch(regexp));
    }

    /**
     * Assert that the values of a set of web resources are all present in a specific table.
     * 
     * @param tableSummaryOrId summary, name or id attribute value of table
     * @param keys Array of web resource names.
     */
    public void assertKeysInTable(String tableSummaryOrId, String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            assertKeyInTable(tableSummaryOrId, keys[i]);
        }
    }
    
    /**
     * Assert that the values of a set of web resources are all present in a
     * specific table.
     * 
     * @param tableSummaryOrId
     *            summary or id attribute value of table
     * @param keys
     *            Array of web resource names.
     */
    public void assertKeysInTable(String tableSummaryOrId, String[] keys, Object[][] args) {
        for (int i = 0; i < keys.length; i++) {
            assertKeyInTable(tableSummaryOrId, keys[i], args[i]);
        }
    }

    /**
     * Assert that a set of text values are all present in a specific table.
     * 
     * @param tableSummaryOrId
     *            summary, name or id attribute value of table
     * @param text
     *            Array of expected text values.
     */
    public void assertTextInTable(String tableSummaryOrId, String[] text) {
        for (int i = 0; i < text.length; i++) {
            assertTextInTable(tableSummaryOrId, text[i]);
        }
    }

    /**
     * Assert that a set of regexp values are all matched in a specific table.
     * 
     * @param tableSummaryOrId summary, name or id attribute value of table
     * @param text Array of expected regexps to match.
     */
    public void assertMatchInTable(String tableSummaryOrId, String[] regexp) {
        for (int i = 0; i < regexp.length; i++) {
            assertMatchInTable(tableSummaryOrId, regexp[i]);
        }
    }

    /**
     * Assert that the value of a given web resource is not present in a specific table.
     * 
     * @param tableSummaryOrId summary, name or id attribute value of table
     * @param key web resource name
     */
    public void assertKeyNotInTable(String tableSummaryOrId, String key) {
        assertTextNotInTable(tableSummaryOrId, getMessage(key));
    }

    /**
     * Assert that supplied text is not present in a specific table.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param text
     */
    public void assertTextNotInTable(String tableSummaryNameOrId, String text) {
        assertTablePresent(tableSummaryNameOrId);
        Assert.assertTrue("Found text: [" + text + "] in table ["
                + tableSummaryNameOrId + "]", !getTestingEngine().getTable(
                tableSummaryNameOrId).hasText(text));
    }

    /**
     * Assert that none of a set of text values are present in a specific table.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param text Array of text values
     */
    public void assertTextNotInTable(String tableSummaryNameOrId, String[] text) {
        for (int i = 0; i < text.length; i++) {
            assertTextNotInTable(tableSummaryNameOrId, text[i]);
        }
    }

    /**
     * Assert that supplied regexp is not present in a specific table.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param text
     */
    public void assertNoMatchInTable(String tableSummaryNameOrId, String regexp) {
        assertTablePresent(tableSummaryNameOrId);
        Assert.assertTrue("Found regexp: [" + regexp + "] in table ["
                + tableSummaryNameOrId + "]", !getTestingEngine().getTable(
                tableSummaryNameOrId).hasMatch(regexp));
    }

    /**
     * Assert that none of a set of regexp values are present in a specific table.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param text Array of text values
     */
    public void assertNoMatchInTable(String tableSummaryNameOrId,
            String[] regexp) {
        for (int i = 0; i < regexp.length; i++) {
            assertNoMatchInTable(tableSummaryNameOrId, regexp[i]);
        }
    }

    /**
     * Assert that a specific table matches an ExpectedTable.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param expectedTable represents expected values (colspan supported).
     */
    public void assertTableEquals(String tableSummaryNameOrId,
            Table expectedTable) {
        getTestingEngine().getTable(tableSummaryNameOrId).assertEquals(
                expectedTable);
    }

    /**
     * Assert that a specific table matches a matrix of supplied text values.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param expectedCellValues double dimensional array of expected values
     */
    public void assertTableEquals(String tableSummaryNameOrId,
            String[][] expectedCellValues) {
        getTestingEngine().getTable(tableSummaryNameOrId).assertEquals(
                new Table(expectedCellValues));
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param startRow index of start row for comparison
     * @param expectedTable represents expected values (colspan and rowspan supported).
     */
    public void assertTableRowsEqual(String tableSummaryNameOrId, int startRow,
            Table expectedTable) {
        getTestingEngine().getTable(tableSummaryNameOrId).assertSubTableEquals(
                startRow, expectedTable);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param startRow index of start row for comparison
     * @param expectedTable represents expected values (colspan and rowspan supported).
     */
    public void assertTableRowsEqual(String tableSummaryNameOrId, int startRow,
            String[][] expectedTable) {
        getTestingEngine().getTable(tableSummaryNameOrId).assertSubTableEquals(
                startRow, new Table(expectedTable));
    }

    /**
     * Assert that the number of rows for a specific table equals expected value.
     * 
     * @param tableSummaryNameOrId summary, name or id attribute value of table
     * @param expectedRowCount expected row count.
     */
    public void assertTableRowCountEquals(String tableSummaryNameOrId,
            int expectedRowCount) {
        assertTablePresent(tableSummaryNameOrId);
        int actualRowCount = getTestingEngine().getTable(tableSummaryNameOrId)
                .getRowCount();
        Assert.assertTrue("Expected row count was " + expectedRowCount
                + " but actual row count is " + actualRowCount,
                actualRowCount == expectedRowCount);
    }

    /**
     * Assert that a specific table matches an ExpectedTable.
     * 
     * @param tableSummaryOrId summary or id attribute value of table
     * @param expectedTable represents expected regexps (colspan supported).
     */
    public void assertTableMatch(String tableSummaryOrId, Table expectedTable) {
        getTestingEngine().getTable(tableSummaryOrId)
                .assertMatch(expectedTable);
    }

    /**
     * Assert that a specific table matches a matrix of supplied regexps.
     * 
     * @param tableSummaryOrId summary or id attribute value of table
     * @param expectedCellValues double dimensional array of expected regexps
     */
    public void assertTableMatch(String tableSummaryOrId,
            String[][] expectedCellValues) {
        getTestingEngine().getTable(tableSummaryOrId).assertMatch(
                new Table(expectedCellValues));
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied regexps.
     * 
     * @param tableSummaryOrId summary or id attribute value of table
     * @param startRow index of start row for comparison
     * @param expectedTable represents expected regexps (colspan and rowspan supported).
     */
    public void assertTableRowsMatch(String tableSummaryOrId, int startRow,
            Table expectedTable) {
        getTestingEngine().getTable(tableSummaryOrId).assertSubTableMatch(
                startRow, expectedTable);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied regexps.
     * 
     * @param tableSummaryOrId summary or id attribute value of table
     * @param startRow index of start row for comparison
     * @param expectedTable represents expected regexps (colspan and rowspan not supported).
     */
    public void assertTableRowsMatch(String tableSummaryOrId, int startRow,
            String[][] expectedTable) {
        getTestingEngine().getTable(tableSummaryOrId).assertSubTableMatch(
                startRow, new Table(expectedTable));
    }

    /**
     * Assert that a form input element with a given name is present.
     * 
     * @param formElementName
     */
    public void assertFormElementPresent(String formElementName) {
        assertFormPresent();
        Assert.assertTrue("Did not find form element with name ["
                + formElementName + "].", getTestingEngine()
                .hasFormParameterNamed(formElementName));
    }

    /**
     * Assert that a form input element with a given name is not present.
     * 
     * @param formElementName
     */
    public void assertFormElementNotPresent(String formElementName) {
        assertFormPresent();
        try {
            Assert.assertTrue("Found form element with name ["
                    + formElementName + "] when not expected.", !getTestingEngine()
                    .hasFormParameterNamed(formElementName));
        } catch (UnableToSetFormException e) {
            // assertFormControlNotPresent
        }
    }

    /**
     * Assert that a form checkbox with a given name is present.
     * 
     * @param checkboxName checkbox name.
     */
    public void assertCheckboxPresent(String checkboxName) {
        assertFormPresent();
        Assert.assertTrue("Did not find form checkbox with name ["
                + checkboxName + "].", getTestingEngine().hasElementByXPath(
                "//input[@type='checkbox' and @name='" + checkboxName + "']"));
    }

    /**
     * Assert that a given checkbox is present.
     * 
     * @param checkboxName checkbox name attribut.
     * @param checkboxValue checkbox value attribut.
     */
    public void assertCheckboxPresent(String checkboxName, String checkboxValue) {
        assertFormPresent();
        Assert.assertTrue("Did not find form checkbox with name ["
                + checkboxName + "] and value [" + checkboxValue + "].",
                getTestingEngine().hasElementByXPath(
                        "//input[@type='checkbox' and @name='" + checkboxName
                                + "' and @value='" + checkboxValue + "']"));
    }

    /**
     * Assert that a form checkbox with a given name is not present.
     * 
     * @param checkboxName checkbox name.
     */
    public void assertCheckboxNotPresent(String checkboxName) {
        assertFormPresent();
        Assert.assertFalse("Found form checkbox with name [" + checkboxName
                + "] when not expected.", getTestingEngine().hasElementByXPath(
                "//input[@type='checkbox' and @name='" + checkboxName + "']"));
    }

    /**
     * Assert that a given checkbox is not present.
     * 
     * @param checkboxName checkbox name.
     * @param checkboxValue checkbox value attribut.
     */
    public void assertCheckboxNotPresent(String checkboxName,
            String checkboxValue) {
        assertFormPresent();
        Assert.assertFalse("Found form checkbox with name [" + checkboxName
                + "] and value [" + checkboxValue + "] when not expected.",
                getTestingEngine().hasElementByXPath(
                        "//input[@type='checkbox' and @name='" + checkboxName
                                + "' and @value='" + checkboxValue + "']"));
    }

    /**
     * Assert that there is a form present.
     * 
     */
    public void assertFormPresent() {
        Assert.assertTrue("No form present", getTestingEngine().hasForm());
    }

    /**
     * Assert that there is a form with the specified name or id present.
     * 
     * @param nameOrID
     */
    public void assertFormPresent(String nameOrID) {
        Assert.assertTrue("No form present with name or id [" + nameOrID + "]",
                getTestingEngine().hasForm(nameOrID));
    }

    /**
     * Assert that there is not a form present.
     * 
     */
    public void assertFormNotPresent() {
        Assert.assertFalse("A form is present", getTestingEngine().hasForm());
    }

    /**
     * Assert that there is not a form with the specified name or id present.
     * 
     * @param nameOrID
     */
    public void assertFormNotPresent(String nameOrID) {
        Assert.assertFalse("Form present with name or id [" + nameOrID + "]",
                getTestingEngine().hasForm(nameOrID));
    }

    /**
     * Assert that a specific form element has an expected value. Can be used to check hidden input.
     * 
     * @param formElementName
     * @param expectedValue
     * @deprecated
     */
    public void assertFormElementEquals(String formElementName,
            String expectedValue) {
        assertFormElementPresent(formElementName);
        Assert.assertEquals(expectedValue, getTestingEngine()
                .getElementAttributByXPath(
                        "//input[@name='" + formElementName + "']", "value"));
    }

    /**
     * Assert that a specific form element matches an expected regexp.
     * 
     * @param formElementName
     * @param regexp
     * @deprecated
     */
    public void assertFormElementMatch(String formElementName, String regexp) {
        assertFormElementPresent(formElementName);
        RE re = null;
        try {
            re = new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue("Unable to match [" + regexp + "] in form element \""
                + formElementName + "\"", re.match(getTestingEngine()
                .getElementAttributByXPath(
                        "//input[@name='" + formElementName + "']", "value")));
    }

    /**
     * Assert that a form element had no value / is empty.
     * 
     * @param formElementName
     * @deprecated
     */
    public void assertFormElementEmpty(String formElementName) {
        assertFormElementPresent(formElementName);
        Assert.assertEquals("", getTestingEngine().getElementAttributByXPath(
                "//input[@name='" + formElementName + "']", "value"));
    }

    /**
     * Assert that an input text element with name <code>formElementName</code> has the <code>expectedValue</code>
     * value.
     * 
     * @param formElementName the value of the name attribute of the element
     * @param expectedValue the expected value of the given input element
     */
    public void assertTextFieldEquals(String formElementName,
            String expectedValue) {
        assertFormElementPresent(formElementName);
        Assert.assertEquals(expectedValue, getTestingEngine()
                .getTextFieldValue(formElementName));
    }

    /**
     * Assert that an input hidden element with name <code>formElementName</code> has the <code>expectedValue</code>
     * value.
     * 
     * @param formElementName the value of the name attribute of the element
     * @param expectedValue the expected value of the given input element
     */
    public void assertHiddenFieldPresent(String formElementName,
            String expectedValue) {
        assertFormElementPresent(formElementName);
        Assert.assertEquals(expectedValue, getTestingEngine()
                .getHiddenFieldValue(formElementName));
    }

    /**
     * Assert that a specific checkbox is selected.
     * 
     * @param checkBoxName
     */
    public void assertCheckboxSelected(String checkBoxName) {
        assertCheckboxPresent(checkBoxName);
        if (!getTestingEngine().isCheckboxSelected(checkBoxName)) {
            Assert.fail("Checkbox with name [" + checkBoxName
                    + "] was not found selected.");
        }
    }

    /**
     * Assert that a specific checkbox is selected.
     * 
     * @param checkBoxName
     * @param checkBoxValue
     */
    public void assertCheckboxSelected(String checkBoxName, String checkBoxValue) {
        assertCheckboxPresent(checkBoxName, checkBoxValue);
        if (!getTestingEngine().isCheckboxSelected(checkBoxName, checkBoxValue)) {
            Assert.fail("Checkbox with name [" + checkBoxName + "] and value ["
                    + checkBoxValue + "] was not found selected.");
        }
    }

    /**
     * Assert that a specific checkbox is not selected.
     * 
     * @param checkBoxName
     */
    public void assertCheckboxNotSelected(String checkBoxName) {
        assertCheckboxPresent(checkBoxName);
        if (getTestingEngine().isCheckboxSelected(checkBoxName)) {
            Assert.fail("Checkbox with name [" + checkBoxName
                    + "] was found selected.");
        }
    }

    /**
     * Assert that a specific checkbox is not selected.
     * 
     * @param checkBoxName
     * @param checkBoxValue
     */
    public void assertCheckboxNotSelected(String checkBoxName,
            String checkBoxValue) {
        assertCheckboxPresent(checkBoxName, checkBoxValue);
        if (getTestingEngine().isCheckboxSelected(checkBoxName, checkBoxValue)) {
            Assert.fail("Checkbox with name [" + checkBoxName + "] and value ["
                    + checkBoxValue + "] was found selected.");
        }
    }

    /**
     * Assert that a specific option is present in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for.
     */
    public void assertRadioOptionPresent(String name, String radioOption) {
        assertFormElementPresent(name);
        if (!getTestingEngine().hasRadioOption(name, radioOption)) {
            Assert.fail("Unable to find option [" + radioOption
                    + "] in radio group [" + name + "]");
        }
    }

    /**
     * Assert that a specific option is not present in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for.
     */
    public void assertRadioOptionNotPresent(String name, String radioOption) {
        assertFormElementPresent(name);
        if (getTestingEngine().hasRadioOption(name, radioOption))
            Assert.fail("Found option [" + radioOption + "] in radio group ["
                    + name + "]");
    }

    /**
     * Assert that a specific option is selected in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for selection.
     */
    public void assertRadioOptionSelected(String name, String radioOption) {
        assertRadioOptionPresent(name, radioOption);
        Assert.assertEquals(radioOption, getTestingEngine()
        		.getSelectedRadio(name));
    }

    /**
     * Assert that a specific option is not selected in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for selection.
     */
    public void assertRadioOptionNotSelected(String name, String radioOption) {
        assertRadioOptionPresent(name, radioOption);
        Assert.assertFalse("Radio option [" + radioOption + "] is selected.",
                radioOption.equals(getTestingEngine()
                        .getSelectedRadio(name)));
    }

    /**
     * Assert that given options are present in a select box (by label).
     * 
     * @param selectName name of the select element.
     * @param optionLabels option labels.
     */
    public void assertSelectOptionsPresent(String selectName,
            String[] optionLabels) {
        assertFormElementPresent(selectName);
        for (int i = 0; i < optionLabels.length; i++)
            Assert.assertTrue("Option [" + optionLabels[i]
                    + "] not found in select element " + selectName,
                    getTestingEngine().hasSelectOption(selectName,
                            optionLabels[i]));
    }

    /**
     * Assert that a specific option is present in a select box (by label).
     * 
     * @param selectName name of the select element.
     * @param optionLabel option label.
     */
    public void assertSelectOptionPresent(String selectName, String optionLabel) {
        assertSelectOptionsPresent(selectName, new String[] { optionLabel });
    }

    /**
     * Assert that given options are present in the Nth select box (by label).
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionLabels option labels.
     */
    public void assertSelectOptionsPresent(String selectName, int index,
            String[] optionLabels) {
        assertFormElementPresent(selectName);
        for (int i = 0; i < optionLabels.length; i++)
            Assert.assertTrue("Option [" + optionLabels[i]
                    + "] not found in select element " + selectName,
                    getTestingEngine().hasSelectOption(selectName, index,
                            optionLabels[i]));
    }

    /**
     * Assert that a specific option is present in the Nth select box (by label).
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionLabel option label.
     */
    public void assertSelectOptionPresent(String selectName, int index, String optionLabel) {
        assertSelectOptionsPresent(selectName, index, new String[] { optionLabel });
    }

    
    /**
     * Assert that given options are present in a select box (by value).
     * 
     * @param selectName name of the select element.
     * @param optionValues option labels.
     */
    public void assertSelectOptionValuesPresent(String selectName,
            String[] optionValues) {
        assertFormElementPresent(selectName);
        for (int i = 0; i < optionValues.length; i++)
            Assert.assertTrue("Option [" + optionValues[i]
                    + "] not found in select element " + selectName,
                    getTestingEngine().hasSelectOptionValue(selectName,
                            optionValues[i]));
    }

    /**
     * Assert that a specific option is present in a select box (by value).
     * 
     * @param selectName name of the select element.
     * @param optionValue option value.
     */
    public void assertSelectOptionValuePresent(String selectName,
            String optionValue) {
        assertSelectOptionValuesPresent(selectName,
                new String[] { optionValue });
    }

    /**
     * Assert that given options are present in the Nth select box (by value).
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionValues option labels.
     */
    public void assertSelectOptionValuesPresent(String selectName,
    											int index, 
    											String[] optionValues) {
        assertFormElementPresent(selectName);
        for (int i = 0; i < optionValues.length; i++)
            Assert.assertTrue("Option [" + optionValues[i]
                    + "] not found in select element " + selectName,
                    getTestingEngine().hasSelectOptionValue(selectName, 
                                                            index,
                                                            optionValues[i]));
    }

    /**
     * Assert that a specific option is present in the Nth select box (by value).
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionValue option value.
     */
    public void assertSelectOptionValuePresent(String selectName, 
    										   int index, 
    										   String optionValue) {
        assertSelectOptionValuesPresent(selectName, index,
                new String[] { optionValue });
    }

    /**
     * Assert that a specific option value is not present in a select box.
     * 
     * @param selectName name of the select element.
     * @param optionValue option value.
     */
    public void assertSelectOptionValueNotPresent(String selectName,
            String optionValue) {
        try {
            assertSelectOptionValuePresent(selectName, optionValue);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Option value" + optionValue + " found in select element "
                + selectName + " when not expected.");
    }

    /**
     * Assert that a specific option is not present in a select box.
     * 
     * @param selectName name of the select element.
     * @param expectedOption option label.
     */
    public void assertSelectOptionNotPresent(String selectName,
            String optionLabel) {
        try {
            assertSelectOptionPresent(selectName, optionLabel);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Option " + optionLabel + " found in select element "
                + selectName + " when not expected.");
    }
    
    /**
     * Assert that a specific option value is not present in a select box.
     * 
     * @param selectName name of the select element.
     * @param optionValue option value.
     */
    public void assertSelectOptionValueNotPresent(String selectName,
            int index, String optionValue) {
        try {
            assertSelectOptionValuePresent(selectName, index, optionValue);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Option value" + optionValue + " found in select element "
                + selectName + " when not expected.");
    }

    /**
     * Assert that a specific option is not present in a select box.
     * 
     * @param selectName name of the select element.
     * @param expectedOption option label.
     */
    public void assertSelectOptionNotPresent(String selectName,
            int index, String optionLabel) {
        try {
            assertSelectOptionPresent(selectName, index, optionLabel);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Option " + optionLabel + " found in select element "
                + selectName + " when not expected.");
    }

    /**
     * Assert that the display values of a select element's options match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param expectedOptions expected labels for the select box.
     */
    public void assertSelectOptionsEqual(String selectName,
            String[] expectedOptions) {
        assertFormElementPresent(selectName);
        assertArraysEqual(expectedOptions, getOptionsFor(selectName));
    }

    /**
     * Assert that the display values of 
     * the Nth select element's options match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param expectedOptions expected labels for the select box.
     */
    public void assertSelectOptionsEqual(String selectName, int index,
            String[] expectedOptions) {
        assertFormElementPresent(selectName);
        assertArraysEqual(expectedOptions, getOptionsFor(selectName, index));
    }

    
    /**
     * Assert that the display values of a select element's options do not match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param expectedOptions expected display values for the select box.
     */
    public void assertSelectOptionsNotEqual(String selectName,
            String[] expectedOptions) {
        assertFormElementPresent(selectName);
        try {
            assertSelectOptionsEqual(selectName, expectedOptions);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Options not expected to be equal");
    }

    /**
     * Assert that the display values of the Nth select element's 
     * options do not match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param expectedOptions expected display values for the select box.
     */
    public void assertSelectOptionsNotEqual(String selectName, int index,
            String[] expectedOptions) {
        assertFormElementPresent(selectName);
        try {
            assertSelectOptionsEqual(selectName, index, expectedOptions);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Options not expected to be equal");
    }

    
    /**
     * Assert that the values of the Nth select element's options match 
     * a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param expectedValues expected values for the select box.
     */
    public void assertSelectOptionValuesEqual(String selectName, int index,
            String[] expectedValues) {
        assertFormElementPresent(selectName);
        assertArraysEqual(expectedValues, getTestingEngine()
                .getSelectOptionValues(selectName, index));

    }

    /**
     * Assert that the values of a select element's options match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param expectedValues expected values for the select box.
     */
    public void assertSelectOptionValuesEqual(String selectName,
            String[] expectedValues) {
        assertFormElementPresent(selectName);
        assertArraysEqual(expectedValues, getTestingEngine()
                .getSelectOptionValues(selectName));

    }

    
    /**
     * Assert that the values of a select element's options do not match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param optionValues expected values for the select box.
     */
    public void assertSelectOptionValuesNotEqual(String selectName,
            String[] optionValues) {
        assertFormElementPresent(selectName);
        try {
            assertSelectOptionValuesEqual(selectName, optionValues);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Values not expected to be equal");
    }

    /**
     * Assert that the values of the Nth select element's options do not match a 
     * given array of strings.
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param optionValues expected values for the select box.
     */
    public void assertSelectOptionValuesNotEqual(String selectName, int index,
            String[] optionValues) {
        assertFormElementPresent(selectName);
        try {
            assertSelectOptionValuesEqual(selectName, index, optionValues);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Values not expected to be equal");
    }

    
    /**
     * Assert that the currently selected display label(s) of a select box matches given label(s).
     * 
     * @param selectName name of the select element.
     * @param labels expected display label(s) of the selected option.
     */
    public void assertSelectedOptionsEqual(String selectName, String[] labels) {
        assertFormElementPresent(selectName);
        Assert.assertEquals(labels.length, getTestingEngine()
                .getSelectedOptions(selectName).length);
        for (int i = 0; i < labels.length; i++)
            Assert.assertEquals(labels[i],
                    getTestingEngine()
                            .getSelectOptionLabelForValue(
                                    selectName,
                                    getTestingEngine().getSelectedOptions(
                                            selectName)[i]));
    }

    /**
     * Assert that the currently selected display label(s) of a select box matches given label(s).
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index used when more than one select element
     * with the same name is expected.
     * @param labels expected display label(s) of the selected option.
     */
    public void assertSelectedOptionsEqual(String selectName, int index, String[] labels) {
        assertFormElementPresent(selectName);
        Assert.assertEquals(labels.length, getTestingEngine()
                .getSelectedOptions(selectName, index).length);
        for (int i = 0; i < labels.length; i++)
            Assert.assertEquals(labels[i],
                    getTestingEngine()
                            .getSelectOptionLabelForValue(
                                    selectName, index,
                                    getTestingEngine().getSelectedOptions(
                                            selectName, index)[i]));
    }

    
    
    /**
     * Assert that the label of the current selected option matches
     * the provided value.
     * @param selectName name of the select element
     * @param optionLabel expected value of the option label
     */
    public void assertSelectedOptionEquals(String selectName, String optionLabel) {
        assertSelectedOptionsEqual(selectName, new String[] { optionLabel });
    }

    /**
     * Assert that the label of the current selected option matches
     * the provided value in the Nth select element with the specified name.
     * @param selectName name of the select element
     * @param index the 0-based index used when more than one select element
     * with the same name is expected.
     * @param optionLabel expected value of the option label
     */
    public void assertSelectedOptionEquals(String selectName, int index, String option) {
        assertSelectedOptionsEqual(selectName, index, new String[] { option });
    }

    
    /**
     * Assert that the currently selected value(s) of a select box matches given value(s).
     * 
     * @param selectName name of the select element.
     * @param values expected value(s) of the selected option.
     */
    public void assertSelectedOptionValuesEqual(String selectName,
            String[] values) {
        assertFormElementPresent(selectName);
        Assert.assertEquals(values.length, getTestingEngine()
                .getSelectedOptions(selectName).length);
        for (int i = 0; i < values.length; i++)
            Assert.assertEquals(values[i], getTestingEngine()
                    .getSelectedOptions(selectName)[i]);
    }

    /**
     * Assert that the currently selected value(s) of the Nth
     * select box with the specified name matches given value(s).
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index used when more than one select element
     * with the same name is expected.
     * @param values expected value(s) of the selected option.
     */
    public void assertSelectedOptionValuesEqual(String selectName,
            int index, String[] values) {
        assertFormElementPresent(selectName);
        Assert.assertEquals(values.length, getTestingEngine()
                .getSelectedOptions(selectName, index).length);
        for (int i = 0; i < values.length; i++)
            Assert.assertEquals(values[i], getTestingEngine()
                    .getSelectedOptions(selectName, index)[i]);
    }

    
    /**
     * Assert that the currently selected value of a select box matches given value.
     * 
     * @param selectName name of the select element.
     * @param value expected value of the selected option.
     */
    public void assertSelectedOptionValueEquals(String selectName, String value) {
        assertSelectedOptionValuesEqual(selectName, new String[] { value });
    }

    /**
     * Assert that the currently selected value of a select box matches given value.
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index used when more than one select element
     * with the same name is expected.
     * @param value expected value of the selected option.
     */
    public void assertSelectedOptionValueEquals(String selectName, int index, String value) {
        assertSelectedOptionValuesEqual(selectName, index, new String[] { value });
    }

    
    
    /**
     * Assert that the currently selected display value(s) of a select box matches a given value(s).
     * 
     * @param selectName name of the select element.
     * @param regexps expected display value of the selected option.
     */
    public void assertSelectedOptionsMatch(String selectName, String[] regexps) {
        assertFormElementPresent(selectName);
        Assert.assertEquals(regexps.length, getTestingEngine()
                .getSelectedOptions(selectName).length);
        for (int i = 0; i < regexps.length; i++) {
            RE re = getRE(regexps[i]);
            Assert.assertTrue("Unable to match [" + regexps[i]
                    + "] in option \""
                    + getTestingEngine().getSelectedOptions(selectName)[i]
                    + "\"", re.match(getTestingEngine().getSelectedOptions(
                    selectName)[i]));
        }
    }

    /**
     * Assert that the currently selected display value(s) of a select box matches a given value(s).
     * 
     * @param selectName name of the select element.
     * @param index the 0-based index used when more than one select element
     * with the same name is expected.
     * @param regexps expected display value of the selected option.
     */
    public void assertSelectedOptionsMatch(String selectName, int index, String[] regexps) {
        assertFormElementPresent(selectName);
        Assert.assertEquals(regexps.length, getTestingEngine()
                .getSelectedOptions(selectName, index).length);
        for (int i = 0; i < regexps.length; i++) {
            RE re = getRE(regexps[i]);
            Assert.assertTrue("Unable to match [" + regexps[i]
                    + "] in option \""
                    + getTestingEngine().getSelectedOptions(selectName, index)[i]
                    + "\" at index " + index, re.match(getTestingEngine().getSelectedOptions(
                    selectName, index)[i]));
        }
    }

    
    
    /**
     * Assert that the label of the current selected option matches
     * the provided regular expression value.
     * @param selectName name of the select element
     * @param regexp the regular expression to match 
     */
    public void assertSelectedOptionMatches(String selectName, String regexp) {
        assertSelectedOptionsMatch(selectName, new String[] { regexp });
    }

    /**
     * Assert that the label of the current selected option matches
     * the provided regular expression in the Nth select element with the specified name.
     * @param selectName name of the select element
     * @param index the 0-based index used when more than one select element
     * with the same name is expected.
     * @param regexp the regular expression to match
     */
    public void assertSelectedOptionMatches(String selectName, int index, String regexp) {
        assertSelectedOptionsMatch(selectName, index, new String[] { regexp });
    }

    
    /**
     * Assert that a submit button is present. <br/> A submit button can be the following HTML elements:
     * <ul>
     * <li>submit input
     * <li>image input
     * <li>submit button
     * </ul>
     * 
     */
    public void assertSubmitButtonPresent() {
        assertFormPresent();
        Assert.assertTrue("no submit button found.", getTestingEngine()
                .hasSubmitButton());
    }

    /**
     * Assert that a submit button with a given name is present. <br/> A submit button can be the following HTML
     * elements:
     * <ul>
     * <li>submit input
     * <li>image input
     * <li>submit button
     * </ul>
     * 
     * @param buttonName
     */
    public void assertSubmitButtonPresent(String buttonName) {
        assertFormPresent();
        Assert.assertTrue("Submit Button [" + buttonName + "] not found.",
                getTestingEngine().hasSubmitButton(buttonName));
    }

    /**
     * Assert that no submit button is present in the current form. <br/> A submit button can be the following HTML
     * elements:
     * <ul>
     * <li>submit input
     * <li>image input
     * <li>submit button
     * </ul>
     * 
     * @param buttonName
     */
    public void assertSubmitButtonNotPresent() {
        assertFormPresent();
        Assert.assertFalse("Submit Button found.", getTestingEngine()
                .hasSubmitButton());
    }

    /**
     * Assert that a submit button with a given name is not present. <br/> A submit button can be the following HTML
     * elements:
     * <ul>
     * <li>submit input
     * <li>image input
     * <li>submit button
     * </ul>
     * 
     * @param buttonName
     */
    public void assertSubmitButtonNotPresent(String buttonName) {
        assertFormPresent();
        Assert.assertFalse("Submit Button [" + buttonName + "] found.",
                getTestingEngine().hasSubmitButton(buttonName));
    }

    /**
     * Assert that a submit button with a given name and value is present. <br/> A submit button can be the following
     * HTML elements:
     * <ul>
     * <li>submit input
     * <li>image input
     * <li>submit button
     * </ul>
     * 
     * @param buttonName
     * @param buttonValue
     */
    public void assertSubmitButtonPresent(String buttonName, String buttonValue) {
        assertFormPresent();
        Assert.assertTrue("Submit Button [" + buttonName + "] with value ["
                + buttonValue + "] not found.", getTestingEngine()
                .hasSubmitButton(buttonName, buttonValue));
    }

    /**
     * Assert that a reset button is present. <br/> A reset button can be the following HTML elements:
     * <ul>
     * <li>reset input
     * <li>reset button
     * </ul>
     * 
     */
    public void assertResetButtonPresent() {
        assertFormPresent();
        Assert.assertTrue("no reset button found.", getTestingEngine()
                .hasResetButton());
    }

    /**
     * Assert that a reset button with a given name is present.<br/> A reset button can be the following HTML elements:
     * <ul>
     * <li>reset input
     * <li>reset button
     * </ul>
     * 
     * @param buttonName
     */
    public void assertResetButtonPresent(String buttonName) {
        assertFormPresent();
        Assert.assertTrue("Reset Button [" + buttonName + "] not found.",
                getTestingEngine().hasResetButton(buttonName));
    }

    /**
     * Assert that no reset button is present in the current form.<br/> A reset button can be the following HTML
     * elements:
     * <ul>
     * <li>reset input
     * <li>reset button
     * </ul>
     * 
     * @param buttonName
     */
    public void assertResetButtonNotPresent() {
        assertFormPresent();
        Assert.assertFalse("Reset Button found.", getTestingEngine()
                .hasResetButton());
    }

    /**
     * Assert that a reset button with a given name is not present.<br/> A reset button can be the following HTML
     * elements:
     * <ul>
     * <li>reset input
     * <li>reset button
     * </ul>
     * 
     * @param buttonName
     */
    public void assertResetButtonNotPresent(String buttonName) {
        assertFormPresent();
        Assert.assertFalse("Reset Button [" + buttonName + "] found.",
                getTestingEngine().hasResetButton(buttonName));
    }

    /**
     * Assert that a button with a given id is present in the current window.<br/> A button can be the following HTML
     * elements:
     * <ul>
     * <li>button input
     * <li>button button
     * </ul>
     * 
     * @param buttonId
     */
    public void assertButtonPresent(String buttonId) {
        assertFormPresent();
        Assert.assertTrue("Button [" + buttonId + "] not found.", getTestingEngine()
                .hasButton(buttonId));
    }

    /**
     * Assert that a button with a given text is present in the current window.
     * 
     * @param text Text representation of button content.
     */
    public void assertButtonPresentWithText(String text) {
        Assert.assertTrue("Did not find button with text [" + text + "].",
                getTestingEngine().hasButtonWithText(text));
    }

    /**
     * Assert that a button with a given text is not present in the current window.
     * 
     * @param text Text representation of button content.
     */
    public void assertButtonNotPresentWithText(String text) {
        Assert.assertFalse("Found button with text [" + text + "].",
                getTestingEngine().hasButtonWithText(text));
    }

    /**
     * Assert that a button with a given id is not present in the current window.
     * 
     * @param buttonId
     */
    public void assertButtonNotPresent(String buttonId) {
        assertFormPresent();
        Assert.assertFalse(
                "Button [" + buttonId + "] found when not expected.",
                getTestingEngine().hasButton(buttonId));
    }

    /**
     * Assert that a link with a given id is present in the response.
     * 
     * @param linkId
     */
    public void assertLinkPresent(String linkId) {
        Assert.assertTrue("Unable to find link with id [" + linkId + "]",
                getTestingEngine().hasLink(linkId));
    }

    /**
     * Assert that no link with the given id is present in the response.
     * 
     * @param linkId
     */
    public void assertLinkNotPresent(String linkId) {
        Assert.assertTrue("link with id [" + linkId + "] found in response",
                !getTestingEngine().hasLink(linkId));
    }

    /**
     * Assert that a link containing the supplied text is present.
     * 
     * @param linkText
     */
    public void assertLinkPresentWithText(String linkText) {
        Assert.assertTrue("Link with text [" + linkText
                + "] not found in response.", getTestingEngine()
                .hasLinkWithText(linkText, 0));
    }

    /**
     * Assert that no link containing the supplied text is present.
     * 
     * @param linkText
     */
    public void assertLinkNotPresentWithText(String linkText) {
        Assert.assertTrue("Link with text [" + linkText
                + "] found in response.", !getTestingEngine().hasLinkWithText(
                linkText, 0));
    }

    /**
     * Assert that a link containing the supplied text is present.
     * 
     * @param linkText
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    public void assertLinkPresentWithText(String linkText, int index) {
        Assert.assertTrue("Link with text [" + linkText + "] and index ["
                + index + "] not found in response.", getTestingEngine()
                .hasLinkWithText(linkText, index));
    }

    /**
     * Assert that no link containing the supplied text is present.
     * 
     * @param linkText
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    public void assertLinkNotPresentWithText(String linkText, int index) {
        Assert.assertTrue("Link with text [" + linkText + "] and index "
                + index + " found in response.", !getTestingEngine()
                .hasLinkWithText(linkText, index));
    }

    // BEGIN RFE 996031...

    /**
     * Assert that a link containing the Exact text is present.
     * 
     * @param linkText
     */
    public void assertLinkPresentWithExactText(String linkText) {
        Assert.assertTrue("Link with Exact text [" + linkText
                + "] not found in response.", getTestingEngine()
                .hasLinkWithExactText(linkText, 0));
    }

    /**
     * Assert that no link containing the Exact text is present.
     * 
     * @param linkText
     */
    public void assertLinkNotPresentWithExactText(String linkText) {
        Assert.assertTrue("Link with Exact text [" + linkText
                + "] found in response.", !getTestingEngine()
                .hasLinkWithExactText(linkText, 0));
    }

    /**
     * Assert that a link containing the Exact text is present.
     * 
     * @param linkText
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    public void assertLinkPresentWithExactText(String linkText, int index) {
        Assert.assertTrue("Link with Exact text [" + linkText + "] and index ["
                + index + "] not found in response.", getTestingEngine()
                .hasLinkWithExactText(linkText, index));
    }

    /**
     * Assert that no link containing the Exact text is present.
     * 
     * @param linkText
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    public void assertLinkNotPresentWithExactText(String linkText, int index) {
        Assert.assertTrue("Link with Exact text [" + linkText + "] and index "
                + index + " found in response.", !getTestingEngine()
                .hasLinkWithExactText(linkText, index));
    }

    // END RFE 996031...

    /**
     * Assert that a link containing a specified image is present.
     * 
     * @param imageFileName A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"</tt>,
     *            you could just pass in <tt>"my_icon.png"</tt>.
     */
    public void assertLinkPresentWithImage(String imageFileName) {
        Assert.assertTrue("Link with image file [" + imageFileName
                + "] not found in response.", getTestingEngine()
                .hasLinkWithImage(imageFileName, 0));
    }

    /**
     * Assert that a link containing a specified image is not present.
     * 
     * @param imageFileName A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"</tt>,
     *            you could just pass in <tt>"my_icon.png"</tt>.
     */
    public void assertLinkNotPresentWithImage(String imageFileName) {
        Assert.assertTrue("Link with image file [" + imageFileName
                + "] found in response.", !getTestingEngine().hasLinkWithImage(
                imageFileName, 0));
    }

    /**
     * Assert that an element with a given id is present.
     * 
     * @param anID element id to test for.
     */
    public void assertElementPresent(String anID) {
        Assert.assertTrue("Unable to locate element with id \"" + anID + "\"",
                getTestingEngine().hasElement(anID));
    }

    /**
     * Assert that an element with a given id is not present.
     * 
     * @param anID element id to test for.
     */
    public void assertElementNotPresent(String anID) {
        Assert.assertFalse("Located element with id \"" + anID + "\"",
                getTestingEngine().hasElement(anID));
    }

    /**
     * Assert that an element with a given xpath is present.
     * 
     * @param xpath element xpath to test for.
     */
    public void assertElementPresentByXPath(String xpath) {
        Assert.assertTrue("Unable to locate element with xpath \"" + xpath
                + "\"", getTestingEngine().hasElementByXPath(xpath));
    }

    /**
     * Assert that an element with a given xpath is not present.
     * 
     * @param xpath element xpath to test for.
     */
    public void assertElementNotPresentByXPath(String xpath) {
        Assert.assertFalse("Located element with xpath \"" + xpath + "\"",
                getTestingEngine().hasElementByXPath(xpath));
    }

    /**
     * Assert that a given element contains specific text.
     * 
     * @param elementID id of element to be inspected.
     * @param text to check for.
     */
    public void assertTextInElement(String elementID, String text) {
        Assert.assertTrue("Unable to locate element with id \"" + elementID
                + "\"", getTestingEngine().hasElement(elementID));
        Assert.assertTrue("Unable to locate [" + text + "] in element \""
                + elementID + "\"", getTestingEngine()
                .isTextInElement(elementID, text));
    }

    public void assertTextNotInElement(String elementID, String text) {
        assertElementPresent(elementID);
        Assert.assertTrue("Unable to locate element with id \"" + elementID
                + "\"", getTestingEngine().hasElement(elementID));
        Assert.assertFalse("Text [" + text + "] found in element [" + elementID
                + "] when not expected", getTestingEngine().isTextInElement(
                elementID, text));
    }

    /**
     * Assert that a given element matches a specific regexp.
     * 
     * @param elementID id of element to be inspected.
     * @param regexp to match.
     */
    public void assertMatchInElement(String elementID, String regexp) {
        Assert.assertTrue("Unable to locate element with id \"" + elementID
                + "\"", getTestingEngine().hasElement(elementID));
        Assert.assertTrue("Unable to match [" + regexp + "] in element \""
                + elementID + "\"", getTestingEngine().isMatchInElement(
                elementID, regexp));
    }

    /**
     * Assert that a given element does not match a specific regexp.
     * 
     * @param elementID id of element to be inspected.
     * @param regexp to match.
     */
    public void assertNoMatchInElement(String elementID, String regexp) {
        assertElementPresent(elementID);
        Assert.assertTrue("Unable to locate element with id \"" + elementID
                + "\"", getTestingEngine().hasElement(elementID));
        Assert.assertFalse("Regexp [" + regexp + "] matched in element ["
                + elementID + "] when not expected", getTestingEngine()
                .isMatchInElement(elementID, regexp));
    }

    /**
     * Assert that a window with the given name is open.
     * 
     * @param windowName
     */
    public void assertWindowPresent(String windowName) {
        Assert.assertTrue("Unable to locate window [" + windowName + "].",
                getTestingEngine().hasWindow(windowName));
    }

    /**
     * Assert that a window with the given ID is open.
     * 
     * @param windowID Javascript window ID.
     */
    public void assertWindowPresent(int windowID) {
        Assert.assertTrue("There is no window with index [" + windowID + "].",
                getTestingEngine().getWindowCount() > windowID);
    }

    /**
     * Assert that at least one window with the given title is open.
     * 
     * @param title
     */
    public void assertWindowPresentWithTitle(String title) {
        Assert.assertTrue(
                "Unable to locate window with title [" + title + "].",
                getTestingEngine().hasWindowByTitle(title));
    }

    /**
     * Assert that the number of opened windows equals given value.
     * 
     * @param windowCount Window count
     */
    public void assertWindowCountEquals(int windowCount) {
        Assert
                .assertTrue("Window count is "
                        + getTestingEngine().getWindowCount() + " but "
                        + windowCount + " was expected.", getTestingEngine()
                        .getWindowCount() == windowCount);
    }

    /**
     * Assert that a frame with the given name or ID is present.
     * 
     * @param frameNameOrId Name or ID of the frame. ID is checked first.
     */
    public void assertFramePresent(String frameNameOrId) {
        Assert.assertTrue("Unable to locate frame with name or ID ["
                + frameNameOrId + "].", getTestingEngine().hasFrame(
                frameNameOrId));
    }

    /**
     * Checks to see if a cookie is present in the response.
     * 
     * @param cookieName The cookie name
     */
    public void assertCookiePresent(String cookieName) {
        List cookies = getTestingEngine().getCookies();
        for (Iterator i = cookies.iterator(); i.hasNext();) {
            if (((Cookie) i.next()).getName().equals(cookieName)) {
                return;
            }
        }
        Assert.fail("Could not find Cookie with name [" + cookieName + "]");
    }

    /**
     * Check to see if a cookie has the given value.
     * 
     * @param cookieName The cookie name
     * @param expectedValue The cookie value
     */
    public void assertCookieValueEquals(String cookieName, String expectedValue) {
        assertCookiePresent(cookieName);
        List cookies = getTestingEngine().getCookies();
        for (Iterator i = cookies.iterator(); i.hasNext();) {
            Cookie c = (Cookie) i.next();
            if (c.getName().equals(cookieName)) {
                Assert.assertEquals(expectedValue, c.getValue());
                return;
            }
        }
        Assert.fail("Should not be reached");
    }

    /**
     * Check to see if a cookie value match the given regexp.
     * 
     * @param cookieName The cookie name
     * @param regexp The regexp
     */
    public void assertCookieValueMatch(String cookieName, String regexp) {
        assertCookiePresent(cookieName);
        RE re = null;
        try {
            re = new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            Assert.fail(e.toString());
        }
        List cookies = getTestingEngine().getCookies();
        for (Iterator i = cookies.iterator(); i.hasNext();) {
            Cookie c = (Cookie) i.next();
            if (c.getName().equals(cookieName)) {
                Assert.assertTrue("Unable to match [" + regexp
                        + "] in cookie \"" + cookieName + "\"", re.match(c
                        .getValue()));
                return;
            }
        }
        Assert.fail("Should not be reached");
    }

    // Form interaction methods

    /**
     * @deprecated
     */
    public String getFormElementValue(String formElementName) {
        assertFormPresent();
        assertFormElementPresent(formElementName);
        return getTestingEngine().getElementAttributByXPath(
                "//input[@name='" + formElementName + "']", "value");
    }

    /**
     * Begin interaction with a specified form. If form interaction methods are called without explicitly calling this
     * method first, JWebUnit will attempt to determine itself which form is being manipulated.
     * 
     * It is not necessary to call this method if their is only one form on the current page.
     * 
     * @param index 0-based index of the form to work with.
     */
    public void setWorkingForm(int index) {
        getTestingEngine().setWorkingForm(index);
    }

    /**
     * Begin interaction with a specified form. If form interaction methods are called without explicitly calling this
     * method first, JWebUnit will attempt to determine itself which form is being manipulated.
     * 
     * It is not necessary to call this method if their is only one form on the current page.
     * 
     * @param nameOrId name or id of the form to work with.
     */
    public void setWorkingForm(String nameOrId) {
        assertFormPresent(nameOrId);
        getTestingEngine().setWorkingForm(nameOrId, 0);
    }

    /**
     * Begin interaction with a specified form. If form interaction methods are called without explicitly calling this
     * method first, JWebUnit will attempt to determine itself which form is being manipulated.
     * 
     * It is not necessary to call this method if their is only one form on the current page.
     * 
     * @param nameOrId name or id of the form to work with.
     * @param index The 0-based index, when more than one form with the same name is expected.
     */
    public void setWorkingForm(String nameOrId, int index) {
        getTestingEngine().setWorkingForm(nameOrId, index);
    }

    /**
     * Set the value of a text or password input field.
     * 
     * @param inputName name of form element.
     * @param value value to set.
     */
    public void setTextField(String inputName, String value) {
        assertFormPresent();
        assertFormElementPresent(inputName);
        getTestingEngine().setTextField(inputName, value);
    }

    /**
     * Select a specified checkbox. If the checkbox is already checked then the checkbox will stay checked.
     * 
     * @param checkBoxName name of checkbox to be selected.
     */
    public void checkCheckbox(String checkBoxName) {
        assertCheckboxPresent(checkBoxName);
        getTestingEngine().checkCheckbox(checkBoxName);
    }

    /**
     * Select a specified checkbox. If the checkbox is already checked then the checkbox will stay checked.
     * 
     * @param checkBoxName name of checkbox to be selected.
     * @param value value of checkbox to be selected.
     */
    public void checkCheckbox(String checkBoxName, String value) {
        assertCheckboxPresent(checkBoxName);
        getTestingEngine().checkCheckbox(checkBoxName, value);
    }

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then the checkbox will stay unchecked.
     * 
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        assertFormElementPresent(checkBoxName);
        getTestingEngine().uncheckCheckbox(checkBoxName);
    }

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then the checkbox will stay unchecked.
     * 
     * @param checkBoxName name of checkbox to be deselected.
     * @param value value of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName, String value) {
        assertFormElementPresent(checkBoxName);
        getTestingEngine().uncheckCheckbox(checkBoxName, value);
    }
    
    /**
     * Select options with given display labels in a select element.
     * 
     * @param selectName name of select element.
     * @param labels labels of options to be selected.
     */
    public void selectOptions(String selectName, String[] labels) {
        assertSelectOptionsPresent(selectName, labels);
        selectOptionsByLabel(selectName, labels);
    }
    
    /**
     * Select an option with a given display label in a select element.
     * 
     * @param selectName name of select element.
     * @param label label of option to be selected.
     */
    public void selectOption(String selectName, String label) {
        selectOptions(selectName, new String[] { label });
    }

    /**
     * Select an option with a given display label in Nth select element.
     * 
     * @param selectName name of select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param label label of option to be selected.
     */
    public void selectOption(String selectName, int index, String label) {
        selectOptions(selectName, index, new String[] { label });
    }
    
    /**
     * Select options with given display labels in the Nth select element.
     * 
     * @param selectName name of select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param labels labels of options to be selected.
     */
    public void selectOptions(String selectName, int index, String[] labels) {
        assertSelectOptionsPresent(selectName, index, labels);
        selectOptionsByLabel(selectName, index, labels);
    }
    
    
    /**
     * Select options with given values in a select element.
     * 
     * @param selectName name of select element.
     * @param values values of options to be selected.
     */
    public void selectOptionsByValues(String selectName, String[] values) {
        assertSelectOptionValuesPresent(selectName, values);
        getTestingEngine().selectOptions(selectName, values);
    }

    /**
     * Select an option with a given value in the Nth select element.
     * 
     * @param selectName name of select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param values values of options to be selected.
     */
    public void selectOptionByValue(String selectName, String value) {
        selectOptionsByValues(selectName, new String[] { value });
    }

    /**
     * Select options with given values in the Nth select element.
     * 
     * @param selectName name of select element.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param values values of options to be selected.
     */
    public void selectOptionsByValues(String selectName, int index, String[] values) {
        assertSelectOptionValuesPresent(selectName, index, values);
        getTestingEngine().selectOptions(selectName, index, values);
    }

    /**
     * Select an option with a given value in a select element.
     * 
     * @param selectName name of select element.
     * @param values values of options to be selected.
     */
    public void selectOptionByValue(String selectName, int index, String value) {
        selectOptionsByValues(selectName, index, new String[] { value });
    }

    
    // Form submission and link navigation methods

    /**
     * Submit form - default submit button will be used (unnamed submit button, or named button if there is only one on
     * the form.
     */
    public void submit() {
        assertSubmitButtonPresent();
        getTestingEngine().submit();
    }

    /**
     * Submit form by pressing named button.
     * 
     * @param buttonName Submit button name attribut value.
     */
    public void submit(String buttonName) {
        assertSubmitButtonPresent(buttonName);
        getTestingEngine().submit(buttonName);
    }

    /**
     * Submit the form by pressing the named button with the given value (label). Useful if you have more than one
     * submit button with same name.
     * 
     * @param buttonName Submit button name attribut value.
     * @param buttonValue Submit button value attribut value.
     */
    public void submit(String buttonName, String buttonValue) {
        assertSubmitButtonPresent(buttonName, buttonValue);
        getTestingEngine().submit(buttonName, buttonValue);
    }

    /**
     * Reset the current form using the default reset button. See {@link #getForm}for an explanation of how the current
     * form is established.
     */
    public void reset() {
        assertResetButtonPresent();
        getTestingEngine().reset();
    }

    /**
     * Navigate by selection of a link containing given text.
     * 
     * @param linkText Text in the link.
     */
    public void clickLinkWithText(String linkText) {
        assertLinkPresentWithText(linkText);
        getTestingEngine().clickLinkWithText(linkText, 0);
    }

    /**
     * Navigate by selecting Nth link containing given text.
     * 
     * @param linkText Text in the link.
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    public void clickLinkWithText(String linkText, int index) {
        assertLinkPresentWithText(linkText, index);
        getTestingEngine().clickLinkWithText(linkText, index);
    }

    /**
     * Navigate by selection of a link with the exact given text.
     * 
     * @param linkText Text of the link.
     */
    public void clickLinkWithExactText(String linkText) {
        assertLinkPresentWithExactText(linkText);
        getTestingEngine().clickLinkWithExactText(linkText, 0);
    }

    /**
     * Navigate by selecting Nth link with the exact given text.
     * 
     * @param linkText Text of the link.
     * @param index The 0-based index, when more than one link with the same text is expected.
     */
    public void clickLinkWithExactText(String linkText, int index) {
        assertLinkPresentWithExactText(linkText, index);
        getTestingEngine().clickLinkWithExactText(linkText, index);
    }

    /**
     * Click the button with the given id.
     * 
     * @param buttonId Button ID attribut value.
     */
    public void clickButton(String buttonId) {
        assertButtonPresent(buttonId);
        getTestingEngine().clickButton(buttonId);
    }

    /**
     * Clicks a button with <code>text</code> of the value attribute.
     * 
     * @param buttonValueText The text of the button (contents of the value attribute).
     */
    public void clickButtonWithText(String buttonValueText) {
        assertButtonPresentWithText(buttonValueText);
        getTestingEngine().clickButtonWithText(buttonValueText);
    }

    /**
     * Navigate by selection of a link with a given image.
     * 
     * @param imageFileName A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"</tt>,
     *            you could just pass in <tt>"my_icon.png"</tt>.
     */
    public void clickLinkWithImage(String imageFileName) {
        assertLinkPresentWithImage(imageFileName);
        getTestingEngine().clickLinkWithImage(imageFileName, 0);
    }

    /**
     * Navigate by selection of a link with given id.
     * 
     * @param linkId id of link
     */
    public void clickLink(String linkId) {
        assertLinkPresent(linkId);
        getTestingEngine().clickLink(linkId);
    }

    /**
     * Clicks a radio option. Asserts that the radio option exists first. *
     * 
     * @param radioGroup name of the radio group.
     * @param radioOption value of the option to check for.
     */
    public void clickRadioOption(String radioGroup, String radioOption) {
        assertRadioOptionPresent(radioGroup, radioOption);
        getTestingEngine().clickRadioOption(radioGroup, radioOption);
    }

    /**
     * Click element with given xpath.
     * 
     * @param xpath xpath of the element.
     */
    public void clickElementByXPath(String xpath) {
        assertElementPresentByXPath(xpath);
        getTestingEngine().clickElementByXPath(xpath);
    }

    /**
     * Get the attribut value of the gicen element. For example, if you have img src="bla.gif" alt="toto",
     * getElementAttributByXPath("//img[@src='bla.gif']", "alt") returns "toto"
     * 
     * @param xpath XPath of the element.
     * @param attribut Name of the attribut.
     * @return The value of the attribut.
     */
    public String getElementAttributByXPath(String xpath, String attribut) {
        assertElementPresentByXPath(xpath);
        return getTestingEngine().getElementAttributByXPath(xpath, attribut);
    }

    /**
     * Get text of the given element.
     * 
     * @param xpath xpath of the element.
     */
    public String getElementTextByXPath(String xpath){
        assertElementPresentByXPath(xpath);
    	return getTestingEngine().getElementTextByXPath(xpath);
    }
    
    
    
    
    // Window and Frame Navigation Methods

    /**
     * Make a given window active.
     * 
     * @param windowName Name of the window.
     */
    public void gotoWindow(String windowName) {
        assertWindowPresent(windowName);
        getTestingEngine().gotoWindow(windowName);
    }

    /**
     * Make a given window active.
     * 
     * @param windowID Javascript ID of the window
     */
    public void gotoWindow(int windowID) {
        assertWindowPresent(windowID);
        getTestingEngine().gotoWindow(windowID);
    }

    /**
     * Make the root window active.
     */
    public void gotoRootWindow() {
        getTestingEngine().gotoRootWindow();
    }

    /**
     * Make first window with the given title active.
     * 
     * @param title Title of the window.
     */
    public void gotoWindowByTitle(String title) {
        assertWindowPresentWithTitle(title);
        getTestingEngine().gotoWindowByTitle(title);
    }

    /**
     * Make the given frame active.
     * 
     * @param frameNameOrId Name or ID of the frame. ID is checked first.
     */
    public void gotoFrame(String frameNameOrId) {
        getTestingEngine().gotoFrame(frameNameOrId);
    }

    /**
     * Go to the given page like if user has typed the URL manually in the browser. Use
     * {@link TestContext#setBaseUrl(String) getTestContext().setBaseUrl(String)} to define base URL. Absolute URL
     * should start with "http://", "https://" or "www.".
     * 
     * @param url absolute or relative URL (relative to base URL).
     * @throws TestingEngineResponseException If something bad happend (404)
     */
    public void gotoPage(String url) throws TestingEngineResponseException {
        try {
            getTestingEngine().gotoPage(createUrl(url, getTestContext().getBaseUrl()));
        } catch (MalformedURLException e) {
            Assert.fail(e.getLocalizedMessage());
        }
    }

    /**
     * Print all the cookies to stdout.
     * 
     */
    public void dumpCookies() {
        List cookies = getTestingEngine().getCookies();
        for (Iterator i = cookies.iterator(); i.hasNext();) {
            Cookie c = (Cookie) i.next();
            System.out.println("Name=" + c.getName() + "; Value="
                    + c.getValue() + "; Domain=" + c.getDomain() + "; Comment="
                    + c.getComment() + "; MaxAge=" + c.getMaxAge() + "; Path="
                    + c.getPath() + "; Version=" + c.getVersion());
        }
    }

    /**
     * Get the source of the HTML page (like in a real browser), or HTTP body for a non HTML content.
     * 
     * @return The HTML content.
     */
    public String getPageSource() {
        return getTestingEngine().getPageSource();
    }

    /**
     * Get the last data sent by the server.
     * 
     * @return HTTP server response.
     */
    public String getServeurResponse() {
        return getTestingEngine().getServerResponse();
    }

    /**
     * Save the last downloaded page (or file) to the disk.
     * 
     * @param f The file name.
     */
    public void saveAs(File f) {
        InputStream in = getTestingEngine().getInputStream();
        int c=0;
        try {
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            while ((c=in.read()) != -1) out.write(c);
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Error when writing to file", e);
        }

    }

    /**
     * Download the current page (or file) and compare it with the given file.
     * 
     * @param expected Expected file URL.
     */
    public void assertDownloadedFileEquals(URL expected) {
        try {
            File tmp = File.createTempFile("jwebunit", null);
            tmp.deleteOnExit();
            saveAs(tmp);
            Assert.assertTrue("Files are not binary equals.", areFilesEqual(
                    expected, tmp.toURL()));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    // Debug methods

    /**
     * Dump html of current response to System.out - for debugging purposes.
     * 
     * @param stream
     * @deprecated Use {@link WebTester#getPageSource()}
     */
    public void dumpHtml() {
        dumpHtml(System.out);
    }

    /**
     * Dump html of current response to a specified stream - for debugging purposes.
     * 
     * @param stream
     * @deprecated Use {@link WebTester#getPageSource()}
     */
    public void dumpHtml(PrintStream stream) {
        stream.println(getTestingEngine().getPageSource());
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     * 
     * @param tableNameOrId
     * @param stream
     */
    public void dumpTable(String tableNameOrId) {
        dumpTable(tableNameOrId, System.out);
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     * 
     * @param tableNameOrId
     * @param table
     * @param stream
     */
    public void dumpTable(String tableNameOrId, PrintStream stream) {
        // String[][] table = getDialogInternal().getTable(tableNameOrId).getStrings();
        // //TODO Print correctly cells with colspan
        // stream.print("\n" + tableNameOrId + ":");
        // for (int i = 0; i < table.length; i++) {
        // String[] cell = table[i];
        // stream.print("\n\t");
        // for (int j = 0; j < cell.length; j++) {
        // stream.print("[" + cell[j] + "]");
        // }
        // }

    }

    // Settings

    /**
     * Enable or disable Javascript support
     */
    public void setScriptingEnabled(boolean value) {
        getTestingEngine().setScriptingEnabled(value);
    }

    /**
     * Set the Testing Engine that you want to use for the tests based on the Testing Engine Key.
     * 
     * @see TestingEngineRegistry
     * @param testingEngineKey The testingEngineKey to set.
     */
    public void setTestingEngineKey(String testingEngineKey) {
        this.testingEngineKey = testingEngineKey;
        testingEngine = null;
    }

    /**
     * Gets the Testing Engine Key that is used to find the proper testing engine class (HtmlUnitDialog /
     * SeleniumDialog) for the tests.
     * 
     * @return Returns the testingEngineKey.
     */
    public String getTestingEngineKey() {
        if (testingEngineKey == null) {
            // use first available testing engine
            String key = TestingEngineRegistry.getFirstAvailable();
            if (key != null) {
                setTestingEngineKey(key);
            } else {
                throw new RuntimeException(
                        "TestingEngineRegistry contains no testing engine. Check you put at least one plugin in the classpath.");
            }
        }
        return testingEngineKey;
    }

    private RE getRE(String regexp) {
        RE re = null;
        try {
            re = new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            Assert.fail(e.toString());
        }
        return re;
    }

    /**
     * Return a string array of select box option labels. <br/>
     * 
     * Example: <br/>
     * 
     * <pre>
     *  &lt;FORM action=&quot;http://my_host/doit&quot; method=&quot;post&quot;&gt;
     *    &lt;P&gt;
     *      &lt;SELECT multiple size=&quot;4&quot; name=&quot;component-select&quot;&gt;
     *        &lt;OPTION selected value=&quot;Component_1_a&quot;&gt;Component_1&lt;/OPTION&gt;
     *        &lt;OPTION selected value=&quot;Component_1_b&quot;&gt;Component_2&lt;/OPTION&gt;
     *        &lt;OPTION&gt;Component_3&lt;/OPTION&gt;
     *        &lt;OPTION&gt;Component_4&lt;/OPTION&gt;
     *        &lt;OPTION&gt;Component_5&lt;/OPTION&gt;
     *      &lt;/SELECT&gt;
     *      &lt;INPUT type=&quot;submit&quot; value=&quot;Send&quot;&gt;&lt;INPUT type=&quot;reset&quot;&gt;
     *    &lt;/P&gt;
     *  &lt;/FORM&gt;
     * </pre>
     * 
     * Should return [Component_1, Component_2, Component_3, Component_4, Component_5]
     * 
     * @param selectName name of the select box.
     * @return Array of options labels.
     */
    private String[] getOptionsFor(String selectName) {
        String[] values = getTestingEngine().getSelectOptionValues(selectName);
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = getTestingEngine().getSelectOptionLabelForValue(
                    selectName, values[i]);
        }
        return result;
    }

    /**
     * Return a string array of select box option labels. 
     * 
     * @param selectName name of the select box.
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @return Array of options labels.
     */
    private String[] getOptionsFor(String selectName, int index) {
        String[] values = getTestingEngine().getSelectOptionValues(selectName, index);
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = getTestingEngine().getSelectOptionLabelForValue(
                    selectName, index, values[i]);
        }
        return result;
    }

    
    
    /**
     * Select options by given labels in a select box.
     * 
     * @param selectName name of the select
     * @param labels labels of options to be selected
     */
    private void selectOptionsByLabel(String selectName, String[] labels) {
        String[] values = new String[labels.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = getTestingEngine().getSelectOptionValueForLabel(
                    selectName, labels[i]);
        }
        getTestingEngine().selectOptions(selectName, values);
    }

    /**
     * Select options by given labels in the Nth select box.
     * 
     * @param selectName name of the select
     * @param index the 0-based index of the select element when multiple
     * select elements are expected. 
     * @param labels labels of options to be selected
     */
    private void selectOptionsByLabel(String selectName, int index, String[] labels) {
        String[] values = new String[labels.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = getTestingEngine().getSelectOptionValueForLabel(
                    selectName, index, labels[i]);
        }
        getTestingEngine().selectOptions(selectName, index, values);
    }

    
    private void assertArraysEqual(String[] exptected, String[] returned) {
        Assert.assertEquals("Arrays not same length", exptected.length,
                returned.length);
        for (int i = 0; i < returned.length; i++) {
            Assert.assertEquals("Elements " + i + "not equal", exptected[i],
                    returned[i]);
        }
    }

    /**
     * Set the value of a form input element.
     * 
     * @param formElementName name of form element.
     * @param value
     * @deprecated use setTextField or other methods
     */
    public void setFormElement(String formElementName, String value) {
        assertFormPresent();
        assertFormElementPresent(formElementName);
        getTestingEngine().setTextField(formElementName, value);
    }

    /**
     * Tell that the given alert boxe is expected.
     * 
     * @param message Message in the alert.
     */
    public void setExpectedJavaScriptAlert(String message) {
        try {
            getTestingEngine().setExpectedJavaScriptAlert(
                    new JavascriptAlert[] { new JavascriptAlert(message) });
        } catch (ExpectedJavascriptAlertException e) {
            Assert.fail("You previously tell that alert with message ["
                    + e.getAlertMessage()
                    + "] was expected, but nothing appeared.");
        }
    }

    /**
     * Tell that the given alert boxes are expected in the given order.
     * 
     * @param messages Messages in the alerts.
     */
    public void setExpectedJavaScriptAlert(String[] messages) {
        JavascriptAlert[] alerts = new JavascriptAlert[messages.length];
        for (int i = 0; i < messages.length; i++) {
            alerts[i] = new JavascriptAlert(messages[i]);
        }
        try {
            getTestingEngine().setExpectedJavaScriptAlert(alerts);
        } catch (ExpectedJavascriptAlertException e) {
            Assert.fail("You previously tell that alert with message ["
                    + e.getAlertMessage()
                    + "] was expected, but nothing appeared.");
        }
    }

    /**
     * Tell that the given confirm boxe is expected.
     * 
     * @param message Message in the confirm.
     * @param action Whether we should click on "OK" (true) or "Cancel" (false)
     */
    public void setExpectedJavaScriptConfirm(String message, boolean action) {
        try {
            getTestingEngine().setExpectedJavaScriptConfirm(
                    new JavascriptConfirm[] { new JavascriptConfirm(message,
                            action) });
        } catch (ExpectedJavascriptConfirmException e) {
            Assert.fail("You previously tell that confirm with message ["
                    + e.getConfirmMessage()
                    + "] was expected, but nothing appeared.");
        }
    }

    /**
     * Tell that the given confirm boxes are expected in the given order.
     * 
     * @param messages Messages in the confirms.
     * @param actions Whether we should click on "OK" (true) or "Cancel" (false)
     */
    public void setExpectedJavaScriptConfirm(String[] messages,
            boolean[] actions) {
        Assert.assertEquals(
                "You should give the same number of messages and actions",
                messages.length, actions.length);
        JavascriptConfirm[] confirms = new JavascriptConfirm[messages.length];
        for (int i = 0; i < messages.length; i++) {
            confirms[i] = new JavascriptConfirm(messages[i], actions[i]);
        }
        try {
            getTestingEngine().setExpectedJavaScriptConfirm(confirms);
        } catch (ExpectedJavascriptConfirmException e) {
            Assert.fail("You previously tell that confirm with message ["
                    + e.getConfirmMessage()
                    + "] was expected, but nothing appeared.");
        }
    }

    /**
     * Tell that the given prompt boxe is expected.
     * 
     * @param message Message in the prompt.
     * @param input What we should put in the prompt (null if user press Cancel)
     */
    public void setExpectedJavaScriptPrompt(String message, String input) {
        try {
            getTestingEngine().setExpectedJavaScriptPrompt(
                    new JavascriptPrompt[] { new JavascriptPrompt(message,
                            input) });
        } catch (ExpectedJavascriptPromptException e) {
            Assert.fail("You previously tell that prompt with message ["
                    + e.getPromptMessage()
                    + "] was expected, but nothing appeared.");
        }
    }

    /**
     * Tell that the given prompt boxes are expected in the given order.
     * 
     * @param messages Messages in the prompts.
     * @param inputs What we should put in the prompt (null if user press Cancel)
     */
    public void setExpectedJavaScriptPrompt(String[] messages, String[] inputs) {
        Assert.assertEquals(
                "You should give the same number of messages and inputs",
                messages.length, inputs.length);
        JavascriptPrompt[] prompts = new JavascriptPrompt[messages.length];
        for (int i = 0; i < messages.length; i++) {
            prompts[i] = new JavascriptPrompt(messages[i], inputs[i]);
        }
        try {
            getTestingEngine().setExpectedJavaScriptPrompt(prompts);
        } catch (ExpectedJavascriptPromptException e) {
            Assert.fail("You previously tell that prompt with message ["
                    + e.getPromptMessage()
                    + "] was expected, but nothing appeared.");
        }
    }

    public void assertImagePresent(String imageSrc, String imageAlt) {
        String xpath = "//img[@src=\"" + imageSrc + "\"";
        if (imageAlt!= null) {
            xpath += " and @alt=\"" + imageAlt + "\"";
        }
        xpath += "]";
        assertElementPresentByXPath(xpath);
    }

    /**
     * @see #assertImageValidAndStore(String, String, java.io.File)
     */
    public void assertImageValid(String imageSrc, String imageAlt) {
        validateImage(imageSrc, imageAlt, null);
    }

    /**
     * Asserts that the image with the given src and alt attribute values exist in the page and is an actual reachable
     * image, then saves it as png with the given file name.
     * 
     * @param imageSrc as it appears in the html page, i.e. relative to the current page.
     */
    public void assertImageValidAndStore(String imageSrc, String imageAlt,
            File out) {
        validateImage(imageSrc, imageAlt, out);
    }

    /**
     * @see #assertImageValidAndStore(String, String, java.io.File)
     */
    public Image getImage(String imageSrc, String imageAlt) {
        return validateImage(imageSrc, imageAlt, null);
    }

    private Image validateImage(String imageSrc, String imageAlt, File out) {
        assertImagePresent(imageSrc, imageAlt);
        URL imageUrl = null;
        try {
            imageUrl = createUrl(imageSrc, getTestingEngine().getPageURL());
        } catch (MalformedURLException e1) {
            Assert.fail(e1.getLocalizedMessage());
        }
        try {
            final InputStream imgStream = getTestingEngine().getInputStream(imageUrl);
            final BufferedImage img = ImageIO.read(imgStream);
            if (img == null) {
                Assert.fail("Could not load image from " + imageUrl);
            }
            if (out != null) {
                ImageIO.write(img, "png", out);
            }
            return img;
        } catch (IOException e) {
            Assert.fail("Could not load or save image from " + imageUrl);
        } catch (TestingEngineResponseException e) {
            Assert.fail("The server returns the code " + e.getHttpStatusCode());
        }
        throw new IllegalStateException();
    }

    protected boolean areFilesEqual(URL f1, URL f2) throws IOException {
        // read and compare bytes pair-wise
        InputStream i1 = f1.openStream();
        InputStream i2 = f2.openStream();
        if (f1 == null) {
            throw new IOException(f1.toString() + " can't be opened.");
        }
        if (f2 == null) {
            throw new IOException(f2.toString() + " can't be opened.");
        }
        int b1, b2;
        do {
            b1 = i1.read();
            b2 = i2.read();
        } while (b1 == b2 && b1 != -1 && b2 != -1);
        i1.close();
        i2.close();
        // true only if end of file is reached for both
        return (b1 == -1) && (b2 == -1);
    }

}
