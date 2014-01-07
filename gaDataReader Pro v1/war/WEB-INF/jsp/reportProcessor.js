 
var callMap={};
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
	

function filterDataByDimension(rowArray,dimensionVal,index)	
{var i,j=0;

//alert("filtering Data.."+dimension+".."+index);
var filteredData={};

	for(i=0;i<rowArray.length;i++)
		{
			if(dimensionVal==rowArray[i][index])
				{
					console.log(rowArray[i]);
					//JSON.parse(JSON.stringify(rowArray[i]));
					filteredData[j]=new Array();
					filteredData[j]=rowArray[i];
					
					j++;
				}
		}
	filteredData=JSON.stringify(filteredData);
	console.log("filteredData:::"+JSON.parse(filteredData));
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
				
			  out.push(i);
			}
	
	return out;

}


function isObjNull(obj)
{
	if(obj != null && obj != 'undefined' && obj != '')
		return false;
	else return true;
}
function createAccountTable(obj)
{ var i;
	var tableHTML="<table id='table_details' style='width:100%;text-align: center'>";
	tableHTML+="<tr><th>Dimension</th>";
	tableHTML+="<th>Send Count</th>";
	tableHTML+="<th>CC Count</th>";
	tableHTML+="<th>Account Load</th>";
	tableHTML+="<th>Total Time</th>";
	tableHTML+="</tr><tbody align='center'>";
	console.log("adding Data");
	for(i=0;i<obj.length;i++)
		{var temp=obj[i];
		tableHTML+="<tr>";
		tableHTML+="<td ><a href='#' onclick='getAgentData(this.innerHTML.trim(),2,4);'>"+temp.label+"</a></td>";
		tableHTML+="<td>"+temp.sendCount+"</td>";
		tableHTML+="<td>"+temp.ccCount+"</td>";
		tableHTML+="<td>"+temp.accLoad+"</td>";
		tableHTML+="<td>"+temp.milliSec+"</td>";
		tableHTML+="</tr>";	
		}
	tableHTML+="</tbody>";
	tableHTML+="<table>";
	
	
		return tableHTML;
}
function createAgentTable(obj)
{ var i;
	var tableHTML="<table id='table_details' style='width:100%;text-align: center'>";
	tableHTML+="<tr><th>Agent Initial</th>";
	tableHTML+="<th>Send Count</th>";
	tableHTML+="<th>CC Count</th>";
	tableHTML+="<th>Account Load</th>";
	tableHTML+="<th>Total Time</th>";
	tableHTML+="</tr><tbody align='center'>";
	console.log("adding Data");
	for(i=0;i<obj.length;i++)
		{var temp=obj[i];
		tableHTML+="<tr>";
		tableHTML+="<td ><a href='#' onclick='getEventDescriptionForCategory(JSON.parse(rowData),this.innerHTML.trim(),2,4);'>"+temp.label+"</a></td>";
		tableHTML+="<td>"+temp.sendCount+"</td>";
		tableHTML+="<td>"+temp.ccCount+"</td>";
		tableHTML+="<td>"+temp.accLoad+"</td>";
		tableHTML+="<td>"+temp.milliSec+"</td>";
		tableHTML+="</tr>";	
		}
	tableHTML+="</tbody>";
	tableHTML+="<table>";
	
	
		return tableHTML;
}
  function localtest()
  {
	  var len=localStorage.getItem("testData").length;
	  
  }
  
  function getCallDetails(connId,index)
  {console.log('callid received'+connId);
	  var callDetails=filterDataByDimension(jsonData,connId,index);
	  console.log("callDetails.......:::"+callDetails);
	  
	  var newar=mapToArray(callDetails);
	  console.log(newar);
	  
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
	  console.log(testAR);
	  return testAR;
  }
  function createCallerMap()
  {
	  var callerTable=getUniqueDimensionFromGAData(jsonData,3);	
	  console.log("::::"+callerTable);
	  
	  //getCallDetails(pass connid)
	  //add data based on call id as key.
	  
	  $j.each(callerTable, function(id,row)
			  {	
			  callMap[row]=	getCallDetails(row,3);
			  });
	  console.log("caller map::"+callMap);
	  
	  return callMap;
  }
  function getCallerAccountDetails()
  {			createCallerMap();
	  var callerAccount=[];
	  
	  $j.each(callMap, function(id,row){
		  var temp=new Array();
		  temp.push(id);
		  temp.push(row[0][6]);
		  console.log(temp);
		  callerAccount.push(temp);
	  });
	  console.log(callerAccount);
	  return callerAccount;
  }
  
  function filterDimension()
  {
	  var dimension=document.getElementById("dimension").value;
	  var filterData=document.getElementById("filterData").value;
	  console.log(dimension+"::"+filterData);
	  if(filterData=="")
		  return jsonData;
	  else
	  return mapToArray((filterDataByDimension(jsonData,filterData,dimension)));
	  
	  
  }
  function filterData()
  {
	  var lTable='';
      var lData='';
	  
	  var options = {'showRowNumber': true};
     lData = new google.visualization.DataTable();
     lData.addColumn('string', 'Account NO');
     lData.addColumn('string', 'Action');
     lData.addColumn('string', 'Agent Initials');
     lData.addColumn('string', 'ConnID');
     lData.addColumn('string', 'IncomingANI/PhoneNo');
     lData.addColumn('string', 'Details');
     lData.addColumn('string', 'Total');
     var rData=filterDimension() ;
     lData.addRows(rData);
     
     options['page'] = 'enable';
     options['pageSize'] = 20;
     options['pagingSymbols'] = {prev: 'prev', next: 'next'};
     options['pagingButtonsConfiguration'] = 'auto';
console.log("lData:::::::::::"+lData);
     lTable = new google.visualization.Table(document.getElementById('table_div'));
     lTable.draw(createDataView("CallDetails",lData), options);
     google.visualization.events.addListener(lTable,'select', selectHandler);
	  
  }
  
  function groupAccountLoadData()
  {
	  
  }
  
  
