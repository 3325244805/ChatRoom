package com.example.chatsys.dto;


import lombok.Data;

@Data
public class Result {
    private boolean success;
    private String message;
    private Object data;

    public static Result success(Object data) {
        Result result = new Result();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static Result fail(String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

}