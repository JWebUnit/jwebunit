/**
 * Copyright (c) 2002-2012, JWebUnit team.
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
 * A Javascript prompt was displayed but not expected.
 * 
 * @author Julien Henry
 */
public class UnexpectedJavascriptPromptException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    /**
     * Message in the unexpected prompt
     */
    private String message;

    public UnexpectedJavascriptPromptException(String message) {
        super("A unexpected prompt with message [" + message
                + "] was displayed");
        this.message = message;
    }

    /**
     * Return the message of the expected prompt
     */
    public String getConfirmMessage() {
        return message;
    }
}
