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

import android.content.Context;
import android.os.Build;
import android.util.Log;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Establishes a secure connection.
 * 
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class SecureConnection {
	/**
	 * Class tag name for Logcat
	 */
	private static final String TAG = Constants.APP_TAG + " SecureConnection";
	/**
	 * HostnameVerifier that accepts untrusted certificates.
	 */
	//private static final UntrustedHostnameVerifier untrustedHN = new UntrustedHostnameVerifier();
	/**
	 * TrustManager that accepts untrusted certificates.
	 */
	//private static final TrustManager[] untrustedTM = new TrustManager[] { new UntrustedTrustManager() };

	/**
	 * Terena CA certificate
	 */
	private static final String TERENACER = "-----BEGIN CERTIFICATE-----\n"
			+ "MIIEmDCCA4CgAwIBAgIQS8gUAy8H+mqk8Nop32F5ujANBgkqhkiG9w0BAQUFADCBlzELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAlVUMRcwFQYDVQQHEw5TYWx0IExha2UgQ2l0eTEeMBwGA1UEChMVVGhlIFVTRVJUUlVTVCBOZXR3b3JrMSEwHwYDVQQLExhodHRwOi8vd3d3LnVzZXJ0cnVzdC5jb20xHzAdBgNVBAMTFlVUTi1VU0VSRmlyc3QtSGFyZHdhcmUwHhcNMDkwNTE4MDAwMDAwWhcNMjAwNTMwMTA0ODM4WjA2MQswCQYDVQQGEwJOTDEPMA0GA1UEChMGVEVSRU5BMRYwFAYDVQQDEw1URVJFTkEgU1NMIENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw+NIxC9cwcupmf0booNdij2tOtDipEMfTQ7+NSUwpWkbxOjlwY9UfuFqoppcXN49/ALOlrhfj4NbzGBAkPjktjolnF8UUeyx56+eUKExVccCvaxSin81joL6hK0V/qJ/gxA6VVOULAEWdJRUYyij8lspPZSIgCDiFFkhGbSkmOFg5vLrooCDQ+CtaPN5GYtoQ1E/iptBhQw1jF218bblp8ODtWsjb9Sl61DllPFKX+4nSxQSFSRMDc9ijbcAIa06Mg9YC18em9HfnY6pGTVQL0GprTvG4EWyUzl/Ib8iGodcNK5Sbwd9ogtOnyt5pn0T3fV/g3wvWl13eHiRoBS/fQIDAQABo4IBPjCCATowHwYDVR0jBBgwFoAUoXJfJhsomEOVXQc31YWWnUvSw0UwHQYDVR0OBBYEFAy9k2gM896ro0lrKzdXR+qQ47ntMA4GA1UdDwEB/wQEAwIBBjASBgNVHRMBAf8ECDAGAQH/AgEAMBgGA1UdIAQRMA8wDQYLKwYBBAGyMQECAh0wRAYDVR0fBD0wOzA5oDegNYYzaHR0cDovL2NybC51c2VydHJ1c3QuY29tL1VUTi1VU0VSRmlyc3QtSGFyZHdhcmUuY3JsMHQGCCsGAQUFBwEBBGgwZjA9BggrBgEFBQcwAoYxaHR0cDovL2NydC51c2VydHJ1c3QuY29tL1VUTkFkZFRydXN0U2VydmVyX0NBLmNydDAlBggrBgEFBQcwAYYZaHR0cDovL29jc3AudXNlcnRydXN0LmNvbTANBgkqhkiG9w0BAQUFAAOCAQEATiPuSJz2hYtxxApuc5NywDqOgIrZs8qy1AGcKM/yXA4hRJMLthoh45gBlA5nSYEevj0NTmDa76AxTpXv8916WoIgQ7ahY0OzUGlDYktWYrA0irkTQ1mT7BR5iPNIk+idyfqHcgxrVqDDFY1opYcfcS3mWm08aXFABFXcoEOUIEU4eNe9itg5xt8Jt1qaqQO4KBB4zb8BG1oRPjj02Bs0ec8z0gH9rJjNbUcRkEy7uVvYcOfVr7bMxIbmdcCeKbYrDyqlaQIN4+mitF3A884saoU4dmHGSYKrUbOCprlBmCiY+2v+ihb/MX5UR6g83EMmqZsFt57ANEORMNQywxFa4Q=="
			+ "\n-----END CERTIFICATE-----";

	/**
	 * Read x509 certificated file from byte[].
	 * 
	 * @param bytes
	 *            certificate in der format
	 * @return certificate
	 */
	private static X509Certificate getCertificate(final byte[] bytes)
			throws IOException, CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate ca;
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		try {
			ca = (X509Certificate) cf.generateCertificate(is);
			Log.d(TAG, "ca=" + ca.getSubjectDN());
		} finally {
			is.close();
		}
		return ca;
	}

	/**
	 * Trust Terena's CA. Adds Terena's CA to default certificates list
	 * 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws CertificateException
	 */
	public void trustTerena() throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException {
		
		String keyStoreType = KeyStore.getDefaultType();
		final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		// Terena CA certificate
		X509Certificate terenaCert = getCertificate(TERENACER.getBytes("UTF-8"));
		
		keyStore.load(null, null);
		
		// load Terena CA certificate
		keyStore.setCertificateEntry("Terena", terenaCert);

		// build our own trust manager
		X509TrustManager tm = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				// nothing to do
				return new X509Certificate[0];
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] chain,
					final String authType) throws CertificateException {
				// nothing to do
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain,
					final String authType) throws CertificateException {
				// nothing to do
				Log.d(TAG, "checkServerTrusted(" + chain + ")");
				X509Certificate cert = chain[0];

				cert.checkValidity();

				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
				list.add(cert);
				CertPath cp = cf.generateCertPath(list);
				try {
					PKIXParameters params = new PKIXParameters(keyStore);
					params.setRevocationEnabled(false); // CLR is broken,
														// remember?
					CertPathValidator cpv = CertPathValidator
							.getInstance(CertPathValidator.getDefaultType());
					cpv.validate(cp, params);
				} catch (KeyStoreException e) {
					Log.e(TAG, "invalid key store", e);
					throw new CertificateException(e);
				} catch (InvalidAlgorithmParameterException e) {
					Log.e(TAG, "invalid algorithm", e);
					throw new CertificateException(e);
				} catch (NoSuchAlgorithmException e) {
					Log.e(TAG, "no such algorithm", e);
					throw new CertificateException(e);
				} catch (CertPathValidatorException e) {
					Log.e(TAG, "verification failed");
					throw new CertificateException(e);
				}
				Log.d(TAG, "verification successful");
			}
		};

		HttpsURLConnection
				.setDefaultSSLSocketFactory(new AdditionalKeyStoresSSLSocketFactory(
						tm));
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				try {
					// check if hostname matches DN
					String dn = session.getPeerCertificateChain()[0]
							.getSubjectDN().toString();

					Log.d(TAG, "DN=" + dn);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
						return dn.contains("CN=" + hostname);
					} else {
						// no SNI on API<9, but I know the first
						// vhost's hostname
						return dn.contains("CN=" + hostname)
								|| dn.contains("CN=" + Preferences.getServer());
					}
				} catch (Exception e) {
					Log.e(TAG, "unexpected exception", e);
					return false;
				}
			}
		});
	}

	/**
	 * Bypasses certificate verification in application.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	/*public static void initUntrustedSecureConnection()
			throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, untrustedTM, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(untrustedHN);

		Log.w(TAG, "Untrusted secure connection initialized");
	}*/

	/**
	 * Initialize certificate verification in application.
	 * 
	 * @param context
	 *            Application context
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 */
	public void initSecureConnection(Context context)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, CertificateException, IOException,
			UnrecoverableKeyException {

		trustTerena();
		Log.i(TAG, "Secure connection initialized");
	}
}
