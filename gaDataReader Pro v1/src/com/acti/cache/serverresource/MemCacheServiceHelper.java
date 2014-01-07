package com.acti.cache.serverresource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;


import com.acti.cache.util.DataStoreObject;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
//import com.message.service.HistoryJsonParser;
//import com.sb.common.service.ScheduleBehaviour;
//import com.sb.common.action.actionhelper.InitialAccountHelper;

public class MemCacheServiceHelper
	{
		private static MemcacheService memCache;
		static {
			try
				{
					memCache = MemcacheServiceFactory.getMemcacheService();
					memCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.SEVERE));
				}
			catch ( Exception e )
				{
					e.printStackTrace();
					memCache = null;
				}
		} 
		
		public static HashMap <Object , Object> getCacheContents(String cacheKey){
			HashMap <Object , Object> cacheMapper = null;
			try
				{
					cacheMapper = (HashMap <Object , Object>) memCache.get( cacheKey );
				}
			catch ( Exception e )
				{
					return null;
				}
			return cacheMapper;
		}
		
		
		
		/**
		 * Setting byte[] contents into Memcache.
		 * @param cacheKey
		 * @param nextTurnkey
		 * @param content
		 * @param accountNumber
		 */
		public static void setInMemCache(String cacheKey, String nextTurnkey,byte[] content){
			HashMap <Object ,Object > cacheMapper = null;
			try
				{
					cacheMapper = new HashMap <Object , Object>();
					cacheMapper.put( "nextObjKey" , nextTurnkey );
					cacheMapper.put( "value" , content );
					System.out.println("Size of data"+content.length);
					/**
					 * While moving to Live i Will stop memCache Set.
					 */
					System.out.println( "MemCache set Key = " + cacheKey + " @ "
							+ ( ( getDateFormat() != null ) ? getDateFormat().format( Calendar.getInstance().getTime() ) : "" ) );
					memCache.put( cacheKey , cacheMapper );
					System.out.println("data set in Memcache....");
				}
			catch ( Exception e )
				{
					System.out.println(" ******** MemCache setting Error ******** ");
					e.printStackTrace();
				}
		}
		
		/*
		 * This the only function used during cache removal
		 */
		public static void removeFromMemCache( List <DataStoreObject> lDataStoreObject )
			{
				boolean retried = false;
				ArrayList <String> removeEntries = new ArrayList <String>();
				try
					{
						Iterator <DataStoreObject> ite = lDataStoreObject.iterator();
						System.out.println( " --- MemCache Cleared --- Start" );
						while ( ite.hasNext() )
							{
								DataStoreObject deletionObject = ite.next();
								memCache.delete( deletionObject.getKey() , 5000 );
								/**
								 * Checking Whether Object got envicted!
								 */
								if ( memCache.get( deletionObject.getKey() ) != null )
										memCache.delete( deletionObject.getKey() , 5000 );
								/**
								 * Print Time stamp
								 */
								System.out.println( deletionObject.getKey() + " @ "
										+ ( ( getDateFormat() != null ) ? getDateFormat().format( Calendar.getInstance().getTime() ) : "exception" ) );
								removeEntries.add( deletionObject.getKey() );
							}
						/**
						 * Entire List rechecked for enviction.
						 */
						memCache.deleteAll( removeEntries , 5000 );
						System.out.println( "MemCache Enteries to be cleared == " + removeEntries );
						System.out.println( " --- MemCache Cleared --- End" );
					}
				catch ( Exception memCacheDelException )
					{
						memCacheDelException.printStackTrace();

						StringBuilder errorMessage = new StringBuilder();
						errorMessage.append( " Time : "
								+ ( ( getDateFormat() != null ) ? getDateFormat().format( Calendar.getInstance().getTime() ) : "" ) + " <br>" );
						
						if ( !removeEntries.isEmpty() )
							{
								Iterator <String> cacheKeyIterator = removeEntries.iterator();
								errorMessage.append( "<br> Cache Keys which was in process of Deletion  : <br>" );
								while ( cacheKeyIterator.hasNext() )
									{
										String deletionKey = cacheKeyIterator.next();
										boolean isDeleted = memCache.get( deletionKey ) != null ? true : false;
										errorMessage.append( deletionKey + " envicted ? " + isDeleted + "<br>" );
									}
							}
						
						if ( memCacheDelException != null )
							{
								for ( StackTraceElement element : memCacheDelException.getStackTrace() )
									errorMessage.append( element.toString() + "<br>" );
							}

						if ( !retried && !removeEntries.isEmpty() )
							{
								memCache.delete( removeEntries , 5000 );
								retried = true;
							}
						errorMessage.append( "<br> Retried deletion : " + retried + " <br>" );
						/*new ScheduleBehaviour().mailSender( "" , " Exception MemCache Deletion " , errorMessage.toString() , null );*/
					}
			}
		
		/*public static void clearCacheForAffectedAccounts( String acctNum ){
			
			String accountId =  null;
			ArrayList <String> accountSpecific = new ArrayList <String>();
			ArrayList <String> accountIdSpecific = new ArrayList <String>();
			try
				{
					accountSpecific.add( "getAccountJDO" );
					accountSpecific.add( "getBrandInformation" );
					accountSpecific.add( "getContactNameAndAnswerphraseCMS" );
					accountSpecific.add( "processJobsJDOList" );
					if( StringUtils.isNotEmpty( acctNum ) )
						for ( String lAccountSpecific : accountSpecific )
							{
								removeFromMemCacheSingleKey( lAccountSpecific+acctNum );
							}
					
					accountIdSpecific.add( "getDirectoriesJDO" );
					accountIdSpecific.add( "getDynamicScriptHelper" );
					accountIdSpecific.add( "getEntireDeliveryMethodsDS" );
					accountIdSpecific.add( "getExportableMsgForms" );
					accountIdSpecific.add( "getMessageFormContentByAccount" );
					accountIdSpecific.add( "getScriptsTempplate" );
					accountIdSpecific.add( "getScriptsTempplateNew" );
					accountIdSpecific.add( "getSenariosByAccountIdDS" );
					
					ArrayList <Object> accountInfoMap = new InitialAccountHelper().getUniquePinWithRespectToAccountNumber( acctNum );
					if ( accountInfoMap != null && accountInfoMap.size() > 0 )
						{
							List <AccountJDO> accountJDO = new HistoryJsonParser().convertJsonToAccountInfo( (String) accountInfoMap.get( 0 ) );
							accountId = accountJDO.get( 0 ).getAccountId();
							if( StringUtils.isNotEmpty( accountId ) ){
								System.out.println(" AccountId for cache affected accounts is not null = "+accountId);
								for ( String lAccountIdSpecific : accountIdSpecific )
									{
										removeFromMemCacheSingleKey( lAccountIdSpecific+accountId );
									} 
							}
							else
								System.out.println(" AccountId for cache affected accounts is -- NULL -- = "+accountId);
						}
				}
			catch ( Exception e )
				{
					e.printStackTrace();
				}
		}*/
		
		public static void removeFromMemCacheSingleKey( DataStoreObject lDataStoreObject )
			{
				try
					{
						memCache.delete( lDataStoreObject.getKey() , 3000 );
						if ( memCache.get( lDataStoreObject.getKey() ) != null )
							{
								memCache.delete( lDataStoreObject.getKey() , 5000 );
								System.out.println( lDataStoreObject.getKey() + " - After Deletion - " + memCache.get( lDataStoreObject.getKey() ) );
							}
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
			}
		
		public static void removeFromMemCacheSingleKey( String memCachekey )
			{
				try
					{
						memCache.delete( memCachekey , 3000 );
						if ( memCache.get( memCachekey ) != null )
							{
								memCache.delete( memCachekey , 5000 );
								System.out.println( memCachekey + " - After Deletion - " + memCache.get( memCachekey ) );
							}
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
			}
		
		
		
		public static DateFormat getDateFormat(){
			try
				{
					DateFormat df = new SimpleDateFormat( "E, MMMM d y 'at' HH:mm:ss z" );
					df.setTimeZone( TimeZone.getTimeZone( "America/Los_Angeles" ) );
					return df;
				}
			catch ( Exception e )
				{
					e.printStackTrace();
					return null;
				}
		}
		
		/**
		 * MemCache Flush
		 */
		public static void clearMemCache(){
			System.out.println(" Before clearing memcache size = "+memCache.getStatistics().getTotalItemBytes());
			memCache.clearAll();
			System.out.println(" Before clearing memcache size = "+memCache.getStatistics().getTotalItemBytes());
		}
	}
