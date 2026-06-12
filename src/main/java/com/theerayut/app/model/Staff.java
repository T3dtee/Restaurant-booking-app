package com.theerayut.app.model;

public class Staff extends Person {
    private final String username;
    private final String password;

    public Staff(String username, String password, Roles role) {
        super(username, username, role);
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }

}
