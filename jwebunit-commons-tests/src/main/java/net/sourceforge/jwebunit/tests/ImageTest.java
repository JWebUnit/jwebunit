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

package net.sourceforge.jwebunit.tests;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertImagePresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertImageValid;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertImageValidAndStore;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static org.junit.Assert.assertNotNull;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 *
 * @author gjoseph
 */
public class ImageTest extends JWebUnitAPITestCase {

    public void setUp() throws Exception {
        super.setUp();
        setBaseUrl(HOST_PATH + "/ImageTest");
        beginAt("/PageWithImages.html");
    }

    @Test public void testSimpleImagePresenceAssertion() throws Throwable {
        assertImagePresent("images/Image1.gif", "image 1");
        assertImagePresent("images/Image2.png", "image 2");
        assertImagePresent("images/photos/Image3.jpg", "image 3");
        assertImagePresent("somedir/Image4.gif", null);
        assertImagePresent("images/InvalidImage.gif", "invalid image");

        assertFail("assertImagePresent", new Object[]{"images/Image4.jpg", "image 4"});
        assertFail("assertImagePresent", new Object[]{"images/wrongUrl.jpg", "image 3"});
        assertFail("assertImagePresent", new Object[]{"images/Image2.png", "wrong alt"});
    }

    @Test public void testGifCanBeLoaded() throws Throwable {
        assertPass("assertImageValid", new Object[]{"images/Image1.gif", "image 1"});
    }

    @Test public void testPngCanBeLoaded() throws Throwable {
        assertPass("assertImageValid", new Object[]{"images/Image2.png", "image 2"});
    }

    @Test public void testJpgCanBeLoaded() throws Throwable {
        assertPass("assertImageValid", new Object[]{"images/photos/Image3.jpg", "image 3"});
    }

    @Test public void testFailsOnInvalidImages() throws Throwable {
        assertFail("assertImageValid", new Object[]{"images/InvalidImage.gif", "invalid image"});
    }

    @Test public void testSavesImage() throws Throwable {
        File testOut = File.createTempFile("jwebunit-test-", ".png");
        testOut.deleteOnExit();
        assertImageValidAndStore("images/Image2.png", "image 2", testOut);
        BufferedImage testImg = ImageIO.read(testOut);
        // let's just assume it's ok if the image was loaded from the filesystem
        assertNotNull(testImg);
    }

    @Test public void testImagesAreExposed() throws Throwable {
        Image image = getImage("images/Image1.gif", "image 1");
        // let's just assume it's ok if the image is there
        assertNotNull(image);
    }

    @Test public void testRelativePathsAreCorrectlyResolved() {
        beginAt("/somedir/AnotherPageWithImages.html");
        assertImageValid("Image4.gif", "image 4 - same dir");
        assertImageValid("images/Image5.png", "image 5 - subdir");
        assertImageValid("../images/photos/Image3.jpg", "image 3 again - topdir");
    }
}
