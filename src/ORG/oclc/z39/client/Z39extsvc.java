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

package ORG.oclc.z39.client;

import java.io.*;
import java.util.*;
import ORG.oclc.ber.*;
import ORG.oclc.z39.*;


/** Z39extsvc creates extended services requests with an already formulated task package as input and translates responses.  
 * 
 * @version @(#)Z39extsvc.java	1.1 07/09/97
 * @author Lisa Cox
 */

public class Z39extsvc {
/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;


/**
 * Status code for response.
 */
        public int status;

/**
 * The DataDir diagnostics received in the extended services response
 */
	public DataDir diagnostics;

/**
 * Error code if Request failed.
 */
	public int errorCode;
/**
 * Error message if Request failed.
 */
	public String errorMsg = "None provided";


/**
 * The TaskPackage DataDir.
 */
	public DataDir taskPackage;

/**
 * The DataDir object containing the response OtherInformation data.
 */
        public DataDir otherInformation;
/**
 * The ExtendedServices packageType oid.
 */
	public String packageType;

  /**
   * integer data type containing the number of bytes in the request.
   */
        public int requestLength;
  /**
   * integer data type containing the number of bytes in the response.
   */
        public int responseLength;

  /**
   * AccessControl object created when an access control
   * request is received.
   */
    public AccessControl accessControl;

/**
 * Z39session
 */
	public Z39session zsession;

	public Z39extsvc(Z39session z) {
	    zsession = z;
	}

	public Z39extsvc() {
	    zsession = new Z39session();
	}

/**
 * Creates and sends a Z39.50 ExtendedServices Request, gets and processes the Z39.50 response
 * @param referenceId Will be returned on Response.
 * @param function the extended services function
 * @param oid the Extended Services ObjectId
 * @param taskPackage the DataDir with the pre-build taskPackage
 * @param userName optional string to identify the user 
 * @param description optional string describing the action 
 * @param packageName optional string to identifying the package
 * @param waitAction optional value for the Extended Services wait action
 * @param allowableFunctionForPermissions one of the valid allowable function
 * settings for the permission - optional
 * @param elementSetName the optional ElementSetName for the request
 * @param otherInformation the DataDir object containing the otherInformation
 * field - optional
 * @param otherInformationOID the OID for the otherInformation field - optional
 * @exception Exception exceptions from BerConnect
 * @exception Diagnostic1 Z39.50 diagnostics 
 * @exception AccessControl exception thrown when server issues AccessControl
 * request. 
 */
	public void doExtSvc(int referenceId, int function, String oid,
		DataDir taskPackage, String userName, String description, 
                String packageName, int waitAction, 
                int retentionTimeValue, String retentionUnitSystem, 
                String retentionUnitType, String retentionUnit, 
                int retentionScaleFactor,
                int allowableFunctionForPermissions, String elementSetName,
                DataDir otherInformation, String otherInformationOID)
                throws Exception, Diagnostic1, AccessControl {

      
           BerString zRequest=null, zResponse=null;
           BerConnect zConnection;

           if (zsession == null || zsession.isConnected() == false) 
             throw new Diagnostic1(Diagnostic1.temporarySystemError, 
                      "Server Not Available");


          zConnection = (BerConnect)zsession.connection;

          zRequest = Request(referenceId, function, oid, taskPackage, userName, 
            description, packageName, waitAction, 
            retentionTimeValue, retentionUnitSystem,  
            retentionUnitType, retentionUnit,  
            retentionScaleFactor, 
            allowableFunctionForPermissions, elementSetName,
            otherInformation, otherInformationOID,
            0, 0);
  
           if (zRequest == null) {
            throw new Diagnostic1(Diagnostic1.temporarySystemError, 
               "Unable to create extended services request");
           }

           try {
             zResponse = zConnection.doRequest(zRequest); 
           }
	   catch (InterruptedIOException ioe) {  
               throw new Diagnostic1(Diagnostic1.databaseUnavailable,  
		     "Unable to send request to the Z39.50 server");  
           }  
           catch (Exception e1) {
             zsession.reset();
             throw new Diagnostic1(Diagnostic1.databaseUnavailable, 
                  "Unable to send request to the Z39.50 server");
           }

           if (zResponse == null) {
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                  "Invalid extended services response received from the Z39.50 Server");
           }


           Response(zResponse);


       }



/**
 * Creates a Z39.50 ExtendedServices Request.
 * @param referenceId Will be returned on Response.
 * @param function the extended services function
 * @param oid the Extended Services ObjectId
 * @param taskPackage the DataDir with the pre-build taskPackage
 * @param userName optional string to identify the user 
 * @param description optional string describing the action 
 * @param packageName optional string to identifying the package
 * @param waitAction optional value for the Extended Services wait action
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int function, String oid,
		DataDir taskPackage, String userName, String description, 
                String packageName, int waitAction) {

           return (Request(referenceId, function, oid, taskPackage, userName, 
                 description, packageName, waitAction, 
                 0, null, null, null, 0,
                 0, null, null, null, 0, 0));

         }

/**
 * Creates a Z39.50 ExtendedServices Request.
 * @param referenceId Will be returned on Response.
 * @param function the extended services function
 * @param oid the Extended Services ObjectId
 * @param taskPackage the DataDir with the pre-build taskPackage
 * @param userName optional string to identify the user 
 * @param description optional string describing the action 
 * @param packageName optional string to identifying the package
 * @param waitAction optional value for the Extended Services wait action
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int function, String oid,
		DataDir taskPackage, String userName, String description, 
                String packageName, int waitAction, 
                int extraLen, int offset) {

	   return Request(referenceId, function, oid,
		taskPackage, userName, description, 
                packageName, waitAction, 
                0, null, null, null, 0,
                0, null, null, null,
                0, 0);

        }
/**
 * Creates a Z39.50 ExtendedServices Request.
 * @param referenceId Will be returned on Response.
 * @param function the extended services function
 * @param oid the Extended Services ObjectId
 * @param taskPackage the DataDir with the pre-build taskPackage
 * @param userName optional string to identify the user 
 * @param description optional string describing the action 
 * @param packageName optional string to identifying the package
 * @param waitAction optional value for the Extended Services wait action
 * @param allowableFunctionForPermissions one of the valid allowable function
 * settings for the permission - optional
 * @param elementSetName the optional ElementSetName for the request
 * @param otherInformation the DataDir object containing the otherInformation
 * field - optional
 * @param otherInformationOID the OID for the otherInformation field - optional
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, int function, String oid,
		DataDir taskPackage, String userName, String description, 
                String packageName, int waitAction, 
                int retentionTimeValue, String retentionUnitSystem,  
                String retentionUnitType, String retentionUnit,  
                int retentionScaleFactor, 
                int allowableFunctionForPermissions, String elementSetName,
                DataDir otherInformation, String otherInformationOID,
                int extraLen, int offset) {


          if (function < 1 || function > 3)
              return null;

          // Null out return values
          reset();

          packageType = oid;

	  DataDir dir = new DataDir(Z39api.extendedservicesRequest, 
                 ASN1.CONTEXT);
          if (referenceId != 0)
	        dir.daddNum(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);
		
           dir.daddNum(Z39extsvcApi.function, ASN1.CONTEXT, function);

           dir.daddoid(Z39extsvcApi.type, ASN1.CONTEXT, oid);

           if (packageName != null && packageName.length() > 0)
             dir.daddChar(Z39extsvcApi.packageName, ASN1.CONTEXT, packageName);

           if (userName != null && userName.length() > 0)
             dir.daddChar(Z39extsvcApi.userid, ASN1.CONTEXT, userName);

           /** add retention Time **/
 
           /** add permissions **/
           if (allowableFunctionForPermissions != 0 && userName != null) {
             DataDir seq = dir.daddTag(Z39extsvcApi.permissions, ASN1.CONTEXT);
             seq = seq.daddTag(ASN1.SEQUENCE, ASN1.UNIVERSAL);
             seq = seq.daddTag(ASN1.SEQUENCE, ASN1.UNIVERSAL);
             seq.daddChar(1, ASN1.CONTEXT, userName);
             seq.daddNum(ASN1.SEQUENCE, ASN1.CONTEXT, 
                 allowableFunctionForPermissions);
           }
           if (description != null && description.length() > 0)
             dir.daddChar(Z39extsvcApi.description, ASN1.CONTEXT, description);

           dir.daddDir(taskPackage);

           if (waitAction > 0)
              dir.daddNum(Z39extsvcApi.waitAction, ASN1.CONTEXT, waitAction);

           if (elementSetName != null && elementSetName.length() > 0)
             dir.daddChar(Z39api.ElementSetName, ASN1.CONTEXT, elementSetName);

           if (otherInformation != null && otherInformationOID != null) {
             OtherInformation.addOIDandData(dir,
                    otherInformationOID, otherInformation);
           }

           if (zsession != null && zsession.sessionId != null)
           {
               OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));
           }

           if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println("ITEM Order: " + dir.toString());

//System.out.println("ITem Order Request: " + dir.toString());
         
           requestLength = dir.recLen() + extraLen;

	    if (extraLen != 0 || offset != 0)
                return new BerString(dir, extraLen, offset);
            else
                return new BerString(dir);
	}

	// this guy will need to be able to return a failure.
	// does he do it by throwing an exception?
	// some of the DataDir constructors are in the same boat.
	public void Response(BerString response) throws AccessControl {
	    DataDir rspdir = new DataDir(response);
	    Response(rspdir);
	}

	public void Response(DataDir response) throws AccessControl {
	    DataDir parm = null, subparm = null;

            if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println(
		"ExtSvc Response: " + response.toString());

//System.out.println("ItemORDER Response: " + response.toString());

	    // if fldid != Z39api.extendedservicesResponse - throw exception
	    // Z39api.logit
            if (response.fldid() == Z39api.accessControlRequest) {
                accessControl = new AccessControl(response, this);
                throw accessControl;
             }

	    errorCode = 0;
	    errorMsg = null;
            diagnostics = null;
            status = 1;
            responseLength = response.recLen();

            // This really saves the task package in the object for
            // other processes to examine 
	    for (parm=response.child();parm != null; parm = parm.next())
	    {

		switch (parm.fldid())
		{
		    case Z39api.ReferenceId:
			referenceId = parm.dgetNum();
			break;
		    case Z39extsvcApi.status:
			status = parm.dgetNum();
			break;
		    case Z39extsvcApi.taskPackage:
                        taskPackage = parm;
                        break;

		    case Z39extsvcApi.diagnostics:
                      if (parm.child() != null && parm.child().child() != null){ 
                        for(subparm=parm.child().child(); subparm != null; 
                            subparm=subparm.next())
                              switch(subparm.fldid())
                               {
                                  case ASN1.INTEGER:  /* integer */
                                    diagnostics = subparm;
                                    errorCode = subparm.dgetNum();
                                    break;
                                  case ASN1.VISIBLESTRING:  /* visiblestring */
                                    errorMsg = subparm.dgetChar();
                                    break;
                               }
                               break;
                          
                      }
                      break;
                    case Z39api.otherInformation: 
                        otherInformation = parm;
                        break; 

		}
	    }


            // The server didn't know who this request was from.  
            // We need to clean-up this
            // user so we can re-init the user
            if (errorCode == Diagnostic1.unknownSessionId) {
              if (zsession != null)
                zsession.fInitDone = false;
            }
	}


	public String toString() {
	    StringBuffer str = new StringBuffer(
			"Z39extsvc: referenceId(" + referenceId +
			")  packageType(" + packageType +
			")  status(" + status +
			")  errorCode(" + errorCode +
			")  requestLength(" + requestLength +
			")  responseLength(" + responseLength +
			")  errorMsg(" + errorMsg + ")\n");
            if (taskPackage != null)
              str.append("taskPackage:\n" + taskPackage);

            if (diagnostics != null)
              str.append("Diagnostics:\n" + diagnostics);

	    return str.toString();
	}

/**
  * Reset response values for re-use of object
  *
  */
  
        private void reset() {
	 errorCode = 0;
	 errorMsg = "None provided";
	 status = 0;
         taskPackage = null;
         packageType = null;
         diagnostics=null;
         requestLength=0;
         responseLength=0;
       }

}



