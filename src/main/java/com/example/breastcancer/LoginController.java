package com.example.breastcancer;

public class LoginController {

    private final Session session;
    private final MainApp mainApp;

    public LoginController(Session session, MainApp mainApp) {
        this.session = session;
        this.mainApp = mainApp;
    }

    public String onLogin(String email, String password) {
        Credentials creds = new Credentials(email, password);
        if (!creds.isValidFormat) {
            return "Enter a valid e-mail / password.";
        }
        if (session.login(creds)) {
            mainApp.onLoginSuccess("Welcome back! Let us continue.");
            return null;
        }
        return "Wrong credentials or network error.";
    }

    public void goToRegister() {
        mainApp.showRegisterPanel();
    }
}

/**
 * This class handles user login logic.
 * We inject the session and main app to keep it decoupled.
 * The core logic is located in the Session class.
 * We access it through session.login(Credentials).
 *
 * Purpose:
 * - Validate user input.
 * - Call session.login() to authenticate the user.
 * - Navigate to the risk assessment panel on success.
 *       *** REMEMBER: ***
 *          1. BY DEFINITION, A USER IS ONLY PROMPTED TO LOGIN
 *             IF THEY ARE NOT ALREADY LOGGED IN.
 *          2. If the user is already logged in, the controller
 *             will automatically redirect them to the risk assessment panel.
 *          3. This happens because when LoginController is created,
 *             it checks session.isLoggedIn(); if true, it bypasses
 *             the login form and calls mainApp.showRiskPanel().
 * - Show error messages on failure.
 *
 * -> see Session.java for more details on authentication and state management.
 */
