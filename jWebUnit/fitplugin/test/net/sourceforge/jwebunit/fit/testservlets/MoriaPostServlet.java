package net.sourceforge.jwebunit.fit.testservlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MoriaPostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if ((!request.getParameter("EnterButton").equals(""))
                && (request.getParameter("password").toLowerCase().equals("friend"))) {
            response.getWriter().print(
                    "<html><head><title>Moria</title></head>" + "<body style=\"background-color: beige\">"
                            + "You made it!" + "</body></html>");
        } else {
            response.getWriter().print(
                    "<html><head><title>Moria Entry Failure</title></head>"
                            + "<body style=\"background-color: beige\">"
                            + "Nope! <a href=\"/enterMoria\">Try again!</a>" + "</body></html>");

        }
    }
}
