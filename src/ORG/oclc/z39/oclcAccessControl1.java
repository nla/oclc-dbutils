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

import java.util.*;

import ORG.oclc.ber.*;

/**
 * oclcAccessControl1 the information passed/sent on an
 * AccessControl Request/Response to get database specific
 * user authorization and password information passed to
 * a remote server. 
 * @version @(#)oclcAccessControl1.java	1.1 07/09/97
 * @author Lisa Cox
 */

public class oclcAccessControl1 {

  /**
   * integer data type defining the RESOURCE field.
   */
   static final int RESOURCE = 1;
  /**
   * integer data type defining the UserID field.
   */
   static final int USERID = 2;
  /**
   * integer data type defining the Password field.
   */
   static final int PASSWORD = 3;

  /**
   * String data type containing the name of the resource for the 
   * userid and password.
   */
   public String resourceName[];
  /**
   * String data type containing the resource userID.
   */
   public String userId[];
  /**
   * String data type containing the resource password.
   */
   public String password[];

      
  /**
   * Constructs an oclcAccessControl1 object.
   */
   public oclcAccessControl1() {

   }

/**
 * Constructs an oclcAccessControl1 object using the input resource name.
 * @param resource the name of the resource.
 */
   public oclcAccessControl1(String resource) {

	this(resource, null, null);
    }

/**
 * Constructs an oclcAccessControl1 object using the input resource name.
 * @param resource the name of the resource.
 */
   public oclcAccessControl1(DataDir dir) {
      DataDir tmp, child;
      Vector dbnames = new Vector(1);
      Vector authos = new Vector(1);
      Vector passwords = new Vector(1);
      int count=0;


      for (tmp=dir.child(); tmp!= null; tmp=tmp.next()) {
        for (child = tmp.child(); child != null; child=child.next()) {
          switch(child.fldid()) {
            case RESOURCE: 
              dbnames.addElement(child.getString());
              authos.addElement(null);
              passwords.addElement(null); 
              break;

            case USERID: 
              authos.setElementAt(child.getString(), count);
              break;

            case PASSWORD: 
              passwords.setElementAt(child.getString(), count);
              break;
          }
        }
        count++;
      }
      if (count > 0) {
        resourceName = new String[dbnames.size()];
        userId = new String[dbnames.size()];
        password = new String[dbnames.size()];
        dbnames.copyInto((Object[])resourceName);
        authos.copyInto((Object[])userId);
        passwords.copyInto((Object[])password);

      }   
    }

/**
 * Constructs an oclcAccessControl object using the input resource name, userId,
 * and password. 
 * @param resource the name of the resource
 * @param userId the userid for the resource
 * @param password the password for the resource 
 */
   public oclcAccessControl1(String resource, String userId, String password) {

	this.resourceName = new String[] {resource};
        if (userId != null) 
          this.userId = new String[] {userId};
        else 
          this.userId = new String[1];

        if (password != null) 
          this.password = new String[] {password}; 
        else
          this.password = new String[1];
    }


   private final void insertResource(String userId,String password,int offset) {
      this.userId[offset] = userId;
      this.password[offset] = password;
   }

/**
 * Adds a resourceName entry for the input userId and password.
 * @param resource the name of the resource
 */
  public void addResource(String resource) {
     addResource(resource, null, null);
   }

/**
 * Adds a resourceName entry for the input userId and password.
 * @param resource the name of the resource
 * @param userId the userid for the resource
 * @param password the password for the resource 
 */
   public void addResource(String resource, String userId, String password) {
     String tmp[] = null;
     boolean done=false;
     if (resource != null) {
       for (int i=0; i<resourceName.length; i++) {
         if (resourceName[i].equals(resource)) {
            insertResource(userId, password, i);
            done = true; 
            break;
         }    
       }
       if (!done) {
         if (resourceName != null) {
           tmp = new String[resourceName.length+1];
           System.arraycopy(resourceName, 0, tmp, 0, resourceName.length);
           tmp[resourceName.length] = resource;
           resourceName = tmp;
         } 
         if (this.userId != null) {
           tmp = new String[this.userId.length+1];
           System.arraycopy(this.userId, 0, tmp, 0, this.userId.length);
           tmp[this.userId.length] = userId;
           this.userId = tmp;
         } 
         if (this.password != null) {
           tmp = new String[this.password.length+1];
           System.arraycopy(this.password, 0, tmp, 0, this.password.length);
           tmp[this.password.length] = password;
           this.password = tmp;
         } 
       }
     }
   }

/**
 * Constructs a String representation of the object.
 * @return String
 */ 
    public String toString() {
      StringBuffer out = new StringBuffer();
      out.append("oclcAccessControl1\n");
      if (resourceName != null) {

        for (int i=0; i<resourceName.length; i++) {
          if (resourceName[i] != null)
            out.append("Resource = '" + resourceName[i] + "'\n");
          if (userId[i] != null)        
            out.append("UserID = '" + userId[i] + "'\n");
          if (password[i] != null) 
            out.append("Password = '" + password[i] + "'\n");
        }
      }
      return out.toString();
    }
/**
 * Adds the diagnostic in the default format to the parent directory
 */
   public void buildDir(DataDir parent) {

       if (parent == null || resourceName == null || resourceName.length == 0)
         return;


       parent.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, 
          AccessControl.OCLC_ACCESS_CONTROL1);

       DataDir subdir, node;
       subdir = parent.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
 
       for (int i=0; i<resourceName.length; i++) {
         node = subdir.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
         node.add(oclcAccessControl1.RESOURCE, ASN1.CONTEXT, 
             resourceName[i]);
         if (userId[i] != null)
           node.add(oclcAccessControl1.USERID, ASN1.CONTEXT, 
             userId[i]);
         if (password[i] != null)
           node.add(oclcAccessControl1.PASSWORD, ASN1.CONTEXT, 
             password[i]);
 
       }
    }
}

