/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit;

import com.meterware.httpunit.HttpUnitOptions;

/**
 * User: djoiner
 * Date: Nov 22, 2002
 * Time: 10:24:54 AM
 */

public class JavaScriptEventsTest  extends JWebUnitTest {

    public void testFormOnSubmit() {
        defineWebPage("TargetPage", "Target");
        defineWebPage("FormOnSubmit",
                "<form method=GET action=\"\" " +
                    "onSubmit=\"javascript:window.open('TargetPage.html', 'child');\">" +
                "<input type=\"submit\"/>" +
                "</form>");
        beginAt("FormOnSubmit.html");
        submit();
        gotoWindow("child");
        assertTextPresent("Target");
    }

    public void testFormOnReset() {
        defineWebPage("TargetPage", "Target");
        defineWebPage("FormOnSubmit",
                "<form method=GET action=\"\" " +
                    "onReset=\"javascript:window.open('TargetPage.html', 'child');\">" +
                "<input type=\"reset\"/>" +
                "</form>");
        beginAt("FormOnSubmit.html");
        reset();
        gotoWindow("child");
        assertTextPresent("Target");
    }

    public void testButtonOnClick() {
        defineWebPage("TargetPage", "Target");
        defineWebPage("FormOnSubmit",
                "<form method=GET action=\"\" " +
                    "onReset=\"javascript:window.open('TargetPage.html', 'child');\">" +
                "<input id=\"b1\" type=\"button\" value=\"click me\" onClick=\"javascript:window.open('TargetPage.html', 'child');\"/>" +
                "</form>");
        beginAt("FormOnSubmit.html");
        clickButton("b1");
        gotoWindow("child");
        assertTextPresent("Target");
    }

    public void testJavaScriptInFile() {
        defineResource("nav.js", "function gotoNext() { window.location='next.html'; return true; }");
        defineResource("index.html", "<html> <head>" +
		                             "<script src=\"nav.js\" type=\"text/javascript\" language=\"javascript\"></script>" +
                                     "</head> <body>" +
		                             "<h1>Javascript Test</h1>" +
                                     "<form><input type=\"button\" onclick=\"gotoNext()\" value=\"Next\" id=\"next\"></form>" +
                                     "</body></html>");
        defineResource("next.html", "<html><head>" +
                                    "<script src=\"nav.js\" type=\"text/javascript\" language=\"javascript\"></script>" +
                                    "</head><body><h1>Next</h1><p>Here is the text we expect</p></body></html>");
        beginAt("index.html");
        clickButton("next");
        assertTextPresent("Here is the text we expect");
        beginAt("index.html");
        //commented out for now due to HttpUnit bug; submit does not trigger onClick, but clickButton of a
        // submit button will trigger both the submission and the onClick.
//        submit();
//        dumpResponse(System.out);
//        assertTextPresent("Here is the text we expect");
    }

    public void testLinkAssertsWorkJavascriptDisabled() {
        defineResource("foobar.js", "function sayWoo() { return true; }");
        defineResource("test.html", "<html><head>" +
                                    "<SCRIPT language=\"JavaScript\" src=\"/foobar.js\"></script></head>" +
                                    "<body><a href=\"foo1.html\">foo1</a><a href=\"foo1.html\">foo2</a></body></html> ");
        HttpUnitOptions.setScriptingEnabled(false);
        beginAt("test.html");
        assertLinkPresentWithText("foo1");
        assertLinkPresentWithText("foo2");
        HttpUnitOptions.setScriptingEnabled(true);
    }

	public void testEmbeddedJSFile()
	{
        defineResource("script.js", "function gotoNext() { window.location='next.html'; return true; }");
		defineResource("script.html", "<html><script src=\"script.js\"></script>" +
					"<title>The Title</title></html>" );

		beginAt("script.html");
		assertTitleEquals("The Title");
	}

	public void testLinkClickSetsWindowLocation()
	{
        defineResource("script.js", "function gotoNext() { window.location='next.html';}");
		defineResource("script.html", "<html><script src=\"script.js\"></script>" +
					"<title>The Title</title><body><a href=\"javascript:gotoNext()\">link</a></body></html>" );
		defineResource("next.html", "<html><title>Next Page</title></html>" );
		
		beginAt("script.html");
		assertTitleEquals("The Title");
		clickLinkWithText("link");
		assertTitleEquals("Next Page");
	}
	
	public void testOnChangeSetsWindowLocation() throws Exception {
		defineResource("test.html", "<html><script>function changeSelect() {document.forms[0].submit();}</script>" +
			"<title>The Title</title><body>" +
			"<form name=\"testForm\" action=\"next.html\" method=\"get\">" + 
			"<select type=\"select\" name=\"testSelect\" onchange=\"changeSelect();\">" +
			"<option value=\"V1\" selected=\"true\">Value1</option>" +
			"<option value=\"V2\">Value2</option>" +
			"</select></form></body></html>");
		defineResource("next.html?testSelect=V1", "<html><title>Next Page Value1</title></html>" );
		defineResource("next.html?testSelect=V2", "<html><title>Next Page Value2</title></html>" );
		beginAt("test.html");
		assertTitleEquals("The Title");
		selectOption("testSelect", "Value2");
//		System.out.println("After Selection in Client:\n" + getDialog().getWebClient().getCurrentPage().getText());
		assertTitleEquals("Next Page Value2");
	}

}
