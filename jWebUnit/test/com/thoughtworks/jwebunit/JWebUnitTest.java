package net.sourceforge.jwebunit;

import net.meterware.httpunit.PseudoServer;
import net.meterware.httpunit.HttpUnitOptions;
import net.meterware.httpunit.PseudoServlet;
import net.sourceforge.jwebunit.WebTestCase;
import net.sourceforge.jwebunit.util.reflect.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

import junit.framework.AssertionFailedError;

/**
 * Superclass for testing jWebUnit.  Uses test utilities from httpunit's test package.
 *
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class JWebUnitTest extends WebTestCase {

    private String hostPath;
    private PseudoServer server;


    public JWebUnitTest(String s) {
        super(s, "http://localhost:80", null);
    }

    public void setUp() throws Exception {
        server = new PseudoServer();
        HttpUnitOptions.reset();
        hostPath = "http://localhost:" + server.getConnectedPort();
        setBaseUrl(hostPath);
    }


    public void tearDown() throws Exception {
        if (server != null) server.shutDown();
    }


    protected void defineResource( String resourceName, String value ) {
        server.setResource( resourceName, value );
    }

    protected void defineWebPage(String pageName, String body) {
        defineResource(pageName + ".html", "<html><head><title>" + pageName + "</title></head>\n" +
                "<body>" + body + "</body></html>");
    }

    protected void defineResource( String resourceName, PseudoServlet servlet ) {
        server.setResource( resourceName, servlet );
    }

    public void assertPassFail(String methodName, Object passArg, Object failArgs) throws Throwable {
        assertPassFail(methodName, new Object[]{passArg}, new Object[]{failArgs});
    }

    public void assertPassFail(String methodName, Object[] passArgs, Object[] failArgs) throws Throwable {
        assertPass(methodName, passArgs);
        assertFail(methodName, failArgs);
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
            assertTrue(e.getTargetException().getClass().equals(exceptionClass));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
