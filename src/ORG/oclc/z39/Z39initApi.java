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
import java.util.*;
import java.io.*;
/**
 * @see zclient
 * @see example
 * @see zapplet
 * @see Z39init
 * @see Z39response
 * @version @(#)Z39initApi.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39initApi {

/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int groupId			= 0;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int userId			= 1;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int password		= 2;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int newPassword		= 3;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int ProtocolVersion		= 3;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int Options			= 4;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int PreferredMessageSize	= 5;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int MaximumRecordSize	= 6;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int idAuthentication	= 7;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int result			= 12;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int ImplementationId	= 110;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int ImplementationName	= 111;
/** 
 * An Init parameter; see the Z39.50 specification.
 */
	public static final int ImplementationVersion	= 112;
/** 
 * An Init parameter; see the Z39.50 specification. OCLC only tag for user
 * information field.
 */
	public static final int PreAuthorized		= 120;
/** 
 * An Init parameter; see the Z39.50 specification. OCLC only tag for user
 * information field.
 */
	public static final int MasterAuthorization	= 121;
/** 
 * An Init parameter; see the Z39.50 specification. OCLC only tag for user
 * information field.
 */
	public static final int BillingGroup		= 122;
/** 
 * An Init parameter; see the Z39.50 specification. OCLC only tag for user
 * information field.
 */
	public static final int UniversalID		= 123;

	// define return codes for authorizations
/**
 * Return code for authorization on Init.
 */
	public static final int InvalidAutho			= 1;
/**
 * Return code for authorization on Init.
 */
	public static final int BadAuthoPassword		= 2;
/**
 * Return code for authorization on Init.
 */
	public static final int NoSearchesRemaining		= 3;
/**
 * Return code for authorization on Init.
 */
	public static final int IncorrectInterfaceType		= 4;
/**
 * Return code for authorization on Init.
 */
	public static final int MaxNumberSimultaneousUsers	= 5;
/**
 * Return code for authorization on Init.
 */
	public static final int BlockedIPAddress		= 6;

/**
 * Return code for authorization on Init.
 */
        public static final int MaxSimultaneousConsortia   	= 7;

/**
 * Error message for InvalidAutho on Init.
 */
	public static final String InvalidAuthoMsg = "Invalid Autho";
	
/**
 * Error message for BadAuthoPassword on Init.
 */
        public static final String BadAuthoPasswordMsg = "Invalid Autho/password";

/**
 * Error message for MaxNumberSimultaneousUsers on Init.
 */
        public static final String MaxNumberSimultaneousUsersMsg =
		"Max Number of Simultaneous Users reached";

/**
 * Error message for MaxSimultaneousConsortia on Init.
 */
	public static final String MaxSimultaneousConsortiaMsg =
		"Max Number of Simultaneous Consortia reached";


	public Z39initApi() {
	}
}

