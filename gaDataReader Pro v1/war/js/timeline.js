var accountMap={};
var callerAccount=[];
var avgLoadTime=0;
var finalData=[];
function createAccountLoadData()
{
	var accoutnList=getUniqueDimensionFromGAData(jsonData,0);
	//console.log(accoutnList);
	 $j.each(accoutnList, function(id,row)
			  {	
		 		accountMap[row]=getCallDetails(row,0);
			  });
	  //console.log("caller map::"+accountMap);
	 
	  
	 
}
function computeAvgLoadTime()
{
	createAccountLoadData();
	console.log("accountMap created::"+accountMap);
$j.each(accountMap, function(id,dimensions){
	  var records=0;
	  var total=0;
	  	var avg=0;
	  	var temp=new Array();
	  $j.each(dimensions, function(cursor,row){
		  			
		  		if(row[4]!="NaN"&& row[4]!="null")
		  		{
		  			//console.log(row[4]);
				  	total+=Number(row[4]);
				  	records=records+1;
			  	}
		  		if(total>0 && total!="NaN")
		  			{	
		  				avg=(total/records);
		  				//console.info("avg::",avg);
		  				//console.info("records::",records);
		  			}
		  		
	  });
	  			//console.log(dimensions[0][0]+"avg::"+avg.toString());
		  		temp.push(dimensions[0][0],parseInt(avg));	
		  		//console.info("avegaged data::"+temp);
		  		callerAccount.push(temp);
});
console.log("compute end");
filteredData=callerAccount;
return callerAccount;
}

function loadTimeSelectHandler() {
	  console.log("select handler");
	  var selection = table.getSelection();
	  var message = '';
	  var rowProp=new Array();
	  console.log(selection);
	  for (var i = 0; i < selection.length; i++){
	    var item = selection[i];
	    	console.log("item row"+item.row);
	    	var handlerData=getAccountLoadHistory(item.row);
	    	console.log(handlerData);
	    	drawCallDetails(handlerData,"AvgLoadTime");
	    	$j('#myModalDetails').modal('show');
	    
	    if (item.row != null && item.column != null) {
	    	var str = data.getFormattedValue(item.row, item.column);
	     
	      message += '{row:' + item.row + ',column:' + item.column + '} = ' + str + '\n';
	    } else if (item.row != null) {
	      var str = data.getFormattedValue(item.row, 1);
	      
	      message += '{row:' + item.row + ', (no column, showing first)} = ' + str + '\n';
	    } else if (item.column != null) {
	    	 
	    	var str = data.getFormattedValue(0, item.column);
	      message += '{(no row, showing first), column:' + item.column + '} = ' + str + '\n';
	    }
	    
	  }
	  if (message == '') {
	    message = 'nothing';
	  }
	  
}


function getAccountLoadHistory(rowIndex)
{var selectedAcct=filteredData[rowIndex][0];
	
  var acctDetails=accountMap[selectedAcct];
	
	  return acctDetails;
}

function accountLoadschema()
{
	var loadSchema=new google.visualization.DataTable();
	
	loadSchema.addColumn('string', 'Account No.');
	loadSchema.addColumn('number', 'Avg. LoadTime');
	console.log(loadSchema);
	return loadSchema;
	
}

function filterAccountLoad(tablediv)
{
	  console.log("filterAccountLoad Called");
	  
    var lData='';
	  
	  var options = {'showRowNumber': true};
   
   lData=getTableStructure("loadTime");
   var rData=getAccountData();console.log(rData)
   
  console.log(lData);
   lData.addRows(rData);
   
   options['page'] = 'enable';
   options['pageSize'] = 20;
   options['pagingSymbols'] = {prev: 'prev', next: 'next'};
   options['pagingButtonsConfiguration'] = 'auto';
   console.log("lData:::::::::::"+lData);
   
   lTable = new google.visualization.Table(document.getElementById('table_div'));
   lTable.draw(createDataView("loadTime",lData), options);
   lTempTable=lTable;
   
   google.visualization.events.addListener(lTable,'select', accountDataListner);
	  
}

function getAccountData()
{
	  var dimension=document.getElementById("dimension").value;
	  var filterData=document.getElementById("filterData").value;
	  if(filterData!="")
		  {
		  filterData=filterData.trim();		  
		  }
	  console.log(dimension+"::"+filterData);
	  
	  document.getElementById("table_call").innerHTML="";
	
		  return filterAccountDataMap(filterData,dimension);
	 // return mapToArray((filterDataByDimension(jsonData,filterData,dimension)));
	  
	  
	  
}



function filterAccountDataMap(filterData,index)
{var tempArray=[];

	  $j.each(callerAccount, function(id,dimensions){
		  
		  $j.each(dimensions, function(cursor,row){
			  var temp=new Array();
			  //console.log(cursor);
			  var lArray=row.split("|");
			  if(lArray[1]==undefined)
				  {
				  lArray[1]="No Acct.";
				  }
			  	if(filterData!="" && filterData!=undefined && lArray[0]!=undefined)
			  	{
			  		
			  		console.log(lArray[0]+":::::"+lArray[1]+":::"+filterData);
			  		if(lArray[0].toLowerCase()==filterData.toLowerCase()||lArray[1].toLowerCase()==filterData.toLowerCase())
			  		{
			  				
				  		tempArray.push(dimensions);
				  		
				  	}
			 	}
			  	else
				 {
			  		tempArray.push(dimensions);
				 }
			  	return false;
			  
		  });
	  });
	  filteredData=tempArray;
	  return tempArray;
}

function accountDataListner()
{  
	   console.log("filter select handler");
	   var selection = lTempTable.getSelection();
	  var message = '';
	  var rowProp=new Array();
	  console.log(selection.length);
	  for (var i = 0; i < selection.length; i++) {
	    var item = selection[i];
	    
		drawCallDetails(getAccountLoadHistory(item.row),"AvgLoadTime");
	    //drawCallDetails(getCallDetails(filteredData.getFormattedValue(item.row, 3)),filteredData.getFormattedValue(item.row, 3));
	   console.log("test...");
	    
	    
	  }
	  if (message == '') {
	    message = 'nothing';
	  }
	 console.log(message);
}
function validateAccountLoadData()
{
	
	$j.each(callerAccount, function(id,dimensions){
		
		if(dimensions[1]!=0 && dimensions[1]!='NaN' && dimensions[1]<=15000)
			{
			finalData.push(dimensions);
			}
		
	});
	
	return finalData;
}
function calcAvgLoadTime()
{
	var sum=0;
$j.each(finalData, function(id,dimensions){
		
				sum=sum+parseInt(dimensions[1]);
				
	});
console.log(sum);
avgLoadTime=sum/finalData.length;
	
	}



function filterAccountLoadData()
{
	var accountData=[];
	$j.each(jsonData, function(id,dimensions){
		  
		  $j.each(dimensions, function(cursor,row){	
			  
				  if(row[1]=="Account Load")
					  {
					accountData.push(row);  
					  }
			  });
		  });
	console.log(filterAccountLoadData());
	jsonData=accountData;
	console.log("Filtered Data for accounts");
	return jsonData;
}
