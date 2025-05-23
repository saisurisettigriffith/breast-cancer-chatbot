package com.example.breastcancer;

/**
 * Mediates between LoginPanel and Session.
 */
public class LoginController {

    private final Session session;
    private final MainApp mainApp;

    public LoginController(Session session, MainApp mainApp) {
        this.session = session;
        this.mainApp = mainApp;
    }

    /** @return error message (null if login succeeded). */
    public String onLogin(String email, String password) {
        Credentials creds = new Credentials(email, password);
        if (!creds.isValidFormat) {
            return "Enter a valid e-mail / password.";
        }
        if (session.login(creds)) {
            mainApp.onLoginSuccess("Welcome back! Let’s continue.");
            return null;
        }
        return "Wrong credentials or network error.";
    }

    public void goToRegister() {
        mainApp.showRegisterPanel();
    }
}
