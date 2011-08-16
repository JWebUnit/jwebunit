/**
 * Copyright (c) 2011, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */

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
public class TestingEngineResponseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
    private int httpStatusCode = -1;

    /**
     * 
     */
    public TestingEngineResponseException() {
        super();
    }

    public TestingEngineResponseException(int httpStatusCode) {
        super();
        this.httpStatusCode = httpStatusCode;
    }

    public TestingEngineResponseException(int httpStatusCode, String msg) {
        super(msg);
        this.httpStatusCode = httpStatusCode;
    }

    public TestingEngineResponseException(int httpStatusCode, String msg, Throwable ex) {
        super(msg, ex);
        this.httpStatusCode = httpStatusCode;
    }

    public TestingEngineResponseException(int httpStatusCode, Exception e) {
        super("The server return "+httpStatusCode+" HTTP code.", e);
        this.httpStatusCode = httpStatusCode;
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

    /**
     * Return the HTTP status code that throw this Exception or -1 if this exception
     * was not thrown because of HTTP status code.
     * @return
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

}
