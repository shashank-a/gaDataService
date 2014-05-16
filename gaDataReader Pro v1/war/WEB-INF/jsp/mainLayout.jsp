<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib  prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %> 
     
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<!-- <script type="text/javascript" async src="http://www.google-analytics.com/ga.js"></script> -->
<script src="http://images.sb.a-cti.com/testing/shashank/gData/reportProcessor.js"></script>
<script src="http://images.sb.a-cti.com/testing/shashank/gData/manageWorker.js"></script>
<script src="http://images.sb.a-cti.com/testing/shashank/gData/gaVisual.js"></script>
<script src="http://images.sb.a-cti.com/testing/shashank/gData/gaXHRProcessor.js"></script>
<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
<script src="/js/jquery.tablesorter.min.js"></script>
 
  <script>
  var $j = jQuery.noConflict();
  
</script> 	
<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
 <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet" media="screen">
<link type="text/css" href="/calendar/themes/base/ui.all.css" rel="stylesheet" />
<link type="text/css" href="/css/Style.css" rel="stylesheet" />
<script type="text/javascript" src="/calendar/ui/ui.core.js"></script>
<script type="text/javascript" src="/calendar/ui/ui.datepicker.js"></script>
    
	
<%
    		String pageName= null;
    		if(request.getAttribute("page")!=null)
    		{
    			pageName=(String)request.getAttribute("page");    			
    		}
    %>
<head>
<%
	int totalRows=0;
	String rowData="";
	String dateFrom="";
	if(request.getAttribute("rowData")!=null)
	{
		rowData=(String)request.getAttribute("rowData");
	}
	dateFrom=(String)request.getAttribute("date");
	String msg=(String)request.getAttribute("error");
	System.out.println("dateFrom::"+dateFrom);
	System.out.println("jsp page");

%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Analytics Report</title>
<script>
	
console.log("time:::"+new Date());
var pageName='<%=pageName%>';
 var rowData='<%=rowData%>';
 var dateFrom='<%=dateFrom%>';
 if(rowData=="")
 {
	 rowData=localStorage.getItem("rowdata");
 	console.log("row data copied")
 	 }
 rowData=decodeURI(rowData,"UTF-8");
 var jsonData=JSON.parse(rowData);
 
 $j(document).ready(function(){
	$j("#dateFrom").datepicker({ dateFormat: "yy-mm-dd" });
	 
 });
 //worker.postMessage(new createCustomObject('sendDetails',JSON.parse(localStorage.getItem("rowdata")),'Send',1,'false'));
 worker.postMessage(new createCustomObject('sendDetails',JSON.parse(localStorage.getItem("rowdata")),"Done",1,'false','sendDetails'));
</script>

<script type='text/javascript'>

      function setKeyword(keyword)
      {
    	  document.getElementById("filterData").value=keyword;    	  
      }
       
      
      
//             var _gaq = _gaq || [];
      
//       	_gaq.push(['_setAccount', 'UA-34454419-1']);
//       	_gaq.push( ['_setDomainName', 'none']);
//       	_gaq.push(['_setAllowLinker', true]);
//       _gaq.push(['_trackPageview']);



          	   
      console.log("ga script added");
      </script>
      <script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-34454419-1', 'auto', {'allowLinker': true});
  ga('send', 'pageview');

</script>
      
      <body>
      <div class="page-header" style="margin:20px 0 0px;padding-bottom:0">
  		<h2><small>SB Analytics Report</small></h2>
  		
  		</div>
  		<div style="text-align: right;padding-right:10px; margin-right:0;position: relative;">
  		
		  <div >
				<a href="#"onclick="DownloadJSON2CSV(filteredData)"><img style="width: 30px;height: 30px"  title="Download TSV" src="/images/Download.png"/></a>
			</div></div>
  </div>
   <ul class="nav nav-tabs" style="margin-bottom:0 px">
		  
<!-- 		  <li > -->
<!-- 		    <a href="#" id="CI" onclick="navigate(this.innerHTML,this.id)">CI</a> -->
<!-- 		  </li> -->
		  
		  <li class="active">
		    <a href="#" id="done" onclick="navigate(this.innerHTML,id)">Send</a>
		  </li>
		  <li >
		    <a href="#" id="cc" onclick="navigate(this.innerHTML,id)">CC</a>
		  </li>
		  <li >
		    <a href="#" id="save" onclick="navigate(this.innerHTML,id)">Save</a>
		  </li>
		  <li >
		    <a href="#" id="annotation" onclick="navigate(this.innerHTML,id)">Annotation</a>
		  </li>
		  
		  <li >
		    <a href="#" id="repeat" onclick="navigate(this.innerHTML,id)">Repeats</a>
		  </li>
		  
		  <li >
		    <a href="#" id="obDetails" onclick="navigate(this.innerHTML,id)">Outbound</a>
		  </li>
		  
		  <li id="cxrData">
		    <a href="#" id="feedbackData" onclick="navigate(this.innerHTML,id)">CX Feedback Data</a>
		  </li>
		  
		  <li id="msgNotes">
		    <a href="#" id="sbNotes" onclick="navigate(this.innerHTML,id)">SB Notes</a>
		 </li>
		 
		  <li>
		    <a href="#" id="chat" onclick="navigate(this.innerHTML,id)">Chat</a>
		  </li>
		  
		  <li>
		    <a href="#" id="eventToTalk" onclick="navigate(this.innerHTML,id)">AR</a>
		  </li>
  
</ul> 
  
<div  id="msgBox"class="alert" >
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
  				<div id='msg'><strong>Info::!</strong> <%=msg!=null?msg:"Select a Date"%></div>
  				
		</div>
  	
  		<div class="btn dropdown-toggle"  data-toggle="dropdown" style="width:100%">
	<div >
    <table>
    <tr><h3>
    		<td>
    				<form id="dataFilter" action="/getCallReport.do">
    				<input type="hidden" name="page" value="cdr"/>
				<h2><small>Date: </small><input type="text" name="dateFrom"  id="dateFrom"   value="${dateFrom}"/>
			<button class="btn" onclick="fetchXHRData();">Fetch Data</button></h3>
			</form>
    		</td>
    <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
    <td><h2><small>Dimension  :</small></h2></td>
	    		<td>
	    			<select onchange="getDataSet(this.value);" id="dimension">
	    				<option value="0">AccountNo</option>
	    				<option  value="1">Action</option>
	    				<option value="2">AgentInitial</option>
	    				<option  value="3" >ConnId</option>
	    				<option value="4">IncomingANI</option>
	    			</select>
	    		</td>
	    <td><small><select  id="matcher" onchange="matcherFlag=this.value;" style="width:100px">
	    				<option value="true">Contains</option>
	    				<option  value="false">Exactly</option>
	    			</select></small></td>
	    <td>
	    <div class="btn-group" >
                <input type='text' class="span4 dropdown-toggle" style="width:200px;" data-toggle="dropdown" id="filterData" value="">
                 <ul class="dropdown-menu" id="actionlist"> 
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Done</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">CallConclusion</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">CallerHistory Annotation</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Account Load</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">SB - Notes</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Annotation/close</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Outbound Call</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Save</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Repeat Set</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Associate Thumbs Down Data</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">Chat</a></li>
                  <li><a href="#" onclick="$j('#filterData').attr('value',this.innerHTML);">eventToTalk</a></li>
     </ul> 
              </div>
			
	    </td>
	    <td>&nbsp;</td>
	    <td>
	    <button class="btn" onclick="w_filterData()">Filter</button>
	    <button class="btn" onclick="clearData()">Clear</button>
	    </td>
	     	    
	    </h2>
	    </tr>
	    
    		</table>
    </div> 
    </div>	
    <div id='loadingmessage' style='display:none;text-align: center'>
  		<img src='http://images.sb.a-cti.com/testing/shashank/gData/loading.gif'/>
	</div>
    <div id='tablechart'>
    </div>
    
        
    
   
  </body>
<script>
document.getElementById("dateFrom").value=dateFrom!="null"?dateFrom:"";
function pushEvent(acctInfo, uAction, initial,Cust1)
{
// 	_gaq.push([ '_setCustomVar', 1, 'ConnId', Cust1 ]);
	console.info(acctInfo, uAction, initial);
// 	_gaq.push([ '_trackEvent', acctInfo, uAction, initial ]);
//ga('set', 'dimension1', Cust1);
//ga('set', 'dimension2', new Date());

	//ga('send', acctInfo, uAction, initial);
	ga('send', {
		  'hitType': 'event',          // Required.
		  'eventCategory': acctInfo,   // Required.
		  'eventAction':uAction,
		  'eventValue': initial
		});
	}


</script>
</html>