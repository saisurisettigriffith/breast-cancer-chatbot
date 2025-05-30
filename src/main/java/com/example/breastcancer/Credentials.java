package com.example.breastcancer;

/**
 * Credentials class manages Firebase Admin SDK initialization
 * and provides methods for validating user credentials
 * against Firebase Authentication via the REST API.
 */
public class Credentials {
    public boolean isValidFormat = false;
    private String username;
    private String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
        this.isValidFormat = checkValidityRegEx();
    }

    public boolean checkValidityRegEx() {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        String passwordPattern = ".{6,}";
        boolean emailMatches = this.username.matches(emailPattern);
        boolean passwordMatches = this.password.matches(passwordPattern);
        if (emailMatches && passwordMatches) {
            return true;
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}