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

import junit.framework.TestCase;
import com.gargoylesoftware.htmlunit.WebClient;
import net.sourceforge.jwebunit.api.ITestingEngine;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HtmlUnitTestingEngineImplTest extends TestCase {
    public void testSetThrowExceptionOnScriptErrorShouldSetSameOnUnderlyingWebClient() throws Exception {
        WebClient client = mock(WebClient.class);
        ITestingEngine engine = new HtmlUnitTestingEngineImpl(client);

        engine.setThrowExceptionOnScriptError(true);

        verify(client).setThrowExceptionOnScriptError(true);
    }

}
