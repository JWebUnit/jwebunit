/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;


/**
 * Custom exception for assertEquals failure. Could be use to get expected and actual value.
 * @author Julien Henry (henryju@yahoo.fr)
 *
 */
@SuppressWarnings("serial")
public class AssertEqualsException extends JWebUnitException {
    
    private Object expected;
    private Object actual;
    
    public AssertEqualsException(Object expected, Object actual) {
        super();
        this.expected=expected;
        this.actual=actual;
    }

    public Object getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }

}
