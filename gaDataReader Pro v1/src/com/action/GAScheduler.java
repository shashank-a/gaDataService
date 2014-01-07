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
import com.util.EmailAttachmentView;
import com.util.EmailUtil;
import com.util.GAUtil;

import java.util.ArrayList;
import java.util.Calendar;
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
	
	@RequestMapping("/fetchV2EventData.do")	
	public void fetchV2GaData(HttpServletRequest req,HttpServletResponse res)
	{
		System.out.println("Cron JOb Triggered @..../fetchV2EventData.do"+System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
		System.out.println(sdf.format(cal.getTime()));
		date=(sdf.format(cal.getTime())).toString();
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2";
		new GADataService().gaDataService(date,dimensions, "V2App", null);
		System.out.println("V2 Service Completed");	
		
	}
	
	//   									
	@RequestMapping("/fetchSBEventData.do")	
	public void fetchSBGaData(HttpServletRequest req,HttpServletResponse res)
	{
		System.out.println("Cron JOb Triggered @"+System.currentTimeMillis());
		System.out.println("target->> /fetchSBEventData.do ");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
		System.out.println(sdf.format(cal.getTime()));
		date=(sdf.format(cal.getTime())).toString();
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3";
		new GADataService().gaDataService(date,dimensions, "StagingACTI", null);
		System.out.println("Staging Service Completed");	
	}
	//Active Cron
	@RequestMapping("/fetchBetaSBData.do")	
	public void fetchBetaSBGaData(HttpServletRequest req,HttpServletResponse res)
	{
		System.out.println("Cron JOb Triggered @"+System.currentTimeMillis());
		System.out.println("target->> /fetchBetaSBEventData.do ");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
		System.out.println(sdf.format(cal.getTime()));
		date=(sdf.format(cal.getTime())).toString();
		if(req.getParameter("dateFrom")!=null&&!req.getParameter("dateFrom").equals(""))
		{
			date=req.getParameter("dateFrom");	
		}
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue1,ga:customVarValue2,ga:customVarValue3";
		new GADataService().gaDataService(date,dimensions, "SBLive", null);
		System.out.println("Live Service Completed");
		
	}
	

	@RequestMapping("/fetchGARawData.do")	
	public void agentFeedBackData(HttpServletRequest req,HttpServletResponse res)
	{
		System.out.println("Cron JOb Triggered @"+System.currentTimeMillis());
		System.out.println("target->> /fetchBetaSBEventData.do ");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone( TimeZone.getTimeZone( "PST8PDT" ) );
		System.out.println(sdf.format(cal.getTime()));
		date=(sdf.format(cal.getTime())).toString();
		if(req.getParameter("dateFrom")!=null&&!req.getParameter("dateFrom").equals(""))
		{
			date=req.getParameter("dateFrom");	
		}
		
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue3";
		new GADataService().gaDataService(date,dimensions, "SBLive", null);
		System.out.println("Live  event Service Completed");
	
		
	}

//	Active Cron
	@RequestMapping("/fetchAccountLoadData.do")	
	public void fetchAccountLoadData(HttpServletRequest req,HttpServletResponse res)
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
		
		String dimensions="ga:eventCategory,ga:eventAction,ga:eventLabel,ga:customVarValue3,ga:customVarValue4";
		new GADataService().gaDataService(date,dimensions, "SBLive", "ga:eventAction==Account Load");
		System.out.println("Live Service Completed");
		
	}
	@RequestMapping("/fetchFilteredData.do")	
	public void fetchFilteredData(HttpServletRequest req,HttpServletResponse res)
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
		gaDataService.gaDataService(date,dimensions, "SBLive", null);
		System.out.println("Data for sb live recorded");
		
		
		
		
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
