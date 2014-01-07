/*
* This class is written for generating a xml .and validating form.
*/


function ValidateAndGenerateXML (){}

ValidateAndGenerateXML.prototype = {

    generateXML:function(pFormId,pAction){
       try {
		var lMessageFormId="";
        if(pAction=='save' || pAction=='send' ||  pAction=='done'){
            lMessageFormId="msgform_"+pFormId;
        }else if(pAction=='update'){
            lMessageFormId="form"+pFormId;
        }
        var lObjForm=document.getElementById(lMessageFormId);
        var lXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        lXml+="<messageform>";
        /*var lTempFieldvalue="";
       
        for(i=0;i<lObjForm.length;i++){
            lTempFieldvalue = lObjForm.elements[i].value;
            
            lTempFieldvalue = lTempFieldvalue.replace(/&/g,'010101');
            lTempFieldvalue = lTempFieldvalue.replace(/</g,'000111');
            lTempFieldvalue = lTempFieldvalue.replace(/>/g,'&gt;');
           
            lTempFieldvalue = lTempFieldvalue.replace(/"/g,'&quot;');
            lTempFieldvalue = lTempFieldvalue.replace(/\'/g,'&apos;');
            lTempFieldvalue = lTempFieldvalue.replace(/\%/g,'101010'); 
            
            
            
            lXml+="<field><id>"+ lObjForm.elements[i].name +"</id><value>"+ lTempFieldvalue +"</value></field>";
        } */
       
        lXml+=this.generateXMLFormData(lMessageFormId);
        lXml+="</messageform>";
        //alert("in xml generator" +lXml)
        return lXml;
       }
       catch(e) { alert(e); }
    },

    generateXMLForAutoSave:function(){
        var lTabContainer,lTabWrap,lTabs,lFormId,lObjForm;
        var lTempFieldvalue="";
        lTabContainer    = document.getElementById('info');

        lTabWrap         = org.ditchnet.dom.DomUtils.getFirstChildByClassName(
                                    lTabContainer,
                                    org.ditchnet.jsp.TabUtils.TAB_WRAP_CLASS_NAME );
        lTabs            = org.ditchnet.dom.DomUtils.getChildrenByClassName(
                                    lTabWrap,
                                    org.ditchnet.jsp.TabUtils.TAB_CLASS_NAME );
        var lXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        lXml+="<messageforms>";
        for (var i = 0; i < lTabs.length; i++) {
            if(lTabs[i].hasAttribute('msgid')){
                lFormId=lTabs[i].getAttribute("msgid");
                lXml+="<messageform id=\""+lFormId+"\">";
                lXml+=this.generateXMLFormData("msgform_"+lFormId);
                lXml+="</messageform>";
            }
        }
        lXml+="</messageforms>";
        return lXml;
    },

    generateXMLFormData:function(pFormId){
        var lObjForm=document.getElementById(pFormId);
        var lXml="";
        var temp="";
        var lCustomField="";
        var pre_select_string="";
        var split_string="";
        var split_tokens="";
        var previousId = "";
        var outPutOrder = "";
        var prevOutputOrder = "";
         if(lObjForm == null || lObjForm == '')
        {
         document.getElementById('messageFormStatus').value = "true";
        }
        else
        {
		        for(var j=0;j<lObjForm.length;j++){
		           	//mLogger.info("In XML Generator:: Before Encoding-- "+lObjForm.elements[j].value);
					
					/*
					  This condition is to check if the FirstName / MI Name / Last Name are emoty , instead showing empty in output file
					  we are appending "UNKNOWN" for each respectlively.
					*/
					
		            var currentObj = lObjForm.elements[j];
		            
		            if(currentObj.getAttribute('fieldtitle') == 'First Name Caller')
		            {
		              if(Trim(document.getElementById(currentObj.id).value).length == 0)
		                 document.getElementById(currentObj.id).value = 'UNKNOWN ';
		            }else if (currentObj.getAttribute('fieldtitle') == 'Last Name Caller' )
		            {
		              if(Trim(document.getElementById(currentObj.id).value).length == 0)
		                 document.getElementById(currentObj.id).value = 'UNKNOWN ';
		            }else if (currentObj.getAttribute('fieldtitle') == 'HIDDEN_Caller ID' )
		            {
		              if(Trim(document.getElementById(currentObj.id).value).length == 0)
		                 document.getElementById(currentObj.id).value =  ObjmessageSystem.getIncomingANI();
		            }
		            
					if(currentObj.nodeName == "SELECT" && currentObj.multiple)
					{
						var values = "";
						for(var index = 0; index < currentObj.options.length; index++){
							if(currentObj.options[index].selected){
								values += currentObj.options[index].value + ","; 	
							}
						} 
					  	lTempFieldvalue = values.substring(0,values.length-1);
					}
 					else if (currentObj.nodeName == "SELECT"
						&& !currentObj.multiple
						&& currentObj.getAttribute('optiontype') != 'Deliverymethods') {
 						var value = "";
					for ( var index = 0; index < currentObj.options.length; index++) {
						if (currentObj.options[index].selected) {
							value = currentObj.options[index].innerHTML;
							if(index==0)
								value="";
						}
					}
					lTempFieldvalue = value.replace(/\"/g,'\"');
				}
					else{
		            	lTempFieldvalue = lObjForm.elements[j].value;
		            }
                
               // lTempFieldvalue = lObjForm.elements[j].value;
                lTempFieldvalue = lTempFieldvalue.replace(/&/g,'010101');
                lTempFieldvalue = lTempFieldvalue.replace(/</g,'000111');
                lTempFieldvalue = lTempFieldvalue.replace(/\+/g,'011110');
                /*  lTempFieldvalue = lTempFieldvalue.replace(/>/g,'&gt;');
                lTempFieldvalue = lTempFieldvalue.replace(/\"/g,'&quot;');
                lTempFieldvalue = lTempFieldvalue.replace(/\'/g,'&apos;');*/
                lTempFieldvalue = lTempFieldvalue.replace(/%/g,'101010'); 
                //mLogger.info("In XML Generator:: After Encoding-- "+lTempFieldvalue);
                if(lTempFieldvalue.indexOf('Lead 1') != -1)
                	lTempFieldvalue = "Lead 1";
                if(previousId == lObjForm.elements[j].name)
                {
                  outPutOrder = prevOutputOrder;
                }
                else
                {
                    outPutOrder = lObjForm.elements[j].getAttribute("outputorder");
                    prevOutputOrder =outPutOrder;
                }
                
                previousId = lObjForm.elements[j].name;
                lXml+="<field type=\""+lObjForm.elements[j].getAttribute("optiontype")+"\" required=\""+lObjForm.elements[j].getAttribute("required")+"\"><id>"+ lObjForm.elements[j].name +"</id><value>"+ lTempFieldvalue +"</value><outputorder>"+ outPutOrder+"</outputorder></field>";
                split_string=lObjForm.elements[j].value;
                //alert("split_string one :::: "+split_string);
                split_tokens=split_string.split("/preselected");
                
               if(split_tokens[1]=="")
               {
            	  // alert("split_string in if :::: "+split_string);
                 pre_select_string+=lObjForm.elements[j].value;
               }
               else
               {
            	   //alert("split_string :::: "+split_string);
            	var lHiddenField = split_string.split("-");
            	
                if(lHiddenField.length > 1 && ( lHiddenField[1] == "Caller ID" || lHiddenField[1] == "Custom Subject")) {
                	lCustomField += lObjForm.elements[j].value;
                }
                temp +=lObjForm.elements[j].value;
               }
            }
		       /* alert("value ::: "+getTabIndex());
		        if(typeof getTabIndex()=='undefined'){
		        	alert("hi");
		        }else{
		        	alert("hello");
		        }*/
            if(getTabIndex()!="1" && getTabIndex()!="2" && typeof getTabIndex()!='undefined')
             {
            	if(temp != "" && temp.length>1 && temp != lCustomField)
                  document.getElementById('messageFormStatus').value = "false";
                 else
                    document.getElementById('messageFormStatus').value = "true";
           }
           else
        	   document.getElementById('messageFormStatus').value = "true";
               
           /*
           * This block is to generate xml content to have Sokolove Call comes date / time to avoid fluctuation Data/Time in upl file and DB as well.
           */    
          lXml+="<field> <id>SokoloveDate</id> <value>"+ObjmessageSystem.getSokoloveCallDate()+"</value> </field>";     
          lXml+="<field> <id>SokoloveTime</id> <value>"+ObjmessageSystem.getSokoloveCallTime()+"</value> </field>";
    }
        return lXml;
    },
    
    //to trim empty spaces
    
    trimAll:function(strValue) {
     var objRegExp = /^(\s*)$/;
        //check for all spaces
        if(objRegExp.test(strValue)) {
           strValue = strValue.replace(objRegExp, '');
           if( strValue.length == 0)
              return strValue;
        }
    
       //check for leading & trailing spaces
       objRegExp = /^(\s*)([\W\w]*)(\b\s*$)/;
       if(objRegExp.test(strValue)) {
           //remove leading and trailing whitespace characters
           strValue = strValue.replace(objRegExp, '$2');
        }
      return strValue;
    },
    
    
  validateMessageFormField:function(pFormId,pAction){
	  //alert("pFormId ::: "+pFormId);
	  //alert("pAction ::: "+pAction);
    var ltempfieldobject;
    var status=true;
      var lDeliveryidPresence = false;
     //var lDeliveryidPresence = false;
    var lAtleastOneNotNullStatus=false;
    var lMessageFormId="";
    if(pAction=='save' || pAction=='send' ){
        lMessageFormId="msgform_"+pFormId;
    }else if(pAction=='update'){
        lMessageFormId="form"+pFormId;
    }
    
    
    
    var lObjForm=document.getElementById(lMessageFormId);
    
    for(i=0;i<lObjForm.length;i++){
    	if(ObjmessageSystem.getIsCallConclusion()!='true')
    		{
            if((CBool(lObjForm.elements[i].getAttribute("required")))&& (Trim(lObjForm.elements[i].value)=='')){
                ltempfieldobject=document.getElementById(lObjForm.elements[i].id);
                if(lObjForm.elements[i].id=='DecisionIntersection'){
                	showAlertPopup("Please fill field: "+lObjForm.elements[i].getAttribute("fieldtitle"));
                }else{
                	showAlertPopup("Please fill field: "+ltempfieldobject.getAttribute("fieldtitle"));
                }
                //lObjForm.elements[i].value='';
               //lObjForm.elements[i].focus();
                status=false;
                return status;
            }
    		}

        if(pAction=="send" && lObjForm.elements[i].name=="deliveryID" && lObjForm.elements[i].value==''){
            showAlertPopup("Please Select Delivery Method");
            status=false;
            lObjForm.elements[i].focus();
            return status;
        }
        
        if(lObjForm.elements[i].id != 'null')
        {
		
		if(Trim(lObjForm.elements[i].id) != '' && lObjForm.elements[i].id != 'undefined')
		{
        	ltempfieldobject = document.getElementById(lObjForm.elements[i].id);
       
         
         if(ltempfieldobject != 'null')
         {
        
            if(lObjForm.elements[i].name!="deliveryID")
            {
                var ltempfield = ltempfieldobject.getAttribute("optiontype");
                if(ObjmessageSystem.getIsCallConclusion()!='true')
                	{
                if(ltempfield==ObjmessageSystem.getFieldTypeTelephoNenumber()){
                    var lIsValidPhone;
                    if(lObjForm.elements[i].value=="N/A"||Trim(lObjForm.elements[i].value)=="()-" ||Trim(lObjForm.elements[i].value)==''){
                      lIsValidPhone=true;
                    }else
                      lIsValidPhone=checkInternationalPhone(lObjForm.elements[i].value);
                    if(lIsValidPhone==false && pAction=='update')
                	{
                	lIsValidPhone=true;
                	}
                    if(lIsValidPhone==false){
                       showAlertPopup(lObjForm.elements[i].value+" is not a valid phone number");
                       lObjForm.elements[i-3].focus();
                       status=false;
                       return status;
                    }
                }
                	}
            }
		 }
		 }
		 }
          if(lAtleastOneNotNullStatus){
              //At least one not null found
          }else{ // continue search for at least one not null field
            if(Trim(lObjForm.elements[i].value)!='' && lObjForm.elements[i].name!="deliveryID"){
                lAtleastOneNotNullStatus=true;
            }else{
                lAtleastOneNotNullStatus=false;
            }
          }
         
          
         

          if(pAction=="send" ){
        	if(lObjForm.elements[i].name == 'EmailId') { 
                    
             lDeliveryidPresence = true;
             }else if(lObjForm.elements[i].name == "deliveryID"){
             lDeliveryidPresence = true;
             }
             
           /*else{
             lDeliveryidPresence = false;
           }*/}else{
           		lDeliveryidPresence = true;
           }
    }
    if(lDeliveryidPresence){
    }else{
	     showAlertPopup("No Delivery type, message cannot be sent");
	      status=false;
	     return status;
    }
    if(lAtleastOneNotNullStatus){
        return status;
    }else{
    	if(ObjmessageSystem.getIsCallConclusion()!='true')
        showAlertPopup("Please fill at least one field");
        //ObjmessageSystemEngine.enableAllFormElement();
        lObjForm.elements[0].focus();
        status=false;
        return status;
    }
    
    
  }

};

var objXMLgenAndValidate = new ValidateAndGenerateXML();
