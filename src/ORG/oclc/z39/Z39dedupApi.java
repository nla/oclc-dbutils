/*
(c)1998 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
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
 * @version @(#)Z39api.java	1.24 12/03/96
 */

public class Z39dedupApi {


/**
 * input result set name; see Z39.50 specification.
 */
   public static final int inputResultSetName =        3;

/**
 * output result set name; see Z39.50 specification.
 */
   public static final int outputResultSetName =       4;

/**
 * dedup parameter; see Z39.50 specification.
 */
   public static final int applicablePortionOfRecord = 5;

/**
 * dedup parameter; see Z39.50 specification.
 */
   public static final int duplicateDetectionCriteria = 6;

  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int levelOfMatch = 1;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int caseSensitive = 2;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int punctuationSensitive = 3;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int regularExpression = 4;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int rsDuplicates = 5;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int clustering = 7;
  
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int retentionCriteria = 8;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int numberOfEntries = 1;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int percentOfEntries = 2;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int duplicatesOnly = 3;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int discardRsDuplicates  = 4;

  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int sortCriteria = 9;

  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int mostComprehensive      = 1;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int leastComprehensive    = 2;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int mostRecent             = 3;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int oldest                 = 4;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int leastCost              = 5;
  /**
   * dedup parameter; see Z39.50 specification.
   */
   public static final int preferredDatabase      = 6;

/**
 * dedup parameter; see Z39.50 specification.
 */
   public static final int status =                    3;

/**
 * dedup parameter; see Z39.50 specification.
 */
   public static final int resultSetCount =           4;

/**
 * dedup parameter; see Z39.50 specification.
 */
   public static final int success = 0;

/**
 * dedup parameter; see Z39.50 specification.
 */
   public static final int failure = 1;


   public Z39dedupApi() {
  }	

}

