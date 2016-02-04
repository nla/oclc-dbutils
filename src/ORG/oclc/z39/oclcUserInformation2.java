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
 * oclcUserInformation2 is used to request and report session and host
 * information. If a client sends a oclcUserInformation2 field on an Init
 * request, the server will response with a filled in oclcUserInformation2
 * field containing a session id, a host name and a port number so that
 * the Z39 session can be conducted in a connection-less mode. The user should
 * then send the oclcUserInformation2 on all subsequent requests (in the
 * OtherInformation field). If the connection is broken, the user should
 * re-connect to the specified port & host (which is not necessarily the 
 * original port & host). A new init is not done as part of the re-connect.
 * The client simply re-establishes the socket connection and sends the
 * next request with the oclcUserInformation2.
 * @version @(#)oclcUserInformation2.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class oclcUserInformation2 {
    
/**
 *  Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.2";
    private static final int Host                    = 124;
    private static final int Port                    = 125;
    private static final int SessionId               = 126;

    public int port;
    public String sessId;
    public String txnId;
    public String host;
    public DataDir sessionIdDir;

    public oclcUserInformation2(DataDir userInformationField) {
	DataDir tmp;
	
	if (userInformationField == null ||
	    userInformationField.fldid() != ASN1.single_ASN1_type)
	    return;
	tmp = userInformationField.child();
	if (tmp == null || tmp.fldid() != ASN1.SEQUENCE)
	    return;
	for (tmp = tmp.child(); tmp != null; tmp = tmp.next())
	{
	    switch (tmp.fldid())
	    {
	  	case Host:
		    host = tmp.getString();
		    break;

		case Port:
		    port = tmp.getInt();
		    break;

		case SessionId:
		    sessId = tmp.getString();
                    int pos;
                    if ((pos = sessId.indexOf('/')) != -1) {
                       txnId = sessId.substring(pos+1);
                       sessId = sessId.substring(0, pos);
                    }
                    sessionIdDir = tmp; 
		    break;

	    }
	}
    } 

/**
 * @return DataDir for an oclcUserInformation2
 */
    public static DataDir buildDir(String host, int port, String sessionId) {

        return buildDir(host, port, sessionId, Thread.currentThread().getName());	

    }

/**
 * @return DataDir for an oclcUserInformation2
 */
    public static DataDir buildDir(String host, int port, String sessionId, String txnId) {
	
	DataDir top = new DataDir(ASN1.SEQUENCE, (int)ASN1.UNIVERSAL);
	if (host != null)
	{
	    top.add(Host, ASN1.CONTEXT, host);
	    top.add(Port, ASN1.CONTEXT, port);
	}
	if (sessionId != null)
	    top.add(SessionId, ASN1.CONTEXT, sessionId + "/" + txnId);

	return top;
    }

}

