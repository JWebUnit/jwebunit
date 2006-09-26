/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A Html table locator is a way to locate one &lt;table&gt;
 * in an HTML page.
 * 
 * @author Julien Henry
 */
public class HtmlTableLocatorBySummary extends HtmlTableLocator {

    public HtmlTableLocatorBySummary(String summary) {
        addAttribut("summary", summary);
    }

}
