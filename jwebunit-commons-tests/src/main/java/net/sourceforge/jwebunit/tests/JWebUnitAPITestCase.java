/**
 * Copyright (c) 2011, JWebUnit team.
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
package net.sourceforge.jwebunit.tests;

import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;

import net.sourceforge.jwebunit.junit.JWebUnit;
import net.sourceforge.jwebunit.tests.util.reflect.StaticMethodInvoker;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;

/**
 * This class is intended be used by all "testcase" classes that are used to test the functionality of the jwebunit core
 * api. This isn't to be extended by end users of the jwebunit api.
 * 
 * @author Nicholas Neuberger
 */
public abstract class JWebUnitAPITestCase {

    protected static final Object[] NOARGS = new Object[0];

    public static final int JETTY_PORT = 8082;

    public static final String JETTY_URL = "/jwebunit";

    public static final String HOST_PATH = "http://localhost:" + JETTY_PORT
            + JETTY_URL;

    @Before
    public void setUp() throws Exception {
        getTestContext().setBaseUrl(HOST_PATH);
        getTestContext().setAuthorization("admin", "admin");
    }
    
    @After
    public void closeBrowser() {
        JWebUnit.closeBrowser();
    }

    public void assertPassFail(String methodName, Object passArg,
            Object failArgs) throws Throwable {
        assertPassFail(methodName, new Object[] { passArg },
                new Object[] { failArgs });
    }

    public void assertPassFail(String methodName, Object[] passArgs,
            Object[] failArgs) throws Throwable {
        assertPass(methodName, passArgs);
        assertFail(methodName, failArgs);
    }

    protected void assertPass(String methodName, Object arg) throws Throwable {
        this.assertPass(methodName, new Object[] { arg });
    }

    protected void assertPass(String methodName, Object[] args)
            throws Throwable {
        StaticMethodInvoker invoker = new StaticMethodInvoker(JWebUnit.class, methodName, args);
        try {
            invoker.invoke();
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public void assertFail(String methodName, Object arg) {
        assertFail(methodName, new Object[] { arg });
    }

    public void assertFail(String methodName, Object[] args) {
        assertException(AssertionError.class, methodName, args);
    }

    public <G extends Throwable> G assertException(Class<G> exceptionClass, String methodName,
            Object[] args) {
        StaticMethodInvoker invoker = new StaticMethodInvoker(JWebUnit.class, methodName, args);
        try {
            invoker.invoke();
            fail("Expected test failure did not occur for method: "
                    + methodName);
            return null; //never called
        } catch (InvocationTargetException e) {
            assertTrue("Expected " + exceptionClass.getName() + "but was "
                    + e.getTargetException().getClass().getName(),
                    exceptionClass.isInstance(e.getTargetException()));
            return (G) e.getTargetException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
