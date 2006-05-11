/* Copyright 2005 Optime data Sweden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Tests the use of HttpUnit and the types of html documents accepted.
 */
public class HtmlParsingTest extends JWebUnitAPITestCase {

    // URL: getDialog().getWebClient().getCurrentPage().getText()
    
    public static Test suite() {
        Test suite = new TestSuite(HtmlParsingTest.class);
        return new JettySetup(suite);
    }   
    
    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/HtmlTest/");
    }
    
    public void testSimpleForm() {
        beginAt("SimpleForm.html");
        assertFormPresent();
        submit();
    }

    public void testInvalidForm() {
        beginAt("InvalidForm.html");
        assertFormPresent();
        submit();
    }

    public void testInvalidFormNoDoctype() {
        beginAt("InvalidFormNoDoctype.html");
        assertFormPresent();
        submit();
    }    

    public void testValidComplexForm() {
        beginAt("ValidComplexForm.html");
        assertFormPresent();
        submit();
    }    

    public void testValidFormNoDoctype() {
        beginAt("ValidFormNoDoctype.html");
        assertFormPresent();
        submit();
    }    
    
    public void testXhtmlStrict() {
        beginAt("XhtmlStrict.html");
        // run a method that gets the DOM
        assertElementPresent("div1");
        //TODO This test gives a "org.w3c.dom.DOMException: NOT_SUPPORTED_ERR" with nekohtml 0.9.5 and httpunit
    }
}
