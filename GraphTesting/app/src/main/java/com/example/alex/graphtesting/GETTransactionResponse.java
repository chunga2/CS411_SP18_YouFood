package com.example.alex.graphtesting;

/**
 * Created by alex on 3/21/18.
 */


public class GETTransactionResponse {
    private String amount;
    private String date;
    private String address;
    private String name;
    private String userEmail;

    public GETTransactionResponse (String amo, String d, String add, String n, String u){
        amount = amo;
        date = d;
        address = add;
        name = n;
        userEmail = u;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getUserEmail() {
        return userEmail;
    }
}

