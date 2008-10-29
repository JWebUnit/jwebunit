/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

/**
 * Test that non-html content is well handled.
 * 
 * @author Julien Henry
 */
public class NonHtmlContentTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(NonHtmlContentTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/NonHtmlContentTest");
    }

    public void testTextContent() {
        beginAt("/text.txt");
        assertTextPresent("Hello\r\nWorld");
    }

    public void testBinaryContent() {
        beginAt("/text.bin");
        assertTextPresent("Hello\r\nWorld");
    }

    public void testImageContent() throws IOException {
        beginAt("/image.png");
        assertDownloadedFileEquals(this.getClass().getResource("/testcases/NonHtmlContentTest/image.png"));
    }
}