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
			
			var responseText = request.responseText;
			console.log("responseData::"+responseText);
			
			
			
			
			//test work heree
			}
		}
			request.open('POST', url, true);
			request.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			request.setRequestHeader("Content-length", parms.length);
			request.setRequestHeader("Connection", "close");
			request.send(parms);
		}