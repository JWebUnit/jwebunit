/**
 * Copyright (c) 2002-2015, JWebUnit team.
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
/**
 *
 */
package net.sourceforge.jwebunit.htmlunit;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import net.sourceforge.jwebunit.api.IElement;

import java.util.ArrayList;
import java.util.List;

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
  private DomElement element;

  public HtmlUnitElementImpl(DomElement element) {
    if (element == null)
      throw new NullPointerException("Cannot create an IElement for a null element.");
    this.element = element;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#attribute(java.lang.String)
   */
  public String getAttribute(String name) {
    if ("value".equals(name) && element instanceof HtmlOption) {
      // for options, we want text if no value was specified
      return ((HtmlOption) element).getValueAttribute();
    } else {
      if (!element.hasAttribute(name))
        return null;

      return element.getAttribute(name);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#name()
   */
  public String getName() {
    return element.getNodeName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#getChildren()
   */
  public List<IElement> getChildren() {
    List<IElement> children = new ArrayList<IElement>();
    for (DomElement e : element.getChildElements()) {
      if (e != null)
        children.add(new HtmlUnitElementImpl(e));
    }
    return children;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#getParent()
   */
  public IElement getParent() {
    DomNode p = element.getParentNode();
    while (true) {
      if (p == null)
        return null;

      if (p instanceof HtmlElement)
        return new HtmlUnitElementImpl((HtmlElement) p);

      // get next parent
      p = p.getParentNode();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#getTextContent()
   */
  public String getTextContent() {
    return element.getTextContent();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#getElement(java.lang.String)
   */
  public IElement getElement(String xpath) {
    // if this fails with a ClassCastException, use getElements().get(0) (performance penalty)
    return new HtmlUnitElementImpl((HtmlElement) element.getFirstByXPath(xpath));
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#getElements(java.lang.String)
   */
  public List<IElement> getElements(String xpath) {
    List<IElement> elements = new ArrayList<IElement>();
    for (Object o : element.getByXPath(xpath)) {
      if (o instanceof HtmlElement)
        elements.add(new HtmlUnitElementImpl((HtmlElement) o));
    }
    return elements;
  }

  public String toString() {
    return "IElement[name=" + getName() + " wrapped=" + element + "]";
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#setAttribute(java.lang.String)
   */
  public void setAttribute(String string) {
    element.setAttributeNS(null, string, "1");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#setAttribute(java.lang.String, java.lang.String)
   */
  public void setAttribute(String name, String value) {
    if ("value".equals(name) && element instanceof HtmlInput) {
      // for HtmlInputs, we want to run any onChange code if the value changes
      ((HtmlInput) element).setValueAttribute(value);
    } else {
      element.setAttribute(name, value);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwebunit.api.IElement#setTextContent(java.lang.String)
   */
  public void setTextContent(String value) {
    if (element instanceof HtmlTextArea) {
      ((HtmlTextArea) element).setText(value);
    } else {
      element.setTextContent(value);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((element == null) ? 0 : element.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final HtmlUnitElementImpl other = (HtmlUnitElementImpl) obj;
    if (element == null) {
      if (other.element != null)
        return false;
    } else if (!element.equals(other.element))
      return false;
    return true;
  }

  /**
   * Return the unwrapped HtmlUnit element that this IElement represents.
   *
   * @return the HtmlUnit element this IElement represents.
   */
  public DomElement getHtmlElement() {
    return element;
  }

}
