package com.example.project114.backend;

public class StaffLogin {
    private String username;
    private String password;

    public StaffLogin(String username, String password){
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
