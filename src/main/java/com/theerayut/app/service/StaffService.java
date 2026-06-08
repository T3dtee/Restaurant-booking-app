package com.theerayut.app.service;

import com.theerayut.app.model.Person;
import com.theerayut.app.model.Staff;

import java.util.ArrayList;
import java.util.List;

//คลาสพนักงาน
public class StaffService {

    public enum Roles{
        Admin,
        Staff
    }
     
    private List<Staff> staffList = new ArrayList<>();

    public StaffService() {
        staffList.add(new Staff("staff1", "12345", Person.Roles.Staff));
        staffList.add(new Staff("admin1", "12345", Person.Roles.Admin));
    }

    public List<Staff> getStaffList(){
        return staffList;
    }

    public void addStaff(Staff staff){
        staffList.add(staff);
    }

    public void removeStaff(Staff staff){
        staffList.remove(staff);
    }

    private final String commonPassword = "12345";

    public Roles login(String username, String password) {
        for (Staff staff : staffList) {
            if (staff.getUsername().equals(username) && staff.getPassword().equals(password)) {
                switch (staff.getRole()) {
                    case Admin -> {
                        return  Roles.Admin;
                    }
                    case Staff -> {
                        return  Roles.Staff;
                    }
                }
            }
        }
        return null;
    }
}