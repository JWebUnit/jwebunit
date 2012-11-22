/**
 * Copyright (c) 2002-2012, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    		long start = System.currentTimeMillis();
    		while (System.currentTimeMillis() < start + (seconds * 1000)) {
    		    try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    continue;
                }
            }
    	}
    	
    	// lets set some headers
    	response.setHeader("Test", "test2");
    	response.setHeader("Header-Added", new java.util.Date().toString());
    	
    	response.getWriter().println("hello, world!");
        
    }

}
