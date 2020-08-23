package com.example.guff_gaf;

public class Friends {
    public String date;

    public Friends() {
        //empty constructor needed
    }

    @Override
    public String toString() {
        return "Friends{" +
                "date='" + date + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
