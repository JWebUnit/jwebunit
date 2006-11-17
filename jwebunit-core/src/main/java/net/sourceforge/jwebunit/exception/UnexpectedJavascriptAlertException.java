/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * A Javascript alert was displayed but not expected.
 * 
 * @author Julien Henry
 */
public class UnexpectedJavascriptAlertException extends RuntimeException {

    /**
     * Message in the unexpected alert
     */
    private String message;

    public UnexpectedJavascriptAlertException(String message) {
        super("An unexpected alert with message [" + message
                + "] was displayed");
        this.message = message;
    }

    /**
     * Return the message in the unexpected alert
     */
    public String getAlertMessage() {
        return message;
    }
}
