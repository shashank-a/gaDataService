<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     <%@page import="java.util.* "%>
     <%@page import="com.account.AccountDetails"%>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" href="http://gadataservice.appspot.com/css/dataTable.css">
 <script src="http://images.sb.a-cti.com/testing/shashank/gData/reportProcessor.js"></script>
<script src="/js/gaVisual.js"></script>
  <script src="/js/gaXHRProcessor.js"></script>
  <script src="/js/localData.js"></script>
  <!--  <script src="/js/localstoragehelper.js"></script>-->
  
<!-- <script src="http://s3-ap-southeast-1.amazonaws.com/shashanksworld/gaprocessor/reportProcessor.js"></script>
<script src="http://s3-ap-southeast-1.amazonaws.com/shashanksworld/gaprocessor/gaVisual.js"></script>
 -->
 
   	<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
 
  <script>
  var $j = jQuery.noConflict();
  
</script>
<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
 <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet" media="screen">
<link type="text/css" href="/calendar/themes/base/ui.all.css" rel="stylesheet" />
<link type="text/css" href="/pines/jquery.pnotify.default.css" rel="stylesheet" />
<script type="text/javascript" src="/calendar/ui/ui.core.js"></script>
<script type="text/javascript" src="/calendar/ui/ui.datepicker.js"></script>
<script type='text/javascript' src='https://www.google.com/jsapi'></script>
<script type='text/javascript' src='/pines/jquery.pnotify.min.js'></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Analytics Report</title>

<script>

 
 $j(document).ready(function(){
	
	 $j("#dateFrom").datepicker({ dateFormat: "yy-mm-dd" });
	 $j("#dateTo").datepicker({ dateFormat: "yy-mm-dd" });
	 
	 
 });
 if(rowData=="")
 {
	 		rowData=localStorage.getItem("rowdata");
 			console.log("row data copied");
 	 }
 rowData=decodeURI(rowData,"UTF-8");
 
 var jsonData=JSON.parse(rowData);
 if(jsonData)
	 {
	 rawDataWidth=jsonData[0].toString().split(",").length-1
	 }
 
 
 

</script>

<script type='text/javascript'>
     
      </script>
      <body>
      <div class="page-header" style="margin:20px 0 0px;padding-bottom:0">
  		<h1><small>SwitchBoard</small></h1></div>
  		
  
   
  
<div  id="msg"class="alert" >
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
  				<strong>Info::!</strong> 
		</div>
  	
  		<div class="btn dropdown-toggle"  data-toggle="dropdown" style="width:100%">
	<div >
    <table>
    <tr><h3>
    		<td>
    				<form id="dataFilter" action="/getCallReport.do">
    				<input type="hidden" name="page" value="obr"/>
				<h2><small>Date From: </small><input type="text" name="dateFrom"  id="dateFrom"   value="${dateFrom}"/>
				<h2><small>Date To: </small><input type="text" name="dateTo"  id="dateTo"   value="${dateFrom}"/>
			<button class="btn" onclick="">Fetch Data</button></h3>
			</form>
    	</td>
    <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
   
    </div> 
    </div>	
    <div class="table table-hover" id='table_div'></div>
    
    <div id="table_call"  style="width: 100%;text-align: justify;"></div>
    
  </body>
<script>
document.getElementById("dateFrom").value=dateFrom!="null"?dateFrom:"";
document.getElementById("csvAction").setAttribute("onclick","DownloadJSON2CSV(jsonData)");


function loadCSVPage()
{console.log("loading;;");
if(dateFrom=="null")
	{
	if(window.location.search.split("&")[1].split("=")[0]=="dateFrom")
	{
		dateFrom=window.location.search.split("&")[1].split("=")[1];
	}
}
var csvURl="http://gadataservice.appspot.com/getCsvData.do?dateFrom="+dateFrom+"&page=csv";
	window.open(csvURl,"AnalyticsData","");
	console.log("loading;;"+csvURl);
	}




</script>
</html>