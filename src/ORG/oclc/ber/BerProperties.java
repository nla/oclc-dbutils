package ORG.oclc.ber;

import java.util.Enumeration;
import java.util.Hashtable;

import ORG.oclc.util.IniInvalidValueException;
import ORG.oclc.util.IniMissingSectionException;
import ORG.oclc.util.IniMissingValueException;
import ORG.oclc.util.RichProperties;

/** 
 * BerProperties is a class for maintaining a two level properties table with
 * a BER serialization form.
 *
 * @version %W% %G%
 * @author Ralph LeVan
 *
 */

public class BerProperties implements RichProperties {
    //    final static boolean MakeMain = true;
    final static boolean MakeMain = false;

    Hashtable sections=new Hashtable();

    public BerProperties() {
    }

    public BerProperties(BerString data) {
        DataDir a=new DataDir(data), b, c;
        String  key, section, value;

        for(b=a.child(); b!=null; b=b.next()) {
            c=b.child();
            section=c.getUTFString();
            for(c=c.next(); c!=null; c=c.next()) {
                key=c.getUTFString();
                c=c.next();
                value=c.getUTFString();
                put(section, key, value);
            }
        }
    }

    public boolean getBooleanValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException {
        return getIntValue(sectionName, key)==1?true:false;
    }

    public boolean getBooleanValue(String sectionName, String key,
      boolean defVal) {
        return getIntValue(sectionName, key, defVal?1:0)==1?true:false;
    }

    public byte getByteValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException {
        String stringValue = getStringValue(sectionName, key);

         try {
          return Byte.parseByte(stringValue);
        }
        catch (NumberFormatException e) {
          throw new IniInvalidValueException(key + ":" + e);
        }
    }
  
    public byte getByteValue(String sectionName, String key,
      byte defaultValue) {
        try {
            return getByteValue(sectionName, key);
        }
        catch (Exception e) { }
        return defaultValue;
    }

    public double getDoubleValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException {
        String stringValue = getStringValue(sectionName, key);

         try {
            return new Double(stringValue).doubleValue();
        }
        catch (NumberFormatException e) {
            throw new IniInvalidValueException(key + ":" + e);
        }
    }
  
    public double getDoubleValue(String sectionName, String key,
      double defaultValue) {
        try {
            return getDoubleValue(sectionName, key);
        }
        catch (Exception e) { }
        return defaultValue;
    }

    public float getFloatValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException {
        String stringValue = getStringValue(sectionName, key);

         try {
            return new Float(stringValue).floatValue();
        }
        catch (NumberFormatException e) {
            throw new IniInvalidValueException(key + ":" + e);
        }
    }
  
    public float getFloatValue(String sectionName, String key,
      float defaultValue) {
        try {
            return getFloatValue(sectionName, key);
        }
        catch (Exception e) { }
        return defaultValue;
    }

    public int getIntValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException {
        String stringValue = getStringValue(sectionName, key);

         try {
          return Integer.parseInt(stringValue);
        }
        catch (NumberFormatException e) {
          throw new IniInvalidValueException(key + ":" + e);
        }
    }
  
    public int getIntValue(String sectionName, String key,
      int defaultValue) {
        try {
            return getIntValue(sectionName, key);
        }
        catch (Exception e) { }
        return defaultValue;
    }

    public long getLongValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException {
        String stringValue = getStringValue(sectionName, key);

         try {
          return Long.parseLong(stringValue);
        }
        catch (NumberFormatException e) {
          throw new IniInvalidValueException(key + ":" + e);
        }
    }
  
    public long getLongValue(String sectionName, String key,
      long defaultValue) {
        try {
            return getLongValue(sectionName, key);
        }
        catch (Exception e) { }
        return defaultValue;
    }

    public Enumeration getSections() {
        return sections.keys();
    }

    public Enumeration getSectionKeys(String sectionName) {
        Hashtable section=(Hashtable)sections.get(sectionName.toLowerCase());
        return section.keys();
    }

    public short getShortValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException,
      IniInvalidValueException {
        String stringValue = getStringValue(sectionName, key);

         try {
          return Short.parseShort(stringValue);
        }
        catch (NumberFormatException e) {
          throw new IniInvalidValueException(key + ":" + e);
        }
    }
  
    public short getShortValue(String sectionName, String key,
      short defaultValue) {
        try {
            return getShortValue(sectionName, key);
        }
        catch (Exception e) { }
        return defaultValue;
    }

    public String getStringValue(String sectionName, String key)
      throws IniMissingSectionException, IniMissingValueException {
        Hashtable section=(Hashtable)sections.get(sectionName.toLowerCase());
        if(section==null)
            throw new IniMissingSectionException(sectionName);
        String value=(String)section.get(key.toLowerCase());
        if(value==null)
            throw new IniMissingValueException(sectionName);
        return value;
    }

    public String getStringValue(String sectionName, String key,
      String defVal) {
        Hashtable section=(Hashtable)sections.get(sectionName.toLowerCase());
        if(section==null)
            return defVal;
        String value=(String)section.get(key.toLowerCase());
        if(value!=null)
            return value;
        return defVal;
    }

    public String getValue(String sectionName, String key) {
        try {
            return getStringValue(sectionName, key);
        }
        catch(Exception e) {}
        return null;
    }

    public String getValue(String sectionName, String key,
      String defaultValue) {
        return getStringValue(sectionName, key, defaultValue);
    }

    public boolean put(String sectionName, String key, boolean value) {
        return put(sectionName, key, value?1:0)==1?true:false;
    }

    public int put(String sectionName, String key, int value) {
        String retVal=put(sectionName, key, Integer.toString(value));
        if(retVal==null)
            return -1;
        return Integer.parseInt(retVal);
    }

    public String put(String sectionName, String key, String value) {
        Hashtable section=(Hashtable)sections.get(sectionName.toLowerCase());
        if(section==null) {
            section=new Hashtable();
            sections.put(sectionName.toLowerCase(), section);
        }
        return (String)section.put(key.toLowerCase(), value);
    }

    public BerString save() {
        DataDir dir=new DataDir(0, ASN1.APPLICATION), subdir;
        Enumeration keys, sections=this.sections.keys();
        Hashtable   section;
        String      sectionName, key, value;

        while(sections.hasMoreElements()) {
            sectionName=(String)sections.nextElement();
            subdir=dir.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
            subdir.addUTF(0, ASN1.CONTEXT, sectionName);
            section=(Hashtable)this.sections.get(sectionName);
            keys=section.keys();
            while(keys.hasMoreElements()) {
                key=(String)keys.nextElement();
                value=(String)section.get(key);
                subdir.addUTF(1, ASN1.CONTEXT, key);
                subdir.addUTF(2, ASN1.CONTEXT, value);
            }
        }

        return new BerString(dir);
    }

    public String toString() {
        return sections.toString();
    }
}
