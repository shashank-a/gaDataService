<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
 
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>


<title>Schrodingers Test</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="http://www.google-analytics.com/ga.js"></script>
<script type="text/javascript" src="js/analytics.js"></script>

<script type="text/javascript">
var name;
var time=<%=System.currentTimeMillis()%>
function getUser()
{var name='name';
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}
name=getUser();
function getUserDetails()
{alert("getUserDetails");
	var url = "/test.do";
	var ajaxObjhttp = new Ajax.Request(url, {
		method: 'POST',
		parameters: {name:'shashanksharma'},
		onCreate: function(transport) {
        	      alert("Creating New Ajax Call");
        	     
                },
                onSuccess: function(transport) {
                      var serverResponse = transport.responseText;
                      
                      alert(serverResponse);
                      
                      document.getElementById("userData").innerHTML=serverResponse;
                      
                      
                      //document.getElementById("status").innerHTML="<b>Logged In</b>";
                      
                      	                      
                },
                onFailure : function(response) {
                      alert("Some error occured while making call to remote server");
                }
        });		
}

function setURL(obj)
{
	var temp=obj.href;
	
	temp+=document.getElementById('emailId').value;
	alert(temp);
	obj.href=temp;
}

function sendemailtest()
{var url = "/email.do";
	var ajaxObjhttp = new Ajax.Request(url, {
	method: 'POST',
	parameters: {name:'shashanksharma'},
	onCreate: function(transport) {
    	      alert("Creating New Ajax Call");
    	     
            },
            onSuccess: function(transport) {
            	alert(transport);
                  var serverResponse = transport.responseText;
                  alert(serverResponse);
                  
                  document.getElementById("userData").innerHTML=serverResponse;
                  
                  //document.getElementById("status").innerHTML="<b>Logged In</b>";
                  	                      
            },
            onFailure : function(response) {
                  alert("Some error occured while making call to remote server");
            }
    });		

	}
	
	

</script>
<div style="text-align: right">
<img src="http://code.google.com/appengine/images/appengine-noborder-120x30.gif" 
alt="Powered by Google App Engine" />
</div>


<!-- <script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-29720187-1']);
  _gaq.push(['_setDomainName', 'schrodingerstest.appspot.com']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); 
    ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; 
    s.parentNode.insertBefore(ga, s);
    
  })();
  
  
</script>
 -->
 </head>

<body>
						<div id="mainDiv"  style="border: 1 px solid black;text-align:center; width: 100%;" >
						
									<span style="width:50%;text-align:center;text-decoration: underline; ">Status Div</span>
									</br></br>
									</br>
									Current Time:-<%=System.currentTimeMillis()%>		
						</div>
						
		<div id="#d001">				
				<div>
						<div id="loginBox" style="border: 1 px solid black;text-align:center; width: 100%;" >
								<div><a href="/registration.do" >Add New User</a></div>
							</br>
							</br>
							<div><a href="/userReport.do" >List All User</a></div>
							</br>
							</br>
							<div><a href=".."  onclick="getUserDetails();" >List All User(ajax)</a></div>
							</br>
							</br>
							<div>
							email Id:<input type="text" name="emailId" id="emailId">
							</div>
							<div><a href="/emailController.do?method=setEmail&emailId=" onclick="eventLogger('GoogleMail','clicked','gmail');"; >Subscribe a newsletter</a></div>
							</br>
							<div><a href="/JavaMail.do" onclick="eventLogger('javaMail','clicked','SMTP',"+<%=System.currentTimeMillis()%>+");">Send  Java Email</a></div>
							<div>
							test:
							</div>
							<div><a href=".." onclick="sendemailtest()">Send an Email(new link)</a></div>
							<div id><a href="#1" onclick="eventLogger('schrodingersTest','test','Friday','"+<%=System.currentTimeMillis()%>+"');">Analytics Test</a></div>
							<div id><a href="#2" onclick="recordCustomVariable(1,'time');">Test Custom variable 1</a></div>
							<div id><a href="#3" onclick="recordCustomVariable(2,'time');">Test Custom variable 2</a></div>
							<div id><a href="#4" onclick="recordGAEvent('8004941270','Send',getUser());">Test Event send 1</a></div>
							<div id><a href="#5" onclick="recordGAEvent('8005251260','Save',getUser());">Test Event save 1</a></div>
							<div id><a href="#6" onclick="recordGAEvent('8004941270','Send',getUser());">Test Event send 2</a></div>
							<div id><a href="#7" onclick="recordGAEvent('8005251280','Save',getUser());">Test Event save 2</a></div>
							
							<div id><a href="/login.do">Authorize request</a></div>
							
							
							
						</div>	
				</div>
		</div>
		<div id="userData"  >
				<%=request.getAttribute("oAuthStatus")	
				%>
					</div>
<div id="login" style="display:none;border: thick;border-color: gray;" >Welcome!!!</div>

  </body>
  
  

</html>