/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * Custom exception for assertContains failure. Could be use to get expected contain
 * and actual text.
 * 
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
@SuppressWarnings("serial")
public class AssertContainsException extends JWebUnitException {

    private Object contain;

    private Object text;

    public AssertContainsException(Object contain, Object text) {
        super();
        this.contain = contain;
        this.text = text;
    }

    public Object getContain() {
        return contain;
    }

    public Object getText() {
        return text;
    }

}
