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
package net.sourceforge.jwebunit.util;

import java.util.Hashtable;

import net.sourceforge.jwebunit.api.ITestingEngine;
import net.sourceforge.jwebunit.exception.TestingEngineRegistryException;

/**
 * This will maintain a registry of known testing engines to be used by JWebUnit.
 * 
 * @author Julien Henry
 */
public class TestingEngineRegistry {

    /**
     * Key of HtmlUnit testing engine.
     */
    public final static String TESTING_ENGINE_HTMLUNIT = "TestingEngineHtmlUnit";

    /**
     * Key of HtmlUnit testing engine.
     */
    public final static String TESTING_ENGINE_SELENIUM = "TestingEngineSelenium";

    private static Hashtable<String,Class<?>> testingEngineMap = new Hashtable<String,Class<?>>();

    static {
        String cp = "net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl";
        // Try to load HtmlUnit Testing Engine to check if it is present.
        try {
            addTestingEngine(TESTING_ENGINE_HTMLUNIT, cp);
        } catch (ClassNotFoundException e) {
            // HtmlUnit Testing Engine is not present in the classpath. Nothing to do.
        }
        cp = "net.sourceforge.jwebunit.selenium.SeleniumTestingEngineImpl";
        // Try to load Selenium Testing Engine to check if it is present.
        try {
            addTestingEngine(TESTING_ENGINE_SELENIUM, cp);
        } catch (ClassNotFoundException e) {
            // Selenium Testing Engine is not present in the classpath. Nothing to do.
        }
    }

    /**
     * Gets the class based on the key of the class.
     * 
     * @param aKey Key of the testing engine
     * @return the testing engine class.
     */
    public static Class<?> getTestingEngineClass(String aKey)
            throws ClassNotFoundException {
        Class<?> theClass = (Class<?>) testingEngineMap.get(aKey);
        return theClass;
    }

    /**
     * Add a new testing engine.
     * 
     * @param key A string to identify the testing engine.
     * @param classpath The full class name.
     * @throws ClassNotFoundException If the class is not in the classpath.
     */
    public static void addTestingEngine(String key, String classpath)
            throws ClassNotFoundException {
        Class<?> c = Class.forName(classpath);
        if (ITestingEngine.class.isAssignableFrom(c)) {
            testingEngineMap.put(key, c);
        } else {
            throw new TestingEngineRegistryException(classpath
                    + " is not an instance of IJWebUnitDialog");
        }
    }

    /**
     * Get first available testing engine key.
     * 
     * @return key of a testing engine, or null is none is available.
     */
    public static String getFirstAvailable() {
        if (testingEngineMap.isEmpty()) {
            return null;
        } else {
            return (String) testingEngineMap.keys().nextElement();
        }
    }

}
