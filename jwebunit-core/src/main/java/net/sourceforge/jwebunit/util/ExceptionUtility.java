/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit.util;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Utility to get stack trace as string from an exception.
 *
 * @author Wilkes Joiner
 */
public class ExceptionUtility {
    public static String stackTraceToString(Throwable t){
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
