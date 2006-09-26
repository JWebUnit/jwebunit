/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A form locator is a way to locate a &lt;form&gt; element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlFormLocator extends HtmlElementLocator {
    
    public HtmlFormLocator() {
        super("form");
    }

    public HtmlFormLocator(String id) {
        super("form", id);
    }

}
