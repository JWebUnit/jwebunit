/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * This is the type for all clickable html elements.
 * 
 * @author Julien Henry
 */
public abstract class ClickableHtmlElementLocator extends HtmlElementLocator {
    
    public ClickableHtmlElementLocator(String tag) {
        super(tag);
    }
    
    public ClickableHtmlElementLocator(String tag, String id) {
        super(tag, id);
    }

}
