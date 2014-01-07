<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     <%@page import="java.util.* "%>
     <%@page import="com.account.AccountDetails"%>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" href="http://gadataservice.appspot.com/css/dataTable.css">
<script src="/js/reportProcessor.js"></script>
<script src="/js/gaXHRProcessor.js"></script>
<script src="/js/sbLocalStorage.js"></script>
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
//String totalRec=(String)request.getAttribute("recordsFetched");
%>
<script>
 var code='<%=code%>';
 var rowData='<%=rowData%>';
 
 
 function showHomePage()
 {	
 	window.location="/GAService.do";
 }
 function altRows(id){
		if(document.getElementsByTagName){  
			
			var table = document.getElementById(id);  
			var rows = table.getElementsByTagName("TR"); 
			 
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
		altRows('dataTable');
	}
		
	function getAccountData(category)
	{console.log(category);
		var responseData=getEventDescriptionForCategory(JSON.parse(localStorage.getItem("testData")),category);
		console.log(responseData);
		document.getElementById('accountData').innerHTML=createAccountTable(responseData);
		document.getElementById('category').innerHTML=category;
		showBox();
		console.log("done");
	}
	
	 </script>
	<link type="text/css" href="/calendar/themes/base/ui.all.css" rel="stylesheet" />
	<script type="text/javascript" src="/calendar/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="/calendar/ui/ui.core.js"></script>
	<script type="text/javascript" src="/calendar/ui/ui.datepicker.js"></script>
	<link type="text/css" href="/css/flyBox.css" rel="stylesheet" />
	
	
	<script type="text/javascript">
	$(function() {
		$("#dateFrom").datepicker({ dateFormat: "yy-mm-dd" });
		$("#dateTo").datepicker({ dateFormat: "yy-mm-dd" });

	});
	
	
	function showBox()
	{
		document.getElementById('light').style.display='block';
		document.getElementById('fade').style.display='block';
		
	}
	function closeBox()
	{
	document.getElementById('light').style.display='none';
	document.getElementById('fade').style.display='none';
	
	}
	
	</script>
	
</head>
<body>
	<div>
	<div style="border: 1 px solid black;text-align:center; width: 100%;">
		<form id="form2" name="redirectForm" action="/getGaData.do">
			<input type="hidden" name="code" id="code" value='<%=code%>'/>
			<input type="hidden" name="accessToken" id="accessToken" value='<%=accessToken%>'/>
			
		<table>
				Google Analytics Event Report:SwitchBoard 
		<br/>
		<tr>
				<td>Date:<input type="text" name="dateFrom" id="dateFrom"  value="${dateFrom}"/></td>
		</tr>
			<tr>	<td>
						<input type="submit" value="submit"/>
						<a href="#" id="contentBox" onclick="">getData</a>
					</td>
			</tr>
			</table>
		</form>
	</div>
				
<br>
<hr>

      </tbody>
    </table>  
    <c:if test="${not empty accountData}">
<table id="dataTable" border="0" cellpadding="5" cellspacing="1" class="altrowstable"
    style="BORDER-RIGHT: ##a9c6c9 1px solid; BORDER-TOP: ##a9c6c9 1px solid; BORDER-LEFT: ##a9c6c9 1px solid; BORDER-BOTTOM: ##a9c6c9 1px solid ;width:100%">  
 <TR>Event w.r.t Accounts ::</TR>
			    <TR>  
			        <TH align="center" valign="middle" nowrap="nowrap" style="width:10%">S No.</TH>
			      <TH align="center" valign="middle" nowrap="nowrap" style="width:50%">Account No</TH>
			      <TH align="center" valign="middle" nowrap="nowrap" style="width:10%">Send Count </TH>	
			      <TH align="center" valign="middle" nowrap="nowrap" style="width:10%">CC Count</TH>
			      <TH align="center" valign="middle" nowrap="nowrap" style="width:20%" >Call Time</TH>
			                               
			    </TR>
 <%int x=1; %>
       <tbody>
      <c:if test="${not empty accountData}">
      
			    <c:forEach var="row" items="${accountData}">  
						    <TR>
						    <TD TF_colKey="data" align="center" valign="middle" >  
							    <c:out value="<%=x++ %>"/></TD> 
							    <TD TF_colKey="data" align="center" valign="middle" > <a href="#" onclick='getAccountData(this.innerHTML.trim())'> 
							    <c:out value="${row.acctNum}"/></a></TD> 
							    <TD TF_colKey="data" align="center" valign="middle" >  
							    <c:out value="${row.sendCount}"/></TD>  
							    <TD TF_colKey="data" align="center" valign="middle" >  
							    <c:out value="${row.callConclusion}"/></TD>  
							    <TD TF_colKey="data" align="center" valign="middle" >  
							    <c:out value="${row.callTotal}"/></TD>   
						    </TR>       
 				</c:forEach>  
     </c:if>
     
     </tbody>
      
    </table>  

</c:if>
</div>
<c:if test="${empty accountData}">
     <div style="width:100%">NO Data:::</div>
   </c:if>


 <div id="light" class="white_content">
    
    <div id="category">
    </div>
    <div id="link">
    <a href="#" align="RIGHT" onclick="closeBox()">Close</a></div>
    <div id='accountData'>
    </div>
    
    
    
    </div>
    <div id="fade" class="black_overlay"></div>

</body>
    
<script>

var totalRecords='<%=(k-1)%>';
document.getElementById("total").innerHTML="Matched Records:"+<%=totalRows%>;
function doSubmitForm(nav)
{if(nav=='next')
	{console.log("next");
	document.getElementById("offset").value='<%=k%>';
	document.getElementById("form2").submit();}
if(nav=='back')
	{	
	console.log("back");
	
	var reverse=document.getElementById("offset").value;
	document.getElementById("offset").value=reverse-5000;
	document.getElementById("form2").submit();
	}
	
function resetOffset()
{
	document.getElementById("offset").value='1';
	}
	
}


</script>



</html>