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

package ORG.oclc.z39;

import ORG.oclc.ber.*;
import ORG.oclc.z39.client.*;
import java.util.*;
import java.io.*;

/**
 * @version @(#)Z39session.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39session {

/**
 * Names of results sets for each search done by this session
 */
	public Vector ResultSetNames;
/**
 * SessionId assigned by server
 */
	public String sessionId;
/**
 * Reference id assigned by client
 */
        public int refId;
/**
 * Host of the server that this session connects to
 */
	public String host;
/**
 * Port of the server that this session connects to
 */
	public int port;
/**
 * ReadTimeout period on the server in milliseconds.
 */
        public int readTimeout;
/**
 * Connection object; this is a place for the client to remember things
 * about this session
 */
	public Object connection;
/**
 * Authorization to use for this session
 */
        public String autho;
/**
 * Password to use for this session
 */
        public String password; 
/**
 * New password for changing the password
 */
        public String newPassword; 
/**
 * Additional userInformation for the z3950 server init.
 */
        public DataDir userInfo;
/**
  * Flag indicating whether the remote server support reconnect.
  */
        public boolean reconnect;
/**
 * ??
 */
        public boolean fInitDone;
/**
 * Flag indicating whether the server supports Trigger Resource, the 
 * default is true and the init response options string will set if
 * off if not supported.
 */
        public boolean doTRC;
/**
 * Flag indicating whether to UTF8 encode the search requests.
 */
        public boolean utf8Encode;

/**
 * A Z39response object that can be re-used.
 */
	public Z39response response;
/**
 * A Z39init object that can be re-used.
 */
	public Z39init     init;
/**
 * A Z39search object that can be re-used.
 */
 	public Z39search   search;
/**
 * A Z39scan object that can be re-used.
 */
	public Z39scan     scan;
/**
 * A Z39present object that can be re-used.
 */
 	public Z39present  present;
/**
 * A Z39delete object that can be re-used.
 */
	public Z39delete   delete;
/**
 * A Z39close object that can be re-used.
 */
	public Z39close    close;
/**
 * A Z39sort object that can be re-used.
 */
	public Z39sort     sort;
/**
 * A Z39trigger resource control object that can be re-used.
 */
	public Z39trc      trc;
/**
 * A Z39extsvc object that can be re-used.
 */
	public Z39extsvc   extsvc;
/**
 * A Z39accessControl access control object that can be re-used.
 */
        public Z39accessControl      accessControl;

/**
  * A Z39dedup access control object that can be re-used.
  */
        public Z39dedup   dedup;

/**
  * The Z39 logging object.
  */
        public Z39logging   logger;
 
	public Z39session() {
	    ResultSetNames = new Vector(20);
	    sessionId = null;
            refId=0;
            autho = null;
            password = null;
            newPassword = null; 
            userInfo = null;
            fInitDone=false;
	    doTRC=true;
            host = null;
            port = 0;
	}

        public void setLog(OutputStream logfile) {
          if (logfile == null) 
            return;

          if (logger == null) {
            try {
              logger = new Z39logging(logfile);
            }
            catch (Exception e) {}
          }
        }

/**
 * Set the hostname and port. 
 */
	public void initClient(String hostName, int portNum) {
            initClient(hostName, portNum, 0);
        }

/**
 * Set the hostname and port and readTimeout.
 */
	public void initClient(String hostName, int portNum, int readTimeout) {
            host = hostName;
            port = portNum;
            this.readTimeout = readTimeout;

	    response = new Z39response(this);
	    init = response.init = new Z39init(this);
	    search = response.search = new Z39search(this);
	    scan = response.scan = new Z39scan(this);
	    sort = response.sort = new Z39sort(this);
	    present = response.present = new Z39present(this);
	    close = new Z39close(this);
	    delete = new Z39delete(this);
	    trc = new Z39trc(this);
	    extsvc = new Z39extsvc(this);
            accessControl = new Z39accessControl(this);
            dedup = new Z39dedup(this);
	}

	public void initClient() {
            initClient(host, port);
	}

	public boolean isConnected() {
           if (this.port == 0 || this.host == null || this.host.length() == 0 ||
               connection == null || this.fInitDone == false) {
             return false;
           }

           return true;
	}

        public void setUserInfo(DataDir info) {
            userInfo = info;
        }

	public void reset() {
	    ResultSetNames = new Vector(20);
            refId=0;
            sessionId=null;
            fInitDone=false;
            closeConnection();
	}

/**
 * Close the Z39 connection to the server
 */
	public void closeConnection() {
            BerConnect c = (BerConnect)connection;
            try {
              if (c != null)
                c.close();
            }
            catch (IOException io){}
            catch (Exception e){}
            connection = null;
	}

	public String toString() {
	    return new String("host(" + host + ") port(" + port + 
		") sessionId(" + sessionId + ")" );
	}
}

