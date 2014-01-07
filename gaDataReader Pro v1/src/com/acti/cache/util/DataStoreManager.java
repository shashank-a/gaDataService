package com.acti.cache.util;

import com.acti.cache.serverresource.CachePersistentHelper;

public class DataStoreManager
	{

		public static String set( Object key , Object accountNumber , byte[] value )
			{
				String resp = "Failed";
				try
					{
						resp = CachePersistentHelper.persistCacheStore( value , (String) key , (String) accountNumber, true );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
						return "Persisting Failed - key = "+key+" - accountNumber - "+accountNumber;
					}
				return resp;
			}
		
		/**
		 * cacheKey and value will be valid, Account number will be null.
		 */
		public static String set( Object key , byte[] objectContent )
			{
				String resp = "Failed";
				try
					{
						resp = CachePersistentHelper.persistCacheStore( objectContent , (String) key , null, true );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
						return "Persisting Failed - key = "+key;
					}
				return resp;
			}

		public static byte[] get( Object key , Object accountNumber )
			{

				byte[] value = null;
				try
					{
						value = get( key );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
				return value;
			}

		public static byte[] get( Object key )
			{

				byte[] objectContent = null;
				try
					{
						objectContent = CachePersistentHelper.getDataStoreContents( (String) key );
					}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
				return objectContent;
			}
	}
