/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * This is the interface for all FrameLocators.
 * A frame locator is a way to locate a frame in a browser.
 * 
 * @author Julien Henry
 */
public class FrameLocatorByName implements FrameLocator {

    private String name;
    
    public FrameLocatorByName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
