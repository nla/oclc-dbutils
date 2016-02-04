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
 * <p> The Z39 Client API Software presented here is a translation
 * of the C version of the API written by Ralph Levan at OCLC.
 * The documentation presented
 * here is pretty much taken from his documentation.</p>
 * <p>A description of our fourth version of the Z39.50 Client API
 * is below. It will, of course, undergo some refinement with
 * use and get extended to support new Z39.50 functionality as
 * required.  The changes from the fourth version include
 * support for type-101 queries (including proximity operators
 * in the Client APIs query grammer) and the removal of some
 * unneeded parameters in the request calls.  Scan was brought
 * into compliance with the final version of the 95 standard.
 * In addition, a number of changes were made to simplify
 * things and to bring the Client API  into compliance with the
 * article that was written about it.  (See the section on
 * availability at the end of this document.)</p>
 * <p>The change from the third version is the addition of support
 * for piggybacked Presents with the SearchRequest.  This means
 * that there is now an optional PRESENT_RESPONSE object in
 * the SEARCH_RESPONSE object.</p>
 * <p>The changes from the second version are the addition of
 * support for a simple userid and password on the InitRequest;
 * support for full boolean queries in the SearchRequest;
 * support for both MARC and SUTRS record syntaxes in the
 * PresentRequest.   A class to support Scan has been
 * added.  It is in an early stage of development and will
 * definitely be enhanced with experience.  Finally, a new
 * method, logging(), provides support for run-time
 * diagnostic logging.<HR>High-level description<HR>
 * The classes process IRP messages, they do not send or
 * receive these messages; that is up to the calling routines.
 * (IRP stands for Information Retrieval Protocol, which is the
 * name of the protocol described by Z39.50.)  An IRP Request
 * method is called and it produces a BER (Basic Encoding
 * Rules, the chosen transfer syntax for ASN.1) record that can
 * be sent to a Z39.50 target.  Conversely, a BER record
 * containing an IRP response is processed and instance variables in
 * an object are filled in with values from the response.
 * The BER records are encoded and decoded using
 * OCLC's BER utilities.  These utilities are available via
 * anonymous ftp to ftp.rsch.oclc.org and are in the
 * pub/BER_utilities directory. They are available in either a C version
 * (as source) or a Java version (as class files).</p>
 * <p>There are two layers to the IRP response methods.  The
 * layer you choose to use will depend on coding style and
 * application complexity.  The lower layer, consisting of
 * Response() methods in the Z39init, Z39present, Z39search
 * and Z39scan classes, are invoked directly with the Z39.50
 * target's 
 * response to an Init, Search, Present or Scan request.  These
 * methods fill in instance variables with the various parameters in
 * the response.  They are appropriate when the application
 * (the Z39.50 origin) is relatively simple and "knows" what
 * kind of response it is receiving.  This is usually a single
 * user application.</p>
 * <p>In more complex applications, it is difficult to know what
 * kind of response has arrived.  The next layer addresses this
 * problem.  The class Z39response will determine the type of
 * response and invoke the appropriate class to process the response.
 * The type is stored in an instance variable in the Z39response object.
 * An object of the appropriate type is also availabe in the Z39response
 * object.</p>
 * <p>In the descriptions of the methods, I will not discuss the
 * meaning of the various parameters to the request methods.
 * They are fully described in the Z39.50 standard.</p>
 * <p>In all the request methods, a long referenceId is provided.
 * The value in the referenceId is in an instance variable filled in by
 * the response routines.  In complex applications, we have
 * found that it is a useful place to put a reference to an object
 * containing information about the request that has just been
 * processed.  Doing this has allowed us to build relatively
 * stateless applications.</p>
 * <p>The BER record that is created by the request methods is
 * put into a BerString object. The BER record can totally fill
 * the buffer in the BerString object (the default action) or it
 * can leave space for a header and trailer. The caller can then
 * move communications headers and/or trailers into the extra space.</p>
 * <hr>Availability<hr>
 * <p>An article about building Z39.50 clients, which includes a
 * detailed description of the Client API, was written for the
 * Z39.50 Implementors Group.  It is available via anonymous
 * ftp at the Z39.50 Maintenance Agencys ftp site at
 * ftp.loc.gov.  You're on your own for finding it there.</p>
 * <p>The article will also be published by the National Institute
 * for Standards and Technology (NIST) as part of a Z39.50
 * monograph.  Good luck finding that too.</p>
 * <p>Finally, the article is available via anonymous ftp at
 * ftp.rsch.oclc.org in the pub/SiteSearch/z39.50_client_api
 * directory.  It is available in several formats, the names of
 * which all begin with zclient.</p>
 * <p>The OCLC ftp site also provides the .class files for the
 * Z39.50 Client API and a simple client application (zclient.java),
 * which are in the pub/SiteSearch/java directory, as well as the BER
 * utilities which are in the pub/BER_utilities/java directory.</p>
 *<hr>Examples<hr><p>
 * Java source for 2 examples is provided. The example.java is a Java
 * application that does an Init, Scan, Search and Present. The zapplet.java
 * is a Java applet that does an Init, Scan, Search and Present.
 * The html file for zapplet.java is zapplet.html.
 * </p>
 * @see zclient
 * @see example
 * @see zapplet
 * @see Z39init
 * @see Z39present
 * @see Z39search
 * @see Z39scan
 * @see Z39response
 * @version @(#)Z39api.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39api {

/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int initRequest		    = 20;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int initResponse		    = 21;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int searchRequest		    = 22;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int searchResponse		    = 23;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int presentRequest		    = 24;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int presentResponse             = 25;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int deleteResultSetRequest      = 26;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int deleteResultSetResponse     = 27;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int accessControlRequest        = 28;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int accessControlResponse       = 29;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int resourceControlRequest      = 30;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int resourceControlResponse     = 31;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int triggerResourceControlRequest = 32;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int resourceReportRequest	    = 33;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int resourceReportResponse	    = 34;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int scanRequest 		    = 35;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int scanResponse		    = 36;
/**
 * A PDU name; see the Z39.50 specification.
 */
        public static final int sortRequest                 = 43;
/**
 * A PDU name; see the Z39.50 specification.
 */
        public static final int sortResponse                = 44;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int extendedservicesRequest     = 46;
/** 
 * A PDU name; see the Z39.50 specification.
 */
	public static final int extendedservicesResponse    = 47;
/**
 * A PDU name; see the Z39.50 specification.
 */
        public static final int Close			    = 48;
/**
 * A PDU name; see the Z39.50 specification.
 */
        public static final int dedupRequest                = 49;
/**
  * A PDU name; see the Z39.50 specification.
  */
        public static final int dedupResponse               = 50;

/** 
 * A Global parameter; see the Z39.50 specification.
 */
	public static final int ReferenceId		= 2;
/**
 * A Global parameter; see the Z39.50 specification.
 */
	public static final int ResultSetId		=  31;

/**
 * A Global parameter; see the Z39.50 specification.
 */
	public static final int DatabaseName       	= 105;

/**
 * A Global parameter; see the Z39.50 specification.
 */
	public static final int ElementSetName       	= 103;


/**
 * Global parameter; see the Z39.50 specification
 */
	public static final int otherInformation   	= 201;


/**
 * UserInformation field; see the Z39.50 specification.
 */
        public static final int UserInformationField    = 11;

/**
 * Exported definition
 */
	public static final int AttributeList           = 44;

/**
 * general Term definition
 */
	public static final int generalTerm             = 45;
  /**
   * numeric Term definition
   */
   public static final int numericTerm = 215;

  /**
   * characterString Term definition
   */
   public static final int characterStringTerm = 216;

  /**
   * OID Term definition
   */
   public static final int oidTerm = 217;

  /**
   * Date-Time Term definition
   */
   public static final int dateTimeTerm = 218;

  /**
   * external Term definition
   */
   public static final int externalTerm = 219;

  /**
   * IntegerAndUnit Term definition
   */
   public static final int integerAndUnitTerm = 220;

  /**
   * Null Term definition
   */
   public static final int nullTerm = 221;
  
/**
 * Exported definition
 */
	public static final int AttributesPlusTerm      = 102;

/**
 * DIAG.2 Diagnostic Format Diag-1; see Z39.50 specification.
 */
	public static final int nonSurrogateDiagnostic      = 130;
/**
 * DIAG.2 Diagnostic Format Diag-1; see Z39.50 specification.
 */
	public static final int MultipleNonSurrogates       = 205;
/**
 * DIAG.2 Diagnostic Format Diag-1; see Z39.50 specification.
 */
	public static final int DiagnosticFormatcondition   =   1;
/**
 * DIAG.2 Diagnostic Format Diag-1; see Z39.50 specification.
 */
	public static final int DiagnosticFormatunspecified =   1;
/**
 * DIAG.2 Diagnostic Format Diag-1; see Z39.50 specification.
 */
	public static final int DiagnosticFormatspecified   =   2;
/**
 * DIAG.2 Diagnostic Format Diag-1; see Z39.50 specification.
 */
	public static final int DiagnosticFormataddMsg      =   1;

	private static final int esTaskPackage = 5;
}
