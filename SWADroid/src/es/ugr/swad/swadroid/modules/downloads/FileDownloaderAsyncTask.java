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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import es.ugr.swad.swadroid.Global;

public class FileDownloaderAsyncTask extends AsyncTask<String,Integer,Boolean> {

	//private TextView fileName;
	//private ProgressBar progressBar;
	private DownloadNotification mNotification;
	private File download_dir;
	private URL url;
	boolean notification = true;
	private long fileSize;
	private String fileName = "";
	private String directoryPath = "";
	
	/**
	 * Downloads tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Downloads";

	/*public FileDownloaderAsyncTask(TextView text, ProgressBar progressBar){
		this.fileName = text;
		this.progressBar = progressBar;
	}*/
	public FileDownloaderAsyncTask(Context context,String fileName, boolean notification, long fileSize){
		this.fileName = fileName;
		mNotification = new DownloadNotification(context);
		this.notification = notification;
		this.fileSize = fileSize;
	}
	
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		Log.i(TAG, "onPostExecute");
		mNotification.completedDownload(this.directoryPath,this.fileName);
	}

	/* Return the path to the directory where files are saved */
	public File getDownloadDir(){
		return this.download_dir;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		

/*		fileName.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setProgress(0);
		progressBar.setMax(100);*/
	}



	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		mNotification.progressUpdate(values[0]);

	}



	@Override
	/**
	 * params[0] - path where to locate the downloaded file
	 * params[1] - url of file to download
	 * */
	protected Boolean doInBackground(String... params) {
		
		
		download_dir = new File(params[0]);
		this.directoryPath = params[0];
		if(!download_dir.exists()) 
			return false;
		try {
			url = new URL(params[1]);
		} catch (MalformedURLException e) {
			// TODO ¿cambiar barra de progreso por mensaje de error?
			e.printStackTrace();
			Log.i(TAG, "Incorrect URL");
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
		String basename = "";
		String extension = "";
		
		if(lastDotIndex == -1)
			basename  = this.fileName.substring(lastSlashIndex + 1);
		else {
			basename  = this.fileName.substring(lastSlashIndex + 1, lastDotIndex);
			extension = this.fileName.substring(lastDotIndex);
		}
		
		/* The prefix must be at least three characters long */
//		if(basename.length() < 3)
//			basename = "tmp";
	
		File output = new File(this.getDownloadDir(),this.fileName) ;
		if(output.exists()){
			int i = 1;
			do{
				output = new File(this.getDownloadDir(),basename+"-"+String.valueOf(i)+extension);
				++i;
			}while(output.exists());
			this.fileName = basename+"-"+String.valueOf(i-1)+extension;
		}	
		mNotification.createNotification(this.fileName);
		
		
		/*Create the output file*/
		Log.i(TAG, "output: " + output.getPath());
		/* Convert the Bytes read to a String. */
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(output);
		} catch (FileNotFoundException e) {
			Log.i(TAG, "no se puede crear fichero de salida");
			e.printStackTrace();
			return false;
		}

		/* Open a connection to the URL and a buffered input stream */
		URLConnection ucon;
		try {
			ucon = url.openConnection();
		} catch (IOException e) {
			// TODO TODO ¿cambiar barra de progreso por mensaje de error?
			Log.i(TAG, "Error conection");mNotification.eraseNotification(this.fileName);
			e.printStackTrace();
			return false;
		}
		
		int lenghtOfFile = ucon.getContentLength();
		Log.i(TAG, "lenghtOfFile = "+String.valueOf(lenghtOfFile));
		
		InputStream is;
		try {
			is = ucon.getInputStream();
		} catch (IOException e) {
			// TODO TODO ¿cambiar barra de progreso por mensaje de error?
			Log.i(TAG, "Error conection");mNotification.eraseNotification(this.fileName);
			e.printStackTrace();
			return false;
		}
		BufferedInputStream bis = new BufferedInputStream(is);
		
		/*  Read bytes to the buffer until there is nothing more to read(-1) */
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int current = 0;
		int total = 100;
		try {
			total = bis.available();
		} catch (IOException e1) {
			Log.i(TAG, "Error lectura bytes");mNotification.eraseNotification(this.fileName);
			e1.printStackTrace();
		};
		
		
		if(notification){
		
			int byteRead = 0;
			int progress = 0;
			try {
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
					
					if(baf.isFull()){ //before the buffer is full, it writes it in the file and clears the buffer
						try {
							fos.write(baf.toByteArray());
							baf.clear();
						} catch (IOException e) {
							Log.i(TAG, "no se puede escribir fichero de salida");
							mNotification.eraseNotification(this.fileName);
							e.printStackTrace();
							return false;
						}
					}
					++byteRead;
					int newValue = Float.valueOf(((float) byteRead*100/ (float) fileSize)).intValue();
					if(newValue > progress){
						progress = newValue;
						Log.i(TAG, "total = "+progress);
						publishProgress(progress);
					}
					//if((byteRead % 10) == 0) publishProgress(byteRead);
				}
			} catch (IOException e) {
				try {
					fos.close();
				} catch (IOException e1) {
					Log.i(TAG, "no se puede cerrar fichero de salida");
					e1.printStackTrace();
					return false;
				}
				Log.i(TAG, "Error conection");mNotification.eraseNotification(this.fileName);
				e.printStackTrace();
				return false;
			}
		}else{

			try {
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
					if(baf.isFull()){ //before the buffer is full, it writes it in the file and clears the buffer
						try {
							fos.write(baf.toByteArray());
							baf.clear();
						} catch (IOException e) {
							Log.i(TAG, "no se puede escribir fichero de salida");
							mNotification.eraseNotification(this.fileName);
							e.printStackTrace();
							try {
								fos.close();
							} catch (IOException e1) {
								Log.i(TAG, "no se puede cerrar fichero de salida");
								e1.printStackTrace();
								return false;
							}
							return false;
						}
					}
				}
			} catch (IOException e) {
				try {
					fos.close();
				} catch (IOException e1) {
					Log.i(TAG, "no se puede cerrar fichero de salida");
					e1.printStackTrace();
					return false;
				}
				mNotification.eraseNotification(this.fileName);
				Log.i(TAG, "Error conection");
				e.printStackTrace();
				return false;
			}
			
		}
		
		/*Close the output file*/
		try {
			fos.close();
		} catch (IOException e) {
			Log.i(TAG, "no se puede cerrar fichero de salida");
			e.printStackTrace();
			return false;
		}
		
/*		try {
			output = File.cre //File.createTempFile("swad48x48", ".gif",this.getDownloadDir());
			//output = File.createTempFile(basename, extension, this.getDownloadDir());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "directory path no existe");
			e.printStackTrace();
			return false;
		}*/

		
		Log.i(TAG, "Terminado");
		
		return true;
	}

}
