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
 * This class uses {@link net.sourceforge.jwebunit.WebTester} as a mixin -
 * See that class for method documentation.
 *
 *  @author Jim Weaver
 *  @author Wilkes Joiner
 */
public class WebTestCase extends TestCase {
    private WebTester tester;

    public WebTestCase(String name) {
        super(name);
        tester = new WebTester();
    }

    public HttpUnitDialog getDialog() {
        return tester.getDialog();
    }

    public TestContext getTestContext() {
        return tester.getTestContext();
    }

    public void beginAt(String relativeURL) {
        tester.beginAt(relativeURL);
    }

    public String getMessage(String key) {
        return tester.getMessage(key);
    }

// Assertions

    public void assertTitleEquals(String title) {
        tester.assertTitleEquals(title);
    }

    public void assertTitleEqualsKey(String titleKey) {
        tester.assertTitleEqualsKey(titleKey);
    }

    public void assertKeyPresent(String key) {
        tester.assertKeyPresent(key);
    }

    public void assertTextPresent(String text) {
        tester.assertTextPresent(text);
    }

    public void assertKeyNotPresent(String key) {
        tester.assertKeyNotPresent(key);
    }

    public void assertTextNotPresent(String text) {
        tester.assertTextNotPresent(text);
    }

    public void assertTablePresent(String tableSummaryOrId) {
        tester.assertTablePresent(tableSummaryOrId);
    }

    public void assertTableNotPresent(String tableSummaryOrId) {
        tester.assertTableNotPresent(tableSummaryOrId);
    }

    public void assertKeyInTable(String tableSummaryOrId, String key) {
        tester.assertKeyInTable(tableSummaryOrId, key);
    }

    public void assertTextInTable(String tableSummaryOrId, String text) {
        tester.assertTextInTable(tableSummaryOrId, text);
    }

    public void assertKeysInTable(String tableSummaryOrId, String[] keys) {
        tester.assertKeysInTable(tableSummaryOrId, keys);
    }

    public void assertTextInTable(String tableSummaryOrId, String[] text) {
        tester.assertTextInTable(tableSummaryOrId, text);
    }

    public void assertKeyNotInTable(String tableSummaryOrId, String key) {
        tester.assertKeyNotInTable(tableSummaryOrId, key);
    }

    public void assertTextNotInTable(String tableSummaryOrId, String text) {
        tester.assertTextNotInTable(tableSummaryOrId, text);
    }

    public void assertTextNotInTable(String tableSummaryOrId, String[] text) {
        tester.assertTextNotInTable(tableSummaryOrId, text);
    }

    public void assertTableEquals(String tableSummaryOrId, ExpectedTable expectedTable) {
        tester.assertTableEquals(tableSummaryOrId, expectedTable.getExpectedStrings());
    }

    public void assertTableEquals(String tableSummaryOrId, String[][] expectedCellValues) {
        tester.assertTableEquals(tableSummaryOrId, expectedCellValues);
    }

    public void assertTableRowsEqual(String tableSummaryOrId, int startRow, ExpectedTable expectedTable) {
        tester.assertTableRowsEqual(tableSummaryOrId, startRow, expectedTable);
    }

    public void assertTableRowsEqual(String tableSummaryOrId, int startRow, String[][] expectedCellValues) {
        tester.assertTableRowsEqual(tableSummaryOrId, startRow, expectedCellValues);
    }

    public void assertFormElementPresent(String parameterName) {
        tester.assertFormElementPresent(parameterName);
    }

    public void assertFormElementNotPresent(String parameterName) {
        tester.assertFormElementNotPresent(parameterName);
    }

    public void assertHasForm() {
        tester.assertHasForm();
    }

    public void assertHasForm(String formName) {
        tester.assertHasForm(formName);
    }

    public void assertFormElementEquals(String parameterName, String expectedValue) {
        tester.assertFormElementEquals(parameterName, expectedValue);
    }

    public void assertCheckboxSelected(String checkBoxName) {
        tester.assertCheckboxSelected(checkBoxName);
    }

    public void assertCheckboxNotSelected(String checkBoxName) {
        tester.assertCheckboxNotSelected(checkBoxName);
    }

    public void assertRadioOptionPresent(String radioGroup, String radioOption) {
        tester.assertRadioOptionPresent(radioGroup, radioOption);
    }

    public void assertRadioOptionNotPresent(String radioGroup, String radioOption) {
        tester.assertRadioOptionNotPresent(radioGroup, radioOption);
    }

    public void assertRadioOptionSelected(String radioGroup, String radioOption) {
        tester.assertRadioOptionSelected(radioGroup, radioOption);
    }

    public void assertRadioOptionNotSelected(String radioGroup, String radioOption) {
        tester.assertRadioOptionNotSelected(radioGroup, radioOption);
    }

    public void assertOptionsEqual(String selectName, String[] options){
        tester.assertOptionsEqual(selectName, options);
    }

    public void assertOptionsNotEqual(String selectName, String[] options){
        tester.assertOptionsNotEqual(selectName, options);
    }

    public void assertOptionValuesEqual(String selectName, String[] options){
        tester.assertOptionValuesEqual(selectName, options);
    }

    public void assertOptionValuesNotEqual(String selectName, String[] options){
        tester.assertOptionValuesNotEqual(selectName, options);
    }

    public void assertOptionEquals(String selectName, String option) {
        tester.assertOptionEquals(selectName, option);
    }

    public void assertSubmitButtonPresent(String buttonName) {
        tester.assertSubmitButtonPresent(buttonName);
    }

    public void assertSubmitButtonNotPresent(String buttonName) {
        tester.assertSubmitButtonNotPresent(buttonName);
    }

    public void assertSubmitButtonValue(String buttonName, String expectedValue) {
        tester.assertSubmitButtonValue(buttonName, expectedValue);
    }

    public void assertLinkPresentWithText(String linkText) {
        tester.assertLinkPresentWithText(linkText);
    }

    public void assertLinkNotPresentWithText(String linkText) {
        tester.assertLinkNotPresentWithText(linkText);
    }

    public void assertLinkPresent(String linkId) {
        tester.assertLinkPresent(linkId);
    }

    public void assertLinkNotPresent(String linkId) {
        tester.assertLinkNotPresent(linkId);
    }

    public void assertElementPresent(String anID) {
        tester.assertElementPresent(anID);
    }

    public void assertElementNotPresent(String anID) {
        tester.assertElementNotPresent(anID);
    }

    public void assertTextInElement(String elID, String text) {
        tester.assertTextInElement(elID, text);
    }


// Form interaction methods

    public void setWorkingForm(String nameOrId) {
        tester.setWorkingForm(nameOrId);
    }

    protected void setFormElement(String parameterName, String value) {
        tester.setFormElement(parameterName, value);
    }

    protected void checkCheckbox(String checkBoxName) {
        tester.checkCheckbox(checkBoxName);
    }

    protected void uncheckCheckbox(String checkBoxName) {
        tester.uncheckCheckbox(checkBoxName);
    }

    public void selectOption(String selectName, String option) {
        tester.selectOption(selectName, option);
    }

// Form submission and link navigation methods

    protected void submit() {
        tester.submit();
    }

    public void submit(String buttonName) {
        tester.submit(buttonName);
    }

    protected void clickLinkWithText(String linkText) {
        tester.clickLinkWithText(linkText);
    }

    protected void clickLink(String linkId) {
        tester.clickLink(linkId);
    }

// Debug methods

    protected void dumpResponse(PrintStream stream) {
        tester.dumpResponse(stream);
    }

    protected void dumpTable(String tableNameOrId, PrintStream stream) {
        tester.dumpTable(tableNameOrId, stream);
    }

    protected void dumpTable(String tableNameOrId, String[][] table) {
        tester.dumpTable(tableNameOrId, table);
    }

}
