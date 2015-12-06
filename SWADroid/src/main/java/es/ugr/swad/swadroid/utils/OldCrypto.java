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

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;

/**
 * Cryptographic class for encryption purposes.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class OldCrypto {
    /**
     * OldCrypto tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " OldCrypto";

    public static String encrypt(Context ctx, String seed, String cleartext) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes("UTF-8"));
            byte[] result = encrypt(rawKey, cleartext.getBytes("UTF-8"));
            return Base64.encodeBytes(result);
        } catch (Exception e) {
            //Send exception details to Google Analytics
            SWADroidTracker.sendException(ctx, e, false);
        }
        return "error";
    }

    public static String decrypt(Context ctx, String seed, String encrypted) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes("UTF-8"));
            byte[] enc = Base64.decode(encrypted);
            byte[] result = decrypt(rawKey, enc);
            return new String(result, "UTF-8");
        } catch (Exception e) {
            //Send exception details to Google Analytics
            SWADroidTracker.sendException(ctx, e, false);
        }
        return "error";
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    public static String md5(Context ctx, final String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes("UTF-8"));
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception e) {
            //Send exception details to Google Analytics
            SWADroidTracker.sendException(ctx, e, false);
        }
        return "error";
    }
}