package com.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class GAUtil {

	public static String getkeyElementFromDimension(String dimensions)
	{
		dimensions=dimensions.replace("ga:","");	
		String key=null;
		String temp[]=dimensions.split(",");
		List<String> test= Arrays.asList(temp);
		
		if(test.contains("eventCategory"))
			key="Cat";
		if(test.contains("eventAction"))
		{
			if(key!=null)
			{
				key=key+"|"+"Act";
			}else
				key="Act";
		}
		if(test.contains("eventLabel"))
		{
			if(key!=null)
			{
				key=key+"|"+"Lab";
			}else
				key="Lab";	
		}
		if(test.contains("customVarValue1"))
		{
			if(key!=null)
			{
				key=key+"|"+"cVal1";
			}else
				key="cVal1";
		}
		if(test.contains("customVarValue2"))
		{
			if(key!=null)
			{
				key=key+"|"+"cVal2";
			}else
				key="cVal2";
		}
		if(test.contains("customVarValue3"))
		{
			if(key!=null)
			{
				key=key+"|"+"cVal3";
			}else
				key="cVal3";
		}
		if(test.contains("customVarValue4"))
		{
			if(key!=null)
			{
				key=key+"|"+"cVal4";
			}else
				key="cVal4";
		}
		if(test.contains("customVarValue5"))
		{
			if(key!=null)
			{
				key=key+"|"+"cVal5";
			}else
				key="cVal5";
		}
		
		
		return key;
	}
	
	public static String getCurrentDate()
	{
		String date="";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		date=(dateFormat.format(cal.getTime())).toString();
		System.out.println("current Date");
		return date;
	}
	
}
