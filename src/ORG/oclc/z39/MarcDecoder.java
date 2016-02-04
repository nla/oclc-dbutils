

package ORG.oclc.z39;

import java.util.StringTokenizer;
import java.io.*;
import ORG.oclc.util.MarcUtil;
import ORG.oclc.util.Util;
import ORG.oclc.ber.DataDir;
import ORG.oclc.ber.ASN1;


/** MarcDecoder allows ZBase to decode Marc records.
 */

public class MarcDecoder {
        private static boolean debug=false;
  	private char array[];

        private byte record[];
        int recordLength;
/**
* Returns a byte array object representation of the input data.
*/
        public byte[] record() { return record; }
 
/**
* Returns a string representation of the input data.
*/
        public String stringData() { return new String(record); }
 

/**
* Initializes an object so that individual Marc field 000
* and Marc field 008 elements may be picked out of a String containing the
* entire field.
* @param data String containing data for the Marc field.
*/
	public MarcDecoder(String data) {
	    array = new char[42]; // longest possible field 008 field
	    data.getChars(0, data.length(), array, 0);
	}

/**
* Constructs a MarcDecoder object using the input InputStream Object.
* @param in InputStream
*/
	public MarcDecoder(InputStream in) 
          throws FileNotFoundException, IOException, EOFException {
            InputStream my_in = in;
            byte length[] = new byte[5];
            int lengthSoFar=0;
            boolean skipping = true;

            lengthSoFar=my_in.read(length, 0, 1);
            while (skipping  && lengthSoFar != -1) {
               try {
                  recordLength = Integer.parseInt(new String(length, 0, 1));
                  skipping=false;
               }
               catch (Exception e) {
                 lengthSoFar=my_in.read(length, 0, 1);
               }
              
            }
            if (lengthSoFar == -1)
               throw new EOFException();

            // Now read the rest of the length field.
            if ((lengthSoFar=my_in.read(length, 1, 4)) == -1)
                throw new EOFException();
            
            try {
               recordLength = Integer.parseInt(new String(length));
            }
            catch (Exception e) {}
  
            if (recordLength > 0) {
               record = new byte[recordLength];
               System.arraycopy(length, 0, record, 0, length.length);
            }
            
            if ((my_in.read(record, 5, recordLength)) == -1)
                throw new EOFException();

             if (debug)
               System.out.println(Util.hexDump(record, recordLength, "data"));

	}
	
/**
* @return recordStatus char
*/
	public final char recordStatus() { return array[0]; }
/**
* @return recordType char
*/
	public final char recordType() { return array[1]; }
/**
* @return bibliographicLevel char
*/
	public final char bibliographicLevel() { return array[2]; }
/**
* Just here for completeness.
* @return byte8 char
*/
	private final char byte8() { return array[3]; }
/**
* Just here for completeness.
* @return byte9 char
*/
	private final char byte9() { return array[4]; }
/**
* @return encodingLevel char
*/
        public final char encodingLevel() { return array[5]; }
/**
* Just here for completeness.
* @return byte18 char
*/
        private final char byte18() { return array[6]; }
/**
* Just here for completeness.
* @return byte19 char
*/
        private final char byte19() { return  array[7]; }
	
/**
* @return char[] containing dateRecordEntered
*/
	public final char[] dateRecordEntered() { 
	    char d[] = new char[8];
	    System.arraycopy(array, 0, d, 0, 8);
	    return d;
	}
/**
* @return byteBeforeDate1 char
*/
	public final char byteBeforeDate1() { return array[8]; }
/**
* @return char[] containing date1
*/
	public final char[] date1() {
	    char d[] = new char[4];
	    System.arraycopy(array, 9, d, 0, 4);
	    return d;
	}
/**
* @return char[] containing date2
*/
	public final char[] date2() {
            char d[] = new char[4];
            System.arraycopy(array, 13, d, 0, 4);
            return d;
        }
/**
* Just here for completeness.
* @return char[] containing byte15to34
*/
	private final char[] byte15to34() {
            char d[] = new char[20];
            System.arraycopy(array, 17, d, 0, 20);
            return d;
        }
/**
* @return char[] containing languageIndex
*/
	public final char[] languageIndex() {
            char d[] = new char[3];
            System.arraycopy(array, 37, d, 0, 3);
            return d;
        }
/**
* @return modifiedRecordIndicator char
*/
	public final char modifiedRecordIndicator() { return array[40]; }
/**
* @return catalogingSourceCode char
*/
	public final char catalogingSourceCode() { return array[41]; }

/**
 * Method to format a marc record into something suitable for display.
 * @param begrec Marc record.
 * @param linelen Desired length of formatted lines.
 * @return String containing formatted record.
 */
	public static final String formatmarc(String begrec, int linelen) {
	    int currentPos, fieldlen, fldid, headerlen, i, offset, reclen;
	    int begdata, len, thisLineLen, endField;
	    int indent = 7;
	    String sFldid;
	    StringBuffer newrec;
	    char sfld_char;
	    
	    reclen = Integer.parseInt(begrec.substring(0,5));
	    headerlen = Integer.parseInt(begrec.substring(12,17));

	    currentPos = 24;
	    begdata = headerlen;
	    newrec = new StringBuffer(begrec.length() * 3);

     
	    while (currentPos < begdata-1)
	    {
		fldid = Integer.parseInt(begrec.substring(currentPos,
		    currentPos+3));
		newrec.append(begrec.substring(currentPos, currentPos+3));
		currentPos += 3;

		fieldlen = Integer.parseInt(begrec.substring(currentPos,
		    currentPos+4));
		currentPos += 4;
		offset = Integer.parseInt(begrec.substring(currentPos,
		    currentPos+5));
		offset += begdata;
		currentPos += 5;

		if (fldid < 10) // fixed length fields
	 	{
		    // fieldlen - 1 to drop the FieldTerminator
                    newrec.append(": "); 
		    newrec.append(begrec.substring(offset,offset+(fieldlen-1)));
		}
		else
		{
		    // first, indicators
                    newrec.append(": "); 

		    for (i=offset; i<reclen && 
			 begrec.charAt(i) != MarcUtil.SUBFIELD_DELIMITER; i++)
			; // empty loop

		    if (i==reclen) // hit the end of the record
			break;

		    if (i != offset) // found some indicators
			newrec.append(begrec.substring(offset, offset+2) + " ");

		    thisLineLen = indent;

                    endField = -1;

		    while (begrec.charAt(i) == MarcUtil.SUBFIELD_DELIMITER && 
			   i < reclen)
		    {

			i++; // skip over delimiter
			sfld_char = begrec.charAt(i++); // skip over sf tag
                        
                        for(endField=i;  endField<reclen &&
                          begrec.charAt(endField) != MarcUtil.FIELD_TERMINATOR &&
                          begrec.charAt(endField) != MarcUtil.SUBFIELD_DELIMITER &&
                          begrec.charAt(endField) != MarcUtil.RECORD_TERMINATOR; endField++);

			if (endField == reclen)
			    break; // hit end of record

			len = endField - i;

			if (len > 0)
			{
			    if (thisLineLen+3 > linelen)
			    {
				newrec.append("\n        $" + sfld_char);
				thisLineLen = indent+3;
			    }
			    else
			    {
				newrec.append(" $" + sfld_char);
				thisLineLen += 3;
			    }

			    if (len+thisLineLen > linelen)
			    {
				if (thisLineLen < linelen)
				{
				    newrec.append(begrec.substring(i,
					i+(linelen-thisLineLen)) + "\n");
				    i += linelen-thisLineLen;
				    len -= linelen-thisLineLen;
				}
				else
				    newrec.append("\n");

				thisLineLen = indent + 1;
				while (len+thisLineLen > linelen)
				{
				    newrec.append("        " +
					begrec.substring(i, 	
					    i+(linelen-thisLineLen)) + "\n");
				    i += linelen-thisLineLen;
                                    len -= linelen-thisLineLen;
				}
				newrec.append("        " + begrec.substring(i, 
				    i+len));
				thisLineLen = len+indent+1;
			    }
			    else
			    {
				newrec.append(begrec.substring(i, i+len));
				thisLineLen += len;
			    }
			}
                       i=endField;
		    }
		}
		newrec.append("\n");
	    }
	    return newrec.toString();
	}

/**
 * Method to format a marc record into BER. This allows overcite to be
 * used for displaying marc records.
 * @param begrec Marc record.
 * @param dir parent node of DataDir to hold BER record.
 * @param frequencyEncode flag to turn on frequency encoding of marc tags.
 * @return true if record was converted, false if errors occurred.
 */
    static public boolean marc2dir(String begrec, DataDir dir, 
	boolean frequencyEncode) {
    	int       begdata, dataOffset, endrec, len, offset, t;
    	DataDir   subdir;
    	DIRECTORY recdir;

	if (begrec == null || dir == null)
            return false;

        try {
          endrec = Integer.parseInt(begrec.substring(0, 5));
	  begdata = Integer.parseInt(begrec.substring(12, 17));
        }
        catch (Exception e) {
           return false;
        }

        // first, move the useful leader stuff into field 000 
	String field_0 = new String(begrec.substring(5, 10) +
	    begrec.substring(17, 20));
        dir.add(0, ASN1.CONTEXT, field_0);

	for (offset = 24; offset < begdata - 1; offset += 12)
        // begdata-1 to avoid getting the FT at end of directory 
    	{
  	    recdir = new DIRECTORY(begrec, offset);
            if (recdir.tag == -1) 
              break;

  	    dataOffset = recdir.offset + begdata;
	    
            // fixed length fields .. turns out that different marc 
            // formats don't have subfields.. regular marc=fields 0-10
            // unimarc=fields 0-1 
            if(begrec.charAt(dataOffset+2) != MarcUtil.SUBFIELD_DELIMITER) 
            {
            	dir.add(
		    frequencyEncode?
		    MarcUtil.marcToFrequency[recdir.tag]:recdir.tag, 
                    ASN1.CONTEXT, 
		    begrec.substring(dataOffset, dataOffset+recdir.length - 1));
                // -1 to drop the FieldTerminator 
            }
            else
            {
            	subdir=null;
            	// first, indicators 
		for (len=0, t=dataOffset; 
		    begrec.charAt(t) != MarcUtil.SUBFIELD_DELIMITER && t < endrec;
		    len++, t++)
		    ;
                if (t==endrec)
                    return false;
            	if (len != 0)
            	{
                    subdir=dir.add(
			frequencyEncode?
			MarcUtil.marcToFrequency[recdir.tag]:
			recdir.tag, ASN1.CONTEXT);
                    subdir.add(0, ASN1.CONTEXT, 
			begrec.substring(dataOffset, dataOffset+len));
            	}
            	while(begrec.charAt(t) == MarcUtil.SUBFIELD_DELIMITER && t<endrec)
            	{
                    t++;  // skip over delimiter 
                    char sfld_char = MarcUtil.sub_tag(begrec, t);
                    t++;  // skip over subfield tag 
                    for(len=0, dataOffset=t; t<endrec && 
			begrec.charAt(t) != MarcUtil.FIELD_TERMINATOR &&
                        begrec.charAt(t) != MarcUtil.SUBFIELD_DELIMITER &&
                        begrec.charAt(t) != MarcUtil.RECORD_TERMINATOR; len++, t++)
			;
                    if (t==endrec)
                        return false;
                    if (len != 0)
                    {
                    	if (subdir == null)
                            subdir = dir.add(
                            	frequencyEncode?
			  	MarcUtil.marcToFrequency[recdir.tag]:
				recdir.tag, ASN1.CONTEXT);
                    	subdir.add(sfld_char, ASN1.CONTEXT, 
			    begrec.substring(dataOffset, dataOffset+len));
                    }
            	}
            }
    	}
    	return true;
    }

}

class DIRECTORY {
    int tag;
    int length;
    int offset;

    DIRECTORY(String rec, int offset) {

      if (rec.charAt(offset) ==  MarcUtil.FIELD_TERMINATOR) {
        tag = -1;
        return;
      }

      tag = Integer.parseInt(rec.substring(offset, offset+3));
      length = Integer.parseInt(rec.substring(offset+3, offset+7));
      this.offset = Integer.parseInt(rec.substring(offset+7, offset+12));
    }
}
