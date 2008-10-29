/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;


/**
 * Represents a problem establishing a form on the current response for which a request is to be built.
 * 
 * @author Wilkes Joiner
 */
public class UnableToSetFormException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnableToSetFormException() {
    }

    public UnableToSetFormException(String s) {
        super(s);
    }
}
