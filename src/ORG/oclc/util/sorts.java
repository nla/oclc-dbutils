
package ORG.oclc.util;

import java.lang.*;

/** sorts is a class providing general routines for sorting objects.
 * @version @(#)sorts.java	1.1 04/29/96
 * @author Jenny Colvard
 *
 * Added shellSort method - Wei Liu 12/10/98
 */

public abstract class sorts {

	public static void bubblesort(Object sort[]) {

            bubblesort(sort, true);
	}

	public static void bubblesort(Object sort[], boolean caseSensitive) {
	    Object temp;
	    int count;
	    int n;
	    n = 0; count = 1;

	    while (n < sort.length - 1 || count != 0) {
		count = 0;
		for (int j=0; j<sort.length-1; j++) {
		    if (caseSensitive) {
			if (((String)sort[j]).compareTo(
						 ((String)sort[j+1])) > 0)
			    {
				temp = sort[j];
				sort[j] = sort[j+1];
				sort[j+1] = temp;
				count++;
			    }
		    }
		    else {
			if (((String)sort[j]).toLowerCase().compareTo(
				 ((String)sort[j+1]).toLowerCase()) > 0)
			    {
				temp = sort[j];
				sort[j] = sort[j+1];
				sort[j+1] = temp;
				count++;
			    }
		    }
		}
		n++;
	    }
	}

  /**
   * ShellSort is a method providing shell sort algorithm for sorting objects.
   */

   public abstract int compare(sorts b);

   public static void shellSort(sorts[] a, int num_elements_to_sort)
   {  int n = num_elements_to_sort;
      int incr = n / 2;
      while (incr >= 1)
      {  for (int i = incr; i < n; i++)
         {  sorts temp = a[i];
            int j = i;
            while (j >= incr
               && temp.compare(a[j - incr]) < 0)
            {  a[j] = a[j - incr];
               j -= incr;
            }
            a[j] = temp;
         }
         incr /= 2;
      }
   }

   public static void shellSort(Object keys[]) {
        int i, j, h;
        String w;
        int N = keys.length;

        for (h = 1; h <= N/9; h = 3*h+1) ;
        for ( ; h > 0; h /= 3)
            for (i = h; i<N; i += 1)
            {
//System.out.println("i=" + i + ";h=" + h + ";N=" + N);
                w = keys[i].toString();
                if (w != null) {
                    for (j = i; j>=h && keys[j-h].toString().compareTo(w) > 0; j -=
 h)
                        keys[j] = keys[j-h];
                    keys[j] = w;
                }
            }
   }
}


