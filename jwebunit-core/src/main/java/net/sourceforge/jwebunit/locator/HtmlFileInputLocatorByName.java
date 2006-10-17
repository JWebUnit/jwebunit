/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A file input locator is a way to locate a &lt;input type="file"&gt;
 * element in a page by its name attribut.
 * 
 * @author Julien Henry
 */
public class HtmlFileInputLocatorByName extends HtmlFileInputLocator {

    public HtmlFileInputLocatorByName(String name) {
        super();
        addAttribut("name", name);
    }

}
