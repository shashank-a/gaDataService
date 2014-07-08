package com.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;





public class UrlFetchServiceUtil {

	public static String httpRequest(String urlString, String params, String methodName, String contentType, HashMap<String,String> header){
		StringBuffer responseJson = new StringBuffer();

		try{

			System.out.println(";-----; checking method " + methodName);

			URL url = new URL(urlString);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod(methodName);//post method
			connection.setReadTimeout(60000);//one second
			connection.setConnectTimeout(60000);//one second
			if(contentType != null && !contentType.isEmpty()){
				connection.setRequestProperty("Content-Type",contentType);//setting contenttype
			}
			if(header!=null )
			{	
				for(String k:header.keySet())
				{System.out.println("header key::"+k.toString());
					connection.setRequestProperty(k.toString(),header.get(k).toString());// setting request header
				}
			}
			if(params != null && !params.isEmpty()){
				OutputStreamWriter writers = new OutputStreamWriter(connection.getOutputStream()); //using connection object getting outputstream 
				writers.write(params); //adding payload to outputstream object.
				//payload contains
				//String payload ="client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&refresh_token="+refreshToken+"&grant_type=refresh_token";
				writers.flush();
			}

			System.out.println(";-----; response code " + connection.getResponseCode());// getting status code for connection 

			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){ //
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String responseString = "";
				while ((responseString = reader.readLine()) != null) {

					responseJson.append(responseString);
				}
			}else{
				responseJson.append("{\"error\" : \"Error from server " + connection.getResponseCode() + ".\"}");
			}

		}catch(Exception e){

			System.out.println(e.getMessage());
		}

		return responseJson.toString();
	}
	
	public static String authenticationHttpRequest(String urlString, String params, String methodName, String contentType, String access_Token) {
		StringBuffer responseJson = new StringBuffer();
		String responseData  = "";
		try{
			
			System.out.println(";-----; checking method " + methodName);
			URL url = new URL(urlString);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod(methodName);
			connection.setReadTimeout(60000);
			connection.setConnectTimeout(60000);
			connection.setInstanceFollowRedirects(false);
			
			if(contentType != null && !contentType.isEmpty()){
				connection.setRequestProperty("Content-Type",contentType);
				connection.setRequestProperty("Authorization","Bearer "+access_Token);

			}
			else{
				connection.setRequestProperty("Authorization","Bearer "+access_Token);

			}
			if(params != null && !params.isEmpty()){
				OutputStreamWriter writers = new OutputStreamWriter(connection.getOutputStream());
				writers.write(params);
				writers.flush();
			}

			System.out.println(";-----; response code " + connection.getResponseCode());

			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String responseString = "";
				while ((responseString = reader.readLine()) != null) {

					responseJson.append(responseString);
				}
			}else{
				responseJson.append("{\"error\" : \"Error from server " + connection.getResponseCode() + ".\"}");
			}

			responseData = responseJson.toString();
	
		}catch(Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println(sw.toString());
		}
		return responseData;
	}
	
	
	

}
