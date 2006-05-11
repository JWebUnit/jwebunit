/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.tests.userlist;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.JWebUnitAPITestCase;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test to show the results of userlist message, which was submitted as a
 * bugreport under number <a
 * href="http://sourceforge.net/support/tracker.php?aid=1049592">1049592 </a>.
 */
public class RajeshNairTest extends JWebUnitAPITestCase {

	/**
	 * Create the testsuite decorated with the Jetty setup.
	 * 
	 * @return the decorated testsuite.
	 */
	public static Test suite() {
		return new JettySetup(new TestSuite(RajeshNairTest.class));
	}

	/**
	 * This test gives a <code>NullPointerException</code> when the
	 * <code>submit()</code> function is called. This is actually a bug in
	 * HTTPUnit.
	 * 
	 * @todo TODO confirm this bug has been solved in a new release of HTTPUnit.
	 */
	public void testSubmitForm() {
		getTestContext().setBaseUrl(HOST_PATH + "/Userlist/RajeshNair");
		beginAt("report.htm");
		try {
			// submit(); // gives NullPointerException.
			clickButton("submit_View_Report"); // works.
		} catch (RuntimeException e) {
			if (e.getMessage().startsWith("java.lang.NullPointerException")) {
				fail("NullPointerException caught");
			}
			throw e;
		}
	}
}