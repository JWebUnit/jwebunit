/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sourceforge.jwebunit.exception.TestingEngineRegistryException;

/**
 * This will maintain a registry of known testing engines to be used by
 * jWebUnit. TestingEngineRegistry try to load official plugins from classpath.
 * You can also add you're own.
 * 
 * @author Nicholas Neuberger
 * @author Julien Henry
 */
public class TestingEngineRegistry {

    private static final Log LOGGER = LogFactory
            .getLog(TestingEngineRegistry.class);

    /**
     * String that identify HtmlUnit plugin.
     */
    public final static String TESTING_ENGINE_HTMLUNIT = "TestingEngineHtmlUnit";

    /**
     * String that identify Selenium plugin.
     */
    public final static String TESTING_ENGINE_SELENIUM = "TestingEngineSelenium";

    private static Hashtable<String, Class<? extends IJWebUnitDialog>> testingEngineMap = new Hashtable<String, Class<? extends IJWebUnitDialog>>();

    static {
        try {
            addTestingEngine(TESTING_ENGINE_HTMLUNIT,
                    "net.sourceforge.jwebunit.htmlunit.HtmlUnitDialog");
        } catch (Exception e) {
            LOGGER
                    .warn("HtmlUnitDialog can't be loaded. Check your classpath.");
        }
        try {
            addTestingEngine(TESTING_ENGINE_SELENIUM,
                    "net.sourceforge.jwebunit.selenium.SeleniumDialog");
        } catch (Exception e) {
            LOGGER
                    .warn("SeleniumDialog can't be loaded. Check your classpath.");
        }
    }

    /**
     * Add a testing engine class to the registry.
     * 
     * @param name
     *            Identifier of the plugin
     * @param dialogClass
     *            The class of the plugin
     */
    public static void addTestingEngine(String name,
            Class<? extends IJWebUnitDialog> dialogClass) {
        testingEngineMap.put(name, dialogClass);
    }

    /**
     * Add a testing engine to the registry by loading it from classpath.
     * 
     * @param name
     *            Identifier of the plugin
     * @param classPath
     *            Fully qualified name of the testing engine's class
     * @throws ClassNotFoundException
     *             If the class was not found
     * @throws TestingEngineRegistryException
     */
    public static void addTestingEngine(String name, String classPath)
            throws ClassNotFoundException, TestingEngineRegistryException {
        // Class c = Class.forName(classPath, true, ClassLoader
        // .getSystemClassLoader()); // DON'T WORK WITH MAVEN
        Class c = Class.forName(classPath);
        Object d = null;
        try {
            d = c.newInstance();
        } catch (Exception e) {
            throw new TestingEngineRegistryException(
                    "Unable to create a new instance of " + c.getName(), e);
        }
        IJWebUnitDialog dial = null;
        try {
            dial = (IJWebUnitDialog) d;
        } catch (ClassCastException e) {
            throw new TestingEngineRegistryException(c.getName()
                    + " doesn't implement IJWebUnitDialog", e);
        }

        addTestingEngine(name, dial.getClass());
    }

    public TestingEngineRegistry() {
    }

    /**
     * Gets the class based on the name of the testing engine.
     * 
     * @param name
     *            Name of the testing engine
     * @return A testing engine.
     */
    public static Class<? extends IJWebUnitDialog> getTestingEngineClass(
            String name) throws TestingEngineRegistryException {
        if (!testingEngineMap.containsKey(name)) {
            throw new TestingEngineRegistryException(
                    "Testing Engine with Key: [" + name
                            + "] not defined for jWebUnit.");
        }
        return testingEngineMap.get(name);
    }

    public static boolean isEmpty() {
        return testingEngineMap.isEmpty();
    }

    public static String getFirstTestingEngineKey() {
        return testingEngineMap.keys().nextElement();
    }

}
