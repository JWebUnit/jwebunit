/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * This is the interface for all HtmlSelectLocators.
 * A Html select locator is a way to locate one &lt;select&gt;
 * in an HTML page.
 * 
 * @author Julien Henry
 */
public class HtmlSelectLocator extends HtmlElementLocator {

    public HtmlSelectLocator() {
        super("select");
    }
}
