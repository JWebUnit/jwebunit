/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;


/**
 * A Javascript confirm was expected but was not thrown.
 * 
 * @author Julien Henry
 */
public class ExpectedJavascriptConfirmException extends Exception {

	private static final long serialVersionUID = 1L;

    /**
     * Expected message in the confirm
     */
    private String message;

    public ExpectedJavascriptConfirmException(String message) {
        super("A confirm was expected with message [" + message + "]");
        this.message = message;
    }

    /**
     * Return the expected message in the confirm
     */
    public String getConfirmMessage() {
        return message;
    }
}
