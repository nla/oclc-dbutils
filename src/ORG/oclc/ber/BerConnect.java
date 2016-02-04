
/*
(c)1996 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
Ohio 43017-0702.  OCLC is a registered trademark of OCLC Online Computer
Library Center, Inc.
 
NOTICE TO USERS:  The Z39.50 Utilities ("Software") has been developed by OCLC
Online Computer Library Center, Inc.  Subject to the terms and conditions set
forth below, OCLC grants to user a perpetual, non-exclusive, royalty-free
license to use, reproduce, alter, modify, and create derivative works from
Software, and to sublicense Software subject to the following terms and
conditions:
 
SOFTWARE IS PROVIDED AS IS.  OCLC MAKES NO WARRANTIES, REPRESENTATIONS, OR
GUARANTEES WHETHER EXPRESS OR IMPLIED REGARDING SOFTWARE, ITS FITNESS FOR ANY
PARTICULAR PURPOSE, OR THE ACCURACY OF THE INFORMATION CONTAINED THEREIN.
 
User agrees that OCLC shall have no liability to user arising therefrom,
regardless of the basis of the action, including liability for special,
consequential, exemplary, or incidental damages, including lost profits,
even if it has been advised of the possibility thereof.
 
User shall cause the copyright notice of OCLC to appear on all copies of
Software, including derivative works made therefrom.
*/

package ORG.oclc.ber;

import ORG.oclc.util.CancellableThread;
import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import ORG.oclc.log.Log;
import ORG.oclc.util.Util;

/** 
 * BerConnect handles the I/0 between a client and a server passing BerEncoded messages.
 * @version @(#)BerConnect.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class BerConnect {

        private static boolean debug = false;

	private Socket           connection;
        private SocketOpener     opener;
        private DataInputStream  in;
	private DataOutputStream out;
	private String           hostname;
	private int              port;
        private int             timeout;
        private int              retryCount=3; 
        private boolean          useConnectTimeout=false;

	public String toString() {
	    return "BerConnect: " + connection + " " + in + " " + out +
		" " + hostname + " " + port;
	}

	private void init(String hostname, int port, int readTimeout,
               boolean useConnectTimeout)
          throws UnknownHostException, IOException {

	    synchronized (this) {

              // This puts a 10 second timer on making the socket connection.
              // Since it is not possible to time a new Socket() call
	      if (useConnectTimeout) {
                 opener = new SocketOpener(hostname, port);
                 connection = opener.makeSocket (10000);
              }
              else 
	        connection = new Socket(hostname, port);  

              if (readTimeout != 0) {
	        connection.setSoTimeout(readTimeout);

              // For timed reads, buffered input stream has a bug, no buffering can be used.
                in = new DataInputStream(connection.getInputStream());
              }
              else {	
                in = new DataInputStream(
		   new BufferedInputStream(connection.getInputStream(), 15360));

                // Leave this as an example
                /*in = new BufferedReader(
	  	 new InputStreamReader(connection.getInputStream()) ,10000);*/
	      }

	      out = new DataOutputStream(
		  new BufferedOutputStream(connection.getOutputStream(), 5000));
	    }
	}

/**
  * Construct a connection object for the input host and port.
  * @param hostname the name of the host
  * @param port the port to connect to 
  */
	public BerConnect(String hostname, int port) 
	throws UnknownHostException, IOException {

	    this.hostname = hostname;
	    this.port = port;
            this.connection = null;
            this.timeout = 0;
	    init(hostname, port, 0, false);
        }

/**
  * Construct a connection object for the input host, port and read timeout.
  * @param hostname the name of the host
  * @param port the port to connect to 
  * @param readTimeout the number of seconds to wait for a read response
  */
	public BerConnect(String hostname, int port, int readTimeout) 
 	  throws UnknownHostException, IOException {

	    this.hostname = hostname;
	    this.port = port;
            this.connection = null;
            this.timeout = readTimeout * 1000;
	    init(hostname, port, this.timeout, false);
        }

    /** 
     * Construct a connection object for the input host, port and flag indicating
     * whether the socket connect should be timed. 
     * @param hostname the name of the host 
     * @param port the port to connect to  
     * @param useConnectTimeout flag to indicate whether the connect request 
     * should be timed. 
     */ 
    public BerConnect(String hostname, int port, boolean useConnectTimeout)  
        throws UnknownHostException, IOException { 
 
	this.hostname = hostname; 
	this.port = port; 
	this.connection = null; 
	this.timeout = 0; 
        this.useConnectTimeout = useConnectTimeout;
	init(hostname, port, 0, useConnectTimeout); 
    } 
 
    /** 
     * Construct a connection object for the input host, port,
     * read timeout, and flag indicating 
     * whether the socket connect should be timed.  
     * @param hostname the name of the host 
     * @param port the port to connect to  
     * @param readTimeout the number of seconds to wait for a read response 
     * @param useConnectTimeout flag to indicate whether the connect request
     * should be timed.
     */ 
    public BerConnect(String hostname, int port, int readTimeout, 
             boolean useConnectTimeout)  
	throws UnknownHostException, IOException { 
	this.hostname = hostname; 
	this.port = port; 
	this.connection = null; 
	this.timeout = readTimeout * 1000; 
        this.useConnectTimeout = useConnectTimeout;
	init(hostname, port, this.timeout, useConnectTimeout); 
    } 
 

/**
 * Send a request. If the socket connection fails, re-connect to hostname and
 * port. Return a response
 * @param request a Ber encoded message
 * @param hostname save this hostname for re-connects
 * @param port save this port for re-connects
 * @exception UnknownHostException the server cannot be located
 * @exception EOFException the server is unavailable
 * @exception FileNotFoundException the server is unavailable
 * @exception IOException the server is unavailable
 * @return a Ber encoded Response
 */
	public BerString doRequest(BerString request, String hostname, 
            int port) throws UnknownHostException, EOFException, 
            FileNotFoundException, IOException, InterruptedIOException { 
 
            this.hostname = hostname;
            this.port = port;
	    return doRequest(request);
	}


/**
 * Send a request. If the socket connection fails, re-connect to the last
 * specified hostname and port. Return a response
 * @param request a Ber encoded request
 * @exception UnknownHostException the server cannot be located
 * @exception EOFException the server is unavailable
 * @exception FileNotFoundException the server is unavailable
 * @exception IOException the server is unavailable
 * @return Ber encoded Response
 */
        public BerString doRequest(BerString request) 
	  throws UnknownHostException, EOFException, FileNotFoundException,
	    IOException, InterruptedIOException
        {
	   return doRequest(request, false);
	}

/**
 * Send a request. If the socket connection fails, re-connect to the last
 * specified hostname and port. Return a response
 * @param request a Ber encoded request
 * @param yieldFlag a boolean indicating whether to yield before reading response
 * @exception UnknownHostException the server cannot be located
 * @exception EOFException the server is unavailable
 * @exception FileNotFoundException the server is unavailable
 * @exception IOException the server is unavailable
 * @return Ber encoded Response
 */
	public BerString doRequest(BerString request, boolean yieldFlag) 
	    throws UnknownHostException, EOFException, FileNotFoundException,
	    IOException, InterruptedIOException {

	    if (request == null)
		return null;
	    if (Thread.currentThread() instanceof CancellableThread) {
		((CancellableThread)Thread.currentThread()).setInputIO(in);
	    }

	    String s;
	    try {
	    for (int i=0; i<this.retryCount; i++) 
	    {

		s = "write";
	        try { 

		  //                    if (Log.out != null)
		  //	System.out.println("Writing OUTBOUND bytes: " + 
		  //			   request.record().length);
             	    out.write(request.record()); 
		    s = "flush";
		    out.flush();


		    s = "read";
                    BerString response=null;
                    synchronized (in) {
		      if (yieldFlag)
			Thread.currentThread().yield();
                      response = new BerString(in);
		      //if (Log.out != null)
		     // 	System.out.println("READ InBOUND bytes: " + 
		      //	   response.record().length);

                    }
		    return response;
		}
                catch (InterruptedIOException n) {
		     if (! CancellableThread.cancelled()) {
			System.out.println("Socket Read timed out (" + port + 
				     "/" + hostname + ") " + n);
			System.out.println("Cancelled: " +
					   CancellableThread.cancelled());

		     }

		    close(true); // This will force it not to break
		    // connection so user can re-use it.

		    if (CancellableThread.cancelled()) 
			throw new 
			    InterruptedIOException("transaction cancelled by user");
		     else
			 throw new InterruptedIOException("transaction cancelled");

                } 
		catch (IOException n) {
                    if (debug) {
		      System.out.println("IOException1: call close" + n);
                      System.out.println("Cancelled= " + 
                           CancellableThread.cancelled());
                      System.out.println("Call Close 2");
                    }
                    close(true);
		    if (CancellableThread.cancelled()) {
                        if (debug)
		   	   System.out.println("Throw Interrupted Exception from IOEXCep1");
		        throw new InterruptedIOException("transaction cancelled by user");
		    }


		    if (i > 0)
		    {
                        if (debug)
			{
			    complain(s, n, request);
			    System.out.println("already tried " + i +
			        " times, will try again in .1s, " +
			        connection.getPort() + " " + 
			        connection.getLocalPort());
			}
		    }
		}

 
		try { 
		    Thread.sleep(50);
		    init(hostname, port, timeout, useConnectTimeout);
		} catch (InterruptedIOException n) {

                  System.out.println("Socket Read timed out2 (" + 
				     port + "/" + hostname + ") " + n);
                  if (debug)
                    System.out.println("Call Close 3");
 
                  close(CancellableThread.cancelled());
		  if (CancellableThread.cancelled())
		      throw new InterruptedIOException("transaction cancelled by user");
		  else
		      throw new IOException("Server Unavailable - Read timeout on socket");

                }
                catch (IOException n) {
		    if (debug && i > 0)
			complain("init", n, request);
		} catch (InterruptedException n) { }


		if (CancellableThread.cancelled()) {
		    throw new InterruptedIOException("transaction cancelled by user");
		}
	    }
	    }
	    finally { 
		if (Thread.currentThread() instanceof CancellableThread) {
		    ((CancellableThread)Thread.currentThread()).setInputIO(null);
		}
	    }
             if (debug)
               System.out.println("Call Close 4");
            close(CancellableThread.cancelled()); // reset connection
            //System.out.println("ServerUnavailable (" + port + "/" + hostname + ") ");

	    throw new EOFException("Server Unavailable");
	}


/**
 * Send a request. If the socket connection fails, re-connect to the last
 * specified hostname and port. This method should ONLY be used when a response
 * is not expected from the server.  When a response is expected, use the doRequest
 * method.
 * @param request Ber encoded request
 * @exception UnknownHostException the server cannot be located
 * @exception EOFException the server is unavailable
 * @exception FileNotFoundException the server is unavailable
 * @exception IOException the server is unavailable
 * @return true if write succeeded
 */
    public boolean sendRequest(BerString request) 
	throws UnknownHostException, EOFException,
	FileNotFoundException,
	IOException {

	if (request == null)
	    return false;
	String s;
	for (int i=0; i<this.retryCount; i++) 
	    {
		s = "write";
		try { 
		    out.write(request.record()); 
		    s = "flush";
		    out.flush();


                    return true;
		}
		catch (IOException n) {
                    close(true);
		    if (i > 0)
			{
			    if (debug)
				{
				    complain(s, n, request);
				    System.out.println("already tried " + i +
					       " times, will try again in .1s, " +
					       connection.getPort() + " " + 
					       connection.getLocalPort()); 
				}
			}
		}
		try
		    {
			Thread.sleep(50);
			init(hostname, port, timeout, useConnectTimeout);
		    }
		catch (InterruptedException e) { e.printStackTrace(); }
		catch (IOException n) {
                    if (debug)
                       n.printStackTrace();
		    if (debug && i > 0)
			complain("init", n, request);
		}
	    }
	return false;
    }


	private void complain(String s, Exception n, BerString request) {
	    System.out.println((new Date()) + s + " failed " + n);
	    System.out.println("connection " + connection);
            if (request != null)
  	      System.out.println("request " + request);
	}

/**
 * Closes the socket connection.
 */
	public void close() throws IOException {
            close(false);
	}

/**
 * Closes the socket connection.
 */
	public void close(boolean cancelled) throws IOException {
            if (debug)
	       System.out.println("CLOSE socket is cancelled: " + cancelled);
            if (connection == null) 
               return;
            try {
	      connection.close(); 
            }
            catch (Exception e) {
             }
	    if (!cancelled) {
		connection = null;
	    }
            if (debug)
	      System.out.println("End of close: connection: " + connection);
	}

/**
 * Sets the read Timeout on the socket to the input number of seconds.
 * @param timeout the number of seconds to wait on a response from a request.
 */
	public void setTimeout(int timeout) {
	    this.timeout = timeout *1000;
            if (connection == null) 
               return;

            try {
              connection.setSoTimeout(this.timeout);
            }
            catch (Exception e) {}

	}

/**
 * Sets the retry count on the number of times to try to send a message where the default = 2.
 * @param counter the number of times to try sending the message before giving up.
 */
	public void setRetryCount(int counter) {
	    this.retryCount = counter;
	}


/**
 * @return the connection
 */
	public Socket connection() {
	    return connection;
	}
}


