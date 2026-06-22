package com.theerayut.app.service;

import com.google.gson.reflect.TypeToken;
import com.theerayut.app.AppData;
import com.theerayut.app.model.Customer;
import com.theerayut.app.util.JsonStorage;

import java.util.ArrayList;
import java.util.List;

public class LoginService {
    private List<Customer> customers;

    public LoginService() {
        customers = JsonStorage.load("customers.json", new TypeToken<List<Customer>>(){}.getType());
        if (customers == null) {
            customers = new ArrayList<>();
        }
        AppData.loginUserData = JsonStorage.load("lastCustomerLogin.json", new TypeToken<Customer>(){}.getType());
    }

    private void addCustomer(Customer customer){
        customers.add(customer);
        JsonStorage.save(customers, "customers.json");
    }
    public List<Customer> getCustomersList() {
        return customers;
    }

    public Customer customerLogin(String name, String phone){
        for (Customer c : customers){
            if (c.getName().equals(name) && c.getPhone().equals(phone)){//เคย login แล้วทุกอย่างตรง
                JsonStorage.save(c, "lastCustomerLogin.json");
                return c;
            }
            else if (c.getPhone().equals(phone) && !c.getName().equals(name)){ //เบอร์ถูกใช้ไปแล้ว แต่ชื่อไม่ตรง
                return null;
            }
        }
        // ไม่เคย login
        Customer c = new Customer(name, phone);
        c.setFirstTimeLogin(true);
        addCustomer(c);
        JsonStorage.save(c, "lastCustomerLogin.json");
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