/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * An anchor text locator is a way to locate a &lt;a&gt; element in a page by its text.
 * 
 * @author Julien Henry
 */
public class HtmlAnchorLocatorByText extends HtmlAnchorLocator {
    
    public HtmlAnchorLocatorByText(String text) {
        super();
        setText(text);
    }

    public HtmlAnchorLocatorByText(String text, int index) {
        super(index);
        setText(text);
    }

}
