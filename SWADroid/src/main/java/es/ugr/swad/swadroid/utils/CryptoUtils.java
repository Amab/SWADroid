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

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import es.ugr.swad.swadroid.Constants;

/**
 * Cryptographic class for encryption purposes.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class CryptoUtils {
    /**
     * Crypto tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " Crypto";

    @Deprecated
    private static final String HEX = "0123456789ABCDEF";

    @Deprecated
    private Cipher eCipher;
    @Deprecated
    private Cipher dCipher;

    @Deprecated
    public CryptoUtils(String passPhrase) {
        try {
            // 8-byte Salt
            byte[] salt = {1, 2, 4, 5, 7, 8, 3, 6};
            // Iteration count
            int iterationCount = 1979;
            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt,
                    iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(
                    "PBEWITHSHA256AND128BITAES-CBC-BC").generateSecret(keySpec);
            eCipher = Cipher.getInstance(key.getAlgorithm());
            dCipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
                    iterationCount);

            // Create the ciphers
            eCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Deprecated
    public String encrypt(String str) {
        String rVal;
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);

            // Encrypt
            byte[] enc = eCipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            rVal = toHex(enc);
        } catch (Exception e) {
            rVal = "Error encrypting: " + e.getMessage();

            Log.e(TAG, e.getMessage(), e);
        }
        return rVal;
    }

    @Deprecated
    public String decrypt(String str) {
        String rVal;
        try {
            // Decode base64 to get bytes
            byte[] dec = toByte(str);

            // Decrypt
            byte[] utf8 = dCipher.doFinal(dec);

            // Decode using utf-8
            rVal = new String(utf8, StandardCharsets.UTF_8);
        } catch (Exception e) {
            rVal = "Error encrypting: " + e.getMessage();

            Log.e(TAG, e.getMessage(), e);
        }
        return rVal;
    }

    @Deprecated
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    @Deprecated
    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuilder result = new StringBuilder(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    @Deprecated
    private static void appendHex(StringBuilder sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    /**
     * Encrypts user password with SHA-512 and encodes it to Base64UrlSafe
     *
     * @param password Password to be encrypted
     * @return Encrypted password
     * @throws NoSuchAlgorithmException
     */
    public static String encryptPassword(String password) throws NoSuchAlgorithmException {
        String p;
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());
        p = Base64Utils.encodeBytes(md.digest());
        p = p.replace('+', '-').replace('/', '_').replace('=', ' ').replaceAll("\\s+", "").trim();

        return p;
    }
}
