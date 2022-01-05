package com.badgr.scoutClasses;

import java.util.ArrayList;

public class scoutPerson {

    private String fName, lName;
    private String user, pass;
    private int age, troop, userID;
    private boolean isSM;



    public scoutPerson(String fn, String ln, String u, String p, int a, int t, int id, boolean iSM) {
        fName = fn;
        lName = ln;
        age = a;
        troop = t;
        user = u;
        userID = id;
        pass = p;
        isSM = iSM;

    }

    //CHANGE BACK TO 0000000000 TODO

    public scoutPerson() {
        this("defaultf", "defaultl", "user", "pass", 0, 0, 1149, false);
    }

    public scoutPerson(ArrayList<String> list) {
        String uID = list.get(0);
        userID = Integer.parseInt(uID);

        fName = String.valueOf(list.get(1));
        lName = String.valueOf(list.get(2));
        user = String.valueOf(list.get(3));

        String a = list.get(5);
        age = Integer.parseInt(a);

        String sm = list.get(6);
        isSM = Integer.parseInt(sm) == 1;

        String t = list.get(7);
        troop = Integer.parseInt(t);

    }

    public int getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = Integer.parseInt(age);
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String name) {
        this.fName = name;
    }

    public int getTroop() {
        return troop;
    }

    public void setTroop(String troop) { this.troop = Integer.parseInt(troop);}

    public void setUser(String user) {this.user = user;}

    public String getUser() {
        return user;
    }

    public void setPass(String pass) {this.pass = pass;}

    public String getPass() {
        return pass;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public boolean isSM() {
        return isSM;
    }

    public void setSM(boolean SM) {
        isSM = SM;
    }

    public int getUserID() { return userID; }

    public void setUserID(int userID) { this.userID = userID; }
}
