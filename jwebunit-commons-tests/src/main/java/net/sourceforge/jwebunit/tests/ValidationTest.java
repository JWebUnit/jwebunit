/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test the assertions provided by WebTestCase using the PseudoServer test
 * package provided by Russell Gold in httpunit.
 * 
 * @author Wilkes Joiner
 * @author Jim Weaver
 */
public class ValidationTest extends JWebUnitAPITestCase {

	public static Test suite() {
		Test suite = new TestSuite(ValidationTest.class);
		return new JettySetup(suite);
	}

    public void testValidHtml() throws Throwable {
    	beginAt("/testValidHtml.html");
    	assertValidHTML();
    }

}
