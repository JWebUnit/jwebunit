/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A submit input locator is a way to locate a &lt;input type="submit"&gt;
 * element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlSubmitInputLocator extends ClickableHtmlElementLocator {

    public HtmlSubmitInputLocator() {
        super("input");
        addAttribut("type", "submit");
    }


}
