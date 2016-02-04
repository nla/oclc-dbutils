// IniFile.java
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
import java.util.StringTokenizer;

/**
 * IniFile reads, indexes and provides access to 'windows' type .ini files
 * @version @(#)IniFile.java	1.3 10/29/96
 * @author Thom Hickey
 */

public class IniFile implements RichProperties {
  Hashtable sections = new Hashtable();
  Hashtable origSections;
  static String path = System.getProperties().getProperty("ini.path");
  public static String rootDir = "";
  public String fileName; 
  private String origfilename;
  private Vector order;
  private Vector leadingComments;
  private long lastModified;
  boolean preserveInputOrder = false;

  static final int startOfSectionChar = '[';


/**
  * Set the the directory path for all the inifile reads
  * @param dir the name of the directory to pre-pend to input filenames
  */
  public static void setRootDir(String dir) {

     if (dir == null || dir.length() == 0)
       return;
  
     if (dir.endsWith(File.separator) == false)
        rootDir = dir + File.separator;
     else
        rootDir = dir;

    return;
  }


  boolean skipToChar(Reader reader,
		     char c)
    {
      int charRead;
      try {
	while ((charRead = reader.read()) != c)
	  if (charRead == -1) {
	    //System.out.println("reached EOF in skipToChar on " + reader);
	    return false;
	  }
	
	return true;
      }
      catch(Exception e) {
	return false;
      }
    }

  static String getString(Reader reader,
			  char c,
			  int charToAdd) 
    {
      StringBuffer sb = new StringBuffer();
      int bin;

      if (charToAdd >= 0) {
	sb.append((char)charToAdd);
	if (charToAdd == '#' || charToAdd == '/')
	  c = '\n';
      }
      
      try {
	while ((bin=reader.read()) != c && bin != '\n') {
	  if (bin == -1) {
	    //System.out.println("reached EOF in getString on " + reader);
	    return "";
	  }
	  sb.append((char)bin);
	  if (sb.length() == 1 && (sb.charAt(0) == '#' || sb.charAt(0)=='/')) {
	    c = '\n';  //Set up to skip whole line
	  }
	}
      }
      catch(Exception e){
	return "";
      }

      if (c != '\n' && bin == '\n')
	return "";
    
      return (sb.toString());
    }

    static String getString(IniData inid, char c) { 
	int start = 0, pos = 0; 

	if (inid.pos >= inid.ary.length) { 
	    return(""); 
	}

	if (inid.ary[inid.pos] == '#' || inid.ary[inid.pos] == '/') {
	  c = '\n'; 
	}

	start = inid.pos; 
	while (true) { 
	    pos = inid.pos; 
	    inid.pos ++; 
	    if (pos >= inid.ary.length) { 
		break; 
	    } // eof 
	    if (inid.ary[pos] == c) { 
		break; 
	    } 
	    if (inid.ary[pos] ==  '\n') { 
		break;
	    } 
	} 
 
	if (start == pos) { 
	    return(""); 
	} 
	return (new String(inid.ary, start, pos-start));
    }  

  static String getString(Reader reader,
			  char c)
    {
      return getString(reader, c, -1);
    }

	/**
	  * Return the integer value of an octal character.
	  * @param c the integer value of the character in [0-7]
	  * @return the value (0-7) of the octal character c
	  * @exception IniInvalidValueException when an illegal character is submitted
	  */
	private static int octchar2int (int c) throws IniInvalidValueException {
		switch (c) {
		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7':
			return (c - '0');
		default:
			throw new IniInvalidValueException("illegal octal char: " + (char)c );
		}
	}

	/**
	  * Return the integer value of a hexadecimal character.
	  * @param c the integer value of the character in [0-9A-Fa-f]
	  * @return the value (0-15) of the hexadecimal character c
	  * @exception IniInvalidValueException when an illegal character is submitted
	  */
	private static int hexchar2int (int c) throws IniInvalidValueException {
		switch (c) {
		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
			return (c - '0');
		case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
			return (c - 'A' + 10);
		case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
			return (c - 'a' + 10);
		default:
			throw new IniInvalidValueException("illegal hex char: " + (char)c );
		}
	}

	/**
	  * Read a line from Reader and interpret embedded \ escapes.
	  * This method reads until a newline or until end of file;
	  * a newline at the end of the file is not required.
	  * Escapes include:
	  * <ul>
	  * 	<li>special characters:
	  *			<ul>
	  *				<li><tt>\\</tt> backslash
	  *				<li><tt>\n</tt> newline
	  *				<li><tt>\t</tt> tab
	  *				<li><tt>\r</tt> carriage return
	  *				<li><tt>\f</tt> formfeed
	  *				<li><tt>\b</tt> backspace
	  *			</ul>
	  * 	<li>octal (<tt>\000</tt> to <tt>\377</tt>) One to 3 digits are allowed.
	  * 	<li>Unicode (<tt>\<!-- -->u0000</tt> to <tt>\<!-- -->uFFFF</tt>) One to 4 hexadecimal "digits" are allowed.
	  * 	<li>a backslash before any other character, including a newline,
	  * 		includes that character in the returned string.
	  *			This allows UNIX-style line continuation.
	  * </ul>
	  * @param reader where to read from
	  * @return interpreted String (without trailing newline) or null on end of file
	  * @exception IniInvalidValueException when an illegal escape is read.
	  * Octal escapes begin with \<!-- -->[0-3] and be followed by [0-7].
	  * Unicode escapes begin with \<!-- -->u, followed by 1-4 characters in [0-9A-Fa-f].
	  * @exception IOException on an I/O error
	  */
	public static String readEscapedLine (Reader reader)
			throws IniInvalidValueException, IOException {
		StringBuffer buf;         // buffer to build line
		int 	c;                // char from input stream
		int 	escvalue;         // value of an octal or unicode escape
		int 	nchars;           // number of chars in escape
		int 	pushback = -1;    // char to push back if we overread an escape
 
		buf = new StringBuffer(100); // assume a "long" line's worth to start
		while (true) {
			if (pushback != -1) {
				c = pushback;
				pushback = -1;
			} else {
				c = reader.read();
			}
			if (c == -1) {
				break;
			} else if (c == '\n') {
				break;
                        } else if (c == '\r') {
                           c = reader.read();
                           if (c == '\n')
                             break;
                           else 
                             pushback = c;
                   
			} else if (c == '\\') {
				c = reader.read();
				switch (c) {
                                case '\r': // CR
                                  c = reader.read();
                                  if (c == '\n')
                                      c = -1;
                                  else
                                    pushback = c;
                                  break;

                                case '\n': c = -1; break; 
                                 
                                default: // just some extra quoting 
                                  buf.append("\\"); 
                                        break; 
                                } 
                        } 
                        if (c != -1) 
                          buf.append((char)c); 

		}
		if ((buf.length() == 0) && (c == -1)) {
			return (null);
			}
		return (buf.toString());
	}

    public static String readEscapedLine (IniData inid)  
	throws IniInvalidValueException, IOException { 
 
	int start = inid.pos; 
	StringBuffer buf = null;  // buffer to build line 
	int     c=-1;             // char from input stream 
	int     escvalue;         // value of an octal or unicode escape 
	int     nchars;           // number of chars in escape 
             
	int startInd = inid.pos; 
	int endInd = inid.pos; 


	while (inid.pos+1 < inid.ary.length) { 
	    endInd = inid.pos; 

	    c = inid.ary[inid.pos++]; 
	    if (c == -1) { 
		break; 
	    } else if (c == '\n') { 
		break; 
	    }
	    else if (c == '\r') { 
	      if (inid.pos+1 >= inid.ary.length) 
		break;
	      c = inid.ary[inid.pos++];
	      if (c == '\n') 
		break; 
	    } else if (c == '\\') { 
	      if (inid.pos+1 >= inid.ary.length)  
                break; 
	      c = inid.ary[inid.pos++]; 
	      if (buf == null) 
		buf = new StringBuffer(100); 
	      buf.append(inid.ary, startInd, endInd-startInd);
	      switch (c) { 
		case '\r': 
		  if (inid.pos+1 >= inid.ary.length)  
		    break; 
		    c = inid.ary[inid.pos++];  
		    if (c == '\n')
			c = -1;
		    else 
			buf.append(inid.ary[inid.pos-1]);
		    break;
		case '\n':
		    c = -1;
                    break;
		default: // just some extra quoting 
                    buf.append("\\");
		    buf.append(inid.ary[inid.pos-1]);
		    break; 
		}
		startInd = inid.pos;
	    } 
	} // end loop 
 
	if (buf != null) { 
	    if (startInd != endInd) { 
		buf.append(inid.ary, startInd, endInd-startInd); 
	    } 
	    if ((buf.length() == 0) && (c == -1)) { return (null); } 
	    return (buf.toString()); 
	} else { 
	    // we didn't do any escapes 
	    if ( (startInd == endInd) && (c ==-1)) { return(null); } 
	    return(inid.data.substring(startInd, endInd)); 
	} 
    }
  static int skipWhite(Reader reader)
    {
      int c;
      while(true) {
	try {
	  c = reader.read();
	}
	catch (IOException e) {
	  return -1;
	}
	
	if (c<0)
	  return c;
	byte b = (byte)c;
	if (b==' ' ||  b=='\n' || b=='\t' || b=='\r')
	  ;
	else
	  return c;
      }
    }
  
    static int skipWhite(IniData inid) { 

	int pos; 
	while(true) { 
	    pos = inid.pos; 
	    inid.pos ++; 
	    if (pos >= inid.ary.length) { return(-1); } 
	    if (!Character.isWhitespace(inid.ary[pos])) { 
		break; 
	    } 
	} 
	return(inid.ary[pos]); 
    } 

    static int getStartOfSection(IniData inid) { 
	int pos; 
	while(true) { 
	    pos = inid.pos; 
	    inid.pos ++; 
	    if (pos >= inid.ary.length) { return(-1); } 
	    if (inid.ary[pos] == startOfSectionChar) { break; } 
	} 
	return(inid.ary[pos]); 
    } 
   

    static int getNonWhiteChar(IniData inid){ 
	return skipWhite(inid); 
    } 
 
  static int getNonWhiteChar(Reader reader){
    return skipWhite(reader);
  }

  IniFileSection createSection(Reader reader,
			       String sname){
    IniFileSection section = new IniFileSection(sname, preserveInputOrder);
    while(section.addALine(reader));
    return section;
  }

    IniFileSection createSection(IniData inid, String sname){ 
	IniFileSection section = new IniFileSection(sname, preserveInputOrder); 
	while(section.addALine(inid)); 
	section.clearHash(); 
	return section; 
    } 


  String getSectionName(Reader reader) {
    String value = getString(reader, ']');
    int endParenOffset;
    // look for system property reference
    if (value.startsWith("$(") && (endParenOffset = value.indexOf(')')) != -1) {
      String newValue = System.getProperty
	(value.substring(2, endParenOffset));
      if (newValue != null) {
	if (endParenOffset < value.length() - 1)
	  value = newValue + value.substring(endParenOffset + 1).toLowerCase();
	else
	  value = newValue;
      }
    } else {
      value = value.toLowerCase();
    }
    return value;
  }

    String getSectionName(IniData inid) { 
	String value = getString(inid, ']'); 
	value = value.toLowerCase(); 
	return value; 
    } 

  private void doFirstIncludes(BufferedReader reader) {
    

    int c = IniFile.getNonWhiteChar(reader);
    String name, includeFile;
    Vector names = new Vector();
    //System.out.println("doFirstIncludes");
    while (c >= 0 && c != IniFile.startOfSectionChar) {
       name = getString(reader, '=', c).trim(); 
       if (name != null && name.length() > 0) {
       //       System.out.println("name is ~" + name + "~"); 
         if (name.startsWith("#include")) { 
            includeFile = name.substring(8); 
            includeFile = includeFile.replace('"', ' '); 
            includeFile = includeFile.trim(); 
	    //System.out.println("Have a first include: " + includeFile);
            names.addElement(includeFile); 
         }
         else if (preserveInputOrder && 
                   name.length() > 0 && name.charAt(0) == '#') 
  	     leadingComments.addElement(name);
       }
       c = IniFile.getNonWhiteChar(reader);

    }

    addIncludeIni(names);
  }

  private void doFirstIncludes(IniData inid) {
    
    String name, includeFile;
    Vector names = new Vector();
    while (inid.pos < inid.ary.length && 
	   inid.ary[inid.pos] != (char)IniFile.startOfSectionChar) {
       name = getString(inid, (char)IniFile.startOfSectionChar).trim(); 
       if (name != null && name.length() > 0) {
         if (name.startsWith("#include")) { 
            includeFile = name.substring(8); 
            includeFile = includeFile.replace('"', ' '); 
            includeFile = includeFile.trim(); 
	    //System.out.println("Have a first include: " + includeFile);
            names.addElement(includeFile); 
         }
         else if (preserveInputOrder && 
                   name.length() > 0 && name.charAt(0) == '#') 
  	     leadingComments.addElement(name);
       }
    }

    addIncludeIni(names);
  }

    /*  void addIncludeIni(Vector names) {
    String includeFile;
    for (int i=0; i< names.size(); i++) {

      includeFile = (String)names.elementAt(i);
      try  {
        IniFile included = new IniFile(includeFile);
        Enumeration s = included.getSections();
        String key;
        IniFileSection sect;
        // Save the included inifiles.
        if (IniFile.preserveInputOrder)
          order.addElement(included);

        // Add all include sections to the inifile
        for (; s.hasMoreElements() ; ) {
          key = (String)s.nextElement();
	  //System.out.println("Adding section: " + key);//
          sect = included.getSection(key);
          if (sect != null) 
            this.sections.put(key, sect);
        }


      }
      catch (Exception e) {
        System.out.println("Could not load include file : '" +
              includeFile + "': " + e);
      }
    }
    } */

  void addIncludeIni(Vector names) {
    if (names == null)
	  return;

    String includeFile;

    for (int i=0; i< names.size(); i++) {

      includeFile = (String)names.elementAt(i);
      try  {
        IniFile included = new IniFile(includeFile, preserveInputOrder);
        Enumeration s = included.getSections();
        String key;
        IniFileSection sect;

        // Save the included inifiles.
        if (preserveInputOrder)
          order.addElement((IniFile)included);

        // Add all include sections to the inifile
	for (; s.hasMoreElements() ; ) {
          key = (String)s.nextElement();
	  //System.out.println("Adding section: " + key);//
	  sect = (IniFileSection)included.sections.get(key);
          if (sect != null) {
	    if (this.sections.get(key) == null) 
		this.sections.put(key, sect);
	  }
        }

      }
      catch (Exception e) {
        System.out.println("Could not load include file : '" +
              includeFile + "': " + e);
      }
    }
  } 

  boolean addASection(BufferedReader reader){
    String sname = getSectionName(reader);
    //System.out.println("Got section name = '"+sname+"'");//
    if (sname.length()==0)
      return false;
    IniFileSection sect = createSection(reader, sname);
    sections.put(sname, sect);
    if (preserveInputOrder)
      order.addElement(sname);

    addIncludeIni(sect.includes());

    return true;
  }

    boolean addASection(IniData inid) { 
	String sname = getSectionName(inid); 
	//System.out.println("Got section name = '"+sname+"'"); 
	if (sname.length()==0) return false; 
	IniFileSection sect = createSection(inid, sname); 
	sections.put(sname.intern(), sect);
	if (preserveInputOrder) {
	  order.addElement(sname);
	  //System.out.println("Section includes are: " + sect.includes());
	  addIncludeIni(sect.includes());
	}

	return true; 
    } 

  /**
   * Sets a lookup Path for locating the inifile, by looking at the 
   * directory structure and locating the first occurrence of the file.
   * @parm path the directory path to look up.
   */ 
  static public void setPath(String path)  
  { 
      IniFile.path = path; 
  } 
 
  /** 
   * Gets the lookup Path for locating the inifile, by looking at the  
   * directory structure and locating the first occurrence of the file. 
   * @return path the directory path to look up. 
   */  

  static public String getPath()  
  { 
      return IniFile.path; 
  } 
 
  static private String locateFile(String fileName)  
  { 
  
      // Unix specific
      if (fileName.startsWith(File.separator) == true) 
        return fileName; 
 
      // NT Specific
      if (fileName.indexOf(":\\") == 1) {
        return fileName; 
      }

 
      //System.err.println("locating " + fileName); 
      if (path != null) { 
        StringTokenizer tokenizer = new StringTokenizer 
          (path, File.pathSeparator); 
        while (tokenizer.hasMoreTokens()) { 
          String dir = tokenizer.nextToken(); 
          File file = new File(dir, fileName); 
          //System.err.println("trying " + file); 
          try { 
            if (file.exists()) { 
              //System.err.println("returning " + file); 
              return file.toString(); 
            } 
          } 
          catch (SecurityException e) { 
          } 
        } 
      } 
      if (fileName.startsWith(File.separator) == false && 
          rootDir.length() > 0) 
        fileName = rootDir + fileName; 
      //System.err.println("Returning " + fileName); 
      return fileName; 
  } 
   
  public void setFilename(String filename) {
     this.origfilename = filename;
     this.fileName = locateFile(filename); 
  }

  public String origFilename() {
    return origfilename;
  }

 /**
   * Constructs a new IniFile object.
   */
  public IniFile() {
     order = new Vector(10);
     leadingComments = new Vector(3);
     preserveInputOrder = true;
  }
    public IniFile(String fileName) throws IOException {  
	this(fileName, false);
    }

    public IniFile(String fileName, boolean preserveOrder) throws IOException { 
	preserveInputOrder = preserveOrder;

	if (preserveInputOrder) {
	    this.order = new Vector(10);            
	    this.leadingComments = new Vector(3); 
	}
	this.origfilename = fileName;          
	this.fileName = locateFile(fileName);  
	String data = readIni(this.fileName);  
	IniData inid = new IniData(data);  
	inid.filename = this.fileName;  

	if (preserveInputOrder)
	    doFirstIncludes(inid);

	getStartOfSection(inid);  
	while(addASection(inid));  
    }

/**
  * Create a new IniFile object 
  * @param fileName the name of the iniFile
  * @exception IOException from failure to open input file
  */

    // old code left here until testing completed!
    /*  public IniFile(String fileName) throws IOException {

     this.order = new Vector(10);
     this.leadingComments = new Vector(3);
     this.origfilename = fileName;
     //     System.out.println("Reading new inifile: " + fileName);
     this.fileName = locateFile(fileName); 

     BufferedReader bufferedReader = null;

     try {
       File f = new File(this.fileName); 
       lastModified = f.lastModified();
       bufferedReader = new BufferedReader(new FileReader(f));
       doFirstIncludes(bufferedReader);  
       while(addASection(bufferedReader));
      }
      catch (IOException e) {
	  throw e;
        }
      finally {
       if (bufferedReader != null)
           bufferedReader.close();
      }

      }*/



    private String readIni(String fn) throws IOException { 
        byte ary[] = null; 
        BufferedInputStream is = null; 
        try { 
            File f = new File(fn); 
	    lastModified = f.lastModified(); 
            int nbytes = (int) f.length(), nread = 0, readSoFar = 0; 
            ary = new byte[nbytes+1]; 
            is = new BufferedInputStream(new FileInputStream(f)); 
            while (nread < nbytes) { 
                nread = is.read(ary, readSoFar, nbytes - readSoFar); 
                if (nread == -1) { break; } 
                readSoFar += nread; 
            } 
	    ary[nbytes] = (byte)'\n';  // make sure file ends in <CR>
        } catch (EOFException eof) { 
            eof.printStackTrace(); 
        } finally { 
            try { 
                is.close(); 
            } 
            catch (Exception ee) {} 
        } 

        String data = new String(ary); 
        data = preProcess(data); 
        return(data); 
    } 
      
    // resolve #include 
    // collapse lines that end in \\ back together 
    // remove comments 
    // Only do this when Don't care about preservation of input for
    // later writing it out
    private String preProcess(String data) throws IOException{ 
	if (!preserveInputOrder)
	  data = doIncludes(data); 
        return(data); 
    } 

    private String doIncludes(String data) throws IOException { 
        if (data.indexOf("#include") == -1) { 
	    return(data); 
	} 
        char ary[] = data.toCharArray(); 
        StringBuffer sb = new StringBuffer(ary.length); 
 
        int pos = 0, len = ary.length, begPos, incPos, begFilePos, endFilePos, 
	    nlpos; 

        String includeFileName, includeFileData; 
        while (pos < len) { 
            begPos = pos; 
       
            incPos = data.indexOf("#include", pos); 
            if (incPos == -1) { 
                pos = len - 1; 
                sb.append(data.substring(begPos)); 
                break;                        
            } else { 
                pos = incPos; 
                nlpos = data.lastIndexOf("\n", pos); 
                if (nlpos != (pos-1)) {  
                    pos += 8; 
                    sb.append(data.substring(begPos, pos)); 
                    continue; 
                } 
                sb.append(data.substring(begPos, pos)); 
                pos += 8; 
            } 
 
            pos++; 
 
            while (pos < len && Character.isWhitespace(ary[pos])) { pos ++; } 
            begFilePos = pos; 
            while (pos < len && !Character.isWhitespace(ary[pos])) { pos ++; } 
            endFilePos = pos; 
            includeFileName = data.substring(begFilePos, endFilePos); 

            includeFileName = includeFileName.replace('"', ' '); 
            includeFileName = includeFileName.replace('\'', ' '); 
            includeFileName = includeFileName.trim(); 
            includeFileName = locateFile(includeFileName); 

            // Check the cache for the include file. 
            // We only put included ini files in the cache.  
            includeFileData = getIncludeDataFromCache(includeFileName); 
            if (includeFileData == null) { 
                includeFileData = readIni(includeFileName); 
                putIncludeDataIntoCache(includeFileName, includeFileData); 
            } 
            sb.append(includeFileData); 
        } 
        return(sb.toString()); 
    } 

    private static Hashtable includeFileData = new Hashtable(); 
    private static synchronized String getIncludeDataFromCache(String fn) { 
        if (includeFileData == null) { return(null); } 
        return((String)includeFileData.get(fn)); 
    } 
    private static synchronized void putIncludeDataIntoCache(String fn, 
							     String data) { 
        if (includeFileData == null) { includeFileData = new Hashtable(); } 
        includeFileData.put(fn, data); 
    } 

    public static synchronized void clearIncludeDataCache() { 
        includeFileData = null; 
    } 

/**
  * Create a new IniFile object 
  * @param inputStream is the InputStream to read
  */
  public IniFile(java.io.InputStream inputStream){
    BufferedReader bufferedReader =
      new BufferedReader(new InputStreamReader(inputStream));
    this.order = new Vector(10);
    doFirstIncludes(bufferedReader);  
    while(addASection(bufferedReader));
  }

/**
  * Create a new IniFile object 
  * @param reader is the Reader to use
  */
  public IniFile(java.io.Reader reader){
    BufferedReader bufferedReader = new BufferedReader(reader);
    this.order = new Vector(10);
    doFirstIncludes(bufferedReader);  
    while(addASection(bufferedReader));
  }

    public String showOrder() {
	if (!preserveInputOrder)
	    return "";
	StringBuffer s = new StringBuffer();
        s.append("File: " + fileName + "\n");
	for (int i=0; i<order.size(); i++) { 
	    if (order.elementAt(i) instanceof String) 
		s.append("[" + (String)order.elementAt(i) + "]\n"); 
	    else {
             s.append("#include \"" +                 
		      ((IniFile)order.elementAt(i)).origfilename + "\"\n");
	     s.append(((IniFile)order.elementAt(i)).showOrder());
	    }
	} 
	return s.toString();
    }

    public IniFile resync() {
	if (!preserveInputOrder)
	    return this;

        File f;
        IniFile ini = this;
        
        try{
	    f = new File(fileName);
            if (f.lastModified() != lastModified) {
		ini = new IniFile(origfilename);
	    }
	}
        catch (Exception e) {
            return this;
        }
         
        for (int i=0; i<order.size(); i++) {  
	   if (order.elementAt(i) instanceof IniFile) {
		  order.setElementAt(((IniFile)order.elementAt(i)).resync(), i);
	      } 
	  }  
        return ini;
    }

    public void writeIniFile() { 
        if (!preserveInputOrder)
	    return;
	String dir;
	int pos= fileName.lastIndexOf(File.separator);
	if (pos != -1) {
	    File directory = new File(fileName.substring(0, pos));
	    if (!directory.exists()) { 
		directory.mkdirs(); 
	    } 
	}

        try {
	  BufferedWriter f = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(fileName)));
	  int i;
	  for (i=0; i<leadingComments.size(); i++) {
	      f.write((String)leadingComments.elementAt(i));
              f.write("\n");
	  }
 
	  for (i=0; i<order.size(); i++) {  
	      if (order.elementAt(i) instanceof String) {
                  IniFileSection s = 
		      (IniFileSection)sections.get((String)order.elementAt(i));
                  if (s != null) {
                    s.writeSection(f);
                    f.write("\n");
		  }
	      }
	      else { 
                  f.write("#include \"" +                  
			   ((IniFile)order.elementAt(i)).origfilename + "\"\n\n"); 
		  ((IniFile)order.elementAt(i)).writeIniFile(); 
	      } 
	  }  
          f.close();
        }
	catch (Exception e) {
	    e.printStackTrace();
	}
    }

  public Vector getAllIncludeFileNames() {
      if (!preserveInputOrder)
	  return null;
	Vector allIncludes = new Vector(order.size());
        IniFile f; 
        Vector subIncludes;
	Object o;
        for (int i=0; i<order.size(); i++) {
	    o = order.elementAt(i);
	    if (o instanceof IniFile) {
	       f = (IniFile)o;
	       if (f.origfilename != null)
		   allIncludes.addElement(f.origfilename);
	       subIncludes = f.getAllIncludeFileNames();
	       for (int j=0; j<subIncludes.size(); j++)
		   allIncludes.addElement(subIncludes.elementAt(j));
	    }
        }   
        return allIncludes;
    }

  public Vector getAllIncludes() {
      if (!preserveInputOrder)
	  return null;
	Vector allIncludes = new Vector(order.size());
        IniFile f; 
        Vector subIncludes;
	Object o;
        for (int i=0; i<order.size(); i++) {
	    o = order.elementAt(i);
	    if (o instanceof IniFile) {
		f = (IniFile)o;
		allIncludes.addElement(f);
		subIncludes = f.getAllIncludes();
		for (int j=0; j<subIncludes.size(); j++)
		    allIncludes.addElement(subIncludes.elementAt(j));
	    }
        }   
        return allIncludes;
    }

/**
  * Given a section name, key and value, add (or update) it in the 
  * IniFile object.
  * @param section the section name
  * @param key the key to look for
  * @param value the value to store.
  */

  public boolean putIntValue(String section, String key, int value) {
    if (!preserveInputOrder) 
	return false;
    return putValue(section.toLowerCase(), key, String.valueOf(value), 0);
  }

/**
  * Given a section name, key and value, add (or update) it in the 
  * IniFile object.
  * @param section the section name
  * @param key the key to look for
  * @param value the value to store.
  */

  public boolean putBooleanValue(String section, String key, boolean value) {
    if (!preserveInputOrder) 
	return false;
    String s;
    if (value)
	s = "true";
    else
        s = "false";  
    return putValue(section.toLowerCase(), key, s, 0);
  }

/**
  * Given a section name, key and value, add (or update) it in the 
  * IniFile object.
  * @param section the section name
  * @param key the key to look for
  * @param value the value to store.
  */
  public boolean putValue(String section, String key, String value) {

      return putValue(section.toLowerCase(), key, value, 0);
  }

  private boolean putValue(String section, String key, String value, int level) {
    if (!preserveInputOrder ||
	section == null || key == null || value == null) 
	return false;


    // locate the last section input that matches.
    IniFileSection sect= null;
    
    boolean didit = false;

    //  System.out.println("Put value: sect= " + section +  
    // " key= " + key + " value= " + value +
    //		       " level= " + level);

    for (int i=order.size()-1; i>= 0 && !didit; i--) {   
	if (order.elementAt(i) instanceof String) { 

            if (  ((String)order.elementAt(i)).equals(section)) {
  	       sect = getSection(section);
	       if (sect != null) {
                 sect.putValue(key, value);
                 didit=true;
   	       }
            }
	} 
	else {  
	    didit = ((IniFile)order.elementAt(i)).putValue(section, key, value,
                        level+1);
	}  
    }   

    if (!didit && level == 0) {
        // create whole new section at the highest level
        section = section.toLowerCase();
	sect = new IniFileSection(section, preserveInputOrder);
        order.addElement(section);
        sect.putValue(key, value);
	sections.put(section, sect);
        didit=true;
    }

    return didit;
  }

  public void removeSection(String section) {
    if (!preserveInputOrder || section == null)
	return;

    //    System.out.println("Remove section: " + section  + " in filename: " +
    // fileName);
    section = section.toLowerCase();

    for (int i=0; i<order.size(); ) {   
	if (order.elementAt(i) instanceof String) { 

            if (  ((String)order.elementAt(i)).equals(section)) {
		order.removeElementAt(i);
		sections.remove(section);
            }
            else 
		i++;  

	} 
	else {  
	    ((IniFile)order.elementAt(i)).removeSection(section);
            i++;
	}  
       
    }   

  }



  public void removeKey(String section, String key) {
    if (!preserveInputOrder || section == null || key == null)
	return;

    section = section.toLowerCase();

    // locate the last section input that matches.
    IniFileSection sect= null;
    
    //System.out.println("Remove Key value: filename= " + fileName + 
    // " sect= " + section +  " key= " + key);

    for (int i=0; i<order.size(); i++) {   
	if (order.elementAt(i) instanceof String) { 
            if (  ((String)order.elementAt(i)).equals(section)) {
  	       sect = getSection(section);
	       if (sect != null) {
                 sect.removeValue(key);
   	       }
            }
	} 
	else {  
	    ((IniFile)order.elementAt(i)).removeKey(section, key);
	}  
    }   

  }

  /**
   * Renames a section in the inifile with the newname.
   * @param oldname the old section name.
   * @param newname the new section name.
   */ 
  public void renameSection(String oldname, String newname) {
    if (!preserveInputOrder || 
	oldname == null || newname == null)
	return;

    //System.out.println("Rename section: " + oldname + " to: " + newname  + 
    // " in filename: " + fileName);
    oldname = oldname.toLowerCase();
    newname = newname.toLowerCase();
    IniFileSection sect= getSection(oldname);

    for (int i=0; i<order.size(); i++) {   
	if (order.elementAt(i) instanceof String) { 
            if (  ((String)order.elementAt(i)).equals(oldname)) {
    	        sect = getSection(oldname);
                if (sect != null) {
                   removeSection(oldname);
                   sect.sectionKey = newname;
                   sections.put(newname, sect);
                   order.insertElementAt(newname, i);
                 }  
                 break;
            }
	} 
	else {  
	    ((IniFile)order.elementAt(i)).renameSection(oldname, newname);
	}  
    }   

  }


/**
  * Given a section name, return the value specified for the input key
  * @param mainKey the section name
  * @param subKey the key to look for
  * @return String the key value or null if the key value does not exist
  */

  public String getValue(String mainKey, String subKey){
    IniFileSection section= getSection(mainKey);
    if (section==null) {
	return null;
    }
    return section.getValue(subKey);
  }

  /**
   *  Given a section name, return the value specified for the input key.
   *  If the value doesn't exist return the default.
   *  @param mainKey the section name
   *  @param subKey the key to look for
   *  @param defaultValue the default value
   *  @return String the key value
   */
  public String getValue(String mainKey,
			 String subKey,
			 String defaultValue)
  {
    String result = getValue(mainKey, subKey);
    if (result == null)
      return defaultValue;
    return result;
  }


  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return String the key value or null if the key value does not exist
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section.
  */
  public String getStringValue(String mainKey, String subKey)
    throws IniMissingSectionException,
	   IniMissingValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getStringValue(subKey);
    }
  
  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return String the key value or the defaultValue if the key value does
     not exist
  */
  public String getStringValue(String mainKey,
			       String subKey,
			       String defaultValue)
    {
      try {
	return getStringValue(mainKey, subKey);
      }
      catch (Exception e) {
      }
      return defaultValue;
    }
  
  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return int the key value
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section
     @exception IniInvalidValueException if the subKey value is not a valid int
  */
  public int getIntValue(String mainKey, String subKey) 
    throws IniMissingSectionException,
	   IniMissingValueException,
	   IniInvalidValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getIntValue(subKey);
    }
  

/**
  * Given a section name, return the Integer value specified for the input key - use for numeric values
  * @param mainKey the section name
  * @param subKey the key to look for
  * @param defaultValue the value to return if the key is not found
  * @return integer the key value
  */
  public int getIntValue(String mainKey,
			 String subKey,
			 int defaultValue){
    try {
      return getIntValue(mainKey, subKey);
    }
    catch (Exception e) {
    }
    return defaultValue;
  }


  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return int the key value
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section
     @exception IniInvalidValueException if the subKey value is not a valid float
  */
  public float getFloatValue(String mainKey, String subKey) 
    throws IniMissingSectionException,
	   IniMissingValueException,
	   IniInvalidValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getFloatValue(subKey);
      
    }
  

/**
  * Given a section name, return the Float value specified for the input key - use for numeric values
  * @param mainKey the section name
  * @param subKey the key to look for
  * @param defaultValue the value to return if the key is not found
  * @return float the key value
  */
  public float getFloatValue(String mainKey,
			     String subKey,
			     float defaultValue){
    IniFileSection section= getSection(mainKey);
    if (section==null) return defaultValue;
    return section.getFloatValue(subKey, defaultValue);
  }

  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return int the key value
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section
     @exception IniInvalidValueException if the subKey value is not set to true or false
  */
  public boolean getBooleanValue(String mainKey, String subKey) 
    throws IniMissingSectionException,
	   IniMissingValueException,
	   IniInvalidValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getBooleanValue(subKey);
    }
  

/**
  * Given a section name, return the boolean value(true/false) specified for the input key 
  * @param mainKey the section name
  * @param subKey the key to look for
  * @param defaultValue the value to return if the key is not found
  * @return float the key value
  */

  public boolean getBooleanValue(String mainKey, String subKey, boolean defaultValue){
    int val = 0;
    if (defaultValue == true)
      val = 1;
    else 
      val = 0;
    IniFileSection section= getSection(mainKey);
    if (section == null)
      return defaultValue;

    val = section.getIntValue(subKey, val);
    if (val == 0)
      return false;
    else 
      return true;
  }

  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return byte the key value
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section
     @exception IniInvalidValueException if the subKey value is not a valid byte
  */
  public byte getByteValue(String mainKey,
			   String subKey) 
    throws IniMissingSectionException,
	   IniMissingValueException,
	   IniInvalidValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getByteValue(subKey);
    }

  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return byte the key value or defaultValue if a key value can't be found
  */
  public byte getByteValue(String mainKey,
			   String subKey,
			   byte defaultValue)
    {
      try {
	return getByteValue(mainKey, subKey);
      }
      catch (Exception e){
      }
      return defaultValue;
    }
  
  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return byte the key value
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section
     @exception IniInvalidValueException if the subKey value is not a valid short
  */
  public short getShortValue(String mainKey,
			     String subKey) 
    throws IniMissingSectionException,
	   IniMissingValueException,
	   IniInvalidValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getShortValue(subKey);
    }

  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return short the key value or defaultValue if a key value can't be found
  */
  public short getShortValue(String mainKey,
			     String subKey,
			     short defaultValue)
    {
      try {
	return getShortValue(mainKey, subKey);
      }
      catch (Exception e){
      }
      return defaultValue;
    }
  
  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return long the key value
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section
     @exception IniInvalidValueException if the subKey value is not a valid long
  */
  public long getLongValue(String mainKey,
			   String subKey) 
    throws IniMissingSectionException,
	   IniMissingValueException,
	   IniInvalidValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getLongValue(subKey);
    }

  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return long the key value or defaultValue if a key value can't be found
  */
  public long getLongValue(String mainKey,
			   String subKey,
			   long defaultValue)
    {
      try {
	return getLongValue(mainKey, subKey);
      }
      catch (Exception e){
      }
      return defaultValue;
    }
  
  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return double the key value
     @exception IniMissingSectionException if the section name is not found
     @exception IniMissingValueException if the subKey value is not found within
     the section
     @exception IniInvalidValueException if the subKey value is not a valid double
  */
  public double getDoubleValue(String mainKey,
			       String subKey) 
    throws IniMissingSectionException,
	   IniMissingValueException,
	   IniInvalidValueException
    {
      IniFileSection section = getSection(mainKey);
      if (section == null)
	throw new IniMissingSectionException(mainKey + " in file " + fileName);
      return section.getDoubleValue(subKey);
    }

  /**
     Given a section name, return the value specified for the input key
     @param mainKey the section name
     @param subKey the key to look for
     @return double the key value or defaultValue if a key value can't be found
  */
  public double getDoubleValue(String mainKey,
			       String subKey,
			       double defaultValue)
    {
      try {
	return getDoubleValue(mainKey, subKey);
      }
      catch (Exception e){
      }
      return defaultValue;
    }
  
  


/**
  * Retrieve all the variables in a section
  * @param mainKey the section name
  * @return IniFileSection the section variables
  */
  public IniFileSection getSection(String mainKey){

      if (!preserveInputOrder) 
	  return (IniFileSection)sections.get(mainKey.toLowerCase());


      IniFileSection sect=null; 
      String key = mainKey.toLowerCase();

      for (int i=order.size()-1; i>= 0 && sect == null ; i--) {    
	  if (order.elementAt(i) instanceof String) {  
	      if (((String)order.elementAt(i)).equals(key)) {
		  sect = (IniFileSection)sections.get(key);
	      }
	  }  
	  else {   
	      sect = ((IniFile)order.elementAt(i)).getSection(mainKey);
	  }   
      }    
      return sect;
  }

  /** Retrieve all the variables in a section
   * @param mainKey the section name
   * @return Enumeration of the section variables
   */
   public Enumeration getSectionKeys(String mainKey){
       IniFileSection sect = getSection(mainKey);
       if (sect != null) 
          return sect.keys();

       return null;
   }

/**
  * Get  the section names in the inifile
  * @return Enumeration the enumeration of all the section name strings
  */
  public Enumeration getSections() {
      //      if (!preserveInputOrder)
	  return sections.keys();
	  //return uniqueSections.keys(); 
  }

  protected void dump(java.io.PrintStream ps){
    for (Enumeration e = sections.keys(); e.hasMoreElements();){
      String key = e.nextElement().toString();
      ((IniFileSection)sections.get(key)).dump(ps);
    }
  }

  protected String printSections(Hashtable sections) {
      StringBuffer s = new StringBuffer(100);
      for (Enumeration e = sections.keys(); e.hasMoreElements();) {
       String key = e.nextElement().toString();
       s.append((IniFileSection)sections.get(key));
      }
      return s.toString();
  }

  public String toString() {
      StringBuffer s = new StringBuffer();
      s.append("-------------------------------------\n");
      s.append("IniFileName: " + origfilename + "\n");
      s.append("-------------------------------------\n");
      if (order != null && order.size() > 0) {
	  Object o;
	  if (leadingComments.size() > 0)
	      s.append("Leading comments: " + leadingComments + "\n");
	  for (int i=0;i<order.size(); i++) {
	      o = order.elementAt(i);
	      if (o instanceof IniFile)
		  s.append(o);
	      else {
		  s.append((IniFileSection)sections.get((String)o));
	      }
	  }
      }
      else {
	  s.append(printSections(sections));
      }
      s.append("-------------------------------------\n");
      return s.toString();
  }

  /**
   * Create an Inifile string for an integer.
   * @param label the variable part of the string
   * @param value the value part of the string
   * @return String 'label = value\n'.
   */
  public static String makeInt(String label, int value) {
      return (label + "= " + String.valueOf(value) + "\n");
  }

/**
  * Create an Inifile string for an integer.
  * @param label the variable part of the string
  * @param value the value part of the string
  * @param deflt the default value for the variable
  * @return String 'label = value\n'.  If the value == deflt, return 
  * empty String
  */
   public static String makeInt(String label, int value, int deflt) {
      if (value == deflt)
        return "";

      return (label + "= " + String.valueOf(value) + "\n");
   }
/**
  * Create an Inifile string for a boolean.
  * @param label the variable part of the string
  * @param value the value part of the string
  * @return String 'label = value\n'. 
  */
   public static String makeBoolean(String label, boolean flag) {
      return (label + "= " + flag + "\n");
   }
  /**
   * Create an Inifile string for a boolean.
   * @param label the variable part of the string
   * @param value the value part of the string
   * @param deflt the default value for the variable
   * @return String 'label = value\n'.  If the value == deflt, return
   * empty String
   */
   public static String makeBoolean(String label, boolean flag,
                                    boolean deflt) {
      if (flag == deflt)
        return "";
 
      return (label + "= " + flag + "\n");
   }
  /**
   * Create an Inifile string for a String.
   * @param label the variable part of the string
   * @param name the value part of the string
   * @return String 'label = value\n'.  If the value == null, return
   * empty String
   */
  public static String makeString(String label, String name) {
      if (name != null && name.length() > 0)
        return (label + "= " + name + "\n");
      else
        return "";
  }

  /**
   * Create an Inifile string for a String.
   * @param label the variable part of the string
   * @param name the value part of the string
   * @param deft the default value
   * @return String 'label = value\n'.  If the value == null, return
   * empty String
   */
  public static String makeString(String label, String name, String deft) {
      return makeString(label, name, deft, false);
  }
 
  /**
   * Create an Inifile string for a String.
   * @param label the variable part of the string
   * @param name the value part of the string
   * @param deft the default value
   * @param quoteOutput flag indicating to surround output with single quotes
   * @return String 'label = value\n'.  If the value == null, return
   * empty String
   */
  public static String makeString(String label, String name, String deft, boolean quoteOutput) {
    if (name != null && name.length() > 0 && !name.equals(deft)) {
        if (quoteOutput)
          return (label + "= '" + name + "'\n");
        else
          return (label + "= " + name + "\n");
    }
      else
        return "";
  }
 

  public static void main(String [] argv){
    IniFile ini;
    boolean writeit=false;
    try {

	if (argv.length > 1) {
	    if (argv.length ==3) { 
	       writeit=true;
	    }

	    ini = new IniFile(argv[0], writeit);
	    if (writeit) {
		System.out.println("t2/x = " + ini.getValue("t2", "x"));
		ini.putValue("testsect", "value", "5");
		System.out.println("test/val = " + 
				   ini.getValue("testsect", "value"));
		ini.writeIniFile();
	    }
	}
	else 
	    ini = new IniFile(argv[0]);

    } catch(Exception e){
	e.printStackTrace();
      System.out.println("Exception in main: "+e);
      return;
    }
    System.out.println("Dumping inifile");
    System.out.println(ini);
    for (int j=1; ; j++) { 
	String sect = ini.getValue("Environments", "env"+j); 
	if (sect == null) { 
	    System.out.println("Couldn't get env"+j); 
	    break; 
	} 
	System.out.println("Section = " + sect); 
	String name = ini.getValue(sect, "name"); 
	System.out.println("Name = " + name); 
    }
    /*    System.out.println("Here is the database name: "+ini.getValue("Database", "Name"));

    try {
      System.out.println("IntValue(100)=" +
			 ini.getIntValue("test", "int", 100));
      System.out.println("IntValue=" +
			 ini.getIntValue("test", "int"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("LongValue(100)=" +
			 ini.getLongValue("test", "long", 100));
      System.out.println("LongValue=" +
			 ini.getLongValue("test", "long"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("ShortValue(100)=" +
			 ini.getShortValue("test", "short", (short)100));
      System.out.println("ShortValue=" +
			 ini.getShortValue("test", "short"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("ByteValue(100)=" +
			 ini.getByteValue("test", "byte", (byte)100));
      System.out.println("ByteValue=" +
			 ini.getByteValue("test", "byte"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("FloatValue(100)=" +
			 ini.getFloatValue("test", "float", (float)100.0));
      System.out.println("FloatValue=" +
			 ini.getFloatValue("test", "float"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("DoubleValue(100)=" +
			 ini.getDoubleValue("test", "double", 100.0));
      System.out.println("DoubleValue=" +
			 ini.getDoubleValue("test", "double"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("BooleanValue(100)=" +
			 ini.getBooleanValue("test", "boolean", true));
      System.out.println("BooleanValue=" +
			 ini.getBooleanValue("test", "boolean"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("StringValue(100)=" +
			 ini.getStringValue("test", "string", "100"));
      System.out.println("StringValue=" +
			 ini.getStringValue("test", "string"));
    }
    catch (Exception e) {
      System.out.println(e);
    }

    try {
      System.out.println("Value(100)=" +
			 ini.getValue("test", "value", "100"));
      System.out.println("Value=" +
			 ini.getValue("test", "value"));
    }
    catch (Exception e) {
      System.out.println(e);
      }*/

  }
}
