package com.service;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;


import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.api.services.analytics.*;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;



import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;


import com.util.DataStoreManager;
import com.util.GAUtil;
import com.util.ZipData;

public class Authenticate
	{
	 
	final static String CLIENT_ID = "171068777204-uh3o3umebclqgdvd030ojm939l4rf3mr.apps.googleusercontent.com";
	final static String CLIENT_SECRET = "UpM_LVYRCLogNP4TTXIUg94a";
	final static String REDIRECT_URL = "http://www.gadataservice.appspot.com/oauth2callback.do";
	final static String SCOPE="https://www.googleapis.com/auth/analytics.readonly";
	final static String APPLICATION_NAME="GA Web Service";
	final static String USER_ID="shashank.ashokkumar@a-cti.com";
	static  String TABLE_ID = "56596375";
	final static JacksonFactory JSON_FACTORY = new  JacksonFactory();
    final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
		
		
		public void gaQurey(GoogleTokenResponse response,String accessToken,String dateFrom,String dateTo,boolean flag,ArrayList<GaData> list,String dimensions, String tableId, String filter, String month)
		{
			System.out.println( response+":"+ accessToken+":"+ dateFrom+":"+ dateTo+":"+ flag+":"+ list+":"+ dimensions+":"+ tableId+":"+ filter);
			if(tableId==null || tableId.equals(""))
			{
				tableId=TABLE_ID;
			}
			String token=""; 
			//NetHttpTransport netHttpTransport = new NetHttpTransport();
			
			//JsonFactory jsonFactory=new JacksonFactory();
			GoogleCredential credential=null;
			//System.out.println(response.getAccessToken());
			
			GaData gaData=null;
			try
			{
			 credential=new GoogleCredential().setAccessToken(accessToken);
		 
			 Analytics analytics=getAnalayticsObject(accessToken);
			 
			
			 System.out.println("analytics Build");
			int k=0,z=9999,max=9999;
			
				//getResultsData("ga:"+tableId,"ga:totalEvents",dimensions,dateFrom,dateFrom,"","","","9999","1",analytics);
				
				System.out.println(dimensions);
				
					Get apiQuery = analytics.data().ga().get("ga:"+tableId,dateFrom,dateTo,"ga:visits");
					apiQuery.setDimensions( dimensions );
					apiQuery.setMetrics("ga:totalEvents");
					if(filter!=null && !filter.equals("") && tableId.equals("62456345"))
					{
						apiQuery.setFilters("ga:eventAction=@Transfer,ga:eventAction==Dialing");	
					}
					
					System.out.println("setting sort");
					String keyElement=GAUtil.getkeyElementFromDimension(dimensions);
					String key="GaDataObject_"+"SBLive"+"_"+dateFrom.replaceAll("-", "")+"_"+keyElement;
					if(tableId.equals("62456345"))
					{key="GaDataObject_"+"V2Outbound"+"_"+dateFrom.replaceAll("-", "")+"_"+keyElement;
						if(month!=null)
						{
							key="GaDataObject_"+"V2Outbound"+"_"+dateFrom.replaceAll("-", "")+"_"+dateTo.replaceAll("-", "");
						}
					}
					int batchSize=1;
					int counter=1;
					byte [] compressData=null;
					String batchKey;
					ArrayList<String> dimArray= new ArrayList<String>();
					HashMap <String,Object> gaDataKeyMap=new HashMap<String,Object>();
					
					while(k<=z)
						{
						
					System.out.println("max result");
					apiQuery.setStartIndex(k+1);
					apiQuery.setMaxResults(max );
					System.out.println("fetching query data.....");
					System.out.println(apiQuery.toString());
					gaData=apiQuery.execute();
					list.add(gaData);
					
					z=gaData.getTotalResults();
					k=k+max;
					batchSize=(z/10000)+1;
					
					if(gaData!=null && counter==1)
					{
						gaDataKeyMap.put("Date",dateFrom);
						gaDataKeyMap.put("Dimension",dimensions);
						
						for(int i=1;i<=batchSize;i++)
						{
							batchKey=key+"_Part_"+(i)+"||"+batchSize;
							dimArray.add(batchKey);
						}
						gaDataKeyMap.put("keyList", dimArray);
						compressData=ZipData.compressBytes(GaDatastoreService.convertObjectToJson(gaDataKeyMap).toString());
						String masterKey="GaDataObject_"+"SBLIVE"+"_"+dateFrom.replaceAll("-", "")+"_"+keyElement;
						if(tableId.equals("62456345"))
						{masterKey="GaDataObject_"+"V2Outbound"+"_"+dateFrom.replaceAll("-", "")+"_"+keyElement;
						if(month!=null)
						{
							masterKey="GaDataObject_"+"V2Outbound"+"_"+dateFrom.replaceAll("-", "")+"_"+dateTo.replaceAll("-", "");
						}
						}
						System.out.println("Writing For:::::"+masterKey);
						DataStoreManager.set(masterKey,dateFrom.replaceAll("-", ""),compressData);
						compressData=null;
						
					}
					
					System.out.println("Looping for:::::: "+(k+1));
					//Write to Data store
					ArrayList<ArrayList<?>> rows=null;
					if(gaData.getRows()!=null)
					{
						rows=new ArrayList(gaData.getRows());	
					}else
					{
						AnalyticsMailer am=new AnalyticsMailer();
						am.initMail("","No data Found in gaData","","shashank.ashokkumar@a-cti.com","GA Exception","","", null);
					}
					
					compressData=ZipData.compressBytes(GaDatastoreService.convertObjectToJson(rows).toString());
					System.out.println("Writing For:::::"+dimArray.get(counter-1));
					DataStoreManager.set(dimArray.get(counter-1),dateFrom.replaceAll("-", ""),compressData);
					
			    	System.out.println("Setting Data into Caches");
			    	System.out.println("Key:::"+key);
			    	batchKey=null;
			    	rows=null;
			    	counter++;
					}
					System.out.println("list size::"+list.size());
					System.out.println("GA account data retrieved++"+gaData.getTotalResults()+"...total of  all results::::::"+gaData.getTotalsForAllResults());
					
					
					System.out.println("data Fetched");
				}
			
			catch (GoogleJsonResponseException e) {
				  // Catch API specific errors.
				  handleApiError(e);

			}
			catch ( IOException e )
				{
					// TODO Auto-generated catch block
				
					e.printStackTrace();
				} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			
			//return null;
		}
		

		private static void handleApiError( GoogleJsonResponseException e )
			{
				// TODO Auto-generated method stub
				System.out.println("Api Error  in query");
				System.out.println(e);
				
			}
		
		public List<List<String>> printColumnHeaders(GaData gaData) {
			/* System.out.println("Column Headers:");

			 for (ColumnHeaders header : gaData.getColumnHeaders()) {
			   System.out.println("Column Name: " + header.getName());
			   System.out.println("Column Type: " + header.getColumnType());
			   System.out.println("Column Data Type: " + header.getColumnType());
			 */
			 if (gaData.getTotalResults() > 0) {
				   System.out.println("Data Table:");

				   // Print the column names.
				   for (ColumnHeaders header : gaData.getColumnHeaders()) {
				     System.out.format("%-32s", header.getName() + '(' + header.getDataType() + ')');
				   }
				   System.out.println("printing row data....");

				   // Print the rows of data.
				   for (List <String> rowValues : gaData.getRows()) {
				     for (String value : rowValues) {
				       System.out.format("%-32s", value);
				     }
				     System.out.println();
				   
				   }
				 } else {
				   System.out.println("No data");
				   return null;
				 }	
			 return null;
		}
		

		public String  updateAccessTokenWithResfreshToken(String refreshToken) {		
			
			String accessToken 	= "";
			JSONObject userDetails	= null;
			String email			= null;
			String tokenUrl="https://accounts.google.com/o/oauth2/token";
			
			ObjectMapper mapper = new ObjectMapper();
			try{
		        JSONObject jsonObject = new JSONObject();
		        jsonObject.put("client_id", CLIENT_ID);
		        jsonObject.put("client_secret", CLIENT_SECRET);
		        jsonObject.put("refresh_token", refreshToken);
		        jsonObject.put("grant_type", "refresh_token");
		        
				String payload ="client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&refresh_token="+refreshToken+"&grant_type=refresh_token";
                /*We got refersh token from resourse bundle. later we are supplying refershtoken to httpRequest method to get HttpRequest object
                 * for accessing Google analytics */ 
				//httpRequest method returns httpRequest object.
				 //responseString contains refreshToken which is used for accessing the google Google analytics.
				 
		        String responseString = UrlFetchServiceUtil.httpRequest(tokenUrl, payload, "POST", "application/x-www-form-urlencoded", null);
		        
				System.out.println("got new accesss token"+responseString);
				
				TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String,String>>() {};
				HashMap<String,String> resultMap = mapper.readValue(responseString.toString(), typeRef);
				accessToken =resultMap.get("access_token");
			}
			catch(Exception e){
				StringWriter sw = new StringWriter();
				PrintWriter pw 	= new PrintWriter(sw);
				e.printStackTrace(pw);
				System.out.println("Error " + sw.toString());
			}
			return accessToken;
			
		} 
		  public static Analytics getAnalayticsObject(String accesstoken){
			  GoogleCredential credential =null;
		    	Analytics analytics =null;
		    	credential= new GoogleCredential.Builder().setClientSecrets(clientSecrets()).setJsonFactory(JSON_FACTORY).setTransport(HTTP_TRANSPORT).build();
		    	credential.setAccessToken(accesstoken);
//		    	//log.info("credentil accesstoken"+credential.getAccessToken());
		    	 analytics = new Analytics.Builder(HTTP_TRANSPORT,JSON_FACTORY,credential).setApplicationName(APPLICATION_NAME).build();
		    	   
		    	
		    	credential = null;
		    	return analytics;
		    	
		    }
		  static public GoogleClientSecrets clientSecrets(){
				try{
					return GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(Authenticate.class.getResourceAsStream("client_secrets.json")));
				}catch(Exception e){
					System.out.println("Problem in getting Client Secrets");
				}
				return null;   
				  
			}
		  public static String  getResultsData(String table_id, String metrics,String dimension, String startDate, String endDate,String filter, String segment, String sort, String maxResult, String startIndex,Analytics analytics) 
			{
			
			  System.out.println("Get the result for analytics");
				GaData value = null;			
				if(segment==null)segment="" ;
				if(sort==null)sort = "" ;
				int maxResults;
				if(maxResult == null)
					maxResults =10000;
				else
					maxResults= Integer.parseInt(maxResult);
				Get apiQuery;
				try {
				//value = analytics.data().ga().get(table_id, startDate, endDate, metrics).setDimensions(dimension).setSegment(segment).setFilters(filter).setSort(sort).setOutput("dataTable").setMaxResults(50).execute().toPrettyString();
					apiQuery = analytics.data().ga().get(table_id, startDate, endDate, metrics);
					if(dimension.length() !=0){
						 apiQuery.setDimensions(dimension);
					 }
					if(filter.length()!=0){
						apiQuery.setFilters(filter);
					}
					if(segment.length()!=0){
						apiQuery.setSegment(segment);
					}
					if(sort.length()!=0){
						apiQuery.setSort(sort);
					}
					if(startIndex != null){
						apiQuery.setStartIndex(Integer.parseInt(startIndex));
					}

					System.out.println("Data send to analytics");
					value = apiQuery.setMaxResults(maxResults).execute();
					System.out.println("Data receive from analytics");
					//log.info(value.toString());
					return value.toString();
				} catch (Exception e) {	
					System.out.println(e.getMessage());
					return e.getMessage();
				}
			
			}
		
	}
