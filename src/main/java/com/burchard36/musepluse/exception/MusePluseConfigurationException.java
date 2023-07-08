package com.burchard36.musepluse.exception;

/**
 * Simple exception to throw when there is a user-generated
 * exception for configs. Serves no purpose other than looking nicer in code
 */
public class MusePluseConfigurationException extends RuntimeException {
    public MusePluseConfigurationException(final String message) {
        super(message);
    }
}
