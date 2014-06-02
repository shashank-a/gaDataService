package com.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.analytics.model.GaData;
import com.service.AnalyticsMailer;
import com.service.Authenticate;
import com.service.GADataService;
import com.service.GaDatastoreService;
import com.service.GaReportProcessor;
import com.util.CsvUtil;
import com.util.DataStoreManager;
import com.util.EmailAttachmentView;
import com.util.EmailUtil;
import com.util.GAUtil;
import com.util.ZipData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

@Controller
public class GAScheduler {
	static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger mLogger = Logger.getLogger(GAScheduler.class.getPackage().getName());
	static {
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		
	}
	static String date="";
	

	@RequestMapping("/fetchBatchData.do")	
	public void fetchBatchData(HttpServletRequest req,HttpServletResponse res)
	{
		ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
		try {
			String dateFrom=req.getParameter("dateFrom");
			String date=null;
			if(dateFrom==null)
			{
				Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			cal.roll(Calendar.DATE, false);
			System.out.println(sdf.format(cal.getTime()) + "::timeZone::"
					+ sdf.getTimeZone());
			date = (sdf.format(cal.getTime())).toString();
			}
			else
			{
				date =dateFrom;
			}
			
			String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
			String accessToken="";
			GaData gaData=null;
			ArrayList<ArrayList<?>> rowData=null;
			ArrayList<String> gaJson=null;
			int z=0;
			GaDatastoreService datastoreService= new GaDatastoreService();
			Authenticate  authenticate =new Authenticate();
			
			String jsonData=new GaDatastoreService().getTempData("shashank.ashokkumar@a-cti.com","refresh_token");
    		HashMap hm=datastoreService.convertJsonToMap(jsonData);
    		if(hm.get("refresh_token")!=null)
    		{System.out.println("refresh Toekn found");
    			 accessToken = authenticate.updateAccessTokenWithResfreshToken(hm.get("refresh_token").toString());
    			System.out.println("accessToken");
    			//temp=(GoogleTokenResponse)authenticate.getNewToken(hm.get("refresh_token").toString());
    		}else
    		{
    			System.out.println("no Refresh token");
    		}
					//System.out.println("Filter seelcted for Ga Query"+filter+"for table Id"+tableId);
					ArrayList<GaData> list=new ArrayList<GaData>();
						
						//System.out.println(" new token response fetched from GoogleRefreshTokenRequest "+temp.getAccessToken());
						authenticate.gaQurey(null,accessToken, date, date,true,list,dimensions, resourceBundle.getString("SBLive"),"", null );
						System.out.println(list.size());
						System.out.println("Ga Data --ArrayList fetched and stored");
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AnalyticsMailer am= new AnalyticsMailer();
			 try {
				am.initMail("",e.toString(),date,"shashank.ashokkumar@a-cti.com","GA Exception","","", null);
			} catch (UnsupportedEncodingException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		
		}
					
	
		
	}
	

@RequestMapping("/fullCXEmailer.do")
public void fullCXEmailer(HttpServletRequest req,HttpServletResponse res) throws Exception, IOException
		{
	
		System.out.println("Cron JOb Triggered @" + System.currentTimeMillis());
		System.out.println("target->> /email tester.do ");

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("PST8PDT"));

		if (req.getParameter("dateFrom") != null) {
			date = req.getParameter("dateFrom");
			System.out.println("Custom Date:" + date);
		} else {
			cal.roll(Calendar.DATE, false);
			System.out.println(sdf.format(cal.getTime()) + "::timeZone::"
					+ sdf.getTimeZone());
			date = (sdf.format(cal.getTime())).toString();
		}

		String dimensions = "ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
		ArrayList<ArrayList<?>> sbRowData = GaDatastoreService.fetchGAData(
				date, dimensions,
				GAUtil.getkeyElementFromDimension(dimensions), "SBLive");
		GaReportProcessor gaReportProcessor= new GaReportProcessor();
	 		
		ArrayList<ArrayList<?>> compiledData = gaReportProcessor.filterDataByDimension(sbRowData,"Associate Thumbs Down Data",1);
		compiledData.addAll(gaReportProcessor.filterDataByDimension(sbRowData,"Associate Thumbs Up Data",1));
		System.out.println(compiledData.size());
	 	
		
		String csvData = null;
		String msgText = "[TEST] Please Find Attached Google Analytics Full CX Feedback Report for "+date;  
		csvData=CsvUtil.formatCsv(compiledData,Charset.defaultCharset()).toString();
		
		System.out.println(msgText);
		AnalyticsMailer am= new AnalyticsMailer();
			 try {
				am.initMail(csvData,msgText,date,"shashank.ashokkumar@a-cti.com","Full CX Feedback Report[Google Analytics]","","", null);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 System.out.println("Service complete");
			 
		}

@RequestMapping("/processAgentReport.do")	
public void processAgentReport(HttpServletRequest req,HttpServletResponse res) throws Exception, IOException
{
	System.out.println("Cron JOb Triggered @"+System.currentTimeMillis());
	System.out.println("target->> /processAgentReport.do ");
	String dateFrom=req.getParameter("dateFrom");
	
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
	
	if (req.getParameter("dateFrom") != null) {
		date = req.getParameter("dateFrom");
		System.out.println("Custom Date:" + date);
	} else {
		cal.roll(Calendar.DATE, false);
		System.out.println(sdf.format(cal.getTime()) + "::timeZone::"
				+ sdf.getTimeZone());
		date = (sdf.format(cal.getTime())).toString();
		dateFrom=date;
	}
	
	
	
	System.out.println("dataFrom:::;   "+dateFrom);
	String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
	ArrayList<ArrayList<?>> rows=null;
	String processedData=null;
	try {
		rows=GaDatastoreService.fetchGADataBatch(date, null, dimensions, GAUtil.getkeyElementFromDimension(dimensions), "SBLIVE", null);
		if(rows!=null)
		{
			String jsonData;
			System.out.println("batch Size::::"+rows.size());
			/*
			Processing  rawData For Agent Details report
			*/
			ArrayList agentActionData=new GaReportProcessor().AgentActionReport(rows);
			
			ArrayList<ArrayList<?>> arr=new ArrayList<ArrayList<?>>(agentActionData);
			System.out.println("########Storing Processed Array#####KEy::"+"AgentActionCount_"+dateFrom.replaceAll("-", ""));
			DataStoreManager.set("AgentActionCount_"+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", ""),ZipData.compressBytes(GaDatastoreService.convertObjectToJson(arr).toString()));
			//Reordering AgentData
			arr=new GaReportProcessor().reorderAgentReport(agentActionData);
			processedData=CsvUtil.formatCsvfromArray(arr,',',Charset.defaultCharset()).toString();
			DataStoreManager.set("AgentActionCount_CSV"+dateFrom.replaceAll("-", ""),dateFrom.replaceAll("-", ""),ZipData.compressBytes(processedData.toString()));
			System.out.println("########Storing CSV Data#####KEy::"+"AgentActionCount_CSV_"+dateFrom.replaceAll("-", ""));
			
			 
		}
		
	} catch (ClassNotFoundException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		AnalyticsMailer am= new AnalyticsMailer();
		 try {
			am.initMail("",e.toString(),date,"shashank.ashokkumar@a-cti.com","GA Exception","","", null);
			
		} catch (UnsupportedEncodingException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
	}
	
	
}
@RequestMapping("/agentActionEmailService.do")
public void agentActionEmailService(HttpServletRequest req,HttpServletResponse res) throws Exception, IOException
		{
	
		System.out.println("Cron JOb Triggered @" + System.currentTimeMillis());
		System.out.println("target->> /email tester.do ");

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("PST8PDT"));

		if (req.getParameter("dateFrom") != null) {
			date = req.getParameter("dateFrom");
			System.out.println("Custom Date:" + date);
		} else {
			cal.roll(Calendar.DATE, false);
			System.out.println(sdf.format(cal.getTime()) + "::timeZone::"
					+ sdf.getTimeZone());
			date = (sdf.format(cal.getTime())).toString();
		}

		
		String csvData = null;
		try {
		
		byte[] b=DataStoreManager.get("AgentActionCount_CSV"+date.replaceAll("-", ""),date.replaceAll("-", ""));
    	if(b.length>1)
    	{	
				csvData=ZipData.extractBytes(b);
    	}else
    	{
    		System.out.println("CSV Data Not present in Cache");
    	}
		
		String msgText = "[TEST] Please Find Attached Agent Action Report for "+date;  
		
		System.out.println(msgText);
		AnalyticsMailer am= new AnalyticsMailer();
			 
				 if(csvData!=null && !csvData.equals(""))
					 {
					String cc="naresh.talluri@a-cti.com,performancemanagement@a-cti.com";
					String bcc="shashank.ashokkumar@a-cti.com,ramanathan.arunachalam@a-cti.com";
					
					 am.initMail(csvData,msgText,date,"gayathri.venkatasayee@a-cti.com","Agent Action Report",cc,bcc, "AgentActionReport");
					//am.initMail(csvData,msgText,date,"shashank.ashokkumar@a-cti.com","Agent Action Report","","");
					 }
				 else
			    		System.out.println("CSV	 Data Not present");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				AnalyticsMailer am= new AnalyticsMailer();
				 try {
					am.initMail("",e.toString(),date,"shashank.ashokkumar@a-cti.com","GA Exception","","", null);
					
				} catch (UnsupportedEncodingException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				
			}
			 System.out.println("Service complete");
			 
		}

@RequestMapping("/fetchV2Outbound.do")	
public void fetchV2Outbound(HttpServletRequest req,HttpServletResponse res)
{System.out.println("fetchV2Outbound");
	ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
	AnalyticsMailer am= new AnalyticsMailer();
	try {
		String dateFrom=req.getParameter("dateFrom");
		String dateTo=req.getParameter("dateTo");
		String dateRange=req.getParameter("range");
		String date=null;
		if(dateFrom==null && dateTo==null)
		{
			Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cal.roll(Calendar.DATE, false);
		System.out.println(sdf.format(cal.getTime()) + "::timeZone::"
				+ sdf.getTimeZone());
		date = (sdf.format(cal.getTime())).toString();
		dateFrom=date;
		dateTo=date;
		}
		else
		{System.out.println("Setting date Range");
			date =dateFrom;
			
		}
		if(dateRange==null)
		{
			dateRange="today";
		}
		
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
		
				ArrayList<GaData> list=new ArrayList<GaData>();
				Authenticate  authenticate =new Authenticate();
				ArrayList<ArrayList<?>> arrayList=null;
				String accessToken="";
		    	String processedData=null;
		    	String jsonData=new GaDatastoreService().getTempData("shashank.ashokkumar@a-cti.com","refresh_token");
	    		GaDatastoreService datastoreService=null;
				HashMap hm=datastoreService.convertJsonToMap(jsonData);
	    		if(hm.get("refresh_token")!=null)
	    		{System.out.println("refresh Toekn found");
	    			 accessToken = authenticate.updateAccessTokenWithResfreshToken(hm.get("refresh_token").toString());
	    			System.out.println("accessToken");
	    			//temp=(GoogleTokenResponse)authenticate.getNewToken(hm.get("refresh_token").toString());
	    		}else
	    		{
	    			System.out.println("no Refresh token");
	    		}
				
					
					authenticate.gaQurey(null,accessToken, dateFrom, dateTo,true,list,dimensions, resourceBundle.getString("V2App"),"ga:eventAction==Call Transfer,ga:eventAction==Dialing", "monthly" );
					System.out.println(list.size());
					System.out.println("Ga Data --ArrayList fetched and stored");
					arrayList=(new GaDatastoreService()).fetchGADataBatch(date,dateTo,dimensions,GAUtil.getkeyElementFromDimension(dimensions), "V2Outbound", dateRange);
					arrayList=(new GaDatastoreService()).addReportHeaders(arrayList);
		    		System.out.println("processed Data"+arrayList.size());
		    		processedData=CsvUtil.formatCsvfromArray(arrayList,',',Charset.defaultCharset()).toString();
		    		
		    		String msgText = "Please Find Attached V2 Outbound Report for "+date;
		    		if(dateRange!=null && dateRange.equals("monthly")){
		    					msgText = " Please Find Attached V2 Outbound Report for date  "+dateFrom+"->"+dateTo;
		    			}
		    		
		    		am.initMail(processedData,msgText,date,"shashank.ashokkumar@a-cti.com","V2 Outbound Report","","", "V2Outbound_");
		    		
					
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		 try {
			am.initMail("",e.toString(),date,"shashank.ashokkumar@a-cti.com","GA Exception","","", null);
			
		} catch (UnsupportedEncodingException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally
	{
		am=null;
		
	}
				

	
}



@RequestMapping("/parseGAJSON.do")	
public void parseGAJSON(HttpServletRequest req,HttpServletResponse res)
{	
	String dateFrom=req.getParameter("dateFrom");
	
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	sdf.setTimeZone(TimeZone.getTimeZone("PST8PDT"));

	if (req.getParameter("dateFrom") != null) {
		date = req.getParameter("dateFrom");
		System.out.println("Custom Date:" + date);
	} else {
		cal.roll(Calendar.DATE, false);
		System.out.println(sdf.format(cal.getTime()) + "::timeZone::"
				+ sdf.getTimeZone());
		date = (sdf.format(cal.getTime())).toString();
	}
	
	dateFrom=date;
	String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
	ArrayList<ArrayList<?>> rows=null;
	try {
		String key="GaDataObject_"+"SBLIVE"+"_"+dateFrom.replaceAll("-", "")+"_"+GAUtil.getkeyElementFromDimension(dimensions);
		byte [] jsonData=DataStoreManager.get(key+"_json", dateFrom.replaceAll("-", ""));
		String processedData=null;
		
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
					
	} 
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		 
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
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
	
	
	

}
