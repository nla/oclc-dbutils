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
 * CharactersetNegotiation  defined by Z39-50.  The spec for this can be
 * found at http://lcweb.loc.gov/z3950/agency/defns/charsets.html.
 * @version %W% %G%
 * @author Ralph LeVan
 */

public class CharactersetNegotiation extends DataDir {

    public static final int proposal = 1;
    public static final int response = 2;

    public static final String iso10646OID = "1.0.10646.1.3.1.3";
    public static final String utf8OID     = "1.0.10646.1.0.8";

    public static final int proposedCharSets           = 1;
    public static final int selectedCharSets           = 1;
    private static final int languages                 = 2;
    private static final int recordsInSelectedCharSets = 3;

    public static final int iso2022  = 1;
    public static final int iso10646 = 2;

    private static final int collections   = 1;
    private static final int encodingLevel = 2;


    public String collection=null;
    public String level=null;


    public CharactersetNegotiation(int proposalOrResponse) {
        super(proposalOrResponse, (int)ASN1.CONTEXT);
    }


    public CharactersetNegotiation(External e) {
        super(new BerString(e));
        DataDir dir=this.child().next().child();
        if(dir.fldid()==response) {
            dir=dir.child();
            if(dir.fldid()==selectedCharSets) {
                dir=dir.child();
                if(dir.fldid()==iso10646) {
                    dir=dir.child();
                    collection=dir.getOID();
                    level=dir.next().getOID();
                }
            }
        }
    }


    /**
     * Creates an ISO 10646 characterset negotiation
     */
    public void add(String collection, String level) {
        DataDir tmp;
        if(this.child()!=null)
            tmp=this.find(proposedCharSets);
        else
            tmp=this.add(proposedCharSets, (int)ASN1.CONTEXT);

        tmp=tmp.add(iso10646, (int)ASN1.CONTEXT);
        tmp.addOID(collections, (int)ASN1.CONTEXT, collection);
        tmp.addOID(encodingLevel, (int)ASN1.CONTEXT, level);
    }
}
