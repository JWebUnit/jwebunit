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
import java.io.PrintWriter;
import java.io.StringWriter;

public class WebFixtureTest extends TestCase {
    static String baseUrlFmt = "http://localhost:";

    static String startTableFmt =
            "<table border=\"1\">" +
            "<tr><td>jwebunit.fit.WebFixture</td></tr>" +
            "<tr><td>baseUrl</td><td>{0}</td></tr>" +
            "<tr><td>begin</td><td>start</td></tr>" +
            "<tr><td>check</td><td>title</td><td>Start Page</td></tr>" +
            "</table>";

        static String enterTableFmt =
            "<table border=\"1\">" +
            "<tr><td>jwebunit.fit.WebFixture</td></tr>" +
            "<tr><td>baseUrl</td><td>{0}</td></tr>" +
            "<tr><td>begin</td><td>start</td></tr>" +
            "<tr><td>enter</td><td>field1</td><td>fieldValue</td></tr>" +
            "</table>";

            static String pressTableFmt =
            "<table border=\"1\">" +
            "<tr><td>jwebunit.fit.WebFixture</td></tr>" +
            "<tr><td>baseUrl</td><td>{0}</td></tr>" +
            "<tr><td>begin</td><td>mainpage?field1=testValue</td></tr>" +
            "<tr><td>enter</td><td>field1</td><td>fieldValue</td></tr>" +
            "<tr><td>press</td><td>submit</td></tr>" +
            "</table>";


    String baseUrl;
    private WebFixture fixture;
    private PseudoServer server;
    private Parse tables;
    private String expectedTable = "<table border=\"1\"><tr><td>jwebunit.fit.WebFixture</td></tr><tr><td>baseUrl</td><td>{0}</td></tr><tr><td>begin</td><td>start</td></tr><tr><td>check</td><td>title</td><td bgcolor=\"#cfffcf\">Start Page</td></tr></table>";

    public WebFixtureTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        fixture = new WebFixture();
        buildSite();
        baseUrl = baseUrlFmt + server.getConnectedPort() + "/";
    }

    private void parseTable(String inputTable) {
        String table = formatTable(inputTable);
        tables = new Parse(table);
        fixture.doTable(tables);
    }

    private void buildSite() {
        server = new PseudoServer();
        server.setResource("start",
                "<html><head><title>Start Page</title></head><body>" +
                "<form method=GET action=\"mainpage\" >" +
                "<input type=\"text\" name=\"field1\"/>" +
                "<input type=\"submit\"/>" +
                "</form></body></html>");
        server.setResource("mainpage?field1=testValue",
                "<html><head><title>Main Page</title></head>" +
                "<body>" +
                "<h1>Main Page</h1>" +
                "</body></html>");
        server.setResource("mainpage", "<html><head><title>Wrong Page</title></head><body></body></html>");
    }

    public void testSetBaseUrl() {
        parseTable(formatTable(startTableFmt));
        assertEquals(baseUrl, fixture.getBaseUrl());
        fixture.tester.assertTitleEquals("Start Page");
    }

    public void testCheckTitle() {
        parseTable(formatTable(startTableFmt));
        StringWriter sw = new StringWriter();
        tables.print(new PrintWriter(sw));
        assertEquals(formatTable(expectedTable), sw.toString());
    }

    public void testEnter() {
        parseTable(formatTable(enterTableFmt));
        fixture.tester.assertFormElementEquals("field1", "fieldValue");
    }

    public void testPress() {
        parseTable(formatTable(pressTableFmt));
        fixture.tester.dumpResponse(System.out);
        fixture.tester.assertTitleEquals("Main Page");
    }

    private String formatTable(String tableFmt) {
        return MessageFormat.format(tableFmt, new Object[] { baseUrl } );
    }
}
