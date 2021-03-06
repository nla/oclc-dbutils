/* 
(c)2000 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin, 
Ohio 43017-0702.  OCLC is a registered trademark of OCLC Online Computer 
Library Center, Inc. 
  
NOTICE TO USERS:  The Z39.50 Utilities ("Software") has been developed by OCLC 
Online Computer Library Center, Inc.  Subject to the terms and conditions set 
forth below, OCLC grants to user a perpetual, non-exclusive, royalty-free 
license to use, reproduce, alter, modify, and create derivative works from 
Software, and to sublicense Software subject to the following terms and 
conditions: 
  
SOFTWARE IS PROVIDED AS IS.  OCLC MAKES NO WARRANTIES, REPRESENTATIONS, OR 
GUARANTEES WHETHER EXPRESS OR IMPLIED REGARDING SOFTWARE, ITS FITNESS FOR ANY 
PARTICULAR PURPOSE, OR THE ACCURACY OF THE INFORMATION CONTAINED THEREIN. 
  
User agrees that OCLC shall have no liability to user arising therefrom, 
regardless of the basis of the action, including liability for special, 
consequential, exemplary, or incidental damages, including lost profits, 
even if it has been advised of the possibility thereof. 
  
User shall cause the copyright notice of OCLC to appear on all copies of 
Software, including derivative works made therefrom. 
*/ 

package ORG.oclc.util;

public class IniData {
    public String data;
    public char ary[];
    public int pos = 0;
    public String filename = null;

    public IniData(String data) {
        this.data = data;
        ary = data.toCharArray();
        pos = 0;
    }
}
