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

package es.ugr.swad.swadroid.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Establishes a secure connection.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class SecureConnection {
    /**
     * HostnameVerifier that accepts untrusted certificates.
     */
    private static UntrustedHostnameVerifier untrustedHN =
            new UntrustedHostnameVerifier();
    /**
     * TrustManager that accepts untrusted certificates.
     */
    private static TrustManager[] untrustedTM =
            new TrustManager[]{new UntrustedTrustManager()};

    /**
     * Bypasses certificate verification in application.
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void initSecureConnection()
            throws NoSuchAlgorithmException, KeyManagementException {
        
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, untrustedTM, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(untrustedHN);
    }
}
