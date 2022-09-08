package com.example.music.tools;

import lombok.Data;

//统一的响应体类工具类
@Data
public class ResponseBodyMessage<T> {

    private int status;//状态码
    private String message;//返回的信息
    private T data;//返回给前端的数据

    public ResponseBodyMessage(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


}
