/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.exception.ElementNotFoundException;

/**
 * A file input locator is a way to locate a &lt;input type="file"&gt;
 * element in a page.
 * 
 * @author Julien Henry
 */
public class HtmlFileInputLocator extends TextFieldHtmlElementLocator {

    public HtmlFileInputLocator() {
        super("input");
        addAttribut("type", "file");
    }

    public void setTextField(IJWebUnitDialog dialog, String value)
            throws ElementNotFoundException {
        dialog.setAttributeValue(this, "value", value);
    }

}
