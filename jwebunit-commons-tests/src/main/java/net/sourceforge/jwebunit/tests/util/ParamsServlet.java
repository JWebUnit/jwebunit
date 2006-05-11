package net.sourceforge.jwebunit.tests.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        out.write("<h1>Submitted parameters</h1>\n<p>Params are:");
        /*
         * Prints POST and GET parameters as name=value1[,value2...] separated
         * with spaces
         */
        java.util.Enumeration params = request.getParameterNames();
        for (; params.hasMoreElements();) {
            String p = params.nextElement().toString();
            String[] v = request.getParameterValues(p);
            out.write(" " + p + "=");
            int n = v.length;
            if (n > 0) {
                out.write(v[0]!=null?v[0]:"");
                for (int i = 1; i < n; i++) {
                    out.write("," + (v[i]!=null?v[i]:""));
                }
            }
        }
        out.write(" </p>\n");
        String ref = request.getHeader("Referer");
        if (ref==null) {
            if (request.getParameterValues("myReferer")!=null) {
                ref = request.getParameterValues("myReferer")[0];
            }
        }
        out.write(HtmlHelper.getLinkParagraph("return", ref));
        out.write(HtmlHelper.getEnd());
    }

}
