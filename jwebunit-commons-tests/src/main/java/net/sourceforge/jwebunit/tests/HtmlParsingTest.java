/**
 * Copyright (c) 2002-2014, JWebUnit team.
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

import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static net.sourceforge.jwebunit.junit.JWebUnit.submit;

import org.junit.Test;

/**
 * Tests the use of HttpUnit and the types of html documents accepted.
 */
public class HtmlParsingTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/HtmlTest/");
    }
    
    @Test public void testSimpleForm() {
        beginAt("SimpleForm.html");
        assertFormPresent();
        submit();
    }

    @Test public void testInvalidForm() {
        beginAt("InvalidForm.html");
        assertFormPresent();
        submit();
    }

    @Test public void testInvalidFormNoDoctype() {
        beginAt("InvalidFormNoDoctype.html");
        assertFormPresent();
        submit();
    }    

    @Test public void testValidComplexForm() {
        beginAt("ValidComplexForm.html");
        assertFormPresent();
        submit();
    }    

    @Test public void testValidFormNoDoctype() {
        beginAt("ValidFormNoDoctype.html");
        assertFormPresent();
        submit();
    }    
    
    @Test public void testXhtmlStrict() {
        beginAt("XhtmlStrict.html");
        // run a method that gets the DOM
        assertElementPresent("div1");
        //TODO This test gives a "org.w3c.dom.DOMException: NOT_SUPPORTED_ERR" with nekohtml 0.9.5 and httpunit
    }
}
