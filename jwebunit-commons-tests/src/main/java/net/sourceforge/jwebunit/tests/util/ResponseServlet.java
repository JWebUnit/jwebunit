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
        
    }

}
