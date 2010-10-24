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

package net.sourceforge.jwebunit.htmlunit;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.JWebUnitAPITestCase;
import net.sourceforge.jwebunit.tests.util.JettySetup;
import net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WaitingRefreshHandler;

/**
 * Unit test to validate JWEbUnit's HtmlUnit plugin will now allow for custom
 * RefreshHandler to be passed in prior to initialization, as well as changed on
 * the fly.
 * 
 * @Date 09/03/2010
 * @author Jason McSwain
 */
public class RefreshHandlerTest extends JWebUnitAPITestCase {
	public RefreshHandlerTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new JettySetup(new TestSuite(RefreshHandlerTest.class));
	}

	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDefaultRefreshHandler() throws Exception {
		if (this.getTestingEngine() instanceof HtmlUnitTestingEngineImpl) {
			setBaseUrl(HOST_PATH + "/RefreshHandlerTest");
			getTestContext().setResourceBundleName("RefreshHandlerTest");
			try {
				beginAt("/testPage.html");
				fail("expected exception b/c page refreshes, but received no exception");
			} catch (RuntimeException re) {
				String msg = re.getMessage();
				assertTrue(
						"msg was not as expected.\n[" + msg + "]",
						msg.endsWith("aborted by HtmlUnit: Attempted to refresh a page using an ImmediateRefreshHandler which could have caused an OutOfMemoryError Please use WaitingRefreshHandler or ThreadedRefreshHandler instead."));
			} catch (Exception e) {
				throw new Exception("received unexpected Exception.", e);
			}
		} else {
			System.out
					.println("[WARN] skipping test [testDefaultRefreshHandler] b/c it only applies to HtmlUnitTestEngineImpl");
		}
	}

	public void testAlternateRefreshHandler() throws Exception {
		if (this.getTestingEngine() instanceof HtmlUnitTestingEngineImpl) {
			HtmlUnitTestingEngineImpl engine = (HtmlUnitTestingEngineImpl) this
					.getTestingEngine();			
			setBaseUrl(HOST_PATH + "/RefreshHandlerTest");
			getTestContext().setResourceBundleName("RefreshHandlerTest");

			engine.setRefreshHandler(new ThreadedRefreshHandler());
			beginAt("/testPage.html");

			assertTrue("refresh handler was not of expected type.["
					+ engine.getWebClient().getRefreshHandler().getClass()
							.getName() + "]", engine.getWebClient()
					.getRefreshHandler() instanceof ThreadedRefreshHandler);

		} else {
			System.out
					.println("[WARN] skipping test [testAlternateRefreshHandler] b/c it only applies to HtmlUnitTestEngineImpl");
		}
	}

	public void testChangeRefreshHandler() throws Exception {
		if (this.getTestingEngine() instanceof HtmlUnitTestingEngineImpl) {
			HtmlUnitTestingEngineImpl engine = (HtmlUnitTestingEngineImpl) this
					.getTestingEngine();
			setBaseUrl(HOST_PATH + "/RefreshHandlerTest");
			getTestContext().setResourceBundleName("RefreshHandlerTest");

			engine.setRefreshHandler(new ThreadedRefreshHandler());
			beginAt("/testPage.html");

			assertTrue("refresh handler was not of expected type.["
					+ engine.getWebClient().getRefreshHandler().getClass()
							.getName() + "]", engine.getWebClient()
					.getRefreshHandler() instanceof ThreadedRefreshHandler);

			engine.setRefreshHandler(new WaitingRefreshHandler());
			assertTrue("refresh handler was not of expected type.["
					+ engine.getWebClient().getRefreshHandler().getClass()
							.getName() + "]", engine.getWebClient()
					.getRefreshHandler() instanceof WaitingRefreshHandler);

		} else {
			System.out
					.println("[WARN] skipping test [testChangeRefreshHandler] b/c it only applies to HtmlUnitTestEngineImpl");
		}
	}

}
