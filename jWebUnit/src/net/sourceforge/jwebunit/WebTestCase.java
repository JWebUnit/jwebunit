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

    public void assertKeyInResponse(String key) {
        tester.assertKeyInResponse(key);
    }

    public void assertTextInResponse(String text) {
        tester.assertTextInResponse(text);
    }

    public void assertKeyNotInResponse(String key) {
        tester.assertKeyNotInResponse(key);
    }

    public void assertTextNotInResponse(String text) {
        tester.assertTextNotInResponse(text);
    }

    public void assertTablePresent(String tableSummary) {
        tester.assertTablePresent(tableSummary);
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

    public void assertFormControlPresent(String formControlName) {
        tester.assertFormControlPresent(formControlName);
    }

    public void assertFormControlNotPresent(String formControlName) {
        tester.assertFormControlNotPresent(formControlName);
    }

    public void assertHasForm() {
        tester.assertHasForm();
    }

    public void assertHasForm(String formName) {
        tester.assertHasForm(formName);
    }

    public void assertFormControlEquals(String formControlName, String expectedValue) {
        tester.assertFormControlEquals(formControlName, expectedValue);
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

    public void assertLinkInResponse(String linkText) {
        tester.assertLinkInResponse(linkText);
    }

    public void assertLinkNotInResponse(String linkText) {
        tester.assertLinkNotInResponse(linkText);
    }

    public void assertLinkPresentByID(String anId) {
        tester.assertLinkPresentByID(anId);
    }

    public void assertLinkNotPresentByID(String anId) {
        tester.assertLinkNotPresentByID(anId);
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

}
