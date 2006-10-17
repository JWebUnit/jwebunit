/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A input locator is a way to locate a &lt;input&gt; element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlInputLocator extends HtmlElementLocator {
    
    public HtmlInputLocator() {
        super("input");
    }

    public HtmlInputLocator(String id) {
        super("input", id);
    }

}
