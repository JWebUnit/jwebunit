package net.sourceforge.jwebunit.webdriver;

import com.gargoylesoftware.htmlunit.TopLevelWindow;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class FixedHtmlUnitDriver extends HtmlUnitDriver {

    @Override
    public void close() {
        if (getCurrentWindow() != null) {
            if (getWebClient().getWebWindows().size() > 1) {
                ((TopLevelWindow) getCurrentWindow().getTopWindow()).close();
                
            }
            else {
                quit();
            }
        }
    }
    
}
