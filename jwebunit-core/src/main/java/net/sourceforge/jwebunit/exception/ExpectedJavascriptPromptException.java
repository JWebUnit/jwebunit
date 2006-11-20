/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

import junit.framework.AssertionFailedError;

/**
 * A Javascript prompt was expected but was not thrown.
 * 
 * @author Julien Henry
 */
public class ExpectedJavascriptPromptException extends Exception {
    /**
     * Expected message in the prompt
     */
    private String message;

    public ExpectedJavascriptPromptException(String message) {
        super("A prompt was expected with message [" + message + "]");
        this.message = message;
    }

    /**
     * Return the expected message in the prompt
     */
    public String getPromptMessage() {
        return message;
    }
}
