/**
 * Copyright (c) 2010, JWebUnit team.
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

package net.sourceforge.jwebunit.tests.util;

import static org.junit.Assert.fail;

import java.net.URL;

import net.sourceforge.jwebunit.tests.JWebUnitAPITestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Sets up and tears down the Jetty servlet engine before and after the tests in
 * the <code>TestSuite</code> have run.
 * 
 * @author Eelco Hillenius
 */
public class JettySetup {
	/**
	 * The Jetty server we are going to use as test server.
	 */
	private static Server jettyServer = null;


	/**
	 * Starts the Jetty server using the <tt>jetty-test-config.xml</tt> file
	 * which is loaded using the classloader used to load Jetty.
	 * 
	 * @see junit.extensions.TestSetup#setUp()
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		try {
			jettyServer = new Server();
			SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(JWebUnitAPITestCase.JETTY_PORT);
            connector.setAcceptors(5);
			connector.setPort(JWebUnitAPITestCase.JETTY_PORT);
			jettyServer.setConnectors(new Connector[] { connector });

			WebAppContext wah = new WebAppContext();
			
			// Handle files encoded in UTF-8
			MimeTypes mimeTypes = new MimeTypes();
			mimeTypes.addMimeMapping("html_utf-8", "text/html; charset=UTF-8");
            mimeTypes.addMimeMapping("txt", "text/plain");
            mimeTypes.addMimeMapping("bin", "application/octet-stream");
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

			URL url = JettySetup.class.getResource("/testcases/");
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
	@AfterClass
	public static void tearDown() throws Exception {
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
