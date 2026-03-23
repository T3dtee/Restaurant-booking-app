package com.example.project114.backend;

//คลาสพนักงาน
public class Staff {
     
    //ประกาศลิสชื่อพนักงาน
    private String[] staffNames = {
        "staff1",
        "staff2",
        "staff3",
        "staff4",
        "staff5"
    };

    private final String commonPassword = "12345";

    public boolean login(String username, String password) {
//ตรวจรหัสก่อน
        if (!commonPassword.equals(password)) {
            return false;
        }

        for (int i = 0; i < staffNames.length; i++) {
            if (staffNames[i].equals(username)) {
                return true;
            }
        }

        return false;
    }
}