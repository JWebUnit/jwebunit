/**
 * Copyright (c) 2002-2015, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * User: djoiner
 * Date: Jul 19, 2002
 * Time: 2:53:19 PM
 */
package net.sourceforge.jwebunit.tests.util.reflect;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.jwebunit.tests.util.reflect.MethodInvoker;


public class MethodInvokerTest {
    private Receiver receiver;

    @Before
    public void setUp() throws Exception {
        receiver = new Receiver();
    }

    @Test
    public void testNoArg() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "noArg");
        invoker.invoke();
        assertTrue(receiver.noArgCalled);
    }

    @Test
    public void testOneArg() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "oneArg", "anArg");
        invoker.invoke();
        assertTrue(receiver.oneArgCalled);
    }

    @Test
    public void testMultipleArgs() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "multiArg", new Object[]{"arg1", "arg2"});
        invoker.invoke();
        assertTrue(receiver.multiArgCalled);
    }

    @Test(expected=NoSuchMethodException.class)
    public void testNoMethod() throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MethodInvoker invoker = new MethodInvoker(receiver, "noMethod");
        invoker.invoke();
    }

    @Test
    public void testMethodThrowsException() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "throwRuntime");
        try {
            invoker.invoke();
        } catch (InvocationTargetException e) {
            assertEquals(e.getTargetException().getClass(), RuntimeException.class);
        }
    }

    @Test
    public void testPrimitiveArgs() throws Exception {
        MethodInvoker invoker = new MethodInvoker(receiver, "primitiveArg", new Integer(1));
        invoker.invoke();
        assertTrue(receiver.primitiveArgCalled);
    }

    @Test
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
