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

import java.io.File;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
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
		
	    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
	    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE); // get download service and 
	    
	    //Create dastination directory if not exists
	    File downloadDirectory = new File(Constants.DOWNLOADS_PATH);
	    if (!downloadDirectory.exists()){
	    	downloadDirectory.mkdir();
	        
	        Log.i(TAG, "Created directory " + Constants.DOWNLOADS_PATH);
	    }
        
        request.setDescription(title);
        request.setTitle(description);
        request.setDestinationInExternalPublicDir(Constants.DIRECTORY_SWADROID, fileName);
        
	    // in order for this if to run, you must use the android 3.2 to compile your app
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        Log.i(TAG, "Downloading file " + fileName + " with DownloadManager >= HONEYCOMB");
	
	        request.allowScanningByMediaScanner();
	        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
	
	        // enqueue file
	        manager.enqueue(request);
	    } else if (Utils.isHTTPUrl(url)) {
	        Log.i(TAG, "Downloading file " + fileName + " with DownloadManager GINGERBREAD");
	
	        // enqueue file
	        manager.enqueue(request);
	    } else {
	        Log.e(TAG, "Can only download HTTP URIs with DownloadManager GINGERBREAD");
	        return false;
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
