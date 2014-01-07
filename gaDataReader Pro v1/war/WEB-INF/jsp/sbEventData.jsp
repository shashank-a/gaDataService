<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     <%@page import="java.util.* "%>
     <%@page import="com.account.AccountDetails"%>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

 <!-- <script src="http://images.sb.a-cti.com/testing/shashank/gData/reportProcessor.js"></script> -->
 <script src="/reportProcessor.js"></script>
<script src="/js/gaVisual.js"></script>
  <script src="/js/localData.js"></script>
  <link rel="shortcut icon" href="/images/google-analytics-favicon.jpg" type="image/jpg">
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
<script type="text/javascript" src="/pines/jquery.pnotify.default.css"></script>
<script type="text/javascript" src="/calendar/ui/ui.datepicker.js"></script>
<script type='text/javascript' src='https://www.google.com/jsapi'></script>
<script type='text/javascript' src='/pines/jquery.pnotify.min.js'></script>
	


<%
    		String pageName= null;
    		if(request.getAttribute("page")!=null)
    		{
    			pageName=(String)request.getAttribute("page");    			
    		}
    %>
<head>
<%
	int startIndex=1;
	int totalRows=0;
	String rowData="";
	System.out.println("inside scriptlets..");
	String dateFrom="";
	if(request.getAttribute("rowData")!=null)
	{
		rowData=(String)request.getAttribute("rowData");
		
	}
	dateFrom=(String)request.getAttribute("date");
	String msg=(String)request.getAttribute("error");
	System.out.println("dateFrom::"+dateFrom);
	System.out.println("jsp page");
	
	int k=startIndex;
%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>SB Analytics Report</title>
<% String code= request.getParameter("code");
String accessToken=(String)request.getAttribute("accessToken");
String dimension=(String)request.getAttribute("dimension");
//String totalRec=(String)request.getAttribute("recordsFetched");
%>
<script>

var pageName='<%=pageName%>';
 var code='<%=code%>';
 var rowData='<%=rowData%>';
 var dimension='<%=dimension%>';
 var dateFrom='<%=dateFrom%>';
 var rawCSVURL=null;
 var activePage=null;
 
  
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
 
 
 

</script>

<script type='text/javascript'>
      google.load('visualization', '1', {packages:['table']});
      google.setOnLoadCallback(sbEventDetail);
      var dataset="";
      var table='';
      var data='';
      var selectedRow='';
      function sbEventDetail() {
    	  data=getTableStructure("seven");
    	  		console.log("add rows");
          data.addRows(jsonData);
          table = new google.visualization.Table(document.getElementById('table_div'));
          
          table.draw(createDataView("seven",data), options);
       	  //google.visualization.events.addListener(table, 'select', selectHandler);
          activePage="SB Event Detail";
      }
      function setKeyword(keyword)
      {
    	  document.getElementById("filterData").value=keyword;    	  
      }
    if(pageName!=null)
  	{
    	console.info("setting page tab",pagename);
  		document.getElementById(pagename).class="active";	
  	}
      
      function notifyUser(msg)
      {
    	  $j.pnotify_remove_all();
    	  $j.pnotify(msg);
    	  
      }
       
      </script>
      <body>
      <div class="page-header" style="margin:20px 0 0px;padding-bottom:0">
  		<h1><small>SwitchBoard</small></h1></div>
  		<div style="text-align: right;padding-right:10px; margin-right:0;position: relative;">
		  <div ><a href="#" id="csvAction" onclick="loadCSVPage()"><img style="width: 30px;height: 30px" src="/images/csv_text.png"/></a>
			</div></div>
  </div>
   <ul class="nav nav-tabs">
		
		  <li class="active">
		    <a href="#" id="eventDetails" onclick="navigate(this.innerHTML,id)">SB Event Detail</a>
		  </li>
		  <li id="adr">
		    <a href="#" id="agentDetails" onclick="navigate(this.innerHTML,id)">Agent Details</a>
		  </li>
		  <li id="ccr">
		    <a href="#" id="ccReport" onclick="navigate(this.innerHTML,id)">CallConclusion</a>
		  </li>
		  <li id="cxr">
		    <a href="#" id="cxFeedback" onclick="navigate(this.innerHTML,id)">CX Feedback</a>
		  </li>
</ul>
  
<div  id="msg"class="alert" >
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
  				<strong>Info::!</strong> <%=msg!=null?msg:"Select a Date"%>
		</div>
  	
  		<div class="btn dropdown-toggle"  data-toggle="dropdown" style="width:100%">
	<div >
    <table>
    <tr><h3>
    		<td>
    				<form id="dataFilter" action="/getEventDetails.do">
    				<input type="hidden" name="page" value="edr"/>
				<h2><small>Date: </small><input type="text" name="dateFrom"  id="dateFrom" onchange="document.getElementById('dataFilter').submit();"  value="${dateFrom}"/>
			<button class="btn" onclick="document.getElementById('dataFilter').submit();">Fetch Data</button></h3>
			</form>
    		</td>
    <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
    <td><h2><small>Dimension  :</small></h2></td>
	    		<td>
	    			<select  onchange="getDataSet(this.value);" id="dimension">
	    				<option value="0">AccountNo</option>
	    				<option  value="1">Action</option>
	    				<option selected="selected" value="2">Agent Email</option>
	    				<option  value="3">ConnId</option>
	    				<option  value="4">CallerID</option>
	    			</select>
	    		</td>
	    <td><h2><small>Value:</small></h2></td>
	    <td>
	    <div class="btn-group" >
                <input type='text' class="span4 dropdown-toggle" style="width:200px;"  onkeypress="filterData('lTable');" data-toggle="" id="filterData" value="">
                <ul class="dropdown-menu" id="actionlist">
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Send</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">CallConclusion</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">CallerHistory Annotation</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Account Load</a></li>
                  
                </ul>
                
              </div>
	    </td>
	    <td>&nbsp;</td>
	    <td>
	    <button class="btn" onclick="filterEventData()">Filter</button>
	    <button class="btn" onclick="clearFilters()">Clear</button>
	    </td>
	     <td><h2><small>Page Size :</small></h2></td>
	    		<td>
	    			<select id="pageOption"  onchange="setPageSize(this.value,'Event Details')">
	    				<option value="20" selected="selected">20</option>
	    				<option value="50">50</option>
	    				<option value="100">100</option>
	    				<option  value="" >No Paging</option>
	    			</select>
	    		</td>
    		<td>
	    </h2>
	    </tr>
    		</table>
    </div> 
    </div>	
    <div class="table table-hover" id='table_div'></div>
    
    <div id="table_call"  style="width: 100%;text-align: justify;"></div>
    
  </body>
<script>
document.getElementById("dateFrom").value=dateFrom!="null"?dateFrom:"";

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
	
setTimeout("notifyUser('This page is in Development Mode. Application can throw Exceptions')",2000);

</script>
</html>