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
