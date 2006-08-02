package net.sourceforge.jwebunit.fit.testservlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ColorPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html");
    	response.getWriter().write("<html><head><title>Color Page</title></head>" +
                "<body style=\"color: rgb(0,0,0); background-color: " + request.getParameter("color") + ";\" link=\"#000099\" vlink=\"#990099\" alink=\"#000099\">" +
                "Your chosen color was " + request.getParameter("color") + "." +
                "</body></html>");
    }

}
