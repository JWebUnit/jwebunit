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

import java.util.List;
import net.sourceforge.jwebunit.api.HttpHeader;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertCookiePresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertCookieValueEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertCookieValueMatch;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getResponseHeaders;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static org.junit.Assert.assertTrue;


/**
 * Test the Cookies methods provided by WebTestCase.
 * 
 * @author Julien HENRY
 */
public class WebCookieTest extends JWebUnitAPITestCase {


    public void setUp() throws Exception {
        super.setUp();
		getTestContext().addCookie("cookie1", "Cookievalue1", "localhost");
		setBaseUrl(HOST_PATH);
    }
    
    @Test
    public void testAddCookie() {
    	beginAt("/cookies.jsp");
    	assertTextPresent("cookie1=Cookievalue1");
    }
    
    @Test
    public void testAddAnotherCookie() {
    	getTestContext().addCookie("cookie2", "Cookievalue2", "localhost");
    	beginAt("/cookies.jsp");
    	assertTextPresent("cookie1=Cookievalue1");
    	assertTextPresent("cookie2=Cookievalue2");
    }

    @Test
    public void testAssertCookiePresent() throws Throwable {
    	beginAt("/cookies.jsp");
    	assertCookiePresent("serveurCookie");
	}

    @Test
    public void testAssertCookieValue() throws Throwable {
    	beginAt("/cookies.jsp");
    	assertCookieValueEquals("serveurCookie", "foo");
	}

    @Test
    public void testAssertCookieMatch() throws Throwable {
    	beginAt("/cookies.jsp");
    	assertCookieValueMatch("serveurCookie", "fo*");
	}
    
    /**
     * When there are several cookies with the same name, it is the last one that should
     * be taken.
     * See <a href="http://tools.ietf.org/html/draft-ietf-httpstate-cookie-21#section-5.3">the spec</a> (§ 11)
     */
    @Test
    public void testCookieMatchLastCookie() {
        beginAt("/cookies.jsp?set_by_headers=true&dont_set=1");
        assertCookieValueMatch("JSESSIONID", "(.)*worker2"); 
    }

    
    /**
     * Test that the cookie still exists across multiple requests,
     * even if the cookie is not explicitly set each time.
     */
    @Test
    public void testCookieWithoutExplicitSet() {
    	beginAt("/cookies.jsp");		// beginAt also resets cookies
    	assertCookieValueEquals("serveurCookie", "foo");
    	gotoPage("/cookies.jsp?dont_set=1");
    	assertCookieValueEquals("serveurCookie", "foo");	// should still be there
    	gotoPage("/cookies.jsp?dont_set=1");
    	assertCookieValueEquals("serveurCookie", "foo");	// should still be there
    	gotoPage("/cookies.jsp?dont_set=1");
    	assertCookieValueEquals("serveurCookie", "foo");	// should still be there
    }
    
    /**
     * Tests if all cookies are received when the server sets several cookies 
     * with same domain, path and name.<p>
     * 
     * See http://tools.ietf.org/html/draft-ietf-httpstate-cookie-21#section-5.3, 11
     */
    @Test
    public void testCookieSetInHeaders() {
        beginAt("/cookies.jsp?set_by_headers=true&dont_set=1");
        List<HttpHeader> headers = getResponseHeaders();
        boolean foundCookie1 = false;
        boolean foundCookie2 = false;
        for (HttpHeader h : headers) {
            if (h.getName().equals("Set-Cookie")) {
                if (h.getValue().contains(".worker1")) {
                    foundCookie1 = true;
                }
                else if (h.getValue().contains(".worker2")) {
                    foundCookie2 = true;
                }
            }
        }
        assertTrue("getResponseHeaders should return all headers even duplicates", foundCookie1 && foundCookie2);
    }
}