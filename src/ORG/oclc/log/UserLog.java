/*
 * UserLog.java
 *********************/

package ORG.oclc.log;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.SimpleDateFormat;

import ORG.oclc.log.Log;


public class UserLog extends Log {
  
  private PrintStream userLog=null;
  private boolean userLogFileOpen = false;  
  String logDirectory;
  String fileName;
  String user;

  SimpleDateFormat dateFmt = 
    new SimpleDateFormat("'D'yyyyMMdd.'T'HHmmss.SSS");
  
  
  /**
    * Construct a UserLog object.
    */
  public void UserLog() {
  }


  /**
    * Open the log file with the input naming parameters.
    * @param user the user id information
    * @param serverLogName the name of the log file
    * @param serverLogDir the directory for the file
    */
  public void open(String user, String fileName, String logDirectory) {
    
    if (userLogFileOpen || getTraceLevel() == 0) 
      return;

    // Set the TimeZone to the host's default 
    // (placed here because the Constructor is not called).
    dateFmt.setTimeZone(TimeZone.getDefault());

    SimpleDateFormat date1 = new SimpleDateFormat(".'D'yyyyMMdd.'T'HHmmss");
    date1.setTimeZone(TimeZone.getDefault());
    Date dateString = new Date();
    String now = date1.format(dateString);
    
    String logFile;
    
    if (logDirectory != null && logDirectory.length() > 0)  {
      if (logDirectory.endsWith(File.separator) == false )
        logDirectory += File.separator;
    }
    else {
      logDirectory = "";
    }  
    this.logDirectory = logDirectory;
    
    if (user == null || user.length() == 0)
      user = "";
    else
      user = "." + user;
    
    this.user = user;
    this.fileName = fileName;

    if (fileName != null && fileName.length() > 0) 
      logFile = logDirectory + fileName + now + user;
    else
      logFile = logDirectory + "UserLog" + now + user;
    
    try {
      if (! userLogFileOpen) {
        userLog = new PrintStream(new FileOutputStream(logFile),true);
        userLogFileOpen = true;
        print("UserLog.open()", 
              "User log opened successfully : " + logFile);
      }
    } catch (FileNotFoundException e) {
      try {
        int pos = logFile.indexOf(logDirectory) + logDirectory.length(); 
        logFile = logFile.substring(pos);
        userLog = new PrintStream(new FileOutputStream(logFile),true);
        userLogFileOpen = true;
        print("UserLog.open()", 
              "User log default opened successfully : " + logFile);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } catch (IOException e2) {
      e2.printStackTrace();
    }
  }

  /**
   * Open the log file with the original name information.
   */
  public void open() {
    open(this.user, this.fileName, this.logDirectory);
  }
 
  
  /**
    * Print to the user log.
    * @param fromClass the name of the class calling the print
    * @param msg the text to print
    */
  public void print(String fromClass, String msg) {
    
    if (userLogFileOpen) {
      if (fromClass != null) {
        Date dateString = new Date();
        String theDate = dateFmt.format(dateString);
        userLog.print("[");
        userLog.print(theDate);
        userLog.print("] [");
        userLog.print(fromClass);
        userLog.println("]");   
      }
      userLog.println(msg);
    }
  }


  /**  
   * Print Errors to the log.  
   * @param reporter the object reporting the error  
   * @param errorSeverity the Code for the severity of the Error  
   * @param excep the Exception object  
   * @param shortDescription a short textual description of the error  
   * @param otherDescription any text that can add additional information  
   * to describe the error.  
   */  
  public void printError(Object reporter, int errorSeverity,  
                                  Throwable excep,  
                                  String shortDescription,  
                                  String otherDescription) {

    String from =null; 
    if (reporter instanceof String) 
        from = (String)reporter; 
    else 
        from = reporter.getClass().getName(); 
    
    if (otherDescription != null && shortDescription != null) 
      print(from, 
        shortDescription + " : " +otherDescription); 
    else if (shortDescription != null) 
      print(from, shortDescription); 
    else if (otherDescription != null) 
       print(from, otherDescription); 
 
   if (excep != null )
      excep.printStackTrace(); 

       
  } 
  
  /**
    * Get the file object.
    */
  public PrintStream getLogfile() {
    return userLog;
  }

  /**
   * Sets the filename for the log file.
   * @param id the user identification string
   * @param logName the filename of the log
   * @param logDir the directory for the log file
   */
  public void setName(String id, String logName, String logDir) {
     this.user = id;
     this.fileName = logName;
     this.logDirectory = logDir;
  }

  /**
   * Sets the userlog rollover time - currently not used.
   * @param time the rollover time in seconds.
   */ 
  public void setrollOverTime(int time) {
  }

  /**
   * Shuts/Re-opens the user log file - currently not used.
   * @param currentTime the current time in milliseconds.
   */ 
  public void rollOver(long currentTime) {
  }

  /**
    * Close the user log.
    */
  public void close() {
    
    try {
      if (userLogFileOpen) {
        print("UserLog.close()", "Normal shutdown of user log.");
        userLog.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    userLogFileOpen=false;
  }
  
}

