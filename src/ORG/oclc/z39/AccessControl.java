/*
(c)1996 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
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

package ORG.oclc.z39;

import ORG.oclc.ber.*;

/**
 * AccessControl defines the code/message values for an Access
 * Control request.
 * @version @(#)AccessControl.java	1.1 07/09/97
 * @author Lisa Cox
 */

public class AccessControl extends Throwable {

   public static final int unspecified = 0;
   public static final int oclcDatabaseAuthorization = 1;

   public static final String PROMPT1  = "1.2.840.10003.8.1";  
   public static final String DES1     = "1.2.840.10003.8.2";  
   public static final String KRB1     = "1.2.840.10003.8.3";  
   public static final String OCLC_ACCESS_CONTROL1 = 
                                      "1.2.840.10003.8.1000.17.1";

   public static final int Challenge = 1;
   public static final int Response = 2;
   public static final int SecurityChallengeSimpleForm = 37;
   public static final int Diagnostic = 223;

   private static final int oid = 8;
   private static final int externallyDefined = 0;


  /**
   * integer data type defining the type of access control challenge.
   */
    public int type;
  /**
   * Object containing information to pertinent to the access control
   * request, such as a list of database names requiring authorization.
   */
    public Object additionalInfo;

  /**
   * Object containing the information from the command that caused
   * the AccessControl to be thrown.
   */
   public Object referringObject; 
      
   public AccessControl() {
   }

/**
 * Constructs an AccessControl object using the input type and 
 * additional Information.
 * @param type error condition
 * @param additionalInfo object containing information to create the challenge
 * request.
 */
    public AccessControl(int type, Object additionalInfo) {
	this.type = type;
	this.additionalInfo = additionalInfo;
    }

/**
 * Constructs an AccessControl object using the input DataDir object containing
 * access control request information.
 * @param dir access control request information
 * request.
 */
   public AccessControl(DataDir dir) {
  
         this(dir, null);
   }

/**
 * Constructs an AccessControl object using the input DataDir object containing
 * access control request information.
 * @param dir access control request information
 * @param referrer the object containing the command for which the AccessControl
 * request was created.
 */
   public AccessControl(DataDir dir, Object referrer) {
  

     referringObject = referrer;

     DataDir tmp, child, dataDir=null;
   
     for (tmp = dir.child(); tmp != null && dataDir == null; tmp = tmp.next())
     {
         child = tmp.child();
         if (child.fldid() == oid) {
               child = child.child();
               if (child.getOID().equals(OCLC_ACCESS_CONTROL1)) {
                  type = oclcDatabaseAuthorization;
                  dataDir = child.next();
           }
         }
     }
     if (dataDir != null) {
       switch (type) {
         case oclcDatabaseAuthorization: 
           additionalInfo = new oclcAccessControl1(dataDir);
           break;
       }
     }
   }

/**
 * Returns an integer containing the type of AccessControl.
 * @return int 
 */

    public int type() {
	return type;
    }

/**
 * Returns the Object containing the additionalInformation needed to 
 * for creating the access control challenge request.
 * @return Object
 */
    public Object additionalInfo() {
	return additionalInfo;
    }

/**
 * Constructs a String representation of the object.
 * @return String
 */ 
    public String toString() {
      StringBuffer out = new StringBuffer();
      out.append("Challenge type= " + this.type + "\n");
      out.append("Challenge info = " + this.additionalInfo);
      return out.toString();
    }
/**
 * Adds the diagnostic in the default format to the parent directory
 */
    public void addChallengeInfo(DataDir parent) {

      switch (type) {
         case oclcDatabaseAuthorization:
          ((oclcAccessControl1)additionalInfo).buildDir(parent);
          break;
      }
 
    }

}

