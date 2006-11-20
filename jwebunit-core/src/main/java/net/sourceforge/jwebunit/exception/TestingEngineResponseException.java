/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;

/**
 * This name reflects the name all of exceptions that will be thrown from a specific "testing engine".
 * 
 * All testing engines will respond, if necessary, using this exception instead of the testing specific engine
 * exceptions.
 * 
 * 
 * @author Nicholas Neuberger
 */
public class TestingEngineResponseException extends Exception {

    private int httpStatusCode;
    
    /**
     * 
     */
    public TestingEngineResponseException() {
        super();
    }

    public TestingEngineResponseException(int httpStatusCode) {
        super();
        this.httpStatusCode=httpStatusCode;
    }

    /**
     * @param arg0
     */
    public TestingEngineResponseException(String msg) {
        super(msg);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public TestingEngineResponseException(String msg, Throwable ex) {
        super(msg, ex);
    }

    /**
     * @param arg0
     */
    public TestingEngineResponseException(Throwable ex) {
        super(ex);
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

}
