package net.sourceforge.jwebunit.fit;

import com.meterware.pseudoserver.PseudoServer;
import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;

import java.io.*;

public class PseudoWebApp {

    private PseudoServer server;
    private static String htmldir = "fitplugin/test/sampleHtml/";

    public static void main (String argv[]) throws Exception {
        System.out.println("Starting up Pseudo Server...");
        htmldir = System.getProperty("user.dir") + "/" + htmldir;
        new PseudoWebApp();
        while (true) { Thread.sleep(1); } //loop until process terminated.
    }

    public PseudoServer getServer() {
        return server;
    }

    public PseudoWebApp() throws Exception {
        buildSite();
    }

    private void buildSite() throws Exception {
        server = new PseudoServer();

        server.setResource("/menu", read(new File(htmldir + "SampleMenu.html")));
        server.setResource("/colorForm", read(new File(htmldir + "ColorForm.html")));
        server.setResource("/personalInfoForm", read(new File(htmldir + "PersonalInfoForm.html")));
        server.setResource("/enterMoria", read(new File(htmldir + "MoriaDoorForm.html")));
        server.setResource("/moria_door.jpeg", readBytes(new File(htmldir + "moria_door.jpeg")), "jpeg image");

        server.setResource("colorPost", new PseudoServlet() {
           public WebResource getPostResponse() {
               WebResource result = new WebResource("<html><head><title>Color Page</title></head>" +
                                                     "<body style=\"color: rgb(0,0,0); background-color: " + getParm("color") + ";\" link=\"#000099\" vlink=\"#990099\" alink=\"#000099\">" +
                                                     "Your chosen color was " + getParm("color") + "." +
                                                     "</body></html>");
               return result;
           }
           public WebResource getGetResponse() throws IOException {
               return getPostResponse();
           }

           public String getParm(String s) {
                return super.getParameter(s) != null? getParameter(s)[0] : "";
           }
        });

        server.setResource("moriaPost", new PseudoServlet() {
           public WebResource getPostResponse() {
               WebResource result = null;
               if ((!getParm("EnterButton").equals("")) &&
                   (getParm("password").toLowerCase().equals("friend"))) {
                   result = new WebResource("<html><head><title>Moria</title></head>" +
                                            "<body style=\"background-color: beige\">" +
                                            "You made it!" + "</body></html>");
               }
               else {
                   result = new WebResource("<html><head><title>Moria Entry Failure</title></head>" +
                                            "<body style=\"background-color: beige\">" +
                                            "Nope! <a href=\"/enterMoria\">Try again!</a>" + "</body></html>");

               }
               return result;
           }
           public WebResource getGetResponse() throws IOException {
               return getPostResponse();
           }

           public String getParm(String s) {
                return super.getParameter(s) != null? getParameter(s)[0] : "";
           }
        });


        server.setResource("personalInfoPost", new PseudoServlet() {
            public WebResource getPostResponse() {
                String citizenship = "not a citizen";
                if (getParameter("citizenCheckbox") != null &&
                    getParameter("citizenCheckbox")[0].equals("on")) {
                    citizenship = "a citizen";
                }
                WebResource result = new WebResource("<html><head><title>Personal Info Results</title></head>" +
                                                     "<body style=\"background-color: beige\"><br>" +
                                                     "<table id=\"infoTable\"><tr><td colspan=2>Personal Information Table</td></tr>" +
                                                     "<tr><td>Name</td><td>Name given is " + getParm("fullName") + ".</td></tr>" +
                                                     "<tr><td>Citizenship</td><td>You indicated that you are " + citizenship + ".</td></tr>" +
                                                     "<tr><td>State of Residence</td><td>You live in " + getParm("state") + ".</td></tr>" +
                                                     "<tr><td>Sex</td><td>You are " + getParm("sex") + ".</td></tr></table>" +
                                                     "</body></html>");
                return result;
            }
            public WebResource getGetResponse() throws IOException {
                return getPostResponse();
            }

            public String getParm(String s) {
                 return super.getParameter(s) != null? getParameter(s)[0] : "none indicated";
            }
         });

    }


    byte [] readBytes(File input) throws IOException {
        byte bytes[] = new byte[(int)(input.length())];
        FileInputStream in = new FileInputStream(input);
        in.read(bytes);
        in.close();
        return bytes;
    }

    String read(File input) throws IOException {
        char chars[] = new char[(int)(input.length())];
        FileReader in = new FileReader(input);
        in.read(chars);
        in.close();
        return new String(chars);
    }
}
