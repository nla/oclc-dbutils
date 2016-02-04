/* $Header: /home/CVS/dbutils/cvsroot/ORG/oclc/ber/IDataDir.java,v 1.1.1.1 2001/07/18 13:32:46 root Exp $ */

package ORG.oclc.ber;

public interface IDataDir
{
  // A public constructor that doesn't take any parameters must be 
  // declared if the class has another constructor.

  public void init(DataDir dir);
  public DataDir toDataDir();
}
