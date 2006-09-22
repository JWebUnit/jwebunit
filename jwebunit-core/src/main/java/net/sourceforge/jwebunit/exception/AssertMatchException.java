/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;


/**
 * Custom exception for RE.match failure. Could be use to get expected regexp and actual value.
 * @author Julien Henry (henryju@yahoo.fr)
 *
 */
@SuppressWarnings("serial")
public class AssertMatchException extends JWebUnitException {
    
    private String expectedRe;
    private String actual;
    
    public AssertMatchException(String expectedRe, String actual) {
        super();
        this.expectedRe=expectedRe;
        this.actual=actual;
    }

    public String getExpected() {
        return expectedRe;
    }

    public String getActual() {
        return actual;
    }

}
