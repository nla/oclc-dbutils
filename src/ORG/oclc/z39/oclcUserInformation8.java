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
 * oclcUserInformation8 is used in Search requests. The information in the
 * additionalSearchInformation field is used to request to rank the results
 * set.
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class oclcUserInformation8 {

/**
 * Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.8";

    public static final String TTN =        "1.2.840.10003.6.1000.17.1";
    public static final String EXTENDTTN  = "1.2.840.10003.6.1000.17.2";
    public static final String RESTRICTOR = "1.2.840.10003.6.1000.17.3";
    public static final String ATC        = "1.2.840.10003.6.1000.17.4";
    public static final String NNN        = "1.2.840.10003.6.1000.17.5";
    public static final String ATN        = "1.2.840.10003.6.1000.17.6";
    public static final String DEDUP      = "1.2.840.10003.6.1000.17.7";
    public static final String QUERYTERMS = "1.2.840.10003.6.1000.17.8";

    private static final int ALGORITHM_OID =   1;
    private static final int QUERY = 2;
    private static final int COMPONENTS = 3;

    private String oid;
    private DataDir query;
    private TermComponentPostings components[];

/**
 * Pick apart a request.
 *
 * @param userInformationField the additional search info containing
 *        an oclcUserInformation8
 */
    public oclcUserInformation8(DataDir userInformationField) {
	DataDir tmp, ttmp, seq;
	
        //System.out.println("o8 " + userInformationField);

	if (userInformationField == null ||
	    userInformationField.fldid() != ASN1.single_ASN1_type) 
	    return;

        tmp = userInformationField.child();
        if (tmp == null || tmp.fldid() != ASN1.SEQUENCE)
            return;

        for (tmp = tmp.child(); tmp != null; tmp = tmp.next())
        {

            switch (tmp.fldid()) {
              case ALGORITHM_OID: 
		oid = tmp.getOID();
                break;
              case QUERY: 
		query = tmp.child();
		if (query.fldid() != 0)
		    query = query.child();
		if (query.fldid() == ASN1.OBJECTIDENTIFIER)
		    query = query.next();
                break;
	      case COMPONENTS:
		oclcUserInformation3 oclc3 = 
		    new oclcUserInformation3(tmp.child());
		components = oclc3.componentResults;
		break;
            }
      }
        //System.out.println("o8 parsed " + this);
    }

/**
 * Build a request.
 * 
 * @param oid object identifier for the query
 * @param query ranking query
 */
    public oclcUserInformation8(String oid, DataDir query) {
	this.oid = oid;
	this.query = query;
    }

    public String oid() {
	return oid;
    }

/**
 * @return query
 */
    public DataDir query() {
	return query;
    }

    public TermComponentPostings[] components() {
	return components;
    }

    public void setcomponents(TermComponentPostings components[]) {
	this.components = components;
    }

/**
  * Alter the query.
  * @param dir the new Z39attributesPlusTerm DataDir.
  */
    public void setquery(DataDir dir) {
	query = dir;
    }

    public Object clone() {
	return new oclcUserInformation8(oid, (DataDir)query.clone());
    }

/**
 * Build an oclcUserInformation8 request and return it as a DataDir
 *
 * @param oid object identifier for the query
 * @param query ranking query
 * @param components actual components used to rank query
 * @return DataDir for oclcUserInformation8
 */
    public static DataDir buildDir(String oid, DataDir query, 
	TermComponentPostings components[]) {

	DataDir top = new DataDir(ASN1.SEQUENCE, (int)ASN1.UNIVERSAL);

	if (oid != null)
	    top.addOID(ALGORITHM_OID, ASN1.CONTEXT, oid);

	if (query != null)
	    top.add(QUERY, ASN1.CONTEXT).add(query);

	if (components != null)
	    top.add(COMPONENTS, ASN1.CONTEXT)
	       .add(ASN1.single_ASN1_type, ASN1.CONTEXT)
	       .add(oclcUserInformation3.buildDir(null, components));

        //System.out.println("o8 dir " + top);

	return top;
    }
/**
 * Build an oclcUserInformation8 request and return it as a DataDir
 *
 * @param oid object identifier for the query
 * @param query ranking query
 * @return DataDir for oclcUserInformation8
 */
    public static DataDir buildDir(String oid, DataDir query) {

	return buildDir(oid, query, null);
    }

    public String toString() {
	return "oclcUserInformation8: oid=" + oid + "; query=" + query +
	    "; components=" + components;
    }
}

