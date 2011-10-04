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
package net.sourceforge.jwebunit.webdriver;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

/**
 * Fix for issue http://code.google.com/p/selenium/issues/detail?id=2295
 * 
 * @author julien, 23 sept. 2011
 */
public class FixedHtmlUnitWebElement extends HtmlUnitWebElement {

    public FixedHtmlUnitWebElement(HtmlUnitDriver parent, HtmlElement element) {
        super(parent, element);
    }

    @Override
    public boolean isSelected() {
        assertElementNotStale();

        if (element instanceof HtmlInput) {
            return ((HtmlInput) element).isChecked();
        }
        else if (element instanceof HtmlOption) {
            return ((HtmlOption) element).isSelected();
        }

        throw new UnsupportedOperationException("Unable to determine if element is selected. Tag name is: " + element.getTagName());
    }

    @Override
    public String getAttribute(String name) {
        assertElementNotStale();

        final String lowerName = name.toLowerCase();

        if (element instanceof HtmlOption && "selected".equals(lowerName)) {
            return ((HtmlOption) element).isSelected() ? "true" : null;
        }

        if ("value".equals(lowerName)) {
            if (element instanceof HtmlTextArea) {
                return ((HtmlTextArea) element).getText();
            }

            if (element instanceof HtmlOption) {
                return ((HtmlOption) element).getValueAttribute();
            }
        }

        return super.getAttribute(name);
    }
}
