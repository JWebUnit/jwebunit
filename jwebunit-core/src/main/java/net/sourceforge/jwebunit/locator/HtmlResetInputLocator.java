/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A reset input locator is a way to locate a &lt;input type="reset"&gt;
 * element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlResetInputLocator extends ClickableHtmlElementLocator {

    public HtmlResetInputLocator() {
        super("input");
        addAttribut("type", "reset");
    }


}
