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
package es.ugr.swad.swadroid.modules.downloads;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Locale;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Class for manage file downloads
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class DownloadFactory {
    /**
     * Tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " DownloadFactory";
    
	/**
	 * Download method for Android >= Gingerbread
	 *
	 * @param url         URL of the file to be downloaded
	 * @param fileName    filename of the file to be downloaded
	 * @param title       title of the download notification
	 * @param description description of the download notification
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static boolean downloadFile(Context context, String url, String fileName, String title,
			String description) {
		
		Uri uri = Uri.parse(url);	 
	    DownloadManager managerHoneycomb;
		DownloadManager.Request requestHoneycomb;
	    es.ugr.swad.swadroid.modules.downloads.DownloadManager managerGingerbread;
	    es.ugr.swad.swadroid.modules.downloads.DownloadManager.Request requestGingerbread;
	    
	    //Create destination directory if not exists
	    File downloadDirectory = new File(Constants.DOWNLOADS_PATH);
	    if (!downloadDirectory.exists()){
	    	if(downloadDirectory.mkdir()) {
	            Log.i(TAG, "Created directory " + Constants.DOWNLOADS_PATH);
            } else {
                Log.e(TAG, "Error creating directory " + Constants.DOWNLOADS_PATH);
                Toast.makeText(context, "Error creating directory " + Constants.DOWNLOADS_PATH, Toast.LENGTH_LONG).show();
                return false;
            }
        }
	    
	    if((Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) && !Utils.isHTTPUrl(url)) {
	    	//DownloadManager GINGERBREAD (HTTPS support)
	        Log.i(TAG, "Downloading file " + fileName + " with custom DownloadManager GINGERBREAD (HTTPS support)");
	        
		    managerGingerbread = new es.ugr.swad.swadroid.modules.downloads.DownloadManager(context.getContentResolver(), "es.ugr.swad.swadroid.modules.downloads");
		    requestGingerbread = new es.ugr.swad.swadroid.modules.downloads.DownloadManager.Request(uri);

	        requestGingerbread.setDescription(title);
	        requestGingerbread.setTitle(description);
	        requestGingerbread.setDestinationInExternalPublicDir(Constants.DIRECTORY_SWADROID, fileName);
	        
	    	managerGingerbread.enqueue(requestGingerbread);
	    } else {	        
		    managerHoneycomb = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		    requestHoneycomb = new DownloadManager.Request(uri);
	        
	        requestHoneycomb.setDescription(title);
	        requestHoneycomb.setTitle(description);
	        requestHoneycomb.setDestinationInExternalPublicDir(Constants.DIRECTORY_SWADROID, fileName);

	    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    		//DownloadManager HONEYCOMB
		        Log.i(TAG, "Downloading file " + fileName + " with DownloadManager >= HONEYCOMB");
		        
		        requestHoneycomb.allowScanningByMediaScanner();
		        requestHoneycomb.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);	    		
	    	} else {
		    	//DownloadManager GINGERBREAD (HTTP) 
		        Log.i(TAG, "Downloading file " + fileName + " with DownloadManager GINGERBREAD (HTTP)");
	    	}
	    	
	    	managerHoneycomb.enqueue(requestHoneycomb);
	    }
	
	    return true;
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

	/**
	 * Method to show file size in bytes in a human readable way
	 * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
	    return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
