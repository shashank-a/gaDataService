	 var lTable='';
	 var lTempTable='';
     var lData='';
     var filteredData='';
     
     
	function selectHandler() {
    	  console.log("select handler");
    	  var selection = table.getSelection();
    	  
    	  var message = '';
    	  var rowProp=new Array();
    	  console.log(selection);
    	  for (var i = 0; i < selection.length; i++) {
    	    var item = selection[i];
    	    	console.log(item.row);
    	    	var handlerData=filterDimension();
    	    	console.log(handlerData);
    	    drawCallDetails(getCallDetails(handlerData[item.row][3],3),handlerData[item.row][3]);
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
      
      function drawCallDetails(callData,schema)
      {
    	  		console.log(callData+":::"+schema);
    	 
    	  		createGATable(callData,schema);
    	  		console.log("after Call ga Table");
      }
     
  	function createGATable(tableJSON,schema)
  	{
  		lTable = new google.visualization.DataTable();
        lData=getTableStructure(schema);
        lData.addRows(tableJSON);
        
        
        lTable = new google.visualization.Table(document.getElementById('table_call'));
        lTable.draw(createDataView(schema,lData), options);
        
        
         	 
       }
  	function getTableStructure(dimension)
    {lData="";
  		
   	 lData = new google.visualization.DataTable();
   	if(dimension=="agentDetail")
		 {
		console.info("in callList",dimension);
		lData.addColumn('string', 'Account NO');
		lData.addColumn('string', 'ConnId');
		return lData;
		 
		 }
   	else if(dimension=="callList")
   		 {
   		console.info("in callList",dimension);
   		lData.addColumn('string', 'Account NO');
   		lData.addColumn('string', 'ConnId');
   		return lData;
   		 
   		 }
   	else if(dimension=="callAction")
		 {
		console.info("in callList",dimension);
		lData.addColumn('string', 'Action');
		lData.addColumn('number', 'Event Total');
		return lData;
		 
		 }
   	else if(dimension=="eventDetails")
	 {
   		console.info("in eventDetails",dimension);
   		lData.addColumn('string', 'Account NO');
   		lData.addColumn('string', 'Action');
   		lData.addColumn('string', 'Agent Initials');
   		lData.addColumn('string', 'Time Stamp');
   		lData.addColumn('string', 'Total');
   		return lData;
	 
	 }
   	else if(dimension=="loadTime")
		 {console.info("inside loadTime",dimension);
   		lData.addColumn('string', 'Account No.');
   		lData.addColumn('number', 'Avg. LoadTime');
   		return lData;
		 }
   	else if(dimension=="AvgLoadTime")
   		{console.info("in AvgLoadTime",dimension);
   		lData.addColumn('string', 'Account NO');
   		lData.addColumn('string', 'Action');
   		lData.addColumn('string', 'Agent Initials');
   		lData.addColumn('string', 'Time Stamp');
   		lData.addColumn('string', 'Load Time');
   		lData.addColumn('string', 'Total');
   		return lData;
   		}
   	 
   	else if(dimension=="seven")
   		{
   		console.info(" in..else",dimension);
   		lData.addColumn('string', 'Account NO');
   		lData.addColumn('string', 'Action');
   		lData.addColumn('string', 'Agent Initials');
   		lData.addColumn('string', 'ConnId');
   		lData.addColumn('string', 'IncomingANI/PhoneNo');
   		lData.addColumn('string', 'Time Stamp');
   		lData.addColumn('string', 'Event Details');
   		lData.addColumn('string', 'Total');
   		}
   	else if(dimension=="v2App")
		{
		console.info("in    v2App",dimension);
		lData.addColumn('string', 'Account NO');
		lData.addColumn('string', 'Action');
		lData.addColumn('string', 'Agent Initials');
		lData.addColumn('string', 'OB Number');
		lData.addColumn('string', 'IB ConnId');
		lData.addColumn('string', 'OB ConnId');
		lData.addColumn('string', 'TimeStamp');
		lData.addColumn('string', 'Total');
		}
   	else{
   		console.info(" in..else",dimension);
   		lData.addColumn('string', 'Account NO');
   		lData.addColumn('string', 'Action');
   		lData.addColumn('string', 'Agent Initials');
   		lData.addColumn('string', 'ConnId');
   		lData.addColumn('string', 'IncomingANI/PhoneNo');
   		lData.addColumn('string', 'Time Stamp');
   		lData.addColumn('string', 'Total');
   		
   		}
   	console.log(lData);
   	return lData;
   	 	 	
    }
  	function createDataView(schema,lRows)
  	{
  		var dataView1 = new google.visualization.DataView(lRows);
  		if(schema=="CallHistory")
  			{
  			dataView1.setColumns([0,1,2,4]);
  			}
  		if(schema=="CallDetails")
			{
			dataView1.setColumns([0,1,2,3,4,5]);	
			}
  		if(schema=="filterData")
		{
		dataView1.setColumns([0,6]);
		}
  		if(schema=="callDataReport" || schema=="loadTime")
		{
		dataView1.setColumns([0,1]);
		}
  		if(schema=="eventDetails")
		{
		dataView1.setColumns([0,1,2,3]);
		}
  		if(schema=="Account Load")
		{
		dataView1.setColumns([0,1,2,3,4,5]);
		}
  		if(schema=="AllDetails")
		{
  			dataView1.setColumns([0,1,2,3,4,5]);
  			
		}
  		if(schema=="seven")
		{
  			dataView1.setColumns([0,1,2,3,4,5,6]);
  			
		}
  		
  		if(schema=="v2App")
		{
  			dataView1.setColumns([0,1,2,3,4,5,6]);
  			
		}
  		console.log(dataView1);
  			return dataView1;
  	}
  	function getTableOptions()
  	{
  		var options = {'showRowNumber': true};
  		options['page'] = 'enable';
        options['pageSize'] = 20;
        options['pagingSymbols'] = {prev: 'prev', next: 'next'};
        options['pagingButtonsConfiguration'] = 'auto';
        return options;
  	}
  	
  	
   function filterDataListner()
   {  
	   console.log("filter select handler");
	   var selection = lTempTable.getSelection();
 	  var message = '';
 	  var rowProp=new Array();
 	  console.log(selection.length);
 	  for (var i = 0; i < selection.length; i++) {
 	    var item = selection[i];
 	   
 	    drawCallDetails(getCallDetails(filteredData.getFormattedValue(item.row, 3)),"");
 	   console.log("test...");
 	    if (item.row != null && item.column != null) {
 	    	var str = filteredData.getFormattedValue(item.row, item.column);
 	     
 	      message += '{row:' + item.row + ',column:' + item.column + '} = ' + str + '\n';
 	    } else if (item.row != null) {
 	      var str = filteredData.getFormattedValue(item.row, 1);
 	      
 	      message += '{row:' + item.row + ', (no column, showing first)} = ' + str + '\n';
 	    } else if (item.column != null) {
 	    	 
 	    	var str = filteredData.getFormattedValue(0, item.column);
 	      message += '{(no row, showing first), column:' + item.column + '} = ' + str + '\n';
 	    }
 	    
 	  }
 	  if (message == '') {
 	    message = 'nothing';
 	  }
 	 console.log(message);
   }
    
   
  	
  	
        
  	