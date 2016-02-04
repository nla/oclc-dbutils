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
import ORG.oclc.z39.MarcDecoder;
import ORG.oclc.util.Util;

/** Z39present creates PresentRequests and translates PresentResponses.
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class Z39present {
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;
/**
 * String data type containing the preferredRecordSyntax in the request 
 */
       public String preferredRecordSyntax;
/**
 * Number of records returned in Response.
 */
	public int numberOfRecordsReturned;
/**
 * Starting point for next PresentRequest.
 */
	public int nextResultsSetPosition;
/**
 * Status. 
 */
	public int presentStatus;
/**
 * Error code if Present failed.
 */
	public int errorCode;
/**
 * Error message if Present failed.
 */
	public String errorMsg = "None provided";
/**
 * Syntax of returned records.
 */
	public String recordSyntax[];
/**
 * Array of records returned in Response.
 */
	public Object records[];
/**
 * Array of duplicate records associated with the records.
 */
	public Object duplicateRecords[][];
/**
 * Array of duplicate records dbnames associated with the records.
 */
	public String duplicateRecordsDbnames[][];
/**
 * Syntax of duplicate returned records.
 */
	public String duplicateRecordsSyntax[][];

/**
 * Array of source database names for each record returned in Response.
 */
	public String dbnames[];

/**
  * Vector of DbPresentData objects containing the database names and source records
  * returned in the present response.
  */
        public Vector presentData;
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
	Z39session zsession=null;

	public Z39present() {
           numberOfRecordsReturned=0;
	}

	public Z39present(Z39session z) {
	    zsession = z;
            numberOfRecordsReturned=0;
        }

        // send and process a PresentRequest 
/**
 * Creates, sends and proceeses a Z39.50 PresentRequest until the number of Records
 * requested is returned or there is a failure 
 * @param referenceId Will be returned on Response.
 * @param resultSetName Identifies the results from a SearchRequest.
 * @param resultSetStartPoint Relative number of first record to Present.
 * @param numberOfRecordsRequested Number of records to return.
 * @param resultSetEndPoint number of postings Plus the relative number of the first record to present
 * @param ElementSetNames Requested data in returned records.
 * @param preferredRecordSyntax Requested syntax of returned records.
 * @param fMakeDataDir flag to indicate whether decode to decode record
 *        data into a DataDir which fills in the presentData Vector
 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 diagnostics 
 */

        public void doPresent(int referenceId, 
                String resultSetName,
		int resultSetStartPoint, int numberOfRecordsRequested,
                int resultSetEndPoint, String ElementSetNames,
                String preferredRecordSyntax, 
                boolean fMakeDataDir) throws Exception, Diagnostic1 {
 
          BerString zRequest = null, zResponse = null; 
          BerConnect zConnection;
          int askedForRecs;
          int numReturned=0;
          int saveOffset=0;
          int startRecno = resultSetStartPoint;
          int endRecno = startRecno + numberOfRecordsRequested - 1;
          
          Object saveRecs[] = null;
          String saveSyntax[] = null;
          int i;


          if (endRecno > resultSetEndPoint) {
            numberOfRecordsRequested = resultSetEndPoint - resultSetStartPoint + 1; 
            endRecno = resultSetEndPoint;
          }
          askedForRecs = numberOfRecordsRequested; 

	  //System.out.println("Startpresent: start: " + resultSetStartPoint);
	  //System.out.println("numrequest: " + numberOfRecordsRequested);
	  //System.out.println("postings: " + resultSetEndPoint);
	  //System.out.println("endrecno: " + endRecno);
	  //System.out.println("askedForRecs: " + askedForRecs);

          if (fMakeDataDir) {
            presentData = new Vector(numberOfRecordsRequested);
          }
          else {
            saveRecs = new Object[numberOfRecordsRequested];
            saveSyntax = new String[numberOfRecordsRequested];
	  }
          

        
          if (zsession == null)
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                      "User's Z39.50 session is not initialized");


          if (zsession.isConnected() == false) 
             throw new Diagnostic1(Diagnostic1.databaseUnavailable,
                      "Unable to connect to the Z39.50 server");

          zConnection = (BerConnect)zsession.connection;

          while (numReturned < askedForRecs &&
                    numberOfRecordsRequested > 0) {

             // Make sure we don't go past the end
             if (resultSetStartPoint+numReturned+numberOfRecordsRequested >
                     endRecno + 1)
               numberOfRecordsRequested = endRecno - 
                                 (resultSetStartPoint + numReturned);

	     //System.out.println("Present: askFor: " + numberOfRecordsRequested + 
	     //  " start: " + startRecno + " endpoint: " + resultSetEndPoint);
	     //System.out.println("numrequest: " + numberOfRecordsRequested);
	     //System.out.println("postings: " + resultSetEndPoint);
	     //System.out.println("endrecno: " + endRecno);
	     //System.out.println("askedForRecs: " + askedForRecs);


             zRequest = Request(referenceId, resultSetName, startRecno,
                           numberOfRecordsRequested, ElementSetNames, 
                           preferredRecordSyntax, 0, 0);
             if (zRequest == null) {
               throw new Diagnostic1(Diagnostic1.systemErrorPresentingRecords,
                   "Unable to create present request");
             }

             try { zResponse = zConnection.doRequest(zRequest); }
             catch (InterruptedIOException ioe) {
		 if (zsession.doTRC) {   
		     try { 
			 if (zsession.logger != null &&  
			     zsession.logger.getLevel() != Z39logging.OFF) { 
			     synchronized (zsession.logger) { 
				 zsession.logger.println("Sending TRC to present"); 
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
             catch (Exception n) {
                zsession.reset();
                throw new Diagnostic1(Diagnostic1.databaseUnavailable,
                  "Unable to send request to the Z39.50 server");
             }


             if (zResponse == null) {
               throw new Diagnostic1(Diagnostic1.temporarySystemError,
                  "Invalid present response received from the Z39.50 Server");
             }
           
             Response(zResponse);
             if (this.presentStatus == Z39presentApi.failure) {
               this.numberOfRecordsReturned = 0;
               this.records = null;
               throw new Diagnostic1(errorCode, errorMsg);

             }

	     //System.out.println("got back " + this.numberOfRecordsReturned);

             numReturned += this.numberOfRecordsReturned;
             startRecno += this.numberOfRecordsReturned;
             numberOfRecordsRequested -= this.numberOfRecordsReturned;
             
             // Hold onto the records until all retrieved
             if (fMakeDataDir) {
                DataDir d;
                DbPresentData dataItem;
                for (int ii=0; ii<this.numberOfRecordsReturned; ii++) { 
                   dataItem = null;
                   d = decodeBerStringData(records[ii], 
                     recordSyntax[ii] != null ? recordSyntax[ii] : 
                     preferredRecordSyntax);
                   if (d != null) {
                      dataItem = new DbPresentData(dbnames[ii], d);
		      dataItem.recordSyntax = recordSyntax[ii];
                      presentData.addElement(dataItem);
                      
                   }
                   if (duplicateRecords[ii] != null && dataItem != null) {
                      for (int jj=0; jj<duplicateRecords[ii].length; jj++) {
                        d = decodeBerStringData(duplicateRecords[ii][jj], 
                       duplicateRecordsSyntax[ii][jj] != null ?
                       duplicateRecordsSyntax[ii][jj] : preferredRecordSyntax);
                        if (d !=null) {
                          dataItem.saveDuplicate(new 
                            DbPresentData(duplicateRecordsDbnames[ii][jj], 
                            d));
                        }
                      }
                   }
                }
             }
             else {
               System.arraycopy(this.records, 0, saveRecs, 
                 saveOffset, this.numberOfRecordsReturned);
               System.arraycopy(this.recordSyntax, 0, saveSyntax,
                 saveOffset, this.numberOfRecordsReturned);
               saveOffset += this.numberOfRecordsReturned;
             }

          }

          if (!fMakeDataDir) {
            this.numberOfRecordsReturned = saveOffset;
            this.records = saveRecs;
            this.recordSyntax = saveSyntax;
          }
          else {
            presentData.trimToSize();
            this.numberOfRecordsReturned = presentData.size();
            this.records = null;
          }

          return;
        }

/**
 * Creates a Z39.50 PresentRequest.
 * @param referenceId Will be returned on Response.
 * @param resultSetName Identifies the results from a SearchRequest.
 * @param resultSetStartPoint Relative number of first record to Present.
 * @param numberOfRecordsRequested Number of records to return.
 * @param ElementSetNames Requested data in returned records.
 * @param preferredRecordSyntax Requested syntax of returned records.
 * @return BerString containing Request or null if space was unavailable
 */
      public BerString Request(int referenceId, String resultSetName,
		int resultSetStartPoint, int numberOfRecordsRequested,
		String ElementSetNames, String preferredRecordSyntax) {

	    return (Request(referenceId, resultSetName,
		resultSetStartPoint, numberOfRecordsRequested,
		ElementSetNames, preferredRecordSyntax, 0, 0));

         }

/**
 * Creates a Z39.50 PresentRequest.
 * @param referenceId Will be returned on Response.
 * @param resultSetName Identifies the results from a SearchRequest.
 * @param resultSetStartPoint Relative number of first record to Present.
 * @param numberOfRecordsRequested Number of records to return.
 * @param ElementSetNames Requested data in returned records.
 * @param preferredRecordSyntax Requested syntax of returned records.
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, String resultSetName,
		int resultSetStartPoint, int numberOfRecordsRequested,
		String ElementSetNames, String preferredRecordSyntax,
		int extraLen, int offset) {
            

            if (numberOfRecordsRequested == 0)
              return null;

            
	    DataDir subdir = null;

	    DataDir dir = new DataDir(Z39api.presentRequest, (int)ASN1.CONTEXT);
            if (referenceId != 0)
                dir.add(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);

	    dir.add(Z39api.ResultSetId, ASN1.CONTEXT, resultSetName);
	    dir.add(Z39presentApi.resultSetStartPoint, ASN1.CONTEXT, 
		resultSetStartPoint);
	    dir.add(Z39presentApi.numberOfRecordsRequested, ASN1.CONTEXT,
		numberOfRecordsRequested);
	    if (ElementSetNames != null && ElementSetNames.length() > 0)
	    {
		subdir = dir.add(Z39presentApi.ElementSetNames, ASN1.CONTEXT);
		subdir.add(Z39presentApi.genericElementSetName, ASN1.CONTEXT, 
		    ElementSetNames);
	    }

	    if (preferredRecordSyntax == null)
              this.preferredRecordSyntax = Z39presentApi.MARC_SYNTAX;
            else
              this.preferredRecordSyntax = preferredRecordSyntax;

            dir.addOID(Z39presentApi.PreferredRecordSyntax, ASN1.CONTEXT,
			this.preferredRecordSyntax);

            if (zsession != null && zsession.sessionId != null)
            {
                OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
            }


	    if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
              synchronized(zsession.logger) {
		zsession.logger.println("PRESENT REQUEST: " + dir.toString());
              } 

            requestLength = dir.recLen() + extraLen;

	    if (extraLen != 0 || offset != 0)
                return new BerString(dir, extraLen, offset);
            else
                return new BerString(dir);
	}

/**
 * Processes a Z39.50 PresentResponse.
 * @param response BerString containing response.
 */
	public void Response(BerString response) {
	    DataDir rspdir = new DataDir(response);
	    Response(rspdir);
	}

/**
 * Processes a Z39.50 PresentResponse.
 * @param response DataDir containing response
 */
	public void Response(DataDir response) {
	    DataDir parm = null, recdir = null, subparm = null;
	    int nRecs=0;

	    if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
              synchronized(zsession.logger) {
		zsession.logger.println(
		"PRESENT Response: "+response.toString());
              }

	    errorCode = 0;
	    errorMsg = null;
	    numberOfRecordsReturned = 0;
            responseLength = response.recLen();

	    for (parm=response.child();parm != null; parm = parm.next())
	    {

		switch (parm.fldid())
		{
		    case Z39api.ReferenceId:
			referenceId = parm.getInt();
			break;
		    case Z39presentApi.NumberOfRecordsReturned:
			numberOfRecordsReturned = parm.getInt();
			if (numberOfRecordsReturned > 0) {
			    records = new Object[numberOfRecordsReturned];
                            dbnames = new String[numberOfRecordsReturned];
                            recordSyntax = new String[numberOfRecordsReturned]; 
                            duplicateRecords = new Object[numberOfRecordsReturned][];
                            duplicateRecordsDbnames = new String[numberOfRecordsReturned][];
                            duplicateRecordsSyntax = 
                              new String[numberOfRecordsReturned][];
                        }
			break;
		    case Z39presentApi.PresentStatus:
			presentStatus = parm.getInt();
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

	    // handle records after all other parms.
	    for (parm=response.child();parm != null; parm = parm.next())
	    {
		switch (parm.fldid())
		{
		    case Z39presentApi.dataBaseOrSurDiagnostics:
			if (parm.child() == null ||
			    parm.child().fldid() != ASN1.SEQUENCE)
			{
			    // Z39api.log it
			    if (zsession.logger != null && 
				zsession.logger.getLevel() != Z39logging.OFF)
				zsession.logger.println(
				    "Ill formed dataBaseOrSurDiagnostic");
			    // return null - throw an exception
			}
                        else {
			  for (recdir=parm.child(), nRecs=0;
			     recdir != null; 
                               recdir=recdir.next()) {
			      if (GetRecord(records, dbnames, recordSyntax, nRecs, recdir))
                                nRecs++;

                          }
                        }
			break;
		}
	    }
	}

	private boolean GetRecord(Object savedRecs[], 
            String savedDbnames[], String savedSyntax[],
            int nRecs, DataDir recdir) {

	    DataDir subparm = recdir.child();

	    if (subparm == null)
	    {
		if (zsession.logger != null && 
		    zsession.logger.getLevel() != Z39logging.OFF)
		    zsession.logger.println(
		    "Ill formed dataBaseOrSurDiagnostic");
		return false;
	    }

            // database name
            
	    if (subparm.fldid() == Z39presentApi.name) {
                savedDbnames[nRecs] = subparm.getString();
		subparm = subparm.next();
            }

	    if (subparm.fldid() == Z39presentApi.databaseRecord) // some kind of record
	    {
		subparm = subparm.child();
		if (subparm.fldid() == Z39presentApi.databaseRecord)
		{
		    subparm = subparm.child();
		    if (subparm == null || subparm.fldid() != ASN1.EXTERNAL) 
		    {
			if (zsession.logger != null && 
			    zsession.logger.getLevel() != Z39logging.OFF)
			    zsession.logger.println("Ill formed Record EXTERNAL");
		 	return false;
		    }
		    subparm = subparm.child();
		    if (subparm == null)
		    {
			if (zsession.logger != null && 
			    zsession.logger.getLevel() != Z39logging.OFF) 
			    zsession.logger.println("Nothing inside the Record EXTERNAL");
		 	return false;
		    }
		    if (subparm.fldid() == ASN1.OBJECTIDENTIFIER)
		    {
			savedSyntax[nRecs] = subparm.getOID();
                        subparm = subparm.next();

		    }

		    if (subparm == null)
		    {
			if (zsession.logger != null &&
			    zsession.logger.getLevel() != Z39logging.OFF)
			    zsession.logger.println("No encoding choice in record!");
		 	return false;
		    }
     
                    if (savedSyntax[nRecs].equals(Z39presentApi.OCLC_CLUSTER_SYNTAX)) {
                          int i=0;
                          for (DataDir dupdir=subparm;
                                    dupdir != null; 
                                    dupdir=dupdir.next())
                             if (dupdir.fldid() == Z39presentApi.duplicateRecord)
                               i++;

                          if (i>0) {
                            duplicateRecords[nRecs] = 
                                          new Object[i];
                            duplicateRecordsDbnames[nRecs] = 
                                          new String[i];
                            duplicateRecordsSyntax[nRecs] = 
                                          new String[i];
                          }
                          i=0;
                          for (DataDir dupdir=subparm;
                                    dupdir != null; 
                                    dupdir=dupdir.next(), i++) {
                            if (dupdir.fldid() == 
                               Z39presentApi.representativeRecord) // main record
                                GetRecord(savedRecs,savedDbnames,savedSyntax,
                                           nRecs,dupdir.child());
                            else 
                             if (dupdir.fldid() ==Z39presentApi.duplicateRecord)
                                GetRecord(duplicateRecords[nRecs], 
                                   duplicateRecordsDbnames[nRecs], 
                                   duplicateRecordsSyntax[nRecs],
                                   i-1, dupdir.child());
                            
                         }
                    }
                    else {
                      // Skip around UW Madison bug
                      if (subparm.fldid() == ASN1.INTEGER)
                          subparm = subparm.next();

	  	      if (subparm.fldid() == ASN1.single_ASN1_type)
		      {
			  savedRecs[nRecs] = new BerString(subparm.child());

		      }
		      else
			if (subparm.fldid()==ASN1.octet_aligned)
			{
			    if (savedSyntax[nRecs].equals(Z39presentApi.SIMPLETEXT_SYNTAX))
			      savedRecs[nRecs]=new BerString(subparm);
			    else
			      savedRecs[nRecs]=new BerString(subparm.data());

			}
			else
			{
			    if (zsession.logger != null &&
				zsession.logger.getLevel() != Z39logging.OFF)
				zsession.logger.println(
				    "Illegal encoding choice in record!");
			    return false;
			}
		    }
                }
                else {
                  int code = 0;
                  String msg = null;
                  for (subparm=subparm.child().child();subparm!=null;
                    subparm=subparm.next())
                     switch(subparm.fldid())
                     {
                       case ASN1.INTEGER:
                          code = subparm.getInt();
                          break;
                       case ASN1.VISIBLESTRING:
                          msg = subparm.getString();
                          break;
                   }
                   savedRecs[nRecs] = new Diagnostic1(code, msg);
                }
            }
	    else // surrogateDiagnostic
	    {
              //I believe this is misplaced, but I am leaving
              // to avoid problems if deleted.. LAC
		for (subparm=subparm.child();subparm!=null;
		     subparm=subparm.next()) {
		    switch(subparm.fldid())
                    {
                        case ASN1.INTEGER: 
                            errorCode = subparm.getInt();
                            break;
                        case ASN1.VISIBLESTRING:
			    errorMsg = subparm.getString();
                            break;
                    }
		}
		savedRecs[nRecs] = new Diagnostic1(errorCode, errorMsg);
	    }

	    return true;
	}

	public String toString() {
            String newLine = System.getProperty("line.separator");
	    StringBuffer str = new StringBuffer(
		"Z39present: referenceId(" + referenceId +
		")" + newLine + "numberOfRecordsReturned(" + numberOfRecordsReturned +
		")" + newLine + "nextResultsSetPosition(" + nextResultsSetPosition +
		")" + newLine + "presentStatus(" + presentStatus +
		")" + newLine + "errorCode(" + errorCode +
		")" + newLine + "errorMsg(" + errorMsg +
		")" + newLine + "preferredRecordSyntax(" + preferredRecordSyntax + 
		")" + newLine + "requestLength(" + requestLength +
		")" + newLine + "responseLength(" + responseLength +
		")" + newLine + "Records:" + newLine);
	    return str.toString();
	}

        public boolean decodeRecords(){
           int i, j;
           DataDir dir; 
           DbPresentData dataItem;
          if (numberOfRecordsReturned == 0 || records == null) {
             return false;
          }
           presentData = new Vector(numberOfRecordsReturned);

           for (i=0; i<numberOfRecordsReturned; i++) {
             dataItem=null;
             if (records[i] != null) {
               dir = decodeBerStringData(records[i], 
                recordSyntax[i] != null ? recordSyntax[i]:preferredRecordSyntax);
               if (dir != null) {
                  dataItem = new DbPresentData(dbnames[i], dir);
		  dataItem.recordSyntax = recordSyntax[i];
                  presentData.addElement(dataItem);
               }
               if (duplicateRecords[i] != null && dataItem != null) {
                 for (j=0; j<duplicateRecords[i].length; j++) {
                    dir = decodeBerStringData(duplicateRecords[i][j], 
                        duplicateRecordsSyntax[i][j] != null ?
                        duplicateRecordsSyntax[i][j] : preferredRecordSyntax);
                    if (dir !=null) {
                      dataItem.saveDuplicate(new 
                       DbPresentData(duplicateRecordsDbnames[i][j], dir));
                    }
                 }
               }
             }

           }
           return true;
        }

  /**
   * Decodes an input BerString data object into a DataDir object according to the 
   * input Z39.50 record syntax of the record.
   * @param record the BerString object.
   * @param recordSyntax the record syntax of the BerString object.
   * @return DataDir
   */
      public static final DataDir decodeBerStringData(Object record, 
           String recordSyntax) {
          DataDir dir = null;
          boolean tryopac = false;
          if (record == null || (recordSyntax == null &&
				 ! (record instanceof Diagnostic1))) 
            return null;

          if (record instanceof Diagnostic1) {
             dir = new DataDir(1, (int)ASN1.APPLICATION);
             DataDir sdir = dir.add(16, ASN1.CONTEXT);
             ((Diagnostic1)record).addDefaultDiagFormat(sdir);
             return dir;
          }
          BerString berString = (BerString)record;

          if (recordSyntax.equals(Z39presentApi.MARC_SYNTAX) ||
              recordSyntax.equals(Z39presentApi.UNIMARC_SYNTAX) ||
              recordSyntax.equals(Z39presentApi.UKMARC_SYNTAX) ||
	      recordSyntax.equals(Z39presentApi.NORMARC_SYNTAX))
          {
             dir = new DataDir(0, (int)ASN1.APPLICATION);

             if ( ! MarcDecoder.marc2dir((new String(berString.record())), 
                dir, false))
               tryopac = true;
          }

          if (recordSyntax.equals(Z39presentApi.EXPLAIN_SYNTAX))
          {
             return null;
          }
          else if (recordSyntax.equals(Z39presentApi.OCLC_BER_SYNTAX) ||
                   recordSyntax.equals(Z39presentApi.GRS1_SYNTAX ) ||
                   recordSyntax.equals(Z39presentApi.DRAHOLDINGS_SYNTAX)) {
            dir = new DataDir(berString);
          }
          else if (recordSyntax.equals(Z39presentApi.SIMPLETEXT_SYNTAX))
          {
            dir = new DataDir(0, (int)ASN1.APPLICATION);
	    //	    System.out.println(Util.hexDump(berString.record(),
	    //berString.record().length, "SUTRS data"));
            DataDir dd = new DataDir(berString);
            dir.add(1, (int)ASN1.CONTEXT, dd.data());

          }
          else if (tryopac || 
                 recordSyntax.equals(Z39presentApi.NOTIS_OPAC_SYNTAX) ||
                 recordSyntax.equals(Z39presentApi.OPAC_SYNTAX) ||
                 recordSyntax.equals(Z39presentApi.IBERMARC_SYNTAX))
          {
       
            DataDir tempDir = new DataDir(berString);

            // the definition of an OPAC record changed and an IMPLICIT SEQUENCE
            // was made explicit 
            if (tempDir.fldid() == ASN1.SEQUENCE)
              tempDir = tempDir.child();

            if (tempDir != null && tempDir.fldid() == 1)  // MARC record
            {
               DataDir subdir = tempDir.child();
       
               // the melvyl folks encoded this as an explicit external 
               if (subdir.fldid() == ASN1.EXTERNAL)
                 subdir = subdir.child();

               // skip the oid field in the external, if present */
               if (subdir != null)
               {
                 if(subdir.fldid() == ASN1.OBJECTIDENTIFIER)
                  subdir = subdir.next();

                 dir = new DataDir(0, (int)ASN1.APPLICATION);

                 if (MarcDecoder.marc2dir((new String(subdir.data())), dir,
                        false) == true)
                   tempDir = tempDir.next();
                 else // marc2dir failed
                 {
                   return null;
                 }
               }
               else // subdir == null
               {
                 return null;
               }
            }

            if (tempDir != null && tempDir.fldid() == 2)  // Holdings data
            {
              DataDir subdir = null;
       
              tempDir = tempDir.child();
              while (tempDir != null)
              {
                subdir = tempDir;
                if (subdir.fldid() == 1)  // MARC holdings record 
                {
                   DataDir holdings = dir.add(1001, ASN1.CONTEXT);
            
                   if (subdir.child() != null && subdir.child().next() != null)
                   {
                      subdir = subdir.child().next();
                      if (MarcDecoder.marc2dir((new String(subdir.data())), holdings,
                              true) == false);
                      {
                        return null;
                      }
                   }
                }
                else  // Holdings and Circ data
                {
                   DataDir holdings = dir.add(1002, ASN1.CONTEXT);
                   DataDir hdir;
                   // NOTIS, bless their hearts, seem to think that the
                   // holdings info begins with a couple of SEQUENCE
                   // tags.  That would be wrong, but this gets around it
                   while(subdir != null && subdir.child() != null && 
                        subdir.child().fldid() == ASN1.SEQUENCE)
                      subdir = subdir.child();

                   // this is to force the end of the while loop in 
                   // case subdir is still equal to tempDir because */
                   // all the holdings will get added right now !   */
                   if (subdir == tempDir)
                     tempDir = null;

                   // loop thru all the holdings nodes 
                   for (; subdir != null; subdir = subdir.next())
                   {
                      // the next 2 lines attaches the holdings info from the
                      // opac record to the end of the marc record. 
                      if (subdir.child() != null)
                      {
                         hdir = holdings.add(ASN1.single_ASN1_type, 
			     ASN1.CONTEXT);
                         DataDir newDir = (DataDir) subdir.clone(false);
                         hdir.add(newDir);
                     }
                  }
               } // holdings & circ
               if (tempDir != null)
                 tempDir = tempDir.next();
           }
        }  // end Holdings  

        // There is a bug in the III encoding of some OPAC Syntax records...  
        // OPAC is supposed to have a datadir around the data and some of  
        // the records are just straight marc, no BER around it.  if we  
        // got this far and there is no record saved, just try and see if 
        // the marc decoder works on it.  
        if (dir == null) {  
          dir = new DataDir(0, (int)ASN1.APPLICATION); 
 
          if (!MarcDecoder.marc2dir((new String(berString.record())),
                                    dir,false)) {
             dir = new DataDir(1, (int)ASN1.APPLICATION);
             DataDir sdir = dir.add(16, ASN1.CONTEXT);
             Diagnostic1 ds = 
              new Diagnostic1(Diagnostic1.systemErrorPresentingRecords,
                Diagnostic1.msg(Diagnostic1.systemErrorPresentingRecords));
             ds.addDefaultDiagFormat(sdir);
          }
        }   

     }


     return dir; // what was created.

    }

/**
  * Reset response values for re-use of object
  *
  */
  private void reset() {
	numberOfRecordsReturned = 0;
	nextResultsSetPosition = 0;
	presentStatus = 0;
	errorCode = 0;
	errorMsg = "None provided";
        requestLength = 0;
        responseLength = 0;
  } 

}








