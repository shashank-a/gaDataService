package com.service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;

import com.util.DataStoreManager;
import com.util.ZipData;
import com.acti.cache.serverresource.MemCacheServiceHelper;

public class GaDatastoreService
{	
	
	private static final Logger mLogger = Logger.getLogger(GaDatastoreService.class.getPackage().getName());
	static final ObjectMapper mapper = new ObjectMapper();
	 //static AppCacheManager appcachemanager= new AppCacheManager();
	static {
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		mapper.getJsonFactory().setCharacterEscapes(new JSONCharacterEscapes());
		
		
	}
	 public static void storeGAData(String dateFrom, ArrayList<ArrayList<?>> rows, String dimension, String keyElement, String app) throws IOException
	    {
	    	
	     	String key="GaDataObject_"+app+"_"+dateFrom.replaceAll("-", "")+"_"+keyElement;
	    	System.out.println("Setting Data into Caches");
	    	System.out.println("Key:::"+key);
	    	ByteArrayOutputStream os = new ByteArrayOutputStream();
	    	ObjectOutput out = null;
	    	try {
	    	  /*out = new ObjectOutputStream(os);   
	    	  out.writeObject(rows);*/
	    		
	    		
	    	  //byte[] rowDataInByte = os.toByteArray();
	    	  //System.out.println("Length of byte array"+rowDataInByte.length);
	    		//march 18 test
	    		byte [] compressData=ZipData.compressBytes(convertObjectToJson(rows).toString());
	    		System.out.println("DataCompressed");
		    	  MemCacheServiceHelper.setInMemCache(key,dimension,compressData);
	    		System.out.println("DataSetInto Cache");
	    		//setting into datastore
	    	  DataStoreManager.set(key,dimension,compressData);
	    	  
	    	  
	    	  
	    	//  appcachemanager.set(dimension,key,ZipData.compressBytes(convertObjectToJson(rows).toString()));
	    	  
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    	
	    
	    }
	    public static ArrayList<ArrayList<?>> fetchGAData(String dateFrom, String dimension, String keyElement, String app) throws IOException, ClassNotFoundException
	    {
	    	ArrayList<ArrayList<?>> rowData=null;
	    	ArrayList<ArrayList<?>> rowData1=null;
	    	String key="GaDataObject_"+app+"_"+dateFrom.replaceAll("-", "")+"_"+keyElement;
	    	System.out.println("fetchGAData  from Datastore");
	    	System.out.println("Key:::"+key);
	    	ByteArrayInputStream bis=null;
	    	ObjectInput in = null;
	    	byte[] rowDataInByte=new byte[0];
	    	try {
	    	 
	    		System.out.println("#######Fetching Data Form  MemCache#######");
	    		HashMap<Object,Object> cacheMap=MemCacheServiceHelper.getCacheContents(key);
	    		if(cacheMap!=null && cacheMap.get("value")!=null)
	    		{
	    			System.out.println(cacheMap.get("value"));
	    			rowDataInByte=(byte[])cacheMap.get("value");
	    		}
	    		else{
	    		System.out.println("#######Fetching Data Form  Data Store#######");
	    		rowDataInByte =DataStoreManager.get(key,dimension);
	    		}
	    	  
	    	 // System.out.println("cache data::"+appcachemanager.get(key,dimension));
	    	  
	    	  System.out.println("rowDataInByte"+rowDataInByte);
	    	  if(rowDataInByte!=null)
	    	  {
		    	  if(rowDataInByte.length>1)
		    	  {	System.out.println("byte array not null");
		    	  	
			    	  rowData=(ArrayList<ArrayList<?>>)convertJsonToObject(ZipData.extractBytes(rowDataInByte));
			   // 	  rowData1=(ArrayList<ArrayList<?>>)convertJsonToObject(ZipData.extractBytes((byte[])appcachemanager.get(key,dimension)));
			    	  //System.out.println(rowData1);
		    	  }
	    	  }
	    	} 
	    	catch(Exception e)
	    	{System.out.println("catched");
	    		printStackTrace(e);
	    		
	    	}
	    	
	    	return rowData;
	    			
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
	    
	    public static String convertObjectToJson( Object historyObject ) throws JsonGenerationException , JsonMappingException , IOException
	    {
	    	
	    	Writer writer = new StringWriter();
	    	String result = "";
	    	mapper.writeValue( writer , historyObject );
	    	result = writer.toString();
	    	return result;
	    }
	    
	    public static ArrayList <ArrayList <?>> convertJsonToObject( String obj )	throws JsonParseException ,
		JsonMappingException ,
		IOException
		{
			ArrayList <ArrayList <?>> rowDataArrayList = mapper.readValue( obj ,
			new TypeReference <ArrayList <ArrayList <?>>>()
			{
			} );
			return rowDataArrayList;
		}
	    
	    
	    
	    

}
