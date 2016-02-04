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

/** Z39scan creates ScanRequests and translates ScanResponses.
 * @version @(#)Z39scan.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39scan { 
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;
/**
 * Number of terms returned.
 */
	public int numberOfEntriesReturned;
/**
 * Status.
 */
	public int scanStatus;
/**
 * Error code if Scan failed.
 */
	public int errorCode;
/**
 * Error message if Scan failed.
 */
	public String errorMsg;
/**
 * Scan results
 */
	public TermInfo terms[];
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

	public Z39scan() {
	}

	public Z39scan(Z39session z) {
	    zsession = z;
	}

/**
 * Creates and sends  a Z39.50 ScanRequest, processes a Z39.50 Scan Response
 * @param referenceId Will be returned on Response.
 * @param databaseName Database to be Scanned.
 * @param term Starting point for Scan.
 * @param stepSize 'N' means Newton indexes ???.
 * @param numberOfTermsRequested How many terms to return.
 * @param preferredPositionInResponse Where the Scan term should appear in the
 * list.
 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 Diagnostics
 * @exception AccessControl exception when server issues AccessControl request
 */
	public void doScan(int referenceId, String databaseName,
            String term, String resultsAttributes,
	    int stepSize, int numberOfTermsRequested, 
	    int preferredPositionInResponse) 
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

          zRequest = Request(referenceId, databaseName,
			    term, resultsAttributes,
			    stepSize, numberOfTermsRequested, 
			    preferredPositionInResponse, 0, 0);

          if (zRequest == null) {
             throw new Diagnostic1(Diagnostic1.malformedScan, "Unable to create scan request");
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
                               zsession.logger.println("Sending TRC to scan"); 
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
           catch (Exception e2) {
             zsession.reset(); 
             throw new Diagnostic1(Diagnostic1.databaseUnavailable,
                  "Unable to send request to the Z39.50 server");
           }

           if (zResponse == null) {
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                  "Invalid scan response received from the Z39.50 Server");
           }

           Response(zResponse);

        }

/**
 * Creates a Z39.50 ScanRequest.
 * @param referenceId Will be returned on Response.
 * @param databaseName Database to be Scanned.
 * @param term Starting point for Scan.
 * @param stepSize 'N' means Newton indexes ???.
 * @param numberOfTermsRequested How many terms to return.
 * @param preferredPositionInResponse Where the Scan term should appear in the
 * list.
 * @return BerString containing Request or null if space was unavailable.
 */
	public BerString Request(int referenceId, String databaseName,
			         String term, String resultsAttributes,
				 int stepSize, int numberOfTermsRequested, 
			         int preferredPositionInResponse) {
           return Request(referenceId, databaseName,
			    term, resultsAttributes,
			    stepSize, numberOfTermsRequested, 
			    preferredPositionInResponse, 0, 0);
        }

/**
 * Creates a Z39.50 ScanRequest.
 * @param referenceId Will be returned on Response.
 * @param databaseName Database to be Scanned.
 * @param term Starting point for Scan.
 * @param stepSize 'N' means Newton indexes ???.
 * @param numberOfTermsRequested How many terms to return.
 * @param preferredPositionInResponse Where the Scan term should appear in the
 * list.
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString.
 * @return BerString containing Request or null if space was unavailable.
 */
	public BerString Request(int referenceId, String databaseName,
			         String term, String resultsAttributes,
				 int stepSize, int numberOfTermsRequested, 
			         int preferredPositionInResponse,
				 int extraLen, int offset)
	{
	    DataDir parm, subparm;
	    boolean fNewton = false;
	    int     type;
	    StringTokenizer st;
//System.out.println("Scan Request start: dbname: " + databaseName + 
//" term: '" + term + "' resultsAttrib: '" + resultsAttributes + 
//"'  stepSize: " + stepSize + " numterms: " + numberOfTermsRequested + 
//" pos: " + preferredPositionInResponse);


            this.reset(); // Reset return values

	    if (stepSize == 'N') // cludgy newton indexid flag
	    {
		fNewton = true;
		stepSize = 1;
	    }

	    // build a z39.50 scan request

	    DataDir dir = new DataDir(Z39api.scanRequest, ASN1.CONTEXT);
	    if (referenceId != 0)
		dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);
 
	    parm = dir.daddTag(Z39scanApi.databaseId, ASN1.CONTEXT);
	    st = new StringTokenizer(databaseName);
	    while (st.hasMoreTokens())
	        parm.daddChar(Z39api.DatabaseName, ASN1.CONTEXT, st.nextToken());

	    parm = dir.daddTag(Z39api.AttributesPlusTerm, ASN1.CONTEXT);
            subparm = parm.daddTag(Z39api.AttributeList, ASN1.CONTEXT);
	    if (!makeZ39AttributesPlusTerm(term, subparm, fNewton))
		subparm.ddelDir();
	    parm.daddChar(Z39api.generalTerm, ASN1.CONTEXT, getTerm(term));

	    dir.daddNum(Z39scanApi.stepSize, ASN1.CONTEXT, stepSize);
	    dir.daddNum(Z39scanApi.numberOfTermsRequested, ASN1.CONTEXT,
			numberOfTermsRequested);
	    dir.daddNum(Z39scanApi.preferredPositionInResponse, ASN1.CONTEXT,
			preferredPositionInResponse);

	    if (resultsAttributes != null)
	    {
//System.out.println("resultsAttributes is " + resultsAttributes);
		parm = OtherInformation.addOIDandData(dir, 
		    oclcUserInformation5.OID, null);
		subparm = OtherInformation.getOID(parm, oclcUserInformation5.OID);
		parm = subparm.parent().daddTag(
		    ASN1.single_ASN1_type, ASN1.CONTEXT);
		makeZ39AttributesPlusTerm(resultsAttributes, parm, fNewton);
	    }

            if (zsession != null && zsession.sessionId != null)
            {
                OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
            }
 


	    if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println("SCAN REQUEST: " + dir.toString());

            requestLength = dir.recLen() + extraLen;

	    if (extraLen != 0 || offset != 0)
                return new BerString(dir, extraLen, offset);
            else
                return new BerString(dir);
	}

	// this guy will need to be able to return a failure.
	// does he do it by throwing an exception?
	// some of the DataDir constructors are in the same boat.
/**
 * Processes a Z39.50 ScanResponse.
 * @param response BerString containing response.
 * @exception AccessControl exception when server issues AccessControl request
 */
	public void Response(BerString response) throws AccessControl {
	    DataDir rspdir = new DataDir(response);
	    Response(rspdir);
	}

/**
 * Processes a Z39.50 ScanResponse.
 * @param response DataDir containing response.
 * @exception AccessControl exception when server issues AccessControl request
 */
	public void Response(DataDir response) throws AccessControl {
	    DataDir parm = null, subparm = null, subsubparm = null;

	    if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println(
		"SCAN Response: " + response.toString());

	    // if fldid != Z39api.scanResponse - throw exception
	    // Z39api.logit
            if (response.fldid() == Z39api.accessControlRequest) {
              accessControl = new AccessControl(response, this);
              throw accessControl;
            }

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
		    case Z39scanApi.numberOfEntriesReturned:
			numberOfEntriesReturned = parm.dgetNum();
			break;
		    case Z39scanApi.scanStatus:
			scanStatus = parm.dgetNum();
			break;
		}
	    }

	    // process other things before processing terms
	    for (parm=response.child();parm != null; parm = parm.next())
	    {
		switch (parm.fldid())
		{
		    case Z39scanApi.entries:
		     subparm = parm.child();
                     if (subparm != null) {
			if (subparm.fldid()==Z39scanApi.Listentries) 
			{
			    if (numberOfEntriesReturned==0)
				break; // no entries
			    terms = new TermInfo[numberOfEntriesReturned];
			    this.GetScanTerms(subparm);
			}
			else // nonSurrogateDiagnostic
			{
			    errorMsg = "None provided";
			    for (subparm=subparm.child();subparm!=null;
				 subparm=subparm.next())
				for (subsubparm=subparm.child();
				     subsubparm!=null;
				     subsubparm=subsubparm.next())
				    switch(subsubparm.fldid())
				    {
				    case ASN1.INTEGER: 
					errorCode = subsubparm.dgetNum();
					break;
				    case ASN1.VISIBLESTRING: 
					errorMsg = subsubparm.dgetChar();
					break;
				    }
			  }
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

/**
 * Build term, displayTerm and postings arrays from ScanResponse.
 * @param parm ScanResponse Z39scanApi.entries field.
 */
        protected void GetScanTerms(DataDir parm) {
            DataDir s, subparm = null;
            int i, type, value;
            Vector theTerms = new Vector ();
            int noMore[] = new int[5];
            int noMoreCount=0;
 
            for (i=0, parm=parm.child(); parm != null; i++, parm=parm.next())
                if (parm.fldid() == Z39scanApi.termInfo)
                {
                    try { theTerms.addElement(new TermInfo(parm));}
//                    try { terms[i] = new TermInfo(parm); }
                    catch (Diagnostic1 d) {
                    }
                }
                else // surrogateDiagnostic
                {
                    numberOfEntriesReturned = 0;
                    subparm = parm.child();
                    if (subparm.fldid() != ASN1.SEQUENCE)
                        continue;
                    subparm=subparm.child().next();
                    if (subparm.dgetNum() == 103) // condition code
                    {
                        theTerms.addElement(new TermInfo("Gave up in this direction",
                            "Gave up in this direction"));
//System.out.println("setting no more....");
                        noMore[noMoreCount++] = i; 

                    }
                    else
                    {
//System.out.println("setting no morezzzz");

                        theTerms.addElement(new TermInfo(
                            "zzzzzzzzzNo more terms in this direction",
                            "No more terms in this direction"));
                        noMore[noMoreCount++] = i;
                    }
                }
            numberOfEntriesReturned = theTerms.size();
            terms = new TermInfo [numberOfEntriesReturned];
/*            for (i=0; i<noMoreCount; i++) {
              if (i==0 && terms.length > 1) {
                 if (terms[i+1].Term.length() > 1)
                terms[i].Term = terms[i+1].Term.substring(0, 
                                terms[i+1].Term.length()-1);
                 else
                   terms[i].Term = "No more terms in this direction";
              } 
              else if (i == terms.length-1 && terms.length > 1) {
                if (terms[i-1].Term.length() > 1) 
                  terms[i].Term = terms[i-1].Term + "zzzzzzzzz";
                else
                  terms[i].Term = "No more terms in this direction";
              }
            } 
System.out.println(termsToString); */
            theTerms.copyInto(terms);
        }

	public String toString() {
            String newLine = System.getProperty("line.separator");
	    StringBuffer str = new StringBuffer(
			"Z39scan: referenceId(" + referenceId +
			")" + newLine + "scanStatus(" + scanStatus +
			")" + newLine + "errorCode(" + errorCode +
			")" + newLine + "errorMsg(" + errorMsg +
			")" + newLine + "requestLength(" + requestLength +
			")" + newLine + "responseLength(" + responseLength +
			")" + newLine + "numberOfEntriesReturned(" +
			  numberOfEntriesReturned +
			")" + newLine + "Terms:" + newLine);
	    for (int i=0; i<numberOfEntriesReturned; i++)
		str.append(terms[i] + newLine);
	    return str.toString();
	}

/**
 * Format a newline separated String of terms and postings.
'united'('united') 17 (use=21;oluc:3;maps:9)
 */
	public String termsToString() {
            String newLine = System.getProperty("line.separator");
	    StringBuffer str = new StringBuffer();
	    for (int i=0; i<numberOfEntriesReturned; i++)
		if (terms[i].Term != null)
		{
		    if (terms[i].displayTerm != null)
			str.append("'" + terms[i].displayTerm + "'('" + 
			    terms[i].Term + "') ");
		    else
			str.append("'" + terms[i].Term + "' ");

		    str.append(terms[i].globalOccurrences);

		    if (terms[i].b != null)
		    {
			for (int k=0; k<terms[i].b.length; k++)
			{
			    str.append(" (");
			    str.append(terms[i].b[k]);
			    str.append(")");
			}
		    }
		    else if (terms[i].a != null)
		        str.append(" (use=" + terms[i].a[0].value + ")");

		    str.append(newLine);
		}

	    return str.toString();
	}

	protected String getTerm(String termPlusAttributes) {
	    int q, s;

	    if (termPlusAttributes.charAt(0) == '"')
	    {
		q = termPlusAttributes.indexOf('"',1); // look for ending quote
		if (q != -1)
		    return termPlusAttributes.substring(1,q);
	    }
	    s = termPlusAttributes.indexOf('/', 0);
	    if (s != -1)
		return termPlusAttributes.substring(0, s);
	    return termPlusAttributes;
	}

       public static boolean makeZ39AttributesPlusTerm(String termPlusAttributes,
                                      DataDir parent, boolean fNewton) {

	    int attrs, attrs1, attrs2, q, type, use = -1, structure= -1;

	    attrs = -1;
	    if (termPlusAttributes.charAt(0) == '"')
	    {
		q = termPlusAttributes.indexOf('"',1);
		if (q != -1)
		    attrs=termPlusAttributes.indexOf('/', q+1);
	    }
	    if (attrs == -1)
		attrs = termPlusAttributes.indexOf('/');
	    if (attrs == -1)
		return false;

	    // explicit use attributes (or newton indexes)
	    if (fNewton)
		type = Attribute.BIB1_newtonIds;
	    else
		type = Attribute.BIB1_use;


           attrs1 = termPlusAttributes.indexOf("u=", attrs);
           if (attrs1 != -1) {  // New way
              attrs1 += 2;
              attrs2 = termPlusAttributes.indexOf(";",attrs1);



              if(attrs2 != -1)   // use attribute found
                use = Integer.parseInt(termPlusAttributes.substring(attrs1, attrs2));
              else 
                use = Integer.parseInt(termPlusAttributes.substring(attrs1));

              attrs1 = termPlusAttributes.indexOf("s=", attrs);
              if (attrs1 != -1) {
                attrs1 += 2;
                attrs2 = termPlusAttributes.indexOf(";",attrs1);
                if(attrs2 != -1) 
                  structure = Integer.parseInt(termPlusAttributes.substring(attrs1, attrs2));
                else 
                  structure = Integer.parseInt(termPlusAttributes.substring(attrs1));
              }
            }
            else {
		if ( (attrs2 = termPlusAttributes.indexOf('/',attrs+1)) != -1) {
                  use = Integer.parseInt(termPlusAttributes.substring(attrs+1, attrs2));
                  structure = Integer.parseInt(termPlusAttributes.substring(attrs2+1));
                }
                else 
                  use = Integer.parseInt(termPlusAttributes.substring(attrs+1));

            }


	    if (!fNewton)
	    {
                if (use != -1) 
  		  parent.daddDir(Attribute.buildDir(type, use));
                if (structure != -1)
  		  parent.daddDir(Attribute.buildDir(Attribute.BIB1_structure,structure));
	    }
	    else if (use != -1)
		parent.daddDir(Attribute.buildDir(type, use));

	    return true;
	}

/*
 * Sorts the terms returned in the scan.  Some of the Z39.50 server return the  data sorted in ebcdic sort order and merging sort results all need them sorted in ascii sort order.
 * @param sort the array of returned terms
 */ 
 
	public void sortTerms(TermInfo sort[]) {
	    TermInfo temp;
	    int count;
	    int n;
	    n = 0; count = 1;

	    while (n < sort.length - 1 || count != 0) {
		count = 0;
		for (int j=0; j<sort.length-1; j++) {
		    if (sort[j].Term.compareTo(sort[j+1].Term) > 0)
		    {
			temp = sort[j];
			sort[j] = sort[j+1];
			sort[j+1] = temp;
			count++;
		    }
		}
		n++;
	    }
	}

/**
  * Reset response values for re-use of object
  *
  */
    private void reset() {
	numberOfEntriesReturned = 0;
	scanStatus = 0;
	errorCode = 0;
	errorMsg = "None provided";
	terms = null;
        requestLength = 0;
        responseLength = 0;
   }
}






