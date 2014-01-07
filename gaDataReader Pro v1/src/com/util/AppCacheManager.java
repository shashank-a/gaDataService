/*
 * AppCacheManager.java Created on July 19, 08:22 PM IST Original code moved
 * from SB-Commonutil to SB4.0 and Java in memory Cache moved to GoogleMemCache
 * Previously was using WhirlyCache.
 */
package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.acti.cache.serverresource.DatastoreCacheServiceHelper;
import com.acti.cache.serverresource.CachePersistentHelper;

public class AppCacheManager
	{
		private static Logger mLogger = Logger.getLogger( AppCacheManager.class.getPackage().getName() );
		public static String set( String key , Object value )
			{
				HashMap <String , Object> cachedObject = new HashMap <String , Object>();
				cachedObject.put( "cacheKey" , key );
				String resp = "Failed";
				try
					{
						cachedObject.put( "value" , zip( objectToByte (value) ) );
						resp = DatastoreCacheServiceHelper.setObject( cachedObject );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
						return resp;
					}
				return resp;
			}


		public static String set( String accountNumber , String key , Object value ) throws Exception
			{
				
				HashMap <String , Object> cachedObject = new HashMap <String , Object>();
				cachedObject.put( "cacheKey" , key );
				cachedObject.put( "accountNumber" , accountNumber );
				String resp = "Failed";
				try
					{
						cachedObject.put( "value" , zip( objectToByte (value) ) );
						resp = DatastoreCacheServiceHelper.setObject( cachedObject );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
						return resp;
					}
				return resp;
			}

		public static String set( String accountNumber , String key , Object value , Object hash )
			{
				HashMap <String , Object> cachedObject = new HashMap <String , Object>();
				key += String.valueOf( hash );
				cachedObject.put( "cacheKey" , key );
				cachedObject.put( "accountNumber" , accountNumber );
				String resp = "Failed";
				try
					{
						cachedObject.put( "value" , zip( objectToByte (value) ) );
						resp = DatastoreCacheServiceHelper.setObject( cachedObject );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
						return resp;
					}
				return resp;
			}


		public static Object get( String accountNumber , String key )
			{
				Object valueFromCache = null;
				Object memCacheValue = null;
				try
					{
						long DAIM = System.currentTimeMillis();
						
						valueFromCache = CachePersistentHelper.getMemCacheContents(key);
//						mLogger.info(" 1.Mem.C ["+key+"] - ["+accountNumber+"] ---> [ "+((valueFromCache !=null)?"_Success_":"_Failed_")+" ] Time --> "+(System.currentTimeMillis()-DAIM));
						if ( valueFromCache != null && (memCacheValue =  unZip( (byte[]) valueFromCache ) )!= null )
							return memCacheValue;
						else
							{
								HashMap <String , Object> cachedObject = new HashMap <String , Object>();
								cachedObject.put( "cacheKey" , key );
								cachedObject.put( "accountNumber" , accountNumber );
								valueFromCache = DatastoreCacheServiceHelper.getObject( cachedObject );
								if ( valueFromCache != null ){
									CachePersistentHelper.persistCacheStore( (byte[]) valueFromCache , key , accountNumber , true );
									valueFromCache =  unZip( (byte[]) valueFromCache  );
								}
							}
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
				return valueFromCache;
			}

		public static Object get( String accountNumber , String key , Object hash )
			{
				Object valueFromCache = null;
				Object memCacheValue = null;
				key += String.valueOf( hash );
				try
					{
					long DAIM = System.currentTimeMillis();
					valueFromCache = CachePersistentHelper.getMemCacheContents(key);
//					mLogger.info(" 2.Mem.C ["+key+"] - ["+accountNumber+"] ---> [ "+((valueFromCache !=null)?"_Success_":"_Failed_")+" ] Time --> "+(System.currentTimeMillis()-DAIM));
					if ( valueFromCache != null && (memCacheValue = byteToObject( unZip( (byte[]) valueFromCache ) ))!= null )
						return memCacheValue;
					else
						{
							HashMap <String , Object> cachedObject = new HashMap <String , Object>();
							cachedObject.put( "cacheKey" , key );
							cachedObject.put( "accountNumber" , accountNumber );
							
									valueFromCache = DatastoreCacheServiceHelper.getObject( cachedObject );
									if( valueFromCache != null ){
										
										CachePersistentHelper.persistCacheStore( (byte[]) valueFromCache , key , accountNumber , true );
										valueFromCache = byteToObject(unZip((byte[])valueFromCache));
										
									}
							}
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
				return valueFromCache;
			}

		public static Object get( String key )
			{
				Object valueFromCache = null;
				Object memCacheValue =  null;
				try
					{
						long DAIM = System.currentTimeMillis();
						valueFromCache = CachePersistentHelper.getMemCacheContents( key );
//						mLogger.info(" 3.Mem.C ["+key+"] ---> [ "+((valueFromCache !=null)?"_Success_":"_Failed_")+" ] Time --> "+(System.currentTimeMillis()-DAIM));
						if ( valueFromCache != null && (memCacheValue =  unZip( (byte[]) valueFromCache ) )!= null )
							return memCacheValue;
						else
							{
								HashMap <String , Object> cachedObject = new HashMap <String , Object>();
								cachedObject.put( "cacheKey" , key );

								valueFromCache = DatastoreCacheServiceHelper.getObject( cachedObject );
								if ( valueFromCache != null ) {
									CachePersistentHelper.persistCacheStore( (byte[]) valueFromCache , key , null , true );
									valueFromCache =  unZip( (byte[]) valueFromCache ) ;
								}
									
							}
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
				return valueFromCache;
			}

		/*
		 * Both Memcache and Datastore, cleared for particular key.
		 */
		public static void remove( HashMap<String,String> removeCacheObj )
			{
				try
					{
						CachePersistentHelper.removeObject( removeCacheObj ,false  );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
			}
		
		/*
		 * MemCache only removed for specific key.
		 */
		public static void removeFromMemCache( HashMap<String,String> removeCacheObj )
			{
				try
					{
						CachePersistentHelper.removeObject( removeCacheObj ,true  );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
			}
		
		/*
		 * Entire dataStore and MemCache will be cleared.
		 */
		public static void removeAll(boolean isMemCacheOnly)
			{
				try
					{
						CachePersistentHelper.removeAll(isMemCacheOnly);
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
			}
		

	public static byte[] zip( byte[] data ) 
		{
			try
				{
					byte[] input = data; // the format... data is the total string
					Deflater df = new  Deflater(Deflater.BEST_COMPRESSION,true); // this function mainly generate the byte code
					df.setInput( input );
					ByteArrayOutputStream baos = new ByteArrayOutputStream( input.length ); // we write the generated byte code in this array
					df.finish();
					byte[] buff = new byte [1024]; // segment segment pop....segment set 1024
					while ( !df.finished() )
						{
							int count = df.deflate( buff ); // returns the generated code... index
							baos.write( buff , 0 , count ); // write 4m 0 to count
						}
					baos.close();
					byte[] output = baos.toByteArray();
//					mLogger.info(" Compressed from ["+data.length+"] to ["+output.length+"] is ==> "+(data.length-output.length));
					return output;
				}
			catch ( Exception zip )
				{
					zip.printStackTrace();
					mLogger.info("  ===  zipping Exception ===  ");
					return null;
				}
		}

	public static byte[] unZip( byte[] input ) 
		{
			try
				{
					Inflater ifl = new Inflater( true );
					ifl.setInput( input );
					ByteArrayOutputStream baos = new ByteArrayOutputStream( input.length );
					byte[] buff = new byte [1024];
					while ( !ifl.finished() )
						{
							int count = ifl.inflate( buff );
							baos.write( buff , 0 , count );
						}
					baos.close();
					byte[] output = baos.toByteArray();
//					mLogger.info(" unZipped from ["+input.length+"] to ["+output.length+"] is ==> "+(output.length-input.length));
					return output;
				}
			catch ( Exception unzip )
				{
					unzip.printStackTrace();
					mLogger.info(" Unzipping Exception so returning  Null ");
					return null;
				}
		}
	
	public static byte[] objectToByte( Object value ){
		try
			{
				ByteArrayOutputStream byteObject = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteObject );
				objectOutputStream.writeObject( value );
				objectOutputStream.flush();
				objectOutputStream.close();
				byteObject.close();
				return byteObject.toByteArray();
			}
		catch ( Exception e )
			{
				e.printStackTrace();
				return null;
			}
	}
	
	public static Object byteToObject( byte[] value ){
		Object lValue = null;
		try
			{
				ByteArrayInputStream byteInputStream = new ByteArrayInputStream( value );
				ObjectInputStream objectInputStream = new ObjectInputStream( byteInputStream );
				lValue = objectInputStream.readObject();
				objectInputStream.close();
				byteInputStream.close();
				return lValue;
			}
		catch ( Exception e )
			{
				e.printStackTrace();
				return null;
			}
	}			
	
	public static String objToJson(Object historyObject){
		try
			{
				ObjectMapper mapper = new ObjectMapper();
				Writer writer = new StringWriter();
				String result = "";
				mapper.writeValue( writer , historyObject );
				result = writer.toString();
				return  result;
			}
		catch ( Exception json )
			{
				json.printStackTrace();
				return null;
			}
	}
}
