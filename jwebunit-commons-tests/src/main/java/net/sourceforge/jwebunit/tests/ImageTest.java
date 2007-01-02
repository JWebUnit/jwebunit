/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.tests.util.JettySetup;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author gjoseph
 */
public class ImageTest extends JWebUnitAPITestCase {

    public static Test suite() {
        Test suite = new TestSuite(ImageTest.class);
        return new JettySetup(suite);
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(HOST_PATH + "/ImageTest");
        beginAt("/PageWithImages.html");
    }

    public void testSimpleImagePresenceAssertion() throws Throwable {
        assertImagePresent("images/Image1.gif", "image 1");
        assertImagePresent("images/Image2.png", "image 2");
        assertImagePresent("images/photos/Image3.jpg", "image 3");
        assertImagePresent("somedir/Image4.gif", null);
        assertImagePresent("images/InvalidImage.gif", "invalid image");

        assertFail("assertImagePresent", new Object[]{"images/Image4.jpg", "image 4"});
        assertFail("assertImagePresent", new Object[]{"images/wrongUrl.jpg", "image 3"});
        assertFail("assertImagePresent", new Object[]{"images/Image2.png", "wrong alt"});
    }

    public void testGifCanBeLoaded() throws Throwable {
        assertPass("assertImageValid", new Object[]{"images/Image1.gif", "image 1"});
    }

    public void testPngCanBeLoaded() throws Throwable {
        assertPass("assertImageValid", new Object[]{"images/Image2.png", "image 2"});
    }

    public void testJpgCanBeLoaded() throws Throwable {
        assertPass("assertImageValid", new Object[]{"images/photos/Image3.jpg", "image 3"});
    }

    public void testFailsOnInvalidImages() throws Throwable {
        assertFail("assertImageValid", new Object[]{"images/InvalidImage.gif", "invalid image"});
    }

    public void testSavesImage() throws Throwable {
        File testOut = File.createTempFile("jwebunit-test-", ".png");
        testOut.deleteOnExit();
        assertImageValidAndStore("images/Image2.png", "image 2", testOut);
        BufferedImage testImg = ImageIO.read(testOut);
        // let's just assume it's ok if the image was loaded from the filesystem
        assertNotNull(testImg);
    }

    public void testImagesAreExposed() throws Throwable {
        Image image = getImage("images/Image1.gif", "image 1");
        // let's just assume it's ok if the image is there
        assertNotNull(image);
    }

    public void testRelativePathsAreCorrectlyResolved() {
        beginAt("/somedir/AnotherPageWithImages.html");
        assertImageValid("Image4.gif", "image 4 - same dir");
        assertImageValid("images/Image5.png", "image 5 - subdir");
        assertImageValid("../images/photos/Image3.jpg", "image 3 again - topdir");
    }
}
