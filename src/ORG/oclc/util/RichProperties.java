package ORG.oclc.util;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * An interface for accessing IniFiles and BerProperties
 * @version %W% %G%
 * @author Ralph LeVan
 */

public interface RichProperties {

  /**
   * Given a section name, return the value specified for the input key
   * @param mainKey the section name
   * @param subKey the key to look for
   * @return String the key value or null if the key value does not exist
   */
  public String getValue(String mainKey, String subKey);


  /**
   *  Given a section name, return the value specified for the input key.
   *  If the value doesn't exist return the default.
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @param defaultValue the default value
   *  @return String the key value
   */
  public String getValue(String mainKey, String subKey, String defaultValue);


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return String the key value or null if the key value does not exist
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section.
   */
  public String getStringValue(String mainKey, String subKey)
    throws IniMissingSectionException, IniMissingValueException;

  
  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return String the key value or the defaultValue if the key value does
   *  not exist
   */
  public String getStringValue(String mainKey, String subKey,
    String defaultValue);

  
  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return int the key value
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section
   *  @exception IniInvalidValueException if the subKey value is not a valid
   *   int
   */
  public int getIntValue(String mainKey, String subKey) 
    throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException;
  

  /**
   *  Given a section name, return the Integer value specified for the input
   *  key - use for numeric values
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @param defaultValue the value to return if the key is not found
   *  @return integer the key value
   */
  public int getIntValue(String mainKey, String subKey, int defaultValue);


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return int the key value
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section
   *  @exception IniInvalidValueException if the subKey value is not a valid
   *   float
   */
  public float getFloatValue(String mainKey, String subKey) 
    throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException;
  

  /**
   *  Given a section name, return the Float value specified for the input
   *  key - use for numeric values
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @param defaultValue the value to return if the key is not found
   *  @return float the key value
   */
  public float getFloatValue(String mainKey, String subKey,
    float defaultValue);


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return int the key value
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section
   *  @exception IniInvalidValueException if the subKey value is not set to
   *   true or false
   */
  public boolean getBooleanValue(String mainKey, String subKey) 
    throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException;
  

  /**
   *  Given a section name, return the boolean value(true/false) specified for
   *  the input key 
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @param defaultValue the value to return if the key is not found
   *  @return float the key value
   */
  public boolean getBooleanValue(String mainKey, String subKey,
    boolean defaultValue);


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return byte the key value
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section
   *  @exception IniInvalidValueException if the subKey value is not a valid
   *   byte
   */
  public byte getByteValue(String mainKey, String subKey) 
    throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException;


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return byte the key value or defaultValue if a key value can't be found
   */
  public byte getByteValue(String mainKey, String subKey, byte defaultValue);

  
  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return byte the key value
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section
   *  @exception IniInvalidValueException if the subKey value is not a valid
   *   short
   */
  public short getShortValue(String mainKey, String subKey) 
    throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException;


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return short the key value or defaultValue if a key value can't be found
   */
  public short getShortValue(String mainKey, String subKey,
    short defaultValue);

  
  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return long the key value
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section
   *  @exception IniInvalidValueException if the subKey value is not a valid
   *   long
   */
  public long getLongValue(String mainKey, String subKey) 
    throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException;


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return long the key value or defaultValue if a key value can't be found
   */
  public long getLongValue(String mainKey, String subKey, long defaultValue);

  
  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return double the key value
   *  @exception IniMissingSectionException if the section name is not found
   *  @exception IniMissingValueException if the subKey value is not found
   *   within the section
   *  @exception IniInvalidValueException if the subKey value is not a valid
   *   double
   */
  public double getDoubleValue(String mainKey, String subKey)
    throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException;


  /**
   *  Given a section name, return the value specified for the input key
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @return double the key value or defaultValue if a key value can't be
   *   found
   */
  public double getDoubleValue(String mainKey, String subKey,
    double defaultValue);
  
  
  /**
   *  Retrieve all the variables in a section
   *  @param mainKey the section name
   *  @return IniFileSection the section variables
   */
  public Enumeration getSectionKeys(String mainKey);


  /**
   *  Get all the section names in the inifile
   *  @return Enumeration the enumeration of all the section name strings
   */
  public Enumeration getSections();
}
