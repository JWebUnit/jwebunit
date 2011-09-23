package net.sourceforge.jwebunit.webdriver;

import org.openqa.selenium.Capabilities;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


public class FixedHtmlUnitDriver extends HtmlUnitDriver {
    
    public FixedHtmlUnitDriver(Capabilities capabilities) {
        super(capabilities);
    }
    
    protected WebElement newHtmlUnitWebElement(HtmlElement element) {
        return new FixedHtmlUnitWebElement(this, element);
      }

}
