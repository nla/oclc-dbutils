package ORG.oclc.ber;


import java.util.*;

import ORG.oclc.util.NullObject;

public class BerHashtable implements BerApi
{
   DataDir ourHashDir;
   Hashtable ourHashtable;
   
   public BerHashtable (Hashtable htable)
   {
      ourHashtable = htable;   
      DataDir hashDir = new DataDir(HASHTABLE, (int)ASN1.CONTEXT);
      if (htable == null)
      {
         hashDir.add(ASN1.BOOLEAN, ASN1.UNIVERSAL, 0);
         ourHashDir = hashDir;
         return;
      }   
      for (Enumeration list = htable.keys(); list.hasMoreElements() ;)
      {
	 // Add the key to the DataDir
	 DataDir genClassDir = null;
         DataDir entryDir = new DataDir(hashDir, HASH_ENTRY,
                            (int)ASN1.CONTEXT);
         Object key = list.nextElement();
         DataDir dir = new DataDir(entryDir, HASH_KEY, (int)ASN1.CONTEXT);
         if (key instanceof String)            
            dir.add(ASN1.GENERALSTRING, ASN1.UNIVERSAL, (String)key);
         else if (key instanceof Number)
         {
            Number number = (Number)key;
            dir.add(ASN1.INTEGER, ASN1.UNIVERSAL, number.intValue());
         }
	 else if (key instanceof IDataDir)
	 {
	   DataDir objDir = obj2DataDir(key);
	   if (objDir != null)
	     dir.add(objDir);
	 }

	 // Add the value to the DataDir
         Object value = htable.get(key);
         dir = new DataDir(entryDir, HASH_VALUE, (int)ASN1.CONTEXT);
	 if (value == null || value instanceof NullObject) {
	     dir.add(ASN1.GENERALSTRING, ASN1.UNIVERSAL, "NULL");
	 }
         else if (value instanceof String)
            dir.add(ASN1.GENERALSTRING, ASN1.UNIVERSAL, (String)value);
         else if (value instanceof Number)
         {
            Number number = (Number)value;
            dir.add(ASN1.INTEGER, ASN1.UNIVERSAL, number.intValue());
         }
         else if (value instanceof Vector)
         {
            BerVector newVector = new BerVector((Vector)value);
            dir.add(newVector.getDataDir());
         }
         else if (value instanceof Hashtable)
         {
            BerHashtable newHashDir = new BerHashtable((Hashtable)value);
            dir.add(newHashDir.getDataDir());
         }
	 else if (value instanceof IDataDir)
	 {
	   DataDir objDir = obj2DataDir(value);
	   if (objDir != null)
	     dir.add(objDir);
	 }
      }
      ourHashDir = hashDir;            
   }      


   public BerHashtable (DataDir hashDir)
   {
      DataDir entryDir;
      DataDir keyDir;
      DataDir valueDir;
      DataDir subDir;
      Object key = null;
      Object value = null;
      BerHashtable subhash;
      BerVector subvector;
      
      if (hashDir == null)
         ourHashDir = null;
      // Create a hashtable with the number of HASH_ENTRY   
      Hashtable hashtable = new Hashtable();
      
      entryDir = hashDir.child();
      while (entryDir != null)
      {
         subDir = entryDir.child();
//System.out.println("berHashtable : subDir = " + subDir);         
         if (subDir.fldid() == HASH_KEY &&
             subDir.asn1class() == ASN1.CONTEXT)
         {
            keyDir = subDir.child();
            if (keyDir.fldid() == ASN1.GENERALSTRING &&
                keyDir.asn1class() == ASN1.UNIVERSAL)
            {
               String data = keyDir.getString();
               key = data;
            }   
            else if (keyDir.fldid() == ASN1.INTEGER &&
                     keyDir.asn1class() == ASN1.UNIVERSAL)
            {
               Integer data = new Integer(keyDir.getInt());
               key = data;
            }
	    else if (keyDir.fldid() == GENERIC_CLASS)
	    {
	      key = dataDir2obj(keyDir);
	    }
         }
         subDir = subDir.next();
         // If subDir is null, then put a NullObject as the value
	 /*
         if (subDir == null)
         {
            value = new NullObject();
            hashtable.put(key, value);
         }   
	 */

         if ((subDir != null) && 
	     (subDir.fldid() == HASH_VALUE) &&
	     (subDir.asn1class() == ASN1.CONTEXT))
         {
            valueDir = subDir.child();
            if (valueDir.fldid() == ASN1.GENERALSTRING &&
                valueDir.asn1class() == ASN1.UNIVERSAL)
            {
               String data = valueDir.getString();
	       if (data.equals("NULL"))
		   value = new NullObject();
	       else
		   value = data;

            }
            else if (valueDir.fldid() == ASN1.INTEGER &&
                     valueDir.asn1class() == ASN1.UNIVERSAL)
            {
               Integer data = new Integer(valueDir.getInt());
               value = data;
            }
            else if (valueDir.fldid() == HASHTABLE &&
                     valueDir.asn1class() == ASN1.CONTEXT)
            {
               subhash = new BerHashtable(valueDir);
               value = subhash.getHashtable();
            }
            else if (valueDir.fldid() == ASN1.SEQUENCE &&
                     valueDir.asn1class() == ASN1.UNIVERSAL)
            {
               subvector = new BerVector(valueDir);
               value = subvector.getVector();
            }
	    else if (valueDir.fldid() == GENERIC_CLASS)
	    {
	      value = dataDir2obj(valueDir);
	    }
//System.out.println("Hashtable entry: " + key + value);
	    if ((key != null) && (value != null))
	      hashtable.put(key, value);        
         }         
         entryDir = entryDir.next();           
      }
      ourHashtable = hashtable;
   }

   public Hashtable getHashtable()
   {
      return(ourHashtable);
   }
   public DataDir getDataDir()
   {
      return (ourHashDir);
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
       String className = classDir.child().getString();
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
       DataDir subdir = new DataDir(GENERIC_CLASS, (int)ASN1.CONSTRUCTED);
       DataDir classDir = new DataDir(subdir, CLASS_NAME,
                         (int)ASN1.CONSTRUCTED);
       classDir.add(ASN1.GENERALSTRING, ASN1.UNIVERSAL, 
			 genericVal.getClass().getName());
       DataDir objDir = new DataDir(subdir, OBJECT_DIR, (int)ASN1.CONSTRUCTED);
       objDir.add(genericVal.toDataDir());
       return subdir;
     }
     catch(Exception e)
     {
       e.printStackTrace();
     }

     return null;
   }
} 
