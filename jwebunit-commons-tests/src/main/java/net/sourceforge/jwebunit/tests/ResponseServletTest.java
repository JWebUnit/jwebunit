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
package net.sourceforge.jwebunit.tests;

import org.junit.Test;

import java.net.SocketTimeoutException;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertHeaderEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertHeaderMatches;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertHeaderNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertHeaderPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertResponseCode;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertResponseCodeBetween;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static net.sourceforge.jwebunit.junit.JWebUnit.setIgnoreFailingStatusCodes;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTimeout;
import static net.sourceforge.jwebunit.junit.JWebUnit.submit;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test redirection support.
 *
 * @author Julien Henry
 */
public class ResponseServletTest extends JWebUnitAPITestCase {

  public void setUp() throws Exception {
    super.setUp();
    setIgnoreFailingStatusCodes(true); // ignore failing status codes
    setBaseUrl(HOST_PATH + "/ResponseServletTest");
  }

  /*
   * currently we can't get the response code from HtmlUnit unless it is a failing code
   */
  @Test
  public void testDefault() {
    beginAt("/SimpleForm.html");
    submit();
    assertResponseCodeBetween(200, 299);

    // test the headers
    assertHeaderPresent("Test");
    assertHeaderNotPresent("Not-present");
    assertHeaderEquals("Test", "test2");
    assertHeaderMatches("Header-Added", "[0-9]{2}");
  }

  @Test
  public void testResponse200() {
    beginAt("/SimpleForm.html");
    setTextField("status", "200");
    submit();
    assertResponseCode(200);
  }

  /*
   * HtmlUnit cannot handle a 301 without a valid Location: header
    @Test public void testResponse301() {
        beginAt("/SimpleForm.html");
        setTextField("status", "301");
        submit();
        assertResponseCode(301);
    }
   */

  @Test
  public void testResponse404() {
    beginAt("/SimpleForm.html");
    assertTitleEquals("response form");
    setTextField("status", "404");
    submit();
    assertResponseCode(404);
  }

  @Test
  public void testResponse501() {
    beginAt("/SimpleForm.html");
    assertTitleEquals("response form");
    setTextField("status", "501");
    submit();
    assertResponseCode(501);
  }

  /**
   * Issue 1674646: add support for specifying the timeout of pages
   */
  @Test
  public void testTimeout() {

    // test that timeout was fired
    setTimeout(500); // specify a global timeout of 0.5 seconds (must be set before the WebConnection is initialised)
    beginAt("/SimpleForm.html");
    assertTitleEquals("response form");
    setTextField("timeout", "1"); // server wait for 1 seconds
    try {
      submit();
      fail("timeout was not called"); // we should not get here
    } catch (RuntimeException e) {
      assertTrue("timeout caused by SocketTimeoutException, but was " + e.getCause().getClass(), e.getCause() instanceof SocketTimeoutException);
    }

    // close and reset the browser
    closeBrowser();

    // test that timeout wasn't fired
    setTimeout(2000); // specify a global timeout of 2 seconds (must be set before the WebConnection is initialised)
    beginAt("/SimpleForm.html");
    assertTitleEquals("response form");
    setTextField("timeout", "1"); // server wait for 1 seconds
    submit();
    assertTextPresent("hello, world!");
  }

}
