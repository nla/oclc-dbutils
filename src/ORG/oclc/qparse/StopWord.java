
package ORG.oclc.qparse;

import java.io.*;
import java.util.*;
import ORG.oclc.util.*;

/** Build a list of words that will never be in the database.
  * @version @(#)StopWord.java  1.18 04/11/97
  * @author Jenny Colvard
  */
  
public class StopWord {
  private String stopwords[];
  private boolean usingDefault;
  static final String DEFAULT_LIST = "a an and are as at be but by for from had have he her his in is it not of on or that the this to was which you &";


  public StopWord(IniFile ini) {
    this("general", ini);
  }

  public StopWord(String section, IniFile ini) {
    if (section == null)
      return;

    String list = ini.getValue(section, "stopwords");
    String golist = ini.getValue(section, "gowords");
    if (golist != null)
        list = DEFAULT_LIST; // will remove from StopWord object later
    stopwords = null;

    if (list == null) 
      list = "";
    else if (list.equalsIgnoreCase("default")) {
      usingDefault=true;
      list = DEFAULT_LIST;
    }

    StringTokenizer words = new StringTokenizer(list, " ");
    stopwords = new String[words.countTokens()];

    int i=0;
    while (words.hasMoreTokens())
      stopwords[i++] = words.nextToken().intern();

    if (golist != null)
        this.removeStopWords(golist);

    //System.out.println("Exit StopWord");
  }

  public StopWord(InputStream file) {
    BufferedReader in = new BufferedReader(new InputStreamReader(file));
    String line;
    int i=0;

    stopwords = new String[100];
    
    while (true) {
      try { 
        line = in.readLine();
        stopwords[i++] = line.intern();
      } catch (IOException e) { 
        /*EOF - done */
        return; 
      } catch (ArrayIndexOutOfBoundsException e) {
        // grow the array, save this element, continue
      }
    }
  }

  public StopWord(String list) {
    StringTokenizer words = null;
    int i=0;

    if (list == null)
      list = "";
    else if (list.equalsIgnoreCase("default")) {
      usingDefault = true;
      list = DEFAULT_LIST;
    }

    words     = new StringTokenizer(list, " ");
    stopwords = new String[words.countTokens()];
 
    while (words.hasMoreTokens())
      stopwords[i++] = words.nextToken().intern();
  }

  public StopWord(String list[]) {
    stopwords = list;
  }

  public String toIniString() {
      if (usingDefault)
        return "default";

      String str = toString();
      if (str.equals(DEFAULT_LIST))
        return "default";
      return str;
  }

  public String toString() {
    StringBuffer outStr = new StringBuffer();

    if (stopwords != null)
      for (int i=0; i<stopwords.length; i++)
        outStr.append(stopwords[i] + " "); 

    return outStr.toString();
  }

  public boolean contains(String word) {
    if (stopwords == null)
      return false;

    for (int i=0; i<stopwords.length; i++)
      if (stopwords[i].equalsIgnoreCase(word))
        return true;

    return false;
  }


/**
  * Remove words in list from the object's stopwords[].
  * <p>
  * Each word in the list is removed from stopwords[],
  * if it is in stopwords[].
  * @param list String of space-separated words
  * @return void
  * 
  */
  private void removeStopWords(String list) {
    if (stopwords == null)
        return;
    int numToRemove = 0; // how many stopwords to remove
    StringTokenizer st = new StringTokenizer(list, " ");
    String word;
    // mark all stopwords for removal by setting to ""
    while (st.hasMoreTokens()) {
        word = st.nextToken();
        for (int i = 0; i < stopwords.length; i++) {
            if (stopwords[i].equalsIgnoreCase(word)) {
                stopwords[i] = "";
                numToRemove++;
                break;
            }
        }
    }
    // effectively, these have been removed, but reallocate anyway
    if (numToRemove > 0) {
        String newStopWords[] = new String[stopwords.length - numToRemove];
        int newi = 0;
        for (int i = 0; i < stopwords.length; i++)
            if (stopwords[i].length() > 0)
                newStopWords[newi++] = stopwords[i];
        stopwords = newStopWords;
    }
  }

}

