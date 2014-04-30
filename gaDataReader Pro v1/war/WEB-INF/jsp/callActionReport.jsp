<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     <%@page import="java.util.* "%>
    <%
    		String pageName= null;
    		if(request.getAttribute("page")!=null)
    		{
    			pageName=(String)request.getAttribute("page");    			
    		}
    %>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" href="http://gadataservice.appspot.com/css/dataTable.css">
 <script src="http://images.sb.a-cti.com/testing/shashank/gData/reportProcessor.js"></script>
<script src="/js/gaVisual.js"></script>
  <script src="/js/gaXHRProcessor.js"></script>
<!-- <script src="http://s3-ap-southeast-1.amazonaws.com/shashanksworld/gaprocessor/reportProcessor.js"></script>
<script src="http://s3-ap-southeast-1.amazonaws.com/shashanksworld/gaprocessor/gaVisual.js"></script>
 -->
 <link rel="shortcut icon" href="/images/google-analytics-favicon.jpg" type="image/jpg">
   	<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
 
  <script>
  var $j = jQuery.noConflict();
  
</script>
<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
 <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet" media="screen">
<link type="text/css" href="/calendar/themes/base/ui.all.css" rel="stylesheet" />
<script type="text/javascript" src="/calendar/ui/ui.core.js"></script>
	<script type="text/javascript" src="/calendar/ui/ui.datepicker.js"></script>
	
	<script type='text/javascript' src='https://www.google.com/jsapi'></script>
	

<!--  <script src="/js/XmlTextParser.js"></script>
<script src="/js/XmlGenerator.js"></script>-->
	

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
<title>Analytics Report</title>
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
 
 
 
 if(rowData=="")
 {rowData=localStorage.getItem("rowdata");
 console.log("row data copied")
 	 }
 rowData=decodeURI(rowData,"UTF-8");
 var jsonData=JSON.parse(rowData);
 
 if(jsonData)
	 {
	 formatTimeStamp();	 
	 }
 
 $j(document).ready(function(){
	
	 $j("#dateFrom").datepicker({ dateFormat: "yy-mm-dd" });
	 $j("#dateTo").datepicker({ dateFormat: "yy-mm-dd" });
	
 });
 
 
</script>

<script type='text/javascript'>
      
	  var pivotIndex="1";
	  google.load('visualization', '1', {packages:['table']});
      google.setOnLoadCallback(pivotDataByDimension);
      var dataset="";
      var table='';
      var data='';
      var selectedRow='';
      
            function setKeyword(keyword)
      {
    	  		document.getElementById("filterData").value=keyword;    	  
      }
     
      </script>
      <body>
      <div class="page-header" style="margin:20px 0 0px;padding-bottom:0">
  		<h1><small><span id="reportName">Call Action Report</span></small></h1></div>
  		<div style="text-align: right;padding-right:10px; margin-right:0;position: relative;">
		  <div ><a href="#" style="position: relative;" onclick="drawGraph();">
				<img style="width: 20px;height: 20px" data-toggle="modal" href="#myModal"  src="http://icons.iconarchive.com/icons/marcus-roberto/google-play/512/Google-Analytics-icon.png"></a>
				<a href="#"onclick="DownloadJSON2CSV(rowData)"><img style="width: 30px;height: 30px" src="/images/csv_text.png"/></a>
			</div></div>
  </div>
  
  		 <ul class="nav nav-tabs">
		  
		  <li id="chr" >
		    <a href="#" id="loadTime" onclick="navigate(this.innerHTML,id)">Load Time</a>
		  </li>
		  <li id="car" class="active">
		    <a href="#" id="callAction" onclick="navigate(this.innerHTML,this.id)">Call Action</a>
		  </li>
		  <li id="cdr">
		    <a href="#" id="callDetails" onclick="navigate(this.innerHTML,id)">Call Details</a>
		  </li>
		  <li>
		    <a href="#" id="eventDetails" onclick="navigate(this.innerHTML,id)">Outbound</a>
		    
		  </li>
		  <li >
		    <a href="#" id="feedbackData" onclick="navigate(this.innerHTML,id)">CX Feedback Data</a>
		    
		  </li>
		  <li id="v2Data">
		    <a href="#" id="v2Report" onclick="navigate(this.innerHTML,id)">V2 Outbound Report</a>
		    
		  </li>
		  <li id="msgNotes">
		    <a href="#" id="sbNotes" onclick="navigate(this.innerHTML,id)">SB Notes</a>
		    
		  </li>
  
		  
		  
</ul>
  
  
<div  id="msg"class="alert" >
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
  				<strong>Info::!</strong> <%=msg!=null?msg:"Select a Date"%>
  				
		</div>
  	
  		<div class="btn dropdown-toggle"  data-toggle="dropdown" style="width:100%">
	<div >
    <table>
    	
    <tr><h2>
    		<td>
    				<form id="dataFilter" action="/getCallReport.do">
    				<input type="hidden" name="page" value="car"/>
				<h2><small>Date: </small><input  type="text" name="dateFrom" id="dateFrom"  value="${dateFrom}"/>
			<button class="btn" onclick="document.getElementById('dataFilter').submit();">Fetch Data</button></h2>
			</form>
    		</td>
    
    <td><h2><small>Dimension  :</small></h2></td>
	    		<td>
	    			<select id="dimension">
	    				<option value="0" selected="selected">AccountNo</option>
	    				<option value="1">Action</option>
	    				<option value="2">AgentInitial</option>
	    				<option  value="3" >ConnId</option>
	    				<option value="4">IncomingANI</option>
	    			</select>
	    		</td>
	    <td><h2><small>Value:</small></h2></td>
	    <td><input type="text" id="filterData"/></td>
	    
	    <td>
	    <button class="btn" onclick="filterAccountLoad('lTable')">Filter</button>
	    </td>
	    </h2>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	    <td><h2><small>Page Size :</small></h2></td>
	    		<td>
	    			<select id="pageOption"  onchange="setPageSize(this.value)">
	    				<option value="20" selected="selected">20</option>
	    				<option value="50">50</option>
	    				<option  value="" >No Paging</option>
	    			</select>
	    		</td>
	    <td>
	    
	    
	    </tr>
	    
    		
    		</table>
    </div> <!--   <div> <span  onclick="setKeyword(this.innerHTML);" class="label label-success">Send</span>
    <span onclick="setKeyword(this.innerHTML);" class="label label-warning">CallConclusion</span>
    <span onclick="setKeyword(this.innerHTML);" class="label label-info">CallerHistory Annotation</span>
    <span  onclick="setKeyword(this.innerHTML);" class="label label-important">Outbound Call</span>
    <span  onclick="setKeyword(this.innerHTML);" class="label label-warning">Account Load</span>
    <span  onclick="setKeyword(this.innerHTML);" class="label label-success">InBoundCall</span>
    <span  onclick="setKeyword(this.innerHTML);" class="label label-info">AP Refetch</span>
    
    </div>
    -->
    </div>	
    <div class="table table-hover" id='table_div'></div>
    
    <div id="table_call"  style="width: 100%;text-align: justify;"></div>
    <div class="modal fade" style="width:1050px;margin-left:0;left: 10% ;"  id="myModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Avg Load Time</h4>
        </div>
        <div class="modal-body" style="width:100%" >
          <script>
          google.load('visualization', '1', {packages: ['corechart']});
          function drawGraph() {
              // Create and populate the data table.
                // Create and draw the visualization.
              var ac = new google.visualization.ComboChart(document.getElementById('graphBody'));
              ac.draw(createDataView("callDataReport",data), {
                title : 'Avg Account Load time.',
                width: 1000,
                height: 400,
                vAxis: {title: "Millisec"},
                hAxis: {title: "Account No|Account Name"},
                seriesType: "bars",
                series: {5: {type: "line"}}
              });
            }
                                  
          </script>
              <div id="graphBody" style="width: 100%; "></div>
        </div>
        
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->
    
  </body>
<script>
document.getElementById("dateFrom").value=dateFrom!="null"?dateFrom:"";

</script>
</html>