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
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.exception.UnableToSetFormException;
import net.sourceforge.jwebunit.util.ExceptionUtility;

import org.w3c.dom.Element;

/**
 * Provides a high-level API for basic web application navigation and validation
 * by wrapping HttpUnit and providing Junit assertions. It supports use of a
 * property file for web resources (a la Struts), though a resource file for the
 * app is not required.
 * 
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class WebTester {
	private IJWebUnitDialog dialog = null;

	private TestContext testContext = null;

	private boolean tableEmptyCellCompression = true;

	/**
	 * This is the reference to the testing engine registry that contains the
	 * list of known testing engines for jWebUnit to use for testing.
	 */
	private TestingEngineRegistry testingEngineRegistry = new TestingEngineRegistry();

	/**
	 * This is the testing engine key that the webtester will use to find the
	 * correct testing engine from the registry.
	 */
	private String testingEngineKey = null;

	public WebTester() {
		super();
	}

	/**
	 * Provides access to the Dialog (testing engine) for subclasses - in case
	 * functionality not yet wrappered required by test.
	 * 
	 * If the dialog is not explicitly set the jWebUnit framework will default
	 * to using the orignal testing engine, which is, httpunit.
	 * 
	 * @return IJWebUnitDialog instance used to wrapper httpunit conversation.
	 */
	public IJWebUnitDialog getDialog() {
		if (dialog == null) {
			//defaulting to the HttpUnitDialog implementation.
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
		Class theClass = getTestingEngineRegistry().getTestingEngineClass(
				theTestingEngineKey);
		try {
			theIJWebUnitDialog = (IJWebUnitDialog) theClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Can't Instantiate Testing Engine with class [" + theClass
					+ "] with key [" + theTestingEngineKey + "]." + "\nError: "
					+ ExceptionUtility.stackTraceToString(e));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("IllegalAccessException using class [" + theClass
					+ "] with key [" + theTestingEngineKey + "]." + "\nError: "
					+ ExceptionUtility.stackTraceToString(e));
		}

		return theIJWebUnitDialog;
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
			//defaulting to the original implementation.
			testContext = new TestContext();
		}
		return testContext;
	}

	/**
	 * Allows setting an external test testContext class that might be extended from
	 * TestContext. Example: setTestContext(new CompanyATestContext());
	 * 
	 * CompanyATestContext extends TestContext.
	 * 
	 * @param aTestContext
	 */
	public void setTestContext(TestContext aTestContext) {
		testContext = aTestContext;
	}

	/**
	 * Begin conversation at a url relative to the application root.
	 * 
	 * @param relativeURL
	 */
	public void beginAt(String aRelativeURL) {
		getDialog().beginAt(createUrl(aRelativeURL), testContext);
	}

	private String createUrl(String aSuffix) {
		aSuffix = aSuffix.startsWith("/") ? aSuffix.substring(1) : aSuffix;
		return getTestContext().getBaseUrl() + aSuffix;
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
		return testContext.toEncodedString(message);
	}

	//Assertions

	/**
	 * Assert title of current html page in conversation matches an expected
	 * value.
	 * 
	 * @param title
	 *            expected title value
	 */
	public void assertTitleEquals(String title) {
		Assert.assertEquals(title, getDialog().getResponsePageTitle());
	}

	/**
	 * Assert title of current html page matches the value of a specified web
	 * resource.
	 * 
	 * @param titleKey
	 *            web resource key for title
	 */
	public void assertTitleEqualsKey(String titleKey) {
		Assert.assertEquals(getMessage(titleKey), getDialog()
				.getResponsePageTitle());
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
	 * Assert that supplied text is present.
	 * 
	 * @param text
	 */
	public void assertTextPresent(String text) {
		if (!getDialog().isTextInResponse(text))
			Assert.fail("Expected text not found in response: [" + text + "]");
	}

	/**
	 * Assert that a web resource's value is not present.
	 * 
	 * @param key
	 *            web resource name
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
		if (getDialog().isTextInResponse(text))
			Assert.fail("Text found in response when not expected: [" + text
					+ "]");
	}

	/**
	 * Assert that a table with a given summary or id value is present.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 */
	public void assertTablePresent(String tableSummaryOrId) {
		if (getDialog().getWebTableBySummaryOrId(tableSummaryOrId) == null)
			Assert.fail("Unable to locate table \"" + tableSummaryOrId + "\"");
	}

	/**
	 * Assert that a table with a given summary or id value is not present.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 */
	public void assertTableNotPresent(String tableSummaryOrId) {
		if (getDialog().getWebTableBySummaryOrId(tableSummaryOrId) != null)
			Assert.fail("Located table \"" + tableSummaryOrId + "\"");
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
	public void assertKeyInTable(String tableSummaryOrId, String key) {
		assertTextInTable(tableSummaryOrId, getMessage(key));
	}

	/**
	 * Assert that supplied text is present in a specific table.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param text
	 */
	public void assertTextInTable(String tableSummaryOrId, String text) {
		assertTablePresent(tableSummaryOrId);
		Assert.assertTrue("Could not find: [" + text + "]" + "in table ["
				+ tableSummaryOrId + "]", getDialog().isTextInTable(
				tableSummaryOrId, text));
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
	public void assertKeysInTable(String tableSummaryOrId, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			assertKeyInTable(tableSummaryOrId, keys[i]);
		}
	}

	/**
	 * Assert that a set of text values are all present in a specific table.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param text
	 *            Array of expected text values.
	 */
	public void assertTextInTable(String tableSummaryOrId, String[] text) {
		for (int i = 0; i < text.length; i++) {
			assertTextInTable(tableSummaryOrId, text[i]);
		}
	}

	/**
	 * Assert that the value of a given web resource is not present in a
	 * specific table.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param key
	 *            web resource name
	 */
	public void assertKeyNotInTable(String tableSummaryOrId, String key) {
		assertTextNotInTable(tableSummaryOrId, getMessage(key));
	}

	/**
	 * Assert that supplied text is not present in a specific table.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param text
	 */
	public void assertTextNotInTable(String tableSummaryOrId, String text) {
		assertTablePresent(tableSummaryOrId);
		Assert.assertTrue("Found text: [" + text + "] in table ["
				+ tableSummaryOrId + "]", !getDialog().isTextInTable(
				tableSummaryOrId, text));
	}

	/**
	 * Assert that none of a set of text values are present in a specific table.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param text
	 *            Array of text values
	 */
	public void assertTextNotInTable(String tableSummaryOrId, String[] text) {
		for (int i = 0; i < text.length; i++) {
			assertTextNotInTable(tableSummaryOrId, text[i]);
		}
	}

	/**
	 * Assert that a specific table matches an ExpectedTable.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param expectedTable
	 *            represents expected values (colspan supported).
	 */
	public void assertTableEquals(String tableSummaryOrId,
			ExpectedTable expectedTable) {
		assertTableEquals(tableSummaryOrId, expectedTable.getExpectedStrings());
	}

	/**
	 * Assert that a specific table matches a matrix of supplied text values.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param expectedCellValues
	 *            double dimensional array of expected values
	 */
	public void assertTableEquals(String tableSummaryOrId,
			String[][] expectedCellValues) {
		assertTableRowsEqual(tableSummaryOrId, 0, expectedCellValues);
	}

	/**
	 * Assert that a range of rows for a specific table matches a matrix of
	 * supplied text values.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param startRow
	 *            index of start row for comparison
	 * @param expectedTable
	 *            represents expected values (colspan supported).
	 */
	public void assertTableRowsEqual(String tableSummaryOrId, int startRow,
			ExpectedTable expectedTable) {
		assertTableRowsEqual(tableSummaryOrId, startRow, expectedTable
				.getExpectedStrings());
	}

	/**
	 * Assert that a range of rows for a specific table matches a matrix of
	 * supplied text values.
	 * 
	 * @param tableSummaryOrId
	 *            summary or id attribute value of table
	 * @param startRow
	 *            index of start row for comparison
	 * @param expectedCellValues
	 *            double dimensional array of expected values
	 */
	public void assertTableRowsEqual(String tableSummaryOrId, int startRow,
			String[][] expectedCellValues) {
		assertTablePresent(tableSummaryOrId);
		String[][] actualTableCellValues;
		if (tableEmptyCellCompression) {
			actualTableCellValues = getDialog().getSparseTableBySummaryOrId(
					tableSummaryOrId);
		} else {
			actualTableCellValues = getDialog().getWebTableBySummaryOrId(
					tableSummaryOrId).asText();
		}
		if (expectedCellValues.length > (actualTableCellValues.length - startRow))
			Assert.fail("Expected rows [" + expectedCellValues.length
					+ "] larger than actual rows in range being compared"
					+ " [" + (actualTableCellValues.length - startRow) + "].");
		for (int i = 0; i < expectedCellValues.length; i++) {
			String[] row = expectedCellValues[i];
			for (int j = 0; j < row.length; j++) {
				if (row.length != actualTableCellValues[i].length)
					Assert.fail("Unequal number of columns for row " + i
							+ " of table " + tableSummaryOrId + ". Expected ["
							+ row.length + "] found ["
							+ actualTableCellValues[i].length + "].");
				String expectedString = row[j];
				Assert.assertEquals("Expected " + tableSummaryOrId
						+ " value at [" + i + "," + j + "] not found.",
						expectedString, testContext
								.toEncodedString(actualTableCellValues[i
										+ startRow][j].trim()));
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
		Assert.assertTrue("Did not find form element with name ["
				+ formElementName + "].", getDialog().hasFormParameterNamed(
				formElementName));
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
					+ formElementName + "] when not expected.", !getDialog()
					.hasFormParameterNamed(formElementName));
		} catch (UnableToSetFormException e) {
			// assertFormControlNotPresent
		}
	}

	/**
	 * Assert that a form input element with a given label is present.
	 * 
	 * @param formElementLabel
	 *            label preceding form element.
	 * @see #setFormElementWithLabel(String,String)
	 */
	public void assertFormElementPresentWithLabel(String formElementLabel) {
		Assert.assertTrue("Did not find form element with label ["
				+ formElementLabel + "].", getDialog().hasFormParameterLabeled(
				formElementLabel));
	}

	/**
	 * Assert that a form input element with a given label is not present.
	 * 
	 * @param formElementLabel
	 *            label preceding form element.
	 * @see #setFormElementWithLabel(String,String)
	 */
	public void assertFormElementNotPresentWithLabel(String formElementLabel) {
		Assert.assertFalse("Found form element with label [" + formElementLabel
				+ "].", getDialog().hasFormParameterLabeled(formElementLabel));
	}

	/**
	 * Assert that there is a form present.
	 *  
	 */
	public void assertFormPresent() {
		Assert.assertTrue("No form present", getDialog().hasForm());
	}

	/**
	 * Assert that there is a form with the specified name or id present.
	 * 
	 * @param nameOrID
	 */
	public void assertFormPresent(String nameOrID) {
		Assert.assertTrue("No form present with name or id [" + nameOrID + "]",
				getDialog().hasForm(nameOrID));
	}

	/**
	 * Assert that there is not a form present.
	 *  
	 */
	public void assertFormNotPresent() {
		Assert.assertFalse("A form is present", getDialog().hasForm());
	}

	/**
	 * Assert that there is not a form with the specified name or id present.
	 * 
	 * @param nameOrID
	 */
	public void assertFormNotPresent(String nameOrID) {
		Assert.assertFalse("Form present with name or id [" + nameOrID + "]",
				getDialog().hasForm(nameOrID));
	}

	/**
	 * Assert that a specific form element has an expected value.
	 * 
	 * @param formElementName
	 * @param expectedValue
	 */
	public void assertFormElementEquals(String formElementName,
			String expectedValue) {
		assertFormElementPresent(formElementName);
		Assert.assertEquals(expectedValue, getDialog().getFormParameterValue(
				formElementName));
	}

	/**
	 * Assert that a form element had no value / is empty.
	 * 
	 * @param formElementName
	 */
	public void assertFormElementEmpty(String formElementName) {
		assertFormElementPresent(formElementName);
		Assert.assertEquals("", getDialog().getFormParameterValue(
				formElementName));
	}

	/**
	 * Assert that a specific checkbox is selected.
	 * 
	 * @param checkBoxName
	 */
	public void assertCheckboxSelected(String checkBoxName) {
		assertFormElementPresent(checkBoxName);
		if (!getDialog().isCheckboxSelected(checkBoxName)) {
			Assert.fail("Checkbox with name [" + checkBoxName
					+ "] was not found selected.");
		}
	}

	/**
	 * Assert that a specific checkbox is not selected.
	 * 
	 * @param checkBoxName
	 */
	public void assertCheckboxNotSelected(String checkBoxName) {
		assertFormElementPresent(checkBoxName);
		if (!getDialog().isCheckboxNotSelected(checkBoxName)) {
			Assert.fail("Checkbox with name [" + checkBoxName
					+ "] was found selected.");
		}
	}

	/**
	 * Assert that a specific option is present in a radio group.
	 * 
	 * @param name
	 *            radio group name.
	 * @param radioOption
	 *            option to test for.
	 */
	public void assertRadioOptionPresent(String name, String radioOption) {
		assertFormElementPresent(name);
		if (!getDialog().hasRadioOption(name, radioOption)) {
			Assert.fail("Unable to find option [" + radioOption
					+ "] in radio group [" + name + "]");
		}
	}

	/**
	 * Assert that a specific option is not present in a radio group.
	 * 
	 * @param name
	 *            radio group name.
	 * @param radioOption
	 *            option to test for.
	 */
	public void assertRadioOptionNotPresent(String name, String radioOption) {
		assertFormElementPresent(name);
		if (getDialog().hasRadioOption(name, radioOption))
			Assert.fail("Found option [" + radioOption + "] in radio group ["
					+ name + "]");
	}

	/**
	 * Assert that a specific option is selected in a radio group.
	 * 
	 * @param name
	 *            radio group name.
	 * @param radioOption
	 *            option to test for selection.
	 */
	public void assertRadioOptionSelected(String name, String radioOption) {
		assertFormElementPresent(name);
		assertFormElementEquals(name, radioOption);
	}

	/**
	 * Assert that a specific option is not selected in a radio group.
	 * 
	 * @param name
	 *            radio group name.
	 * @param radioOption
	 *            option to test for selection.
	 */
	public void assertRadioOptionNotSelected(String name, String radioOption) {
		assertFormElementPresent(name);
		Assert.assertTrue("Radio option " + radioOption + " is not selected",
				!radioOption.equals(getDialog().getFormParameterValue(name)));
	}

	/**
	 * Assert that a specific option is present in a select box.
	 * 
	 * @param selectName
	 *            name of the select element.
	 * @param expectedOption
	 *            option label.
	 */
	public void assertOptionPresent(String selectName, String optionLabel) {
		assertFormElementPresent(selectName);
		Assert.assertTrue("Option [" + optionLabel
				+ "] not found in select element " + selectName, getDialog()
				.hasSelectOption(selectName, optionLabel));
	}

	/**
	 * Assert that a specific option is not present in a select box.
	 * 
	 * @param selectName
	 *            name of the select element.
	 * @param expectedOption
	 *            option label.
	 */
	public void assertOptionNotPresent(String selectName, String optionLabel) {
		assertFormElementPresent(selectName);
		Assert.assertTrue("Option " + optionLabel + " found in select element "
				+ selectName + " when not expected.", !getDialog()
				.hasSelectOption(selectName, optionLabel));
	}

	/**
	 * Assert that the display values of a select element's options match a
	 * given array of strings.
	 * 
	 * @param selectName
	 *            name of the select element.
	 * @param expectedOptions
	 *            expected display values for the select box.
	 */
	public void assertOptionsEqual(String selectName, String[] expectedOptions) {
		assertFormElementPresent(selectName);
		assertArraysEqual(expectedOptions, getDialog()
				.getOptionsFor(selectName));
	}

	/**
	 * Assert that the display values of a select element's options do not match
	 * a given array of strings.
	 * 
	 * @param selectName
	 *            name of the select element.
	 * @param expectedOptions
	 *            expected display values for the select box.
	 */
	public void assertOptionsNotEqual(String selectName,
			String[] expectedOptions) {
		assertFormElementPresent(selectName);
		try {
			assertOptionsEqual(selectName, expectedOptions);
		} catch (AssertionFailedError e) {
			return;
		}
		Assert.fail("Options not expected to be equal");
	}

	/**
	 * Assert that the values of a select element's options match a given array
	 * of strings.
	 * 
	 * @param selectName
	 *            name of the select element.
	 * @param expectedValues
	 *            expected values for the select box.
	 */
	public void assertOptionValuesEqual(String selectName,
			String[] expectedValues) {
		assertFormElementPresent(selectName);
		assertArraysEqual(expectedValues, getDialog().getOptionValuesFor(
				selectName));

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
	 * Assert that the values of a select element's options do not match a given
	 * array of strings.
	 * 
	 * @param selectName
	 *            name of the select element.
	 * @param optionValues
	 *            expected values for the select box.
	 */
	public void assertOptionValuesNotEqual(String selectName,
			String[] optionValues) {
		assertFormElementPresent(selectName);
		try {
			assertOptionValuesEqual(selectName, optionValues);
		} catch (AssertionFailedError e) {
			return;
		}
		Assert.fail("Values not expected to be equal");
	}

	/**
	 * Assert that the currently selected display value of a select box matches
	 * a given value.
	 * 
	 * @param selectName
	 *            name of the select element.
	 * @param option
	 *            expected display value of the selected option.
	 */
	public void assertOptionEquals(String selectName, String option) {
		assertFormElementPresent(selectName);
		Assert.assertEquals(option, getDialog().getSelectedOption(selectName));
	}

	/**
	 * Assert that a submit button with a given name is present.
	 * 
	 * @param buttonName
	 */
	public void assertSubmitButtonPresent(String buttonName) {
		assertFormPresent();
		Assert.assertTrue("Submit Button [" + buttonName + "] not found.",
				getDialog().hasSubmitButton(buttonName));
	}

	/**
	 * Assert that a submit button with a given name is not present.
	 * 
	 * @param buttonName
	 */
	public void assertSubmitButtonNotPresent(String buttonName) {
		assertFormPresent();
		Assert.assertFalse("Submit Button [" + buttonName + "] found.",
				getDialog().hasSubmitButton(buttonName));
	}

	/**
	 * Assert that a submit button with a given name and value is present.
	 * 
	 * @param buttonName
	 * @param buttonValue
	 */
	public void assertSubmitButtonPresent(String buttonName, String buttonValue) {
		assertFormPresent();
		Assert.assertTrue("Submit Button [" + buttonName + "] with value ["
				+ buttonValue + "] not found.", getDialog().hasSubmitButton(
				buttonName, buttonValue));
	}

	/**
	 * Assert that a button with a given id is present.
	 * 
	 * @param buttonId
	 */
	public void assertButtonPresent(String buttonId) {
		assertFormPresent();
		Assert.assertTrue("Button [" + buttonId + "] not found.", getDialog()
				.hasButton(buttonId));
	}

	/**
	 * Assert that a button with a given text is present.
	 * 
	 * @param text
	 */
	public void assertButtonPresentWithText(String text) {
		Assert.assertTrue("Did not find button with text [" + text + "].",
				getDialog().hasButtonWithText(text));
	}

	/**
	 * Assert that a button with a given text is not present.
	 * 
	 * @param text
	 */
	public void assertButtonNotPresentWithText(String text) {
		Assert.assertFalse("Found button with text [" + text + "].",
				getDialog().hasButtonWithText(text));
	}

	/**
	 * Assert that a button with a given id is not present.
	 * 
	 * @param buttonId
	 */
	public void assertButtonNotPresent(String buttonId) {
		assertFormPresent();
		Assert.assertFalse("Button [" + buttonId + "] found.", getDialog()
				.hasButton(buttonId));
	}

	/**
	 * Assert that a link with a given id is present in the response.
	 * 
	 * @param linkId
	 */
	public void assertLinkPresent(String linkId) {
		Assert.assertTrue("Unable to find link with id [" + linkId + "]",
				getDialog().isLinkPresent(linkId));
	}

	/**
	 * Assert that no link with the given id is present in the response.
	 * 
	 * @param linkId
	 */
	public void assertLinkNotPresent(String linkId) {
		Assert.assertTrue("link with id [" + linkId + "] found in response",
				!getDialog().isLinkPresent(linkId));
	}

	/**
	 * Assert that a link containing the supplied text is present.
	 * 
	 * @param linkText
	 */
	public void assertLinkPresentWithText(String linkText) {
		Assert.assertTrue("Link with text [" + linkText
				+ "] not found in response.", getDialog()
				.isLinkPresentWithText(linkText));
	}

	/**
	 * Assert that no link containing the supplied text is present.
	 * 
	 * @param linkText
	 */
	public void assertLinkNotPresentWithText(String linkText) {
		Assert.assertTrue("Link with text [" + linkText
				+ "] found in response.", !getDialog().isLinkPresentWithText(
				linkText));
	}

	/**
	 * Assert that a link containing the supplied text is present.
	 * 
	 * @param linkText
	 * @param index
	 *            The 0-based index, when more than one link with the same text
	 *            is expected.
	 */
	public void assertLinkPresentWithText(String linkText, int index) {
		Assert.assertTrue("Link with text [" + linkText + "] and index ["
				+ index + "] not found in response.", getDialog()
				.isLinkPresentWithText(linkText, index));
	}

	/**
	 * Assert that no link containing the supplied text is present.
	 * 
	 * @param linkText
	 * @param index
	 *            The 0-based index, when more than one link with the same text
	 *            is expected.
	 */
	public void assertLinkNotPresentWithText(String linkText, int index) {
		Assert.assertTrue("Link with text [" + linkText + "] and index "
				+ index + " found in response.", !getDialog()
				.isLinkPresentWithText(linkText, index));
	}

	//BEGIN RFE 996031...

	/**
	 * Assert that a link containing the Exact text is present.
	 * 
	 * @param linkText
	 */
	public void assertLinkPresentWithExactText(String linkText) {
		Assert.assertTrue("Link with Exact text [" + linkText
				+ "] not found in response.", getDialog()
				.isLinkPresentWithExactText(linkText));
	}

	/**
	 * Assert that no link containing the Exact text is present.
	 * 
	 * @param linkText
	 */
	public void assertLinkNotPresentWithExactText(String linkText) {
		Assert.assertTrue("Link with Exact text [" + linkText
				+ "] found in response.", !getDialog()
				.isLinkPresentWithExactText(linkText));
	}

	/**
	 * Assert that a link containing the Exact text is present.
	 * 
	 * @param linkText
	 * @param index
	 *            The 0-based index, when more than one link with the same text
	 *            is expected.
	 */
	public void assertLinkPresentWithExactText(String linkText, int index) {
		Assert.assertTrue("Link with Exact text [" + linkText + "] and index ["
				+ index + "] not found in response.", getDialog()
				.isLinkPresentWithExactText(linkText, index));
	}

	/**
	 * Assert that no link containing the Exact text is present.
	 * 
	 * @param linkText
	 * @param index
	 *            The 0-based index, when more than one link with the same text
	 *            is expected.
	 */
	public void assertLinkNotPresentWithExactText(String linkText, int index) {
		Assert.assertTrue("Link with Exact text [" + linkText + "] and index "
				+ index + " found in response.", !getDialog()
				.isLinkPresentWithExactText(linkText, index));
	}

	//END RFE 996031...

	/**
	 * Assert that a link containing a specified image is present.
	 * 
	 * @param imageFileName
	 *            A suffix of the image's filename; for example, to match
	 *            <tt>"images/my_icon.png"</tt>, you could just pass in
	 *            <tt>"my_icon.png"</tt>.
	 */
	public void assertLinkPresentWithImage(String imageFileName) {
		Assert.assertTrue("Link with image file [" + imageFileName
				+ "] not found in response.", getDialog()
				.isLinkPresentWithImage(imageFileName));
	}

	/**
	 * Assert that a link containing a specified image is not present.
	 * 
	 * @param imageFileName
	 *            A suffix of the image's filename; for example, to match
	 *            <tt>"images/my_icon.png"</tt>, you could just pass in
	 *            <tt>"my_icon.png"</tt>.
	 */
	public void assertLinkNotPresentWithImage(String imageFileName) {
		Assert.assertTrue("Link with image file [" + imageFileName
				+ "] found in response.", !getDialog().isLinkPresentWithImage(
				imageFileName));
	}

	/**
	 * Assert that an element with a given id is present.
	 * 
	 * @param anID
	 *            element id to test for.
	 */
	public void assertElementPresent(String anID) {
		Assert.assertNotNull("Unable to locate element with id \"" + anID
				+ "\"", getDialog().getElement(anID));
	}

	/**
	 * Assert that an element with a given id is not present.
	 * 
	 * @param anID
	 *            element id to test for.
	 */
	public void assertElementNotPresent(String anID) {
		Assert.assertNull("Located element with id \"" + anID + "\"",
				getDialog().getElement(anID));
	}

	/**
	 * Assert that a given element contains specific text.
	 * 
	 * @param elementID
	 *            id of element to be inspected.
	 * @param text
	 *            to check for.
	 */
	public void assertTextInElement(String elementID, String text) {
		Element element = getDialog().getElement(elementID);
		Assert.assertNotNull("Unable to locate element with id \"" + elementID
				+ "\"", element);
		Assert.assertTrue("Unable to locate [" + text + "] in element \""
				+ elementID + "\"", getDialog().isTextInElement(element, text));
	}

	public void assertTextNotInElement(String elementID, String text) {
		assertElementPresent(elementID);
		Element element = getDialog().getElement(elementID);
		Assert.assertNotNull("Unable to locate element with id \"" + elementID
				+ "\"", element);
		Assert.assertFalse("Text [" + text + "] found in element [" + elementID
				+ "] when not expected", getDialog().isTextInElement(element,
				text));
	}

	/**
	 * Assert that a window with the given name is open.
	 * 
	 * @param windowName
	 */
	public void assertWindowPresent(String windowName) {
		Assert.assertNotNull("Unable to locate window [" + windowName + "].",
				getDialog().getWindow(windowName));
	}

	/**
	 * Assert that at least one window with the given title is open.
	 * 
	 * @param title
	 */
	public void assertWindowPresentWithTitle(String title) {
		Assert.assertNotNull("Unable to locate window with title [" + title
				+ "].", getDialog().getWindowByTitle(title));
	}

	/**
	 * Assert that a frame with the given name is present.
	 * 
	 * @param frameName
	 */
	public void assertFramePresent(String frameName) {
		Assert.assertNotNull("Unable to locate frame [" + frameName + "].",
				getDialog().getFrame(frameName));
	}

	/**
	 * Checks to see if a cookie is present in the response. Contributed by
	 * Vivek Venugopalan.
	 * 
	 * @param cookieName
	 *            The cookie name
	 */
	public void assertCookiePresent(String cookieName) {
		Assert.assertTrue("Could not find Cookie : [" + cookieName + "]",
				getDialog().hasCookie(cookieName));
	}

	public void assertCookieValueEquals(String cookieName, String expectedValue) {
		assertCookiePresent(cookieName);
		Assert.assertEquals(expectedValue, getDialog().getCookieValue(
				cookieName));
	}

	public void dumpCookies() {
		dumpCookies(System.out);
	}

	public void dumpCookies(PrintStream stream) {
		getDialog().dumpCookies(stream);
	}

	// is Pattern methods

	/**
	 * Return true if given text is present anywhere in the current response.
	 * 
	 * @param text
	 *            string to check for.
	 */
	public boolean isTextInResponse(String text) {
		return getDialog().isTextInResponse(text);
	}

	//Form interaction methods

	/**
	 * Begin interaction with a specified form. If form interaction methods are
	 * called without explicitly calling this method first, jWebUnit will
	 * attempt to determine itself which form is being manipulated.
	 * 
	 * It is not necessary to call this method if their is only one form on the
	 * current page.
	 * 
	 * @param nameOrId
	 *            name or id of the form to work with. Gets the value of a form
	 *            input element. Allows getting information from a form element.
	 *            Also, checks assertions as well.
	 * 
	 * @param formElementName
	 *            name of form element.
	 * @param value
	 */
	public String getFormElementValue(String formElementName) {
		assertFormPresent();
		assertFormElementPresent(formElementName);
		return getDialog().getFormParameterValue(formElementName);
	}

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
	public void setWorkingForm(String nameOrId) {
		getDialog().setWorkingForm(nameOrId);
	}

	/**
	 * Set the value of a form input element.
	 * 
	 * @param formElementName
	 *            name of form element.
	 * @param value
	 */
	public void setFormElement(String formElementName, String value) {
		assertFormPresent();
		assertFormElementPresent(formElementName);
		getDialog().setFormParameter(formElementName, value);
	}

	/**
	 * Set the value of a form input element. The element is identified by a
	 * preceding "label". For example, in "<code>Home Address : &lt;input
	 * type='text' name='home_addr' /&gt;</code>", "
	 * <code>Home Address</code>" could be used as a label. The label must
	 * appear within the associated <code>&lt;form&gt;</code> tag.
	 * 
	 * @param formElementLabel
	 *            label preceding form element.
	 * @param value
	 */
	public void setFormElementWithLabel(String formElementLabel, String value) {
		String name = getDialog().getFormElementNameForLabel(formElementLabel);
		Assert.assertNotNull("Did not find form element with label ["
				+ formElementLabel + "].", name);
		getDialog().setFormParameter(name, value);
	}

	/**
	 * Select a specified checkbox. If the checkbox is already checked then the
	 * checkbox will stay checked.
	 * 
	 * @param checkBoxName
	 *            name of checkbox to be deselected.
	 */
	public void checkCheckbox(String checkBoxName) {
		assertFormElementPresent(checkBoxName);
		getDialog().checkCheckbox(checkBoxName);
	}

	public void checkCheckbox(String checkBoxName, String value) {
		assertFormElementPresent(checkBoxName);
		getDialog().checkCheckbox(checkBoxName, value);
	}

	/**
	 * Deselect a specified checkbox. If the checkbox is already unchecked then
	 * the checkbox will stay unchecked.
	 * 
	 * @param checkBoxName
	 *            name of checkbox to be deselected.
	 */
	public void uncheckCheckbox(String checkBoxName) {
		assertFormElementPresent(checkBoxName);
		getDialog().uncheckCheckbox(checkBoxName);
	}

	public void uncheckCheckbox(String checkBoxName, String value) {
		assertFormElementPresent(checkBoxName);
		getDialog().uncheckCheckbox(checkBoxName, value);
	}

	/**
	 * Select an option with a given display value in a select element.
	 * 
	 * @param selectName
	 *            name of select element.
	 * @param option
	 *            display value of option to be selected.
	 */
	public void selectOption(String selectName, String option) {
		assertOptionPresent(selectName, option);
		getDialog().selectOption(selectName, option);
	}

	//Form submission and link navigation methods

	/**
	 * Submit form - default submit button will be used (unnamed submit button,
	 * or named button if there is only one on the form.
	 */
	public void submit() {
		assertFormPresent();
		getDialog().submit();
	}

	/**
	 * Submit form by pressing named button.
	 * 
	 * @param buttonName
	 *            name of button to submit form with.
	 */
	public void submit(String buttonName) {
		assertSubmitButtonPresent(buttonName);
		getDialog().submit(buttonName);
	}

	/**
	 * Submit the form by pressing the named button with the given value
	 * (label). Useful if you have more than one submit button with same name.
	 * 
	 * @author Dragos Manolescu
	 * @param buttonName
	 * @param buttonValue
	 */
	public void submit(String buttonName, String buttonValue) {
		assertSubmitButtonPresent(buttonName, buttonValue);
		getDialog().submit(buttonName, buttonValue);
	}

	/**
	 * Reset the current Dialog
	 * @see resetForm to reset a form in the response.
	 */
	public void reset() {
		getDialog().reset();
	}
	
    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    public void resetForm() {
    	getDialog().resetForm();
    }    
    
	

	/**
	 * Navigate by selection of a link containing given text.
	 * 
	 * @param linkText
	 */
	public void clickLinkWithText(String linkText) {
		assertLinkPresentWithText(linkText);
		getDialog().clickLinkWithText(linkText);
	}

	/**
	 * Navigate by selection of a link containing given text.
	 * 
	 * @param linkText
	 * @param index
	 *            The 0-based index, when more than one link with the same text
	 *            is expected.
	 */
	public void clickLinkWithText(String linkText, int index) {
		assertLinkPresentWithText(linkText, index);
		getDialog().clickLinkWithText(linkText, index);
	}

	/**
	 * Search for labelText in the document, then search forward until finding a
	 * link called linkText. Click it. Navigate by selection of a link with the
	 * exact given text.
	 * 
	 * SF.NET RFE: 996031
	 * 
	 * @param linkText
	 */
	public void clickLinkWithExactText(String linkText) {
		assertLinkPresentWithExactText(linkText);
		getDialog().clickLinkWithExactText(linkText);
	}

	/**
	 * Navigate by selection of a link with the exact given text.
	 * 
	 * SF.NET RFE: 996031
	 * 
	 * @param linkText
	 * @param index
	 *            The 0-based index, when more than one link with the same text
	 *            is expected.
	 */
	public void clickLinkWithExactText(String linkText, int index) {
		assertLinkPresentWithExactText(linkText, index);
		getDialog().clickLinkWithExactText(linkText, index);
	}

	/**
	 * Search for labelText in the document, then search forward until finding a
	 * link called linkText. Click it.
	 */
	public void clickLinkWithTextAfterText(String linkText, String labelText) {
		getDialog().clickLinkWithTextAfterText(linkText, labelText);
	}

	/**
	 * Click the button with the given id.
	 * 
	 * @param buttonId
	 */
	public void clickButton(String buttonId) {
		assertButtonPresent(buttonId);
		getDialog().clickButton(buttonId);
	}

	/**
	 * Navigate by selection of a link with a given image.
	 * 
	 * @param imageFileName
	 *            A suffix of the image's filename; for example, to match
	 *            <tt>"images/my_icon.png"</tt>, you could just pass in
	 *            <tt>"my_icon.png"</tt>.
	 */
	public void clickLinkWithImage(String imageFileName) {
		assertLinkPresentWithImage(imageFileName);
		getDialog().clickLinkWithImage(imageFileName);
	}

	/**
	 * Navigate by selection of a link with given id.
	 * 
	 * @param linkId
	 *            id of link
	 */
	public void clickLink(String linkId) {
		assertLinkPresent(linkId);
		getDialog().clickLink(linkId);
	}

	/**
	 * Clicks a radio option. Asserts that the radio option exists first.
	 *  *
	 * @param radioGroup
	 *            name of the radio group.
	 * @param radioOption
	 *            value of the option to check for.
	 */
	public void clickRadioOption(String radioGroup, String radioOption) {
		assertRadioOptionPresent(radioGroup, radioOption);
		getDialog().clickRadioOption(radioGroup, radioOption);
	}

	//Window and Frame Navigation Methods

	/**
	 * Make a given window active (current response will be window's contents).
	 * 
	 * @param windowName
	 */
	public void gotoWindow(String windowName) {
		assertWindowPresent(windowName);
		getDialog().gotoWindow(windowName);
	}

	/**
	 * Make the root window active.
	 */
	public void gotoRootWindow() {
		getDialog().gotoRootWindow();
	}

	/**
	 * Make first window with the given title active.
	 * 
	 * @param windowName
	 */
	public void gotoWindowByTitle(String title) {
		assertWindowPresentWithTitle(title);
		getDialog().gotoWindowByTitle(title);
	}

	/**
	 * Make the named frame active (current response will be frame's contents).
	 * 
	 * @param frameName
	 */
	public void gotoFrame(String frameName) {
		getDialog().gotoFrame(frameName);
	}

	/**
	 * Patch sumbitted by Alex Chaffee.
	 */
	public void gotoPage(String url) {
		try {
			getDialog().gotoPage(createUrl(url));
		} catch (TestingEngineResponseException aTestingEngineResponseException) {
			handleTestingEngineResponseException(aTestingEngineResponseException);
		}
	}
	
	/**
	 * Allows easier coding of exceptions thrown by the testing engines.
	 */
	private void handleTestingEngineResponseException(TestingEngineResponseException aTestingEngineResponseException) {
		Assert.fail("Method failed due to Exception Thrown: " + ExceptionUtility.stackTraceToString(aTestingEngineResponseException));
	}
	

	//Debug methods

	/**
	 * Dump html of current response to System.out - for debugging purposes.
	 * 
	 * @param stream
	 */
	public void dumpResponse() {
		getDialog().dumpResponse();
	}

	/**
	 * Dump html of current response to a specified stream - for debugging
	 * purposes.
	 * 
	 * @param stream
	 */
	public void dumpResponse(PrintStream stream) {
		getDialog().dumpResponse(stream);
	}

	/**
	 * Dump the table as the 2D array that is used for assertions - for
	 * debugging purposes.
	 * 
	 * @param tableNameOrId
	 * @param stream
	 */
	public void dumpTable(String tableNameOrId, PrintStream stream) {
		getDialog().dumpTable(tableNameOrId, stream);
	}

	/**
	 * Dump the table as the 2D array that is used for assertions - for
	 * debugging purposes.
	 * 
	 * @param tableNameOrId
	 * @param table
	 */
	public void dumpTable(String tableNameOrId, String[][] table) {
		getDialog().dumpTable(tableNameOrId, table);
	}

	/**
	 * Dump the table as the 2D array that is used for assertions - for
	 * debugging purposes.
	 * 
	 * @param tableNameOrId
	 * @param table
	 * @param stream
	 */
	public void dumpTable(String tableNameOrId, String[][] table,
			PrintStream stream) {
		getDialog().dumpTable(tableNameOrId, table, stream);
	}

	//Settings
	/**
	 * This setting controls whether the tester will compress html tables before
	 * compaing them against expecteds via table assertions. Compression means
	 * that rows with no displayable contents in their cells are eliminated
	 * before comparison, and also that empty cells in a row are discarded
	 * except as padding at the end of a row to reach the number of columns
	 * needed for non-empty content.
	 * 
	 * The default is compression true. The point of compressing the table
	 * before comparison is to prevent testers from having to worry about
	 * testing table spacing rather than content.
	 * 
	 * Confused? Example:
	 * 
	 * If compression is true the following html table (,'s mark cell
	 * divisions):
	 * 
	 * alpha,beta,charlie delta,&nbsp;,echo, &nbsp;,&nbsp;,&nbsp;
	 * &nbsp;,foxtrot,&nbsp;
	 * 
	 * Will be compressed to the following before comparison with actual.
	 * alpha,beta,charlie delta,echo,&nbsp; foxtrot,&nbsp;,&nbsp;
	 */
	public void setTableEmptyCellCompression(boolean bool) {
		this.tableEmptyCellCompression = bool;
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
	}

	/**
	 * Gets the Testing Engine Key that is used to find the proper testing
	 * engine class (HttpUnitDialog / JacobieDialog) for the tests.
	 * 
	 * @return Returns the testingEngineKey.
	 */
	public String getTestingEngineKey() {
		if (testingEngineKey == null) {
			//default to using the httpunit testing engine.
			setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTTPUNIT);
		}
		return testingEngineKey;
	}

	/**
	 * Protected only because consumers outside don't need to set a new
	 * registry.
	 * 
	 * @param testingEngineRegistry
	 *            The testingEngineRegistry to set.
	 */
	protected void setTestingEngineRegistry(
			TestingEngineRegistry testingEngineRegistry) {
		this.testingEngineRegistry = testingEngineRegistry;
	}

	/**
	 * Gets the Testing Engine Registry.
	 * 
	 * @return Returns the testingEngineRegistry.
	 */
	public TestingEngineRegistry getTestingEngineRegistry() {
		return testingEngineRegistry;
	}

}