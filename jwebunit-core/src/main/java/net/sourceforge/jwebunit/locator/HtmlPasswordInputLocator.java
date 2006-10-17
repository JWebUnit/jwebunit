/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;

/**
 * A password input locator is a way to locate a &lt;input type="password"&gt;
 * element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlPasswordInputLocator extends TextFieldHtmlElementLocator {

    public HtmlPasswordInputLocator() {
        super("input");
        addAttribut("type", "password");
    }

    public void setTextField(IJWebUnitDialog dialog, String value)
            throws ElementNotFoundException {
        dialog.setAttributeValue(this, "value", value);
    }

}
