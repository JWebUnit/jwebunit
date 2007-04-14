/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * A Javascript prompt was displayed but not expected.
 * 
 * @author Julien Henry
 */
public class UnexpectedJavascriptPromptException extends RuntimeException {

    /**
     * Message in the unexpected prompt
     */
    private String message;

    public UnexpectedJavascriptPromptException(String message) {
        super("A unexpected prompt with message [" + message
                + "] was displayed");
        this.message = message;
    }

    /**
     * Return the message of the expected prompt
     */
    public String getConfirmMessage() {
        return message;
    }
}
