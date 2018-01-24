package com.factory.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    public ApiResponse() {
    }

    public ApiResponse(RespStatus status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    private String message;

    private RespStatus status;

    private Object data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RespStatus getStatus() {
        return status;
    }

    public void setStatus(RespStatus status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    enum RespStatus {
        OK, ERROR
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(RespStatus.ERROR, null, message);
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(RespStatus.OK, data, null);
    }

}
