/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
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
	}

}
