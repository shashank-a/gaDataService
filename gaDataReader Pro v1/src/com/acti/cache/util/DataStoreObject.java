package com.acti.cache.util;

import java.util.Arrays;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class DataStoreObject {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	@PrimaryKey	
	private String key;
	
	@Persistent
	private String accountNumber;
	
	@Persistent
	private String dateAdded;
	
	@Persistent
	private long dateAddedInMilliseconds;

	/**
	 * @return the dateAddedInMilliseconds
	 */
	public long getDateAddedInMilliseconds()
		{
			return dateAddedInMilliseconds;
		}

	/**
	 * @param dateAddedInMilliseconds the dateAddedInMilliseconds to set
	 */
	public void setDateAddedInMilliseconds( long dateAddedInMilliseconds )
		{
			this.dateAddedInMilliseconds = dateAddedInMilliseconds;
		}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Persistent(serialized = "true")
	private byte[] value;
	
	@Persistent
	private String nextDSOKey;	

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public String getNextDSOKey() {
		return nextDSOKey;
	}

	public void setNextDSOKey(String nextDSOKey) {
		this.nextDSOKey = nextDSOKey;
	}
	
	@Override
	public String toString()
		{
			return "DataStoreObject [key=" + key + ", accountNumber=" + accountNumber + ", dateAdded=" + dateAdded + ", dateAddedInMilliseconds="
					+ dateAddedInMilliseconds + ", nextDSOKey=" + nextDSOKey + "]";
		}

}
