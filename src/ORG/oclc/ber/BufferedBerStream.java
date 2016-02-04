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

import java.io.*;

/** BufferedBerStream is a class to sequentially read BER records from an 
 * InputStream. Here is an example that opens a file, reads all the
 * records and prints each record.
<pre>
BufferedBerStream berStream = null;
BerString berRec;
//
try { berStream = new BufferedBerStream(new FileInputStream("berfile")); }
catch (FileNotFoundException n) {
    System.out.println(n.toString());
    return;
}
//
while (true) {
    try { berRec = berStream.readBerString(); 
	System.out.println(berRec.toString());
    }
    catch (FileNotFoundException n) {
        System.out.println(n.toString());
	return;
    } catch (EOFException n) {
        System.out.println(n.toString());
	return;
    } catch (IOException n) {
        System.out.println(n.toString());
	return;
    }
}
</pre>
 * @see BerString
 * @version @(#)BufferedBerStream.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class BufferedBerStream extends BufferedInputStream {

/**
* Create a BufferedInputStream ready for reading BerStrings. The default
* buffer size is 10000 bytes.
* @param in filename
* @exception FileNotFoundException Creation of BufferedInputStream failed
*/
	public BufferedBerStream(String filename) 
            throws FileNotFoundException {
 
	    super(new FileInputStream(filename), 10000);
	}

/**
* Create a BufferedInputStream ready for reading BerStrings. The default
* buffer size is 10000 bytes.
* @param in InputStream
*/
	public BufferedBerStream(InputStream in) {
	    super(in, 10000);
	}

/**
* Create a BufferedInputStream ready for reading BerStrings.
* @param in InputStream
* @param int buffer size
*/
	public BufferedBerStream(InputStream in, int size) {
	    super(in, size);
	}

/**
* Create a BufferedInputStream ready for reading BerStrings.
* @param in filename
* @param int buffer size
* @exception FileNotFoundException Creation of BufferedInputStream failed
*/
	public BufferedBerStream(String filename, int size) 
	    throws FileNotFoundException {
	    super(new FileInputStream(filename), size);
	}

/**
* Read a BerString from the InputStream.
* @exception FileNotFoundException Creation of BufferedInputStream failed.
* @exception EOFException End of file was reached.
* @exception IOException I/O Error occured while reading file.
*/
	public BerString readBerString() 
	    throws FileNotFoundException, EOFException, IOException {
	    return new BerString(in);
	}
}
