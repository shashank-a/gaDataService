function XmlTextParser (){}

XmlTextParser.prototype = {
    responseText:"",

    initialize:function() { },

    /*
     * Takes a string representing XML as a string.  The XML string is stored in the responseText field.
     *
     *
     */
    setResponseText:function(_responseText) { this.responseText = _responseText; },
    
    /*
     * Returns the XML String.
     *
     */
    getResponseText:function() {
	return this.responseText;
    },


    /*
     * Takes an element name and attribute value for the type attribute and returns the innerXML as a string.
     *
     * Example: getElementByTagNameAndTypeAttribute("response","data"); returns the inner contents of the <response type="data"> element.
     *
     */
    getElementByTagNameAndTypeAttribute:function(elemName,typeAttributeValue) {
	/* This regular expression searches for an xml opening element named elemName, with no spaces between the < and r,
	 * that may or may not contain attributes, 
 	 * but which must contain an attribute named type that is set to the value of "data" and which must be surrounded by double-quotes
	 */
	var regBeginTag = "<"+elemName+"\\s+[[a-z,\\=,\\\",\\s*]*\\s*]*type\\=\\\""+typeAttributeValue+"\\\"[\\s*[a-z,\\=,\\\"]*\\s*]*>";
	var regEndTag = "<\\s*\\/\\s*response\\s*>";
	var responseDataXml = this.getInnerXML(regBeginTag,regEndTag,this.getResponseText());
  	return responseDataXml;

    },

    /*
     * This is a more general method for obtaining innerXML by specifying the name of the matching attribute.
     * String - name of the element
     * String - name of the attribute
     * String - value of the attribute
     *
     * Returns the innerXML contents as a string.
     */
    getElementByTagNameAndAttribute:function(elemName, attribName, attribValue) {
        var regBeginTag = "<"+elemName+"\\s+[[a-z,\\=,\\\",\\s*]*\\s*]*"+attribName+"\\=\\\""+attribValue+"\\\"[\\s*[a-z,\\=,\\\"]*\\s*]*>";
        var regEndTag = "<\\s*\\/\\s*"+elemName+"\\s*>";
        var responseDataXml = this.getInnerXML(regBeginTag,regEndTag,this.getResponseText());

        return responseDataXml;
    },



    

	/*
	 *
	 * exp is a regular expression
	 * str is a string
	 * returns a string matching a regular expression exp executed on str
	 *
	 */	
	getMatch:function(exp,str) {
		  var re = new RegExp(exp,"");
		  var m = re.exec(str);
		  if (m == null) {
		    //mLogger.warn("XmlTextParser.getMatch():: No match");
		    return "";
		  } else {
		    var s="";
		    for (i = 0; i < m.length; i++) {
		      s = s + m[i] + "\n";
                    }
		    //mLogger.debug("XmlTextParser.getMatch():: " + s);
		    return s;
		  }
	},

	/*
         * Takes a regular expression matching a beginning xml tag and a regular expression matching the ending tag.
         * Returns the inner contents as a string.
         */
	getInnerXML:function(regBeginTag,regEndTag,str) {
		//mLogger.debug("XmlTextParser.getInnerXml:: get the xmlText");
		var xmlText = this.getMatch(regBeginTag+"[^,.]*"+regEndTag, str);
		//mLogger.debug("XmlTextParser.getInnerXml:: xmlText = " + xmlText);
		var frontTag = this.getMatch(regBeginTag, xmlText);
		//mLogger.debug("XmlTextParser.getInnerXml:: frontTag = " + frontTag);
		//mLogger.debug("getInnerXml:: length = " + frontTag.length);
		var endTag = this.getMatch(regEndTag, xmlText);
		//mLogger.debug("getInnerXml:: endTag = " + endTag.length);
		//mLogger.debug("getInnerXml:: length = " + endTag.length);
		var innerXml = xmlText.substring(frontTag.length);
		innerXml = innerXml.substring(0,innerXml.length-endTag.length);
		return innerXml;	
	}
		
};


//var xmlTextParser = new XmlTextParser();
