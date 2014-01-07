var worker = new Worker('respond.js');
var tempdata="Helloo";

// Setup an event listener that will handle messages received from the worker.
worker.addEventListener('message', function(e) {
  // Log the workers message.
  console.log(e.data);
  
}, false);

//worker.postMessage("Hellloooo world");
worker.postMessage(JSON.parse(localStorage.getItem("rowdata")));

console.log("main.js");