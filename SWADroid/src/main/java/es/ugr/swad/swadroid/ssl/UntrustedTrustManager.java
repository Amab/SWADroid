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

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * TrustManager that accepts untrusted certificates.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class UntrustedTrustManager implements X509TrustManager {
    /**
     * Given the partial or complete certificate chain provided by the peer,
     * build a certificate path to a trusted root and return if it can be
     * validated and is trusted for client SSL authentication based on the
     * authentication type.
     *
     * @param chain    Certificate.
     * @param authType Authentication type.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    /**
     * Given the partial or complete certificate chain provided by the peer,
     * build a certificate path to a trusted root and return if it can be
     * validated and is trusted for server SSL authentication based on the
     * authentication type.
     *
     * @param chain    Certificate.
     * @param authType Authentication type.
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    /**
     * Return an array of certificate authority certificates which are trusted
     * for authenticating peers.
     *
     * @return Array of certificate authority certificates.
     */
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
