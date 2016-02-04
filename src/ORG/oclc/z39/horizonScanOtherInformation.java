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

import ORG.oclc.ber.*;

/**
 * horizonScanOtherInformation is used to hold the authoritative information
 * returned in the OtherInformation field of a Scan response from a HORIZON
 * server, identified by the OID = "1.2.840.10003.10.1000".
 * @version @(#)horizonScanOtherInformation.java	1.1 05/20/1999
 * @author Lisa Cox
 */

public class horizonScanOtherInformation {
    
  /**
   *  String data type defining the Object identifier for this field.
   */
    public static final String OID = "1.2.840.10003.10.1000";

  /**
   * integer data type defining the SEE reference.
   */
    public static final int SEE_REFERENCE = 0;
  /**
   * integer data type defining the SEE_ALSO reference.
   */
    public static final int SEE_ALSO_REFERENCE = 1;

  /**
   * integer array containing the relation type for each
   * entry (see or see_also).
   */ 
    public int relationType[];
  /**
   * String array containing the Authoritative Display Terms. 
   */ 
    public String authoritativeDisplayTerm[];

  /**
   * String array containing the Authoritative Search Terms. 
   */ 
    public String authoritativeSearchTerm[];
 
  /**
   * integer array containing the count for the Authoritative Term.
   */ 
    public int occurrences[];
 

  /**
   * Constructs a horizonScanOtherInformation object from the input DataDir
   * @param otherInformation the DataDir object containing the data.
   */ 
    public horizonScanOtherInformation(DataDir otherInformationField) {
	DataDir tmp, tmp1;
	int i=0;
        int dfltRelation;

	if (otherInformationField == null ||
	    otherInformationField.fldid() != ASN1.single_ASN1_type)
	    return;
	tmp = otherInformationField.child();
	if (tmp == null || tmp.fldid() != ASN1.SEQUENCE)
	    return;
        if (tmp.child() == null)
          return;
        tmp = tmp.child();
        if (tmp == null)
           return;

        if (tmp.fldid() == 5)
          dfltRelation = SEE_REFERENCE;
        else 
          dfltRelation = SEE_ALSO_REFERENCE;

        relationType = new int[tmp.count()];
        for (i=0; i<relationType.length; i++)
           relationType[i] = dfltRelation;

        authoritativeDisplayTerm = new String[tmp.count()];
        occurrences = new int[tmp.count()];
        authoritativeSearchTerm = new String[tmp.count()];
        i=0;
        for (tmp = tmp.child();tmp != null && tmp.fldid() == ASN1.SEQUENCE;
           tmp = tmp.next(), i++) {

  	  for (tmp1 = tmp.child(); tmp1 != null; tmp1 = tmp1.next())
	  {
            switch (tmp1.fldid()) { 
               case 1: 
                  relationType[i] = tmp1.getInt();
                  break; 
 
               case 2: 
                  authoritativeDisplayTerm[i] = tmp1.getString(); 
                  break; 
 
               case 3: 
                  occurrences[i] = tmp1.getInt(); 
                  break; 

                case 4:  
                  authoritativeSearchTerm[i] = tmp1.getString(); 
                  break; 
           } 
         }
       }
    } 

  /**
   * Generates a String representation of the object.
   */
  public String toString() {
     StringBuffer s = new StringBuffer();
     for (int i=0; i<occurrences.length; i++) {
     s.append("-Authoritative Display Term: '" + authoritativeDisplayTerm[i] +
          "'\n");
     s.append("  Authoritative Search Term: '" + authoritativeSearchTerm[i] +
          "'\n");
     if (relationType[i] == 0)
        s.append("  Relation Type: SEE Reference\n");
     else 
        s.append("  Relation Type: SEE Reference\n");
     s.append("  Count: " + occurrences[i] + "\n");
     }
     return s.toString();
  }

}

