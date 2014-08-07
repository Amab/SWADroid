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
import es.ugr.swad.swadroid.model.Model;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities class.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class Utils {
    private static final String TAG = Constants.APP_TAG + " Utils";

    /**
     * Generates a random string of length len
     *
     * @param len Length of random string
     * @return A random string of length len
     */
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(Constants.AB.charAt(Constants.rnd.nextInt(Constants.AB.length())));
        return sb.toString();
    }

    /**
     * Indicates if the db was cleaned
     */
    public static boolean isDbCleaned() {
        return Constants.dbCleaned;
    }

    /**
     * Set the fact that the db was cleaned
     */
    public static void setDbCleaned(boolean state) {
        Constants.dbCleaned = state;
    }

    /**
     * Checks if any connection is available
     *
     * @param ctx Application context
     * @return true if there is a connection available, false in other case
     */
    public static boolean connectionAvailable(Context ctx) {
        boolean connAvailable = false;
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Survey all networks (wifi, gprs...)
        NetworkInfo[] networks = connec.getAllNetworkInfo();

        for (NetworkInfo network : networks) {
            //If any of them has a connection available, put boolean to true
            if (network.isConnected()) {
                connAvailable = true;
            }
        }

        //If boolean remains false there is no connection available
        return connAvailable;
    }

    /**
     * Function to parse from Integer to Boolean
     *
     * @param n Integer to be parsed
     * @return true if n!=0, false in other case
     */
    public static boolean parseIntBool(int n) {
        return n != 0;
    }

    /**
     * Function to parse from String to Boolean
     *
     * @param s String to be parsed
     * @return true if s equals "Y", false in other case
     */
    public static boolean parseStringBool(String s) {
        return s.equals("Y");
    }

    /**
     * Function to parse from Boolean to Integer
     *
     * @param b Boolean to be parsed
     * @return 1 if b==true, 0 in other case
     */
    public static int parseBoolInt(boolean b) {
        return b ? 1 : 0;
    }

    /**
     * Function to parse from Boolean to String
     *
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

    private static boolean isHTTPUrl(String url) {
        return url.startsWith("http://");
    }

    /**
     * Download method for Android >= Gingerbread
     *
     * @param url         URL of the file to be downloaded
     * @param fileName    filename of the file to be downloaded
     * @param title       title of the download notification
     * @param description description of the download notification
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean downloadFileGingerbread(Context context, String url, String fileName, String title, String description) {
        DownloadManager.Request request;
        DownloadManager manager;

        Log.d(TAG, "URL received: " + url);

        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.i(TAG, "Downloading file " + fileName + " with DownloadManager >= HONEYCOMB");

            request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(title);
            request.setTitle(description);

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Constants.DOWNLOADS_PATH, fileName);

            // get download service and enqueue file
            manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } else if (isHTTPUrl(url)) {
            Log.i(TAG, "Downloading file " + fileName + " with DownloadManager GINGERBREAD");

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

	public static boolean isInteger(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException nfe) {
	        nfe.printStackTrace();
	    }
	    return false;
	}

	public static boolean isLong(String str) {
	    try {
	    	Long.parseLong(str);
	        return true;
	    } catch (NumberFormatException nfe) {
	    	// Do nothing
	    }
	    return false;
	}

	public static boolean isValidDni(String dni) {
	    String dniPattern = "^[A-Z]?\\d{1,16}[A-Z]?$";    // (0 or 1 letter) + (1 to 16 digits) + (0 or 1 letter)
	
	    Pattern pattern = Pattern.compile(dniPattern, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(dni);
	
	    return matcher.matches();
	    /*if (matcher.matches())
	        return checkDniLetter(dni);
		return false;*/
	}

	public static boolean checkDniLetter(String n) {
	    String number = n.substring(0, n.length() - 1);
	    String letter = n.substring(n.length() - 1, n.length());
	
	    int code = (Integer.valueOf(number)) % 23;
	    String[] abc = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E", "T"};
	
	    return abc[code].compareToIgnoreCase(letter) == 0;
	}

	public static boolean isValidNickname(String nickname) {
	    String patronNickname = "@[a-zA-Z_0-9]{1,17}";    // 1 to 17 letters, underscored or digits
	
	    Pattern pattern = Pattern.compile(patronNickname, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(nickname);
	
	    return matcher.matches();
	}

    public static String fixLinks(String body) {
        String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        body = body.replaceAll(regex, "<a href=\"$0\">$0</a>");
        return body;
    }
    
    /**
     * Creates a string of notification codes separated by commas from a list of notifications
     */
    public static String getSeenNotificationCodes(List<Model> markedNotificationsList) {
    	String seenNotifCodes = "";
    	Model m;
    	
    	for(Iterator<Model> it = markedNotificationsList.iterator(); it.hasNext();) {
    		m = it.next();
    		seenNotifCodes += m.getId();
    		
    		if(it.hasNext()) {
    			seenNotifCodes += ",";
    		}
    	}
    	
    	return seenNotifCodes;
    }

	/**
     * Generates the stars sequence to be showed on password field
     *
     * @param size Length of the stars sequence
     * @return Stars as a string concatenation of the specified length
     */
    public static String getStarsSequence(int size) {
        String stars = "";

        for (int i = 0; i < size; i++) {
            stars += "*";
        }

        return stars;
    }
    
    public static String unAccent(String s) {
        //
        // JDK1.5
        //   use sun.text.Normalizer.normalize(s, Normalizer.DECOMP, 0);
        //
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
