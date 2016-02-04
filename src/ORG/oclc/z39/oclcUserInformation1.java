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
 * This is used in the userInformationField of an Init.
 * @version @(#)oclcUserInformation1.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class oclcUserInformation1 {

/**
 * Object identifier for this field
 */
    public static final String OID = "1.2.840.10003.10.1000.17.1";

    private static final int messageOfTheDay         = 1;
    private static final int dbList                  = 2;
    private static final int InitFailureReason       = 3;
    private static final int InitFailureCode         = 1;
    private static final int InitFailureMsg          = 2;
    
/**
 * Daily news, in human readable text
 */
    public String MessageOfTheDay;
/**
 * Failure Message in human readable text.
 */
    public String failureMessage;
/**
 * Condition code, if the init failed
 */
    public int failureCode;
/**
 * List of databases available from this site
 */
    public String DbList[];
/**
 * List of long database names
 */
    public String DisplayDbList[];

    public oclcUserInformation1(DataDir userInformationField) {
	DataDir tmp, tmp2;
	int i;
	
	MessageOfTheDay = null;
	failureCode = 0;
        failureMessage = null;
	DbList = null;
	if (userInformationField == null ||
	    userInformationField.fldid() != ASN1.single_ASN1_type) {
	    return;
        }
	tmp = userInformationField.child();
	if (tmp == null || tmp.fldid() != ASN1.SEQUENCE) {
	    return;
        }
	for (tmp = tmp.child(); tmp != null; tmp = tmp.next())
	{
	    switch (tmp.fldid())
	    {
	  	case messageOfTheDay:
		    MessageOfTheDay = tmp.getString();
		    break;

		case InitFailureReason:
		    if (tmp.form() == ASN1.PRIMITIVE)
                        failureCode = tmp.getInt();
		    else {
                    for (tmp2 = tmp.child(); tmp2 != null; tmp2 = tmp2.next()) {
			if (tmp2.fldid() == InitFailureCode) {
			    failureCode = tmp2.getInt();
                        }
                        else if (tmp2.fldid() == InitFailureMsg) {
                           failureMessage = tmp2.getString();
                        }
                    }
		    }
		    break;

                case InitFailureMsg:
	            failureMessage = tmp.getString(); 
		    break;

		case ASN1.SEQUENCE:
		    DbList = new String[tmp.count()];
		    for (i=0,tmp2=tmp.child(); tmp2!=null && i<DbList.length;
			i++, tmp2 = tmp2.next()) 
			DbList[i] = tmp2.getString();
		    break;

		case 4:
		    tmp2 = tmp.child();
		    if (tmp2 == null || tmp2.fldid() != ASN1.SEQUENCE)
			break;
		    DisplayDbList = new String[tmp2.count()];
		    for (i=0,tmp2=tmp2.child(); 
			tmp2!=null && i<DisplayDbList.length;
                        i++, tmp2 = tmp2.next())
                        DisplayDbList[i] = tmp2.getString();
		    break;
	    }
	}
    } 

/**
 * @return DataDir for an oclcUserInformation1 
 */
    public static DataDir buildDir(String MessageOfTheDay, int failureCode, 
	String DbList[], String DisplayDbList[]) {
	
	DataDir top = new DataDir(ASN1.SEQUENCE, (int)ASN1.UNIVERSAL);
	DataDir tmp = top, tmp2;
	
	if (MessageOfTheDay != null)
	    tmp.add(messageOfTheDay, ASN1.CONTEXT, MessageOfTheDay);
	if (failureCode != 0){
	    tmp.add(InitFailureReason, ASN1.CONTEXT, failureCode);
	    String errmsg = getErrMsg(failureCode);
	    tmp.add(InitFailureMsg, ASN1.CONTEXT, errmsg);
	}
	if (DbList != null)
	{
	    tmp2 = tmp.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
	    for (int i=0; i<DbList.length; i++)
	        tmp2.add(dbList, ASN1.CONTEXT, DbList[i]);
	}
	if (DisplayDbList != null)
	{
	    tmp2 = tmp.add(4, ASN1.CONTEXT);
	    tmp2 = tmp2.add(ASN1.SEQUENCE, ASN1.UNIVERSAL);
	    for (int i=0; i<DisplayDbList.length; i++)
                tmp2.add(dbList, ASN1.CONTEXT, DisplayDbList[i]);
	}
	return top;
    }

/**
 * @return error message according to the error code
 */
   public static String getErrMsg(int errcode){
      if (errcode == Z39initApi.InvalidAutho)
	return Z39initApi.InvalidAuthoMsg;
      else if (errcode == Z39initApi.BadAuthoPassword)
	return Z39initApi.BadAuthoPasswordMsg;
      else if (errcode == Z39initApi.MaxNumberSimultaneousUsers)
	return Z39initApi.MaxNumberSimultaneousUsersMsg;
      else
	return new String("unknow errcode");
   }
}
