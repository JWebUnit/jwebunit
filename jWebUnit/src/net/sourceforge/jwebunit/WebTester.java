/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

import java.io.PrintStream;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.sourceforge.jwebunit.util.ExceptionUtility;

import org.w3c.dom.Element;

/**
 * Provides a high-level API for basic web application navigation and validation
 * by wrapping HttpUnit and providing Junit assertions.  It supports use of a property file for web
 * resources (a la Struts), though a resource file for the app is not required.
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
     * Assert that a table with a given summary or id value is present.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     */
    public void assertTablePresent(String tableSummaryOrId) {
        if (dialog.getWebTableBySummaryOrId(tableSummaryOrId) == null)
            Assert.fail("Unable to locate table \"" + tableSummaryOrId + "\"");
    }

    /**
     * Assert that a table with a given summary or id value is not present.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     */
    public void assertTableNotPresent(String tableSummaryOrId) {
        if (dialog.getWebTableBySummaryOrId(tableSummaryOrId) != null)
            Assert.fail("Located table \"" + tableSummaryOrId + "\"");
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
     * Assert that supplied text is present in a specific table.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param text
     */
    public void assertTextInTable(String tableSummaryOrId, String text) {
        assertTablePresent(tableSummaryOrId);
        Assert.assertTrue("Could not find: [" + text + "]" +
                "in table [" + tableSummaryOrId + "]",
                dialog.isTextInTable(tableSummaryOrId, text));
    }

    /**
     * Assert that the values of a set of web resources are all present in a specific table.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param keys Array of web resource names.
     */
    public void assertKeysInTable(String tableSummaryOrId, String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            assertKeyInTable(tableSummaryOrId, keys[i]);
        }
    }

    /**
     * Assert that a set of text values are all present in a specific table.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param text Array of expected text values.
     */
    public void assertTextInTable(String tableSummaryOrId, String[] text) {
        for (int i = 0; i < text.length; i++) {
            assertTextInTable(tableSummaryOrId, text[i]);
        }
    }

    /**
     * Assert that the value of a given web resource is not present in a specific table.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param key web resource name
     */
    public void assertKeyNotInTable(String tableSummaryOrId, String key) {
        assertTextNotInTable(tableSummaryOrId, getMessage(key));
    }

    /**
     * Assert that supplied text is not present in a specific table.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param text
     */
    public void assertTextNotInTable(String tableSummaryOrId, String text) {
        assertTablePresent(tableSummaryOrId);
        Assert.assertTrue("Found text: [" + text + "] in table [" +
                tableSummaryOrId + "]",
                !dialog.isTextInTable(tableSummaryOrId, text));
    }

    /**
     * Assert that none of a set of text values are present in a specific table.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param text Array of text values
     */
    public void assertTextNotInTable(String tableSummaryOrId, String[] text) {
        for (int i = 0; i < text.length; i++) {
            assertTextNotInTable(tableSummaryOrId, text[i]);
        }
    }

    /**
     * Assert that a specific table matches an ExpectedTable.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param expectedTable represents expected values (colspan supported).
     */
    public void assertTableEquals(String tableSummaryOrId, ExpectedTable expectedTable) {
        assertTableEquals(tableSummaryOrId, expectedTable.getExpectedStrings());
    }

    /**
     * Assert that a specific table matches a matrix of supplied text values.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param expectedCellValues double dimensional array of expected values
     */
    public void assertTableEquals(String tableSummaryOrId, String[][] expectedCellValues) {
        assertTableRowsEqual(tableSummaryOrId, 0, expectedCellValues);
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param startRow index of start row for comparison
     * @param expectedTable represents expected values (colspan supported).
     */
    public void assertTableRowsEqual(String tableSummaryOrId, int startRow, ExpectedTable expectedTable) {
        assertTableRowsEqual(tableSummaryOrId, startRow, expectedTable.getExpectedStrings());
    }

    /**
     * Assert that a range of rows for a specific table matches a matrix of supplied text values.
     *
     * @param tableSummaryOrId summary or id attribute value of table
     * @param startRow index of start row for comparison
     * @param expectedCellValues double dimensional array of expected values
     */
    public void assertTableRowsEqual(String tableSummaryOrId, int startRow, String[][] expectedCellValues) {
        assertTablePresent(tableSummaryOrId);
        String[][] sparseTableCellValues = dialog.getSparseTableBySummaryOrId(tableSummaryOrId);
        if (expectedCellValues.length > (sparseTableCellValues.length - startRow))
            Assert.fail("Expected rows [" + expectedCellValues.length + "] larger than actual rows in range being compared" +
                    " [" + (sparseTableCellValues.length - startRow) + "].");
        for (int i = 0; i < expectedCellValues.length; i++) {
            String[] row = expectedCellValues[i];
            for (int j = 0; j < row.length; j++) {
                if (row.length != sparseTableCellValues[i].length)
                    Assert.fail("Unequal number of columns for row " + i + " of table " + tableSummaryOrId +
                            ". Expected [" + row.length + "] found [" + sparseTableCellValues[i].length + "].");
                String expectedString = row[j];
                Assert.assertEquals("Expected " + tableSummaryOrId + " value at [" + i + "," + j + "] not found.",
                        expectedString, context.toEncodedString(sparseTableCellValues[i + startRow][j].trim()));
            }
        }
    }

    /**
     * Assert that a form input element with a given name is present.
     *
     * @param formElementName
     */
    public void assertFormElementPresent(String formElementName) {
        assertFormPresent();
        Assert.assertTrue("Did not find form element with name [" + formElementName + "].",
                dialog.hasFormParameterNamed(formElementName));
    }

    /**
     * Assert that a form input element with a given name is not present.
     *
     * @param formElementName
     */
    public void assertFormElementNotPresent(String formElementName) {
        assertFormPresent();
        try {
            Assert.assertTrue("Found form element with name [" + formElementName + "] when not expected.",
                    !dialog.hasFormParameterNamed(formElementName));
        } catch (UnableToSetFormException e) {
            // assertFormControlNotPresent
        }
    }

    /**
     * Assert that a form input element with a given label is present.
     *
     * @param formElementLabel label preceding form element.
     * @see #setFormElementWithLabel(String,String)
     */
    public void assertFormElementPresentWithLabel(String formElementLabel) {
        Assert.assertTrue("Did not find form element with label [" + formElementLabel + "].",
                dialog.hasFormParameterLabeled(formElementLabel));
    }

    /**
     * Assert that a form input element with a given label is not present.
     *
     * @param formElementLabel label preceding form element.
     * @see #setFormElementWithLabel(String,String)
     */
    public void assertFormElementNotPresentWithLabel(String formElementLabel) {
        Assert.assertFalse("Found form element with label [" + formElementLabel + "].",
                dialog.hasFormParameterLabeled(formElementLabel));
    }

    /**
     * Assert that there is a form present.
     *
     */
    public void assertFormPresent() {
        Assert.assertTrue("No form present", dialog.hasForm());
    }

    /**
     * Assert that there is a form with the specified name or id present.
     * @param nameOrID
     */
    public void assertFormPresent(String nameOrID) {
        Assert.assertTrue("No form present with name or id [" + nameOrID + "]", dialog.hasForm(nameOrID));
    }

    /**
     * Assert that there is not a form present.
     *
     */
    public void assertFormNotPresent() {
    	Assert.assertFalse("A form is present", dialog.hasForm());
    }

    /**
     * Assert that there is not a form with the specified name or id present.
     * @param nameOrID
     */
    public void assertFormNotPresent(String nameOrID) {
    	Assert.assertFalse("Form present with name or id [" + nameOrID + "]", dialog.hasForm(nameOrID));
    }
    
    /**
     * Assert that a specific form element has an expected value.
     *
     * @param formElementName
     * @param expectedValue
     */
    public void assertFormElementEquals(String formElementName, String expectedValue) {
        assertFormPresent();
        Assert.assertEquals(expectedValue, dialog.getFormParameterValue(formElementName));
    }

    /**
     * Assert that a form element had no value / is empty.
     *
     * @param formElementName
     */
    public void assertFormElementEmpty(String formElementName) {
        assertFormPresent();
        Assert.assertEquals("", dialog.getFormParameterValue(formElementName));
    }

    /**
     * Assert that a specific checkbox is selected.
     *
     * @param checkBoxName
     */
    public void assertCheckboxSelected(String checkBoxName) {
        assertFormPresent();
        assertFormElementPresent(checkBoxName);
        Assert.assertEquals("on", dialog.getFormParameterValue(checkBoxName));
    }

    /**
     * Assert that a specific checkbox is not selected.
     *
     * @param checkBoxName
     */
    public void assertCheckboxNotSelected(String checkBoxName) {
        assertFormPresent();
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
     * @param expectedValues expected values for the select box.
     */
    public void assertOptionValuesEqual(String selectName, String[] expectedValues) {
        assertFormElementPresent(selectName);
        assertArraysEqual(expectedValues, dialog.getOptionValuesFor(selectName));

    }

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
     * @param optionValues expected values for the select box.
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
        assertFormPresent();
        Assert.assertTrue("Submit Button [" + buttonName + "] not found.", dialog.hasSubmitButton(buttonName));
    }

    /**
     * Assert that a submit button with a given name is not present.
     *
     * @param buttonName
     */
    public void assertSubmitButtonNotPresent(String buttonName) {
        assertFormPresent();
        Assert.assertFalse("Submit Button [" + buttonName + "] found.", dialog.hasSubmitButton(buttonName));
    }

    /**
     * Assert that a submit button with a given name and value is present.
     *
     * @param buttonName
     * @param expectedValue
     */
    public void assertSubmitButtonValue(String buttonName, String expectedValue) {
        assertFormPresent();
        assertSubmitButtonPresent(buttonName);
        Assert.assertEquals(expectedValue, dialog.getSubmitButtonValue(buttonName));
    }

    /**
     * Assert that a button with a given id is present.
     *
     * @param buttonId
     */
    public void assertButtonPresent(String buttonId) {
        assertFormPresent();
        Assert.assertTrue("Button [" + buttonId + "] not found.", dialog.hasButton(buttonId));
    }

    /**
     * Assert that a button with a given id is not present.
     *
     * @param buttonId
     */
    public void assertButtonNotPresent(String buttonId) {
        assertFormPresent();
        Assert.assertFalse("Button [" + buttonId + "] found.", dialog.hasButton(buttonId));
    }


    /**
     * Assert that a link with a given id is present in the response.
     *
     * @param linkId
     */
    public void assertLinkPresent(String linkId) {
        Assert.assertTrue("Unable to find link with id [" + linkId + "]", dialog.isLinkPresent(linkId));
    }

    /**
     * Assert that no link with the given id is present in the response.
     *
     * @param linkId
     */
    public void assertLinkNotPresent(String linkId) {
        Assert.assertTrue("link with id [" + linkId + "] found in response", !dialog.isLinkPresent(linkId));
    }

    /**
     * Assert that a link containing the supplied text is present.
     *
     * @param linkText
     */
    public void assertLinkPresentWithText(String linkText) {
        Assert.assertTrue("Link with text [" + linkText + "] not found in response.", dialog.isLinkPresentWithText(linkText));
    }

    /**
     * Assert that no link containing the supplied text is present.
     *
     * @param linkText
     */
    public void assertLinkNotPresentWithText(String linkText) {
        Assert.assertTrue("Link with text [" + linkText + "] found in response.", !dialog.isLinkPresentWithText(linkText));
    }

    /**
     * Assert that a link containing the supplied text is present.
     *
     * @param linkText
     * @param index The 0-based index, when more than one link with the same
     *              text is expected.
     */
    public void assertLinkPresentWithText(String linkText, int index) {
        Assert.assertTrue(
            "Link with text ["
                + linkText
                + "] and index "
                + index
                + " not found in response.",
            dialog.isLinkPresentWithText(linkText, index));
    }

    /**
     * Assert that no link containing the supplied text is present.
     *
     * @param linkText
     * @param index The 0-based index, when more than one link with the same
     *              text is expected.
     */
    public void assertLinkNotPresentWithText(String linkText, int index) {
        Assert.assertTrue(
            "Link with text ["
                + linkText
                + "] and index "
                + index
                + " found in response.",
            !dialog.isLinkPresentWithText(linkText, index));
    }

    /**
     * Assert that a link containing a specified image is present.
     *
     * @param imageFileName A suffix of the image's filename; for example, to match
     *                      <tt>"images/my_icon.png"</tt>, you could just pass in
     *                      <tt>"my_icon.png"</tt>.
     */
    public void assertLinkPresentWithImage(String imageFileName) {
        Assert.assertTrue("Link with image file [" + imageFileName + "] not found in response.", dialog.isLinkPresentWithImage(imageFileName));
    }

    /**
     * Assert that a link containing a specified image is not present.
     *
     * @param imageFileName A suffix of the image's filename; for example, to match
     *                      <tt>"images/my_icon.png"</tt>, you could just pass in
     *                      <tt>"my_icon.png"</tt>.
     */
    public void assertLinkNotPresentWithImage(String imageFileName) {
        Assert.assertTrue("Link with image file [" + imageFileName + "] found in response.", !dialog.isLinkPresentWithImage(imageFileName));
    }

    /**
     * Assert that an element with a given id is present.
     *
     * @param anID element id to test for.
     */
    public void assertElementPresent(String anID) {
        Assert.assertNotNull("Unable to locate element with id \"" + anID + "\"", dialog.getElement(anID));
    }

    /**
     * Assert that an element with a given id is not present.
     *
     * @param anID element id to test for.
     */
    public void assertElementNotPresent(String anID) {
        Assert.assertNull("Located element with id \"" + anID + "\"", dialog.getElement(anID));
    }

    /**
     * Assert that a given element contains specific text.
     *
     * @param elementID id of element to be inspected.
     * @param text to check for.
     */
    public void assertTextInElement(String elementID, String text) {
        Element element = dialog.getElement(elementID);
        Assert.assertNotNull("Unable to locate element with id \"" + elementID + "\"", element);
        Assert.assertTrue("Unable to locate [" + text + "] in element \"" + elementID + "\"", dialog.isTextInElement(element, text));
    }

    /**
     * Assert that a window with the given name is open.
     *
     * @param windowName
     */
    public void assertWindowPresent(String windowName) {
        Assert.assertNotNull("Unable to locate window [" + windowName + "].", dialog.getWindow(windowName));
    }

    /**
     * Assert that a frame with the given name is present.
     *
     * @param frameName
     */
    public void assertFramePresent(String frameName) {
        Assert.assertNotNull("Unable to locate frame [" + frameName + "].", dialog.getFrame(frameName));
    }
    
    /** 
     *  Checks to see if a cookie is present in the response.
     *  Contributed by Vivek Venugopalan.
     * 
     * @param cookieName  The cookie name 
     */	
    public void assertCookiePresent(String cookieName) {
		Assert.assertTrue("Could not find Cookie : [" + cookieName + "]", dialog.hasCookie(cookieName));
	}
    
    public void assertCookieValueEquals(String cookieName, String expectedValue) {
    	assertCookiePresent(cookieName);
    	Assert.assertEquals(expectedValue, dialog.getCookieValue(cookieName));
    }
    
    public void dumpCookies() {
    	dumpCookies(System.out);
    }
    
    public void dumpCookies(PrintStream stream) {
    	dialog.dumpCookies(stream);
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
     * @param formElementName name of form element.
     * @param value
     */
    public void setFormElement(String formElementName, String value) {
        assertFormPresent();
        assertFormElementPresent(formElementName);
        dialog.setFormParameter(formElementName, value);
    }

    /**
     * Set the value of a form input element. The element is identified by a
     * preceding "label". For example, in "<code>Home Address : &lt;input
     * type='text' name='home_addr' /&gt;</code>", "<code>Home Address</code>"
     * could be used as a label. The label must appear within the associated
     * <code>&lt;form&gt;</code> tag.
     *
     * @param formElementLabel label preceding form element.
     * @param value
     */
    protected void setFormElementWithLabel(String formElementLabel,
                                           String value) {
        String name = dialog.getFormElementNameForLabel(formElementLabel);
        Assert.assertNotNull("Did not find form element with label [" + formElementLabel + "].", name);
        dialog.setFormParameter(name, value);
    }

    /**
     * Select a specified checkbox.
     *
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void checkCheckbox(String checkBoxName) {
        assertFormElementPresent(checkBoxName);
        dialog.setFormParameter(checkBoxName, "on");
    }

    public void checkCheckbox(String checkBoxName, String value) {
        assertFormElementPresent(checkBoxName);
        dialog.updateFormParameter(checkBoxName, value);
    }

    /**
     * Deselect a specified checkbox.
     *
     * @param checkBoxName name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        assertFormElementPresent(checkBoxName);
        dialog.removeFormParameter(checkBoxName);
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        assertFormElementPresent(checkBoxName);
        dialog.removeFormParameterWithValue(checkBoxName, value);
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
        assertFormPresent();
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
     * Reset the current form.
     */
    public void reset() {
        dialog.reset();
    }

    /**
     * Navigate by selection of a link containing given text.
     *
     * @param linkText
     */
    public void clickLinkWithText(String linkText) {
        assertLinkPresentWithText(linkText);
        dialog.clickLinkWithText(linkText);
    }

    /**
     * Navigate by selection of a link containing given text.
     *
     * @param linkText
     * @param index The 0-based index, when more than one link with the same
     *              text is expected.
     */
    public void clickLinkWithText(String linkText, int index) {
        assertLinkPresentWithText(linkText, index);
        dialog.clickLinkWithText(linkText, index);
    }

    /**
     * Search for labelText in the document, then search forward until
     * finding a link called linkText.  Click it.
     */
    public void clickLinkWithTextAfterText(String linkText, String labelText) {
        dialog.clickLinkWithTextAfterText(linkText, labelText);
    }

    /**
     * Click the button with the given id.
     *
     * @param buttonId
     */
    public void clickButton(String buttonId) {
        assertButtonPresent(buttonId);
        dialog.clickButton(buttonId);
    }

    /**
     * Navigate by selection of a link with a given image.
     *
     * @param imageFileName A suffix of the image's filename; for example, to match
     *                      <tt>"images/my_icon.png"</tt>, you could just pass in
     *                      <tt>"my_icon.png"</tt>.
     */
    public void clickLinkWithImage(String imageFileName) {
        assertLinkPresentWithImage(imageFileName);
        dialog.clickLinkWithImage(imageFileName);
    }


    /**
     * Navigate by selection of a link with given id.
     *
     * @param linkId id of link
     */
    public void clickLink(String linkId) {
        assertLinkPresent(linkId);
        dialog.clickLink(linkId);
    }

//Window and Frame Navigation Methods

    /**
     * Make a given window active (current response will be window's contents).
     *
     * @param windowName
     */
    public void gotoWindow(String windowName) {
        assertWindowPresent(windowName);
        dialog.gotoWindow(windowName);
    }

    /**
     * Make the root window active.
     */
    public void gotoRootWindow() {
        dialog.gotoRootWindow();
    }

    /**
     * Make the named frame active (current response will be frame's contents).
     *
     * @param frameName
     */
    public void gotoFrame(String frameName) {
        dialog.gotoFrame(frameName);
    }

    /**
     *  Patch sumbitted by Alex Chaffee.
     */
    public void gotoPage(String url) {
        dialog.gotoPage(createUrl(url));
    }

//Debug methods


    /**
     * Dump html of current response to System.out - for debugging purposes.
     *
     * @param stream
     */
    public void dumpResponse() {
    	dialog.dumpResponse();
    }
    
    /**
     * Dump html of current response to a specified stream - for debugging purposes.
     *
     * @param stream
     */
    public void dumpResponse(PrintStream stream) {
    	dialog.dumpResponse(stream);
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param stream
     */
    public void dumpTable(String tableNameOrId, PrintStream stream) {
    	dialog.dumpTable(tableNameOrId, stream);
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param table
     */
    public void dumpTable(String tableNameOrId, String[][] table) {
    	dialog.dumpTable(tableNameOrId, table);
    }

    /**
     * Dump the table as the 2D array that is used for assertions - for debugging purposes.
     *
     * @param tableNameOrId
     * @param table
     * @param stream
     */
    public void dumpTable(String tableNameOrId, String[][] table, PrintStream stream) {
    	dialog.dumpTable(tableNameOrId, table, stream);
    }

}
