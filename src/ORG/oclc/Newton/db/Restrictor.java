
package ORG.oclc.Newton.db;

/**
 * Required functionality for Restrictor object.
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */
 
public interface Restrictor {

/**
 * @param restValue to check
 * @return true if restValue is a match
 */
    public boolean matches(byte restValue[]);
    public int getValue(byte restValue[]);
}
