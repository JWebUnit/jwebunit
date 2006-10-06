/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * This is the interface for all Windowlocators.
 * A window locator is a way to locate a window in a browser.
 * 
 * @author Julien Henry
 */
public class WindowLocatorByName implements WindowLocator {
    
    private String name;
    
    public WindowLocatorByName(String name) {
        this.name=name;
    }
    
    public String getName() {
        return this.name;
    }

}
