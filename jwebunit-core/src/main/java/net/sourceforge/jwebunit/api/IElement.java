/**
 * 
 */
package net.sourceforge.jwebunit.api;

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
	public String attribute(String name);
	
	/**
	 * Get the element name.
	 * 
	 * @return The element name
	 */
	public String name();
	
}
