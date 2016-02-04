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

package ORG.oclc.z39.client;

import java.io.*;
import java.util.*;
import ORG.oclc.ber.*;
import ORG.oclc.z39.*;

/** Z39accessControl creates AccessControl Responses to send
 * to the Z3950 server. 
 * @version @(#)Z39accessControl.java	1.1 10/27/98
 * @author Lisa Cox
 */

public class Z39accessControl {
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;
/**
 * AccessControl object created when an access control
 * request is received.
 */
  public AccessControl accessControl;

/**
  * integer data type containing the number of bytes in the request.
  */
        public int requestLength;
/**
  * integer data type containing the number of bytes in the response.
  */
        public int responseLength;
  
/**
 * Z39session
 */
	public Z39session zsession;

	public Z39accessControl(Z39session z) {
	    zsession = z;
	}

	public Z39accessControl() {
	    zsession = new Z39session();
	}

/**
 * Creates and sends a Z3950 AccessControl Response and receives
 * the response to the original command from the server.
 *
 * @param referenceId Will be sent on the Response.
 * @param accessControlInfo AccessControl object containing the
 * response information.
 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 diagnostics
 */
     public void doAccessControl(int referenceId, 
             AccessControl accessControlInfo) 
                throws Exception, Diagnostic1, AccessControl {
      
           BerString zRequest=null, zResponse=null;
           BerConnect zConnection;

           if (zsession == null) 
             throw new Diagnostic1(Diagnostic1.temporarySystemError, 
                      "User's Z39.50 session is not initialized");

           // Re-init to the server
           if (zsession.isConnected() == false) {
             try {

                 zsession.init.reInit();

                 if (zsession.connection == null)
                   throw new Diagnostic1(Diagnostic1.databaseUnavailable, 
                     "Unable to connect to the Z39.50 server");
             }
             catch (Exception e) {
               throw new Diagnostic1(Diagnostic1.databaseUnavailable, 
                   "Unable to connect to the Z39.50 server");
             } 
             catch (Diagnostic1 d) {
               throw d;
             } 
          }  


          zConnection = (BerConnect)zsession.connection;

          zRequest = Response(referenceId, accessControlInfo,
                                0, 0);
  
           if (zRequest == null) {
            throw new Diagnostic1(Diagnostic1.malformedQuery, 
               "Unable to create access control response");
           }

           try {
             zResponse = zConnection.doRequest(zRequest); 
           }
	   catch (InterruptedIOException ioe) { 
	       throw new Diagnostic1(Diagnostic1.databaseUnavailable, 
		     "Unable to send request to the Z39.50 server"); 
	   } 
           catch (Exception e1) {
             zsession.reset();
             throw new Diagnostic1(Diagnostic1.databaseUnavailable, 
                  "Unable to send request to the Z39.50 server");
           }

           if (zResponse == null || accessControl.referringObject == null) {
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                  "Invalid access control response received from the " + 
                  "Z39.50 Server");
           }

           Response(zResponse);

        }



/**
 * Creates a Z3950 AccessControl Response.
 * @param referenceId Will be sent on the Response.
 * @param accessControlInfo AccessControl object containing the
 * response information.
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Response(int referenceId, 
                                 AccessControl accessControlInfo) { 
            return Response(referenceId, accessControlInfo, 0, 0);
         }


/**
 * Creates a Z3950 AccessControl Response.
 * @param referenceId Will be sent on the Response.
 * @param accessControlInfo AccessControl object containing the
 * response information.
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */

      public BerString Response(int referenceId, 
                AccessControl accessControlInfo, 
                int extraLen, int offset) {

          DataDir subdir;

          accessControl = accessControlInfo;

          DataDir dir = new DataDir(Z39api.accessControlResponse,ASN1.CONTEXT);
          if (referenceId != 0)
	        dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

          subdir = dir.daddTag(0, ASN1.CONTEXT);
          subdir = subdir.daddTag(ASN1.EXTERNAL, ASN1.UNIVERSAL);            

          accessControlInfo.addChallengeInfo(subdir);
 

          if (zsession != null && zsession.sessionId != null)
          {
             OtherInformation.addOIDandData(dir,
                 oclcUserInformation2.OID,
               oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
          }

          if (zsession.logger != null && 
             zsession.logger.getLevel() == Z39logging.HIGH) {
              synchronized (zsession.logger) {
		zsession.logger.println("SEARCH REQUEST: " + dir.toString());
            }
          } 

          requestLength = dir.recLen() + extraLen;
	  if (extraLen != 0 || offset != 0)
              return new BerString(dir, extraLen, offset);
          else
              return new BerString(dir);
	}

        public void Response(BerString response) throws AccessControl {
            Response(new DataDir(response));
        }

        public void Response(DataDir dir) throws AccessControl { 

          responseLength = dir.recLen();

          if (accessControl.referringObject instanceof Z39search)
              ((Z39search)accessControl.referringObject).Response(dir);
 
           else if (accessControl.referringObject instanceof Z39scan)
              ((Z39scan)accessControl.referringObject).Response(dir);
 
           else if (accessControl.referringObject instanceof Z39extsvc)
              ((Z39extsvc)accessControl.referringObject).Response(dir);
        }

	public String toString() {
	    StringBuffer str = new StringBuffer(
			"Z39accessControl: referenceId(" + referenceId +
			")\nACInfo(" + accessControl + ")\n");


	    return str.toString();
	}


}
