/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

import junit.framework.AssertionFailedError;

/**
 * A Javascript alert was expected but was not thrown.
 * 
 * @author Julien Henry
 */
public class ExpectedJavascriptAlertException extends Exception {

    /**
     * Expected message in the alert
     */
    private String message;

    public ExpectedJavascriptAlertException(String message) {
        super("An alert was expected with message [" + message + "]");
        this.message = message;
    }

    /**
     * Return the expected message in the alert
     */
    public String getAlertMessage() {
        return message;
    }
}
