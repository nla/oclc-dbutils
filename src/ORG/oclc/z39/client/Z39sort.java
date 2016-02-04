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



/** Z39sort creates SortRequests and translates SortResponses.
 * @version @(#)Z39sort.java	1.22 02/24/97
 */

public class Z39sort {
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;
/**
 * Status. 
 */
	public int sortStatus;
/*
 * Error code if Sort failed.
 */
	public int errorCode;
/**
 * Error message if Sort failed.
 */
	public String errorMsg = "None provided";

  /**
   * integer data type containing the number of bytes in the request.
   */
        public int requestLength;
  /**
   * integer data type containing the number of bytes in the response.
   */
        public int responseLength;


	Z39session zsession;

	public Z39sort(Z39session z) {
	    zsession = z;
	}

	public Z39sort() {
	    zsession = new Z39session();
	}

/**
 * Creates and sends a Z39.50 SortRequest, gets and processes the Z39.50 response
 * @param referenceId Will be returned on Response.
 * @param resultSetName name of the resultSet to sort
 * @param sortResultSetName name of the sorted resultSet
 * @param sortKeys array of sort keys
 * @param sortOrder array of the order to sort corresponding to the sort keys
 * @param fUseSortAttributes flag to indicate how to build the sort request. true=request uses the sortAttributes encoding. see the Z39.50 specification
 * @param sortAccrossDatabases flag to indicate to tell the ZBase that 
 * the results should be the sort of all the records from all the databases
 * in the group vs. having the sorts be within each db not merged together 
 * where true indicates to sort accross database boundaries.
 * @exception Exception exceptions from Z39 send/receive
 * @exception Diagnostic1 Z39.50 diagnostics
 */
	public void doSort(int referenceId, String resultSetName,
		String sortResultSetName, String sortKeys[],
		int sortOrder[], 
		boolean fUseSortAttributes,
                boolean sortAccrossDatabases) throws Exception, Diagnostic1 {

      
           BerString zRequest=null, zResponse=null;
           BerConnect zConnection;

           if (zsession == null || zsession.connection == null)
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                      "User's Z39.50 session is not initialized");

           zConnection = (BerConnect)zsession.connection;

           zRequest = Request(referenceId, resultSetName,
		sortResultSetName, sortKeys,
		sortOrder, fUseSortAttributes, sortAccrossDatabases, 0, 0);

  
           if (zRequest == null) {
             throw new Diagnostic1(Diagnostic1.illegalSort, 
                "Unable to create sort request");
           }

           try {
             zResponse = zConnection.doRequest(zRequest);
           }
	   catch (InterruptedIOException ioe) {  
               if (zsession.doTRC) {   
                   try { 
                       if (zsession.logger != null &&  
                           zsession.logger.getLevel() != Z39logging.OFF) { 
                           synchronized (zsession.logger) { 
                               zsession.logger.println("Sending TRC to sort"); 
                           } 
                       }  
                       zsession.trc.doTriggerResourceControl(0, 3); 
                   } 
                   catch (Exception trce) { 
                       //   trce.printStackTrace(); 
                   } 
                   catch (Diagnostic1 trcd) { 
                       // System.out.println(trcd); 
                   } 
               } 

               //differentiate between 105 and 109 diagnostics
               if (ioe.getMessage().endsWith("user"))
                  throw new Diagnostic1(Diagnostic1.terminatedAtOriginRequest,
                     "Unable to send request to the Z39.50 server");
               else
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
                  "Invalid sort response received from the Z39.50 Server");
           }
           Response(zResponse);

      }



/**
 * Creates a Z39.50 SortRequest.
 * @param referenceId Will be returned on Response.
 * @param resultSetName name of the resultSet to sort
 * @param sortResultSetName name of the sorted resultSet
 * @param sortKeys array of sort keys
 * @param sortOrder array of the order to sort corresponding to the sort keys
 * @param fUseSortAttributes flag to indicate how to build the sort request. true=request uses the sortAttributes encoding. see the Z39.50 specification
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, String resultSetName,
		String sortResultSetName, String sortKeys[],
		int sortOrder[], 
		boolean fUseSortAttributes) {

           return (Request(referenceId, resultSetName,
		sortResultSetName, sortKeys,
		sortOrder, fUseSortAttributes, false, 0, 0));

         }

/**
 * Creates a Z39.50 SortRequest.
 * @param referenceId Will be returned on Response.
 * @param resultSetName name of the resultSet to sort
 * @param sortResultSetName name of the sorted resultSet
 * @param sortKeys array of sort keys
 * @param sortOrder array of the order to sort corresponding to the sort keys
 * @param fUseSortAttributes flag to indicate how to build the sort request. true=request uses the sortAttributes encoding. see the Z39.50 specification
 * @param sortAccrossDatabases flag to indicate to tell the ZBase that
 * the results should be the sort of all the records from all the databases
 * in the group vs. having the sorts be within each db not merged together
 * where true indicates to sort accross database boundaries.
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, String resultSetName,
		String sortResultSetName, String sortKeys[],
		int sortOrder[], 
		boolean fUseSortAttributes, boolean sortAccrossDatabases) {

           return (Request(referenceId, resultSetName,
		sortResultSetName, sortKeys,
		sortOrder, fUseSortAttributes, sortAccrossDatabases, 0, 0));

         }

/**
 * Creates a Z39.50 SortRequest.
 * @param referenceId Will be returned on Response.
 * @param resultSetName name of the resultSet to sort
 * @param sortResultSetName name of the sorted resultSet
 * @param sortKeys array of sort keys up to 6
 * @param sortOrder array of the order to sort corresponding to the sort keys
 * @param fUseSortAttributes flag to indicate how to build the sort request. true=request uses the sortAttributes encoding. see the Z39.50 specification
 * @param sortAccrossDatabases flag to indicate to tell the ZBase that
 * the results should be the sort of all the records from all the databases
 * in the group vs. having the sorts be within each db not merged together
 * where true indicates to sort accross database boundaries.
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, String resultSetName,
		String sortResultSetName, String sortKeys[],
		int sortOrder[], 
		boolean fUseSortAttributes, boolean sortAccrossDatabases,
	   	int extraLen, int offset) {

            DataDir sortseq, seq, subdir, subparm, seq1, parm;
	    int caseValue = 1;

 	    sortStatus = 0;
	    errorCode = 0;
	    errorMsg = "None provided";

	    DataDir dir = new DataDir(Z39api.sortRequest, ASN1.CONTEXT);
	    if (referenceId != 0)
	        dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

            if (resultSetName != null) {
              parm = dir.daddTag(Z39sortApi.inputResultSetName, ASN1.CONTEXT);
 	      parm.daddChar(ASN1.VISIBLESTRING, ASN1.CONTEXT, resultSetName);
	    }	

            if (sortResultSetName != null) 
              dir.daddChar(Z39sortApi.outputResultSetName, ASN1.CONTEXT, 
                  sortResultSetName);



            sortseq = dir.daddTag(Z39sortApi.sequence, ASN1.CONTEXT);
           
            for (int i=0; i<sortKeys.length; i++) {
               if (sortKeys[i] != null && sortKeys[i].equals("-1") == false) {
                 seq = sortseq.daddTag(ASN1.SEQUENCE, ASN1.UNIVERSAL);
		 subdir = seq.daddTag(Z39sortApi.genericSortKey, ASN1.CONTEXT);
                 if (fUseSortAttributes == false)
                   subdir.daddChar(Z39sortApi.field, ASN1.CONTEXT, sortKeys[i]);
                 else {
                    parm = subdir.daddTag(Z39sortApi.attributes, ASN1.CONTEXT);
                    parm.daddoid(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, 
			"1.2.840.10003.3.1");
                    subparm = parm.daddTag(Z39sortApi.AttributeList, ASN1.CONTEXT);

                    int    useInt;
                   
                    try {useInt = Integer.parseInt(sortKeys[i]); }
                    catch (NumberFormatException e) {
                      useInt = 0;
                    }

                    subparm.daddDir(Attribute.buildDir(Attribute.BIB1_use, useInt));
                    
               }
               /* Sort Order */
               seq.daddNum(Z39sortApi.relation, ASN1.CONTEXT, sortOrder[i]);

               /* Case - Insensitive */
               seq.daddNum(Z39sortApi.caseSensitivity, ASN1.CONTEXT, caseValue);
               
            }
         }

    
         if (sortAccrossDatabases == false)
         {
                OtherInformation.addOIDandData(dir,
                    oclcUserInformation9.OID, null);
         }
 

         if (zsession != null && zsession.sessionId != null)
            {
                OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
            }


         if (zsession.logger != null &&
                zsession.logger.getLevel() == Z39logging.HIGH)
                zsession.logger.println("SORT REQUEST: " + dir.toString());


         requestLength = dir.recLen() + extraLen;

	 if (extraLen != 0 || offset != 0)
            return new BerString(dir, extraLen, offset);
          else
            return new BerString(dir);
	}


	// this guy will need to be able to return a failure.
	// does he do it by throwing an exception?
	// some of the DataDir constructors are in the same boat.
	public void Response(BerString response) {
	    DataDir rspdir = new DataDir(response);
	    Response(rspdir);
	}

	public void Response(DataDir response) {
	    DataDir parm = null, subparm = null;


            if (zsession.logger != null &&
                zsession.logger.getLevel() == Z39logging.HIGH)
                zsession.logger.println("SORT RESPONSE: " + response.toString());


	    errorCode = 0;
	    errorMsg = null;
            responseLength = response.recLen();

	    for (parm=response.child();parm != null; parm = parm.next())
	    {

		switch (parm.fldid())
		{
		    case Z39api.ReferenceId:
			referenceId = parm.dgetNum();
			break;
		    case Z39sortApi.status:
			sortStatus = parm.dgetNum();
			break;

		    case 5:
			for (subparm=parm.child().child(); subparm != null; 
			     subparm=subparm.next())
			    switch(subparm.fldid())
			    {
				case ASN1.INTEGER:
				    errorCode = subparm.dgetNum();
				    break;
				case ASN1.VISIBLESTRING:
				    errorMsg = subparm.dgetChar();
				    break;
			    }
			break;
		}
	    }
            // The server didn't know who this request was from.  We need to clean-up this
            // user so we can re-init the user
            if (errorCode == Diagnostic1.unknownSessionId) {
              if (zsession != null)
                zsession.fInitDone = false;
            }
	}

	public String toString() {
            String newLine = System.getProperty("line.separator");
	    StringBuffer str = new StringBuffer(
	      "Z39sort: referenceId(" + referenceId +
	      ")" + newLine + "SortStatus(" + sortStatus +
	      ")" + newLine + "requestLength(" + requestLength +
	      ")" + newLine + "responseLength(" + responseLength +
	      ")" + newLine + "errorCode(" + errorCode +
	      ")" + newLine + "errorMsg(" + errorMsg + ")" + newLine);
	    return str.toString();
	}
}
