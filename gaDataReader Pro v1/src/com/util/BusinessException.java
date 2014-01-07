/*
 * BusinessException.java
 *
 * Created on April 6, 2006, 12:49 PM
 *
 */

package com.util;

public class BusinessException
        extends Exception {
    
    
    public BusinessException(String text) {
        super(text);
    }
    
    public BusinessException(String text, StackTraceElement[] elements) {
        super(text);
        StringBuffer errors = new StringBuffer();
        errors.append(text);
        errors.append("\n");
        for (int i = 0; i < elements.length; i++) {
            errors.append("      at ");
            errors.append(elements[i].toString());
            errors.append("\n");
        }
    }
    
}
