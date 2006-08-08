/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;


/**
 * Global ancestor for all exception used by jWebUnit.
 * @author Julien Henry (henryju@yahoo.fr)
 *
 */
public class JWebUnitException extends Exception {
    
    public JWebUnitException(String msg) {
        super(msg);
    }

    public JWebUnitException(String msg, Exception cause) {
        super(msg, cause);
    }
}
