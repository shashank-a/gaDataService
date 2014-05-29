package com.util;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
//import com.google.appengine.tools.cloudstorage.GcsFileOptions;
//import com.google.appengine.tools.cloudstorage.GcsFilename;
//import com.google.appengine.tools.cloudstorage.GcsInputChannel;
//import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
//import com.google.appengine.tools.cloudstorage.GcsService;
//import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
//import com.google.appengine.tools.cloudstorage.RetryParams;
//import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
//import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
//import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Controller
public class GaBlobstoreService {
	
	 
	 
	 @SuppressWarnings("resource")
	 public String writeDataToBucket(String data)
	 {
		 String bucket = "http://gadataservice.appspot.com/GAData/";
		
		return "done"; 
	 }
}
