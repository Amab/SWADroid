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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import es.ugr.swad.swadroid.Constants;

public class Utils {

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

}
