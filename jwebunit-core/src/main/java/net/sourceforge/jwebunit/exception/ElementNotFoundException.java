/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * This exception should be used when an expected element is not found.
 * 
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
public class ElementNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ElementNotFoundException(String msg) {
        super(msg);
    }

    public ElementNotFoundException(String msg, Exception cause) {
        super(msg, cause);
    }

}
