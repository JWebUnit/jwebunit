/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

import net.sourceforge.jwebunit.locator.Locator;

/**
 * This exception should be used when an unexpected element is found.
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
@SuppressWarnings("serial")
public class ElementFoundException extends JWebUnitException {
    
    private Locator elementFound;
   
    /**
     * @return Returns the elementNotFound.
     */
    public Locator getElementFound() {
        return elementFound;
    }

    public ElementFoundException(String msg) {
        super(msg);
    }

    public ElementFoundException(String msg, Exception cause) {
        super(msg, cause);
    }
    
    public ElementFoundException(Locator elementFound) {
        super();
        this.elementFound=elementFound;        
    }

}
