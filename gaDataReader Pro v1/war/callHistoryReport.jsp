	<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     <%@page import="java.util.* "%>
     <%@page import="com.account.AccountDetails"%>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" href="http://gadataservice.appspot.com/css/dataTable.css">
<script src="/reportProcessor.js"></script>
<!-- http://s3-ap-southeast-1.amazonaws.com/shashanksworld/gaprocessor/reportProcessor.js
http://s3-ap-southeast-1.amazonaws.com/shashanksworld/gaprocessor/gaVisual.js-->
<script src="/js/gaVisual.js"></script>
<script src="/js/gaXHRProcessor.js"></script>

<script src="/js/jquery.js"></script>
<link type="text/css" href="/calendar/themes/base/ui.all.css" rel="stylesheet" />
<link type="text/css" href="/css/flyBox.css" rel="stylesheet" />
<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
 <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet" media="screen">




<!--  <script src="/js/XmlTextParser.js"></script>
<script src="/js/XmlGenerator.js"></script>-->


<head>
<%
	int startIndex=1;
	int totalRows=0;
	String rowData="";
	System.out.println("inside scriptlets..");
	String dimension="";	
	if(request.getAttribute("rowData")!=null)
	{
		rowData=(String)request.getAttribute("rowData");
		dimension=(String)request.getAttribute("dimension");
	}
	System.out.println("jsp page");
	int k=startIndex;
%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Analytics Report</title>
<% String code= request.getParameter("code");
String accessToken=(String)request.getAttribute("accessToken");


//String totalRec=(String)request.getAttribute("recordsFetched");
%>
<script>
 var code='<%=code%>';
 var rowData='<%=rowData%>';
 var dimension='<%=dimension%>';
 if(rowData=="")
 {rowData=localStorage.getItem("rowdata");
 console.log("row data copied")
 	 }
 var jsonData=JSON.parse(rowData);
 
</script>
<script type='text/javascript' src='https://www.google.com/jsapi'></script>
<script type='text/javascript'>
      google.load('visualization', '1', {packages:['table']});
      google.setOnLoadCallback(drawTable);
      var table='';
      var data='';
      var selectedRow='';
      function drawTable() {
    	  var options = {'showRowNumber': true};
       	data=getTableStructure(dimension);
       	data.addRows(getCallerHistoryDetails());
       	options['page'] = 'enable';
        options['pageSize'] = 20;
        options['pagingSymbols'] = {prev: 'prev', next: 'next'};
        options['pagingButtonsConfiguration'] = 'auto';

        table = new google.visualization.Table(document.getElementById('table_div'));
        table.draw(createDataView("CallHistory"), options);
        //google.visualization.events.addListener(table, 'select', selectHandler);
           
      }
   
      
      </script>
      <body>
      <div class="page-header">
  		<h1><small>Caller History Report</small></h1>
		</div>
   <ul class="nav nav-tabs">
		  <li class="active">
		    <a href="#" id="callAction" onclick="navigate(this.innerHTML,this.id)">Call Action</a>
		  </li>
		  <li >
		    <a href="#" id="loadTime" onclick="navigate(this.innerHTML,id)">Load Time</a>
		  </li>
</ul>
    
    <div id='table_div'></div>
    
    <div id="table_call"  style="width: 100%;text-align: justify;"></div>
    
  </body>


</html>