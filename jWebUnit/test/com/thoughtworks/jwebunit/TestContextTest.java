/*
 * User: DJoiner
 * Date: Aug 16, 2002
 * Time: 8:41:00 AM
 */
package net.sourceforge.jwebunit;

import junit.framework.TestCase;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Locale;

public class TestContextTest extends TestCase {
    private TestContext context;

    public TestContextTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
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
    }

}
