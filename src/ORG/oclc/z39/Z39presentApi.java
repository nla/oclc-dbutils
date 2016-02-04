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
 * @see Z39present
 * @see Z39response
 * @version @(#)Z39presentApi.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39presentApi {

/**
 * OPAC record syntax; see the Z39.50 specification.
 */
        public static final String OPAC_SYNTAX =
                "1.2.840.10003.5.102";
/**
 * OCLC_BER record syntax; see the Z39.50 specification.
 */
        public static final String OCLC_BER_SYNTAX =
                "1.2.840.10003.5.1000.17.1";
/**
 * OCLC_CLUSTER_SYNTAX - OID identifying a duplicate records cluster
 * in the Z39.50 response.
 */
        public static final String OCLC_CLUSTER_SYNTAX =
                "1.2.840.10003.5.1000.17.2";
/**
 * MARC record syntax; see the Z39.50 specification.
 */
        public static final String MARC_SYNTAX =
                "1.2.840.10003.5.10";
/**
 * SUTRS record syntax; see the Z39.50 specification.
 */
        public static final String SIMPLETEXT_SYNTAX =
                "1.2.840.10003.5.101";
/**
 * EXPLAIN record syntax; see the Z39.50 specification.
 */

        public static final String EXPLAIN_SYNTAX =
                "1.2.840.10003.5.100";

/**
 * NOTIS_OPAC record syntax; see the Z39.50 specification.
 */
        public static final String NOTIS_OPAC_SYNTAX =
                "1.2.840.10003.5.1000.11.2";

/**
 * GRS1 record syntax; see the Z39.50 specification.
 */
        public static final String GRS1_SYNTAX =
                "1.2.840.10003.5.105";

/**
 * UNIMARC record syntax; see the Z39.50 specification.
 */
        public static final String UNIMARC_SYNTAX = 
                 "1.2.840.10003.5.1";

/**
 * UKMARC record syntax; see the Z39.50 specification.
 */
        public static final String  UKMARC_SYNTAX = 
                 "1.2.840.10003.5.11";

/** 
  * NORMARC record syntax; see the Z39.50 specification. 
  */ 
     public static final String  NORMARC_SYNTAX =  
	    "1.2.840.10003.5.12"; 
 

/**
 * A record syntax; see the Z39.50 specification. NOTIS is
 * using this syntax to deliver data incorrectly, so this is
 * a way to interpret that data.
 */
        public static final String IBERMARC_SYNTAX =
                  "1.2.840.10003.5.21";

/**
 * DRAHOLDINGS proprietary record syntax.
 */
        public static final String  DRAHOLDINGS_SYNTAX =
                 "1.2.840.10003.5.1000.10.1";

/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int name			=   0;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int databaseRecord		=   1;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int surrogateDiagnostic	=   2;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int ElementSetNames 	=  19;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int complexRecordComposition =  209;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int NumberOfRecordsReturned	=  24;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int NextResultSetPosition	=  25;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int PresentStatus		=  27;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int dataBaseOrSurDiagnostics =  28;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int numberOfRecordsRequested =  29;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int additionalRanges =  212;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int resultSetStartPoint	=  30;
/**
 * Search/Present parameter; see Z39.50 specification.
 */
	public static final int PreferredRecordSyntax 	= 104;

/**
 * Representative record in a duplicates cluster.
 */
	public static final int representativeRecord 	= 1;

/**
 * Duplicate record in a duplicates cluster.
 */
	public static final int duplicateRecord 	= 2;

/**
 * Duplicate count in a duplicates cluster.
 */
	public static final int duplicateCount 	        = 3;

/**
 * Present status; see Z39.50 specification.
 */
	public static final int success   = 0;
/**
 * Present status; see Z39.50 specification.
 */
	public static final int partial_1 = 1;
/**
 * Present status; see Z39.50 specification.
 */
	public static final int partial_2 = 2;
/**
 * Present status; see Z39.50 specification.
 */
	public static final int partial_3 = 3;
/**
 * Present status; see Z39.50 specification.
 */
	public static final int partial_4 = 4;
/**
 * Present status; see Z39.50 specification.
 */
	public static final int failure   = 5;

/**
 * Element Set Names; see Z39.50 specification.
 */
        public static final int genericElementSetName 	= 0;


	public Z39presentApi() {
	}

}


