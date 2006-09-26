/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * This is the class for locating html buttons.
 * A Html button corresponds to &lt;button&gt; element
 * in an HTML page.
 * 
 * @see HtmlSubmitInput
 * @see HtmlButtonInput
 * @see HtmlResetInput
 * 
 * @author Julien Henry
 */

public class HtmlButtonLocator extends ClickableHtmlElementLocator {

    public HtmlButtonLocator() {
        super("button");
    }

    public HtmlButtonLocator(String id) {
        super("button", id);
    }

    public HtmlButtonLocator(int index) {
        super("button");
        setIndex(index);
    }
}
