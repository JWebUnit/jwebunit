package net.sourceforge.jwebunit.fit;

import com.meterware.pseudoserver.PseudoServer;

public class PseudoWebApp {

    private PseudoServer server;

    public static void main (String argv[]) {
        new PseudoWebApp();
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
        server.setResource("/menu",
                "<html><head><title>Menu Page</title></head><body>" +
                "<a href=\"/colorForm\">Color form page</a>" +
                "<a href=\"/multiButtonForm\">Multiple button form page</a>" +
                "<a href=\"/multiForm\">Two forms page</a>" +
                "<a href=\"/tables\">Table page</a>" +
                "<table summary=\"t1\">" +
                "<tr><td>table text</td></tr>" +
                "</table></body></html>");
        server.setResource("/colorForm",
                "<html><head><title>Choose Color Page</title></head><body>" +
                "<form method=GET action=\"/colorPost\" >" +
                "Enter color (red or blue):" +
                "<input type=\"text\" name=\"color\" value=\"default\"/>" +
                "<input type=\"submit\"/>" +
                "</form>" +
                "</body></html>");
        server.setResource("/colorPost?color=blue",
                "<html><head><title>Blue Page</title></head>" +
                "<body style=\"color: rgb(0,0,0); background-color: rgb(0,0,153);\" link=\"#000099\" vlink=\"#990099\" alink=\"#000099\">" +
                "</body></html>");
        server.setResource("/colorPost?color=red",
                "<html><head><title>Red Page</title></head>" +
                "<body style=\"color: rgb(0,0,0); background-color: rgb(255,0,0);\" link=\"#000099\" vlink=\"#990099\" alink=\"#000099\">" +
                "</body></html>");
        server.setResource("/multiButtonForm",
                "<html><head><title>Multi-Button Page</title></head><body>" +
                "<form method=GET action=\"/buttonPost\" >" +
                "Enter friend and choose button: " +
                "<input type=\"text\" name=\"textField\"/>" +
                "<input type=\"submit\"name=\"Button1\" value=\"Button_1\"/>" +
                "<input type=\"submit\"name=\"Button2\" value=\"Button_2\"/>" +
                "</form></body></html>");
        server.setResource("/buttonPost?textField=friend&Button1=Button_1",
                "<html><head><title>Button 1 Pressed Page</title></head>" +
                "<body>" + "<h1>Button 1 Pressed</h1>" + "</body></html>");
        server.setResource("/buttonPost?textField=friend&Button2=Button_2",
                "<html><head><title>Button 2 Pressed Page</title></head>" +
                "<body>" + "<h1>Button 2 Pressed</h1>" + "</body></html>");
        server.setResource("/multiForm",
                "<html><head><title>Multi-Form Page</title></head><body>" +
                "<form name=\"buttonForm\" method=GET action=\"/buttonPost\" >" +
                "Enter friend and choose button: " +
                "<input type=\"text\" name=\"textField\"/>" +
                "<input type=\"submit\"name=\"Button1\" value=\"Button_1\"/>" +
                "<input type=\"submit\"name=\"Button2\" value=\"Button_2\"/>" +
                "</form>" + "<hr>" +
                "<form name=\"colorForm\" method=GET action=\"/colorPost\" >" +
                "Enter color (red or blue):" +
                "<input type=\"text\" name=\"color\" value=\"default\"/>" +
                "<input type=\"submit\"/>" +
                "</form>" +
                "</body></html>");
        server.setResource("/tables",
                "<html><head><title>Table Page</title></head><body>" +
                "<table summary=\"t1\">" +
                "<tr><td>table text</td></tr>" +
                "</table></body></html>");
    }
}
