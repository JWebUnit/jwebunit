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
 * This class uses {@link net.sourceforge.jwebunit.WebTester} as a mixin.
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

    public void assertTablePresent(String tableSummary) {
        tester.assertTablePresent(tableSummary);
    }

    public void assertTableNotPresent(String tableSummary) {
        tester.assertTableNotPresent(tableSummary);
    }

    public void assertKeyInTable(String tableSummary, String key) {
        tester.assertKeyInTable(tableSummary, key);
    }

    public void assertTextInTable(String tableSummary, String text) {
        tester.assertTextInTable(tableSummary, text);
    }

    public void assertKeysInTable(String tableSummary, String[] keys) {
        tester.assertKeysInTable(tableSummary, keys);
    }

    public void assertTextInTable(String tableSummary, String[] text) {
        tester.assertTextInTable(tableSummary, text);
    }

    public void assertKeyNotInTable(String tableSummary, String key) {
        tester.assertKeyNotInTable(tableSummary, key);
    }

    public void assertTextNotInTable(String tableSummary, String text) {
        tester.assertTextNotInTable(tableSummary, text);
    }

    public void assertTextNotInTable(String tableSummary, String[] text) {
        tester.assertTextNotInTable(tableSummary, text);
    }

    public void assertTableEquals(String tableSummary, ExpectedTable expectedTable) {
        tester.assertTableEquals(tableSummary, expectedTable.getExpectedStrings());
    }

    public void assertTableEquals(String tableSummary, String[][] expectedCellValues) {
        tester.assertTableEquals(tableSummary, expectedCellValues);
    }

    public void assertTableRowsEqual(String tableSummary, int startRow, ExpectedTable expectedTable) {
        tester.assertTableRowsEqual(tableSummary, startRow, expectedTable);
    }

    public void assertTableRowsEqual(String tableSummary, int startRow, String[][] expectedCellValues) {
        tester.assertTableRowsEqual(tableSummary, startRow, expectedCellValues);
    }

    public void assertFormParameterPresent(String parameterName) {
        tester.assertFormParameterPresent(parameterName);
    }

    public void assertFormParameterNotPresent(String parameterName) {
        tester.assertFormParameterNotPresent(parameterName);
    }

    public void assertHasForm() {
        tester.assertHasForm();
    }

    public void assertHasForm(String formName) {
        tester.assertHasForm(formName);
    }

    public void assertFormParameterEquals(String parameterName, String expectedValue) {
        tester.assertFormParameterEquals(parameterName, expectedValue);
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

    protected void submit() {
        tester.submit();
    }

    public void submit(String buttonName) {
        tester.submit(buttonName);
    }

    protected void clickLink(String linkText) {
        tester.clickLink(linkText);
    }

    protected void clickLinkByID(String anID) {
        tester.clickLinkByID(anID);
    }

    protected void dumpResponse(PrintStream stream) {
        tester.dumpResponse(stream);
    }

    protected void dumpTable(String tableName, PrintStream stream) {
        tester.dumpTable(tableName, stream);
    }

    protected void dumpTable(String tableName, String[][] table) {
        tester.dumpTable(tableName, table);
    }

    protected void setFormParameter(String parameterName, String value) {
        tester.setFormParameter(parameterName, value);
    }

    protected void removeFormParameter(String parameterName) {
        tester.removeFormParameter(parameterName);
    }

    public void setWorkingForm(String nameOrId) {
        tester.setWorkingForm(nameOrId);
    }


    public String[] getOptionsFor(String selectName) {
        return tester.getOptionsFor(selectName);
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

    public void assertSelectedOptionEquals(String selectName, String option) {
        tester.assertOptionEquals(selectName, option);
    }

    public void selectOption(String selectName, String option) {
        tester.selectOption(selectName, option);
    }

    public void assertElementPresent(String anID) {
        tester.assertElementPresent(anID);
    }

    public void assertElementNotPresent(String anID) {
        tester.assertElementNotPresent(anID);
    }

}
