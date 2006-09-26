/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.html;

import net.sourceforge.jwebunit.locator.HtmlOptionLocator;

/**
 * Represents one option of a select.
 * 
 * @author Julien Henry
 */
public class SelectOption {

    private String value;
    private String label;
    private HtmlOptionLocator locator;
    
    public SelectOption(HtmlOptionLocator locator, String value, String label) {
        this.locator=locator;
        this.value=value;
        this.label=label;
    }
    
    public String getLabel() {
        return label;
    }
    public String getValue() {
        return value;
    }
    public HtmlOptionLocator getLocator() {
        return locator;
    }
}
