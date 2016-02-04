// IniFileSection.java
/* 
(c)2000 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin, 
Ohio 43017-0702.  OCLC is a registered trademark of OCLC Online Computer 
Library Center, Inc. 
  
NOTICE TO USERS:  The Z39.50 Utilities ("Software") has been developed by OCLC 
Online Computer Library Center, Inc.  Subject to the terms and conditions set 
forth below, OCLC grants to user a perpetual, non-exclusive, royalty-free 
license to use, reproduce, alter, modify, and create derivative works from 
Software, and to sublicense Software subject to the following terms and 
conditions: 
  
SOFTWARE IS PROVIDED AS IS.  OCLC MAKES NO WARRANTIES, REPRESENTATIONS, OR 
GUARANTEES WHETHER EXPRESS OR IMPLIED REGARDING SOFTWARE, ITS FITNESS FOR ANY 
PARTICULAR PURPOSE, OR THE ACCURACY OF THE INFORMATION CONTAINED THEREIN. 
  
User agrees that OCLC shall have no liability to user arising therefrom, 
regardless of the basis of the action, including liability for special, 
consequential, exemplary, or incidental damages, including lost profits, 
even if it has been advised of the possibility thereof. 
  
User shall cause the copyright notice of OCLC to appear on all copies of 
Software, including derivative works made therefrom. 
*/ 

package ORG.oclc.util;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * IniFileSection provides facitilities to manage a section
 * of an windows .ini file.
 * @see oclc.guidon.util.IniFile
 * @version @(#)IniFileSection.java	1.2 10/29/96
 * @author Thom Hickey
 */


public class IniFileSection extends Hashtable{

  String sectionKey;
  Vector includes = null;
  Hashtable starredNames=null;
  boolean preserveInputOrder;
  Vector origname = null;

  public IniFileSection(String key){ 
      this(key, false);
  } 

  public IniFileSection(String key, boolean preserveOrder){
      preserveInputOrder = preserveOrder;
    sectionKey = key;
  }
    /**
     * Returns a Vector object containing the names of the include 
     * files for the section.
     * @return Vector
     */
  public Vector includes() {
    return includes;
  }
    /**
     * Clears saved data used in reading the section.
     */
  public void clearHash() { 
      starredNames = null; 
  } 
    /**
     * Returns the name of the section.
     * @return String
     */
  public String getName() 
  {
      return sectionKey;
  }

    /**
     * Writes the section data to the input BufferedWriter.
     * @param f the output buffer
     */ 
  public void writeSection(BufferedWriter f) {

      String val;
      try {
	  f.write("[" + sectionKey + "]\n");
      }
      catch (Exception e) {
	  e.printStackTrace();
         return; // couldn't write name, done!
      } 
    
      boolean prevIsComment=false;
      String variable ;
      try {
	  //	  System.out.println("writeSection: orig: " + origname);
       for (int i=0; origname != null && i<origname.size(); i++) {
	   variable = (String)origname.elementAt(i);
  	   val = getValue(variable);

           if (val != null) {
	       prevIsComment=false;
               val = Util.replaceString(val, "\n", "\\n");
               val = Util.replaceString(val, "\t", "\\t");
               val = Util.replaceString(val, "\r", "\\r");
               if (val.length() > 0)
    	         f.write(variable + " = " + val + "\n"); 
           }
           else {
	       if (!prevIsComment)
		   f.write("\n");
	       if (variable.startsWith("#"))
		   f.write(variable + "\n");
	       prevIsComment=true;
           }
       }

     }
     catch (Exception e) {}

  }  

  boolean addALine(Reader reader){
    int c;
    if ((c = IniFile.getNonWhiteChar(reader)) < 0 ||
	c == IniFile.startOfSectionChar)
      return false;
    
    String name = IniFile.getString(reader, '=', c).trim();
    //    System.out.println("name is ~" + name + "~");

    if (name.length()==0)
      return true;
    if (name.startsWith("#include")) {
      String includeFile = name.substring(8);
      includeFile = includeFile.replace('"', ' ');
      includeFile = includeFile.trim();
      if (includes == null)
	  includes = new Vector();
      includes.addElement(includeFile);
      //System.out.println("Saving include filename: " + includeFile);
    }
    else if (name.charAt(0) != '#' && name.startsWith("/*") == false) {
      String orig = name;

      name = name.toLowerCase();

      if (name.charAt(name.length() - 1) == '*') {

          /* if name ends with a star, this is an automatically numbered list */
          String nameRoot = name.substring(0, name.length() - 1);
	  if (starredNames == null)
	      starredNames = new Hashtable();
          Integer number=(Integer)starredNames.get(nameRoot);
          if(number==null)
              number=new Integer(0);
          int num=number.intValue()+1;


          name=nameRoot+num;
          orig = name;

          starredNames.put(nameRoot, new Integer(num));
         
      }

      if (preserveInputOrder) {
	  if (origname == null)
	      origname = new Vector(5);
	  origname.addElement(orig); 
      }
      
      //String value = IniFile.getString(reader, '\n').trim();
      String value;
      try {
	value = IniFile.readEscapedLine(reader).trim();
      } catch (Exception e) {
	//System.out.println(e);
	value="";
      }
      //System.out.println("value is ~" + value + "~");

      if (value != null && value.length()>0) {

        // Any strings that wanted \n or \r or \t and now
        // have the actual characters, need to fix that
	//  Double \\ indicates NT file separator
        value = fixEscape(value);


	//System.out.println("addALine: "+name+" = " + value);//
	// look for system property reference
	int endParenOffset;
	if (value.startsWith("$(") &&
	    (endParenOffset = value.indexOf(')')) != -1) {
	  String newValue = System.getProperty
	    (value.substring(2, endParenOffset));
	  if (newValue != null) {
	    if (endParenOffset < value.length() - 1)
	      value = newValue + value.substring(endParenOffset + 1);
	    else
	      value = newValue;
	  }
	}
      }
      put(name, value);
    }
    else if (preserveInputOrder) {/* this is a comment */
	if (origname == null) 
	    origname = new Vector(5); 
	origname.addElement(name);
    }
    
    return true;
  }

    boolean addALine(IniData inid){ 
	int c; 
	c = IniFile.getNonWhiteChar(inid);
	if (c < 0 || 
	    c == IniFile.startOfSectionChar) 
	    return false; 
 
	if (preserveInputOrder && origname == null)
	    origname = new Vector(5);

	inid.pos --; 
	String name = IniFile.getString(inid, '=').trim(); 
	if (name.length()==0) return true; 
	//System.out.println("name is ~" + name + "~"); 

	if (name.charAt(0) == '#' || name.startsWith("/*")) { 
	    if (name.startsWith("#include")) {
		String includeFile = name.substring(8);
		includeFile = includeFile.replace('"', ' ');
		includeFile = includeFile.trim();
		if (includes == null)
		    includes =new Vector();

		includes.addElement(includeFile);
	    }
            else if (preserveInputOrder) { // comment
		    origname.addElement(name);
	    }
	    return(true); 
	} 
	String orig= name;
	name = name.toLowerCase();
	if (name.charAt(name.length() - 1) == '*') { 
	    // if name ends with a star, this is an automatically numbered 
	    // list  
	    if (starredNames == null) 
		starredNames = new Hashtable();
	    IntObj number=(IntObj)starredNames.get(name); 
	    if (number==null) { 
		number=new IntObj(0); 
		starredNames.put(name, number); 
	    } 
	    number.val ++; 
	    name = name.substring(0, name.length() - 1) + number.val; 
	    orig = name;
	} 
	if (preserveInputOrder)   
	    origname.addElement(orig);    
    
	String value; 
	try { 
	    value = IniFile.readEscapedLine(inid).trim(); 
	} catch (Exception e) { 
	    System.out.println("Variable name = " +  orig);
	    e.printStackTrace();
	    //System.out.println(e); 
	    value=""; 
	} 
	//System.out.println("value is ~" + value + "~"); 

	// Return if nothing there.
	if (value == null || value.length() == 0) 
	    return true; 

	// Any strings that wanted \n or \r or \t and now 
	// have the actual characters, need to fix that 
	//  Double \\ indicates NT file separator 
	value = fixEscape(value); 
	    
	// look for system property reference 
	int endParenOffset; 
	if (value.startsWith("$(") && 
	    (endParenOffset = value.indexOf(')')) != -1) { 
	    String newValue = System.getProperty 
		(value.substring(2, endParenOffset)); 
	    if (newValue != null) { 
		if (endParenOffset < value.length() - 1) 
		    value = newValue + value.substring(endParenOffset + 1); 
		else 
		    value = newValue; 
	    } 
	} 

	//System.out.println("Saving entry: " + name + " value: " + value);
	put(name.intern(), value.intern()); 
	
	return true; 
    } 

  public String [] getKeys(){
    String [] keys = new String[size()];
    int  i=0;
    for (Enumeration e = keys(); e.hasMoreElements();i++)
      keys[i] = e.nextElement().toString();
    return keys;
  }
  public void dump(java.io.PrintStream ps){
   ps.println("["+sectionKey+"]");
   for (Enumeration s = keys(); s.hasMoreElements();){
        String name = s.nextElement().toString();
        ps.println("  "+name+" = '"+get(name)+"'");
      }
  }

  public String toString(){
      StringBuffer s = new StringBuffer();
      
      s.append("["+sectionKey+"]\n");
      if (preserveInputOrder)
	  s.append("orig data: " + origname + "\n");
      for (Enumeration e = keys(); e.hasMoreElements();){
	  String name = e.nextElement().toString();
	  s.append(name+" = '"+get(name)+"'\n");
      }
      return s.toString(); 
  }

  public void putValue(String key, Object s) {
      if (preserveInputOrder) {

	  if (key != null) {
	      if (get(key.toLowerCase()) == null) {
		  if (origname == null)
		      origname = new Vector();
		  origname.addElement(key.toLowerCase());
	      }
	      put(key.toLowerCase(), s);
	  }
      }
  }

  public void removeValue(String key) {
      if (preserveInputOrder) {
	  if (key != null) {
	      if (origname != null) {
		  for (int i=0; i<origname.size(); i++) {
		      String n = (String)origname.elementAt(i);
		      if (n.toLowerCase().equals(key.toLowerCase())) {
			  origname.removeElementAt(i);
			  break;
		      }
		  }
	      }

	      remove(key.toLowerCase());
	  }
      }
  }

  public String getValue(String key) {
    Object o = get(key.toLowerCase());
    return (String)o;
  }

  public String getStringValue(String key)
    throws IniMissingValueException {
    Object o = get(key.toLowerCase());
    if (o == null)
      throw new IniMissingValueException(key);
    return (String)o;
  }

  public String getStringValue(String key,
			       String defaultValue) {
    try {
      return getStringValue(key);
    }
    catch (IniMissingValueException e) {
    }
    return defaultValue;
  }
  
  public int getIntValue(String key)
    throws IniMissingValueException,
	   IniInvalidValueException
    {
      String stringValue = getStringValue(key);

      /* this TRUE/FALSE stuff is only left here for compatibility with old
	 code; new code should use get getBooleanValue */
      if (stringValue.equalsIgnoreCase("TRUE"))
	return 1;
      else if (stringValue.equalsIgnoreCase("FALSE"))
	return 0;
      try {
	return Integer.parseInt(stringValue);
      }
      catch (NumberFormatException e) {
	throw new IniInvalidValueException(key + ":" + e);
      }
    }
  
  public int getIntValue(String key,
			 int defaultValue)
    {
      try {
	return getIntValue(key);
      }
      catch (Exception e) {
      }
      return defaultValue;
    }

  public boolean getBooleanValue(String key) 
    throws IniMissingValueException,
	   IniInvalidValueException
    {
      String stringValue = getStringValue(key);
      if (stringValue.equalsIgnoreCase("TRUE"))
	return true;
      else if (stringValue.equalsIgnoreCase("FALSE"))
	return false;
      throw new IniInvalidValueException(key + "=" + stringValue);
    }

  public boolean getBooleanValue(String key, boolean defaultValue)
    {
      try {
	return getBooleanValue(key);
      }
      catch (Exception e) {
	return defaultValue;
      }
    }

  public byte getByteValue(String key)
    throws IniMissingValueException,
	   IniInvalidValueException
    {
      String stringValue = getStringValue(key);

      try {
	return Byte.parseByte(stringValue);
      }
      catch (NumberFormatException e) {
	throw new IniInvalidValueException(key + ":" + e);
      }
    }
  
  public byte getByteValue(String key,
			   byte defaultValue)
    {
      try {
	return getByteValue(key);
      }
      catch (Exception e) {
      }
      return defaultValue;
    }


  public short getShortValue(String key)
    throws IniMissingValueException,
	   IniInvalidValueException
    {
      String stringValue = getStringValue(key);

      try {
	return Short.parseShort(stringValue);
      }
      catch (NumberFormatException e) {
	throw new IniInvalidValueException(key + ":" + e);
      }
    }
  
  public short getShortValue(String key,
			     short defaultValue)
    {
      try {
	return getShortValue(key);
      }
      catch (Exception e) {
      }
      return defaultValue;
    }


  public long getLongValue(String key)
    throws IniMissingValueException,
	   IniInvalidValueException
    {
      String stringValue = getStringValue(key);

      try {
	return Long.parseLong(stringValue);
      }
      catch (NumberFormatException e) {
	throw new IniInvalidValueException(key + ":" + e);
      }
    }
  
  public long getLongValue(String key,
			   long defaultValue)
    {
      try {
	return getLongValue(key);
      }
      catch (Exception e) {
      }
      return defaultValue;
    }


  public float getFloatValue(String key) 
    throws IniMissingValueException,
	   IniInvalidValueException
    {
      String stringValue = getStringValue(key);
      try {
	return new Float(stringValue).floatValue();
      }
      catch (NumberFormatException e) {
	throw new IniInvalidValueException(key + ":" + e);
      }
    }


  public float getFloatValue(String key,
			     float defaultValue)
    {
      try {
	return getFloatValue(key);
      }
      catch (Exception e) {
      }
      return defaultValue;
    }


  public double getDoubleValue(String key) 
    throws IniMissingValueException,
	   IniInvalidValueException
    {
      String stringValue = getStringValue(key);
      try {
	return new Double(stringValue).doubleValue();
      }
      catch (NumberFormatException e) {
	throw new IniInvalidValueException(key + ":" + e);
      }
    }


  public double getDoubleValue(String key,
			       double defaultValue)
    {
      try {
	return getDoubleValue(key);
      }
      catch (Exception e) {
      }
      return defaultValue;
    }


  final String fixEscape(String data) {
    if (data == null)
        return null; 
 
     StringBuffer t1; 
 
     int pos = data.indexOf('\\');
     if (pos == -1) 
       return data; 
 
     t1 = new StringBuffer(); 
     int start =0; 
     int end = data.length();
     while (pos != -1) { 
       t1.append(data.substring(start, pos)); 
       if (pos+1 < end) {
         pos++; // bump past slash
         if (data.charAt(pos) == '\\')
           t1.append(File.separator);
         else if (data.charAt(pos) == 'n')
           t1.append('\n');
         else if (data.charAt(pos) == 'r')
           t1.append('\r');
         else if (data.charAt(pos) == 't')
           t1.append('\t');
         else {
           t1.append('\\');
           t1.append(data.charAt(pos));  
         }
         start = pos+1;
         if (start < end)
           pos = data.indexOf('\\', start);
         else
           pos = -1;
       }
       else {
         t1.append(data.charAt(pos));
         pos = -1;
       }
     } 
     if (start < data.length())
       t1.append(data.substring(start)); 
     return t1.toString(); 
  } 

}
class IntObj { 
    public int val = 0; 
    public IntObj(int val) { this.val = val; } 
} 
