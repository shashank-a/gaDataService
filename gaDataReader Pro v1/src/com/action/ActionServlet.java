package com.action;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import com.util.StringUtil;







import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.account.AccountDetails;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.analytics.model.GaData;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.service.Authenticate;
import com.service.GADataService;
import com.service.GaDatastoreService;
import com.service.GaReportProcessor;
import com.util.AppCacheManager;
import com.util.AppMemCacheManager;
import com.util.GAUtil;
import com.util.ZipData;

import java.util.ResourceBundle;
import java.util.zip.DataFormatException;

import com.util.CsvUtil;

import java.beans.*;

@Controller
public class ActionServlet {
	
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
			String dimensions=req.getParameter("dimension");
			if(req.getParameter("state")!=null)
			{
			JSONObject gaFilters=new JSONObject(req.getParameter("state")); 
			code=gaFilters.getString("code");
			dateFrom=gaFilters.getString("dateFrom");
			rawDataFlag=gaFilters.getString("rawDataFlag");
			tableId=gaFilters.getString("tableId");
			dimensions=gaFilters.getString("dimension");
			}
			System.out.println("dimensions::"+dimensions);
			//String dateTo=req.getParameter("dateTo");
						
			System.out.println("token:::"+token);
			System.out.println("dateFrom:::"+dateFrom);
			System.out.println("code:::"+code);	
			System.out.println("tableId::"+tableId+"----"+resourceBundle.getString(tableId));
			Authenticate  authenticate =new Authenticate();
			System.out.println("instance created");
			 dimensions=resourceBundle.getString(dimensions);
			if(dateFrom!=null)
			{System.out.println("Fetching row data");
				rowData=GaDatastoreService.fetchGAData(dateFrom,dimensions, GAUtil.getkeyElementFromDimension(dimensions),tableId);
			
				if(rowData==null)
				{
					int z=0;
				
							rowData=new ArrayList<ArrayList<?>>();
							//if(token.equals("null")||token.equals(""))
							if(token==null)
							{
								System.out.println("no session  obj");
								googleTokenResponse=authenticate.getData(code);
								if(googleTokenResponse==null)
								{
									//NO response token  req redirected to get access token...
									System.out.println("getAuth Code");
									
									res.sendRedirect(getAuthCode(req,res));
								}
								if(googleTokenResponse.getRefreshToken()!=null)					
								{
									authenticate.gaQurey(googleTokenResponse, googleTokenResponse.getAccessToken(), dateFrom, dateFrom,false,list,dimensions, resourceBundle.getString(tableId), null);
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
								authenticate.gaQurey(temp,temp.getAccessToken(), dateFrom, dateFrom,true,list,dimensions, resourceBundle.getString(tableId), null);
								
							}
							
							
							while(z<list.size())
							{
								gaData=list.get(z);
								//rowData.addAll(gaData.getRows());
								z++;
								ArrayList<ArrayList<String>> dataSet=new ArrayList(gaData.getRows());
								rowData.addAll(dataSet);
							}
							GaDatastoreService.storeGAData(dateFrom,rowData, dimensions, GAUtil.getkeyElementFromDimension(dimensions), tableId);
							
				}
				if(rowData!=null)
				{  
				GaReportProcessor gaService= new GaReportProcessor();
				 rowData=gaService.escapeGAData(rowData,0);
					if(rawDataFlag==null)
					{
						ArrayList<AccountDetails> accountDetails=(new GaReportProcessor()).getAccountDataFromGaData(rowData);
						System.out.println("accountDetails::::"+accountDetails.size());
						req.setAttribute("accountData", accountDetails);	
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
			 sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
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
	public String getAccountDetails(HttpServletRequest req,HttpServletResponse res)
	{
			try {
				String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2";
				System.out.println("dateFrom::;"+req.getParameter("dateFrom"));
				System.out.println("category::;"+req.getParameter("category").trim());
				
				req.setAttribute("accNo",req.getParameter("category").trim());
				//new GaReportProcessor().getEventDescriptionForCategory(GaDatastoreService.fetchGAData(req.getParameter("dateFrom").trim()), req.getParameter("category").trim());
				req.setAttribute("agentData", new GaReportProcessor().getEventDescriptionForCategory(GaDatastoreService.fetchGAData(req.getParameter("dateFrom").trim(), dimensions, GAUtil.getkeyElementFromDimension(dimensions), null), req.getParameter("category").trim()));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "agentWiseData";
			
	}
	
	
	@RequestMapping("/oauth2callback.do")
	public void showDefaultPage(HttpServletRequest req, HttpServletResponse res)
	{
	String code=req.getParameter("code");
	String state=req.getParameter("state");
	
	System.out.println("Auth Code received::::"+code);
	System.out.println("state received::::"+state);
	req.setAttribute("code",code);
	try {
		JSONObject gaFilter= new JSONObject(state);
		System.out.println(gaFilter.get("tableId"));
		System.out.println(gaFilter.get("rawDataFlag"));
		System.out.println(gaFilter.get("token"));
		getGAData(req,res);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
			 sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB), "SBLive");
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "callHistoryReport";
	}
	
	
	@RequestMapping("/getEventDetails.do")
	public String getEventDetails(HttpServletRequest req,HttpServletResponse res)
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
			 sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
			 System.out.println("Sb Row Data Received");
			 
			 
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return viewPage;
		
	}
	
	@RequestMapping("/redirector.do")
	public String redirector(HttpServletRequest req,HttpServletResponse res)
	{
		
			return "callDataReport";
			
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
			 sbRowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
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
		} catch (ClassNotFoundException e) {
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
			 v2RowData=GaDatastoreService.fetchGAData(date,dimensionsSB,GAUtil.getkeyElementFromDimension(dimensionsSB),mode);
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 return viewPage;
		 
	}	
	
	
	@RequestMapping("/getCacheData.do")
	public void getCacheData(HttpServletRequest req,HttpServletResponse res)
	{
		String key=req.getParameter("key");
		
		try {
			
			System.out.println("fetching the data from cache");
			System.out.println(ZipData.extractBytes((byte[])appcachemanager.get(key)).toString());
			
			
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
    
    
}
