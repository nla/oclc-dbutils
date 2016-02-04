
package ORG.oclc.z39;

import java.util.*;
import ORG.oclc.ber.*;
/**
  * Utility class to find the attributes from a query 
  */
public class AttributeUtil {

    static private final boolean debug = false;

    static public Attribute[] putAttributes(String attributes) {
	int type, value;
	Vector list = new Vector();
	StringTokenizer st = new StringTokenizer(attributes, ";");
	String token;
	Attribute attrs[];

	while (st.hasMoreTokens()) {
	    token = st.nextToken();

	    type = value = -1;

            if (Character.isDigit(token.charAt(0)))
	    {
                type = Attribute.BIB1_use;
		value = Integer.parseInt(token);
	    }
            else if (token.startsWith("u"))
	    {
		type = Attribute.BIB1_use;
                value = Integer.parseInt(token.substring(2));
	    }
	    else if (token.startsWith("s"))
	    {
		type = Attribute.BIB1_structure;
                value = Integer.parseInt(token.substring(2));
	    }
	    else if (token.startsWith("r"))
	    {
		type = Attribute.BIB1_truncation;
                value = Integer.parseInt(token.substring(2));
	    }
            else if (token.startsWith("newtonId"))
            {
		type = Attribute.BIB1_newtonIds;
                value = Integer.parseInt(token.substring(9));
            }

	    if (type != -1)
	        list.addElement(new Attribute(type, value));
	}
       
	if (list.size() == 0)
	    return null;
	attrs = new Attribute[list.size()];
	list.copyInto(attrs);
	return attrs;
    }

    static public String getAttributes(DataDir dir) throws Diagnostic1 {
	Attribute attribute;
        StringBuffer attrs = new StringBuffer();
 
        if (dir.fldid() != ASN1.SEQUENCE) // do down a level
            dir = dir.child();

        for ( ; dir != null; dir = dir.next())
        {
            attribute = new Attribute(dir);
            if (attrs.length() > 0)
                attrs.append(';');

            switch (attribute.type) {
                case Attribute.BIB1_use:
                    attrs.append("u=");
                    // attrs.append("use=");
                    attrs.append(attribute.value);
                    break;
                case Attribute.BIB1_structure:
                  //                    attrs.append("structure=");
                    attrs.append("s=");
                    switch (attribute.value) {
                        case Attribute.BIB1_Structure_Phrase:
                            attrs.append(Attribute.BIB1_Structure_Phrase);
                            break;
                        case Attribute.BIB1_Structure_Date:
                            attrs.append(Attribute.BIB1_Structure_Date);
                            break;
                        case Attribute.BIB1_Structure_WordList:
                            attrs.append(Attribute.BIB1_Structure_WordList);
                            break;
			case Attribute.BIB1_Structure_WordListAdj:
                            attrs.append(Attribute.BIB1_Structure_WordListAdj);
                            break;
                        case Attribute.BIB1_Structure_Word:
                            attrs.append(Attribute.BIB1_Structure_Word);
                            break;
                        case Attribute.BIB1_Structure_Key:
                            attrs.append(Attribute.BIB1_Structure_Key);
                            break;
                    }
                    break;
                case Attribute.BIB1_newtonIds:
                    attrs.append("newtonId=");
                    attrs.append(attribute.value);
                    break;
            }
        }
 
        if (debug)
            System.out.println("processAttributes attrs='" + attrs);

        return attrs.toString();
    }
}
