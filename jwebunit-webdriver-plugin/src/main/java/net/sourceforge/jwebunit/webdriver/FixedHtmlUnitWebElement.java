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
