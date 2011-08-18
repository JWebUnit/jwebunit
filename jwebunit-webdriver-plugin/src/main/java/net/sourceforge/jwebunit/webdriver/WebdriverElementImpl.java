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

/**
 * 
 */
package net.sourceforge.jwebunit.webdriver;

import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jwebunit.api.IElement;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * Webdriver implementation of IElement wrapper.
 * 
 * @author henryju
 * 
 */
public class WebdriverElementImpl implements IElement {

    /**
     * The wrapped element.
     */
    private WebElement element;

    public WebdriverElementImpl(WebElement element) {
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
        return element.getAttribute(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#name()
     */
    public String getName() {
        return element.getTagName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#getChildren()
     */
    public List<IElement> getChildren() {
        List<IElement> children = new ArrayList<IElement>();
        for (WebElement e : element.findElements(By.xpath("/"))) {
            if (e != null)
                children.add(new WebdriverElementImpl(e));
        }
        return children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#getParent()
     */
    public IElement getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#getTextContent()
     */
    public String getTextContent() {
        return element.getText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#getElement(java.lang.String)
     */
    public IElement getElement(String xpath) {
        return new WebdriverElementImpl((WebElement) element.findElement(By.xpath(xpath)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#getElements(java.lang.String)
     */
    public List<IElement> getElements(String xpath) {
        List<IElement> elements = new ArrayList<IElement>();
        for (WebElement o : element.findElements(By.xpath(xpath))) {
            elements.add(new WebdriverElementImpl(o));
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#setAttribute(java.lang.String, java.lang.String)
     */
    public void setAttribute(String string, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#setTextContent(java.lang.String)
     */
    public void setTextContent(String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int hashCode() {
        final int prime = 37;
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
        final WebdriverElementImpl other = (WebdriverElementImpl) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        }
        else if (!element.equals(other.element))
            return false;
        return true;
    }

    /**
     * Return the unwrapped Webdriver element that this IElement represents.
     * 
     * @return the Webdriver element this IElement represents.
     */
    public WebElement getHtmlElement() {
        return element;
    }

}
