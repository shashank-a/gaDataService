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
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.Vector;
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
	public void fetchFilteredData(HttpServletRequest req,HttpServletResponse res)
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
						authenticate.gaQurey(temp,temp.getAccessToken(), date, date,true,list,dimensions, resourceBundle.getString("SBLive"),"" );
						System.out.println(list.size());
						System.out.println("Ga Data --ArrayList fetched and stored");
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					
	
		
	}
	@RequestMapping("/fetchV2Data.do")	
	public void fetchV2Data(HttpServletRequest req,HttpServletResponse res)
	{
		System.out.println("Cron JOb Triggered @"+System.currentTimeMillis());
		System.out.println("target->> /fetchAccountLoadData.do ");
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
		
		if(req.getParameter("dateFrom")!=null)
		{date=req.getParameter("dateFrom");
			System.out.println("Custom Date:"+date);
		}else
		{System.out.println(sdf.format(cal.getTime())+"::timeZone::"+sdf.getTimeZone());
			date=(sdf.format(cal.getTime())).toString();
		}
		
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
		GADataService gaDataService=new GADataService();
		
		gaDataService.gaDataService(date,dimensions, "V2App", null);
		System.out.println("Data for V2 app  recorded");
		
		System.out.println("Live Service Completed");
		
	}
	
	
	@RequestMapping("/gaProcessedData.do")	
	public void gaReportMailListner(HttpServletRequest req,HttpServletResponse res) throws Exception, IOException
	{
		System.out.println("Cron JOb Triggered @"+System.currentTimeMillis());
		System.out.println("target->> /fetchAccountLoadData.do ");
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
		
		if(req.getParameter("dateFrom")!=null)
		{date=req.getParameter("dateFrom");
			System.out.println("Custom Date:"+date);
		}else
		{	cal.roll(Calendar.DATE, false);
			System.out.println(sdf.format(cal.getTime())+"::timeZone::"+sdf.getTimeZone());
			date=(sdf.format(cal.getTime())).toString();
		}
		
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3";
		ArrayList<ArrayList<?>> sbRowData=GaDatastoreService.fetchGAData(date,dimensions,GAUtil.getkeyElementFromDimension(dimensions),"SBLive");
		String csvData=null;
		String msgText="Please Find Attached Google Analytics Raw Data Report for "+date;  
		/*if(sbRowData!=null)
		{
			String bcc[]={"shashank.ashokkumar@a-cti.com"};
			csvData=CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString();
			Vector <EmailAttachmentView>attachMents= new Vector<EmailAttachmentView>();
			EmailAttachmentView encl=new EmailAttachmentView();
			encl.setAttachmentText(csvData);
			attachMents.add(encl);
			new EmailUtil().msgSend( "shashank.ashokkumar@a-cti.com" , bcc,"shashanksworld@gmail.com" , "Switchboard Agent Performace Report", false , msgText, "" , attachMents ,true);
		}*/
		
		new GaReportProcessor().getAgentDetailReport(sbRowData);
		
		
	}
	
	
	
	
	
@RequestMapping("/gaDataEmailService.do")
public void emailTester(HttpServletRequest req,HttpServletResponse res) throws Exception, IOException
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

		String dimensions = "ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3";
		ArrayList<ArrayList<?>> sbRowData = GaDatastoreService.fetchGAData(
				date, dimensions,
				GAUtil.getkeyElementFromDimension(dimensions), "SBLive");
		String csvData = null;
		String msgText = "[TEST] Please Find Attached Google Analytics Raw Data Report for "+date;  
		csvData=CsvUtil.formatCsv(sbRowData,Charset.defaultCharset()).toString();
		
		System.out.println(msgText);
		AnalyticsMailer am= new AnalyticsMailer();
			 try {
				am.initMail(csvData,msgText,date,"gayathri.venkatasayee@a-cti.com","Analytics Raw Data Report");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 System.out.println("Service complete");
			 
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
				am.initMail(csvData,msgText,date,"shashank.ashokkumar@a-cti.com","Full CX Feedback Report[Google Analytics]");
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
	}
	
	String dateFrom=req.getParameter("dateFrom");
	System.out.println("dataFrom:::;   "+dateFrom);
	String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3,ga:customVarValue4";
	ArrayList<ArrayList<?>> rows=null;
	String processedData=null;
	try {
		rows=GaDatastoreService.fetchGADataBatch(dateFrom, dimensions, GAUtil.getkeyElementFromDimension(dimensions), "SBLIVE");
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
			 try {
				 if(csvData!=null && !csvData.equals(""))
					 am.initMail(csvData,msgText,date,"gayathri.venkatasayee@a-cti.com","Agent Action Report");
				 else
			    		System.out.println("CSV Data Not present");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 System.out.println("Service complete");
			 
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
