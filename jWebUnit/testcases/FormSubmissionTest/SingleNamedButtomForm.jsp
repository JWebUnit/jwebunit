<html>
<head></head>
<body>
	<form method=GET action="TargetPage?color=blue">
		<input type=submit>
	</form>
	<% request.getParameter("color") %>
	
</body>
</html>