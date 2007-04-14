/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * Exception thrown when the testing engine registry can't find the appropriat key for a specific testing engine, etc.
 * 
 * @author Nick Neuberger
 */
public class TestingEngineRegistryException extends RuntimeException {
    public TestingEngineRegistryException() {
    }

    public TestingEngineRegistryException(String s) {
        super(s);
    }
}
