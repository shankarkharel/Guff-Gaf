package com.example.guff_gaf;

public class Friend_Request {
    String request_type;

    @Override
    public String toString() {
        return "Friend_Request{" +
                "request_type=" + request_type +
                '}';
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
