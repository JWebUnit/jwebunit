/*
 * User: DJoiner
 * Date: Aug 16, 2002
 * Time: 8:41:00 AM
 */
package net.sourceforge.jwebunit.tests;

import javax.servlet.http.Cookie;

import net.sourceforge.jwebunit.TestContext;

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
        context.addCookie("key", "val");
        context.setLocale(Locale.CANADA_FRENCH);
    }


    public void testInit() {
        assertTrue(context.hasAuthorization());
        assertTrue(context.hasCookies());
        assertEquals(context.getUser(), "user");
        assertEquals(context.getPassword(), "pwd");
        List cookies = context.getCookies();
        Cookie c = (Cookie)cookies.get(0);
        assertEquals(c.getName(), "key");
        assertEquals(c.getValue(), "val");
        assertEquals(Locale.CANADA_FRENCH, context.getLocale());
        assertEquals("http://localhost:8080", context.getBaseUrl());
        assertNull(context.getResourceBundleName());
    }

    public void testResourceBundle() {
        String name = "net.sourceforge.jwebunit.TestContextBundle";
        context.setResourceBundleName("net.sourceforge.jwebunit.TestContextBundle");
        assertEquals(name, context.getResourceBundleName());
    }

    public void testUserAgent() {
        getTestContext().setBaseUrl(HOST_PATH + "/TestContextTest");
        String userAgent = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.3) Gecko/20060426 Firefox/1.5.0.3";
        getTestContext().setUserAgent(userAgent);
        beginAt("/testPage.html");
        dumpResponse(System.out);
        assertTextPresent("Browser user-agent: "+userAgent);
    }

}
