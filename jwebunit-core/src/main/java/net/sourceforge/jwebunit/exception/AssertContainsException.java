/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * Custom exception for assertContains failure. Could be use to get expected content
 * and actual text.
 * 
 * @author Julien Henry (henryju@yahoo.fr)
 * 
 */
@SuppressWarnings("serial")
public class AssertContainsException extends JWebUnitException {

    private Object content;

    private Object text;

    public AssertContainsException(Object content, Object text) {
        super();
        this.content = content;
        this.text = text;
    }

    public Object getExpectedContent() {
        return content;
    }

    public Object getActualText() {
        return text;
    }

}
