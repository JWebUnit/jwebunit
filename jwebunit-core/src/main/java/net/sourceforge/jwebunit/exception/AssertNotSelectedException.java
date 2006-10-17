/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

import net.sourceforge.jwebunit.locator.Locator;

/**
 * This exception should be used when an element is not selected.
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
@SuppressWarnings("serial")
public class AssertNotSelectedException extends JWebUnitException {
    
    private Locator elementNotSelected;
   
    /**
     * @return Returns the elementNotSelected.
     */
    public Locator getElementNotSelected() {
        return elementNotSelected;
    }

    public AssertNotSelectedException(String msg) {
        super(msg);
    }

    public AssertNotSelectedException(String msg, Exception cause) {
        super(msg, cause);
    }
    
    public AssertNotSelectedException(Locator elementNotSelected) {
        super();
        this.elementNotSelected=elementNotSelected;        
    }

}
