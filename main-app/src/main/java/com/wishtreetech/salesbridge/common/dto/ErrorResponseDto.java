package com.wishtreetech.salesbridge.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * The type Error response dto.
 */
public class ErrorResponseDto {
    private String message;
    private int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    /**
     * Instantiates a new Error response dto.
     *
     * @param message the message
     * @param status  the status
     */
    public ErrorResponseDto(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = new Date();
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
}
