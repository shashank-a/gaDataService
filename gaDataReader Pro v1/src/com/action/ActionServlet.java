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

@Controller
public class ActionServlet {
	
	
	final static String CLIENT_ID = "171068777204-uh3o3umebclqgdvd030ojm939l4rf3mr.apps.googleusercontent.com";
	final static String CLIENT_SECRET = "UpM_LVYRCLogNP4TTXIUg94a";
	final static String REDIRECT_URL = "http://www.gadataservice.appspot.com/oauth2callback.do";
	final static String SCOPE="https://www.googleapis.com/auth/analytics.readonly";
	final static String APPLICATION_NAME="GA Web Service";
	final static String USER_ID="shashank.ashokkumar@a-cti.com";
	static  String TABLE_ID = "56596375";
	
	private static final Logger mLogger = Logger.getLogger(ActionServlet.class.getPackage().getName());
	AppCacheManager appcachemanager= new AppCacheManager();
	
	@RequestMapping("/getGaData.do")
	public String getGAData(HttpServletRequest req,HttpServletResponse res) throws NoSuchMethodException
	{
		System.out.println("inside getGAData");
		System.out.println("state::;"+req.getParameter("state"));
	try{		
			String accessToken="";
			GaData gaData=null;
			//String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel";
			HttpSession session=req.getSession(true);
			ArrayList<GaData> list=new ArrayList<GaData>();
			ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
			System.out.println("request string::"+req.getQueryString()); 
			
					
			ArrayList<ArrayList<?>> rowData=null;
			GoogleTokenResponse googleTokenResponse=null;
			/*
			 * request Parameters
			*/
			String code=req.getParameter("code");
			String token=req.getParameter("accessToken");
			String dateFrom=req.getParameter("dateFrom");
			String rawDataFlag=req.getParameter("rawDataFlag");
			String tableId=req.getParameter("tableId");
			//String dimensions=req.getParameter("dimension");
			if(req.getParameter("state")!=null)
			{
			JSONObject gaFilters=new JSONObject(req.getParameter("state")); 
			//code=gaFilters.getString("code");
			dateFrom=gaFilters.getString("dateFrom");
			//rawDataFlag=gaFilters.getString("rawDataFlag");
			tableId=gaFilters.getString("tableId");
			//dimensions=gaFilters.getString("dimension");
			}
			//System.out.println("dimensions::"+dimensions);
			//String dateTo=req.getParameter("dateTo");
						
			System.out.println("token:::"+token);
			System.out.println("dateFrom:::"+dateFrom);
			System.out.println("code:::"+code);	
			System.out.println("tableId::"+tableId+"----"+resourceBundle.getString(tableId));
			Authenticate  authenticate =new Authenticate();
			System.out.println("instance created");
			 
			String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
			if(dateFrom!=null)
			{System.out.println("Fetching row data");
				//rowData=GaDatastoreService.fetchGAData(dateFrom,dimensions, GAUtil.getkeyElementFromDimension(dimensions),tableId);
			
				if(rowData==null)
				{
					int z=0;
				
							rowData=new ArrayList<ArrayList<?>>();
							//if(token.equals("null")||token.equals(""))
							System.out.println("checking token::");
							if(token==null)
							{
								System.out.println("no session  obj");
								googleTokenResponse=authenticate.getData(code);
								if(googleTokenResponse==null )
								{
									//NO response token  req redirected to get access token...
									System.out.println("getAuth Code");
									
									res.sendRedirect(getAuthCode(req,res));
								}
								else if(googleTokenResponse.getRefreshToken()!=null)					
								{
									authenticate.gaQurey(googleTokenResponse, googleTokenResponse.getAccessToken(), dateFrom, dateFrom,false,list,dimensions, resourceBundle.getString(tableId), null, null);
									accessToken="accessToken";
								//	session.setAttribute("refreshToken", googleTokenResponse);
								}
							}
							else
							{
								System.out.println("in else block");
								//googleTokenResponse=(GoogleTokenResponse)session.getAttribute("tokenResponse");
								String temptoken=(authenticate.loadData(new GoogleCredential())).getRefreshToken();
								GoogleTokenResponse temp=(GoogleTokenResponse)authenticate.getNewToken(temptoken);
								System.out.println(" new token response fetched from GoogleRefreshTokenRequest "+temp.getAccessToken());
								accessToken="someValue";
								authenticate.gaQurey(temp,temp.getAccessToken(), dateFrom, dateFrom,true,list,dimensions, resourceBundle.getString(tableId), null, null);
								
							}
							
							
							while(z<list.size())
							{
								gaData=list.get(z);
								//rowData.addAll(gaData.getRows());
								z++;
								ArrayList<ArrayList<String>> dataSet=new ArrayList(gaData.getRows());
								rowData.addAll(dataSet);
							}
							//GaDatastoreService.storeGAData(dateFrom,rowData, dimensions, GAUtil.getkeyElementFromDimension(dimensions), tableId);
							
				}
				if(rowData!=null)
				{  
				GaReportProcessor gaService= new GaReportProcessor();
				 rowData=gaService.escapeGAData(rowData,0);
					if(rawDataFlag==null)
					{
						/*ArrayList<AccountDetails> accountDetails=(new GaReportProcessor()).getAccountDataFromGaData(rowData);
						System.out.println("accountDetails::::"+accountDetails.size());
						req.setAttribute("accountData", accountDetails);*/	
					}else
					{
						req.setAttribute("dataTable", rowData);
					}
					
					req.setAttribute("dimensions", (dimensions.replaceAll("ga:","")).split(","));
					
				}
			}
			
					accessToken="someValue";	
					System.out.println("back To Controller");
					req.setAttribute("rowData", GaDatastoreService.convertObjectToJson(rowData).toString());
					System.out.println("after token fetch"+accessToken);
					req.setAttribute("dateFrom",req.getParameter("dateFrom"));
					req.setAttribute("accessToken", accessToken);
					req.setAttribute("rawDataFlag", rawDataFlag);
					
					System.out.println("Printing GData Object...");
					
				   req.setAttribute("headers",null);	
				   System.out.println("accountDataAdded");
				  
				   System.out.println("REquest attributes Added->>>");
				   System.out.println("calling...account data::");
				   
			} catch (Exception e) {
				printStackTrace(e);
			}
		
		System.out.println("End of function");

			return "search";
	}
	@RequestMapping("/getCallReport.do")
	public String mergeGaData(HttpServletRequest req,HttpServletResponse res)
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
		//ArrayList<ArrayList<?>> v2RowData=null;
		
		String dimensionsSB="";
		//String dimensionsV2="";
		if(page!=null){
				if( page.equals("cdr")){
					viewPage="callDataReport";
					dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3";
					}
				if(page.equals("car"))
				{
					viewPage="callActionReport";
					dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3";
				}
				if(page.equals("obr"))
				{
					viewPage="outboundReport";
					dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
				}
				if(page.equals("cxrData"))
				{
					viewPage="feedbackData";
					dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
				}
				if(page.equals("msgNotes"))
				{
					viewPage="sbNotes";
					dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
				}
				
				
				req.setAttribute("page",page);
		}
		 try {
			 GaReportProcessor gaService= new GaReportProcessor();
			 
			 System.out.println("dimensions:::"+dimensionsSB);
			 //sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
			 sbRowData=GaDatastoreService.fetchGADataBatch(date, null, date, GAUtil.getkeyElementFromDimension(dimensionsSB), "SBLIVE", null);
			 System.out.println("Sb Row Data Received");
			 
			 ArrayList<ArrayList<?>> compiledData= null;
			 ArrayList<ArrayList<?>> filteredData=null;
			 
			 if(sbRowData!=null)
			 {GaReportProcessor gaReportProcessor= new GaReportProcessor();
			 	compiledData=gaService.escapeGAData(sbRowData,0);
			 	
			 	if(page.equals("obr"))
			 	{
			 		compiledData=gaReportProcessor.filterDataByDimension(compiledData,"Outbound\\sCall\\-*[A-Z]*",1);
			 	}
			 	if(page.equals("cxrData"))
			 	{
			 		 filteredData=gaReportProcessor.filterDataByDimension(compiledData,"Associate\\sThumbs\\sUp\\sData|Associate\\sThumbs\\sDown\\sData",1);
			 		//filteredData.addAll(gaReportProcessor.filterDataByDimension(compiledData,"Associate Thumbs Up Data",1));
			 		gaService.escapeGAData(filteredData,6);
			 		System.out.println(filteredData.size());
			 		compiledData=filteredData;
			 		
			 	}if(page.equals("msgNotes"))
			 	{
			 		filteredData=gaReportProcessor.filterDataByDimension(compiledData,"SB\\s-\\sNotes",1);
				 	gaService.escapeGAData(filteredData,6);
			 		System.out.println(filteredData.size());
			 		compiledData=filteredData;
			 	}
			 	
			 	
			 	
//			 	for(ArrayList al:compiledData)
//			 	{
//			 		System.out.println("->"+al.get(1));
//			 	}
			 	
			 
			 	System.out.println("Printing Compiled Data");
				req.setAttribute("rowData", GaDatastoreService.convertObjectToJson(compiledData).toString());
				req.setAttribute("dimension", GAUtil.getkeyElementFromDimension(dimensionsSB));
				req.setAttribute("error", "Total Records:"+compiledData.size());
				System.out.println("page:::"+page);
			 }else
			 {
				 //viewPage="search";
				// viewPage="callDataReport";
			 }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return viewPage;
	}
	
	
	
	@RequestMapping("/getAcccountDetails.do")
	public String getAccountDetails(HttpServletRequest req,HttpServletResponse res) throws IOException
	{
			String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2";
			System.out.println("dateFrom::;"+req.getParameter("dateFrom"));
			System.out.println("category::;"+req.getParameter("category").trim());
			
			req.setAttribute("accNo",req.getParameter("category").trim());
			//new GaReportProcessor().getEventDescriptionForCategory(GaDatastoreService.fetchGAData(req.getParameter("dateFrom").trim()), req.getParameter("category").trim());
			//req.setAttribute("agentData", new GaReportProcessor().getEventDescriptionForCategory(GaDatastoreService.fetchGAData(req.getParameter("dateFrom").trim(), dimensions, GAUtil.getkeyElementFromDimension(dimensions), null), req.getParameter("category").trim())); 
			return "agentWiseData";
			
	}
	
	@ResponseBody
	@RequestMapping("/oauth2callback.do")
	public void oauth2callback(HttpServletRequest request, HttpServletResponse res)
	{

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
			String responseString = UrlFetchServiceUtil.httpRequest(address, payload, "POST", "application/x-www-form-urlencoded");
			System.out.println("The response String "+responseString);
			TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String,String>>() {};
			HashMap<String,String> resultMap = mapper.readValue(responseString.toString(), typeRef);
			System.out.println("The responseMap "+resultMap);
			(new GaDatastoreService()).storeTempData("shashank.ashokkumar@a-cti.com",resultMap,"refresh_token");
			
			
			
		}
		catch(Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw 	= new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println("Error " + sw.toString());
		}
	}
		

	

	
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
	
	@RequestMapping("/getCallHistoryReport.do")
	public String getSBCallReport(HttpServletRequest req,HttpServletResponse res)
	{
		String date=req.getParameter("dateFrom");
		String viewPage="";
		if(date==null ||date.equals(""))
		{req.setAttribute("reportTitle", "Account LoadTime");
			
			req.setAttribute("error", "Incorrect Date Format..");
			date=GAUtil.getCurrentDate();
			
		}
		req.setAttribute("date", date);
		String page=req.getParameter("page");
		if(page!=null){
			if( page.equals("chr")){
				viewPage="callHistoryReport";}
			req.setAttribute("page",page);
	}
		ArrayList<ArrayList<?>> sbRowData=null;
		ArrayList<ArrayList<?>> v2RowData=null;
		
		String dimensionsSB="";
		 
		 try {
			 GaReportProcessor gaService= new GaReportProcessor();
			 dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue3,ga:customVarValue4";
			 System.out.println("dimensions:::"+dimensionsSB);
			// sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB), "SBLive");
			 if(sbRowData!=null)
			 {
			 System.out.println("Sb Row Data Received"+sbRowData.size());
			 
			 
			 sbRowData=gaService.escapeGAData(sbRowData,0);
			 
			 //commenting only for account load report
//			 sbRowData= new GaReportProcessor().filterDataByDimension(sbRowData,"Account Load",1);
//			 System.out.println("Sb Row Data Filtered"+sbRowData.size());
//			    sbRowData=gaService.escapeGAData(sbRowData,0);
			 
			 
			 
			   /* ArrayList<AccountDetails> accountDetails=gaService.getAccountDataFromGaData(sbRowData);
				System.out.println("accountDetails::::"+accountDetails.size());
				req.setAttribute("accountData", accountDetails);*/
			 
			 System.out.println("Printing Compiled Data");
			 
			 req.setAttribute("rowData", GaDatastoreService.convertObjectToJson(sbRowData).toString());
			 req.setAttribute("dimension", GAUtil.getkeyElementFromDimension(dimensionsSB));
			 req.setAttribute("error", "Total Records:"+sbRowData.size());
			 req.setAttribute("reportTitle", "Account LoadTime");
			 }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "callHistoryReport";
	}
	
	
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
	
	
	
	@RequestMapping("/getC3OutboundReport.do")
	public String getC3OutboundReport(HttpServletRequest req,HttpServletResponse res)
	{
		String date=req.getParameter("dateFrom");
		String page=req.getParameter("page");
		String mode=req.getParameter("mode");
		ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
		String tableID=resourceBundle.getString("V2App");
		System.out.println("tableID::;"+tableID);
		String viewPage="v2OutboundReport";
		if(date==null ||date.equals(""))
		{
			req.setAttribute("error", "Incorrect Date Format..");
			date=GAUtil.getCurrentDate();
			System.out.println("Current Date::::::::::::::::::::::::::::::"+date);
			req.setAttribute("date", date);	
		}
		if(mode==null||mode.equals(""))
		{
							mode="V2App";
		}
		
		System.out.println("Trying to fetch Data for ++++++++"+mode);
		System.out.println("date::"+date);
		
		ArrayList<ArrayList<?>> v2RowData=null;
		
		String dimensionsSB="";
		
		
		 try {
			 GaReportProcessor gaService= new GaReportProcessor();
			 dimensionsSB="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
			 System.out.println("dimensions:::"+dimensionsSB);
			 //v2RowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
			 System.out.println("Sb Row Data Received");
			 
			 //ArrayList<ArrayList<?>> compiledData= new ArrayList<ArrayList<?>>();http://mvcsampledemo.appspot.com/
			 
			 if(v2RowData!=null)
			 {
				 System.out.println("Printing Compiled Data");
					req.setAttribute("rowData", GaDatastoreService.convertObjectToJson(v2RowData).toString());
					req.setAttribute("dimension", GAUtil.getkeyElementFromDimension(dimensionsSB));
					req.setAttribute("error", "Total Records:"+v2RowData.size());
					System.out.println("page:::"+page);
				
			 	
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 return viewPage;
		 
	}	
	
	
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

    public static void printStackTrace(Throwable t) {
	mLogger.error(getStackTrace(t));
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
    
    @RequestMapping("/getJsonData.do")
    @ResponseBody
	public void getJsonData(HttpServletRequest req,HttpServletResponse res)
	{
		String dateFrom=req.getParameter("dateFrom");
		
			try {
				ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
				System.out.println("Cron JOb Triggered @"+System.currentTimeMillis());
				System.out.println("target->> /get JSOn Data.do ");
				String date=null;
				if(dateFrom==null)
				{Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
				System.out.println(sdf.format(cal.getTime())+"::timeZone::"+sdf.getTimeZone());
				date=(sdf.format(cal.getTime())).toString();
				}
				else
				{
					date =dateFrom;
				}
				
				String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
				
				GaData gaData=null;
				ArrayList<ArrayList<?>> rowData=null;
				ArrayList<String> gaJson=null;
				int z=0;
					
						//System.out.println("Filter seelcted for Ga Query"+filter+"for table Id"+tableId);
						ArrayList<GaData> list=new ArrayList<GaData>();
						Authenticate  authenticate =new Authenticate();
						
						String temptoken=(authenticate.loadData(new GoogleCredential())).getRefreshToken();
						
							GoogleTokenResponse temp=(GoogleTokenResponse)authenticate.getNewToken(temptoken);
							System.out.println(" new token response fetched from GoogleRefreshTokenRequest "+temp.getAccessToken());
							authenticate.gaQurey(temp,temp.getAccessToken(), date, date,true,list,dimensions, resourceBundle.getString("SBLive"),"", null );
							System.out.println("Ga Data --ArrayList");
							System.out.println(list.size());
						
						if(list.size()>0)
						{	
							rowData=new ArrayList<ArrayList<?>>();
								//gaJson=new ArrayList<String>();
						
							while(z<list.size())
							{
								gaData=list.get(z);
								
								if(gaData!=null  & gaData.getRows()!=null)
								{
								
									z++;
									ArrayList<ArrayList<String>> dataSet=new ArrayList(gaData.getRows());
									rowData.addAll(dataSet);
									dataSet=null;
								}
								
							}
							System.out.println("Row Data Size::"+rowData.size());
							
							//res.getWriter().println(GaDatastoreService.convertObjectToJson(rowData).toString());
						}
						else{
								System.out.println("NO data Fetched");
						}
						
						z=0;
				
			
			System.out.println("fetching the data from cache");
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}	
    
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
			am.initMail("",testMessage,"","shashank.ashokkumar@a-cti.com","GA Exception","","", null);
			
			res.setCharacterEncoding("UTF-8");
			res.getWriter().println("Mail Sent");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
   }
    
	@RequestMapping("/gaGCSService.do")
	public void gaGCSService(HttpServletRequest req,HttpServletResponse res)
	{String redirectString="";
	String accessToken;
    	//String code=req.getParameter("code");
    	try {
//    		HttpTransport httpTransport = new NetHttpTransport();
//		    JacksonFactory jsonFactory = new JacksonFactory();
//		    InputStream is = Authenticate.class.getResourceAsStream("client_secrets.json");
//		    GoogleClientSecrets clientSecrets =
//		    GoogleClientSecrets.load(jsonFactory,new InputStreamReader(is));
//			
//			System.out.println(clientSecrets);
    		
    		
    		GaDatastoreService datastoreService= new GaDatastoreService();
    		//System.out.println("Calling refresh:::"+updateAccessTokenWithResfreshToken("1/y4LrLLimtZcMGFSHgJaM_9PP-WuudOQraUHf-MTTWNE"));
    		String jsonData=new GaDatastoreService().getTempData("shashank.ashokkumar@a-cti.com","refresh_token");
    		HashMap hm=datastoreService.convertJsonToMap(jsonData);
    		if(hm.get("refresh_token")!=null)
    		{System.out.println("refresh Toekn found");
    		
    			accessToken=new Authenticate().updateAccessTokenWithResfreshToken(hm.get("refresh_token").toString());
    			System.out.println("accessToken");
    			//authenticate.gaQurey(null,accessToken, "20140516", "20140516",true,list,dimensions, resourceBundle.getString("SBLive"),"", null );
    			
    		}else
    		{
    	String redirectUri="http://www.gadataservice.appspot.com/oauth2callback.do";
   		  redirectString="https://accounts.google.com/o/oauth2/auth?client_id="+"171068777204-uh3o3umebclqgdvd030ojm939l4rf3mr.apps.googleusercontent.com"+"&redirect_uri="+redirectUri+"&response_type=code&scope=https://www.googleapis.com/auth/analytics.readonly&access_type=offline&approval_prompt=force";
   		 System.out.println("redirect URI::::"+redirectString);
   		res.sendRedirect(redirectString);
   		}

   				} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		 
	}
	
		public String UrlFetchServiceUtil(String tokenUrl, String payload, String reqType,String contentType)
	{
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		String lResponseString = "";
		String lRespObj = "";
		String respObj = null;
		try {
			URL url = new URL( tokenUrl );
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout( 60000 );
			connection.setRequestMethod( "POST" );
			connection.setDoOutput( true );
			connection.setDoInput( true );
			connection.setRequestProperty( "content-type" , contentType );
			OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );
			
			System.out.println("tokenString::"+tokenUrl);
			writer.write( payload);
			writer.flush();
			reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
			while ( ( lResponseString = reader.readLine() ) != null )
				{
					respObj = lResponseString;
				}
			
			System.out.println(respObj);
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return respObj;
	}
	
	
    
}
