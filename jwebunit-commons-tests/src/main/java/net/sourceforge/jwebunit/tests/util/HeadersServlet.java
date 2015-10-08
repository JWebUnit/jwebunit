/**
 * Copyright (c) 2002-2015, JWebUnit team.
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
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet to dump HTTP headers received by server.
 * @author henryju
 *
 */
public class HeadersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(HtmlHelper.getStart("Received headers"));
        out.write("<h1>Received headers</h1>\n<p>Headers are:<br/>");
        /*
         * Prints headers as name=[value] separated
         * by <BR/>
         */
        java.util.Enumeration headers = request.getHeaderNames();
        for (; headers.hasMoreElements();) {
            String h = headers.nextElement().toString();
            String v = request.getHeader(h);
            out.write(h + "=[" + v);
            if (headers.hasMoreElements()) {
            	out.write("]<br/>\n");
            }
        }
        out.write("]</p>\n");
        String ref = request.getHeader("Referer");
        if (ref == null) {
            if (request.getParameterValues("myReferer") != null) {
                ref = request.getParameterValues("myReferer")[0];
            }
        }
        out.write(HtmlHelper.getLinkParagraph("return", ref));
        out.write(HtmlHelper.getEnd());
    }

}
