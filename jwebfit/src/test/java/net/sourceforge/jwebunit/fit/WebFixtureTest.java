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
import net.sourceforge.jwebunit.fit.testservlets.ColorPostServlet;
import net.sourceforge.jwebunit.fit.testservlets.MoriaPostServlet;
import net.sourceforge.jwebunit.fit.testservlets.PersonalInfoPostServlet;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.Resource;

import junit.framework.TestCase;

public class WebFixtureTest extends TestCase {

	/**
	 * The minimum number of FIT checks in an accepted test suite.
	 * This is here so that the test won't pass in absence of FIT tables.
	 */
    public static final int MINIMUM_TESTS = 50;
    
    // file locations for running FIT
    public static final String FIT_FOLDER = "src/test/fit/";
    public static final String OUTPUT_FOLDER = "target/fit-reports/";
    public static final String TEST_HTML_FOLDER = "src/test/resources/testSite/";
    
    // server options
    public static final int JETTY_PORT_DEFAULT = 8082;
    public static final String JETTY_PORT_PROPERTY = "jetty.port";
    public static final String JETTY_CONTEXT = "/";
    public static final String JETTY_HOST = "localhost";
    
    private HttpServer server = null;
    
    // translation between urls used in old .fit files and the urls in jetty context
    private static Map oldUrls = null;
    static {
        oldUrls = new HashMap();
        oldUrls.put("/colorForm", "/ColorForm.html");
        oldUrls.put("/enterMoria", "/MoriaDoorForm.html");
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
        fixture.setBaseUrl(getApplicationRootUrl());
        WebTester tester = WebFixture.tester;
        tester.beginAt("readme.html");
        tester.assertTitleEquals("Test root");
    }

    protected String getApplicationRootUrl() {
        return "http://" + JETTY_HOST + ":" + getJettyPort();
    }

    public void testWebFixture() throws Exception {
        // avoid the need of the system property, always use .fit files for input
        RunnerUtility.overrideSystemPropertyAndUseWikiParser = true;
        // run the tests
        DirectoryRunner testRunner = 
            DirectoryRunner.parseArgs(new String[]
                {FIT_FOLDER, OUTPUT_FOLDER});
        testRunner.run();
        testRunner.getResultWriter().write();
        // sanity check
        assertTrue("Should find at least " + MINIMUM_TESTS + " tests",
                0 < testRunner.getResultWriter().getTotal());
        // report failures to JUnit
        String resultsUrl = OUTPUT_FOLDER + "index.html";
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
//        if (!context.getResource("readme.html").exists()) {
//            // allow the test to run from parent project
//            testRoot = FIT_FOLDER + testRoot;
//            setUpPathToStaticContents(context);
//        }
        // check that the context root contains the web pages
        assertTrue("Should find readme.html in the configured jetty context: " + context.getResourceBase(),
                context.getResource("readme.html").exists());
        setUpContextHandlers(context);
    }

    private void setUpPathToStaticContents(HttpContext context) {
        context.setResourceBase(TEST_HTML_FOLDER);
    }

    private void setUpContextHandlers(HttpContext context) {
        // servlet handlers for the post requests from old .fit files
        ServletHandler handler= new ServletHandler();
        handler.addServlet("colorPost","/colorPost", ColorPostServlet.class.getName());
        handler.addServlet("moriaPost","/moriaPost", MoriaPostServlet.class.getName());
        handler.addServlet("personalInfoPost", "/personalInfoPost", PersonalInfoPostServlet.class.getName());
        context.addHandler(handler);
        // handle static HTML
        context.addHandler(getStaticHTMLResourceHandler()); 
    }
    
    private ResourceHandler getStaticHTMLResourceHandler() {
        ResourceHandler handler = new ResourceHandler() {
            private static final long serialVersionUID = 1L;
            protected Resource getResource(String pathInContext) throws IOException {
                Resource r = super.getResource(pathInContext);
                // don't want to update the .fit files until the old tests work
                if (!r.exists() && oldUrls.containsKey(pathInContext)) {
                    r = super.getResource(oldUrls.get(pathInContext).toString());
                }
                if (!r.exists()) {
                    
                }
                assertTrue("The requested resource '" + pathInContext + "' must exist", r.exists());
                return r;
            }
        };
        return handler;
    }
    
    public static int getJettyPort() {
        String p = System.getProperty(JETTY_PORT_PROPERTY, "" + JETTY_PORT_DEFAULT);
        return Integer.parseInt(p);
    }

}