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
 * TermInfo is returned by a Z39-50 Scan request.
 * @version @(#)TermInfo.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class TermInfo {

/*
Z39scanApi.entries(7)
    Z39scanApi.Listentries(1):
        {
        Z39scanApi.termInfo(1):
                Z39api.generalTerm(45) - string
                Z39scanApi.displayTerm(0) - string
                Z39scanApi.AttributeList(44) -
                Z39scanApi.globalOccurrences(2) - number
                Z39scanApi.byAttributes(3)
        } repeat for each term
        Z39scanApi.nonsurrogateDiagnostics(2):
           SEQUENCE
                OBJECTIDENTIFIER(6): (oid) UNIVERSAL
                INTEGER(2): errcode UNIVERSAL
                    if value is 103 then gave up looking.
                VISIBLESTRING(26): errormsg UNIVERSAL
           //use a diag at top for missing top.
           //use one at bottom for missing bottom.
    Z39scanApi.nonsurrogateDiagnostics(2):
        SEQUENCE(16):
                OBJECTIDENTIFIER(6): (oid) UNIVERSAL
                INTEGER(2): errcode UNIVERSAL
                VISIBLESTRING(26): errormsg UNIVERSAL
*/

    private static final int DISPLAYTERM       = 0;
    private static final int ALTERNATIVETERM   = 4;
    private static final int GLOBALOCCURRENCES = 2;
    private static final int BYATTRIBUTES      = 3;
/**
  * The term.
  */
    public String Term;
/**
  * The display term for the term.
  */
    public String displayTerm;
/**
  * The alternative term for the term.
  */
    public String alternativeTerm;
    private String nocase;
/**
  * The term type returned in the Z3950 response.
  */
    public int    termType;
/**
  * The use/structure attributes in the Z3950 response.
  */
    public Attribute a[];
/**
  * The total number of occurrences for the term.
  */
    public int globalOccurrences;
/**
  * The per-database breakdown for the term.
  */
    public byAttributes b[];
/**
  * Object containing the parsed OtherInformation data associated
  * with the term.  The Object can be a notisScanOtherInformation object,
  * or a horizonScanOtherInformation object(not implemented yet), 
  * the caller must identify it.
  */
    public Object otherInformationData;

/*
 * integer data type containing the type value on the attributesPlusTerm.
 */
   int type;
/*
 * integer data type containing the use value on the attributesPlusTerm.
 */
   int use;
/*
 * integer data type containing the structure value on the attributesPlusTerm.
 */
   int structure;

   private DataDir otherInfoDir;
/**
  * Construct an object.
  */
    public TermInfo() {
    }
/**
  * Construct an object.
  * @param term the scanned term
  * @param displayterm the term to display
  */
    public TermInfo(String term, String displayTerm) {
        Term = term;
        this.displayTerm = displayTerm;
        this.termType =0;
        this.globalOccurrences = 0;
    }
/**
  * Construct an object from the Z3950 response.
  * @param termInfo the datadir containing the scan response
  */ 
    public TermInfo(DataDir termInfo) throws Diagnostic1 {

        DataDir tmp, tmp2;
        int     i;

        termType = 0;

        if (termInfo.fldid() != Z39scanApi.termInfo)
            throw new Diagnostic1(Diagnostic1.malformedQuery, null);


        for (tmp = termInfo.child(); tmp != null; tmp = tmp.next())
            switch (tmp.fldid()) {
                case Z39api.generalTerm:
                    Term = tmp.getString();
                    break;
                case DISPLAYTERM:
                    displayTerm = tmp.getString();
                    break;
                case ALTERNATIVETERM:
                    if (tmp.child() != null && tmp.child().child() != null) {
                        tmp2=tmp.child().child();
                        while (tmp2 != null) {
                          switch(tmp2.fldid()) {
                            case Z39api.generalTerm:
                              alternativeTerm = tmp2.getString();
                              break;
                            case Z39api.AttributeList:
                              DataDir seq, attr;
                              for(seq=tmp2.child(); seq != null; 
                                    seq = seq.next()) {
                                attr = seq.child();
                                while (attr != null) {
                                switch (attr.fldid()) {
                                   case Attribute.AttributeType:
                                       type = attr.getInt();
                                       attr = attr.next();
                                       break;
                                   case Attribute.AttributeValue:
                                       if (type == Attribute.BIB1_use) {
                                          use = attr.getInt();
                                       }
                                       else if (type==Attribute.BIB1_structure)
                                          structure = attr.getInt();
                                       attr = attr.next();
                                       break;

                                   default:
                                     attr = attr.next();
                                     break;
                                }
                               } 
                              }
                          }
                          tmp2 = tmp2.next();
                         }
                        }
                        break;
                case Z39api.AttributeList:
                    a = new Attribute[tmp.count()];
                    for (i=0,tmp2=tmp.child(); i<a.length; i++,tmp2=tmp2.next())
                        a[i] = new Attribute(tmp2);
                    break;
                case GLOBALOCCURRENCES:
                    globalOccurrences = tmp.getInt();
                    break;
                case BYATTRIBUTES:
                    b = new byAttributes[tmp.count()];
                    for (i=0,tmp2=tmp.child(); tmp2!=null; i++,tmp2=tmp2.next())
	                b[i] = new byAttributes(tmp2);
                    break;

                case Z39api.otherInformation:
                    otherInfoDir = tmp;

                    tmp2 = OtherInformation.getOID(tmp,  
			"1.2.840.10003.10.2000.32.2");
                    // GEAC experimental otherinfo 
                    if (tmp2 != null && tmp2.child() != null && 
			tmp2.child().child() != null &&
                        tmp2.child().child().next() != null &&
                        tmp2.child().child().next().child() != null) {
                        tmp2 = tmp2.child().child().next().child();
                        termType = tmp2.getInt();
                    }
                    else { 
                      /* NOTIS: 1.2.840.10003.10.1000.16.1 */ 
                       tmp2 = OtherInformation.getOID(tmp,
                               "1.2.840.10003.10.1000.16.1");
                       if (tmp2 != null) {
                          otherInformationData =
                            new notisScanOtherInformation(tmp2.next());
                       }   
                       else {
                         /* Horizon: 1.2.840.10003.10.1000 */ 
                         tmp2 = OtherInformation.getOID(tmp,
                              "1.2.840.10003.10.1000");
                         if (tmp2 != null) {
                            otherInformationData = 
                              new horizonScanOtherInformation(tmp2.next());
			    //System.out.println("Horizon scan");
                            //System.out.println(otherInformationData);
                         }
                       }
                    }   
                    break;   
            }
    }

/**
 * @return the number of attributes with non-zero counts for this term
 */
    public int getNumGTZero(){
        int count =0;
        for(int i=0; i<b[0].num.size(); i++)
	    if (((Integer)b[0].num.elementAt(i)).intValue() > 0)
                ++count;
        return count;
    }

/**
 *  Get the index to the byAttributes array for the 
 *  first database that has postings for the scan Term.
 *  @return int 
 */
    public int getFirstDBOffset(){
        for(int i=0; i<b[0].num.size(); i++)
	    if (((Integer)b[0].num.elementAt(i)).intValue() > 0)
                return i;
        return -1;
    }

/*
 * Get the term to display - displayTerm takes precedence over
 * the Term. 
 * @return displayTerm if it exists, else Term
 */
    public final String getText(){
        if (displayTerm!=null)
            return displayTerm;
        else return Term;
    }

/**
 * Get the original scan Term.
 * @return String 
 */
    public final String getRawText(){
        return Term;
    }

/**
 * Get the total number of postings for the scan Term.
 * @return int - globalOccurrences
 */
    public final int termCount() {
        return globalOccurrences;
    }

/**
 * Accessor method to the displayTerm.
 * @return String
 */
    public final String displayTerm() { 
        return displayTerm;
    }

/**
 * Accessor method to the alternativeTerm.
 * @return String
 */
    public final String alternativeTerm() {
        return alternativeTerm;
    }

/*
 * Accessor method to the termType.
 * @return int termType
 */
    public final int termType() {
        return termType;
    }

/** 
 * Accessor method to the Term.
 * @return String
 */
    public final String Term() {
        return Term;
    }

/**
 * Clean up Term to be a displayTerm.
 */
    public void trim(int n){
        String t = getText();
        if (t.length()<=n)
            displayTerm = "";
        else 
            displayTerm = t.substring(0, t.length()-n);
// System.out.println("Just trimmed '"+Term+"' to '"+displayTerm+"'");
    }

/**
 * Find the offset into the byAttributes array for the
 * first entry with the use attribute of BIB1.
 * @return the first use attribute for this term
 */
    public int getFirstUse(){
	for (int i=0; i<b.length; i++)
        {
	    if (b[i].attributes[0].type == Attribute.BIB1_use)
	        return b[i].attributes[0].value;
        }
        return Attribute.BIB1_Use_Anywhere;
    }

/**
 * Combine the by database information into a 
 * single count with the specified database name.
 * @param newName the new database name
 */
    public void mergeDbOccurrences(String newName) {
   	for (int i=0; i<b.length; i++)
   	{
//System.out.println(b[i]);
       	    b[i].mergeDbOccurrences(newName);
   	}
    }

/**
 * Compare the input term to the scan Term.
 * @param anotherTerm compare this term to anotherTerm
 * @return true if the terms are equal
 */
    public int compareTo(String anotherTerm) {
	return Term.compareTo(anotherTerm);
    }

    public int compareToI(String anotherTerm) {
	if (nocase == null)
	    nocase = getText().toLowerCase();
	return nocase.compareTo(anotherTerm.toLowerCase());
    }

/**
 * Compare this TermInfo object to another TermInfo object.
 * @param anotherTerm compare this term to anotherTerm
 * @return true if the terms are equal
 */
    public int compareTo(TermInfo anotherTerm) {
	return Term.compareTo(anotherTerm.Term);
    }

    public int compareToI(TermInfo anotherTerm) {
        return compareToI(anotherTerm.getText());
    }

/**
 * If byAttribute does not exist, create one. If attributes exist, create
 * the byAttribute from the dbName and attributes. If attributes does not
 * exist, create byAttribute from the default attributes and the dbName.
 * @param dbName database name for byAttribute 
 * @param defaultAttrs list of default attributes
 */
    public void setByAttribute(String dbName, Attribute defaultAttrs[]) {
        int postings = globalOccurrences;
        if (b == null)
        {
            if (a != null)
                addByAttribute(a, dbName, globalOccurrences);
            else
                addByAttribute(defaultAttrs, dbName, globalOccurrences);
            a = null;
        }
        globalOccurrences = postings;  // restore the correct postings
    }

/**
 * Merge the byAttribute of otherTerm with the byAttribute 
 * of this term.
 * @param otherTerm term containing attributes to merge with this term
 * @param dbName database name for these attributes
 * @param defaultAttrs default attributes for this term
 */
    public void addByAttribute(TermInfo otherTerm, String dbName, 
	Attribute defaultAttrs[]) {
	int i, j;

	// give me a byAttribute if I need one && I can do it
	if (b == null)
	{
	    if (a != null)
	        addByAttribute(a, dbName, globalOccurrences);
	    else
		addByAttribute(defaultAttrs, dbName, globalOccurrences);
	    a = null;
	}

	if (otherTerm.b != null)
	{
	    for (j=0; j<otherTerm.b.length; j++)
	    {
		for (i=0; i<otherTerm.b[j].databaseNames.size(); i++)
		{
		    addByAttribute(otherTerm.b[j].attributes, 
			(String)otherTerm.b[j].databaseNames.elementAt(i),
			((Integer)otherTerm.b[j].num.elementAt(i)).intValue());
		}
	    }
	}
	else if (otherTerm.a != null)
	    addByAttribute(otherTerm.a, dbName, otherTerm.globalOccurrences);
	else
	    addByAttribute(defaultAttrs, dbName, otherTerm.globalOccurrences);
    }

/**
 * Add these attributes to this term.
 * @param a attributes to add
 * @param dbName database name for attributes
 * @param postings record count for attributes
 */
    public void addByAttribute(Attribute a[], String dbName, int postings) {

	byAttributes tb[];
        int i;

//System.out.println("addByAttribute " + this);
        if (b == null)
        {
	    b = new byAttributes[1];
	    b[0] = new byAttributes(a, dbName, postings);
        }
        else
        {
// look for existence of attribute before adding it
            for (i=0; i<b.length; i++)
	    {
		if (b[i].attributesMatch(a))
		{
		    b[i].addOccurrences(dbName, postings);
		    break;
		}
	    }

            if (i == b.length) // did not find it;
            {
		tb = new byAttributes[b.length + 1];
		tb[b.length] = new byAttributes(a, dbName, postings);
		System.arraycopy(b, 0, tb, 0, b.length);
		b = tb;
            }
        }
        globalOccurrences += postings;
//System.out.println("2addByAttribute " + this);
    }          

  public static DataDir buildDir(String term, String displayTerm,
          Attribute a[], int globalOccurrences, byAttributes b[]) {
      return buildDir(term, displayTerm,  a, globalOccurrences,
                       b, null, 0, 0, null);
  }
/**
 * Build a DataDir TermInfo from the parameters.
 * @param term the scan term
 * @param displayTerm the display term
 * @param a the attributes for the term
 * @param globalOccurrences the the number of occurrences
 * @param b the breakdown by database of scan results
 * @return DataDir
 */
    public static DataDir buildDir(String term, String displayTerm,
                                   Attribute a[], 
                                   int globalOccurrences, 
                                   byAttributes b[],
                                   String alternativeTerm,
                                   int use,
                                   int structure, DataDir otherInfoDir) {

        int     i;
        DataDir tmp;
        DataDir top = new DataDir(Z39scanApi.termInfo, (int)ASN1.CONTEXT);
        if (term != null)
            top.add(Z39api.generalTerm, ASN1.CONTEXT, term);
        if (displayTerm != null)
            top.add(DISPLAYTERM, ASN1.CONTEXT, displayTerm);
        if (a != null)
        {
            tmp = top.add(Z39api.AttributeList, ASN1.CONTEXT);
            for (i=0; i<a.length; i++)
                tmp.add(Attribute.buildDir(a[i]));
        }
        if (globalOccurrences > 0)
            top.add(GLOBALOCCURRENCES, ASN1.CONTEXT,
                globalOccurrences);
        if (b != null)
        {
            tmp = top.add(BYATTRIBUTES, ASN1.CONTEXT);
            for (i=0; i<b.length; i++)
                tmp.add(byAttributes.buildDir(b[i]));
        
        }
        if (alternativeTerm != null) {
          tmp = top.add(ALTERNATIVETERM, ASN1.CONTEXT);
          tmp = tmp.add(Z39api.AttributesPlusTerm, ASN1.CONTEXT);
          DataDir ttmp = tmp.add(Z39api.AttributeList, ASN1.CONTEXT);
          ttmp.add(Attribute.buildDir(Attribute.BIB1_use,use));
          ttmp.add(Attribute.buildDir(Attribute.BIB1_structure,structure));
          tmp.add(Z39api.generalTerm, ASN1.CONTEXT,alternativeTerm);
        } 

        if (otherInfoDir != null) {
          top.add(otherInfoDir);
        }

        return top;

    }

/** 
 * Build a DataDir TermInfo from the TermInfo object.
 * @param t the TermInfo object
 * @return DataDir
 */
    public static DataDir buildDir(TermInfo t) {
        return TermInfo.buildDir(t.Term, t.displayTerm, 
            t.a, t.globalOccurrences, t.b, t.alternativeTerm, 
             t.use, t.structure, t.otherInfoDir);
    }
/**
  * Generate a String representation of the object.
  */
    public String toString() {
        StringBuffer str = new StringBuffer(Term + " " + displayTerm + " " +
            globalOccurrences + " ");
        for (int i=0; a != null && i<a.length; i++)
            str.append(a[i]);
        for (int i=0; b != null && i<b.length; i++)
            str.append(b[i]);
        if (alternativeTerm != null) {
          str.append(" (alternate: '" + alternativeTerm +"'"); 
          str.append(" type: " + type);
          str.append(" use: " + use);
          str.append(" structure: " + structure + ")");
        }
        if (this.otherInformationData != null) {
            str.append(this.otherInformationData);
        } 
        return str.toString();
    }
}
