/**
 * Copyright (c) 2010, JWebUnit team.
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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookiesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.write(HtmlHelper.getStart("Submitted cookies"));
		out.write("<h1>Submitted cookies</h1>\n<p>Cookies are:");
		/*
		 * Prints POST and GET cookies as name=value1[,value2...] separated with
		 * <br/>
		 */

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				out.write(cookies[i].getName() + "=" + cookies[i].getValue()
						+ "<br/>");
			}
		}

		out.write(" </p>\n");
		String ref = request.getHeader("Referer");
		if (ref == null) {
			if (request.getParameterValues("myReferer") != null) {
				ref = request.getParameterValues("myReferer")[0];
			}
		}
		out.write(HtmlHelper.getLinkParagraph("return", ref));

		out.write(HtmlHelper.getEnd());
		
		// to disable explicitly setting the cookie on each request
		if (request.getParameter("dont_set") == null) {
			Cookie cookie = new Cookie("serveurCookie","foo");
			response.addCookie(cookie);
		}
		
		/*
		 * To test if serveral same cookies with same path, domain and name 
		 * are passed through to the test API. This "should" not be done by a 
		 * server but there are use cases where it has to be done. One example is 
		 * the JSESSIONID cookie which is set by Tomcat but has to be modified in a 
		 * mod_jk - clustered environment in order to let the client jump to another 
		 * worker (-> Tomcat cluster member). However within the web application the 
		 * JSESSIONID cookie has already been added to the response and cannot be 
		 * removed from there via API. Solution is to set another cookie to overwrite this.  
		 * 
		 * See http://tools.ietf.org/html/draft-ietf-httpstate-cookie-21#section-5.3, 11
		 */
		if(request.getParameter("threesamecookies") != null) {
			// 1
			Cookie jsessionIDCookie = new Cookie("JSESSIONID", "07D486AC962DE67F176F70B7C9816AAE.worker1");
			jsessionIDCookie.setPath("/");
			// session cookie:
			jsessionIDCookie.setMaxAge(-2);
			jsessionIDCookie.setDomain("localhost");
			response.addCookie(jsessionIDCookie);
			// 2
			jsessionIDCookie = new Cookie("JSESSIONID", "07D486AC962DE67F176F70B7C9816AAE.worker2");
			jsessionIDCookie.setMaxAge(-2);
			jsessionIDCookie.setDomain("localhost");
			response.addCookie(jsessionIDCookie);
			
			// 3
			jsessionIDCookie = new Cookie("JSESSIONID", "07D486AC962DE67F176F70B7C9816AAE.worker3");
			jsessionIDCookie.setMaxAge(-2);
			jsessionIDCookie.setDomain("localhost");
			jsessionIDCookie.setSecure(true);
			response.addCookie(jsessionIDCookie);
		}
	}

}
