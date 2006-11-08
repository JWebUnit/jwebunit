/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit;

import java.util.Hashtable;

import net.sourceforge.jwebunit.exception.TestingEngineRegistryException;

/**
 * This will maintain a registry of known testing engines to be used by
 * jWebUnit.
 * 
 * @author Nicholas Neuberger
 * @author Julien Henry
 */
public class TestingEngineRegistry {

    // TODO Move this to a JDK1.5 typesafe enum
    public final static String TESTING_ENGINE_HTMLUNIT = "TestingEngineHtmlUnit";

    private static Hashtable testingEngineMap = null;

    public TestingEngineRegistry() {
    }

    /**
     * Gets the map of testing engines defined within jwebunit.
     * Need to be synchronized for concurrent testing.
     * @return
     */
    public static synchronized Hashtable getTestingEngineMap() {
        if (testingEngineMap == null) {
            testingEngineMap = new Hashtable();
            String cp = "net.sourceforge.jwebunit.htmlunit.HtmlUnitDialog";
            //Try to load HtmlUnitDialog to check if it is present.
            try {
                Class.forName(cp);
            } catch (ClassNotFoundException e) {
                // HtmlUnitDialog is not present in the classpath. Return an empty map.
                return testingEngineMap;
            }
            // HtmlUnitDialog was found. Add it to the map.
            testingEngineMap.put(TESTING_ENGINE_HTMLUNIT, cp);
        }
        return testingEngineMap;
    }

    /**
     * Gets the class based on the key of the class.
     * 
     * @param aKey
     * @return
     */
    public static Class getTestingEngineClass(String aKey)
            throws ClassNotFoundException {
        Class theClass = Class
                .forName((String) getTestingEngineMap().get(aKey));
        if (theClass == null) {
            throw new TestingEngineRegistryException(
                    "Testing Engine with Key: [" + aKey
                            + "] not defined for jWebUnit.");
        }
        return theClass;
    }

}
