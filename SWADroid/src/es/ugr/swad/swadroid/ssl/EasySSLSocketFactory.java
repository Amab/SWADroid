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

import java.io.IOException;  
import java.net.InetAddress;  
import java.net.InetSocketAddress;  
import java.net.Socket;  
import java.net.UnknownHostException;  

import javax.net.ssl.SSLContext;  
import javax.net.ssl.SSLSocket;  
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;  

import org.apache.http.conn.ConnectTimeoutException;  
import org.apache.http.conn.scheme.LayeredSocketFactory;  
import org.apache.http.conn.scheme.SocketFactory;  
import org.apache.http.params.HttpConnectionParams;  
import org.apache.http.params.HttpParams;  
/**
 * SSLSocketFactory for self-signed certificates.
 * 
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class EasySSLSocketFactory extends SSLSocketFactory implements SocketFactory, LayeredSocketFactory 
{  
    private SSLContext sslcontext = null;  

    private static SSLContext createEasySSLContext() throws IOException 
    {  
      try
      {  
        SSLContext context = SSLContext.getInstance("TLS");  
        context.init(null, new TrustManager[] { new EasyX509TrustManager(null) }, null);  
        return context;  
      }
      catch (Exception e) 
      {  
        throw new IOException(e.getMessage());  
      }  
    }  

    private SSLContext getSSLContext() throws IOException 
    {  
      if (this.sslcontext == null) 
      {  
        this.sslcontext = createEasySSLContext();  
      }  
      return this.sslcontext;  
    }  

    /** 
     * @see org.apache.http.conn.scheme.SocketFactory#connectSocket(java.net.Socket, java.lang.String, int, 
     *      java.net.InetAddress, int, org.apache.http.params.HttpParams) 
     */  
    public Socket connectSocket(Socket sock,
                                    String host,
                                    int port, 
                                    InetAddress localAddress,
                                    int localPort,
                                    HttpParams params) 

                throws IOException, UnknownHostException, ConnectTimeoutException 
    {  
      int connTimeout = HttpConnectionParams.getConnectionTimeout(params);  
      int soTimeout = HttpConnectionParams.getSoTimeout(params);  
      InetSocketAddress remoteAddress = new InetSocketAddress(host, port);  
      SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());  

      if ((localAddress != null) || (localPort > 0)) 
      {  
        // we need to bind explicitly  
        if (localPort < 0) 
        {  
          localPort = 0; // indicates "any"  
        }  
        InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);  
        sslsock.bind(isa);  
      }  

      sslsock.connect(remoteAddress, connTimeout);  
      sslsock.setSoTimeout(soTimeout);  
      return sslsock;    
    }  

    /** 
     * @see org.apache.http.conn.scheme.SocketFactory#createSocket() 
     */  
    public Socket createSocket() throws IOException {  
        return getSSLContext().getSocketFactory().createSocket();  
    }  

    /** 
     * @see org.apache.http.conn.scheme.SocketFactory#isSecure(java.net.Socket) 
     */  
    public boolean isSecure(Socket socket) throws IllegalArgumentException {  
        return true;  
    }  

    /** 
     * @see org.apache.http.conn.scheme.LayeredSocketFactory#createSocket(java.net.Socket, java.lang.String, int, 
     *      boolean) 
     */  
    public Socket createSocket(Socket socket,
                                   String host, 
                                   int port,
                                   boolean autoClose) throws IOException,  
                                                             UnknownHostException 
    {  
      return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);  
    }  

    // -------------------------------------------------------------------  
    // javadoc in org.apache.http.conn.scheme.SocketFactory says :  
    // Both Object.equals() and Object.hashCode() must be overridden  
    // for the correct operation of some connection managers  
    // -------------------------------------------------------------------  

    public boolean equals(Object obj) {  
        return ((obj != null) && obj.getClass().equals(EasySSLSocketFactory.class));  
    }  

    public int hashCode() {  
        return EasySSLSocketFactory.class.hashCode();  
    }

	@Override
	public String[] getDefaultCipherSuites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException,
			UnknownHostException {
		
		return getSSLContext().getSocketFactory().createSocket(host, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost,
			int localPort) throws IOException, UnknownHostException {
		
		return getSSLContext().getSocketFactory().createSocket(host, port, localHost, localPort);
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		
		return getSSLContext().getSocketFactory().createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress address, int port,
			InetAddress localAddress, int localPort) throws IOException {
		
		return getSSLContext().getSocketFactory().createSocket(address, port, localAddress, localPort);
	}  
}
