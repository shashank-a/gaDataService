package com.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.account.AccountDetails;
import com.action.ActionServlet;
import com.util.DataStoreManager;
import com.util.StringUtil;

public class GaReportProcessor {
	private static final Logger mLogger = Logger.getLogger(GaReportProcessor.class.getPackage().getName());
	
	/*
	public ArrayList<AccountDetails> getAccountDataFromGaData(ArrayList<ArrayList<?>> dataTable)
	{
		try {
		ArrayList<ArrayList<String>> groupedData=null;
		
		HashSet hm=getUniqueDimensionFromGAData(dataTable,0);
		Iterator it= hm.iterator();
		AccountDetails acctDetails;
		ArrayList<AccountDetails> accountData=new ArrayList<AccountDetails>();
		ArrayList<List<?>> checkList=new ArrayList<List<?>>();
		//int loopvalue=0;
		
		System.out.println("UNique accounts::::"+hm.size());
		System.out.println("DataTable Size:::"+dataTable.size());
		
		for(String tempAcc:hm)
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
	*/
	/*public ArrayList<AccountDetails> getEventDescriptionForCategory(ArrayList<ArrayList<?>> dataTable,String category)
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
*/
	
	public HashSet getUniqueDimensionFromGAData (ArrayList<ArrayList<?>> rowData,int index)
	{
		//LinkedHashMap <String,String> hm=new LinkedHashMap<String,String>();
		HashSet <String> hs= new HashSet<String>();
		
		for (ArrayList <?> rowValues : rowData) {
			  
			String tempacc=rowValues.get(index).toString();
			//hm.put(tempacc,tempacc);
			hs.add(tempacc);
		   	}
	
		return hs;
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
		HashSet uniqueAgent=getUniqueDimensionFromGAData(rowData,2);
		HashSet uniqueAction=getUniqueDimensionFromGAData(rowData,1);
		
		for(Object agent:uniqueAgent)
		{
			LinkedHashMap rowObj=new LinkedHashMap(); 
			rowObj.put("Agent Email", agent.toString());
			
			for(Object action:uniqueAction)
			{
				rowObj.put(action.toString(),0);
			}		
			ArrayList<ArrayList<?>> agentDetails=filterDataByDimension(rowData,agent.toString(),2);
			for(ArrayList<?> row:agentDetails)
			{
				
				if(rowObj.get(row.get(1))!=null)
				{
					rowObj.put(row.get(1),Integer.parseInt(rowObj.get(row.get(1)).toString()) +1);
				}
			}
			
			processedJSON.put(agent.toString(),rowObj);
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
    
    
    
    public ArrayList AgentActionReport(ArrayList<ArrayList<?>> jsonData) throws JsonGenerationException, JsonMappingException, IOException
    {
    	

    		HashSet<String> uniqueAgentMap=getUniqueDimensionFromGAData(jsonData,2);
		  HashSet<String> uniqueActionSet=getUniqueDimensionFromGAData(jsonData,1);
		  HashSet<String> uniqueAgentSet=null;
		  
		  TreeMap<String,Object> rowObj= null;
		  ArrayList agentData=new ArrayList<String>();
		  ArrayList agentActionDetils= new ArrayList();
		  List columnName=null;
		  
		  uniqueActionSet.add("Account Load (IB)");
		  uniqueActionSet.add("Account Load (NOID)");
		  uniqueActionSet.add("Account Load (CI)");
			 uniqueActionSet.add("CallConclusion (IB)");
			uniqueActionSet.add("CallConclusion (NOID)");
			uniqueActionSet.add("CallConclusion (CI)");
			uniqueActionSet.add("Send (IB)");
			uniqueActionSet.add("Send (NOID)");
			uniqueActionSet.add("Send (CI)");

		  //  
		  ArrayList<String> uniqueAction=new ArrayList(Arrays.asList(uniqueActionSet.toArray()));
		//ction.sort();
		
		for(String agentInitial:uniqueAgentMap)
		{
			rowObj=new TreeMap<String,Object>();
			rowObj.put("Agent Email", agentInitial);
			for(Object action:uniqueAction)
			{
				rowObj.put(action.toString(), 0);
			}
			ArrayList<ArrayList<?>> agentDetails=filterDataByDimension(jsonData,agentInitial,2);
			
			
			
			for(ArrayList<?> row:agentDetails)
			{
				if(rowObj.get(row.get(1))!=null && !rowObj.get(row.get(1)).toString().equals(""))
				{
					
					//rowObj[row[1]]=rowObj[row[1]]+1;
					rowObj.put(row.get(1).toString(), Integer.parseInt(rowObj.get(row.get(1).toString()).toString())+1);
					if(row.get(3).toString().contains("us-cs-telephony"))
						{
						if(row.get(1).equals("CallConclusion"))
							rowObj.put("CallConclusion (IB)",Integer.parseInt(rowObj.get("CallConclusion (IB)").toString())+1);
						if(row.get(1).equals("Done"))
							rowObj.put("Send (IB)",Integer.parseInt(rowObj.get("Send (IB)").toString())+1);
						if(row.get(1).equals("Account Load"))
							rowObj.put("Account Load (IB)",Integer.parseInt(rowObj.get("Account Load (IB)").toString())+1);
					}
					else if(row.get(3).toString().equals("Fetch"))
					{
						if(row.get(1).equals("CallConclusion"))
							rowObj.put("CallConclusion (NOID)",Integer.parseInt(rowObj.get("CallConclusion (NOID)").toString())+1);
						if(row.get(1).equals("Done"))
						rowObj.put("Send (NOID)",Integer.parseInt(rowObj.get("Send (NOID)").toString())+1);
						if(row.get(1).equals("Account Load"))
								{
							rowObj.put("Account Load (NOID)",Integer.parseInt(rowObj.get("Account Load (NOID)").toString())+1);
								}
					}
				else
					{
					if(row.get(1).equals("CallConclusion"))
						rowObj.put("CallConclusion (CI)",Integer.parseInt(rowObj.get("CallConclusion (CI)").toString())+1);
					if(row.get(1).equals("Done"))
					rowObj.put("Send (CI)",Integer.parseInt(rowObj.get("Send (CI)").toString())+1);
					}
					if(row.get(1).equals("Account Load-SBChat")||row.get(1).equals("Account Load-eventToTalk")||row.get(1).equals("Account Load-repeat"))
						{
							rowObj.put("Account Load (CI)",Integer.parseInt(rowObj.get("Account Load (CI)").toString())+1);
							rowObj.put("Account Load",Integer.parseInt(rowObj.get("Account Load").toString())+1);
						}
					
				}

					
				}
			
			if(columnName==null)
				{
					columnName=new ArrayList(Arrays.asList(rowObj.keySet().toArray()));
				}
			agentData.add(maptoArray(rowObj));
			
			}
		agentActionDetils.add(columnName);
		agentActionDetils.addAll(agentData);
		
		
			return agentActionDetils;
				
		}
    
    public ArrayList maptoArray(TreeMap hm)
    {
    	ArrayList arr= new ArrayList();
    	for(Object o: hm.keySet())
    	{
    			arr.add(hm.get(o.toString()));
    	}
    	
		return arr;
    	
    }
    public ArrayList<ArrayList<?>> reorderAgentReport(ArrayList agentActionDetils)
    {
    	int index=0;
    	index=((ArrayList)agentActionDetils.get(0)).indexOf("Agent Email");
    	ArrayList row =null;
    	
    	ArrayList<ArrayList<?>> orderedReport=new ArrayList<ArrayList<?>>();
    			for(Object o:agentActionDetils)
    			{ row=new ArrayList();
    				String temp;
    				ArrayList a=(ArrayList)o;
    					temp=a.get(index).toString();
    					a.remove(index);
    					row.add(temp);
    					row.addAll(a);
    					orderedReport.add(row);
    			}
    		
    	return orderedReport;
    	
    }
    
	
}
