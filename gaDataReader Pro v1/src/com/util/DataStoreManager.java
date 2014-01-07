package com.util;

import java.util.HashMap;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;


public class DataStoreManager {
	
	private static final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	
	public static void set(Object key, Object dimensions, byte[] value){
				
		System.out.println("DataStoreManager.set(Object key, Object accountNumber, Object value): key=" + key + ", accountNumber=" + dimensions);
		try{											
			
			byte[] objectContent = value;
			
			long memSizeOfObject = objectContent.length;
			
			PersistenceManager pm = pmf.getPersistenceManager();
			
			String newCacheKey;
			String currentCacheKey = (String) key;			
			
			System.out.println("datastore:we are in set 3 " + memSizeOfObject + " " +  (memSizeOfObject/900000));
			
			int index, keyCount = 1;
			
			for(index = 0; index < (memSizeOfObject/900000); index++){
				
				byte[] tempList = new byte[900000];
				
				System.arraycopy(objectContent, index*900000, tempList, 0, 900000);
				
				System.out.println("datastore:we are in set 4 " + index + " " + tempList.length);
				
				GaDataObject dso = new GaDataObject();
				
				newCacheKey = ((String) key) + "_" + keyCount;
				keyCount++;
				dso.setKey((String)currentCacheKey);
				dso.setDimension((String)dimensions);
				dso.setValue(tempList);
				dso.setNextDSOKey(newCacheKey);				
							
				pm.makePersistent(dso);
				
				System.out.println("datastore:we are in set 5 " + currentCacheKey);
											
				currentCacheKey = newCacheKey;														
			}
			
			byte[] tempList = new byte[(int)(memSizeOfObject % 900000)];
			System.arraycopy(objectContent, index*900000, tempList, 0, (int)(memSizeOfObject % 900000));
			newCacheKey = null;
			GaDataObject dso = new GaDataObject();
			dso.setKey((String)currentCacheKey);
			dso.setValue(tempList);
			dso.setDimension((String)dimensions);
			dso.setNextDSOKey(newCacheKey);			
			pm.makePersistent(dso);
			
			System.out.println("we are in set 6 " + dso.getNextDSOKey() + " " + currentCacheKey);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public static void set(Object key, byte[] objectContent){
		
		System.out.println("DataStoreManager.set(Object key, Object value): key=" + key);
		
		PersistenceManager pm = pmf.getPersistenceManager();
		
		try{
			
			long memSizeOfObject = objectContent.length;
			
			String newCacheKey;
			String currentCacheKey = (String) key;
			
			System.out.println("datastore:we are in set 3 " + memSizeOfObject + " " +  (memSizeOfObject/900000));
			
			int index, keyCount = 1;
			
			for(index = 0; index < (memSizeOfObject/900000); index++){
				
				byte[] tempList = new byte[900000];
				
				System.arraycopy(objectContent, index*900000, tempList, 0, 900000);
				
				System.out.println("datastore:we are in set 4 " + index + " " + tempList.length);
				
				GaDataObject dso = new GaDataObject();
				
				newCacheKey = ((String) key) + "_" + keyCount;
				keyCount++;
				dso.setKey((String)currentCacheKey);
				dso.setValue(tempList);
				dso.setNextDSOKey(newCacheKey);				
							
				pm.makePersistent(dso);
				
				System.out.println("datastore:we are in set 5 " + currentCacheKey);
											
				currentCacheKey = newCacheKey;														
			}
			
			byte[] tempList = new byte[(int)(memSizeOfObject % 900000)];
			System.arraycopy(objectContent, index*900000, tempList, 0, (int)(memSizeOfObject % 900000));
			newCacheKey = null;
			GaDataObject dso = new GaDataObject();
			dso.setKey((String)currentCacheKey);
			dso.setValue(tempList);
			dso.setNextDSOKey(newCacheKey);			
			pm.makePersistent(dso);
			
			System.out.println("we are in set 6 " + dso.getNextDSOKey() + " " + currentCacheKey);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}		
	}
	
	static void setObj(CacheKeyObject value){
		
		PersistenceManager pm = pmf.getPersistenceManager();
		System.out.println("dataStore manamager " + value.getKeyList().size() + " " + value.getAccountNumber());
		try{
			pm.makePersistent(value);						
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			pm.close();
		}
		
		CacheKeyObject value1 = getObj(value.getAccountNumber());
		
		//System.out.println("dateStore.setObj() = " + value1.getKeyList());
		
	}
	
	public static byte[] get(Object key, Object dimension){
		
		System.out.println("DataStoreManager.get(Object key, Object accountNumber): key=" + key + ", dimension=" +  dimension);
		
		byte[] value = null;
		
		try{
		
			HashMap<Object, Object> accountHashMap = null;
			value = get(key);
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		System.out.println("let's ");
		
		return value;
	}
	
	public static byte[] get(Object key){
		
		System.out.println("DataStoreManager.get(Object key): key=" + key);
	
		byte[] objectContent = null;
		
		PersistenceManager pm = pmf.getPersistenceManager();
		
		try{
		
			GaDataObject value = null; //= pm.getObjectById(GaDataObject.class, key);
			
			try{
				value = pm.getObjectById(GaDataObject.class, key);
				System.out.println("data length:::"+value.getValue().length);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			System.out.println("we are in get 1 ");
			
			if(value != null){
				objectContent = new byte[0];
				while(key != null){
					try{
						value = pm.getObjectById(GaDataObject.class, key);					
					}catch (Exception e) {
						// TODO: handle exception
						//e.printStackTrace();
					}
					System.out.println("we are in get 2 " + key);
					System.out.println("let's check the dimensions " + value.getDimension());
					byte[] tempList = (byte[]) value.getValue();
					byte[] tempOriginal = objectContent;
					objectContent = new byte[objectContent.length+tempList.length];
					System.arraycopy(tempOriginal, 0, objectContent, 0, tempOriginal.length);
					System.arraycopy(tempList, 0, objectContent, tempOriginal.length, tempList.length);
					key = value.getNextDSOKey();					
					
				}
				
				System.out.println("size of returning arrayList " + objectContent.length);
				
			}
		}catch(JDOObjectNotFoundException je){
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
		return objectContent;
	}
	
	static CacheKeyObject getObj(Object key){
		
		CacheKeyObject value = null;
		
		PersistenceManager pm = pmf.getPersistenceManager();
		
		try{
			value = pm.getObjectById(CacheKeyObject.class, key);
			value.getKeyList();
			
		}catch(JDOObjectNotFoundException je){
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
		
		return value;
	}
	
	static void removeObj(Object key){
		
		System.out.println("DataStoreManager.removeObj(Object key): key=" + key);
		
		PersistenceManager pm = pmf.getPersistenceManager();
		
		try{
			
			GaDataObject value = null; //= pm.getObjectById(GaDataObject.class, key);
			Object tempKey = null;
						
			Query query = pm.newQuery(CacheKeyObject.class);
			query.setFilter("accountNumber == accoutntNumberParam");
			query.declareParameters("String accoutntNumberParam");
			query.deletePersistentAll(key);
			System.out.println("we deleted all rows with key = " + key);
						
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
	}
	
	public static void removeObject(Object key){
		
		System.out.println("DataStoreManager.get(Object key): key=" + key);
		
		PersistenceManager pm = pmf.getPersistenceManager();
		
		try{
			
			GaDataObject value = null; //= pm.getObjectById(GaDataObject.class, key);
			Object tempKey = null;
						
			Query query = pm.newQuery(GaDataObject.class);
			query.setFilter("accountNumber == accoutntNumberParam");
			query.declareParameters("String accoutntNumberParam");
			query.deletePersistentAll(key);
			System.out.println("we deleted all rows with key = " + key);
						
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
		
		try{
			pm = pmf.getPersistenceManager();
			
			GaDataObject value = null; //= pm.getObjectById(GaDataObject.class, key);
			Object tempKey = null;
						
			Query query = pm.newQuery(GaDataObject.class);
			query.setFilter("key == accoutntNumberParam");
			query.declareParameters("String accoutntNumberParam");
			query.deletePersistentAll(key);
			System.out.println("we deleted all rows with key = " + key);
						
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
	}
	
	public static void removeObject(Object key, Object accountNumber){
		
		System.out.println("removeObject(Object key, Object accountNumber) Yet to be implemented....");
		
		System.out.println("DataStoreManager.get(Object key): key=" + key);
		
		PersistenceManager pm = pmf.getPersistenceManager();
		
		try{
			
			GaDataObject value = null; //= pm.getObjectById(GaDataObject.class, key);
			Object tempKey = null;
			
			
			Query query = pm.newQuery(GaDataObject.class);
			query.setFilter("key == keyParam && accountNumber == accoutntNumberParam");
			query.declareParameters("String keyParam, String accoutntNumberParam");
			query.deletePersistentAll(key, accountNumber);
			System.out.println("we deleted all rows with key = " + key);
			
			/*List<GaDataObject> results = (List<GaDataObject>) query.execute((String) key, (String) accountNumber);
			System.out.println("result set..." + results.size());*/
						
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
				
	}
	
	public static void removeAll(){
		
		System.out.println("DataStoreManager.removeAll():");
		
		PersistenceManager pm = pmf.getPersistenceManager();
		try {			
			Query query = pm.newQuery(GaDataObject.class);
			query.setFilter("key != keyParam");
			query.declareParameters("String keyParam");
			query.deletePersistentAll("`");			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
		
		pm = pmf.getPersistenceManager();
		
		try {			
			Query query = pm.newQuery(CacheKeyObject.class);
			query.setFilter("accountNumber != keyParam");
			query.declareParameters("String keyParam");
			query.deletePersistentAll("`");			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			pm.close();
		}
		
	}
}
