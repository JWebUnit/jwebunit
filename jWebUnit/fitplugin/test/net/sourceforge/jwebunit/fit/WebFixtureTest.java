/*
 * User: djoiner
 * Date: Sep 26, 2002
 * Time: 11:36:01 AM
 */
package net.sourceforge.jwebunit.fit;

import junit.framework.TestCase;
import fit.Parse;
import com.meterware.pseudoserver.PseudoServer;

import java.text.MessageFormat;
import java.text.ParseException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class WebFixtureTest extends TestCase {
    static String baseUrlFmt = "http://localhost:";

    static String startTableFmt =
            "<table border=\"1\">" +
            "<tr><td>jwebunit.fit.WebFixture</td></tr>" +
            "<tr><td>baseUrl</td><td>{0}</td></tr>" +
            "<tr><td>begin</td><td>start</td></tr>" +
            "<tr><td>check</td><td>assertTitleEquals</td><td>Start Page</td></tr>" +
            "<tr><td>check</td><td>assertTextInTable</td><td>t1</td><td>table text</td></tr>" +
            "</table>";

    static String enterTableFmt =
            "<table border=\"1\">" +
            "<tr><td>jwebunit.fit.WebFixture</td></tr>" +
            "<tr><td>baseUrl</td><td>{0}</td></tr>" +
            "<tr><td>begin</td><td>start</td></tr>" +
            "<tr><td>enter</td><td>field1</td><td>testValue</td></tr>" +
            "</table>";

    static String pressTableFmt =
            "<table border=\"1\">" +
            "<tr><td>jwebunit.fit.WebFixture</td></tr>" +
            "<tr><td>baseUrl</td><td>{0}</td></tr>" +
            "<tr><td>begin</td><td>start</td></tr>" +
            "<tr><td>enter</td><td>field1</td><td>testValue</td></tr>" +
            "<tr><td>press</td><td>submit</td></tr>" +
            "</table>";

    static String pressLinkFmt =
            "<table border=\"1\">" +
            "<tr><td>jwebunit.fit.WebFixture</td></tr>" +
            "<tr><td>baseUrl</td><td>{0}</td></tr>" +
            "<tr><td>begin</td><td>start</td></tr>" +
            "<tr><td>press</td><td>link</td><td>click me</td></tr>" +
            "</table>";


    String baseUrl;
    private WebFixture fixture;
    private PseudoServer server;
    private Parse tables;
    private String expectedTable = "<table border=\"1\"><tr><td>jwebunit.fit.WebFixture</td></tr><tr><td>baseUrl</td><td>http://localhost:2600/</td></tr><tr><td>begin</td><td>start</td></tr><tr><td>check</td><td>assertTitleEquals</td><td bgcolor=\"#cfffcf\">Start Page</td></tr><tr><td>check</td><td>assertTextInTable</td><td bgcolor=\"#cfffcf\">t1</td><td>table text</td></tr></table>";

    public WebFixtureTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        fixture = new WebFixture();
        buildSite();
        baseUrl = baseUrlFmt + server.getConnectedPort() + "/";
    }

    private void parseTable(String inputTable) throws ParseException {
        String table = formatTable(inputTable);
        tables = new Parse(table);
        fixture.doTable(tables);
    }

    private void buildSite() {
        server = new PseudoServer();
        server.setResource("/start",
                "<html><head><title>Start Page</title></head><body>" +
                "<form method=GET action=\"/mainpage\" >" +
                "<input type=\"text\" name=\"field1\"/>" +
                "<input type=\"submit\"/>" +
                "<a href=\"/mainpage?field1=testValue\">click me</a>" +
                "</form>" +
                "<table summary=\"t1\">" +
                "<tr><td>table text</td></tr>" +
                "</table></body></html>");
        server.setResource("/mainpage?field1=testValue",
                "<html><head><title>Main Page</title></head>" +
                "<body>" +
                "<h1>Main Page</h1>" +
                "</body></html>");
        server.setResource("/mainpage", "<html><head><title>Wrong Page</title></head><body></body></html>");
    }

    public void testSetBaseUrl() throws ParseException {
        parseTable(formatTable(startTableFmt));
        assertEquals(baseUrl, fixture.getBaseUrl());
        fixture.tester.assertTitleEquals("Start Page");
        //System.out.println(tableToString());
        assertEquals(formatTable(expectedTable), tableToString());
    }

    private String tableToString() {
        StringWriter sw = new StringWriter();
        tables.print(new PrintWriter(sw));
        String tableOut = sw.toString();
        return tableOut;
    }

    public void testEnter() throws ParseException {
        parseTable(formatTable(enterTableFmt));
        fixture.tester.assertFormElementEquals("field1", "testValue");
    }

    public void testPressSubmit() throws ParseException {
        parseTable(formatTable(pressTableFmt));
        fixture.tester.assertTitleEquals("Main Page");
    }

    public void testPressLink() throws ParseException {
        parseTable(formatTable(pressLinkFmt));
        fixture.tester.assertTitleEquals("Main Page");
    }

    private String formatTable(String tableFmt) {
        return MessageFormat.format(tableFmt, new Object[]{baseUrl});
    }
}
