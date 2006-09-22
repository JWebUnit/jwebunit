/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.html;

import net.sourceforge.jwebunit.locator.HtmlSelectLocator;

/**
 * Represents an option of a select.
 * 
 * @author Julien Henry
 */
public class SelectOption {

    private String value;
    private String text;
    private HtmlSelectLocator locator;
    
    public SelectOption(HtmlSelectLocator locator, String value, String text) {
        this.locator=locator;
        this.value=value;
        this.text=text;
    }
    
    public String getText() {
        return text;
    }
    public String getValue() {
        return value;
    }
    public HtmlSelectLocator getLocator() {
        return locator;
    }
}
