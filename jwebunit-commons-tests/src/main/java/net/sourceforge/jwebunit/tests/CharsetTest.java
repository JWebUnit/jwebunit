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

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Make sure JWebUnit handles character conversions properly.
 * 
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class CharsetTest extends JWebUnitAPITestCase {
    public CharsetTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new JettySetup(new TestSuite(CharsetTest.class));
    }

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/CharsetTest");
        beginAt("/charset.html_utf-8");
    }

    public void testEuro() {
        assertTextFieldEquals("eur", "\u20AC");
    }

    public void testDollar() {
        assertTextFieldEquals("usd", "$");
    }

    public void testYen() {
        assertTextFieldEquals("yen", "\u00A5");
    }

    public void testPound() {
        assertTextFieldEquals("gbp", "\u00A3");
    }
}
