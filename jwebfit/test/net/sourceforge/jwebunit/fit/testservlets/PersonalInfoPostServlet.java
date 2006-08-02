package net.sourceforge.jwebunit.fit.testservlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonalInfoPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String citizenship = "not a citizen";
        if (request.getParameter("citizenCheckbox") != null &&
            request.getParameterValues("citizenCheckbox")[0].equals("on")) {
            citizenship = "a citizen";
        }
        response.getWriter().write("<html><head><title>Personal Info Results</title></head>" +
                                             "<body style=\"background-color: beige\"><br>" +
                                             "<table id=\"infoTable\"><tr><td colspan=2>Personal Information Table</td></tr>" +
                                             "<tr><td>Name</td><td>Name given is " + request.getParameter("fullName") + ".</td></tr>" +
                                             "<tr><td>Citizenship</td><td>You indicated that you are " + citizenship + ".</td></tr>" +
                                             "<tr><td>State of Residence</td><td>You live in " + request.getParameter("state") + ".</td></tr>" +
                                             "<tr><td>Sex</td><td>You are " + request.getParameter("sex") + ".</td></tr></table>" + "<br><br>" +
                                             "<table id=\"infoTableColHeaders\"><tr><td colspan=3>Personal Information Table - Column Headers</td></tr>" +
                                             "<tr><td>Name</td><td>Citizenship</td><td>State of Residence</td><td>Sex</td></tr>" +
                                             "<tr><td>" + request.getParameter("fullName") + "</td><td>" + citizenship + "</td><td>" + request.getParameter("state") + "</td><td>" + request.getParameter("sex") + "</td></tr>" +
                                             "<tr><td>Fred Evans</td><td>a citizen</td><td>Tennessee</td><td>male</td></tr>" +
                                             "<tr><td>Rebecca VonStavoren</td><td>not a citizen</td><td>North Carolina</td><td>female</td></tr></table>" +
                                             "</body></html>");
    }

}
