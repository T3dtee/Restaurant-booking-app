package com.theerayut.app.model;

public class Person {
     
    protected String name;

    protected Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // เกิด polymorphism
    public String getRole() {
        return "Person";
    }
}
