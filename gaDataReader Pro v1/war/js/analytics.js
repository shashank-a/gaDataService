var pageTracker = _gat._getTracker("UA-29720187-1");
pageTracker._setDomainName("schrodingerstest.appspot.com");
pageTracker._setAllowLinker(true);
pageTracker._initData();
pageTracker._trackPageview();
pageTracker._trackPageLoadTime();


function recordGAEvent(account,action,user,clickstamp) {
	alert("Page Tracker::"+pageTracker._getName());
	pageTracker._trackEvent(account,action,user);
	pageTracker._setVar(clickstamp);
	pageTracker._trackPageview();
	
	alert("Event logged with::"+account+"::"+action+"::"+user+"::@"+clickstamp);
	
}
function recordCustomVariable(index,userTime)
{	var date=new Date().toString();
	pageTracker._setCustomVar(index,index,date.substring(0,25),1);
	pageTracker._setVar(date.substring(0,25));
	pageTracker._trackPageview();
	alert("Custom variable"+index+":"+userTime+"  recorded"+"::@"+new Date());
}

/*
var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-29720187-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); 
    ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; 
    s.parentNode.insertBefore(ga, s);
    
  })();
  
  
  
  function eventLogger(cat,act,label,time)
	{
	  _gaq.push(['_setCustomVar',1,act,time,2]);
		_gaq.push(['_trackEvent', cat, act, label]);
		
	}
*/