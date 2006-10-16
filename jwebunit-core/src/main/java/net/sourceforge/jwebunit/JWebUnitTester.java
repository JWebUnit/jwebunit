/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.jwebunit.exception.AssertContainsException;
import net.sourceforge.jwebunit.exception.AssertEqualsException;
import net.sourceforge.jwebunit.exception.AssertMatchException;
import net.sourceforge.jwebunit.exception.AssertNotContainsException;
import net.sourceforge.jwebunit.exception.AssertNotMatchException;
import net.sourceforge.jwebunit.exception.ElementFoundException;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;
import net.sourceforge.jwebunit.exception.TestingEngineRegistryException;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.locator.HtmlButtonLocator;
import net.sourceforge.jwebunit.locator.HtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlTableLocator;
import net.sourceforge.jwebunit.util.ExceptionUtility;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Provides a high-level API for basic web application navigation and
 * validation. It supports use of a property file for web resources (a la
 * Struts), though a resource file for the app is not required.
 * 
 * @author Julien Henry
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class JWebUnitTester {
    private IJWebUnitDialog dialog = null;

    private TestContext testContext = null;

    /**
     * This is the testing engine key that the webtester will use to find the
     * correct testing engine from the registry.
     */
    private String testingEngineKey = null;

    public JWebUnitTester() {
        super();
    }

    /**
     * Provides access to the Dialog (testing engine) for subclasses - in case
     * functionality not yet wrappered required by test.
     * 
     * If the dialog is not explicitly set the jWebUnit framework will default
     * to using the orignal testing engine, which is, htmlunit.
     * 
     * @return IJWebUnitDialog instance used to wrapper htmlunit conversation.
     */
    protected IJWebUnitDialog getDialog() {
        if (dialog == null) {
            // defaulting to the HtmlUnitDialog implementation.
            dialog = initializeDialog();
        }
        return dialog;
    }

    /**
     * Initializes the IJWebUnitDialog when the dialog is null. This will
     * construct a new instance of the dialog based on the specified testing
     * engine key.
     */
    public IJWebUnitDialog initializeDialog() {
        IJWebUnitDialog theIJWebUnitDialog = null;
        String theTestingEngineKey = getTestingEngineKey();
        Class theClass;
        try {
            theClass = TestingEngineRegistry
                    .getTestingEngineClass(theTestingEngineKey);
        } catch (TestingEngineRegistryException e1) {
            throw new RuntimeException(e1);
        }
        try {
            theIJWebUnitDialog = (IJWebUnitDialog) theClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Can't Instantiate Testing Engine with class [" + theClass
                            + "] with key [" + theTestingEngineKey + "]."
                            + "\nError: "
                            + ExceptionUtility.stackTraceToString(e));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("IllegalAccessException using class ["
                    + theClass + "] with key [" + theTestingEngineKey + "]."
                    + "\nError: " + ExceptionUtility.stackTraceToString(e));
        }

        return theIJWebUnitDialog;
    }

    /**
     * Close the current conversation
     */
    public void closeBrowser() {
        try {
            getDialog().closeBrowser();
        } catch (TestingEngineResponseException aTestingEngineResponseException) {
            handleTestingEngineResponseException(aTestingEngineResponseException);
        }
    }

    /**
     * Close the current window
     */
    public void closeWindow() {
        getDialog().closeWindow();
    }

    public void setDialog(IJWebUnitDialog aIJWebUnitDialog) {
        dialog = aIJWebUnitDialog;
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
     * Allows setting an external test testContext class that might be extended
     * from TestContext. Example: setTestContext(new CompanyATestContext());
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
     * {@link TestContext#setBaseUrl(String) getTestContext().setBaseUrl(String)}
     * to define base URL. Absolute URL should start with "http://", "https://"
     * or "www.".
     * 
     * @param url
     *            absolute or relative URL (relative to base URL).
     */
    public void beginAt(String url) throws MalformedURLException {
        try {
            getDialog().beginAt(createUrl(url), testContext);
        } catch (TestingEngineResponseException aTestingEngineResponseException) {
            handleTestingEngineResponseException(aTestingEngineResponseException);
        }

    }

    private URL createUrl(String url) throws MalformedURLException {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return new URL(url);
        } else if (url.startsWith("www.")) {
            return new URL("http://" + url);
        } else {
            url = url.startsWith("/") ? url.substring(1) : url;
            return new URL(getTestContext().getBaseUrl() + url);
        }
    }

    /**
     * Return the value of a web resource based on its key. This translates to a
     * property file lookup with the locale based on the current TestContext.
     * 
     * @param key
     *            name of the web resource.
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
                    + "]." + "\nError: "
                    + ExceptionUtility.stackTraceToString(e));
        }
        return message;
    }

    // Assertions

    /**
     * Assert title of current html page in conversation matches an expected
     * value.
     * 
     * @param title
     *            expected title value
     */
    public void assertTitleEquals(String title) throws AssertEqualsException {
        if (!title.equals(getDialog().getPageTitle())) {
            throw new AssertEqualsException(title, getDialog().getPageTitle());
        }
    }

    /**
     * Assert title of current html page in conversation matches an expected
     * regexp.
     * 
     * @param regexp
     *            expected title regexp
     */
    public void assertTitleMatch(String regexp) throws RESyntaxException,
            AssertMatchException {
        RE re = null;
        re = new RE(regexp, RE.MATCH_SINGLELINE);
        if (!re.match(getDialog().getPageTitle())) {
            throw new AssertMatchException(regexp, getDialog().getPageTitle());
        }
    }

    /**
     * Assert title of current html page matches the value of a specified web
     * resource.
     * 
     * @param titleKey
     *            web resource key for title
     */
    public void assertTitleEqualsKey(String titleKey)
            throws AssertEqualsException {
        if (!getMessage(titleKey).equals(getDialog().getPageTitle())) {
            throw new AssertEqualsException(getMessage(titleKey), getDialog()
                    .getPageTitle());
        }
    }

    /**
     * Assert that a web resource's value is present.
     * 
     * @param key
     *            web resource name
     */
    public void assertKeyPresent(String key) throws AssertContainsException {
        assertTextPresent(getMessage(key));
    }

    /**
     * Assert that supplied text is present in page (visible text).
     * 
     * @param text
     */
    public void assertTextPresent(String text) throws AssertContainsException {
        if ((getDialog().getPageText().indexOf(text) < 0))
            throw new AssertContainsException(text, getDialog().getPageText());
    }

    /**
     * Assert that supplied regexp is matched.
     * 
     * @param regexp
     */
    public void assertMatch(String regexp) throws AssertMatchException {
        RE re = getRE(regexp);
        if (!re.match(getDialog().getPageText()))
            throw new AssertMatchException(regexp, getDialog().getPageText());
    }

    /**
     * Assert that a web resource's value is not present.
     * 
     * @param key
     *            web resource name
     */
    public void assertKeyNotPresent(String key) throws AssertContainsException {
        assertTextNotPresent(getMessage(key));
    }

    /**
     * Assert that supplied text is not present.
     * 
     * @param text
     */
    public void assertTextNotPresent(String text)
            throws AssertContainsException {
        if (getDialog().getPageText().indexOf(text) >= 0)
            throw new AssertContainsException(text, getDialog().getPageText());
    }

    /**
     * Assert that supplied regexp is not present.
     * 
     * @param regexp
     */
    public void assertNoMatch(String regexp) throws AssertMatchException {
        RE re = getRE(regexp);
        if (re.match(getDialog().getPageText()))
            throw new AssertMatchException(regexp, getDialog().getPageText());
    }

    /**
     * 
     * @param tableSummaryNameOrId
     * @return Object that represent a html table in a way independent from
     *         plugin.
     */
    public Table getTable(HtmlTableLocator tableLocator) throws ElementNotFoundException {
        return getDialog().getTable(tableLocator);
    }

    /**
     * Assert element is present.
     * 
     */
    public void assertElementPresent(HtmlElementLocator locator)
            throws ElementNotFoundException {
        if (!getDialog().hasElement(locator))
            throw new ElementNotFoundException(locator);
    }

    /**
     * Assert element is not present.
     * 
     */
    public void assertElementNotPresent(HtmlElementLocator locator)
            throws ElementFoundException {
        if (getDialog().hasElement(locator))
            throw new ElementFoundException(locator);
    }

    /**
     * Assert that the value of a given web resource is present in a specific
     * table.
     * 
     * @param key
     *            web resource name
     */
    public void assertKeyInTable(HtmlTableLocator tableLocator, String key)
            throws ElementNotFoundException, AssertContainsException {
        assertTextInTable(tableLocator, getMessage(key));
    }

    /**
     * Assert that supplied text is present in a specific table.
     * 
     * @param text
     */
    public void assertTextInTable(HtmlTableLocator tableLocator, String text)
            throws ElementNotFoundException, AssertContainsException {
        if (!getDialog().getTable(tableLocator).hasText(text)) {
            throw new AssertContainsException(text, getDialog().getTable(
                    tableLocator).toString());
        }
    }

    /**
     * Assert that supplied regexp is matched in a specific table.
     * 
     * @param regexp
     */
    public void assertMatchInTable(HtmlTableLocator tableLocator, String regexp)
            throws ElementNotFoundException, AssertMatchException {
        Table table = getDialog().getTable(tableLocator);
        if (!table.hasMatch(regexp)) {
            throw new AssertMatchException(regexp, table.toString());
        }
    }

    /**
     * Assert that the values of a set of web resources are all present in a
     * specific table.
     * 
     * @param keys
     *            Array of web resource names.
     */
    public void assertKeysInTable(HtmlTableLocator tableLocator, String[] keys)
            throws ElementNotFoundException, AssertContainsException {
        for (int i = 0; i < keys.length; i++) {
            assertKeyInTable(tableLocator, keys[i]);
        }
    }

    /**
     * Assert that a set of text values are all present in a specific table.
     * 
     * @param text
     *            Array of expected text values.
     */
    public void assertTextInTable(HtmlTableLocator tableLocator, String[] text)
            throws ElementNotFoundException, AssertContainsException {
        for (int i = 0; i < text.length; i++) {
            assertTextInTable(tableLocator, text[i]);
        }
    }

    /**
     * Assert that a set of regexp values are all matched in a specific table.
     * 
     * @param tableSummaryOrId
     *            summary, name or id attribute value of table
     * @param text
     *            Array of expected regexps to match.
     */
    public void assertMatchInTable(HtmlTableLocator tableLocator,
            String[] regexp) throws ElementNotFoundException,
            AssertMatchException {
        for (int i = 0; i < regexp.length; i++) {
            assertMatchInTable(tableLocator, regexp[i]);
        }
    }

    /**
     * Assert that the value of a given web resource is not present in a
     * specific table.
     * 
     * @param key
     *            web resource name
     */
    public void assertKeyNotInTable(HtmlTableLocator tableLocator, String key)
            throws AssertNotContainsException, ElementNotFoundException {
        assertTextNotInTable(tableLocator, getMessage(key));
    }

    /**
     * Assert that supplied text is not present in a specific table.
     * 
     * @param tableSummaryNameOrId
     *            summary, name or id attribute value of table
     * @param text
     */
    public void assertTextNotInTable(HtmlTableLocator tableLocator, String text)
            throws ElementNotFoundException, AssertNotContainsException {
        assertElementPresent(tableLocator);
        Table table = getDialog().getTable(tableLocator);
        if (table.hasText(text)) {
            throw new AssertNotContainsException(text, table.toString());
        }
    }

    /**
     * Assert that none of a set of text values are present in a specific table.
     * 
     * @param tableSummaryNameOrId
     *            summary, name or id attribute value of table
     * @param text
     *            Array of text values
     */
    public void assertTextNotInTable(HtmlTableLocator tableLocator,
            String[] text) throws ElementNotFoundException,
            AssertNotContainsException {
        for (int i = 0; i < text.length; i++) {
            assertTextNotInTable(tableLocator, text[i]);
        }
    }

    /**
     * Assert that supplied regexp is not present in a specific table.
     * 
     * @param tableSummaryNameOrId
     *            summary, name or id attribute value of table
     * @param text
     */
    public void assertNoMatchInTable(HtmlTableLocator tableLocator,
            String regexp) throws ElementNotFoundException,
            AssertNotMatchException {
        assertElementPresent(tableLocator);
        Table table = getDialog().getTable(tableLocator);
        if (table.hasMatch(regexp)) {
            throw new AssertNotMatchException(regexp, table.toString());
        }
    }

    /**
     * Assert that none of a set of regexp values are present in a specific
     * table.
     * 
     * @param text
     *            Array of text values
     */
    public void assertNoMatchInTable(HtmlTableLocator tableLocator,
            String[] regexp) throws AssertNotMatchException,
            ElementNotFoundException {
        for (int i = 0; i < regexp.length; i++) {
            assertNoMatchInTable(tableLocator, regexp[i]);
        }
    }

    /**
     * Assert that a specific table matches an ExpectedTable.
     * 
     * @param expectedTable
     *            represents expected values (colspan supported).
     */
    public void assertTableEquals(HtmlTableLocator tableLocator,
            Table expectedTable) throws ElementNotFoundException, AssertEqualsException {
        getDialog().getTable(tableLocator).assertEquals(expectedTable);
    }

    /**
     * Assert that a specific table matches a matrix of supplied text values.
     * 
     * @param expectedCellValues
     *            double dimensional array of expected values
     */
    public void assertTableEquals(HtmlTableLocator tableLocator,
            String[][] expectedCellValues) throws ElementNotFoundException, AssertEqualsException {
        getDialog().getTable(tableLocator).assertEquals(
                new Table(expectedCellValues));
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of
     * supplied text values.
     * 
     * @param startRow
     *            index of start row for comparison
     * @param expectedTable
     *            represents expected values (colspan and rowspan supported).
     */
    public void assertTableRowsEqual(HtmlTableLocator tableLocator,
            int startRow, Table expectedTable) throws ElementNotFoundException, AssertEqualsException {
        getDialog().getTable(tableLocator).assertSubTableEquals(startRow,
                expectedTable);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of
     * supplied text values.
     * 
     * @param startRow
     *            index of start row for comparison
     * @param expectedTable
     *            represents expected values (colspan and rowspan supported).
     */
    public void assertTableRowsEqual(HtmlTableLocator tableLocator,
            int startRow, String[][] expectedTable)
            throws ElementNotFoundException, AssertEqualsException {
        getDialog().getTable(tableLocator).assertSubTableEquals(startRow,
                new Table(expectedTable));
    }

    /**
     * Assert that the number of rows for a specific table equals expected
     * value.
     * 
     * @param tableSummaryNameOrId
     *            summary, name or id attribute value of table
     * @param expectedRowCount
     *            expected row count.
     */
    public void assertTableRowCountEquals(HtmlTableLocator tableLocator,
            int expectedRowCount) throws ElementNotFoundException,
            AssertEqualsException {
        assertElementPresent(tableLocator);
        int actualRowCount = getDialog().getTable(tableLocator).getRowCount();
        if (actualRowCount != expectedRowCount) {
            throw new AssertEqualsException(expectedRowCount, actualRowCount);
        }
    }

    /**
     * Assert that a specific table matches an ExpectedTable.
     * 
     * @param expectedTable
     *            represents expected regexps (colspan/rowspan supported).
     */
    public void assertTableMatch(HtmlTableLocator tableLocator,
            Table expectedTable) throws ElementNotFoundException, AssertMatchException {
        getDialog().getTable(tableLocator).assertMatch(expectedTable);
    }

    /**
     * Assert that a specific table matches a matrix of supplied regexps.
     * 
     * @param expectedCellValues
     *            double dimensional array of expected regexps
     */
    public void assertTableMatch(HtmlTableLocator tableLocator,
            String[][] expectedCellValues) throws ElementNotFoundException, AssertMatchException {
        getDialog().getTable(tableLocator).assertMatch(
                new Table(expectedCellValues));
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of
     * supplied regexps.
     * 
     * @param startRow
     *            index of start row for comparison
     * @param expectedTable
     *            represents expected regexps (colspan and rowspan supported).
     */
    public void assertTableRowsMatch(HtmlTableLocator tableLocator,
            int startRow, Table expectedTable) throws ElementNotFoundException, AssertMatchException {
        getDialog().getTable(tableLocator).assertSubTableMatch(startRow,
                expectedTable);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of
     * supplied regexps.
     * 
     * @param tableSummaryOrId
     *            summary or id attribute value of table
     * @param startRow
     *            index of start row for comparison
     * @param expectedTable
     *            represents expected regexps (colspan and rowspan not
     *            supported).
     */
    public void assertTableRowsMatch(HtmlTableLocator tableLocator,
            int startRow, String[][] expectedTable) throws ElementNotFoundException, AssertMatchException {
        getDialog().getTable(tableLocator).assertSubTableMatch(startRow,
                new Table(expectedTable));
    }

    public void assertAttributEquals(HtmlElementLocator locator,
            String attribut, String value) throws ElementNotFoundException,
            AssertEqualsException {
        String actual = getDialog().getAttributeValue(locator, attribut);
        if (!actual.equals(value)) {
            throw new AssertEqualsException(value, actual);
        }
    }

    public void assertAttributMatch(HtmlElementLocator locator,
            String attribut, String regexp) throws ElementNotFoundException,
            AssertMatchException {
        String actual = getDialog().getAttributeValue(locator, attribut);
        RE re = getRE(regexp);
        if (!re.match(actual)) {
            throw new AssertMatchException(regexp, actual);
        }
    }

    // /**
    // * Assert that a specific form element matches an expected regexp.
    // *
    // * @param formElementName
    // * @param regexp
    // */
    // public void assertFormElementMatch(String formElementName, String regexp)
    // {
    // assertFormElementPresent(formElementName);
    // RE re = null;
    // try {
    // re = new RE(regexp, RE.MATCH_SINGLELINE);
    // } catch (RESyntaxException e) {
    // Assert.fail(e.toString());
    // }
    // Assert.assertTrue("Unable to match [" + regexp + "] in form element \""
    // + formElementName + "\"", re.match(getDialog()
    // .getFormParameterValue(formElementName)));
    // }
    //
    // /**
    // * Assert that a form element had no value / is empty.
    // *
    // * @param formElementName
    // */
    // public void assertFormElementEmpty(String formElementName) {
    // assertFormElementPresent(formElementName);
    // Assert.assertEquals("", getDialog().getFormParameterValue(
    // formElementName));
    // }
    //
    //
    // /**
    // * Assert that an input hidden element with name
    // * <code>formElementName</code> has the <code>expectedValue</code>
    // * value.
    // *
    // * @param formElementName
    // * the value of the name attribute of the element
    // * @param expectedValue
    // * the expected value of the given input element
    // */
    // public void assertHiddenFieldPresent(String formElementName,
    // String expectedValue) {
    // assertFormElementPresent(formElementName);
    // Assert.assertEquals(expectedValue, getDialog().getHiddenFieldValue(
    // formElementName));
    // }
    //
    // /**
    // * Assert that a specific checkbox is selected.
    // *
    // * @param checkBoxName
    // */
    // public void assertCheckboxSelected(String checkBoxName) {
    // assertCheckboxPresent(checkBoxName);
    // if (!getDialog().isCheckboxSelected(checkBoxName)) {
    // Assert.fail("Checkbox with name [" + checkBoxName
    // + "] was not found selected.");
    // }
    // }
    //
    // /**
    // * Assert that a specific checkbox is not selected.
    // *
    // * @param checkBoxName
    // */
    // public void assertCheckboxNotSelected(String checkBoxName) {
    // assertCheckboxPresent(checkBoxName);
    // if (getDialog().isCheckboxSelected(checkBoxName)) {
    // Assert.fail("Checkbox with name [" + checkBoxName
    // + "] was found selected.");
    // }
    // }
    //
    // /**
    // * Assert that a specific option is present in a radio group.
    // *
    // * @param name
    // * radio group name.
    // * @param radioOption
    // * option to test for.
    // */
    // public void assertRadioOptionPresent(String name, String radioOption) {
    // assertFormElementPresent(name);
    // if (!getDialog().hasRadioOption(name, radioOption)) {
    // Assert.fail("Unable to find option [" + radioOption
    // + "] in radio group [" + name + "]");
    // }
    // }
    //
    // /**
    // * Assert that a specific option is not present in a radio group.
    // *
    // * @param name
    // * radio group name.
    // * @param radioOption
    // * option to test for.
    // */
    // public void assertRadioOptionNotPresent(String name, String radioOption)
    // {
    // assertFormElementPresent(name);
    // if (getDialog().hasRadioOption(name, radioOption))
    // Assert.fail("Found option [" + radioOption + "] in radio group ["
    // + name + "]");
    // }
    //
    // /**
    // * Assert that a specific option is selected in a radio group.
    // *
    // * @param name
    // * radio group name.
    // * @param radioOption
    // * option to test for selection.
    // */
    // public void assertRadioOptionSelected(String name, String radioOption) {
    // assertFormElementPresent(name);
    // assertFormElementEquals(name, radioOption);
    // }
    //
    // /**
    // * Assert that a specific option is not selected in a radio group.
    // *
    // * @param name
    // * radio group name.
    // * @param radioOption
    // * option to test for selection.
    // */
    // public void assertRadioOptionNotSelected(String name, String radioOption)
    // {
    // assertFormElementPresent(name);
    // Assert.assertTrue("Radio option " + radioOption + " is not selected",
    // !radioOption.equals(getDialog().getFormParameterValue(name)));
    // }
    //
    // /**
    // * Assert that given options are present in a select box (by label).
    // *
    // * @param selectName
    // * name of the select element.
    // * @param optionLabels
    // * option labels.
    // */
    // public void assertSelectOptionsPresent(String selectName,
    // String[] optionLabels) {
    // assertFormElementPresent(selectName);
    // for (int i = 0; i < optionLabels.length; i++)
    // Assert.assertTrue("Option [" + optionLabels[i]
    // + "] not found in select element " + selectName,
    // getDialog().hasSelectOption(selectName, optionLabels[i]));
    // }
    //
    // /**
    // * Assert that a specific option is present in a select box (by label).
    // *
    // * @param selectName
    // * name of the select element.
    // * @param optionLabel
    // * option label.
    // */
    // public void assertSelectOptionPresent(String selectName, String
    // optionLabel) {
    // assertSelectOptionsPresent(selectName, new String[] {optionLabel});
    // }
    //
    // /**
    // * Assert that given options are present in a select box (by value).
    // *
    // * @param selectName
    // * name of the select element.
    // * @param optionValues
    // * option labels.
    // */
    // public void assertSelectOptionValuesPresent(String selectName,
    // String[] optionValues) {
    // assertFormElementPresent(selectName);
    // for (int i = 0; i < optionValues.length; i++)
    // Assert.assertTrue("Option [" + optionValues[i]
    // + "] not found in select element " + selectName,
    // getDialog().hasSelectOptionValue(selectName,
    // optionValues[i]));
    // }
    //
    // /**
    // * Assert that a specific option is present in a select box (by value).
    // *
    // * @param selectName
    // * name of the select element.
    // * @param optionValue
    // * option value.
    // */
    // public void assertSelectOptionValuePresent(String selectName,
    // String optionValue) {
    // assertSelectOptionValuesPresent(selectName, new String[] {optionValue});
    // }
    //
    // public void assertSelectOptionValueNotPresent(String selectName,
    // String optionValue) {
    // try {
    // assertSelectOptionValuePresent(selectName, optionValue);
    // } catch (AssertionFailedError e) {
    // return;
    // }
    // Assert.fail("Option value" + optionValue + " found in select element "
    // + selectName + " when not expected.");
    // }
    //
    // /**
    // * Assert that a specific option is not present in a select box.
    // *
    // * @param selectName
    // * name of the select element.
    // * @param expectedOption
    // * option label.
    // */
    // public void assertSelectOptionNotPresent(String selectName,
    // String optionLabel) {
    // try {
    // assertSelectOptionPresent(selectName, optionLabel);
    // } catch (AssertionFailedError e) {
    // return;
    // }
    // Assert.fail("Option " + optionLabel + " found in select element "
    // + selectName + " when not expected.");
    // }
    //
    // /**
    // * Assert that the display values of a select element's options match a
    // * given array of strings.
    // *
    // * @param selectName
    // * name of the select element.
    // * @param expectedOptions
    // * expected labels for the select box.
    // */
    // public void assertSelectOptionsEqual(String selectName,
    // String[] expectedOptions) {
    // assertFormElementPresent(selectName);
    // assertArraysEqual(expectedOptions, getOptionsFor(selectName));
    // }
    //
    // /**
    // * Assert that the display values of a select element's options do not
    // match
    // * a given array of strings.
    // *
    // * @param selectName
    // * name of the select element.
    // * @param expectedOptions
    // * expected display values for the select box.
    // */
    // public void assertSelectOptionsNotEqual(String selectName,
    // String[] expectedOptions) {
    // assertFormElementPresent(selectName);
    // try {
    // assertSelectOptionsEqual(selectName, expectedOptions);
    // } catch (AssertionFailedError e) {
    // return;
    // }
    // Assert.fail("Options not expected to be equal");
    // }
    //
    // /**
    // * Assert that the values of a select element's options match a given
    // array
    // * of strings.
    // *
    // * @param selectName
    // * name of the select element.
    // * @param expectedValues
    // * expected values for the select box.
    // */
    // public void assertSelectOptionValuesEqual(String selectName,
    // String[] expectedValues) {
    // assertFormElementPresent(selectName);
    // assertArraysEqual(expectedValues, getDialog().getSelectOptionValues(
    // selectName));
    //
    // }
    //
    // /**
    // * Assert that the values of a select element's options do not match a
    // given
    // * array of strings.
    // *
    // * @param selectName
    // * name of the select element.
    // * @param optionValues
    // * expected values for the select box.
    // */
    // public void assertSelectOptionValuesNotEqual(String selectName,
    // String[] optionValues) {
    // assertFormElementPresent(selectName);
    // try {
    // assertSelectOptionValuesEqual(selectName, optionValues);
    // } catch (AssertionFailedError e) {
    // return;
    // }
    // Assert.fail("Values not expected to be equal");
    // }
    //
    // /**
    // * Assert that the currently selected display label(s) of a select box
    // * matches given label(s).
    // *
    // * @param selectName
    // * name of the select element.
    // * @param labels
    // * expected display label(s) of the selected option.
    // */
    // public void assertSelectedOptionsEqual(String selectName, String[]
    // labels) {
    // assertFormElementPresent(selectName);
    // Assert.assertEquals(labels.length, getDialog().getSelectedOptions(
    // selectName).length);
    // for (int i = 0; i < labels.length; i++)
    // Assert.assertEquals(labels[i], getDialog()
    // .getSelectOptionLabelForValue(selectName,
    // getDialog().getSelectedOptions(selectName)[i]));
    // }
    //
    // public void assertSelectedOptionEquals(String selectName, String option)
    // {
    // assertSelectedOptionsEqual(selectName, new String[] {option});
    // }
    //
    // /**
    // * Assert that the currently selected value(s) of a select box matches
    // given
    // * value(s).
    // *
    // * @param selectName
    // * name of the select element.
    // * @param values
    // * expected value(s) of the selected option.
    // */
    // public void assertSelectedOptionValuesEqual(String selectName,
    // String[] values) {
    // assertFormElementPresent(selectName);
    // Assert.assertEquals(values.length, getDialog().getSelectedOptions(
    // selectName).length);
    // for (int i = 0; i < values.length; i++)
    // Assert.assertEquals(values[i], getDialog().getSelectedOptions(
    // selectName)[i]);
    // }
    //
    // /**
    // * Assert that the currently selected value of a select box matches given
    // * value.
    // *
    // * @param selectName
    // * name of the select element.
    // * @param value
    // * expected value of the selected option.
    // */
    // public void assertSelectedOptionValueEquals(String selectName, String
    // value) {
    // assertSelectedOptionValuesEqual(selectName, new String[] {value});
    // }
    //
    // /**
    // * Assert that the currently selected display value(s) of a select box
    // * matches a given value(s).
    // *
    // * @param selectName
    // * name of the select element.
    // * @param regexps
    // * expected display value of the selected option.
    // */
    // public void assertSelectedOptionsMatch(String selectName, String[]
    // regexps) {
    // assertFormElementPresent(selectName);
    // Assert.assertEquals(regexps.length, getDialog().getSelectedOptions(
    // selectName).length);
    // for (int i = 0; i < regexps.length; i++) {
    // RE re = getRE(regexps[i]);
    // Assert.assertTrue("Unable to match [" + regexps[i]
    // + "] in option \""
    // + getDialog().getSelectedOptions(selectName)[i] + "\"", re
    // .match(getDialog().getSelectedOptions(selectName)[i]));
    // }
    // }
    //
    // public void assertSelectedOptionMatches(String selectName, String regexp)
    // {
    // assertSelectedOptionsMatch(selectName, new String[] {regexp});
    // }
    //
    // /**
    // * Assert that a submit button is present.
    // *
    // */
    // public void assertSubmitButtonPresent() {
    // assertFormPresent();
    // Assert.assertTrue("no submit button found.", getDialog()
    // .hasSubmitButton());
    // }
    //
    // /**
    // * Assert that a submit button with a given name is present.
    // *
    // * @param buttonName
    // */
    // public void assertSubmitButtonPresent(String buttonName) {
    // assertFormPresent();
    // Assert.assertTrue("Submit Button [" + buttonName + "] not found.",
    // getDialog().hasSubmitButton(buttonName));
    // }
    //
    // /**
    // * Assert that no submit button is present in the current form.
    // *
    // * @param buttonName
    // */
    // public void assertSubmitButtonNotPresent() {
    // assertFormPresent();
    // Assert.assertFalse("Submit Button found.", getDialog()
    // .hasSubmitButton());
    // }
    //
    // /**
    // * Assert that a submit button with a given name is not present.
    // *
    // * @param buttonName
    // */
    // public void assertSubmitButtonNotPresent(String buttonName) {
    // assertFormPresent();
    // Assert.assertFalse("Submit Button [" + buttonName + "] found.",
    // getDialog().hasSubmitButton(buttonName));
    // }
    //
    // /**
    // * Assert that a submit button with a given name and value is present.
    // *
    // * @param buttonName
    // * @param buttonValue
    // */
    // public void assertSubmitButtonPresent(String buttonName, String
    // buttonValue) {
    // assertFormPresent();
    // Assert.assertTrue("Submit Button [" + buttonName + "] with value ["
    // + buttonValue + "] not found.", getDialog().hasSubmitButton(
    // buttonName, buttonValue));
    // }
    //
    // /**
    // * Assert that a reset button is present.
    // *
    // */
    // public void assertResetButtonPresent() {
    // assertFormPresent();
    // Assert.assertTrue("no reset button found.", getDialog()
    // .hasResetButton());
    // }
    //
    // /**
    // * Assert that a reset button with a given name is present.
    // *
    // * @param buttonName
    // */
    // public void assertResetButtonPresent(String buttonName) {
    // assertFormPresent();
    // Assert.assertTrue("Reset Button [" + buttonName + "] not found.",
    // getDialog().hasResetButton(buttonName));
    // }
    //
    // /**
    // * Assert that no reset button is present in the current form.
    // *
    // * @param buttonName
    // */
    // public void assertResetButtonNotPresent() {
    // assertFormPresent();
    // Assert.assertFalse("Reset Button found.", getDialog().hasResetButton());
    // }
    //
    // /**
    // * Assert that a reset button with a given name is not present.
    // *
    // * @param buttonName
    // */
    // public void assertResetButtonNotPresent(String buttonName) {
    // assertFormPresent();
    // Assert.assertFalse("Reset Button [" + buttonName + "] found.",
    // getDialog().hasResetButton(buttonName));
    // }
    //
    // /**
    // * Assert that a button with a given id is present in the current window.
    // *
    // * @param buttonId
    // */
    // public void assertButtonPresent(String buttonId) {
    // assertFormPresent();
    // Assert.assertTrue("Button [" + buttonId + "] not found.", getDialog()
    // .hasButton(buttonId));
    // }
    //
    /**
     * Assert that a button with a given text is present in the current window.
     * 
     * @param text
     */
    public void assertButtonPresentWithText(String text)
            throws ElementNotFoundException {
        HtmlElementLocator l = new HtmlButtonLocator();
        int count = getDialog().getCount(l);
        for (int i = 0; i < count; i++) {
            HtmlElementLocator li = new HtmlButtonLocator(count);
            try {
                if (getDialog().getText(li).equals(text)) {
                    return;
                }
            } catch (ElementNotFoundException e) {
                continue;
            }
        }
        throw new ElementNotFoundException(l);
    }

    /**
     * Assert that a button with a given text is not present in the current
     * window.
     * 
     * @param text
     */
    public void assertButtonNotPresentWithText(String text)
            throws ElementFoundException {
        HtmlElementLocator l = new HtmlButtonLocator();
        int count;
        try {
            count = getDialog().getCount(l);
        } catch (ElementNotFoundException e1) {
            return;
        }
        for (int i = 0; i < count; i++) {
            HtmlElementLocator li = new HtmlButtonLocator(count);
            try {
                if (getDialog().getText(li).equals(text)) {
                    throw new ElementFoundException(li);
                }
            } catch (ElementNotFoundException e) {
                continue;
            }
        }
        return;
    }

    // /**
    // * Assert that a button with a given id is not present in the current
    // * window.
    // *
    // * @param buttonId
    // */
    // public void assertButtonNotPresent(String buttonId) {
    // assertFormPresent();
    // Assert.assertFalse(
    // "Button [" + buttonId + "] found when not expected.",
    // getDialog().hasButton(buttonId));
    // }
    //
    // /**
    // * Assert that a link with a given id is present in the response.
    // *
    // * @param linkId
    // */
    // public void assertLinkPresent(String linkId) {
    // Assert.assertTrue("Unable to find link with id [" + linkId + "]",
    // getDialog().hasLink(linkId));
    // }
    //
    // /**
    // * Assert that no link with the given id is present in the response.
    // *
    // * @param linkId
    // */
    // public void assertLinkNotPresent(String linkId) {
    // Assert.assertTrue("link with id [" + linkId + "] found in response",
    // !getDialog().hasLink(linkId));
    // }
    //
    // /**
    // * Assert that a link containing the supplied text is present.
    // *
    // * @param linkText
    // */
    // public void assertLinkPresentWithText(String linkText) {
    // Assert.assertTrue("Link with text [" + linkText
    // + "] not found in response.", getDialog().hasLinkWithText(
    // linkText, 0));
    // }
    //
    // /**
    // * Assert that no link containing the supplied text is present.
    // *
    // * @param linkText
    // */
    // public void assertLinkNotPresentWithText(String linkText) {
    // Assert.assertTrue("Link with text [" + linkText
    // + "] found in response.", !getDialog().hasLinkWithText(
    // linkText, 0));
    // }
    //
    // /**
    // * Assert that a link containing the supplied text is present.
    // *
    // * @param linkText
    // * @param index
    // * The 0-based index, when more than one link with the same text
    // * is expected.
    // */
    // public void assertLinkPresentWithText(String linkText, int index) {
    // Assert.assertTrue("Link with text [" + linkText + "] and index ["
    // + index + "] not found in response.", getDialog()
    // .hasLinkWithText(linkText, index));
    // }
    //
    // /**
    // * Assert that no link containing the supplied text is present.
    // *
    // * @param linkText
    // * @param index
    // * The 0-based index, when more than one link with the same text
    // * is expected.
    // */
    // public void assertLinkNotPresentWithText(String linkText, int index) {
    // Assert.assertTrue("Link with text [" + linkText + "] and index "
    // + index + " found in response.", !getDialog().hasLinkWithText(
    // linkText, index));
    // }
    //
    // // BEGIN RFE 996031...
    //
    // /**
    // * Assert that a link containing the Exact text is present.
    // *
    // * @param linkText
    // */
    // public void assertLinkPresentWithExactText(String linkText) {
    // Assert.assertTrue("Link with Exact text [" + linkText
    // + "] not found in response.", getDialog().hasLinkWithExactText(
    // linkText, 0));
    // }
    //
    // /**
    // * Assert that no link containing the Exact text is present.
    // *
    // * @param linkText
    // */
    // public void assertLinkNotPresentWithExactText(String linkText) {
    // Assert.assertTrue("Link with Exact text [" + linkText
    // + "] found in response.", !getDialog().hasLinkWithExactText(
    // linkText, 0));
    // }
    //
    // /**
    // * Assert that a link containing the Exact text is present.
    // *
    // * @param linkText
    // * @param index
    // * The 0-based index, when more than one link with the same text
    // * is expected.
    // */
    // public void assertLinkPresentWithExactText(String linkText, int index) {
    // Assert.assertTrue("Link with Exact text [" + linkText + "] and index ["
    // + index + "] not found in response.", getDialog()
    // .hasLinkWithExactText(linkText, index));
    // }
    //
    // /**
    // * Assert that no link containing the Exact text is present.
    // *
    // * @param linkText
    // * @param index
    // * The 0-based index, when more than one link with the same text
    // * is expected.
    // */
    // public void assertLinkNotPresentWithExactText(String linkText, int index)
    // {
    // Assert.assertTrue("Link with Exact text [" + linkText + "] and index "
    // + index + " found in response.", !getDialog()
    // .hasLinkWithExactText(linkText, index));
    // }
    //
    // // END RFE 996031...
    //
    // /**
    // * Assert that a link containing a specified image is present.
    // *
    // * @param imageFileName
    // * A suffix of the image's filename; for example, to match
    // * <tt>"images/my_icon.png"</tt>, you could just pass in
    // * <tt>"my_icon.png"</tt>.
    // */
    // public void assertLinkPresentWithImage(String imageFileName) {
    // Assert.assertTrue("Link with image file [" + imageFileName
    // + "] not found in response.", getDialog().hasLinkWithImage(
    // imageFileName, 0));
    // }
    //
    // /**
    // * Assert that a link containing a specified image is not present.
    // *
    // * @param imageFileName
    // * A suffix of the image's filename; for example, to match
    // * <tt>"images/my_icon.png"</tt>, you could just pass in
    // * <tt>"my_icon.png"</tt>.
    // */
    // public void assertLinkNotPresentWithImage(String imageFileName) {
    // Assert.assertTrue("Link with image file [" + imageFileName
    // + "] found in response.", !getDialog().hasLinkWithImage(
    // imageFileName, 0));
    // }
    //
    // /**
    // * Assert that an element with a given id is present.
    // *
    // * @param anID
    // * element id to test for.
    // */
    // public void assertElementPresent(String anID) {
    // Assert.assertTrue("Unable to locate element with id \"" + anID + "\"",
    // getDialog().hasElement(anID));
    // }
    //
    // /**
    // * Assert that an element with a given id is not present.
    // *
    // * @param anID
    // * element id to test for.
    // */
    // public void assertElementNotPresent(String anID) {
    // Assert.assertFalse("Located element with id \"" + anID + "\"",
    // getDialog().hasElement(anID));
    // }
    //
    // /**
    // * Assert that an element with a given xpath is present.
    // *
    // * @param xpath
    // * element xpath to test for.
    // */
    // public void assertElementPresentByXPath(String xpath) {
    // Assert.assertTrue("Unable to locate element with xpath \"" + xpath
    // + "\"", getDialog().hasElementByXPath(xpath));
    // }
    //
    // /**
    // * Assert that an element with a given xpath is not present.
    // *
    // * @param xpath
    // * element xpath to test for.
    // */
    // public void assertElementNotPresentByXPath(String xpath) {
    // Assert.assertFalse("Located element with xpath \"" + xpath + "\"",
    // getDialog().hasElementByXPath(xpath));
    // }
    //
    // /**
    // * Assert that a given element contains specific text.
    // *
    // * @param elementID
    // * id of element to be inspected.
    // * @param text
    // * to check for.
    // */
    // public void assertTextInElement(String elementID, String text) {
    // Assert.assertTrue("Unable to locate element with id \"" + elementID
    // + "\"", getDialog().hasElement(elementID));
    // Assert.assertTrue("Unable to locate [" + text + "] in element \""
    // + elementID + "\"", getDialog()
    // .isTextInElement(elementID, text));
    // }
    //
    // public void assertTextNotInElement(String elementID, String text) {
    // assertElementPresent(elementID);
    // Assert.assertTrue("Unable to locate element with id \"" + elementID
    // + "\"", getDialog().hasElement(elementID));
    // Assert.assertFalse("Text [" + text + "] found in element [" + elementID
    // + "] when not expected", getDialog().isTextInElement(elementID,
    // text));
    // }
    //
    // /**
    // * Assert that a given element matches a specific regexp.
    // *
    // * @param elementID
    // * id of element to be inspected.
    // * @param regexp
    // * to match.
    // */
    // public void assertMatchInElement(String elementID, String regexp) {
    // Assert.assertTrue("Unable to locate element with id \"" + elementID
    // + "\"", getDialog().hasElement(elementID));
    // Assert.assertTrue("Unable to match [" + regexp + "] in element \""
    // + elementID + "\"", getDialog().isMatchInElement(elementID,
    // regexp));
    // }
    //
    // /**
    // * Assert that a given element does not match a specific regexp.
    // *
    // * @param elementID
    // * id of element to be inspected.
    // * @param regexp
    // * to match.
    // */
    // public void assertNoMatchInElement(String elementID, String regexp) {
    // assertElementPresent(elementID);
    // Assert.assertTrue("Unable to locate element with id \"" + elementID
    // + "\"", getDialog().hasElement(elementID));
    // Assert.assertFalse("Regexp [" + regexp + "] matched in element ["
    // + elementID + "] when not expected", getDialog()
    // .isMatchInElement(elementID, regexp));
    // }
    //
    // /**
    // * Assert that a window with the given name is open.
    // *
    // * @param windowName
    // */
    // public void assertWindowPresent(String windowName) {
    // Assert.assertTrue("Unable to locate window [" + windowName + "].",
    // getDialog().hasWindow(windowName));
    // }
    //
    // /**
    // * Assert that a window with the given ID is open.
    // *
    // * @param windowID
    // * Javascript window ID.
    // */
    // public void assertWindowPresent(int windowID) {
    // Assert.assertTrue("There is no window with index [" + windowID + "].",
    // getDialog().getWindowCount() > windowID);
    // }
    //
    // /**
    // * Assert that at least one window with the given title is open.
    // *
    // * @param title
    // */
    // public void assertWindowPresentWithTitle(String title) {
    // Assert.assertTrue(
    // "Unable to locate window with title [" + title + "].",
    // getDialog().hasWindowByTitle(title));
    // }
    //
    // /**
    // * Assert that the number of opened windows equals given value.
    // *
    // * @param windowCount
    // * Window count
    // */
    // public void assertWindowCountEquals(int windowCount) {
    // Assert.assertTrue("Window count is " + getDialog().getWindowCount()
    // + " but " + windowCount + " was expected.", getDialog()
    // .getWindowCount() == windowCount);
    // }
    //
    // /**
    // * Assert that a frame with the given name is present.
    // *
    // * @param frameName
    // */
    // public void assertFramePresent(String frameName) {
    // Assert.assertTrue("Unable to locate frame [" + frameName + "].",
    // getDialog().hasFrame(frameName));
    // }
    //
    // /**
    // * Checks to see if a cookie is present in the response.
    // *
    // * @param cookieName
    // * The cookie name
    // */
    // public void assertCookiePresent(String cookieName) {
    // Assert.assertTrue("Could not find Cookie : [" + cookieName + "]",
    // getDialog().hasCookie(cookieName));
    // }
    //
    // public void assertCookieValueEquals(String cookieName, String
    // expectedValue) {
    // assertCookiePresent(cookieName);
    // Assert.assertEquals(expectedValue, getDialog().getCookieValue(
    // cookieName));
    // }
    //
    // public void assertCookieValueMatch(String cookieName, String regexp) {
    // assertCookiePresent(cookieName);
    // RE re = null;
    // try {
    // re = new RE(regexp, RE.MATCH_SINGLELINE);
    // } catch (RESyntaxException e) {
    // Assert.fail(e.toString());
    // }
    // Assert.assertTrue("Unable to match [" + regexp + "] in cookie \""
    // + cookieName + "\"", re.match(getDialog().getCookieValue(
    // cookieName)));
    // }
    //
    // public void assertJavascriptAlertPresent(String msg) {
    // String alert = null;
    // try {
    // alert = getDialog().getJavascriptAlert();
    // } catch (ElementNotFoundException e) {
    // Assert.fail(e.getMessage());
    // }
    // Assert.assertEquals(msg, alert);
    // }
    //
    // // Form interaction methods
    //
    // /**
    // * Begin interaction with a specified form. If form interaction methods
    // are
    // * called without explicitly calling this method first, jWebUnit will
    // * attempt to determine itself which form is being manipulated.
    // *
    // * It is not necessary to call this method if their is only one form on
    // the
    // * current page.
    // *
    // * @param nameOrId
    // * name or id of the form to work with. Gets the value of a form
    // * input element. Allows getting information from a form element.
    // * Also, checks assertions as well.
    // *
    // * @param formElementName
    // * name of form element.
    // * @param value
    // */
    // public String getFormElementValue(String formElementName) {
    // assertFormPresent();
    // assertFormElementPresent(formElementName);
    // return getDialog().getFormParameterValue(formElementName);
    // }
    //

    /**
     * Begin interaction with a specified form. If form interaction methods are
     * called without explicitly calling this method first, jWebUnit will
     * attempt to determine itself which form is being manipulated.
     * 
     * It is not necessary to call this method if their is only one form on the
     * current page.
     * 
     * @param nameOrId
     *            name or id of the form to work with.
     */
    public void setWorkingForm(HtmlFormLocator formLocator)
            throws ElementNotFoundException {
        getDialog().setWorkingForm(formLocator);
    }

    //
    // /**
    // * Set the value of a text or password input field.
    // *
    // * @param inputName
    // * name of form element.
    // * @param value
    // * value to set.
    // */
    // public void setTextField(String inputName, String value) {
    // assertFormPresent();
    // assertFormElementPresent(inputName);
    // getDialog().setTextField(inputName, value);
    // }
    //
    // /**
    // * Select a specified checkbox. If the checkbox is already checked then
    // the
    // * checkbox will stay checked.
    // *
    // * @param checkBoxName
    // * name of checkbox to be selected.
    // */
    // public void checkCheckbox(String checkBoxName) {
    // assertCheckboxPresent(checkBoxName);
    // getDialog().checkCheckbox(checkBoxName);
    // }
    //
    // /**
    // * Select a specified checkbox. If the checkbox is already checked then
    // the
    // * checkbox will stay checked.
    // *
    // * @param checkBoxName
    // * name of checkbox to be selected.
    // * @param value
    // * value of checkbox to be selected.
    // */
    // public void checkCheckbox(String checkBoxName, String value) {
    // assertCheckboxPresent(checkBoxName);
    // getDialog().checkCheckbox(checkBoxName, value);
    // }
    //
    // /**
    // * Deselect a specified checkbox. If the checkbox is already unchecked
    // then
    // * the checkbox will stay unchecked.
    // *
    // * @param checkBoxName
    // * name of checkbox to be deselected.
    // */
    // public void uncheckCheckbox(String checkBoxName) {
    // assertFormElementPresent(checkBoxName);
    // getDialog().uncheckCheckbox(checkBoxName);
    // }
    //
    // /**
    // * Deselect a specified checkbox. If the checkbox is already unchecked
    // then
    // * the checkbox will stay unchecked.
    // *
    // * @param checkBoxName
    // * name of checkbox to be deselected.
    // * @param value
    // * value of checkbox to be deselected.
    // */
    // public void uncheckCheckbox(String checkBoxName, String value) {
    // assertFormElementPresent(checkBoxName);
    // getDialog().uncheckCheckbox(checkBoxName, value);
    // }
    //
    // /**
    // * Select options with given display labels in a select element.
    // *
    // * @param selectName
    // * name of select element.
    // * @param labels
    // * labels of options to be selected.
    // */
    // public void selectOptions(String selectName, String[] labels) {
    // assertSelectOptionsPresent(selectName, labels);
    // selectOptionsByLabel(selectName, labels);
    // }
    //
    // /**
    // * Select an option with a given display label in a select element.
    // *
    // * @param selectName
    // * name of select element.
    // * @param label
    // * label of option to be selected.
    // */
    // public void selectOption(String selectName, String label) {
    // selectOptions(selectName, new String[] {label});
    // }
    //
    // /**
    // * Select options with given values in a select element.
    // *
    // * @param selectName
    // * name of select element.
    // * @param values
    // * values of options to be selected.
    // */
    // public void selectOptionsByValues(String selectName, String[] values) {
    // assertSelectOptionValuesPresent(selectName, values);
    // getDialog().selectOptions(selectName, values);
    // }
    //
    // /**
    // * Select an option with a given value in a select element.
    // *
    // * @param selectName
    // * name of select element.
    // * @param values
    // * values of options to be selected.
    // */
    // public void selectOptionByValue(String selectName, String value) {
    // selectOptionsByValues(selectName, new String[] {value});
    // }
    //
    // // Form submission and link navigation methods
    //
    // /**
    // * Submit form - default submit button will be used (unnamed submit
    // button,
    // * or named button if there is only one on the form.
    // */
    // public void submit() {
    // assertFormPresent();
    // getDialog().submit();
    // }
    //
    // /**
    // * Submit form by pressing named button.
    // *
    // * @param buttonName
    // * name of button to submit form with.
    // */
    // public void submit(String buttonName) {
    // assertSubmitButtonPresent(buttonName);
    // getDialog().submit(buttonName);
    // }
    //
    // /**
    // * Submit the form by pressing the named button with the given value
    // * (label). Useful if you have more than one submit button with same name.
    // *
    // * @author Dragos Manolescu
    // * @param buttonName
    // * @param buttonValue
    // */
    // public void submit(String buttonName, String buttonValue) {
    // assertSubmitButtonPresent(buttonName, buttonValue);
    // getDialog().submit(buttonName, buttonValue);
    // }
    //
    // /**
    // * Reset the current form. See {@link #getForm}for an explanation of how
    // * the current form is established.
    // */
    // public void reset() {
    // getDialog().reset();
    // }
    //
    // /**
    // * Navigate by selection of a link containing given text.
    // *
    // * @param linkText
    // */
    // public void clickLinkWithText(String linkText) {
    // assertLinkPresentWithText(linkText);
    // getDialog().clickLinkWithText(linkText, 0);
    // }
    //
    // /**
    // * Navigate by selection of a link containing given text.
    // *
    // * @param linkText
    // * @param index
    // * The 0-based index, when more than one link with the same text
    // * is expected.
    // */
    // public void clickLinkWithText(String linkText, int index) {
    // assertLinkPresentWithText(linkText, index);
    // getDialog().clickLinkWithText(linkText, index);
    // }
    //
    // /**
    // * Search for labelText in the document, then search forward until finding
    // a
    // * link called linkText. Click it. Navigate by selection of a link with
    // the
    // * exact given text.
    // *
    // * SF.NET RFE: 996031
    // *
    // * @param linkText
    // */
    // public void clickLinkWithExactText(String linkText) {
    // assertLinkPresentWithExactText(linkText);
    // getDialog().clickLinkWithExactText(linkText, 0);
    // }
    //
    // /**
    // * Navigate by selection of a link with the exact given text.
    // *
    // * SF.NET RFE: 996031
    // *
    // * @param linkText
    // * @param index
    // * The 0-based index, when more than one link with the same text
    // * is expected.
    // */
    // public void clickLinkWithExactText(String linkText, int index) {
    // assertLinkPresentWithExactText(linkText, index);
    // getDialog().clickLinkWithExactText(linkText, index);
    // }
    //
    // /**
    // * Click the button with the given id.
    // *
    // * @param buttonId
    // */
    // public void clickButton(String buttonId) {
    // assertButtonPresent(buttonId);
    // getDialog().clickButton(buttonId);
    // }
    //
    // /**
    // * Clicks a button with <code>text</code> of the value attribute.
    // *
    // * @param text
    // * the text of the button (contents of the value attribute).
    // */
    // public void clickButtonWithText(String buttonValueText) {
    // assertButtonPresentWithText(buttonValueText);
    // getDialog().clickButtonWithText(buttonValueText);
    // }
    //
    // /**
    // * Navigate by selection of a link with a given image.
    // *
    // * @param imageFileName
    // * A suffix of the image's filename; for example, to match
    // * <tt>"images/my_icon.png"</tt>, you could just pass in
    // * <tt>"my_icon.png"</tt>.
    // */
    // public void clickLinkWithImage(String imageFileName) {
    // assertLinkPresentWithImage(imageFileName);
    // getDialog().clickLinkWithImage(imageFileName, 0);
    // }
    //
    // /**
    // * Navigate by selection of a link with given id.
    // *
    // * @param linkId
    // * id of link
    // */
    // public void clickLink(String linkId) {
    // assertLinkPresent(linkId);
    // getDialog().clickLink(linkId);
    // }
    //
    // /**
    // * Clicks a radio option. Asserts that the radio option exists first. *
    // *
    // * @param radioGroup
    // * name of the radio group.
    // * @param radioOption
    // * value of the option to check for.
    // */
    // public void clickRadioOption(String radioGroup, String radioOption) {
    // assertRadioOptionPresent(radioGroup, radioOption);
    // getDialog().clickRadioOption(radioGroup, radioOption);
    // }
    //
    // /**
    // * Click element with given xpath.
    // *
    // * @param xpath
    // * xpath of the element.
    // */
    // public void clickElementByXPath(String xpath) {
    // assertElementPresentByXPath(xpath);
    // getDialog().clickElementByXPath(xpath);
    // }
    //
    // // Window and Frame Navigation Methods
    //
    // /**
    // * Make a given window active.
    // *
    // * @param windowName
    // */
    // public void gotoWindow(String windowName) {
    // assertWindowPresent(windowName);
    // getDialog().gotoWindow(windowName);
    // }
    //
    // /**
    // * Make a given window active.
    // *
    // * @param windowID
    // * Javascript ID of the window
    // */
    // public void gotoWindow(int windowID) {
    // assertWindowPresent(windowID);
    // getDialog().gotoWindow(windowID);
    // }
    //
    // /**
    // * Make the root window active.
    // */
    // public void gotoRootWindow() {
    // getDialog().gotoRootWindow();
    // }
    //
    // /**
    // * Make first window with the given title active.
    // *
    // * @param windowName
    // */
    // public void gotoWindowByTitle(String title) {
    // assertWindowPresentWithTitle(title);
    // getDialog().gotoWindowByTitle(title);
    // }
    //
    // /**
    // * Make the named frame active (current response will be frame's
    // contents).
    // *
    // * @param frameName
    // */
    // public void gotoFrame(String frameName) {
    // getDialog().gotoFrame(frameName);
    // }
    //
    // /**
    // * Go to the given page like if user has typed the URL manually in the
    // * browser. Use
    // * {@link TestContext#setBaseUrl(String)
    // getTestContext().setBaseUrl(String)}
    // * to define base URL. Absolute URL should start with "http://",
    // "https://"
    // * or "www.".
    // *
    // * @param url
    // * absolute or relative URL (relative to base URL).
    // */
    // public void gotoPage(String url) {
    // try {
    // getDialog().gotoPage(createUrl(url));
    // } catch (TestingEngineResponseException aTestingEngineResponseException)
    // {
    // handleTestingEngineResponseException(aTestingEngineResponseException);
    // }
    // }
    //
    /**
     * Allows easier coding of exceptions thrown by the testing engines.
     */
    private void handleTestingEngineResponseException(
            TestingEngineResponseException aTestingEngineResponseException) {
        // Assert.fail("Method failed due to Exception Thrown: "
        // + ExceptionUtility
        // .stackTraceToString(aTestingEngineResponseException));
    }

    //
    // public void dumpCookies() {
    // String[][] cookies = getDialog().getCookies();
    // for (int i = 0; i < cookies.length; i++) {
    // System.out.println(cookies[i][0] + "=" + cookies[i][1]);
    // }
    //
    // }
    //
    // // Debug methods
    //
    // /**
    // * Dump html of current response to System.out - for debugging purposes.
    // *
    // * @param stream
    // */
    // public void dumpHtml() {
    // dumpHtml(System.out);
    // }
    //
    // /**
    // * Dump html of current response to a specified stream - for debugging
    // * purposes.
    // *
    // * @param stream
    // */
    // public void dumpHtml(PrintStream stream) {
    // stream.println(getDialog().getPageSource());
    // }
    //
    // /**
    // * Dump the table as the 2D array that is used for assertions - for
    // * debugging purposes.
    // *
    // * @param tableNameOrId
    // * @param stream
    // */
    // public void dumpTable(String tableNameOrId) {
    // dumpTable(tableNameOrId, System.out);
    // }
    //
    // /**
    // * Dump the table as the 2D array that is used for assertions - for
    // * debugging purposes.
    // *
    // * @param tableNameOrId
    // * @param table
    // * @param stream
    // */
    // public void dumpTable(String tableNameOrId, PrintStream stream) {
    // // String[][] table = getDialog().getTable(tableNameOrId).getStrings();
    // // //TODO Print correctly cells with colspan
    // // stream.print("\n" + tableNameOrId + ":");
    // // for (int i = 0; i < table.length; i++) {
    // // String[] cell = table[i];
    // // stream.print("\n\t");
    // // for (int j = 0; j < cell.length; j++) {
    // // stream.print("[" + cell[j] + "]");
    // // }
    // // }
    //
    // }
    //
    // Settings

    /**
     * Enable or disable Javascript support
     */
    public void setScriptingEnabled(boolean value) {
        getDialog().setScriptingEnabled(value);
    }

    /**
     * Set the Testing Engine that you want to use for the tests based on the
     * Testing Engine Key.
     * 
     * @see TestingEngineRegistry
     * @param testingEngineKey
     *            The testingEngineKey to set.
     */
    public void setTestingEngineKey(String testingEngineKey) {
        this.testingEngineKey = testingEngineKey;
        dialog = null;
    }

    /**
     * Gets the Testing Engine Key that is used to find the proper testing
     * engine class (HttpUnitDialog / JacobieDialog) for the tests.
     * 
     * @return Returns the testingEngineKey.
     */
    public String getTestingEngineKey() {
        if (testingEngineKey == null) {
            // use first available dialog
            if (!TestingEngineRegistry.isEmpty()) {
                setTestingEngineKey(TestingEngineRegistry
                        .getFirstTestingEngineKey());
            } else {
                throw new RuntimeException(
                        "TestingEngineRegistry contains no dialog. Check you put at least one plugin in the classpath.");
            }
        }
        return testingEngineKey;
    }

    private RE getRE(String regexp) {
        RE re = null;
        try {
            re = new RE(regexp, RE.MATCH_SINGLELINE);
        } catch (RESyntaxException e) {
            // Assert.fail(e.toString());
        }
        return re;
    }
    //
    // /**
    // * Return a string array of select box option labels. <br/>
    // *
    // * Exemple: <br/>
    // *
    // * <pre>
    // * &lt;FORM action=&quot;http://my_host/doit&quot;
    // method=&quot;post&quot;&gt;
    // * &lt;P&gt;
    // * &lt;SELECT multiple size=&quot;4&quot;
    // name=&quot;component-select&quot;&gt;
    // * &lt;OPTION selected
    // value=&quot;Component_1_a&quot;&gt;Component_1&lt;/OPTION&gt;
    // * &lt;OPTION selected
    // value=&quot;Component_1_b&quot;&gt;Component_2&lt;/OPTION&gt;
    // * &lt;OPTION&gt;Component_3&lt;/OPTION&gt;
    // * &lt;OPTION&gt;Component_4&lt;/OPTION&gt;
    // * &lt;OPTION&gt;Component_5&lt;/OPTION&gt;
    // * &lt;/SELECT&gt;
    // * &lt;INPUT type=&quot;submit&quot; value=&quot;Send&quot;&gt;&lt;INPUT
    // type=&quot;reset&quot;&gt;
    // * &lt;/P&gt;
    // * &lt;/FORM&gt;
    // * </pre>
    // *
    // * Should return [Component_1, Component_2, Component_3, Component_4,
    // * Component_5]
    // *
    // * @param selectName
    // * name of the select box.
    // */
    // private String[] getOptionsFor(String selectName) {
    // String[] values = getDialog().getSelectOptionValues(selectName);
    // String[] result = new String[values.length];
    // for (int i = 0; i < values.length; i++) {
    // result[i] = getDialog().getSelectOptionLabelForValue(selectName,
    // values[i]);
    // }
    // return result;
    // }
    //
    // /**
    // * Select options by given labels in a select box
    // *
    // * @param selectName
    // * name of the select
    // * @param labels
    // * labels of options to be selected
    // */
    // private void selectOptionsByLabel(String selectName, String[] labels) {
    // String[] values = new String[labels.length];
    // for (int i = 0; i < values.length; i++) {
    // values[i] = getDialog().getSelectOptionValueForLabel(selectName,
    // labels[i]);
    // }
    // getDialog().selectOptions(selectName, values);
    // }
    //
    // private void assertArraysEqual(String[] exptected, String[] returned) {
    // Assert.assertEquals("Arrays not same length", exptected.length,
    // returned.length);
    // for (int i = 0; i < returned.length; i++) {
    // Assert.assertEquals("Elements " + i + "not equal", exptected[i],
    // returned[i]);
    // }
    // }

}
