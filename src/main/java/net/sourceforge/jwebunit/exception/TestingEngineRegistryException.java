/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
**********************************/
package net.sourceforge.jwebunit.exception;


/**
 * Exception thrown when the testing engine registry can't find the
 * appropriat key for a specific testing engine, etc.
 * @author Nick Neuberger
 */
public class TestingEngineRegistryException extends RuntimeException {
    public TestingEngineRegistryException() {
    }

    public TestingEngineRegistryException(String s) {
        super(s);
    }
}
