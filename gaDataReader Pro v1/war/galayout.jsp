<html>
<%
String dataasjson = (String)request.getAttribute("gaJsonData");
String gaVisitorsData = (String)request.getAttribute("gaVisitorsData");
String code= request.getParameter("code");
String accessToken=(String)request.getAttribute("accessToken");
%>
	<head>
	<link href="http://images.sb.a-cti.com/css/bundle.css" rel="stylesheet" type="text/css" />
	<link href="/css/graph.css" rel="stylesheet" type="text/css" />
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
		<script>
		var rowdata;
		var accounts = new Array();
		var acctSplitarray = new Array();
		var acctdatamap = new Array();
		var callsactioninfo;
		var gaVisitData;
		var account;
		var visitedRowArray = new Array();
		var visiteddataarray;
		var gadata;
		var dataMap=[];
		
		
			function doPageLoad()
			
				try{
					
					var gadata = localStorage.getItem("gadata");//'<%=dataasjson%>';
					//gadata = '<%=dataasjson%>';
					gaVisitData = localStorage.getItem("nat");//'<%=gaVisitorsData%>';
					//localStorage.setItem("nat",'<%=gaVisitorsData%>');
					//localStorage.setItem("gadata",'<%=dataasjson%>');
					//gaVisitData = '<%=gaVisitorsData%>';
					var parseddata = JSON.parse(gadata);
					
					if(!isObjNull(parseddata)){
						callsactioninfo = parseddata.rows;	
						//$('.agent_heading').find('span').html(callsactioninfo.length);
						$.each(callsactioninfo, function(rowid) {
							var acctvalmap = new Array();
							rowdata = callsactioninfo[rowid];
							//console.log("---->"+rowdata);
							acctSplitarray = rowdata.slice(",");
							accounts[rowid] = acctSplitarray[0].substring(0,10)+"-Acct";
							if(!isObjNull(acctdatamap[acctSplitarray[0].substring(0,10)+"-Acct"] )){
								acctvalmap = acctdatamap[acctSplitarray[0].substring(0,10)+"-Acct"];
							}
							//console.log("acctvalmap before::"+acctvalmap);								
							if(acctSplitarray[1] == 'Save')
							{	acctvalmap[0] = rowdata;
								
							}
							else if(acctSplitarray[1] == 'Send')
							{	acctvalmap[1] = rowdata;
							}
							else if(acctSplitarray[1] == 'CallConclusion')
							{	acctvalmap[2] = rowdata;
							}
							acctdatamap[acctSplitarray[0].substring(0,10)+"-Acct"] =acctvalmap;
							
							
							//console.log("rowdata::"+rowdata);
							if(!isObjNull(dataMap[acctSplitarray[0].substring(0,10)+"-Acct"]))
								{
								(dataMap[acctSplitarray[0].substring(0,10)+"-Acct"]).push(acctvalmap);	
								}
							else
								{
								
								var a=[];
								dataMap[acctSplitarray[0].substring(0,10)+"-Acct"]=a;
								(dataMap[acctSplitarray[0].substring(0,10)+"-Acct"]).push(acctvalmap);
								
								}
						});
						
					}
				}
				catch(e){
					alert("Error :" +e);
				}
				accounts = eliminateDuplicates(accounts);
				$.each(accounts, function(id,acct){
					var savecount = 0;
					var sendcount = 0;
					var cccount = 0;
					var companyname;
					var replaceAcct = acct.replace("-Acct","");
					//console.log("Account::"+acct);
					if(!isObjNull(acctdatamap[acct][0])){
						//console.log(acctdatamap[acct][0]);
						//console.log(acctdatamap[acct][0].slice(",")[2]);
						companyname = acctdatamap[acct][0].slice(",")[0].substring(11);
						savecount = acctdatamap[acct][0].slice(",")[2];
					}
					if(!isObjNull(acctdatamap[acct][1])){
						//console.log(acctdatamap[acct][1]);
						companyname = acctdatamap[acct][1].slice(",")[0].substring(11);
						sendcount = acctdatamap[acct][1].slice(",")[2];
					}
						
					if(!isObjNull(acctdatamap[acct][2])){
						//console.log(acctdatamap[acct][2]);
						companyname = acctdatamap[acct][2].slice(",")[0].substring(11);
						cccount = acctdatamap[acct][2].slice(",")[2];
					}
					
					
					$('#calldetails_id').append("<tr><td>"+acct.replace("-Acct","")+"</td><td>"+companyname+"</td><td>"+savecount+"</td><td>"+sendcount+"</td><td>"+cccount+"</td><td class='btn_light_end' style='height:10px;cursor:pointer;' onclick='viewvisitorsgraph("+replaceAcct+")'>View Data</td></tr>");
					
				});
				var parsedVisitors = JSON.parse(gaVisitData);
				
				var visrowdata;
				var visacct;
				$.each(parsedVisitors.rows,function(row){
					visrowdata = parsedVisitors.rows[row];
					visacct = visrowdata.slice(",")[0].substring(0,10);
					if(!isObjNull(visitedRowArray[visacct+'-vAcct'])){
						visitedRowArray[visacct+'-vAcct'] +=  ","+visrowdata.slice(",")[1]+"-"+visrowdata.slice(",")[2]+"-"+visrowdata.slice(",")[3]
					}else{
						visitedRowArray[visacct+'-vAcct'] =  visrowdata.slice(",")[1]+"-"+visrowdata.slice(",")[2]+"-"+visrowdata.slice(",")[3]
					}
						
						
					//console.log("Visitors :::"+parsedVisitors.rows[row]);
				});
				console.log("visitedRowArray::"+visitedRowArray['8663969826-vAcct']);
			}
			
			function viewvisitorsgraph(acctno){
				visiteddataarray = new Array();
				account = acctno;
				var acct = acctno+"-vAcct";
				var visHours = new Array();
				var hour;
				
				$.each(visitedRowArray[acct].split(","),function(id,data){
					hour = data.split('-')[0];
					if(hour.length ==2 & hour.substring(0,1) == '0')
						hour = hour.substring(1);
					visHours[hour] = data.split('-')[2];
					console.log("id:"+id+", data:"+data+": hour"+hour+", val:"+data.split('-')[2]);
				});
				console.log("visHours::"+JSON.stringify(visHours)+"====="+visHours[1]);
				 
				$.each(visHours,function(id,data){
					
					var val;
					if(typeof visHours[id] != 'undefined')
						val = visHours[id];
					else val = 0;
					
					console.log("id:"+id+",val::"+val);
					visiteddataarray[id] = [id.toString(),parseInt(val)];
				});
				  console.log("visiteddataarray:::"+JSON.stringify(visiteddataarray));
				  drawChart();
				  $('.report_holder').show(1000);
				
			}
			google.load("visualization", "1", {packages:["corechart"]});
			 google.setOnLoadCallback(drawChart);
			 function drawChart() {
		    	  var data = new google.visualization.DataTable();
		    	  data.addColumn('string', 'Hours');
		          data.addColumn('number', 'No.of Visitors');
		          
		          data.addRows(visiteddataarray);
		    	  var options = {
		    	    title: account+' - PageViews',
		    	    width: 1100,
		    	    height: 400,
		    	    displayAnnotations: true,  
		    	  };
		    	  var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
		    	  chart.draw(data, options);
		      }
			 
			function isObjNull(obj)
			{
				if(obj != null && obj != 'undefined' && obj != '')
					return false;
				else return true;
			}
			
			function eliminateDuplicates(arr) {
				  var i,
				      len=arr.length,
				      out=[],
				      obj={};

				  for (i=0;i<len;i++) {
				    obj[arr[i]]=0;
				  }
				  for (i in obj) {
				    out.push(i);
				  }
				  $('.agent_heading').find('span').html(out.length);
				  return out;
				}
			$(window).keydown(function(event){
				if(event.keyCode == 27)
					$('.report_holder').hide(1000);
			});
			 
		</script>
		
		<link type="text/css" href="/calendar/themes/base/ui.all.css" rel="stylesheet" />
		<script type="text/javascript" src="/calendar/jquery-1.3.2.js"></script>
		<script type="text/javascript" src="/calendar/ui/ui.core.js"></script>
		<script type="text/javascript" src="/calendar/ui/ui.datepicker.js"></script>
		<link type="text/css" href="/calendar/demos.css" rel="stylesheet" />
		<script type="text/javascript">
		$(function() {
			$("#dateFrom").datepicker({ dateFormat: "yy-mm-dd" });
			$("#dateTo").datepicker({ dateFormat: "yy-mm-dd" });

		});
		</script>
	</head>
	<body onload="doPageLoad();">
	<div id="history_cnt_wrapper"  class="ar9 slg">
		<h1>Analytics Report</h1>
		 <div id="fc" class="tr9">
				<form id="form2" name="redirectForm" action="/getGaData.do">
				<ul>
					<li><label>From Date:</label><input type="text" 
						name="dateFrom" id="dateFrom" value="" class="inputS" /><br /> <span>(yyyy-mm-dd)</span>
						<input type="hidden" name="code" id="code" value='<%=code%>'/>
			<input type="hidden" name="accessToken" id="accessToken" value='<%=accessToken%>'/>
			
					</li>
					<li><label>To Date:</label><input type="text" 
						name="dateTo"  id="dateTo" value="" class="inputS" /><br /> <span>(yyyy-mm-dd)</span>
					</li>
					<li><input type="submit" value="Get Data"
						class="btn_dark_end" /></li>
				</ul>
				<div class="clear"></div>
				<div class="clear"></div>
				</form>
				<div class="clear"></div>
				<div class="clear"></div>
			</div>
		<br />
		<!-- Details of Count -->
			<div id="dc" class="br9">
				<div class="agent_holder" style="height:475;overflow:auto;">
						<table id="calldetails_id" >
							<thead>
								<div class="agent_heading">
									<label>Total Count: <span>100</span>
									</label>
									<div class="clear"></div>
								</div>
								<th width="200px">Account Number</th>
								<th width="200px">Company Name</th>
								<th width="200px">Save Count</th>
								<th width="200px">Send Count</th>
								<th width="200px">CallConclusion Count</th>
								<th width="200px">Visitors</th>
							</thead>
						</table>
				</div>
			</div>
			<!-- dc -->
			
	</div>
	<div class="report_holder" id="chart_div" ></div>
	<!-- wrapper -->
</body>
</html>