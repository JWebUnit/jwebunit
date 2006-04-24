/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package riki;

import net.sourceforge.jwebunit.fit.WebFixture;
import com.meterware.httpunit.HttpUnitOptions;

/**
 * User: djoiner
 * Date: Nov 25, 2002
 * Time: 10:57:45 AM
 */

public class RikiFixture extends WebFixture {
    
    public RikiFixture() {
        HttpUnitOptions.reset();
        HttpUnitOptions.setAcceptCookies(true);
        tester.getTestContext().setBaseUrl("http://localhost/wiki");
    }

    // action: page, PageName
    public void page() {
        String pageName = cells.more.text();
        tester.beginAt("wiki.rb?page=" + pageName);
    }

    // press: login
    public void login() {
        tester.clickLink("login");
    }

    // press: login
    public void logout() {
        tester.clickLink("logout");
    }


    // check: action button, button value
    public void checkActionButton(String value) {
        tester.setWorkingForm("action");
        tester.assertSubmitButtonValue("actionButton", value);
    }

    // |check| action button not present |
    public void checkActionButtonNotPresent() {
        tester.assertSubmitButtonNotPresent("actionButton");
    }


    //|check | page | aTitle |
    public void checkPage(String title) {
        tester.assertTitleEquals(title);
    }

}
