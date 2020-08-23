package com.example.guff_gaf;

public class Chat {
    public Chat() {
    //empty constructor needed.
    }
    private String seen;
    private long timestamp;

    @Override
    public String toString() {
        return "Chat{" +
                "seen='" + seen + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
