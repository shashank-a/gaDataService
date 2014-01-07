package com.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.account.AccountDetails;
import com.action.ActionServlet;
import com.util.StringUtil;

public class GaReportProcessor {
	private static final Logger mLogger = Logger.getLogger(GaReportProcessor.class.getPackage().getName());
	
	
	public ArrayList<AccountDetails> getAccountDataFromGaData(ArrayList<ArrayList<?>> dataTable)
	{
		try {
		ArrayList<ArrayList<String>> groupedData=null;
		
		LinkedHashMap <String,String> hm=getUniqueDimensionFromGAData(dataTable,0);
		Iterator it= hm.entrySet().iterator();
		AccountDetails acctDetails;
		ArrayList<AccountDetails> accountData=new ArrayList<AccountDetails>();
		ArrayList<List<?>> checkList=new ArrayList<List<?>>();
		//int loopvalue=0;
		
		System.out.println("UNique accounts::::"+hm.size());
		System.out.println("DataTable Size:::"+dataTable.size());
		
		for(String tempAcc:hm.keySet())
		{	
			Integer callTime=new Integer(0);
			int send=0;
			int callCon=0;
			int accLoad=0;
			int mAnnotate=0;
			int cAnnotate=0;
			int outboudCall=0;
			int apRefetch=0;
			int inBound=0;
			
			
			
			acctDetails=new AccountDetails();
			for(ArrayList<?> rowdata: dataTable)
			{//loopvalue++;
				if(tempAcc.equals(rowdata.get(0)))
						{
					
							if(rowdata.get(1).equals("Send"))
							{
								send++;
							}
							else if(rowdata.get(1).equals("CallConclusion"))
							{
								callCon++;
							}
							else if(rowdata.get(1).equals("Account Load"))
							{
								accLoad++;
							}
							else if(rowdata.get(1).equals("InBoundCall"))
							{
								inBound++;
							}
							else if(rowdata.get(1).equals("Message Annotation"))
							{
								mAnnotate++;
							}
							else if(rowdata.get(1).equals("CallerHistory Annotation"))
							{
								cAnnotate++;
							}
							else if(rowdata.get(1).equals("Outbound Call"))
							{
								outboudCall++;
							}
							
						}
			}
			acctDetails.setAcctNum(tempAcc);
			acctDetails.setCallTotal(callTime.toString());
			acctDetails.setSendCount(send);
			acctDetails.setCallConclusion(callCon);
			acctDetails.setAccLoad(accLoad);
			acctDetails.setAccLoad(accLoad);
			accountData.add(acctDetails);
			
		}
		
		
		System.out.println("Returning...");
		return accountData;
		} catch (Exception e) {
			printStackTrace(e);
			return null;
		}
	}
	
	public ArrayList<AccountDetails> getEventDescriptionForCategory(ArrayList<ArrayList<?>> dataTable,String category)
	{
		try 
		{
		
		ArrayList<ArrayList<?>> groupedData=null;
		//
		//Iterator it= hm.entrySet().iterator();
		AccountDetails acctDetails;
		ArrayList<AccountDetails> accountData=new ArrayList<AccountDetails>();
		
		//int loopvalue=0;
		System.out.println("category::"+category);
		groupedData=filterDataByDimension(dataTable,category,0);
		System.out.println("Filtered Data size"+groupedData.size());
		LinkedHashMap <String,String> hm=getUniqueDimensionFromGAData(groupedData,2);
		
		System.out.println("UNique agents::::"+hm.size());
		System.out.println("DataTable Size:::"+groupedData.size());
		for(String tempAgent:hm.keySet())
		{	
			Integer callTime=new Integer(0);
			int send=0;
			int callCon=0;
			acctDetails=new AccountDetails();
			for(ArrayList<?> rowdata: groupedData)
			{//loopvalue++;
			if(tempAgent.equals(rowdata.get(2)))
						{System.out.println("rowdata.get(1):::"+rowdata.get(1));
							if(("Send").equals(rowdata.get(1).toString().trim()))
							{
								System.out.println("Send ++");
								send++;
							}
							else if(rowdata.get(1).equals("CallConclusion"))
							{System.out.println("CallConclusion ++");
								callCon++;
							}
							callTime=callTime+(new Integer(rowdata.get(3).toString()));
						}
			}
			acctDetails.setAcctNum(category);
			acctDetails.setEventLabel(tempAgent);
			acctDetails.setCallTotal(callTime.toString());
			acctDetails.setSendCount(send);
			acctDetails.setCallConclusion(callCon);
			accountData.add(acctDetails);
			
		}
		printAccountData(accountData);
		
		System.out.println("Returning...");
		return accountData;
		} catch (Exception e) {
			printStackTrace(e);
			return null;
		}
	}

	
	public LinkedHashMap getUniqueDimensionFromGAData (ArrayList<ArrayList<?>> rowData,int index)
	{
		LinkedHashMap <String,String> hm=new LinkedHashMap<String,String>();
		
		for (ArrayList <?> rowValues : rowData) {
			  
			String tempacc=rowValues.get(index).toString();
			hm.put(tempacc,tempacc);
		   	}
	
		System.out.println(hm);
		return hm;
	}
	
	public ArrayList<ArrayList<?>> filterDataByDimension(ArrayList<ArrayList<?>> rowData,String value,int index)
	{
		ArrayList<ArrayList<?>> filteredList=new ArrayList<ArrayList<?>>();
		//System.out.println("index::"+index+"value::"+value);
		 Pattern p = Pattern.compile(value);
		 
		for(ArrayList<?> entry: rowData)
		{
			Matcher m = p.matcher(entry.get(index).toString().trim());
			if(m.matches())
			{//System.out.println("entry::"+entry.get(index).toString());
				filteredList.add(entry);
			}
		}	
		
		return filteredList;
	}
	
	public ArrayList<ArrayList<?>> escapeGAData(ArrayList<ArrayList<?>> rowData,int index)
	{ArrayList<ArrayList<?>> testArray=new ArrayList<ArrayList<?>>(); 
		for(ArrayList rowvalue:rowData)
		{ Object temp=rowvalue.get(index);
			temp=temp.toString().replaceAll("\"", "").replaceAll(",","");
			temp=StringUtil.removeHTMLElement(temp.toString());
			temp=temp.toString().replaceAll("&nbsp;","");
			System.out.println(temp);
			
			//temp=StringUtil.removeSpecialChars(temp.toString());
			rowvalue.set(index, temp); 
			
			testArray.add(new ArrayList(rowvalue));
		}
		return testArray;
		
	}
	
	
	public LinkedHashMap<String, HashMap> getAgentDetailReport(ArrayList<ArrayList<?>> rowData) throws JsonGenerationException, JsonMappingException, IOException
	{
		
		LinkedHashMap<String, HashMap> processedJSON=null;
		HashMap<String,String> uniqueAgent=getUniqueDimensionFromGAData(rowData,2);
		HashMap<String,String> uniqueAction=getUniqueDimensionFromGAData(rowData,1);
		
		for(String agent:uniqueAgent.keySet())
		{
			LinkedHashMap rowObj=new LinkedHashMap(); 
			rowObj.put("Agent Email", agent);
			
			for(String action:uniqueAction.keySet())
			{
				rowObj.put(action,0);
			}		
			ArrayList<ArrayList<?>> agentDetails=filterDataByDimension(rowData,agent,2);
			for(ArrayList<?> row:agentDetails)
			{
				
				if(rowObj.get(row.get(1))!=null)
				{
					rowObj.put(row.get(1),Integer.parseInt(rowObj.get(row.get(1)).toString()) +1);
				}
			}
			
			processedJSON.put(agent,rowObj);
			System.out.print(GaDatastoreService.convertObjectToJson(processedJSON));
		}
		
	return processedJSON;
			 
	}
	

    public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }


    public static void printStackTrace(Throwable t) {
	mLogger.error(getStackTrace(t));
    }
    
    public void printAccountData(ArrayList<AccountDetails> accountDetails)
    {	
    	for(AccountDetails acc:accountDetails)
    	{
    		System.out.println("acc:"+acc.getAcctNum()+"label:"+acc.getEventLabel()+"count:"+acc.getSendCount()+"CC:"+acc.getCallConclusion()+"time::"+acc.getCallTotal());
    	}
    	
    }
}
