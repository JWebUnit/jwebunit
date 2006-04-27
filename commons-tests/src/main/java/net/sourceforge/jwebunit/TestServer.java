package net.sourceforge.jwebunit;

import java.io.IOException;

import com.meterware.pseudoserver.HttpUserAgentTest;
import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.httpunit.WebConversation;

/**
 * Contributed by 
 * @author Archie Cowan (archiec at users.sourceforge.net)
 * @deprecated
 */
public class TestServer extends HttpUserAgentTest {

    static {
        new WebConversation();
    }

    public TestServer() {
    	super("TestServer");
    }

    public void defineWebPage(String name, String body) {
        super.defineWebPage(name, body);
    }

    public void defineResource(String name, String value) {
        super.defineResource(name, value);
    }

    public void defineResource(String name, PseudoServlet servlet) {
        super.defineResource(name, servlet);
    }

    public int getConnectedPort() throws IOException {
    	return getHostPort();
    }
    
    public String getHostPath() {
        return super.getHostPath();
    }

    public void setUp() throws Exception {
        super.setUp();
        Thread.sleep(50);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }
}
