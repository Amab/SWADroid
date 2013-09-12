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

import com.bugsense.trace.BugSenseHandler;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

/**
 * Cryptographic class for encryption purposes.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Crypto {

    private Cipher ecipher;
    private Cipher dcipher;

    // 8-byte Salt
    private final byte[] salt = {1, 2, 4, 5, 7, 8, 3, 6};

    // Iteration count
    private final int iterationCount = 1979;

    public Crypto(String passPhrase) {
        try {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt,
                    iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(
                    "PBEWITHSHA256AND128BITAES-CBC-BC").generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
                    iterationCount);

            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (Exception e) {
            e.printStackTrace();

            //Send exception details to Bugsense
            BugSenseHandler.sendException(e);
        }
    }

    public String encrypt(String str) {
        String rVal;
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            rVal = toHex(enc);
        } catch (Exception e) {
            rVal = "Error encrypting: " + e.getMessage();
            e.printStackTrace();

            //Send exception details to Bugsense
            BugSenseHandler.sendException(e);
        }
        return rVal;
    }

    public String decrypt(String str) {
        String rVal;
        try {
            // Decode base64 to get bytes
            byte[] dec = toByte(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            rVal = new String(utf8, "UTF8");
        } catch (Exception e) {
            rVal = "Error encrypting: " + e.getMessage();
            e.printStackTrace();

            //Send exception details to Bugsense
            BugSenseHandler.sendException(e);
        }
        return rVal;
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
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
        p = Base64.encodeBytes(md.digest());
        p = p.replace('+', '-').replace('/', '_').replace('=', ' ').replaceAll("\\s+", "").trim();

        return p;
    }
}
