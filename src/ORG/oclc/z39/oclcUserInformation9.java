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
 * oclcUserInformation9 is used in Sort requests to let the target know
 * that the sorts for cross-database searches should interleave the records
 * from allthe databases into on combined set.
 *
 * @version %W% %G%
 * @author Lisa Cox
 */

public class oclcUserInformation9 {

/**
 * Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.9";


/**
 * Pick apart a request.
 *
 * @param userInformationField the additional sort info containing
 *        an oclcUserInformation9
 */
    public oclcUserInformation9(DataDir userInformationField) {
	DataDir tmp, ttmp, seq;

        if (userInformationField == null ||
            userInformationField.fldid() != ASN1.single_ASN1_type)
            return;
        tmp = userInformationField.child();

        if (tmp == null || tmp.fldid() != ASN1.SEQUENCE)
            return;
    }
	

/**
 * @return DataDir for an oclcUserInformation9
 */
    public static DataDir buildDir() {
 
        DataDir top = new DataDir(ASN1.SEQUENCE, (int)ASN1.UNIVERSAL);
 
        return top;
    }

    public String toString() {
	return "oclcUserInformation9: ";
    }
}

