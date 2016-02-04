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

import ORG.oclc.ber.*;

/**
 * DbResults returns additional search information by database.
 * @version @(#)DbResults.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class DbResults {

    private static final int DATABASES             = 1;
    private static final int LIST                  = 2;
    private static final int COUNT                 = 2;
    private static final int RESTRICTORSUMMARY     = 4;
    private static final int RESTRICTORNAME        = 1;
    private static final int RESTRICTORUSE         = 2;
    private static final int RESTRICTORCOUNT       = 3;
    private static final int DIAGNOSTICS           = 5;
    private static final int DIAGNOSTICCODE        = 1;
    private static final int DIAGNOSTICMSG         = 2; 

/**
 * List of database names for these results
 */
    public String dbName[];
/**
 * Record count
 */
    public int    count;
/**
 * Diagnostic for ERRORS
 */
    public Diagnostic1  diagnostic;
/**
 * For combined databases, the first record number for retrieval
 */
    public int    fetchOffset;

/**
 * List of names of restrictors represented in this result set
 */
    public String restrictorNames[];
/**
 * List of attribute type = use values for these restrictors
 */
    public int    restrictorUses[];
/**
 * Record counts for each restrictor
 */
    public int    restrictorCounts[];

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
 

   
    public DbResults() {
       
    }

/**
  * @param dbname the database name
  * @param hitcount the result search count
  */
    public DbResults(String dbname, int hitcount) {
      this.dbName = new String[] {(String)dbname};
      this.count = hitcount;
      this.fetchOffset = 1;
    }

/**
  * @param dbname the database name
  * @param hitcount the result search count
  * @param fetchOffset the offset to start record reads from
  */
    public DbResults(String dbname, int hitcount, int fetchOffset) {
      this.dbName = new String[] {(String)dbname};
      this.count = hitcount;
      this.fetchOffset = fetchOffset;
    }

/**
  * @param dbname array of database names
  * @param hitcount the result search count
  */
    public DbResults(String[] dbname, int hitcount) {
      this.dbName = new String[dbname.length];
      for (int i=0; i<dbname.length; i++)
        this.dbName[i] = dbname[i];
      this.count = hitcount;
      this.fetchOffset = 1;
    }

/**
  * @param dbname array of database names
  * @param hitcount the result search count
  * @param fetchOffset the offset to start record reads from
  */
    public DbResults(String[] dbname, int hitcount, int fetchOffset) {
      this.dbName = new String[dbname.length];
      for (int i=0; i<dbname.length; i++)
        this.dbName[i] = dbname[i];
      this.count = hitcount;
      this.fetchOffset = fetchOffset;
    }


/**
 * 
 */
    public DbResults(DataDir resultsbyDb) {
	DataDir tmp, ttmp;
	int     i;

//System.out.println("resultsbyDb: " + resultsbyDb);

	for (DataDir seq = resultsbyDb.child(); 
	     seq != null && seq.fldid() == ASN1.SEQUENCE && 
		seq.child() != null; 
	     seq = seq.next())
	{
	    for (DataDir parm = seq.child(); parm != null; parm = parm.next())
	      switch (parm.fldid())
	      {
		  case DATABASES:
		      if (parm.child().fldid() == LIST)
		      {
			  tmp = parm.child().child();
			  dbName = new String[parm.child().count()]; 
			  for (i=0; i<dbName.length && tmp != null; 
			       i++, tmp = tmp.next())
			      dbName[i] = tmp.getString();
		      }
		      break;
		  case COUNT:
		      count = parm.getInt();
		      break;
		  case DIAGNOSTICS:
                      diagnostic = new Diagnostic1();
                      for (tmp=parm.child(); tmp != null; tmp = tmp.next()) {
                         switch(tmp.fldid()) {
                           case DIAGNOSTICCODE:
                            diagnostic.condition = tmp.getInt();
                            break;
                           case DIAGNOSTICMSG:
                            diagnostic.addinfo = tmp.getString();
                            break;
                        }
                      }
		      break;
		  case RESTRICTORSUMMARY:
		      restrictorNames = new String[parm.count()];
		      restrictorUses = new int[parm.count()];
		      restrictorCounts = new int[parm.count()];
		      for (i=0,tmp=parm.child(); tmp != null;tmp=tmp.next(),i++)
		      {
			if (tmp.fldid() != ASN1.SEQUENCE)
			    continue;
			for (ttmp=tmp.child(); ttmp != null; ttmp=ttmp.next())
			    switch(ttmp.fldid())
			    {
				case RESTRICTORNAME:
				    restrictorNames[i] = ttmp.getString();
				    break;
				case RESTRICTORUSE:
				    restrictorUses[i] = ttmp.getInt();
				    break;
				case RESTRICTORCOUNT:
				    restrictorCounts[i] = ttmp.getInt();
				    break;
			    }
		      }
	      }
		
	}
    }

/**
  * Test to see if entry has errors (diagnostics associated with it)
  * @return boolean true = has error, false = good search
  */
    public boolean hasError() {
      if (this.diagnostic == null)
        return false;

      return true;
    }

/**
  * Return the official diagnostic error message (Diagnostic1.msg()) from the result diagnostic code.
  * If the diagnostic error code == Diagnostic1.unspecifiedError and there is text stored with
  * the error, then send the text back, otherwise get the message associated with the diagnostic1
  * error code.
  * @return String with diagnostic text 
  */
    public String diagnostic1Info() {
      if (this.diagnostic == null)
        return "";

      if (this.diagnostic.condition() == Diagnostic1.unspecifiedError &&
              this.diagnostic.addinfo() != null && this.diagnostic.addinfo().length() > 0)
        return this.diagnostic.addinfo();

      return this.diagnostic.msg(diagnostic.condition());

    }

/**
  * Create a Diagnostic 
  * @param errorCode the diagnostic error code
  * @param errorMsg the diagnostic error message
  */
    public void addDiagnostic(int errorCode, String errorMsg) {
      this.diagnostic = new Diagnostic1(errorCode, errorMsg);
    }

/**
  * Save a Diagnostic 
  * @param d the diagnostic1 object to save
  */
    public void addDiagnostic(Diagnostic1 d) {
      this.diagnostic = d;
    }

/**
  * assemble a directory node with the DbResults information
  * @param parent the parent node of a data dir to add the info to
  */
    public boolean assembleDir(DataDir parent) {
	DataDir s, t, u;
	int i;

	s = parent.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
	s = s.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
	
	t = s.add(DATABASES, ASN1.CONTEXT);
	t = t.add(LIST, ASN1.CONTEXT);
	for (i=0; i<dbName.length; i++)
	    if (dbName[i] != null) {
		t.add(Z39api.DatabaseName, ASN1.CONTEXT, dbName[i]);
          }
	
	s.add(COUNT, ASN1.CONTEXT, count);

        /* If Diagnostic allocated, then send it too */
        if (diagnostic != null) {
          t = s.add(DIAGNOSTICS, ASN1.CONTEXT);
          t.add(DIAGNOSTICCODE, ASN1.CONTEXT, diagnostic.condition);
          if (diagnostic.addinfo != null)
            t.add(DIAGNOSTICMSG, ASN1.CONTEXT, diagnostic.addinfo);
        }

        if (restrictorNames != null) {	
  	  t = s.add(RESTRICTORSUMMARY, ASN1.CONTEXT);
	  for (i=0; i<restrictorNames.length; i++)
	  {
            if (restrictorNames[i] != null){
  	      u = t.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
	      u.add(RESTRICTORNAME, ASN1.CONTEXT, restrictorNames[i]);
	      u.add(RESTRICTORUSE, ASN1.CONTEXT, restrictorUses[i]);
	      u.add(RESTRICTORCOUNT, ASN1.CONTEXT, restrictorCounts[i]);
            }
	  }
        }

	return true;
    }

/**
  * Get the dbName
  * @return String
  */
    public String dbName() {
      return dbName[0];
    }

/**
  * Get the search postings
  * @return int
  */
    public int postings() {
      return count;

    }

/**
  * Get the first Record number for the database from the resultset
  * @return int
  */

    public int firstRecno() {
      return fetchOffset;
    }

/**
  * Get the diagnostic
  * @return Diagnostic1 
  */

    public Diagnostic1 diagnostic() {
      return  diagnostic;
    }

/**
  * Generate a String representation of this object
  * @return String
  */

    public String toString() {
	StringBuffer str = new StringBuffer();
	int i;

	if (dbName != null)
	    for (i=0; i<dbName.length; i++)
	        str.append("dbName[" + i + "]=" + dbName[i] + "\n");
	else
	    str.append("no dbName\n");
	str.append("count=" + count + " fetchOffset= " + fetchOffset +"\n");
        if (diagnostic != null) {
         str.append(diagnostic + "\n");
        }
//	if (restrictorNames != null)
//	    for (i=0; i<restrictorNames.length; i++)
//	        str.append("restrictorName=" + restrictorNames[i] + ";use=" +
//		    restrictorUses[i] + ";count=" + restrictorCounts[i] + "\n");
	return str.toString();
    }
  
    /**
      *  Given an array of DbResults, calculate the total postings 
      *  @return int totalPostings
      */
     public static int totalPostings(DbResults results[]) {
        int total = 0;
        for (int i=0; i<results.length; i++)
          total += results[i].count;
        return total;
    } 

   /**
      *  Given an array of DbResults, create a string with all the
      *  dbnames
      *  @return String all database Names 
      */
     public static String allNames(DbResults results[]) {
        StringBuffer name = new StringBuffer();
        for (int i=0; i<results.length; i++) {
          if (name.length() > 0)
            name.append(" ");
          name.append(results[i].dbName[0]);
        }
        return name.toString();
    }

   /**
      *  Given an array of DbResults, get the partial results search information from
      *  the oclcUserInformation7 data
      *  @return oclcUserInformation7 object
      */
     public static oclcUserInformation7 getPartialResults(DbResults results[]) {
        if (results == null)
           return null;
        return results[0].oclc7;

    }

   /**
      *  Given an array of DbResults, get the partial results search information from
      *  the oclcUserInformation8 data
      *  @return oclcUserInformation8 object
      */
     public static oclcUserInformation8 getUserInfo8(DbResults results[]) {
        if (results == null)
           return null;
        return results[0].oclc8;
    }


    /**
      *  Given an array of DbResults, get the component results from the search
      *  @return TermComponentPostings array
      */
     public static TermComponentPostings[] getComponentPostings(DbResults results[]) {
        if (results == null)
           return null;
        return results[0].componentResults;
     }

}

