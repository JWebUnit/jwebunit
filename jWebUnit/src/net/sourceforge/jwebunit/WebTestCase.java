/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/

package net.sourceforge.jwebunit;

import java.io.PrintStream;

import junit.framework.TestCase;

/**
 * Superclass for Junit TestCases which provides web application navigation and
 * Junit assertions. This class uses {@link net.sourceforge.jwebunit.WebTester}
 * as a mixin - See that class for method documentation.
 * 
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class WebTestCase extends TestCase {
    private WebTester tester = null;

    //BEGIN CONSTRUCTORS....
    
    public WebTestCase(String name) {
        super(name);
    }

    public WebTestCase() {
    }

    //END CONSTRUCTORS....
    
    //BEGIN JUNIT SETUP / TEARDOWN / RUNBARE OVERRIDES....
    
    public void setUp() throws Exception {
    	super.setUp();

        //New implementation on choosing a testing engine (dialog).
        //setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_JACOBIE);
        //setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTTPUNIT);
    }
    
    public void tearDown() throws Exception {
    	
    	//this resets the dialog / nulls out objects, etc.
    	//close IE from the JacobieDialog.
    	reset();
    	
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
            setTester(new WebTester());
            super.runBare();
        } finally {
            setTester(null);
        }
    }

    //END JUNIT SETUP / TEARDOWN / RUNBARE OVERRIDES....  

    /**
     * Select the Testing Engine that you want to use for the tests. 
     * If this isn't called, then jWebUnit will default to using
     * httpunit as the testing engine.
     */
    public void setTestingEngineKey(String aTestingEngineKey) {
    	getTester().setTestingEngineKey(aTestingEngineKey);
    }
    
    public WebTester getTester() {
        return tester;
    }

    public void setTester(WebTester aWebTester) {
		this.tester = aWebTester;
    }

    public IJWebUnitDialog getDialog() {
        return getTester().getDialog();
    }

    public TestContext getTestContext() {
        return getTester().getTestContext();
    }

    public void beginAt(String relativeURL) {
        getTester().beginAt(relativeURL);
    }

    public String getMessage(String key) {
        return getTester().getMessage(key);
    }

    // Assertions

    public void assertTitleEquals(String title) {
        getTester().assertTitleEquals(title);
    }

    public void assertTitleMatch(String regexp) {
        tester.assertTitleMatch(regexp);
    }

    public void assertTitleEqualsKey(String titleKey) {
        getTester().assertTitleEqualsKey(titleKey);
    }

    public void assertKeyPresent(String key) {
        getTester().assertKeyPresent(key);
    }

    public void assertTextPresent(String text) {
        getTester().assertTextPresent(text);
    }

    public void assertMatch(String regexp) {
        tester.assertMatch(regexp);
    }

    public void assertKeyNotPresent(String key) {
        getTester().assertKeyNotPresent(key);
    }

    public void assertTextNotPresent(String text) {
        getTester().assertTextNotPresent(text);
    }

    public void assertNoMatch(String regexp) {
        tester.assertNoMatch(regexp);
    }

    public void assertTablePresent(String tableSummaryOrId) {
        getTester().assertTablePresent(tableSummaryOrId);
    }

    public void assertTableNotPresent(String tableSummaryOrId) {
        getTester().assertTableNotPresent(tableSummaryOrId);
    }

    public void assertKeyInTable(String tableSummaryOrId, String key) {
        getTester().assertKeyInTable(tableSummaryOrId, key);
    }

    public void assertTextInTable(String tableSummaryOrId, String text) {
        getTester().assertTextInTable(tableSummaryOrId, text);
    }

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
        tester.assertNoMatchInTable(tableSummaryOrId, regexp);
    }

    public void assertNoMatchInTable(String tableSummaryOrId, String[] regexp) {
        tester.assertNoMatchInTable(tableSummaryOrId, regexp);
    }

    public void assertTableEquals(String tableSummaryOrId, ExpectedTable expectedTable) {
        getTester().assertTableEquals(tableSummaryOrId, expectedTable.getExpectedStrings());
    }

    public void assertTableEquals(String tableSummaryOrId, String[][] expectedCellValues) {
        getTester().assertTableEquals(tableSummaryOrId, expectedCellValues);
    }

    public void assertTableRowsEqual(String tableSummaryOrId, int startRow, ExpectedTable expectedTable) {
        getTester().assertTableRowsEqual(tableSummaryOrId, startRow, expectedTable);
    }

    public void assertTableRowsEqual(String tableSummaryOrId, int startRow, String[][] expectedCellValues) {
        getTester().assertTableRowsEqual(tableSummaryOrId, startRow, expectedCellValues);
    }

    public void assertTableMatch(String tableSummaryOrId,
            ExpectedTable expectedTable) {
        tester.assertTableMatch(tableSummaryOrId, expectedTable
                .getExpectedStrings());
    }

    public void assertTableMatch(String tableSummaryOrId,
            String[][] expectedCellValues) {
        tester.assertTableMatch(tableSummaryOrId, expectedCellValues);
    }

    public void assertTableRowsMatch(String tableSummaryOrId, int startRow,
            ExpectedTable expectedTable) {
        tester.assertTableRowsMatch(tableSummaryOrId, startRow, expectedTable);
    }

    public void assertTableRowsMatch(String tableSummaryOrId, int startRow,
            String[][] expectedCellValues) {
        tester.assertTableRowsMatch(tableSummaryOrId, startRow,
                expectedCellValues);
    }

    public void assertFormElementPresent(String formElementName) {
        getTester().assertFormElementPresent(formElementName);
    }

    public void assertFormElementNotPresent(String formElementName) {
        getTester().assertFormElementNotPresent(formElementName);
    }

    public void assertFormElementPresentWithLabel(String formElementLabel) {
        getTester().assertFormElementPresentWithLabel(formElementLabel);
    }

    public void assertFormElementNotPresentWithLabel(String formElementLabel) {
        getTester().assertFormElementNotPresentWithLabel(formElementLabel);
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

    public void assertFormElementEquals(String formElementName,
            String expectedValue) {
        getTester().assertFormElementEquals(formElementName, expectedValue);
    }

    public void assertFormElementMatch(String formElementName,
            String regexp) {
        tester.assertFormElementMatch(formElementName, regexp);
    }

    public void assertFormElementEmpty(String formElementName) {
        getTester().assertFormElementEmpty(formElementName);
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

    public void assertRadioOptionNotPresent(String radioGroup, String radioOption) {
        getTester().assertRadioOptionNotPresent(radioGroup, radioOption);
    }

    public void assertRadioOptionSelected(String radioGroup, String radioOption) {
        getTester().assertRadioOptionSelected(radioGroup, radioOption);
    }

    public void assertRadioOptionNotSelected(String radioGroup, String radioOption) {
        getTester().assertRadioOptionNotSelected(radioGroup, radioOption);
    }

    public void assertOptionPresent(String selectName, String optionLabel) {
        getTester().assertOptionPresent(selectName, optionLabel);
    }

    public void assertOptionNotPresent(String selectName, String optionLabel) {
        getTester().assertOptionNotPresent(selectName, optionLabel);
    }

    public void assertOptionsEqual(String selectName, String[] options) {
        getTester().assertOptionsEqual(selectName, options);
    }

    public void assertOptionsNotEqual(String selectName, String[] options) {
        getTester().assertOptionsNotEqual(selectName, options);
    }

    public void assertOptionValuesEqual(String selectName, String[] options) {
        getTester().assertOptionValuesEqual(selectName, options);
    }

    public void assertOptionValuesNotEqual(String selectName, String[] options) {
        getTester().assertOptionValuesNotEqual(selectName, options);
    }

    public void assertOptionEquals(String selectName, String option) {
        getTester().assertOptionEquals(selectName, option);
    }

    public void assertOptionMatch(String selectName, String regexp) {
        tester.assertOptionMatch(selectName, regexp);
    }

    public void assertSubmitButtonPresent(String buttonName) {
        getTester().assertSubmitButtonPresent(buttonName);
    }

    public void assertSubmitButtonNotPresent(String buttonName) {
        getTester().assertSubmitButtonNotPresent(buttonName);
    }

    public void assertSubmitButtonPresent(String buttonName, String expectedValue) {
        getTester().assertSubmitButtonPresent(buttonName, expectedValue);
    }

    public void assertButtonPresent(String buttonID) {
        getTester().assertButtonPresent(buttonID);
    }

    public void assertButtonPresentWithText(String text) {
        getTester().assertButtonPresentWithText(text);
    }

    public void assertButtonNotPresentWithText(String text) {
        getTester().assertButtonNotPresentWithText(text);
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

    //SF.NET RFE: 996031
    public void assertLinkPresentWithExactText(String linkText) {
        getTester().assertLinkPresentWithExactText(linkText);
    }

    //SF.NET RFE: 996031
    public void assertLinkNotPresentWithExactText(String linkText) {
        getTester().assertLinkNotPresentWithExactText(linkText);
    }

    //SF.NET RFE: 996031
    public void assertLinkPresentWithExactText(String linkText, int index) {
        getTester().assertLinkPresentWithExactText(linkText, index);
    }

    //SF.NET RFE: 996031
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

    public void assertTextInElement(String elID, String text) {
        getTester().assertTextInElement(elID, text);
    }

    public void assertTextNotInElement(String elID, String text) {
        getTester().assertTextNotInElement(elID, text);
    }

    public void assertMatchInElement(String elID, String regexp) {
        tester.assertMatchInElement(elID, regexp);
    }

    public void assertNoMatchInElement(String elID, String regexp) {
        tester.assertNoMatchInElement(elID, regexp);
    }

    public void assertWindowPresent(String windowName) {
        getTester().assertWindowPresent(windowName);
    }

    public void assertWindowPresentWithTitle(String title) {
        getTester().assertWindowPresentWithTitle(title);
    }

    public void assertFramePresent(String frameName) {
        getTester().assertFramePresent(frameName);
    }

    /**
     * Contributed by Vivek Venugopalan.
     */
    public void assertCookiePresent(String cookieName) {
        getTester().assertCookiePresent(cookieName);
    }

    public void assertCookieValueEquals(String cookieName, String expectedValue) {
        getTester().assertCookieValueEquals(cookieName, expectedValue);
    }
    
    public void assertCookieValueMatch(String cookieName, String regexp) {
        tester.assertCookieValueMatch(cookieName, regexp);
    }

//  is Pattern methods
     
    /**
     * Return true if given text is present anywhere in the current response.
     * 
     * @param text
     *            string to check for.
     */    
    public boolean isTextInResponse(String text) {
        return getTester().isTextInResponse(text);
    }

    
//  cookie methods.    
    
    public void dumpCookies() {
        getTester().dumpCookies();
    }

    public void dumpCookies(PrintStream stream) {
        getTester().dumpCookies(stream);
    }

    // Form interaction methods
    
    /**
     * Gets the value of a form input element.  Allows getting information from a form element.
     * Also, checks assertions as well.
     *
     * @param formElementName name of form element.
     * @param value
     */
    public String getFormElementValue(String formElementName) {
        return getTester().getFormElementValue(formElementName);
    }    

    public void setWorkingForm(String nameOrId) {
        getTester().setWorkingForm(nameOrId);
    }

    public void setFormElement(String formElementName, String value) {
        getTester().setFormElement(formElementName, value);
    }

    public void setFormElementWithLabel(String formElementLabel, String value) {
        getTester().setFormElementWithLabel(formElementLabel, value);
    }

    public void checkCheckbox(String checkBoxName) {
        getTester().checkCheckbox(checkBoxName);
    }

    public void checkCheckbox(String checkBoxName, String value) {
        getTester().checkCheckbox(checkBoxName, value);
    }

    public void uncheckCheckbox(String checkBoxName) {
        getTester().uncheckCheckbox(checkBoxName);
    }

    public void uncheckCheckbox(String checkBoxName, String value) {
        getTester().uncheckCheckbox(checkBoxName, value);
    }

    public void selectOption(String selectName, String option) {
        getTester().selectOption(selectName, option);
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

    public void reset() {
        getTester().reset();
    }

    /**
     * Reset the current form. See {@link #getForm}for an explanation of how
     * the current form is established.
     */
    public void resetForm() {
    	getTester().resetForm();
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

    protected void clickLinkWithTextAfterText(String linkText, String labelText) {
        getTester().clickLinkWithTextAfterText(linkText, labelText);
    }

    public void clickLinkWithImage(String imageFileName) {
        getTester().clickLinkWithImage(imageFileName);
    }

    public void clickLink(String linkId) {
        getTester().clickLink(linkId);
    }

    public void clickButton(String buttonId) {
        getTester().clickButton(buttonId);
    }
    
    protected void clickRadioOption(String radioGroup, String radioOption) {
        getTester().clickRadioOption(radioGroup, radioOption);
    }

    //Window and Frame Navigation Methods

    public void gotoRootWindow() {
        getTester().gotoRootWindow();
    }

    public void gotoWindowByTitle(String title) {
        getTester().gotoWindowByTitle(title);
    }

    public void gotoWindow(String windowName) {
        getTester().gotoWindow(windowName);
    }

    public void gotoFrame(String frameName) {
        getTester().gotoFrame(frameName);
    }

    /**
     * Patch sumbitted by Alex Chaffee.
     */
    public void gotoPage(String page) {
        getTester().gotoPage(page);
    }

    // Debug methods

    protected void dumpResponse(PrintStream stream) {
        getTester().dumpResponse(stream);
    }

    protected void dumpTable(String tableNameOrId, PrintStream stream) {
        getTester().dumpTable(tableNameOrId, stream);
    }

    protected void dumpTable(String tableNameOrId, String[][] table) {
        getTester().dumpTable(tableNameOrId, table);
    }
    
    //Settings
    
    public void setTableEmptyCellCompression(boolean bool) {
        getTester().setTableEmptyCellCompression(bool);
    }

}