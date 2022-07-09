package org.ncgr.datastore.validation;

/**
 * Just a wrapper for Exception to throw when a particular validation fails.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
    
}
