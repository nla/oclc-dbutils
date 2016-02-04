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
 * oclcUserInformation5 is part of a Scan request. It allows the user to
 * specify an additional set of attributes to describe the attributes for
 * all the terms returned. The attributes in the TermPlusAttributeList 
 * parameter specify the attributes of the starting point. In the absence
 * of oclcUserInformation5, then the starting point attributes are the 
 * same as the result set attributes. When a client is scanning several
 * indexes, it may be desirable to specify a particular index for a starting
 * point but still have the multiple indexes represented the list of terms
 * returned.
 * @version @(#)oclcUserInformation5.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class oclcUserInformation5 {
    
// this is going to be resultSetAttributes for scan
/**
 * Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.5";

    Attribute attributes[];

    public oclcUserInformation5(DataDir otherInformation) throws Diagnostic1 {
	DataDir tmp;
	
	if (otherInformation == null ||
	    otherInformation.fldid() != ASN1.single_ASN1_type)
	    return;

	if (otherInformation.child() == null || 
	    otherInformation.child().fldid() != ASN1.SEQUENCE)
	    return;

	attributes = new Attribute[otherInformation.count()];
	int i;

	for (tmp = otherInformation.child(), i=0; 
	     tmp.fldid() == ASN1.SEQUENCE; tmp = tmp.next(), i++)
	{
	    attributes[i] = new Attribute(tmp);
	}
    } 
}
