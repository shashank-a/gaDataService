package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

public class AppMemCacheManager {	
	private static Cache cache;
	
	static{
		try
		{
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
            System.out.println("-----let we check......" + cache.isEmpty());

		}
	catch ( CacheException e )
		{
			e.printStackTrace();
			cache = null;
		}catch (Exception e) {
			// TODO: handle exception	
			e.printStackTrace();
		}
	}

	public static void set(String key, Object accountNumber, byte[] value){
		
		System.out.println("AppMemCacheManager.set(Object key, Object accountNumber, Object value): key=" + key + ", accountNumber=" + accountNumber);
		
		try{
								
			byte[] objectContent = value;
			
			ArrayList<String> keyList = null;
			CacheKeyObject  cacheKey = null;
			
			long memSizeOfObject = objectContent.length;
			
			String newCacheKey;
			String currentCacheKey = (String) key;
			
			if(!key.contains("SubHistory")){
				cacheKey = DataStoreManager.getObj(accountNumber);
				
				System.out.println("while setting the cache.... " + (cacheKey == null));
				
				if(cacheKey == null){
					cacheKey = new CacheKeyObject();				
					cacheKey.setKeyList(new ArrayList());
				}else if(cacheKey.getKeyList() == null){
					cacheKey.setKeyList(new ArrayList());
				}
				
				cacheKey.setAccountNumber((String)accountNumber);								
				
				keyList = cacheKey.getKeyList();
				
				System.out.println("----------keylist while retriving-------------" + keyList.size());
				
				keyList.add(currentCacheKey);
			}
																		
			System.out.println("we are in set 3 " + memSizeOfObject + " " +  (memSizeOfObject/900000));
			
			int index, keyCount = 1;
			
			for(index = 0; index < (memSizeOfObject/900000); index++){
				
				byte[] tempList = new byte[900000];
				
				System.arraycopy(objectContent, index*900000, tempList, 0, 900000);
				
				System.out.println("we are in set 4 " + index + " " + tempList.length);
				
				HashMap<Object, Object> cachMap = new HashMap<Object, Object>();
				cachMap.put("value", tempList);
				newCacheKey = ((String) key) + "_" + keyCount;
				keyCount++;
				cachMap.put("nextObjKey", newCacheKey);
				
				cache.put(currentCacheKey, cachMap);
				
				System.out.println("we are in set 5 " + currentCacheKey);
											
				currentCacheKey = newCacheKey;
				if(!key.contains("SubHistory")){
					keyList.add(newCacheKey);
				}
				
			}
			
			byte[] tempList = new byte[(int)(memSizeOfObject % 900000)];
			System.arraycopy(objectContent, index*900000, tempList, 0, (int)(memSizeOfObject % 900000));				
			HashMap<Object, Object> cachMap = new HashMap<Object, Object>();
			cachMap.put("value", tempList);
			newCacheKey = null;				
			cachMap.put("nextObjKey", newCacheKey);
			
			cache.put(currentCacheKey, cachMap);
			System.out.println("we are in set 51 " + currentCacheKey);
			
			if(!key.contains("SubHistory")){
				cacheKey.setKeyList(keyList);
				DataStoreManager.setObj(cacheKey);
				
				System.out.println("----------keylist while saving-------------" + cacheKey.getKeyList().size());
			}
									
			System.out.println("we are in set 6 " + cache.get(currentCacheKey));
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	/*public static void set(Object key, Object value){
		
		
		System.out.println("AppMemCacheManager.set(Object key, Object accountNumber, Object value): key=" + key);
		
		try{
			ByteArrayOutputStream byteObject = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteObject);
			objectOutputStream.writeObject(value);			
			objectOutputStream.flush();
			objectOutputStream.close();
			byteObject.close();
			
			System.out.println("we are in set 2");
			
			byte[] objectContent = byteObject.toByteArray();
			
			long memSizeOfObject = objectContent.length;
			
			String newCacheKey;
			String currentCacheKey = (String) key;
			
			System.out.println("we are in set 3 " + memSizeOfObject + " " +  (memSizeOfObject/900000));
			
			int index, keyCount = 1;
			
			for(index = 0; index < (memSizeOfObject/900000); index++){
				
				byte[] tempList = new byte[900000];
				
				System.arraycopy(objectContent, index*900000, tempList, 0, 900000);
				
				System.out.println("we are in set 4 " + index + " " + tempList.length);
				
				HashMap<Object, Object> cachMap = new HashMap<Object, Object>();
				cachMap.put("value", tempList);
				newCacheKey = ((String) key) + "_" + keyCount;
				keyCount++;
				cachMap.put("nextObjKey", newCacheKey);
				
				cache.put(currentCacheKey, cachMap);
				
				System.out.println("we are in set 5 " + currentCacheKey);
											
				currentCacheKey = newCacheKey;										
				
			}
			
			byte[] tempList = new byte[(int)(memSizeOfObject % 900000)];
			System.arraycopy(objectContent, index*900000, tempList, 0, (int)(memSizeOfObject % 900000));				
			HashMap<Object, Object> cachMap = new HashMap<Object, Object>();
			cachMap.put("value", tempList);
			newCacheKey = null;				
			cachMap.put("nextObjKey", newCacheKey);
			
			cache.put(currentCacheKey, cachMap);
			
			System.out.println("we are in set 6 " + tempList.length + " " + currentCacheKey);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}*/
	
	public static void set(Object key, byte[] objectContent){
		
		
		System.out.println("AppMemCacheManager.set(Object key, Object accountNumber, Object value): key=" + key);
		
		try{			
			
			long memSizeOfObject = objectContent.length;
			
			String newCacheKey;
			String currentCacheKey = (String) key;
			
			System.out.println("we are in set 3 " + memSizeOfObject + " " +  (memSizeOfObject/900000));
			
			int index, keyCount = 1;
			
			for(index = 0; index < (memSizeOfObject/900000); index++){
				
				byte[] tempList = new byte[900000];
				
				System.arraycopy(objectContent, index*900000, tempList, 0, 900000);
				
				System.out.println("we are in set 4 " + index + " " + tempList.length);
				
				HashMap<Object, Object> cachMap = new HashMap<Object, Object>();
				cachMap.put("value", tempList);
				newCacheKey = ((String) key) + "_" + keyCount;
				keyCount++;
				cachMap.put("nextObjKey", newCacheKey);
				
				cache.put(currentCacheKey, cachMap);
				
				System.out.println("we are in set 5 " + currentCacheKey);
											
				currentCacheKey = newCacheKey;										
				
			}
			
			byte[] tempList = new byte[(int)(memSizeOfObject % 900000)];
			System.arraycopy(objectContent, index*900000, tempList, 0, (int)(memSizeOfObject % 900000));				
			HashMap<Object, Object> cachMap = new HashMap<Object, Object>();
			cachMap.put("value", tempList);
			newCacheKey = null;				
			cachMap.put("nextObjKey", newCacheKey);
			
			cache.put(currentCacheKey, cachMap);
			
			System.out.println("we are in set 6 " + tempList.length + " " + currentCacheKey);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void removeObject(Object key){
		
		System.out.println("AppMemCacheManager.removeObject(Object key) : key=" + key);
		
		try{
			
			CacheKeyObject cacheKey = DataStoreManager.getObj(key);
			
			System.out.println("1   let we check cacheKey " + cacheKey);
			
			if(cacheKey == null){
				cache.remove(key);
			}else{
				ArrayList<String> keyList = cacheKey.getKeyList();
				System.out.println("2     let we check cacheKey " + keyList.size());
				for(int index = 0; index < keyList.size(); index++){
					String cKey = (String)keyList.get(index);
					cache.remove(cKey);
				}
				
				DataStoreManager.removeObj(key);
			}
			
			
			/*HashMap<Object, Object> value = (HashMap<Object, Object>) cache.get(key);		
			
			System.out.println("we are in get 1");
			Object temp_Key = null;
			if(value != null){
				//newList = new ArrayList();
				
				while(key != null){
					value = (HashMap<Object, Object>) cache.get(key);
					System.out.println("we are in get 2 " + key);
					temp_Key = key;
					key = value.get("nextObjKey");
					cache.remove(temp_Key);						
				}					
			}*/
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public static void removeObject(Object key, Object accountNumber){
		
		System.out.println("AppMemCacheManager.removeObject(Object key) : key=" + key + accountNumber);
		
		try{
			
			CacheKeyObject cacheKey = DataStoreManager.getObj(accountNumber);
			
			if(cacheKey != null){				
				ArrayList<String> keyList = cacheKey.getKeyList();				
				for(int index = 0; index < keyList.size(); index++){					
					String cKey = (String)keyList.get(index);
					System.out.println("AppMemCacheManager.removeObject(Object key) : key= " + key + " " + cKey);
					if(cKey.contains((String)key)){
						System.out.println("removeing");
						cache.remove(cKey);
						keyList.remove(index--);						
					}					
				}
				
				//DataStoreManager.removeObj(key);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public static byte[] get(Object key, Object accountNumber){
		
		System.out.println("AppMemCacheManager.get(key,accountNumber): key=" + key + ", accountNumber=" + accountNumber);
		
		byte[] value = null;
		
		try{
			value = get(key);
			/*HashMap<Object, Object> accountHashMap = null;
			
			byte[] accountByteMap = get(accountNumber);
			
			if(accountByteMap != null){
				ByteArrayInputStream byteInputStream = new ByteArrayInputStream(accountByteMap);
				ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
			
			// Get value hash from Cache for the account
				accountHashMap = (HashMap<Object, Object>) objectInputStream.readObject();
			
			// Let's get accountHashMap form cache
			//HashMap<Object, Object> accountHashMap = (HashMap<Object, Object>) get(accountNumber);
			
			// IF accountHash map is not there in cache (When first value being set in accountHashMap)
				
			}
			//System.out.println("have we got the hashMap " + accountHashMap);
			
			if(accountHashMap != null){
				// Check whether we have the value for the key in accountHashMap
				value = (byte[]) accountHashMap.get(key);
				// If we don't get value from accountHashMap
				if(value == null){
					// Let we directly check in cache
					value = get(key);
				}
			}else{
				// Let we directly check in cache with the key
				value = get(key);
			}*/
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return value;
	}
	
	/*public static Object get(Object key){
		
		System.out.println("AppMemCacheManager.get(Object key): key=" + key);
		
		Object lValue = null;
		
		try{
		
			HashMap<Object, Object> value = (HashMap<Object, Object>) cache.get(key);		
			
			System.out.println("we are in get 1");
			
			if(value != null){
				//newList = new ArrayList();
				byte[] objectContent = new byte[0];
				while(key != null){
					value = (HashMap<Object, Object>) cache.get(key);
					System.out.println("we are in get 2 " + key);
					byte[] tempList = (byte[]) value.get("value");
					byte[] tempOriginal = objectContent;
					objectContent = new byte[objectContent.length+tempList.length];
					
					System.arraycopy(tempOriginal, 0, objectContent, 0, tempOriginal.length);
					System.arraycopy(tempList, 0, objectContent, tempOriginal.length, tempList.length);
					
					key = value.get("nextObjKey");
					//value = (HashMap) cache.get(key);
					
				}
				
				System.out.println("size of returning arrayList " + objectContent.length);
				
				ByteArrayInputStream byteInputStream = new ByteArrayInputStream(objectContent);
				ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
				
				lValue = objectInputStream.readObject();
				
				//System.out.println("we got the object " + obj.getClass());
				
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return lValue;
	}*/
	
	public static byte[] get(Object key){
		
		System.out.println("AppMemCacheManager.get(Object key): key=" + key);
		
		//Object lValue = null;
		
		byte[] objectContent = null;
		
		try{
		
			HashMap<Object, Object> value = (HashMap<Object, Object>) cache.get(key);		
			
			System.out.println("we are in get 1 " + value);
			
			if(value != null){
				//newList = new ArrayList();
				objectContent = new byte[0];
				while(key != null){
					value = (HashMap<Object, Object>) cache.get(key);
					System.out.println("we are in get 2 " + key);
					byte[] tempList = (byte[]) value.get("value");
					byte[] tempOriginal = objectContent;
					objectContent = new byte[objectContent.length+tempList.length];
					
					System.arraycopy(tempOriginal, 0, objectContent, 0, tempOriginal.length);
					System.arraycopy(tempList, 0, objectContent, tempOriginal.length, tempList.length);
					
					key = value.get("nextObjKey");
					//value = (HashMap) cache.get(key);
					
				}
				
				System.out.println("size of returning arrayList " + objectContent.length);
				
				//System.out.println("we got the object " + obj.getClass());
				
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return objectContent;
	}
	
	public static void removeAll(){
		System.out.println("AppMemCacheManager.removeAll():");
		try{
			cache.clear();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
