package com.theerayut.app.service;

import com.google.gson.reflect.TypeToken;
import com.theerayut.app.AppData;
import com.theerayut.app.model.Person;
import com.theerayut.app.model.Staff;
import com.theerayut.app.util.JsonStorage;

import java.util.ArrayList;
import java.util.List;

//คลาสพนักงาน
public class StaffService {

    public enum Roles{
        Admin,
        Staff
    }
     
    private List<Staff> staffList;

    public StaffService() {
        staffList = JsonStorage.load("staffList.json", new TypeToken<List<Staff>>(){}.getType());
        if (staffList == null) {
            staffList = new ArrayList<>();
            staffList.add(new Staff("staff1", "12345", Person.Roles.Staff));
            staffList.add(new Staff("admin1", "12345", Person.Roles.Admin));
        }
        //AppData.loginStaffData = JsonStorage.load("lastStaffLogin.json", new TypeToken<Staff>(){}.getType());
    }

    public List<Staff> getStaffList(){
        return staffList;
    }

    public void addStaff(Staff staff){
        staffList.add(staff);
        JsonStorage.save(staffList, "staffList.json");
    }

    public void removeStaff(Staff staff){
        staffList.remove(staff);
        JsonStorage.save(staffList, "staffList.json");
    }

    public Roles login(String username, String password) {
        for (Staff staff : staffList) {
            if (staff.getUsername().equals(username) && staff.getPassword().equals(password)) {
                //JsonStorage.save(staff, "lastStaffLogin.json");
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