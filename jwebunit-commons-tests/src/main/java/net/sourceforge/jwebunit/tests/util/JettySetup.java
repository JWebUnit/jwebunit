/**
 * Copyright (c) 2002-2014, JWebUnit team.
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

import java.net.URL;
import net.sourceforge.jwebunit.tests.JWebUnitAPITestCase;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.BeforeClass;

import static org.junit.Assert.fail;

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
	
	private static boolean started = false;


	/**
	 * Starts the Jetty server using the <tt>jetty-test-config.xml</tt> file
	 * which is loaded using the classloader used to load Jetty.
	 * 
	 * @see junit.extensions.TestSetup#setUp()
	 */
	@BeforeClass
	public static void startup() throws Exception {
	    if (!started) {
    		try {
    			jettyServer = new Server();
    			ServerConnector connector = new ServerConnector(jettyServer);
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
    			HashLoginService myrealm = new HashLoginService("MyRealm");
    			URL config = JettySetup.class.getResource("/jetty-users.properties");
    			myrealm.setConfig(config.toString());
    			jettyServer.addBean(myrealm);
    			
    			wah.setContextPath(JWebUnitAPITestCase.JETTY_URL);
    
    			URL url = JettySetup.class.getResource("/testcases/");
    			wah.setWar(url.toString());
    			
    			jettyServer.start();
    			
    			started = true;
    
    		} catch (Exception e) {
    			e.printStackTrace();
    			fail("Could not start the Jetty server: " + e);
    		}
	    }
	}
	
	protected static void shutdown() throws Exception {
        try {
            jettyServer.stop();
            started = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Jetty server was interrupted: " + e);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not stop the Jetty server: " + e);
        }
    }
}
