/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A reset input locator is a way to locate a &lt;input type="reset"&gt;
 * element in a page by its name attribut.
 * 
 * @author Julien Henry
 */
public class HtmlResetInputLocatorByName extends HtmlResetInputLocator {

    public HtmlResetInputLocatorByName(String name) {
        super();
        addAttribut("name", name);
    }

}
