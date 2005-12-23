<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
<h1>Submitted parameters</h1>
<p>Params are:<%
	/* Prints POST and GET parameters as name=value1[,value2...] separated with spaces */
	java.util.Enumeration params = request.getParameterNames();
	for (; params.hasMoreElements() ;) {
        String p = params.nextElement().toString();
		String[] v = request.getParameterValues(p);
		out.write(" " + p + "=");
		int n = v.length;
		if (n > 0) {
			out.write(v[0]);
			for (int i = 1; i < n; i++) {
				out.write("," + v[i]); 
			}
		}
    }
	out.write(" ");
%>
<p><a id="return" href="<%= request.getHeader("Referer") %>">return</a></p>
</p>
</body>
</html>