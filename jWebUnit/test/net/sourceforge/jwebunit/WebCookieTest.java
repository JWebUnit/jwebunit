package net.sourceforge.jwebunit;

import net.sourceforge.jwebunit.JWebUnitTest;

/**
 * Test the Cookies provided by WebTestCase using the PseudoServer test package provided
 * by Russell Gold in httpunit.
 *
 * @author  Vivek Venugopalan
 */
public class WebCookieTest extends JWebUnitTest {

    public void setUp() throws Exception {
        getTestContext().addCookie("cookie1", "Cookievalue1");
        super.setUp();
        addTestPage();
        beginAt("/testPage.html");
    }


    public void testAssertCookieDump() throws Throwable {
            dumpCookies();
    }

    private void addTestPage() {
    	defineWebPage("testPage", "This is a test page." +
    			"<table summary=\"testTable\">" +
				"<tr ID=\"row1\"><td>table text</td></tr>" +
				"<tr><td>table text row 2</td></tr>" +
				"<tr><td>table text row 3</td><td>row 3 col 1</td>" +
				"<a id=\"test_link_id\" href=\"someurl.html\">test link</a>" +
				"<a id=\"test_graphic_link_id\" href=\"someurl2.html\"><img src=\"graphic.jpg\"/></a>" +
				"<form>" +
				"<input type=\"text\" name=\"testInputElement\" value=\"testValue\"/>" +
				"<input type=\"submit\" name=\"submitButton\" value=\"buttonLabel\"/>" +
				"<input type=\"checkbox\" name=\"checkboxselected\" CHECKED>" +
				"<input type=\"checkbox\" name=\"checkboxnotselected\">" +
				"</form>" +
				"<span id=\"span_id\">Span Text</span>" +
				"<span id=\"outer_id\">Outer <span id=\"inner_id\">Inner Text</span> Text</span>" +
				"<form name=\"form2\"></form>" +
    	"</table>");
    	defineWebPage("noFormPage", "");
    }
}
