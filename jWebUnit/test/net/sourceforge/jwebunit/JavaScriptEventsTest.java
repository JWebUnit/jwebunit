/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

/**
 * User: djoiner
 * Date: Nov 22, 2002
 * Time: 10:24:54 AM
 */

public class JavaScriptEventsTest  extends JWebUnitTest {

    public void testFormOnSubmit() {
        defineWebPage("TargetPage", "<html><body>Target</body></html>");
        defineWebPage("FormOnSubmit",
                "<html><body>" +
                "<form method=GET action=\"\" " +
                    "onSubmit=\"javascript:window.open('TargetPage.html', 'child');\">" +
                "<input type=\"submit\"/>" +
                "</form>" +
                "</body></html");
        beginAt("FormOnSubmit.html");
        submit();
        gotoWindow("child");
        assertTextPresent("Target");
    }

    public void testFormOnReset() {
        defineWebPage("TargetPage", "<html><body>Target</body></html>");
        defineWebPage("FormOnSubmit",
                "<html><body>" +
                "<form method=GET action=\"\" " +
                    "onReset=\"javascript:window.open('TargetPage.html', 'child');\">" +
                "<input type=\"reset\"/>" +
                "</form>" +
                "</body></html");
        beginAt("FormOnSubmit.html");
        reset();
        gotoWindow("child");
        assertTextPresent("Target");
    }

    public void testButtonOnClick() {
        defineWebPage("TargetPage", "<html><body>Target</body></html>");
        defineWebPage("FormOnSubmit",
                "<html><body>" +
                "<form method=GET action=\"\" " +
                    "onReset=\"javascript:window.open('TargetPage.html', 'child');\">" +
                "<input id=\"b1\" type=\"button\" value=\"click me\" onClick=\"javascript:window.open('TargetPage.html', 'child');\"/>" +
                "</form>" +
                "</body></html");
        beginAt("FormOnSubmit.html");
        clickButton("b1");
        gotoWindow("child");
        assertTextPresent("Target");
    }

}
