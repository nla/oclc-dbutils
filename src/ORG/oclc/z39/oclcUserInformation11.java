/*
(c)1999 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
Ohio 43017-0702.  OCLC is a registered trademark of OCLC Online Computer
Library Center, Inc.
 
*/

package ORG.oclc.z39;

import ORG.oclc.ber.*;

/**
 * This class represents the Z39.50 user information field that
 * JaSSI includes in its Z39.50 init request.
 *
 * @see ORG.oclc.z39.userInformationField
 */
public class oclcUserInformation11 {
    
   /**
    *  Object identifier for this Z39.50 User Information Format object
    */
   public static final String OID = "1.2.840.10003.10.1000.17.11";

   /**
    * The field identifier for the user's authorization string
    */
   private static final int AUTHO = 1;

   /**
    * The field identifier for the user's password
    */
   private static final int PASSWORD = 2;

   /**
    * The field identifier for the user's uid
    */
   private static final int UID = 3;

   /**
    * The field identifier for user's system
    */
   private static final int SYSTEM = 4;

   /**
    * The field identifier for the code of the user's institution
    */
   private static final int INSTITUTION_CODE = 5;
   
   /**
    * The user's authorization string
    */
   private String autho;

   /**
    * The user's password
    */
   private String password;

   /**
    * The user's uid
    */
   private String uid;

   /**
    * The user's system
    */
   private String system;

   /**
    * The institution code of the user's institution
    */
   private String institutionCode;

  /**
   * true means Z39.50 client is internal client
   */ 
   private boolean isInternalZ39Client;
  

   /*
    * Returns user's autho
    */
   public String getAutho() {
      return autho;
   }

   /*
    * Returns user's password
    */
   public String getPassword() {
      return password;
   }

   /*
    * Returns user's uid
    */
   public String getUid() {
      return uid;
   }

   /*
    * Returns user's system
    */
   public String getSystem() {
      return system;
   }

   /*
    * Returns user's institution code
    */
   public String institutionCode() {
      return institutionCode;
   }

   /*
    * Returns true if user's Z39 client is internal
    */
   public boolean getIsInternalZ39Client() {
      return isInternalZ39Client;
   }

   public oclcUserInformation11(DataDir userInformationField) {
      DataDir dir;
	
      isInternalZ39Client = false;
     
      if ((userInformationField == null) ||
	  (userInformationField.fldid() != ASN1.single_ASN1_type)) {
         return;
      }
      
      dir = userInformationField.child();
      if ((dir == null) || (dir.fldid() != ASN1.SEQUENCE)) {
         return;
      }

      isInternalZ39Client = true;

      for (dir = dir.child(); dir != null; dir = dir.next()) {
         switch (dir.fldid()) {
	    case AUTHO:
	       autho = dir.getString();
	       break;

            case PASSWORD:
               password = dir.getString();
               break;

            case UID:
	       uid = dir.getString();
	       break;

            case SYSTEM:
               system = dir.getString();
               break;

	    case INSTITUTION_CODE:
               institutionCode = dir.getString();
               break;
       
            default:
	 }
      }

      // Require all elements to be present to be considered internal client
/*
      if ((autho != null) &&
          (password != null) && 
          (uid != null) && 
          (system != null) &&
          (institutionCode != null)) {
         isInternalZ39Client = true;
      }
      else {
         autho = null;
         password = null;
         uid = null;
         system = null;
         institutionCode = null;
      } 
*/
   }


   /**
    * @return userInformationField for an oclcUserInformation11
    */
   public static DataDir buildDir(String autho, String password, String uid,
                                  String system, String institutionCode) {
      DataDir dir = new DataDir(ASN1.SEQUENCE, ASN1.UNIVERSAL);
      if (autho != null) {
        dir.add(AUTHO, ASN1.CONTEXT, autho);
      }
      if (password != null) {
        dir.add(PASSWORD, ASN1.CONTEXT, password);
      }
      if (uid != null) {
        dir.add(UID, ASN1.CONTEXT, uid);
      }
      if (system != null) {
        dir.add(SYSTEM, ASN1.CONTEXT, system);
      }
      if (institutionCode != null) {
        dir.add(INSTITUTION_CODE, ASN1.CONTEXT, institutionCode);
      }
      
      DataDir tmpParent = new DataDir(ASN1.SEQUENCE, ASN1.UNIVERSAL);
      userInformationField.addOIDandData(tmpParent, OID, dir);
      
      return tmpParent.child();
   }

}

