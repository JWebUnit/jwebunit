/**
 * 
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
	
}
