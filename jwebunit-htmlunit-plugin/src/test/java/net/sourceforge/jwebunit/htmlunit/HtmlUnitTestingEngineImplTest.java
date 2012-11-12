/**
 * Copyright (c) 2011, JWebUnit team.
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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import junit.framework.TestCase;
import net.sourceforge.jwebunit.api.ITestingEngine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HtmlUnitTestingEngineImplTest extends TestCase {
  public void testSetThrowExceptionOnScriptErrorShouldSetSameOnUnderlyingWebClient() throws Exception {
    WebClient client = mock(WebClient.class);
    WebClientOptions options = mock(WebClientOptions.class);
    when(client.getOptions()).thenReturn(options);
    ITestingEngine engine = new HtmlUnitTestingEngineImpl(client);

    engine.setThrowExceptionOnScriptError(true);

    verify(options).setThrowExceptionOnScriptError(true);
  }

}
