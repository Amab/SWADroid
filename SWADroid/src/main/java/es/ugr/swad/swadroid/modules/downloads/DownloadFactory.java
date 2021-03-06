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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Locale;

import es.ugr.swad.swadroid.Constants;

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
	    DownloadManager manager;
		DownloadManager.Request request;

		manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		request = new DownloadManager.Request(uri);

		request.setDescription(title);
		request.setTitle(description);
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Constants.DIRECTORY_SWADROID + File.separator + fileName);

		Log.i(TAG, "Downloading file " + fileName);

		request.allowScanningByMediaScanner();
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		manager.enqueue(request);
	
	    return true;
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
