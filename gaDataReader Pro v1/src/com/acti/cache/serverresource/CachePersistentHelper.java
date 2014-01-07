package com.acti.cache.serverresource;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.acti.cache.util.DataStoreObject;
import com.service.PMF;
import com.util.AppCacheManager;

@SuppressWarnings( "unchecked" )
public class CachePersistentHelper
	{
		private static final Logger logger = Logger.getLogger( CachePersistentHelper.class.getPackage().getName() );
		/**
		 * @author kamesh
		 * Obtainin Object from MemCache if Present.
		 * @param key - PrimaryKey
		 * @return compressed byte[]
		 */
		public static byte[] getMemCacheContents(String key){
			byte[] objectContent = null;
			HashMap <Object , Object> memCacheMapper = null;
			boolean isSkip = false;
			try
				{
					/**
					 * MemCache call.
					 */
					memCacheMapper = MemCacheServiceHelper.getCacheContents( key );
					if( memCacheMapper != null ) {
						objectContent = new byte[0];
						isSkip = true;
						while( StringUtils.isNotEmpty( key ) ){
							if( isSkip )
								isSkip = false;
							else 
								memCacheMapper = MemCacheServiceHelper.getCacheContents( key );
								
								byte[] tempList = (byte[]) memCacheMapper.get( "value" );
								byte[] tempOriginal = objectContent;
								objectContent = new byte[objectContent.length+tempList.length];
								
								System.arraycopy(tempOriginal, 0, objectContent, 0, tempOriginal.length);
								System.arraycopy(tempList, 0, objectContent, tempOriginal.length, tempList.length);
								
								key = (String)memCacheMapper.get( "nextObjKey" );	
						}
					}
				}
			catch ( Exception e )
				{
					e.printStackTrace();
					return null;
				}
			return objectContent;
		}
		
		/**
		 * @author kamesh
		 * Obtaining data from datastore when, memCache got envicted or no Data
		 * for particular key in MemCache.
		 * @param key - primaryKey 
		 * @return compressed Byte[]
		 */
		public static byte[] getDataStoreContents(String key){
			DataStoreObject value = null;
			byte[] objectContent = null;
			PersistenceManager pm = null; 
			boolean isSkip = false;
			try
				{

					/**
					 * Datastore call.
					 */
					pm = PMF.get().getPersistenceManager();
					value = pm.getObjectById(DataStoreObject.class, key);
					/**
					 * skip flag is used in sense to Ignore, second time querying of same key in case of Object is chained with newObjkey
					 * When size of persisted object during persistence was > 900kb.
					 */
					if( value != null ) {
						objectContent = new byte[0];
						isSkip = true;
						while( StringUtils.isNotEmpty( key ) ){
							try{
								if( isSkip )
									isSkip = false;
								else 
									value = pm.getObjectById(DataStoreObject.class, key);

							}catch(JDOObjectNotFoundException je){
//								logger.info("_____ Specified Key not Found in DataStore _____ "+key);
								return null;
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							byte[] tempList = (byte[]) value.getValue();
							byte[] tempOriginal = objectContent;
							objectContent = new byte[objectContent.length+tempList.length];

							System.arraycopy(tempOriginal, 0, objectContent, 0, tempOriginal.length);
							System.arraycopy(tempList, 0, objectContent, tempOriginal.length, tempList.length);
							/**
							 * Iteratively key, changes in Case of Object size > 900kb.
							 */
							key = value.getNextDSOKey();					

						}
					}
				}
			catch(JDOObjectNotFoundException je){
//				logger.info("_____ Specified Key not Found in DataStore _____ "+key);
				return null;
			}
			catch ( Exception e )
				{
					e.printStackTrace();
					return null;
				}
			return objectContent;
		}
		
		/**
		 * @author kamesh
		 * 
		 * Persist's compressed Object in datastore and memcache, if fails - retries 3 times and 
		 * pushes mail to dev.sb@a-cti.com
		 * @param content
		 * @param primaryKey - Will be the key in datastore
		 * @param accountNumber  
		 * @param isMemCacheStoreOnly - flag to differentiate between MemCache set or Dual persist ( Datastore && MemCache )
		 * @return Status about transaction is returned.
		 */
		public static String persistCacheStore( byte[] content , String primaryKey , String accountNumber, boolean isMemCacheStoreOnly )
			{
				PersistenceManager pm = null;
				String currentCacheKey = primaryKey;
				String response = "--- MemCache Update --- ";
				if( !isMemCacheStoreOnly )
					response = " -- Persisting Failed -- " + currentCacheKey;
				int retries = 1;
				boolean retryFlag = false;
				DataStoreObject dso = null;
				try
					{
						String newCacheKey = null;

						byte[] objectContent = content;
						long memSizeOfObject = objectContent.length;

						pm = PMF.get().getPersistenceManager();
						DateFormat df = new SimpleDateFormat( "E, MMMM d y 'at' HH:mm:ss z" );
						df.setTimeZone( TimeZone.getTimeZone( "America/Los_Angeles" ) );
						int index , keyCount = 1;
						/**
						 * Loop used to split object, if size > 900kb. Memcache
						 * limit 1mb.
						 */
						for ( index = 0 ; index < ( memSizeOfObject / 900000 ) ; index++ )
							{
								byte[] tempList = new byte [900000];
								System.arraycopy( objectContent , index * 900000 , tempList , 0 , 900000 );
								newCacheKey = primaryKey + "_" + keyCount++ ;

								/**
								 * MemCache set is initiated!
								 */
								if ( isMemCacheStoreOnly )
									MemCacheServiceHelper.setInMemCache( currentCacheKey , newCacheKey , tempList );
								/**
								 * Persisting in datastore ignored when,
								 * memcache doesn't contain object, but
								 * datastore contains object. In such situation
								 * Memcache set only Mandatory.
								 */
								if ( !isMemCacheStoreOnly )
									{
										dso = new DataStoreObject();
										dso.setKey( currentCacheKey );
										dso.setAccountNumber( ( accountNumber != null ) ? accountNumber.trim() : "N/A" );
										dso.setValue( tempList );
										dso.setNextDSOKey( newCacheKey );
										dso.setDateAdded( df.format( Calendar.getInstance().getTime() ) );
										dso.setDateAddedInMilliseconds( Calendar.getInstance().getTime().getTime() );
										/**
										 * Initiating Transaction.
										 */
										synchronized (dso)
											{
												pm.currentTransaction().begin();
												pm.makePersistent( dso );
												pm.currentTransaction().commit();
											};
										response = "  Datastore SET  -- Persisted Successfully -- " + currentCacheKey;
									}
								currentCacheKey = newCacheKey;
							}

						/**
						 * Used when current size is lesser than 900kb 98%
						 * relies below 900kb.
						 */
						byte[] tempList = new byte [(int) ( memSizeOfObject % 900000 )];
						System.arraycopy( objectContent , index * 900000 , tempList , 0 , (int) ( memSizeOfObject % 900000 ) );
						newCacheKey = null;
						/**
						 * MemCache set is initiated!
						 */
						if ( isMemCacheStoreOnly )
							MemCacheServiceHelper.setInMemCache( currentCacheKey , newCacheKey , tempList );
						/**
						 * Persisting in datastore ignored when, memcache
						 * doesn't contain object, but datastore contains. In
						 * such situation Memcache set is Mandatory.
						 */
						if ( !isMemCacheStoreOnly )
							{
								dso = new DataStoreObject();
								dso.setKey( currentCacheKey );
								dso.setValue( tempList );
								dso.setAccountNumber( ( accountNumber != null ) ? accountNumber.trim() : "N/A" );
								dso.setNextDSOKey( newCacheKey );
								dso.setDateAdded( df.format( Calendar.getInstance().getTime() ) );
								dso.setDateAddedInMilliseconds( Calendar.getInstance().getTime().getTime() );
								/**
								 * Initiating Transaction.
								 */
								synchronized (dso)
									{
										pm.currentTransaction().begin();
										pm.makePersistent( dso );
										pm.currentTransaction().commit();
									};
								response = " -- Persisted Successfully -- " + currentCacheKey;
							}
					}
				catch ( Exception e )
					{
						logger.error( " persistCacheStore Main Cache Block " );
						MemCacheServiceHelper.removeFromMemCacheSingleKey(dso.getKey());
						/**
						 * 3 Times retry mechanism.
						 */
						logger.info(" ----------------------------------- Retry --------------------------------------------------- Start ");
						while ( retries <= 3 )
							{
								logger.error(" Retry Executing , retryNo - "+retries);
								retryFlag = dataStoreRetry( retries++ , dso );
								if( !retryFlag && retries > 3 ){
									/**
									 * pushing mail sequence.
									 */
									pushRetryFailedMail(retries,dso,"Retries Failed",e);
								}
								else if( retryFlag )
									{
//										pushRetrySuccessMail(retries,dso,e);
										logger.info(" Retry - "+(retries-1)+", Success - "+currentCacheKey);
										response = " -- Retry - "+(retries-1)+" Persisted Successfully -- " + currentCacheKey;
										break;
									}
								logger.error(" Retry "+(retries-1)+" Failed, Continuing...");
							}
						logger.info(" ----------------------------------- Retry --------------------------------------------------- End ");
						logger.info( response + currentCacheKey );
						return response;
					}
				finally
					{
						if ( pm.currentTransaction().isActive() ){
							pm.currentTransaction().rollback();
							pm.close();
							/**
							 * When rolling back the transaction we are removing the 
							 * memCache key.
							 */
							MemCacheServiceHelper.removeFromMemCacheSingleKey(dso.getKey());
						}
							
					}
				return response;
			}
		
		/**
		 * Datastore retry and  persist sequence.
		 * @return
		 */
		public static boolean dataStoreRetry( int retryNo , DataStoreObject lDataStoreObject )
			{
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try
					{
						if ( retryNo <= 3 )
							{
								pm.currentTransaction().begin();
								pm.makePersistent( lDataStoreObject );
								pm.currentTransaction().commit();
								return true;
							}
					}
				catch ( Exception e )
					{
						e.printStackTrace();
						MemCacheServiceHelper.removeFromMemCacheSingleKey(lDataStoreObject.getKey());
						return false;
					}
				finally
					{
						if ( pm.currentTransaction().isActive() )
							{
								pm.currentTransaction().rollback();
								pm.close();
								/**
								 * When rolling back the transaction we are removing the 
								 * memCache key.
								 */
								MemCacheServiceHelper.removeFromMemCacheSingleKey(lDataStoreObject.getKey());
							}
					}
				return false;
			}
		
		/**
		 * @author kamesh
		 * Based on AccountNumber or key, remove Operation will be performed.
		 * If AccountNumber and Key are present, Key routine proceeded.
		 * @param removeCacheObj
		 * @param isMemCacheRemoveOnly - flag to differentiate between MemCache remove or Dual remove ( Datastore && MemCache )
		 */
		public static void removeObject( HashMap <String , String> removeCacheObj , boolean isMemCacheRemoveOnly )
			{
				PersistenceManager pm = PMF.get().getPersistenceManager();
				List <DataStoreObject> list = null;
				if ( removeCacheObj != null )
					{
						String accountNumber = removeCacheObj.get( "accountNumber" );
						String key = removeCacheObj.get( "key" );
						String queryValue = null;
						try
							{
								Query query = pm.newQuery( DataStoreObject.class );
								if ( StringUtils.isNotEmpty( accountNumber ) )
									{
										query.setFilter( "accountNumber == keyOrAcctNum" );
										queryValue = accountNumber;
										/**
										 * Clearing cache for affected Accounts.
										 */
										/*MemCacheServiceHelper.clearCacheForAffectedAccounts(accountNumber);*/
									}
								else if ( StringUtils.isNotEmpty( key ) )
									{
										query.setFilter( "key == keyOrAcctNum" );
										queryValue = key;
									}
								query.declareParameters( "String keyOrAcctNum" );
								list = (List <DataStoreObject>) query.execute( queryValue );
								
								cacheRemove( list );
								
								if ( !isMemCacheRemoveOnly )
									query.deletePersistentAll( queryValue );
							}
						catch ( Exception e )
							{
								e.printStackTrace();
							}
						finally
							{
								pm.close();
							}
						if ( !isMemCacheRemoveOnly )
							logger.info( " Datastore and MemCache cleared - " + queryValue );
						else
							logger.info( " MemCache -- ONLY -- cleared for key - " + queryValue );
					}
			}
		
		/**
		 * @author kamesh
		 * Removing mapped key's from memCache.
		 * @param result list of keys to envict from memcache
		 */
		public static void cacheRemove( List <DataStoreObject> result )
			{
				List <DataStoreObject> deletionObect = null;
				deletionObect = new ArrayList <DataStoreObject>();

				for ( DataStoreObject lDataStoreObject : result )
					{
						deletionObect.add( lDataStoreObject );
					}
				MemCacheServiceHelper.removeFromMemCache( deletionObect );
			}
		
		/**
		 * @author kamesh
		 * Removing entire entity DataStoreObject From dataStore and flushing MemCache.
		 */
		public static void removeAll(boolean isMemCacheOnly){
			try
				{
					logger.info(" -------------------- Clearing Entire DataStore -------------------- ");
					
					if( !isMemCacheOnly )
						logger.info("Datastore object count - "+PMF.get().getPersistenceManager().newQuery(DataStoreObject.class).deletePersistentAll());
					else
						logger.info("DataStore will not be cleared -- MemCache Flag is = "+isMemCacheOnly);
					
					MemCacheServiceHelper.clearMemCache();
				}
			catch ( Exception e )
				{
					e.printStackTrace();
				}
		}
		
		
		/**
		 * @author kamesh
		 * Pushes failure notification of a retry to dev.sb
		 * @param Retry Count
		 * @param Datastore Object to obtain Object which was retried
		 * @param Heading
		 * @param lExcep to be added to bodyOfMail.
		 * @return status flag
		 */
		public static boolean pushRetryFailedMail(int retries,DataStoreObject dso,String Heading, Exception lExcep){
			try
				{
					MemCacheServiceHelper.removeFromMemCacheSingleKey(dso.getKey());
					StringBuilder errorMessage = new  StringBuilder();
					String acctOrCacheKey = ( dso.getAccountNumber() != null && !dso.getAccountNumber().equals( "N/A" )  ) ? dso.getAccountNumber().trim() : dso.getKey().trim();
					errorMessage.append( "<br>"+Heading+"<br>" );
					errorMessage.append( "<br>"+dso.toString()+"<br>" );
					errorMessage.append( "<br>Value : "+AppCacheManager.objToJson( AppCacheManager.byteToObject( AppCacheManager.unZip( (byte[]) dso.getValue() ) ) )+"<br>" );
					if( lExcep != null) {
						for ( StackTraceElement element : lExcep.getStackTrace() )
							errorMessage.append( element.toString() + "<br>" );
					}
					//new ScheduleBehaviour().mailSender( acctOrCacheKey , " Exception RETRY EXCEEDED " , errorMessage.toString() , null );
					return true;
				}
			catch ( Exception e )
				{
					e.printStackTrace();
					return false;
				}
		}
		
		/**
		 * @author kamesh
		 * Pushes success notification of a retry to dev.sb
		 * @param Retry count
		 * @param Datastore Object to obtain Object which was retried
		 * @return status flag
		 */
		public static boolean pushRetrySuccessMail(int retries,DataStoreObject dso, Exception lExcep){
			try
				{
					StringBuilder retrySuccess = new  StringBuilder();
					String acctOrCacheKey = ( dso.getAccountNumber() != null && !dso.getAccountNumber().equals( "N/A" )  ) ? dso.getAccountNumber().trim() : dso.getKey().trim();
					retrySuccess.append( "<br>Retry Sucess - "+(retries-1)+"<br>" );
					retrySuccess.append( "<br>"+dso.toString()+"<br>" );
					retrySuccess.append( "<br>Value : "+AppCacheManager.objToJson( AppCacheManager.byteToObject( AppCacheManager.unZip( (byte[]) dso.getValue() ) ) )+"<br><br>" );
					if( lExcep != null) {
						for ( StackTraceElement element : lExcep.getStackTrace() )
							retrySuccess.append( element.toString() + "<br>" );
					}
					//new ScheduleBehaviour().mailSender( acctOrCacheKey , " Exception RETRY Success - "+(retries-1)+" " , retrySuccess.toString() , null );
					return true;
				}
			catch ( Exception e )
				{
					e.printStackTrace();
					return false;
				}
		}
		
	}
