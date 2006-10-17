/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A password input locator is a way to locate a &lt;input type="password"&gt;
 * element in a page by its name attribut.
 * 
 * @author Julien Henry
 */
public class HtmlPasswordInputLocatorByName extends HtmlPasswordInputLocator {

    public HtmlPasswordInputLocatorByName(String name) {
        super();
        addAttribut("name", name);
    }

}
