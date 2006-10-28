/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests.util;

import java.net.URL;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.security.BasicAuthenticator;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.Password;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.xml.XmlConfiguration;

import junit.extensions.TestSetup;
import junit.framework.Test;

import net.sourceforge.jwebunit.TestingEngineRegistry;
import net.sourceforge.jwebunit.tests.JWebUnitAPITestCase;

/**
 * Sets up and tears down the Jetty servlet engine before and after the tests in
 * the <code>TestSuite</code> have run.
 * 
 * @author Eelco Hillenius
 */
public class JettySetup extends TestSetup {
	/**
	 * The Jetty server we are going to use as test server.
	 */
	private Server jettyServer = null;

	/**
	 * Constructor.
	 * 
	 * @param test
	 */
	public JettySetup(Test test) {
		super(test);
	}

	/**
	 * Starts the Jetty server using the <tt>jetty-test-config.xml</tt> file
	 * which is loaded using the classloader used to load Jetty.
	 * 
	 * @see junit.extensions.TestSetup#setUp()
	 */
	public void setUp() {
		try {
			jettyServer = new Server();
			SocketConnector connector = new SocketConnector();
			connector.setPort(JWebUnitAPITestCase.JETTY_PORT);
			jettyServer.setConnectors(new Connector[] { connector });

			WebAppContext wah = new WebAppContext();
			
			// Handle files encoded in UTF-8
			MimeTypes mimeTypes = new MimeTypes();
			mimeTypes.addMimeMapping("html_utf-8", "text/html; charset=UTF-8");
			wah.setMimeTypes(mimeTypes);
			
			
			HandlerCollection handlers= new HandlerCollection();
			handlers.setHandlers(new Handler[]{wah, new DefaultHandler()});

			jettyServer.setHandler(wah);
			HashUserRealm myrealm = new HashUserRealm("MyRealm");
			myrealm.put("jetty", "jetty");
			myrealm.addUserToRole("jetty", "user");
			myrealm.put("admin", "admin");
			myrealm.addUserToRole("admin", "admin");
			jettyServer.setUserRealms(new UserRealm[]{myrealm});
			
			wah.setContextPath(JWebUnitAPITestCase.JETTY_URL);

			URL url = this.getClass().getResource("/testcases/");
			wah.setWar(url.toString());
			
			jettyServer.start();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Could not start the Jetty server: " + e);
		}
	}

	/**
	 * Stops the Jetty server.
	 * 
	 * @see junit.extensions.TestSetup#tearDown()
	 */
	public void tearDown() {
		try {
			jettyServer.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Jetty server was interrupted: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Could not stop the Jetty server: " + e);
		}
	}
}