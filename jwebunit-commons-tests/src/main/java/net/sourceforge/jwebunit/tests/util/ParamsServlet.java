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

public class ParamsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(HtmlHelper.getStart("Submitted parameters"));
        out.write("<h1>Submitted parameters</h1>\n<p>Params are:<br/>");
        /*
         * Prints POST and GET parameters as name=value1[,value2...] separated
         * by <BR/>
         */

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            List /* FileItem */items = null;
            try {
                items = upload.parseRequest(request);
            } catch (FileUploadException e) {
                throw new ServletException(e);
            }

            String ref = null;
            // Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    out.write(" " + item.getFieldName() + "="
                            + item.getString());
                    if (item.getFieldName().equals("myReferer")) {
                        ref = item.getString();
                    }
                } else {
                    String fieldName = item.getFieldName();
                    String fileName = item.getName();
                    out.write(" " + fieldName + "=" + fileName
                            + "{" + new String(item.get()) + "}");

                }
                if (iter.hasNext()) {
                	out.write("<br/>\n");
                }
            }
            out.write(" </p>\n");
            out.write(HtmlHelper.getLinkParagraph("return", ref));
        } else {
            java.util.Enumeration params = request.getParameterNames();
            for (; params.hasMoreElements();) {
                String p = params.nextElement().toString();
                String[] v = request.getParameterValues(p);
                out.write(p + "=");
                int n = v.length;
                if (n > 0) {
                    out.write(v[0] != null ? v[0] : "");
                    for (int i = 1; i < n; i++) {
                        out.write("," + (v[i] != null ? v[i] : ""));
                    }
                }
                if (params.hasMoreElements()) {
                	out.write("<br/>\n");
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
        }
        out.write(HtmlHelper.getEnd());
    }

}
