
package ORG.oclc.Newton.db;

/**
 * Individual entries in a restrictor summary.
 *
 * @version @(#)RestrictorSummaryEntry.java	1.5 10/23/98
 * @author Jenny Colvard
 */
 
public class RestrictorSummaryEntry {

/**
 * ie, "English"
 */
    String name;
/**
 * ie, "ln"
 */
    String indexAbb;
/**
 * Number of records with this restrictor value
 */
    int    count;
/**
 * ie, "1031"
 */
    int    use;

    public RestrictorSummaryEntry(String name, String indexAbb, int use) {
	this.name = name;
	this.indexAbb = indexAbb;
	this.use = use;
    }

    public RestrictorSummaryEntry(RestrictorSummaryEntry map, int count) {
	if (map != null) {
	    this.name = map.name;
	    this.indexAbb = map.indexAbb;
	    this.use = map.use;
	}
	this.count = count;
    }

/**
 * @return name
 */
    public String getName() {
	return name;
    }

/**
 * @return indexAbB
 */
    public String getAbbreviation() {
	return indexAbb;
    }

/**
 * @return count
 */
    public int getCount() {
	return count;
    }

/**
 * @param count new count value
 */
    public void setCount(int count) {
	this.count = count;
    }

/**
 * @return use
 */
    public int getUse() {
	return use;
    }

    public String toString() {
	return "name=" + name + "; indexAbb=" + indexAbb + "; count=" + count + "; use=" + use + "\n";
    }
}
