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
 * oclcUserInformation6 is used in Search requests. The client requests the
 * information by sending in an oclcUserInformation6 field. The server
 * responds by returning a filled in oclcUserInformation6 field. The field
 * describes the postings per term in a search.  This is compatible only
 * with the OCLC FirstSearch Z39.50 service.
 * @version @(#)oclcUserInformation6.java	1.1 11/01/97
 * @author Lisa Cox
 */

public class oclcUserInformation6 extends oclcUserInformation3 {

/**
 * Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.2";
    public static final String responseOID = "1.2.840.10003.10.1";

    public oclcUserInformation6(DataDir userInformationField) {
        super(userInformationField);
    }

/**
 * @param cp array of term component postings
 * @return DataDir for an oclcUserInformation6
 * 
 */
    public static DataDir buildDir(TermComponentPostings cp[]) {
	
        return buildDir(null, cp);
    }

}

