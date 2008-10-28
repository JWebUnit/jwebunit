/**
 * 
 */
package net.sourceforge.jwebunit.htmlunit;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

import net.sourceforge.jwebunit.api.IElement;

/**
 * HtmlUnit implementation of IElement wrapper.
 * 
 * @author jmwright
 *
 */
public class HtmlUnitElementImpl implements IElement {
	
	/**
	 * The wrapped element.
	 */
	private HtmlElement element;
	
	public HtmlUnitElementImpl(HtmlElement element) {
		this.element = element;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.IElement#attribute(java.lang.String)
	 */
	@Override
	public String attribute(String name) {
		return element.getAttribute(name);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.jwebunit.api.IElement#name()
	 */
	@Override
	public String name() {
		return element.getNodeName();
	}

}
