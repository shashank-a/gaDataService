<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
    <%@page import="java.util.* "%>
    
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
    
<%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %>         
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%

	ArrayList<String> headers=(ArrayList<String> )request.getAttribute("headers");
	ArrayList<ArrayList<String>> dataTable=(ArrayList<ArrayList<String>>)request.getAttribute("dataTable");

%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="http://gawebservice.appspot.com/css/dataTable.css">


<title>Data Grid</title>
<script type="text/javascript">

function filterData()
{
	var key=document.getElementById("filterKey").value;
	var i=0;
			while(i <(document.getElementsByTagName('tbody')[0]).childNodes.length)){
				if((document.getElementsByTagName('tbody')[0]).childNodes[2*i+1].innerHTML).indexOf(key)!=-1)
				{
					(document.getElementsByTagName('tbody')[0]).childNodes[2*i+1]='none';				
				}
				i++;
			}
	}
function showHomePage()
{	
	window.location="/GAService.do";
}

function altRows(id){
	if(document.getElementsByTagName){  
		
		var table = document.getElementById(id);  
		var rows = table.getElementsByTagName("tr"); 
		 
		for(i = 0; i < rows.length; i++){          
			if(i % 2 == 0){
				rows[i].className = "evenrowcolor";
			}else{
				rows[i].className = "oddrowcolor";
			}      
		}
	}
}

window.onload=function(){
	altRows('dataGrid');
}
</script>
</head>
<body>
<div>
<form id="filter" onsubmit="TF_filterTable(dataTable, filter);return false" 
         onreset="_TF_showAll(dataTable)">
	<div>
				<input type="text" TF_colKey="data"  id="filterKey" onkeyup="TF_filterTable(dataTable, filter)"/>
				</div>
	</form>
</div>
Data Grid

<div id="dataGrid"  >

    <table id="dataTable" border="0" cellpadding="5" cellspacing="1" class="altrowstable"
    style="BORDER-RIGHT: ##a9c6c9 1px solid; BORDER-TOP: ##a9c6c9 1px solid; BORDER-LEFT: ##a9c6c9 1px solid; BORDER-BOTTOM: ##a9c6c9 1px solid">  
 
      
      <TR > 
      	 <TH colspan="1" align="center" valign="middle"  nowrap="nowrap">Account Number<c:out value="${acctNum}"/></TH>
       </TR>
        <TR >  
      	<TH colspan="1" align="center" valign="middle"  nowrap="nowrap">Date Range:-><c:out value="${dateFrom}"/>    ----to----    <c:out value="${dateTo}"/></TH>
       </TR>
       
			    <TR >  
			        <c:forEach var="colHead" items="${headers}">  
			      <TH align="center" valign="middle" nowrap="nowrap"><c:out value="${colHead}"/></TH>
			    </c:forEach>                           
			    </TR>
 
       <tbody>
     
      
      <c:if test="${not empty dataTable}">
			    <c:forEach var="row" items="${dataTable}">  
						    <TR >  
						    <c:forEach var="entry" items="${row}">  
							    <TD TF_colKey="data" align="center" valign="middle" >  
							    <c:out value="${entry}"/></TD>  
						    </c:forEach>  
						  
						    </TR>       
 				</c:forEach>  
     </c:if>
     </tbody>
      
    </table>  
    
</div>

<a href="#" onclick="showHomePage();">showHomePage</a>


		
</body>
</html>