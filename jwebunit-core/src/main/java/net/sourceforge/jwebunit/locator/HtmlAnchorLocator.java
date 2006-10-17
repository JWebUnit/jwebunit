/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * An anchor locator is a way to locate a &lt;a&gt; element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlAnchorLocator extends ClickableHtmlElementLocator {
    
    public HtmlAnchorLocator() {
        super("a");
    }

    public HtmlAnchorLocator(String id) {
        super("a", id);
    }

    public HtmlAnchorLocator(int index) {
        super("a");
        setIndex(index);
    }
}
