<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     <%@page import="java.util.* "%>
     <%@page import="com.account.AccountDetails"%>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" href="http://gadataservice.appspot.com/css/dataTable.css">
 <script src="/reportProcessor.js"></script>
<script src="/js/gaVisual.js"></script>
  <script src="/js/gaXHRProcessor.js"></script>
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
<script type="text/javascript" src="/calendar/ui/ui.core.js"></script>
	<script type="text/javascript" src="/calendar/ui/ui.datepicker.js"></script>
	
	<script type='text/javascript' src='https://www.google.com/jsapi'></script>
	

<!--  <script src="/js/XmlTextParser.js"></script>
<script src="/js/XmlGenerator.js"></script>-->
	
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
	 //formatTimeStamp();	 
	 }
 
 $j(document).ready(function(){
	
	 $j("#dateFrom").datepicker({ dateFormat: "yy-mm-dd" });
	 $j("#dateTo").datepicker({ dateFormat: "yy-mm-dd" });
	
 });
 
</script>

<script type='text/javascript'>
      google.load('visualization', '1', {packages:['table']});
      google.setOnLoadCallback(drawTable);
      var dataset="";
      var table='';
      var data='';
      var selectedRow='';
      function drawTable() {
    	  
    	  		data=getTableStructure("callList");
    	  		console.log("add rows");
          data.addRows(getCallerAccountDetails());
          table = new google.visualization.Table(document.getElementById('table_div'));
          table.draw(createDataView("callDataReport",data), options);
       	  google.visualization.events.addListener(table, 'select', selectHandler);
      }
      function setKeyword(keyword)
      {
    	  document.getElementById("filterData").value=keyword;    	  
      }
      if(pageName!=null)
  	{console.info("setting page tab",pagename);
  		document.getElementById(pagename).class="active";	
  	}
      </script>
      <body>
      <div class="page-header" style="margin:20px 0 0px;padding-bottom:0">
  		<h1><small>Call Data Report</small></h1></div>
  		<div style="text-align: right;padding-right:10px; margin-right:0;position: relative;">
		  <div >
				<a href="#"onclick="DownloadJSON2CSV(rowData)"><img style="width: 30px;height: 30px" src="/images/csv_text.png"/></a>
			</div></div>
  </div>
   <ul class="nav nav-tabs">
		  <li>
		    <a href="#" id="loadTime" onclick="navigate(this.innerHTML,id)">Load Time</a>
		  </li>
		  <li >
		    <a href="#" id="callAction" onclick="navigate(this.innerHTML,this.id)">Call Action</a>
		  </li>
		  
		  <li class="active">
		    <a href="#" id="callDetails" onclick="navigate(this.innerHTML,id)">Call Details</a>
		  </li>
		  <li id="obr">
		    <a href="#" id="eventDetails" onclick="navigate(this.innerHTML,id)">Outbound</a>
		    
		  </li>
		  <li id="cxrData">
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
    <tr><h3>
    		<td>
    				<form id="dataFilter" action="/getCallReport.do">
    				<input type="hidden" name="page" value="cdr"/>
				<h2><small>Date: </small><input type="text" name="dateFrom"  id="dateFrom" onchange="document.getElementById('dataFilter').submit();"  value="${dateFrom}"/>
			<button class="btn" onclick="document.getElementById('dataFilter').submit();">Fetch Data</button></h3>
			</form>
    		</td>
    <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
    <td><h2><small>Dimension  :</small></h2></td>
	    		<td>
	    			<select onchange="getDataSet(this.value);" id="dimension">
	    				<option value="0">AccountNo</option>
	    				<option selected="selected" value="1">Action</option>
	    				<option value="2">AgentInitial</option>
	    				<option  value="3" >ConnId</option>
	    				<option value="4">IncomingANI</option>
	    			</select>
	    		</td>
	    <td><h2><small>Value:</small></h2></td>
	    <td>
	    <div class="btn-group" >
                <input type='text' class="span4 dropdown-toggle" style="width:200px;"  onkeypress="filterData('lTable');" data-toggle="dropdown" id="filterData" value="">
                <ul class="dropdown-menu" id="actionlist">
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Send</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">CallConclusion</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">CallerHistory Annotation</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Account Load</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">InBoundCall</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">AP Refetch</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Outbound Call</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">DIR</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Scenario</a></li>
                  
                </ul>
              </div>
	    </td>
	    <td>&nbsp;</td>
	    <td>
	    <button class="btn" onclick="filterData('lTable')">Filter</button>
	    <button class="btn" onclick="document.getElementById('filterData').value=''">Clear</button>
	    </td>
	     <td><h2><small>Page Size :</small></h2></td>
	    		<td>
	    			<select id="pageOption"  onchange="setPageSize(this.value)">
	    				<option value="20" selected="selected">20</option>
	    				<option value="50">50</option>
	    				<option  value="" >No Paging</option>
	    			</select>
	    		</td>
	    <td>
	    
	    </h2>
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
    <!-- 
    <div id="table_call"  style="width: 100%;text-align: justify;"></div>
    
     -->
    <!-- Visualisation Components for  Report-->
    
    
    <div class="btn dropdown-toggle"  data-toggle="dropdown" style="width:100%">
    </div>
    
    <div class="modal fade" style="width:1050px;margin-left:0;left: 10% ;"  id="myModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Avg Load Time</h4>
        </div>
        <div class="modal-body" style="width:100%" >
          <script>
          
             
          </script>
              <div id="graphBody" style="width: 100%; "></div>
        </div>
        
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->
  
  <div class="modal fade" style="width:98%;margin-left:0;left:1% ;"  id="myModalDetails">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Call Details</h4>
        </div>
        <div class="modal-body" style="width:100%" >
          <script>
                                  test data
          </script>
          <div id="table_call"  style="width: 98%;text-align: justify;"></div>
        </div>
        
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->
  
    
  </body>
<script>
document.getElementById("dateFrom").value=dateFrom!="null"?dateFrom:"";

</script>
</html>