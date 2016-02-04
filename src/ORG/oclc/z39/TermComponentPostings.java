/*
(c)1996 OCLC Online Computer Library Center, Inc., 6565 Frantz Road, Dublin,
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

package ORG.oclc.z39;

import ORG.oclc.ber.*;

/**
 * TermComponentPostings returns additional search information by database.
 * @version @(#)TermComponentPostings.java	1.1 07/09/97
 * @author Lisa Cox
 */

public class TermComponentPostings {

/**
 * Full/Partial results value
 */
    public int  fullResults;

/**
 * the query expression with use/structure information
 */
    public DataDir queryExpression;

/**
 * the postings for the expression
 */
    public int count;
/**
  * the Structure attributes.
  */
    private Attribute structure;

/**
 * the Attributes for the term
 */
    private Attribute attributes;

/**
 * the query term
 */
    private String term;

/**
  * the per database breakdown of the results
  */
    public DbResults dbResults[];

    /**
     * Constructs a TermComponentPostings object. 
     */   
    public TermComponentPostings() {
       
    }

    /** 
     * Constructs a TermComponentPostings object for the input term and postings
     * count.
     * @param term the query term
     * @param count the postings count for the term  
     */   
    public TermComponentPostings(String term, int count) { 
	this.term = term;
	this.count = count;
        
    } 
/**
  * Accessor method to the attributes
  */
  public Attribute attributes() {
     if (attributes == null)
       parseQueryExpression();
     return attributes;
   }

/**
  * Accessor method to the attributes
  */
  public int structure() {
      if (structure == null)
	  parseQueryExpression();
      if (structure == null)
	  return -1;
      return structure.value;
   }

/**
  * Accessor method to the attributes
  */
  public int use() {
      if (attributes == null)
	  parseQueryExpression();
      if (attributes == null)
	  return -1;
      return attributes.value;
   }

/** 
  * Accessor method to the term
  * @return String
  */
   
    public String term() {
      if (term == null)
        parseQueryExpression();
 
      return term;
    }

    public final DataDir attributesPlusTerm() {
      DataDir node=queryExpression;

      for(;node!=null && node.fldid() != Z39searchApi.type_101 &&
          node.fldid() !=Z39searchApi.type_1; node=node.subElement());

      if (node != null) {
        node=node.subElement();
        while (node!=null) {
           switch(node.fldid()) {
              case Z39searchApi.Operand:
                return node.child();

              default:
                node = node.next();
                break;
           }
         }  
      }
      return null;
    } 

    private void parseQueryExpression() {

      DataDir node=attributesPlusTerm();

      if (node != null)
           for (node=node.subElement();node != null; node=node.next()){
             switch (node.fldid()) {
               case Z39api.generalTerm:
                term = node.getString();
                break;
               case Z39api.AttributeList:
                try {
		    DataDir subnode;
		    Attribute tmp;
		    for (subnode=node.child(); subnode != null; 
			 subnode = subnode.next() ) {
			tmp = new Attribute(subnode);
			if (tmp.type == Attribute.BIB1_use) 
			    attributes = tmp;
			else if (tmp.type == Attribute.BIB1_structure)
			    structure = tmp;
		    }
                }
                catch (Diagnostic1 d) {}
                break;
             }
            
       }
 
      }
    
/**
  * Create TermComponentPostings info string
  * @return String
  */

    public String toString() {
	StringBuffer str = new StringBuffer();
	int i;
        str.append("term= '" + term() + "'\ncount= " + count +
        " fullResultsFlag= ");
        if (fullResults ==0) 
          str.append("false\n");
        else
          str.append("true\n");
        str.append("use = " + use() + "\n");
        str.append("structure = " + structure() + "\n");

//        str.append("queryExp: \n" + queryExpression + "\n");
        if (dbResults != null) {
          str.append("\nBreakDown by database:\n");
          for (i=0; i<dbResults.length; i++)  
            str.append(dbResults[i]);
        } 
	return str.toString();
    }

}
