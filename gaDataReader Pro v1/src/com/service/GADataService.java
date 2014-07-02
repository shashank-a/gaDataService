package com.service;


import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;


public class GADataService {
	private static final Logger mLogger = Logger.getLogger(GaDatastoreService.class.getPackage().getName());
	
	
	static ResourceBundle resourceBundle= ResourceBundle.getBundle("GaReportConstant");
	
	

public static String getStackTrace(Throwable t)
{
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    t.printStackTrace(pw);
    pw.flush();
    sw.flush();
    return sw.toString();
}


public static void printStackTrace(Throwable t) {
mLogger.error(getStackTrace(t));
}
	
	
}
