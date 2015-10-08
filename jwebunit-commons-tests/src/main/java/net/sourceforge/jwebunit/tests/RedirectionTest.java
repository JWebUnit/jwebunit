/**
 * Copyright (c) 2002-2015, JWebUnit team.
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
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;

import org.junit.Test;

/**
 * Test redirection support.
 * 
 * @author Julien Henry
 */
public class RedirectionTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/RedirectionTest");
    }

    @Test public void testRedirection() {
        beginAt("/redirect.html");
        assertTitleEquals("Redirected");
        closeBrowser();
        beginAt(HOST_PATH + "/redirect.jsp");
        assertTitleEquals("Redirected");
    }

}
