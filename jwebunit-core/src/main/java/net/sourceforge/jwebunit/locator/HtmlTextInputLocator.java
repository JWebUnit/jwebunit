/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;

/**
 * A input locator is a way to locate a &lt;input&gt; element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlTextInputLocator extends TextFieldHtmlElementLocator {
    
    public HtmlTextInputLocator() {
        super("input");
        addAttribut("type", "text");
    }
    
    public void setTextField(IJWebUnitDialog dialog, String value) throws ElementNotFoundException {
        dialog.setAttributeValue(this, "value", value);
    }


}
