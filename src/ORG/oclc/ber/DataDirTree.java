package ORG.oclc.ber;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

import ORG.oclc.util.Util;

public class DataDirTree extends DataDir {

    private static final Vector trees = new Vector(10);

    boolean   usingLeftOvers;
    DataDir   bottom=null, leftOvers=null, oldRoot;


    public DataDir reset(int fldid, int asn1class) {
        oldRoot=child;

        level=0;
        if(oldRoot!=null) {
            oldRoot.parent=null;
            findBottom(oldRoot);
        }
        else
            bottom=null;
        usingLeftOvers=false;
        resetDir(this, fldid, asn1class);
        form=ASN1.CONSTRUCTED;
        return this;
    }


    public DataDir reset(BerString record) {
        byte b;
        int     asn1class, fldid, fieldlen[]=new int[1], lenlen, nodesize,
                size, taglen[]=new int[1], tfldlen;

        level=0;
        oldRoot=child;
        if(oldRoot!=null) {
            oldRoot.parent=null;
            findBottom(oldRoot);
        }
        else
            bottom=null;
        usingLeftOvers=false;
        record.offset = 0;

        b=record.record[record.offset];
        asn1class=(b&0xff)>>6;
        fldid=record.getTag(taglen);
        resetDir(this, fldid, asn1class);
        if ((b&0x20) == 0x20)
            form = ASN1.CONSTRUCTED;
        else
            form = ASN1.PRIMITIVE; 

        lenlen = record.getLen(fieldlen);
        
        if (fieldlen[0] == -1) {
            if (record.IsCompleteBER( 0x7fffffff, fieldlen)) {
                fieldlen[0] -= (taglen[0]+lenlen+2);
                // -= 2;  // don't include trailing nulls in loop later
            }
       }        
        
        tfldlen = fieldlen[0];
        if (form == ASN1.PRIMITIVE) {
            byteDataSource = record.record;
            dataOffset     = record.offset;
            count          = tfldlen;
            record.offset += tfldlen;
            findBottom(this);
            return this;
        }
        
        size = record.offset + tfldlen;
        while (record.offset < size) {
            // bldNode does not need this fieldLen or tagLen
            // but it was allocating lots more of them and this
            // save alots of 'new's!
            bldNode(this, record, fieldlen, taglen);
        }

        if(!usingLeftOvers && bottom!=null) {
            if(leftOvers!=null)
                leftOvers.parent=bottom;
            bottom.child=leftOvers;
            leftOvers=oldRoot;
        }

        return this;
    }


    private void bldNode(DataDir dir, BerString record, int fieldlen[],
      int taglen[]) {
        byte b = record.record[record.offset];
        int  asn1class,  fldid,  lenlen,  oldoffset,  size,  tfldlen;

        asn1class = (b&0xff) >> 6;
        
        oldoffset = record.offset;      
        fldid     = record.getTag(taglen);
        lenlen    = record.getLen(fieldlen);
        
        tfldlen = fieldlen[0];
        if (tfldlen == -1) {
            if (record.IsCompleteBER(oldoffset, 0x7fffffff, fieldlen)) {
                fieldlen[0] -= (taglen[0]+lenlen+2);
            }
            tfldlen = fieldlen[0];  // re-set local copy
        }
        
        if (record.offset+tfldlen > record.record.length) {
            record.offset += tfldlen;
            return;
        }
        
        if ((b & 0x20) == 0) {  // PRIMITIVE            
            if (tfldlen > 0 || (tfldlen==0 && !record.indefinite)) 
                add(dir, fldid, asn1class, record.record, record.offset,
                    tfldlen);
            record.offset += tfldlen;
        } else if (tfldlen > 0) {
            DataDir newdir = add(dir, fldid, asn1class);

            size = record.offset + tfldlen;
            while (record.offset < size)
                bldNode(newdir, record, fieldlen, taglen);
        }
    }


    public void complete() {
        if(!usingLeftOvers && bottom!=null) {
            if(leftOvers!=null)
                leftOvers.parent=bottom;
            bottom.child=leftOvers;
            leftOvers=oldRoot;
        }
    }


    public DataDir add(DataDir parent, int fldid, int asn1class) {
        if (parent.form == ASN1.PRIMITIVE) {  // can't add a child to a leaf
            System.out.println("Trying to add a child to "+parent);
            Thread.currentThread().dumpStack();
            return null;
        }

        DataDir newdir = getFreeDataDir(fldid, asn1class);
        newdir.parent = parent;
        newdir.form = ASN1.CONSTRUCTED;

        if (parent.child==null)
            parent.child=parent.last_child=newdir;
        else {
            parent.last_child.next=newdir;
            newdir.prev=parent.last_child;
            parent.last_child=newdir;
        }
        parent.count++;

        return newdir;
    }
    
    public final DataDir add(DataDir parent, int fldid, int asn1class,
      byte[] data, int offset, int len) {
        if (parent.form == ASN1.PRIMITIVE)  // can't add a child to a leaf node
            return null;
        
        DataDir newdir = add(parent, fldid, asn1class);
        newdir.form = ASN1.PRIMITIVE;
        newdir.byteDataSource = data;
        newdir.dataOffset = offset;
        newdir.count = len;
        return newdir;
    }


    public final DataDir addUTF(DataDir parent, int fldid, int asn1class,
      String s) {
        if (parent.form == PRIMITIVE)  // can't add a child to a leaf node
            return null;

        DataDir             newdir;

        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        newdir=add(parent, fldid, asn1class, bytes, 0, bytes.length);
        newdir.stringDataSource = s;
        return newdir;
    }


    public final DataDir addUTF(DataDir parent, int fldid, int asn1class,
      char[] chars, int offset, int count) {
        if (parent.form == PRIMITIVE)  // can't add a child to a leaf node
            return null;

        byte[]              bytes = new byte[count*3];
        DataDir             newdir;

        int newcount=Util.encodeUtf8(chars, offset, count, bytes, 0, count*3);
        newdir=add(parent, fldid, asn1class, bytes, 0, newcount);
        return newdir;
    }


    public static void freeTree(DataDirTree tree) {
        synchronized (trees) {
                trees.addElement(tree);
        }
    }


    private DataDir getFreeDataDir(int fldid, int asn1class) {
        if(bottom==null)
            if(leftOvers==null) {
                return new DataDir(fldid, asn1class);
            }
            else {
                usingLeftOvers=true;
                findBottom(leftOvers);
            }

        DataDir dir=bottom;

        if(bottom.prev!=null) {
            bottom=bottom.prev;
            bottom.next=null;
            if(bottom.child!=null) {
                findBottom(bottom.child);
            }
        }
        else 
            if(bottom.parent!=null) {
                bottom=bottom.parent;
                bottom.child=null;
            }
            else
                if(usingLeftOvers) {
                    usingLeftOvers=false;
                    leftOvers=bottom=null;
                }
                else
                    if(leftOvers!=null) {
                        findBottom(leftOvers);
                        usingLeftOvers=true;
                    }
                    else
                        bottom=null;

        resetDir(dir, fldid, asn1class);
        return dir;
    }


    public static DataDirTree getTree() {
        synchronized (trees) {
            DataDirTree tree;
            int         size = trees.size();
            if (size == 0) {
                // Create a new one
                tree=new DataDirTree();
                return(tree);
            }

            tree = (DataDirTree) trees.elementAt(size - 1);
            trees.setSize(size - 1);
            return(tree);
        }
    }


    private void resetDir(DataDir dir, int fldid, int asn1class) {
        dir.child = dir.last_child = dir.parent = dir.next = dir.prev = null;
        dir.object=null;
        dir.fldid=fldid;
        dir.asn1class=asn1class;
        dir.byteDataSource=null;
        dir.charDataSource=null;
        dir.stringDataSource=null;
        dir.count = dir.dataOffset = 0;
    }

    private int level=0;
    private void findBottom(DataDir root) {
        if(++level>20) {  // probably in a loop
            leftOvers=null;
            bottom=new DataDir(0, 0);
            level--;
            return;
        }

        while(root.next!=null)
            root=root.next;
        if(root.child!=null) {
            findBottom(root.child);
        }
        else {
            bottom=root;
        }
        level--;
    }
}
