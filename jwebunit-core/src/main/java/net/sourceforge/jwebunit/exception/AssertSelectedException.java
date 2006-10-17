/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

import net.sourceforge.jwebunit.locator.Locator;

/**
 * This exception should be used when an element is selected.
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
@SuppressWarnings("serial")
public class AssertSelectedException extends JWebUnitException {
    
    private Locator elementSelected;
   
    /**
     * @return Returns the elementSelected.
     */
    public Locator getElementSelected() {
        return elementSelected;
    }

    public AssertSelectedException(String msg) {
        super(msg);
    }

    public AssertSelectedException(String msg, Exception cause) {
        super(msg, cause);
    }
    
    public AssertSelectedException(Locator elementSelected) {
        super();
        this.elementSelected=elementSelected;        
    }

}
