/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;


/**
 * A Javascript confirm was displayed but not expected.
 *
 * @author Julien Henry
 */
public class UnexpectedJavascriptConfirmException extends RuntimeException {
	
	/**
	 * Message in the unexpected confirm
	 */
	private String message;
	
    public UnexpectedJavascriptConfirmException(String message) {
    	super("A unexpected confirm with message ["+message+"] was displayed");
        this.message=message;
    }
    
    /**
	 * Return the message of the expected confirm
	 */
	public String getConfirmMessage() {
		return message;
	}
}
