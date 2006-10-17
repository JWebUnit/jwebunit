/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;

/**
 * This is the type for all text field html elements.
 * 
 * @author Julien Henry
 */
public abstract class TextFieldHtmlElementLocator extends HtmlElementLocator {
    
    public TextFieldHtmlElementLocator(String tag) {
        super(tag);
    }
    
    public TextFieldHtmlElementLocator(String tag, String id) {
        super(tag, id);
    }
    
    public abstract void setTextField(IJWebUnitDialog dialog, String value) throws ElementNotFoundException;
    
}
