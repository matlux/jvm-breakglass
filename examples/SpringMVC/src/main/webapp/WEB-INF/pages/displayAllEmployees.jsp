<html>
<head>
<link rel="stylesheet" type="text/css" href="css/mystyle.css" media="screen" />
<META HTTP-EQUIV="refresh" CONTENT="5;URL=/SpringMVC/city">
</head>
<body>
<div class="main">
    <div class="right">
        <img src="images/bruce.jpg" />
        <h2>Message from our president:</h2>
        Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate.
        <p> May the Closure be with you!
        <p> Lots of hugs,
        <p> Bruce
    </div>
    <div class="title">
	<h1>Very Important Enterprise Application</h1>	
    </div>
    <div class="left"></div>	
	<h2>List of All Employees:</h2>	
	<h2>${empsRes}</h2>	
	
	<h3>Select a report:</h3><br>
	<form action="/SpringMVC/city/NY" method="get">
  		<input type="submit" value="Search Employees living in NY" class="reportbutton">
	</form>
	<form action="/SpringMVC/city/london" method="get">
  		<input type="submit" value="Search Employees living in London" class="reportbutton">
	</form>

    </div>
</div>
</body>
</html>