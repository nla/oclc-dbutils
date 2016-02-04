package ORG.oclc.ber;

import java.util.*;

public class BerVector implements BerApi 
{
   DataDir ourVectorDir = null;
   Vector ourVector = null;

   public BerVector(Vector vector)
   {
      ourVector = vector;     
      DataDir vectorDir = new DataDir(ASN1.SEQUENCE, ASN1.UNIVERSAL);
      if (vector.isEmpty())
      {
         //do this because empty CONSTRUCTED directories go away
         //maybe send a NullObject?
         vectorDir.daddNum(ASN1.BOOLEAN,ASN1.UNIVERSAL, 0);
         ourVectorDir = vectorDir;
      }         
      for (Enumeration list = vector.elements(); list.hasMoreElements() ;)
      {

         Object entry = list.nextElement();
         if (entry instanceof String)
            vectorDir.daddChar(ASN1.GENERALSTRING, ASN1.UNIVERSAL,(String)entry);
         else if (entry instanceof Number)
         {
            Number number = (Number)entry;
            vectorDir.daddNum(ASN1.INTEGER, ASN1.UNIVERSAL, number.intValue());
         }
         else if (entry instanceof Vector)
         {
            BerVector newVectorDir = new BerVector((Vector)entry);
            vectorDir.daddDir(newVectorDir.getDataDir());   
         }
         else if (entry instanceof Hashtable)
         {
            BerHashtable newHashDir = new BerHashtable((Hashtable)entry);
            vectorDir.daddDir(newHashDir.getDataDir());
         }
	 else if (entry instanceof IDataDir)
	 {
	   DataDir obj = obj2DataDir(entry);
	   if (obj != null)
	     vectorDir.daddDir(obj);
	 }
      }
      ourVectorDir = vectorDir;            
   }      

   public BerVector (DataDir vectorDir)
   {
      DataDir seqDir;
      DataDir dataDir;
      Object data = null;
      BerHashtable subhash;
      BerVector subvector;
      boolean empty = true;
      
      ourVectorDir = vectorDir;
            
      if (vectorDir == null)
      {
//System.out.println("BerVector: vectorDir is null");      
         ourVector = null;
      }   
      // Create a vector with the number of SEQUENCE entries   
      Vector vector = new Vector(vectorDir.count());
//System.out.println("BerVector: vectorDir: " + vectorDir);
//System.out.println("BerVector: count: " + vectorDir.count());
      
      dataDir = vectorDir.child();
      while (dataDir!= null)
      {
         // Empty vector
         if (dataDir.fldid() == ASN1.BOOLEAN &&
             dataDir.asn1class() == ASN1.UNIVERSAL)
            {
               empty = true;
               break;
            }  
        
         if (dataDir.fldid() == ASN1.GENERALSTRING &
             dataDir.asn1class() == ASN1.UNIVERSAL)
            data = dataDir.dgetChar();
         else if (dataDir.fldid() == ASN1.INTEGER &&
                  dataDir.asn1class() == ASN1.UNIVERSAL)
            data = new Integer(dataDir.dgetNum());
         else if (dataDir.fldid() == HASHTABLE &&
                  dataDir.asn1class() == ASN1.CONTEXT)
         {
            subhash = new BerHashtable(dataDir);
            data =  subhash.getHashtable();
         }
         else if (dataDir.fldid() == ASN1.SEQUENCE &&
                  dataDir.asn1class() == ASN1.UNIVERSAL)
         {
            subvector = new BerVector(dataDir);
            data = subvector.getVector();
         }
	 else if (dataDir.fldid() == GENERIC_CLASS)
	 {
	    data = dataDir2obj(dataDir);
	 }
	 if (data != null)
	 {
	   vector.addElement(data);
	   empty = false;
	 }
                 
         dataDir = dataDir.next();
      }
   if (empty)
      {
//System.out.println("Vector is empty");      
      vector.removeAllElements();
      }
   ourVector = vector;      
   }

   
   public Vector getVector()
   {
      return(ourVector);
   }
   public DataDir getDataDir()
   {
      return (ourVectorDir);
   }        

  // Private Methods

   private Object dataDir2obj(DataDir dir)
   {
     try
     {
       DataDir classDir = null;
       DataDir valueDir = null;
       DataDir child = dir.child();
       while (child != null)
       {
	 switch(child.fldid())
	 {
           case CLASS_NAME: classDir = child; break;
           case OBJECT_DIR: valueDir = child; break;
	 }
	 child = child.next();
       }
       String className = classDir.child().dgetChar();
       Class cl = Class.forName(className);
       IDataDir obj = (IDataDir) cl.newInstance();
       obj.init(valueDir.child());
       return obj;
     }
     catch(Exception e)
     {
       e.printStackTrace();
     }
     return null;
   }

   private DataDir obj2DataDir(Object obj)
   {
     try
     {
       IDataDir genericVal = (IDataDir) obj;
       DataDir subdir = new DataDir(GENERIC_CLASS, ASN1.CONSTRUCTED);
       DataDir classDir = new DataDir(subdir, CLASS_NAME, ASN1.CONSTRUCTED);
       classDir.daddChar(ASN1.GENERALSTRING, ASN1.UNIVERSAL, 
			 genericVal.getClass().getName());
       DataDir objDir = new DataDir(subdir, OBJECT_DIR, ASN1.CONSTRUCTED);
       objDir.daddDir(genericVal.toDataDir());
       return subdir;
     }
     catch(Exception e)
     {
       e.printStackTrace();
     }

     return null;
   }
} 
