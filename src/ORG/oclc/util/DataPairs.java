/*
 * DataPairs.java
 ************************************************************************/

package ORG.oclc.util;

import java.util.Vector;
import java.util.Enumeration;

/**
  * The DataPairs class defines a vector where DataPair objects 
  * will be stored.
  *
  * @see DataPair
  */
public class DataPairs extends Vector {

    /**
     * Constructs a DataPairs object vector of initial size 10.
     */
    public DataPairs(){
        super(10, 0);
    }

    /**
     * Constructs a DataPairs object vector of initial size as specified 
     * in the input parameter.
     * @param initialCapacity the initial size of the DataPairs vector.
     */
    public DataPairs(int initialCapacity){
        super(initialCapacity, 0);
    }
  
    /**
     * Constructs a DataPairs object vector of initial size and incremental 
     * size as specified in the input parameters.
     * @param initialCapacity the initial size of the DataPairs vector.
     * @param capacityIncrement the increment size when increasing 
     * the current capacity of the DataPairs vector.
     */
    public DataPairs(int initialCapacity, int capacityIncrement) { 
        super(initialCapacity, capacityIncrement);
    }

    /**
     * Tests if the specified input DataPair name is contained in this vector.
     * @param   elem the name of the DataPair object.
     * @return  <code>true</code> if the DataPair object is a component in
     *          this vector; <code>false</code> otherwise.
     */
    public final boolean contains(String elem) {
        return indexOf(elem, 0) >= 0;
    }

    /**
     * Adds the input name/value Strings to the end of this DataPairs vector
     * as a DataPair object.
     * @param  name  the name of the DataPair object.
     * @param  value the value of the DataPair object.
     */
    public final void addElement(String name, String value) {
        super.addElement( new DataPair(name, value) );
    }

    /**
     * Searches for the first occurrence of the input DataPair name
     * and tests for equality using the <code>String equals</code> method. 
     *
     * @param   elem the name of the DataPair object.
     * @return  the index of the first occurrence of the DataPair object in
     *          this vector at position <code>index</code> or later in the
     *          vector; returns <code>-1</code> if the DataPair object
     *          is not found.
     */
    public final int indexOf(String elem) {
        return indexOf(elem, 0);
    }

    /**
     * Searches for the first occurrence of the input DataPair name, beginning 
     * the search at the input <code>index</code> value, and testing 
     * for equality using the <code>String equals</code> method. 
     *
     * @param   elem  the name of the DataPair object.
     * @param   index the index in the vector to start the search.
     * @return  the index of the first occurrence of the DataPair object in
     *          this vector at position <code>index</code> or later in the
     *          vector; returns <code>-1</code> if the DataPair object
     *          is not found.
     */
    public final int indexOf(String elem, int index) {
        DataPair w = null;

        for (int i=index; i< elementCount ; i++) {
            w = (DataPair)elementData[i]; 
            if ((w.name()).equals(elem))
                return i;
        }
        return -1;
    }

    /**
     * Searches backwards for the input DataPair name by starting 
     * from the end of the vector.
     *
     * @param   elem the name of the DataPair object.
     * @return  the index of the last occurrence of the DataPair object in this
     *          vector; <code>-1</code> if the DataPair object is not found.
     */
    public final int lastIndexOf(String elem) {
        return lastIndexOf(elem, elementCount-1);
    }

    /**
     * Searches backwards for the input DataPair name by starting 
     * from the input index value within the vector.
     *
     * @param   elem the name of the DataPair object.
     * @param   index the index in the vector to start the search.
     * @return  the index of the last occurrence of the specified object in
     *          this vector at position less than <code>index</code> in the
     *          vector; <code>-1</code> if the DataPair object is not found.
     */
    public final int lastIndexOf(String elem, int index) {
        DataPair w = null;

        for (int i = index ; i >= 0 ; i--) {
            w = (DataPair)elementData[i]; 
            if ((w.name()).equals(elem)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for the input DataPair value and returns the DataPair String 
     * name.
     *
     * @param  value the value of the DataPair object.
     * @return the String name of the DataPair object;
     *         a <code>null</code> if a DataPair object is not found.
     */
    public final String getKey(String value) {
        DataPair w;
        int      i;

        for (i = 0 ; i < elementCount ; i++) {
            w = (DataPair)elementData[i];
            if ((w.get()).equals(value)) {
                return w.name();
            }
        }
        return null;
    }

    /** 
     * Searches for the input DataPair value and returns the DataPair String 
     * name.  Same as getKey except is case insensitive. 
     * 
     * @param  value the value of the DataPair object. 
     * @return the String name of the DataPair object; 
     *         a <code>null</code> if a DataPair object is not found. 
     */ 
    public final String getKeyCI(String value) { 
        DataPair w; 
        int      i;
 
        value = value.toLowerCase(); 
 
        for (i = 0 ; i < elementCount ; i++) { 
            w = (DataPair)elementData[i]; 
            if ((w.get()).equalsIgnoreCase(value)) {
                return w.name(); 
            } 
        }
        return null;
    }

    /**
     * Searches for the input DataPair name and returns the DataPair String
     * value.
     *
     * @param  elem the name of the DataPair object.
     * @return the String value of the DataPair object;
     *         a <code>null</code> if a DataPair object is not found.
     */
    public final String get(String elem) {
        int i = indexOf(elem, 0);

        if (i == -1)
            return null;
        else {
            DataPair w = (DataPair)elementData[i];
            return w.get();
        }
    }

    /**
     * Searches for the input DataPair name and returns the DataPair String 
     * value or the input parameter default String value.
     *
     * @param elem the name of the DataPair object.
     * @param def the default String value.
     * @return the String value of the DataPair object;
     *          or the default String value if the DataPair object
     *          is not found.
     */
    public final String get(String elem, String def) {
        int i = indexOf(elem, 0);
 
        if (i == -1)
            return def;
        else {
            DataPair w = (DataPair)elementData[i];
            return w.get();
        }
    }

    /**
     * Searches for the input DataPair name and returns the DataPair
     * integer value or a default integer value if not found.
     *
     * @param  elem the name of the DataPair object.
     * @param  def the default integer value.
     * @return the integer value of the DataPair object;
     *         or the default integer value if the DataPair object
     *         is not found.
     */
    public final int getInt(String elem, int def) {
        int value=def;

        String s1;
        // Get string value of widget
        if ((s1 = get(elem)) != null) {
            try {
                value  = Integer.parseInt(s1);
            } catch (NumberFormatException e) {
                value = def;
            }
        }
        return value;
    }

    /**
     * Searches the vector for ALL occurrences of the input DataPair name
     * and builds a space separated String of all the DataPair object values.
     *
     * @param   elem the name of the DataPair object.
     * @return  a String containing all the DataPair object values 
     *          found in the vector;
     *          <code>null</code> if the DataPair object(s) is not found.
     * @deprecated Incorrectly named.
     * @see getAll 
     */
    public final String getall(String elem) {
        return getAll(elem);
    }

    /**
     * Searches the vector for ALL occurrences of the input DataPair name
     * and builds a space separated String of all the DataPair object values.
     *
     * @param   elem the name of the DataPair object.
     * @return  a String containing all the DataPair object values 
     *          found in the vector;
     *          <code>null</code> if the DataPair object(s) is not found.
     */
    public final String getAll(String elem) {
        StringBuffer buffer = null;
        Vector       all = getAllVector(elem);

        if (all != null) {
            buffer = new StringBuffer();

            for (Enumeration e=all.elements(); e.hasMoreElements(); ) {
                if (buffer.length() > 0)
                    buffer.append(" ");
                buffer.append((String)e.nextElement());
            }
            return buffer.toString();
        } else 
            return null;
    }

    /**
     * Searches the vector for ALL occurrences of the input DataPair name
     * and returns the values in a vector.
     *
     * @param   elem the name of the DataPair object.
     * @return  a Vector containing all the DataPair object values
     *          found in the vector;
     *          <code>null</code> if the DataPair object(s) is not found.
     */
    public final Vector getAllVector(String elem) {
        DataPair w;
        int      next = indexOf(elem, 0);
        Vector   all = null;

        if (next != -1) {
            all = new Vector();

            while (next != -1) {
                w =  (DataPair)elementData[next];
                all.addElement(w.get());
                next = indexOf(elem, next+1);
            }
        }

        return all;
    }

    /**
     * Searches the vector for ALL occurrences that start with the input
     * DataPair name and returns the values in a vector.
     *
     * @param   elem the stem name of the DataPair object.
     * @return  a Vector containing all the DataPair object values
     *          found in the vector;
     *          <code>null</code> if the DataPair object(s) is not found.
     */
    public final Vector getAllStartsWithVector(String elem) {
        DataPair w;
        Vector   all = null;
 
        for (int i=0; i< elementCount ; i++) { 
            w = (DataPair)elementData[i];  
            if ((w.name()).startsWith(elem)) {
                if (w.get().length() > 0) {
                    if (all == null)
                        all = new Vector();
                    all.addElement(w.get());
                }
            }
        }

        return all;
    }

    /**
     * Replaces the DataPair value of the first occurrence of the 
     * DataPair object with the input DataPair name. A new DataPair object
     * is created if an existing match is not found.
     *
     * @param  name the name of the DataPair object.
     * @param  value the new value for the DataPair object.
     */
    public final void replaceElement(String name, String value){
        int i = indexOf(name, 0);

        if (i == -1) {
            super.addElement( new DataPair(name, value) );
        }
        else {
            DataPair w = (DataPair)elementData[i];
            w.setValue(value);
        }
    }

    /**
     * Sets the DataPair object at the specified <code>index</code> of this 
     * vector to be the input DataPair name and value parameters. 
     * The previous DataPair object at that position is discarded.
     * <p>
     * The index must be a value greater than or equal to <code>0</code> 
     * and less than the current size of the vector. 
     *
     * @param  name the new name of the DataPair object.
     * @param  value the new value for the DataPair object.
     * @param  index the index of the DataPair object to replace.
     */
    public final void setElementAt(String name, String value, int index) {
        super.setElementAt(new DataPair(name, value), index);
    }

    /**
     * Inserts a new DataPair object at the specified <code>index</code>
     * using the input DataPair name and value parameters.
     * 
     * <p>Each DataPair object in this vector with 
     * an index greater or equal to the specified <code>index</code> is 
     * shifted upward to have an index one greater than the value it had 
     * previously. 
     * <p>
     * The index must be a value greater than or equal to <code>0</code> 
     * and less than or equal to the current size of the vector. 
     *
     * @param  name the new name of the DataPair object.
     * @param  value the new value for the DataPair object.
     * @param  index the index of the DataPair object to insert.
     */
    public final void insertElementAt(String name, String value, int index) {
        super.insertElementAt(new DataPair(name, value), index);
    }

    /**
     * Removes the first occurrence of the DataPair object matching
     * the input DataPair name.
     *
     * @param elem the name of the DataPair object.
     */
    public final void remove(String elem) {
        int i = indexOf(elem, 0);

        if (i == -1)
            return;
        else {
            super.removeElementAt(i);
        }
    }

    /**
     * Removes all occurrences of the DataPair object matching
     * the input DataPair name.
     *
     * @param elem the name of the DataPair object.
     */
    public final void removeAll(String elem) {
        int i = indexOf(elem, 0);
        
        while (i != -1) {
            super.removeElementAt(i);
            i = indexOf(elem, 0);
        }
    }

    /**
     * Removes all occurrences of the DataPair object matching
     * the input DataPair name.
     *
     * @param elem the name of the DataPair object.
     * 
     * @deprecated Incorrectly named.
     * @see removeAll      
     */
    public final void removeall(String elem) {
        removeAll(elem);
    }

    /**
     * Returns the String representation of this object.
     *
     * @param separator the string to separate DataPair Name and Value with.
     * @param lineSeparator the string to separate each DataPair in the vector 
     *                      with.
     *
     * @return the String representation of this object.
     */
    public final synchronized String toString(String separator,
      String lineSeparator, boolean encode) {
        DataPair     w = null;
        Enumeration  e = elements();
        int          max = size() - 1;
        String       nL = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();

        for (int i = 0 ; i <= max ; i++) {
            w = (DataPair)e.nextElement();
            buf.append(lineSeparator);
            buf.append(w.toString(separator, encode));
        }

        return buf.toString();
    }

    /**
     * Returns the String representation of this object.
     * @return the String representation of this object.
     */
    public final synchronized String toString(String separator) {
        String nL = System.getProperty("line.separator");
            
        return this.toString(separator, nL, false);
    }

    /**
     * Returns the String representation of this object.
     * @return the String representation of this object.
     */
    public final synchronized String toString(String separator, 
      String lineSeparator) {
        String nL = System.getProperty("line.separator");
            
        return this.toString(separator, lineSeparator, false);
    }
}

