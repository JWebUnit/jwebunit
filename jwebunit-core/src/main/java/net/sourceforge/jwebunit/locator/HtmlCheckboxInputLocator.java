/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A checkbox input locator is a way to locate a &lt;input type="checkbox"&gt;
 * element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlCheckboxInputLocator extends ClickableHtmlElementLocator {

    public HtmlCheckboxInputLocator() {
        super("input");
        addAttribut("type", "checkbox");
    }


}
