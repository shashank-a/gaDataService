package com.action;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.json.CDL;
import org.json.JSONArray;

import com.util.StringUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.account.AccountDetails;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.analytics.model.GaData;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.JsonObject;
import com.service.AnalyticsMailer;
import com.service.Authenticate;
import com.service.GADataService;
import com.service.GaDatastoreService;
import com.service.GaReportProcessor;
import com.util.AppCacheManager;
import com.util.AppMemCacheManager;
import com.util.DataStoreManager;
import com.util.GAUtil;
import com.util.GaBlobstoreService;
import com.util.ZipData;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ResourceBundle;
import java.util.zip.DataFormatException;

import com.service.UrlFetchServiceUtil;
import com.util.CsvUtil;

import java.beans.*;

// TODO: Auto-generated Javadoc
/**
 * The Class ActionServlet.
 */
@Controller
public class ActionServlet {
	
	
	/** The Constant CLIENT_ID. */
	final static String CLIENT_ID = "171068777204-uh3o3umebclqgdvd030ojm939l4rf3mr.apps.googleusercontent.com";
	
	/** The Constant CLIENT_SECRET. */
	final static String CLIENT_SECRET = "UpM_LVYRCLogNP4TTXIUg94a";
	
	/** The Constant REDIRECT_URL. */
	final static String REDIRECT_URL = "http://www.gadataservice.appspot.com/oauth2callback.do";
	
	/** The Constant SCOPE. */
	final static String SCOPE="https://www.googleapis.com/auth/analytics.readonly";
	
	/** The Constant mLogger. */
	private static final Logger mLogger = Logger.getLogger(ActionServlet.class.getPackage().getName());
	
	/** The appcachemanager. */
	AppCacheManager appcachemanager= new AppCacheManager();
	
	/** The resource bundle. */
	ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
	
	

	
	/**
	 * Oauth2callback.
	 *  Call Back method for google O auth 
	 *  this method is also responsible for storing refresh token in Datastore 
	 * @param request the request
	 * @param res the res
	 */
	@ResponseBody
	@RequestMapping("/oauth2callback.do")
	public void oauth2callback(HttpServletRequest request, HttpServletResponse res)
	{System.out.println("reached call back url..  oauth2callback");

		String refreshToken	= "";
		String accessToken 	= "";
		JSONObject userDetails	= null;
		String email			= null;
		String address="https://accounts.google.com/o/oauth2/token";

		String code		= request.getParameter("code");
		String error 	= request.getParameter("error");
		String grant_type="authorization_code";
		String offline = "offline";
		ObjectMapper mapper = new ObjectMapper();

		String payload ="code="+code+"&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&redirect_uri="+REDIRECT_URL+"&grant_type="+grant_type;
		System.out.println(payload);
		try{
			String responseString = UrlFetchServiceUtil.httpRequest(address, payload, "POST", "application/x-www-form-urlencoded", null);
			System.out.println("The response String "+responseString);
			TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String,String>>() {};
			HashMap<String,String> resultMap = mapper.readValue(responseString.toString(), typeRef);
			System.out.println("The responseMap "+resultMap);
			System.out.println("storing refresh token for::;"+resourceBundle.getString("email").toString());
			(new GaDatastoreService()).storeTempData(resourceBundle.getString("email").toString(),resultMap,"refresh_token");
			
			
			
		}
		catch(Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw 	= new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println("Error " + sw.toString());
		}
	}
		

	

	
	/**
	 * Gets the auth code.
	 *NOt being used
	 * @param req the req
	 * @param res the res
	 * @return the auth code
	 */
	public String  getAuthCode(HttpServletRequest req,HttpServletResponse res) 
	{	
		String redirectUri="http://www.gadataservice.appspot.com/oauth2callback.do";
		System.out.println("in getAuthCode");
		System.out.println("request string::"+req.getQueryString());
		
		//FilterJson
		String code=req.getParameter("code");
		String token=req.getParameter("accessToken");
		String dateFrom=req.getParameter("dateFrom");
		String rawDataFlag=req.getParameter("rawDataFlag");
		String tableId=req.getParameter("tableId");
		String dimension=req.getParameter("dimension");
		
		JSONObject gaFilter= new JSONObject();
		
		try {
			
			gaFilter.put("code",code);
			gaFilter.put("token", token);
			gaFilter.put("dateFrom", dateFrom.replaceAll("-", ""));
			gaFilter.put("rawDataFlag", rawDataFlag);
			gaFilter.put("tableId",tableId );
			gaFilter.put("dimension",dimension );
			
			
			System.out.println(gaFilter.toString());
			
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	 try
		{
		//Adding  filter params to google API request..
		 
		 String redirectString="https://accounts.google.com/o/oauth2/auth?client_id="+"171068777204-uh3o3umebclqgdvd030ojm939l4rf3mr.apps.googleusercontent.com"+"&redirect_uri="+redirectUri+"&response_type=code&scope=https://www.googleapis.com/auth/analytics.readonly&access_type=offline&approval_prompt=force&state="+gaFilter;
		 System.out.println("redirect URI::::"+redirectString);	
		 //res.sendRedirect(redirectString);
		 return redirectString;
			
		}
	
	
	 catch (Exception ee)
		 {
			 ee.printStackTrace();
			 
		 }
	 return "";
	}
	
	
	
	/**
	 * Gets the event details.
	 *
	 * @param req the req
	 * @param res the res
	 * @return the event details
	 * @throws ClassNotFoundException the class not found exception
	 */
	@RequestMapping("/getEventDetails.do")
	public String getEventDetails(HttpServletRequest req,HttpServletResponse res) throws ClassNotFoundException
	{
		String date=req.getParameter("dateFrom");
		String page=req.getParameter("page");
		String mode=req.getParameter("mode");
		String viewPage="sbEventData";
		if(date==null ||date.equals(""))
		{
			req.setAttribute("error", "Incorrect Date Format..");
			date=GAUtil.getCurrentDate();
			System.out.println("Current Date::::::::::::::::::::::::::::::"+date);
			req.setAttribute("date", date);	
		}
		if(mode==null||mode.equals(""))
		{
							mode="SBLive";
		}
		
		System.out.println("Trying to fetch Data for ++++++++"+mode);
		System.out.println("date::"+date);
		
		ArrayList<ArrayList<?>> sbRowData=null;
		
		String dimensionsSB="";
		
		
		 try {
			 GaReportProcessor gaService= new GaReportProcessor();
			 dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
			 if(mode.equals("StagingACTI"))
			 {
				 dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
				 
			 }
			 System.out.println("dimensions:::"+dimensionsSB);
			 //sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
			 sbRowData=GaDatastoreService.fetchGADataBatch(date, null, dimensionsSB, GAUtil.getkeyElementFromDimension(dimensionsSB), "SBLIVE", null);
			 System.out.println("Sb Row Data Received");
			 
			 
			 
			 if(sbRowData!=null)
			 {
				 System.out.println("Total Records:"+sbRowData.size());
				 if(page !=null && page.equals("csv"))
					{
					 
					    res.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
				        res.setHeader("Content-Disposition","attachment;filename=analyticsData.csv");
				        res.setCharacterEncoding("UTF-8");
				        res.getWriter().println(CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString());
				        // req.setAttribute("csvData", CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString());
				        System.out.println("printing csv data");
						
					}
			 	//compiledData=gaService.escapeGAData(sbRowData,0);
				 				
				 //System.out.println(CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString());
			 	System.out.println("Printing Compiled Data");
				req.setAttribute("rowData", GaDatastoreService.convertObjectToJson(sbRowData).toString());
				req.setAttribute("dimension", GAUtil.getkeyElementFromDimension(dimensionsSB));
				req.setAttribute("error", "Total Records:"+sbRowData.size());
				System.out.println("page:::"+page);
				
				
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return viewPage;
		
	}
	
	/**
	 * Redirector.
	 *
	 * @param req the req
	 * @param res the res
	 * @return the string
	 */
	@RequestMapping("/redirector.do")
	public String redirector(HttpServletRequest req,HttpServletResponse res)
	{
		
		String page=req.getParameter("page");
		System.out.println("page::"+page);
		
		if(page!=null && !page.equals(""))
		{
			return page;
		}
		return "mainLayout";	
			
	}
	
	/**
	 * Gets the csv data.
	 *
	 * @param req the req
	 * @param res the res
	 * @return the csv data
	 */
	@ResponseBody
	@RequestMapping("/getCsvData.do")
	public void getCsvData(HttpServletRequest req,HttpServletResponse res)
	{
		String date=req.getParameter("dateFrom");
		String page=req.getParameter("page");
		String mode=req.getParameter("mode");
		String viewPage="";
		if(date==null ||date.equals(""))
		{
			req.setAttribute("error", "Incorrect Date Format..");
			date=GAUtil.getCurrentDate();
			System.out.println("Current Date::::::::::::::::::::::::::::::"+date);
			req.setAttribute("date", date);	
		}
		if(mode==null||mode.equals(""))
		{
							mode="SBLive";
		}
		
		System.out.println("Trying to fetch Data for ++++++++"+mode);
		System.out.println("date::"+date);
		
		ArrayList<ArrayList<?>> sbRowData=null;
		
		String dimensionsSB="";
		
		
		 try {
			 GaReportProcessor gaService= new GaReportProcessor();
			 dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3";
			 System.out.println("dimensions:::"+dimensionsSB);
			// sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
			 System.out.println("Sb Row Data Received");
			 
			 ArrayList<ArrayList<?>> compiledData= new ArrayList<ArrayList<?>>();
			 
			 if(sbRowData!=null)
			 {
				 if(page !=null && page.equals("csv"))
					{
					 
					 	res.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
				        res.setHeader("Content-Disposition","attachment;filename=analyticsData.csv");
				        res.setCharacterEncoding("UTF-8");
				        res.getWriter().println(CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString());
				        
				        // req.setAttribute("csvData", CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString());
				        System.out.println("printing csv data");
						
					}
			 	
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}	
	
	
	
	
	
	
	/**
	 * Gets the cache data.
	 *
	 * @param req the req
	 * @param res the res
	 * @return the cache data
	 */
	@RequestMapping("/getDSData.do")
	public void getCacheData(HttpServletRequest req,HttpServletResponse res)
	{
		String key=req.getParameter("key");
		String dateFrom=req.getParameter("dateFrom");
		byte [] jsonData=null;
		try {
			
			System.out.println("fetching the data from Datastore");
			if(dateFrom!=null && !dateFrom.equals(""))
			{
				jsonData=DataStoreManager.get(key, dateFrom.replaceAll("-", ""));	
			}
			else
			{
				jsonData=DataStoreManager.get(key);
			}
			
			if(jsonData!=null )
			{
				res.getWriter().println(ZipData.extractBytes(jsonData).toString());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
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
     * Gets the batch data.
     *
     * @param req the req
     * @param res the res
     * @return the batch data
     */
    @RequestMapping("/getBatchData.do")
    @ResponseBody
    public void getBatchData(HttpServletRequest req,HttpServletResponse res)
    {
    	String dateFrom=req.getParameter("dateFrom");
    	String dataType=req.getParameter("dataType");
    	String printFlag=req.getParameter("printFlag");
    	
    	String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
    	ArrayList<ArrayList<?>> rows=null;
    	try {
    		
    		
    			String processedData=null;
    			
    			//res.getWriter().println(rows.toString());
    			String key="GaDataObject_"+"SBLIVE"+"_"+dateFrom.replaceAll("-", "")+"_"+GAUtil.getkeyElementFromDimension(dimensions);
    			if(dataType!=null && !dataType.equals("") && (dataType.equals("csv")||dataType.equals("tsv")))
    			{
    					
    					byte [] csvData=DataStoreManager.get(key+"_"+dataType, dateFrom.replaceAll("-", ""));
    					if(csvData!=null)
    					{
    						
								processedData=(String)ZipData.extractBytes(csvData);
    					}else
    					{
    						rows=GaDatastoreService.fetchGADataBatch(dateFrom, null, dimensions, GAUtil.getkeyElementFromDimension(dimensions), "SBLIVE", null);
    						if(rows!=null)
    			    		{
    							
    							System.out.println("batch Size::::"+rows.size());
	    						processedData=CsvUtil.formatCsv(rows,Charset.defaultCharset()).toString();
	    						byte [] compressData=ZipData.compressBytes(processedData);
	    						System.out.println(dataType+" DataCompressed");
	    						DataStoreManager.set(key+"_"+dataType, dateFrom.replaceAll("-", ""), compressData);
    			    		}
    					}
    					
    					
   					 	res.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
   				        res.setHeader("Content-Disposition","attachment;filename=analyticsData."+dataType);
   				        res.setCharacterEncoding("UTF-8");
   				        
   				        
   				        // req.setAttribute("csvData", CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString());
   				        System.out.println("printing csv data");
   				
    				
    			}
    			if(dataType!=null && dataType.equals("json"))
    			{
    				
    				byte [] jsonData=DataStoreManager.get(key+"_json", dateFrom.replaceAll("-", ""));
    				
    				if(jsonData==null)
    				{
    						
		    				rows=GaDatastoreService.fetchGADataBatch(dateFrom, null, dimensions, GAUtil.getkeyElementFromDimension(dimensions), "SBLIVE", null);
		    				if(rows!=null)
				    		{
		    				System.out.println("batch Size::::"+rows.size());
		    				//res.getWriter().println(GaDatastoreService.convertObjectToJson(rows).toString());
		    				processedData=GaDatastoreService.convertObjectToJson(rows).toString();
		    				byte [] compressData=ZipData.compressBytes(processedData);
							System.out.println(" JSON DataCompressed");
							DataStoreManager.set(key+"_json", dateFrom.replaceAll("-", ""), compressData);
							System.out.println("JSON Data Stored in DataStore  :  "+key+"_json");
							
				    		}
		    		}else
		    		{
		    			processedData=(String)ZipData.extractBytes(jsonData);
		    			
		    		}
		    				
		    		
    			}
    			
    			if(processedData!=null && printFlag!=null)
    			{
    				res.getWriter().println(processedData);
    			}
    	
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }
    
    /**
     * Gets the batch csv.
     *
     * @param req the req
     * @param res the res
     * @return the batch csv
     * @throws JSONException the jSON exception
     */
    @RequestMapping("/getBatchCSV.do")
    @ResponseBody
    public void getBatchCSV(HttpServletRequest req,HttpServletResponse res) throws org.json.JSONException
    {
    	
    	String dateFrom=req.getParameter("dateFrom");
    	System.out.println("dataFrom:::;"+dateFrom);
    	String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
    	ArrayList<ArrayList<?>> rows=null;
    	String processedData=null;
    	try {
    		rows=GaDatastoreService.fetchGADataBatch(dateFrom, null, dimensions, GAUtil.getkeyElementFromDimension(dimensions), "SBLIVE", null);
    		if(rows!=null)
    		{
    			String jsonData;
    			System.out.println("batch Size::::"+rows.size());
    			ArrayList agentActionData=new GaReportProcessor().AgentActionReport(rows);
    			//String agentReport=GaDatastoreService.convertObjectToJson(arr).toString();
    			//DataStoreManager.set("AgentActionCount_JSON_"+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", ""),ZipData.compressBytes(agentReport.toString()));
    			//processedData=CsvUtil.formatCsv(agentActionData,Charset.defaultCharset()).toString();
    			//DataStoreManager.set("AgentActionCount_CSV"+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", ""),ZipData.compressBytes(processedData.toString()));
    			//jsonData=ZipData.extractBytes(DataStoreManager.get("AgentActionCount_JSON_"+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", "")));
    			//System.out.println("jsonData"+jsonData);
    			
    			ArrayList<ArrayList<?>> arr=new ArrayList<ArrayList<?>>(agentActionData);
    			DataStoreManager.set("AgentActionCount_"+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", ""),ZipData.compressBytes(GaDatastoreService.convertObjectToJson(arr).toString()));
    			arr=new GaReportProcessor().reorderAgentReport(agentActionData);
    			processedData=CsvUtil.formatCsvfromArray(arr,'\t',Charset.defaultCharset()).toString();
    			DataStoreManager.set("AgentActionCount_tsv"+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", ""),ZipData.compressBytes(processedData.toString()));
    			res.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
			    res.setHeader("Content-Disposition","attachment;filename=AgentActionCount"+dateFrom.replaceAll("-", "")+".tsv");
			    res.setCharacterEncoding("UTF-8");
    			res.getWriter().println(processedData);
    		}
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    
    }
    
    /**
     * Gets the agent details.
     *
     * @param req the req
     * @param res the res
     * @return the agent details
     * @throws JSONException the jSON exception
     */
    @RequestMapping("/getAgentDetails.do")
    @ResponseBody
    public void getAgentDetails(HttpServletRequest req,HttpServletResponse res) throws org.json.JSONException
    {
    	
    	String dateFrom=req.getParameter("dateFrom");
    	System.out.println("dataFrom:::;   "+dateFrom);
    	String fileType=req.getParameter("type");
    	
    	try {
    		byte[] b=DataStoreManager.get("AgentActionCount_"+fileType+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", ""));
    		
    	
    	if(b.length>1)
    	{
    		
				String csvData=ZipData.extractBytes(b);
				res.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
			    res.setHeader("Content-Disposition","attachment;filename=AgentActionCount"+dateFrom.replaceAll("-", "")+"."+fileType.toLowerCase());
			    res.setCharacterEncoding("UTF-8");
    			res.getWriter().println(csvData);
    	
    	}
    
    } catch (IOException
			| DataFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	
    }
    
    /**
     * Gets the key value.
     *
     * @param req the req
     * @param res the res
     * @return the key value
     * @throws JSONException the jSON exception
     */
    @RequestMapping("/getKeyValue.do")
    @ResponseBody
    public void getKeyValue(HttpServletRequest req,HttpServletResponse res) throws org.json.JSONException
    {
    	String date=req.getParameter("dateFrom");
    	String project=req.getParameter("project");
    	String fileType=req.getParameter("fileType");
    	String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
    	ArrayList<ArrayList<?>> arrayList=null;
    	String processedData=null;
    	try {
    		arrayList=new GaDatastoreService().fetchGADataBatch(date,null,dimensions,GAUtil.getkeyElementFromDimension(dimensions), project, null);
    		System.out.println("processed Data"+arrayList.size());
    		processedData=CsvUtil.formatCsvfromArray(arrayList,',',Charset.defaultCharset()).toString();
			res.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
		    res.setHeader("Content-Disposition","attachment;filename="+project+"_"+date.replaceAll("-", "")+".csv");
		    res.setCharacterEncoding("UTF-8");
			res.getWriter().println(processedData);
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    /**
     * Send error message.
     *
     * @param req the req
     * @param res the res
     * @throws JSONException the jSON exception
     */
    @RequestMapping("/sendErrorEmail.do")
    @ResponseBody
    public void sendErrorMessage(HttpServletRequest req,HttpServletResponse res) throws org.json.JSONException
    {
    	String testMessage=req.getParameter("msg");
    	if(testMessage==null||!testMessage.equals(""))
    	{
    		testMessage="This is test Email";
    	}
    	
    	try {
    		AnalyticsMailer am=new AnalyticsMailer();
			am.initMail("",testMessage,"",resourceBundle.getString("errorMail").toString(),"GA Exception","","", null);
			
			res.setCharacterEncoding("UTF-8");
			res.getWriter().println("Mail Sent");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
   }
    
	/**
	 * Gets the gA service.
	 *   This method can be used to add new service account for reporting service.
	 *   It is recommended to use a common dev account for this service  which is having access to all the analytics application.
	 * @param req the req
	 * @param res the res
	 * @return the gA service
	 */
	@RequestMapping("/getGAService.do")
	public void getGAService(HttpServletRequest req,HttpServletResponse res)
	{String redirectString="";
	String accessToken;
	
	System.out.println("inside getGASecvice");
    	//String code=req.getParameter("code");
    	try {
    		
    			GaDatastoreService datastoreService= new GaDatastoreService();
    			System.out.println("setting url");
    			String redirectUri="http://www.gadataservice.appspot.com/oauth2callback.do";
	   		  	redirectString="https://accounts.google.com/o/oauth2/auth?client_id="+"171068777204-uh3o3umebclqgdvd030ojm939l4rf3mr.apps.googleusercontent.com"+"&redirect_uri="+redirectUri+"&response_type=code&scope=https://www.googleapis.com/auth/analytics.readonly&access_type=offline&approval_prompt=force";
	   		  	System.out.println("redirect URI::::"+redirectString);
	   		 	res.sendRedirect(redirectString);
    			
    			HashMap hm=null;
    		if(hm!=null || hm.get("refresh_token")!=null)
    		{	System.out.println("refresh Toekn found");
    			accessToken=new Authenticate().updateAccessTokenWithResfreshToken(hm.get("refresh_token").toString());
    			System.out.println("accessToken");
    			//authenticate.gaQurey(null,accessToken, "20140516", "20140516",true,list,dimensions, resourceBundle.getString("SBLive"),"", null );
    		}

   				} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		 
	}
			
    
}
