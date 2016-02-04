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

/* @see zclient
 * @see example
 * @see zapplet
 * @see Z39init
 * @see Z39present
 * @see Z39search
 * @see Z39scan
 * @see Z39response
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class Z39closeApi {

/**
 * Close parameter; see Z39.50 specification.
 */
	public static final int closeReason	      = 211;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int finished	      = 0;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int shutdown	      = 1;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int systemProblem	      = 2;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int costLimit	      = 3;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int resources	      = 4;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int securityViolation     = 5;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int protocolError	      = 6;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int lackOfActivity	      = 7;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int peerAbort	      = 8;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int unspecified	      = 9;
/** 
 * Close reason; see  Z39.50 specification.
 */
	public static final int responseToClose	      = 10;

}

