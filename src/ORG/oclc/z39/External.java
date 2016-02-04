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
 * External is used as a base class for all other Z39.50 external objects.
 * @version %W% %G%
 * @author Ralph LeVan
 */

import ORG.oclc.ber.DataDir;

public class External extends DataDir {
    public static final String CharacterSetAndLanguageNegotiationOID=
        "1.2.840.10003.15.1";
    public static final String OtherInformationOID=
        "1.2.840.10003.10.3";
    public static final String oclcUserInformation1OID=
        "1.2.840.10003.10.1000.17.1";

    boolean isOK=true;
    byte[]  byteData=null;
    DataDir asn1Data=null;
    String  OID=null;

/**
 *  External's always have an EXTERNAL tag as their root.  If you need an
 *  IMPLICIT EXTERNAL, ask for External.child();
 */

    public External(DataDir root) {
        super(ASN1.EXTERNAL, (int)ASN1.UNIVERSAL);
        DataDir dir=root, oid=null;
        if(root.fldid()!=ASN1.EXTERNAL && root.fldid()!=ASN1.OBJECTIDENTIFIER){
            root=root.child();
        }
        if(root.fldid()==ASN1.EXTERNAL) {
            oid=addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, 
                root.child().getOID());
            dir=root.child().next();
        }
        else
            if(root.fldid()==ASN1.OBJECTIDENTIFIER) {
                oid=addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, 
                    root.getOID());
                dir=root.next();
            }
            else {
                dir = oid = root.child();
            }

        add(dir);
        if(oid.fldid()!=ASN1.OBJECTIDENTIFIER ||
          oid.asn1class()!=ASN1.UNIVERSAL) {
            isOK=false;
            return;
        }
        OID=oid.getOID();

        if(dir.fldid()==ASN1.single_ASN1_type)
            asn1Data=dir.child();
        else
            if (dir.fldid()==ASN1.octet_aligned)
                byteData=dir.data();
            else
                isOK=false;
    }


    public External(String OID, DataDir data) {
        super(ASN1.EXTERNAL, (int)ASN1.UNIVERSAL);
        this.OID=OID;
        asn1Data=data;
        addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, OID);
        DataDir dir=add(ASN1.single_ASN1_type, ASN1.CONTEXT);
        if(data!=null)  // data might be added later
            dir.add(data);
    }


    public External(String OID, byte[] data) {
        super(ASN1.EXTERNAL, (int)ASN1.UNIVERSAL);
        this.OID=OID;
        byteData=data;
        addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, OID);
        add(ASN1.octet_aligned, ASN1.CONTEXT, data);
    }


    public DataDir asn1Data() {
        return asn1Data;
    }

    public String OID() {
        return OID;
    }


    public boolean OK() {
        return isOK;
    }
}
