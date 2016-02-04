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

/** Z39trc creates Trigger Resource Control Requests
 * @version @(#)Z39trc.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39trc {
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
        public int referenceId;
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

	public Z39trc(Z39session z) {
	    zsession = z;
	}

	public Z39trc() {
	    zsession = new Z39session();
	}

/**
 * Creates and sends a Z39.50 TriggerResourceControl request. No response is expected
 * @param referenceId Will be returned on Response.
 * @param action Resource Control Action
 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 diagnostics 
 */
	public void doTriggerResourceControl(int referenceId, int action)
                 throws Exception, Diagnostic1 {

      
           BerString zRequest=null, zResponse=null;
           BerConnect zConnection;

           if (zsession == null || zsession.isConnected() == false) 
             throw new Diagnostic1(Diagnostic1.temporarySystemError, 
                      "User's Z39.50 session is not initialized");


          zConnection = (BerConnect)zsession.connection;

          zRequest = Request(referenceId, action, 0, 0);
  
          if (zRequest == null) {
            throw new Diagnostic1(Diagnostic1.temporarySystemError, "Unable to create request");

          }

          // No response expected, just send it
          try {
             zConnection.sendRequest(zRequest); 
           }
           catch (Exception e1) {
             zsession.reset();
             throw new Diagnostic1(Diagnostic1.databaseUnavailable, 
                  "Unable to send request to the Z39.50 server");
           }
           return;

       }



/**
 * Creates a Z39.50 TriggerResourceControl Request.
 * @param referenceId Will be returned on Response.
 * @param action 
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int action) {

           return (Request(referenceId, action, 0, 0));

         }

/**
 * Creates a Z39.50 TriggerResourceControl Request.
 * @param referenceId Will be returned on Response.
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int action,
	   	       int extraLen, int offset) {

            this.referenceId = referenceId;

	    DataDir dir = new DataDir(Z39api.triggerResourceControlRequest, ASN1.CONTEXT);
	    if (referenceId != 0)
	        dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

	    dir.daddNum(Z39trcApi.action, ASN1.CONTEXT, action);

            if (zsession != null && zsession.sessionId != null)  {
                OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
            }
 
            if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println("TRC: " + dir.toString());

            requestLength = dir.recLen() + extraLen;

	    if (extraLen != 0 || offset != 0) 
                return new BerString(dir, extraLen, offset);
            else
                return new BerString(dir);
	}


	public String toString() {
		return "Z39trc: referenceId(" + referenceId + ")";

	}


}
