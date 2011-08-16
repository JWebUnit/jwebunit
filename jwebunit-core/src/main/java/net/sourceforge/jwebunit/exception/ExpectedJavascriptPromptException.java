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
 * A Javascript prompt was expected but was not thrown.
 * 
 * @author Julien Henry
 */
public class ExpectedJavascriptPromptException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
     * Expected message in the prompt
     */
    private String message;

    public ExpectedJavascriptPromptException(String message) {
        super("A prompt was expected with message [" + message + "]");
        this.message = message;
    }

    /**
     * Return the expected message in the prompt
     */
    public String getPromptMessage() {
        return message;
    }
}
