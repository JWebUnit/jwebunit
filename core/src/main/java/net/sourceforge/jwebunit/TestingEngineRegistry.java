/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit;

import java.util.Hashtable;

import net.sourceforge.jwebunit.exception.TestingEngineRegistryException;

/**
 * This will maintain a registry of known testing engines to be used by jWebUnit.
 * @author Nicholas Neuberger
 * @author Julien Henry
 */
public class TestingEngineRegistry {
	
	//TODO Move this to a JDK1.5 typesafe enum	
	public final static String TESTING_ENGINE_HTTPUNIT = "TestingEngineHttpUnit"; 
    	public final static String TESTING_ENGINE_HTMLUNIT = "TestingEngineHtmlUnit";
	public final static String TESTING_ENGINE_JACOBIE = "TestingEngineJacobie"; 
	public final static String TESTING_ENGINE_SELENIUM = "TestingEngineSelenium"; 

	private static Hashtable testingEngineMap = null;
	
	public TestingEngineRegistry() {
	}

	/**
	 * Gets the map of testing engines defined within jwebunit.
	 * @return
	 */
	public Hashtable getTestingEngineMap() {
		if (testingEngineMap == null) {
			testingEngineMap = new Hashtable();

			testingEngineMap.put(TESTING_ENGINE_HTTPUNIT, "net.sourceforge.jwebunit.httpunit.HttpUnitDialog");
            		testingEngineMap.put(TESTING_ENGINE_HTMLUNIT, "net.sourceforge.jwebunit.htmlunit.HtmlUnitDialog");
			testingEngineMap.put(TESTING_ENGINE_JACOBIE, "net.sourceforge.jwebunit.jacobie.JacobieDialog");
			testingEngineMap.put(TESTING_ENGINE_SELENIUM, "net.sourceforge.jwebunit.selenium.SeleniumDialog");
		}
		return testingEngineMap;
	}

	/**
	 * Gets the class based on the key of the class.
	 * @param aKey
	 * @return
	 */
	public Class getTestingEngineClass(String aKey) throws ClassNotFoundException {
		Class theClass = Class.forName((String) getTestingEngineMap().get(aKey));
		if (theClass == null) {
			throw new TestingEngineRegistryException("Testing Engine with Key: [" + aKey + "] not defined for jWebUnit.");
		}
		return theClass;
	}
	
	
	
}
