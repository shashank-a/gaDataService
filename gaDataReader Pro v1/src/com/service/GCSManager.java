package com.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;





import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GCSManager {
	
	  private static final String APPLICATION_NAME = "gadataservice";

	  
	  private static final boolean IS_APP_ENGINE = false;


	  private static final String BUCKET_NAME = "gadataservice.appspot.com";

	  
	  private static final java.io.File DATA_STORE_DIR =
	      new java.io.File(System.getProperty("user.home"), ".store/storage_sample");
	  
	  private static FileDataStoreFactory dataStoreFactory;

	  /** Global instance of the HTTP transport. */
	  private static HttpTransport httpTransport;

	  /** Global instance of the JSON factory. */
	  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	  /** Global instance of this sample's settings. */
	  
	  private static Storage storage;
	  
	  public String accessBucket(Storage client) 
	  {
		  System.out.println("accessing buckeyt");
		  try {
			  System.out.println("getting bucket name");
			Storage.Buckets.Get getBucket = client.buckets().get(BUCKET_NAME);
			System.out.println("setting bucket name"+getBucket);
			getBucket.setProjection("full");
			System.out.println("befiore Executing");
			  Bucket bucket = getBucket.execute();
			  System.out.println("name: " + BUCKET_NAME);
			  System.out.println("location: " + bucket.getLocation());
			  System.out.println("timeCreated: " + bucket.getTimeCreated());
			  System.out.println("owner: " + bucket.getOwner());
			  
			  // List the contents of the bucket.
			  Storage.Objects.List listObjects = client.objects().list(BUCKET_NAME);
			  com.google.api.services.storage.model.Objects objects;
			  do {
			    objects = listObjects.execute();
			    for (StorageObject object : objects.getItems()) {
			      System.out.println(object.getName() + " (" + object.getSize() + " bytes)");
			    }
			    listObjects.setPageToken(objects.getNextPageToken());
			  } while (null != objects.getNextPageToken());
		} catch (IOException ioe)
		{
			System.out.println("IOE"+ioe);
		}
		  catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception caught");
			e.printStackTrace();
		}
		
	      
	      return null;
	  }


	  	

}
	  


