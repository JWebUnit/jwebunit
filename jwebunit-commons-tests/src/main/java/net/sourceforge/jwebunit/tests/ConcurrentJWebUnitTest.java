/**
 * Copyright (c) 2011, JWebUnit team.
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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithText;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.code.tempusfugit.concurrency.annotations.Repeating;

import com.google.code.tempusfugit.concurrency.annotations.Concurrent;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;

import com.google.code.tempusfugit.concurrency.RepeatingRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test parallel execution of JWebUnit.
 * 
 * @author Julien Henry
 */
public class ConcurrentJWebUnitTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
    	//don't call super.setUp to avoid temporary state where baseUrl = HOST_PATH
    	getTestContext().setAuthorization("admin", "admin");
    	setBaseUrl(HOST_PATH + "/NavigationTest");
    }

    @Rule public Timeout timeoutRule = new Timeout(10000);

    @Rule public ConcurrentRule concurrently = new ConcurrentRule();
    @Rule public RepeatingRule repeatedly = new RepeatingRule();

    @Test
    @Concurrent(count = 5)
    @Repeating (repetition = 10)
    public void testClickLinkWithTextN() {
        beginAt("/pageWithLink.html");
        assertTitleEquals("pageWithLink");

        clickLinkWithText("an active link", 0);
        assertTitleEquals("targetPage");

        beginAt("/pageWithLink.html");
        clickLinkWithText("an active link", 1);

        assertTitleEquals("targetPage2");
        beginAt("/pageWithLink.html");
        try {
            clickLinkWithText("an active link", 2);
            fail();
        } catch (AssertionError expected) {
            assertEquals("Link with text [an active link] and index [2] "
                    + "not found in response.", expected.getMessage());
        }
        assertTitleEquals("pageWithLink");
    }

}