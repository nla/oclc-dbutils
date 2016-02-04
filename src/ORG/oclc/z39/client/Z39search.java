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

/** Z39search creates SearchRequests and translates SearchResponses.
 * @version @(#)Z39search.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39search {
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;
/**
 * Number of records located by Search. 
 */
	public int resultCount;
/**
 * Status. 
 */
	public int searchStatus;
/**
 * Piggybacked Present Status. 
 */
	public int resultSetStatus;
/**
 * Error code if Search failed.
 */
	public int errorCode;
/**
 * Error message if Search failed.
 */
	public String errorMsg = "None provided";
/**
 * PresentResponse if piggybacked Present was requested.
 */
	public Z39present Present;
/**
 * Search Results information.
 */
	public DbResults dbResults[];

/**
  * The partial search results information.
  */
    public oclcUserInformation7 oclc7;
 
/**
  * The ranked query results.
  */
    public oclcUserInformation8 oclc8;
 
/**
 * TermComponentPostings Information, if present.
 */
    public TermComponentPostings componentResults[];
 
  /**
   * integer data type containing the number of bytes in the request.
   */
        public int requestLength;
  /**
   * integer data type containing the number of bytes in the response.
   */
        public int responseLength;

  /**
   * AccessControl object created when an access control
   * request is received.
   */
   public AccessControl accessControl;

/**
 * Z39session
 */
	public Z39session zsession;

	public Z39search(Z39session z) {
	    zsession = z;
	}

	public Z39search() {
	    zsession = new Z39session();
	}

/**
 * Creates and sends a Z39.50 SearchRequest, gets and processes the Z3950 
 * response
 *
 * @param referenceId Will be returned on Response.
 * @param smallSetUpperBound Result set boundary to return all records.
 * @param largeSetLowerBound Result set boundary to return no records.
 * @param mediumSetPresentNumber Number of records to return for medium sets.
 * @param replaceIndicator Indicates response in cases where the resultSetName
 * already exists.
 * @param resultSetName Identifies the results from a SearchRequest.
 * @param databaseNames Set of databases to which the query applies.
 * @param smallSetElementSetNames Preferred composition of records.
 * @param mediumSetElementSetNames Preferred composition of records.
 * @param preferredRecordSyntax Requested syntax of returned records.
 * @param query Search query.
 * @param query_type Identifies the type of query. Types 0, 1, 100 & 101 are
 *        supported.
 * @param searchResultsOID Requests additional information to be returned
 *         with search.
 * @param Z39attributesPlusTerm the pre-built z39.50 query part
 * @param fMakeDataDir flag indicating whether to convert returned recs
 * to DataDir format
 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 diagnostics
 */
     public void doSearch(int referenceId, int smallSetUpperBound,
             int largeSetLowerBound, int  mediumSetPresentNumber,
             int replaceIndicator, String resultSetName,
             String databaseNames, String smallSetElementSetNames,
             String mediumSetElementSetNames, String preferredRecordSyntax,
             String query, int query_type, String searchResultsOID,
             DataDir Z39attributesPlusTerm,
             boolean fMakeDataDir) 
             throws Exception, Diagnostic1, AccessControl {
 
       doSearch(referenceId, 
                       smallSetUpperBound, 
                       largeSetLowerBound, 
                       mediumSetPresentNumber,
                       replaceIndicator,
                       resultSetName,
                       databaseNames,
                       smallSetElementSetNames,
                       mediumSetElementSetNames,
                       preferredRecordSyntax,
                       query,
                       query_type,
                       searchResultsOID,
                       Z39attributesPlusTerm,
                       null,
                       null,
                       null,
		       null,
                       fMakeDataDir);
     }

/**
 * Creates and sends a Z39.50 SearchRequest, gets and processes the Z39.50 response
 * @param referenceId Will be returned on Response.
 * @param smallSetUpperBound Result set boundary to return all records.
 * @param largeSetLowerBound Result set boundary to return no records.
 * @param mediumSetPresentNumber Number of records to return for medium sets.
 * @param replaceIndicator Indicates response in cases where the resultSetName
already exists.
 * @param resultSetName Identifies the results from a SearchRequest.
 * @param databaseNames Set of databases to which the query applies.
 * @param smallSetElementSetNames Preferred composition of records.
 * @param mediumSetElementSetNames Preferred composition of records.
 * @param preferredRecordSyntax Requested syntax of returned records.
 * @param query Search query.
 * @param query_type Identifies the type of query. Types 0, 1, 100 & 101 are 
 *        supported.
 * @param searchResultsOID Requests additional information to be returned
 *         with search.
 * @param Z39attributesPlusTerm the pre-built z39.50 query part
 * @param oclcUserInfo additional search info that is ready to add to the 
 *        request
 * @param oclcUserInfoOID object identifier for the additional search info
 * @param rankQuery type 101 query (DataDir) or RPN (String) query for ranking
 *        the result set
 * @param rankOID ranking algorithm identifier
 * @param fMakeDataDir flag indicating whether to convert returned recs
 * to DataDir format

 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 diagnostics 
 */
	public void doSearch(int referenceId, int smallSetUpperBound,
		int largeSetLowerBound, int  mediumSetPresentNumber,
		int replaceIndicator, String resultSetName, 
		String databaseNames, String smallSetElementSetNames, 
		String mediumSetElementSetNames, String preferredRecordSyntax, 
		String query, int query_type, String searchResultsOID,
	   	DataDir Z39attributesPlusTerm, DataDir oclcUserInfo,
                String oclcUserInfoOID, Object rankQuery, String rankOID,
                boolean fMakeDataDir) 
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

          zRequest = Request(referenceId, smallSetUpperBound,
		largeSetLowerBound, mediumSetPresentNumber,
		replaceIndicator, resultSetName, 
		databaseNames,  smallSetElementSetNames, 
		mediumSetElementSetNames, preferredRecordSyntax, 
		query, query_type, searchResultsOID,
                Z39attributesPlusTerm, oclcUserInfo, oclcUserInfoOID,
                rankQuery, rankOID, 0, 0);
  
           if (zRequest == null) {
            throw new Diagnostic1(Diagnostic1.malformedQuery, "Unable to create search request");

//          System.out.println("unable to build search request");
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
			       zsession.logger.println("Sending TRC to search");
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
                  "Invalid search response received from the Z39.50 Server");
           }


           Response(zResponse);

           // If flag is set to decode recs into datadir and we
           // got records back, then decode and make sure we got
           // all we wanted 
           if (resultCount > 0 && 
                  Present.numberOfRecordsReturned > 0) {

             if (fMakeDataDir) 
               Present.decodeRecords();


             if(Present.numberOfRecordsReturned != mediumSetPresentNumber &&
                resultCount > Present.numberOfRecordsReturned) {
  
               int numrecs = mediumSetPresentNumber - 
                  Present.numberOfRecordsReturned;
               //System.out.println("Numrecs returned = " + numrecs +
               //" asked for: " + mediumSetPresentNumber);

               try {

                zsession.present.doPresent(zsession.refId > 0 ? 
                  zsession.refId++ : 0,
                  resultSetName, Present.numberOfRecordsReturned+1,
                  numrecs, resultCount, 
                  mediumSetElementSetNames, preferredRecordSyntax, 
                  fMakeDataDir);
               }
               catch (Exception e) {
                 // Don't barf good results received.
               }
               catch (Diagnostic1 d) {
                 // Don't barf good results received.
               }
               // If we got the records back. add them to the
               // Present Vector or the BerString
              saveGoodRecords(fMakeDataDir);
            }
          }
        }
        private void saveGoodRecords(boolean fMakeDataDir) {
           // We got the records back. add them to the
           // Present Vector or the BerString
           if (fMakeDataDir) {
             if (zsession.present.presentData != null &&
                 zsession.present.presentData.size() > 0) {
               Enumeration e=zsession.present.presentData.elements();
               while ( e.hasMoreElements() )
                  Present.presentData.addElement(e.nextElement());
               Present.numberOfRecordsReturned = 
                                Present.presentData.size();
             }
           }
           else {
             if (zsession.present.records != null &&
                 zsession.present.records.length > 0) { 
               String newDbnames[] = new String[Present.dbnames.length + 
                                     zsession.present.dbnames.length];
               Object newRecords[] = 
                       new Object[Present.records.length + 
                                     zsession.present.records.length];
               System.arraycopy(Present.records, 0, newRecords,
                         0, Present.numberOfRecordsReturned);
               System.arraycopy(zsession.present.records, 0,
                         newRecords, Present.records.length, 
                         zsession.present.records.length);
               System.arraycopy(Present.dbnames, 0, newDbnames,
                         0, Present.dbnames.length);
               System.arraycopy(zsession.present.dbnames, 0,
                         newDbnames, Present.dbnames.length, 
                         zsession.present.dbnames.length);
               Present.records = newRecords;
               Present.dbnames = newDbnames;
               Present.numberOfRecordsReturned = Present.records.length;
            }
          }

        }



/**
 * Creates a Z39.50 SearchRequest.
 * @param referenceId Will be returned on Response.
 * @param smallSetUpperBound Result set boundary to return all records.
 * @param largeSetLowerBound Result set boundary to return no records.
 * @param mediumSetPresentNumber Number of records to return for medium sets.
 * @param replaceIndicator Indicates response in cases where the resultSetName
already exists.
 * @param resultSetName Identifies the results from a SearchRequest.
 * @param databaseNames Set of databases to which the query applies.
 * @param smallSetElementSetNames Preferred composition of records.
 * @param mediumSetElementSetNames Preferred composition of records.
 * @param preferredRecordSyntax Requested syntax of returned records.
 * @param query Search query.
 * @param query_type Identifies the type of query. Types 0, 1, 100 & 101 are 
 *        supported.
 * @param searchResultsOID Requests additional information to be returned
 *         with search.
 * @param Z39attributesPlusTerm the pre-built z39.50 query part
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int smallSetUpperBound,
		int largeSetLowerBound, int  mediumSetPresentNumber,
		int replaceIndicator, String resultSetName, 
		String databaseNames, String smallSetElementSetNames, 
		String mediumSetElementSetNames, String preferredRecordSyntax, 
		String query, int query_type, String searchResultsOID,
	   	DataDir Z39attributesPlusTerm) {

           return (Request(referenceId, smallSetUpperBound,
		largeSetLowerBound, mediumSetPresentNumber,
		replaceIndicator, resultSetName, 
		databaseNames,  smallSetElementSetNames, 
		mediumSetElementSetNames, preferredRecordSyntax, 
		query, query_type, searchResultsOID,
	   	Z39attributesPlusTerm, null, null, null, null, 0, 0));
         }


      public BerString Request(int referenceId, int smallSetUpperBound,
                int largeSetLowerBound, int  mediumSetPresentNumber,
                int replaceIndicator, String resultSetName,
                String databaseNames, String smallSetElementSetNames,
                String mediumSetElementSetNames, String preferredRecordSyntax,
                String query, int query_type, String searchResultsOID,
                DataDir Z39attributesPlusTerm, DataDir oclcUserInfo,
                String oclcUserInfoOID, Object rankQuery, String rankOID) {

           return (Request(referenceId, smallSetUpperBound,
		largeSetLowerBound, mediumSetPresentNumber,
		replaceIndicator, resultSetName, 
		databaseNames,  smallSetElementSetNames, 
		mediumSetElementSetNames, preferredRecordSyntax, 
		query, query_type, searchResultsOID,
	   	Z39attributesPlusTerm, oclcUserInfo, 
                oclcUserInfoOID, rankQuery, rankOID, 0, 0));

      }
 

/**
 * Creates a Z39.50 SearchRequest.
 * @param referenceId Will be returned on Response.
 * @param smallSetUpperBound Result set boundary to return all records.
 * @param largeSetLowerBound Result set boundary to return no records.
 * @param mediumSetPresentNumber Number of records to return for medium sets.
 * @param replaceIndicator Indicates response in cases where the resultSetName
already exists.
 * @param resultSetName Identifies the results from a SearchRequest.
 * @param databaseNames Set of databases to which the query applies.
 * @param smallSetElementSetNames Preferred composition of records.
 * @param mediumSetElementSetNames Preferred composition of records.
 * @param preferredRecordSyntax Requested syntax of returned records.
 * @param query Search query.
 * @param query_type Identifies the type of query. Types 0, 1, 100 & 101 are 
 *        supported.
 * @param searchResultsOID Requests additional information to be returned
 *         with search.
 * @param Z39attributesPlusTerm the pre-built z39.50 query part
 * @param oclcUserInfo additional search info that is ready to add to the 
 *        request
 * @param oclcUserInfoOID object identifier for the additional search info
 * @param rankQuery type 101 query (DataDir) or RPN (String) query for ranking
 *        the result set
 * @param rankOID ranking algorithm identifier
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int smallSetUpperBound,
		int largeSetLowerBound, int  mediumSetPresentNumber,
		int replaceIndicator, String resultSetName, 
		String databaseNames, String smallSetElementSetNames, 
		String mediumSetElementSetNames, String preferredRecordSyntax, 
		String query, int query_type, String searchResultsOID,
	   	DataDir Z39attributesPlusTerm, 
                DataDir oclcUserInfo,
                String oclcUserInfoOID, Object rankQuery, String rankOID,
                int extraLen, int offset) {
	    DataDir parm = null, subdir = null;
	    StringTokenizer st;

            // Reset for a new request
            reset();

	    DataDir dir = new DataDir(Z39api.searchRequest, (int)ASN1.CONTEXT);
	    if (referenceId != 0)
	        dir.add(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

		
	    dir.add(Z39searchApi.smallSetUpperBound, ASN1.CONTEXT,
		smallSetUpperBound);
	    dir.add(Z39searchApi.largeSetLowerBound, ASN1.CONTEXT,
		largeSetLowerBound);
	    dir.add(Z39searchApi.mediumSetPresentNumber, ASN1.CONTEXT,
		mediumSetPresentNumber);
	    dir.add(Z39searchApi.replaceIndicator, ASN1.CONTEXT,
		replaceIndicator);
	
	    zsession.ResultSetNames.addElement(resultSetName);
	    if(zsession != null && zsession.utf8Encode)
		dir.addUTF(Z39searchApi.resultSetName, ASN1.CONTEXT, 
			   resultSetName); 
	    else 
		dir.add(Z39searchApi.resultSetName, ASN1.CONTEXT, resultSetName); 
 

	    parm = dir.add(Z39searchApi.databaseNames, ASN1.CONTEXT);
	    st = new StringTokenizer(databaseNames, " ");
	    while (st.hasMoreTokens())
		if(zsession!=null && zsession.utf8Encode)
		    parm.addUTF(Z39api.DatabaseName, ASN1.CONTEXT, st.nextToken()); 
		else 
		    parm.add(Z39api.DatabaseName, ASN1.CONTEXT, st.nextToken()); 

	    if (smallSetElementSetNames != null)
	    {
		subdir = dir.add(Z39searchApi.smallSetElementSetNames, 
		    ASN1.CONTEXT);
		if(zsession!=null && zsession.utf8Encode) 
		    subdir.addUTF(Z39presentApi.genericElementSetName, 
				  ASN1.CONTEXT, smallSetElementSetNames); 
		else 
		    subdir.add(Z39presentApi.genericElementSetName, ASN1.CONTEXT,  
			   smallSetElementSetNames); 
	    }

	    if (mediumSetElementSetNames != null)
	    {
		subdir = dir.add(Z39searchApi.mediumSetElementSetNames,
		    ASN1.CONTEXT);
		if(zsession!=null && zsession.utf8Encode) 
		    subdir.addUTF(Z39presentApi.genericElementSetName, 
				  ASN1.CONTEXT, mediumSetElementSetNames); 
		else 
		    subdir.add(Z39presentApi.genericElementSetName, ASN1.CONTEXT,  
			   mediumSetElementSetNames); 
	    }

	    if (preferredRecordSyntax != null)
		dir.addOID(Z39presentApi.PreferredRecordSyntax, ASN1.CONTEXT,
		    preferredRecordSyntax);
	    else
		dir.addOID(Z39presentApi.PreferredRecordSyntax, ASN1.CONTEXT,
		    Z39presentApi.MARC_SYNTAX);
	    parm = dir.add(Z39searchApi.query, ASN1.CONTEXT);

	    if (Z39attributesPlusTerm != null && query_type != 0)
	    {
	        parm = parm.add(query_type, ASN1.CONTEXT);
	        parm.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, 
		    "1.2.840.10003.3.1");
		parm.add(Z39attributesPlusTerm);
	    }
	    else if (query_type==1 || query_type==100) // 100 for backward comp.
	    {
		if (!this.make_type_x(query, parm, Z39searchApi.type_1, zsession, true))
		    return null;
	    }
	    else if (query_type==101)
	    {
		if (!this.make_type_x(query, parm, Z39searchApi.type_101, zsession, true))
		    return null;
	    }
	    else if (query_type==0) {
		if(zsession !=null && zsession.utf8Encode) 
		    parm.addUTF(Z39searchApi.type_0, ASN1.CONTEXT, query); 
		else 
		    parm.add(Z39searchApi.type_0, ASN1.CONTEXT, query); 
	    }


	    if (searchResultsOID != null)
	    {
                // The FirstSearch request is built with the
                // the InfoCategory field in the otherInformation request
                if (searchResultsOID.equals(oclcUserInformation6.OID)) {

  		  OtherInformation.addOIDandData(dir,
                    oclcUserInformation6.OID, 1, 
                    Z39searchApi.additionalSearchInfo);
                }
                else 
  		  OtherInformation.addOIDandData(dir, searchResultsOID,
		    null, Z39searchApi.additionalSearchInfo);
	    }

            if (oclcUserInfo != null) {
                OtherInformation.addOIDandData(dir, oclcUserInfoOID,
                    oclcUserInfo, Z39searchApi.additionalSearchInfo);
            }

            if (rankQuery != null)
            {
                DataDir t = null;
 
                if (rankQuery instanceof DataDir)
                    t = (DataDir)rankQuery;
                else
                {
                    t = new DataDir(0, (byte)0);
		    if (query_type != 0) {
			if (!this.make_type_x((String)rankQuery, t,
			    Z39searchApi.type_101, zsession, true))
			    t = null;
		    } else if (query_type == 0) {
			if(zsession!=null && zsession.utf8Encode) 
			    t.addUTF(Z39searchApi.type_0, ASN1.CONTEXT, 
				     (String)rankQuery); 
			else 
			    t.add(Z39searchApi.type_0, ASN1.CONTEXT,  
			      (String)rankQuery); 
		    }
		    if (t != null)
			t = t.child();
                }
                if (t != null)
                    OtherInformation.addOIDandData(dir,
                        oclcUserInformation8.OID,
                        oclcUserInformation8.buildDir(rankOID, t),
                        Z39searchApi.additionalSearchInfo);
            }

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

        public static final boolean makeZ39AttributesPlusTerm(DataDir dir, String query, int query_type) {
            return make_type_x(query, dir, query_type, null, false);
        }
        public static final boolean makeZ39AttributesPlusTerm(DataDir dir, String query, int query_type, boolean addOIDData) {
            return make_type_x(query, dir, query_type, null, addOIDData);
        }
	private static boolean make_type_x(String query, DataDir dir, 
				      int query_type, Z39session zsession, boolean addOID) {

            // The allocation of the number of nodes will be greater than needed in
            // some cases, but this gives a good estimation!
	    StringTokenizer st = new StringTokenizer(query);
	    nodes node[] = new nodes[st.countTokens()];
	    int rc = 0;
	    DataDir parm = dir;

//System.out.println("make type x: num nodes: " + node.length);

	    if (node.length==0)
	    {
                if (zsession != null && zsession.logger != null && 
		    zsession.logger.getLevel() != Z39logging.OFF)
		    zsession.logger.println("No query provided!");
		return false;
	    }

  	    String q = query.trim();

  	    int i=0, j, o, len, nodecnt=0;

            try {
    	     for (o=0, len = q.length(); o < len; o=j+1) {
  	      if (q.charAt(o) == '"') {
		i = q.indexOf('"', o+1);
		if (i == -1)
		    i = o;
  	      }
	      else
		i = o;

 	      j = q.indexOf(' ', i);
 	      if (j == -1)
	        j = q.length();

              // If length > 0, add new node 
              if ( (j-o) > 0 ) {
                node[nodecnt] = new nodes(q.substring(o,j), query_type, zsession);

                if (zsession != null && zsession.logger != null && 
		    zsession.logger.getLevel() != Z39logging.OFF)
		    zsession.logger.println(
		    "node["+nodecnt+"].type=" + nodes.operators[node[nodecnt].type]+
		    ", .term='"+node[nodecnt].term+"'");

  	        //System.out.println("node["+nodecnt+"].type=" + nodes.operators[node[nodecnt].type]+
		    //", .term='"+node[nodecnt].term+"'");

                nodecnt++; 
             }
	   }
         }
         catch (Exception e) {
             System.out.println("caught exception from nodemaker");
             e.printStackTrace();
         }


//System.out.println("link nodes " + (nodecnt-1));


	    rc=nodes.linkNodes(node, nodecnt-1); //node.length-1);
	    if (rc != -1)
	    {
                if (zsession != null && zsession.logger != null && 
		    zsession.logger.getLevel() != Z39logging.OFF)
		    zsession.logger.println(
		    "Ill formed query, linkNodes returned " + rc);
		return false;
	    }

            if (zsession != null && zsession.logger != null && 
		zsession.logger.getLevel() != Z39logging.OFF)
		zsession.logger.println("building type-"+query_type+" query");

            if (addOID) {
  	      parm = dir.add(query_type, ASN1.CONTEXT);
	      parm.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, 
		"1.2.840.10003.3.1");
            }
	    node[nodecnt-1].buildQuery(parm);

	    return true;
	}

	// this guy will need to be able to return a failure.
	// does he do it by throwing an exception?
	// some of the DataDir constructors are in the same boat.
	public void Response(BerString response) throws AccessControl {
	    DataDir rspdir = new DataDir(response);
	    Response(rspdir);
	}

	public void Response(DataDir response) throws AccessControl {
	    DataDir parm = null, subparm = null;

            if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
              synchronized (zsession.logger) {
		zsession.logger.println(
		"SEARCH Response: " + response.toString());
              }


	    // if fldid != Z39api.searchResponse - throw exception
	    // Z39api.logit
            if (response.fldid() == Z39api.accessControlRequest) {
               accessControl = new AccessControl(response, this);
               throw accessControl;
            }

	    errorCode = 0;
	    errorMsg = null;
	    Present = new Z39present(zsession);
	    Present.Response(response);
	    dbResults = null;
	    componentResults = null;
            responseLength = response.recLen();

	    for (parm=response.child();parm != null; parm = parm.next())
	    {
//System.out.println(parm.toString());
		switch (parm.fldid())
		{
		    case Z39api.ReferenceId:
			referenceId = parm.getInt();
			break;
		    case Z39searchApi.resultCount:
			resultCount = parm.getInt();
			break;
		    case Z39searchApi.searchStatus:
			searchStatus = parm.getInt();
			break;
		    case Z39searchApi.resultSetStatus:
			resultSetStatus = parm.getInt();
			break;
		    case Z39searchApi.additionalSearchInfo:
		   	DataDir data = OtherInformation.getData(parm, 
			    oclcUserInformation3.OID);
			if (data != null)
			{
			    oclcUserInformation3 o3 = 
			 	new oclcUserInformation3(data);
			    dbResults = o3.dbResults;
                            componentResults = o3.componentResults;
			    o3 = null;
			}
                        else { // Attempt to get the FirstSearch CompPost 
                          data = OtherInformation.getData(parm, 
                             oclcUserInformation6.responseOID);
                          if (data != null) {
                             oclcUserInformation6 o6 = 
                                new oclcUserInformation6(data);
			     dbResults = o6.dbResults;
                             componentResults = o6.componentResults;
                             o6 = null;
                          }
                        }

                        data = OtherInformation.getData(parm,
                            oclcUserInformation7.OID);
                        if (data != null)
                            oclc7 = new oclcUserInformation7(data);
 
                        data = OtherInformation.getData(parm,
                            oclcUserInformation8.OID);
                        if (data != null)
                            oclc8 = new oclcUserInformation8(data);

                        break;
		    case Z39api.nonSurrogateDiagnostic:
			for (subparm=parm.child(); subparm != null; 
			     subparm=subparm.next())
			    switch(subparm.fldid())
			    {
				case ASN1.INTEGER:
				    errorCode = subparm.getInt();
				    break;
				case ASN1.VISIBLESTRING:
				    errorMsg = subparm.getString();
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
	    StringBuffer str = new StringBuffer(
			"Z39search: referenceId(" + referenceId +
			")\nresultCount(" + resultCount +
			")\nsearchStatus(" + searchStatus +
			")\nresultSetStatus(" + resultSetStatus + 
			")\nrequestLength(" + requestLength +
			")\nresponseLength(" + responseLength +
			")\nerrorCode(" + errorCode +
			")\nerrorMsg(" + errorMsg + ")\n");

            if (componentResults != null) {
  	      for (int i=0; i<componentResults.length; i++)
	 	str.append("componentResults[" + i + "]:\n" + componentResults[i] + "\n");
              str.append("-------------------------\n"); 
            }

            if (dbResults != null) {
  	      for (int i=0; dbResults != null && i<dbResults.length; i++)
	 	str.append("dbResults[" + i + "]:\n" + dbResults[i] + "\n");
              str.append("-------------------------\n"); 
            }
            if (oclc7 != null)
                str.append(oclc7);
 
            if (oclc8 != null)
                str.append(oclc8);

	    if (Present != null)
	        str.append(Present.toString()); 

	    return str.toString();
	}

/**
  * Reset response values for re-use of object
  *
  */
  
        private void reset() {
  	 resultCount =0;
	 searchStatus = 0;
	 resultSetStatus = 0;
	 errorCode = 0;
	 errorMsg = "None provided";
	 Present = null;
	 dbResults = null;
         oclc7= null;
         oclc8 = null;
         requestLength=0;
         responseLength=0;
       }

}
