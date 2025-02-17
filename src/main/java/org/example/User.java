package org.example;

public class User {
    public String password;
    public Wallet wallet;

    public User(String password) {
        this.password = password;
        this.wallet = new Wallet();
    }
}
