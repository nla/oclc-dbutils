
package ORG.oclc.Newton.db;

import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import ORG.oclc.z39.*;
import ORG.oclc.ber.*;
import ORG.oclc.util.*;
import ORG.oclc.qparse.DbAttributes;
import ORG.oclc.qparse.IndexMap;
import ORG.oclc.qparse.Map;
import ORG.oclc.qparse.StopWord;
import ORG.oclc.qparse.TermNormalizer;

/**
 *
 * @version %W% %G%
 * @author Jenny Colvard
 */

public abstract class NewtonDatabase {

    private static final boolean debug = false;

/**
 * Direction flag for browse. 
 */
    static public final char NEXT = 'n';
/**
 * Direction flag for browse.
 */
    static public final char PREVIOUS = 'p';
 
/**
 * Normalized wildcard character.
 */
    //static public final char ZERO_OR_ONE_WILD  = '?';
    static public final char ZERO_OR_ONE_WILD  = (char)0x01;
/**
 * Normalized wildcard character.
 */
    //static public final char ZERO_OR_MORE_WILD = '*';
    static public final char ZERO_OR_MORE_WILD = (char)0x02;
/**
 * Normalized wildcard character.
 */
    //static public final char ONE_ONLY_WILD     = '#';
    static public final char ONE_ONLY_WILD     = (char)0x03;

    protected boolean inUpdateMode;

/**
 * Flag to use default search attributes.
 */
    static final public String DEFAULT_SEARCH_ATTRIBUTES = "default_search";
/**
 * Flag to use default browse attributes.
 */
    static final public String DEFAULT_BROWSE_ATTRIBUTES = "default_browse";

    protected Hashtable    attributesToFilter;
    protected Hashtable    attributesToIndexes;
    protected Hashtable    classesByOID;
    protected DbAttributes attributesMap;
    protected IndexMap     indexMap;

    public void initNewtonDatabase(IniFile inifile) {
	attributesToFilter = new Hashtable(20);
	attributesToIndexes = new Hashtable(20);
        classesByOID = new Hashtable(20);
	attributesMap = new DbAttributes("attributes", inifile);
	indexMap = attributesMap.getMap(); // get BIB1 by default

        IniFileSection OIDsection = inifile.getSection("OIDclassFiles");
        if (OIDsection != null)
        {
            String k[] = OIDsection.getKeys();
            for (int i = 0; k != null && i < k.length; i++)
            {
		try {
                    classesByOID.put(k[i], 
			Class.forName(OIDsection.getValue(k[i])));
		} catch (ClassNotFoundException e) {
		}
                System.out.println("OID= " + k[i] + "; CLASS= " +
                    OIDsection.getValue(k[i]));
            }
        }
    }

/**
 * Load and initialize a NewtonDatabase.
 * 
 * @param inifile to read
 * @param dbname section containing NewtonDatabase class name
 * @return NewtonDatabase or null if loading failed
 */
    static public NewtonDatabase SetupDatabase(IniFile inifile, String dbname) {
	String nClass = inifile.getValue(dbname, "class");
	if (debug)
	    System.out.println("dbname=" + dbname + "; class=" + nClass);
	if (nClass == null)
	    nClass = "ORG.oclc.Newton.db.light.light";
	try {
	    Class dbClass;
	    dbClass = Class.forName(nClass);
	    if (debug)
		System.out.println("dbClass=" + dbClass);
	    NewtonDatabase db = (NewtonDatabase)dbClass.newInstance();
	    if (debug)
		System.out.println("db=" + db);
	    db.initNewtonDatabase(inifile);
	    if (!db.open(inifile, dbname))
		return null;
	    return db;
	} catch (Exception e) {
	    System.err.println("Failed to create class " + nClass + ": " + e);
	    e.printStackTrace();
	    return null;
	}
    }

/**
 * NewtonDatabases must be able to open themselves.
 * 
 * @param inifile for parameters
 * @param dbname starting section in inifile
 * @return true if the open succeeded
 * @exception Exception if an error occured
 */
    abstract public boolean open(IniFile inifile, String dbname) 
	throws Exception;

/**
 * Close the database
 */
    abstract public void close();

/**
 * @return true if the database open and ready
 */
    abstract public boolean OK();

/**
 * @return true if this db supports browse
 */
    abstract public boolean browsableIndex();

/**
 * Get a record from the database.
 *
 * @param recordNumber to retrieve
 * @param format to retrieve the record in
 * @return BerString containing record
 * @exception IOException probably a read error
 */
    public BerString[] getRecord(int recordNumbers[], String format, 
	Object userData) {

	BerString recs[] = new BerString[recordNumbers.length];
	for (int i=0; i < recs.length; i++) {
	    try {
	        recs[i] = getRecord(recordNumbers[i], format, userData);
	    } catch (IOException e) {
		recs[i] = null;
	    } 
	}
	return recs;
    }

    abstract public BerString getRecord(int recordNumber, String format, 
	Object userData) throws IOException;

/**
 * Get a prox object. Used for retrieving prox data from a List.
 *
 * @param piece describing prox data ("1=2", "1=@2", etc)
 * @return Prox object
 */
    abstract public Prox getProx(String piece);

/**
 * See if the term is a restrictor.
 *
 * @param term to look up
 * @param attributes for term
 * @return Restrictor if the term is a restrictor, else null
 * @exception Diagnostic1 probably an unsupported attribute combination
 */
    abstract public Restrictor getRestrictor(String term, String attributes,
	Object userData) throws Diagnostic1;

/**
 *  Find the term in the database.
 *
 * @param term to look up
 * @param attributes for term
 * @return Term if the term exists, else null
 * @exception IOException probably a read error
 * @exception Diagnostic1 probably an unsupported attribute combination
 */
    abstract public Object findTerm(String term, String attributes, 
	Object userObject) throws DbOutOfSyncException, IOException, Diagnostic1;
/**
 *  Find the terms in the database.
 *
 * @param terms to look up
 * @param attributes for term
 * @return Term if the term exists, else null
 * @exception IOException probably a read error
 * @exception Diagnostic1 probably an unsupported attribute combination
 */
    abstract public Object findTerms(String term[], String attributes, 
	Object userObject) throws DbOutOfSyncException, IOException, Diagnostic1;

/**
 *  Browse a term in the database.
 *
 * @param term to look up
 * @param attributes for term
 * @param direction to go for inexact matches - the next term or the prev term
 * @return Term if the term exists, else null
 * @exception IOException probably a read error
 * @exception Diagnostic1 probably an unsupported attribute combination
 */
    abstract public Term browseTerm(String term, String attributes,
	char direction) throws IOException, Diagnostic1;

/**
 * Get a List from a term. 
 *
 * @param term to get List from.
 * @return List for examining record numbers, restrictors, and prox data.
 * @exception IOException probably a read error
 */
    abstract public List getList(Term term) 
	throws DbOutOfSyncException, IOException;

/**
 * @return some stats about the db.
 */
    abstract public String getStats();

/**
 * @return an initialized restrictor summary.
 */
    abstract public RestrictorSummary getRestrictorSummary();

/**
 * @return database info in an IniFile type format
 */
    abstract public RichProperties getRichProperties();

/**
 * @return the largest record number
 */
    abstract public int getLargestRecordNumber();

/**
 * @return StopWord list
 */
    abstract public StopWord getStopWords(String attributes);

/**
 * @return DbAttributes Object defining all the indexes
 */
    public DbAttributes getDbAttributes() {
	return attributesMap;
    }

/**
 * @return QueryFilterObject
 */
   public TermNormalizer termNormalizerObject(String attributes) {
        TermNormalizer filter = null;

        if (debug)
             System.out.println("termNormalizerObject attrs=" + attributes);

	if (attributes == null || attributes.length() == 0)
	    attributes = DEFAULT_SEARCH_ATTRIBUTES;

        filter = (TermNormalizer)attributesToFilter.get(attributes);

        if (filter == null) {
            if (debug)
                System.out.println("need to find filter");
            try {
                Map map = null;
                String indexes = convertAttributesToIndexes(attributes);
                if (indexes != null && indexes.length() == 1)
                    map = indexMap.getMapbyAlternateIndex(indexes.charAt(0));
                if (debug)
                    System.out.println("index(0)=" + (int)indexes.charAt(0)
                         + "; map=" + map + "; indexes=" + indexes);
                if (map != null)
                    filter = map.termNormalizerObject();

                if (debug) {
                    System.out.println("filter=" + filter);
		    System.out.println("map=" + map);
		    if (map == null || filter == null)
			System.out.println("indexMap=" + indexMap);
		}

                if (filter != null)
                    attributesToFilter.put(attributes, filter);
            } catch (Diagnostic1 d) {
            }
        }

        return filter;

    }

/**
 * @param attributes to parse
 * @return true if this attribute string has multiple indexes
 */
    public boolean multipleIndexes(String attributes) {
        try {
            String indexes = convertAttributesToIndexes(attributes);
            if (indexes.length() > 1 || indexes.indexOf(0) != -1)
                return true;
        } catch (Diagnostic1 d) {
        }
        return false;
    }

/**
 * @return true if this database has stored truncated records
 */
    abstract public boolean hasTruncatedRecords();
/**
 * Decode the item number passed in by converting from the input
 * byteorder to the correct native byteorder.
 * @return int the decoded item number
 */
    abstract public int decodeItemNumber(byte[] data, int offset);

    public Class getClassByOID(String OID) throws ClassNotFoundException {
        Class oidclass = (Class)classesByOID.get(OID);
        if (debug)
            System.out.println("OID is " + OID + "; class=" + oidclass);
        if (oidclass == null)
            return Class.forName(OID); // try it anyway
        return oidclass;
    }

    abstract public boolean getUpdateMode();
    abstract public void setUpdateMode(boolean inUpdate);
    abstract public void resync() throws IOException;

/**
 * Convert an attribute string to a list of newton index ids.
 *
 * @param attributes to convert
 * @return newton index ids
 * @exception Diagnostic1 probably an unsupported attribute combination
 */
    public String convertAttributesToIndexes(String attributes) 
	throws Diagnostic1 { // 'u=31' OR 'u=31;s=1;u=27'

        String answer;
        char t[];
        StringTokenizer st;
        String token;
        int i, j, newtonId, next = 0, use = 0, structure = -1;
        Map map = null;

        if (debug)
            System.out.println("convertAttributesToIndexes attributes='" + attributes +
                "'; attributesToIndexes=" + attributesToIndexes);
        if ((answer = (String)attributesToIndexes.get(attributes)) != null)
            return answer;

        if (attributes == DEFAULT_SEARCH_ATTRIBUTES)
        {
            if (debug)
                System.out.println("default search index is " +
                    indexMap.getDefaultAlternateId(false));
	    answer = new String(
		new char[] {(char)indexMap.getDefaultAlternateId(false)});
            attributesToIndexes.put(attributes, answer);
            return answer;
        }
        else if (attributes == DEFAULT_BROWSE_ATTRIBUTES)
        {
	    answer = new String(
		new char[] {(char)indexMap.getDefaultAlternateId(true)});
            attributesToIndexes.put(attributes, answer);
            return answer;
        }

        st = new StringTokenizer(attributes, ";");

        if ((i=attributes.indexOf("s=")) != -1)
        {
            if ((j = attributes.indexOf(';', i)) == -1)
                j = attributes.length();
            structure = Integer.parseInt(attributes.substring(i+2, j));
	    if (structure == Attribute.BIB1_Structure_WordListOR ||
		structure == Attribute.BIB1_Structure_WordList ||
		structure == Attribute.BIB1_Structure_WordListAdj) 
		structure = Attribute.BIB1_Structure_Word; 
            t = new char[st.countTokens() - 1];
            if (debug)
                System.out.println("s=" + structure);
        }
        else
            t = new char[st.countTokens()];

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (debug)
                System.out.println("token='" + token + "'");

            use = -1;
            if (Character.isDigit(token.charAt(0)))
                use = Integer.parseInt(token);
            else if (token.startsWith("u"))
                use = Integer.parseInt(token.substring(2));
            else if (token.startsWith("newtonId"))
            {
                newtonId = Integer.parseInt(token.substring(9));
                t[next++] = (char)newtonId;
            }

            if (debug)
                System.out.println("u=" + use);

            if (use != -1)
            {
                if (structure != -1)
                {
                    map = indexMap.getMapbyUseStructure(use, structure);
                    if (map == null) 
                        throw new Diagnostic1(
                            Diagnostic1.unsupportedAttributeCombination, null);
                }
                else {
                    map = indexMap.getMapbyUse(use);
                    if (map == null)
                        throw new Diagnostic1(Diagnostic1.unsupportedUseAttribute,
                            String.valueOf(use));
                }
                t[next++] = (char)map.getIndexValue();
            }
        }
        if (next > 0)
        {
            answer = new String(t, 0, next);
            attributesToIndexes.put(attributes, answer);
            return answer;
        }
        return null;
    }

    static public boolean isWildChar(char c) {
	if (c == ZERO_OR_ONE_WILD || c == ZERO_OR_MORE_WILD ||
	    c == ONE_ONLY_WILD) 
	    return true;
	return false;
    }
}
