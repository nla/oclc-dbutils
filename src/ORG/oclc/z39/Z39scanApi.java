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
 * @see Z39search
 * @see Z39response
 * @version @(#)Z39scanApi.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39scanApi {

/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int databaseId			= 3;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int termListAndStartPoint		= 4;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int scanStatus			= 4;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int stepSize			= 5;
/** 
 * Scan parameter; see Z39.50 specification.
 */
        public static final int scanResponseStepSize            = 3;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int numberOfEntriesReturned		= 5;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int numberOfTermsRequested		= 6;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int positionOfTerm			= 6;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int preferredPositionInResponse	= 7;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int entries				= 7;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int Listentries			= 1;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int termInfo			= 1;
/** 
 * Scan parameter; see Z39.50 specification.
 */
	public static final int nonsurrogateDiagnostics		= 2;
/**
 * Scan status; see Z39.50 specification.
 */
	public static final int scan_status_success   = 0;
/**
 * Scan status; see Z39.50 specification.
 */
	public static final int scan_status_partial_1 = 1;
/**
 * Scan status; see Z39.50 specification.
 */
	public static final int scan_status_partial_2 = 2;
/**
 * Scan status; see Z39.50 specification.
 */
	public static final int scan_status_partial_3 = 3;
/**
 * Scan status; see Z39.50 specification.
 */
	public static final int scan_status_partial_4 = 4;
/**
 * Scan status; see Z39.50 specification.
 */
	public static final int scan_status_partial_5 = 5;
/**
 * Scan status; see Z39.50 specification.
 */
	public static final int scan_status_failure   = 6;

	public Z39scanApi() {
 	}
}
