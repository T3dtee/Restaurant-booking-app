package com.theerayut.app.model;

public abstract class Person {
     
    protected String name;
    protected String id;

    protected Person(String name) {
        this.name = name;
    }
    protected Person(String name, String phone) {
        this.name = name;
        this.id = phone;
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    // เกิด polymorphism
    public abstract String getRole();
}
