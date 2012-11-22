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
/**
 * 
 */
package net.sourceforge.jwebunit.webdriver;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jwebunit.api.IElement;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Webdriver implementation of IElement wrapper.
 * 
 * @author henryju
 * 
 */
public class WebDriverElementImpl implements IElement {

    /**
     * The wrapped element.
     */
    private WebElement element;
    
    /**
     * A reference to the driver.
     */
    private WebDriver driver;

    public WebDriverElementImpl(WebDriver driver, WebElement element) {
        this.driver = driver;
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
        for (WebElement e : element.findElements(By.xpath("child::*"))) {
            if (e != null)
                children.add(new WebDriverElementImpl(driver, e));
        }
        return children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#getParent()
     */
    public IElement getParent() {
        return new WebDriverElementImpl(driver, element.findElement(By.xpath("parent::*")));
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
        return new WebDriverElementImpl(driver, (WebElement) element.findElement(By.xpath(xpath)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#getElements(java.lang.String)
     */
    public List<IElement> getElements(String xpath) {
        List<IElement> elements = new ArrayList<IElement>();
        for (WebElement o : element.findElements(By.xpath(xpath))) {
            elements.add(new WebDriverElementImpl(driver, o));
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
    public void setAttribute(String name) {
        ((JavascriptExecutor) driver).executeScript("return arguments[0].setAttribute(arguments[1], true);", element, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#setAttribute(java.lang.String, java.lang.String)
     */
    public void setAttribute(String name, String value) {
        if ("value".equals(name) && "input".equals(element.getTagName())) {
            // for inputs, we want to run any onChange code if the value changes
            element.sendKeys(value);
        } else {
            ((JavascriptExecutor) driver).executeScript("return arguments[0].setAttribute(arguments[1], arguments[2]);", element, name, value);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.jwebunit.api.IElement#setTextContent(java.lang.String)
     */
    public void setTextContent(String value) {
        if (element.getTagName().equals("textarea")) {
            element.clear();
            element.sendKeys(value);
        } else {
            ((JavascriptExecutor) driver).executeScript(
                "var parent = arguments[0];" +
                "var children = parent.childNodes;" +
                "for (i=0; i< children.length; i++) {" +
                "  parent.removeChild(children[i]);" +
                "}" +
                "parent.appendChild(document.createTextNode(arguments[1]));"
                , element, value);
        }
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
        final WebDriverElementImpl other = (WebDriverElementImpl) obj;
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
