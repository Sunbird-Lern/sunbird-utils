package org.sunbird.common.util;


import org.apache.commons.lang3.StringUtils;

/**
 * this class is used to match the identifiers.
 *
 */
public class Matcher {

    /**
     * this method will match the two arguments , equal or not
     * @param i1
     * @param i2
     * @return boolean
     */
    public static boolean matchIdentifiers(String i1,String i2){
        return StringUtils.equalsIgnoreCase(i1,i2);
    }
}
