package net.sourceforge.jwebunit.fit;

import com.meterware.pseudoserver.PseudoServer;

public class PseudoWebApp {

    private PseudoServer server;

    public static void main (String argv[]) {
        PseudoWebApp app = new PseudoWebApp();
        while (true) {
            //loop until process terminated.
        }
    }

    public PseudoServer getServer() {
        return server;
    }

    public PseudoWebApp() {
        buildSite();
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
        server.setResource("/multiForm",
                "<html><head><title>Multi-Button Page</title></head><body>" +
                "<form method=GET action=\"/mainpage\" >" +
                "<input type=\"text\" name=\"field1\"/>" +
                "<input type=\"submit\"name=\"Button1\" value=\"Button_1\"/>" +
                "<input type=\"submit\"name=\"Button2\" value=\"Button_2\"/>" +
                "</form></body></html>");
        server.setResource("/mainpage?field1=testValue",
                "<html><head><title>Main Page</title></head>" +
                "<body>" +
                "<h1>Main Page</h1>" +
                "</body></html>");
        server.setResource("/mainpage?field1=testValue&Button1=Button_1",
                "<html><head><title>Button 1 Pressed Page</title></head>" +
                "<body>" +
                "<h1>Button 1 Pressed</h1>" +
                "</body></html>");
        server.setResource("/mainpage", "<html><head><title>Wrong Page</title></head><body></body></html>");
    }
}
