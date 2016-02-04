/*
(c)1999 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
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



/** Z39dedup creates DedupRequests and translates DedupResponses.
 * @version @(#)Z39dedup.java	1.22 02/24/97
 */

public class Z39dedup {
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;
/**
 * Status. 
 */
	public int dedupStatus;
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

    /**
     * DbResults array object containing the breakdown of the representative
     * records in a dedup when more than 1 db in search.
     */
     public DbResults dbResults[];
/**
  * integer data type containing the deduped count of unique postings.
  */
        public int dedupedPostingsCount;

	Z39session zsession;

	public Z39dedup(Z39session z) {
	    zsession = z;
	}

	public Z39dedup() {
	    zsession = new Z39session();
	}

/**
 * Creates and sends a Z39.50 DedupRequest, gets and processes 
 * the Z39.50 response
 * @param referenceId Will be returned on Response.
 * @param resultSetName name of the resultSet to dedup
 * @param dedupResultSetName name of the dedup resultSet
 * @param numDupsToKeep the maximum number of duplicates
 * to retain for a record
 * @param countIsPercent boolean indicating whether the dup count
 * is a percent(true) or a record count
 * @param detectionCriteria the tag indicating the type of duplicate
 *  detectionCriteria
 * @param levelOfMatch the level of match value for the duplicateDetection
 * criteria
 * @param sortCriteria the tag indicating the type of sort Criterion to use
 * @param preferredDatabases space separated list of database names to use
 * when the sort criterion is preferred databases.
 * @param applicableKeys this specifies the keys for the applicable portion
 * of the record field that can be used to indicate which parts of the
 * dedup key specification to use instead of letting the server decide
 * what the criteria for deduplication is. 
 * @exception Exception exceptions from Z39 send/receive
 * @exception Diagnostic1 Z39.50 diagnostics
 */
	public void doDedup(int referenceId, String resultSetName,
		String dedupResultSetName, int numDupsToKeep, 
                boolean countIsPercent,
                int detectionCriteria, int levelOfMatch,
                int sortCriteria, String preferredDatabases, 
                String[] applicableKeys)
                 throws Exception, Diagnostic1 {

      
           BerString zRequest=null, zResponse=null;
           BerConnect zConnection;

           if (zsession == null || zsession.connection == null)
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                      "User's Z39.50 session is not initialized");

           zConnection = (BerConnect)zsession.connection;

           zRequest = Request(referenceId, resultSetName, dedupResultSetName,
                              numDupsToKeep, countIsPercent, 
                              detectionCriteria, levelOfMatch, 
                              sortCriteria, preferredDatabases, 
                              applicableKeys, 0, 0);

  
           if (zRequest == null) {
             throw new Diagnostic1(Diagnostic1.illegalSort, "Unable to create sort request");
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
                  "Invalid sort response received from the Z39.50 Server");
           }
           Response(zResponse);

      }


/**
 * Creates a Z39.50 DedupRequest.
 * @param referenceId Will be returned on Response.
 * @param resultSetName name of the resultSet to dedup
 * @param dedupResultSetName name of the deduped resultSet
 * @param numDupsToKeep the maximum number of duplicates
 * to retain for a record
 * @param countIsPercent boolean indicating whether the dup count
 * is a percent(true) or a record count
 * @param detectionCriteria the tag indicating the type of duplicate
 *  detectionCriteria
 * @param levelOfMatch the level of match value for the duplicateDetection
 * criteria
 * @param sortCriteria the tag indicating the type of sort Criterion to use
 * @param preferredDatabases space separated list of database names to use
 * when the sort criterion is preferred databases.
 * @param applicableKeys this specifies the keys for the applicable portion
 * of the record field that can be used to indicate which parts of the
 * dedup key specification to use instead of letting the server decide
 * what the criteria for deduplication is. 
 * @return BerString containing Request or null if space was unavailable
 */
  public BerString Request(int referenceId, String resultSetName,
                           String dedupResultSetName,
                           int numDupsToKeep, boolean countIsPercent,
                           int detectionCriteria, int levelOfMatch, 
                           int sortCriteria, String preferredDatabases,
                           String[] applicableKeys) {

           return (Request(referenceId,resultSetName, dedupResultSetName,
                           numDupsToKeep,  countIsPercent,
                           detectionCriteria, levelOfMatch, 
                           sortCriteria, preferredDatabases, 
                           applicableKeys, 0, 0));

         }

/**
 * Creates a Z39.50 DedupRequest.
 * @param referenceId Will be returned on Response.
 * @param resultSetName name of the resultSet to dedup
 * @param dedupResultSetName name of the dedup resultSet
 * @param numDupsToKeep the maximum number of duplicates
 * to retain for a record
 * @param countIsPercent boolean indicating whether the dup count
 * @param detectionCriteria the tag indicating the type of duplicate
 *  detectionCriteria
 * @param levelOfMatch the level of match value for the duplicateDetection
 * criteria
 * @param sortCriteria the tag indicating the type of sort Criterion to use
 * @param preferredDatabases space separated list of database names to use
 * when the sort criterion is preferred databases.
 * is a percent(true) or a record count
 * @param applicableKeys this specifies the keys for the applicable portion
 * of the record field that can be used to indicate which parts of the
 * dedup key specification to use instead of letting the server decide
 * what the criteria for deduplication is. 
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, String resultSetName,
                String dedupResultSetName, int numDupsToKeep,
                boolean countIsPercent,
                int detectionCriteria, int levelOfMatch, 
                int sortCriteria, String preferredDatabases,
  	        String[] applicableKeys,
	   	int extraLen, int offset) {


 	    dedupStatus = 0;
	    errorCode = 0;
	    errorMsg = "None provided";
            DataDir parm;

	    DataDir dir = new DataDir(Z39api.dedupRequest, ASN1.CONTEXT);
	    if (referenceId != 0)
	        dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

            if (resultSetName != null) {
              parm = dir.daddTag(Z39dedupApi.inputResultSetName, ASN1.CONTEXT);
 	      parm.daddChar(ASN1.VISIBLESTRING, ASN1.CONTEXT, resultSetName);
	    }	

            if (dedupResultSetName != null) 
              dir.daddChar(Z39dedupApi.outputResultSetName, ASN1.CONTEXT, 
                  dedupResultSetName);


            if (applicableKeys != null) {
		parm = dir.daddTag(Z39dedupApi.applicablePortionOfRecord, 
                                        ASN1.CONTEXT);
		DuplicateCriteria.addESpecData(parm, applicableKeys);
            } 

            parm = dir.daddTag(Z39dedupApi.duplicateDetectionCriteria,
                                  ASN1.CONTEXT);
            switch(detectionCriteria) {
              case Z39dedupApi.levelOfMatch: 
                parm.add(Z39dedupApi.levelOfMatch, ASN1.CONTEXT,
                              levelOfMatch);
                break;
              case Z39dedupApi.caseSensitive:     
              case Z39dedupApi.punctuationSensitive:
              case Z39dedupApi.rsDuplicates:
                parm.add(detectionCriteria, ASN1.CONTEXT, (String)null);
                break;
            }

            dir.daddNum(Z39dedupApi.clustering, ASN1.CONTEXT, 1);
 
            parm = dir.daddTag(Z39dedupApi.retentionCriteria, ASN1.CONTEXT);
            if (countIsPercent)
              parm.daddNum(Z39dedupApi.percentOfEntries, ASN1.CONTEXT,
                              numDupsToKeep);
            else 
              parm.daddNum(Z39dedupApi.numberOfEntries, ASN1.CONTEXT, 
                           numDupsToKeep);
           
            parm = dir.daddTag(Z39dedupApi.sortCriteria, ASN1.CONTEXT); 
            if (preferredDatabases != null) {
             StringTokenizer st = new StringTokenizer(preferredDatabases, " ");
             while (st.hasMoreTokens())
                parm.daddChar(Z39dedupApi.preferredDatabase, 
                    ASN1.CONTEXT, st.nextToken());               
            }
            else
              parm.daddChar(sortCriteria, ASN1.CONTEXT, (String)null);

            if (zsession != null && zsession.sessionId != null)
            {
                OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
            }


           if (zsession.logger != null &&
                zsession.logger.getLevel() == Z39logging.HIGH)
                zsession.logger.println("DEDUP REQUEST: " + dir.toString());


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
                zsession.logger.println("DEDUP RESPONSE: " + 
                    response.toString());


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
		    case Z39dedupApi.status:
			dedupStatus = parm.dgetNum();
			break;

                    case Z39dedupApi.resultSetCount:
                        dedupedPostingsCount = parm.dgetNum();
                        break;
                   
                    case Z39api.otherInformation:
                        DataDir data = OtherInformation.getData(parm,
                            oclcUserInformation3.OID);
                        if (data != null)
                        {
                            oclcUserInformation3 o3 =
                                new oclcUserInformation3(data);
                            dbResults = o3.dbResults;
                        }
                        break;
  
		    case Z39api.nonSurrogateDiagnostic:
			for (subparm=parm.child(); subparm != null; 
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
	      "Z39dedup: referenceId(" + referenceId +
	      ")" + newLine + "DedupStatus(" + dedupStatus +
	      ")" + newLine + "uniquePostings(" + dedupedPostingsCount+
	      ")" + newLine + "requestLength(" + requestLength +
	      ")" + newLine + "responseLength(" + responseLength +
	      ")" + newLine + "errorCode(" + errorCode +
	      ")" + newLine + "errorMsg(" + errorMsg + ")" + newLine);
	    return str.toString();
	}
}
