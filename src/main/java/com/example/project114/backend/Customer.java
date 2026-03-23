package com.example.project114.backend;

//คลาสลูกค้า
public class Customer extends Person{
    
    private String phone;

    public Customer(String name, String phone) {
        super(name);   // เรียก constructor ของ Person
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String getRole() {
        return "Customer";
    }
    
}
