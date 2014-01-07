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
<script src="/js/sbLocalStorage.js"></script>
<script src="/js/jquery.js"></script>
<link type="text/css" href="/calendar/themes/base/ui.all.css" rel="stylesheet" />
<link type="text/css" href="/css/flyBox.css" rel="stylesheet" />




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
//String totalRec=(String)request.getAttribute("recordsFetched");
%>
<script>
 var code='<%=code%>';
 var rowData='<%=rowData%>';
 rowData=localStorage.getItem('rowdata');
 var jsonData=JSON.parse(rowData);
 
</script>

<script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load('visualization', '1.1', {packages: ['controls']});
    </script>
<script type='text/javascript'>


      </script>
      <body>
    <div>
	    <table>
	    <tr>
	    <td>Dimension:</td>
	    		<td>
	    			<select id="dimension">
	    				<option>AccountNo</option>
	    				<option>AgentInitial</option>
	    				<option selected="selected">ConnId</option>
	    				<option>Action</option>
	    				<option>IncomingANI</option>
	    			</select>
	    		</td>
	    <td>Value:</td>
	    <td><input type="text" id="filterData"/></td>
	    </tr>
    		</table>
    </div> 
      <div id='table_div'></div>
</body>


</html>