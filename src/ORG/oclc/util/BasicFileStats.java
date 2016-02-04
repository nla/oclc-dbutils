package ORG.oclc.util;

import java.util.*;


public interface BasicFileStats
{
  /**
     Each object which implements BasicFileStats
     should add themselves to this vector when they are instantiated and 
     remove themselves when they are closed.
   */
  public static final Vector fileStatsObjectTable = new Vector();
  
  /** 
      return the entire filename including path. 
  */
  public String getFilename();
  
  /**
     return total number of reads performed since the last time the stats
     where reset
   */
  public long getReadCount();
  
  /**
     return total number of writes performed since the last time the stats
     where reset
   */
  public long getWriteCount();
  
  /**
     return total accumulative read time in milli seconds since the last time 
     the stats where reset. This time also includes seek time.
  */
  public long getTotalReadTime();
  

  /**
     return total accumulative write time in milli seconds since the last time
     the stats where reset. This time also includes seek time.
  */
  public long getTotalWriteTime();
  
  /**
     return the files block size in byte.
   */
  public int getBlockSize();
  

  /**
     retset all of the stats
   */
  public void resetStats();
}
