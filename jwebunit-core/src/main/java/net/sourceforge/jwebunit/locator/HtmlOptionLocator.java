/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * This is the interface for all HtmlOptionLocators.
 * A Html select locator is a way to locate one &lt;option&gt;
 * in an HTML page.
 * 
 * @author Julien Henry
 */
public class HtmlOptionLocator extends ClickableHtmlElementLocator {
    
    private HtmlSelectLocator parentSelect;
    
    public HtmlOptionLocator() {
        super("option");
    }
   
    public HtmlOptionLocator(HtmlSelectLocator parentSelect) {
        super("option");
        this.parentSelect=parentSelect;
    }

    public HtmlOptionLocator(HtmlSelectLocator parentSelect, int index) {
        super("option");
        this.parentSelect=parentSelect;
        setIndex(index);
    }
}
