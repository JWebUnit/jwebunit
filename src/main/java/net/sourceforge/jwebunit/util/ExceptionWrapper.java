/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * User: djoiner
 * Date: Nov 21, 2002
 * Time: 2:05:07 PM
 */

public class ExceptionWrapper extends RuntimeException {
    private Throwable thrown;

    public ExceptionWrapper(Throwable t) {
        thrown = t;
    }

    public Throwable fillInStackTrace() {
        return thrown == null? null : thrown.fillInStackTrace();
    }

    public String getMessage() {
        return thrown.getMessage();
    }

    public String getLocalizedMessage() {
        return thrown.getLocalizedMessage();
    }

    public String toString() {
        return thrown.toString();
    }

    public void printStackTrace() {
        thrown.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        thrown.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        thrown.printStackTrace(s);
    }
}
