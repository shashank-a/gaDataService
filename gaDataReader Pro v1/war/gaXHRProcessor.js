function processGARequest(parms,url) {
	
	var isSynchronous = false;
	if (window.XMLHttpRequest) {
		request = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		request = new ActiveXObject("Msxml2.XMLHTTP");
	} else {
		request = new ActiveXObject("Microsoft.XMLHTTP");
	}
	request.onreadystatechange = function() {
		if (request.readyState == 4) {
			console.log("Successful Request");
						$j('#loadingmessage').hide(); 
			
			var responseText = request.responseText;

			rowData=responseText;
			jsonData=JSON.parse(rowData);
			filteredData=jsonData;
						document.getElementById("msg").innerHTML="######Total Records "+jsonData.length;
//			worker.postMessage(new createCustomObject('sendDetails',JSON.parse(localStorage.getItem("rowdata")),"Account Load",1,'false','sendDetails'));
			//test work heree
			}
		}
			request.open('POST', url, true);
			request.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			
			$j('#loadingmessage').show(); 
		document.getElementById('tablechart').innerHTML="";
			request.send(parms);
		}
		
		function fetchXHRData()
		{
			var key="";
			var date=document.getElementById('dateFrom').value;
			
			if(app=="SwitchBoard")
	        {key="GaDataObject_SBLIVE_"+date.replace(/-/g,'')+"_Cat|Act|Lab|cVal1|cVal2|cVal3|cVal4_json";}
	    	if(app=="v2")
	    	{key="GaDataObject_V2Outbound_"+date.replace(/-/g,'')+"_"+date.replace(/-/g,'');}

			processGARequest('',"getDSData.do?key="+key);
		
		}