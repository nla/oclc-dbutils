/* 
 * Lock.java  
 *****************************************************************************/

package ORG.oclc.lock;

import java.io.*;
import java.net.*;
import java.util.*;

import ORG.oclc.log.*;
import ORG.oclc.ber.*;
import ORG.oclc.util.*;


/**
  * The Lock class is a container object that maintains the information 
  * from an individual Lock Server BER Request.
  * <p>
  * This class also defines the BER tag structure for a Lock Server Request
  * and Response, Actions associated with a Lock Server Request, and
  * a status of the Lock Request Action indicated in the Lock Server Response.
  *
  */
public class Lock {

  // BER tag structure
  public static final int ROOT_TAG       = 1000;
  public static final int COMMAND_TAG    = 1001;
  public static final int USERID_TAG     = 1010;
  public static final int KEY_TAG        = 1020;
  public static final int DESC_TAG       = 1030;
  public static final int STATUS_TAG     = 1040;
  public static final int QUERY_TAG      = 1050;
  public static final int TYPE_TAG       = 1060;
  public static final int TIMEOUT_TAG    = 1070;
  public static final int TIMESTAMP_TAG  = 1080;

  // Lock Actions
  public static final int LOCK             = 1;
  public static final int UNLOCK           = 2;
  public static final int EXCLUSIVELOCK    = 3;
  public static final int NONEXCLUSIVELOCK = 4;
  public static final int QUERYLOCK        = 5;
  public static final int TOUCH            = 6;

  // Lock Status
  public static final String STATUS_LOCKED         = "Locked";
  public static final String STATUS_UNLOCKED       = "Unlocked";
  public static final String STATUS_LOCKEXISTS     = "Lock Exists";
  public static final String STATUS_NOLOCKEXISTS   = "No Lock Exists";
  public static final String STATUS_RECEXISTS      = "Record Exists";
  public static final String STATUS_NORECEXISTS    = "No Record Exists";
  public static final String STATUS_TOUCHED        = "Touched";
  public static final String STATUS_FAILED         = "Request Failed";

  private static final String nL = System.getProperty("line.separator");

  private String command = null;
  private String userId  = null;
  private String key     = null;
  private String desc    = null;
  private int type = 0;
  private long timeStamp;                      
  private long timeOut = 0;             // Default no timeout (milliseconds)


  /** 
    * Constructs a Lock Object.
    */
  public Lock() {

    timeStamp = System.currentTimeMillis();
  }

  /** 
    * Constructs a Lock Object with a timeout parameter.
    * @param t the timeout parameter for this lock in milliseconds.
    */
  public Lock(long t) {
    timeStamp = System.currentTimeMillis();
    timeOut = t;
  }


  /** Returns the Lock command specified in this Lock object.
    * @return the Lock command specified in this Lock object.
    */
  public final String getLockCommand() {
    return (this.command);
  }

  /** Returns the Lock user id specified in this Lock object.
    * @return the Lock user id specified in this Lock object.
    */
  public final String getLockUserId() {
    return (this.userId);
  }

  /** Returns the Lock key specified in this Lock object.
    * @return the Lock key specified in this Lock object.
    */
  public final String getLockKey() {
    return (this.key);
  }

  /** Returns the Lock type specified in this Lock object.
    * @return the Lock type specified in this Lock object.
    */
  public final int getLockType() {
    return (this.type);
  }

  /** Returns the Lock description specified in this Lock object. 
    * @return the Lock description specified in this Lock object. 
    */
  public final String getLockDesc() {
    return (this.desc);
  }

  /** Returns the Lock timestamp specified in this Lock object.
    * @return the Lock timestamp specified in this Lock object. 
    */
  public final long getTimeStamp() {
    return (this.timeStamp);
  }

  /** Returns the Lock timeout period for this Lock object. 
    * @return the Lock timeout period for this Lock object. 
    */
  public final long getTimeout() {
    return (this.timeOut);
  }

  /** Sets the Lock command to the specified input command.
    * @param c the Lock command.
    */
  public final void setLockCommand(String c) {
    this.command = c;
  }

  /** Sets the Lock user id to the specified input user id.
    * @param u the Lock user id.
    */
  public final void setLockUserId(String u) {
    this.userId = u;
  }

  /** Sets the Lock key to the specified input key.
    * @param k the Lock key.
    */
  public final void setLockKey(String k) {
    this.key = k;
  }

  /** Sets the Lock type to the specified input type.
    * @param t the Lock type.
    */
  public final void setLockType(int t) {
    this.type = t;
  }

  /** Sets the Lock description to the specified input description.
    * @param d the Lock description.
    */
  public final void setLockDesc(String d) {
    this.desc = d;
  }

  /** Sets the Lock timeout value to the specified input timeout value.
    * @param t the Lock timeout value.
    */
  public final void setTimeout(long t) {
    this.timeOut = t;
  }

  /** Sets the Lock timestamp to the specified input timestamp.
    * @param t the Lock timestamp.
    */
  public final void setTimeStamp(long t) {
     this.timeStamp = t;
  }

  /**
    * Checks the Lock object to see if it has timed out or expired.
    *
    * @return a boolean indicating true if a timeout has occurred
    * and false if not.
    */
  public boolean timedOut() {

     if (timeOut == 0)
       return(false);

     if ((System.currentTimeMillis() - timeStamp) > timeOut)
       return(true);

     return(false);
  }
 

  /**
    * Parses the BER Request sent to the Lock Server and stores the
    * request information in the Lock object.
    *
    * @param berRequest the BER Request message sent to the Lock Server.
    */
  public void parseBERRequest(BerString berRequest) {

    if (berRequest == null) return;
    DataDir dir = new DataDir(berRequest);
    parseBERRequest(dir);

  }


  /**
    * Parses the BER Request sent to the Lock Server and stores the
    * request information in the Lock object.
    *
    * @param dir the DataDir record sent in the BER Request message 
    * to the Lock Server.
    */
  public void parseBERRequest(DataDir dir) {

    DataDir subdir = dir.child();
    while (subdir != null) {
      switch(subdir.fldid()) {
        case COMMAND_TAG: setLockCommand(subdir.dgetChar()); 
            break;
        case USERID_TAG: setLockUserId(subdir.dgetChar()); 
            break;
        case KEY_TAG: setLockKey(subdir.dgetChar()); 
            break;
        case DESC_TAG: setLockDesc(subdir.dgetChar()); 
            break;
        case TYPE_TAG: setLockType(subdir.dgetNum()); 
            break;
        case TIMEOUT_TAG: setTimeout(subdir.dgetLong()); 
            break;
        case TIMESTAMP_TAG: setTimeStamp(subdir.dgetLong()); 
            break;
      }
      subdir = subdir.next();
    }

  }


  /** 
    * Generates the String representation of this object.
    * @return the String representation of this object
    */
  public final String toString() {
    StringBuffer buf = new StringBuffer();
 
    buf.append("-------------------" + nL);
    buf.append("Lock Object" + nL);
    buf.append("-------------------" + nL);
    buf.append("Lock Command     = " + this.command + nL);
    buf.append("Lock UserId      = " + this.userId + nL);
    buf.append("Lock Key         = " + this.key + nL);
    buf.append("Lock Type        = " + this.type + nL);
    buf.append("Lock Description = " + this.desc + nL);
    buf.append("Lock TimeStamp   = " + this.timeStamp + nL);
    buf.append("Lock Timeout     = " + this.timeOut + nL);
    
    return(buf.toString());
  }

}
