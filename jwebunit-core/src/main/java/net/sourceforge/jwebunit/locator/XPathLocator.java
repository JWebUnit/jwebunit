/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

/**
 * Locate element by XPath.
 * 
 * @author Julien Henry
 */
public class XPathLocator implements HtmlElementLocator {
    
    private String xpath;
    
    public XPathLocator(String xpath) {
        this.xpath=xpath;
    }
    
    public String getXPath() {
        return xpath;
    }

}
