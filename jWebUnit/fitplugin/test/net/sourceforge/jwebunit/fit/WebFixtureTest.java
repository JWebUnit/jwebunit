/*
 * User: djoiner
 * Date: Sep 26, 2002
 * Time: 11:36:01 AM
 */
package net.sourceforge.jwebunit.fit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jwebunit.WebTester;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.DumpHandler;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.util.Resource;

import junit.framework.TestCase;

public class WebFixtureTest extends TestCase {

    public static final int MINIMUM_TESTS = 50;
    public static final String PLUGIN_FOLDER = "fitplugin/";
    public static final String TEST_HTML_FOLDER = "sampleHtml/";
    public static final int JETTY_PORT_DEFAULT = 8081;
    public static final String JETTY_PORT_PROPERTY = "jetty.port";
    public static final String JETTY_CONTEXT = "/";
    public static final String JETTY_HOST = "localhost";
    
    private HttpServer server = null;
    private String testRoot = "test/";
    
    // translation between urls used in old .fit files and the urls in jetty context
    private static Map oldUrls = null;
    static {
        oldUrls = new HashMap();
        oldUrls.put("/colorForm", "/ColorForm.html");
        oldUrls.put("/moriaDoorForm", "/MoriaDoorForm.html");
        oldUrls.put("/personalInfoForm", "/PersonalInfoForm.html");
        oldUrls.put("/sampleMenu", "/SampleMenu.html");
        oldUrls.put("/pageWithPopupLink", "/pageWithPopupLink.html");
        oldUrls.put("/menu", "/SampleMenu.html");
    }
    
    public WebFixtureTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setUpConfiguredServer();
        setUpContextWithTestWebapp();
        // run
        server.start();
    }
    
    protected void tearDown() throws Exception {
        server.stop();
        super.tearDown();
    }

    public void testFixtureStart() throws Exception {
        WebFixture fixture = new WebFixture();
        fixture.setBaseUrl("http://" + JETTY_HOST + ":" + getJettyPort());
        WebTester tester = WebFixture.tester;
        tester.beginAt("index.html");
        tester.assertTitleEquals("Test root");
    }

    public void testWebFixture() throws Exception {
        //new PseudoWebApp();
        // avoid the need of the system property, always use .fit files for input
        RunnerUtility.overrideSystemPropertyAndUseWikiParser = true;
        // run the tests
        DirectoryRunner testRunner = 
            DirectoryRunner.parseArgs(new String[]
                {testRoot + "testInput",
                 testRoot + "testOutput"});
        testRunner.run();
        testRunner.getResultWriter().write();
        // sanity check
        assertTrue("Should find at least " + MINIMUM_TESTS + " tests",
                0 < testRunner.getResultWriter().getTotal());
        // report failures to JUnit
        String resultsUrl = testRoot + "testOutput/index.html";
        assertEquals("Failures detected. Check " + resultsUrl + ".", 0, 
            testRunner.getResultWriter().getCounts().wrong);
        assertEquals("Exceptions detected. Check " + resultsUrl + ".", 0, 
            testRunner.getResultWriter().getCounts().exceptions);
    }    
    
    // ---- below are methods for setting up the test web ----
    
    private void setUpConfiguredServer() throws UnknownHostException {
        server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort(getJettyPort());
        listener.setHost(JETTY_HOST);
        listener.setMinThreads(1);
        listener.setMaxThreads(10);
        server.addListener(listener);
    }
    
    private void setUpContextWithTestWebapp() throws IOException {
        // add the files in sampleHtml to context
        HttpContext context = server.addContext(JETTY_CONTEXT);
        setUpPathToStaticContents(context);
        if (!context.getResource("index.html").exists()) {
            // allow the test to run from parent project
            testRoot = PLUGIN_FOLDER + testRoot;
            setUpPathToStaticContents(context);
        }
        // check that the context root contains the web pages
        assertTrue("Should find index.html in the configured jetty context: " + context.getResourceBase(),
                context.getResource("index.html").exists());
        setUpContextHandlers(context);
    }

    private void setUpPathToStaticContents(HttpContext context) {
        context.setResourceBase(testRoot + TEST_HTML_FOLDER);
    }

    private void setUpContextHandlers(HttpContext context) {
        // handle static HTML
        context.addHandler(getStaticHTMLResourceHandler()); 
        //context.addHandler(new DumpHandler());
    }
    
    private ResourceHandler getStaticHTMLResourceHandler() {
        return new ResourceHandler() {
            protected Resource getResource(String pathInContext) throws IOException {
                Resource r = super.getResource(pathInContext);
                if (!r.exists() && oldUrls.containsKey(pathInContext)) { // don't want to update the .fit files until the old tests work
                    r = super.getResource(oldUrls.get(pathInContext).toString());
                }
                assertTrue("The requested resource '" + pathInContext + "' must exist", r.exists());
                return r;
            }
        };
    }
    
    public static int getJettyPort() {
        String p = System.getProperty(JETTY_PORT_PROPERTY, "" + JETTY_PORT_DEFAULT);
        return Integer.parseInt(p);
    }

}