package net.sourceforge.jwebunit.tests;

import java.lang.reflect.InvocationTargetException;

import junit.framework.AssertionFailedError;
import net.sourceforge.jwebunit.WebTestCase;
import net.sourceforge.jwebunit.tests.util.reflect.MethodInvoker;


/**
 * This class is intended be used by all "testcase" classes that are
 * used to test the functionality of the jwebunit core api.  This isn't
 * to be extended by end users of the jwebunit api.
 * 
 * @author Nicholas Neuberger
 */
public abstract class JWebUnitAPITestCase extends WebTestCase {

    protected static final Object[] NOARGS = new Object[0];
    
    public static final int JETTY_PORT = 8082;
    
    public static final String JETTY_URL = "/jwebunit";

	public static final String HOST_PATH = "http://localhost:"+JETTY_PORT+JETTY_URL;
	
    private static String dialogKey = null;
	/**
	 * @param name
	 */
	public JWebUnitAPITestCase(String name) {
		super(name);
	}

	/**
	 * 
	 */
	public JWebUnitAPITestCase() {
		super();
	}
	
	public void setUp() throws Exception {
		super.setUp();
		getTestContext().setBaseUrl(HOST_PATH);
        setTestingEngineKey(getDialogKey());
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

    /**
     * @return Returns the testingEngineKey.
     */
    public static String getDialogKey() {
        return dialogKey;
    }

    /**
     * @param testingEngineKey The testingEngineKey to set.
     */
    public static void setDialogKey(String testingEngineKey) {
        JWebUnitAPITestCase.dialogKey = testingEngineKey;
    }
	
	

}
