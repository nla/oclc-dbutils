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
import java.net.*;
import java.util.Enumeration; 

import ORG.oclc.ber.*;
import ORG.oclc.z39.*;

/** Z39init creates InitRequests and translates InitResponses.
 * @version @(#)Z39init.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39init {

/**
 * Value passed to Request and returned by a target Z39.50 server and
 * Response stores it here.
 */
	public int referenceId;

/**
 * Value passed as part of sessionId response containing the transactionId.
 * Response stores it here.
 */
	public String txnId;

/**
 * Negotiates whether specific features are available. See Z39.50 
 * Specification.
 */
	public String options;

/**
 * Preferred maximum size in bytes of responses to a PresentRequest. 
 * See Z39.50 Specification.
 */
	public int preferredMessageSize;

/**
 * Override of preferredMessageSize for retrieval of exceptionally large 
 * records. See Z39.50 Specification.
 */
	public int maximumRecordSize;

/**
 * 'Accept' or 'Reject'. See Z39.50 Specification. 
 */
	public short result;

/**
 * String containing message from Z39.50 server.
 */
	public String MessageOfTheDay;

/**
 * Array of available DatabaseNames.
 */
	public String DBList[];
	public String DisplayDBList[];

/**
 * Reason InitRequest failed.
 */
	public short failureCode;
/**
 * Failure message for init request.
 */
	public String failureMsg;

/**
 * integer data type containing the number of bytes in the request.
 */
      public int requestLength;
/**
 * integer data type containing the number of bytes in the response.
 */
        public int responseLength;


/**
 * Z39session
 */
	public Z39session zsession;

	public Z39init() {
 	}

	public Z39init(Z39session z) {
	    zsession = z;
	}

/**
 * Creates, sends, and parses the response for a Z39.50 Init.
 * @param referenceId Will be returned on Response and allows Requests
 * and responses to be matched up by the application.
 * @param options See Z39.50 Specification.
 * @param preferredMessageSize See Z39.50 Specification.
 * @param maximumRecordSize See Z39.50 Specification.
 * @param id use null for anonymous access
 * @param password use null for anonymous access
 * @param newpassword See Z39.50 Specification.
 * @param userInformationField See Z39.50 Specification.
 * @return boolean indicating success of failure of request
 * @exception Exception caught from BerConnect classes trying to send/receive the message
 * @exception Diagnostic1 Z39.50 Diagnostic message
 */


	public void doInit(int referenceId, String options,
			 int preferredMessageSize, int maximumRecordSize,
			 String id, String password, String newpassword,
			 DataDir userInformationField,
                         boolean reconnect)
                         throws Exception, Diagnostic1 {

          BerString zRequest = null, zResponse = null;
          BerConnect zConnection = null;

          if (zsession == null) {
            throw new Diagnostic1(Diagnostic1.temporarySystemError,
                      "User's Z39.50 session is not initialized");
          }

         
          if (zsession.connection == null) {
            try {
		zConnection = new BerConnect(zsession.host, 
					      zsession.port, 
					      zsession.readTimeout);
	    }
	    catch (SocketException s) {
		/* Address already in use  */
		for (int i = 0; i < 2; i++) {
		    try {
			Thread.sleep(500);
			zConnection = new BerConnect(zsession.host, 
						     zsession.port, 
						     zsession.readTimeout);
			break;
		    }
		    catch (Exception e) {
			//hmmm, probably should look at these ...
		    }
		}
	    }
            catch (Exception n) {
              zsession.reset();
              throw new Diagnostic1(Diagnostic1.databaseUnavailable,
                     "Unable to connect to the Z39.50 server");
            }

            if (zConnection == null) {
              throw new Diagnostic1(Diagnostic1.databaseUnavailable,
                     "Unable to connect to the Z39.50 server");
            }

            zsession.connection = (Object) zConnection;
         }
         else
           zConnection = (BerConnect)zsession.connection;

         zsession.setUserInfo(userInformationField);
         zsession.reconnect = reconnect;
         
         if (zsession.fInitDone == false) {

          zRequest = Request(referenceId, options,
			 preferredMessageSize, maximumRecordSize,
			 id, password, newpassword,
			 userInformationField, reconnect, 0, 0);
          if (zRequest == null) {

            if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println("unable to build init request");

            throw new Diagnostic1(Diagnostic1.temporarySystemError, 
				  "Unable to create init request");
          }

          try { zResponse = zConnection.doRequest(zRequest); }
            catch (Exception n) {
              zsession.reset();
              throw new Diagnostic1(Diagnostic1.databaseUnavailable,
                  "Unable to send request to the Z39.50 server");
            }

          if (zResponse == null) {
             throw new Diagnostic1(Diagnostic1.temporarySystemError,
                  "Invalid init response received from the Z39.50 Server");
           }


           Response(zResponse);

         }
         return;
     }

/** 
  * Reinitializes session to Z39.50 server if connection is gone
  * The init parms to the server are the original parms
  *
  * @exception Exception caught from BerConnect classes trying to send/receive the message
  * @exception Diagnostic1 Z39.50 Diagnostic message
  */
     public void reInit() throws Exception, Diagnostic1 {

       if (zsession != null && zsession.isConnected() == false) {
         try {
              doInit(zsession.refId, null,
              preferredMessageSize != 0 ? preferredMessageSize : 5000000,
              maximumRecordSize != 0 ? maximumRecordSize : 10000000,
              zsession.autho, zsession.password, null,
              zsession.userInfo, zsession.reconnect);
         }
         catch (Exception e) {
           throw e;
         }
         catch (Diagnostic1 d) {
           throw d;
         }
       }

       return;
     } 

/**
 * Creates a Z39.50 InitRequest.
 * @param referenceId Will be returned on Response and allows Requests
 * and responses to be matched up by the application.
 * @param options See Z39.50 Specification.
 * @param preferredMessageSize See Z39.50 Specification.
 * @param maximumRecordSize See Z39.50 Specification.
 * @param id use null for anonymous access
 * @param password use null for anonymous access
 * @param newpassword See Z39.50 Specification.
 * @param userInformationField See Z39.50 Specification.
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, String options,
			 int preferredMessageSize, int maximumRecordSize,
			 String id, String password, String newpassword,
			 DataDir userInformationField,
                         boolean reconnect)  {

	 return (Request(referenceId, options,
			 preferredMessageSize, maximumRecordSize,
			 id, password, newpassword,
			 userInformationField, reconnect, 0, 0));

       }

/**
 * Creates a Z39.50 InitRequest.
 * @param referenceId Will be returned on Response and allows Requests
 * and responses to be matched up by the application.
 * @param options See Z39.50 Specification.
 * @param preferredMessageSize See Z39.50 Specification.
 * @param maximumRecordSize See Z39.50 Specification.
 * @param id use null for anonymous access
 * @param password use null for anonymous access
 * @param newpassword See Z39.50 Specification.
 * @param userInformationField See Z39.50 Specification.
 * @param reconnect flag indicating whether to request reconnect information from the server
 * @param extraLen Allow this much extra room in the built BER record.
 * @param offset Build the Request at this offset in the BerString
 * @return BerString containing Request or null if space was unavailable
 */
	public BerString Request(int referenceId, String options,
			 int preferredMessageSize, int maximumRecordSize,
			 String id, String password, String newpassword,
			 DataDir userInformationField, 
                         boolean reconnect, 
			 int extraLen, int offset)
	{
            // search, present, delSet, trc, scan, exsvc, nameresults 
            // see http://lcweb.loc.gov/z3950/agency/options.html
            // for full list
            String options_supported ="yyynynyyynynnnynnny";


	    String protocol_version  = "yy";
	    String newstring = null;
	    DataDir subdir = null;

	    DataDir dir = new DataDir(Z39api.initRequest, (int)ASN1.CONTEXT);
	    if (referenceId != 0)
	        dir.add(Z39api.ReferenceId, ASN1.CONTEXT, referenceId);
	    dir.addBits(Z39initApi.ProtocolVersion, ASN1.CONTEXT, protocol_version);
	    if (options != null && !options.equals(""))
		dir.addBits(Z39initApi.Options, ASN1.CONTEXT, options);
	    else
		dir.addBits(Z39initApi.Options, ASN1.CONTEXT, options_supported);
	    dir.add(Z39initApi.PreferredMessageSize, ASN1.CONTEXT, 
		preferredMessageSize);
	    dir.add(Z39initApi.MaximumRecordSize, ASN1.CONTEXT, 
		maximumRecordSize);
	    if (id != null && !id.equals(""))
	    {
		if (password != null && !password.equals("") &&
		    newpassword != null && !newpassword.equals(""))
		    newstring = new String(id + "/" + password + 
			"/" + newpassword);
		else if (password != null && !password.equals(""))
	     	    newstring = new String(id + "/" + password);
		else if (newpassword != null && !newpassword.equals(""))
		    newstring = new String(id + "/" + newpassword);
		else
		    newstring = id;
		subdir = dir.add(Z39initApi.idAuthentication,(int)ASN1.CONTEXT);
		subdir.add(ASN1.VISIBLESTRING, ASN1.UNIVERSAL, newstring);
	    }
	    dir.add(Z39initApi.ImplementationId, ASN1.CONTEXT, "1995");
	    dir.add(Z39initApi.ImplementationName, ASN1.CONTEXT, 
  		"OCLC IRP API");
	    dir.add(Z39initApi.ImplementationVersion, ASN1.CONTEXT, "3.0");


	    if (userInformationField != null)
		dir.add(userInformationField);

            if (reconnect) {

              OtherInformation.addOIDandData(dir,
                    oclcUserInformation2.OID,
                    oclcUserInformation2.buildDir(null, 0, zsession.sessionId));

              /* subdir = dir.add(Z39api.UserInformationField, ASN1.EXTERNAL);
               subdir = subdir.add(ASN1.EXTERNAL,
                    ASN1.UNIVERSAL);
               subdir.daddoid(ASN1.OBJECTIDENTIFIER,
               ASN1.UNIVERSAL, oclcUserInformation2.OID);*/
            } 

            if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
		zsession.logger.println("INIT REQUEST: " + dir.toString());

            requestLength = dir.recLen() + extraLen;

	    if (extraLen != 0 || offset != 0)
		return new BerString(dir, extraLen, offset);
	    else
	        return new BerString(dir);
	}

/**
 * Processes a Z39.50 InitResponse.
 * @param response BerString containing response.
 */
	public void Response(BerString response) {
	    Response(new DataDir(response));
	}

/**
 * Processes a Z39.50 InitResponse.
 * @param response DataDir containing response
 */
	public void Response(DataDir response) {
	    DataDir pchild = null, ppchild = null;
	    String oid;
	    int i;

            if (zsession.logger != null && 
		zsession.logger.getLevel() == Z39logging.HIGH)
	         zsession.logger.println(
		 "INIT Response: " + response.toString());


            zsession.fInitDone = true;
            responseLength = response.recLen();

	    for (pchild = response.child(); pchild != null; 
	  	 pchild=pchild.next())
	    {
//System.out.println(pchild.toString());
		switch (pchild.fldid())
		{
		    case Z39api.ReferenceId:
			referenceId = pchild.getInt();
			break;
		    case Z39initApi.Options:	
			options = pchild.getBits();
			// Set the TRC to FALSE
			if (options != null && options.length() > 5 &&
			    options.charAt(4) == 'n') {
			    zsession.doTRC=false;
			}
			break;
		    case Z39initApi.PreferredMessageSize:
			preferredMessageSize = pchild.getInt();
			break;
		    case Z39initApi.MaximumRecordSize:
			maximumRecordSize = pchild.getInt();
			break;
		    case Z39initApi.result:
			result = (short)pchild.getInt();
			break;
		    case Z39api.otherInformation:
			if (zsession == null)  // cannot save data w/o it
			    break;

			DataDir data = OtherInformation.getData(pchild, 
			    oclcUserInformation2.OID);
			if (data != null) {
  			  oclcUserInformation2 o2 = new oclcUserInformation2(data);
			  zsession.host = o2.host;
			  zsession.port = o2.port;
			  zsession.sessionId = o2.sessId;
                          txnId = o2.txnId;
//System.out.println("INIT response sessionId is " + zsession.sessId);
  			  o2 = null;
                        }
                        ppchild = pchild.child();
                        if (ppchild.fldid() == ASN1.SEQUENCE && ppchild.child() != null &&
                            ppchild.child().fldid() == 2) {
                           if (result == 0)
                             failureMsg = ppchild.child().getString();
                           else
                            MessageOfTheDay = ppchild.child().getString();
                        }
    			break;

		    case Z39api.UserInformationField:
			userInformationField userInfo = 
			    new userInformationField(pchild); 
			if(userInfo.OID()==External.oclcUserInformation1OID) { 
			    doOclcUserInformation1(userInfo.external()); 
			} 
			else 
			    if(userInfo.OID()==External.OtherInformationOID) { 
				ExternalOtherInformation otherInfo = new 
				    ExternalOtherInformation(userInfo.child()); 
				Enumeration enum=otherInfo.elements(); 
				External extern; 
				while(enum.hasMoreElements()) { 
				    extern=(External)enum.nextElement(); 
				    if(extern.OID().equals( 
					   External.oclcUserInformation1OID)) { 
					doOclcUserInformation1(extern); 
				    } 
				    else 
					if(extern.OID().equals(External. 
				       CharacterSetAndLanguageNegotiationOID)) {
					    doCharNegotiation(extern); 
					} 
				} 
			    } 
			break; 

		}
	    }
	}

    private void doCharNegotiation(External external) { 
        CharactersetNegotiation cn=new CharactersetNegotiation(external); 
        if(cn.collection!=null && cn.level!=null) 
            if(cn.collection.equals(CharactersetNegotiation.iso10646OID) && 
	       cn.level.equals(CharactersetNegotiation.utf8OID)) 
                zsession.utf8Encode=true; 
    } 
 
 
    private void doOclcUserInformation1(External external) { 
        oclcUserInformation1 o1 = 
            new oclcUserInformation1(external.child().next()); 
        failureCode = (short)o1.failureCode; 
        MessageOfTheDay = o1.MessageOfTheDay; 
        failureMsg = o1.failureMessage; 
        if (result == 0 && failureMsg == null && MessageOfTheDay != null) 
            failureMsg = MessageOfTheDay; 
        DBList = o1.DbList; 
        DisplayDBList = o1.DisplayDbList; 
        o1 = null; 
    } 
 

        public String getFailureMsg() {

            if (failureMsg != null)
              return failureMsg;

            switch (this.failureCode) {
              
               case Z39initApi.InvalidAutho:
                 return "Invalid Authorization Number to the Database Server";

               case Z39initApi.BadAuthoPassword:
                 return "Invalid Password to the Database Server";
       
               case Z39initApi.NoSearchesRemaining:
                 return "There are No Searches Remaining for this Authorization Number";

               case Z39initApi.IncorrectInterfaceType:
                 return "The authorization number entered is not valid for " +
                        "this service.  Please check your authorization " +
                        "number";

               case Z39initApi.MaxNumberSimultaneousUsers: 
                 return "The maximum number of simultaneous Z39.50 users allowed for " +
                        "this authorization number are logged on.  Please try " +
                        "again later";

               case Z39initApi.BlockedIPAddress:
                 return "IP address to access the Database Server is blocked";
           }
          return ("");
        }

	public String toString() {
            String newLine = System.getProperty("line.separator");
	    StringBuffer str = new StringBuffer(
			"Z39init: referenceId(" + referenceId +
			")" + newLine + "options(" + options +
			")" + newLine + "txnId(" + txnId +
			")" + newLine + "preferredMessageSize(" + preferredMessageSize +
			")" + newLine + "maximumRecordSize(" + maximumRecordSize +
			")" + newLine + "requestLength(" + requestLength +
			")" + newLine + "responseLength(" + responseLength +
			")" + newLine + "result(" + result +
			")" + newLine + "MessageOfTheDay(" + MessageOfTheDay +
			")" + newLine + "DBList(");
	    for (int i=0; DBList != null && i<DBList.length; i++)
                str.append(DBList[i]);
	    str.append(")" + newLine + "failureCode(" + failureCode + ")\n");
            str.append("failureMsg(" + getFailureMsg() + ")"); 
            

	    return str.toString();
	}
}
