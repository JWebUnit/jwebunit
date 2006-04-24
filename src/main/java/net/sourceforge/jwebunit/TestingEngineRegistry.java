/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit;

import java.util.Hashtable;

import net.sourceforge.jwebunit.exception.TestingEngineRegistryException;
import net.sourceforge.jwebunit.plugins.htmlunit.HtmlUnitDialog;
import net.sourceforge.jwebunit.plugins.httpunit.HttpUnitDialog;
import net.sourceforge.jwebunit.plugins.jacobie.JacobieDialog;

/**
 * This will maintain a registry of known testing engines to be used by jWebUnit.
 * @author Nicholas Neuberger
 */
public class TestingEngineRegistry {
	
	public final static String TESTING_ENGINE_HTTPUNIT = "TestingEngineHttpUnit"; 
    public final static String TESTING_ENGINE_HTMLUNIT = "TestingEngineHtmlUnit";
	public final static String TESTING_ENGINE_JACOBIE = "TestingEngineJacobie"; 

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

			testingEngineMap.put(TESTING_ENGINE_HTTPUNIT, HttpUnitDialog.class);
            testingEngineMap.put(TESTING_ENGINE_HTMLUNIT, HtmlUnitDialog.class);
			testingEngineMap.put(TESTING_ENGINE_JACOBIE, JacobieDialog.class);
		}
		return testingEngineMap;
	}

	/**
	 * Gets the class based on the key of the class.
	 * @param aKey
	 * @return
	 */
	public Class getTestingEngineClass(String aKey) {
		Class theClass = (Class) getTestingEngineMap().get(aKey);
		if (theClass == null) {
			throw new TestingEngineRegistryException("Testing Engine with Key: [" + aKey + "] not defined for jWebUnit.");
		}
		return theClass;
	}
	
	
	
}
