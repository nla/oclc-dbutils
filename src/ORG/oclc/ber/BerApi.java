/* $Header: /home/CVS/dbutils/cvsroot/ORG/oclc/ber/BerApi.java,v 1.1.1.1 2001/07/18 13:32:46 root Exp $ */

package ORG.oclc.ber;

public interface BerApi
{
  // Field IDs
  public static int BOOLEAN       = 100;

  // Generic Class that implements IDataDir
  public static int GENERIC_CLASS = 110;
  public static int CLASS_NAME    = 111;
  public static int OBJECT_DIR    = 112;

  // Hashtable
  public static int HASHTABLE   = 200;
  public static int HASH_ENTRY  = 210;
  public static int HASH_KEY    = 211;
  public static int HASH_VALUE  = 212;

  // Vector
  public static int VECTOR      = 300;
}
