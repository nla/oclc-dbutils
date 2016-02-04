/*
(c)1996 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
Ohio 43017-0702.  OCLC is a registered trademark of OCLC Online Computer
Library Center, Inc.
 
NOTICE TO USERS:  The BER Utilities ("Software") has been developed by OCLC
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

package ORG.oclc.ber;

/** ASN1 is the constant data needed for BerString and
 *  DataDir. It also contains generic routines used by 
 *  both of those classes.
<p> The BER utilities presented here are a translation of the C version of
berutil written by Ralph Levan at OCLC. The documentation presented here
is pretty much taken from his docummentation. </p>
<p>
OCLC has chosen not to use an ASN.1 compiler in its ASN.1 activities.
These activities include ASN.1 encoding of z39.50, DBI (an internal variant
on z39.50) and ASN.1 encoding of all database records, including MARC records.
Strictly speaking, OCLC does not use ASN.1 internally, other than as a
language for communicating record specifications to programmers.  However,
all the records mentioned above are encoded using the Basic Encoding Rules
and are manipulable by the BER decoders of ASN.1 compilers.

We have chosen to represent the BER records internally as a tree structure of
linked lists that describe the parts of the BER record.  We believe that this
hierarchical structure fully represents the complexity of record encodable
with the BER.

The nodes in this tree structure contain the BER tag, class, form and
either a pointer to data or a pointer to the next node down in the
hierarchy, depending on whether the form was primitive or constructed.  We
often use a picture of this tree structure for describing the syntax of the
record being encoded, rather than using ASN.1.  For instance, the
DeleteResultSetRequest is described in ASN.1 as:
<pre>
     DeleteResultSetRequest ::=
	SEQUENCE
	  {referenceId                  ReferenceId OPTIONAL,
	  deleteSetFunction        [32] IMPLICIT INTEGER
					     {DeleteSpecificSet (0),
					     deleteAllSets      (1)}
	  resultSetId                   ResultSetId OPTIONAL}

</pre>
with appropriate definitions for the non-terminals given somewhere else.
We describe the same thing with the following picture:
<pre>

DeleteResultSetRequest [26]
	  |
     [ReferenceId [2]] --- deleteSetFunction [32] --- [resultSetId [31]]
	     |
	OCTETSTRING

</pre>
with appropriate descriptions of the values that the leaves in the tree can
take.  We do not make any claims that this is better than ASN.1, it's just
that it maps directly into the tree structure that gets mapped into and out
of the BER records.

What we lose by using our own internal syntax is the use of a Presentation
Layer produced by someone else.  This means that currently those things
that could be done by the Presentation layer are done by the application.
This includes integer byte ordering, characterset translation and BER
encoding.  While this could be a major loss, is hasn't been one so far and
we haven't heard of any real Presentation layers that do all the things
promised anyway.
</p>
 * @see BerString
 * @see DataDir
 * @version @(#)ASN1.java	1.10 01/13/97
 * @author Jenny Colvard
 */

public class ASN1 {
	// ASN.1 tag classes
	public static final byte UNIVERSAL   = 0;
	public static final byte APPLICATION = 1;
	public static final byte CONTEXT     = 2;
	public static final byte PRIVATE     = 3;

	// ASN.1 tag forms
	public static final byte PRIMITIVE   = 0;
	public static final byte CONSTRUCTED = 1;

	// ASN.1 UNIVERSAL data types
	public static final byte BOOLEAN		=  1;
	public static final byte INTEGER		=  2;
	public static final byte BITSTRING		=  3;
	public static final byte OCTETSTRING		=  4;
	public static final byte NULL			=  5;
	public static final byte OBJECTIDENTIFIER	=  6;
	public static final byte OBJECTDESCRIPTOR	=  7;
	public static final byte EXTERNAL		=  8;
        public static final byte ENUMERATED             = 10;
	public static final byte SEQUENCE		= 16;
	public static final byte SET			= 17;
	public static final byte VISIBLESTRING		= 26;
	public static final byte GENERALSTRING		= 27;

	// ASN.1 EXTERNAL encoding choices
	public static final byte single_ASN1_type	= 0;
	public static final byte octet_aligned		= 1;
	public static final byte arbitrary		= 2;

/**
* Get the length needed to represent the given fldid.
* @param fldid determine length needed to encode this
* @return length needed to encode given fldid
*/
	protected static final int tagLen(int fldid) {
            return ((fldid < 31) ? 1 : (fldid < 128) ? 2 : 3);
        }

/**
* Get the length needed to represent the given length.
* @param length determine length needed to encode this
* @return length needed to encode given length
*/
        protected static final int lenLen(int length) {
            return ((length < 128) ? 1 :
                    (length < 256) ? 2 :
                    (length < 65536L) ? 3 : 4);
        }

/**
* Get the length needed to represent the given number.
* @param number determine length needed to encode this
* @return length needed to encode given number
*/
	protected static final int numLen(long num) {
	    num = num < 0 ? -num : num;
	    return ((num < 128) ? 1 :
		    (num < 32768) ? 2 :
		    (num < 8388608) ? 3 : 
		    (num < 2147483648L) ? 4 :
		    (num < 549755813888L) ? 5 :
		    (num < 140737488355328L) ? 6 :
		    (num < 36028797018963968L) ? 7 : 8);
	}

/**
* Put a number into a given buffer
* @param record buffer to use
* @param offset offset into buffer
* @param num number to put into buffer
*/
	protected static final void putNum(byte record[], 
	    int offset, long num) {
	    for (int count = numLen(num) - 1; count >= 0; count--)
	    {
		record[offset+count] = (byte)(num & 0xff);
		num >>= 8;
	    }
        }

/**
* Get a number from a buffer
* @param record buffer
* @param offset start at this offset
* @param len length of the number to get
*/
	public static final long getLong(byte record[], 
	    int offset, int len) {
            long num = 0;
            
            if ((record[offset]&0xff) > 127)
                num = ~(0);
 
            for (int i = 0; i < len; i++)
                num = (num << 8) + (record[i+offset]&0xff);
 
            return num;
        }

	public static final int getNum(byte record[], 
	    int offset, int len) {
            int num = 0;
            
            if ((record[offset]&0xff) > 127)
                num = ~(0);
 
            for (int i = 0; i < len; i++)
                num = (num << 8) + (record[i+offset]&0xff);
 
            return num;
        }

}
