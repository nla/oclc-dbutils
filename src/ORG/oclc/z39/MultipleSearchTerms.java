
package ORG.oclc.z39;

import ORG.oclc.ber.*;

public class MultipleSearchTerms {
  
    static private final char EXCLUSIVE_RIGHT = ')';
    static private final char EXCLUSIVE_LEFT  = '(';
    static private final char INCLUSIVE_RIGHT = ']';
    static private final char INCLUSIVE_LEFT  = '[';

    static private final int TERM     = 1;
    static private final int ENDPOINT = 2;

    static private final String MultipleSearchTermsOID = "1.2.840.10003.10.5";

    public String term1;
    public String term2;
    public boolean endPointIncluded1;
    public boolean endPointIncluded2;

    public MultipleSearchTerms(String terms) {
	if (!isMultipleSearchTerms(terms))
	    throw new IllegalArgumentException("string is not a MultipleSearchTerms");

	if (terms.charAt(0) == EXCLUSIVE_LEFT)
	    endPointIncluded1 = false;
	else 
	    endPointIncluded1 = true;

	if (terms.charAt(terms.length() - 1) == EXCLUSIVE_RIGHT)
	    endPointIncluded2 = false;
	else 
	    endPointIncluded2 = true;
	
	int comma = terms.indexOf(',');
	term1 = terms.substring(1, comma);
	term2 = terms.substring(comma+1, terms.length()-1);
    }

    public MultipleSearchTerms(DataDir dir) {
        DataDir child;

        if (!isMultipleSearchTerms(dir))
            throw new IllegalArgumentException("dir is not a MultipleSearchTerms");

        try {
            child = dir.child().next().child().child().child();
            term1 = child.getString();
            endPointIncluded1 = child.next().getInt() == 0 ? false : true;
            child = child.parent().parent().next().child().child();
            term2 = child.getString();
            endPointIncluded2 = child.next().getInt() == 0 ? false : true;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("dir is not a MultipleSearchTerms");
        }
    }

    public String toString() {
	StringBuffer sb = new StringBuffer(term1.length() + term2.length() + 2);
        if (endPointIncluded1)
            sb.append(INCLUSIVE_LEFT);
        else
            sb.append(EXCLUSIVE_LEFT);

        sb.append(term1).append(',').append(term2);

        if (endPointIncluded2)
            sb.append(INCLUSIVE_RIGHT);
        else
            sb.append(EXCLUSIVE_RIGHT);

        return sb.toString();
    }

    static public String toString(DataDir dir) {
        DataDir child;
        String term1, term2;
	boolean endPointIncluded1, endPointIncluded2;
	StringBuffer sb;

        if (!isMultipleSearchTerms(dir))
            throw new IllegalArgumentException("dir is not a MultipleSearchTerms");

        try {
            child = dir.child().next().child().child().child();
            term1 = child.getString();
            endPointIncluded1 = child.next().getInt() == 0 ? false : true;
            child = child.parent().parent().next().child().child();
            term2 = child.getString();
            endPointIncluded2 = child.next().getInt() == 0 ? false : true;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("dir is not a MultipleSearchTerms");
        }

	sb = new StringBuffer(term1.length() + term2.length() + 2);
	if (endPointIncluded1)
	    sb.append(INCLUSIVE_LEFT);
	else
	    sb.append(EXCLUSIVE_LEFT);

	sb.append(term1).append(',').append(term2);

	if (endPointIncluded2)
	    sb.append(INCLUSIVE_RIGHT);
	else
	    sb.append(EXCLUSIVE_RIGHT);

	return sb.toString();
    }

    // form is [start,end) where  (==exclusive and [==inclusive
    static public DataDir toDataDir(String terms) {

        String term1, term2;
        boolean endPointIncluded1, endPointIncluded2;

	if (!isMultipleSearchTerms(terms))
	    throw new IllegalArgumentException("string is not a MultipleSearchTerms");

	if (terms.charAt(0) == EXCLUSIVE_LEFT)
	    endPointIncluded1 = false;
	else 
	    endPointIncluded1 = true;

	if (terms.charAt(terms.length() - 1) == EXCLUSIVE_RIGHT)
	    endPointIncluded2 = false;
	else 
	    endPointIncluded2 = true;
	
	int comma = terms.indexOf(',');
	term1 = terms.substring(1, comma);
	term2 = terms.substring(comma+1, terms.length()-1);

	DataDir dir = new DataDir(Z39api.externalTerm, (int)ASN1.CONTEXT);
        dir.addOID(ASN1.OBJECTIDENTIFIER, ASN1.APPLICATION, MultipleSearchTermsOID);
	DataDir child = dir.add(ASN1.single_ASN1_type, ASN1.APPLICATION);
	DataDir childchild = child.add(ASN1.SEQUENCE, ASN1.APPLICATION);
	childchild = childchild.add(ASN1.SEQUENCE, ASN1.APPLICATION);
	childchild.add(TERM, ASN1.CONTEXT, term1);
	if (endPointIncluded1)
	    childchild.add(ENDPOINT, ASN1.CONTEXT, 1);
	else
	    childchild.add(ENDPOINT, ASN1.CONTEXT, 0);
	childchild = child.add(ASN1.SEQUENCE, ASN1.APPLICATION);
	childchild = childchild.add(ASN1.SEQUENCE, ASN1.APPLICATION);
	childchild.add(TERM, ASN1.CONTEXT, term2);
	if (endPointIncluded2)
	    childchild.add(ENDPOINT, ASN1.CONTEXT, 1);
	else
	    childchild.add(ENDPOINT, ASN1.CONTEXT, 0);
	return dir;
    }

    static public boolean isMultipleSearchTerms(DataDir dir) {
	DataDir child;
	if (dir.fldid() == Z39api.externalTerm && (child = dir.child()) != null)
	    if (child.fldid() == ASN1.OBJECTIDENTIFIER)
		if (child.getOID().equals(MultipleSearchTermsOID))
		    return true;
	return false;
    }

    static public boolean isMultipleSearchTerms(String terms) {
	if (terms.length() < 5)
	    return false;
	char c = terms.charAt(0);
	if (c == INCLUSIVE_LEFT || c == EXCLUSIVE_LEFT) {
	    c = terms.charAt(terms.length() - 1);
	    if (c == INCLUSIVE_RIGHT || c == EXCLUSIVE_RIGHT)
	        if (terms.indexOf(',') != -1)
	            return true;
	}
	return false;
    }

    static public void main(String args[]) {

	if (!isMultipleSearchTerms(args[0])) {
	    System.out.println(args[0] + " is not a MultipleSearchTerms");
	    return;
	}

	DataDir dir = MultipleSearchTerms.toDataDir(args[0]);
	System.out.println("multi dir is " + dir);
	System.out.println("dir is multi ? " + isMultipleSearchTerms(dir));
	String multi2 = MultipleSearchTerms.toString(dir);
	System.out.println("multi2 is " + multi2);
    }
}
