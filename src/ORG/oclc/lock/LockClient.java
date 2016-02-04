/*
 * LockClient.java
 *****************************************************************/

package ORG.oclc.lock;

import java.io.*;
import java.net.*;
import java.util.*;

import ORG.oclc.log.*;
import ORG.oclc.ber.*;
import ORG.oclc.util.*;
import ORG.oclc.lock.*;

/**
 *
 *
 */
public class LockClient {

  private String host;
  private int port;
  
  /**
   *
   *
   */
  public LockClient(String loadFile) {

    readLoadFile(loadFile);
  }

  /**
   *
   *
   */
  public LockClient(String host, int port) {
    this.host = host;
    this.port = port;
  }
  
  /**
   *
   *
   */
   public LockClient(String host, String port) {
       this.host = host;
       this.port = Integer.parseInt(port);
   }


  //   public static void main(String args[]) {

  //     LockClient lc = new LockClient("orc.rsch.oclc.org", 11110);
  //     LockClient lc = new LockClient("bongo.dev.oclc.org", 63653);
       
  //     System.out.println("before lock");
  //     lc.lock("user01", "abii0001", "Title: Mother Goose");
  //     System.out.println("-------------------------------------------------------------");
      //System.out.println("before query");
      //lc.query(null, "abii0001", null);
      //System.out.println("-------------------------------------------------------------");
      //System.out.println("before lock");
      //lc.lock("user01", "abii0001", "Title: Mother Goose");
      //System.out.println("-------------------------------------------------------------");
      //System.out.println("before unlock");
      //lc.unlock("user01", "abii0001");
      //System.out.println("-------------------------------------------------------------");
      //System.out.println("before query");
      //lc.query(null, "abii0001", null);
      //System.out.println("-------------------------------------------------------------");


      //lc.lock("user01", "abii0001", "Title: Mother Goose 1");
      //lc.lock("user01", "abii0002", "Title: Mother Goose 2");
      //lc.lock("user01", "abii0003", "Title: Mother Goose 3");

      //lc.query("user01", null, null);

      //lc.lock("Vince", "updatedb:corc3", "Test lock of the DB for backups");
      //lc.unlock("Vince", "updatedb:corc3");
  //   }


  /**
   *
   *
   */
   public LockResponse lock(String user, String key, String description) {
       return(lock(user, key, description, 0));
   }

  /**
   *
   *
   */
   public LockResponse lock(String user, String key, String description, int timeout) {
      BerString berRequest = 
        build("LockIt", user, key, description, Lock.LOCK, timeout);

      DataDir resp = sendRequest(berRequest);

      LockResponse lr = new LockResponse();
      if (resp == null) { 
         lr.status = LockResponse.NOCONNECTION; 
         return(lr); 
      }
      DataDir status = resp.find(Lock.STATUS_TAG, ASN1.CONTEXT);
      if (status.getString().equals(Lock.STATUS_LOCKED)) {
         lr.status = LockResponse.SUCCEEDED;
      } else {
         lr.status = LockResponse.FAILED;

         lr.results = new Vector();
         Lock tmpLock;
         for ( DataDir sub = resp.child() ; sub != null ; sub = sub.next() ) {
             if (sub.fldid() == Lock.QUERY_TAG) {
                 tmpLock = new Lock();
                 tmpLock.parseBERRequest(sub);
                 lr.results.addElement(tmpLock);
             }
         }
      }
      lr.response = resp;
      return(lr);
   }

  /**
   *
   *
   */
   public LockResponse touch(String user, String key, int timeout) {
      BerString berRequest = build("Touch", user,key,null,Lock.TOUCH, timeout);
      DataDir resp = sendRequest(berRequest);
      LockResponse lr = new LockResponse();
      if (resp == null) { 
         lr.status = LockResponse.NOCONNECTION; 
         return(lr); 
      }
      DataDir status = resp.find(Lock.STATUS_TAG, ASN1.CONTEXT);
      if (status.getString().equals(Lock.STATUS_TOUCHED)) {
          lr.status = LockResponse.SUCCEEDED;
      } else {
          lr.status = LockResponse.FAILED;
      }
      lr.response = resp;
      return(lr);
   }
    
  /**
   *
   *
   */
   public LockResponse unlock(String user, String key) {
      BerString berRequest = build("UnLockIt", user, key, null, Lock.UNLOCK, 0);
      DataDir resp = sendRequest(berRequest);
      LockResponse lr = new LockResponse();
      if (resp == null) { 
         lr.status = LockResponse.NOCONNECTION; 
         return(lr); 
      }
      DataDir status = resp.find(Lock.STATUS_TAG, ASN1.CONTEXT);
      if (status.getString().equals(Lock.STATUS_UNLOCKED)) {
          lr.status = LockResponse.SUCCEEDED;
      } else {
          lr.status = LockResponse.FAILED;
      }
      lr.response = resp;
      return(lr);
   }
    
  /**
   *
   *
   */
   public LockResponse query(String user, String key, String description) {
      BerString berRequest = 
        build("QueryIt", user, key, description, Lock.QUERYLOCK, 0);
      DataDir resp = sendRequest(berRequest);
      LockResponse lr = new LockResponse();
      if (resp == null) { 
         lr.status = LockResponse.NOCONNECTION; 
         return(lr); 
      }
      DataDir status = resp.find(Lock.STATUS_TAG, ASN1.CONTEXT);
      if (status.getString().equals(Lock.STATUS_RECEXISTS)) {
         lr.results = new Vector();
         Lock tmpLock;
         for ( DataDir sub = resp.child() ; sub != null ; sub = sub.next() ) {
             if (sub.fldid() == Lock.QUERY_TAG) {
                 tmpLock = new Lock();
                 tmpLock.parseBERRequest(sub);
                 lr.results.addElement(tmpLock);
             }
         }
      }      
      lr.response = resp;
      return(lr);
   }
    

   private void readLoadFile(String fileName) {

     String loadFile = fileName;

     String str1 = null;

     try {
       File f = new File(loadFile);
       long len1 = f.length();                  
       byte data1[] = new byte[(int) len1 + 1];
       FileInputStream inFile = new FileInputStream(loadFile);

       inFile.read(data1, 0, (int) len1);
       inFile.close();

       str1 = new String(data1);
     } catch (FileNotFoundException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     }

     StringTokenizer st = new StringTokenizer(str1, ",", false);
     String token = "";

     // Get host and port from .load file
     token = st.nextToken();
     this.host = st.nextToken();

     token = st.nextToken();
     this.port = Integer.parseInt(token);

   }


  /**
   *
   *
   */
   private final DataDir sendRequest(BerString berStr) {
      BerString resp = null;
      DataDir dir = null;

      try {
         BerConnect lockConn = new BerConnect(this.host, this.port);
         resp = lockConn.doRequest(berStr, true);
         dir = new DataDir(resp);
         lockConn.close();
      } catch (Exception e) {
          e.printStackTrace();
          return(null);
      }

      return(dir);
  }


  private static final BerString build(String command, String user, String key, String desc, int type, int timeout) {
     DataDir dir = new DataDir(Lock.ROOT_TAG, (int)ASN1.APPLICATION);

     if (command != null) { dir.add(Lock.COMMAND_TAG, ASN1.CONTEXT, command); }
     if (user != null) { dir.add(Lock.USERID_TAG, ASN1.CONTEXT, user); }
     if (key != null)  { dir.add(Lock.KEY_TAG, ASN1.CONTEXT, key); }
     if (desc != null) { dir.add(Lock.DESC_TAG, ASN1.CONTEXT, desc); }
     if (timeout != 0)  { dir.add(Lock.TIMEOUT_TAG, ASN1.CONTEXT, timeout); }
     dir.add(Lock.TYPE_TAG, ASN1.CONTEXT, type);
     
     return (new BerString(dir));
  }

}
