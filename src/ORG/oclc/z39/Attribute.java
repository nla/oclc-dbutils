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
 * Attribute is a class for AttributeElements. AttributeElements are defined 
 * by Z39-50-APDU-1995.
 * @version @(#)Attribute.java	1.1 01/21/99
 * @author Jenny Colvard
 */

public class Attribute {

/** 
 * Z39.50 Bib-1 attribute set object identifier.
 */
    static public final String BIB1 = "1.2.840.10003.3.1";
/**
 * Z39.50 Exp-1 attribute set object identifier
 */
    static public final String EXP1 = "1.2.840.10003.3.2";
/**
 * Z39.50 ZDSR attribute set object identifer
 */
    static public final String ZDSR = "1.2.840.10003.3.3";
/**
 * Z39.50 Bib-1 attribute type.
 */
    public static final int BIB1_use          = 1;
/**
 * Z39.50 Bib-1 attribute type.
 */
    public static final int BIB1_relation     = 2;
/**
 * Z39.50 Bib-1 attribute type.
 */
    public static final int BIB1_position     = 3;
/**
 * Z39.50 Bib-1 attribute type.
 */
    public static final int BIB1_structure    = 4;
/**
 * Z39.50 Bib-1 attribute type.
 */
    public static final int BIB1_truncation   = 5;
/**
 * Z39.50 Bib-1 attribute type.
 */
    public static final int BIB1_completeness   = 6;
/**
 * Z39.50 Bib-1 attribute type.
 */
    public static final int BIB1_newtonIds    = 127;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_PersonalName           = 1;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_CorporateName          = 2;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ConferenceName         = 3;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Title                  = 4;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleSeries            = 5;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleUniform           = 6;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ISBN                   = 7;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ISSN                   = 8;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_LCcardNumber           = 9;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_BNBcardNo              = 10;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_BGFnumber              = 11;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_LocalNumber            = 12;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_DeweyClassification    = 13;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_UDCclassification      = 14;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_BlissClassification    = 15;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_LCcallNumber           = 16;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NLMcallNumber          = 17;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NALcallNumber          = 18;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_MOScallNumber          = 19;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_LocalClassification    = 20;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_SubjectHeading         = 21;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_SubjectRameau          = 22;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_BDIindexSubject        = 23;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_INSPECsubject          = 24;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_MESHsubject            = 25;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_PAsubject              = 26;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_LCsubjectHeading       = 27;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_RVMsubjectHeading      = 28;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_LocalSubjectIndex      = 29;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_UseDate                = 30;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_DateOfPublication      = 31;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_DateOfAcquisition      = 32;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleKey               = 33;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleCollective        = 34;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleParallel          = 35;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleCover             = 36;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleAddedTitlePage    = 37;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleCaption           = 38;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleRunning           = 39;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleSpine             = 40;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleOtherVariant      = 41;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleFormer            = 42;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleAbbreviated       = 43;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_TitleExpanded          = 44;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_SubjectPrecis          = 45;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_SubjectRSWK            = 46;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_SubjectSubdivision     = 47;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NoNatlBiblio           = 48;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NoLegalDeposit         = 49;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NoGovtPub              = 50;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NoMusicPublisher       = 51;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NumberDb               = 52;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NumberLocalCall        = 53;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_CodeLanguage           = 54;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_CodeGeographicArea     = 55;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_CodeInstitution        = 56;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NameAndTitle           = 57;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_NameGeographic         = 58;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_PlacePublication       = 59;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_CODEN                  = 60;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_MicroformGeneration    = 61;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Abstract               = 62;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Note                   = 63;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_AuthorTitle            = 1000;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_RecordType             = 1001;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Name                   = 1002;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Author                 = 1003;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_AuthorNamePersonal     = 1004;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_AuthorNameCorporate    = 1005;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_AuthorNameConference   = 1006;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_IdentifierStandard     = 1007;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_SubjectLCchildrens     = 1008;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_SubjectNamePersonal    = 1009;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_BodyOfText             = 1010;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_DateTimeAddedToDb      = 1011;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_DateTimeLastModified   = 1012;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_AuthorityFormatId      = 1013;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ConceptText            = 1014;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ConceptReference       = 1015;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Any                    = 1016;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ServerChoice           = 1017;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Publisher              = 1018;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_RecordSource           = 1019;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Editor                 = 1020;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_BibLevel               = 1021;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_GeographicClass        = 1022;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_IndexedBy              = 1023;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_MapScale               = 1024;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_MusicKey               = 1025;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_RelatedPeriodical      = 1026;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ReportNumber           = 1027;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_StockNumber            = 1028;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ThematicNumber         = 1030;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_MaterialType           = 1031;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_DocId                  = 1032;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_HostItem               = 1033;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_ContentType            = 1034;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_Anywhere               = 1035;
/**
 * Z39.50 Bib-1 use attribute.
 */
    static public final int BIB1_Use_AuthorTitleSubject     = 1036;
/**
 * Z39.50 Bib-1 relation attribute.
 */
    static public final int BIB1_Relation_stem              = 101;
/**
 * Z39.50 Bib-1 relation attribute.
 */
    static public final int BIB1_Relation_relevance         = 102;
/**
 * Z39.50 Bib-1 relation attribute.
 */
    static public final int BIB1_Relation_within            = 104;
/**
 * Z39.50 Bib-1 position attribute.
 */
    static public final int BIB1_Position_anyPosition       = 3;
/**
 * Z39.50 Bib-1 structure attribute.
 */
    static public final int BIB1_Structure_Phrase       = 1;
/**
 * Z39.50 Bib-1 structure attribute.
 */
    static public final int BIB1_Structure_Word         = 2;
/**
 * Z39.50 Bib-1 structure attribute.
 */
    static public final int BIB1_Structure_Key          = 3;
/**
 * Z39.50 Bib-1 structure attribute.
 */
    static public final int BIB1_Structure_Date         = 5;
/**
 * Z39.50 Bib-1 structure attribute.
 */
    static public final int BIB1_Structure_WordList     = 6;
/**
 * Z39.50 Bib-1 structure attribute. - OCLC ONLY
 */
    static public final int BIB1_Structure_WordListAdj  = 7;
/**
 * Z39.50 Bib-1 structure attribute. - OCLC ONLY
 */
    static public final int BIB1_Structure_WordListOR   = 8;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_Trunc_RightTruncation  = 1;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_Trunc_LeftTruncation   = 2;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_Trunc_LeftAndRightTruncation = 3;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_Trunc_DoNotTruncate   = 100;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_ProcessWildCardInSearch  = 101;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_regExpr_1                = 102;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_regExpr_2                = 103;
/**
 * Z39.50 Bib-1 truncation attribute.
 */
    static public final int BIB1_ProcessWildCards         = 104;

/**
 * Z39.50 Exp-1 attribute type
 */
    static public final int EXP1_use                    = 1;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_ExplainCategory    = 1;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_DatabaseName       = 3;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_TargetName         = 4;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_AttributeSetOID    = 5;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_ProcessingContext  = 13;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_ProcessingName     = 14;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_TermListName       = 15;
/**
 * Z39.50 Exp-1 use attribute
 */
    static public final int EXP1_Use_ProcessingOID      = 27;
/**
 * Z39.50 ZDSR attribute type
 */
    static public final int ZDSR_use                 = 1;
/**
 * Z39.50 ZDSR attribute type
 */
    static public final int ZDSR_weight              = 5;
/**
 * Z39.50 ZDSR use attribute
 */
    static public final int ZDSR_Use_Score           = 1;
/**
 * Z39.50 ZDSR use attribute
 */
    static public final int ZDSR_Use_Rank            = 2;

    static public  final int attributeSet            = 1;
    static public  final int AttributeType           = 120;
    static public  final int AttributeValue          = 121;
    
/**
 * Type of attribute.
 */
    public int type;
/**
 * Value of attribute.
 */
    public int value;
/**
 * Set - object identifer defining attribute set
 */
    public String set;

/**
 * Create an Attribute from a DataDir.
 * @param attribute AttributeElement DataDir starting with SEQUENCE tag 
 * @exception Diagnostic1 when the structure of the DataDir does not conform 
 * to the standard
 */
    public Attribute(DataDir attribute) throws Diagnostic1 {

	DataDir tmp;
	
	if (attribute.fldid() != ASN1.SEQUENCE)
	    throw new Diagnostic1(Diagnostic1.malformedQuery, null);

	tmp = attribute.child();
	if (tmp.fldid() == attributeSet)
	{
	    set = tmp.getOID();
            tmp = tmp.next();  
	}
	else
	    set = BIB1;

	type = tmp.getInt();

        if (tmp.next().fldid() != AttributeValue)
          throw new Diagnostic1(Diagnostic1.complexNotSupportedInType1, null);

	value = tmp.next().getInt();
    
    } 

/**
 * Create an Attribute from a specified type and value.
 */
    public Attribute(int type, int value) {
	this.type = type;
	this.value = value;
	this.set = BIB1;
    }

/**
 * Test an Attribute object against this Attribute object  to see if the 
 * types and values match.
 * @param a the other Attribute object
 * @return true or false
 */
    public boolean equals(Attribute a) {
	if (type == a.type && value == a.value && set == a.set)
	    return true;
	return false;
    }

/**
 * Build an AttributeElement DataDir based on the specified attribute type
 * and value.
 * @return DataDir starting with SEQUENCE tag and containing type and value
 * children.
 */
    public static DataDir buildDir(int type, int value, String set) {
	
	DataDir top = new DataDir(ASN1.SEQUENCE, (int)ASN1.UNIVERSAL);
	if (set != null)
	    top.addOID(attributeSet, ASN1.CONTEXT, set);
  	top.add(AttributeType, ASN1.CONTEXT, type);
	top.add(AttributeValue, ASN1.CONTEXT, value);
	return top;
    }

/**
 * Build an AttributeElement DataDir based on the specified attribute type
 * and value.
 * @return DataDir starting with SEQUENCE tag and containing type and value
 * children.
 */
    public static DataDir buildDir(int type, int value) {
	return Attribute.buildDir(type, value, null);
    }

/**
 * Build an AttributeElement DataDir based on the specified Attribute object.
 * @return DataDir starting with SEQUENCE tag and containing type and value
 * children.
 */
    public static DataDir buildDir(Attribute a) {
	return Attribute.buildDir(a.type, a.value, a.set);
    }

/**
 * Return a String describing the type and value of this Attribute object.
 */
    public String toString() {
	if (set == BIB1) {
	    switch (type) {
		case BIB1_use:
		    return "use=" + value;
		case BIB1_relation:
		    return "relation=" + value;
		case BIB1_position:
		    return "position=" + value;
		case BIB1_structure:
		    return "structure=" + value;
		case BIB1_truncation:
		    return "truncation=" + value;
		case BIB1_newtonIds:
		    return "newtonId=" + value;
	    }
	}
	return ("(type=" + type + ";value=" + value + ") ");
    }
}
