package com.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.analytics.Analytics;
//import com.google.api.services.storage.Storage;
//import com.google.api.services.storage.Storage.Builder;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.service.Authenticate;
import com.service.GaDatastoreService;
import com.service.UrlFetchServiceUtil;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.service.GCSManager;
import com.google.api.client.json.JsonFactory;

@Controller
public class GCSController {
	
	final static String CLIENT_ID = "333926265691-6soksjjedhife34sjc94n08bda50tuu4.apps.googleusercontent.com";
	final static String CLIENT_SECRET = "NxbqzStxL_6rXZGK3k3tdoaN";
	final static String REDIRECT_URL = "http://www.gadataservice.appspot.com/gcsCallback.do";
	final static String SCOPE="https://www.googleapis.com/auth/devstorage.full_control";
	final static String APPLICATION_NAME="gadataservice";
	final static String USER_ID="shashank.ashokkumar@a-cti.com";	
	final static JacksonFactory JSON_FACTORY = new  JacksonFactory();
    final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
	@ResponseBody
	@RequestMapping("/gcsCallback.do")
	public void oauth2callback(HttpServletRequest request, HttpServletResponse res)
	{
		System.out.println("Inside GCS Callback");
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
			(new GaDatastoreService()).storeTempData("GACloudStorage",resultMap,"refresh_token_GCS");
			
			
		}
		catch(Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw 	= new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println("Error " + sw.toString());
		}
	}
	
	@RequestMapping("/gaGCSService.do")
	public void gaGCSService(HttpServletRequest req,HttpServletResponse res)
	{String redirectString="";
	String accessToken;
    System.out.println("Inside GCS controller");
   
    	try {
    		GaDatastoreService datastoreService= new GaDatastoreService();
    		//System.out.println("Calling refresh:::"+updateAccessTokenWithResfreshToken("1/y4LrLLimtZcMGFSHgJaM_9PP-WuudOQraUHf-MTTWNE"));
    		String jsonData=new GaDatastoreService().getRefreshToken("GACloudStorage","refresh_token_GCS");
    		
    		if(jsonData!=null)
    		{System.out.println("checkign json Structure");
		    		HashMap hm=datastoreService.convertJsonToMap(jsonData);
		    		if(hm.get("refresh_token")!=null)
		    		{System.out.println("refresh Toekn found");
		    		
		    			accessToken=updateAccessTokenWithResfreshToken(hm.get("refresh_token").toString());
		    			System.out.println("accessToken");
		    			//Storage stg=createGCSObject(accessToken);
		    			//(new GCSManager()).accessBucket(stg);
		    			String url="https://www.googleapis.com/upload/storage/v1/b/gadataservice.appspot.com/o?uploadType=media&name=GAData";
						String params="This is test Data to be sent";
						HashMap<String,String> headermap= new HashMap<String,String>();
						headermap.put("Content-Type", "text/plain");
						headermap.put("Content-Length", "100");
						headermap.put("Authorization", accessToken);
						headermap.put("key", "AIzaSyAHyKokt84euCyVve6V1AEM7uum9p93KIc");
						
						
						String resp=UrlFetchServiceUtil.httpRequest(url, params, "POST", "text/plain",headermap);
						System.out.println("request done"+resp);
		    					    			
		    		}
    		}else
		    		{
		   		  redirectString="https://accounts.google.com/o/oauth2/auth?client_id="+"333926265691-6soksjjedhife34sjc94n08bda50tuu4.apps.googleusercontent.com"+"&redirect_uri="+REDIRECT_URL+"&response_type=code&scope="+SCOPE+"&access_type=offline&approval_prompt=force";
		   		 System.out.println("redirect URI::::"+redirectString);
		   		res.sendRedirect(redirectString);
		   		}
    		

   				} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
//	 public Storage createGCSObject(String accessToken)
//	 { 
//		 //Storage storage=null;
//		/*try {
//			GoogleCredential credential =null;
//				credential= new GoogleCredential.Builder().setClientSecrets(clientSecrets()).setJsonFactory(JSON_FACTORY).setTransport(HTTP_TRANSPORT).build();
//				credential.setAccessToken(accessToken);
//			 //Storage gcs= Storage.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
//			 Storage.Builder build = new Storage.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential);
//			 build.setApplicationName(APPLICATION_NAME);
//			 storage = build.build();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("problem in building GCS");
//		}*/
//		 
//		 return storage;
//	 }
	 
	 static public GoogleClientSecrets clientSecrets(){
		 GoogleClientSecrets gcs=null;	
		 try{
				
				InputStream is=Authenticate.class.getResourceAsStream("client_secret_GCS.json");
				System.out.println("Stream Created");
				gcs= GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(is));
				System.out.println("gcs loaded"+gcs);
			
			
			}
		 catch(IOException e){
			 System.out.println("IO:"+e.toString());
		 }
		 catch(Exception e){
			 System.out.println("E:"+e.toString());
				System.out.println(e.getStackTrace());
				e.printStackTrace();
				System.out.println("Problem in getting Client Secrets");
				
			}
			return gcs;
			
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

	 

	

}
