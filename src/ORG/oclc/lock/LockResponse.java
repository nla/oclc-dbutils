/* 
 * LockResponse.java  
 *****************************************************************************/

package ORG.oclc.lock;

import java.io.*;
import java.net.*;
import java.util.*;

import ORG.oclc.log.*;
import ORG.oclc.ber.*;
import ORG.oclc.util.*;
import ORG.oclc.lock.*;


public class LockResponse {

  public static final String statusString[]= {"SUCCEEDED", "FAILED",
                                             "NOCONNECTION"};
  public static final int SUCCEEDED    = 0;
  public static final int FAILED       = 1;
  public static final int NOCONNECTION = 2;

  public int status = -1;

  public DataDir response = null;
  public Vector results = null;

  public String toString() {
    
    StringBuffer sb=new StringBuffer();
    sb.append("LockResponse: status=");
    try {
      sb.append(statusString[status]);
    }
    catch(ArrayIndexOutOfBoundsException e) {
      sb.append("undefined");
    }

    sb.append("\nresults=").append(results);
    sb.append("\nresponse:\n").append(response);
    return sb.toString();
  }

}
