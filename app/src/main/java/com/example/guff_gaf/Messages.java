package com.example.guff_gaf;

public class Messages  {
    public Messages() {
        //empty constructor needed.
    }
    private String msg ,from,seen,type;
    private Long timestamp;

    public Messages(String type) {
        this.type = type;
    }

    public Messages(String msg, String from, String seen, Long timestamp) {
        this.msg = msg;
        this.from = from;
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
