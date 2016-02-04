
package ORG.oclc.Newton.db;

/**
 * Suammarization of a set by restrictor values.
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */
 
public interface RestrictorSummary {

/**
 * Count the values for a restrictor
 */
    public void addEntry(byte restrictorValue[]);
/**
 * Retrieve the non-zero restrictor values, one at a time.
 */
    public RestrictorSummaryEntry nextRestrictorEntry(); 
}
