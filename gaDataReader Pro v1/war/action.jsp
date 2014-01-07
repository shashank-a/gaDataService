
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>action jsp</title>
<script>

url="/getCallReport.do?page=cdr&dateFrom=";
	window.location=url;


var _gaq = _gaq || [];
function pushData()
{
	console.log("..push data called");
	var domain=document.getElementById("domain").value;
	var gatc=document.getElementById("gatc").value;
	
	var _gaq = _gaq || [];
	_gaq.push(['_setAccount', gatc]);
	_gaq.push( ['_setDomainName', domain]);
	_gaq.push(['_setAllowLinker', true]);
	_gaq.push(['_trackPageview']);
	
	console.log("..pushing enviroment variables");
	var	cat =document.getElementById("cat").value;
	var act=document.getElementById("act").value;;
	var label=document.getElementById("label").value;

	_gaq.push(['_trackEvent', cat,act,label]);
	console.log("event pushed");
}

	

	(function() {
  	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  	ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	})();

	
	
</script>
<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
 <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet" media="screen">
</head>
<body>

	Welcome.....

	<div style="width: 100%;">
	<div align="center">

	<a href="/GAService.do">login action</a>
	
	<div style="border: 1 px solid black;text-align:center; width: 100%;">
					<form id="form3" name="redirectForm" action="/GAService.do">
						<input type="text" name="dateFrom" id="dateForm">
						<input type="text" name="dateTo" id="dateTo">

						<input type="submit" value="fetch">
					</form>	
			</div>
			<br>
			<br>
			<br>
		<hr>	
	
</body>
</html>