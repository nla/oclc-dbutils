
package ORG.oclc.util;


/** 
 * @version @(#)Util.java       1.18 04/01/97 
 * @author Jenny Colvard 
 */ 
import java.io.*;
import java.util.Vector;

import ORG.oclc.ber.*;

import sun.io.ByteToCharConverter;
import sun.io.CharToByteConverter;

public class Util {
  static final String newLine = System.getProperty("line.separator");

  static final int dbLongAt(byte data[], int offset) {
    return ((data[offset] & 0xff) << 24) + ((data[offset+1] & 0xff) << 16) +
      ((data[offset+2] & 0xff) << 8) + (data[offset+3] & 0xff);
  }

  static final short dbShortAt(byte data[], int offset) {
    return (short)(((data[offset] & 0xff) << 8) + (data[offset+1] & 0xff));
  }

  public static String byteArrayToString(byte array[]) {
    return byteArrayToString(array, 0, array.length);
  }

  public static String hexDump(String data, int len, String label) {
    byte databytes[] = data.getBytes();

    return hexDump(databytes, len, label);

  }


  public static String hexDump(byte data[], int len, String label) {
    StringBuffer outStr = new StringBuffer();

    if (label != null) {
      if (label.endsWith("\n"))
        outStr.append(label.substring(0, label.length()-1));
      else
        outStr.append(label);
      outStr.append(" Length: ");
    }
    else
      outStr.append("Data  Length: ");
 
    outStr.append(len);
    outStr.append(newLine);

    outStr.append(byteArrayToString(data, 0, len));

    return outStr.toString();
  }

  public static String byteArrayToString(byte array[], int offset, int length) {
    StringBuffer str = new StringBuffer();
    StringBuffer alpha = new StringBuffer();
    int stopat = length + offset;
    char c;
    int type;
 
    for (int i=1; offset < stopat; offset++,i++) {
      if ((array[offset]&0xff)<16)
        str.append(" 0");
      else
        str.append(" ");
      str.append(Integer.toString(array[offset]&0xff,16));

      c = (char)array[offset];
      type = Character.getType(c);

      //      if (Character.isLetterOrDigit(c) || (c > )
      if (type == Character.CONTROL || type == Character.LINE_SEPARATOR)
        alpha.append('.');
      else
        alpha.append(c);

        
      if ((i%16)==0) {
        str.append("  " + alpha + newLine);
        alpha.setLength(0);
      }
    }
    offset = 0;
    
    str.append("  " + alpha + newLine);
    str.append(newLine);

    return str.toString();
  }

/**
  * Collapse a String from an input String.
  * @param data the input String
  * @param collapse the data to collapse from the string.
  * @return String
  */
  public static final String collapseString(String data, String collapse) {
     if (data == null || collapse == null)
        return null;

     StringBuffer t1;

     int pos = data.indexOf(collapse);
     if (pos == -1)
       return data;

     t1 = new StringBuffer();
     int start =0;
     while (pos != -1) {
       t1.append(data.substring(start, pos));
       start = pos+collapse.length();
       pos = data.indexOf(collapse, start); 
     }
     t1.append(data.substring(start));
     return t1.toString();
   }

/**
  * Replaces a String with another String.
  * @param data the input String
  * @param original the data to replace from the string.
  * @param newdata the data to replace it with.
  * @return String
  */
  public static final String replaceString(String data, String original, String newdata) {
     if (data == null || original == null)
        return null;

     StringBuffer t1;
     if (newdata == null)
       newdata = "";

     int pos = data.indexOf(original);
     if (pos == -1)
       return data;

     t1 = new StringBuffer();
     int start =0;
     while (pos != -1) {
       t1.append(data.substring(start, pos));
       t1.append(newdata);
       start = pos+original.length();
       pos = data.indexOf(original, start); 
     }
     t1.append(data.substring(start));
     return t1.toString();
   }
    /**
     * Returns a stack trace of the current threads
     * @return String
     */
   public static final String stackTrace() {
      return stackTrace("Stack Dump");
   }
    /**
     * Returns a stack trace of the current threads titled with the input
     * string.
     * @param str the title of the trace.
     * @return String
     */
   public static final String stackTrace(String str) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new Exception(str).printStackTrace(ps);
        return(baos.toString());
   }


    private static final Vector UTF8CharToByteConverterPool = new Vector(10);
    /**
     * Get a CharToByteConverter from the pool or create a new one if there
     * aren't any available from the pool.
     * 
     */
    public static final CharToByteConverter getUTF8CharToByteConverter() {
        synchronized (UTF8CharToByteConverterPool) {
            int                 size = UTF8CharToByteConverterPool.size();
            CharToByteConverter conv;

            if (size == 0) {
                // Create a new one
                try {
                    conv = CharToByteConverter.getConverter("UTF8");
                } catch(UnsupportedEncodingException e) { conv=null; }
            } else {
                conv = (CharToByteConverter)
                    UTF8CharToByteConverterPool.elementAt(size - 1);
                UTF8CharToByteConverterPool.setSize(size - 1);
            }
            return(conv);
        }
    }


    /**
     * Release a UTF8 CharToByteConverter back to the pool.
     * 
     * @param conv      the converter
     * 
     */
    public static final void freeUTF8CharToByteConverter(
      CharToByteConverter conv) {
        synchronized (UTF8CharToByteConverterPool) {
            UTF8CharToByteConverterPool.addElement(conv);
        }
    }


    private static final Vector UTF8ByteToCharConverterPool = new Vector(10);
    /**
     * Get a ByteToCharConverter from the pool or create a new one if there
     * aren't any available from the pool.
     * 
     */
    public static final ByteToCharConverter getUTF8ByteToCharConverter() {
        synchronized (UTF8ByteToCharConverterPool) {
            ByteToCharConverter conv;
            int                 size = UTF8ByteToCharConverterPool.size();
            if (size == 0) {
                // Create a new one
                try {
                    conv = ByteToCharConverter.getConverter("UTF8");
                } catch(UnsupportedEncodingException e) { conv=null; }
            } else {
                conv = (ByteToCharConverter)
                    UTF8ByteToCharConverterPool.elementAt(size - 1);
                UTF8ByteToCharConverterPool.setSize(size - 1);
            }
            return(conv);
        }
    }

    /**
     * Release a ByteToCharConverter back to the pool.
     * 
     * @param conv      the converter
     * 
     */
    public static final void freeUTF8ByteToCharConverter(
      ByteToCharConverter conv) {
        synchronized (UTF8ByteToCharConverterPool) {
            UTF8ByteToCharConverterPool.addElement(conv);
        }
    }


    public static final byte[] getUTF8Bytes(String s) {
        char[]              chars = s.toCharArray();
        CharToByteConverter conv=Util.getUTF8CharToByteConverter();
        byte[]              bytes = new byte[chars.length*3], moreBytes;

        try {
            int count=conv.convert(chars, 0, chars.length,
                bytes, 0, chars.length*3);
            freeUTF8CharToByteConverter(conv);
            moreBytes=new byte[count];
            System.arraycopy(bytes, 0, moreBytes, 0, count);
        } catch(sun.io.UnknownCharacterException e) {
            moreBytes=null;
            e.printStackTrace();
        } catch(sun.io.MalformedInputException e) {
            moreBytes=null;
            e.printStackTrace();
        } catch(sun.io.ConversionBufferFullException e) {
            moreBytes=null;
            e.printStackTrace();
        }
        return moreBytes;
    }


    public static final String toBars(String s) {
        char         c;
        int          i, letterLength=0, originalLength=s.length();
        String       barCode=null;
        StringBuffer bars=null, buf=new StringBuffer(s);

        for(i=0; i<buf.length(); i++) {
            c=buf.charAt(i);
            if((barCode=toBars(c))!=null) {
                buf.deleteCharAt(i);
                if(isDiacritic(c)) {
                    if(bars==null)
                        bars=new StringBuffer();
                    bars.setLength(0);
                    bars.append(toBars(c));
                    // suck up any more diacritics
                    while(i<buf.length()) {
                        c=buf.charAt(i);
                        if(isDiacritic(c)) {
                            buf.deleteCharAt(i);
                            bars.append(toBars(c));
                        }
                        else
                            break;
                    }
                    buf.insert(i-letterLength, bars);
                    i+=bars.length()-1;
                }
                else { // non-diacritic barCode
                    buf.insert(i, barCode);
                    letterLength=barCode.length();
                    i+=letterLength-1;
                }
            }
            else
                letterLength=1;
        }
        if(buf.length()!=originalLength)
            return buf.toString();
        return s;
    }

    static final String toBars(char c) {
        switch(c) {
        case '\u00a1':
            return "|ie|";
        case '\u00a3':
            return "|ps|";
        case '\u00a9':
            return "|cs|";
        case '\u00ae':
            return "|bp|";
        case '\u00b0':
            return "|ds|";
        case '\u00b1':
            return "|pm|";
        case '\u00b2':
            return "|p2|";
        case '\u00b3':
            return "|p3|";
        case '\u00b7':
            return "|dm|";
        case '\u00b9':
            return "|p1|";
        case '\u00bf':
            return "|iq|";
        case '\u00c6':
            return "|AE|";
        case '\u00d0':
            return "|ET|";
        case '\u00d8':
            return "|SO|";
        case '\u00de':
            return "|IT|";
        case '\u00e6':
            return "|ae|";
        case '\u00f0':
            return "|et|";
        case '\u00f8':
            return "|so|";
        case '\u00fe':
            return "|it|";
        case '\u0110':
            return "|DC|";
        case '\u0111':
            return "|dc|";
        case '\u0131':
            return "|ti|";
        case '\u0141':
            return "|PL|";
        case '\u0142':
            return "|pl|";
        case '\u0152':
            return "|OE|";
        case '\u0153':
            return "|oe|";
        case '\u01a0':
            return "|HO|";
        case '\u01a1':
            return "|ho|";
        case '\u01af':
            return "|HU|";
        case '\u01b0':
            return "|hu|";
        case '\u02b9':
            return "|mz|";
        case '\u02ba':
            return "|tz|";
        case '\u02be':
            return "|al|";
        case '\u02bf':
            return "|ay|";
        case '\u0300':
            return "|gr|";
        case '\u0301':
            return "|ac|";
        case '\u0302':
            return "|cf|";
        case '\u0303':
            return "|td|";
        case '\u0304':
            return "|ma|";
        case '\u0306':
            return "|br|";
        case '\u0307':
            return "|sd|";
        case '\u0308':
            return "|um|";
        case '\u0309':
            return "|pq|";
        case '\u030a':
            return "|ca|";
        case '\u030b':
            return "|da|";
        case '\u030c':
            return "|ha|";
        case '\u0310':
            return "|cu|";
        case '\u0313':
            return "|cc|";
        case '\u0315':
            return "|co|";
        case '\u031c':
            return "|rc|";
        case '\u0323':
            return "|db|";
        case '\u0324':
            return "|dd|";
        case '\u0325':
            return "|cb|";
        case '\u0326':
            return "|hl|";
        case '\u0327':
            return "|ce|";
        case '\u0328':
            return "|hr|";
        case '\u032e':
            return "|up|";
        case '\u0332':
            return "|us|";
        case '\u0333':
            return "|ud|";
        case '\u03b1':
            return "|ga|";
        case '\u03b2':
            return "|gb|";
        case '\u03b3':
            return "|gc|";
        case '\u200c':
            return "|zn|";
        case '\u200d':
            return "|zj|";
        case '\u2070':
            return "|p0|";
        case '\u2074':
            return "|p4|";
        case '\u2075':
            return "|p5|";
        case '\u2076':
            return "|p6|";
        case '\u2077':
            return "|p7|";
        case '\u2078':
            return "|p8|";
        case '\u2079':
            return "|p9|";
        case '\u207a':
            return "|p+|";
        case '\u207b':
            return "|p-|";
        case '\u207d':
            return "|p(|";
        case '\u207e':
            return "|p)|";
        case '\u2080':
            return "|b0|";
        case '\u2081':
            return "|b1|";
        case '\u2082':
            return "|b2|";
        case '\u2083':
            return "|b3|";
        case '\u2084':
            return "|b4|";
        case '\u2085':
            return "|b5|";
        case '\u2086':
            return "|b6|";
        case '\u2087':
            return "|b7|";
        case '\u2088':
            return "|b8|";
        case '\u2089':
            return "|b9|";
        case '\u208a':
            return "|b+|";
        case '\u208b':
            return "|b-|";
        case '\u208d':
            return "|b(|";
        case '\u208e':
            return "|b)|";
        case '\u2113':
            return "|sl|";
        case '\u2117':
            return "|rs|";
        case '\u266d':
            return "|mf|";
        case '\u266f':
            return "|ms|";
        case '\ufe20':
            return "|ll|";
        case '\ufe21':
            return "|lr|";
        case '\ufe22':
            return "|tl|";
        case '\ufe23':
            return "|tr|";
        }
        return null;
    }


    public static final boolean isDiacritic(char c) {
        if(c>='\u0300' && c<='\u036f')
            return true;
        if(c>='\ufe20' && c<='\ufe2f')
            return true;
        return false;
    }


    public static String fromBars(String str){
        if(str==null)
            return(null);
        char[] dirty = str.toCharArray();
        char[] clean = fromBars(dirty);
        if(clean==dirty)
            return(str);
        return(new String(clean));
    }


    /**
     *  Decomposes combined diacritics and converts OCLC vertical bar
     *  syntax to Unicode characters.
     */
    public static char[] fromBars(char[] dirty) {
        return fromBars(dirty, 0, dirty.length);
    }


    /**
     *  Decomposes combined diacritics and converts OCLC vertical bar
     *  syntax to Unicode characters.
     */
    public static char[] fromBars(char[] dirty, int offset, int length) {
        char              diacritic[]=null, c, clean[]=null;
        int               dst, j=0, numDiacritics=0, src;
        VerticalBarResult result=null;

        for(dst=0, src=offset; dst<length; dst++, src++) {
            c=dirty[src];
            if(c>='\u00c0' && c<'\u02b0') {
                if(clean==null) {
                    clean=new char[length*2];
                    System.arraycopy(dirty, offset, clean, 0, dst);
                    j=dst;
                }
                j+=decompose(c, clean, j);
            }
            else if(c=='|') {
                if(clean==null) {
                    clean=new char[length*2];
                    System.arraycopy(dirty, offset, clean, 0, dst);
                    j=dst;
                }
                if(result==null) {
                    result=new VerticalBarResult();
                    diacritic=new char[length*2];
                }
                eatVerticalBars(dirty, src, dirty.length, result);
                if(result.isDiacritic)
                    diacritic[numDiacritics++]=result.newChar;
                else
                    clean[j++]=result.newChar;
                dst=result.byteOffset;
            }
            else
                if(clean!=null) {
                    clean[j++]=c;
                    if(numDiacritics>0) {
                        System.arraycopy(diacritic, 0, clean, j,
                            numDiacritics);
                        j+=numDiacritics;
                        numDiacritics=0;
                    }
                }
        }
        if(clean!=null) {
            char[] shorten=new char[j];
            System.arraycopy(clean, 0, shorten, 0, j);
            return shorten;
        }
        return dirty;
    }


    static final char GRAVE        = '\u0300';
    static final char ACUTE        = '\u0301';
    static final char CIRCUMFLEX   = '\u0302';
    static final char TILDE        = '\u0303';
    static final char MACRON       = '\u0304';
    static final char BREVE        = '\u0306';
    static final char DOT_ABOVE    = '\u0307';
    static final char UMLAUT       = '\u0308';
    static final char DIAERESIS    = UMLAUT;
    static final char RING_ABOVE   = '\u030a';
    static final char DOUBLE_ACUTE = '\u030b';
    static final char HACEK        = '\u030c';
    static final char CARON        = HACEK;
    static final char CEDILLA      = '\u0327';

    private static int decompose(char c, char[] str, int offset) {
        switch(c) {
        case '\u00c0':
            str[offset++]='A';
            str[offset]=GRAVE;
            return 2;
        case '\u00c1':
            str[offset++]='A';
            str[offset]=ACUTE;
            return 2;
        case '\u00c2':
            str[offset++]='A';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00c3':
            str[offset++]='A';
            str[offset]=TILDE;
            return 2;
        case '\u00c4':
            str[offset++]='A';
            str[offset]=UMLAUT;
            return 2;
        case '\u00c5':
            str[offset++]='A';
            str[offset]=RING_ABOVE;
            return 2;
        case '\u00c7':
            str[offset++]='C';
            str[offset]=CEDILLA;
            return 2;
        case '\u00c8':
            str[offset++]='E';
            str[offset]=GRAVE;
            return 2;
        case '\u00c9':
            str[offset++]='E';
            str[offset]=ACUTE;
            return 2;
        case '\u00ca':
            str[offset++]='E';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00cb':
            str[offset++]='E';
            str[offset]=UMLAUT;
            return 2;
        case '\u00cc':
            str[offset++]='I';
            str[offset]=GRAVE;
            return 2;
        case '\u00cd':
            str[offset++]='I';
            str[offset]=ACUTE;
            return 2;
        case '\u00ce':
            str[offset++]='I';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00cf':
            str[offset++]='I';
            str[offset]=UMLAUT;
            return 2;
        case '\u00d1':
            str[offset++]='N';
            str[offset]=TILDE;
            return 2;
        case '\u00d2':
            str[offset++]='O';
            str[offset]=GRAVE;
            return 2;
        case '\u00d3':
            str[offset++]='O';
            str[offset]=ACUTE;
            return 2;
        case '\u00d4':
            str[offset++]='O';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00d5':
            str[offset++]='O';
            str[offset]=TILDE;
            return 2;
        case '\u00d6':
            str[offset++]='O';
            str[offset]=UMLAUT;
            return 2;
        case '\u00d9':
            str[offset++]='U';
            str[offset]=GRAVE;
            return 2;
        case '\u00da':
            str[offset++]='U';
            str[offset]=ACUTE;
            return 2;
        case '\u00db':
            str[offset++]='U';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00dc':
            str[offset++]='U';
            str[offset]=UMLAUT;
            return 2;
        case '\u00dd':
            str[offset++]='Y';
            str[offset]=ACUTE;
            return 2;
        case '\u00e0':
            str[offset++]='a';
            str[offset]=GRAVE;
            return 2;
        case '\u00e1':
            str[offset++]='a';
            str[offset]=ACUTE;
            return 2;
        case '\u00e2':
            str[offset++]='a';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00e3':
            str[offset++]='a';
            str[offset]=TILDE;
            return 2;
        case '\u00e4':
            str[offset++]='a';
            str[offset]=UMLAUT;
            return 2;
        case '\u00e5':
            str[offset++]='a';
            str[offset]=RING_ABOVE;
            return 2;
        case '\u00e7':
            str[offset++]='c';
            str[offset]=CEDILLA;
            return 2;
        case '\u00e8':
            str[offset++]='e';
            str[offset]=GRAVE;
            return 2;
        case '\u00e9':
            str[offset++]='e';
            str[offset]=ACUTE;
            return 2;
        case '\u00ea':
            str[offset++]='e';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00eb':
            str[offset++]='e';
            str[offset]=UMLAUT;
            return 2;
        case '\u00ec':
            str[offset++]='i';
            str[offset]=GRAVE;
            return 2;
        case '\u00ed':
            str[offset++]='i';
            str[offset]=ACUTE;
            return 2;
        case '\u00ee':
            str[offset++]='i';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00ef':
            str[offset++]='i';
            str[offset]=UMLAUT;
            return 2;
        case '\u00f1':
            str[offset++]='n';
            str[offset]=TILDE;
            return 2;
        case '\u00f2':
            str[offset++]='o';
            str[offset]=GRAVE;
            return 2;
        case '\u00f3':
            str[offset++]='o';
            str[offset]=ACUTE;
            return 2;
        case '\u00f4':
            str[offset++]='o';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00f5':
            str[offset++]='o';
            str[offset]=TILDE;
            return 2;
        case '\u00f6':
            str[offset++]='o';
            str[offset]=UMLAUT;
            return 2;
        case '\u00f9':
            str[offset++]='u';
            str[offset]=GRAVE;
            return 2;
        case '\u00fa':
            str[offset++]='u';
            str[offset]=ACUTE;
            return 2;
        case '\u00fb':
            str[offset++]='u';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u00fc':
            str[offset++]='u';
            str[offset]=UMLAUT;
            return 2;
        case '\u00fd':
            str[offset++]='y';
            str[offset]=ACUTE;
            return 2;
        case '\u00ff':
            str[offset++]='y';
            str[offset]=UMLAUT;
            return 2;
        case '\u0100':
            str[offset++]='A';
            str[offset]=MACRON;
            return 2;
        case '\u0101':
            str[offset++]='a';
            str[offset]=MACRON;
            return 2;
        case '\u0102':
            str[offset++]='A';
            str[offset]=BREVE;
            return 2;
        case '\u0103':
            str[offset++]='a';
            str[offset]=BREVE;
            return 2;
        case '\u0106':
            str[offset++]='C';
            str[offset]=ACUTE;
            return 2;
        case '\u0107':
            str[offset++]='c';
            str[offset]=ACUTE;
            return 2;
        case '\u0108':
            str[offset++]='C';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0109':
            str[offset++]='c';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u010a':
            str[offset++]='C';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u010b':
            str[offset++]='c';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u010c':
            str[offset++]='C';
            str[offset]=CARON;
            return 2;
        case '\u010d':
            str[offset++]='c';
            str[offset]=CARON;
            return 2;
        case '\u010e':
            str[offset++]='D';
            str[offset]=CARON;
            return 2;
        case '\u010f':
            str[offset++]='d';
            str[offset]=CARON;
            return 2;
        case '\u0112':
            str[offset++]='E';
            str[offset]=MACRON;
            return 2;
        case '\u0113':
            str[offset++]='e';
            str[offset]=MACRON;
            return 2;
        case '\u0114':
            str[offset++]='E';
            str[offset]=BREVE;
            return 2;
        case '\u0115':
            str[offset++]='e';
            str[offset]=BREVE;
            return 2;
        case '\u0116':
            str[offset++]='E';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u0117':
            str[offset++]='e';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u011a':
            str[offset++]='E';
            str[offset]=CARON;
            return 2;
        case '\u011b':
            str[offset++]='e';
            str[offset]=CARON;
            return 2;
        case '\u011c':
            str[offset++]='G';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u011d':
            str[offset++]='g';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u011e':
            str[offset++]='G';
            str[offset]=BREVE;
            return 2;
        case '\u011f':
            str[offset++]='g';
            str[offset]=BREVE;
            return 2;
        case '\u0120':
            str[offset++]='G';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u0121':
            str[offset++]='g';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u0122':
            str[offset++]='G';
            str[offset]=CEDILLA;
            return 2;
        case '\u0123':
            str[offset++]='g';
            str[offset]=CEDILLA;
            return 2;
        case '\u0124':
            str[offset++]='H';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0125':
            str[offset++]='h';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0128':
            str[offset++]='I';
            str[offset]=TILDE;
            return 2;
        case '\u0129':
            str[offset++]='i';
            str[offset]=TILDE;
            return 2;
        case '\u012a':
            str[offset++]='I';
            str[offset]=MACRON;
            return 2;
        case '\u012b':
            str[offset++]='i';
            str[offset]=MACRON;
            return 2;
        case '\u012c':
            str[offset++]='I';
            str[offset]=BREVE;
            return 2;
        case '\u012d':
            str[offset++]='i';
            str[offset]=BREVE;
            return 2;
        case '\u0130':
            str[offset++]='I';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u0134':
            str[offset++]='J';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0135':
            str[offset++]='j';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0136':
            str[offset++]='K';
            str[offset]=CEDILLA;
            return 2;
        case '\u0137':
            str[offset++]='k';
            str[offset]=CEDILLA;
            return 2;
        case '\u0139':
            str[offset++]='L';
            str[offset]=ACUTE;
            return 2;
        case '\u013a':
            str[offset++]='l';
            str[offset]=ACUTE;
            return 2;
        case '\u013b':
            str[offset++]='L';
            str[offset]=CEDILLA;
            return 2;
        case '\u013c':
            str[offset++]='l';
            str[offset]=CEDILLA;
            return 2;
        case '\u0143':
            str[offset++]='N';
            str[offset]=ACUTE;
            return 2;
        case '\u0144':
            str[offset++]='n';
            str[offset]=ACUTE;
            return 2;
        case '\u0145':
            str[offset++]='N';
            str[offset]=CEDILLA;
            return 2;
        case '\u0146':
            str[offset++]='n';
            str[offset]=CEDILLA;
            return 2;
        case '\u0147':
            str[offset++]='N';
            str[offset]=CARON;
            return 2;
        case '\u0148':
            str[offset++]='n';
            str[offset]=CARON;
            return 2;
        case '\u014c':
            str[offset++]='O';
            str[offset]=MACRON;
            return 2;
        case '\u014d':
            str[offset++]='o';
            str[offset]=MACRON;
            return 2;
        case '\u014e':
            str[offset++]='O';
            str[offset]=BREVE;
            return 2;
        case '\u014f':
            str[offset++]='o';
            str[offset]=BREVE;
            return 2;
        case '\u0150':
            str[offset++]='O';
            str[offset]=DOUBLE_ACUTE;
            return 2;
        case '\u0151':
            str[offset++]='o';
            str[offset]=DOUBLE_ACUTE;
            return 2;
        case '\u0154':
            str[offset++]='R';
            str[offset]=ACUTE;
            return 2;
        case '\u0155':
            str[offset++]='r';
            str[offset]=ACUTE;
            return 2;
        case '\u0156':
            str[offset++]='R';
            str[offset]=CEDILLA;
            return 2;
        case '\u0157':
            str[offset++]='r';
            str[offset]=CEDILLA;
            return 2;
        case '\u0158':
            str[offset++]='R';
            str[offset]=CARON;
            return 2;
        case '\u0159':
            str[offset++]='r';
            str[offset]=CARON;
            return 2;
        case '\u015a':
            str[offset++]='S';
            str[offset]=ACUTE;
            return 2;
        case '\u015b':
            str[offset++]='s';
            str[offset]=ACUTE;
            return 2;
        case '\u015c':
            str[offset++]='S';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u015d':
            str[offset++]='s';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u015e':
            str[offset++]='S';
            str[offset]=CEDILLA;
            return 2;
        case '\u015f':
            str[offset++]='s';
            str[offset]=CEDILLA;
            return 2;
        case '\u0160':
            str[offset++]='S';
            str[offset]=CARON;
            return 2;
        case '\u0161':
            str[offset++]='s';
            str[offset]=CARON;
            return 2;
        case '\u0162':
            str[offset++]='T';
            str[offset]=CEDILLA;
            return 2;
        case '\u0163':
            str[offset++]='t';
            str[offset]=CEDILLA;
            return 2;
        case '\u0164':
            str[offset++]='T';
            str[offset]=CARON;
            return 2;
        case '\u0165':
            str[offset++]='t';
            str[offset]=CARON;
            return 2;
        case '\u0168':
            str[offset++]='U';
            str[offset]=TILDE;
            return 2;
        case '\u0169':
            str[offset++]='u';
            str[offset]=TILDE;
            return 2;
        case '\u016a':
            str[offset++]='U';
            str[offset]=MACRON;
            return 2;
        case '\u016b':
            str[offset++]='u';
            str[offset]=MACRON;
            return 2;
        case '\u016c':
            str[offset++]='U';
            str[offset]=BREVE;
            return 2;
        case '\u016d':
            str[offset++]='u';
            str[offset]=BREVE;
            return 2;
        case '\u016e':
            str[offset++]='U';
            str[offset]=RING_ABOVE;
            return 2;
        case '\u016f':
            str[offset++]='u';
            str[offset]=RING_ABOVE;
            return 2;
        case '\u0170':
            str[offset++]='U';
            str[offset]=DOUBLE_ACUTE;
            return 2;
        case '\u0171':
            str[offset++]='u';
            str[offset]=DOUBLE_ACUTE;
            return 2;
        case '\u0174':
            str[offset++]='W';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0175':
            str[offset++]='w';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0176':
            str[offset++]='Y';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0177':
            str[offset++]='y';
            str[offset]=CIRCUMFLEX;
            return 2;
        case '\u0178':
            str[offset++]='Y';
            str[offset]=UMLAUT;
            return 2;
        case '\u0179':
            str[offset++]='Z';
            str[offset]=ACUTE;
            return 2;
        case '\u017a':
            str[offset++]='z';
            str[offset]=ACUTE;
            return 2;
        case '\u017b':
            str[offset++]='Z';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u017c':
            str[offset++]='z';
            str[offset]=DOT_ABOVE;
            return 2;
        case '\u017d':
            str[offset++]='Z';
            str[offset]=CARON;
            return 2;
        case '\u017e':
            str[offset++]='z';
            str[offset]=CARON;
            return 2;
        case '\u01cd':
            str[offset++]='A';
            str[offset]=CARON;
            return 2;
        case '\u01ce':
            str[offset++]='a';
            str[offset]=CARON;
            return 2;
        case '\u01cf':
            str[offset++]='I';
            str[offset]=CARON;
            return 2;
        case '\u01d0':
            str[offset++]='i';
            str[offset]=CARON;
            return 2;
        case '\u01d1':
            str[offset++]='O';
            str[offset]=CARON;
            return 2;
        case '\u01d2':
            str[offset++]='o';
            str[offset]=CARON;
            return 2;
        case '\u01d3':
            str[offset++]='U';
            str[offset]=CARON;
            return 2;
        case '\u01d4':
            str[offset++]='u';
            str[offset]=CARON;
            return 2;
        case '\u01d5':
            str[offset++]='U';
            str[offset++]=UMLAUT;
            str[offset]=MACRON;
            return 3;
        case '\u01d6':
            str[offset++]='u';
            str[offset++]=UMLAUT;
            str[offset]=MACRON;
            return 3;
        case '\u01d7':
            str[offset++]='U';
            str[offset++]=UMLAUT;
            str[offset]=ACUTE;
            return 3;
        case '\u01d8':
            str[offset++]='u';
            str[offset++]=UMLAUT;
            str[offset]=ACUTE;
            return 3;
        case '\u01d9':
            str[offset++]='U';
            str[offset++]=UMLAUT;
            str[offset]=CARON;
            return 3;
        case '\u01da':
            str[offset++]='u';
            str[offset++]=UMLAUT;
            str[offset]=CARON;
            return 3;
        case '\u01db':
            str[offset++]='U';
            str[offset++]=UMLAUT;
            str[offset]=GRAVE;
            return 3;
        case '\u01dc':
            str[offset++]='u';
            str[offset++]=UMLAUT;
            str[offset]=GRAVE;
            return 3;
        case '\u01de':
            str[offset++]='A';
            str[offset++]=UMLAUT;
            str[offset]=MACRON;
            return 3;
        case '\u01df':
            str[offset++]='a';
            str[offset++]=UMLAUT;
            str[offset]=MACRON;
            return 3;
        case '\u01e0':
            str[offset++]='A';
            str[offset++]=DOT_ABOVE;
            str[offset]=MACRON;
            return 3;
        case '\u01e1':
            str[offset++]='a';
            str[offset++]=DOT_ABOVE;
            str[offset]=MACRON;
            return 3;
        }
        str[offset]=c;  // nothing for us to do
        return 1;
    }


    private static void eatVerticalBars(char[] byteBuf,
      int byteOffset, int maxByteOffset, VerticalBarResult result) {
        boolean isDiacritic=false;
        int firstByte, secondByte, thirdByte;
        char newChar='\u007c';

        if(byteOffset+2<maxByteOffset) {
            firstByte=(byteBuf[byteOffset+1] & 0xff);
            if(firstByte!=0x7c) {
                secondByte=(byteBuf[byteOffset+2] & 0xff);
                if(secondByte==0x7c) { // one character sequences
                    byteOffset+=2;
                    switch(firstByte) {
                    case ' ':
                        newChar='\u00a0';  // non-breaking space
                        break;
                    case '$':
                        newChar='\u0024';  // dollar sign
                        break;
                    case '^':
                        newChar='\u005e';  // spacing caret
                        break;
                    case '_':
                        newChar='\u005f';  // spacing underscore
                        break;
                    case '`':
                        newChar='\u0060';  // spacing grave
                        break;
                    case '~':
                        newChar='\u0060';  // spacing tilde
                        break;
                    default:
                        byteOffset-=2;
                    }
                }
                else if(byteOffset+3<maxByteOffset) {
                    thirdByte=(byteBuf[byteOffset+3] & 0xff);
                    if(thirdByte==0x7c) { // two character sequences
                        byteOffset+=3;
                        switch(firstByte) {
                        case 'A': // AE
                            if(secondByte=='E')
                                newChar='\u00c6';     //Cap Digraph AE
                            else
                                byteOffset-=3;
                            break;
                        case 'D': // DC
                            if(secondByte=='C')
                                newChar='\u0110';   // Cap D w/crossbar
                            else
                                byteOffset-=3;
                            break;
                        case 'E': // ET
                            if(secondByte=='T')
                                newChar='\u00d0';     // Cap Eth
                            else
                                byteOffset-=3;
                            break;
                        case 'H': // HO HU
                            if(secondByte=='O')
                                newChar='\u01a0';     // Cap Hooked O
                            else if(secondByte=='U')
                                newChar='\u01af';     // Cap Hooked U
                            else
                                byteOffset-=3;
                            break;
                        case 'I': // IT
                            if(secondByte=='T')
                                newChar='\u00de'; //Cap Icelandic Thorn
                            else
                                byteOffset-=3;
                            break;
                        case 'O': // OE
                            if(secondByte=='E')
                                newChar='\u0152';     //Cap Digraph OE
                            else
                                byteOffset-=3;
                            break;
                        case 'P': // PL
                            if(secondByte=='L')
                                newChar='\u0141';     // Cap Polish L
                            else
                                byteOffset-=3;
                            break;
                        case 'S': // SO
                            if(secondByte=='O')
                                newChar='\u00d8';//Cap Scand. O w/slash
                            else
                                byteOffset-=3;
                            break;
                        case 'a': // ac ae al ay
                            if(secondByte=='c') {
                                newChar='\u0301';     // acute accent
                                isDiacritic=true;
                            }
                            else if(secondByte=='e')
                                newChar='\u00e6';     // digraph ae
                            else if(secondByte=='l')
                                newChar='\u02be';     // alif
                            else if(secondByte=='y')
                                newChar='\u02bf';     // ayn
                            else
                                byteOffset-=3;
                            break;
                        case 'b': // bp br b( b) b+ b- b0-9
                            if(secondByte=='p')
                                newChar='\u00ae';   // sub. patent mark
                            else if(secondByte=='r') {
                                newChar='\u0306';      // breve
                                isDiacritic=true;
                            }
                            else if(secondByte=='(')
                                newChar='\u208d';    // sub. left paren
                            else if(secondByte==')')
                                newChar='\u208e';   // sub. right paren
                            else if(secondByte=='+')
                                newChar='\u208a';     // sub. plus
                            else if(secondByte=='-')
                                newChar='\u208b';     // sub. minus
                            else if(secondByte=='0')
                                newChar='\u2080';     // sub. 0
                            else if(secondByte=='1')
                                newChar='\u2081';     // sub. 1
                            else if(secondByte=='2')
                                newChar='\u2082';     // sub. 2
                            else if(secondByte=='3')
                                newChar='\u2083';     // sub. 3
                            else if(secondByte=='4')
                                newChar='\u2084';     // sub. 4
                            else if(secondByte=='5')
                                newChar='\u2085';     // sub. 5
                            else if(secondByte=='6')
                                newChar='\u2086';     // sub. 6
                            else if(secondByte=='7')
                                newChar='\u2087';     // sub. 7
                            else if(secondByte=='8')
                                newChar='\u2088';     // sub. 8
                            else if(secondByte=='9')
                                newChar='\u2089';     // sub. 9
                            else
                                byteOffset-=3;
                            break;
                        case 'c': // ca cb cc ce cf co cs cu
                            if(secondByte=='a') {
                                newChar='\u030a';      // circle above
                                isDiacritic=true;
                            }
                            else if(secondByte=='b') {
                                newChar='\u0325';      // circle below
                                isDiacritic=true;
                            }
                            else if(secondByte=='c') {
                                newChar='\u0313';      // high comma centered
                                isDiacritic=true;
                            }
                            else if(secondByte=='e') {
                                newChar='\u0327';      // cedilla
                                isDiacritic=true;
                            }
                            else if(secondByte=='f') {
                                newChar='\u0302';      //circumflex
                                isDiacritic=true;
                            }
                            else if(secondByte=='o') {
                                newChar='\u0315';      //high comma offcenter
                                isDiacritic=true;
                            }
                            else if(secondByte=='s')
                                newChar='\u00a9';     // copyright sign
                            else if(secondByte=='u') {
                                newChar='\u0310';      // candrabindu
                                isDiacritic=true;
                            }
                            else
                                byteOffset-=3;
                            break;
                        case 'd': // da db dc dd dm ds
                            if(secondByte=='a') {
                                newChar='\u030b';      // double acute
                                isDiacritic=true;
                            }
                            else if(secondByte=='b') {
                                newChar='\u0323';      // dot below
                                isDiacritic=true;
                            }
                            else if(secondByte=='c')
                                newChar='\u0111';     // d w/cross bar
                            else if(secondByte=='d') {
                                newChar='\u0324';     // dbl dot below
                                isDiacritic=true;
                            }
                            else if(secondByte=='m')
                                newChar='\u00b7';     // dot in middle
                            else if(secondByte=='s')
                                newChar='\u00b0';     // degree sign
                            else
                                byteOffset-=3;
                            break;
                        case 'e': // et
                            if(secondByte=='t')
                                newChar='\u00f0';     // eth
                            else
                                byteOffset-=3;
                            break;
                        case 'g': // ga gb gc gr
                            if(secondByte=='a')
                                newChar='\u03b1';     // greek alpha
                            else if(secondByte=='b')
                                newChar='\u03b2';     // greek beta
                            else if(secondByte=='c')
                                newChar='\u03b3';     // greek gamma
                            else if(secondByte=='r') {
                                newChar='\u0300';     // grave
                                isDiacritic=true;
                            }
                            else
                                byteOffset-=3;
                            break;
                        case 'h': // ha hl ho hr hu
                            if(secondByte=='a') {
                                newChar='\u030c';     // hacek
                                isDiacritic=true;
                            }
                            else if(secondByte=='l') {
                                newChar='\u0326';     // hook left
                                isDiacritic=true;
                            }
                            else if(secondByte=='o')
                                newChar='\u01a1';     // hooked o
                            else if(secondByte=='r') {
                                newChar='\u0328';     // hook right
                                isDiacritic=true;
                            }
                            else if(secondByte=='u')
                                newChar='\u01b0';     // hooked u
                            else
                                byteOffset-=3;
                            break;
                        case 'i': // ie iq it
                            if(secondByte=='e')
                                newChar='\u00a1';     // inverted !
                            else if(secondByte=='q')
                                newChar='\u00bf';     // inverted ?
                            else if(secondByte=='t')
                                newChar='\u00fe';    // icelandic thorn
                            else
                                byteOffset-=3;
                            break;
                        case 'l': // ll lr
                            if(secondByte=='l') {
                                newChar='\ufe20';// ligature left half
                                isDiacritic=true;
                            }
                            else if(secondByte=='r') {
                                newChar='\ufe21';// ligature right half
                                isDiacritic=true;
                            }
                            else
                                byteOffset-=3;
                            break;
                        case 'm': // ma mf ms mz
                            if(secondByte=='a') {
                                newChar='\u0304';     // macron
                                isDiacritic=true;
                            }
                            else if(secondByte=='f')
                                newChar='\u266d';     // musical flat
                            else if(secondByte=='s')
                                newChar='\u266f';     // musical sharp
                            else if(secondByte=='z')
                                newChar='\u02b9';     // miagkiy znak
                            else
                                byteOffset-=3;
                            break;
                        case 'o': // oe
                            if(secondByte=='e')
                                newChar='\u0153';     // digraph oe
                            else
                                byteOffset-=3;
                            break;
                        case 'p': // pl pm pq ps p( p) p+ p-
                            if(secondByte=='l')
                                newChar='\u0142';     // polish l
                            else if(secondByte=='m')
                                newChar='\u00b1';     // plus or minus
                            else if(secondByte=='q') {
                                newChar='\u0309';     // pseudo ?
                                isDiacritic=true;
                            }
                            else if(secondByte=='s')
                                newChar='\u00a3';     // pound sterling
                            else if(secondByte=='(')
                                newChar='\u207d';     // sup. lt. paren
                            else if(secondByte==')')
                                newChar='\u207e';     // sup. rt. paren
                            else if(secondByte=='+')
                                newChar='\u207a';     // sup. +
                            else if(secondByte=='-')
                                newChar='\u207b';     // sup. -
                            else if(secondByte=='0')
                                newChar='\u2070';     // sup. 0
                            else if(secondByte=='1')
                                newChar='\u00b9';     // sup. 1
                            else if(secondByte=='2')
                                newChar='\u00b2';     // sup. 2
                            else if(secondByte=='3')
                                newChar='\u00b3';     // sup. 3
                            else if(secondByte=='4')
                                newChar='\u2074';     // sup. 4
                            else if(secondByte=='5')
                                newChar='\u2075';     // sup. 5
                            else if(secondByte=='6')
                                newChar='\u2076';     // sup. 6
                            else if(secondByte=='7')
                                newChar='\u2077';     // sup. 7
                            else if(secondByte=='8')
                                newChar='\u2078';     // sup. 8
                            else if(secondByte=='9')
                                newChar='\u2079';     // sup. 9
                            else
                                byteOffset-=3;
                            break;
                        case 'r': // rc rs
                            if(secondByte=='c') {
                                newChar='\u031c';     // right cedilla
                                isDiacritic=true;
                            }
                            else if(secondByte=='s')
                                newChar='\u2117';     // recording sign
                            else
                                byteOffset-=3;
                            break;
                        case 's': // sd sl so
                            if(secondByte=='d') {
                                newChar='\u0307';     // superior dot
                                isDiacritic=true;
                            }
                            else if(secondByte=='l')
                                newChar='\u2113';     // script l
                            else if(secondByte=='o')
                                newChar='\u00f8';   // scand. o w/slash
                            else
                                byteOffset-=3;
                            break;
                        case 't': // td ti tl tr tz
                            if(secondByte=='d') {
                                newChar='\u0303';     // tilde
                                isDiacritic=true;
                            }
                            else if(secondByte=='i')
                                newChar='\u0131';     // turkish i
                            else if(secondByte=='l') {
                                newChar='\ufe22';     // tilde left
                                isDiacritic=true;
                            }
                            else if(secondByte=='r') {
                                newChar='\ufe23';     // tilde right
                                isDiacritic=true;
                            }
                            else if(secondByte=='z')
                                newChar='\u02ba';     // tverdyi znak
                            else
                                byteOffset-=3;
                            break;
                        case 'u': // ud um up us
                            if(secondByte=='d') {
                                newChar='\u0333';     // underscore dbl
                                isDiacritic=true;
                            }
                            else if(secondByte=='m') {
                                newChar='\u0308';     // umlaut
                                isDiacritic=true;
                            }
                            else if(secondByte=='p') {
                                newChar='\u032e';     // upadhmaniya
                                isDiacritic=true;
                            }
                            else if(secondByte=='s') {
                                newChar='\u0332';     // underscore
                                isDiacritic=true;
                            }
                            else
                                byteOffset-=3;
                            break;
                        case 'z': // zj zn
                            if(secondByte=='j')
                                newChar='\u200d';     // 0 width joiner
                            else if(secondByte=='n')
                                newChar='\u200c'; // 0 width non-joiner
                            else
                                byteOffset-=3;
                            break;
                        default:
                            byteOffset-=3;
                        }
                    }
                }
            }
        }
        result.reset(newChar, byteOffset, isDiacritic);
    }
}

class VerticalBarResult {
    public boolean isDiacritic;
    public char    newChar;
    public int     byteOffset;

    public void reset(char newChar, int byteOffset, boolean isDiacritic) {
        this.newChar=newChar;
        this.byteOffset=byteOffset;
        this.isDiacritic=isDiacritic;
    }
}

