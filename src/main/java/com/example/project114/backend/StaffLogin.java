package com.example.project114.backend;

public class StaffLogin extends Person {
    private String username;
    private String password;

    public StaffLogin(String username, String password){
        super(username);
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }

    public String getRole() {
        return "Staff";
    }

}
