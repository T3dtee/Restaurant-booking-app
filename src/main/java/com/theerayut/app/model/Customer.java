package com.theerayut.app.model;

//คลาสลูกค้า
public class Customer extends Person{
    
    private final String phone;

    public Customer(String name, String phone) {
        super(name, phone);   // เรียก constructor ของ Person
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
