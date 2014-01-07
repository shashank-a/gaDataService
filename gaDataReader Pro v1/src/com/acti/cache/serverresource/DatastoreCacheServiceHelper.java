package com.acti.cache.serverresource;
	
import java.util.HashMap;

import com.acti.cache.util.DataStoreManager;

public class DatastoreCacheServiceHelper {
	
		public static Object getObject( HashMap <String , Object> cachedObjectMap )
			{

				String cacheKey = (String) cachedObjectMap.get( "cacheKey" );
				Object accountNumber = (String) cachedObjectMap.get( "accountNumber" );

				byte[] value = null;

				try
					{

						if ( accountNumber != null )
							value = DataStoreManager.get( cacheKey , accountNumber );
						else
							value = DataStoreManager.get( cacheKey );

					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
				return value;
			}
	
		public static String setObject( HashMap <String , Object> cachedObjectMap )
			{

				String cacheKey = (String) cachedObjectMap.get( "cacheKey" );
				Object accountNumber = cachedObjectMap.get( "accountNumber" );
				String resp = "";
				try
					{

						if ( accountNumber != null )
							resp = DataStoreManager.set( cacheKey , accountNumber , (byte[]) cachedObjectMap.get( "value" ) );
						else
							resp = DataStoreManager.set( cacheKey , (byte[]) cachedObjectMap.get( "value" ) );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
						return "Persisting Failed - key = "+cacheKey+" - accountNumber - "+accountNumber;
					}

				return resp;
			}
	
}
