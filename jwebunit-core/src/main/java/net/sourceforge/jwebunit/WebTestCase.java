/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit;

import java.io.PrintStream;
import java.net.MalformedURLException;

import javax.security.auth.login.FailedLoginException;

import org.apache.regexp.RESyntaxException;

import net.sourceforge.jwebunit.exception.AssertContainsException;
import net.sourceforge.jwebunit.exception.AssertEqualsException;
import net.sourceforge.jwebunit.exception.AssertMatchException;
import net.sourceforge.jwebunit.exception.ElementFoundException;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.locator.HtmlButtonLocator;
import net.sourceforge.jwebunit.locator.HtmlElementLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocator;
import net.sourceforge.jwebunit.locator.HtmlFormLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlTableLocator;
import net.sourceforge.jwebunit.locator.HtmlTableLocatorByName;
import net.sourceforge.jwebunit.locator.HtmlTableLocatorBySummary;

import junit.framework.TestCase;

/**
 * Superclass for Junit TestCases which provides web application navigation and
 * Junit assertions. This class uses {@link net.sourceforge.jwebunit.WebTester}
 * as a mixin - See that class for method documentation.
 * 
 * @author Julien Henry
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class WebTestCase extends TestCase {
    private JWebUnitTester tester = null;
    
    public WebTestCase(String name) {
        super(name);
    }

    public WebTestCase() {
        super();
    }

    // BEGIN JUNIT SETUP / TEARDOWN / RUNBARE OVERRIDES....

    public void tearDown() throws Exception {
        closeBrowser();
        super.tearDown();
    }

    /**
     * Clean up unused memory. Using <tt>setUp</tt> and <tt>tearDown</tt> is
     * not an option for this requires the subclasses of this class to call the
     * respective <tt>super</tt> methods.
     * 
     * Original patch contributed by Budi Boentaran.
     */
    public void runBare() throws Throwable {
        try {
            setTester(new JWebUnitTester());
            super.runBare();
        } finally {
            setTester(null);
        }
    }

    // END JUNIT SETUP / TEARDOWN / RUNBARE OVERRIDES....

    /**
     * Select the Testing Engine that you want to use for the tests. If this
     * isn't called, then jWebUnit will default to using htmlunit as the testing
     * engine.
     */
    public void setTestingEngineKey(String aTestingEngineKey) {
        getTester().setTestingEngineKey(aTestingEngineKey);
    }

    public void setScriptingEnabled(boolean value) {
        getTester().setScriptingEnabled(value);
    }

    public JWebUnitTester getTester() {
        return tester;
    }

    public void setTester(JWebUnitTester aWebTester) {
        this.tester = aWebTester;
    }

    public IJWebUnitDialog getDialog() {
        return getTester().getDialog();
    }

    public TestContext getTestContext() {
        return getTester().getTestContext();
    }

    public void beginAt(String relativeURL) {
        try {
            getTester().beginAt(relativeURL);
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        }
    }

    public void closeBrowser() {
        getTester().closeBrowser();
    }

    public void closeWindow() {
        getTester().closeWindow();
    }

    public String getMessage(String key) {
        return getTester().getMessage(key);
    }

    // Assertions

    public void assertTitleEquals(String title) {
        try {
            getTester().assertTitleEquals(title);
        } catch (AssertEqualsException e) {
            failNotEquals("Page title not expected", title, e.getActual());
        }
    }

    public void assertTitleMatch(String regexp) {
        try {
            getTester().assertTitleMatch(regexp);
        } catch (RESyntaxException e) {
            //TODO add link to the apache regexp site
            fail("Invalid regexp " + regexp);
        } catch (AssertMatchException e) {
            fail(regexp + " do not match actual title: " + e.getActualText());
        }
    }

    public void assertTitleEqualsKey(String titleKey) {
        try {
            getTester().assertTitleEqualsKey(titleKey);
        } catch (AssertEqualsException e) {
            failNotEquals("Title do not match", e.getExpected(), e.getActual());
        }
    }

    public void assertKeyPresent(String key) {
        try {
            getTester().assertKeyPresent(key);
        } catch (AssertContainsException e) {
            failNotSame("Unable to find the given text", e.getExpectedContent(), e.getActualText());
        }
    }

    public void assertTextPresent(String text) {
        try {
            getTester().assertTextPresent(text);
        } catch (AssertContainsException e) {
            failNotSame("Unable to find the given text", e.getExpectedContent(), e.getActualText());
        }
    }

    public void assertMatch(String regexp) {
        try {
            getTester().assertMatch(regexp);
        } catch (AssertMatchException e) {
            failNotSame("Unable to find a match for the given regexp", e.getExpectedRE(), e.getActualText());
        }
    }

    public void assertKeyNotPresent(String key) {
        try {
            getTester().assertKeyNotPresent(key);
        } catch (AssertContainsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void assertTextNotPresent(String text) {
        try {
            getTester().assertTextNotPresent(text);
        } catch (AssertContainsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void assertNoMatch(String regexp) {
        try {
            getTester().assertNoMatch(regexp);
        } catch (AssertMatchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void assertElementPresent(HtmlElementLocator element) {
        try {
            getTester().assertElementPresent(element);
        } catch (ElementNotFoundException e) {
            fail("Element not found: "+element.toString());
        }
    }
    
    public void assertElementNotPresent(HtmlElementLocator element) {
        try {
            getTester().assertElementNotPresent(element);
        } catch (ElementFoundException e) {
            fail("Element found when not expected: "+element.toString());
        }
    }

    /**
     * 
     * @deprecated
     */
    public void assertTablePresent(String id) {
        assertElementPresent(new HtmlTableLocator(id));
    }

    /**
     * 
     * @deprecated
     */
    public void assertTableNotPresent(String id) {
        assertElementNotPresent(new HtmlTableLocator(id));
    }

    /**
     * 
     * @deprecated
     */
    public void assertKeyInTable(String tableSummaryOrId, String key) {
        try {
            getTester().assertKeyInTable(new HtmlTableLocatorByName(tableSummaryOrId), key);
        } catch (ElementNotFoundException e) {
            try {
                getTester().assertKeyInTable(new HtmlTableLocatorBySummary(tableSummaryOrId), key);
            } catch (ElementNotFoundException e1) {
                fail("Unable to find given table");
            } catch (AssertContainsException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                fail("Given table do not contains text");
            }
        } catch (AssertContainsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("Given table do not contains text");
        }
    }

    public void assertKeyInTable(HtmlTableLocator tableLocator, String key) {
        try {
            getTester().assertKeyInTable(tableLocator, key);
        } catch (ElementNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        } catch (AssertContainsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 
     * @deprecated
     */
    public void assertTextInTable(String tableSummaryOrId, String text) {
        try {
            getTester().assertTextInTable(new HtmlTableLocatorByName(tableSummaryOrId), text);
        } catch (ElementNotFoundException e) {
            try {
                getTester().assertTextInTable(new HtmlTableLocatorBySummary(tableSummaryOrId), text);
            } catch (ElementNotFoundException e1) {
                fail("Unable to find given table");
            } catch (AssertContainsException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                fail("Given table do not contains text");
            }
        } catch (AssertContainsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("Given table do not contains text");
        }
    }
    
    public void assertTextInTable(HtmlTableLocator tableLocator, String text) {
        try {
            getTester().assertTextInTable(tableLocator, text);
        } catch (ElementNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        } catch (AssertContainsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    /**
     * @deprecated
     */
    public void assertMatchInTable(String tableSummaryOrId, String regexp) {
        tester.assertMatchInTable(tableSummaryOrId, regexp);
    }

    public void assertKeysInTable(String tableSummaryOrId, String[] keys) {
        getTester().assertKeysInTable(tableSummaryOrId, keys);
    }

    public void assertTextInTable(String tableSummaryOrId, String[] text) {
        getTester().assertTextInTable(tableSummaryOrId, text);
    }

    public void assertMatchInTable(String tableSummaryOrId, String[] regexp) {
        tester.assertMatchInTable(tableSummaryOrId, regexp);
    }

    public void assertKeyNotInTable(String tableSummaryOrId, String key) {
        getTester().assertKeyNotInTable(tableSummaryOrId, key);
    }

    public void assertTextNotInTable(String tableSummaryOrId, String text) {
        getTester().assertTextNotInTable(tableSummaryOrId, text);
    }

    public void assertTextNotInTable(String tableSummaryOrId, String[] text) {
        getTester().assertTextNotInTable(tableSummaryOrId, text);
    }

    public void assertNoMatchInTable(String tableSummaryOrId, String regexp) {
        getTester().assertNoMatchInTable(tableSummaryOrId, regexp);
    }

    public void assertNoMatchInTable(String tableSummaryOrId, String[] regexp) {
        getTester().assertNoMatchInTable(tableSummaryOrId, regexp);
    }

    public void assertTableEquals(String tableSummaryOrId, Table expectedTable) {
        getTester().assertTableEquals(tableSummaryOrId, expectedTable);
    }

    public void assertTableEquals(String tableSummaryOrId,
            String[][] expectedCellValues) {
        getTester().assertTableEquals(tableSummaryOrId, expectedCellValues);
    }

    public void assertTableRowsEqual(String tableSummaryOrId, int startRow,
            Table expectedTable) {
        getTester().assertTableRowsEqual(tableSummaryOrId, startRow,
                expectedTable);
    }

    public void assertTableRowCountEquals(String tableSummaryOrId,
            int expectedRowCount) {
        getTester().assertTableRowCountEquals(tableSummaryOrId,
                expectedRowCount);
    }

    public void assertTableRowsEqual(String tableSummaryOrId, int startRow,
            String[][] expectedCellValues) {
        getTester().assertTableRowsEqual(tableSummaryOrId, startRow,
                expectedCellValues);
    }

    public void assertTableMatch(String tableSummaryOrId, Table expectedTable) {
        getTester().assertTableMatch(tableSummaryOrId, expectedTable);
    }

    public void assertTableMatch(String tableSummaryOrId,
            String[][] expectedCellValues) {
        getTester().assertTableMatch(tableSummaryOrId, expectedCellValues);
    }

    public void assertTableRowsMatch(String tableSummaryOrId, int startRow,
            Table expectedTable) {
        getTester().assertTableRowsMatch(tableSummaryOrId, startRow, expectedTable);
    }

    public void assertTableRowsMatch(String tableSummaryOrId, int startRow,
            String[][] expectedCellValues) {
        getTester().assertTableRowsMatch(tableSummaryOrId, startRow,
                expectedCellValues);
    }

    public void assertFormElementPresent(String formElementName) {
        getTester().assertFormElementPresent(formElementName);
    }

    public void assertFormElementNotPresent(String formElementName) {
        getTester().assertFormElementNotPresent(formElementName);
    }

    public void assertFormPresent() {
        getTester().assertFormPresent();
    }

    public void assertFormPresent(String formName) {
        getTester().assertFormPresent(formName);
    }

    public void assertFormNotPresent() {
        getTester().assertFormNotPresent();
    }

    public void assertFormNotPresent(String formName) {
        getTester().assertFormNotPresent(formName);
    }

    /**
     * @deprecated 
     */
    public void assertFormElementEquals(String formElementName,
            String expectedValue) {
        getTester().assertFormElementEquals(formElementName, expectedValue);
    }

    public void assertFormElementMatch(String formElementName, String regexp) {
        getTester().assertFormElementMatch(formElementName, regexp);
    }

    public void assertFormElementEmpty(String formElementName) {
        getTester().assertFormElementEmpty(formElementName);
    }
    
    public void assertTextFieldEquals(String formElementName, String expectedValue) {
        getTester().assertTextFieldEquals(formElementName, expectedValue);
    }
    
    public void assertHiddenFieldPresent(String formElementName, String expectedValue) {
        getTester().assertHiddenFieldPresent(formElementName, expectedValue);
    }

    public void assertCheckboxSelected(String checkBoxName) {
        getTester().assertCheckboxSelected(checkBoxName);
    }

    public void assertCheckboxNotSelected(String checkBoxName) {
        getTester().assertCheckboxNotSelected(checkBoxName);
    }

    public void assertRadioOptionPresent(String radioGroup, String radioOption) {
        getTester().assertRadioOptionPresent(radioGroup, radioOption);
    }

    public void assertRadioOptionNotPresent(String radioGroup,
            String radioOption) {
        getTester().assertRadioOptionNotPresent(radioGroup, radioOption);
    }

    public void assertRadioOptionSelected(String radioGroup, String radioOption) {
        getTester().assertRadioOptionSelected(radioGroup, radioOption);
    }

    public void assertRadioOptionNotSelected(String radioGroup,
            String radioOption) {
        getTester().assertRadioOptionNotSelected(radioGroup, radioOption);
    }

    public void assertSelectOptionPresent(String selectName, String optionLabel) {
        getTester().assertSelectOptionPresent(selectName, optionLabel);
    }

    public void assertSelectOptionNotPresent(String selectName,
            String optionLabel) {
        getTester().assertSelectOptionNotPresent(selectName, optionLabel);
    }

    public void assertSelectOptionValuePresent(String selectName,
            String optionValue) {
        getTester().assertSelectOptionValuePresent(selectName, optionValue);
    }

    public void assertSelectOptionValueNotPresent(String selectName,
            String optionValue) {
        getTester().assertSelectOptionValueNotPresent(selectName, optionValue);
    }

    public void assertSelectOptionsEqual(String selectName, String[] options) {
        getTester().assertSelectOptionsEqual(selectName, options);
    }

    public void assertSelectOptionsNotEqual(String selectName, String[] options) {
        getTester().assertSelectOptionsNotEqual(selectName, options);
    }

    public void assertSelectOptionValuesEqual(String selectName,
            String[] options) {
        getTester().assertSelectOptionValuesEqual(selectName, options);
    }

    public void assertSelectOptionValuesNotEqual(String selectName,
            String[] options) {
        getTester().assertSelectOptionValuesNotEqual(selectName, options);
    }

    public void assertSelectedOptionEquals(String selectName, String label) {
        getTester().assertSelectedOptionEquals(selectName, label);
    }

    public void assertSelectedOptionsEqual(String selectName, String[] labels) {
        getTester().assertSelectedOptionsEqual(selectName, labels);
    }

    public void assertSelectedOptionValueEquals(String selectName, String value) {
        getTester().assertSelectedOptionValueEquals(selectName, value);
    }

    public void assertSelectedOptionValuesEqual(String selectName,
            String[] values) {
        getTester().assertSelectedOptionValuesEqual(selectName, values);
    }

    public void assertSelectedOptionMatches(String selectName, String regexp) {
        getTester().assertSelectedOptionMatches(selectName, regexp);
    }

    public void assertSelectedOptionsMatch(String selectName, String[] regexps) {
        getTester().assertSelectedOptionsMatch(selectName, regexps);
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertSubmitButtonPresent()
     */
    public void assertSubmitButtonPresent() {
        getTester().assertSubmitButtonPresent();
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertSubmitButtonPresent(String)
     */
    public void assertSubmitButtonPresent(String buttonName) {
        getTester().assertSubmitButtonPresent(buttonName);
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertSubmitButtonNotPresent()
     */
    public void assertSubmitButtonNotPresent() {
        getTester().assertSubmitButtonNotPresent();
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertSubmitButtonNotPresent(String)
     */
    public void assertSubmitButtonNotPresent(String buttonName) {
        getTester().assertSubmitButtonNotPresent(buttonName);
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertSubmitButtonPresent(String,
     *      String)
     */
    public void assertSubmitButtonPresent(String buttonName,
            String expectedValue) {
        getTester().assertSubmitButtonPresent(buttonName, expectedValue);
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertResetButtonPresent()
     */
    public void assertResetButtonPresent() {
        getTester().assertResetButtonPresent();
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertResetButtonPresent(String)
     */
    public void assertResetButtonPresent(String buttonName) {
        getTester().assertResetButtonPresent(buttonName);
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertResetButtonNotPresent()
     */
    public void assertResetButtonNotPresent() {
        getTester().assertResetButtonNotPresent();
    }

    /**
     * @see net.sourceforge.jwebunit.WebTester#assertResetButtonNotPresent(String)
     */
    public void assertResetButtonNotPresent(String buttonName) {
        getTester().assertResetButtonNotPresent(buttonName);
    }

    /**
     * @deprecated use assertElementPresent(new HtmlButtonLocator(buttonID));
     */
    public void assertButtonPresent(String buttonID) {
        assertElementPresent(new HtmlButtonLocator(buttonID));
    }

    public void assertButtonPresentWithText(String text) {
        try {
            getTester().assertButtonPresentWithText(text);
        } catch (ElementNotFoundException e) {
            fail("Button not found with text "+text);
        }
    }

    public void assertButtonNotPresentWithText(String text) {
        try {
            getTester().assertButtonNotPresentWithText(text);
        } catch (ElementFoundException e) {
            fail("Button found");
        }
    }

    public void assertButtonNotPresent(String buttonID) {
        getTester().assertButtonNotPresent(buttonID);
    }

    public void assertLinkPresent(String linkId) {
        getTester().assertLinkPresent(linkId);
    }

    public void assertLinkNotPresent(String linkId) {
        getTester().assertLinkNotPresent(linkId);
    }

    public void assertLinkPresentWithText(String linkText) {
        getTester().assertLinkPresentWithText(linkText);
    }

    public void assertLinkNotPresentWithText(String linkText) {
        getTester().assertLinkNotPresentWithText(linkText);
    }

    public void assertLinkPresentWithText(String linkText, int index) {
        getTester().assertLinkPresentWithText(linkText, index);
    }

    public void assertLinkNotPresentWithText(String linkText, int index) {
        getTester().assertLinkNotPresentWithText(linkText, index);
    }

    // SF.NET RFE: 996031
    public void assertLinkPresentWithExactText(String linkText) {
        getTester().assertLinkPresentWithExactText(linkText);
    }

    // SF.NET RFE: 996031
    public void assertLinkNotPresentWithExactText(String linkText) {
        getTester().assertLinkNotPresentWithExactText(linkText);
    }

    // SF.NET RFE: 996031
    public void assertLinkPresentWithExactText(String linkText, int index) {
        getTester().assertLinkPresentWithExactText(linkText, index);
    }

    // SF.NET RFE: 996031
    public void assertLinkNotPresentWithExactText(String linkText, int index) {
        getTester().assertLinkNotPresentWithExactText(linkText, index);
    }

    public void assertLinkPresentWithImage(String imageFileName) {
        getTester().assertLinkPresentWithImage(imageFileName);
    }

    public void assertLinkNotPresentWithImage(String imageFileName) {
        getTester().assertLinkNotPresentWithImage(imageFileName);
    }

    public void assertElementPresent(String anID) {
        getTester().assertElementPresent(anID);
    }

    public void assertElementNotPresent(String anID) {
        getTester().assertElementNotPresent(anID);
    }

    /**
     * Assert that an element with a given xpath is present.
     * 
     * @param xpath
     *            element xpath to test for.
     */
    public void assertElementPresentByXPath(String xpath) {
        getTester().assertElementPresentByXPath(xpath);
    }

    /**
     * Assert that an element with a given xpath is not present.
     * 
     * @param xpath
     *            element xpath to test for.
     */
    public void assertElementNotPresentByXPath(String xpath) {
        getTester().assertElementNotPresentByXPath(xpath);
    }

    public void assertTextInElement(String elID, String text) {
        getTester().assertTextInElement(elID, text);
    }

    public void assertTextNotInElement(String elID, String text) {
        getTester().assertTextNotInElement(elID, text);
    }

    public void assertMatchInElement(String elID, String regexp) {
        getTester().assertMatchInElement(elID, regexp);
    }

    public void assertNoMatchInElement(String elID, String regexp) {
        getTester().assertNoMatchInElement(elID, regexp);
    }

    public void assertWindowPresent(String windowName) {
        getTester().assertWindowPresent(windowName);
    }

    public void assertWindowPresent(int windowID) {
        getTester().assertWindowPresent(windowID);
    }

    public void assertWindowPresentWithTitle(String title) {
        getTester().assertWindowPresentWithTitle(title);
    }

    public void assertWindowCountEquals(int windowCount) {
        getTester().assertWindowCountEquals(windowCount);
    }

    public void assertFramePresent(String frameName) {
        getTester().assertFramePresent(frameName);
    }

    public void assertCookiePresent(String cookieName) {
        getTester().assertCookiePresent(cookieName);
    }

    public void assertCookieValueEquals(String cookieName, String expectedValue) {
        getTester().assertCookieValueEquals(cookieName, expectedValue);
    }

    public void assertCookieValueMatch(String cookieName, String regexp) {
        getTester().assertCookieValueMatch(cookieName, regexp);
    }
    
    public void assertJavascriptAlertPresent(String msg) {
        getTester().assertJavascriptAlertPresent(msg);
    }

    // Form interaction methods

    /**
     * Gets the value of a form input element. Allows getting information from a
     * form element. Also, checks assertions as well.
     * 
     * @param formElementName
     *            name of form element.
     * @param value
     */
    public String getFormElementValue(String formElementName) {
        return getTester().getFormElementValue(formElementName);
    }

    public void setWorkingForm(HtmlFormLocator formLocator) {
        try {
            getTester().setWorkingForm(formLocator);
        } catch (ElementNotFoundException e) {
            fail("Form not found "+ e.getElementNotFound().toString());
        }
    }

    /**
     * @deprecated use setWorkingForm(HtmlFormLocator)
     */
    public void setWorkingForm(String nameOrId) {
        try {
            getTester().setWorkingForm(new HtmlFormLocator(nameOrId));
        } catch (ElementNotFoundException e) {
            try {
                getTester().setWorkingForm(new HtmlFormLocatorByName(nameOrId));
            } catch (ElementNotFoundException e2) {
                fail("Unable to find a form whose name or id is "+nameOrId);
            }
        }
    }

    /**
     * @deprecated use setWorkingForm(HtmlFormLocator)
     */
    public void setWorkingForm(String nameOrId, int index) {
        try {
            getTester().setWorkingForm(new HtmlFormLocator(nameOrId)); //Id should be unique
        } catch (ElementNotFoundException e) {
            try {
                getTester().setWorkingForm(new HtmlFormLocatorByName(nameOrId, index));
            } catch (ElementNotFoundException e2) {
                fail("Unable to find a form whose name or id is "+nameOrId);
            }
        }
    }

    public void setTextField(String textFieldName, String value) {
        getTester().setTextField(textFieldName, value);
    }

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be selected.
     */
    public void checkCheckbox(String checkBoxName) {
        getTester().checkCheckbox(checkBoxName);
    }

    /**
     * Select a specified checkbox. If the checkbox is already checked then the
     * checkbox will stay checked.
     * 
     * @param checkBoxName
     *            name of checkbox to be selected.
     * @param value
     *            value of checkbox to be selected.
     */
    public void checkCheckbox(String checkBoxName, String value) {
        getTester().checkCheckbox(checkBoxName, value);
    }

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then
     * the checkbox will stay unchecked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName) {
        getTester().uncheckCheckbox(checkBoxName);
    }

    /**
     * Deselect a specified checkbox. If the checkbox is already unchecked then
     * the checkbox will stay unchecked.
     * 
     * @param checkBoxName
     *            name of checkbox to be deselected.
     * @param value
     *            value of checkbox to be deselected.
     */
    public void uncheckCheckbox(String checkBoxName, String value) {
        getTester().uncheckCheckbox(checkBoxName, value);
    }

    /**
     * Select an option with a given display label in a select element.
     * 
     * @param selectName
     *            name of select element.
     * @param label
     *            label of option to be selected.
     */
    public void selectOption(String selectName, String label) {
        getTester().selectOption(selectName, label);
    }

    public void selectOptions(String selectName, String[] labels) {
        getTester().selectOptions(selectName, labels);
    }

    public void selectOptionByValue(String selectName, String value) {
        getTester().selectOptionByValue(selectName, value);
    }

    public void selectOptionsByValues(String selectName, String[] values) {
        getTester().selectOptionsByValues(selectName, values);
    }

    // Form submission and link navigation methods

    public void submit() {
        getTester().submit();
    }

    public void submit(String buttonName) {
        getTester().submit(buttonName);
    }

    public void submit(String buttonName, String buttonValue) {
        getTester().submit(buttonName, buttonValue);
    }

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    public void reset() {
        getTester().reset();
    }

    public void clickLinkWithText(String linkText) {
        getTester().clickLinkWithText(linkText);
    }

    public void clickLinkWithText(String linkText, int index) {
        getTester().clickLinkWithText(linkText, index);
    }

    protected void clickLinkWithExactText(String linkText) {
        getTester().clickLinkWithExactText(linkText);
    }

    protected void clickLinkWithExactText(String linkText, int index) {
        getTester().clickLinkWithExactText(linkText, index);
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
        getTester().clickLinkWithImage(imageFileName);
    }

    public void clickLink(String linkId) {
        getTester().clickLink(linkId);
    }

    public void clickButton(String buttonId) {
        getTester().clickButton(buttonId);
    }

    public void clickButtonWithText(String buttonValueText) {
        getTester().clickButtonWithText(buttonValueText);
    }

    protected void clickRadioOption(String radioGroup, String radioOption) {
        getTester().clickRadioOption(radioGroup, radioOption);
    }

    /**
     * Click element with given xpath.
     * 
     * @param xpath
     *            xpath of the element.
     */
    protected void clickElementByXPath(String xpath) {
        getTester().clickElementByXPath(xpath);
    }

    public void gotoRootWindow() {
        getTester().gotoRootWindow();
    }

    public void gotoWindowByTitle(String title) {
        getTester().gotoWindowByTitle(title);
    }

    public void gotoWindow(String windowName) {
        getTester().gotoWindow(windowName);
    }

    public void gotoWindow(int windowID) {
        getTester().gotoWindow(windowID);
    }

    public void gotoFrame(String frameName) {
        getTester().gotoFrame(frameName);
    }

    protected void dumpCookies() {
        getTester().dumpCookies();
    }

    protected void gotoPage(String page) {
        getTester().gotoPage(page);
    }

    // Debug methods

    protected void dumpHtml() {
        getTester().dumpHtml();
    }

    protected void dumpHtml(PrintStream stream) {
        getTester().dumpHtml(stream);
    }

    protected void dumpTable(String tableNameOrId, PrintStream stream) {
        getTester().dumpTable(tableNameOrId, stream);
    }

    protected void dumpTable(String tableNameOrId) {
        getTester().dumpTable(tableNameOrId);
    }
    
    /**
     * @deprecated Use setTextField instead.
     */
    protected void setFormElement(String formElementName, String value) {
        getTester().setFormElement(formElementName, value);
    }

}
