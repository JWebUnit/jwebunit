/*
 * User: djoiner
 * Date: Jul 19, 2002
 * Time: 2:53:19 PM
 */
package net.sourceforge.jwebunit.util.reflect;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;


public class MethodInvokerTest extends TestCase {
    private Receiver receiver;

    public MethodInvokerTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        receiver = new Receiver();
    }

    public void testNoArg() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "noArg");
        invoker.invoke();
        assertTrue(receiver.noArgCalled);
    }

    public void testOneArg() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "oneArg", "anArg");
        invoker.invoke();
        assertTrue(receiver.oneArgCalled);
    }

    public void testMultipleArgs() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "multiArg", new Object[]{"arg1", "arg2"});
        invoker.invoke();
        assertTrue(receiver.multiArgCalled);
    }

    public void testNoMethod() {
        try {
            MethodInvoker invoker = new MethodInvoker(receiver, "noMethod");
            invoker.invoke();
            fail();
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            fail();
        }
    }

    public void testMethodThrowsException() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "throwRuntime");
        try {
            invoker.invoke();
        } catch (InvocationTargetException e) {
            assertEquals(e.getTargetException().getClass(), RuntimeException.class);
        }
    }

    public void testPrimitiveArgs() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "primitiveArg", new Integer(1));
        invoker.invoke();
        assertTrue(receiver.primitiveArgCalled);
    }

    public void testAllPrimitives() throws Exception {
        Object[] args = new Object[]{new Boolean(true), new Byte((byte) 1),
                                     new Character('c'), new Double(1),
                                     new Float(1), new Integer(1),
                                     new Long(1), new Short((short) 1)};
        MethodInvoker invoker = new MethodInvoker(receiver, "allPrimitives", args);
        invoker.invoke();
        assertTrue(receiver.allPrimitivesCalled);
    }

    class Receiver {
        private boolean noArgCalled;
        private boolean oneArgCalled;
        private boolean multiArgCalled;
        private boolean primitiveArgCalled;
        private boolean allPrimitivesCalled;

        public void noArg() {
            noArgCalled = true;
        }

        public void oneArg(String arg) {
            oneArgCalled = true;
        }

        public void multiArg(String arg1, String arg2) {
            multiArgCalled = true;
        }

        public void primitiveArg(int i) {
            primitiveArgCalled = true;
        }

        public void allPrimitives(boolean bool, byte b,
                                  char c, double d,
                                  float f, int i,
                                  long l, short s) {
            allPrimitivesCalled = true;
        }

        public void throwRuntime() {
            throw new RuntimeException();
        }
    }
}
