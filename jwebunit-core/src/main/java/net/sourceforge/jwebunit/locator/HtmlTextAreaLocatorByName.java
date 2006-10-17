/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * A textarea locator is a way to locate a &lt;textarea&gt; element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlTextAreaLocatorByName extends HtmlTextAreaLocator {
    
    public HtmlTextAreaLocatorByName(String name) {
        super();
        addAttribut("name", name);
    }

}
