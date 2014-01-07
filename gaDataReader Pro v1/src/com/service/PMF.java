package com.service;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class PMF {

	private static PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private PMF() {

	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}
}
