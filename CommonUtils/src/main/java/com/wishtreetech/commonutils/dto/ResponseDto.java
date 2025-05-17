package com.wishtreetech.commonutils.dto;

/**
 * The type Response dto.
 *
 * @param <T> the type parameter
 */
public class ResponseDto<T> {
    private boolean status;
    private T data;

    /**
     * Instantiates a new Response dto.
     */
    public ResponseDto() {}

    /**
     * Instantiates a new Response dto.
     *
     * @param status the status
     * @param data   the data
     */
    public ResponseDto(boolean status, T data) {
        this.status = status;
        this.data = data;
    }

    /**
     * Is status boolean.
     *
     * @return the boolean
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(T data) {
        this.data = data;
    }
}
