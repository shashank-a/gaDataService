var db=null;
   function initDB()
   {
	   db = openDatabase('analyticsDB', '1.0', 'my first database', 2 * 1024 * 1024);
	   db.transaction(function (tx) {  
		   tx.executeSql('CREATE TABLE IF NOT EXISTS localData (id unique, acctNo unique, data )');
		});
   }
   
   
   
   function writeData()
   {
	   		
	   db.transaction(function (tx) {  
		   tx.executeSql('CREATE TABLE IF NOT EXISTS localData (id unique, acctNo unique, data )');
		   var query="INSERT INTO localData (id, acctNo,data) VALUES ('"+new Date().getTime()+"', '8"+Math.floor((Math.random()*1000000000)+1)+"','This is some JSon Data')";
		   console.log(query);
		   tx.executeSql(query);
		 });
   
   }
   
   function readData()
   {
	   
	   db.transaction(function (tx) {
		   tx.executeSql('SELECT * FROM localData', [], function (tx, results) {
		    var len = results.rows.length, i;
		    console.log(results.rows);
		    
		  }, null);
		 });
	   
   }
   
   function dropTable(tableName)
   {
	   initDB();
	   db.transaction(function (tx) {  
		   var query="DROP TABLE "+tableName;
		   console.log(query);
		   tx.executeSql(query);
		   
		 });
	   
   }
   
   