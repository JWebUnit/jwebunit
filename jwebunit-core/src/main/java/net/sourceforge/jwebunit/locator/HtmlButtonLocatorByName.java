/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * This is the class for locating html buttons by name.
 * A Html button corresponds to &lt;button&gt; element
 * in an HTML page.
 * 
 * @see HtmlSubmitInputByName
 * @see HtmlButtonInputByName
 * @see HtmlResetInputByName
 * 
 * @author Julien Henry
 */

public class HtmlButtonLocatorByName extends HtmlButtonLocator {

    public HtmlButtonLocatorByName(String name) {
        super();
        addAttribut("name", name);
    }

    public HtmlButtonLocatorByName(String name, int index) {
        super(index);
        addAttribut("name", name);
    }
}
