/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Download the file located at the given URL, save it to a file.
 * It also launches the notification on bar status and erases it when the download is completed or failed.
 * Note that we are responsible for the deletion of the file when it is no longer needed.
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Victor Terron <vterron@iaa.es>
 */


public class FileDownloaderAsyncTask extends AsyncTask<String, Integer, Boolean> {
    private final Context mContext;
    private URL url;
    private String fileName = "";
    private boolean downloadSuccess = true;

    /**
     * Downloads tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Downloads";


    public FileDownloaderAsyncTask(Context context, String fileName, long fileSize) {
        this.fileName = fileName;
        this.mContext = context;
    }

    @Override
    /**
     * params[0] - path where to locate the downloaded file
     * params[1] - url of file to download
     * */
    protected Boolean doInBackground(String... params) {
        try {
            url = new URL(params[1]);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Incorrect URL", e);

            //Send exception details to Bugsense
            BugSenseHandler.sendException(e);

            downloadSuccess = false;
            return false;
        }

        String titleNotification = mContext.getString(R.string.notificationDownloadTitle); //Initial text that appears in the status bar
        String descriptionNotification = fileName;
        downloadSuccess = DownloadFactory.downloadFile(mContext, url.toString(), fileName, titleNotification, descriptionNotification);

        return downloadSuccess;
    }
}
