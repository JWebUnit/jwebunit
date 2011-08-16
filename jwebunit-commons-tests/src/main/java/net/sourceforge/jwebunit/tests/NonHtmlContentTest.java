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

package net.sourceforge.jwebunit.tests;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertDownloadedFileEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;

import java.io.IOException;

import org.junit.Test;

/**
 * Test that non-html content is well handled.
 * 
 * @author Julien Henry
 */
public class NonHtmlContentTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/NonHtmlContentTest");
    }

    @Test public void testTextContent() {
        beginAt("/text.txt");
        assertTextPresent("Hello\r\nWorld");
    }

    @Test public void testBinaryContent() {
        beginAt("/text.bin");
        assertTextPresent("Hello\r\nWorld");
    }

    @Test public void testImageContent() throws IOException {
        beginAt("/image.png");
        assertDownloadedFileEquals(this.getClass().getResource("/testcases/NonHtmlContentTest/image.png"));
    }
}