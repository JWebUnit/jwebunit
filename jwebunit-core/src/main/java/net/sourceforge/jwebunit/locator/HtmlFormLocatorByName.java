/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A form locator is a way to locate a &lt;form&gt; element in a page.
 * Use this locator when you want to locate a form by name.
 * 
 * @author Julien Henry
 */
public class HtmlFormLocatorByName extends HtmlFormLocator {
    
    /**
     * Locate a form by its name.
     * @param name
     */
    public HtmlFormLocatorByName(String name) {
        addAttribut("name", name);
    }
    
    /**
     * Locate a form by its name. If there is more than one form with
     * the same name, specify the 0-based index.
     * @param name
     * @param index
     */
    public HtmlFormLocatorByName(String name, int index) {
        addAttribut("name", name);
        setIndex(index);
    }


}
