/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
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
                getTestContext().setBaseUrl(HOST_PATH+"/CharsetTest");
                beginAt("/charset.html_utf-8");
        }

        public void testEuro() {
                assertFormElementEquals("eur", "\u20AC");
        }

        public void testDollar() {
                assertFormElementEquals("usd", "$");
        }

        public void testYen() {
                assertFormElementEquals("yen", "\u00A5");
        }

        public void testPound() {
                assertFormElementEquals("gbp", "\u00A3");
        }
}