/********************************************************************************
 * jWebUnit, simplified web testing API for HttpUnit
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * 651 W Washington Ave. Suite 500
 * Chicago, IL 60661 USA
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     + Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     + Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     + Neither the name of ThoughtWorks, Inc., jWebUnit, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/

package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.HttpUnitDialog;
import junit.framework.TestCase;

import java.io.PrintStream;

/**
 * Superclass for Junit TestCases to test deployed web applications.
 * This class uses WebTester as a mixin. This class provides a simple
 * API for basic web application navigation and assertions by wrappering HttpUnit.
 * It supports use of a property file for web resources (ala Struts), though a
 * resource file for the app is not required.
 *
 *  @author Jim Weaver
 *  @author Wilkes Joiner
 */
public class WebTestCase extends TestCase {
    private WebTester tester;

    /**
     * Constructor to create a new web TestCase.
     *
     * @param name junit test name.
     * @param baseUrl root url of web application to be tested.
     * @param resourceBundleName path name for property file containing web
     * resources.  May be null.
     */
    public WebTestCase(String name, String baseUrl, String resourceBundleName) {
        this(name, baseUrl, resourceBundleName, new TestContext());
    }

    public WebTestCase(String name, String baseUrl, String resourceBundleName, TestContext context) {
        super(name);
        tester = new WebTester(baseUrl, resourceBundleName, context);
    }

    /**
     * Change the base url used for testing.
     *
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        tester.setBaseUrl(baseUrl);
    }

    /**
     * Provides access to the httpunit wrapper for subclasses - in case functionality not
     * yet wrappered required by test.
     *
     * @return HttpUnitDialog instance used to wrapper httpunit conversation.
     */
    public HttpUnitDialog getDialog() {
        return tester.getDialog();
    }

    /**
     * Provide access to test context.
     *
     * @return TestContext
     */
    public TestContext getTestContext() {
        return tester.getTestContext();
    }

    /**
     * Begin conversation at a url relative to the application root.
     *
     * @param relativeURL
     */
    public void beginAt(String relativeURL) {
        tester.beginAt(relativeURL);
    }

    /**
     * Return the value of a web resource baseed on its key. This translates to a
     * property file lookup with the locale based on the TestSuiteLocale (US
     * used as default if no TestSuiteLocale has been set up for the test).
     *
     * @param key name of the web resource.
     * @return value of the web resource.
     */
    public String getMessage(String key) {
        return tester.getMessage(key);
    }

    /**
     * Assert title of current html page in conversation matches an expected value.
     *
     * @param title expected title value
     */
    public void assertTitleEquals(String title) {
        tester.assertTitleEquals(title);
    }

    /**
     * Assert title of current html page matches the value of a specified web resource.
     *
     * @param titleKey web resource key for title
     */
    public void assertTitleEqualsKey(String titleKey) {
        tester.assertTitleEqualsKey(titleKey);
    }

    /**
     * Assert that a web resource's value is present.
     *
     * @param key web resource name
     */
    public void assertKeyInResponse(String key) {
        tester.assertKeyInResponse(key);
    }

    /**
     * Assert that supplied text is present.
     *
     * @param text
     */
    public void assertTextInResponse(String text) {
        tester.assertTextInResponse(text);
    }

    /**
     * Assert that a web resource's value is not present.
     *
     * @param key web resource name
     */
    public void assertKeyNotInResponse(String key) {
        tester.assertKeyNotInResponse(key);
    }

    /**
     * Assert that supplied text is not present.
     *
     * @param text
     */
    public void assertTextNotInResponse(String text) {
        tester.assertTextNotInResponse(text);
    }

    /**
     * Assert that a table with a given summary value is present.
     *
     * @param tableSummary summary value of table
     */
    public void assertTablePresent(String tableSummary) {
        tester.assertTablePresent(tableSummary);
    }

    /**
     * Assert that the value of a given web resource is present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param key web resource name
     */
    public void assertKeyInTable(String tableSummary, String key) {
        tester.assertKeyInTable(tableSummary, key);
    }

    /**
     * Assert that supplied text is present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param text
     */
    public void assertTextInTable(String tableSummary, String text) {
        tester.assertTextInTable(tableSummary, text);
    }

    /**
     * Assert that the values of a set of web resources are all present in a specific table.
     *
     * @param tableSummary
     * @param keys Array of web resource names.
     */
    public void assertKeysInTable(String tableSummary, String[] keys) {
        tester.assertKeysInTable(tableSummary, keys);
    }

    /**
     * Assert that a set of text values are all present in a specific table.
     *
     * @param tableSummary
     * @param text Array of expected text values.
     */
    public void assertTextInTable(String tableSummary, String[] text) {
        tester.assertTextInTable(tableSummary, text);
    }

    /**
     * Assert that the value of a given web resource is not present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param key web resource name
     */
    public void assertKeyNotInTable(String tableSummary, String key) {
        tester.assertKeyNotInTable(tableSummary, key);
    }

    /**
     * Assert that supplied text is not present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param text
     */
    public void assertTextNotInTable(String tableSummary, String text) {
        tester.assertTextNotInTable(tableSummary, text);
    }

    /**
     * Assert that none of a set of text values are present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param text Array of text values
     */
    public void assertTextNotInTable(String tableSummary, String[] text) {
        tester.assertTextNotInTable(tableSummary, text);
    }

    /**
     * Assert that a specific table matches an ExpectedTable.
     *
     * @param tableSummary summary value of table
     * @param expectedTable represents expected values (colspan supported).
     */
    public void assertTableEquals(String tableSummary, ExpectedTable expectedTable) {
        tester.assertTableEquals(tableSummary, expectedTable.getExpectedStrings());
    }

    /**
     * Assert that a specific table matches a matrix of supplied text values.
     *
     * @param tableSummary summary value of table
     * @param expectedCellValues double dimensional array of expected values
     */
    public void assertTableEquals(String tableSummary, String[][] expectedCellValues) {
        tester.assertTableEquals(tableSummary, expectedCellValues);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     *
     * @param tableSummary summary value of table
     * @param startRow index of start row for comparison
     * @param expectedTable represents expected values
     */
    public void assertTableRowsEqual(String tableSummary, int startRow, ExpectedTable expectedTable) {
        tester.assertTableRowsEqual(tableSummary, startRow, expectedTable);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     *
     * @param tableSummary summary value of table
     * @param startRow index of start row for comparison
     * @param expectedCellValues double dimensional array of expected values
     */
    public void assertTableRowsEqual(String tableSummary, int startRow, String[][] expectedCellValues) {
        tester.assertTableRowsEqual(tableSummary, startRow, expectedCellValues);
    }

    /**
     * Assert that a form input element with a given name is present.
     *
     * @param formControlName
     */
    public void assertFormControlPresent(String formControlName) {
        tester.assertFormControlPresent(formControlName);
    }

    /**
     * Assert that a form input element with a given name is not present.
     *
     * @param formControlName
     */
    public void assertFormControlNotPresent(String formControlName) {
        tester.assertFormControlNotPresent(formControlName);
    }

    /**
     * Assert that there is a form present.
     *
     */
    public void assertHasForm() {
        tester.assertHasForm();
    }

    public void assertHasForm(String formName) {
        tester.assertHasForm(formName);
    }
    /**
     * Assert that a specific form element has an expected value.
     *
     * @param formControlName
     * @param expectedValue
     */
    public void assertFormControlEquals(String formControlName, String expectedValue) {
        tester.assertFormControlEquals(formControlName, expectedValue);
    }

    /**
     * Assert that a specified input field of type checkbox is present and selected (checked).
     * Note that httpunit currently returns a value of "on" for selected checkboxes,
     * so this is what we check for.
     *
     * @param checkBoxName
     */
    public void assertCheckboxSelected(String checkBoxName) {
        tester.assertCheckboxSelected(checkBoxName);
    }

    /**
     * Assert that a specified checkbox input field is present but not selected.
     * Note that httpunit returns a value of null for unselected checkboxes so that
     * is what is tested for.
     * @param checkBoxName
     */
    public void assertCheckboxNotSelected(String checkBoxName) {
        tester.assertCheckboxNotSelected(checkBoxName);
    }

    /**
     * Assert that a submit button with a given name is present.
     *
     * @param buttonName
     */
    public void assertSubmitButtonPresent(String buttonName) {
        tester.assertSubmitButtonPresent(buttonName);
    }

    /**
     * Assert that a submit button with a given name is not present.
     *
     * @param buttonName
     */
    public void assertSubmitButtonNotPresent(String buttonName) {
        tester.assertSubmitButtonNotPresent(buttonName);
    }

    /**
     * Assert that a submit button with a given name and value is present.
     *
     * @param buttonName
     * @param expectedValue
     */
    public void assertSubmitButtonValue(String buttonName, String expectedValue) {
        tester.assertSubmitButtonValue(buttonName, expectedValue);
    }

    /**
     * Assert that a link with supplied text is present.
     *
     * @param linkText
     */
    public void assertLinkInResponse(String linkText) {
        tester.assertLinkInResponse(linkText);
    }

    /**
     * Assert that a link with supplied text is not present.
     *
     * @param linkText
     */
    public void assertLinkNotInResponse(String linkText) {
        tester.assertLinkNotInResponse(linkText);
    }

    /**
     * Submit form.
     */
    protected void submit() {
        tester.submit();
    }

    /**
     * Submit form by pressing named button.
     */
    public void submit(String buttonName) {
        tester.submit(buttonName);
    }

    public void submitForm(String formName) {
        tester.submitForm(formName);
    }

    public void submitForm(String formName, String buttonName) {
        tester.submitForm(formName, buttonName);
    }

    /**
     * Navigate by selection of a specified link.
     *
     * @param linkText
     */
    protected void clickLink(String linkText) {
        tester.clickLink(linkText);
    }

    /**
     * Dump html of current response to a specified stream - for debugging purposes.
     *
     * @param stream
     */
    protected void dumpResponse(PrintStream stream) {
        tester.dumpResponse(stream);
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for debuggin purposes.
     *
     * @param tableName
     * @param stream
     */
    protected void dumpTable(String tableName, PrintStream stream) {
        tester.dumpTable(tableName, stream);
    }

    /**
     * Dump the table as the 2D array that is used for assertions. - for debuggin purposes.
     *
     * @param tableName
     * @param table
     */
    protected void dumpTable(String tableName, String[][] table) {
        tester.dumpTable(tableName, table);
    }

    /**
     * Set the value of a form input element.
     *
     * @param parameterName name of form element.
     * @param value
     */
    protected void setFormParameter(String parameterName, String value) {
        tester.setFormParameter(parameterName, value);
    }

    /**
     * Remove a form input element (turn a checkbox off).
     * @param parameterName
     */
    protected void removeFormParameter(String parameterName) {
        tester.removeFormParameter(parameterName);
    }

}
