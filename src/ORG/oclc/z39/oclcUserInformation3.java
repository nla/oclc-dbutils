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
 * oclcUserInformation3 is used in Search requests. The client requests the
 * information by sending in an oclcUserInformation3 field. The server
 * responds by returning a filled in oclcUserInformation3 field. The field
 * describes the postings per database and summary information about the
 * restrictor words represented in the result set.
 * @version @(#)oclcUserInformation3.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class oclcUserInformation3 {

/**
 * Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.3";
    private static final int resultsByDb           = 8;
    private static final int fullQueryFlag         = 2;
    private static final int queryExpression       = 3;
    private static final int postings              = 6;
    private static final int Query                 = 2;

    public DbResults dbResults[];
    public TermComponentPostings componentResults[];

    public oclcUserInformation3(DataDir userInformationField) {
	DataDir tmp, ttmp;


        int fetchOffset;

        DataDir seq;
        TermComponentPostings res;
        Vector saved = new Vector(5);
        DbResults result[];

        if (userInformationField == null ||
            userInformationField.fldid() != ASN1.single_ASN1_type)
            return;
        if (userInformationField.child() == null || userInformationField.child().child() == null)
          return;
        seq = userInformationField.child().child();

        for (; seq != null ; seq = seq.next()) {
         if (seq.fldid() == ASN1.SEQUENCE) {

           fetchOffset=1;
           res = new TermComponentPostings();

           for (tmp = seq.child(); tmp != null; tmp = tmp.next())
           {
            switch (tmp.fldid()) {
              case fullQueryFlag:
                res.fullResults = tmp.getInt();
                break;
             case queryExpression:
                res.queryExpression = tmp;

                break;
              case postings:
                res.count = tmp.getInt();
                break;
              case resultsByDb:
                result = new DbResults[tmp.count()];
                ttmp = tmp.child();
                for (int i=0; ttmp != null && ttmp.fldid() == ASN1.SEQUENCE;
                    ttmp = ttmp.next(), i++) {
                    result[i] = new DbResults(ttmp);
                    result[i].fetchOffset = fetchOffset;
                    fetchOffset += result[i].count;
                }
                // If this is the full Result entry, save in the public results;
                // otherwise save with the component postings                
                if (res.fullResults == 1) 
                  dbResults = result;
                else 
                  res.dbResults = result;
              break;
            }
          }
          // Only save if a partial result 
          if (res.fullResults == 0) 
            saved.addElement(res);
  
        }
      }
      if (saved.size() > 0) {
        componentResults = new TermComponentPostings[saved.size()];
        saved.copyInto( (Object[])componentResults );
        saved = null;
      }
    } 

/**
 * @return DataDir for an oclcUserInformation3 
 */
    public static DataDir buildDir(DbResults dbResults[], TermComponentPostings cp[]) {

        DataDir root = new DataDir(ASN1.single_ASN1_type, (int)ASN1.CONTEXT);
        DataDir node = null, tmp=null, seq; 
        int i;

        seq = root.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
        if (dbResults != null) {  	
  	  node = seq.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
          // Set flag indicating that this is the full Results Node 
          node.add(fullQueryFlag,ASN1.CONTEXT, 1);
  	  tmp = node.add(resultsByDb, ASN1.CONTEXT);
          for (i=0; i<dbResults.length; i++)
              if (dbResults[i] != null) {
                 dbResults[i].assembleDir(tmp);

             }
        }

        if (cp != null) {
	    for (i=0; i<cp.length; i++) {          

		if (cp[i] != null) {
		    node = seq.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
		    node.add(fullQueryFlag, ASN1.CONTEXT, cp[i].fullResults);
         
		    node.add((DataDir)cp[i].queryExpression.clone(false));
		    node.add(postings, ASN1.CONTEXT, cp[i].count);
		    if (cp[i].dbResults != null) {
			tmp = node.add(resultsByDb, ASN1.CONTEXT);
			
			for (int j=0; j<cp[i].dbResults.length; j++)
			    if (cp[i].dbResults[j] != null) {
				cp[i].dbResults[j].assembleDir(tmp);
			    }
		    }
		}
	    }
        }
	return root.subElement();
    }

    public static DataDir makeSubQueryExpression(String OID, String term, 
	String attributes) {

	DataDir t, d;
        t = d = new DataDir(queryExpression, (int)ASN1.CONTEXT);
        d = d.add(Query, ASN1.CONTEXT);
        d = d.add(Z39searchApi.query, ASN1.CONTEXT);
        d = d.add(Z39searchApi.type_101, ASN1.CONTEXT);
	if (OID != null)
            d.addOID(ASN1.OBJECTIDENTIFIER, ASN1.APPLICATION, OID);
        d = d.add(Z39searchApi.Operand, ASN1.CONTEXT);
        d = d.add(Z39api.AttributesPlusTerm, ASN1.CONTEXT);

	if (attributes != null)
	{
            Attribute a[] = AttributeUtil.putAttributes(attributes);
	    if (a != null)
	    {
		d = d.add(Z39api.AttributeList, ASN1.CONTEXT);
                for (int i=0; i<a.length; i++)
                    d.add(Attribute.buildDir(a[i]));
                d = d.parent();
	    }
	}

        d.add(Z39api.generalTerm, ASN1.CONTEXT, term);

	return t;
    }
}

