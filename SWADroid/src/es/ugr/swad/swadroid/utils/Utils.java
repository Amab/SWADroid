/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.swad.swadroid.utils;

import java.util.List;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import es.ugr.swad.swadroid.Constants;

public class Utils {
	public static final String TAG = Constants.APP_TAG + " Utils";

	/**
	 * Generates a random string of length len
	 * @param len Length of random string
	 * @return A random string of length len
	 */
	public static String randomString(int len) 
	{
	   StringBuilder sb = new StringBuilder(len);
	   for(int i = 0; i < len; i++) 
	      sb.append(Constants.AB.charAt(Constants.rnd.nextInt(Constants.AB.length())));
	   return sb.toString();
	}

	/**
	 * Indicates if the db was cleaned
	 * @param newState - true when the database was cleaned and it was not handled it
	 * 				   - false if the database does not change
	 * */
	public static boolean isDbCleaned(){
		return Constants.dbCleaned;
	}

	/**
	 * Set the fact that the db was cleaned
	 * @param newState - true when the database was cleaned
	 * 				   - false after the fact is noticed and handled it
	 * */
	public static void setDbCleaned(boolean state){
		Constants.dbCleaned = state;
	}

	/**
	 * Checks if any connection is available 
	 * @param ctx Application context
	 * @return true if there is a connection available, false in other case
	 */
	public static boolean connectionAvailable(Context ctx){
	    boolean connAvailable = false;
	    ConnectivityManager connec =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	
	    //Survey all networks (wifi, gprs...)
	    NetworkInfo[] networks = connec.getAllNetworkInfo();
	    
	    for(int i=0; i<networks.length; i++){
	        //If any of them has a connection available, put boolean to true
	        if (networks[i].isConnected()){
	            connAvailable = true;
	        }
	    }
	    
	    //If boolean remains false there is no connection available        
	    return connAvailable;
	}

	/**
	 * Function to parse from Integer to Boolean
	 * @param n Integer to be parsed
	 * @return true if n!=0, false in other case
	 */
	public static boolean parseIntBool(int n) {
		return n!=0;
	}

	/**
	 * Function to parse from String to Boolean
	 * @param s String to be parsed
	 * @return true if s equals "Y", false in other case
	 */
	public static boolean parseStringBool(String s) {
		return s.equals("Y") ? true : false;
	}

	/**
	 * Function to parse from Boolean to Integer
	 * @param b Boolean to be parsed
	 * @return 1 if b==true, 0 in other case
	 */
	public static int parseBoolInt(boolean b) {
		return b ? 1 : 0;
	}

	/**
	 * Function to parse from Boolean to String
	 * @param b Boolean to be parsed
	 * @return "Y" if b==true, "N" in other case
	 */
	public static String parseBoolString(boolean b) {
		return b ? "Y" : "N";
	}
	
	/**
	 * @param context used to check the device version and DownloadManager information
	 * @return true if the download manager is available
	 */
	public static boolean isDownloadManagerAvailable(Context context) {
	    try {
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            return false;
	        }
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
	        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
	                PackageManager.MATCH_DEFAULT_ONLY);
	        return list.size() > 0;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static boolean isHTTPUrl(String url) {
		return url.startsWith("http://");
	}
	
	/**
	 * Download method for Android >= Gingerbread
	 * 
	 * @param url URL of the file to be downloaded
	 * @param fileName filename of the file to be downloaded
	 * @param title title of the download notification
	 * @param description description of the download notification
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static boolean downloadFileGingerbread(Context context, String url, String fileName, String title, String description) {
		DownloadManager.Request request;
		DownloadManager manager;
		
		Log.d(TAG, "URL received: " + url);
		
		// in order for this if to run, you must use the android 3.2 to compile your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Log.d(TAG, "Downloading file " + fileName + " with DownloadManager >= HONEYCOMB");
			
			request = new DownloadManager.Request(Uri.parse(url));
			request.setDescription(title);
			request.setTitle(description);
			
		    request.allowScanningByMediaScanner();
		    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

			// get download service and enqueue file
			manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(request);		
		} else if(isHTTPUrl(url)){
			Log.d(TAG, "Downloading file " + fileName + " with DownloadManager GINGERBREAD");
			
			request = new DownloadManager.Request(Uri.parse(url));
			request.setDescription(title);
			request.setTitle(description);
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

			// get download service and enqueue file
			manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(request);
		} else {
			Log.e(TAG, "Can only download HTTP URIs with DownloadManager GINGERBREAD");
			return false;
		}
		
		return true;
	}

}
