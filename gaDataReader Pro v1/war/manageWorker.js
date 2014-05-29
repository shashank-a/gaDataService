var worker = new Worker('respond.js');

function createCustomObject(report,jData,filter,filterIndex,csvData,matcher){
	this.report=report;
	this.data=jData;
	this.filter=filter;
	this.filterIndex=filterIndex;
	this.csvData=csvData;
	this.matcher=matcher;
};
var matcherFlag="true";

worker.addEventListener('message', function(e) {
	  // Log the workers message.
	  console.log("response::"+e.data);
	  filteredData=mapToArray(e.data);
	  console.log(filteredData.length);
	  //filteredData=e.data;
	  //document.getElementById("tablechart").innerHTML=filteredData;
	  console.log("report type"+e.report);
		$j('#loadingmessage').hide(); 
	  if(filteredData!=undefined && filteredData.length!=0)
	  {
		document.getElementById("msg").innerHTML="######Total Records "+filteredData.length;	  
		
	  generateTable(filteredData,filteredData.length);
	  if(e.report=='sendDetails')
		  {
		  generateTable(filteredData,100000);
		  }
		}else
		{
		document.getElementById('tablechart').innerHTML="";
		document.getElementById("msg").innerHTML="######Total Records "+filteredData==undefined?"0":filteredData;	  
		}

	  
}, false);


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

function w_filterData()
{   
	console.log("filter");
	var dimIndex=document.getElementById("dimension").value;
	var dimString=document.getElementById("filterData").value;
	if(jsonData!='undefined' && jsonData!='' && dimString!='')
	{$j('#loadingmessage').show(); 
	console.log("matcherFlag"+matcherFlag);
		worker.postMessage(new createCustomObject('sendDetails',filteredData,dimString,dimIndex,'false',matcherFlag));
	}else
	{console.log("Working on local Data");
		worker.postMessage(new createCustomObject('sendDetails',JSON.parse(localStorage.getItem("rowdata")),dimString,dimIndex,'false',matcherFlag));

	}
		
}
function setCallFilter(id)
{
	clearData();
	document.getElementById("filterData").value=id;
	document.getElementById("dimension").value=3;
	console.log("added values");
	w_filterData();
	
}

function generateTable(data,range){
	var columnName=[];
	columnName.push('AccountNumber');
	columnName.push('Action');
	columnName.push('Label');
	columnName.push('ConnID');
	columnName.push('ANI');
	columnName.push('TimeStamp');
	columnName.push('Details');
	columnName.push('Total');
	
   	var outputvalue = data; 
	var div = document.getElementById('tablechart');
	div.innerHTML = "";
	var table = document.createElement('table');
	table.setAttribute('border','1');
	table.setAttribute('class','tablesorter');
	table.setAttribute('id','gatable');		
	var thead = document.createElement('thead');
	var col_tr = document.createElement('tr');		
	
	if(typeof outputvalue =="undefined" ){
		var th = document.createElement('th');
		var text = document.createTextNode('No result Found');
		th.appendChild(text);
		col_tr.appendChild(th);
		thead.appendChild(col_tr);
		table.appendChild(thead);
		div.appendChild(table);
		$('#totalcountrep').text(" No results found");
	}
	else{
		var text;
		var th;
		var linkIndex;
		 
		th= document.createElement('th');
		text = document.createTextNode('Index\u00A0\u00A0\u00A0\u00A0');
		th.appendChild(text);
		col_tr.appendChild(th);
//          if(outputvalue.customheaders)
//        	  columnName = outputvalue.customheaders;
//          else
//        	  columnName = outputvalue.columnHeaders
		for(var i=0,j; j=columnName[i];++i){
			console.log(j);
			
			if(columnName[i]=="ConnID")
			{ 	console.log("ConnID setting style");
				th = document.createElement('th');
				th.setAttribute("type","link");
				text = document.createTextNode(j+'\u00A0\u00A0\u00A0\u00A0');
				th.appendChild(text);
				col_tr.appendChild(th);
				linkIndex=i;
			}
			else{
				th = document.createElement('th');
				//th.setAttribute("class","header");
				text = document.createTextNode(j+'\u00A0\u00A0\u00A0\u00A0');
				th.appendChild(text);
				col_tr.appendChild(th);
			}
		}			
		columnName=null;
		thead.appendChild(col_tr);
		table.appendChild(thead);
		var tbody = document.createElement('tbody');
		var td;
		var tr;
		var text_1;			
       if(range)
    	   for(var k=0, l; (l=outputvalue[k]) && (k<range); ++k){
				tr = document.createElement('tr');
				td = document.createElement('td');
				text_1 = document.createTextNode(k+1);
				td.appendChild(text_1);
				tr.appendChild(td);   				
				for(var m=0, n; n=l[m]; ++m){
					td = document.createElement('td');
					
					if(m==linkIndex)
						{	console.log("pass");
							//text_1 = document.createTextNode(n+"test");
							text_1=document.createElement('p');
							text_1.innerHTML="<a href='#' onclick='setCallFilter(this .innerHTML)'>"+n+"</a>";
							text_1.setAttribute('title',"click to see call details");
							console.log(text_1);	
						}
					else
						{
						text_1 = document.createTextNode(n);
						
						}
					td.appendChild(text_1);
					tr.appendChild(td);
				}
				tbody.appendChild(tr);
			}
       else
		for(var k=0, l; l=outputvalue; ++k){
			tr = document.createElement('tr');
			td = document.createElement('td');
			text_1 = document.createTextNode(k+1);
			td.appendChild(text_1);
			tr.appendChild(td);
			for(var m=0, n; n=l[m]; ++m){
				td = document.createElement('td');
				text_1 = document.createTextNode(n);
				td.appendChild(text_1);
				tr.appendChild(td);
			}
			tbody.appendChild(tr);
		}
		   table.appendChild(tbody);			  
		   div.appendChild(table);
		   $j('#gatable').tablesorter();
	//	   if(outputvalue.length)
//		$('#totalcountrep').text("total no. of results "+outputvalue.rows.length +" / "+outputvalue.totalResults+ "Date Range: StartDate: "+outputvalue.query['start-date']+" EndDate:  "+ outputvalue.query['end-date']);
	}
}
