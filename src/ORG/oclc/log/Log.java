/*
 * Log.java
 *************************/

package ORG.oclc.log;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.SimpleDateFormat;
/**
 * The Log class defines common logging calls and abstract methods that must be
 * implemented by each type of logging class.  It also defines log levels
 * that the logging classes will recognize.
*/ 

public abstract class Log {

  public static Log out;
  /**
   * integer data type defining  current trace level for the log object.
   */
  protected int traceLevel = 0;

  public static final int TRACE_NONE             = 0;
  public static final int TRACE_ENTERS_AND_EXITS = 1;
  public static final int TRACE_PARMS            = 4;
  public static final int TRACE_MSGS             = 8;
  public static final int TRACE_READ_MSGS        = 16;
  public static final int TRACE_WRITE_MSGS       = 32;
  public static final int TRACE_ALL              = 64;
  public static final int TRACE_CERTIFIED        = 128;
  public static final int TRACE_CRITICAL         = 256;
  public static final int TRACE_FATAL            = 512;
  public static final int TRACE_WARNING          = 1024;
  public static final int TRACE_INFO             = 4096;
  public static final int TRACE_ARCHIVE          = 8192;
  public static final int TRACE_HOUSEKEEPING     = 16384;
  public static final int TRACE_ALL_ON           = 0xFFFFFFFF;


  /**
   * Open the log file using the input id string, logname, and logDirectory.
   * @param id the text to start the filename with
   * @param logName the common part of the filename
   * @param logDir the directory for the filename
   */
  public abstract void open(String id, String logName, String logDir);
  /**
   * Open the log file with the existing id string, logname, and logDirectory.
   */
  public abstract void open();

  /**
   * Sets the log filename variables to the input id string, logname, 
   * and logDirectory.
   * @param id the text to start the filename with
   * @param logName the common part of the filename
   * @param logDir the directory for the filename
   */
  public abstract void setName(String id, String logName, String logDir);
  
  /**
   * Close the log.
   */
  public abstract void close();

  /**
   * Prints the msg string to the logfile.
   * @param fromClass the name of the calling class
   * @param msg the text to print. 
   */
   abstract void print(String fromClass, String msg);

  /**
   * Sets the userlog rollover time - currently not used.
   * @param time the rollover time in seconds.
   */
  public abstract void setrollOverTime(int time);

  /**
   * Retrieves the PrintStream object for the logfile.
   * @return PrintStream 
   */
  public abstract PrintStream getLogfile();

  /**
   * Shuts/Re-opens the log file.
   * @param currentTime the current time in milliseconds.
   */
  public abstract void rollOver(long currentTime);


  /** 
   * Print Errors to the log. 
   * @param reporter the object reporting the error 
   * @param errorSeverity the Code for the severity of the Error 
   * @param excep an the Exception or Throwable object 
   * @param shortDescription a short textual description of the error 
   * @param otherDescription any text that can add additional information 
   * to describe the error. 
   */ 
  public abstract void printError(Object reporter, int errorSeverity, 
                                  Throwable excep, 
                                  String shortDescription, 
                                  String otherDescription);
     

  /**
   * Prints the input message text and the class from which it was
   * called to the log.
   * @param fromClass the name of the class invoking the print
   * @param msg the text to print
   */
  public final void println(String fromClass, String msg) {
        print(fromClass, msg);
  }

  /**
   * Prints the input message text to the log.
   * @param msg the text to print
   */
  public final void println(String msg) {
        print(null, msg);
  }

  /**
   * Prints the input object and the class from which it was
   * called to the log.
   * @param fromClass the name of the class invoking the print
   * @param msg the object to print
   */
  public final void println(String fromClass, Object msg) {
        print(fromClass, msg.toString());
  }

  /**
   * Prints the input object to the log.
   * @param msg the object to print
   */
  public final void println(Object msg) {
        print(null, msg.toString());
  }

  /**
   * Prints the input message text and the class from which it was
   * called to the log if the input trace level is turned on
   * @param traceType the trace level to compare to
   * @param fromClass the name of the class invoking the print
   * @param msg the text to print
   */
  public final void println(int traceType, String fromClass, String msg) {
       if ((traceLevel & traceType) != 0)
        print(fromClass, msg);
  }

  /**
   * Prints the input message text to the log,
   * if the input trace level is turned on 
   * @param traceType the trace level to compare to
   * @param msg the text to print
   */
  public final void println(int traceType, String msg) {
       if ((traceLevel & traceType) != 0)
        print(null, msg);
  }

  /**
   * Prints the input object and the class from which it was
   * called to the log if the input trace level is turned on
   * @param traceType the trace level to compare to
   * @param fromClass the name of the class invoking the print
   * @param msg the object to print
   */
  public final void println(int traceType, String fromClass, Object msg) {
      if ((traceLevel & traceType) != 0)
        print(fromClass, msg.toString());
  }

  /**
   * Prints the input object to the log if the input trace level is turned on
   * @param traceType the trace level to compare to
   * @param msg the object to print
   */
  public final void println(int traceType, Object msg) {
      if ((traceLevel & traceType) != 0)
        print(null, msg.toString());
  }

  /**
   * Retrieves the current Trace Level setting.
   * @return int
   */
  public final int getTraceLevel() {
     return (traceLevel);
  }

  /**
   * Sets the current Trace Level setting to the input integer.
   * @param newTraceLevel the new trace level
   */
  public final void putTraceLevel(int newTraceLevel) {
     traceLevel = newTraceLevel;
  }

  /**
   * Sets the current Trace Level setting to the string representation
   * of the trace level (TRACE_NONE for example).
   * @param traceString the new trace level
   */
  public final void putTraceLevel(String traceString) {
     putTraceLevel(parseTraceLevel(traceString));
  }

  /**
   * Tests to see if the input trace level is currently on.
   * @param traceType the trace level to test against
   * @return boolean
   */
  public final boolean traceOn(int traceType) {
      if ( (traceLevel & traceType) != 0)
       return true;
      else
       return false; 
  }

  /**
   * Decodes the input traceString values to a numeric value.
   * @param traceString a list of trace levels such as 
   * TRACE_MSGS+TRACE_ENTERS_AND_EXITS
   * @param int the numeric trace level 
   */
  public final static int parseTraceLevel(String traceString) {

    int traceLevel = 0;
    
    if (traceString == null)
      return 0;
    
    try {
      traceLevel = Integer.parseInt(traceString);
      return (traceLevel);
    } catch (NumberFormatException e) {
      traceLevel = 0;
    }
    
    traceString = traceString.replace('+', ' ');
    traceString = traceString.replace(',', ' ');
    traceString = traceString.replace('|', ' ');
    
    StringTokenizer tu = new StringTokenizer(traceString);
    
    while (tu.hasMoreTokens()) {
      String tmpString = tu.nextToken();
      
      if (tmpString.equalsIgnoreCase("TRACE_NONE"))
        traceLevel |= TRACE_NONE;
      if (tmpString.equalsIgnoreCase("TRACE_ENTERS_AND_EXITS"))
        traceLevel |= TRACE_ENTERS_AND_EXITS;
      if (tmpString.equalsIgnoreCase("TRACE_PARMS"))
        traceLevel |= TRACE_PARMS;
      if (tmpString.equalsIgnoreCase("TRACE_MSGS"))
        traceLevel |= TRACE_MSGS;
      if (tmpString.equalsIgnoreCase("TRACE_READ_MSGS"))
        traceLevel |= TRACE_READ_MSGS;
      if (tmpString.equalsIgnoreCase("TRACE_WRITE_MSGS"))
        traceLevel |= TRACE_WRITE_MSGS;
      if (tmpString.equalsIgnoreCase("TRACE_HOUSEKEEPING"))
        traceLevel |= TRACE_HOUSEKEEPING;
      if (tmpString.equalsIgnoreCase("TRACE_ALL")) {
        traceLevel = TRACE_ALL_ON;
        break;
      }
    }
    return (traceLevel);
  }

  /**
   * Decodes the input trace integer values to a string representing 
   * the trace level
   * @param traceInt the input trace level
   * @param String for the trace level such as TRACE_MSGS
   */
  public static String getTraceLevelString(int traceInt) {
       switch (traceInt) {
          case 0: return "TRACE_NONE";
          case 1: return "TRACE_ENTERS_AND_EXITS";
          case 4: return "TRACE_PARMS";
          case 8: return "TRACE_MSGS";
          case 16: return "TRACE_READ_MSGS";
          case 32: return "TRACE_WRITE_MSGS";
          case 64: return "TRACE_ALL";
          case 128: return "TRACE_CERTIFIED";
          case 256: return "TRACE_CRITICAL";
          case 512: return "TRACE_FATAL";
          case 1024: return "TRACE_WARNING";
          case 4096: return "TRACE_INFO";
          case 8192: return "TRACE_ARCHIVE";
          case 16384: return "TRACE_HOUSEKEEPING";
          case 0xFFFFFFFF: return "TRACE_ALL_ON";
          default: return "Unknown trace level";
      }
    }

  /**
   * Opens a stdout file for the input server name.
   * @param server the name of the server to open the file for
   */
  public static void setOut(String server) {
    
    String host = "";
    
    try {
      host = InetAddress.getLocalHost().toString();
      host = host.substring(host.indexOf('/') + 1);
    } catch (UnknownHostException e) {
      host = "unknown";
    }
    
    SimpleDateFormat date1 =
      new SimpleDateFormat("'.D'yyyyMMdd.'T'HHmmss.SSS");
    date1.setTimeZone(TimeZone.getDefault());
    Date dateString = new Date();
    String logFile = "errorLog." + server + ".host-" + host + 
      date1.format(dateString);
    
    try {
      PrintStream pLog = null;
      pLog = new PrintStream(new FileOutputStream(logFile));
      System.setOut(pLog);
      System.setErr(pLog);
      System.out.println("Critical error in " + server + " on host " + host);
    } catch (IOException e) {
      e.printStackTrace();        
    }
  }
  
}

