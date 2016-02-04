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
 * userInformationField is used in the Init transaction.
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class userInformationField extends DataDir {
    External external;
    public userInformationField(External e) {
        super(Z39api.UserInformationField, (int)ASN1.CONTEXT);
        add(e);
        external=e;
    }


    public userInformationField(DataDir dir) {
        super(new BerString(dir));
        external=new External(dir.child());
    }


    public DataDir asn1Data() {
        return external.asn1Data();
    }

    public External external() {
        return external;
    }

    public String OID() {
        return external.OID();
    }


/**
 * Look in userInformationField for the information indentified by the 
 * specified OID
 * @param userInformationField userInformationField of a Z39 init transaction
 * @param OID object identifier defining the information
 * @return DataDir of the information
 */
    public static DataDir getData(DataDir userInformation, String OID) {

        DataDir tmp;

        tmp = userInformation.child(); 
        if (tmp.fldid() != ASN1.EXTERNAL)
            return null;

        tmp = tmp.child();
        if (tmp.fldid() == ASN1.OBJECTIDENTIFIER)
        {
	    String oid=tmp.getOID();
	    if(oid.equals(External.OtherInformationOID)) {
System.out.println("in userInformation.getData: trying for OtherInformation");
		DataDir tempDir=OtherInformation.getData(tmp.next().child(), OID);
System.out.println("tempDir="+tempDir);
		return OtherInformation.getData(tmp.next().child(), OID);
	    }
            else if (!OID.equals(tmp.getOID()))
                return null;
        }
        else
            return null;
        tmp = tmp.next();
        if (tmp.fldid() == ASN1.single_ASN1_type)
            return tmp;
        
        return null;
    }

/**
 * Add the object identifier and information structure to the parent directory
 * @param parent parent directory
 * @param OID object identifier for the information
 * @param data the information
 * @return the parent
 */
    public static DataDir addOIDandData(DataDir parent, String OID, 
      DataDir data) {
        DataDir tmp=parent.add(Z39api.UserInformationField, ASN1.CONTEXT);
        return addOIDandDataOnly(tmp, OID, data);
    }


    private static DataDir addOIDandDataOnly(DataDir parent, String OID, 
        DataDir data) {
        DataDir tmp = parent.add(ASN1.EXTERNAL, ASN1.UNIVERSAL);
        if (OID != null)
            tmp.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, OID);
        if (data != null)
        {
            tmp = tmp.add(ASN1.single_ASN1_type, ASN1.CONTEXT);
            tmp.add(data);
        }

        return parent;
    }
}
