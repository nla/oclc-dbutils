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

/**
 * Diagnostic1 includes the values defined in Z39-50-diagnostic 1.
 * @version @(#)Diagnostic1.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Diagnostic1 extends Throwable {

    public static final int permanentSystemError = 1;
    public static final int temporarySystemError = 2;
    public static final int unsupportedSearch = 3;
    public static final int termAllStopWords = 4;
    public static final int tooManyArgumentWords = 5;
    public static final int tooManyBooleanOperators = 6;
    public static final int tooManyTruncatedWords = 7;
    public static final int tooManyIncompleteSubfields = 8;
    public static final int truncatedWordsTooShort = 9;
    public static final int invalidFormatForRecordNumberSearchTerm = 10;
    public static final int tooManyCharactersInSearchStmt = 11;
    public static final int tooManyRecordsRetrieved = 12;
    public static final int presentRequestOutOfRange = 13;
    public static final int systemErrorPresentingRecords = 14;
    public static final int recordNotAuthorizedForInterSystem = 15;
    public static final int recordExceedsPreferredMessageSize = 16;
    public static final int recordExceedsExceptionalRecordSize = 17;
    public static final int resultSetCannotBeSearchTerm = 18;
    public static final int onlySingleResultSetInSearchTerm = 19;
    public static final int onlyANDingOfASingleResultSetSearchTerm = 20;
    public static final int resultSetExistsAndReplaceIsFalse = 21;
    public static final int resultNamingNotSupported = 22;
    public static final int unsupportedCombinationOfDbs = 23;
    public static final int elementSetNamesUnsupported = 24;
    public static final int elementSetNameUnsupportedForDb = 25;
    public static final int onlyGenericElementSetNameSupported = 26;
    public static final int resultSetNoLongerExists = 27;
    public static final int resultSetInUse = 28;
    public static final int aDataBaseIsLocked = 29;
    public static final int resultSetDoesNotExist = 30;
    public static final int resourcesExhaustedNoResults = 31;
    public static final int resourcesExhaustedPartialResultsAvailable = 32;
    public static final int resourcesExhaustedSubsetAvailable = 33;
    public static final int unspecifiedError = 100;
    public static final int accessControlFailure = 101;
    public static final int challengeRequiredOperationTerminated = 102;
    public static final int challengeRequiredRecordNotIncluded = 103;
    public static final int challengeFailedRecordNotIncluded = 104;
    public static final int terminatedAtOriginRequest = 105;
    public static final int noAbstractSyntaxesForThisRecord = 106;
    public static final int queryTypeNotSupported = 107;
    public static final int malformedQuery = 108;
    public static final int databaseUnavailable = 109;
    public static final int operatorUnsupported = 110;
    public static final int tooManyDatabasesSpecified = 111;
    public static final int tooManyResultSetsCreated = 112;
    public static final int unsupportedAttributeType = 113;
    public static final int unsupportedUseAttribute = 114;
    public static final int unsupportedTermValueforUseAttribute = 115;
    public static final int useAttributeRequiredAndMissing = 116;
    public static final int unsupportedRelationAttribute = 117;
    public static final int unsupportedStructureAttribute = 118;
    public static final int unsupportedPositionAttribute = 119;
    public static final int unsupportedTruncationAttribute = 120;
    public static final int unsupportedAttributeSet = 121;
    public static final int unsupportedCompletenessAttribute = 122;
    public static final int unsupportedAttributeCombination = 123;
    public static final int unsupportedCodedValueForTerm = 124;
    public static final int malformedSearchTerm = 125;
    public static final int illegalTermValueForAttribute = 126;
    public static final int unparsableFormatForUnnormalizedValue = 127;
    public static final int illegalResultSetName = 128;
    public static final int proximityOfSetsNotSupported = 129;
    public static final int illegalResultSetInProxSearch = 130;
    public static final int unsupportedProximityRelation = 131;
    public static final int unsupportedProximityUnitCode = 132;
    public static final int proximityUnsupportedWithThisAttributeCombination = 201;
    public static final int unsupportedDistanceForProximity = 202;
    public static final int orderedProxFlagUnsupported = 203;
    public static final int onlyZeroStepSizeForScanSupported = 205;
    public static final int specifiedStepSizeUnsupportedForScan = 206;
    public static final int cannotSortAccordingToSequence = 207;
    public static final int noResultSetNameSuppliedForSort = 208;
    public static final int genericSortNotSupported = 209;
    public static final int databaseSpecificSortNotSupported = 210;
    public static final int tooManySortKeys = 211;
    public static final int duplicateSortKeys = 212;
    public static final int unsupportedMissingDataAction = 213;
    public static final int illegalSortRelation = 214;
    public static final int illegalCaseValue = 215;
    public static final int illegalMissingDataAction = 216;
    public static final int cannotGuaranteeRecordSegments = 217;
    public static final int ESpackageNameInUse = 218;
    public static final int ESnoSuchPackage = 219;
    public static final int ESquotaExceeded = 220;
    public static final int EStypeNotSupported = 221;
    public static final int ESidNotAuthorized = 222;
    public static final int ESpermissionDenied = 223;
    public static final int ESexecutionFailed = 224;
    public static final int ESexecutionNotSupported = 225;
    public static final int ESexecutionNotSupportedForParameters = 226;
    public static final int noDataInRequestedSyntax = 227;
    public static final int malformedScan = 228;
    public static final int termTypeNotSupported = 229;
    public static final int tooManyInputResultsForSort = 230;
    public static final int incompatibleRecordFormatsForSort = 231;
    public static final int termListUnsupportedInScan = 232;
    public static final int unsupportedValueOfPositionInResponse = 233;
    public static final int tooManyIndexTermsProcessed = 234;
    public static final int databaseDoesNotExist = 235;
    public static final int accessToDatabaseDenied = 236;
    public static final int illegalSort = 237;
    public static final int recordNotAvailableInRequestedSyntax = 238;
    public static final int recordSyntaxNotSupported = 239;
    public static final int resourcesExhaustedLookingForScanTerms = 240;
    public static final int beginningOrEndOfListInScan = 241;
    public static final int maxSegmentSizeTooSmallForRecord = 242;
    public static final int additionalRangesParameterUnsupported = 243;
    public static final int compSpecParameterUnsupported = 244;
    public static final int restrictionNotSupportedInType1 = 245;
    public static final int complexNotSupportedInType1 = 246;
    public static final int attributeSetNotSupportedInType1 = 247;

    public static final int unknownSessionId = 1000;

    public static final String BIB1 = "1.2.840.10003.4.1";
    private static String msgs[];

    int condition;
    String addinfo;

    static {
       msgs = new String[250];
       msgs[1] = "Permanent system error";
       msgs[2] = "Temporary system error";
       msgs[3] = "Unsupported search";
       msgs[4] = "Term all stopWords";
       msgs[5] = "Too many argument words";
       msgs[6] = "Too many boolean operators";
       msgs[7] = "Too many truncated words";
       msgs[8] = "Too many incomplete subfields";
       msgs[9] = "Truncated words too short";
       msgs[10] = "Invalid format for record number search term";
       msgs[11] = "Too many characters in search statement";
       msgs[12] = "Too many records retrieved";
       msgs[13] = "Present request out of range";
       msgs[14] = "System error presenting records";
       msgs[15] = "Record not authorized for inter system";
       msgs[16] = "Record exceeds preferred message size";
       msgs[17] = "Record exceeds exceptional record size";
       msgs[18] = "Resultset cannot be search term";
       msgs[19] = "Only single resultset in search term";
       msgs[20] = "Only ANDing of a single resultset search term";
       msgs[21] = "Resultset exists and replace is false";
       msgs[22] = "Result naming not supported";
       msgs[23] = "Unsupported combination of databases";
       msgs[24] = "Elementset names unsupported";
       msgs[25] = "Elementset name unsupported for database";
       msgs[26] = "Only generic elementset name supported";
       msgs[27] = "Resultset no longer exists";
       msgs[28] = "Resultset in use";
       msgs[29] = "a database is locked";
       msgs[30] = "Resultset does not exist";
       msgs[31] = "Resources exhausted - no results";
       msgs[32] = "Resources exhausted - partial results available";
       msgs[33] = "Resources exhausted - subset available";
       msgs[100] = "Unspecified error";
       msgs[101] = "Access control failure";
       msgs[102] = "Challenge required - operation terminated";
       msgs[103] = "Challenge required - record not included";
       msgs[104] = "Challenge failed - record not included";
       msgs[105] = "Terminated at origin request";
       msgs[106] = "No abstract syntaxes for this record";
       msgs[107] = "Query type not supported";
       msgs[108] = "Malformed query"; 
       msgs[109] = "Database Unavailable"; 
       msgs[110] = "Operator unsupported"; 
       msgs[111] = "Too many databases specified";
       msgs[112] = "Too many resultsets created";
       msgs[113] = "Unsupported attribute type";
       msgs[114] = "Unsupported use attribute";
       msgs[115] = "Unsupported term value for use attribute";
       msgs[116] = "Use attribute required and missing";
       msgs[117] = "Unsupported relation attribute";
       msgs[118] = "Unsupported structure attribute";
       msgs[119] = "Unsupported position attribute";
       msgs[120] = "Unsupported truncation attribute";
       msgs[121] = "Unsupported attribute set";
       msgs[122] = "Unsupported completeness attribute";
       msgs[123] = "Unsupported attribute combination";
       msgs[124] = "Unsupported coded value for term";
       msgs[125] = "Malformed search term";
       msgs[126] = "Illegal term value for attribute";
       msgs[127] = "Unparsable format for unnormalized value";
       msgs[128] = "Illegal resultset name";
       msgs[129] = "Proximity of sets not supported";
       msgs[130] = "Illegal resultset in prox search";
       msgs[131] = "Unsupported proximity relation";
       msgs[132] = "Unsupported proximity unit code";
       msgs[201] = "Proximity unsupported with this attribute combination";
       msgs[202] = "Unsupported distance for proximity";
       msgs[203] = "Ordered prox flag unsupported";
       msgs[205] = "Only zero step size for scan supported";
       msgs[206] = "Specified step size unsupported for scan";
       msgs[207] = "Cannot sort according to sequence";
       msgs[208] = "No resultset name supplied for sort";
       msgs[209] = "Generic sort not supported";
       msgs[210] = "Database specific sort not supported";
       msgs[211] = "Too many sort keys";
       msgs[212] = "Duplicate sort keys";
       msgs[213] = "Unsupported missing data action";
       msgs[214] = "Illegal sort relation";
       msgs[215] = "Illegal case value";
       msgs[216] = "Illegal missing data action";
       msgs[217] = "Cannot guarantee record segments";
       msgs[218] = "ES package name in use";
       msgs[219] = "ES no such package";
       msgs[220] = "ES quota exceeded";
       msgs[221] = "ES type not supported";
       msgs[222] = "ES Id not authorized";
       msgs[223] = "ES permission denied";
       msgs[224] = "ES execution failed";
       msgs[225] = "ES execution not supported";
       msgs[226] = "ES execution not supported for parameters";
       msgs[227] = "No data in requested syntax";
       msgs[228] = "Malformed scan";
       msgs[229] = "Term type not supported";
       msgs[230] = "Too many input results for sort";
       msgs[231] = "Incompatible record formats for sort";
       msgs[232] = "Term list unsupported in scan";
       msgs[233] = "Unsupported value of position in response";
       msgs[234] = "Too many index terms processed";
       msgs[235] = "Database does not exist";
       msgs[236] = "Access to database denied";
       msgs[237] = "Illegal sort";
       msgs[238] = "Record not available in requested syntax";
       msgs[239] = "Record syntax not supported";
       msgs[240] = "Resources exhausted looking for scan terms";
       msgs[241] = "Beginning or end of list in scan";
       msgs[242] = "Maximum segment size too small for record";
       msgs[243] = "Additional ranges parameter unsupported";
       msgs[244] = "Comp spec parameter unsupported";
       msgs[245] = "Restriction not supported in Type1";
       msgs[246] = "Complex not supported in Type1";
       msgs[247] = "Attribute set not supported in Type1";

      
    }

    public Diagnostic1() {
	condition = unspecifiedError;
	addinfo = null;
    }

/**
 * @param condition error condition
 * @param addinfo text string further describing error
 */
    public Diagnostic1(int condition, String addinfo) {
	this.condition = condition;
	this.addinfo = addinfo;
    }

/**
 * Accessor method to Diagnostic code
 */

    public int condition() {
	return condition;
    }

/**
 * Accessor method to Diagnostic msg
 */

    public String addinfo() {
	return addinfo;
    }

/**
  * Retrieve error message from the code
  * @param code the error code
  * @returns the message if available
  */

    public static String msg(int code) {
	if (code < 0  || code > msgs.length) 
          return "";
        if (msgs[code] == null)
          return "";
        return msgs[code];
    }

    public String toString() {
      StringBuffer out = new StringBuffer();
      out.append("Diagnostic1 code= " + this.condition + " msg = " + this.addinfo);
      out.append(" Official msg= " + msg(this.condition));
      return out.toString();
    }
/**
 * Adds the diagnostic in the default format to the parent directory
 */
    public void addDefaultDiagFormat(DataDir parent) {
	parent.addOID(ASN1.OBJECTIDENTIFIER, ASN1.UNIVERSAL, BIB1);
	parent.add(ASN1.INTEGER, ASN1.UNIVERSAL, condition);
	if (addinfo != null)
	    parent.add(ASN1.VISIBLESTRING, ASN1.UNIVERSAL, addinfo);
	else
	    parent.add(ASN1.VISIBLESTRING, ASN1.UNIVERSAL, "");
    }
}
