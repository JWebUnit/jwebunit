package net.sourceforge.jwebunit.sample;

import junit.framework.TestCase;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;

/**
 * HttpUnit and JUnit search test.
 *
 * @author Jim Weaver
 */
public class SearchExample extends TestCase {

    public void testSearch() throws Exception {
        WebConversation wc = new WebConversation();
        WebResponse resp = wc.getResponse( "http://www.google.com");
        WebForm form = resp.getForms()[0];
        form.setParameter("q", "HttpUnit");
        WebRequest req = form.getRequest("btnG");
        resp = wc.getResponse(req);
        assertNotNull(resp.getLinkWith("HttpUnit"));
        resp = resp.getLinkWith("HttpUnit").click();
        assertEquals(resp.getTitle(), "HttpUnit");
        assertNotNull(resp.getLinkWith("User's Manual"));
    }
}