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
import com.google.api.client.util.Base64;
import com.util.DataStoreManager;
import com.util.StringUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class GaReportProcessor.
 */
public class GaReportProcessor {
	
	/** The Constant mLogger. */
	private static final Logger mLogger = Logger.getLogger(GaReportProcessor.class.getPackage().getName());
	
	
	
	/**
	 * Gets the unique dimension from ga data.
	 *
	 * @param rowData the row data
	 * @param index the index
	 * @return the unique dimension from ga data
	 */
	public HashSet getUniqueDimensionFromGAData (ArrayList<ArrayList<?>> rowData,int index)
	{
		/**
		 * Dimension indexes
		 * 1:accountNumber||AccountName
		 * 2.Action
		 * 3.AgentEmail
		 * 4.ConnId or 'Fetch' in case of account Fetch
		 * 5.IncomingANI
		 * 6.TimeStamp
		 * 7.totalValue // total value here is not considered for any calculation.
		 * 
		 * */
		//LinkedHashMap <String,String> hm=new LinkedHashMap<String,String>();
		HashSet <String> hs= new HashSet<String>();
		
		for (ArrayList <?> rowValues : rowData) {
			  
			String tempacc=rowValues.get(index).toString();
			//hm.put(tempacc,tempacc);
			hs.add(tempacc);
		   	}
	
		return hs;
	}
	
	/**
	 * Filter data by dimension index and provided value.
	 *for eg:
	 *filterDataByDimension(jsonData,'shashank.ashokkumar@a-cti.com',2)
	 * This method will return all the records in raw json with value 'shashank.ashokkumar@a-cti.com' at index 2 of the array.
	 * @param rowData the row data
	 * @param value the value
	 * @param index the index
	 * @return the array list
	 */
	public ArrayList<ArrayList<?>> filterDataByDimension(ArrayList<ArrayList<?>> rowData,String value,int index)
	{
		System.out.println("inside filterDataByDimension:: "+value);
		ArrayList<ArrayList<?>> filteredList=new ArrayList<ArrayList<?>>();
		//System.out.println("index::"+index+"value::"+value);
		 try {
			 Pattern p=null;
			 p= Pattern.compile(value);	 
			 
			for(ArrayList<?> entry: rowData)
			{
				Matcher m = p.matcher(entry.get(index).toString().trim());
				if(m.matches())
				{//System.out.println("entry::"+entry.get(index).toString());
					filteredList.add(entry);
				}
			}
		} catch(java.util.regex.PatternSyntaxException reg1)
		{
			System.out.println("regError:"+value+"index:"+index);
			reg1.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return filteredList;
	}
	
	/**
	 * Escape ga data.
	 *
	 * @param rowData the row data
	 * @param index the index
	 * @return the array list
	 */
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
	
	
	/**
	 * Gets the agent detail report.
	 *
	 * @param rowData the row data
	 * @return the agent detail report
	 * @throws JsonGenerationException the json generation exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public LinkedHashMap<String, HashMap> getAgentDetailReport(ArrayList<ArrayList<?>> rowData) throws JsonGenerationException, JsonMappingException, IOException
	{
		
		LinkedHashMap<String, HashMap> processedJSON=null;
		HashSet uniqueAgent=getUniqueDimensionFromGAData(rowData,2);
		HashSet uniqueAction=getUniqueDimensionFromGAData(rowData,1);
		System.out.println("uniqueAgent::"+uniqueAgent);
		for(Object agent:uniqueAgent)
		{
			LinkedHashMap rowObj=new LinkedHashMap();
			if(!StringUtil.isEmail(agent.toString()))
					{
					agent =StringUtil.decodeBase64(agent.toString());
					}
			rowObj.put("Agent Email", agent.toString());
			System.out.println("email:::"+agent.toString());
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
	

    /**
     * Gets the stack trace.
     *
     * @param t the t
     * @return the stack trace
     */
    public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }


    /**
     * Prints the stack trace.
     *
     * @param t the t
     */
    public static void printStackTrace(Throwable t) {
	mLogger.error(getStackTrace(t));
    }
    
    /**
     * Prints the account data.
     *
     * @param accountDetails the account details
     */
    public void printAccountData(ArrayList<AccountDetails> accountDetails)
    {	
    	for(AccountDetails acc:accountDetails)
    	{
    		System.out.println("acc:"+acc.getAcctNum()+"label:"+acc.getEventLabel()+"count:"+acc.getSendCount()+"CC:"+acc.getCallConclusion()+"time::"+acc.getCallTotal());
    	}
    	
    }
    
    
    
    /**
     * Agent action report.
     *
     * @param jsonData the json data
     * @return the array list
     * @throws JsonGenerationException the json generation exception
     * @throws JsonMappingException the json mapping exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ArrayList AgentActionReport(ArrayList<ArrayList<?>> jsonData) throws JsonGenerationException, JsonMappingException, IOException
    {
    	
    	 /*
    	  * Fetching unique agent email and unique actions from rawData pulled from DataStore
        **/
    	  HashSet<String> uniqueAgentMap=getUniqueDimensionFromGAData(jsonData,2);
    	  HashSet<String> uniqueActionSet=getUniqueDimensionFromGAData(jsonData,1);
    	  
		  HashSet<String> uniqueAgentSet=null;
		  
		  
		  TreeMap<String,Object> rowObj= null;
		  ArrayList agentData=new ArrayList<String>();
		  ArrayList agentActionDetils= new ArrayList();
		  List columnName=null;
		  
		  //adding additional action as Total of Inbound(IB) , Fetch (NOID), CustomInteraction(CI) for various actions performed.
		  //This feature was added on request of performance management team
		  	uniqueActionSet.add("Account Load (IB)");
		  	uniqueActionSet.add("Account Load (NOID)");
		  	uniqueActionSet.add("Account Load (CI)");
			uniqueActionSet.add("CallConclusion (IB)");
			uniqueActionSet.add("CallConclusion (NOID)");
			uniqueActionSet.add("CallConclusion (CI)");
			uniqueActionSet.add("Send (IB)");
			uniqueActionSet.add("Send (NOID)");
			uniqueActionSet.add("Send (CI)");
			uniqueActionSet.add("Annotation/close(IB)");
			uniqueActionSet.add("Annotation/close(NOID)");
			uniqueActionSet.add("Annotation/close(CI)");
			//added for DeadAIR call
			//IT requested for this feature
			uniqueActionSet.add("CallConclusion(DAC)");
			
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
			
			//fetching all the records for one agent as agentDetails
			ArrayList<ArrayList<?>> agentDetails=filterDataByDimension(jsonData,agentInitial,2);
			
			//adding up action count from agentDetails
			for(ArrayList<?> row:agentDetails)
			{
				{
					
					//rowObj[row[1]]=rowObj[row[1]]+1;
					rowObj.put(row.get(1).toString(), Integer.parseInt(rowObj.get(row.get(1).toString()).toString())+1);
					//filtering data on the basis of connid for inbound call
					if(row.get(3).toString().contains("us-cs-telephony"))
						{
						if(row.get(1).equals("CallConclusion"))
							{
							 if(row.get(6).equals("Dead Air Call") ) 
								 rowObj.put("CallConclusion(DAC)",Integer.parseInt(rowObj.get("CallConclusion(DAC)").toString())+1);
							 else
								 rowObj.put("CallConclusion (IB)",Integer.parseInt(rowObj.get("CallConclusion (IB)").toString())+1);
							}
						else if(row.get(1).equals("Done"))
							rowObj.put("Send (IB)",Integer.parseInt(rowObj.get("Send (IB)").toString())+1);
						else if(row.get(1).equals("Account Load"))
							rowObj.put("Account Load (IB)",Integer.parseInt(rowObj.get("Account Load (IB)").toString())+1);
						else if(row.get(1).equals("Annotation/close"))
							rowObj.put("Annotation/close(IB)",Integer.parseInt(rowObj.get("Annotation/close(IB)").toString())+1);
							
					}//filtering data on the basis of connid for Fetch
					else if(row.get(3).toString().equals("Fetch"))
					{
						if(row.get(1).equals("CallConclusion"))
							rowObj.put("CallConclusion (NOID)",Integer.parseInt(rowObj.get("CallConclusion (NOID)").toString())+1);
						else if(row.get(1).equals("Done"))
							rowObj.put("Send (NOID)",Integer.parseInt(rowObj.get("Send (NOID)").toString())+1);
						else if(row.get(1).equals("Account Load"))
								{
							rowObj.put("Account Load (NOID)",Integer.parseInt(rowObj.get("Account Load (NOID)").toString())+1);
								}
						else if(row.get(1).equals("Annotation/close"))
							{rowObj.put("Annotation/close(NOID)",Integer.parseInt(rowObj.get("Annotation/close(NOID)").toString())+1);
							}

					}//filtering data for Custom Interaction
				else
					{
					if(row.get(1).equals("CallConclusion"))
						rowObj.put("CallConclusion (CI)",Integer.parseInt(rowObj.get("CallConclusion (CI)").toString())+1);
					else if(row.get(1).equals("Done"))
					rowObj.put("Send (CI)",Integer.parseInt(rowObj.get("Send (CI)").toString())+1);
					
					else if(row.get(1).equals("Account Load-SBChat")||row.get(1).equals("Account Load-eventToTalk")||row.get(1).equals("Account Load-repeat"))
						{
							rowObj.put("Account Load (CI)",Integer.parseInt(rowObj.get("Account Load (CI)").toString())+1);
							rowObj.put("Account Load",Integer.parseInt(rowObj.get("Account Load").toString())+1);
						}
					else if(row.get(1).equals("Annotation/close"))
						{rowObj.put("Annotation/close(CI)",Integer.parseInt(rowObj.get("Annotation/close(CI)").toString())+1);
						}
				
				}					
				}
			}
			
			if(columnName==null)
				{
					columnName=new ArrayList(Arrays.asList(rowObj.keySet().toArray()));
				}
			agentData.add(maptoArray(rowObj));
			
			}
		//adding first row as column name
		agentActionDetils.add(columnName);
		//appending action count to the table.
		agentActionDetils.addAll(agentData);
		
		
			return agentActionDetils;
				
		}
    
    /**
     * Mapto array.
     *
     * @param hm the hm
     * @return the array list
     */
    public ArrayList maptoArray(TreeMap hm)
    {
    	ArrayList arr= new ArrayList();
    	for(Object o: hm.keySet())
    	{
    			arr.add(hm.get(o.toString()));
    	}
    	
		return arr;
    	
    }
    
    /**
     * Reorder agent report.
     *
     * @param agentActionDetils the agent action detils
     * @return the array list
     */
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
