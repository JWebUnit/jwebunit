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

import com.meterware.httpunit.WebTable;
import com.meterware.httpunit.SubmitButton;
import net.sourceforge.jwebunit.HttpUnitDialog;
import net.sourceforge.jwebunit.util.ExceptionUtility;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import java.io.PrintStream;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * This is a delegate to test deployed web applications using JUnit. This class
 * provides a simple API for basic web application navigation and assertions
 * by wrapping HttpUnit.  It supports use of a property file for web
 * resources (ala Struts), though a resource file for the app is not required.
 *
 *  @author Jim Weaver
 *  @author Wilkes Joiner
 */
public class WebTester {
    private HttpUnitDialog dialog;
    private TestContext context = new TestContext();

    /**
     * Provides access to the httpunit wrapper for subclasses - in case functionality not
     * yet wrappered required by test.
     *
     * @return HttpUnitDialog instance used to wrapper httpunit conversation.
     */
    public HttpUnitDialog getDialog() {
        return dialog;
    }

    /**
     * Provide access to test context.
     *
     * @return TestContext
     */
    public TestContext getTestContext() {
        return context;
    }

    /**
     * Begin conversation at a url relative to the application root.
     *
     * @param relativeURL
     */
    public void beginAt(String relativeURL) {
        String url = createUrl(relativeURL);
        dialog = new HttpUnitDialog(url, context);
    }

    private String createUrl(String suffix) {
        suffix = suffix.startsWith("/") ? suffix.substring(1) : suffix;
        return getTestContext().getBaseUrl() + suffix;
    }

    /**
     * Return the value of a web resource based on its key. This translates to a
     * property file lookup with the locale based on the current TestContext.
     *
     * @param key name of the web resource.
     * @return value of the web resource, encoded according to TestContext.
     */
    public String getMessage(String key) {
        String message = "";
        Locale locale = context.getLocale();
        try {
            message = ResourceBundle.getBundle(getTestContext().getResourceBundleName(), locale).getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No message found for key [" + key + "]." +
                    "\nError: " + ExceptionUtility.stackTraceToString(e));
        }
        return context.toEncodedString(message);
    }
    
    //Assertions

    /**
     * Assert title of current html page in conversation matches an expected value.
     *
     * @param title expected title value
     */
    public void assertTitleEquals(String title) {
        Assert.assertEquals(title, dialog.getResponsePageTitle());
    }

    /**
     * Assert title of current html page matches the value of a specified web resource.
     *
     * @param titleKey web resource key for title
     */
    public void assertTitleEqualsKey(String titleKey) {
        Assert.assertEquals(getMessage(titleKey), dialog.getResponsePageTitle());
    }

    /**
     * Assert that a web resource's value is present.
     *
     * @param key web resource name
     */
    public void assertKeyPresent(String key) {
        assertTextPresent(getMessage(key));
    }

    /**
     * Assert that supplied text is present.
     *
     * @param text
     */
    public void assertTextPresent(String text) {
        if (!dialog.isTextInResponse(text))
            Assert.fail("Expected text not found in response: [" + text + "]");
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
     * Assert that supplied text is not present.
     *
     * @param text
     */
    public void assertTextNotPresent(String text) {
        if (dialog.isTextInResponse(text))
            Assert.fail("Text found in response when not expected: [" + text + "]");
    }

    /**
     * Assert that a table with a given summary value is present.
     *
     * @param tableSummary summary value of table
     */
    public void assertTablePresent(String tableSummary) {
        if (dialog.getWebTableBySummary(tableSummary) == null)
            Assert.fail("Unable to locate table \"" + tableSummary + "\"");
    }

    /**
     * Assert that a table with a given summary value is not present.
     *
     * @param tableSummary summary value of table
     */
    public void assertTableNotPresent(String tableSummary) {
        if (dialog.getWebTableBySummary(tableSummary) != null)
            Assert.fail("Located table \"" + tableSummary + "\"");
    }

    /**
     * Assert that the value of a given web resource is present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param key web resource name
     */
    public void assertKeyInTable(String tableSummary, String key) {
        assertTextInTable(tableSummary, getMessage(key));
    }

    /**
     * Assert that supplied text is present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param text
     */
    public void assertTextInTable(String tableSummary, String text) {
        assertTablePresent(tableSummary);
        Assert.assertTrue("Could not find: [" + text + "]", dialog.isTextInTable(tableSummary, text));
    }

    /**
     * Assert that the values of a set of web resources are all present in a specific table.
     *
     * @param tableSummary
     * @param keys Array of web resource names.
     */
    public void assertKeysInTable(String tableSummary, String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            assertKeyInTable(tableSummary, keys[i]);
        }
    }

    /**
     * Assert that a set of text values are all present in a specific table.
     *
     * @param tableSummary
     * @param text Array of expected text values.
     */
    public void assertTextInTable(String tableSummary, String[] text) {
        for (int i = 0; i < text.length; i++) {
            assertTextInTable(tableSummary, text[i]);
        }
    }

    /**
     * Assert that the value of a given web resource is not present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param key web resource name
     */
    public void assertKeyNotInTable(String tableSummary, String key) {
        assertTextNotInTable(tableSummary, getMessage(key));
    }

    /**
     * Assert that supplied text is not present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param text
     */
    public void assertTextNotInTable(String tableSummary, String text) {
        assertTablePresent(tableSummary);
        Assert.assertTrue("Found text: [" + text + "]", !dialog.isTextInTable(tableSummary, text));
    }

    /**
     * Assert that none of a set of text values are present in a specific table.
     *
     * @param tableSummary summary value of table
     * @param text Array of text values
     */
    public void assertTextNotInTable(String tableSummary, String[] text) {
        for (int i = 0; i < text.length; i++) {
            assertTextNotInTable(tableSummary, text[i]);
        }
    }

    /**
     * Assert that a specific table matches an ExpectedTable.
     *
     * @param tableSummary summary value of table
     * @param expectedTable represents expected values (colspan supported).
     */
    public void assertTableEquals(String tableSummary, ExpectedTable expectedTable) {
        assertTableEquals(tableSummary, expectedTable.getExpectedStrings());
    }

    /**
     * Assert that a specific table matches a matrix of supplied text values.
     *
     * @param tableSummary summary value of table
     * @param expectedCellValues double dimensional array of expected values
     */
    public void assertTableEquals(String tableSummary, String[][] expectedCellValues) {
        assertTableRowsEqual(tableSummary, 0, expectedCellValues);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     *
     * @param tableSummary summary value of table
     * @param startRow index of start row for comparison
     * @param expectedTable represents expected values (colspan supported).
     */
    public void assertTableRowsEqual(String tableSummary, int startRow, ExpectedTable expectedTable) {
        assertTableRowsEqual(tableSummary, startRow, expectedTable.getExpectedStrings());
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     *
     * @param tableSummary summary value of table
     * @param startRow index of start row for comparison
     * @param expectedCellValues double dimensional array of expected values
     */
    public void assertTableRowsEqual(String tableSummary, int startRow, String[][] expectedCellValues) {
        String[][] sparseTableCellValues = getSparseTable(tableSummary);
        if (expectedCellValues.length > (sparseTableCellValues.length - startRow))
            Assert.fail("Expected rows [" + expectedCellValues.length + "] larger than actual rows in range being compared" +
                    " [" + (sparseTableCellValues.length - startRow) + "].");
        for (int i = 0; i < expectedCellValues.length; i++) {
            String[] row = expectedCellValues[i];
            for (int j = 0; j < row.length; j++) {
                if (row.length != sparseTableCellValues[i].length)
                    Assert.fail("Unequal number of columns for row " + i + " of table " + tableSummary +
                            ". Expected [" + row.length + "] found [" + sparseTableCellValues[i].length + "].");
                String expectedString = row[j];
                Assert.assertEquals("Expected " + tableSummary + " value at [" + i + "," + j + "] not found.",
                        expectedString, context.toEncodedString(sparseTableCellValues[i + startRow][j].trim()));
            }
        }

    }

    private String[][] getSparseTable(String tableSummary) {
        assertTablePresent(tableSummary);
        WebTable table = dialog.getWebTableBySummary(tableSummary);
        table.purgeEmptyCells();
        String[][] sparseTableCellValues = table.asText();
        return sparseTableCellValues;
    }

    /**
     * Assert that a form input element with a given name is present.
     *
     * @param parameterName
     */
    public void assertFormElementPresent(String parameterName) {
        assertHasForm();
        Assert.assertTrue("Did not find form control with name [" + parameterName + "].",
                dialog.hasFormParameterNamed(parameterName));
    }

    /**
     * Assert that a form input element with a given name is not present.
     *
     * @param parameterName
     */
    public void assertFormElementNotPresent(String parameterName) {
        assertHasForm();
        try {
            Assert.assertTrue("Found form control with name [" + parameterName + "] when not expected.",
                    !dialog.hasFormParameterNamed(parameterName));
        } catch (UnableToSetFormException e) {
            // assertFormControlNotPresent
        }
    }

    /**
     * Assert that there is a form present.
     *
     */
    public void assertHasForm() {
        Assert.assertTrue("No form present", dialog.hasForm());
    }

    /**
     * Assert that there is a form with the specified name present.
     * @param formName
     */
    public void assertHasForm(String formName) {
        Assert.assertTrue("No form present", dialog.hasForm(formName));
    }

    /**
     * Assert that a specific form element has an expected value.
     *
     * @param formControlName
     * @param expectedValue
     */
    public void assertFormElementEquals(String formControlName, String expectedValue) {
        assertHasForm();
        Assert.assertEquals(expectedValue, dialog.getFormParameterValue(formControlName));
    }

    /**
     * Assert that a specific checkbox is selected.
     *
     * @param checkBoxName
     */
    public void assertCheckboxSelected(String checkBoxName) {
        assertHasForm();
        assertFormElementPresent(checkBoxName);
        Assert.assertEquals("on", dialog.getFormParameterValue(checkBoxName));
    }

    /**
     * Assert that a specific checkbox is not selected.
     *
     * @param checkBoxName
     */
    public void assertCheckboxNotSelected(String checkBoxName) {
        assertHasForm();
        assertFormElementPresent(checkBoxName);
        Assert.assertNull(dialog.getFormParameterValue(checkBoxName));
    }
    
    /**
     * Assert that a specific option is present in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for.
     */
    public void assertRadioOptionPresent(String name, String radioOption) {
        if (!dialog.hasRadioOption(name, radioOption))
            Assert.fail("Unable to find option " + radioOption + " in radio group " + name);
    }

    /**
     * Assert that a specific option is not present in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for.
     */
    public void assertRadioOptionNotPresent(String name, String radioOption) {
        if (dialog.hasRadioOption(name, radioOption))
            Assert.fail("Found option " + radioOption + " in radio group " + name);
    }

    /**
     * Assert that a specific option is selected in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for selection.
     */
    public void assertRadioOptionSelected(String name, String radioOption) {
        assertFormElementEquals(name, radioOption);
     }

    /**
     * Assert that a specific option is not selected in a radio group.
     * 
     * @param name radio group name.
     * @param radioOption option to test for selection.
     */
    public void assertRadioOptionNotSelected(String name, String radioOption) {
        Assert.assertTrue("Radio option " + radioOption + " is not selected",
                !radioOption.equals(dialog.getFormParameterValue(name)));
    }

    /**
     * Assert that the display values of a select element's options match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param expectedOptions expected display values for the select box.
     */
    public void assertOptionsEqual(String selectName, String[] expectedOptions) {
        assertFormElementPresent(selectName);
        assertArraysEqual(expectedOptions, dialog.getOptionsFor(selectName));
    }

    /**
     * Assert that the display values of a select element's options do not match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param expectedOptions expected display values for the select box.
     */
    public void assertOptionsNotEqual(String selectName, String[] expectedOptions) {
        assertFormElementPresent(selectName);
        try {
            assertOptionsEqual(selectName, expectedOptions);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Options not expected to be equal");
   }

    /**
     * Assert that the values of a select element's options match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param expectedOptions expected values for the select box.
     */
    public void assertOptionValuesEqual(String selectName, String[] expectedValues) {
        assertFormElementPresent(selectName);
        assertArraysEqual(expectedValues, dialog.getOptionValuesFor(selectName));

    }

    //Todo: Move to assert utility class
    private void assertArraysEqual(String[] exptected, String[] returned) {
        Assert.assertEquals("Arrays not same length", exptected.length, returned.length);
        for (int i = 0; i < returned.length; i++) {
            Assert.assertEquals("Elements " + i + "not equal", exptected[i], returned[i]);
        }
    }

    /**
     * Assert that the values of a select element's options do not match a given array of strings.
     * 
     * @param selectName name of the select element.
     * @param expectedOptions expected values for the select box.
     */
    public void assertOptionValuesNotEqual(String selectName, String[] optionValues) {
        assertFormElementPresent(selectName);
        try {
            assertOptionValuesEqual(selectName, optionValues);
        } catch (AssertionFailedError e) {
            return;
        }
        Assert.fail("Values not expected to be equal");
    }

    /**
     * Assert that the currently selected display value of a select box matches a given value.
     * 
     * @param selectName name of the select element.
     * @param option expected display value of the selected option.
     */
    public void assertOptionEquals(String selectName, String option) {
        assertFormElementPresent(selectName);
        Assert.assertEquals(option, dialog.getSelectedOption(selectName));
    }

    /**
     * Assert that a submit button with a given name is present.
     *
     * @param buttonName
     */
    public void assertSubmitButtonPresent(String buttonName) {
        assertHasForm();
        Assert.assertNotNull("Button [" + buttonName + "] not found.", dialog.getSubmitButton(buttonName));
    }

    /**
     * Assert that a submit button with a given name is not present.
     *
     * @param buttonName
     */
    public void assertSubmitButtonNotPresent(String buttonName) {
        assertHasForm();
        SubmitButton button = null;
        try {
            button = dialog.getSubmitButton(buttonName);
        } catch (UnableToSetFormException e) {
        }
        Assert.assertNull("Button [" + buttonName + "] found.", button);
    }

    /**
     * Assert that a submit button with a given name and value is present.
     *
     * @param buttonName
     * @param expectedValue
     */
    public void assertSubmitButtonValue(String buttonName, String expectedValue) {
        assertHasForm();
        assertSubmitButtonPresent(buttonName);
        Assert.assertEquals(expectedValue, dialog.getSubmitButton(buttonName).getValue());
    }

    /**
     * Assert that a link with a given id is present in the response.
     * 
     * @param linkId
     */
    public void assertLinkPresent(String linkId) {
        Assert.assertTrue("Unable to find link with id ["+ linkId + "]", dialog.isLinkPresentById(linkId));
    }

    /**
     * Assert that no link with the given id is present in the response.
     * 
     * @param linkId
     */
    public void assertLinkNotPresent(String linkId) {
        Assert.assertTrue("Unable to find link with id ["+ linkId + "]", !dialog.isLinkPresentById(linkId));
    }
    
    /**
     * Assert that a link containing the supplied text is present.
     *
     * @param linkText
     */
    public void assertLinkPresentWithText(String linkText) {
        Assert.assertTrue("Link with text [" + linkText + "] not found in response.", dialog.isLinkInResponse(linkText));
    }

    /**
     * Assert that no link containing the supplied text is present.
     *
     * @param linkText
     */
    public void assertLinkNotPresentWithText(String linkText) {
        Assert.assertTrue("Link with text [" + linkText + "] found in response.", !dialog.isLinkInResponse(linkText));
    }
    
    /**
     * Assert that an element with a given id is present.
     * 
     * @param anID element id to test for.
     */
    public void assertElementPresent(String anID) {
        Assert.assertNotNull("Unable to locate element with id \"" +anID+ "\"", dialog.getElement(anID));
    }

    /**
     * Assert that an element with a given id is not present.
     * 
     * @param anID element id to test for.
     */
    public void assertElementNotPresent(String anID) {
        Assert.assertNull("Located element with id \"" +anID+ "\"", dialog.getElement(anID));
    }

    //Form interaction methods

    /**
     * Begin interaction with a specified form.  If form interaction methods are called without
     * explicitly calling this method first, jWebUnit will attempt to determine itself which form
     * is being manipulated.
     * 
     * It is not necessary to call this method if their is only one form on the current page.
     * 
     * @param nameOrId name or id of the form to work with.
     */
    public void setWorkingForm(String nameOrId) {
        dialog.setWorkingForm(nameOrId);
    }

     /**
     * Set the value of a form input element.
     *
     * @param parameterName name of form element.
     * @param value
     */
    public void setFormElement(String parameterName, String value) {
        assertHasForm();
        assertFormElementPresent(parameterName);
        dialog.setFormParameter(parameterName, value);
    }

    /**
     * Select a specified checkbox.
     *
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
        assertHasForm();
        assertFormElementPresent(checkBoxName);
        dialog.setFormParameter(checkBoxName, "on");
    }   

    /**
     * Deselect a specified checkbox.
     *
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        assertHasForm();
        assertFormElementPresent(checkBoxName);
        dialog.removeFormParameter(checkBoxName);
    }   

    /**
     * Select an option with a given display value in a select element.
     * 
     * @param selectName name of select element.
     * @param option display value of option to be selected.
     */
    public void selectOption(String selectName, String option) {
        assertFormElementPresent(selectName);
        dialog.selectOption(selectName, option);
    }
    
    //Form submission and link navigation methods

    /**
     * Submit form - default submit button will be used (unnamed submit button, or
     * named button if there is only one on the form.
     */
    public void submit() {
        assertHasForm();
        dialog.submit();
    }

    /**
     * Submit form by pressing named button.
     * 
     * @param buttonName name of button to submit form with.
     */
    public void submit(String buttonName) {
        assertSubmitButtonPresent(buttonName);
        dialog.submit(buttonName);
    }

    /**
     * Navigate by selection of a specified link.
     *
     * @param linkText
     */
    public void clickLink(String linkText) {
        dialog.clickLink(linkText);
    }

    /**
     * Navigate by selection of a specified link
     * 
     * @param anId id of link
     */
    public void clickLinkByID(String anID) {
        dialog.clickLinkByID(anID);
    }

    //Debug methods

    /**
     * Dump html of current response to a specified stream - for debugging purposes.
     *
     * @param stream
     */
    public void dumpResponse(PrintStream stream) {
        try {
            stream.println(dialog.getResponseText());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for debuggin purposes.
     *
     * @param tableName
     * @param stream
     */
    public void dumpTable(String tableName, PrintStream stream) {
        dumpTable(tableName, getSparseTable(tableName), stream);
    }

    /**
     * Dump the table as the 2D array that is used for assertions. - for debuggin purposes.
     *
     * @param tableName
     * @param table
     */
    public void dumpTable(String tableName, String[][] table) {
        dumpTable(tableName, table, System.out);
    }

    /**
     * Dump the table as the 2D array that is used for assertions. - for debuggin purposes.
     *
     * @param tableName
     * @param table
     * @param stream
     */
    public void dumpTable(String tableName, String[][] table, PrintStream stream) {
        stream.print("\n" + tableName + ":");
        for (int i = 0; i < table.length; i++) {
            String[] cell = table[i];
            stream.print("\n\t");
            for (int j = 0; j < cell.length; j++) {
                stream.print("[" + cell[j] + "]");
            }
        }
    }

}
