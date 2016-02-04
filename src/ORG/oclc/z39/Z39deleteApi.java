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
 * @version %W% %G%
 * @author Jenny Colvard
 */

public class Z39deleteApi {

        public static final int OPERATIONSTATUS       = 0;
        public static final int LISTSTATUSES          = 1;
	public static final int DELETEFUNCTION	      = 32;
        public static final int SETSTATUS             = 33;
        public static final int NUMNOTDELETED         = 34;
        public static final int BULKSTATUSES          = 35;
        public static final int MESSAGE               = 36;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int success                                 = 0;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int resultSetDidNotExist                    = 1;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int previouslyDeletedByTarget               = 2;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int systemProblemAtTarget                   = 3;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int accessNotAllowed                        = 4;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int resourceControlAtOrigin                 = 5;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int resourceControlAtTarget                 = 6;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int bulkDeleteNotSupported                  = 7;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int notAllRsltSetsDeletedOnBulkDlte         = 8;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int notAllRequestedResultsSetsDeleted       = 9;
/**
 * deleteResultSet status; see Z39.50 specification.
 */
        public static final int resultSetInUse                          = 10;

/**
 * deleteFunction - list
 */
	public static final int list = 0;
/**
 * deleteFunction - all
 */
	public static final int all  = 1;

	public Z39deleteApi() {
	}
}

