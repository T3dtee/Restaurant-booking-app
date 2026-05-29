package com.theerayut.app.service;

import com.theerayut.app.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class LoginService {
    private List<Customer> customers = new ArrayList<>();

    private void addCustomer(Customer customer){
        customers.add(customer);
    }
    public List<Customer> getCustomersList() {
        return customers;
    }

    public Customer customerLogin(String name, String phone){
        for (Customer c : customers){
            if (c.getName().equals(name) && c.getPhone().equals(phone)){ //เคย login แล้วทุกอย่างตรง
                return c;
            }
            else if (c.getPhone().equals(phone) && !c.getName().equals(name)){ //เบอร์ถูกใช้ไปแล้ว แต่ชื่อไม่ตรง
                return null;
            }
        }
        // ไม่เคย login
        Customer c = new Customer(name, phone);
        addCustomer(c);
        return c;
    }

    public Customer findCustomerById(String id){
        for (Customer c : customers){
            if (c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }
}