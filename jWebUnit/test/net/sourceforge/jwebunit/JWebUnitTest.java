package net.sourceforge.jwebunit;

import com.meterware.pseudoserver.PseudoServer;
import com.meterware.pseudoserver.PseudoServlet;
import net.sourceforge.jwebunit.util.reflect.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

import junit.framework.AssertionFailedError;

/**
 * Superclass for testing jWebUnit.  Uses test utilities from httpunit's test package.
 *
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class JWebUnitTest extends WebTestCase {

    protected static final Object[] NOARGS = new Object[0];
    protected String hostPath;
    protected static PseudoServer server = new PseudoServer();

    public JWebUnitTest() {
        try {
            hostPath = "http://localhost:" + server.getConnectedPort();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setUp() throws Exception {
       super.setUp();
       getTestContext().setBaseUrl(hostPath);
    }

    public void tearDown() throws Exception {
//        if (server != null) server.shutDown();
        server.clearResources();
    }

    protected void defineResource(String resourceName, String value) {
        server.setResource(resourceName, value);
    }

    protected void defineWebPage(String pageName, String body) {
        defineResource(pageName + ".html", "<html><head><title>" + pageName + "</title></head>\n" +
                                           "<body>" + body + "</body></html>");
    }

    protected void defineResource(String resourceName, PseudoServlet servlet) {
        server.setResource(resourceName, servlet);
    }

    public void assertPassFail(String methodName, Object passArg, Object failArgs) throws Throwable {
        assertPassFail(methodName, new Object[]{passArg}, new Object[]{failArgs});
    }

    public void assertPassFail(String methodName, Object[] passArgs, Object[] failArgs) throws Throwable {
        assertPass(methodName, passArgs);
        assertFail(methodName, failArgs);
    }

    protected void assertPass(String methodName, Object arg) throws Throwable {
        this.assertPass(methodName, new Object[] {arg});
    }


    protected void assertPass(String methodName, Object[] args) throws Throwable {
        MethodInvoker invoker = new MethodInvoker(this, methodName, args);
        try {
            invoker.invoke();
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public void assertFail(String methodName, Object arg) {
        assertFail(methodName, new Object[]{arg});
    }

    public void assertFail(String methodName, Object[] args) {
        assertException(AssertionFailedError.class, methodName, args);
    }

    public void assertException(Class exceptionClass, String methodName, Object[] args) {
        MethodInvoker invoker = new MethodInvoker(this, methodName, args);
        try {
            invoker.invoke();
            fail("Expected test failure did not occur for method: " + methodName);
        } catch (InvocationTargetException e) {
            assertTrue("Expected " + exceptionClass.getName() + "but was " + e.getTargetException().getClass().getName(),
                       exceptionClass.isInstance(e.getTargetException()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    protected void gotoResource(String url) {

    }
}
