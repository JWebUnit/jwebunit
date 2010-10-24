/**
 * Copyright (c) 2010, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */


package net.sourceforge.jwebunit.tests;

import java.text.NumberFormat;
import java.util.Locale;

import net.sourceforge.jwebunit.tests.util.JettySetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for MessageBundle related assertions
 * @author Chris Eldredge
 */
public class ResourceBundleAssertionsTest extends JWebUnitAPITestCase {

    static final String resourceBundleName = "MessageBundle";

    static final String pageTitle = "Keyed Message Title - Main Page";

    static final String text = "This is a test page.";

    public static Test suite() {
        Test suite = new TestSuite(ResourceBundleAssertionsTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/ResourceBundleAssertionsTest");
        getTestContext().setResourceBundleName(resourceBundleName);
        beginAt("/testPage.html");
    }

    public void testGetMessage() {
        assertEquals(pageTitle, getMessage("title.fixed"));
    }

    public void testGetMessageWithArg() {
        assertEquals(pageTitle, getMessage("title.with.args", new String[] { "Main Page" }));
    }

    /**
     * Verify number formatting works in Default Locale
     */
    public void testGetFormattedMessage() {
        final Double amount = new Double(1234.56);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        final String expectedValue = numberFormat.format(amount);

        assertEquals(expectedValue, tester
                .getMessage("currency.with.args", new Object[] { amount }));
    }

    /**
     * Verify Test uses the Locale specified by framework
     */
    public void testGetLocalizedFormattedMessage() {
        final Double amount = new Double(1234.56);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        final String expectedValue = numberFormat.format(amount);

        getTestContext().setLocale(Locale.FRANCE);

        assertEquals(expectedValue, tester
                .getMessage("currency.with.args", new Object[] { amount }));
    }

    public void testAssertTitleEqualsKey() throws Throwable {
        assertPassFail("assertTitleEqualsKey", "title.fixed", "title.not.used");
    }

    public void testAssertTitleEqualsKeyWithArg() throws Throwable {
        //assertTitleEqualsKey("title.with.args", new String[] {"Main Page"});
        assertPassFail("assertTitleEqualsKey", new Object[] { "title.with.args",
                new Object[] { "Main Page" } }, new Object[] { "title.with.args",
                new Object[] { "Wrong" } });
    }

    public void testAssertKeyPresent() throws Throwable {
        assertPassFail("assertKeyPresent", "text.fixed", "title.not.used");
    }

    public void testAssertKeyPresentWithArg() throws Throwable {
        assertPassFail("assertKeyPresent",
                new Object[] { "text.with.args", new Object[] { "test" } }, new Object[] {
                        "text.with.args", new Object[] { "wrong" } });
    }

    public void testAssertTextNotPresent() throws Throwable {
        assertPassFail("assertTextNotPresent", "no such text", text);
    }

    public void testAssertNotKeyPresent() throws Throwable {
        assertPassFail("assertKeyNotPresent", "title.not.used", "text.fixed");
    }

    public void testAssertKeyNotPresentWithArg() throws Throwable {
        assertPassFail("assertKeyNotPresent", new Object[] { "text.with.args",
                new Object[] { "wrong" } }, new Object[] { "text.with.args",
                new Object[] { "test" } });
    }

    public void testAssertKeyInTable() throws Throwable {
        assertPassFail("assertKeyInTable", new Object[] { "testTable", "table.fixed" },
                new Object[] { "testTable", "title.not.used" });
    }

    public void testAssertKeyInTableWithArgs() throws Throwable {
        assertPassFail("assertKeyInTable", new Object[] { "testTable", "table.with.args",
                new Object[] { "Data", "Table" } }, new Object[] { "testTable", "table.with.args",
                new Object[] { "wrong" } });
    }

    public void testAssertKeyNotInTable() throws Throwable {
        assertPassFail("assertKeyNotInTable", new Object[] { "testTable", "title.not.used" },
                new Object[] { "testTable", "table.fixed" });
    }

    public void testAssertKeysInTable() throws Throwable {
        assertPassFail("assertKeysInTable", new Object[] { "testTable",
                new String[] { "table.fixed", "table2.fixed" } }, new Object[] { "testTable",
                new String[] { "table.fixed", "title.not.used" } });
    }

    public void testAssertKeysInTableWithArgs() throws Throwable {
        // okay, it's not pretty but somebody *might* want to use it...
        assertPassFail("assertKeysInTable",
                new Object[] {
                        "testTable",
                        new String[] { "table.with.args", "table2.with.args" },
                        new Object[][] { new Object[] { "Data", "Table" },
                                new Object[] { "Data", "In" } } }, new Object[] { "testTable",
                        new String[] { "table.with.args", "table2.with.args" },
                        new Object[][] { new Object[] { "Wrong" }, new Object[] { "Wrong" } } });
    }

}