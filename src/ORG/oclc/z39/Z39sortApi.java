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
 * @version @(#)Z39api.java	1.24 12/03/96
 */

public class Z39sortApi {


/**
 * input result set name; see Z39.50 specification.
 */
   public static final int inputResultSetName =        3;

/**
 * output result set name; see Z39.50 specification.
 */
   public static final int outputResultSetName =       4;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int sequence =                  5;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int element =                   0;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int genericSortKey =            1;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int relation =                  1;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int caseSensitivity =           2;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int field =                     0;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int attributes =                2;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int AttributeList           =  44;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int status =                    3;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int resultSetStatus =           4;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int success = 0;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int failure = 2;

/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int partial = 1;


/**
 * sort parameter; see Z39.50 specification.
 */
   public static final int empty = 1;
   public static final int interim = 2;
   public static final int unchanged = 3;
   public static final int none = 4;

   public Z39sortApi() {
  }	

}

