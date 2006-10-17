/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;

/**
 * A textarea locator is a way to locate a &lt;textarea&gt; element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlTextAreaLocator extends TextFieldHtmlElementLocator {
    
    public HtmlTextAreaLocator() {
        super("textarea");
    }
    
    public void setTextField(IJWebUnitDialog dialog, String value) throws ElementNotFoundException {
        dialog.setTextArea(this, value);
    }

}
