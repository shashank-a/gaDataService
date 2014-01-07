self.addEventListener('message', function(e) {
	
	self.postMessage(getUniqueDimensionFromWorker(e.data,3));
  
}, false);


function getUniqueDimensionFromWorker(rowArray,index)
{//console.log(index);
	
	var i,
    len=rowArray.length,
    out=[],
    obj={};
		
			for (i=0;i<len;i++) 
			{
				
				//if((rowArray[i][index]!="NULL")||(rowArray[i][index]!="null"))
				//{
							if(index==2)
							{		uniqueAgent=(rowArray[i][index]).toUpperCase();
									obj[uniqueAgent]=0;
							}
							else
								obj[rowArray[i][index]]=0;
								
				//}
			 
			}
			for (i in obj) {
				out.push(i);
			}
			
		
	return out;

}