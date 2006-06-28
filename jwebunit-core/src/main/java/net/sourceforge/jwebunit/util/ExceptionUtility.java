/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
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
