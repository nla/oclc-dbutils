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
 * This handles the internal version of OtherInformation as defined
 * in the Z39.50 standard.  There is also an ExternalOtherInformation
 * object when the OtherInfo needs to go into a userInformationField
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class OtherInformation extends DataDir {
    private static final int externallyDefinedInfo = 4;
    private static final int oid = 5;
    private static final int infoCategory = 1;
    private static final int categoryValue = 2;


    public OtherInformation() {
        super(Z39api.otherInformation, (int)ASN1.CONTEXT);
    }


    public OtherInformation(DataDir dir) {
        super(new BerString(dir));
    }


    public DataDir add(External e) {
        DataDir seq = add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
        DataDir info = seq.add(externallyDefinedInfo, ASN1.CONTEXT);
        info.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, e.child().getOID());
        info.add(e.child().next());
        return seq;
    }


    public Enumeration elements() {
        return new OtherInformationEnumeration(this);
    }


    public boolean OK() {
        if(fldid()==Z39api.otherInformation)
            return true;
        return false;
    }


  /**
   * Look in otherInformation for an OID
   * @param otherInformation OtherInformation field of a Z39 transaction
   * @return DataDir of the object identifier
   */
  public static DataDir getOID(DataDir otherInformation) {

        DataDir child, tmp, oidDir=null;

        for (tmp = otherInformation.child(); tmp != null && oidDir == null;
               tmp = tmp.next())
          {
            child = tmp.child();
            if (child.fldid() == oid) {
                 oidDir = child;
            }
            else if (child.fldid() == externallyDefinedInfo) {
               child = child.child();
               if (child.fldid() == ASN1.OBJECTIDENTIFIER)
                 {
                    oidDir = child;
                 }
            }

          }

        return oidDir;
  }

/**
 * Look in otherInformation for the specified OID 
 * @param otherInformation OtherInformation field of a Z39 transaction
 * @param OID object identifier defining the information
 * @return DataDir of the object identifier
 */
    public static DataDir getOID(DataDir otherInformation, String OID) {
 
        DataDir child, tmp, oidDir=null;
 
        for (tmp = otherInformation.child(); tmp != null && oidDir == null;
          tmp = tmp.next()) {
            for (child = tmp.child(); child != null; child = child.next()) {
                if (child.fldid() == oid)  {
                   if (OID.equals(child.getOID()))
                     oidDir = child; 

                }  
                else if (child.fldid() == externallyDefinedInfo) {
                   if (child.child().fldid() == ASN1.OBJECTIDENTIFIER)
                   {
                      if (OID.equals(child.child().getOID()))
                        oidDir = child.child(); 
                   }
                }
            }

        }
 
        return oidDir;
    }

/**
 * Look in otherInformation for the information indentified by the specified 
 * OID
 * @param otherInformation OtherInformation field of a Z39 transaction
 * @param OID object identifier defining the information
 * @return DataDir of the information
 */
    public static DataDir getData(DataDir otherInformation, String OID) {

        DataDir child, tmp, dataDir=null;

        for (tmp=otherInformation.child(); tmp != null && dataDir == null;
          tmp = tmp.next()) {
            child = tmp.child();
            if (child.fldid() == oid) {
              if (OID.equals(child.getOID())) {
                dataDir = child.next();
              }
            }
            else if (child.fldid() == externallyDefinedInfo) {
               child = child.child();
               if (child.fldid() == ASN1.OBJECTIDENTIFIER) {
                  if (OID.equals(child.getOID()))
                    dataDir = child.next();  // try next one
               }
            }
            if (dataDir != null && dataDir.fldid() != ASN1.single_ASN1_type)
                 dataDir = null;

        }
        
        return dataDir;
    }

/**
 * Add the object identifier and information structure to the parent
 * directory with an alternate fldid
 * @param parent parent directory
 * @param OID object identifier for the information
 * @param data the information
 * @param int fldid
 * @return the parent
 */
    public static DataDir addOIDandData(DataDir parent, String OID, 
        DataDir data, int fldid) {
        
        DataDir tmp;

        if (parent.find(fldid, ASN1.CONTEXT)!=null) {
            for (tmp = parent.child(); 
                tmp != null && 
                (tmp.fldid() != fldid ||
                tmp.asn1class() != ASN1.CONTEXT); tmp = tmp.next() )
                ; // empty loop
            // tmp is now the correct parent
            parent = tmp;
        }
        else
            parent = parent.add(fldid, ASN1.CONTEXT);
        return addOIDandDataOnly(parent, OID, data);
    }


    public static DataDir addOIDandDataOnly(DataDir parent, String OID, 
      DataDir data) {
        DataDir tmp = parent.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
        tmp = tmp.add(externallyDefinedInfo, ASN1.CONTEXT);
        if (OID != null)
            tmp.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, OID);
        if (data != null) {
            tmp = tmp.add(ASN1.single_ASN1_type, ASN1.CONTEXT);
            tmp.add(data);
        }

        return parent;
    }

/**
 * Add the object identifier and information structure to the parent
 * directory with an alternate fldid
 * @param parent parent directory
 * @param OID object identifier for the information
 * @param category the setting for the InfoCategory Value
 * @param int fldid
 * @return the parent
 */
    public static DataDir addOIDandData(DataDir parent, String OID, 
        int category, int fldid) {
        
        DataDir tmp;

        if (parent.find(fldid, ASN1.CONTEXT)!=null) {
            for (tmp = parent.child(); 
                tmp != null && 
                (tmp.fldid() != fldid ||
                tmp.asn1class() != ASN1.CONTEXT); tmp = tmp.next() )
                ; // empty loop
            // tmp is now the correct parent
            parent = tmp;
        }
        else
            parent = parent.add(fldid, ASN1.CONTEXT);
     
        tmp = parent.add(Z39api.otherInformation, ASN1.CONTEXT);
        tmp = tmp.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
        DataDir ttmp = tmp.add(infoCategory, ASN1.CONTEXT);
        ttmp.add(categoryValue, ASN1.CONTEXT, category);
        tmp.addOID(oid, ASN1.CONTEXT, OID);


        return parent;
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
        return OtherInformation.addOIDandData(parent, OID, data,
            Z39api.otherInformation);
    }

/**
  * Remove the OID and data from the directory.
  * @param parent parent directory
  * @param OID object identifier
  */  

   public static void removeOIDandData(DataDir parent, String OID) {

        DataDir dir = 
            parent.find(Z39api.otherInformation,ASN1.CONTEXT);
        if (dir != null) {
          // check to make sure this is for the one we want
          DataDir data = getData(dir,OID);
          if (data == null)
            return;
          dir.delete(); // found it, delete node
        }
    }

}



