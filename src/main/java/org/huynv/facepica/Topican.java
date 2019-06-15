package org.huynv.facepica;

public class Topican {
    private int id;
    private String account;
    private String email;

    public Topican(int id_arg, String account_arg, String email_arg) {
        this.id = id_arg;
        this.account = account_arg;
        this.email = email_arg;
    }

    public int getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAccount() {
        return this.account;
    }
}
