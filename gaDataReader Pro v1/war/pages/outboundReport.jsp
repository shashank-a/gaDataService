<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <script src="/js/gaXHRProcessor.js"></script>
  
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
<script type='text/javascript' src='/pines/jquery.pnotify.min.js'></script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Analytics Report</title>

<script>

 
 $j(document).ready(function(){
	
	 $j("#dateFrom").datepicker({ dateFormat: "yy-mm-dd" });
	 $j("#dateTo").datepicker({ dateFormat: "yy-mm-dd" });
	 
	 
 });
  
 function validateEmail(email) { 
	  
	    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return re.test(email);
	} 
 function validateFormData()
 {
	 if($j("#dateFrom")[0].value=='')
		 return false;
		 if($j("#dateTo")[0].value=='')
			 return false;	 
			 if(validateEmail($j("#email")[0].value))
				 return false;
			 
 }
 
 function fetchOutboundData()
 { 
	 processGARequest($j('#dataFilter').serialize(),'http://gabackend.gadataservice.appspot.com/fetchV2Outbound.do?');
 }
 
 
  
 
 
 

</script>

<script type='text/javascript'>
     
      </script>
      <body>
      <div class="page-header" style="margin:20px 0 0px;padding-bottom:0">
  		<h1><small>V2 Outbound Report</small></h1></div>
  		
<div  id="msg"class="alert" >
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
  				<strong>Info::!</strong>
		</div>	
	<div class="form-group" style="width:100%;background-image: linear-gradient(to bottom, #ffffff, #e6e6e6);background-color: buttonshadow;" >
    <table>
    <tr>
    <td>
    	<form id="dataFilter"  action='http://gabackend.gadataservice.appspot.com/fetchV2Outbound.do'>
    				<input type="hidden" name="range" value="monthly"/>
				<small>Date From: </small><input type="text" class="form-control" name="dateFrom"  id="dateFrom"   value=""/>
				<small>Date To: </small><input type="text" class="form-control" name="dateTo"  id="dateTo"   value=""/>
				<small>Email: </small><input type="text"  class="form-control" name="email"  id="email"   value=""/>
				<button  >Fetch Data</button>
<!-- 				<input class='btn' type="button" name="fetchData" onclick='fetchOutboundData()' value="Fetch Data"> -->
			</form>
    	</td>
    <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
   </table>
    </div> 
    	
    <div class="table table-hover" id='table_div'></div>
    
    <div id="table_call"  style="width: 100%;text-align: justify;"></div>
    
  </body>
<script>
//document.getElementById("dateFrom").value=dateFrom!="null"?dateFrom:"";
//document.getElementById("csvAction").setAttribute("onclick","DownloadJSON2CSV(jsonData)");


function loadCSVPage()
{console.log("loading;;");
if(dateFrom=="null")
	{
	if(window.location.search.split("&")[1].split("=")[0]=="dateFrom")
	{
		//dateFrom=window.location.search.split("&")[1].split("=")[1];
	}
}
var csvURl="http://gadataservice.appspot.com/getCsvData.do?dateFrom="+dateFrom+"&page=csv";
	window.open(csvURl,"AnalyticsData","");
	console.log("loading;;"+csvURl);
	}




</script>
</html>