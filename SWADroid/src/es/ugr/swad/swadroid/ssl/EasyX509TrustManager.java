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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
/**
 * TrustManager for self-signed certificates.
 * 
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class EasyX509TrustManager implements X509TrustManager 
{  
    private X509TrustManager standardTrustManager = null;  

    /** 
     * Constructor for EasyX509TrustManager. 
     */  
    public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException 
    {  
      super();  
      TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());  
      factory.init(keystore);  
      TrustManager[] trustmanagers = factory.getTrustManagers();  
      if (trustmanagers.length == 0) 
      {  
        throw new NoSuchAlgorithmException("no trust manager found");  
      }  
      this.standardTrustManager = (X509TrustManager) trustmanagers[0];  
    }  

    /** 
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String authType) 
     */  
    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException 
    {  
      standardTrustManager.checkClientTrusted(certificates, authType);  
    }  

    /** 
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String authType) 
     */  
    public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException 
    {  
    // Clean up the certificates chain and build a new one.
        // Theoretically, we shouldn't have to do this, but various web servers
        // in practice are mis-configured to have out-of-order certificates or
        // expired self-issued root certificate.
        int chainLength = certificates.length;
        if (certificates.length > 1) 
        {
          // 1. we clean the received certificates chain.
          // We start from the end-entity certificate, tracing down by matching
          // the "issuer" field and "subject" field until we can't continue.
          // This helps when the certificates are out of order or
          // some certificates are not related to the site.
          int currIndex;
          for (currIndex = 0; currIndex < certificates.length; ++currIndex) 
          {
            boolean foundNext = false;
            for (int nextIndex = currIndex + 1;
                           nextIndex < certificates.length;
                           ++nextIndex) 
            {
              if (certificates[currIndex].getIssuerDN().equals(
                            certificates[nextIndex].getSubjectDN())) 
              {
                foundNext = true;
                // Exchange certificates so that 0 through currIndex + 1 are in proper order
                if (nextIndex != currIndex + 1) 
                {
                  X509Certificate tempCertificate = certificates[nextIndex];
                  certificates[nextIndex] = certificates[currIndex + 1];
                  certificates[currIndex + 1] = tempCertificate;
                }
                break;
            }
            }
            if (!foundNext) break;
      }

          // 2. we exam if the last traced certificate is self issued and it is expired.
          // If so, we drop it and pass the rest to checkServerTrusted(), hoping we might
          // have a similar but unexpired trusted root.
          chainLength = currIndex + 1;
          X509Certificate lastCertificate = certificates[chainLength - 1];
          Date now = new Date();
          if (lastCertificate.getSubjectDN().equals(lastCertificate.getIssuerDN())
                  && now.after(lastCertificate.getNotAfter())) 
          {
            --chainLength;
          }
      } 

    standardTrustManager.checkServerTrusted(certificates, authType);    
    }  

    /** 
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers() 
     */  
    public X509Certificate[] getAcceptedIssuers() 
    {  
      return this.standardTrustManager.getAcceptedIssuers();  
    }    
}  
