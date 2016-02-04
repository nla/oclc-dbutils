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

/** deleteRequest builds and parses DeleteResultSetRequests.
 * @version @(#)Z39delete.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39delete {

/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here
 */
	public int referenceId;
/**
 * Specified by the origin. Request to delete all results set or just
 * a specified list of results sets
 */
	public int deleteFunction;
/**
 * If the deleteFunction is 'list', then this is the list
 */
   	public String resultSetList[];
/**
 * Returned by the target
 */
	public int deleteOperationStatus;
/**
 * If the deleteFunction was 'list', then these are statuses for each list
 */
	public int deleteListStatuses[];
/**
 * If the deleteFunction was 'bulk' and some were not delete, the target
 * fills this in
 */
	public int numberNotDeleted;
/**
 * Optional text message from the target
 */
	public String deleteMessage;
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

	public Z39delete() {
	}

	public Z39delete(Z39session z) {
           zsession = z;
	}

/**
 * Creates, Issues, and Processes a Z39.50 DeleteResultSet
 * @param referenceId Will be returned on Response and allows Requests
 *        and responses to be matched up by the application.
 * @param deleteFunction See Z39.50 Specification.
 * @param resultSetList See Z39.50 Specification.
 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 diagnostic
 */

        public void doDelete(int referenceId, int deleteFunction,
	    String resultSetList[] ) throws Exception, Diagnostic1 {

           BerString zRequest=null, zResponse=null;
           BerConnect zConnection;

           if (zsession == null || zsession.connection == null)
              throw new Diagnostic1(Diagnostic1.temporarySystemError,
                      "User's Z39.50 session is not initialized");

 
           zConnection = (BerConnect)zsession.connection;

           zRequest = Request(referenceId, deleteFunction, resultSetList, 0, 0);
           if (zRequest == null) {
             throw new Diagnostic1(Diagnostic1.temporarySystemError, "Unable to create delete request");
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

           if (zResponse == null) {
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                  "Invalid delete response received from the Z39.50 Server");
           }

           Response(zResponse);
           return;

        }
/**
 * Creates a Z39.50 DeleteResultSetRequest
 * @param referenceId Will be returned on Response and allows Requests
 *        and responses to be matched up by the application.
 * @param deleteFunction See Z39.50 Specification.
 * @param resultSetList See Z39.50 Specification.
 * @return BerString containing Request or null if space was unavailable
 */
	public final BerString Request(int referenceId, int deleteFunction,
	    String resultSetList[]) {

	    return Request(referenceId, deleteFunction, resultSetList, 0, 0);
        }
          

/**
 * Creates a Z39.50 DeleteResultSetRequest
 * @param referenceId Will be returned on Response and allows Requests
 *        and responses to be matched up by the application.
 * @param deleteFunction See Z39.50 Specification.
 * @param resultSetList See Z39.50 Specification.
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public final BerString Request(int referenceId, int deleteFunction,
	    String resultSetList[], int extraLen, int offset) {


	    this.referenceId = referenceId;
	    this.deleteFunction = deleteFunction;
	    this.resultSetList = resultSetList;
            return(toBerString(extraLen, offset));
 	}

/**
 * Creates a Z39.50 DeleteRequest BerString given that all the initclass request are
 * already set up.
 * @return BerString containing Request or null if space was unavailable
 */
        public BerString toBerString() {
           return toBerString(0, 0);

        }

/**
 * Creates a Z39.50 DeleteRequest BerString given that all the initclass request are
 * already set up
 * @param extraLen Allow this much extra room in the built BER return record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
        public BerString toBerString(int extraLen, int offset) {

            BerString newData = toBer();

            if (extraLen != 0 || offset != 0)
            {
              requestLength += extraLen;
              DataDir newDir = new DataDir(newData);
              return new BerString(newDir, extraLen, offset);
            }
            else
              return newData;
        }
/**
 * BER encodes the Z39.50 Delete Request
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString toBer() {

             
	    DataDir dir, seq;

            this.reset();

	    dir = new DataDir(Z39api.deleteResultSetRequest, ASN1.CONTEXT);
            if (referenceId != 0)
                dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

            dir.daddNum(Z39deleteApi.DELETEFUNCTION, ASN1.CONTEXT, deleteFunction);
            if (deleteFunction == Z39deleteApi.list && resultSetList != null)
            {
                seq = dir.daddTag(ASN1.SEQUENCE, ASN1.UNIVERSAL);
                for (int i=0; i<resultSetList.length; i++)
                {
                    seq.daddChar(Z39api.ResultSetId, ASN1.CONTEXT, 
                        resultSetList[i]);
                }
            }

            if (zsession != null && zsession.sessionId != null)
            {
                OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
            }
 

            requestLength = dir.recLen();

	    if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println("INIT: " + dir.toString() );
 
            return new BerString(dir);
	}

/**
 * Processes a Z39.50 DeleteResponse.
 * @param response BerString containing response.
 */
	public void Response(BerString response) {
	    DataDir dir = new DataDir(response);
	    init(dir);
  	}


	public void Response(DataDir response) {
	    init(response);
  	}

	private void init(DataDir dir) {
	    DataDir child, seq, seq2;
	    int     i;

	    if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println(
		"DeleteResultSetResponse: " + dir);

            responseLength = dir.recLen();
 
            // if fldid != Z39api.deleteResultSetResponse - throw exception
            // Z39api.logit
 
            for (child = dir.child(); child != null; child=child.next())
            {
		switch (child.fldid())
		{
		    case Z39api.ReferenceId:
			referenceId = child.dgetNum();
                        break;

		    case Z39deleteApi.OPERATIONSTATUS:
			deleteOperationStatus = child.dgetNum();
			break;

		    case Z39deleteApi.LISTSTATUSES:
		    case Z39deleteApi.BULKSTATUSES:
		 	resultSetList = new String[child.count()];
			deleteListStatuses = new int[child.count()];
			for (i = 0, seq = child.child(); seq != null; 
			    seq = seq.next(), i++)
			    for (seq2 = seq.child(); seq2 != null; 
				seq2 = seq2.next())
			    {
				switch (seq2.fldid())
				{
				    case Z39api.ResultSetId:
					resultSetList[i] = seq2.dgetChar();
				 	break;
				    case Z39deleteApi.SETSTATUS:
					deleteListStatuses[i] = seq2.dgetNum();
					break;
				}
			    }
			break;

		    case Z39deleteApi.NUMNOTDELETED:
			numberNotDeleted = child.dgetNum();

		    case Z39deleteApi.MESSAGE:
			deleteMessage = child.dgetChar();
			break;
		}
	    }
	}


	public String toString() {
            String newLine = System.getProperty("line.separator");
	    StringBuffer str = new StringBuffer(
			"deleteRequest: referenceId(" + referenceId +
			")" + newLine + "deleteFunction(" + deleteFunction +
			")" + newLine + "requestLength(" + requestLength +
			")" + newLine + "responseLength(" + responseLength +
			")" + newLine + "ResultSetList(");
	    for (int i=0; resultSetList != null && i<resultSetList.length; i++)
                str = str.append(resultSetList[i]+"(" + 
		    resultSetList[i] + ") ");
	    return str.toString();
	}

/**
  * Reset response values for re-use of object
  *
  */
    private void reset() {
	deleteOperationStatus = 0;
	deleteListStatuses = null;
	numberNotDeleted = 0;
        requestLength=0;
        responseLength=0;
	deleteMessage = null;
   }
}



