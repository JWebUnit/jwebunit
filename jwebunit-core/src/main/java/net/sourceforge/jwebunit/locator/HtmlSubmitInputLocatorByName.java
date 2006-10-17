/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A submit input locator is a way to locate a &lt;input type="submit"&gt;
 * element in a page by its name attribut.
 * 
 * @author Julien Henry
 */
public class HtmlSubmitInputLocatorByName extends HtmlSubmitInputLocator {

    public HtmlSubmitInputLocatorByName(String name) {
        super();
        addAttribut("name", name);
    }

}
