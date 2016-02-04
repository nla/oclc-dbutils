
package ORG.oclc.qparse;

import java.util.*;
import ORG.oclc.util.*;

/**
 * The IndexMap class maintains all the index information defined for 
 * a database including a list of Map objects defining each individual
 * index and the IndexLists that are used by the interface to build
 * html output.  The class also provides a set of methods for retrieving
 * index specification information.
 * @see Map
 * @see StopWord
 */

public class IndexMap {
  static final int INVALID_INDEX_NUM = -1;
  
  static final int TOPIC_ABBS      = 0;
  static final int TOPIC_NAMES     = 1;
  static final int TOPIC_INDEXTYPE = 2;
  
  static final int INFO_WORD_INDEX   = 1;
  static final int INFO_PHRASE_INDEX = 2;
  static final int INFO_RANGEABLE    = 4;

  static final int ABB_OP_UNDEF   = 0;
/**
  * integer defining the value for search operator 'and'.
  */
  public static final int ABB_OP_AND     = 1;
/**
  * integer defining the value for search operator 'with'.
  */
  public static final int ABB_OP_WITH    = 2;
/**
  * integer defining the value for search operator 'near'.
  */
  public static final int ABB_OP_NEAR    = 3;
/**
  * integer defining the value for search operator 'or'.
  */
  public static final int ABB_OP_OR      = 4;
  
  static final String DEFAULT_RANGE_MIN = "@default";
  static final String DEFAULT_RANGE_MAX = "@default";
  
/**
  * integer data type defining the value for proximity searching using
  * the <i>with</i> operator.
  */
  public static final int PROX_WITH  = 0;
/**
  * integer data type defining the value for proximity searching using
  * the <i>sec</i> operator.
  */
  public static final int PROX_SEC   = 1;
/**
  * integer data type defining the value for proximity searching using
  * the <i>parg</i> operator.
  */
  public static final int PROX_PARG  = 2;
  
/**
  * integer data type defining the value for the Z3950 use attribute word.
  */
  public static final int WORD   = 1;
/**
  * integer data type defining the value for the Z3950 use attribute phrase.
  */
  public static final int PHRASE = 2;
/**
  * integer data type defining the value for the Z3950 use attribute any.
  */
  public static final int ANY    = 3;
  
/**
  * String data type containing the Z3950 OID for this set of indices.
  */
  public  String OID;

/**
  * String data type containing the name of the section within the 
  * inifile that the configuration data is read.
  */
  public String sectionName;

  private int prox_nums[];
  private int default_operator;
  
  private boolean explainDb;
  protected String globalfilter;
  private Map    maps[];
  private Map    defaultSearchMap=null;
  private Map    defaultBrowseMap=null;
  
  private String[] pluralEndings = null;
  
  private StopWord stopWords;
  
  private Hashtable indexLists = null;

/**
  * Construct an IndexMap object.
  */
  public IndexMap() {
    prox_nums = new int[3];
    prox_nums[PROX_WITH] = 2;
    prox_nums[PROX_PARG] = 3;
    prox_nums[PROX_SEC] = 4;
    OID = ORG.oclc.z39.Attribute.BIB1;
    sectionName = "BIB1attributes";
  }
/**
  * Construct an IndexMap object using the data specified in the named 
  * section of the input IniFile object.
  */ 
  public IndexMap(String attrSection, IniFile ini) {
    init(attrSection, ini, false);
  }
  
  // The liteFlag indicates to set up use/structure/alternateId info only
  /**
   * Construct an IndexMap object using the data in the named section 
   * from the input IniFile object and only load information needed by
   * the ZBase server - a lite load to conserve memory.
   */
  public IndexMap(String attrSection, IniFile ini, boolean liteFlag) {
    init(attrSection, ini, liteFlag);
  }
  

  private void init(String attrSection, IniFile ini, boolean liteFlag) {
    String temp;
    String input;
    String tmpVal = null;
    int i, j, jj, k, attrCntr;
    int max_number_of_indexes=0;
    String sectionName, use, structure, normClass;
    String otherInfo = null;
    Map mapentry;
    String section = "IndexLists";
    String varNames[];
    String value = null;
    String index = null;

    Vector indexes = null;
    StringTokenizer list = null;
    Map m = null;
    int numIndexes = 0;    
    IniFileSection inisection = null;
    boolean done = false;
    boolean f101Trunc = false;
    
    this.sectionName = attrSection.intern();
    
    for (i = 0; ; i++) {
      temp = new String("index" + i);
      input = ini.getValue(attrSection, temp);
      if (input != null) {
        max_number_of_indexes++;
      }
      else if (i>0)
        break;
    }
    
    try {
      stopWords = new StopWord(this.sectionName,  ini);
    } catch (Exception e) { /* NULL catch */ }
    
    
    maps = new Map[max_number_of_indexes];
    this.explainDb = false;  
    
    input = ini.getValue(attrSection, "OID");
    OID = ORG.oclc.z39.Attribute.BIB1;
    
    if (input != null) {     
      if (input.equalsIgnoreCase("EXP1")) {
        OID = ORG.oclc.z39.Attribute.EXP1;
        explainDb = true;
      } else if (input.equalsIgnoreCase("ZDSR"))
        OID = ORG.oclc.z39.Attribute.ZDSR;
    }
    
    prox_nums = new int[3];
    prox_nums[PROX_WITH] = 2;
    prox_nums[PROX_PARG] = 3;
    prox_nums[PROX_SEC] = 4;
        
    globalfilter = ini.getValue(attrSection, "Filter",
           "ORG.oclc.qnorm.NewtonDbBaseTermNormalizer").intern();
    

    f101Trunc = ini.getBooleanValue(attrSection, "enable101Truncation", false);


    for (i = 0, j=0; j < max_number_of_indexes ; i++) {
      temp = new String("index" + i);
      mapentry = new Map();
      sectionName = ini.getValue(attrSection, temp);
      
      if (sectionName != null) {
        mapentry.name = sectionName;
        mapentry.Use = ini.getIntValue(sectionName, "use", -1);
        mapentry.Structure = ini.getIntValue(sectionName, "structure", -1);
        mapentry.alternateIdString = ini.getValue(sectionName, "alternateId");
        try {
          mapentry.alternateId = Integer.parseInt(mapentry.alternateIdString);
        }
        catch (Exception e) {} // it will be left at -1


        mapentry.Truncation   = ini.getValue(sectionName, "Truncation");
        mapentry.Relation     = ini.getValue(sectionName, "Relation");
        mapentry.Position     = ini.getValue(sectionName, "Position");
        mapentry.Completeness = ini.getValue(sectionName, "Completeness");
        //Hook to tag on user's crap
        otherInfo = ini.getValue(sectionName, "otherInfo");


        if (mapentry.Structure == 1 || mapentry.Structure >= 100) {
          mapentry.fPhrase = true;
          mapentry.fQuotes = true;
        } else
          mapentry.fQuotes = ini.getBooleanValue(sectionName, "AutoQuote", 
                                                 false);
        mapentry.cleanForQuery = ini.getBooleanValue(sectionName,
                                                     "cleanForQuery", true);
        
        mapentry.fRestrictor = ini.getBooleanValue(sectionName, 
                                                   "restrictor", false);
        mapentry.hideInLists = ini.getBooleanValue(sectionName, 
                                                   "hideInLists", false);

        mapentry.fRelation = ini.getBooleanValue(sectionName, 
                                                 "useRelationAttribute",
                                                 false);
        mapentry.fTrunc = 
            ini.getBooleanValue(sectionName, "enableTruncationMasks", true);

        mapentry.f101Trunc = f101Trunc;

        mapentry.fWithin = ini.getBooleanValue(sectionName, "supportsWithin",
                                               true);

        done = false;
        DataPairs rv = new DataPairs();
        int separator;
        StringBuffer buf = new StringBuffer();

        for (k=1; !done; k++) {
            value = ini.getValue(sectionName , "value"+k );

            if (value == null)
              done = true;
            else {
              separator = value.indexOf(":");
              if (separator != -1)
                rv.addElement(value.substring(0, separator), 
                       value.substring(separator+1));
              else 
                rv.addElement(value, "");
            } 
        }
        if (rv.size() > 0)
            mapentry.restrictorValues = rv;

        
        normClass = ini.getValue(sectionName, "Filter", globalfilter);
        if (normClass != null) {
           try { 
             Class c = Class.forName(normClass);
             mapentry.filter = (TermNormalizer)c.newInstance(); 
             mapentry.filter.initNormalizer(ini, sectionName, mapentry);
           } 
           catch (ClassNotFoundException e) { 
            System.out.println("Cannot load Filters class " + 
                 normClass + " " + e); 
           } 
           catch (Exception e1) { 
            System.out.println("Cannot load Filters class " + normClass +
                " " + e1); 
           } 

        }

        // Index specific stopwords.
        try {
          tmpVal = ini.getValue(sectionName, "stopwords");
          if (tmpVal != null) 
            mapentry.stopwords = new StopWord(sectionName,  ini);
          else if (ini.getValue(sectionName, "gowords") != null)
            mapentry.stopwords = new StopWord(sectionName,  ini);
        } catch (Exception e) { /* NULL */ }
 
        mapentry.fPlural = ini.getBooleanValue(sectionName, "plural", false);
        mapentry.fRange = ini.getBooleanValue(sectionName, "range", false);
        
        if (mapentry.fRange || mapentry.fRestrictor) {
          mapentry.range_min = ini.getValue(sectionName, "min");
          mapentry.range_max = ini.getValue(sectionName, "max");

          if (mapentry.range_min == null)
            mapentry.range_min = DEFAULT_RANGE_MIN;
          if (mapentry.range_max == null)
            mapentry.range_max = DEFAULT_RANGE_MAX;
        }        

        if (liteFlag == false) {
          mapentry.longName = ini.getValue(sectionName, "longname");
          
          if (mapentry.longName == null) 
            mapentry.longName = sectionName;
        }
          
        mapentry.abbrev = ini.getValue(sectionName, "abb");
        if (mapentry.abbrev == null) {
            System.out.println("DB File("+ini.fileName+
                               ") mapentry.abbrev not found ("+
                               sectionName +") setting to null.");
            mapentry.abbrev = "";
          }

        buf.setLength(0);           
          
        if (mapentry.Use != -1) {
              buf.append("u=");
              buf.append(mapentry.Use + ";");
          }

        if (mapentry.Structure != -1) {
              buf.append("s=");
              buf.append(mapentry.Structure + ";");
          }

        if (mapentry.Relation != null) {
              buf.append("r=");
              buf.append(mapentry.Relation + ";");
          }

        if (mapentry.Position != null) {
              buf.append("p=");
              buf.append(mapentry.Position + ";");
          }

        if (mapentry.Truncation != null) {
              buf.append("t=");
              buf.append(mapentry.Truncation + ";");
          }
          
        if (mapentry.Completeness != null) {
              buf.append("c=");
              buf.append(mapentry.Completeness + ";");
          }

        if (otherInfo != null) // should be of the form name=value;
              buf.append(otherInfo);

        mapentry.searchId = buf.toString();

        maps[j++] = mapentry;
      } else if (i > 0)
        break;
    }
        
    input = ini.getValue(attrSection, "default");
    if (input != null) {
      mapentry = getMapbyName(input);
      if (mapentry != null) 
        defaultSearchMap = mapentry;
      else
        defaultSearchMap = maps[0];
    }
    
    input = ini.getValue(attrSection, "browse_default");
    if (input != null) {
      mapentry = getMapbyName(input);
      if (mapentry != null)  
        defaultBrowseMap = mapentry;
      else
        defaultBrowseMap = maps[0];
    }
    
    default_operator = ini.getIntValue(attrSection, "operator", ABB_OP_AND);
    
 
    tmpVal = ini.getValue(attrSection, "plural_endings");
    setPluralEndings(tmpVal);

    if (OID.equals(ORG.oclc.z39.Attribute.BIB1)) {
      inisection = ini.getSection(section);
      if (inisection != null) {
        indexLists = new Hashtable(5);
        varNames = inisection.getKeys();

        if (varNames != null) {
          // Get each variable-value 
          for (i=0; i<varNames.length; i++) {
            value = ini.getValue(section, varNames[i]);
          
            if (value != null) {
              if (value.equalsIgnoreCase("all")) {
                indexes = new Vector(maps.length);

                for (j=0; j<maps.length; j++) {
                 if (maps[j].hideInLists == false) 
                  indexes.addElement(maps[j]);
                }

                indexLists.put(varNames[i].intern(), indexes);              
              }  else if (value.equalsIgnoreCase("allminusrestrictors")) {
                   indexes = new Vector(maps.length);

                   for (j=0; j<maps.length; j++) {
                      if (maps[j].isRestrictor() == false &&
                          maps[j].hideInLists == false)
                        indexes.addElement(maps[j]);
                   }
                   indexLists.put(varNames[i].intern(), indexes);
              }  else {
                list = new StringTokenizer(value, ", ");
                numIndexes = list.countTokens();

                indexes = new Vector(numIndexes);
        
                for (j=0; j<numIndexes; j++) {
                  index = list.nextToken();

                  m = null;
                  if (index.equalsIgnoreCase("words")) {
                    for (jj=0; jj<maps.length; jj++)
                      if (!maps[jj].hideInLists && maps[jj].Structure != 1 && 
                          maps[jj].Structure != 6  && maps[jj].Structure < 100)
                      indexes.addElement(maps[jj]);

                } else if (index.equalsIgnoreCase("phrases")) {
                  for (jj=0; jj<maps.length; jj++)
                    if (!maps[jj].hideInLists && (maps[jj].Structure == 1 ||
                        maps[jj].Structure == 6 ||
                        maps[jj].Structure >= 100  ))
                      indexes.addElement(maps[jj]);

                  } else {
                     m = getMapbyName(index);
                    if (m != null) {
                      indexes.addElement(m);
                    } else
                      System.out.println("DB File("+ini.fileName+
                                         ") Ignoring Index("+
                                         index+") not found.");
                                       
                  }
                }
                indexLists.put(varNames[i].intern(), indexes);
              }
            }
          }
        }
      }
    }
  }
  
  private final Map defaultBrowseMap() {
    if (defaultBrowseMap == null)
      setDefaultBrowseMap(null);
    return defaultBrowseMap;
  }
  private final Map defaultSearchMap() {
    if (defaultSearchMap == null)
      setDefaultSearchMap(null);
    return defaultSearchMap;
  }
  
  
  /**
   * Generates an IniFile type String representation of the object.
   * @return String
   */
  public String toIniString() {
    StringBuffer str = new StringBuffer();
    String OIDStr="BIB1";
    String section= "[Indexes]\n";
    if (OID.equals(ORG.oclc.z39.Attribute.BIB1))
      section = "[BIB1attributes]\n"; 
    else if (OID.equals(ORG.oclc.z39.Attribute.EXP1)) {
      section = "[EXP1attributes]\n";
      OIDStr = "EXP1";
    }
    else if (OID.equals(ORG.oclc.z39.Attribute.ZDSR)) {
      section = "[ZDSRattributes]\n";
      OIDStr = "ZDSR";
    }
    
    str.append(section);
    str.append(IniFile.makeString("OID", OIDStr));
    str.append(IniFile.makeInt("operator", default_operator));
    str.append(IniFile.makeString("default", defaultSearchMap().name));
    if (pluralEndings != null) {
      str.append("plural_endings = ");
      for (int i=0; i<pluralEndings.length; i++) {
        if (i>0)
          str.append(" ");
        str.append(pluralEndings[i]);
      }
      str.append("\n");
    }
// Not in use right now.    
    if (globalfilter != null)
      str.append(IniFile.makeString("Filter", globalfilter));

    
    String istrings[] = new String[maps.length];
    int i, cnt=0;
    for (i=0; i<maps.length; i++) {
      if (maps[i].name != null) {
        str.append(IniFile.makeString("index"+(cnt+1), maps[i].name));
        istrings[cnt++] = maps[i].toIniString(); 
      }
    }
    int stopoffset = str.length();
    str.append("\n");
    for (i=0; i<cnt; i++)  {
      str.append(istrings[i]);
    }

    if (stopWords != null)
      str.insert(stopoffset,
         IniFile.makeString("stopwords", stopWords.toIniString()));
    
    return str.toString();
  }


  /**
   * Generates a String representation of the object.
   * @return String
   */        
  public String toString() {
    StringBuffer outStr = new StringBuffer();
    int i;
    String name = null;
    
    if (defaultSearchMap != null)
      outStr.append("Search default:\n" + defaultSearchMap + "\n");
    if (defaultBrowseMap != null)
      outStr.append("Browse default:\n" + defaultBrowseMap + "'\n");
    
    outStr.append("OID Map: " + OID +" \n");
    if (globalfilter != null)
      outStr.append("Default Db Normalization Routine: '" + globalfilter + "'\n\n"); 
    
    for (i=0; i<maps.length; i++) 
      if (maps[i] != null) 
        outStr.append("index(" + (i+1) + "):\n" + maps[i].toString() + "\n");
    
    if (pluralEndings != null)
      for (i=0; i<pluralEndings.length; i++) 
        if (pluralEndings[i] != null) 
          outStr.append("pluralEndings("+(i+1)+"): "+pluralEndings[i]+"\n");

    if (stopWords != null)
      outStr.append("\nStopword List:\n"+stopWords.toString());
    
    if (indexLists != null) {
      outStr.append("\nIndex Lists :\n");
      i = 1;
      for (Enumeration ee = indexLists.keys(); ee.hasMoreElements(); i++) {
        name = (String)ee.nextElement();
        outStr.append("  map["+i+"]='"+name+"'\n");
      }
    }
    return outStr.toString();
  }
  
  /**
   * Returns a String containing the Z3950 OID for this index specification.
   * @return String
   */
  public final String OID() {
    return OID;
  }
    
  /** 
   * Returns a String containing the section within the input IniFile object
   * where this information was read from.
   * @return String
   */
  public final String sectionName() {
    return sectionName;
  }
  /**
   * Returns an array of Map objects for this IndexMap.
   * @return Map[]
   */
  public final Map[] getMap() {
    return maps;
  }
  /**
  * Sets the default searching operator.
  * @param operator the integer value for the operator.
  */
  public void setDefaultOperator(int operator) {
    default_operator = operator;
  }
  
/**
  * Sets the default search Map object for the database to the input
  * Map object.
  * @param  Map object.
  */
  public void setDefaultSearchMap(Map map) {  
    if (map ==null)
      defaultSearchMap = maps[0];
    else 
      defaultSearchMap=map;
    
  }
  /**
  * Sets the default browse Map object for the database to the input
  * Map object.
  * @param  Map object.
  */
  public void setDefaultBrowseMap(Map map) {
    if (map == null) 
      defaultBrowseMap = maps[0];
    else
      defaultBrowseMap=map;
  }


  /**
  * Sets the plural endings for the database to the input string.
  * @String endings a space separated list of plural endings for plural 
  * searching.
  */
  public void setPluralEndings(String endings) {
    
    int i;
    if (endings != null) {
      StringTokenizer st = new StringTokenizer(endings, " ,");
      int size = st.countTokens();
      
      pluralEndings = new String[size + 1];
      pluralEndings[0] = "";
      for (i = 1; i < size; i++)
        pluralEndings[i] = st.nextToken().intern();
    }
    else {
      pluralEndings = new String[3];
      pluralEndings[0] = "";
      pluralEndings[1] = "s";
      pluralEndings[2] = "es";
    }
  }
  
  /**
   * Adds a new Map object to the IndexMap.
   * @ param map the object to add
   */
  public final void addMap(Map map) {
    Map newmaps[];
    int dest = 0;
    if (maps != null) {
      newmaps = new Map[maps.length +1];
      System.arraycopy(maps, 0, newmaps, 0, maps.length);
      dest = maps.length;
    }
    else
      newmaps = new Map[1];
          
    newmaps[dest] = map;
    maps = newmaps;
    
  }
  /**
   * Deletes a Map object in the IndexMap.
   * @ param map the object to delete
   */
  public final void deleteMap(Map map) {
    Map newmaps[]=null;
    int dest = 0;
    if (maps == null || map == null)
        return;

    newmaps = new Map[maps.length - 1];

    int j=0;
    for (int i=0; i<maps.length; i++) 
        if (maps[i] != map && j < newmaps.length)
            newmaps[j++] = maps[i];
          
    maps = newmaps;
    
  }

  /**
   * Resets the array of Map entries to the input array.
   * @param map the array of Map objects
   */
  public final void setMap(Map[] map) {
    maps = map;
  }
  
/**
  * Retrieves a Map object for the input string based on the 'abbrev' 
  * field in the Map.
  * @return Map
  */
  public final Map getMapbyAbb(String abb) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++) {
      if (maps[i] != null && maps[i].abbrev != null && 
          maps[i].abbrev.equals(abb))
        return maps[i];
    }
    return null;
  }
  /**
   * Retrieves a Map object for the input string based on the 'name' field 
   * in the Map.
   * @return Map.
   */
  public final Map getMapbyName(String name) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++) {
      if (maps[i] != null) {
        if (maps[i].name != null && maps[i].name.equalsIgnoreCase(name))
          return maps[i];
        //        else if (maps[i].abbrev != null && 
        //                 maps[i].abbrev.equalsIgnoreCase(name))
        //          return maps[i];
      }
    }
    return null;
  }

 /**
   * Retrieves a Map object for the input string based on the 'longName'
   * field in the Map.
   * @return Map.
   */
  public final Map getMapbyLongname(String name) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++) {
      if (maps[i] != null) {
       if (maps[i].longName != null && maps[i].longName.equalsIgnoreCase(name))
            
         return maps[i];
        //        else if (maps[i].abbrev != null && 
        //                 maps[i].abbrev.equalsIgnoreCase(name))
        //          return maps[i];
      }
    }
    return null;
  }
  
   /**
   * Retrieves a Map object for the input integer based on the 'Use' field
   * in the Map.
   * @return Map.
   */
  public final Map getMapbyIndex(int index) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].Use == index)
        return maps[i];
    
    return null;
  }
   /**
   * Retrieves a Map object for the input String based on the 'Use' field
   * in the Map.
   * @return Map.
   */
  public final Map getMapbyIndex(String index) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].Use != -1 && String.valueOf(maps[i].Use).equals(index))
        return maps[i];
    return null;
  }
  
 /**
   * Retrieves a Map object for the input use and structure based on
   * the 'Use' and 'Structure' fields in the Map. 
   * @return Map.
   */
  public final Map getMapbyUseStructure(int use, int structure) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].Use == use && maps[i].Structure == structure)
        return maps[i];
    return null;
  }

 /**
   * Retrieves a Map object for the input integer based on the 'Use' field
   * in the Map.
   * @return Map.
   */  
  public final Map getMapbyUse(int use) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].Use == use)
        return maps[i];
    return null;
  }
  
 /**
   * Retrieves a Map object for the input integer based on the 'alternateId'
   * field in the Map.
   * @return Map.
   */
  public final Map getMapbyAlternateIndex(int index) {
    if (maps == null)
      return null;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].alternateId == index)
        return maps[i];
    return null;
  }
  
   /**
   * Retrieves an integer containing the alternateId value from a Map object
   * base on the input 'use'.
   * @return int
   */
  public final int getAlternateIdbyUse(int use) {
    if (maps == null)
      return -1;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].Use == use)
        return maps[i].alternateId;
    return -1;
  }
  /**
   * Retrieves a String containing the alternateId value from a Map object
   * base on the input 'use'.
   * @return String or null
   */
  public final String getAlternateIdStringbyUse(int use) {
    if (maps == null)
      return null;

    for (int i=0; i<maps.length; i++)
      if (maps[i].Use == use)
        return maps[i].alternateIdString;
    return null;
  }  
   /**
   * Retrieves an integer containing the alternateId value from a Map object
   * base on the input 'use' and 'structure' 
   * @return int
   */
  public final int getAlternateIdbyUseStructure(int use, int structure) {
    if (maps == null)
      return -1;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].Use == use && maps[i].Structure == structure)
        return maps[i].alternateId;
    return -1;
  }
  /**
   * Retrieves a String containing the alternateId value from a Map object
   * base on the input 'use' and 'structure'
   * @return String or null
   */
  public final String getAlternateIdStringbyUseStructure(int use, int structure) {
    if (maps == null)
      return null;

    for (int i=0; i<maps.length; i++)
      if (maps[i].Use == use && maps[i].Structure == structure)
        return maps[i].alternateIdString;
    return null;
  }

   /**
   * Retrieves an integer containing the Use value from a Map object
   * base on the input 'alternateId'.
   * @return int
   */
  public final int getUsebyAlternateId(int alternateId) {
    if (maps == null)
      return -1;
    
    for (int i=0; i<maps.length; i++)
      if (maps[i].alternateId == alternateId)
        return maps[i].Use;
    return -1;
  }
  

   /**
   * Retrieves an integer containing the default alternateId for the
   * IndexMap object. 
   * @param in_scan indicates whether to return the default for
   * search(false) or browse(true)
   * @return int
   */
  public final int getDefaultAlternateId(boolean in_scan) {
    
    Map dMap = getDefaultMap(in_scan);
    return dMap.alternateId;
  }
   /**
   * Retrieves a String containing a formatted use/structure 
   * for the default searching indices in the IndexMap object.
   * @param in_scan indicates whether to return the default for
   * search(false) or browse(true)
   * @return String
   */
  public final String getDefaultId(boolean in_scan) {
    return in_scan ? defaultBrowseMap.searchId : defaultSearchMap.searchId;
  }
  
    /**
   * Retrieves a Map object 
   * for the default searching indices in the IndexMap object.
   * @param in_scan indicates whether to return the default for
   * search(false) or browse(true)
   * @return String
   */
  public final Map getDefaultMap(boolean in_scan) {
    return in_scan ? defaultBrowseMap() : defaultSearchMap();
  }
  
  /**
   * Retrieves an integer for the proximity number from the 
   * input proximity type.
   * @return int
   */
  public final int prox_num(int prox_type) {
    return prox_nums[prox_type];
  }
  
/**
   * Retrieves an integer for the default operator for the IndexMap.
   * @return int.
  */
  public final int getDefaultOp() {
    return default_operator;
  }
  
/**
  * Returns an array of Strings containing the plural endings for 
  * plural searching in the database.
  * @return String[]
  */ 
  public final String[] getPluralEndings() {
    return pluralEndings;
  }
  /**
   * Returns the StopWord object for search stopwords.
  * @return StopWord
  */
  public final StopWord getStopWords() {
    return stopWords;
  }

  /** 
   * Sets the StopWord object to the input StopWord object.
   * @param StopWord 
   */
 public final void setStopWords(StopWord stopwords) {
    stopWords = stopwords;
  }

  /**
   * Retrieves a Vector containing a list of Map objects for the
   * input list name.
   * @param name the name of the indexlist to retrieve.
   * @returns Vector
   */ 
  public final Vector getIndexList(String name) {
    Vector v = null;

    if (name != null)
      if (indexLists != null)
        v = (Vector)indexLists.get(name); 

    return v;
  }
    
/**
  * Creates a String representation for a numeric searching operator.
  * @param operator 
  * @return String "and", "or", "not", "with"
  */
  public final String opToString(int operator) {
    switch (operator) {
    case ABB_OP_AND:
      return "and";
    case ABB_OP_WITH: 
      return "with";
    case ABB_OP_NEAR:
      return "near";
    case ABB_OP_OR:
      return "or";
    }
    return "and";
  }

    /**
     * Clones a copy of the object.
     * @return Object
     */
    public Object clone() {
        IndexMap im = new IndexMap();
        im.defaultSearchMap = defaultSearchMap;
        im.defaultBrowseMap = defaultBrowseMap;
        im.OID = OID;
        im.globalfilter = globalfilter;
        im.maps = new Map[maps.length];
     
        for (int i =0; i<maps.length; i++) {
            if (maps[i] != null)  
                im.maps[i] = (Map)maps[i].clone();
        }

        im.sectionName = sectionName; 
 
        im.prox_nums = prox_nums;
        im.default_operator = default_operator;
        im.explainDb = explainDb;
        im.pluralEndings = pluralEndings;
        im.stopWords = stopWords; 
        im.indexLists = indexLists;   
        return (Object)im;

    } 

}


