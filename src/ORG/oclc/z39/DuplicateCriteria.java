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

package ORG.oclc.z39;

import java.util.*;

import ORG.oclc.ber.*;
/**
 * DuplicateCriteria contains information for how deduplication
 * should be performed in a Z39 dedup. 
 * @version @(#)DuplicateCriteria.java	1.1 07/09/97
 */

public class DuplicateCriteria {

  public static final String ESpec1OID = "1.2.840.10003.11.1";

/**
  * integer data type defining the level of match value when it is specified
  * as the type of duplicateDetectionCriterion.
  */
   public int levelOfMatch;

/**
  * boolean data type defining whether the duplicate detection algorithm
  * should be case sensitive.
  */
    public boolean caseSensitive;

/**
  * boolean data type defining whether the duplicate detection algorithm
  * should pay attention to punctuation.
  */
    public boolean punctuationSensitive;

/**
  * integer data type defining whether the maximum number
  * of dups is a percentage or a record count - 
  * Z39dedupApi.numberOfEntries or Z39dedupApi.percentOfEntries.
  */
    public int retentionType;
/**
 * integer data type defining the maximum number of 
 * duplicate records to maintain for an original record
 */
    public int retentionValue;
/**
  * boolean data type defining the clustering parameter
  */
    public boolean clustering;

/**
  * integer data type defining the type of Sort Criterion.
  */
    public int sortCriterionType;

/**
  * String data type defining the list of preferred dbnames in 
  * the sort criterion specification.
  */
    public Vector sortCriterionDbs;

/**
  * Array object of Strings containing the applicable portion of the 
  * record key specification. 
  */
    public String applicableKeys[];

/**
 * Constructs a DuplicateCriteria object.
 */
    public DuplicateCriteria() {
       
    }

 /**
  * Gets the applicable portion of the record data from the 
  * Element Specification Format - 1 structure.
  */
    public void saveApplicableKeys(DataDir dir) {
        DataDir sdir;

        if (dir.fldid() == ASN1.OBJECTIDENTIFIER) 
        { 
	   if (!ESpec1OID.equals(dir.getOID())) 
             return;
        } 

	dir = dir.next(); 
        if (dir.fldid() != ASN1.single_ASN1_type) 
            return;
         
        dir = dir.child();  // 5 ElementRequest
        if (dir == null)
	    return;
        dir = dir.child(); // 2 CompositeElement
        if (dir == null)
	    return;
        dir = dir.child();  // 1 ElementList
        if (dir == null)
	    return;
        applicableKeys = new String[dir.count()];
	int i=0;
	for (sdir=dir.child(); sdir != null; sdir=sdir.next()) {
	    applicableKeys[i++] = sdir.getString();  
        }
       
    }   

 /**
  * Adds the applicable portion of the record data into an
  * Element Specification Format - 1 structure.
  */
    public static void addESpecData(DataDir dir, String[] applicableKeys) {
        DataDir sdir, tmp;

        dir.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, ESpec1OID);
        tmp = dir.add(ASN1.single_ASN1_type, ASN1.CONTEXT); 
        tmp = tmp.add(5, ASN1.CONTEXT);
        tmp = tmp.add(2, ASN1.CONTEXT);
        tmp = tmp.add(1, ASN1.CONTEXT);
        for (int i=0; i<applicableKeys.length; i++) {
	    tmp.add(1, ASN1.CONTEXT, applicableKeys[i]);
        }
    }   
/**
  * Generates a String representation of this object
  * @return String
  */

    public String toString() {
	StringBuffer str = new StringBuffer();
        int i;

        str.append("DuplicateCriteria\n");
        str.append("retention type(" + retentionType + ")\n");
        str.append("retention value(" + retentionValue + ")\n");
        str.append("clustering(" + clustering +")\n");
        str.append("sortCriterionType(" + sortCriterionType + ")\n");
        if (sortCriterionDbs != null) {
           for (i=0; i< sortCriterionDbs.size(); i++)
            str.append("sortCriterionDb(" + 
              sortCriterionDbs.elementAt(i) + ")\n");
        }


        if (applicableKeys != null) {
           for (i=0; i< applicableKeys.length; i++)
	       str.append("key(" + applicableKeys[i] + ")\n");
        }

	return str.toString();
    }
  

}

