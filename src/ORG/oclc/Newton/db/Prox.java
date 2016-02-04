
package ORG.oclc.Newton.db;

/**
 * Required functionality for a Prox object.
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */
 
public interface Prox {

/**
 * Compare 2 prox values.
 *
 * @param prox 1
 * @param prox 2
 * @return 0 if equal; <0 if p1 < p2; >0 if p1 > p2
 */
    public int compare(int p1, int p2);
}
