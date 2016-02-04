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

import ORG.oclc.ber.*;
import ORG.oclc.z39.*;

/** Z39response takes a unknown Z39.50 response, identifies it and calls
 *  the appropriate routine to examine the response.
 * @version @(#)Z39response.java	1.1 07/09/97
 * @author Jenny Colvard
 */

public class Z39response { 
/**
 * Value for type meaning this response was not in BER format.
 */
	public static final int not_BER              = 0;
/**
 * Value for type meaning this response was of an unknown type.
 */
	public static final int zClient_unknown        = 1;
/**
 * Value for type meaning this response was an Init.
 */
	public static final int zClient_init           = 2;
/**
 * Value for type meaning this response was a Search.
 */
	public static final int zClient_search         = 3;
/**
 * Value for type meaning this response was a Present.
 */
	public static final int zClient_present        = 4;
/**
 * Value for type meaning this response was a Scan.
 */
	public static final int zClient_scan           = 5;

/**
 * Value for type meaning this response was a Extended Service Docorder FS Price Request
 */

        private static final int zClient_docorder_price = 6;
/**
 * Value for type meaning this response was a Extended Service Docorder Item Order
 */
        private static final int zClient_docorder       = 7;

/**
 * Value for type meaning this response was a Sort
 */
        private static final int zClient_sort           = 8;


/**
 * Type of the last response examined.
 */
	public int type;
/**
 * InitResponse. If this is !null, Response will use this object. If
 * this is null, Response will create the object and use it.
 */
	public Z39init init = null;
/** 
 * ScanResponse. If this is !null, Response will use this object. If
 * this is null, Response will create the object and use it.
 */
	public Z39scan scan = null;
/** 
 * SearchResponse. If this is !null, Response will use this object. If
 * this is null, Response will create the object and use it.
 */
	public Z39search search = null;
/** 
 * PresentResponse. If this is !null, Response will use this object. If
 * this is null, Response will create the object and use it.
 */
	public Z39present present = null;

/**
 * SortResponse. If this is !null, Response will use this object. If
 * this is null, Response will create the object and use it.
 */
        public Z39sort sort = null;

	//private Z39order order = null;
	//private Z39orderPrice orderPrice = null;

	// The caller can fill in the Z39 command values above
  	// or Response() will create new ones (if needed) to hold
	// the response information.

	// this guy will need to be able to return a failure.
	// does he do it by throwing an exception?
	// some of the DataDir constructors are in the same boat.
/**
 * Z39session
 */
	public Z39session zsession = null;

	public Z39response() {
	}

	public Z39response(Z39session z) {
	    zsession = z;
	}
/**
 * Determines the type and processes a Z39.50 Response.
 * @param response - BerString containing Response.
 */
	public void Response(BerString response) throws AccessControl {
	    DataDir rspdir = new DataDir(response);
	    Response(rspdir);
	}

/**
 * Determines the type and processes a Z39.50 Response.
 * @param response - DataDir containing Response.
 */
	public void Response(DataDir response) throws AccessControl {
	    type = zClient_unknown;
	
	    switch(response.fldid())
	    {
		case Z39api.initResponse:
		    type=zClient_init;
		    if (init == null)
			init = new Z39init(zsession);
		    init.Response(response);
		    break;
		case Z39api.searchResponse:
		    type=zClient_search;
		    if (search == null)
			search = new Z39search(zsession);
		    search.Response(response);
		    break;
		case Z39api.presentResponse:
		    type=zClient_present;
		    if (present == null)
			present = new Z39present(zsession);
		    present.Response(response);
		    break;
		case Z39api.scanResponse:
		    type=zClient_scan;
		    if (scan == null)
			scan = new Z39scan(zsession);
		    scan.Response(response);
		    break;
                case Z39api.sortResponse:
                    type=zClient_sort;
                    if (sort == null)
                        sort = new Z39sort(zsession);
                    sort.Response(response);
                    break;
/*
		case Z39api.extendedserviceResponse:
		    for (DataDir tdir = response.child();
			 tdir != null; tdir = tdir.next())
		    	if (tdir.fldid() == Z39api.esTaskPackage)
			    break;
		    if (tdir != null)
			for (tdir=tdir.child();tdir!=null;tdir=tdir.child())
			    if (tdir.fldid()==ASN1.OBJECTIDENTIFIER)
			    {
			        String oid = tdir.dgetoid();
				if (oid.equals(EXTENDED_SERVICE_ORDER))
				{
				    type=zClient_docorder;
				    orderRsp = new orderResponse(response);
				}
				else
				    if (oid.equals(OCLC.EXTENDED_SERVICE_PRICE))
				    {
					type=zClient_docorder_price;
					orderPriceRsp = new 
					    orderPriceResponse(response);
				    }
				break;
		 	    }
		    break;
*/
	    }
	}

	public String toString() {
	    StringBuffer str = new StringBuffer(
			"Z39response: type = " + type);

	    if (init != null)
		str.append(init.toString());
	    if (scan != null)
		str.append(scan.toString());
	    if (search != null)
		str.append(search.toString());
	    if (present != null)
		str.append(present.toString());
	    return str.toString();
	}
}
