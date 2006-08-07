/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;


/**
 * Exception thrown when the testing engine registry can't find the
 * appropriat key for a specific testing engine, etc.
 * @author Nick Neuberger
 */
public class TestingEngineRegistryException extends JWebUnitException {

    public TestingEngineRegistryException(String s) {
        super(s);
    }

    public TestingEngineRegistryException(String s, Exception e) {
        super(s, e);
    }
}
