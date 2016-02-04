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

import java.util.*;
import ORG.oclc.ber.*;

/**
 * oclcUserInformation7 is used in Search requests. The client requests the
 * information by filling in the minimum, maximim and/or percent fields. The
 * response contains (for each database) the total records in the database
 * and the last record number examined.
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class oclcUserInformation7 {

/**
 * Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.7";

    // request
    private static final int MINIMUMRECORDSTORETRIEVE = 1;
    private static final int MAXIMUMRECORDSTORETRIEVE = 2;
    private static final int PERCENTTOCOMPLETE        = 3;
    // response
    private static final int TOTALINDATABASE          = 4;
    private static final int LASTRECNOEXAMINED        = 5;
    private static final int DBNAME                   = 6;
    private static final int RESPONSE                 = 7;

    private int lastExamined[], min, max, percent, total[];
    private String dbnames[];

/**
 * Pick apart a request or response.
 *
 * @param userInformationField the additional search info containing 
 * an oclcUserInformation7.
 */
    public oclcUserInformation7(DataDir userInformationField) {
	DataDir tmp, ttmp, seq;
	
//	System.out.println(userInformationField);

	if (userInformationField == null ||
	    userInformationField.fldid() != ASN1.single_ASN1_type) 
	    return;

        tmp = userInformationField.child();
        if (tmp == null || tmp.fldid() != ASN1.SEQUENCE)
            return;

        for (tmp = tmp.child(); tmp != null; tmp = tmp.next())
        {

            switch (tmp.fldid()) {
              case MINIMUMRECORDSTORETRIEVE: 
		min = tmp.getInt();
                break;
              case MAXIMUMRECORDSTORETRIEVE: 
		max = tmp.getInt();
                break;
              case PERCENTTOCOMPLETE: 
		percent = tmp.getInt();
                break;
	      case RESPONSE:
		  dbnames = new String[tmp.count()];
		  total = new int[tmp.count()];
		  lastExamined = new int[tmp.count()];
		  int i;

		  for (i = 0, seq = tmp.child(); 
		      seq != null && seq.fldid() == ASN1.SEQUENCE; 
		      seq = seq.next(), i++)
		  {
		      for (ttmp = seq.child(); ttmp != null; ttmp = ttmp.next())
			  switch (ttmp.fldid()) {
			      case TOTALINDATABASE:
				  total[i] = ttmp.getInt();
				  break;
			      case LASTRECNOEXAMINED:
		                  lastExamined[i] = ttmp.getInt();
				  break;
			      case DBNAME:
				  dbnames[i] = ttmp.getString();
				  break;
			  }
		  }
		  break;
            }
      }
    }

/**
 * Build a response.
 *
 * @param dbname name of database for this response
 * @param totalInDatabase number of records in database
 * @param lastExamined last record number examined for this search. 0 = all the 
 *        record numbers were examined, the search completed.
 */
    public oclcUserInformation7(String dbname, int totalInDatabase, 
	int lastExamined) {
	changeToResponse(dbname, totalInDatabase, lastExamined);
    }

    public oclcUserInformation7() {
    }

/**
 * Build a request.
 *
 * @param minimumRecordsToRetrieve the result set must have at least this
 *        many records
 * @param maximumRecordsToRetrieve the result set is satisfactory when it has
 *        this many records
 * @param percentToComplete the result set is satisfactory when this many 
 *        records have been examined. 
 */
    public oclcUserInformation7(int minimumRecordsToRetrieve,
	int maximumRecordsToRetrieve, int percentToComplete) {
	min = minimumRecordsToRetrieve;
	max = maximumRecordsToRetrieve;
	percent = percentToComplete;
    }

    public Object clone() {
	return new oclcUserInformation7(min, max, percent);
    }

/**
 * @return min
 */
    public int minimumRecordsToRetrieve() {
	return min;
    }

/**
 * @return max
 */
    public int maximumRecordsToRetrieve() {
	return max;
    }

/**
 * @return percent
 */
    public int percentToComplete() {
	return percent;
    }

/**
 * @return total
 */
    public int[] totalInDatabase() {
	return total;
    }

/**
 * @return lastExamined
 */
    public int[] lastRecnoExamined() {
	return lastExamined;
    }

/**
 * @return dbNames
 */
    public String[] dbNames() {
	return dbnames;
    }

/**
 * Add an additional set of response data to the current response.
 *
 * @param dbname name of database for this response
 * @param totalInDatabase number of records in database
 * @param lastExamined last record number examined for this search. 0 = all the 
 */
    public void addAnotherResponse(String dbname, int totalInDatabase,
	int lastExamined) {

	String t[] = new String[dbnames.length + 1];
	System.arraycopy(dbnames, 0, t, 0, dbnames.length);
	t[dbnames.length] = dbname;
	dbnames = t;

	int tt[] = new int[total.length + 1];
	System.arraycopy(total, 0, tt, 0, total.length);
	tt[total.length] = totalInDatabase;
	total = tt;

	tt = new int[this.lastExamined.length + 1];
	System.arraycopy(this.lastExamined, 0, tt, 0, this.lastExamined.length);
	tt[this.lastExamined.length] = lastExamined;
	this.lastExamined = tt;
    }

/**
 * Change the request to a response. 0's out the request fields and sets the
 * response fields.
 *
 * @param dbname names of databases in this response
 * @param totalInDatabase number of records in each database
 * @param lastExamined last record number examined for each database
 */
    public void changeToResponse(String dbname[], int totalInDatabase[], 
	int lastExamined[]) {

	min = max = percent = 0;

	dbnames = dbname;
	total = totalInDatabase;
	this.lastExamined = lastExamined;
    }

/**
 * Change the request to a response. 0's out the request fields and sets the
 * response fields.
 *
 * @param dbname name of database for this response
 * @param totalInDatabase number of records in database
 * @param lastExamined last record number examined for this search. 0 = all the 
 */
    public void changeToResponse(String dbname, int totalInDatabase, 
	int lastExamined) {

	min = max = percent = 0;

	dbnames = new String[] {dbname};
	total = new int[] {totalInDatabase};
	this.lastExamined = new int[] {lastExamined};
    }

/**
 * @param oclc7 oclcUserInformation7 to convert to DataDir
 * @return DataDir for an oclcUserInformation7
 * 
 */
    public static DataDir buildDir(oclcUserInformation7 oclc7) {
	return buildDir(oclc7.min, oclc7.max, oclc7.percent, oclc7.dbnames,
	    oclc7.total, oclc7.lastExamined);
    }

/**
 * Build an oclcUserInformation7 request and convert it to a DataDir
 *
 * @param minimumRecordsToRetrieve the result set must have at least this
 *        many records
 * @param maximumRecordsToRetrieve the result set is satisfactory when it has
 *        this many records
 * @param percentToComplete the result set is satisfactory when this many 
 *        records have been examined. 
 * @return DataDir for an oclcUserInformation7 
 */
    public static DataDir buildDir(int minimumRecordsToRetrieve,
	int maximumRecordsToRetrieve, int percentToComplete) {

	return buildDir(minimumRecordsToRetrieve, maximumRecordsToRetrieve,
	    percentToComplete, null, null, null);
    }

/**
 * @param minimumRecordsToRetrieve the result set must have at least this
 *        many records
 * @param maximumRecordsToRetrieve the result set is satisfactory when it has
 *        this many records
 * @param percentToComplete the result set is satisfactory when this many 
 *        records have been examined. 
 * @param dbname name of database for this response
 * @param totalInDatabase number of records in database
 * @param lastExamined last record number examined for this search. 0 = all the 
 * @return DataDir for an oclcUserInformation7 
 */
    public static DataDir buildDir(int minimumRecordsToRetrieve,
	int maximumRecordsToRetrieve, int percentToComplete, 
	String dbnames[], int totalInDatabase[], int lastExamined[]) {

	DataDir top = new DataDir(ASN1.SEQUENCE, (int)ASN1.UNIVERSAL);

	if (minimumRecordsToRetrieve != 0)
	    top.add(MINIMUMRECORDSTORETRIEVE, ASN1.CONTEXT, 
		minimumRecordsToRetrieve);

	if (maximumRecordsToRetrieve != 0)
	    top.add(MAXIMUMRECORDSTORETRIEVE, ASN1.CONTEXT, 
		maximumRecordsToRetrieve);

	if (percentToComplete != 0)
	    top.add(PERCENTTOCOMPLETE, ASN1.CONTEXT, percentToComplete);

	if (dbnames != null)
	{
	    DataDir t = top.add(RESPONSE, ASN1.CONTEXT), seq;
	    for (int i = 0; i < dbnames.length; i++)
	    {
		seq = t.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
		seq.add(DBNAME, ASN1.CONTEXT, dbnames[i]);
		seq.add(TOTALINDATABASE, ASN1.CONTEXT, totalInDatabase[i]);
	        seq.add(LASTRECNOEXAMINED, ASN1.CONTEXT, lastExamined[i]);
	    }
	}

	return top;
    }

/**
 * Combine an array of oclcUserInformation7 objects into a single object
 * with results for a single combined databases. 
 *
 * @param o array of oclcUserInformation7 objects
 * @return new oclcUserInformation7 object with combined information
 */
    public static oclcUserInformation7 combine(oclcUserInformation7 o[]) {

	oclcUserInformation7 oclc7 = new oclcUserInformation7();

	for (int i = 0; i < o.length; i++)
	{
            if (o[i] != null) {
  	      oclc7.min = o[i].min;
	      oclc7.max = o[i].max;
	      oclc7.percent = o[i].percent;

	      if (o[i].dbnames != null)
		if (oclc7.total == null)
		    oclc7.changeToResponse(o[i].dbnames, o[i].total, 
			o[i].lastExamined);
		else
		{
		    oclc7.total[0] += o[i].total[0];
		    oclc7.lastExamined[0] += o[i].lastExamined[0];
		}
             }
	}

	return oclc7;
    }

/**
 * Merge an array of oclcUserInformation7 objects into a single object
 * with results for multiple databases. 
 *
 * @param o array of oclcUserInformation7 objects
 * @return new oclcUserInformation7 object with merged information
 */
    public static oclcUserInformation7 merge(oclcUserInformation7 o[]) {
	
	oclcUserInformation7 oclc7 = new oclcUserInformation7();

	for (int i = 0; i < o.length; i++)
	{
           if (o[i] != null) {
	    oclc7.min = o[i].min;
	    oclc7.max = o[i].max;
	    oclc7.percent = o[i].percent;

	    if (o[i].dbnames != null)
		if (oclc7.total == null)
		    oclc7.changeToResponse(o[i].dbnames, o[i].total, 
			o[i].lastExamined);
		else
	            for (int j = 0; j < o[i].dbnames.length; j++)
			oclc7.addAnotherResponse(o[i].dbnames[j],
			    o[i].total[j], o[i].lastExamined[j]);
           }
	}

	return oclc7;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer("oclcUserInformation7: min=");
	sb.append(min);
	sb.append("; max=");
	sb.append(max);
	sb.append("; percent = " );
	sb.append(percent);
	if (dbnames != null)
	    for (int i = 0; i < dbnames.length; i++)
	    {
	        sb.append("; dbname=");
	        sb.append(dbnames[i]);
	        sb.append("; total=");
	        sb.append(total[i]);
	        sb.append("; lastExamined=");
	        sb.append(lastExamined[i]);
	    }
	return sb.toString();
    }
}

