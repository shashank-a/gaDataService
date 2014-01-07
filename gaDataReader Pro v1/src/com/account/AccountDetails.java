package com.account;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AccountDetails {
	
	
	private String  acctNum;
	private String  eventCategory;
	private String  eventAction;
	private String  eventLabel;
	private String  connID;
	private String  incomingANI;
	
	
	
	private int sendCount;
	private int saveCount;
	private int callConclusion;
	private int accLoad;
	
	private String callTotal;
	private int totalEvents; 
	
	
	
	
	
	public String getConnID() {
		return connID;
	}
	public void setConnID(String connID) {
		this.connID = connID;
	}
	public String getIncomingANI() {
		return incomingANI;
	}
	public void setIncomingANI(String incomingANI) {
		this.incomingANI = incomingANI;
	}
	public int getAccLoad() {
		return accLoad;
	}
	public void setAccLoad(int accLoad) {
		this.accLoad = accLoad;
	}
	LinkedHashMap <String,ArrayList<String>> callDetails;
	
	
	
	public int getTotalEvents() {
		return totalEvents;
	}
	public void setTotalEvents(int totalEvents) {
		this.totalEvents = totalEvents;
	}
	public String getEventCategory() {
		return eventCategory;
	}
	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}
	public String getEventAction() {
		return eventAction;
	}
	public void setEventAction(String eventAction) {
		this.eventAction = eventAction;
	}
	public String getEventLabel() {
		return eventLabel;
	}
	public void setEventLabel(String eventLabel) {
		this.eventLabel = eventLabel;
	}
	public String getAcctNum() {
		return acctNum;
	}
	public void setAcctNum(String acctNum) {
		this.acctNum = acctNum;
	}
	public int getSendCount() {
		return sendCount;
	}
	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}
	public int getSaveCount() {
		return saveCount;
	}
	public void setSaveCount(int saveCount) {
		this.saveCount = saveCount;
	}
	public int getCallConclusion() {
		return callConclusion;
	}
	public void setCallConclusion(int callConclusion) {
		this.callConclusion = callConclusion;
	}
	public LinkedHashMap<String, ArrayList<String>> getCallDetails() {
		return callDetails;
	}
	public void setCallDetails(LinkedHashMap<String, ArrayList<String>> callDetails) {
		this.callDetails = callDetails;
	}
	public String getCallTotal() {
		return callTotal;
	}
	public void setCallTotal(String callTotal) {
		this.callTotal = callTotal;
	}
	
	
	
	
	
}
