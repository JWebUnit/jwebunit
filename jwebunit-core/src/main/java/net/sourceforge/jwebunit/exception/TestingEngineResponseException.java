/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.exception;
/**
 * This name reflects the name all of exceptions that will be thrown
 * from a specific "testing engine" or soon deprecated name of "dialog".
 * 
 * All testing engines will respond, if necessary, using this exception instead
 * of the testing specific engine exceptions.
 * 
 * Example: HttpUnit is SAX based and depending on specific operations from httpunit
 * 			a SAXException is thrown, but the HttpUnitDialog method will catch the
 * 			original exception and wrap inside this TestingEngineResponseException 
 * 
 * @author Nicholas Neuberger
 */
@SuppressWarnings("serial")
public class TestingEngineResponseException extends Exception {

	/**
	 * 
	 */
	public TestingEngineResponseException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public TestingEngineResponseException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TestingEngineResponseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public TestingEngineResponseException(Throwable arg0) {
		super(arg0);
	}

}
