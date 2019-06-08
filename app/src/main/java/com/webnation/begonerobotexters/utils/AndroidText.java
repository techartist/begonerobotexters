package com.webnation.begonerobotexters.utils;

import android.content.Context;
import android.content.res.Resources;
import timber.log.Timber;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AndroidText {
	
    private String fileText = "";
    private String fileName = "";
    private Context context = null;
    
    public AndroidText(String filename, Context c) {
    	this.fileName = filename;
    	this.context = c;
    }
    
    
	public String getAndroidText() throws Resources.NotFoundException {
	   	 String fileContent = "";
	   	 int i;
	   	 int resourceID,resourceID2;
		 boolean throwsException = false;
	   	 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	       // try opening the myfilename.txt
	        try {
	       	 resourceID = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
	       	 Timber.d("resourceID =" + resourceID);
	       	 InputStream inputStream = context.getResources().openRawResource(resourceID);
	   	  
	   	     if (inputStream.available() > 0) { 
	   		    i = inputStream.read();
	   		  while (i != -1)
	   		      {
	   		       byteArrayOutputStream.write(i);
	   		       i = inputStream.read();
	   		      }
	   		      inputStream.close();
	   		     fileContent = byteArrayOutputStream.toString();
	   	  }
	   		  
	     } 
	     catch (Resources.NotFoundException e) {
	   	  e.printStackTrace();
	   	  Timber.e(e.toString());
			 throwsException = true;
	     } 
	     catch (Exception e) { 
	       	 e.printStackTrace();
			 Timber.e(e);
			 throwsException = true;
	     }

		if (throwsException) {
			Resources.NotFoundException exception = new Resources.NotFoundException();
			throw exception;
		}

			return fileContent;
	}

}
