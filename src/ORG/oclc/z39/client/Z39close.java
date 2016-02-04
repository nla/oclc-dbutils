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
import ORG.oclc.ber.*;
import ORG.oclc.z39.*;


/** Z39close creates Close requests.
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class Z39close {

/**
 * Value passed to Request and returned by a target Z39.50 server
 */
	public int referenceId;
/**
 * Why the origin or target is closing the z-association
 */
	public int closeReason;
/**
 * integer data type containing the number of bytes in the request.
 */
	public int requestLength;
/**
 * integer data type containing the number of bytes in the response.
 */
	public int responseLength;
/**
 * 
 */
	public String sessionId;
	public String txnId;
/**
 * Z39session
 */
        public Z39session zsession;

        public Z39close() {
        }

        public Z39close(Z39session z) {
           zsession = z;
        }

        public String sessionId() {
           return sessionId;
        }

        public String txnId() {
           return txnId;
        }
 
/**
 * Creates, sends, and processes a Z39.50 Close Request
 * @param referenceId Will be returned on Response and allows Requests
 * and responses to be matched up by the application.
 * @param closeReason See Z39.50 Specification.
 * @exception Exception when the request fails
 */
      public void doClose(int referenceId, int closeReason) 
          throws Exception, Diagnostic1 {

           BerString zRequest=null, zResponse=null;
           BerConnect zConnection;

           // Session gone, just return
           if (zsession == null || zsession.isConnected() == false)
             return;

           zConnection = (BerConnect)zsession.connection;

           zRequest = Request(referenceId, closeReason);
           
           if (zRequest == null) 
             throw new Exception("Unable to create close request");

           try {zResponse = zConnection.doRequest(zRequest); }
           catch (Exception e) {
            throw e;
           }

           if (zResponse == null) {
             throw new Exception("No response received to close request");
           }

           Response(zResponse);

      }
/**
 * Creates a Z39.50 Close Request
 * @param referenceId Will be returned on Response and allows Requests
 * and responses to be matched up by the application.
 * @param closeReason See Z39.50 Specification.
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int closeReason) {

           return Request(referenceId, closeReason, 0, 0); 
	}

/**
 * Creates a Z39.50 Close Request
 * @param referenceId Will be returned on Response and allows Requests
 * and responses to be matched up by the application.
 * @param closeReason See Z39.50 Specification.
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int closeReason, 
            int extraLen, int offset)
	{
            requestLength = 0;

	    DataDir dir = new DataDir(Z39api.Close, ASN1.CONTEXT);
	    if (referenceId != 0)
	        dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

	    dir.daddNum(Z39closeApi.closeReason, ASN1.CONTEXT, closeReason);

            if (zsession != null && (zsession.sessionId != null || sessionId != null))
            {
                if (sessionId != null || txnId != null) {
                  OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId, txnId));
                }
                else
                  OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));

            }


	    if (zsession != null && zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println("CLOSE: " + dir.toString() );

            requestLength = dir.recLen() + extraLen;

	    if (extraLen != 0 || offset != 0) 
		return new BerString(dir, extraLen, offset);
	    else
	        return new BerString(dir);

	}

       public BerString Request(byte[] referenceId, int closeReason,
            int extraLen, int offset)
        {
            requestLength = 0;

            DataDir dir = new DataDir(Z39api.Close, ASN1.CONTEXT);
           if (referenceId != null)
                dir.add(Z39api.ReferenceId, ASN1.CONTEXT, referenceId, 0,
referenceId.length);

            dir.daddNum(Z39closeApi.closeReason, ASN1.CONTEXT, closeReason);

            if (zsession != null && (zsession.sessionId != null || sessionId
!=
null))
            {
                if (sessionId != null || txnId != null) {
                  OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0,
zsession.sessionId, txnId));
                }
                else
                  OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0,
zsession.sessionId));

            }


            if (zsession != null && zsession.logger != null &&
                zsession.logger.getLevel() == Z39logging.HIGH)
                zsession.logger.println("CLOSE: " + dir.toString() );

            requestLength = dir.recLen() + extraLen;

            if (extraLen != 0 || offset != 0)
                return new BerString(dir, extraLen, offset);
            else
                return new BerString(dir);

}

/**
 * Processes a Z39.50 Close
 * @param response BerString containing response.
 */
	public void Response(BerString response) {
	    DataDir rspdir = new DataDir(response);
	    Response(rspdir);
	}

/**
 * Processes a Z39.50 Close.
 * @param response DataDir containing response
 */
	public void Response(DataDir response) {
	    DataDir pchild = null;

	    if (zsession != null && zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println(
		    "CLOSE Response: " + response.toString());

            responseLength = response.recLen();

	    for (pchild = response.child(); pchild != null; 
	  	 pchild=pchild.next())
	    {
//System.out.println(pchild.toString());
		switch (pchild.fldid())
		{
		    case Z39api.ReferenceId:
			referenceId = pchild.dgetNum();
			break;
		    case Z39closeApi.closeReason:	
			closeReason = pchild.dgetNum();
			break;
		    case Z39api.otherInformation:
                      DataDir data = OtherInformation.getData(pchild, oclcUserInformation2.OID);
                      if (data != null) {
                       oclcUserInformation2 o2 = new oclcUserInformation2(data);
                       sessionId = o2.sessId;
                       txnId = o2.txnId;
                      }
                      break;
		}
	    }
	}

	public String toString() {
            String newLine = System.getProperty("line.separator");
	    StringBuffer str = new StringBuffer(
			"Z39close: referenceId(" + referenceId +
                        ")" + newLine + "sessionId(" + sessionId +
                        ")" + newLine + "txnId(" + txnId +
			")" + newLine + "closeReason(" + closeReason + ")"); 
	    return str.toString();
	}
}
