package com.service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.DataFormatException;

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

import com.acti.cache.serverresource.MemCacheServiceHelper;
import com.util.DataStoreManager;
import com.util.ZipData;

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
	    	HashMap<String, Object> hm=null;
	    	try {
	    	 
	    		
	    		System.out.println("#######Fetching Data Form  Data Store#######");
	    		rowDataInByte =DataStoreManager.get(key,dimension);
	    	  
	    	 // System.out.println("cache data::"+appcachemanager.get(key,dimension));
	    	  
	    	  System.out.println("rowDataInByte"+rowDataInByte);
	    	  if(rowDataInByte!=null)
	    	  {
		    	  if(rowDataInByte.length>1)
		    	  {	System.out.println("byte array not null");
		    	   hm = (HashMap<String,Object>)convertJsonToHashMap(ZipData.extractBytes(rowDataInByte));
		    	   rowData=(ArrayList<ArrayList<?>>)hm.get("gaDataList");
		    		  System.out.println("list data received..."+rowData.size());
			    	  //rowData=(ArrayList<ArrayList<?>>)convertJsonToObject(ZipData.extractBytes(rowDataInByte));
		    	  	//rowData1=(ArrayList<ArrayList<?>>)convertJsonToObject(ZipData.extractBytes((byte[])appcachemanager.get(key,dimension)));
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
	    
	    public static HashMap<String,Object> convertJsonToHashMap( String obj )	throws JsonParseException ,
		JsonMappingException ,
		IOException
		{
	    	HashMap<String,Object> gaDataMap = mapper.readValue( obj ,
			new TypeReference <HashMap<String,Object>>()
			{
			} );
			return gaDataMap;
		}
	    
	    public static ArrayList <String> convertJsonToObjectArray( String obj )	throws JsonParseException ,
		JsonMappingException ,
		IOException
		{
			ArrayList <String> rowDataArrayList = mapper.readValue( obj ,
			new TypeReference <ArrayList <String>>()
			{
			} );
			return rowDataArrayList;
		}
	    public static HashMap <String,String> convertJsonToMap( String obj )	throws JsonParseException ,
		JsonMappingException ,
		IOException
		{
			HashMap <String,String> rowDataArrayList = mapper.readValue( obj ,
			new TypeReference <HashMap <String,String>>()
			{
			} );
			return rowDataArrayList;
		}
	    
	    // methods to read Datastore in batch operation...
	    
	    public static ArrayList<ArrayList<?>> fetchGADataBatch(String dateFrom, String dateTo, String dimension, String keyElement, String app, String range) throws IOException, ClassNotFoundException
	    {
	    	//ArrayList<ArrayList<?>> rowData=null;
	    	ArrayList<ArrayList<?>> rows=null;
	    	ArrayList<ArrayList<?>> list=new ArrayList<ArrayList<?>>();
	    	ArrayList<String> keyList=null;
	    	
	    	HashMap<String,Object> hm=null;
	    	
	    	byte[] rowDataInByte=new byte[0];
	    	try {
	    	String key="GaDataObject_"+app+"_"+dateFrom.replaceAll("-", "")+"_"+keyElement;
	    	if(range!=null)
	    	{
	    		key="GaDataObject_"+app+"_"+dateFrom.replaceAll("-", "")+"_"+dateTo.replaceAll("-", "");
	    	}
	    		
	    		System.out.println("#######Fetching Data Form  Data Store#######"+key);
	    		rowDataInByte =DataStoreManager.get(key,dateFrom.replaceAll("-", ""));

	    	  if(rowDataInByte!=null)
	    	  {
		    	  if(rowDataInByte.length>1)
		    	  {	System.out.println("byte array not null");
		    	  	
		    	  hm=(HashMap<String,Object>)convertJsonToHashMap(ZipData.extractBytes(rowDataInByte));
		    	  rowDataInByte=null;
				    	  if(hm.get("gaDataList")==null)
				    	  {
				    	  byte[] byparts=new byte[0];
					    	  if(hm!=null)
					    	  {
					    		  System.out.println("Fetching KeyLIst Data from DataStore...");
					    		   keyList=(ArrayList<String>) hm.get("keyList");
					    		   for(String k:keyList)
					    		   {
					    			   byparts=DataStoreManager.get(k,dateFrom.replaceAll("-", ""));
					    			   rows=convertJsonToObject(ZipData.extractBytes(byparts));
					    			   list.addAll(rows);
					    		   }
					    		   hm.put("gaDataList", list);
					    		   hm.put("length",list.size());
					    		   byte [] compressData=ZipData.compressBytes(convertObjectToJson(hm).toString());
					    		   System.out.println("DataCompressed");
					    		   
					    		   DataStoreManager.set(key,dateFrom.replaceAll("-", ""),compressData);
					    		   System.out.println("Data Written in Cache....."+key);
					    	  }
				    	  }
				    	  else
				    	  {
				    		  list=(ArrayList<ArrayList<?>>)hm.get("gaDataList");
				    		  System.out.println("list data received..."+list.size());
						  }
		    	  }
		    	  
	    	  }
	    	} 
	    	catch(Exception e)
	    	{System.out.println("catched");
	    		printStackTrace(e);
	    		
	    	}
	    	
	    	return list;
	    			
	    }
	    
	    
	    public ArrayList<ArrayList<?>> addReportHeaders(ArrayList<ArrayList<?>> rowData)
	    {
	    	
	    	ArrayList<ArrayList<?>> ar= new ArrayList<ArrayList<?>>();
	    	ArrayList column=new ArrayList();
	    	column.add("Account");
	    	column.add("Action");
	    	column.add("Agent");
	    	column.add("OBNumber");
	    	column.add("InboundConnId");
	    	column.add("OutboundConnId");
	    	column.add("TimeStamp");
	    	column.add("Total");
	    	ar.add(column);
	    	ar.addAll(rowData);
	    
	    	return ar;
	    			
	    }
	    
	    
	    public String storeTempData(String key, Object obj, String dimension) throws JsonMappingException
	    {
	    	try {
				byte [] compressData=ZipData.compressBytes(convertObjectToJson(obj).toString());
				System.out.println("DataCompressed");
				
				//setting into datastore
				DataStoreManager.set(key,dimension,compressData);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return "";
	    }
	    
	    public String getTempData(String key, String dimension) throws JsonMappingException
	    {String jsonData="";
	    	try {
				
				System.out.println("DataCompressed");
				//setting into datastore
				
				byte[] rowDataInByte =DataStoreManager.get(key,dimension);
				jsonData=ZipData.extractBytes(rowDataInByte);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DataFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		return jsonData;
	    }
	    
	    
	    
	    

}
