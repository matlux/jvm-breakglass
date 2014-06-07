<html>
<head>
<META HTTP-EQUIV="refresh" CONTENT="5;URL=/SpringMVC/city">
</head>
<body>
	<h1>Very Important Enterprise Application</h1>	
	<h2>List of All Employees:</h2>	
	<h2>${empsRes}</h2>	
	
	Select a report:<br>
	<form action="/SpringMVC/city/NY" method="get">
  		<input type="submit" value="Search Employees living in NY">
	</form>
	<form action="/SpringMVC/city/london" method="get">
  		<input type="submit" value="Search Employees living in London">
	</form>
</body>
</html>