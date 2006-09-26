/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * Custom exception for assertNotContains failure. Could be use to get not expected content
 * and actual text.
 * 
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
@SuppressWarnings("serial")
public class AssertNotContainsException extends JWebUnitException {

    private Object content;

    private Object text;

    public AssertNotContainsException(Object content, Object text) {
        super();
        this.content = content;
        this.text = text;
    }

    public Object getNotExpectedContent() {
        return content;
    }

    public Object getActualText() {
        return text;
    }

}
