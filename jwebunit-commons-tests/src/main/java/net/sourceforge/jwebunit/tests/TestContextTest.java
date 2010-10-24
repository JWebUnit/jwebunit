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

import javax.servlet.http.Cookie;

import net.sourceforge.jwebunit.util.TestContext;

import java.util.List;
import java.util.Locale;

public class TestContextTest extends JWebUnitAPITestCase {
    private TestContext context;

    public TestContextTest(String s) {
        super(s);
    }

    public void setUp() throws Exception {
        super.setUp();
        context = new TestContext();
        context.setAuthorization("user", "pwd");
        context.addCookie("key", "val", "www.foo.bar");
        context.setLocale(Locale.CANADA_FRENCH);
    }


    public void testInit() {
        assertTrue(context.hasAuthorization());
        assertTrue(context.hasCookies());
        assertEquals(context.getUser(), "user");
        assertEquals(context.getPassword(), "pwd");
        List<?> cookies = context.getCookies();
        Cookie c = (Cookie)cookies.get(0);
        assertEquals(c.getName(), "key");
        assertEquals(c.getValue(), "val");
        assertEquals(c.getDomain(), "www.foo.bar");
        assertEquals(Locale.CANADA_FRENCH, context.getLocale());
        assertEquals("http://localhost:8080", context.getBaseUrl().toString());
        assertNull(context.getResourceBundleName());
    }

    public void testResourceBundle() {
        String name = "/TestContextBundle";
        context.setResourceBundleName("/TestContextBundle");
        assertEquals(name, context.getResourceBundleName());
    }

    public void testUserAgent() {
        getTestContext().setBaseUrl(HOST_PATH + "/TestContextTest");
        String userAgent = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.3) Gecko/20060426 Firefox/1.5.0.3";
        getTestContext().setUserAgent(userAgent);
        beginAt("/testPage.html");
        assertTextPresent("Browser user-agent: "+userAgent);
    }

}
