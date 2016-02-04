
/*
 * TermNormalizer.java
 *************************/
package ORG.oclc.qparse;


import java.util.*;
import java.io.*;
import ORG.oclc.util.IniFile;

/**
 * The interface class for user defined term normalization
 * filtering classes.  
 */
public interface TermNormalizer
{
   /** 
     * The normalizer initialization method
     * @param ini the IniFile containing configuration information
     * @param section the section name for the inifile
     * @param indexInfo the Map object associated with the index.
     */
   public void initNormalizer(IniFile ini, String section, Map indexInfo);

   /** 
     * The method for filtering a term.
     * @param query the input string
     * @return String[] the filtered term
     */
   public String[] filterit(String query);
}
 

