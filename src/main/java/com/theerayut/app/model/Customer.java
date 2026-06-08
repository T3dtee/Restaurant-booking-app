package com.theerayut.app.model;

//คลาสลูกค้า
public class Customer extends Person{
    
    private final String phone;
    private transient boolean isFirstTimeLogin = false;

    public Customer(String name, String phone) {
        super(name, phone);   // เรียก constructor ของ Person
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isFirstTimeLogin() { return isFirstTimeLogin; }
    public void setFirstTimeLogin(boolean isNewInstance) { this.isFirstTimeLogin = isNewInstance; }
    
}
