/*
 * DataPair.java
 ************************************************************************/

package ORG.oclc.util;

import java.net.URLEncoder;

/**
 * The DataPair class keeps track of name/value pairs
 */

public class DataPair {

    private String name=null, value=null;

    /**
     * Constructs a DataPair object with name = "" and value = "".
     */
    public DataPair () {
        this("","");
    }

    /**
     * Constructs a DataPair object with name = n and value = "".
     * @param n the name for the DataPair object.
     */
    public DataPair (String n) {
        this(n, "");
    }

    /**
     * Constructs a DataPair object with name = n and value = v.
     * @param n the name for the DataPair object.
     * @param v the value for the DataPair object.
     */
    public DataPair(String n, String v) {
        name = n;
        value = v;
    }

    /**
     * Creates a String representation of the DataPair object.
     * @return the String representation of the DataPair name and value.
     */
    public String toString() {
        return toString("=", false);
    }

    /**
     * Creates a String representation of the DataPair object.
     * @return the String representation of the DataPair name and value.
     */
    public String toString(String separator) {
        return toString(separator, false);
    }

    /**
     * Creates a URL safe String representation of the DataPair object using
     * the input parameter String for the separator value.
     * @return the String representation of the DataPair name and value.
     */
    public String toString(String separator, boolean encode) {
        if (encode)
            return URLEncoder.encode(name)+separator+URLEncoder.encode(value);
        else 
            return name+separator+value;
    }

    /**
     * Sets the value of the existing DataPair object using the input parameter
     * String value.
     * @param v the value for the DataPair object.
     */
    public void setValue(String v) {
        value = v;
    }

    /**
     * Returns the value of the DataPair object.
     * @return the value of the DataPair object.
     */
    public String get() {
        return value;
    }

    /**
     * Returns the name in the DataPair object.
     * @return the name in the DataPair object.
     */
    public String name() {
        return name;
    }

    /**
     * Tests to see if the DataPair object value is empty.
     * @return true if the value is empty or false otherwise.
     */
    public boolean isEmpty () {
        return (value.equals("") || value == null);
    }

    /**
     * Tests to see if the DataPair object value is equal to the input
     * parameter String value.
     * @param v the String value to test for equality.
     * @return true if the value is equal or false otherwise.
     */
    public boolean equals(String v){
        return value.equals(v);
    }
}
