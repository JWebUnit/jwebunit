/*
 * Created on Aug 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.jwebunit.userlist;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.WebTestCase;
import net.sourceforge.jwebunit.util.JettySetup;

/**
 * @author Martijn
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RajeshNairTest extends WebTestCase {

    public static Test suite() {
        return new JettySetup(new TestSuite(RajeshNairTest.class));
    }

    public void testSubmitForm() {
        getTestContext().setBaseUrl(
                "http://localhost:8081/jwebunit/Userlist/RajeshNair");
        beginAt("report.htm");
        try {
            submit(); // gives NullPointerException.
            //clickButton("submit_View_Report"); // works.
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("java.lang.NullPointerException")) {
                fail("NullPointerException caught");
            }
            throw e;
        }
    }
}