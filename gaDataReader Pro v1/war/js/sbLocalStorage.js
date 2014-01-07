var localStoreFlag=false;

function setStorageFlag()
{
	if(typeof(Storage)!=="undefined")
	{	
		localStoreFlag=true;
		
	}
	else
	{
		localStoreFlag=false;
	}
}


var appVariables={}
var requestParamData={}
var requestAttrData={}

function store(key,Obj)
{console.log("<br/>store locally<br/>");
	var jsonString=JSON.stringify(Obj);
	localStorage[key]=JSON.stringify(jsonString);
	
}

function loadData(key)
{var lString;var jString;var jObj;
	console.log("Loading Data.from localstorage....."+key);
	 lString=localStorage[key];
	 jString=JSON.parse(lString);

	 jObj=eval('(' +jString + ')');

	
	return jObj;
}


function checkLocalStorage(keyName)
{
	var testO=localStorage[keyName];
	if(testO!=null && testO!='undefined' && testO!='' )
		{
		console.log ("LocalStorage Present");
		return true;
		}
	
	else{
		return null;
	}
}
function parseJSON(obj)
{
	var testObj=JSON.parse(obj);
	var jsonObj=eval("("+obj + ")" );
		
	return jsonObj;
}
 function loadObjMessageSystem(ObjMsgSys){
	 
	 var tempObj=loadData(ObjMsgSys);
	 
		 
	 ObjmessageSystem.setAccountNumber(tempObj.mAccountNumber);
	 ObjmessageSystem.setAccountId(tempObj.mAccountId);
	 ObjmessageSystem.setBrandName(tempObj.mBrandName);
	 ObjmessageSystem.setContactName(tempObj.mContactName);
	 ObjmessageSystem.setAccountUniquePin(tempObj.mAccountUniquePin);
	 ObjmessageSystem.setAnswerPhrase(tempObj.mAnswerPhrase);
	 ObjmessageSystem.setURL(tempObj.mURL);
	 ObjmessageSystem.setCallType(tempObj.mCallType);
	 ObjmessageSystem.setAgentUniquePin(tempObj.mAgentUniquePin);
	 ObjmessageSystem.setIncomingANI(tempObj.mIncomingANI);
	 ObjmessageSystem.setCheckHistoryId(tempObj.mHistoryIdToCheck);
	 ObjmessageSystem.setFieldTypeTelephoNenumber(tempObj.mFieldTypeTelephoNenumber);
	ObjmessageSystem.setAccountNumberOld(tempObj.mAccountNumberOld);
	ObjmessageSystem.setInteractionType(tempObj.mInteractionType);
	ObjmessageSystem.setBillingDetails(tempObj.mBillingDetails);
	ObjmessageSystem.setConnId(tempObj.mConnId);
	ObjmessageSystem.setPhoneNumberToDial(tempObj.mPhoneNumberToDial);
	ObjmessageSystem.setRequestRouteTo(tempObj.mRequestRouteTo);
	ObjmessageSystem.setEmailInteractionType(tempObj.mEmailInteractionType);
	ObjmessageSystem.setInlineScriptingURL(tempObj.mInlineScriptingURL);
	
	ObjmessageSystem.setCallDelay(tempObj.mCallDelay);
	ObjmessageSystem.setPauseEvent(tempObj.mPauseEvent);
	ObjmessageSystem.setPauseDuration(tempObj.mPauseDuration);
	ObjmessageSystem.setCurrentTabIndex(tempObj.mCurrentTabIndex);
	ObjmessageSystem.setDNIS(tempObj.mDNIS);
	ObjmessageSystem.setf8disableoption(tempObj.mf8disableoption);
	ObjmessageSystem.setStatusFlag(tempObj.mStatusFlag);
	ObjmessageSystem.setinComingChatTo(tempObj.minComingChatTo);
	ObjmessageSystem.setBillingRepeatConnId(tempObj.mBillingRepeatConnId);
	ObjmessageSystem.setBillingRepeatAaccountnumber(tempObj.mBillingRepeatAaccountnumber);
	ObjmessageSystem.setBillingRepeatTime(tempObj.mBillingRepeatTime);
	ObjmessageSystem.setRepeatsUserPin(tempObj.mRepeatsUserPin);
	ObjmessageSystem.setRepeatsUserName(tempObj.mRepeatsUserName);
	ObjmessageSystem.setRhisId(tempObj.mRhisId);
	ObjmessageSystem.setTimeStamp(tempObj.mTimeStamp); 
 }

 
  function  clearLocalStorageForAccount(acc)
  {
	  
	  if(localStorage["ObjmessageSystem_"+acc]!=null)
		  {
		  localStorage.removeItem("ObjmessageSystem_"+acc);
		  console.log("clearing..ObjmessageSystem_");
		  }
	  if(localStorage["content_wrapper_"+acc]!=null)
		  {
		  localStorage.removeItem("content_wrapper_"+acc);
		  console.log("clearing..content_wrapper_");
		  }
	  if(localStorage["scenarios_index_"+acc]!=null)
	  {
		  localStorage.removeItem("scenarios_index_"+acc);
		  console.log("clearing..scenarios_index_");
		  }
	  if(localStorage["scenarios_content_"+acc]!=null)
		  {
		  localStorage.removeItem("scenarios_content_"+acc);
		  console.log("clearing..scenarios_content_");
		  }
	  if(localStorage["directory_list_"+acc]!=null)
		  {
		  localStorage.removeItem("directory_list_"+acc);
		  console.log("clearing..directory_list_");
		  }
	  else
		  {
		  console.log("No LocalStorage Content");
		  }
	 
	  }



