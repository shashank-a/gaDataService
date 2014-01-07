var localstoragehelper = {
	localobj : '',
	updateAnswerPhrae : function(data) {
		this.parseddata = (JSON.parse(JSON.stringify(data))).data;
		console.log("data in updatAnswerPhrase:" + this.parseddata);
		//localStorage.setItem(this.parseddata.split('||')[0], JSON.stringify(this.parseddata.split('||')[1]));
	},
	getKeyData : function(account){
		fetchRecordeByaccountNo(account);
		//var accountdata = localStorage.getItem(account);
		//console.log("accountdata ::: "+accountdata);
		//var parsedData = JSON.parse(JSON.stringify(accountdata));
		
		if(localAcctInfo){
			//console.log("getting from localstorage:"+parsedData);
			console.log("getting from IndexedDB:"+localAcctInfo);
			return localAcctInfo;
		}
			
	},
	setKeyData : function(account,key,val){
		console.log("inside setKeyData::"+account+",key:"+key+",val:"+val);
		fetchRecordeByaccountNo(account);
		//var accountdata = localStorage.getItem(account);
	//	console.log("verifying accountdata from localstorage:"+account+","+accountdata);
		console.log("verifying accountdata from INdexDB:::::"+account+","+localAcctInfo);
		if(localAcctInfo){
			//accountdata.key = val;
			//console.log("before storing in local account:"+account+","+accountdata);
			//localStorage.setItem(account,JSON.stringify(val));
			createNewRecord(account,JSON.stringify(val));
		}else{
			accountdata = new Object();
			accountdata[key] = val;
			//console.log("before storing in local else:"+account+","+accountdata);
			//console.log("before storing in local else:"+account+","+JSON.stringify(accountdata));
			//localStorage.setItem(account,JSON.stringify(val));
			createNewRecord(account,JSON.stringify(val));
		}
	}
}


						//   Code for  Storage of local data in indexed db.

var indexedDB = window.indexedDBSync || window.webkitIndexedDB || window.mozIndexedDB || window.msIndexedDB;
var IDBTransaction = window.IDBTransaction || window.webkitIDBTransaction;
var dbName = "analyticsDB";
var db = null;
var result = null;
var localAcctInfo=null;

function createObjectStore() {
    var openRequest = indexedDB.open(dbName,2);
    openRequest.onerror = function (e) {
    	console.log("on errorrrrr");
        console.log("Database error: " + e.target.errorCode);
    };
    openRequest.onsuccess = function (event) {
        console.log("success");
        db = openRequest.result;
    };
    openRequest.onupgradeneeded = function (evt) {
    	console.log("onupgradeneeded::");
        var employeeStore = evt.currentTarget.result.createObjectStore("localData", {
            keyPath: "id",
            autoIncrement: true
        });
        employeeStore.createIndex("dateFrom", "dateFrom", {
            unique: true
        });
        employeeStore.createIndex("timestamp", "timestamp", {
            unique: false
        });
        
        
        console.log("Object Store Created");
        
        
    };
}

function createNewRecord(dateString, data) {
    console.info("this is a test event::",dateString);
    try {
        
        //console.log(db);
        var transaction = db.transaction("localData", "readwrite");
        //console.log(transaction);
        var store = transaction.objectStore("localData");
        //console.log(store);
        if (db != "" && db != null) {
            var request = store.add({
                "dateFrom": dateString,
                "timestamp": data
            });
            request.onsuccess = function (evt) {

                console.log("GA record was added successfully.");
            };
            request.onerror = function (evt) {
                console.log("GA record was not added.");
                console.log(evt.value);
                console.log("GA record was not added.");
            };
        }
    } catch (e) {
        console.log("catch block"+e);

    }
}

function openDatabase() {
    var openRequest = indexedDB.open(dbName);
    openRequest.onerror = function (e) {
        console.log("Database error: " + e.target.errorCode);
    };
    openRequest.onsuccess = function (event) {
        db = openRequest.result;
    };
    openRequest.onerror = function(event){
    	console.log(e);
    };
}

function deleteDatabase(dbtoDel) {
    var deleteDbRequest = indexedDB.deleteDatabase(dbtoDel);
    deleteDbRequest.onsuccess = function (event) {
        // database deleted successfully
    	console.log("Deleted Sccessfully");
    };
    deleteDbRequest.onerror = function (e) {
        console.log("Database error: " + e.target.errorCode);
    };
}

function fetchEmployee(acctNo) {
    try {
    	
        if (db != null) {
            //console.log(db);

            var store = db.transaction("localData").objectStore("localData");
            conslole.log(store);
            store.get(1).onsuccess = function (event) {
                console.log("success");
                var employee = event.target.result;
                console.log(employee);
                if (employee == null) {
                    result.value = "GA record not found";
                } else {
                    var jsonStr = JSON.stringify(employee);
                    result.innerHTML = jsonStr;
                }
            };
        }
    } catch (e) {
        console.log("catch block");

    }
}

function drawTableFromLocalStore(dateString,tablekey) {
    try {
if(db==null)
	{
	console.log("opening database");
	openDatabase();
	}

        if (db != null) {
            //console.log("Inside db");
            var store = db.transaction("localData").objectStore("localData");
            //console.log("store created");
            //console.log(store);
            var index = store.index("dateFrom");
            	
            index.get(dateString).onsuccess = function (evt) {
                var employee = evt.target.result;
                if(employee==undefined)
                	{
                	
                console.log("No Record Found!!!!!!!!!!!!!!!!!");
                filteredData=null;
                	}
                else{
                	console.info("result:::",employee);
                	result = employee;
                	//console.log(result);
                	//localAcctInfo=JSON.parse(employee.value);
                	console.log("tablekey::"+tablekey);
                		if(tablekey=="AgentDetails")
                			{
                			filteredData=JSON.parse(result.timestamp);
                			drawAgentDetail();
                			}
                		if(tablekey=="columnName")
                			{
                			columnData=JSON.parse(result.timestamp);
                			console.log(columnData);
                			}
                	//accounthelper.getaccountinfo(lURL);
                	
                	//console.log(localAcctInfo);
                }
            

            };
        }
    } catch (e) {
        console.log(e);
    }
}

function deleteRecodByAccountNo(acctNo)
{
	fetchRecordeByaccountNo(acctNo);
	console.log(result);
	
	 var id=null;
	  var store = db.transaction("localData","readwrite").objectStore("localData");
	  if(result)
		  id=result.id;
	
	  var request = store.delete(id);
	
	  request.onsuccess = function(e) {
	    console.log("Recprd Deleted for "+acctNo) ;// Refresh the screen
	  };

	  request.onerror = function(e) {
	    console.log(e);
	  };
	
}
function init()
{console.log("init called");
	openDatabase();
	
	
}

function readDbInfo()
{
	try{
	var objStore=db.transaction("localData").objectStore("localData");
	
	}catch (e)
	{		console.log("catch Block");	
			console.log(e.name);
			if(e.name=="NotFoundError")
				{
				createObjectStore();
				}
	}
	
	
}
function updateAccount(acctNo,element,val) {
	try {
		
		var transaction = db.transaction("localData", "readwrite");
		var store = transaction.objectStore("localData");                    
	  	var jsonStr;
	  	var employee;
	  	console.info("store active",store);
		if ( db != null) {
			console.log("db not null");
			var index = store.index("accountNo");
        	console.info("index active",index);
        	console.log(acctNo);
            index.get(acctNo).onsuccess = function (evt) {
                var employee = evt.target.result;
				// save old value
				
				//var jObject=JSON.parse(employee.value);
				//jObject[new Date().getTime()]="This is a test update at"+new Date().getTime();
				employee[element]=val;
					console.log(employee);
				var request = store.put(employee);
				var request=""
					//alert("loading from localObject");
					//document.getElementById(id).innerHTML=JSON.parse(employee[id]);
					
				request.onsuccess = function(e) {
					console.log("data updated");
				};
				
				request.onerror = function(e) {
					console.log(e.value);
				};				

				
				
			}; // fetch employee first time
		}
	}
	catch(e){
		console.log(e);
	}
}

function loadLocalAccount(acct)
{
	
	fetchRecordeByaccountNo(acct);
	var lURL='/InitialAccountAction/getAccountInfo.do?acctNum='+acct+'&userpin=6NLEX&calltype=fetch&tab=0&fetch=true&statusFlag=true&currentTabIndex=24117531875855092&phoneNum=ready'	
	accounthelper.getaccountinfo(lURL);
	
}

window.addEventListener("DOMContentLoaded", init, false);
