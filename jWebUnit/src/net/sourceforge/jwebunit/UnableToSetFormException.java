/*
 * User: DJoiner
 * Date: Sep 13, 2002
 * Time: 1:01:41 PM
 */
package net.sourceforge.jwebunit;

import junit.framework.AssertionFailedError;

public class UnableToSetFormException extends AssertionFailedError {
    public UnableToSetFormException() {
    }

    public UnableToSetFormException(String s) {
        super(s);
    }
}
