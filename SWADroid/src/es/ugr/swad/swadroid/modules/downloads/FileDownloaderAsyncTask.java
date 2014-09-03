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
import es.ugr.swad.swadroid.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Download the file located at the given URL, save it to a file.
 * It also launches the notification on bar status and erases it when the download is completed or failed.
 * . Note that we are responsible for the deletion of
 * the file when it is no longer needed.
 * @throws MalformedUrlException: if a malformed URL is given as parameter.
 * @throws IOException: most probably because the connection to the server fails.
 * @throws FileNotFoundException: if the URL points to a non-existent file or to a directory - such as "www.ugr.es/"
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Víctor Terrón <vterron@iaa.es>
 */


public class FileDownloaderAsyncTask extends AsyncTask<String, Integer, Boolean> {
    private static final int BLOCK_SIZE = 1024 * 16; // 16kb

    //private TextView fileName;
    //private ProgressBar progressBar;
    private final Context mContext;
    private final DownloadNotification mNotification;
    private File download_dir;
    private URL url;
    private boolean notification = true;
    private final long fileSize;
    private String fileName = "";
    private String directoryPath = "";
    private boolean downloadSuccess = true;
    private final boolean isDownloadManagerAvailable;
    private boolean isDownloadManagerSWADroid;

    /**
     * Downloads tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Downloads";


    public FileDownloaderAsyncTask(Context context, String fileName, boolean notification, long fileSize) {
        this.fileName = fileName;
        this.mNotification = new DownloadNotification(context);
        this.notification = notification;
        this.fileSize = fileSize;
        this.mContext = context;
        this.isDownloadManagerAvailable = Utils.isDownloadManagerAvailable(mContext);
        this.isDownloadManagerSWADroid = false;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        //Log.d(TAG, "onPostExecute");
        if (isDownloadManagerSWADroid) {
            if (downloadSuccess)
                mNotification.completedDownload(this.directoryPath, this.fileName);
            else
                mNotification.eraseNotification(this.fileName);
        }
    }

    /* Return the path to the directory where files are saved */
    File getDownloadDir() {
        return this.download_dir;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        //Log.d(TAG, "onProgressUpdate");
        if (isDownloadManagerSWADroid) {
            mNotification.progressUpdate(values[0]);
        }

    }

    private void notifyFailed() {
        downloadSuccess = false;

        if (isDownloadManagerSWADroid) {
            mNotification.eraseNotification(this.fileName);
        }
    }

    /**
     * Download method for Android < Gingerbread
     *
     * @param basename  filename (without extension) of the file to be downloaded
     * @param extension extension of the file to be downloaded
     * @return true if the file has been downloaded correctly
     *         false otherwise
     */
    private boolean downloadFileCustom(String basename, String extension) {
        FileOutputStream fos = null;
        boolean result = true;
        /* The prefix must be at least three characters long */
        //		if(basename.length() < 3)
        //			basename = "tmp";

        try {
            File output = new File(download_dir, this.fileName);
            if (output.exists()) {
                int i = 1;
                do {
                    output = new File(download_dir, basename + "-" + String.valueOf(i) + extension);
                    ++i;
                } while (output.exists());
                this.fileName = basename + "-" + String.valueOf(i - 1) + extension;
            }
            mNotification.createNotification(this.fileName);

			
			/*Create the output file*/
            Log.d(TAG, "output: " + output.getPath());
			/* Convert the Bytes read to a String. */
            fos = new FileOutputStream(output);
	
			/* Open a connection to the URL and a buffered input stream */
            URLConnection ucon = url.openConnection();

            int lenghtOfFile = ucon.getContentLength();
            Log.d(TAG, "lenghtOfFile = " + String.valueOf(lenghtOfFile));

            InputStream is;
            is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
			
			/*  Read bytes to the buffer until there is nothing more to read(-1) */
            //ByteArrayBuffer baf = new ByteArrayBuffer(50);
            byte data[] = new byte[BLOCK_SIZE];
            int byteRead = 0;
            int current = 0;

            if (notification) {
                Log.d(TAG, "notification = " + notification);

                int progress = 0;
                int newValue;
                //while ((current = bis.read(data)) != -1) {
                while ((byteRead < fileSize) && (current != -1)) {
                    Log.d(TAG, "current = " + current);

                    current = bis.read(data);
                    fos.write(data, 0, current);

                    byteRead = byteRead + current;
                    newValue = Float.valueOf(((float) byteRead * 100 / (float) fileSize)).intValue();

                    Log.d(TAG, "byteRead = " + byteRead);
                    Log.d(TAG, "newValue = " + newValue);
                    if (newValue > progress) {
                        progress = newValue;
                        Log.d(TAG, "total = " + progress);

                        publishProgress(progress);
                    }
                    //if((byteRead % 10) == 0) publishProgress(byteRead);
                }
            } else {
                //while ((current = bis.read(data)) != -1) {
                while ((byteRead < fileSize) && (current != -1)) {
                    Log.d(TAG, "current = " + current);

                    current = bis.read(data);
                    fos.write(data, 0, current);

                    byteRead = byteRead + current;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            //Send exception details to Bugsense
            BugSenseHandler.sendException(e);

            notifyFailed();
            result = false;
        } finally {
			/*Close the output file*/
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "no se puede cerrar el fichero de salida");
                e.printStackTrace();

                //Send exception details to Bugsense
                BugSenseHandler.sendException(e);

                notifyFailed();
                result = false;
            }
        }

        return result;
    }

    @Override
    /**
     * params[0] - path where to locate the downloaded file
     * params[1] - url of file to download
     * */
    protected Boolean doInBackground(String... params) {
        //Log.d(TAG, "doInBackground");
        download_dir = new File(params[0]);
        this.directoryPath = params[0];
        if (!download_dir.exists()) {
            Log.e(TAG, "Download folder" + this.directoryPath + " not found");
            downloadSuccess = false;
            return false;
        }

        try {
            url = new URL(params[1]);
        } catch (MalformedURLException e) {
            e.printStackTrace();

            //Send exception details to Bugsense
            BugSenseHandler.sendException(e);
//			Log.i(TAG, "Incorrect URL");

            downloadSuccess = false;
            return false;
        }
		
		/* The downloaded file will be saved to a temporary file, whose prefix
		 * will be the basename of the file and the suffix its extension */
        //if(FileDownloader.getFilenameFromURL(url.getPath() == null)
        //	throw new FileNotFoundException("URL does not point to a file");

        int lastSlashIndex = this.fileName.lastIndexOf("/");
        int lastDotIndex = this.fileName.lastIndexOf(".");
		
		/* Avoid StringIndexOutOfBoundsException from being thrown if the
		 * file has no extension (such as "http://www.domain.com/README" */
        String basename;
        String extension = "";

        if (lastDotIndex == -1)
            basename = this.fileName.substring(lastSlashIndex + 1);
        else {
            basename = this.fileName.substring(lastSlashIndex + 1, lastDotIndex);
            extension = this.fileName.substring(lastDotIndex);
        }

        Log.d(TAG, "isDownloadManagerAvailable=" + isDownloadManagerAvailable);
        if (isDownloadManagerAvailable) {
            String titleNotification = mContext.getString(R.string.app_name) + " " + mContext.getString(R.string.notificationDownloadTitle); //Initial text that appears in the status bar
            String descriptionNotification = fileName;
            downloadSuccess = Utils.downloadFileGingerbread(mContext, url.toString(), fileName, titleNotification, descriptionNotification);
        }

        if (!isDownloadManagerAvailable || !downloadSuccess) {
            Log.i(TAG, "Downloading file " + fileName + " with DownloadManager SWADroid");
            isDownloadManagerSWADroid = true;
            downloadSuccess = downloadFileCustom(basename, extension);
        }

//		Log.d(TAG, "Terminado");

        return downloadSuccess;
    }

}
