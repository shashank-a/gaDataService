package com.service;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.appengine.auth.oauth2.AppEngineCredentialStore;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;
import com.google.api.client.googleapis.*;
import com.google.api.client.googleapis.auth.clientlogin.*;
import com.google.api.client.googleapis.json.*;
import com.google.api.client.http.*;
import com.util.DataStoreManager;
import com.util.GAUtil;
import com.util.ZipData;

import java.io.*;



@SuppressWarnings( "deprecation" )
public class Authenticate
	{
	 
	final String CLIENT_ID = "171068777204-uh3o3umebclqgdvd030ojm939l4rf3mr.apps.googleusercontent.com";
	final String CLIENT_SECRET = "UpM_LVYRCLogNP4TTXIUg94a";
	final String REDIRECT_URL = "http://www.gadataservice.appspot.com/oauth2callback.do";
	final String SCOPE="https://www.googleapis.com/auth/analytics.readonly";
	final String APPLICATION_NAME="GA Web Service";
	final String USER_ID="shashank.ashokkumar@a-cti.com";
	static  String TABLE_ID = "56596375";
	
	
	
	
	
		public GoogleTokenResponse getData(String authorizationCode) throws IOException
			{
		 System.out.println("Auth code"+authorizationCode);
				// Use the authorization code to get an access token and a refresh token.
		 GoogleTokenResponse res=null;
		
		try {
		  res = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
					CLIENT_ID, 
		  		      CLIENT_SECRET, 
		  		      authorizationCode,
		  		      REDIRECT_URL).setGrantType("authorization_code").execute();
		              
		} catch (IOException ioe) {
			
			System.out.println( "in io cache"+ioe.getMessage());
		  ioe.printStackTrace();
		  return null;
		  
		}
		
		
		if(res!=null)
			{
				
				System.out.println("response. access token..."+res.getAccessToken());
				System.out.println("refresh Token...."+res.getRefreshToken());
				System.out.println("Expires IN...."+res.getExpiresInSeconds());
				System.out.println("Token Type:::"+res.getTokenType());
				
			}
		
		System.out.println("Creating credential...");
		
		return res;
		}
		public GoogleTokenResponse getNewToken(String refreshToken) throws IOException
		{ String token="";
		System.out.println("GoogleRefreshTokenRequest...");
		GoogleTokenResponse tokenresponse= new GoogleRefreshTokenRequest(new NetHttpTransport(),new  JacksonFactory() , refreshToken, CLIENT_ID, CLIENT_SECRET).execute(); 
	
			return tokenresponse;
			
		}
		public void storeData(GoogleCredential googleCredential)
		{
			 CredentialStore credentialStore=new AppEngineCredentialStore();
			credentialStore.store( USER_ID,googleCredential);
			
			System.out.println("Credentials stored::");
			System.out.println("loading Data::::");
			System.out.println((loadData(new GoogleCredential())).getRefreshToken());
			
		}
		public GoogleCredential loadData(GoogleCredential googleCredential)
		{
			 CredentialStore credentialStore=new AppEngineCredentialStore();
			credentialStore.load(USER_ID,googleCredential);
			System.out.println("googleCredential  loaded  acces token::"+googleCredential.getAccessToken());
			return googleCredential;
		}
		public void gaQurey(GoogleTokenResponse response,String accessToken,String dateFrom,String dateTo,boolean flag,ArrayList<GaData> list,String dimensions, String tableId, String filter, String month)
		{
			System.out.println( response+":"+ accessToken+":"+ dateFrom+":"+ dateTo+":"+ flag+":"+ list+":"+ dimensions+":"+ tableId+":"+ filter);
			if(tableId==null || tableId.equals(""))
			{
				tableId=TABLE_ID;
			}
			String token=""; 
			NetHttpTransport netHttpTransport = new NetHttpTransport();
			JacksonFactory jacksonFactory = new JacksonFactory();
			 GoogleCredential credential  ;
			System.out.println(response.getAccessToken());
			
			GaData gaData=null;
			
			 credential=new GoogleCredential().setFromTokenResponse(response);
			 
			 if(!flag)
			 {if(credential.getRefreshToken()!=null)
				 storeData(credential);
			 }
			
			 credential= new GoogleCredential().setAccessToken(accessToken);
			 
			 System.out.println("credential  refresh tokens::"+credential.getRefreshToken());
			 Analytics analytics = Analytics.builder(netHttpTransport, jacksonFactory)
				      .setHttpRequestInitializer(credential)
				      .setApplicationName(APPLICATION_NAME)
				      .build();
			 
			
			 System.out.println("analytics Build");
			int k=0,z=9999,max=9999;
			try
				{
				
				
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
						am.initMail("","No data Found in gaData","","shashank.ashokkumar@a-cti.com","GA Exception","","");
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
		
		
		
		
	}
