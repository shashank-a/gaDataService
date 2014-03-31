package com.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.analytics.GAQuery;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.analytics.model.GaData;
import com.util.GAUtil;

public class GADataService {
	private static final Logger mLogger = Logger.getLogger(GaDatastoreService.class.getPackage().getName());
	
	public void getGAData(GAQuery gaQuery)
	{
		
		
		
	}
	static ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
	public void gaDataService( String dateFrom,String dimensions, String tableId, String filter)
	{GaData gaData=null;
	ArrayList<ArrayList<?>> rowData=null;
	ArrayList<String> gaJson=null;
	int z=0;
		try {
			
			
			System.out.println("Filter seelcted for Ga Query"+filter+"for table Id"+tableId);
			ArrayList<GaData> list=new ArrayList<GaData>();
			Authenticate  authenticate =new Authenticate();
			
			
			String temptoken=(authenticate.loadData(new GoogleCredential())).getRefreshToken();
			
				GoogleTokenResponse temp=(GoogleTokenResponse)authenticate.getNewToken(temptoken);
				System.out.println(" new token response fetched from GoogleRefreshTokenRequest "+temp.getAccessToken());
				
				authenticate.gaQurey(temp,temp.getAccessToken(), dateFrom, dateFrom,true,list,dimensions, resourceBundle.getString(tableId),filter );
				System.out.println("Ga Data --ArrayList");
			if(list.size()>0)
			{	rowData=new ArrayList<ArrayList<?>>();
					//gaJson=new ArrayList<String>();
			
				while(z<list.size())
				{
					gaData=list.get(z);
					//rowData.addAll(gaData.getRows());
					z++;
					if(gaData!=null  & gaData.getRows()!=null)
					{
					ArrayList<ArrayList<String>> dataSet=new ArrayList(gaData.getRows());
					System.out.println("batch size::"+dataSet.size());
					rowData.addAll(dataSet);
					//gaJson.add(gaData.toString());
					//dataSet=null;
					}
				}
				System.out.println("Row Data Size::"+rowData.size());
				//String totalJson=convertObjectToJson(gaJson);
			}
			
			
			z=0;
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			printStackTrace(e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			printStackTrace(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			printStackTrace(e);
		}
		//storeGAData(dateFrom,dateTo,rowData);
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
