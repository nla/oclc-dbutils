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

/**
 * DbPresentData contains information for retrieved database
 * records in a Z39 Present. 
 * @version @(#)DbPresentData.java	1.1 07/09/97
 */

public class DbPresentData {

/**
 * Database name for the source record.
 */
    public String dbName;

/**
 * Record Syntax for source record.
 */
    public String recordSyntax;

/**
  * Object containing the database record.
  */
    public Object data;

/**
  *  Vector of duplicate records associated with 
  *  the record.
  */
    public Vector duplicateRecords;

    public DbPresentData() {
       
    }

/**
  * @param dbname the database name
  * @param hitcount the result search count
  */
    public DbPresentData(String dbname, Object dataRecord) {
      this.dbName = dbname;
      if (dataRecord == null)
         this.data = new Diagnostic1(Diagnostic1.systemErrorPresentingRecords, 
                                                "Record Unavailable"); 
      else
        this.data = dataRecord;
    }


/**
  * Save any duplicate records associated with this record.
  * @param dbname the database name
  * @param hitcount the result search count
  */
    public final void saveDuplicate(Object duplicate) {
      if (duplicateRecords == null)
         duplicateRecords = new Vector();
      duplicateRecords.addElement(duplicate);
    }


/**
  * Determine whether this record has any duplicate records stored with it.
  * @param dbname the database name
  * @param hitcount the result search count
  */
    public final boolean hasDuplicates() {
      if (duplicateRecords == null)
        return false;
       return true;
    }


/**
  * Returns the String containing the database name for the source record.
  * @return String
  */
    public final String dbName() {
      return dbName;
    }

/**
  * Returns the object containing the data for the source record.
  * @return Object
  */
    public final Object data() {
      return data;

    }

/**
  * Returns the Vector containing the duplicate records associated with this record.
  * @return Vector
  */
    public final Vector getDuplicates() {
      return duplicateRecords;
    }

/**
  * Generates a String representation of this object
  * @return String
  */

    public String toString() {
	StringBuffer str = new StringBuffer();
	int i;

	if (dbName != null)
          str.append("dbName: " + dbName + "\n");
	else
	    str.append("no dbName\n");
	if (recordSyntax != null)
          str.append("syntax: " + recordSyntax + "\n");
	str.append("Data\n" + data);

	return str.toString();
    }
  

}

