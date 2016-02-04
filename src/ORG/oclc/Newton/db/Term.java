
package ORG.oclc.Newton.db;

import java.io.*;
import ORG.oclc.z39.*;
import ORG.oclc.Newton.db.List;

/**
 * General properties for a term.
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */
 
public interface Term {

/**
 * Get the next term with the specified attributes.
 */
    public Term nextTerm(String attributes) throws IOException, Diagnostic1;
/**
 * Get the previous term with the specified attributes.
 */
    public Term prevTerm(String attributes) throws IOException, Diagnostic1;

/**
 * Get a String representation of the term.
 */
    public String term();
/**
 * Get the attributes for the term.
 */
    public Attribute[] attributes();
/**
 * Get the postings for the term.
 */
    public int postings();

/**
 * Compare this term to another term. 
 */
    public int compare(Term anotherTerm);
    // this compare does not look at indexId
/**
 * Compare this term to a byte array. This version does not examine index Ids,
 */
    public int compare(byte anotherTerm[]);
/**
 * Is this exactly what what requested, or an inexact match?
 */
    public boolean matched();
/**
 * Is this term less than what was requested?
 */
    public boolean LT();
/**
 * Is this term greater than what was requested?
 */
    public boolean GT();
/**
 * Set the compare value for this term.
 */
    public int set_cmp(int set);

    public List getList(NewtonDatabase db) throws IOException, 
	DbOutOfSyncException;

    public boolean isStopWord();

    public boolean hasRestrictors();

    public void cleanUp();
}
