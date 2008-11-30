/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple servlet that allows us to test for HTTP response codes.
 * 
 * @author Jevon
 *
 */
public class ResponseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	// set the status?
    	if (request.getParameter("status") != null && request.getParameter("status").length() > 0) {
    		response.setStatus( new Integer(request.getParameter("status")) );
    	}
    	
    	// set the content type?
    	if (request.getParameter("content-type") != null && request.getParameter("content-type").length() > 0) {
    		response.setContentType( request.getParameter("content-type") );
    	}
    	
    	// wait for a timeout?
    	if (request.getParameter("timeout") != null && request.getParameter("timeout").length() > 0) {
    		int seconds = Integer.parseInt(request.getParameter("timeout"));
    		long start = System.nanoTime();
    		while (System.nanoTime() < start + (seconds * 1000 * 1000))
    			;	// wait
    	}
    	
    	// lets set some headers
    	response.setHeader("Test", "test2");
    	response.setHeader("Header-Added", new java.util.Date().toString());
    	
    	response.getWriter().println("hello, world!");
        
    }

}
