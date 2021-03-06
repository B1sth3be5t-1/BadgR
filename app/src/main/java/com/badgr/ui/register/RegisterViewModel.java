package com.badgr.ui.register;


import android.util.Patterns;

import androidx.lifecycle.ViewModel;


public class RegisterViewModel extends ViewModel {

    //uses class's checks to return whether the register info is valid
    public static boolean registerDataChanged(String fName, String lName, String user, String pass, String age, String troop) {
        return isFNameValid(fName) && isLNameValid(lName) && isUserNameValid(user) && isAgeValid(age) && isTroopValid(troop) &&
                passNumberValid(pass) && passLengthValid(pass) && passUpperValid(pass);
    }

    //checks whether the username is formatted like an email correctly
    public static boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(username).matches() && username.contains("@");

    }

    //checks if there is an uppercase letter in the password
    public static boolean passUpperValid(String p) {
        for (int i = 0; i < p.length(); i++) {
            if (Character.isUpperCase(p.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    //checks if there is a number in the password
    public static boolean passNumberValid(String p) {
        for (int i = 0; i < p.length(); i++) {
            if (Character.isDigit(p.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    //checks if password.length() >= 8 chars
    public static boolean passLengthValid(String p) {
        return p.length() >= 8;
    }

    //checks if 0 < age <= 120
    public static boolean isAgeValid(String a) {
        int age;
        try {
            age = Integer.parseInt(a);
        } catch (NumberFormatException e) {
            age = -1;
        }
        return age > 0 && age <= 120;
    }

    //checks if  0 < troop <= 9999
    public static boolean isTroopValid(String t) {
        int troop;
        try {
            troop = Integer.parseInt(t);
        } catch (NumberFormatException e) {
            troop = -1;
        }
        return troop > 0 && troop <= 9999;
    }

    //checks name has no numbers and exists
    public static boolean isFNameValid(String fn) {
        if (fn.equals("")) {
            return false;
        }
        for (int i = 0; i < fn.length(); i++) {
            if (Character.isDigit(fn.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //checks name has no numbers and exists
    public static boolean isLNameValid(String ln) {
        if (ln.equals("")) {
            return false;
        }
        for (int i = 0; i < ln.length(); i++) {
            if (Character.isDigit(ln.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}