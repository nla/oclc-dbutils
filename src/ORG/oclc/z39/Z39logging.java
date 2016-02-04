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
 * Z39logging provides a means to turn on logging for all Z39 transactions.
 * @version @(#)Z39logging.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39logging extends PrintWriter {

    public static final int OFF  = 0;
    public static final int LOW  = 1;
    public static final int HIGH = 2;

    public static int level = OFF;

    public Z39logging(String filename, int bufferSize) throws IOException {
	super(new BufferedOutputStream(
	    new FileOutputStream(filename), bufferSize));
    }

    public Z39logging(OutputStream out) throws IOException {
        super(out);
    }   

  /** Constructor that supports automatic flushing of the log when a
   *  newline is written.  This is important in servers when Z39logging
   *  may not be closed gracefully.
   *  @param filename Name of the log file.
   *  @param bufferSize Size of the buffer used by the OutputStream.
   *  @param autoFlush true indicates automatic flushing.
   *  @exception IOException
   */
    public Z39logging(String filename, int bufferSize, boolean autoFlush) 
      throws IOException {
      super(new BufferedOutputStream(
	    new FileOutputStream(filename), bufferSize), autoFlush);
    }

    public void close() {
      super.close();
    }

    public static int getLevel() {
	return level;
    }

    public static void setLevel(int newLevel) {
	level = newLevel;
    }

    public static Z39logging logger;
}

