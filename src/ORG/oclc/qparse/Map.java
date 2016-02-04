/**
 */

package ORG.oclc.qparse;

import java.util.*;

import ORG.oclc.util.*;
/**
  * The Map class maintains an individual index definition and provides
  * methods to access the data.
  * @see IndexMap
  */
public class Map {
/**
  * integer data type for the Z3950 use attribute value.
  */ 
  protected int      Use = -1;
/**
  * integer data type for the Z3950 structure attribute value.
  */
  protected int      Structure = -1;

    /**
     * The Z3950 Relation attribute value.
     */
    protected String Relation = null;

    /**
     * The Z3950 Position attribute value.
     */
    protected String Position = null;

    /**
     * The Z3950 Truncation attribute value.
     */
    protected String Truncation = null;

    /**
     * The Z3950 Completeness attribute value.
     */
    protected String Completeness = null;


/**
  * integer data type for the newton IndexId for local databases 
  * and the "real" use attribute value for remote Z39 databases.
  */
  protected int      alternateId = -1;
/**
  * String data type containing the alternateId value that
  * is mapped from the incoming Z3950 use/structure attributes.
  */
  protected String   alternateIdString;
/**
  * String data type containing the fully qualified Z3950 search string.
  */
  protected String   searchId = null;
/**
  * boolean data type indicating whether to treat the index as a phrase.
  */
  protected boolean  fPhrase = false;
/**
  * boolean data type indicating whether to automatically add 
  * Quotes to the term this index this qualifies.
  */
  protected boolean  fQuotes = false;

/**
  * boolean data type indicating whether to allow plural searching.
  */
  protected boolean  fPlural = false; //aka can_be_plural
/**
  * boolean data type indicating whether to allow range searching.
  */
  protected boolean  fRange  = false;
/**
  * boolean data type indicating whether to allow Z39.50 Realtion Attributes.
  */
  protected boolean  fRelation  = false;

/**
  * boolean data type indicating whether to allow the "within" 
  * Z39.50 Realtion op. (r=104;)
  */
  protected boolean  fWithin  = false;

/**
  * boolean data type indicating whether to allow Z39.50 Truncation Attributes.
  */
  protected boolean  fTrunc  = true;
/**
  * boolean data type indicating whether to allow the Z39.50 BIB1 101 
  * Truncation Attributes.
  */
  protected boolean  f101Trunc  = false;
/**
  * String data type containing the minimum for the range.
  */
  protected String   range_min = null;
/**
  * String data type containg the maximum for ranges.
  */
  protected String   range_max = null;
/**
  * String data type containing the index abbreviation.
  */
  protected String   abbrev = null;
/**
  * String data type containing the long name of the index.
  */
  protected String   longName = null; 
/**
  * String data type containing the name of the index.
  */
  protected String name = null;
 
/**
  * boolean data type indicating whether index is a restrictor - meaning it is
  * not searchable unless it appears in a boolean search.
  */
  protected boolean  fRestrictor = false; 

  /**
   * boolean data type indicating whether this index should appear in any
   * of the [indexlists] when the shorthand notation is used such as 
   * 'all', 'phrases', 'words'.
   */
  protected boolean hideInLists=false;

/**
  * DataPairs object containing a list of strings to search 
  * the restrictor with.
  * <br>DisplayString=InternalSearchString (e.g., English:eng)
  */
  protected DataPairs restrictorValues = null; 

/**
  * boolean data type indicating whether default query 
  * filtering should be performed for terms using this index 
  * where default = true.
  */
  protected boolean  cleanForQuery=true;
/**
  * TermNormalizer object for the index to provide specialized
  * query filter capabilities for the query term in this index.
  */
  protected TermNormalizer filter = null;


/**
  * Index Specific stopwords list.
  */
  protected StopWord stopwords = null;
    
/**
  * Construct a Map object.
  */
  public Map() {

  }
/**
  * Sets the alternateId value.
  * @param id the value
  */
  public final void setalternateId(int id) {
     alternateId = id;
     alternateIdString = String.valueOf(id).intern();
  }
  /**
   * Sets the alternateId value to the incoming String.
   * @param id the value
   */
  public final void setalternateId(String id) {
    alternateIdString = id.intern(); 
    try {
     alternateId = Integer.parseInt(id);
    }
    catch (Exception e) {};
  }
/**
  * Sets the use attribute value.
  * @param use
  */  
  public final void setUse(int use) {
     Use = use;
  }
/**
  * Sets the structure attribute value.
  * @param structure 
  */
  public final void setStructure(int structure) {
     Structure = structure;
  }

    /**
     * Sets the Relation attribute value.
     * @param relation
     */
    public final void setRelation(String relation) {
        if (relation == null || relation.length() == 0)
            return; 

        Relation = relation.intern();
    }

    /**
     * Sets the Position attribute value.
     * @param position
     */
    public final void setPosition(String position) {
        if (position == null || position.length() ==0)
            return;
        Position = position.intern();
    }

    /**
     * Sets the Truncation attribute value.
     * @param truncation
     */
    public final void setTruncation(String truncation) {
        if (truncation == null || truncation.length() == 0)
            return;
        Truncation = truncation.intern();
    }

    /**
     * Sets the Completeness attribute value.
     * @param completeness
     */
    public final void setCompleteness(String completeness) {
        if (completeness == null || completeness.length() == 0)
            return; 
        Completeness = completeness.intern();
    }
    



/**
  * Sets the abbreviation.
  * @param abb
  */
  public final void setAbb(String abb) {
     abbrev = abb.intern();
  }
/**
  * Sets the range flag.
  * @param flag 
  */
  public final void setRangeFlag(boolean flag) {
     fRange = flag;
  }
/**
  * Sets the plural flag.
  * @param flag
  */
  public final void setPluralFlag(boolean flag) {
     fPlural = flag;
  } 
/**
  * Sets the Truncation flag.
  * @param flag
  */
  public final void setTruncationFlag(boolean flag) {
     fTrunc = flag;
  }
/**
  * Sets the 101 Truncation flag.
  * @param flag
  */
  public final void set101TruncationFlag(boolean flag) {
     f101Trunc = flag;
  }


/**
  * Sets the restrictor flag.
  * @param flag
  */
  public final void setRestrictorFlag(boolean flag) {
     fRestrictor = flag;
  } 
/**
  * Sets the range maximum.
  * @param max
  */
  public final void setRangeMax(String max) {
     range_max = max.intern();
  }
/**
  * Sets the range minimum.
  * @param min
  */
  public final void setRangeMin(String min) {
     range_min = min.intern();
  }
/**
  * Sets the textual long name of the index.
  * @param name
  */
  public final void setLongName(String name) {
     longName = name.intern();
  }   
/**
  * Sets the name of the index.
  * @param name
  */
  public final void setName(String name) {
     this.name =  name.intern();
  }
/**
  * Returns a String containing the index abbreviation.
  * @return String
  */
  public final String getAbb() {
    if (abbrev != null)
      return abbrev;
    return "";
  }
  /**
   * Returns a String containing the alternateId value.
   * @return String
   */
  public final String getAlternateIdString() {
    return alternateIdString;
  }
/**
  * Returns an integer containing the alternateId value.
  * @return int
  */ 
  public final int getIndexValue() {
    return alternateId;
  }
/**
  * Returns a String containing the name of the index.
  * @return String
  */
  public final String getName() {
    return name;
  }
/**
  * Returns a String containing the long name of the index.
  * @return String
  */
  public final String getLongName() {
    return longName;
  }

  /** 
   * Returns a String containing the name of the index. 
   * @return String 
   */ 
  public final String name() { 
    return name;
  } 
    /**
     * Gets the Relation attribute value.
     * @return String
     */
    public final int getRelation() {
        int value = -1;
        if (Relation != null)
             value = Integer.parseInt(Relation);

        return value;
    }

    /** 
     * Gets the String defining the Relation attribute value according to
     * the Z39.50 Standard definition 
     * @return String 
     */ 
    public final String getRelationString() { 
        int value = -1; 
        String s=null;
        if (Relation == null) 
            return null;

        value = Integer.parseInt(Relation); 
        switch (value) {
        case 1: s = "less than"; break;
        case 2: s = "less than or equal to"; break;
        case 3: s = "equal"; break;
        case 4: s = "greater or equal"; break;
        case 5: s = "greater than"; break;
        case 6: s = "not equal"; break;
        case 100: s = "phonetic"; break;
        case 101: s = "stem"; break;
        case 102: s = "relevance"; break;
        case 103: s = "always matches"; break;
        }
 
        return s;
    } 

    /**
     * Gets the Position attribute value.
     * @return String 
     */
    public final int getPosition() {
        int value = -1;
        if (Position != null)
             value = Integer.parseInt(Position);

        return value;
    }

    /**  
     * Gets the String defining the Position attribute value according to 
     * the Z39.50 Standard definition  
     * @return String  
     */  
    public final String getPositionString() {  
        int value = -1;  
        String s=null; 
        if (Position == null)  
            return null; 
 
        value = Integer.parseInt(Position);
        switch (value) { 
        case 1: s ="first in field"; break;
        case 2: s ="first in subfield"; break;
        case 3: s ="any position in field"; break;
        }
        return s;
    }


    /**
     * Gets the Truncation attribute value.
     * @return String 
     */
    public final int getTruncation() {
        int value = -1;
        if (Truncation != null)
             value = Integer.parseInt(Truncation);

        return value;
    }

    /**   
     * Gets the String defining the Truncation attribute value according to  
     * the Z39.50 Standard definition   
     * @return String   
     */   
    public final String getTruncationString() {   
        int value = -1;   
        String s=null;  
        if (Truncation == null)   
            return null;  
  
        value = Integer.parseInt(Truncation); 
        switch (value) {  
        case 1: s ="right truncation"; break; 
        case 2: s ="left truncation"; break; 
        case 3: s ="left and right"; break; 
        case 100: s ="do not truncate"; break;
        case 101: s ="process # in search term"; break;
        case 102: s ="regExpr-1"; break;
        case 103: s ="regExpr-2"; break;
        case 104: s ="104"; break;
        } 
        return s; 
    }

    /**
     * Gets the Completeness attribute value.
     * @return String 
     */
    public final int getCompleteness() {
        int value = -1;
        if (Completeness != null)
             value = Integer.parseInt(Completeness);

        return value;
    }

    /**   
     * Gets the String defining the Completeness attribute value according to  
     * the Z39.50 Standard definition   
     * @return String   
     */   
    public final String getCompletenessString() {   
        int value = -1;   
        String s=null;  
        if (Completeness == null)   
            return null;  
  
        value = Integer.parseInt(Completeness); 
        switch (value) {  
        case 1: s ="incomplete subfield"; break; 
        case 2: s ="complete subfield"; break; 
        case 3: s ="complete field"; break; 
        } 
        return s; 
    }

/**
  * Returns a Class object for normalizing(filtering) the query.
  * @deprecated use termNormalizerClass() 
  * @return Class 
  */
  public final Class queryNormalizerClass(){
    return termNormalizerClass();
  }

/**
  * Returns a Class object for normalizing(filtering) the query.
  * @return Class 
  */
  public final Class termNormalizerClass(){
   if (filter != null)
     return ((Object)filter).getClass();
   return null;
  }

/**
  * Returns the TermNormalizer object for filtering the term.
  * @return TermNormalizer
  */
  public final TermNormalizer termNormalizerObject(){
    return filter;
  }

  /**
   * Returns the index specific StopWord object.
   * @return StopWord
   */ 
  public final StopWord stopwords() {
    return stopwords;
  }

  public void putIniData(IniFile ini, IndexMap parent) {
      ini.putValue(name, "longname", longName);
      ini.putValue(name, "abb", abbrev);
      ini.putIntValue(name, "use", Use);
      ini.putIntValue(name, "structure", Structure);
      if (alternateId != -1)
          ini.putIntValue(name, "alternateId", alternateId);
      if (Completeness != null)
          ini.putValue(name, "completeness", Completeness);
      if (Truncation != null)
          ini.putValue(name, "truncation", Truncation);
      if (Position != null)
          ini.putValue(name, "position", Position);
      if (Relation != null)
          ini.putValue(name, "relation", Relation);
      if (fRange == true) 
          ini.putBooleanValue(name, "range", fRange);
      if (range_min != null)
          ini.putValue(name, "min", range_min);
      if (range_max != null)
          ini.putValue(name, "max", range_max);
      if (fPlural == true)
          ini.putBooleanValue(name, "plural", fPlural);
      if (Structure != 1 && Structure < 100 && fQuotes) 
          ini.putBooleanValue(name, "AutoQuote", fQuotes);
      if (!cleanForQuery)
          ini.putBooleanValue(name, "cleanForQuery", cleanForQuery);
      if (hideInLists)
          ini.putBooleanValue(name, "hideInLists", hideInLists);
      if (fRelation)
          ini.putBooleanValue(name, "useRelationAttribute", fRelation);
      if (!fTrunc)
          ini.putBooleanValue(name, "enableTruncationMasks", fTrunc);
      if (!f101Trunc)
          ini.putBooleanValue(name, "enable101Truncation", f101Trunc);
      if (!fWithin)
          ini.putBooleanValue(name, "supportsWithin", fWithin);
      if (fRestrictor)
          ini.putBooleanValue(name, "restrictor", fRestrictor);

      if (stopwords != null && !stopwords.toIniString().equals("default"))
          ini.putValue(name, "stopwords", stopwords.toIniString());

      if (restrictorValues != null) {
          for (int i=0; i<restrictorValues.size(); i++) {
              DataPair d = (DataPair)restrictorValues.elementAt(i);
              ini.putValue(name, "value"+(i+1), d.name() + ":" + d.get());
          }
      }
      if (filter != null && 
          !filter.getClass().getName().equals(parent.globalfilter)) {
          ini.putValue(name, "filter", filter.getClass().getName());
      }
       
    }
/**
  * Generates an IniFile type string representation of the object.
  * @return String
  */
  public String toIniString() {
    StringBuffer str = new StringBuffer();
    str.append("[");
    str.append(name);
    str.append("]\n");
    str.append(IniFile.makeString("longname", longName));
    str.append(IniFile.makeString("abb", abbrev));
    str.append(IniFile.makeInt("use", Use, -1));
    str.append(IniFile.makeInt("structure", Structure, -1));
    str.append(IniFile.makeString("alternateId", alternateIdString));
    str.append(IniFile.makeBoolean("plural", fPlural, false));
    str.append(IniFile.makeBoolean("range", fRange, false));
    if (range_min!=null && !range_min.equals(IndexMap.DEFAULT_RANGE_MIN))
      str.append(IniFile.makeString("min", range_min));
    if (range_max != null && !range_max.equals(IndexMap.DEFAULT_RANGE_MAX))
      str.append(IniFile.makeString("max", range_max));

    str.append(IniFile.makeBoolean("restrictor", fRestrictor, false));
    str.append(IniFile.makeBoolean("cleanForQuery",
                   cleanForQuery, true));
    if (filter != null) 
      str.append(IniFile.makeString("Filter", filter.getClass().getName()));

    if (stopwords != null) 
      str.append(stopwords.toIniString());
                 
    str.append("\n");

    return str.toString();
  } 
/**
  * Generates a String representation of the object.
  * @return String
  */
  public String toString() {
    StringBuffer outStr = new StringBuffer();

    outStr.append(" name                 = '" + name + "'\n");
    outStr.append(" title                = '" + longName + "'\n");
    outStr.append(" abbrev               = '" + abbrev + "'\n");
    outStr.append(" searchId             = '" + searchId + "'\n");
    outStr.append(" use                  = '" + Use + "'\n"); 
    outStr.append(" relation             = '" + Relation + "'\n");
    outStr.append(" position             = '" + Position + "'\n");
    outStr.append(" structure            = '" + Structure + "'\n");
    outStr.append(" truncation           = '" + Truncation + "'\n");
    outStr.append(" completeness         = '" + Completeness + "'\n");

    if (alternateIdString != null)
      outStr.append(" altId                = '" + alternateIdString + "'\n");
    if (fPhrase)
      outStr.append(" Phrase flag          = "  + fPhrase+ "\n");
    if (fPlural)
      outStr.append(" Plural flag          = "  + fPlural + "\n");
    if (fRange)
      outStr.append(" Range flag           = '"  + fRange + "'\n");
    if (range_min != null && !range_min.equals(IndexMap.DEFAULT_RANGE_MIN))
      outStr.append(" Range min            = '"  + range_min + "'\n");
    if (range_max != null && !range_max.equals(IndexMap.DEFAULT_RANGE_MAX))
      outStr.append(" Range max            = '"  + range_max + "'\n");

    outStr.append(" cleanForQuery        = " + cleanForQuery + "\n");
    if (filter != null)
      outStr.append(" Normalization Routine='"+ filter.getClass().getName()+"'\n");

    outStr.append(" restrictor flag      = '" + fRestrictor + "'\n");
    outStr.append(" relation attr. flag  = '" + fRelation + "'\n");
    outStr.append(" supports within flag = '" + fWithin + "'\n");
    outStr.append(" enable trunc masks   = '" + fTrunc + "'\n");
    outStr.append(" enable 101 trunc     = '" + f101Trunc + "'\n");
    if (restrictorValues != null) {
      outStr.append(restrictorValues);
    }
    if (stopwords != null) 
      outStr.append(stopwords.toString());

    return outStr.toString();
  }
/**
  * Returns a String containing the fully qualified 
  * String to add to the Z39.50 type query.
  * @return String
  */
  public final String getIndex() {
    if (searchId != null) 
      return searchId;
    else 
      return alternateIdString;
  }
/**
  * Returns a boolean indicating whether the query should be cleaned up when
  * making a Z3950 query.
  * @return boolean
  */
  public final boolean cleanForQuery() {
    return cleanForQuery;
  }
/**
  * Returns boolean indicating whether index is a phrase type.
  * @return boolean
  */ 
  public final boolean isPhrase() {
    return fPhrase;
  }

/**
  * Sets the flag indicating that the index is a phrase type index
  */ 
  public final void setPhrase() {
      fPhrase = true;
      fQuotes=true;
  }

/**
  * Sets the filter up for the index
  */ 
  public final void setFilter(String classname) {

      try {  
          Class c = Class.forName(classname);
          filter = (TermNormalizer)c.newInstance();  
          //filter.initNormalizer(ini, sectionName, this);
      }  
      catch (ClassNotFoundException e) {  
          System.out.println("Cannot load Filters class " +  
                             classname + " " + e);  
      }  
      catch (Exception e1) {  
          System.out.println("Cannot load Filters class " + classname + 
                             " " + e1);  
      }  

  }

/**
  * Returns boolean indicating whether index is should be included
  * in any shorthand lists for [indexlists]
  * @return boolean
  */ 
  public final boolean hideInLists() {
    return hideInLists;
  }

/**
  * Returns boolean indicating whether index is a restrictor.
  * @return boolean
  */ 
  public final boolean isRestrictor() {
    return fRestrictor;
  }

/**
  * Returns a DataPairs object with the list of possible 
  * name=value pairs for the index.
  * @return DataPairs 
  */ 
  public final DataPairs getRestrictorValues() {
    return restrictorValues;
  }


  /**
   * Returns boolean indicating whether the object the index qualifies should
   * automatically have quotes added (during term collection).
   * Indexes with structure phrase are quoted by default.
   * @return boolean
   */ 
  public final boolean doQuotes() {
    return fQuotes;
  }


  /**
   * Returns boolean indicating whether the object the index qualifies should
   * automatically have Relation attributes added. 
   * (e.g., 1990-2100/u=30;s=5;r=3;)
   * @return boolean
   */ 
  public final boolean doRelation() {
    return fRelation;
  }


/**
  * Returns the integer for the Use attribute value.
  * @return int
  */
  public final int getUse() {
    return Use;
  }
/**
  * Returns the integer for the structure attribute value.
  * @return int
  */
  public final int getStructure() {
    return Structure;
  }

    /**    
     * Gets the String defining the Structure attribute value according to   
     * the Z39.50 Standard definition    
     * @return String    
     */    
    public final String getStructureString() {    
        String s=null;   

        switch (Structure) {   
        case 1: s ="phrase"; break;
        case 2: s ="word"; break;
        case 3: s ="key"; break;
        case 4: s = "year"; break;
        case 5: s = "date (normalized)"; break;
        case 6: s = "word list"; break;
        case 100: s = "date (un-normalized)"; break;
        case 101: s = "name (normalized)"; break;
        case 102: s = "name (un-normalized)"; break;
        case 103: s = "structure"; break;
        case 104: s = "urx"; break;
        case 105: s = "free-form-text"; break;
        case 106: s = "document-text"; break;
        case 107: s = "local number"; break;
        case 108: s = "string"; break;
        case 109: s = "numeric string"; break;

        }  
        return s;  
    } 

/**
  * Returns a String containing the range minimum value.
  * @return String
  */
  public final String getRangeMin(String term) {
    if (range_min == null)
      return null;

    if (range_min.equals(IndexMap.DEFAULT_RANGE_MIN))
      return Character.isDigit(term.charAt(0)) ? "0" : "a";
    else
      return range_min;
  }
/**
  * Returns a String containing the range maximum value.
  * @return String
  */
  public final String getRangeMax(String term) {
    if (range_max == null)
      return null;

    if (range_max.equals(IndexMap.DEFAULT_RANGE_MAX))
      return Character.isDigit(term.charAt(0)) ? "999999999":"zzzzzzzzz";
    else
      return range_max;
  }

/**
  * Returns boolean indicating whether the plural searching is available.
  * @return boolean
  */
  public final boolean canBePlural() {
    return fPlural;
  }
/**
  * Returns boolean indicating whether the plural searching is active.
  * @return boolean
  */
  public final boolean isPlural() {
    return fPlural;
  }

/**
  * Returns boolean indicating whether to look for truncation masks  
  * ('#', '*', or '?') in search terms.
  * @return boolean
  */
  public final boolean allowTruncation() {
    return fTrunc;
  }
/**
  * Returns boolean indicating whether to allow the use of 101 BIB1 
  * truncation masks ('#') in RPN queries.
  * @return boolean
  */
  public final boolean allow101Truncation() {
    return f101Trunc;
  }


/**
  * Returns boolean indicating whether to target supports within relation
  * operator (r=104;)
  * @return boolean
  */
  public final boolean allowWithin() {
    return fWithin;
  }


/**
  * Returns boolean indicating whether the index is rangeable.
  * @return boolean
  */  
  public final boolean isRangeable() {
      //return range_min != null && range_max != null;
      return fRange;
  }

  /**
   * Creates a copy of the Object.
   * @return Object
   */
  public Object clone() {
    Map m = new Map();
    m.Use = Use;
    m.Structure = Structure;
    m.Relation = Relation;
    m.Position = Position;
    m.Truncation = Truncation;
    m.Completeness = Completeness;
    m.alternateId = alternateId;
    m.alternateIdString = alternateIdString; 
    m.searchId = searchId;
    m.fPhrase = fPhrase;
    m.fQuotes = fQuotes;
    m.fPlural = fPlural;
    m.fRange  = fRange;
    m.fRelation  = fRelation;

    m.fWithin  = fWithin;
    m.fTrunc  = fTrunc;
    m.f101Trunc  = f101Trunc;
    m.range_min = range_min;
    m.range_max = range_max;
    m.abbrev = abbrev;
    m.longName = longName;
    m.name = name;
    m.fRestrictor = fRestrictor;
    m.hideInLists= hideInLists;
    m.restrictorValues = restrictorValues;
    m.cleanForQuery = cleanForQuery;
    m.filter = filter;
    m.stopwords = stopwords;
    return m;
     
  }
}
