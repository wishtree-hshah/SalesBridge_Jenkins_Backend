package com.wishtreetech.rocketreach.exception;

/**
 * The type Api response exception.
 */
public class ApiResponseException extends RuntimeException {
    private final int statusCode;

    /**
     * Instantiates a new Api response exception.
     *
     * @param statusCode   the status code
     */
    public ApiResponseException(int statusCode, String errorMsg) {
        super(errorMsg);
        this.statusCode = statusCode;
    }

    /**
     * Gets status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
