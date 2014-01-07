 
var callMap={};

var options = {'showRowNumber': true};
options['page'] = 'enable';
options['pageSize'] = 50;
options['pagingSymbols'] = {prev: 'prev', next: 'next'};
options['pagingButtonsConfiguration'] = 'auto';
options['allowHtml'] = 'true';
var uniqueAction=null;
var columnData=null;


function navigate(page,id)
{ 
	
	if(page=="Call Action")
	window.location="/getCallReport.do?page=car&dateFrom="+document.getElementById("dateFrom").value;
	if(page=="Load Time")
		window.location="/getCallHistoryReport.do?page=chr&dateFrom="+document.getElementById("dateFrom").value;
	if(page=="Call Details")
		window.location="/getCallReport.do?page=cdr&dateFrom="+document.getElementById("dateFrom").value;
	if(page=="Agent Details")
		{
		eventDetails.parentElement.className="";
		agentDetails.parentElement.className="active";
		clearData();
		drawAgentDetail();
		
		}
	if(page=="SB Event Detail")
		{
		eventDetails.parentElement.className="active";
		agentDetails.parentElement.className="";
		clearData();
		sbEventDetail();	
		}
	activePage=page;
	
}
function setPageSize(value,tabName) {
	
    if (value) {
      options['pageSize'] = parseInt(value, 10);
      options['page'] = 'enable';
    } else {
      options['pageSize'] = null;
      options['page'] = null;  
    }
    console.log("Setting page Size::;"+value);
    
    if(activePage=="SB Event Detail")
    	{
    	sbEventDetail();
    	}
    if(activePage=="Agent Details")
    	{
    	drawAgentDetail();}
    else{
    	drawTable();	
    }
    
  }


function processGAData(gadata)
{	
	console.log(gadata.length);
	var gaDataArray=JSON.parse(gadata);
	console.log("jsonData"+gaDataArray);
	
}

function getEventDescriptionForCategory(rowArray,dimensionFilter,filterIndex,primaryDimension)
{	
	var rowObj;
	var uniqueDimension;
	var filteredData;
	console.log("length"+rowArray.length);
	if(!isObjNull(dimensionFilter))
		{
		filteredData=filterDataByDimension(rowArray,dimensionFilter,filterIndex);	
		}
	
	console.log("filteredData::"+filteredData);
	
	if(!isObjNull(filteredData))
		{
		uniqueDimension=getUniqueDimensionFromGAData(filteredData,primaryDimension);
		}
	else{
		uniqueDimension=getUniqueDimensionFromGAData(filteredData,primaryDimension);
	}
	console.log("uniqueDimension::"+uniqueDimension);
	var len=filteredData.length;
	var account=new Array();
	
	$j.each(uniqueDimension, function(id,dimension){
		rowObj={};
		var send=0;
		var cc=0;
		var totalTime=0;
		var thumbsUp=0;
		var thumbsDown=0;
		var accLoad=0;
		$j.each(filteredData, function(id,row)
				{
					if(row[primaryDimension]==dimension)
						{
							if(row[1]=="Send")
								{
									send=send+1;
								}
							if(row[1]=="CallConclusion")
							{
								cc=cc+1;
							}
							if((row[1]=="Thumbs Up"))
								{
								thumbsUp=thumbsUp+1;
								}
							if((row[1]=="Thumbs Down"))
							{
								thumbsDown=thumbsDown+1;
							}
							if(row[1]=="Account Load")
							{
								accLoad=accLoad+1;
							}
							totalTime=parseInt(totalTime)+parseInt(row[3]);
						}
					
					rowObj.sendCount=send;
					rowObj.ccCount=cc;
					rowObj.milliSec=totalTime;
					rowObj.label=dimension;
					rowObj.thumbsUpCount=thumbsUp;
					rowObj.thumbsDownCount=thumbsDown;
					rowObj.accLoad=accLoad;
					rowObj.dimension=dimension;
					
				});
		
		account.push(rowObj);
	});

return account;
}
	

function filterDataByDimension(rowArray,dimension,index)	
{var i,j=0;

//console.log("filtering Data.."+dimension+".."+index);
var filteredData={};

	for(i=0;i<rowArray.length;i++)
		{
			if(dimension!=undefined && rowArray[i][index]!=undefined)
			{if(dimension.toLowerCase()==rowArray[i][index].toLowerCase())
				{
					//console.log(rowArray[i]);
					//JSON.parse(JSON.stringify(rowArray[i]));
					filteredData[j]=new Array();
					filteredData[j]=rowArray[i];
					
					j++;
				}
			}
		}
	filteredData=JSON.stringify(filteredData);
	//console.log("filteredData:::"+JSON.parse(filteredData));
	//console.log(filteredData);
	
	return JSON.parse(filteredData);
		
}
function getUniqueDimensionFromGAData(rowArray,index)
{//console.log(index);
	
	var i,
    len=rowArray.length,
    out=[],
    obj={};

			for (i=0;i<len;i++) {
			  obj[rowArray[i][index]]=0;
			 
			}
			for (i in obj) {
				
			  out.push(i.toUpperCase());
			}
	
	return out;

}


function isObjNull(obj)
{
	if(obj != null && obj != 'undefined' && obj != '')
		return false;
	else return true;
}

  function localtest()
  {
	  var len=localStorage.getItem("testData").length;
	  
  }
  
  function getCallDetails(dimValue,dimIndex)
  {
	  
  	//console.log('callid received'+dimValue+"dimIndex::"+dimIndex);
	  var callDetails=filterDataByDimension(jsonData,dimValue,dimIndex);
	  //console.log("callDetails.......:::"+callDetails);
	  
	  var newar=mapToArray(callDetails);
	  //console.log(newar);
	  
	  console.log("end");
	  return newar;
	  
  }
  function mapToArray(kv)
  {
	  var testAR=new Array();
	  
	  $j.each(kv, function(id,row)
			  {
		  testAR.push(row);
			  });
	  //console.log(testAR);
	  return testAR;
  }
  function createCallerMap(pivotBy)
  {
	  var callerTable=getUniqueDimensionFromGAData(jsonData,pivotBy);	
	  console.log("::::"+callerTable);
	  
	  //getCallDetails(pass connid)
	  //add data based on call id as key.
	  
	  $j.each(callerTable, function(id,row)
			  {	
			  callMap[row]=	getCallDetails(row,pivotBy);
			  });
	  console.log("caller map::"+callMap);
	  
	  return callMap;
  }
  function getCallerAccountDetails()
  {			createCallerMap(3);
	  var callerAccount=[];
	  
	  $j.each(callMap, function(id,row){
		  var temp=new Array();
		  
		  temp.push(row[0][0]);
		  temp.push(row[0][3]);
		  console.log(temp);
		  callerAccount.push(temp);
	  });
	  
	  return callerAccount;	
  }
  
  function filterDimension()
  {
	  var dimension=document.getElementById("dimension").value;
	  var filterData=document.getElementById("filterData").value;
	  if(filterData!="")
		  {
		  filterData=filterData.trim();		  
		  }
	  console.log(dimension+"::"+filterData);
	  
	  document.getElementById("table_call").innerHTML="";
	
		  return filterCallDataMap(filterData,dimension);
	 // return mapToArray((filterDataByDimension(jsonData,filterData,dimension)));
	  
  }
  function filterData(tablediv)
  {
	  console.log("filterData Called");
	  
      var lData='';
	  
	  var options = {'showRowNumber': true};
     lData = new google.visualization.DataTable();
     lData.addColumn('string', 'Account NO');
     lData.addColumn('string', 'Action');
     lData.addColumn('string', 'Agent Initials');
     lData.addColumn('string', 'Conn Id');
     lData.addColumn('string', 'IncomingANI/PhoneNo');
     lData.addColumn('string', 'TimeStamp');
     lData.addColumn('string', 'Total');
     var rData=filterDimension();console.log(rData)
     console.log("rData::"+rData.length);
     lData.addRows(rData);
     
     options['page'] = 'enable';
     options['pageSize'] = 20;
     options['pagingSymbols'] = {prev: 'prev', next: 'next'};
     options['pagingButtonsConfiguration'] = 'auto';
     console.log("lData:::::::::::"+lData);
     
     lTable = new google.visualization.Table(document.getElementById('table_div'));
     lTable.draw(createDataView("filterData",lData), options);
     lTempTable=lTable;
     filteredData=lData;
     google.visualization.events.addListener(lTable,'select', filterDataListner);
	  
  }

  
  function getCallerHistoryDetails()
  {return mapToArray(filterDataByDimension(jsonData,"Account Load",1));
	  	    	  
  }
  console.log("filter event data");
  function filterEventData()
  {
	  var dimension=document.getElementById("dimension").value;
	  var filterData=document.getElementById("filterData").value;
	  	console.log("filter data"+filterData+":::"+dimension);
	  if(filterData!="")
		  {
	  	data=getTableStructure("eventDetails");
		console.log("add rows");
		filteredData=mapToArray(filterDataByDimension(jsonData,filterData,dimension));
		data.addRows(filteredData);
		table = new google.visualization.Table(document.getElementById('table_div'));
		options['pageSize'] = null;
	      options['page'] = null;
	      document.getElementById("csvAction").setAttribute("onclick","DownloadJSON2CSV(filteredData)")
		table.draw(createDataView("eventDetails",data), options);
		
		  }
		else
			{
			if(activePage=="SB Event Detail")
				sbEventDetail();
			else
				drawTable();
				
			}
		  
  }
  console.log("filter data set");
  function getDataSet(index)
  { 
	  
console.log(index);	  dataset=getUniqueDimensionFromGAData(jsonData,index);
	  $j(".basicTypeahead").typeahead({
			source: dataset
			,items: 15
		});
	  $j('#filterData').typeahead({source: dataset});
	if(index!=1){
	  $j(".span4").attr("data-toggle","");
	  $j(".span4").attr("value","");
  
  }
  else
  { $j(".span4").attr("data-toggle","dropdown");
  }
	  
 
  }
  
  
  
  
  function filterCallDataMap(filterData,index)
  {var callerAccount=[];
  
	  $j.each(callMap, function(id,dimensions){
		  console.log(id);
		  console.log(dimensions);
		  $j.each(dimensions, function(cursor,row){
			  var temp=new Array();
			  console.log(cursor);
			  	if(filterData!="" && filterData!=undefined)
			  	{
			  		console.log(row[index]);	
			  		if(row[index].toLowerCase()==filterData.toLowerCase())
			  		{
			  			console.log(row[index]);
					  	//temp.push(row[0]);
				  		//temp.push(row[3]);
				  		
				  		temp.push.apply(temp,row);
				  		console.log(temp);
				  		callerAccount.push(temp);
				  	}
			 	}
			  	else
				 {
			  		temp.push.apply(temp,row);	
			  		callerAccount.push(temp);
				 }
			  	return false;
			  
		  });
	  });
	  filteredData=callerAccount;
	  return callerAccount;
  }
  
  function formatTimeStamp()
  {
	   
	   $j.each(jsonData,function(id,row){
		   
			   var timeStamp=new Date(parseInt(row[6]));
			   jsonData[id][6]=timeStamp.toString();
			   
			
	   });
	return jsonData;   
  }
  			
  function groupDataByDimension(dimIndex)
  {
	  var uniqueAct=getUniqueDimensionFromGAData(jsonData,dimIndex);
	  
	  $j.each(uniqueAct, function(id,row)
			  {	
			  callMap[row]=	getCallDetails(row,dimIndex);
			  });
	  console.log("caller map::"+callMap);
	  
	  var callerAccount=[];
	  
	  $j.each(callMap, function(id,row){
		  var temp=new Array();
		  
		  temp.push(id);
		  temp.push(row.length);
		  console.log(temp);
		  callerAccount.push(temp);
	  });
	  
	  filteredData=mapToArray(callerAccount);;
	  console.log("::"+filteredData);
	  
	  return callerAccount;
	  
  }
  function callActionHandler()
  {
	  var selection = table.getSelection();
	  var message = '';
	  var rowProp=new Array();
	  console.log(selection);
	  //alert(selection);
	  for (var i = 0; i < selection.length; i++) {
	    var item = selection[i];
	    	
	    	document.getElementById("table_call").innerHTML="";
	    	
	    	var handlerData=callMap[filteredData[item.row][0]];
	    	console.log(handlerData);
	    drawCallDetails(handlerData,"CallDetails");
	  }
	  
  }
  
  function pivotDataByDimension() {
	  var options = {'showRowNumber': true};
	  		data=getTableStructure("callAction");
	  		console.log("add rows");
      data.addRows(groupDataByDimension(pivotIndex));
    	  options['page'] = 'enable';
      options['pageSize'] = 50;
      options['pagingSymbols'] = {prev: 'prev', next: 'next'};
      options['pagingButtonsConfiguration'] = 'auto';

      table = new google.visualization.Table(document.getElementById('table_div'));
      table.draw(createDataView("callDataReport",data), options);
   	  google.visualization.events.addListener(table, 'select', callActionHandler);
   
  }
  
  
  
  
  function DownloadJSON2CSV(objArray)
  {
      var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
      console.log(array);
      var str = '';
       
      for (var i = 0; i < objArray.length; i++) {
          var line = '';
          for (var index in array[i]) {
              if(line != '') line += ','
           
              line += array[i][index];
          }
   
          str += line + '\r\n';
      }
   
      if (navigator.appName != 'Microsoft Internet Explorer')
      {
          window.open('data:text/csv;charset=utf-8,' + escape(str));
      }
      else
      {
          var popup = window.open('','csv','');
          popup.document.body.innerHTML = '<pre>' + str + '</pre>';
      }          
  }
 	function clearFilters()
 	{
 		document.getElementById('filterData').value='';
 		document.getElementById("csvAction").setAttribute("onclick","loadCSVPage()");
 		filteredData='';
 	}
 	
 	function clearData()
 	{
 		
 		document.getElementById("table_div").innerHTML="";
 		clearFilters();

 		
 	}
 	
 	
 	function agentActionSummary()
 	  {
 		  var uniqueAgent=getUniqueDimensionFromGAData(jsonData,2);
 		   var uniqueAction=getUniqueDimensionFromGAData(jsonData,1);
 		  var agentData=new Array();
 		  var agentArray=new Array();
 			$j.each(uniqueAgent, function(id,agentInitial){
 				var rowObj={};
 				var Send=0;
 				var CallConclusion=0;
 				var totalTime=0;
 				var thumbsUp=0;
 				var thumbsDown=0;
 				var accLoad=0;
 				rowObj["Agent Email"]=agentInitial;
 				$j.each(uniqueAction,function(k,action){
 					rowObj[action]=0;
 				});
							
 				var  agentDetails=filterDataByDimension(jsonData,agentInitial,2)
 					
 						$j.each(agentDetails,function(k,row){
 								
 							if(rowObj[row[1]]!=undefined)
 								{
 									
 								rowObj[row[1]]=rowObj[row[1]]+1;
 								}
 							
 						});
 				
 				agentData.push(rowObj);
 			
 			});
 			
 			var columnName= new Array();
 			for(i in agentData[0])
 				{
 				columnName.push(i);
 				}
 		  //console.log(columnName);
 		  
 		 $j.each(agentData,function (id, agentMap)
 		 {
 			agentArray.push(mapToArray(agentMap));
 		 });
 		  
 		  filteredData=agentArray;
 		 uniqueAgent=null;
 		uniqueAction=null;
 		agentData=null;
// 		if(filteredData)
// 		{	
// 			setTimeout("createNewRecord(dateFrom+'&AgentDetails',JSON.stringify(filteredData))","9000");
// 			
// 			createNewRecord(dateFrom+'&columnName',JSON.stringify(columnName));
// 		}
 		 return columnName;
 		  
 		  
 	  }
 	
 	function drawAgentDetail()
 	{
 		
 		
  		var columnData=agentActionSummary();
  		
  		 var data = new google.visualization.DataTable();
  		data.addColumn("string", "Agent Email");
        for(c in columnData)
  			{
        	if(columnData[c]!="Agent Email")
  			{data.addColumn("number", columnData[c]);
  			}
  			}
        
        console.log("Adding Rows");
  		data.addRows(filteredData);
	  
  options['pagingButtonsConfiguration'] = 'auto';

  table = new google.visualization.Table(document.getElementById('table_div'));
  table.draw(data, options);
  google.visualization.events.addListener(table, 'select', callActionHandler);
 		
 	}

  
  