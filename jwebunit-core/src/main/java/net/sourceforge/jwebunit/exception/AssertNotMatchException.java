/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * Custom exception for RE.match success when not expected. Could be use to get
 * expected regexp and actual value.
 * 
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
@SuppressWarnings("serial")
public class AssertNotMatchException extends JWebUnitException {

    private String notExpectedRe;

    private String actual;

    public AssertNotMatchException(String notExpectedRe, String actual) {
        super();
        this.notExpectedRe = notExpectedRe;
        this.actual = actual;
    }

    public String getNotExpectedRE() {
        return notExpectedRe;
    }

    public String getActualText() {
        return actual;
    }

}
