

package ORG.oclc.Newton.db;

import java.io.*;
import java.util.BitSet;

/**
 * Interface defining access to a generic postings list.
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */
 
public interface List {

/**
 * Value returned for end of list.
 */
    public static final int NOT_A_REC = -1;

/**
 * Return the next record number. 
 */
    public int nextRecno() throws DbOutOfSyncException, IOException;
/**
 * Return the next record number that is equal to or smaller than next.
 */
    public int nextRecno(int next) throws DbOutOfSyncException, IOException;
/**
 * Return the specified record number. List begins with 1.
 */
    public int getRecno(int offset) throws DbOutOfSyncException, IOException;
/**
 * Return the restrictor for the current record number.
 */
    public byte[] nextRestrictor();
    public int    nextRestrictorValue(Restrictor r);
    public boolean nextRestrictorMatch(Restrictor r);
    public void    addRestrictorSummary(RestrictorSummary r);

/**
 * Return all the proximity information for the current record number.
    public int[] getProxInfo(Prox prox);
 */
    public BitSet getProxInfo(Prox prox, BitSet bitset);
/** 
 * Return the total number of extensions (pieces of proximity information)
 * for the current record number.
 */
    public int numExtensions(Prox prox);
/**
 * Set the counters back to the beginning of the list.
 */
    public void resetList() throws DbOutOfSyncException, IOException;
    public Object clone();

/**
 * Clean up non-reusable objects when we are done.
 */
    public void cleanUp();
}
