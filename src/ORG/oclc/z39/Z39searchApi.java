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
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class Z39searchApi {
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int smallSetUpperBound      =  13;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int largeSetLowerBound      =  14;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int mediumSetPresentNumber  =  15;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int replaceIndicator        =  16;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int resultSetName           =  17;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int databaseNames           =  18;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int query                   =  21;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int searchStatus            =  22;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int resultCount             =  23;
/**
 * Search paramter; see Z39.50 specification.
 */
        public static final int resultSetStatus         =  26;
/**
 * Search/Present paramter; see Z39.50 specification.
 */
        public static final int smallSetElementSetNames = 100;
/**
 * Search/Present paramter; see Z39.50 specification.
 */
        public static final int mediumSetElementSetNames = 101;
/**
 * Search/Present paramter; see Z39.50 specification.
 */
        public static final int additionalSearchInfo    = 203;
/**
 * Query type for Search request; see Z39.50 specification.
 */
	public static final int type_0      = 0;
/**
 * Query type for Search request; see Z39.50 specification.
 */
	public static final int type_1      = 1;
/**
 * Query type for Search request; see Z39.50 specification.
 */
	public static final int type_2      = 2;
/**
 * Query type for Search request; see Z39.50 specification.
 */
	public static final int type_100    = 100;
/**
 * Query type for Search request; see Z39.50 specification.
 */
	public static final int type_101    = 101;
/**
 * Query parameter; see Z39.50 specification.
 */
        public static final int Operator    =  46;     
/**
 * Query parameter for Search request; see Z39.50 specification.
 */
	public static final int Operand	= 0;
/**
 * Query parameter for Search request; see Z39.50 specification.
 */
	public static final int Query	= 1;
/**
 * Query parameter for Search request; see Z39.50 specification.
 */
	public static final int and	= 0;
/**
 * Query parameter for Search request; see Z39.50 specification.
 */
	public static final int or	= 1;
/**
 * Query parameter for Search request; see Z39.50 specification.
 */
	public static final int and_not	= 2;
/**
 * Query parameter for Search request; see Z39.50 specification.
 */
	public static final int prox	= 3;
/**
 * prox parameter; see Z39.50 specification.
 */
	public static final int exclusion	= 1;
/**
 * prox parameter; see Z39.50 specification.
 */
	public static final int distance	= 2;
/**
 * prox parameter; see Z39.50 specification.
 */
	public static final int ordered		= 3;
/**
 * prox parameter; see Z39.50 specification.
 */
	public static final int relationType	= 4;
/**
 * prox parameter; see Z39.50 specification.
 */
	public static final int proximityUnitCode = 5;

/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int lessThan	= 1;
/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int lessThanOrEqual	= 2;
/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int equal		= 3;
/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int greaterThanOrEqual 	= 4;
/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int greaterThan	= 5;
/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int notEqual	= 6;
/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int known		= 1;
/**
 * prox relationship parameter; see Z39.50 specification.
 */
	public static final int privateProx	= 2;

/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int character	=  1;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int word		=  2;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int sentence	=  3;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int paragraph	=  4;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int section		=  5;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int chapter		=  6;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int document	=  7;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int element		=  8;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int subelement	=  9;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int elementType	= 10;
/**
 * Proximity unit code; see Z39.50 specification.
 */
	public static final int byteProx	= 11; 

/**
 * Search status; see Z39.50 specification.
 */
	public static final int subset   = 1;
/**
 * Search status; see Z39.50 specification.
 */
	public static final int interim  = 2;
/**
 * Search status; see Z39.50 specification.
 */
	public static final int none     = 3;

	public Z39searchApi() {
	}

}

