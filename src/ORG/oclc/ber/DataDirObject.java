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

/** DataDirObject is an interface that allows
 *  various types of objects to be stored in a DataDir. The DataDir class
 *  provides methods for storing and retrieving bitstrings, byte arrays, 
 *  Strings, numbers, and OIDs (Object Identifier from Z39.50). If you want
 *  1) to store an object of an additional type and 2) do not want to
 *  convert that object to a byte array or String then implement the object
 *  from this interface. Use daddObj() to insert the object into a DataDir.
 *  The implemented object will need a method to convert a byte array back
 *  into the object since a DataDir built from a BerString only has byte
 *  arrays. 
 * @version @(#)DataDirObject.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public interface DataDirObject {

/**
* Method to convert the object to a byte array.
*/
    public byte[] toByteArray();
/**
* Method to get the length of the object.
*/
    public int length();
/**
* Method to convert the object to a String.
*/
    public String toString();
/**
* Method to create a clone of the object.
*/
    public Object clone();
}

