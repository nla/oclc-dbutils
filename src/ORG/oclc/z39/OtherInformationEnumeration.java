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

import java.util.Enumeration;

import ORG.oclc.ber.*;

/**
 * Global OtherInformation defined by Z39-50.
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class OtherInformationEnumeration implements Enumeration {
    DataDir seq=null;

    public OtherInformationEnumeration(OtherInformation otherInfo) {
        seq=otherInfo.child();
    }


    public boolean hasMoreElements() {
        if(seq==null || seq.fldid()!=ASN1.SEQUENCE)
            return false;
        return true;
    }


    public Object nextElement() {
        External e=new External(seq.child());
        seq=seq.next();
        return e;
    }
}



