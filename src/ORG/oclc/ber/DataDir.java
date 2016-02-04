/* 
 * DataDir.java
 *
 * (c)1998 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
 *  Ohio 43017-0702.  OCLC is a registered trademark of OCLC Online Computer
 *  Library Center, Inc.
 *    
 *  NOTICE TO USERS:  The BER Utilities ("Software") has been developed by OCLC
 *  Online Computer Library Center, Inc.  Subject to the terms and conditions 
 *  set forth below, OCLC grants to user a perpetual, non-exclusive, 
 *  royalty-free license to use, reproduce, alter, modify, and create 
 *  derivative works from Software, and to sublicense Software subject to the 
 *  following terms and conditions:
 *  
 *  SOFTWARE IS PROVIDED AS IS.  OCLC MAKES NO WARRANTIES, REPRESENTATIONS, OR
 *  GUARANTEES WHETHER EXPRESS OR IMPLIED REGARDING SOFTWARE, ITS FITNESS FOR 
 *  ANY PARTICULAR PURPOSE, OR THE ACCURACY OF THE INFORMATION CONTAINED 
 *  THEREIN.
 *  
 *  User agrees that OCLC shall have no liability to user arising therefrom,
 *  regardless of the basis of the action, including liability for special,
 *  consequential, exemplary, or incidental damages, including lost profits,
 *  even if it has been advised of the possibility thereof.
 *  
 *  User shall cause the copyright notice of OCLC to appear on all copies of
 *  Software, including derivative works made therefrom.
 *****************************************************************************/

package ORG.oclc.ber;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Vector;

import ORG.oclc.util.Util;

/** DataDir is a class for manipulating tree structures and for putting data
 * into and getting data out of tree structures. 
 * <p>
 * Example: Create a DBI
 * Begin Session request and get the session id from the response. This
 * example assumes methods to send requests and get responses from a DBI
 * Server.
 * </p>
 <pre>
  BerString ber_record;
  DataDir dir, subdir;
  int sessid;
  //
  dir = new DataDir(DBI.DBI_IN, ASN1.APPLICATION);  // create DataDir
  subdir = dir.add(DBI.BEGINS, ASN1.CONTEXT);       // make it a Begin Session
  subdir.add(DBI.BS_USERID, ASN1.CONTEXT, "ralph"); // add userid
  ber_record = new BerString(dir);                  // make BER record
  //
  send_request(ber_record);
  ber_record = get_response();
  //
  dir = new DataDir(ber_record);
  subdir = dir.child();       // skip over DBI_OUT node
  subdir = subdir.child();    // skip over BEGINS node
  while (subdir != null)      // find node with tag==SESSID
  {
   if (subdir.fldid() == DBI.SESSID)
   {
    sessid = subdir.getInt();
    break;
   }
   subdir = subdir.next();    // point to sibling of this node
  }
 </pre>
 * @see ASN1
 */
public class DataDir extends ASN1 {
    
    DataDir child;       // first (or left-most) child
    DataDir last_child;  // last (or right-most) child
    DataDir parent;
    DataDir next;
    DataDir prev;
    DataDir lastChildFound;  // used by the find and findNext methods
    
    DataDirObject object;
    long number;
    
    public int fldid;
    public int asn1class;
    int form;
    int length;
    

    /*     Ralphs's new variables... */
    public Object obj=null; //A holder so we don't have to have parallel trees
    byte   byteDataSource[]=null;
    char   charDataSource[]=null;
    String stringDataSource=null;
    int    dataOffset=0;
    int    count;
    String byteEncoding;

    private static final byte mask[] = {(byte)0x80, (byte)0x40, (byte)0x20, 
                                        (byte)0x10, (byte)0x08, (byte)0x04, 
                                        (byte)0x02, (byte)0x01};
    
    private static final Hashtable putoids = new Hashtable(10);
    private static final Hashtable getoids = new Hashtable(10);
    
    
    protected DataDir() { // DataDirTree needed this one
    }

    
    /**
     * Create a root (non-leaf and no parent) DataDir with the specified fldid
     * and asn1class.
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * 
     * @deprecated see see DataDir(int, int)
     */
    public DataDir(int fldid, byte asn1class) {
        this(fldid, (int)asn1class);
    }
    
    
    /**
     * Create a root (non-leaf and no parent) DataDir with the specified fldid
     * and asn1class.
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     */
    public DataDir(int fldid, int asn1class) {
        this.fldid = fldid;
        this.asn1class = asn1class;
        form = CONSTRUCTED;
    }
    

  /**
   * Create a leaf (with no parent) DataDir with the specified fldid,
   * asn1class, and data.
   *
   * This node can be added to a tree with the add method from a node already
   * on the tree.
   * @param fldid     tag to be assigned to new DataDir
   * @param asn1class class to be assigned to new DataDir
   * @param obj       data to be put into the new DataDir
   */
  public DataDir(int fldid, int asn1class, long num) {
        this.fldid = fldid;
        this.asn1class = asn1class;
        form = PRIMITIVE;
        replace(num);
  }

  /**
   * Create a leaf (with no parent) DataDir with the specified fldid,
   * asn1class, and data.  Only String, and byte[] are supported.  All
   * other data will be ignored and can be put into the DataDir with
   * a replace method.
   *
   * This node can be added to a tree with the add method from a node already
   * on the tree.
   * @param fldid     tag to be assigned to new DataDir
   * @param asn1class class to be assigned to new DataDir
   * @param obj       data to be put into the new DataDir
   */
  public DataDir(int fldid, int asn1class, Object obj) {
        this.fldid = fldid;
        this.asn1class = asn1class;
        form = PRIMITIVE;
        if(obj instanceof byte[])
            replace((byte[])obj);
        else if(obj instanceof String)
            replace((String)obj);
  }

    /**
     * Create a non-leaf node that will be a child of the specified parent.
     * @param fldid     tag to be assigned to new node
     * @param asn1class class to be assigned to new node
     * @deprecated see see DataDir(DataDir, int, int)
     */
    public DataDir(DataDir parent_dir, int fldid, byte asn1class) {
        this(parent_dir, fldid, (int)asn1class);
    }

    
    /**
     * Create a non-leaf node that will be a child of the specified parent.
     * @param fldid     tag to be assigned to new node
     * @param asn1class class to be assigned to new node
     */
    public DataDir(DataDir parent_dir, int fldid, int asn1class) {
        // cannot return null from constructor. figure out how
        // to do this!!!!
        // if (parent.form == PRIMITIVE)
        // return null;
        
        this.fldid = fldid;
        this.asn1class = asn1class;
        form = CONSTRUCTED;
        
        parent = parent_dir;
        if (parent_dir!=null){
            parent_dir.count++;

            if (parent_dir.child == null)
                parent_dir.child = parent_dir.last_child = this;
            else {
                this.prev = parent_dir.last_child;
                parent_dir.last_child.next = this;
                parent_dir.last_child = this;
            }
        }
    }
    
    
    /**
     * Create a DataDir tree from a BER record.
     * @param record BER record
     */
    public DataDir(BerString record) {
        int lenlen     = 0;
        int nodesize   = 0;
        int size       = 0;
        int fieldlen[] = new int[1];
        int taglen[]   = new int[1];
        int tfldlen    = 0;
        
        record.offset = 0;
        
        if ((byte)(record.record[record.offset]&0x20) == 0x20)
            form = CONSTRUCTED;
        else
            form = PRIMITIVE; 
                
        asn1class = (byte)((record.record[record.offset]&0xff) >> 6);
        fldid = record.getTag(taglen);
        
        lenlen = record.getLen(fieldlen);
        
        if (fieldlen[0] == -1) {
            if (record.IsCompleteBER( 0x7fffffff, fieldlen)) {
                fieldlen[0] -= (taglen[0]+lenlen+2);
                // -= 2;  // don't include trailing nulls in loop later
            }
       }        
        
        tfldlen = fieldlen[0];
        if (form == PRIMITIVE) {
            byteDataSource = record.record;
            dataOffset     = record.offset;
            count          = tfldlen;
            record.offset += tfldlen;

            return;
        }
        
        size = record.offset + tfldlen;
        while (record.offset < size) {
            // bldNode does not need this fieldLen or tagLen
            // but it was allocating lots more of them and this
            // save alots of 'new's!
            bldNode(record, fieldlen, taglen);
        }
    }
    
    
    /**
     * Add a DataDir as the last child of this one.
     * @param newdir DataDir to be added
     */
    public final DataDir add(DataDir newdir) {
        return add(newdir, count);  // add as the last child
    }
    
    
    /**
     * Add a DataDir as a child of this one at a specified offset.
     * @param newdir DataDir to be added
     * @param offset 0=first child; >0 = position; >number of children = last
     */
    public final DataDir add(DataDir newdir, int offset) {
        if (form == PRIMITIVE)
            return null;
        
        if (newdir.next != null) {
            try {
                throw new Exception("DataDir.add(): newdir has inappropriate siblings");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (child != null) {
            if (offset == 0) {
                newdir.next = child;
                child.prev  = newdir;
                child       = newdir;
            } else if (offset >= count) {
                last_child.next = newdir;
                newdir.prev = last_child;
                last_child = newdir;
            } else {
                DataDir pchild = null;

                for(pchild=child; offset > 0; offset--)
                    pchild = pchild.next;

                pchild.prev.next = newdir;
                newdir.prev = pchild.prev;
                pchild.prev = newdir;
                newdir.next = pchild;
            }
        } else {
            last_child = child = newdir;
            newdir.prev = null;
        }

        count++;
        newdir.parent = this;

        return newdir;
    }
    
    
    /**
     * Add a non-leaf child of this DataDir.
     * 
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     */
    public DataDir add(int fldid, int asn1class) {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        newdir = new DataDir(fldid, asn1class);
        newdir.parent = this;

        if (child==null)
            child=last_child=newdir;
        else {
            last_child.next=newdir;
            newdir.prev=last_child;
            last_child=newdir;
        }
        count++;

        return newdir;
    }
    
    
    /**
     * Add a leaf DataDir to the directory with an array of 8-bit byte data.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param data      String reference 
     */
    public final DataDir add(int fldid, int asn1class, byte[] data) {
        return add(fldid, asn1class, data, 0, data.length);
    }
    
    
    /**
     * Add a leaf DataDir to the directory with data contained in a byte array.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param byte[]    data array containing bytes to be added
     * @param int       offset into array
     * @param int       len of data to be added
     */
    public final DataDir add(int fldid, int asn1class, byte[] data,
                             int offset, int len) {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        newdir = add(fldid, asn1class);
        newdir.form = PRIMITIVE;
        newdir.byteDataSource = data;
        newdir.dataOffset = offset;
        newdir.count = len;

        return newdir;
    }
    
    
    /**
     * Add a leaf DataDir to the directory with INTEGER data.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param num       int to be added
     */
    public final DataDir add(int fldid, int asn1class, int num) {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        newdir = add(fldid, asn1class);
        newdir.form   = PRIMITIVE;
        newdir.number = num;
        newdir.count  = numLen(num);
        newdir.object = null;

        return newdir;
    }
    
    
    /**
     * Add a leaf DataDir to the directory with LONG data.
     * 
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param num       long to be added
     */
    public final DataDir add(int fldid, int asn1class, long num) {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        newdir = add(fldid, asn1class);
        newdir.form   = PRIMITIVE;
        newdir.number = num;
        newdir.count  = numLen(num);
        newdir.object = null;

        return newdir;
    }
    
    
    /**
     * Add a leaf DataDir to the directory with DataDirObject data.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param obj       DataDirObject to be added
     */
    public DataDir add(int fldid, int asn1class, DataDirObject obj) {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        newdir = add(fldid, asn1class);
        newdir.form   = PRIMITIVE;
        newdir.object = obj;
        newdir.count  = obj.length();

        return newdir;
    }
    
    
    /**
     * Add a leaf DataDir to the directory with String data.  Render 
     * the String to bytes using the system default encoding.
     * 
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param data      String to be added
     */
    public final DataDir add(int fldid, int asn1class, String data) {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        newdir = add(fldid, asn1class);
        newdir.form             = PRIMITIVE;
        newdir.stringDataSource = data;
        newdir.byteEncoding     = "local";

        if (data != null) {
            newdir.byteDataSource = data.getBytes();
            newdir.count          = newdir.byteDataSource.length;
        } else {
            newdir.byteDataSource = new byte[1];
            newdir.count          = 0;
        }

        return newdir;
    }
    
    
    /**
     * Add a leaf DataDir to the directory with String data.  Render 
     * the String to bytes using the system default encoding.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param data      String to be added
     * @param enc       byte encoding to use
     *
     * @exception java.io.UnsupportedEncodingException thrown by the
     * String.getBytes() method
     *
     * @see java.lang.String#getBytes
     */
    public final DataDir add(int fldid, int asn1class, String data,
                             String enc) throws UnsupportedEncodingException {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        newdir = add(fldid, asn1class);
        newdir.form             = PRIMITIVE;
        newdir.stringDataSource = data;

        if (data != null) {
            newdir.byteDataSource = data.getBytes(enc);
            byteEncoding          = enc;
            newdir.count          = newdir.byteDataSource.length;
        } else {
            newdir.byteDataSource = new byte[1];
            newdir.count          = 0;
        }

        return newdir;
    }
    
    
    /**
     * Add a leaf DataDir which contains a BITSTRING to a directory.
     * NOTE: Any of the characters '1', 'y', 'Y', 't' or 'T' get a 1 in the
     * encoded bitstring.  Any other characters get a 0.
     * 
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param bits      String containing array of bits.
     */
    public final DataDir addBits(int fldid, int asn1class, String bits) {
        
        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        int len      = bits.length();
        byte bytes[] = new byte[1 + len/8 + ((len%8)>0?1:0)];
        int offset   = 0;
        byte unused  = (byte)(8-len%8);
        
        if (unused == 8)
            unused = 0;
        
        bytes[offset++]=unused;
        for (int i=0; i<len; i+=8, offset++) {
            bytes[offset] = 0;
            for (int j=0; j<8 && i+j<len; j++)
                if (bits.charAt(i+j)=='y' || bits.charAt(i+j)=='Y' ||
                    bits.charAt(i+j)=='t' || bits.charAt(i+j)=='T' ||
                    bits.charAt(i+j)=='1')
                    bytes[offset] |= mask[j];
        }

        return add(fldid, asn1class, bytes, 0, bytes.length);
    }
    
    
    /**
     * Add a leaf DataDir to a directory which contains an OID.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param cstring   String containing human readable OID
     */
    public final DataDir addOID(int fldid, int asn1class, String OID) {
        int OID_offset = 0;
        int dot        = 0;
        int offset     = 0;
        int value      = 0;
        byte place[]   = null;
        byte ptr[]     = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;
                
        if ((ptr = (byte[])putoids.get(OID)) == null) {
            place = new byte[100];
            while (OID_offset < OID.length() &&
                   Character.isDigit(OID.charAt(OID_offset)) == true) {
                if (offset > 90) // too large
                    return null;
                
                dot = OID.indexOf('.', OID_offset);
                // was dot = dot == -1 ? OID.length() : dot;
                if (dot == -1)
                    dot = OID.length();

                value = Integer.parseInt(OID.substring(OID_offset, dot));
                
                if (offset==0) {  // 1st two are special
                    if (dot == -1)
                        return null; // can't be this short
                    OID_offset = dot+1; // skip past '.'
                    
                    dot = OID.indexOf('.', OID_offset);
                    //was dot = dot == -1 ? OID.length() : dot;
                    if (dot == -1)
                        dot = OID.length();
                    
                    value = value * 40 +
                        Integer.parseInt(OID.substring(OID_offset,dot));
                }
                
                if (value >= 0x80) {
                    int count = 0;
                    byte bits[] = new byte[12]; // save a 84 (12*7) bit number
                    
                    while (value != 0) {
                        bits[count++] = (byte)(value & 0x7f);
                        value >>= 7;
                    }

                    // Now place in the correct order
                    while (--count > 0)
                        place[offset++] = (byte)(bits[count] | 0x80);
                    
                    place[offset++] = bits[count];
                } else
                    place[offset++] = (byte)value;
                
                dot = OID.indexOf('.', OID_offset);
                if (dot != -1)
                    OID_offset = dot+1;
                else
                    OID_offset = OID.length();
            }
            
            ptr = new byte[offset];
            System.arraycopy(place,0,ptr,0,offset);
            putoids.put(OID, ptr);
        }

        return add(fldid, asn1class, ptr, 0, ptr.length);
    }
    
    
    /**
     * Add a leaf DataDir to the directory with String data.  Render 
     * the String to bytes using the UTF-8 encoding.  The advantage of 
     * using this over the simple add method for strings is that this 
     * method does not throw the UnsupportedEncodingException.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param chars     char array to be added
     * @param offset    index into array of first character to be added
     * @param length    number of characters to be added
     */
    public final DataDir addUTF(int fldid, int asn1class, char[] chars,
      int offset, int length) {
        DataDir newdir = null;

        if (form == PRIMITIVE)  // can't add a child to a leaf node
            return null;

        newdir = add(fldid, asn1class);
        newdir.form             = PRIMITIVE;
        newdir.stringDataSource = null;
        newdir.byteEncoding     = "UTF8";

        if(chars!=null) {

            char[] newChars = Util.fromBars(chars, offset, length);
            if(newChars!=chars) {
                offset=0;
                length=newChars.length;
                chars=newChars;
            }

            newdir.byteDataSource = new byte[length*3];
            newdir.count = Util.encodeUtf8(chars, offset, length, newdir.byteDataSource, 0, length * 3);
        } else {
            newdir.byteDataSource = new byte[1];
            newdir.count          = 0;
        }
        return newdir;
    }

    /**
     * Add a leaf DataDir to the directory with String data.  Render 
     * the String to bytes using the UTF-8 encoding.  The advantage of 
     * using this over the simple add method for strings is that this 
     * method does not throw the UnsupportedEncodingException.
     *
     * @param fldid     tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param data      String to be added
     */
    public final DataDir addUTF(int fldid, int asn1class, String data) {
        if(data==null)
            return addUTF(fldid, asn1class, (char[])null, 0, 0);
        char[] chars = data.toCharArray();
        return addUTF(fldid, asn1class, chars, 0, chars.length);
    }
    
    
    /**
     * Assembles a BER record from a DataDir into a BerString object.
     * NOTE: recLen() must be called immediately before calling asmRec().
     * 
     * recLen() sets information in the DataDir that is critical for the
     * function of asmRec(). recLen() also returns the length needed to
     * construct the BerString object.
     * 
     * @param record object to hold BER record
     */
    public final void asmRec(BerString record) {
        DataDir pchild = null;
        
        record.putTag(fldid, (byte)asn1class, (byte)form);
        record.putLen(length);

        if (form == CONSTRUCTED)
            for (pchild = child; pchild != null; pchild = pchild.next)
                pchild.asmRec(record);
        else {
            if (byteDataSource == null) {
                if (object != null)
                    record.putChar(object.toByteArray(), object.length());
                else 
                    record.putNumber(number);
            } else
                record.putChar(byteDataSource, dataOffset, count);
        }
    }
    
    
    private void bldNode(BerString record, int fieldlen[], int taglen[]) {
        int asn1class; 
        byte b;
        int fldid; 
        int lenlen; 
        int size;
        int tfldlen;
        int oldoffset;
        
        b         = record.record[record.offset];
        asn1class = (byte)((b&0xff) >> 6);
        //asn1class = (int) record.record[record.offset];

        
        oldoffset = record.offset;      
        fldid     = record.getTag(taglen);
        lenlen    = record.getLen(fieldlen);
        
        tfldlen = fieldlen[0];
        if (tfldlen == -1) {
            if (record.IsCompleteBER(oldoffset, 0x7fffffff, fieldlen)) {
                //System.out.println("Indefinte record setting: " + 
                // record.indefinite);
                fieldlen[0] -= (taglen[0]+lenlen+2);
                // -= 2;  // don't include trailing nulls in loop later
            }
            tfldlen = fieldlen[0];  // re-set local copy
        }
        
        if (record.offset+tfldlen > record.record.length) {
            record.offset += tfldlen;
            return;
        }
        
        if ((b & 0x20) == 0) {  // PRIMITIVE            
            if (b!=0 || tfldlen > 0) // not end end-of-indefinite-legth flag
                add(fldid, asn1class, record.record, record.offset, tfldlen);
            record.offset += tfldlen;
        } else if (tfldlen > 0) {
            DataDir newdir = add(fldid, asn1class);

            size = record.offset + tfldlen;
            while (record.offset < size)
                newdir.bldNode(record, fieldlen, taglen);
        }
    }
    
    
    /**
     * Clone the DataDir and all it's children and siblings. It will clone all
     * data referenced by the DataDirs.
     * 
     * @return cloned DataDir
     */
    public final Object clone() {
        return clone(null, true);
    }
    
    
    /**
     * Clone the DataDir and all it's children. doSibs indicates whether 
     * to do the siblings of the DataDir.
     * 
     * @param doSibs true = clone siblings; false = do not clone siblings
     * @return cloned DataDir
     */
    public final Object clone(boolean doSibs) {
        return clone(null, doSibs);
    }
    
    
    private final Object clone(DataDir parent, boolean doSibs) {
        DataDir newdir    = null; 
        DataDir pchild    = null; 
        DataDir tmpParent = null;
        
        if (form == CONSTRUCTED) {
            if(parent==null)
                newdir = new DataDir(fldid, asn1class);
            else
                newdir = parent.add(fldid, asn1class);

            for (pchild = child; pchild != null; pchild = pchild.next)
                pchild.clone(newdir, doSibs);
            
            if (parent == null && next != null && doSibs) {
                tmpParent = new DataDir(fldid, asn1class);
                tmpParent.daddDir(newdir);

                // Add siblings
                for (pchild=next; pchild != null; pchild=pchild.next)
                    pchild.clone(tmpParent, doSibs);

                // remove artificial parent
                for (pchild = newdir; pchild != null; pchild = pchild.next)
                    pchild.parent = null;
            }
        } else {
           if (byteDataSource == null) {
              if (object == null)
                 if (parent != null)
                    newdir = parent.add(fldid, asn1class, getLong());
                 else
                    newdir = new DataDir(fldid, asn1class, getLong());
              else
                 if (parent != null)
                    newdir = parent.add(fldid, asn1class, 
                                        (DataDirObject)object.clone());
                 else
                    newdir = new DataDir(fldid, asn1class,
                                         (DataDirObject)object.clone());
           } else {
              if (count > 0)
                 if (parent != null)
                    newdir = parent.add(fldid, asn1class, data());
                 else
                    newdir = new DataDir(fldid, asn1class, data());
              else 
                 if (parent != null)
                    newdir = parent.add(fldid, asn1class, (String)null);
                 else
                    newdir = new DataDir(fldid, asn1class, (String)null);
           }
        }
        
        return newdir;
    }


    /** Compares this DataDir's data array to the specified DataDir's data 
     * array. 
     * The result is true if and only if the argument is not
     * null and is a DataDir that contains 
     * the same data array as this DataDir object.
     * 
     * @param that - the DataDir to compare with.
     * @return true if the object's data are the same; false otherwise.
     */
    public boolean dataEquals (DataDir that) {
        boolean result = false;
        byte a[], b[]; 

        if (that == null)
            return result;
        
        if (this.fldid() == that.fldid()) {
            a = this.data();
            b = that.data();
            if (a != null && b!= null && a.length == b.length) {
                //                int i;

                if (a.equals(b)) 
                    return true;
                    
                /*
                for (i = 0; i < a.length; i++) {
                    if (a[i] != b[i]) break;
                }
                if (i == a.length) result = true;
                */
            } else if ( a == null && b == null )
                result = true;
        }

        return result;
    }

    
    
    /**
     * Delete this DataDir and its children from a DataDir tree.  This
     * method does nothing if this DataDir is the root of the DataDir tree.
     */
    public final void delete() {
        if (parent == null)  // this is the root node
            return;
        
        parent.count--;
        if (parent.last_child == this)
            parent.last_child = prev;

        if (parent.child == this)
            parent.child = next;


        if(next!=null)
            next.prev=prev;
         
        if(prev!=null)
            prev.next=next;

        prev = next = parent = null;
    }

    
    /** Returns a new DataDir that contains the difference betweeen the current
     *  DataDir and the input DataDir.
     *
     * @param that
     * @return DataDir
     */
    public DataDir diff (DataDir that) { // order doesn't matter
        // need to put these somewhere more useful
        int INSERT = -11;
        int DELETE = -12;
        int CHANGE = -13;
        DataDir dir = null, d = null;
        DataDir sd = this.child();
        DataDir od = null;

        if (that == null) {
            dir = new DataDir(0, ASN1.APPLICATION);
            d = dir.add(DELETE, ASN1.APPLICATION);
            if (this.form() == ASN1.PRIMITIVE) {
                d.add(this.fldid(), this.asn1class(), this.getString());
            } else {
                d.add((DataDir)this.clone(false));
            }
            //System.out.print(" 1:" + this.fldid());
        } else if (this.fldid() != that.fldid()) {
            dir = new DataDir(0, ASN1.APPLICATION);
            d = dir.add(DELETE, ASN1.APPLICATION);
            if (this.form() == ASN1.PRIMITIVE) {
                d.add(this.fldid(), this.asn1class(), this.getString());
            } else {
                d.add((DataDir)this.clone(false));
            }
            d = dir.add(INSERT, ASN1.APPLICATION);
            if (that.form() == ASN1.PRIMITIVE) {
                d.add(that.fldid(), that.asn1class(), that.getString());
            } else {
                d.add((DataDir)that.clone(false));
            }
            //System.out.print(" 2:" + this.fldid());
        } else if (sd == null) {
            od = that.child();

            // then this and that are both primitives, so compare their data
            if (od == null) { 
                //System.out.print(" 3");
                if (this.dataEquals(that)) { // exact match, do nothing
                    //System.out.print("a:" + this.fldid());
                } else {
                    //System.out.print("b:" + this.fldid());
                    dir = new DataDir(0, ASN1.APPLICATION);
                    d = dir.add(CHANGE, ASN1.APPLICATION);
                    if (this.form() == ASN1.PRIMITIVE) {
                        d.add(this.fldid(), this.asn1class(), 
                              this.getString());
                    } else {
                        d.add((DataDir)this.clone(false));
                    }
                }
            } else {           
                while (od != null) {
                    dir = new DataDir(0, ASN1.APPLICATION);
                    d = dir.add(INSERT, ASN1.APPLICATION);
                    if (od.form() == ASN1.PRIMITIVE) {
                        d.add(od.fldid(), od.asn1class(), od.getString());
                    } else {
                        d.daddDir((DataDir)od.clone(false));
                    }
                    od = od.next();
                }
                //System.out.print(" 4:" + this.fldid());
            }
        } else {
            int last_fldid = -1;
            Vector same_fldid_sibs = new Vector();
            Vector match_candidates = new Vector();
            Hashtable already_processed = new Hashtable();
            DataDir sib = null, umsib;
            while (sd != null) {
                //System.out.print(" sd.fldid:" + sd.fldid());
                //if ((sd.fldid() != last_fldid)) { 
                if((sd.fldid()!=last_fldid) && (already_processed.get(Integer.toString(sd.fldid())) == null)) {

                    already_processed.put(Integer.toString(sd.fldid()), "x");

                    //System.out.print(" 5:" + this.fldid());
                    same_fldid_sibs = new Vector();
                    same_fldid_sibs.addElement(sd);
                    sib = sd.next();
                    while (sib != null) {
                        if (sd.fldid() == sib.fldid()) 
                            same_fldid_sibs.addElement(sib);
                        sib = sib.next;
                    }
                    //System.out.print(" sfs.size:" + same_fldid_sibs.size());
                    match_candidates = new Vector();
                    od = that.child();
                    while (od != null) {
                        if (sd.fldid() == od.fldid()) 
                            match_candidates.addElement(od);
                        od = od.next;
                    }
                    //System.out.print(" mc.size:" + match_candidates.size());
                    if (match_candidates.size() == 0) {
                        //System.out.print(" 6:" + this.fldid());
                        if (dir == null) 
                            dir = new DataDir(0, ASN1.APPLICATION);
                        for (int j = 0; j < same_fldid_sibs.size(); j++) {
                            sib = (DataDir)same_fldid_sibs.elementAt(j);
                            //already_processed.put(sib, "x");
                            d = dir.add(DELETE, ASN1.APPLICATION);
                            if (sib.form() == ASN1.PRIMITIVE) {
                                d.add(sib.fldid(), sib.asn1class(), 
                                      sib.getString());
                            } else {
                                d.add((DataDir)sib.clone(false));
                            }
                        }
                    } else {
                        Vector unmatched_sibs = new Vector();
                        //System.out.print(" 7:" + this.fldid());
                        int i, mcs, uss;
                        DataDir md = null;
                        DataDir match = null;
                        DataDir dif = null;
                        int j;
                        for (i = 0; i < same_fldid_sibs.size(); i++) {
                            sib = (DataDir)same_fldid_sibs.elementAt(i);
                            md = null;
                            match = null;
                            dif = null;
                            mcs = match_candidates.size();
                            for (j = 0; j < mcs; j++) {
                                //System.out.print(" 8:" + this.fldid());
                                md = (DataDir)match_candidates.elementAt(j);
                                dif = sib.diff(md);
                                if (dif == null) { // exact match
                                    //System.out.print(" 9:" + this.fldid());
                                    match= (DataDir)match_candidates.elementAt(j);
                                    match_candidates.removeElementAt(j);
                                    //already_processed.put(sib, "x");
                                    already_processed.put(match, "x");
                                    break;
                                }
                            }
                            //System.out.print(" A:" + this.fldid());
                            if (match == null) { 
                                // no exact match for sib in match_candidates
                                //System.out.print(" B:" + this.fldid());
                                unmatched_sibs.addElement(sib);
                            }
                        }

                        // Put an unmatched candidate together with an 
                        // unmatched sib and log the sib as the previous 
                        // version of a change.
                        // Don't bother trying to determine best matches, just
                        // use their order to pair them up for now.
                        mcs = match_candidates.size();
                        uss = unmatched_sibs.size();
                        for (i = 0; i < uss && i < mcs; i++) {
                            //System.out.print(" C:" + this.fldid());
                            sib = (DataDir)unmatched_sibs.elementAt(0);
                            unmatched_sibs.removeElementAt(0);
                            //already_processed.put(sib, "x");
                            umsib = (DataDir)match_candidates.elementAt(0);
                            match_candidates.removeElementAt(0);
                            already_processed.put(umsib, "x");

                            if (dir == null) 
                                dir = new DataDir(0, ASN1.APPLICATION);
                            d = dir.add(CHANGE, ASN1.APPLICATION);
                            if (sib.form() == ASN1.PRIMITIVE) {
                                d.add(sib.fldid(), sib.asn1class(), 
                                      sib.getString());
                            } else {
                                d.add((DataDir)sib.clone(false));
                            }
                        }
                        if (uss != mcs) { 
                            // Finish off any leftovers in either this or that
                            Vector v = null;
                            int action = 0;
                            if (uss > mcs) {
                                v = unmatched_sibs;
                                action = DELETE;
                                //System.out.print(" D:" + this.fldid());
                            } else {
                                v = match_candidates;
                                action = INSERT;
                                //System.out.print(" E:" + this.fldid());
                            }
                            int sz = v.size();
                            for (i = 0; i < sz; i++) {
                                //System.out.print(" F:" + this.fldid());
                                sib = (DataDir)v.elementAt(0);
                                v.removeElementAt(0);
                                //if (action == DELETE) 
                                //already_processed.put(sib, "x");
                                if (action == INSERT) 
                                    already_processed.put(sib, "x");
                                if (dir == null) 
                                    dir = new DataDir(0, ASN1.APPLICATION);
                                d = dir.add(action, ASN1.APPLICATION);
                                if (sib.form() == ASN1.PRIMITIVE) {
                                    d.add(sib.fldid(), sib.asn1class(), 
                                          sib.getString());
                                } else {
                                    d.daddDir((DataDir)sib.clone(false));
                                }
                            }
                        }
                    }
                }
                last_fldid = sd.fldid();
                sd = sd.next();
            }
            
            od = that.child();
            while (od != null) {
                //System.out.print(" od.fldid:" + od.fldid());
                if (already_processed.get(od) == null) {
                    if (dir == null) 
                        dir = new DataDir(0, ASN1.APPLICATION);
                    d = dir.add(INSERT, ASN1.APPLICATION);
                    if (od.form() == ASN1.PRIMITIVE) {
                        d.add(od.fldid(), od.asn1class(), od.getString());
                    } else {
                        d.add((DataDir)od.clone(false));
                    }
                }
                od = od.next();
            }
        }
        //System.out.println("\n<----- " + this.fldid());
        return dir;
    }


    /** Compares this DataDir to the specified DataDir. 
     * The result is true if and only if the argument is not
     * null and is a DataDir object that contains 
     * the same nodes and data values as this DataDir object.
     * 
     * @param that - the DataDir to compare with.
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals (DataDir that) {
        boolean result = false;
        DataDir thisChd = this.child();
        DataDir thatChd = that.child();
        DataDir thisSib = null, thatSib = null;

        //      if ( ((this == null) &&) ||
        if (that == null)
            return result;

        if (thisChd == null && thatChd == null && this.dataEquals(that))
            result = true;
        else if (thisChd != null) {
            thisSib = thisChd.next();
            if (thatChd != null) {
                thatSib = thatChd.next();
                result = thisChd.equals(thatChd) ;
            }
        }

        while (thisSib != null && thatSib != null) {
            if ((result=thisSib.equals(thatSib)) == false)
                break;
            thisSib = thisSib.next;
            thatSib = thatSib.next;
        }

        return result;
    }


    /**
     * Finds a child DataDir of this DataDir.
     *
     * @param fldid     tag of DataDir to be found
     * 
     * @return DataDir if found, null if not found
     */
    public final DataDir find(int fldid) {
        return find(fldid, -1, 1);
    }


    /**
     * Finds a child DataDir of this DataDir.
     *
     * @param fldid     tag of DataDir to be found
     * @param asn1class class of DataDir to be found
     * 
     * @return DataDir if found, null if not found
     */
    public final DataDir find(int fldid, int asn1class) {
        return find(fldid, asn1class, 1);
    }


    
    /**
     * Finds a child DataDir of this DataDir.
     *
     * @param fldid     tag of DataDir to be found
     * @param asn1class class of DataDir to be found
     * @param occurrence number of occrrences of the DataDir to be found
     * 
     * @return DataDir if found, null if not found
     */
    public final DataDir find(int fldid, int asn1class, int occurrence) {
        if (child == null)
            return null;

        if (occurrence >= 0) { 
           for (lastChildFound=child; lastChildFound != null; 
                lastChildFound=lastChildFound.next) {
              if (lastChildFound.fldid==fldid &&
                 (lastChildFound.asn1class==asn1class || (asn1class == -1)) &&
                  (--occurrence <= 0)) {
                  break;
              }
           }
        } else {
           for (lastChildFound=last_child; lastChildFound != null; 
                lastChildFound=lastChildFound.prev) {
              if (lastChildFound.fldid==fldid &&
                 (lastChildFound.asn1class==asn1class || (asn1class == -1)) &&
                  (++occurrence >= 0)) {
                  break;
              }
           }
        }

        if ((lastChildFound != null) && (lastChildFound.fldid==fldid) &&
            ((lastChildFound.asn1class==asn1class) || (asn1class == -1)) ) {
            return lastChildFound;
        }

        return null;
    }
    
    
    /**
     * Finds a child DataDir of this DataDir, continuing from where the last
     * find or findNext method left off it's search.
     * 
     * @param fldid     tag of DataDir to be found
     * @param asn1class class of DataDir to be found
     *
     * @return DataDir if found, null if not found
     */
    public final DataDir findNext(int fldid, int asn1class) {
        if (lastChildFound == null)
            return null;
        
        for (lastChildFound=lastChildFound.next; lastChildFound != null;
             lastChildFound=lastChildFound.next)
            if (lastChildFound.fldid==fldid &&
                lastChildFound.asn1class==asn1class)
                break;

        return lastChildFound;
    }
    
    
    /**
     * Get a BITSTRING from a leaf DataDir.
     *
     * @return A String with 'y' for 1 bits and 'n' for 0 bits.
     */
    public final String getBits() {
        StringBuffer bits = new StringBuffer();
        int last_used = 8 - (byteDataSource[dataOffset+0] & 0xff);
        int i = 0;
        
        for (i=1; i<count-1; i++)
            for (int j=0; j<8; j++)
                bits.append((byteDataSource[dataOffset+i] & mask[j])!=0
                            ?"y":"n");
                            
        for (int j=0; j<last_used; j++)
            bits.append((byteDataSource[dataOffset+i] & mask[j])!=0?"y":"n");

        return bits.toString();
    }
    
    
    /**
     * Get an array of bytes from a leaf DataDir.
     * 
     * @return byte[]
     */
    public final byte[] getBytes() {
        byte[] bytes=new byte[count];

        System.arraycopy(byteDataSource, dataOffset, bytes, 0, count);

        return bytes;
    }
    
    
    /**
     * Get an int from a leaf DataDir.
     * 
     * @return value of int data. Undefined if not a leaf DataDir or if leaf
     *         DataDir doesn't contain int data.
     *
     * @exception java.lang.ArithmeticException thrown when number is too large
     *            to fit into an int.
     */
    public final int getInt() {
        if (byteDataSource != null)
            number = getNum(byteDataSource, dataOffset, count);

        if (number>Integer.MAX_VALUE)
            throw new ArithmeticException("Tried to cast long value of "+
                                          number+" to an integer");
        return (int)number;
    }
    
    
    /**
     * Get a long from a leaf DataDir.
     *
     * @return value of long data. Undefined if not a leaf DataDir or if leaf
     *         DataDir doesn't contain long data.
     */
    public final long getLong() {
        if (byteDataSource != null)
            return getLong(byteDataSource, dataOffset, count);

        return number;
    }
    
    
    /**
     * Get a human readable OID from a leaf DataDir.
     *
     * @return A String with human readable OID
     */
    public final String getOID() {
        String oidBytes;
        String s = null;
        int offset = 0;
        int value;
        StringBuffer oid = new StringBuffer();
        
        oidBytes = new String(byteDataSource, dataOffset, count);
        if ((s = (String)getoids.get(oidBytes)) != null)
            return s;
        
        while (offset < count) {
            value = 0;
            do {
                value <<= 7;
                value |= byteDataSource[dataOffset+offset] & 0x7f;
            } while ((byteDataSource[dataOffset+offset++] & 0x80) != 0);
            
            if (oid.length() == 0)
                oid = oid.append(Integer.toString(value/40) + "." +
                                 Integer.toString(value%40));
            else
                oid = oid.append("." + Integer.toString(value));
        }
        
        s = oid.toString().intern();
        getoids.put(oidBytes, s);

        return s;
    }
    
    
    /**
     * Get a String from a leaf DataDir.
     * 
     * @return String
     */
    public final String getString() {
        if (stringDataSource==null ||
          (byteEncoding!=null && !byteEncoding.equals("local"))) {
            byteEncoding="local";
            stringDataSource = new String(byteDataSource, dataOffset, count);
        }

        return stringDataSource;
    }
    
    
    /**
     * Get a String from a leaf DataDir whose bytes are in a specific encoding.
     * 
     * @param enc String containing byte encoding.
     * @return String
     * 
     * @exception java.io.UnsupportedEncodingException thrown by String 
     *            constructor.
     * 
     * @see java.lang.String
     */
    public final String getString(String enc) 
        throws UnsupportedEncodingException {

        if(stringDataSource==null ||
          (byteEncoding!=null && !byteEncoding.equals(enc))) {
            stringDataSource = new String(byteDataSource, dataOffset, count,
                                          enc);
            byteEncoding=enc;
        }

        return stringDataSource;
    }
    
    
    /**
     * Get a String from a leaf DataDir whose bytes are encoded as UTF8.
     * 
     * @return String
     */
    public final String getUTFString() {
        if(stringDataSource==null ||
          (byteEncoding!=null && !byteEncoding.equals("UTF8"))) {
            stringDataSource = new String(byteDataSource, dataOffset, count, StandardCharsets.UTF_8);
            byteEncoding     = "UTF8";
        }

        return stringDataSource;
    }
    

    /**
     * Insert a new DataDir tree after this DataDir node.
     * @param newDir new tree to be inserted after this node.
     */
    public DataDir insertAfter(DataDir newDir) {
        if (parent == null)
            return null;  // can add a sibling to the root node!

        if (next!=null) {
            next.prev=newDir;
        } else {
            parent.last_child = newDir;
        }
        newDir.next=next;
        newDir.parent=parent;
        newDir.prev=this;
        next=newDir;
        parent.count++;

        return newDir;
    }

    /**
     * Insert a new DataDir tree before this DataDir node.
     * @param newDir new tree to be inserted before this node.
     */
    public DataDir insertBefore(DataDir newDir) {
        if (parent == null)
            return null;  // can add a sibling to the root node!

        if(prev==null)
            parent.child=newDir;
        else
            prev.next=newDir;
        newDir.prev=prev;
        newDir.parent=parent;
        newDir.next=this;
        prev=newDir;
        parent.count++;

        return newDir;
    }

    
    /**
     * Insert a new parent DataDir above this DataDir and all its siblings.
     * 
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     */
    public DataDir insertParent(int fldid, int asn1class) {
        DataDir dir    = null;
        DataDir newDir = null;;
        
        newDir = new DataDir(fldid, asn1class);
        if (parent != null) {
            newDir.child = parent.child;
            parent.child = newDir;
            parent.count = 1;
        } else
            newDir.child = this;

        newDir.parent = parent;
        
        for (newDir.count=0, dir=newDir.child; dir != null; 
             newDir.count++, dir=dir.next)
            dir.parent = newDir;
        
        return newDir;
    }
    
    
    /**
     * Returns the length of the BER record that would be created from the
     * branch of the DataDir tree started at this DataDir.
     * 
     * @return length of record that would be created by asmRec().
     */
    public final int recLen() {
        DataDir pchild = null;
        
        length = 0;

        if (form == CONSTRUCTED)
            for (pchild=child; pchild!=null; pchild=pchild.next)
                length += pchild.recLen();
        else
            length = count;
        
        return length + tagLen(fldid) + lenLen(length);
    }
    
    
    /**
     * Replace this dir with another
     * 
     * @param newdir directory to replace this one with
     */
    public final void replace(DataDir newdir) {
        if (newdir == this)
            return;

        if (parent != null) {
            newdir.parent = parent;
 
            if (parent.child == this)
                parent.child = newdir;

            if (parent.last_child == this)
                parent.last_child = newdir;
        }

        if (prev != null) { 
            prev.next = newdir;
            newdir.prev = prev;
        }
        
        if (next != null) {
            next.prev = newdir;
            newdir.next = next; 
        }
    }
    
    
    /**
     * Remove the reference to any data and create a reference to the specified
     * DataDirObject. 
     * <p>
     * This is useful when the DataDir was constructed from a
     * BerString. The caller can create an object from the byte array in the
     * original DataDir and then replace the byte array with a reference to the
     * desired object.
     * 
     * @param obj object reference to added to DataDir
     */
    public final void replace(DataDirObject obj) {
        object = obj;
        count = obj.length();
        byteDataSource = null;
        charDataSource = null;
        stringDataSource = null;
        byteEncoding = null;
        number = 0;
    }
    
    
    /**
     * Replace the data contents with an INTEGER value.
     * 
     * @param num INTEGER
     */
    public final void replace(int num) {
        number = num;
        count = numLen(num);
        byteDataSource = null;
        charDataSource = null;
        object = null;
        stringDataSource = null;
        byteEncoding = null;
    }
    
    
    /**
     * Replace the data contents with a LONG value.
     *
     * @param num LONG
     */
    public final void replace(long num) {
        number = num;
        count = numLen(num);
        byteDataSource = null;
        charDataSource = null;
        object = null;
        stringDataSource = null;
        byteEncoding = null;
    }
    
    
    /**
     * Replace the data contents with a new String
     * 
     * @param data String reference
     */
    public final void replace(String data) {
        stringDataSource = data;
        byteEncoding = "local";
        byteDataSource = data.getBytes();
        dataOffset = 0;
        count = byteDataSource.length;
        object = null;
        charDataSource = null;
        number = 0;
    }
            
  /**
   * Replace the data contents with a new byte array
   * 
   * @param data byte[]
   */
  public final void replace(byte[] data) {
        stringDataSource = null;
        byteEncoding = null;
        byteDataSource = data;
        dataOffset = 0;
        count = byteDataSource.length;
        object = null;
        charDataSource = null;
        number = 0;
  }

    /**
     * Replace this dir with another and return the correct location.
     * @param newdir directory to replace this one with
     * @return the new dir
     */
    public final DataDir replaceDir(DataDir newdir) {
        replace(newdir);
        return newdir;
    }
    

  /**
   * Replace the data contents with the UTF8 encoding of a new String
   *
   * @param data String reference
   */
    public final void replaceUTF(String data) {

        byteDataSource = data.getBytes(StandardCharsets.UTF_8);
        count = byteDataSource.length;
        stringDataSource = data;
        byteEncoding = "UTF8";
        dataOffset = 0;
        object = null;
        charDataSource = null;
        number = 0;
    }
            

    /**
     *  Replace the asn1class with a new value
     *
     *  @param value
     */
    public final void setClass(int val) {
        asn1class=val;
    }
    
    
    /**
     * Change the fldid setting.
     *
     * @param fldid to change the node to.
     */
    public final void setFldid(int fldid) {
        this.fldid = fldid;
    }
    
    
    /**
     * Prints a formatted hex dump of the directory to stdout.
     *
     */
    
    public final void print() {
        print("\n");
    }
    
    private final void print(String line_prefix) {
        StringBuffer alpha = null;
        byte data[]        = null;
        System.out.println("DataDir: fldid(" + fldid +
                           ") asn1class(" + asn1class +
                           ") form(" + form +
                           ") length(" + length +
                           ") count(" + count + ")");
        System.out.flush();
        
        if (form == CONSTRUCTED && child != null) {
            System.out.println(line_prefix + "  child: ");
            System.out.flush();
            child.print(line_prefix + "  ");
        } else { // form == PRIMITIVE
            alpha = new StringBuffer();
            data  = data();

            System.out.println(line_prefix + "    data: ");
            System.out.flush();
            for (int i=0; data != null && i<count; i++) {
                if ((data[i]&0xff)<16)
                    System.out.println(" 0");
                else
                    System.out.println(" ");
                System.out.flush();
                System.out.println(Integer.toString(data[i]&0xff,16));
                System.out.flush();
                
                alpha.append(String.valueOf((char)(data[i])));
                if (((i+1)%16)==0) {
                    System.out.println("  " + alpha + line_prefix + "          ");
                    System.out.flush();
                    alpha.setLength(0);
                }
            }

            if (data == null) {
                if (object != null)
                    System.out.println(object.toString());
                else
                    System.out.println(Long.toString(number));
                System.out.flush();
            }
            System.out.println("  " + alpha);
            System.out.flush();
        }

        if (next != null)
            System.out.println(line_prefix + "sibling: " + next.toString(line_prefix));

        System.out.flush();
        return;
    }


    /**
     * Produce a formatted hex dump of a directory.
     *
     * @return String 
     */
    public final String toString() {
        return toString("\n");
    }


    private final String toString(String line_prefix) {
        StringBuffer alpha = null;
        byte data[]        = null;
        StringBuffer str = new StringBuffer("DataDir: fldid(" + fldid +
                                            ") asn1class(" + asn1class +
                                            ") form(" + form +
                                            ") length(" + length +
                                            ") count(" + count + ")");

        if (form == CONSTRUCTED && child != null)
            str.append(line_prefix + "  child: " +
                       child.toString(line_prefix + "  "));
        else { // form == PRIMITIVE

            alpha = new StringBuffer();
            data  = data();

            str.append(line_prefix + "    data: ");
            for (int i=0; data != null && i<count; i++) {
                if ((data[i]&0xff)<16)
                    str.append(" 0");
                else
                    str.append(" ");
                str.append(Integer.toString(data[i]&0xff,16));
                
                alpha.append(String.valueOf((char)(data[i])));
                if (((i+1)%16)==0) {
                    str.append("  " + alpha + line_prefix + "          ");
                    alpha.setLength(0);
                }
            }

            if (data == null) {
                if (object != null)
                    str.append(object.toString());
                else
                    str.append(Long.toString(number));
            }
            str.append("  " + alpha);
        }

        if (next != null)
            str.append(line_prefix + "sibling: " + next.toString(line_prefix));

        return str.toString();
    }
           
    /**
     * Convenience method for writing DataDirs to files. 
     * Creates or appends a file depending on the specified value of the 
     * append parameter and creates BerString object from the DataDir.
     * The method writes the BerString of the DataDir to the open
     * file. 
     *
     *  @param filename - name of the file to write.
     *
     */
    public void writeToFile(String filename) {
        writeToFile(filename, false);
    }

    /**
     * Convenience method for writing DataDirs to files. 
     * Creates or appends a file depending on the specified value of the 
     * append parameter and creates BerString object from the DataDir.
     * The method writes the BerString of the DataDir to the open
     * file. 
     *
     *  @param filename - name of the file to write.
     *  @param append - boolean indicating whether or not append the file.
     *  @see BerString
     *  @see FileOutputStream
     *  @see BufferedOutputStream
     */
    public void writeToFile(String filename, boolean append) {
        try {
          BufferedOutputStream berout = 
              new BufferedOutputStream(new FileOutputStream(filename, append));
          BerString berRec = new BerString(this);
          berRec.writeBerString(berout);
          berout.flush();
          berout.close();
       } catch (Exception e) {
          System.out.println("Exception: " + e);
       }
    }


    
    
    /**
     * Accessor method for child of this DataDir.
     */
    public final DataDir child() { 
        return child; 
    }

    /**
     * Accessor method for child of this DataDir. 
     * Does the same thing as child()
     */
    public final DataDir subElement() { 
        return child; 
    }

    /**
     * Accessor method for parent of this DataDir.
     */
    public final DataDir parent() { 
        return parent; 
    }

    /**
     * Accessor method for next sibling of this DataDir.
     */
    public final DataDir next() { 
        return next; 
    }

    /**
     * Accessor method for next sibling of this DataDir. 
     * Does the same thing as next()
     */
    public final DataDir nextElement() { 
        return next; 
    }

    /**
     * Accessor method for previous sibling of this DataDir.
     */
    public final DataDir prev() { 
        return prev; 
    }

    /**
     * Accessor method for previous sibling of this DataDir. 
     * Does the same thing as prev()
     */
    public final DataDir prevElement() { 
        return prev; 
    }

    /**
     * Accessor method for DataDirObject for this DataDir.
     */
    public final DataDirObject object() { 
        return object; 
    }

    /**
     * Accessor method for data for this DataDir.
     */
    public final void data(byte dest[], int offset, int length) {
        System.arraycopy(byteDataSource, dataOffset, dest, offset, 
                         Math.min(length, count));
    }
    
    /**
     * Accessor method for data for this DataDir.
     */
    public final byte[] data() {
        byte [] data = null;

        if (byteDataSource != null && count != 0) {
            data = new byte[count];
            System.arraycopy(byteDataSource, dataOffset, data, 0, count);
        }

        return data;
    }

    /**
     * Accessor method for fldid for this DataDir.
     */
    public final int fldid() { 
        return fldid; 
    }

    /**
     * Accessor method for asn1class for this DataDir.
     */
    public final byte asn1class() { 
        return (byte)asn1class; 
    }

    /**
     * Accessor method for form for this DataDir.
     */
    public final byte form() { 
        return (byte)form; 
    }

    /**
     * Accessor method for count for this DataDir. 
     * <p>
     * If the DataDir is PRIMITIVE, then the count is the length of 
     * the data, object or number.  
     * <p> 
     * If the DataDir is CONSTRUCTED, then the count is the number of 
     * children belonging to this DataDir.
     */
    public final int count() { 
        return count; 
    }
    
    /**
     * Set the next to null.
     */
    public final void resetNext() {
        next = null;
    }

    /**
     * Set the prev to null.
     */
    public final void resetPrev() {
        prev = null;
    }

    /**
     * Set the child to null.
     */
    public final void resetChild() {
        child = null;
    }

    /**
     * Set the parent to null.
     */ 
    public final void resetParent() {
        parent = null;
    }

    
    //////////////////////////////////////////////////////////////////////////
    // deprecated API below
    ////////////////////////////////////////////////////////////////////////// 

    /**
     * Add a leaf DataDir which contains a BITSTRING to a directory.
     * NOTE: Any of the characters '1', 'y', 'Y', 't' or 'T' get a 1 in the
     * encoded bitstring. Any other characters get a 0.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param bits String containing array of bits.
     * @deprecated Use addBits method instead.
     */
    public final DataDir daddBits(int fldid, byte asn1class, String bits) {
        return addBits(fldid, asn1class, bits);
    }
        
    /**
     * Add a leaf DataDir to the directory with an array of 8-bit byte data.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param byte[] data
     * @deprecated Use add method instead.
     */
    public final DataDir daddBytes(int fldid, byte asn1class, byte[] data) {
        return add(fldid, asn1class, data);
    }
        
    /**
     * Add a leaf DataDir to the directory with byte[] data.
     * I hope to deprecate the old daddChar method.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param byte[] data
     * @param int offset
     * @param int len
     * @deprecated Use add method instead.
     */
    public final DataDir daddBytes(int fldid, byte asn1class, 
                                   byte[] data, int offset, int len) {
        return add(fldid, asn1class, data, 0, data.length);
    }
        
    /**
     * Add a leaf DataDir to the directory with byte[] data.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param byte[] data
     * @param int offset
     * @param int len
     * @deprecated Use the add method instead
     */
    public final DataDir daddChar(int fldid, byte asn1class, 
                                  byte[] data, int offset, int len) {
        return add(fldid, asn1class, data, offset, len);
    }


    /**
     * Add a leaf DataDir to the directory with byte[] data.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param byte[] data
     * @deprecated Use the add method instead
     */
    public final DataDir daddChar(int fldid, byte asn1class, byte[] data) {
        return add(fldid, asn1class, data, 0, data.length);
    }

        
    /**
     * Add a leaf DataDir to the directory with a string of 7-bit byte data.
     * The data will be converted to a byte array.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param data String reference 
     * @deprecated Use the add method instead.
     */
    public final DataDir daddChar(int fldid, byte asn1class, String data) {
        return add(fldid, asn1class, data);
    }
    
    
    /**
     * Attach one directory to another.
     * @param newdir directory to be added
     * @deprecated Use the add method instead.
     */
    public final DataDir daddDir(DataDir newdir) {
        return daddDir(newdir, count);
    }
    
    /**
     * Attach one directory to another at a specified offset.
     *
     * @param newdir directory to be added
     * @param offset 0=first child; >0 = position; >number of children = last
     * @deprecated Use the add method instead.
     */
    public final DataDir daddDir(DataDir newdir, int offset) {
        DataDir pchild = null;
        
        if (form == PRIMITIVE)
            return null;
        
        if (child != null)
            {
                if (offset == 0)
                    {
                        newdir.next = child;
                        child.prev = newdir;
                        child = newdir;
                    }
                else if (offset >= count)
                    {
                        last_child.next = newdir;
                        newdir.prev = last_child;
                        last_child = newdir;
                    }
                else
                    {
                        for (pchild = child; offset > 0; 
                             offset--, pchild = pchild.next)
                            ; // empty loop
                        pchild.prev.next = newdir;
                        newdir.prev = pchild.prev;
                        pchild.prev = newdir;
                        newdir.next = pchild;
                    }
            }
        else
            {
                last_child = child = newdir;
                newdir.prev = null;
            }
        count++;
        newdir.parent = this;
        return newdir;
    }
    
    /**
     * Add a leaf DataDir to the directory with INTEGER data.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param num INTEGER
     * @deprecated Use the add method instead.
     */
    public final DataDir daddNum(int fldid, byte asn1class, int num) {
        return add(fldid, asn1class, num);
    }
    
    
    /**
     * Add a leaf DataDir to the directory with LONG data.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param num LONG
     * @deprecated Use the add method instead.
     */
    public final DataDir daddNum(int fldid, byte asn1class, long num) {
        return add(fldid, asn1class, num);
    }
    
    
    /**
     * Remove the reference to any data and create a reference to the specified
     * DataDirObject. This is useful when the DataDir was constructed from a
     * BerString. The caller can create an object from the byte array in the
     * original DataDir and then replace the byte array with a reference to the
     * desired object.
     * @param obj object reference to added to DataDir
     * @deprecated Use replace method instead
     */
    public void daddObj(DataDirObject obj) {
        object = obj;
        count = obj.length();
        byteDataSource = null;
        number = 0;
    }
    
    
    /**
     * Add a leaf DataDir to the directory with DataDirObject data.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param obj object reference 
     * @deprecated Use the add method instead.
     */
    public DataDir daddObj(int fldid, byte asn1class, DataDirObject obj) {
        return add(fldid, asn1class, obj);
    }
    
    
    /**
     * Add a leaf DataDir to a directory which contains an OID.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param OID String containing human readable OID
     * @deprecated Use addOID method instead.
     */
    public final DataDir daddoid(int fldid, byte asn1class, String OID) {
        return addOID(fldid, asn1class, OID);
    }
    
    
    /**
     * Add a leaf DataDir to the directory with String data.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @param data String reference 
     * @deprecated Use add method instead
     */
    public final DataDir daddString(int fldid, byte asn1class, 
                                    String data) {
        return add(fldid, asn1class, data);
    }
    
    
    /**
     * Add a non-leaf DataDir to a directory.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @deprecated Use add method instead.
     */
    public DataDir daddTag(int fldid, byte asn1class) {
        return add(fldid, asn1class);
    }
    
    
    /**
     * Remove a DataDir and its children from a directory.
     * @deprecated Use the delete method instead.
     */
    public final boolean ddelDir() { // delete a non-root dir entry
        if (parent == null)
            return false;
        
        delete();
        return true;
    }
    
    
    /**
     * Get a BITSTRING from a leaf DataDir.
     * @return A String with 'y' for 1 bits and 'n' for 0 bits.
     * @deprecated Use the getBits method instead
     */
    public final String dgetBits() {
        return getBits();
    }
    
    
    /**
     * Get a String from a leaf DataDir.
     * @return String
     * @deprecated Use getString method instead.
     */
    public final String dgetChar() {
        return getString();
    }
    
    
    /**
     * Get a LONG from a leaf DataDir.
     * @return value of LONG data. Undefined if not a leaf DataDir or if leaf
     * DataDir doesn't contain LONG data.
     * @deprecated Use the getLong method instead.
     */
    public final long dgetLong() {
        return getLong();
    }
    
    
    /**
     * Get an INTEGER from a leaf DataDir.
     * @return value of INTEGER data. Undefined if not a leaf DataDir or 
     * if leaf DataDir doesn't contain INTEGER data.
     * @deprecated Use the getLong method instead.
     */    
    public final int dgetNum() {
        return (int)getLong();
    }
    
    
    /**
     * Get a human readable OID from a leaf DataDir.
     * @return A String with human readable OID
     * @deprecated Use the getOID method instead.
     */
    public final String dgetoid() {
        return getOID();
    }
    
    
    /**
     * Re-initialize the root DataDir of a directory.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @return root DataDir
     * @deprecated Not needed.  Create new DataDir instead
     */
    DataDir dinit(int fldid, byte asn1class) { 
        parent = child = next = prev = null;
        byteDataSource = null;
        number = 0;
        this.fldid = fldid;
        this.asn1class = asn1class;
        form = 0;
        length = count = 0;

        return this;
    }
    
    
    /**
     * Insert a new parent DataDir above this DataDir and all its siblings.
     * @param fldid tag to be assigned to new DataDir
     * @param asn1class class to be assigned to new DataDir
     * @deprecated Use insertParent instead
     */
    public DataDir dinsTag(int fldid, byte asn1class) {
        return insertParent(fldid, asn1class);
    }
    
    
    /**
     * Replace the data contents with a new String
     * @param data String reference
     * @deprecated Use replace method instead.
     */
    public final void drepChar(String data) {
        replace(data);
    }
    
    
    /**
     * Replace this dir with another
     * @param newdir directory to replace this one with
     * @return the new dir
     * @deprecated Use the replace method instead.
     */
    public final DataDir dreplaceDir(DataDir newdir) {
        return replaceDir(newdir);
    }
    
    
    /**
     * Replace the data contents with an INTEGER value.
     * @param num INTEGER
     * @deprecated Use the replace method instead.
     */
    public final DataDir dreplaceNum(int num) {
        replace(num);
        return this;
    }
    
    
    /**
     * Checks for existence of a particular DataDir in a directory.
     * @param fldid tag of DataDir to be found
     * @param asn1class class of DataDir to be found
     * @return true if found, false if not found
     * @deprecated Use the find and findNext methods instead.
     */
    public final boolean dtagFound(int fldid, byte asn1class) { 
        if (find(fldid, asn1class)!=null)
            return true;
        else
            return false;
    }
    
    
    /**
     * Finds a particular DataDir in a directory.
     * @param fldid tag of DataDir to be found
     * @param asn1class class of DataDir to be found
     * @return DataDir if found, null if not found
     * @deprecated Use the find and findNext methods instead.
     */
    public final DataDir dtagLocate(int fldid, byte asn1class) {
        return find(fldid, asn1class, 1);
    }


    /**
     * Finds a the Nth particular DataDir in a directory.
     * @param fldid tag of DataDir to be found
     * @param asn1class class of DataDir to be found
     * @param occurrence number of occrrences of the DataDir to be found
     * @return DataDir if found, null if not found
     * @deprecated Use the find and findNext methods instead.
     */
    public final DataDir dtagLocate(int fldid, byte asn1class, int occurrence){
        return find(fldid, asn1class, occurrence);
    }

    
    
    /**
     * Get an INTEGER from a leaf DataDir. Does same thing as getInt()
     * @return value of INTEGER data. Undefined if not a leaf DataDir or 
     * if leaf DataDir doesn't contain INTEGER data.
     * @exception java.lang.ArithmeticException thrown when number is too large
     * to fit into an int.
     * @deprecated Use getInt or getLong instead.
     */
    public final int integerData() {
        return getInt();
    }
    
    
    /**
     * Get a String from a leaf DataDir. Does the same thing as dgetChar()
     * @return String
     * @deprecated Use the getString method instead.
     */
    public final String stringData() {
        return getString();
    }


    /**
     *  Replace the asn1class with a new value
     * 
     *  @param value
     *
     * @deprecated see setClass(int)
     */
    public final void setClass(byte val) {
        this.setClass((int)val);
    }



    //////////////////////////////////////////////////////////////////////////
    // deprecated API (above)
    //////////////////////////////////////////////////////////////////////////

    /** Compares this DataDir's data array to the specified DataDir's data 
     * array. 
     * The result is true if and only if the argument is not
     * null and is a DataDir that contains 
     * the same data array as this DataDir object.
     * 
     * @param that - the DataDir to compare with.
     * @return true if the object's data are the same; false otherwise.
     * @deprecated Use dataEquals method instead.
     */
    public boolean hasSameDataAs (DataDir that) {
        return this.dataEquals(that);
    }


    /** Compares this DataDir to the specified DataDir. 
     * The result is true if and only if the argument is not
     * null and is a DataDir object that contains 
     * the same nodes and data values as this DataDir object.
     * 
     * @param that - the DataDir to compare with.
     * @return true if the objects are the same; false otherwise.
     * @deprecated Use equals method instead.
     */
    public boolean isSameAs (DataDir that) {
        return this.equals(that);
    }

    /**
     * Locates the first primitive data node and returns the String data 
     * held at that location.
     * @return String
     */

    public String getData() {
       return(getData(-1));
    }

    /** 
     * Locates the primitive data node for the input fieldid 
     * and returns the String data held at that location. 
     * @param f the fieldid for the primitive node
     * @return String 
     */ 
    public String getData(int f) {
        DataDir dir = this;
        if (( (f == -1) || (dir.fldid() == f) ) && (dir.form() == ASN1.PRIMITIVE)) {
            return(dir.getString());
        }
        for ( DataDir sub = dir.child() ; sub != null; sub = sub.next() ) {
           if ( (f == -1) || (sub.fldid() == f) ) {
              if (sub.form() == ASN1.PRIMITIVE) {
                 return(sub.getString());
              } else {
                  return(sub.getData());
              }
           }
        }

       return(null);
    }

    /**
     * Locates the first primitive data node and returns the String data 
     * held at that location.
     * @return String
     */

    public String getUTFData() {
       return(getUTFData(-1));
    }

    /** 
     * Locates the primitive data node for the input fieldid 
     * and returns the String data held at that location. 
     * @param f the fieldid for the primitive node
     * @return String 
     */ 
    public String getUTFData(int f) {
        DataDir dir = this;
        if (( (f == -1) || (dir.fldid() == f) ) && (dir.form() == ASN1.PRIMITIVE)) {
            return(dir.getUTFString());
        }
        for ( DataDir sub = dir.child() ; sub != null; sub = sub.next() ) {
           if ( (f == -1) || (sub.fldid() == f) ) {
              if (sub.form() == ASN1.PRIMITIVE) {
                 return(sub.getUTFString());
              } else {
                  return(sub.getUTFData());
              }
           }
        }

       return(null);
    }

    /**
     * Locates the first primitive data node and replaces the data
     * held at that location with the input data.
     * @param subdata the string to store
     */
    public void replaceData(String subdata) {
       DataDir dir = this;
       if (dir.form() == ASN1.PRIMITIVE) {
          dir.replace(subdata);
          return;
       }
       for ( DataDir sub = dir.child() ; sub != null; sub = sub.next() ) {
          if (sub.form() == ASN1.PRIMITIVE) {
             sub.replace(subdata);
          } else {
              sub.replaceData(subdata);
          }
          return;
       }
    }

    /**
     * Locates the first primitive data node and replaces the data
     * held at that location with the input data.
     * @param subdata the string to store
     */
    public void replaceUTFData(String subdata) {
       DataDir dir = this;
       if (dir.form() == ASN1.PRIMITIVE) {
          dir.replaceUTF(subdata);
          return;
       }
       for ( DataDir sub = dir.child() ; sub != null; sub = sub.next() ) {
          if (sub.form() == ASN1.PRIMITIVE) {
             sub.replaceUTF(subdata);
          } else {
              sub.replaceUTFData(subdata);
          }
          return;
       }
    }

    /**
     * Locates the first DataDir object for the first occurrence of a 
     * primitive data node where data is located.
     * @return DataDir
     */
    public DataDir findDataNode() {
       return(findDataNode(-1));
    }

    /** 
     * Locates the DataDir object for the primitive node matching
     * the input fieldid.
     * @param f the fieldid to locate
     * @return DataDir 
     */ 
    public DataDir findDataNode(int f) {
        DataDir dir = this;
        for ( DataDir sub = dir.child() ; sub != null; sub = sub.next() ) {
            if ( (f == -1) || (sub.fldid() == f) ) {
                if (sub.form() == ASN1.PRIMITIVE) {
                    return(sub);
                } else {
                    return(sub.findDataNode(f));
                }
            }
        }

        return(null);
    }

    public DataDir findNode() {
        return(findNode(-1));
    }

    public DataDir findNode(int f) {
        DataDir dir = this;
        for ( DataDir sub = dir.child() ; sub != null; sub = sub.next() ) {
            if ( (f == -1) || (sub.fldid() == f) ) {
                if (sub.form() == ASN1.PRIMITIVE) {
                    return(sub);
                } else {
                    return(sub.findNode(f));
                }
            }
        }

        return(null);
    }
}
