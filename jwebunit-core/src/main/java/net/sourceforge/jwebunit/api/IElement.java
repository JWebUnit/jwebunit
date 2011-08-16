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
package net.sourceforge.jwebunit.api;

import java.util.List;

/**
 * A wrapper around elements so we can access their properties directly,
 * without requiring either direct access to the testing engine DOM,
 * or implementing every permutation of assert test.
 * 
 * @author jmwright
 *
 */
public interface IElement {

	/**
	 * Get the value of an attribute.
	 * 
	 * @param name The attribute name
	 * @return The value of the attribute
	 */
	public String getAttribute(String name);
	
	/**
	 * Get the element name, for example "input", "textarea", "select".
	 * 
	 * @return The element name
	 */
	public String getName();
	
	/**
	 * Get the text content, if any, of this element.
	 * 
	 * @return The text content, if any, of this element.
	 */
	public String getTextContent();
	
	/**
	 * Get the parent element, or null if none exists.
	 * 
	 * @return The parent element or null
	 */
	public IElement getParent();
	
	/**
	 * Get direct child elements of this element.
	 * 
	 * @return A list of child elements
	 */
	public List<IElement> getChildren();
	
	/**
	 * Get an element from this element by xpath.
	 * 
	 * @param xpath The xpath to serach
	 * @return an IElement if found, or null
	 */
	public IElement getElement(String xpath);
	
	/**
	 * Get all elements from this element by xpath.
	 * 
	 * @param xpath The xpath to search
	 * @return A list of all matching elements
	 */
	public List<IElement> getElements(String xpath);

	/**
	 * Set an attribute on this element.
	 * 
	 * @param string
	 */
	public void setAttribute(String string);

	/**
	 * Set an attribute on this element.
	 * 
	 * @param string
	 * @param value
	 */
	public void setAttribute(String string, String value);

	/**
	 * Set the text content on this element.
	 * 
	 * @param value
	 */
	public void setTextContent(String value);
	
	/**
	 * Two {@link IElement}s are equal if they 
	 * refer to the same element in the current page.
	 * 
	 * @param obj the object to compare
	 * @return <code>true</code> if the object is an {@link IElement}, and refers to the same
	 * 		element as this {@link IElement}
	 */
	public boolean equals(Object obj);
	
}
