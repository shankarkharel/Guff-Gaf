package com.example.guff_gaf;

import android.net.Uri;

import java.net.URL;

public class Users {

    public  String name;
    public  String status;
    public String dp;
   //public URL Dp;

    public Users() {
    }

    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", dp='" + dp + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) { this.status = status; }

   public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }
}
