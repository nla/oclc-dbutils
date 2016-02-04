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

package ORG.oclc.z39.client;

import java.util.*;
import java.lang.*;
import java.io.*;
import ORG.oclc.ber.*;
import ORG.oclc.z39.*;

/** nodes is a class used to parse and build a search request.
 * @version @(#)nodes.java      1.1 07/09/97
 * @author Jenny Colvard
 */

class nodes {
    static final String operators[] =
        {"AND","OR","NOT","OPERAND","NEAR","WITHIN"};

    static final int AND     = 0;
    static final int OR      = 1;
    static final int NOT     = 2;
    static final int OPERAND = 3;
    static final int NEAR    = 4;
    static final int WITHIN  = 5;

    int        type;
    nodes      left=null, right=null;
    String     term;
    Z39session zsession;

    nodes(String term, int query_type, Z39session zsession) {
        int t=0;

        this.zsession = zsession;
        this.term = term;

        if (query_type == Z39searchApi.type_101)
            if ((t=term.indexOf('/')) != -1)
                term = term.substring(0, t);

        if (term.equalsIgnoreCase("and"))
            type=AND;
        else if (term.equalsIgnoreCase("or"))
            type=OR;
        else if (term.equalsIgnoreCase("not"))
            type=NOT;
        else if (query_type == Z39searchApi.type_101 &&
          term.equalsIgnoreCase("near"))
            type=NEAR;
        else if (query_type == Z39searchApi.type_101 && 
          term.equalsIgnoreCase("within"))
            type=WITHIN;
        else
            type=OPERAND;
    }


    static int linkNodes(nodes node[], int which) {
        int left;

        //System.out.println("which is " + which);
        //System.out.println("type is " + node[which].type);
        if (which < 0) {
            return 0;
        }
         
        if (node[which].type == OPERAND) {
            node[which].left = node[which].right = null;

            return which-1;
        }

        if (which<2) {
            return which;
        }

        node[which].right = node[which-1];
        left = linkNodes(node, which-1);

        // Added test to make sure left >= 0... caused an error in invalid
        // complex query
        if (left >= (which-1) || left < 0) // error flag
            return which;
        node[which].left = node[left];
        return linkNodes(node, left);
    }


    void buildQuery(DataDir parent) {
        DataDir parm = null, seq = null, subparm = null;
        boolean within = false;
        char c;
        int i, t, aType, value;
        String attr, attributes[], set;
        StringTokenizer st = null;

        if (type==OPERAND) {
            parm = parent.add(Z39searchApi.Operand, ASN1.CONTEXT);

            if (zsession != null) {
                for (i=0; i<zsession.ResultSetNames.size(); i++)
                    if (term.equals(zsession.ResultSetNames.elementAt(i))) {
                        if(zsession!=null && zsession.utf8Encode)
                            parm.addUTF(Z39api.ResultSetId, ASN1.CONTEXT,
                                term);
                        else
                            parm.add(Z39api.ResultSetId, ASN1.CONTEXT, term);
                        return;
                    }
            }
            parm = parm.add(Z39api.AttributesPlusTerm, ASN1.CONTEXT);
            subparm = parm.add(Z39api.AttributeList, ASN1.CONTEXT);

            t = term.lastIndexOf('/');
            if (t != -1) {  // New way 'abc/u=x;r=x;p=x;s=x;t=x;c=x;'
                st = new StringTokenizer(term.substring(t+1), ";");
                attributes = new String[st.countTokens()];
                term = term.substring(0,t); // chop term at slash
                set = "bib1";

                for (i = 0; st.hasMoreTokens(); i++) {
                    // pull them off until we know what to do with them
                    attributes[i] = st.nextToken();
                    if (attributes[i].startsWith("oid=")) {
                        set = attributes[i].substring(4);
                        attributes[i] = null;
                    }
                }

                if (i > 0) { // found new style attributes 
                    if (set.equals("bib1")) {
                        for (i = 0; i < attributes.length; i++) {
                            if (attributes[i] == null)
                                continue;
                            c = attributes[i].charAt(0);
                            value = Integer.parseInt(
                                attributes[i].substring(2));
                            aType = -1;
                            switch (c) {
                            case 'c':
                                aType = Attribute.BIB1_completeness;
                                break;
                            case 'p':
                                aType = Attribute.BIB1_position;
                                break;
                            case 'r':
                                aType = Attribute.BIB1_relation;
                                if (value==Attribute.BIB1_Relation_within)
                                    within = true;
                                break;
                            case 's':
                                aType = Attribute.BIB1_structure;
                                break;
                            case 't':
                                aType = Attribute.BIB1_truncation;
                                break;
                            case 'u':
                                aType = Attribute.BIB1_use;
                                break;
                            }
                            if (aType != -1)
                                subparm.add(Attribute.buildDir(aType, value));
                        }
                    } // end of "bib1" attributes
                    else if (set.equals("zdsr")) {
                        for (i = 0; i < attributes.length; i++) {
                            if (attributes[i] == null)
                                continue;
                            c = attributes[i].charAt(0);
                            value = Integer.parseInt(
                                attributes[i].substring(2));
                            aType = -1;
                            switch (c) {
                            case 'u':
                                aType = Attribute.ZDSR_use;
                                break;
                            case 'w':
                                aType = Attribute.ZDSR_weight;
                                break;
                            }
                            if (aType != -1)
                                subparm.add(Attribute.buildDir(aType, value,
                                    Attribute.ZDSR));
                        }
                    } // end of "zdsr" attributes
                } // end of new style attributes
                else {
                    // old style tokens only support bib1
                    // try the old way 'abc/x/x/x/x'
                    st = new StringTokenizer(term.substring(t+1), "/");
                    if (st.hasMoreTokens()) {
                        term = term.substring(0,t); // chop term at slash
                        //System.out.println("term.toLowerCase(). is " +
                        //    term.toLowerCase());
    
                        // use
                        attr = st.nextToken(); 
                        subparm.add(Attribute.buildDir(Attribute.BIB1_use,
                            Integer.parseInt(attr)));
    
                        if (st.hasMoreTokens()) {
                            // structure
                            attr = st.nextToken();
                            subparm.add(Attribute.buildDir(
                                Attribute.BIB1_structure,
                                Integer.parseInt(attr)));
    
                            if (st.hasMoreTokens()) {
                                // truncate
                                attr = st.nextToken();
                                subparm.add(Attribute.buildDir(
                                    Attribute.BIB1_truncation, 
                                    Integer.parseInt(attr)));
                            }
                        }
                    }
                    else {
                        subparm.add(Attribute.buildDir(
                            Attribute.BIB1_position,
                            Attribute.BIB1_Position_anyPosition));
                    }
                } // end of old style tokens
            }

            if (subparm.count() == 0)  // no attributes
                subparm.delete();

            //System.out.println("term.toLowerCase(). is " +
            //    term.toLowerCase());
            if (term.length() > 0 && term.charAt(0)=='"') {
                t = term.indexOf('"', 1);
                if (t == -1)
                    t = term.length()-1;
                if(zsession!=null && zsession.utf8Encode)
                    parm.addUTF(Z39api.generalTerm, ASN1.CONTEXT, 
                        term.substring(1,t));
                else
                    parm.add(Z39api.generalTerm, ASN1.CONTEXT, 
                        term.substring(1,t));
            }
            else if(within && MultipleSearchTerms.isMultipleSearchTerms(term)) 
                parm.add(MultipleSearchTerms.toDataDir(term));
            else
                if(zsession!=null && zsession.utf8Encode)
                    parm.addUTF(Z39api.generalTerm, ASN1.CONTEXT, term);
                else
                    parm.add(Z39api.generalTerm, ASN1.CONTEXT, term);
            return;
        }

        parm = parent.add(Z39searchApi.Query, ASN1.CONTEXT);
        left.buildQuery(parm);
        right.buildQuery(parm);
        subparm = parm.add(Z39searchApi.Operator, ASN1.CONTEXT);

        if (type == WITHIN) {
            int distance = 1, unit = 2;

            if (term.indexOf('/') != -1) {
                st = new StringTokenizer(term, "/");
                attr = st.nextToken(); // skip this one
                distance = Integer.parseInt(st.nextToken());
                if (st.hasMoreTokens())
                    unit = Integer.parseInt(st.nextToken());
            }
            seq = subparm.add(Z39searchApi.prox, ASN1.CONTEXT);
            seq.add(Z39searchApi.exclusion, ASN1.CONTEXT, 0);
            seq.add(Z39searchApi.distance, ASN1.CONTEXT, distance);
            seq.add(Z39searchApi.ordered, ASN1.CONTEXT, 1);
            seq.add(Z39searchApi.relationType, ASN1.CONTEXT, 2);
            subparm = seq.add(Z39searchApi.proximityUnitCode, ASN1.CONTEXT);
            subparm.add(Z39searchApi.known, ASN1.CONTEXT, unit);
        }
        else if (type == NEAR) {
            int distance = 1, unit = 2;

            if (term.indexOf('/') != -1) {
                st = new StringTokenizer(term, "/");
                attr = st.nextToken(); // skip this one
                distance = Integer.parseInt(st.nextToken());
                if (st.hasMoreTokens())
                    unit = Integer.parseInt(st.nextToken());
            }
            seq = subparm.add(Z39searchApi.prox, ASN1.CONTEXT);
            seq.add(Z39searchApi.exclusion, ASN1.CONTEXT, 0);
            seq.add(Z39searchApi.distance, ASN1.CONTEXT, distance);
            seq.add(Z39searchApi.ordered, ASN1.CONTEXT, 0);
            seq.add(Z39searchApi.relationType, ASN1.CONTEXT, 2);
            subparm = seq.add(Z39searchApi.proximityUnitCode, ASN1.CONTEXT);
            subparm.add(Z39searchApi.known, ASN1.CONTEXT, unit);
        }
        else { // Operators are a IMPLICIT NULL in request.
            //System.out.println("Adding type: " + type + " term: " + term +
            //    " tree."); 
            subparm.add(type, ASN1.CONTEXT, (String)null);
        }
    }


    public void dumpNodes() {
        if (type==OPERAND) {
            return;
        }
        left.dumpNodes();
        right.dumpNodes();
    }
}
