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

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.TypedValue;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.SWADNotification;

/**
 * Utilities class.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class Utils {
	/**
	 * Random generator
	 */
	private static final Random rnd = new Random();
	/**
	 * Base string to generate random alphanumeric strings
	 */
	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private Utils() {}
	
    /**
     * Generates a random string of length len
     *
     * @param len Length of random string
     * @return A random string of length len
     */
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    /**
     * Checks if any connection is available
     *
     * @param ctx Application context
     * @return true if there is a connection available, false in other case
     */
    public static boolean connectionAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null
                && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
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
    public static String getSeenNotificationCodes(List<SWADNotification> markedNotificationsList) {
    	StringBuilder seenNotifCodes = new StringBuilder();
    	Model m;

    	for(Iterator<SWADNotification> it = markedNotificationsList.iterator(); it.hasNext();) {
    		m = it.next();
    		seenNotifCodes.append(m.getId());

    		if(it.hasNext()) {
    			seenNotifCodes.append(",");
    		}
    	}

    	return seenNotifCodes.toString();
    }
    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static float getPixelsFromSp(float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                Resources.getSystem().getDisplayMetrics()
        );
    }

}
