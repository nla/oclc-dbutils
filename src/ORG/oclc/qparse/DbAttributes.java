
package ORG.oclc.qparse;

import java.util.*;
import ORG.oclc.util.*;


/**
 * @version @(#)DbAttributes.java       1.4 04/28/97
 * @author Jenny Colvard
 */
  
public class DbAttributes {

    private static final boolean debug = false;
    
    private Hashtable indexMaps;  // Hashed out by Attribute type
    private Vector OIDList = null;


    public DbAttributes() {
       indexMaps = new Hashtable();
    }


    public DbAttributes(String section, IniFile ini) {
         init(section, ini, false);
    }

    public DbAttributes(String section, IniFile ini, boolean liteFlag) {
         init(section, ini, liteFlag);
    }



    

    private void init(String section, IniFile ini, boolean liteFlag) {

        String temp;

        String input, attrSection;
        int i, j, attrCntr;
        Map maps[] = null;
        IndexMap iMap=null;

        indexMaps = new Hashtable();
        OIDList = new Vector();

        for (attrCntr=0; ;attrCntr++) {
          temp = new String("type" + attrCntr);
          attrSection = ini.getValue(section, temp);

          if (attrSection == null) {
            if (attrCntr > 0)
              break;
          }
          else {

            iMap = new IndexMap(attrSection, ini, liteFlag);
         
            if (iMap != null) {
                indexMaps.put(iMap.OID(), iMap);
                OIDList.addElement(iMap.OID());
            }
          }
  

         }
      if (debug)
        System.out.println("Done with loading data");

    }

/**
  * Generate an IniFile format string representation of the object.
  * @return String
  */
    public String toIniString() {
      StringBuffer str = new StringBuffer();
      str.append("\n[attributes]\n");
    
      int i=0; 
      String sect=null;
      IndexMap iMap;
      String key;
      String mapstrings[] = new String[indexMaps.size()];

      for (Enumeration e = indexMaps.keys(); e.hasMoreElements() ; ) {
          key = (String)e.nextElement();
          iMap = getMap(key);
          if (iMap != null) {
             if (key.equals(ORG.oclc.z39.Attribute.BIB1)) 
               sect = "BIB1attributes";
             else if (key.equals(ORG.oclc.z39.Attribute.EXP1))
               sect = "EXP1attributes";
             else if (key.equals(ORG.oclc.z39.Attribute.ZDSR))
               sect = "ZDSRattributes";
             else 
               sect = null;
            if (sect != null) {
              mapstrings[i] = iMap.toIniString();
              str.append(IniFile.makeString("type"+(i+1), sect));
              i++;
            } 
          }
               
      }
      str.append("\n");

      for (i=0; i<mapstrings.length; i++) {
        str.append(mapstrings[i]);
        str.append("\n");
      }

      return str.toString();
    }
/**
  * Generate a string representation of the object.
  * @return String
  */
    public String toString() {
      StringBuffer outStr = new StringBuffer();
      IndexMap iMap;
      String key;
      
      for (Enumeration e = indexMaps.keys(); e.hasMoreElements() ; ) {
          key = (String)e.nextElement();
          iMap = getMap(key);
          if (iMap != null)
            outStr.append("OID: " + key + " Map: \n" + iMap.toString());
      }

      return outStr.toString();
    }

    public void add(IndexMap map, String OID) {
       indexMaps.put(OID.intern(), map);
    }


   public final IndexMap getMap() {
     return getMap(ORG.oclc.z39.Attribute.BIB1);
   }

   public final IndexMap getMap(String OID) {
      if (OID == null)
        return null;

      return (IndexMap)indexMaps.get(OID);
   }

    public Vector getOIDList() {
        return OIDList;
    }
}


