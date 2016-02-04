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
import ORG.oclc.util.Util;

/** BerString is a class for BER encoded strings. <p> Here is an example to
 * read a BER
 * record from System.in, build a DataDir tree over the BER record and
 * write a formatted version of the DataDir tree to System.out.
 * <pre>
 * BerString berRec = new BerString(System.in);
 * DataDir dir = new DataDir(berRec);
 * System.out.println(dir.toString());
 * </pre>
 * <p>
 * The BER utilities are available via anonymous ftp to ftp.rsch.oclc.org and
 * are in the pub/BER_utilities directory.  They are available in either a C
 * version (as source) or a Java version (as class files).
 * </p>
 * @see ASN1
 * @see BufferedBerStream
 * @version @(#)BerString.java  1.1 07/09/97
 * @author Jenny Colvard
 */

public class BerString extends ASN1 implements Serializable {
    static final boolean debug=false;

    protected byte record[];
    protected int  offset;
    protected boolean indefinite;

    public boolean EOFFound = false;


    public BerString(byte record[]) {
        this.record = record;
        offset = 0;
    }


   /**
    * Build a ber_record from a directory.
    * @param dir directory
    */
    public BerString(DataDir dir) {
        record = new byte[dir.recLen()];
        dir.asmRec(this);
        offset = 0;
    }


   /**
    * Build a ber_record from a directory and leave space in the buffer
    * for other data to be provided by the application. This is a tricky
    * one but it's very useful. The BER record is often only part of a
    * record being built for inclusion in a package to be given to telecom
    * and the BER record gets headers and trailers. If you use 'new
    * BerString(DataDir)', it will allocate space for the record and build the
    * record and then you'll have to move the BER record over to the area where
    * your telecom message is being built. Ralph hates moving data
    * unnecessarily. A preferable method is to leave room in the BER
    * record buffer for the header and trailer information.
    <pre>
    int header_size = 13, trailer_size = 14;
    DataDir dir; // BER record you want between header & trailer
    BerString berRec = new BerString(dir, header_size + trailer_size,
    header_size);
    </pre>
    * @param dir directory
    * @param length additional space requested
    * @param offset where to build the BER record in the new buffer
    */
    public BerString(DataDir dir, int extraLength, int offset) {
        record = new byte[extraLength + dir.recLen()];
        this.offset = offset;
        dir.asmRec(this);
        this.offset = 0;
    }


   /**
    * Read a ber_record from the InputStream.
    * @param in InputStream
    * @exception FileNotFoundException Creation of BufferedInputStream failed
    * @exception IOException Error reading InputStream
    */
    public BerString(InputStream in)
      throws FileNotFoundException, IOException, EOFException {
        int         bytesRead, lengthSoFar, remainder[] = new int[1];
        InputStream my_in = in;

        record = new byte[7]; // enough to hold the length
        if ((lengthSoFar=my_in.read(record, 0, 2)) == -1) {
            EOFFound = true;
            //return;
            EOFException e = new EOFException("Normal EOF");
            throw e;
        }

        while (!IsCompleteBER(lengthSoFar, remainder)) {
	    if(debug)
		System.out.println("remainder="+remainder[0]+", lengthSoFar="+
                    lengthSoFar);
            if (remainder[0]<1) {
                if (remainder[0] == -1)
                    indefinite=true;
                if (lengthSoFar + 1 > record.length)
                    record = resizeRecord(record, record.length + 10);
		if(debug)
		    System.out.println("reading a byte into offset "+
                        lengthSoFar);
                if ((bytesRead=my_in.read(record, lengthSoFar, 1)) == -1)
                    throw new EOFException("Unexpected EOF #2");
            }
            else {
                /*System.err.println("lengthSoFar=" + lengthSoFar +
                      ", remainder=" + remainder[0] +
                      ", recordLength=" + record.length);*/
                if ((lengthSoFar+ remainder[0]) > record.length) {
                    record = resizeRecord(record, lengthSoFar + remainder[0]);
                }
                if ((bytesRead=my_in.read(record, lengthSoFar,
                  remainder[0])) == -1)
                    throw new EOFException("Unexpected EOF #3");
            }
	    lengthSoFar += bytesRead;
        }

        if (record.length != lengthSoFar) {
            record = resizeRecord(record, lengthSoFar);
        }
        offset = 0;

	if(debug) {
	    System.out.println("Final record length="+lengthSoFar);
	    System.out.println("remainder (should equal final length) ="+
                remainder[0]);
	}
    }


   /**
    * Find the length of a BER record.
    * @return length
    */
    public final int asn1Len() {
        int lenlen = 0, fieldlen[]=new int[1];
        int fldid, taglen[] = new int[1];
        int saveOffset = offset;

        offset = 0;
        fldid = getTag(taglen);
        lenlen = getLen(fieldlen);

        if (fieldlen[0] == -1) {
            if (IsCompleteBER(0, 0x7fffffff, fieldlen)){
                fieldlen[0] -= (taglen[0]+lenlen);
                /* don't include trailing nulls in loop later */
                fieldlen[0] -= 2;
            }
        }

        offset=saveOffset;
	if(debug)
	    System.out.println("BerString.asn1Len returning "+
                (lenlen + fieldlen[0] + offset));
        return (lenlen + fieldlen[0] + offset);
    }


   /**
    * Get a length directly from a BER record.
    * @return length of BER record
    */
    public final int getLen(int fieldlen[]) {
        int lenlen=0;

        int c = record[offset++] & 0xff;

        /* if the sign bit is turned on in the first byte of
           the length field, then the first byte contains
           the number of subsequent bytes necessary to contain
           the length, or the length is indefinite if
           the remaining bits are zero.
           Otherwise, the first byte contains the length */

        if ((c) < 128) {  // sign bit off
            fieldlen[0] = c;
            return 1;
        }

        if ((c-128) > 4) {
            fieldlen[0] = -1;
            return 0;
        }

        if (c-127 > 0x7fffffff) {
            fieldlen[0] = -1;
            return 0;
        }

        /* indefinite length */
        /* fix for compiler bug */
        if ((c-128)==0) {
            //if (c==128) {
            fieldlen[0] = -1;
            return 1;      // the length of the field is 1
        }

        fieldlen[0] = 0;
        for (int i=0; i<(c-128) && offset < record.length; i++) {
            fieldlen[0]<<=8;
            fieldlen[0] += (record[offset++]&0xff);
        }
        lenlen = c-127;

        return lenlen;
    }


    private final int get_len(int fieldlen[], int offset, int len) {
        if (len == 0) {
            fieldlen[0] = -1;
            return 0;
        }
        byte c = record[offset++];

        if ((c&0xff) < 128) {
            fieldlen[0] = c;
            return 1;
        }

        int tlen = 0;
        if (((c&0xff)-128) > 4) {
            fieldlen[0] = -1;
            return 0;
        }

        if ((c&0xff)-127 > len) {
            fieldlen[0] = -1;
            return 0;
        }

        if ((c&0xff)==128) {
            fieldlen[0] = -1;
            return 1;
        }

        for (int i=0; i<((c&0xff)-128); i++) {
            tlen<<=8;
            tlen += (record[offset++]&0xff);
        }

        fieldlen[0] = tlen;
        return (c&0xff)-127;
    }


   /**
    * Get a tag directly from a BER record.
    * @return tag
    */
    public final int getTag() {
        return getTag(null);
    }


   /**
    * Get a tag directly from a BER record and fill in the tagLen in
    * the incoming integer array.
    * @return tag
    */
    public final int getTag(int tagLen[]) {
        int len = (((record[offset] & 0x1f) != 0x1f) ? 1 :
                   ((record[offset+1]&0xff) < 0x80) ? 2 : 3);
        int id_code = 0;

        if (len == 1)
            id_code = record[offset] & 0x1f;
        else
            for (int i = 1; i < len; i++)
                id_code = (id_code << 7) | (record[i+offset] & 0x7f);

        offset += len;

        if (tagLen != null)
            tagLen[0] = len;
        return id_code;
    }


    private final int get_tag(int tag[], int offset, int len) {
        if (len == 0) {
            tag[0] = -1;
            return 0;
        }

        byte c = (byte)(record[offset++] & 0x1f);
        int taglen = 1;

        if ((c&0xff) < 0x1f)
            tag[0] = c;
        else {
            if (len==1) {
                tag[0] = -1;
                return 0;
            }

            tag[0] = 0;
            c = record[offset++];
            taglen++;
            while ( (c&0xff)>0x80 && taglen<len) {
                tag[0] += c & 0x7f;
                tag[0] <<= 7;
                c = record[offset++];
                taglen++;
            }
            if ( (c&0xff)>0x80 && taglen==len) { // missing part
            }
            tag[0] += c;
        }
        return taglen;
    }


   /**
    * Determine if a complete BER record has been received.
    * @param len length of BER record
    * @param remainder number of bytes missing from record, or 0
    * @return true or false. If false, the remainder will tell you how many
    * bytes need to be read. This value could be 0, which means that the
    * record has indeterminate length or that you haven't even received the
    * length portion of the record yet. How you read the remainder of the
    * record, in this case, will depend on the access method. Best to just read
    * 1 byte at a time until IsCompleteBER() tells you that you are done or
    * gives you a definite length to read. If true, remainder[0] is set to the
    * actual length of the record.
    */
    public final boolean IsCompleteBER(int len, int remainder[]) {
        return (IsCompleteBER(0, len, remainder));
    }

    public final boolean IsCompleteBER(int offset, int len, int remainder[]) {
        int lenlen, localoffset=offset, taglen, tag = -1;
        int fieldlen, headerlen;
        int locallen=len;

	if(debug) {
	    System.out.println("in IsCompleteBER: offset="+offset+
                ", len="+len);
	    System.out.println(
                Util.byteArrayToString(record, offset,
                ((len+offset)>record.length?(record.length-offset):len)));
	}
        remainder[0] = 0;
        if (locallen == 0)
            return false;

	// begin the old get_tag code, embedded now
        int c = record[localoffset++] & 0x1f; // only last 5 bits of the first
                                              // byte of a BER tag
	if(debug)
	    System.out.println("tag: c="+c);
        taglen = 1;
        if (c < 0x1f)
            tag = c;
        else {
            if (locallen==1) { // not enough data yet
		if(debug)
		    System.out.println(
                        "not enough data to assemble a tag yet");
                return false;
	    }
            tag = 0;
            c = record[localoffset++]&0xff;
            taglen++;
            while(c>0x80 && taglen<locallen) {
                tag += c & 0x7f;
                tag <<= 7;
                c = record[localoffset++]&0xff;
                taglen++;
            }
            if(c>0x80 && taglen==locallen) {  // missing part
		if(debug)
		    System.out.println(
                        "still not enough data to assemble a tag yet");
                return false;
	    }
            tag+=c;
        }
	if(debug)
	    System.out.println("tag="+tag+", taglen="+taglen);
	// end get_tag


	// begin the old get_len code, embedded now
        if ((locallen-taglen) == 0) {
	    if(debug)
		System.out.println("no length yet");
            return false;
	}
        c = record[localoffset++]&0xff;
	if(debug)
	    System.out.println("len: c="+c);
        if(c < 128) {
            fieldlen = c;
            lenlen = 1;
        }
        else {
            if(c-128 > 4) {  // second nibble in byte says how many following
		// bytes contain the length. If more than 4, throw exception
		throw new IllegalStateException("BER length field > 4 bytes");
	    }
            if(c-127 > (len-taglen)) {
		if(debug)
		    System.out.println(
                        "not enough data to assemble a length yet");
                return false;
	    }
            if (c==128) {
                fieldlen = -1;
                lenlen=1;
            }
            else {
                fieldlen = 0;
                for (int i=0; i<(c-128); i++) {
                    fieldlen<<=8;
                    fieldlen += (record[localoffset++]&0xff);
                }
                lenlen = c-127;
            }

            int tlen = 0;
        }
	if(debug)
	    System.out.println("fieldlen="+fieldlen+", lenlen="+lenlen);
	// end get_len


        headerlen=taglen+lenlen;
	if(debug)
	    System.out.println("headerlen="+headerlen);

        if (lenlen==1 && fieldlen == -1) { // indefinite length ber record
            int ifieldlen[] = new int[1], totlen=0, ioffset=offset;
            int b0=-1, b1=-1;
            remainder[0] = -1;

            ifieldlen[0] = 0;

            // loop through the subfields and see if they are complete
            for(ioffset+=headerlen, locallen-=headerlen; locallen > 1;
              ioffset+=ifieldlen[0], locallen-=ifieldlen[0]) {
                b0 = record[ioffset];
                b1 = record[ioffset+1];
		if(b0==0 && b1==0)
		    break;
                ifieldlen[0] = -1;

                if (!(IsCompleteBER(ioffset, locallen, ifieldlen))) {
                    remainder[0] = ifieldlen[0];
                    return false;
                }

                totlen += ifieldlen[0];
            }

            if (locallen>1 && b0==0 && b1==0) {
                remainder[0]=headerlen+totlen+2;  /* + 2 nulls at end */
                return true;
            }
            remainder[0] = -1;
            return false;

        } // done with Indefinite length

        if (fieldlen+headerlen <= locallen) {
            remainder[0]=fieldlen+headerlen;
            return true;
        }

        remainder[0]=fieldlen+headerlen-len;
        return false;
    }


   /**
    * Accessor method for byte[] record.
    */
    public byte[] record() {
        return record;
    }


    private final byte[] resizeRecord(byte[] record, int recordSize) {
        byte[] newRecord = new byte[recordSize];
        System.arraycopy(record, 0, newRecord, 0,
            Math.min(record.length, newRecord.length));
        /*
        System.err.println("Resizing ber record from " + record.length +
            " to " + newRecord.length + " bytes.");
        */
        return newRecord;
    }


   /**
    * Allows caller to reset offset to beginning of record.
    */
    public void setOffset(int offset) {
        this.offset = offset;
    }


   /**
    * Creates String representation of BER record.
    */
    public final String toString() {
        return toString(record.length);
    }


   /**
    * Creates String representation of BER record.
    * @param length Truncate output to this length.
    */
    public final String toString(int length) {
        StringBuffer str = new StringBuffer();
        int stopat;

        offset = 0;
        stopat = offset+length;

        for (int i=1; offset < stopat; offset++,i++) {
            if ((record[offset]&0xff)<16)
                str = str.append(" 0");
            else
                str = str.append(" ");
            str = str.append(Integer.toString(record[offset]&0xff,16));
            if ((i%20)==0)
                str = str.append("\n");
        }
        offset = 0;
        return str.toString();
    }


   /**
    * Put an array of bytes in to a BER record.
    * @param chars array of bytes
    * @param length number of bytes to copy into record
    */
    public final void putChar(byte chars[], int length) {
        System.arraycopy(chars, 0, record, offset, length);
        offset += length;
    }


    public final void putChar(byte chars[], int toffset, int length) {
        System.arraycopy(chars, toffset, record, offset, length);
        offset += length;
    }


   /**
    * Put a length directly into a BER record.
    * @param length length to put into record
    */
    public final void putLen(int len) {
        if (len < 128)
            record[offset++] = (byte)len;
        else {
            int t;
            record[offset] = (byte)(lenLen(len) - 1);
            for (t = record[offset]; t > 0; t--) {
                record[offset+t] = (byte)(len & 0xff);
                len >>= 8;
            }
            t = offset;
            offset += (record[offset]&0xff) + 1;
            record[t] += 128; // turn on bit 8 in length byte.
        }
    }


   /**
    * Put a number into a BER record.
    * @param number number to put
    */
    public final void putNumber(long number) {
        putNum(record, offset, number);
        offset+=numLen(number);
    }


   /**
    * Put a tag directly into a BER record.
    * @param fldid fldid of tag
    * @param asn1class class of tag
    * @param form form of tag
    */
    public final void putTag(int fldid, byte asn1class, byte form) {
        if (fldid < 31)
            record[offset++] = (byte)(fldid + asn1class*64 + form*32);
        else {
            record[offset++] = (byte)(31 + asn1class*64 + form*32);
            if (fldid < 128)
                record[offset++] = (byte)(fldid);
            else {
                record[offset++] = (byte)(128 + fldid/128);
                record[offset++] = (byte)(fldid % 128);
            }
        }
    }


   /**
    * Write the BerString to the OutputStream.
    */
    public final void writeBerString(OutputStream out) throws IOException {
        out.write(record);
    }
}
