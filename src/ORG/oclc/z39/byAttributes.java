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

import java.util.Vector;
import ORG.oclc.ber.*;

/**
 * Object to maintain the per-database results for a
 * Z39.50 scan.
 * @version @(#)byAttributes.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class byAttributes {

    private static final int BYDATABASE = 3;
    private static final int NUM        = 1;
    private static final int ATTRIBUTELIST =1;
/**
  * The scan result attributes.
  */    
    Attribute attributes[];
/**
  * The database names in the scan.
  */
    Vector databaseNames;
/**
  * The postings counts for the term by database.
  */
    Vector num;
/**
  * The total postings for the scan.
  */
    int    totalNum;

/**
 * Get an array representation of all the database names 
 * in the scan.
 * @return String[]
 */
    public String[] databaseNames() {
	String s[] = new String[databaseNames.size()];
	databaseNames.copyInto(s);
	return s;
    }

/**
 * Get an array representation of all the postings counts
 * in the scan.
 * @return Integer[]
 */
    public Integer[] nums() {
	Integer i[] = new Integer[num.size()];
	num.copyInto(i);
	return i;
    }

/**
 * Set the databaseName in an attribute.
 * @param name is the string to set the entry to
 * @param whichName denotes the position in the databaseNames vector
 */
    public void setDatabaseName(String name, int whichName) {
	databaseNames.setElementAt(name, whichName);
    }

/**
 * Build a list of attributes for a term with database and posting information.
 */
    public byAttributes(Attribute a[], String d, int n) {
	attributes = a;
	databaseNames = new Vector();
	databaseNames.addElement(d);
	num = new Vector();
	num.addElement((new Integer(n)));
	totalNum += n;
    }

/**
 * Build  a list of attributes for a term with database and posting information
 * from a TermInfo DataDir.
 * @return byAttributes object
 * @exception Diagnostic1 when the input record is not formatted according to
 * the standard
 */
    public byAttributes(DataDir byAttribute) throws Diagnostic1 {
	DataDir tmp, tmp2, tmp3, dbres;
	int     i;
	
	if (byAttribute.fldid() != ASN1.SEQUENCE)
	    throw new Diagnostic1(Diagnostic1.malformedQuery, null);
        try {
	tmp = byAttribute.child();
        if (tmp.fldid() != ATTRIBUTELIST)
          throw new Diagnostic1(Diagnostic1.malformedQuery, null);

        dbres = tmp.next(); 
        if (dbres != null && dbres.fldid() != BYDATABASE) 
            throw new Diagnostic1(Diagnostic1.malformedQuery, null); 

        tmp = tmp.child();
	if (tmp.fldid() != Z39api.AttributeList)
	    throw new Diagnostic1(Diagnostic1.malformedQuery, null);
	attributes = new Attribute[tmp.count()];
	for (tmp2 = tmp.child(), i = 0; tmp2 != null; tmp2 = tmp2.next(), i++)
	    attributes[i] = new Attribute(tmp2);

        // Collect the dbresults
	databaseNames = new Vector(tmp.count());
	num = new Vector(tmp.count());
	for (tmp2 = dbres.child(), i = 0; tmp2 != null; tmp2 = tmp2.next(), i++)
	{
	    if (tmp2.fldid() != ASN1.SEQUENCE)
	        throw new Diagnostic1(Diagnostic1.malformedQuery, null);
	    for (tmp3 = tmp2.child(); tmp3 != null; tmp3 = tmp3.next())
	        switch(tmp3.fldid()) {
		    case Z39api.DatabaseName:
			databaseNames.addElement(tmp3.getString());
			break;
		    case NUM:
			num.addElement((new Integer(tmp3.getInt())));
			totalNum += tmp3.getInt();
		 	break;
		}
	}
        }
        catch (Exception e) {
          e.printStackTrace();
        }
    } 

/**
 * Merges multiple database names and counts into a single database name 
 * and count.
 * @param newName name of the new merged database
 */
    public void mergeDbOccurrences(String newName) {
	databaseNames.setSize(0);
	databaseNames.addElement(newName);
	num.setSize(0);
	num.addElement((new Integer(totalNum)));
    }

/**
 * Compare the input Attribute object to this object.
 * @param a the input Attribute
 * @return true if the attributes specified by 'a' match this list of 
 * attributes
 */
    public boolean attributesMatch(Attribute a[]) {
	int i, j;

	if (a.length != attributes.length)
	    return false;

	for (i=0; i<attributes.length; i++)
	{
	    for (j=0; j<a.length; j++)
	        if (attributes[i].equals(a[j]))
		    break; 
	    if (j==a.length) // not found
		return false;
	}
	return true;
    }

/**
 * Adds a new database name and postings count to this
 * attribute combination.
 */
    public void addOccurrences(String dbName, int postings) {
	databaseNames.addElement(dbName);
	num.addElement((new Integer(postings)));
	totalNum += postings;
    }

/**
 * Build a DataDir from an Attribute object array.
 * @param attributes attribute combination
 * @param databaseNames list of databases with results 
 * @param num list of counts to go with database names
 * @return a byAttributes DataDir built from the parameters
 */
    public static DataDir buildDir(Attribute attributes[], 
	Vector databaseNames, Vector num) {

	int     i;
	DataDir tmp, tmp2;
	DataDir top = new DataDir(ASN1.SEQUENCE, (int)ASN1.UNIVERSAL);
        tmp = top.add(ATTRIBUTELIST, ASN1.CONTEXT);
	tmp = tmp.add(Z39api.AttributeList, ASN1.CONTEXT);
	for (i=0; i<attributes.length; i++)
	    tmp.add(Attribute.buildDir(attributes[i]));
	tmp = top.add(BYDATABASE, ASN1.CONTEXT);

	for (i=0; i<databaseNames.size(); i++)
	{
	    tmp2 = tmp.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
	    tmp2.add(Z39api.DatabaseName, ASN1.CONTEXT, 
		(String)databaseNames.elementAt(i));
	    tmp2.add(NUM, ASN1.CONTEXT, 
		((Integer)num.elementAt(i)).intValue());
	}

	return top;
    }

/**
 * Build a DataDir from a byAttributes Object.
 * @return a byAttributes DataDir built from the byAttributes object
 */
    public static DataDir buildDir(byAttributes b) {
	return byAttributes.buildDir(b.attributes, b.databaseNames, b.num);
    }
/**
  * Generate a String representation of this object.
  * @return String
  */
    public String toString() {
	StringBuffer str = new StringBuffer();
	for (int i=0; i<attributes.length; i++)
	{
	    str.append(attributes[i]);
	    str.append(';');
	}
	str.setLength(str.length() - 1);
	for (int i=0; i<databaseNames.size(); i++)
	{
	    str.append(' ');
	    str.append((String)databaseNames.elementAt(i));
	    str.append("=");
	    str.append(((Integer)num.elementAt(i)).intValue());
	}
	return str.toString();
    }
}
