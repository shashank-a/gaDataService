
var w_Data;
var w_Temp;
var filteredData="";
var matcherFlag="";

		

self.addEventListener('message', function(e) {
	
	//agentActionSummary();
	
	
//	var data = e.data;
//	var keys = Object.keys(e.data);
//	for(i=0;i<keys.length;i++){
//		self.postMessage(JSON.stringify(data[keys[i]]));
//		data[keys[i]] = 'shashank'+i;
//	}
//	self.postMessage(e.data);
	var reqObj=e.data;
		w_Data=reqObj.data;
		matcherFlag=reqObj.matcher;
	
	if(reqObj.report=="sendDetails")
		{
		w_Temp=filterDataByDimensionWorker(w_Data,reqObj.filter,reqObj.filterIndex);
		
		self.postMessage(w_Temp);
		}
	
	//self.postMessage(getUniqueDimensionFromWorker(w_Data,2));
	
  
}, false);




function getUniqueDimensionFromWorker(rowArray,index)
{//console.log(index);
	
	var i,
    len=rowArray.length,
    out=[],
    obj={};
		
			for (i=0;i<len;i++) 
			{
				
				//if((rowArray[i][index]!="NULL")||(rowArray[i][index]!="null"))
				//{
							if(index==2)
							{		uniqueAgent=(rowArray[i][index]).toUpperCase();
									obj[uniqueAgent]=0;
							}
							else
								obj[rowArray[i][index]]=0;
								
				//}
			 
			}
			for (i in obj) {
				out.push(i);
			}
			
		
	return out;

}

	function agentActionSummary()
	  {
		  var uniqueAgent=getUniqueDimensionFromWorker(w_Data,2);
		   var uniqueAction=getUniqueDimensionFromWorker(w_Data,1);
		  var agentData=new Array();
		 var agentArray=new Array();
		uniqueAction.push("Account Load (IB)");
		uniqueAction.push("Account Load (NOID)");
		uniqueAction.push("Account Load (CI)");
		 uniqueAction.push("CallConclusion (IB)");
		uniqueAction.push("CallConclusion (NOID)");
		uniqueAction.push("CallConclusion (CI)");
		uniqueAction.push("Send (IB)");
		uniqueAction.push("Send (NOID)");
		uniqueAction.push("Send (CI)");
		uniqueAction.sort();
		
			
		for(var id=0;id<uniqueAgent.length; id+=1)
			{	
				var rowObj={};
				var Send=0;
				var CallConclusion=0;
				var totalTime=0;
				var thumbsUp=0;
				var thumbsDown=0;
				var accLoad=0;
				rowObj["Agent Email"]=uniqueAgent[id];
				
					for (var k=0;k<uniqueAction;k+=1)
					{
					   rowObj[uniqueAction[k]]=0;
					}
						
					var  agentDetails=filterDataByDimensionWorker(w_Data,uniqueAgent[id],2);
						
					for(var k=0;k<agentDetails.length;k++)
						{
							var row =agentDetails[k];
							if(rowObj[row[1]]!=undefined)
								{
								rowObj[row[1]]=rowObj[row[1]]+1;
								
								if(/us-cs-telephony/.test(row[3]))
										{
											if(row[1]=="CallConclusion")
												rowObj["CallConclusion (IB)"]=rowObj["CallConclusion (IB)"]+1;
											if(row[1]=="Send")
												rowObj["Send (IB)"]=rowObj["Send (IB)"]+1;
											if(row[1]=="Account Load")
												{rowObj["Account Load (IB)"]=rowObj["Account Load (IB)"]+1;
												
												}
										}
								
								else if(row[3]=="Fetch")
									{
									if(row[1]=="CallConclusion")
										rowObj["CallConclusion (NOID)"]=rowObj["CallConclusion (NOID)"]+1;
									if(row[1]=="Send")
										rowObj["Send (NOID)"]=rowObj["Send (NOID)"]+1;
									if(row[1]=="Account Load")
											{
												rowObj["Account Load (NOID)"]=rowObj["Account Load (NOID)"]+1;
											}
								}
								else
									{
									if(row[1]=="CallConclusion")
									rowObj["CallConclusion (CI)"]=rowObj["CallConclusion (CI)"]+1;
								if(row[1]=="Send")
									rowObj["Send (CI)"]=rowObj["Send (CI)"]+1;
								
									}
								if(row[1]=="Account Load-SBChat"||row[1]=="Account Load-eventToTalk"||row[1]=="Account Load-repeat")
									{
										rowObj["Account Load (CI)"]=rowObj["Account Load (CI)"]+1;
										rowObj["Account Load"]=rowObj["Account Load"]+1;
									}
									
								}
							
						}
				//console.log(rowObj);
				agentData.push(rowObj);
			
			}
			
			var columnName= new Array();
			for(i in agentData[0])
				{
 			
 					columnName.push(i);
 			
				}
		  
		 
		 for(agentMap in agentData)
			 {
			 agentArray.push(mapToArrayWorker(agentMap));
			 }
		  
		  filteredData=agentArray;
		 uniqueAgent=null;
		uniqueAction=null;
		agentData=null;
//		if(filteredData)
//		{	
//			setTimeout("createNewRecord(dateFrom+'&AgentDetails',JSON.stringify(filteredData))","9000");
//			
//			createNewRecord(dateFrom+'&columnName',JSON.stringify(columnName));
//		}
		 return columnName;
		  
		  
	  }
	

function filterDataByDimensionWorker(rowArray,dimension,index)	
{var i,j=0;

//console.log("filtering Data.."+dimension+".."+index);
var filteredData={};
dimension=dimension.replace("+","");
var patt = new RegExp(dimension.toLowerCase());

	for(i=0;i<rowArray.length;i++)
		{
			if(dimension!=undefined && rowArray[i][index]!=undefined)
			{
				//if(dimension.toLowerCase()==rowArray[i][index].toLowerCase())
				if(matcherFlag=="true")
					{
						if(patt.test(rowArray[i][index].toLowerCase()))
						{
							//console.log(rowArray[i]);
							//JSON.parse(JSON.stringify(rowArray[i]));
							filteredData[j]=new Array();
							filteredData[j]=rowArray[i];
							
							j++;
						}
						
					}
				else
					{
					if(rowArray[i][index].toLowerCase()==dimension.toLowerCase())
					{
						//console.log(rowArray[i]);
						//JSON.parse(JSON.stringify(rowArray[i]));
						filteredData[j]=new Array();
						filteredData[j]=rowArray[i];
						
						j++;
					}
					}
				
			}
		}
	filteredData=JSON.stringify(filteredData);
	//console.log("filteredData:::"+JSON.parse(filteredData));
	//console.log(filteredData);
	
	return JSON.parse(filteredData);
		
}

function mapToArrayWorker(kv)
{
	  var testAR=new Array();
	  
	  for(obj in kv)
		  {
		  testAR.push(obj);
		  }
	  
	  //console.log(testAR);
	  return testAR;
}

function getCallDetails(jsonData,dimValue,dimIndex)
{
	  
	//console.log('callid received'+dimValue+"dimIndex::"+dimIndex);
	  var callDetails=filterDataByDimensionWorker(jsonData,dimValue,dimIndex);
	  //console.log("callDetails.......:::"+callDetails);
	  
	  var newar=mapToArrayWorker(callDetails);
	  //console.log(newar);
	  
	 // console.log("end");
	  return newar;
	  
}


function filterDimension(dimension,data)
{
	  var dimension=document.getElementById("dimension").value;
	  var filterData=document.getElementById("filterData").value;
	  if(filterData!="")
		  {
		  filterData=filterData.trim();		  
		  }
	  
	  
	  document.getElementById("table_call").innerHTML="";
	
		  return filterCallDataMap(filterData,dimension);
	  
}
function escapeFilteredData(arrayObject)
{
	var escapedArray=new Array();
	var k=w_Temp;
	
	for(x=0;x<12;x++)
		{
		var row=arrayObject[x];
			row[0]=row[0].replace(/\,/g,"");
			row[6]=row[6].replace(/\,/g,"");
			postMessage(k.length);
			escapedArray.push(row);
		
		}
	
	return arrayObject;
}






