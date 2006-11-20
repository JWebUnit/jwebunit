/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

import junit.framework.AssertionFailedError;

/**
 * Represents a problem establishing a form on the current response for which a request is to be built.
 * 
 * @author Wilkes Joiner
 */
public class UnableToSetFormException extends AssertionFailedError {
    public UnableToSetFormException() {
    }

    public UnableToSetFormException(String s) {
        super(s);
    }
}
