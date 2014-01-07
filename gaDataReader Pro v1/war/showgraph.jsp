<html>
  <head>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
    	  var data = new google.visualization.DataTable();
    	  data.addColumn('string', 'Topping');
          data.addColumn('number', 'Slices');
          var testArray = new Array();
          var subArray = new Array(2);
          subArray[0] = "0";
          subArray[1] =  2;
          testArray[0] = subArray;
          testArray[1] = ["1",5];
          testArray[2] = ["2",7];
          console.log("testArray:::"+JSON.stringify(testArray));
          data.addRows(testArray);
    	  var options = {
    	    title: 'Daily Logs',
    	    displayAnnotations: true,  
    	  };
    	  var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
    	  chart.draw(data, options);
      }
    </script>
  </head>
  <body>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
  </body>
</html>