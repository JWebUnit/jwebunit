package net.sourceforge.jwebunit;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meterware.httpunit.WebClient;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class ServletUnitTest extends JWebUnitTest {

    public void setUpWebClient() {
        ServletRunner runner = new ServletRunner();
        runner.registerServlet("myServlet.html", MyServlet.class.getName());

        WebClient client = runner.newClient();
        getTestContext().setWebClient(client);
        getTestContext().setBaseUrl("http://dummy/");
    }

    public void testSetUpWithoutJwebunit() throws Exception {
        setUpWebClient();

        WebClient client = getTestContext().getWebClient();
        WebResponse response =
            client.getResponse("http://dummy/myServlet.html");
        assertEquals("text/html", response.getContentType());
        assertEquals(
            "<html><head><title>myServlet</title></head>\n"
                + "<body></body></html>\n",
            response.getText());
    }

    public void testNullContextToHttpUnitDialog() throws Exception {
        checkWebConversation(null);
    }

    public void testNormalContextToHttpUnitDialog() throws Exception {
        checkWebConversation(getTestContext());
    }

    private void checkWebConversation(TestContext whichContext) {
        defineResource("foo.html", "foo");

        String url = getTestContext().getBaseUrl() + "foo.html";
        HttpUnitDialog dialog = new HttpUnitDialog(url, whichContext);

        assertTrue(dialog.getWebClient() instanceof WebConversation);
        assertSame(dialog.getWebClient(), dialog.getWebConversation());
    }

    public void testServletUnit() throws Exception {
        setUpWebClient();

        beginAt("/myServlet.html");
        assertTitleEquals("myServlet");

        assertTrue(getDialog().getWebClient() instanceof ServletUnitClient);
        try {
            getDialog().getWebConversation();
            fail();
        } catch (ClassCastException expected) {
        }        
    }

    public static class MyServlet extends HttpServlet {
        public void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().print(
                "<html><head><title>myServlet</title></head>\n"
                    + "<body></body></html>\n");
        }
    }
}
