


package com.util;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class GaCredential {
	
	@PrimaryKey	
	private String key;
	
	@Persistent
	private String GoogleCredential;

	
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

}
