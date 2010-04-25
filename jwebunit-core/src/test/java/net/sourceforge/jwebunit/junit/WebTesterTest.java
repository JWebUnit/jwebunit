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

package net.sourceforge.jwebunit.junit;

import junit.framework.TestCase;
import net.sourceforge.jwebunit.api.ITestingEngine;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebTesterTest extends TestCase {
    public void testAssertImageSrcContainsShouldDelegateXPathExpressionToUnderlyingTestingEngine() throws Exception {
        String xpath = "//img[" + contains("src", "SOME URL") + "]";

        ITestingEngine engine = engine(xpath, true);
        WebTester tester = webTester(engine);

        tester.assertImagePresentPartial("SOME URL", null);

        verify(engine).hasElementByXPath(xpath);
    }

    public void testAssertImageContainsShouldDelegateXPathExpressionToUnderlyingTestingEngine() throws Exception {
        String xpath = "//img[" + contains("src", "SOME URL") + " and " + contains("alt", "SOME ALT TEXT") + "]";

        ITestingEngine engine = engine(xpath, true);
        WebTester tester = webTester(engine);

        tester.assertImagePresentPartial("SOME URL", "SOME ALT TEXT");

        verify(engine).hasElementByXPath(xpath);
    }

    private String contains(String attribute, String substring) {
        return "contains(@" + attribute + ", \"" + substring + "\")";
    }

    private ITestingEngine engine(String xpath, boolean hasElement) {
        ITestingEngine mock = mock(ITestingEngine.class);
        when(mock.hasElementByXPath(xpath)).thenReturn(hasElement);
        return mock;
    }

    private WebTester webTester(ITestingEngine engine) {
        WebTester tester = new WebTester();
        tester.setDialog(engine);
        return tester;
    }
}
