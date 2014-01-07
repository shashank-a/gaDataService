<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     <%@page import="java.util.* "%>
     <%@page import="com.account.AccountDetails"%>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" href="http://gadataservice.appspot.com/css/dataTable.css">
<script src="/reportProcessor.js"></script>
<script src="/js/gaXHRProcessor.js"></script>
<script src="/js/gaVisual.js"></script>
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
	
	if(request.getAttribute("accountData")!=null)
	{
		ArrayList<AccountDetails> accountDetails=(ArrayList<AccountDetails>)request.getAttribute("accountData");
		rowData=(String)request.getAttribute("rowData");
		
	}
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
    	  		data.setCell(0, {style: ' font-size:9pt;'});
    	  		console.log("add rows");
          data.addRows(jsonData);
        options['page'] = 'enable';
          options['pageSize'] = 50;
          options['pagingSymbols'] = {prev: 'prev', next: 'next'};
          options['pagingButtonsConfiguration'] = 'auto';

          table = new google.visualization.Table(document.getElementById('table_div'));
          table.draw(createDataView("callDataReport",data), options);
        google.visualization.events.addListener(table, 'select', selectHandler);
      }
      
      </script>	
      <body>
      <div class="page-header">
  		<h1><small>Call Data Report</small></h1>
		</div>
    <div>
    <table>
    
	    <tr>
	    <td><h2><small>Dimension  :</small></h2></td>
	    		<td>
	    			<select id="dimension">
	    				<option value="0">AccountNo</option>
	    				<option value="1">Action</option>
	    				<option value="2">AgentInitial</option>
	    				<option  value="3" selected="selected">ConnId</option>
	    				<option value="4">IncomingANI</option>
	    			</select>
	    		</td>
	    <td><h2><small>Value:</small></h2></td>
	    <td><input type="text" id="filterData"/></td>
	    <td>&nbsp;</td>
	    <td>
	    <button class="btn" onclick="filterData()">Filter</button>
	    </td>
	    </tr>
	    
    		</table>
    </div>  
    <div id='table_div'></div>
    
    <div id="table_call"  style="width: 100%;text-align: justify;"></div>
    
  </body>


</html>