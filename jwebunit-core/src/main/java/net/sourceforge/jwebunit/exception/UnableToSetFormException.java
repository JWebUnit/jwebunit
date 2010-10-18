/**
 * Copyright (c) 2010, JWebUnit team.
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
 * Represents a problem establishing a form on the current response for which a request is to be built.
 * 
 * @author Wilkes Joiner
 */
public class UnableToSetFormException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnableToSetFormException() {
    }

    public UnableToSetFormException(String s) {
        super(s);
    }
}
