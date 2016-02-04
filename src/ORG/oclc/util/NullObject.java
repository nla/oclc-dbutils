/* $Header: /home/CVS/dbutils/cvsroot/ORG/oclc/util/NullObject.java,v 1.1.1.1 2001/07/18 13:32:46 root Exp $ */
/*
 * NullObject.java
 * 
 * Copyright (c) 1997 OCLC Online Computer Library Center, Inc.
 ************************************************************************/
package ORG.oclc.util;

import java.io.*;

/** The NullObject class is used to indicate that the value of a column
 *  should be set to NULL when updating database rows.
 */
public class NullObject
{
  /**
    * Returns a String object that contains the string "NULL".
    */
  public String toString()
  {
    return "NULL";
  }
}
