/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
		
		Cookie cookie = new Cookie("serveurCookie","foo");
		response.addCookie(cookie);
	}

}
