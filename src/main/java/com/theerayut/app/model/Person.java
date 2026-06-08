package com.theerayut.app.model;

public class Person {
     
    protected String name;
    protected String id;
    protected Roles role;

    public enum Roles {
        Staff,
        Admin,
        Customer,
    }

    protected Person(String name, Roles role) {
        this.name = name;
        this.role = role;
    }
    protected Person(String name, String phone) {
        this.name = name;
        this.id = phone;
        role = Roles.Customer;
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    // เกิด polymorphism
    public Roles getRole() {
        return role;
    }
}
