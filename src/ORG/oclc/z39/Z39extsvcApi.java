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
 * @version @(#)Z39initApi.java  1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39extsvcApi {

/**
 * An oclc defined Extended Services Object Id; see the Z39.50 specification.
 */
  public static final String oclcServicePrice=       "1.2.840.10003.9.1000.17.1";

/**
 * An oclc defined Extended Services Object Id; see the Z39.50 specification.
 */
  public static final String oclcOrderSupplierInfo = "1.2.840.10003.9.1000.17.2";

/**
 * An oclc defined Extended Services Object Id; see the Z39.50 specification.
 */
  public static final String oclcRequestRecord = "1.2.840.10003.9.1000.17.3";
    
/**
 * An oclc defined Extended Services Object Id; see the Z39.50 specification.
 */
  public static final String oclcPatronRecord = "1.2.840.10003.9.1000.17.4";
    
/**
 * An extended services object Id; see the Z39.50 specification.
 */
  public static final String persistantResultSet =    "1.2.840.10003.9.1";
/**
 * An extended services object Id; see the Z39.50 specification.
 */

  public static final String persistantQuery =        "1.2.840.10003.9.2";
/**
 * An extended services object Id; see the Z39.50 specification.
 */

  public static final String periodicQuerySchedule =  "1.2.840.10003.9.3";
/**
 * An extended services object Id; see the Z39.50 specification.
 */

  public static final String itemOrder =              "1.2.840.10003.9.4";
/**
 * An extended services object Id; see the Z39.50 specification.
 */

  public static final String databaseUpdate =         "1.2.840.10003.9.5";
/**
 * An extended services object Id; see the Z39.50 specification.
 */

  public static final String exportSpecification =    "1.2.840.10003.9.6";
/**
 * An extended services object Id; see the Z39.50 specification.
 */

  public static final String exportInvocation =       "1.2.840.10003.9.7";

/**
 * An extended services object Id; see the Z39.50 specification.
 */
  public static final String databaseUpdateLock =     "1.2.840.10008.13.6";



/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int function                     =3;
/**
 * An extended services parameter; see the Z39.50 specification.
 */

   public static final int type                         =4;
/**
 * An extended services parameter; see the Z39.50 specification.
 */

   public static final int status                       =3; 
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int diagnostics                  =4; 
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int taskSpecificParameters       =10; 
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int waitAction                   =11; 
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int taskPackage                  =5;
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int packageName                  =5;

/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int userid                       =6;
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int retentionTime                =7;
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int permissions                 =8;
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int description                  =9;
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int toKeep                       =1;
/**
 * An extended services parameter; see the Z39.50 specification.
 */
   public static final int notToKeep                    =2;



  public Z39extsvcApi() {
  }

}

